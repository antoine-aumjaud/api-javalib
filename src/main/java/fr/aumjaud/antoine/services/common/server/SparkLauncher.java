package fr.aumjaud.antoine.services.common.server;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;

import java.util.Properties;

import com.google.gson.Gson;

import fr.aumjaud.antoine.services.common.PropertyHelper;
import fr.aumjaud.antoine.services.common.logger.ApplicationLogger;
import fr.aumjaud.antoine.services.common.security.NoAccessException;
import fr.aumjaud.antoine.services.common.security.SecurityHelper;
import fr.aumjaud.antoine.services.common.security.WrongRequestException;

/**
 * Launch a Spark server with default services
 */
public class SparkLauncher {

	private final static String COMMON_CONFIG_FILENAME = "common.properties";

	private final PropertyHelper propertyHelper = new PropertyHelper();
	private final SecurityHelper securityHelper = new SecurityHelper();
	private final Gson gson = new Gson();

	private final Properties commonProperties;
	private final SparkImplementation sparkImplementation;
	private Properties appProperties;

	public SparkLauncher(SparkImplementation sparkImplementation) {
		this.sparkImplementation = sparkImplementation;

		commonProperties = propertyHelper.loadProperties(COMMON_CONFIG_FILENAME);
		
		loadAppProperties();

		//Define listening port
		port(9080);

		//Common method
		get("/hi", (request, response) -> "hello");
		get("/info", "application/json", (request, response) -> new Info(), gson::toJson);

		//Enter in secure paths
		path("/secure", () -> {
			
			//Reload config form file
			get("/reloadConfig", (request, response) -> {
				loadAppProperties();
				return "done";
			});

			//Manage secure access
			before("/*", (request, response) -> {
				String configSecureToken = appProperties.getProperty(SecurityHelper.SECURE_KEY_NAME);
				String requestSecureKey = request.headers(SecurityHelper.SECURE_KEY_NAME);
				if(requestSecureKey == null)
					requestSecureKey = request.queryParams(SecurityHelper.SECURE_KEY_NAME);
				securityHelper.checkAccess(configSecureToken, requestSecureKey);
			});
		});

		//Handle Exceptions
		exception(NoAccessException.class, (e, request, response) -> {
			ApplicationLogger.getInstance().accessIssue(e.getMessage());
			response.status(401);
			response.body("no access: " + e.getClientMessage());
		});
		exception(WrongRequestException.class, (e, request, response) -> {
			ApplicationLogger.getInstance().parameterIssue(e.getMessage());
			response.status(412);
			response.body("wrong request: " + e.getClientMessage());
		});
		
		sparkImplementation.initSpark("/secure");
	}

	private void loadAppProperties() {
		appProperties = propertyHelper.loadProperties(sparkImplementation.getAppConfigName());
		sparkImplementation.setConfig(appProperties);
	}
	
	private class Info {
		private final String name, version, buildDate;

		public Info() {
			this.name = commonProperties.getProperty("application.name");
			this.version = commonProperties.getProperty("application.version");
			this.buildDate = commonProperties.getProperty("build.date");
		}

		@SuppressWarnings("unused") //by gson
		public String getName() {
			return name;
		}

		@SuppressWarnings("unused") //by gson
		public String getVersion() {
			return version;
		}

		@SuppressWarnings("unused") //by gson
		public String getBuildDate() {
			return buildDate;
		}
	}
}