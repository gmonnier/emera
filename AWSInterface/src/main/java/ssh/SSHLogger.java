package ssh;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

public class SSHLogger implements com.jcraft.jsch.Logger {
	static java.util.Hashtable<Integer, String> name = new java.util.Hashtable<Integer, String>();
	static {
		name.put(new Integer(DEBUG), "DEBUG: ");
		name.put(new Integer(INFO), "INFO: ");
		name.put(new Integer(WARN), "WARN: ");
		name.put(new Integer(ERROR), "ERROR: ");
		name.put(new Integer(FATAL), "FATAL: ");
	}

	public boolean isEnabled(int level) {
		return true;
	}

	public void log(int level, String message) {
		LOG.debug(name.get(new Integer(level)) + message);
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
}
