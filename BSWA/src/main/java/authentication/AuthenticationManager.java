package authentication;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

public class AuthenticationManager {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private AuthenticationManager() {
	}
	
	public static synchronized AuthenticationManager getInstance() {
		return AuthenticationManagerHolder.instance;
	}

	private static class AuthenticationManagerHolder {
		public final static AuthenticationManager instance = new AuthenticationManager();
	}
	
}
