package test;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.systemUtil.SystemCommand;

import awsinterfaceManager.AWSS3InterfaceManager;

public class Init {

	// Application logging.
	public final static boolean LOG4J_LOGGING = true;
	public final static boolean LOG4J_LOGGING_CONSOLE_ON = true;

	// Set up logging properties.
	static {

		// Clear logs directory
		new SystemCommand().removeAllINDirectory("logs");

		// Set up the log4j logger
		Log4JLogger.setup(LOG4J_LOGGING, LOG4J_LOGGING_CONSOLE_ON, "conf/logging", "AmazonWebServicesInterface");

	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void main(String[] args) {

		LOG.info("---------------------------------------------");
		LOG.info("--------- START AWS Interface ---------------");
		LOG.info("---------------------------------------------");

		String analysesDirectoryRoot = "emera-result";

		List<Object[]> userRepositories = AWSS3InterfaceManager.getInstance().listAllFilesInBucket(analysesDirectoryRoot);
		for (int i = 0; i < userRepositories.size(); i++) {
			LOG.debug(Arrays.toString(userRepositories.get(i)));
		}

	}
}
