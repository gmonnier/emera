package logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

public class Log4JLogger {

	public static Logger logger;
	
	public static Logger connectionlogger;

	public static void setup(boolean log4jLogging, boolean consoleActivated,
			String configurationDir, String appName) {
		
		System.setProperty("app.name", appName);

		String outputFile;
		if (!log4jLogging) {
			outputFile = "log4j2Disable.xml";
		} else {
			if (consoleActivated) {
				outputFile = "log4j2.xml";
			} else {
				outputFile = "log4j2WithoutConsole.xml";
			}
		}
		System.setProperty(
				XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY,
				configurationDir + "/" + outputFile);

		System.out
				.println("Property XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY set to -> "
						+ System.getProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY));

		
		logger = LogManager.getLogger("com.gmo.basicLogger");
		logger.entry();
		
		connectionlogger = LogManager.getLogger("com.gmo.connectionLogger");
		connectionlogger.entry();
		
		System.out.println(logger);
		System.out.println(connectionlogger);

	}

}
