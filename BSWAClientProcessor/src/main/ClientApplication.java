package main;

import logger.JavaStyleLogger;
import logger.Log4JLogger;
import network.ProcessorClient;

import org.apache.logging.log4j.Logger;

import process.ProcessManager;
import systemUtil.SystemCommand;
import ui.MainFrame;
import configuration.ClientContextManager;

public class ClientApplication {

	// Application logging.
	public final static boolean LOG4J_LOGGING = true;
	public final static boolean LOG4J_LOGGING_CONSOLE_ON = false;
	public final static boolean JAVA_LOGGING_ON = false;

	// Set up logging properties.
	static {
		// Clear logs directory
		new SystemCommand().removeAllINDirectory("logs");

		// Set up the log4j logger
		Log4JLogger.setup(LOG4J_LOGGING, LOG4J_LOGGING_CONSOLE_ON, "conf/logging", "ProcClient");

		JavaStyleLogger.setup(JAVA_LOGGING_ON, "conf/logging");

	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void main(String[] args) {

		LOG.info("---------------------------------------------");
		LOG.info("----START Client processing APPLICATION------");
		LOG.info("---------------------------------------------");

		ProcessorClient procClient = new ProcessorClient();

		if (ClientContextManager.getInstance().getConfig().isUiMode()) {
			LOG.info("Start UI Mode");
			MainFrame mainframe = new MainFrame();
			ProcessManager.getInstance().setUIInterface(mainframe);
			procClient.setUiInterface(mainframe);
		} else {
			LOG.info("No UI Mode");
		}

		// Initialisation de la connexion avec le serveur
		// Attempt a connexion. If connexion failed, launch a thread which will
		// continuously attempt to connect
		if (!procClient.initializeConnexion(false)) {
			procClient.launchConnexionThread();
		}

	}
}
