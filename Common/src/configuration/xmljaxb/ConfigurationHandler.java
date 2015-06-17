package configuration.xmljaxb;

import java.util.HashSet;
import java.util.Set;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

class ConfigurationHandler {

	private static final ConfigurationHandler instance = new ConfigurationHandler();

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private Set<Class> setHolder = new HashSet<Class>();

	private ConfigurationHandler() {
	}

	/**
	 * Define wether a configuration of this type has already been created.
	 * 
	 * @return true if creation may be done, false otherwise.
	 */
	public static boolean checkInstantiationValidity(Class classOf) {
		LOG.debug("Check instantiation validity for class " + classOf);
		return !instance.setHolder.contains(classOf);
	}

	public static void instantiationDone(Class classOf) {
		LOG.debug("New configuration for " + classOf);
		instance.setHolder.add(classOf);
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
