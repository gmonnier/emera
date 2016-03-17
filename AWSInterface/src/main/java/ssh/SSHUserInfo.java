package ssh;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class SSHUserInfo implements UserInfo, UIKeyboardInteractive {
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	public String getPassword() {
		return null;
	}

	public boolean promptYesNo(String str) {
		// Always in silent mode.
		return true;
	}

	String passphrase;
	JTextField passphraseField = (JTextField) new JPasswordField(20);

	public String getPassphrase() {
		return passphrase;
	}

	public boolean promptPassphrase(String message) {
		LOG.debug(message + "  --> automatic reply yes");
		return true;
	}

	public boolean promptPassword(String message) {
		LOG.debug(message + "  --> automatic reply true");
		return true;
	}

	public void showMessage(String message) {
		LOG.debug(message + "  --> do not display message");
	}

	public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
		return null;
	}
}
