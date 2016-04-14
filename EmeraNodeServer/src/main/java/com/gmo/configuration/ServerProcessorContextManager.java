package com.gmo.configuration;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.xmljaxb.AbstractConfigurationManager;
import com.gmo.generated.configuration.serverprocessorconfig.ServerProcessorConfig;
import com.gmo.logger.Log4JLogger;

public class ServerProcessorContextManager extends AbstractConfigurationManager<ServerProcessorConfig> {

	private final static String contextFile = "conf/ServerProcessorConfig.xml";

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private static ServerProcessorContextManager instance;

	private ServerProcessorContextManager() {
		super(new File(contextFile), new ServerProcessorConfig());
	}

	public static synchronized ServerProcessorContextManager getInstance() {
		if (instance == null) {
			instance = new ServerProcessorContextManager();
		}
		return instance;
	}
}
