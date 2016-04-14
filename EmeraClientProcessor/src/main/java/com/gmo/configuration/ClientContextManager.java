package com.gmo.configuration;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.xmljaxb.AbstractConfigurationManager;
import com.gmo.generated.configuration.clientprocessorconfig.ClientProcessorConfig;
import com.gmo.logger.Log4JLogger;

public class ClientContextManager extends AbstractConfigurationManager<ClientProcessorConfig> {

	private final static String contextFile = "conf/ClientProcessorConfig.xml";

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private static ClientContextManager instance;

	private ClientContextManager() {
		super(new File(contextFile), new ClientProcessorConfig());
	}

	public static synchronized ClientContextManager getInstance() {
		if (instance == null) {
			instance = new ClientContextManager();
		}
		return instance;
	}
}