package authentication;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

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
