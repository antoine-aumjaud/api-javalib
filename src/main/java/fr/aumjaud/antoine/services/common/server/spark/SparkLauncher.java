package fr.aumjaud.antoine.services.common.server.spark;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.path;
import static spark.Spark.port;

import java.util.Properties;
import java.util.Base64;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aumjaud.antoine.services.common.PropertyHelper;
import fr.aumjaud.antoine.services.common.logger.ApplicationLogger;
import fr.aumjaud.antoine.services.common.security.NoAccessException;
import fr.aumjaud.antoine.services.common.security.SecurityHelper;
import fr.aumjaud.antoine.services.common.security.WrongRequestException;
import fr.aumjaud.antoine.services.common.server.ServerInfo;

/**
 * Launch a Spark server with default services
 */
public class SparkLauncher {

	private final static String COMMON_CONFIG_FILENAME = "common.properties";

	private static final Logger LOGGER = LoggerFactory.getLogger(SparkLauncher.class);

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

		//Add CORS
		before("/*", (request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Credentials", "true");
			response.header("Access-Control-Allow-Headers", "Authorization, Content-Type");
		});

		//Manage OPTIONS method from browsers
		options("/*", (request, response) -> {
			response.header("Access-Control-Max-Age", "86400");
			response.status(200);
			return "";
		});
		
		//Common method
		get("/hi", (request, response) -> "hello");
		get("/info", "application/json", (request, response) -> new ServerInfo(commonProperties), gson::toJson);

		//Enter in secure paths
		path("/secure", () -> {
			
			//Reload config form file
			get("/reloadConfig", (request, response) -> {
				loadAppProperties();
				return "done";
			});

			//Manage secure access
			before("/*", (request, response) -> {
				if(request.requestMethod().equals("OPTIONS")) return; //do not check params for OPTIONS request

				String configSecureToken = appProperties.getProperty(SecurityHelper.SECURE_KEY_NAME);
				String requestAuthorization = request.headers(SecurityHelper.AUTHORIZATION_HEADER);
				if(requestAuthorization != null) {
					if(requestAuthorization.startsWith("Basic")) {
						String requestSecureKeyAuthHeader = new String(Base64.getDecoder().decode(requestAuthorization.substring(6 /* "Basic".length */)), "UTF-8");
						requestSecureKeyAuthHeader = requestSecureKeyAuthHeader.substring(0, requestSecureKeyAuthHeader.length() - 1); //remove ":" separator between username and password
						securityHelper.checkSecureKeyAccess(configSecureToken, requestSecureKeyAuthHeader);
						return;
					}
					else if(requestAuthorization.startsWith("Bearer"))  {
						String requestTokenAuthHeader = requestAuthorization.substring(7 /* "Bearer".length */);
						securityHelper.checkJWTAccess(requestTokenAuthHeader, sparkImplementation.getApiName());
						return;
					}
				}

				String requestSecureKeyHeader = request.headers(SecurityHelper.SECURE_KEY_NAME);
				if(requestSecureKeyHeader != null) {
					securityHelper.checkSecureKeyAccess(configSecureToken, requestSecureKeyHeader);
					return;
				}
				String requestSecureKeyParam  = request.queryParams(SecurityHelper.SECURE_KEY_NAME);
				if(requestSecureKeyParam != null) {
					securityHelper.checkSecureKeyAccess(configSecureToken, requestSecureKeyParam);
					return;
				}

				throw new NoAccessException("no credentials", "Try to access to API without credentials");
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
		exception(Exception.class, (e, request, response) -> {
			LOGGER.error("Unhandled exception returned to client", e);
			response.status(500);
			response.body("internal server error, see server logs");
		});
	
		sparkImplementation.initSpark("/secure");
	}

	private void loadAppProperties() {
		appProperties = propertyHelper.loadProperties(sparkImplementation.getAppConfigName());
		sparkImplementation.setConfig(appProperties);
	}
}