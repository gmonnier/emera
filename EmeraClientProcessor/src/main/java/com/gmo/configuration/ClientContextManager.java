package com.gmo.configuration;

import java.io.File;
import java.util.logging.Logger;

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