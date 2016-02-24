package ssh;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import configuration.AWSContextManager;

public class SSHClientExecutor implements Runnable {

	/**
	 * InstantiateExecutor service for this . This service is used to start
	 * worker threads on data analysis.
	 */
	private final static ExecutorService sshExecutorService = Executors.newSingleThreadExecutor();

	private String remoteAdress;

	private final static String command = "cd bin/grnaClient && ./startup.sh";

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private int nbAttempt;

	private final static int ATTEMPT_MAX = 50;

	public SSHClientExecutor(String remoteAdress) {
		this.remoteAdress = remoteAdress;
		this.nbAttempt = 0;
	}

	public void startRemoteClient() {
		sshExecutorService.execute(this);
	}

	public void run() {
		LOG.debug("Enter run method " + remoteAdress);

		while (nbAttempt < ATTEMPT_MAX) {

			try {

				LOG.debug("Starting remote client on " + remoteAdress);
				JSch.setLogger(new SSHLogger());
				JSch jsch = new JSch();
				// Define the private key file
				LOG.debug("Add identity file " + AWSContextManager.getInstance().getConfig().getSshPrivateKeyFile());
				jsch.addIdentity(AWSContextManager.getInstance().getConfig().getSshPrivateKeyFile());

				String user = AWSContextManager.getInstance().getConfig().getSshUser();
				String host = remoteAdress;

				Session session = jsch.getSession(user, host, 22);
				// username and passphrase will be given via UserInfo interface.
				UserInfo ui = new SSHUserInfo();
				session.setUserInfo(ui);
				session.connect();

				Channel channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				LOG.debug("Set command to channel : " + command);

				((ChannelExec) channel).setErrStream(null);
				InputStream in = channel.getInputStream();

				channel.connect();

				byte[] tmp = new byte[1024];
				while (true) {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 1024);
						if (i < 0)
							break;
						LOG.debug("From SSH channel input : " + new String(tmp, 0, i));
						channel.disconnect();
					}
					if (channel.isClosed()) {
						if (in.available() > 0)
							continue;
						LOG.debug("exit-status: " + channel.getExitStatus());
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (Exception ee) {
						LOG.error("Exception while sleeping");
					}
				}

				session.disconnect();

				break;

			} catch (JSchException | IOException e) {
				nbAttempt++;
				LOG.error("Attempting ssh connection with " + remoteAdress);
				if (nbAttempt == ATTEMPT_MAX) {
					LOG.error("Unable to start SSH client application on remote machine : " + remoteAdress, e);
				}
			}

		}
	}
}
