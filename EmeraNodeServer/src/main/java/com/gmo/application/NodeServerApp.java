package com.gmo.application;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.coreprocessing.AnalysisManager;
import com.gmo.externalInterfaces.rmiclient.NodeNotificationsRMIClient;
import com.gmo.externalInterfaces.rmiserver.NodeRMIServer;
import com.gmo.logger.JavaStyleLogger;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorserver.ProcessorServerManager;
import com.gmo.systemUtil.SystemCommand;

public class NodeServerApp {

	// Application logging.
	public final static boolean LOG4J_LOGGING = true;
	public final static boolean LOG4J_LOGGING_CONSOLE_ON = true;

	// Set up logging properties.
	static {
		// Clear logs directory
		new SystemCommand().removeAllINDirectory("logs");

		// Set up the log4j logger
		Log4JLogger.setup(LOG4J_LOGGING, LOG4J_LOGGING_CONSOLE_ON, "conf/logging", "NodeServer");

		JavaStyleLogger.setup(true, "conf/logging");
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void main(String[] args) {

		LOG.info("---------------------------------------------");
		LOG.info("-------- START Node server ------------------");
		LOG.info("---------------------------------------------");

		initRMIServer();

		initCommonParameters();

		initProcessorServer();
	}

	private static void initProcessorServer() {
		ProcessorServerManager.getInstance();
	}

	private static void initCommonParameters() {
		LOG.info("Init common paramaters...");
		AnalysisManager.getInstance().setAnalysisResultsLocationParameters(NodeNotificationsRMIClient.getInstance().fetchResultsLocation());
		LOG.info("Results location parameters fetched to : " + AnalysisManager.getInstance().getAnalysisResultsLocationType() + "  --- " + AnalysisManager.getInstance().getAnalysisResultsLocation());
	}

	private static void initRMIServer() {
		LOG.info("[RMI Module] --> request to start Node rmi server");
		new NodeRMIServer();
	}
}
