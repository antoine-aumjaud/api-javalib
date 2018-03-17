package fr.aumjaud.antoine.services.common.server;

import java.util.Properties;

/**
 * Interface to implement by application to be launch by SparkLauncher class
 */
public interface SparkImplementation {

	/**
	 * Set the configuration
	 * @param appProperties the configuration to set
	 */
	void setConfig(Properties appProperties);

	/**
	 * Return the configuration file name the the application
	 * @return the configuration file name the the application
	 */
	String getAppConfigName();

	/**
	 * Return the name the application
	 * @return the name the application
	 */
	String getApiName();

	/**
	 * Specific part of spark initialization for this application
	 * @param securePath the path of secured services
	 */
	void initSpark(String securePath);

}