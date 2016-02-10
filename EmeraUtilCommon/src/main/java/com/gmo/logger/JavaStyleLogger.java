package com.gmo.logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

public class JavaStyleLogger {

	static public void setup(boolean javaLogging, String configurationDir) {

		if (javaLogging) {
			// Get the global logger to configure it
			try {
				LogManager.getLogManager().readConfiguration(new FileInputStream(configurationDir + "/JavaStyleLogs.properties"));
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}

	}

}
