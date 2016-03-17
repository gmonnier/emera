package com.gmo.configuration.xmljaxb;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

public class ShutdownConfigurationHandler<MODEL> implements Runnable {
	
	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private ConfigurationWriter<MODEL> writer;
	
	public ShutdownConfigurationHandler(ConfigurationWriter<MODEL> writer) {
		this.writer = writer;
	}

	@Override
	public void run() {
		System.out.println("Entering runnable method. Marshal xml configuration file " + writer.getContextXMLFile().getAbsolutePath());
		writer.marshalXMLFile();
	}

}
