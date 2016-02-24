package application;

import org.apache.logging.log4j.Logger;

import ssh.SSHClientExecutor;

import com.gmo.logger.Log4JLogger;
import com.gmo.systemUtil.SystemCommand;

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

		new SSHClientExecutor("54.186.77.247").startRemoteClient();
		
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
