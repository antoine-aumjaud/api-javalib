package fr.aumjaud.antoine.services.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLogger {
	private static ApplicationLogger INSTANCE = new ApplicationLogger();

	private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger("SECURITY");

	public static ApplicationLogger getInstance() {
		return INSTANCE;
	}

	public void accessIssue(String message) {
		SECURITY_LOGGER.error("Access without token: {}", message);
	}
	public void parameterIssue(String message) {
		SECURITY_LOGGER.error("Wrong parameter: {}", message);
	}
}
