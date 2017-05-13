package fr.aumjaud.antoine.services.common;

import java.util.Properties;

public class TechnicalResource {
	//private static Logger logger = LoggerFactory.getLogger(TechnicalResource.class);

	private Properties commonProperties;

	public TechnicalResource(Properties commonProperties) {
		this.commonProperties = commonProperties;
	}

	public String hi() {
		return "hello";
	}

	public String info() {
		return String.format("{\"name\": \"%s\", \"version\":\"%s\"}", //
				commonProperties.getProperty("application_name"), //
				commonProperties.getProperty("version"));
	}
}
