package fr.aumjaud.antoine.services.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertyHelper {
	private static final Logger logger = LoggerFactory.getLogger(PropertyHelper.class);

	public Properties loadProperties(String configFileName) {
		try (InputStream is = PropertyHelper.class.getClassLoader().getResourceAsStream(configFileName)) {
			if (is == null) {
				throw new IllegalStateException("No config file in classpath: " + configFileName);
			}
			Properties p = new Properties();
			p.load(is);
			logger.info(configFileName + " loaded");
			return p;
		} catch (IOException e) {
			logger.error("Can't load properties", e);
			return null;
		}
	}
}
