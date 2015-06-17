package main;

import logger.JavaStyleLogger;
import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import rmiImplementations.RMIServer;
import systemUtil.SystemCommand;

public class BaseSpacePlatformApp {

	// Application logging.
	public final static boolean LOG4J_LOGGING = true;
	public final static boolean LOG4J_LOGGING_CONSOLE_ON = true;

	// Set up logging properties.
	static {
		// Clear logs directory
	    new SystemCommand().removeAllINDirectory("logs");

		// Set up the log4j logger
		Log4JLogger.setup(LOG4J_LOGGING, LOG4J_LOGGING_CONSOLE_ON,
				"conf/logging", "BaseSpacePlatform");

		JavaStyleLogger.setup(true, "conf/logging");
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void main(String[] args) {

		LOG.info("---------------------------------------------");
		LOG.info("-----START BaseSpace Serv APPLICATION--------");
		LOG.info("---------------------------------------------");
		LOG.info("Rmi Module --> request to start baseSpaceModel rmi interface server");
		new RMIServer();

	}

}
