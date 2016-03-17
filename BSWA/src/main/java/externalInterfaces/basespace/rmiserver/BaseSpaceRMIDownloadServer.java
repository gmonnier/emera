package externalInterfaces.basespace.rmiserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IDownloadListener;
import com.gmo.logger.Log4JLogger;
import com.gmo.rmiconfig.SecurityPolicy;

public class BaseSpaceRMIDownloadServer implements Runnable {

	private static Thread currentServiceServerThread;

	private final static Logger LOG = Log4JLogger.logger;

	private DownloadListenerImpl downloadListener;

	public static Registry registry;

	public BaseSpaceRMIDownloadServer() {
		try {
			downloadListener = new DownloadListenerImpl();
		} catch (RemoteException e) {
			LOG.error("[RMI-MODULE] Exception when create Remote Interfaces implementations");
			e.printStackTrace();
		}

		startServer();
	}

	/**
	 * Start the server thread for the service.
	 */
	private void startServer() {

		if (currentServiceServerThread != null && currentServiceServerThread.isAlive()) {
			currentServiceServerThread.stop();
		}

		currentServiceServerThread = new Thread(this);
		currentServiceServerThread.setName("[RMI-MODULE] Rmi connection for baseSpace interface model");
		currentServiceServerThread.start();

	}

	@Override
	public void run() {

		LOG.info("[RMI-MODULE] Start service server Thread");
		LOG.info("[RMI-MODULE] Apply security manager");
		Policy.setPolicy(new SecurityPolicy());

		LOG.info("[RMI-MODULE] Export RMI objects");
		try {

			registry = LocateRegistry.createRegistry(10001);

			UnicastRemoteObject.unexportObject(downloadListener, true);
			IDownloadListener modelInfoSkeleton = (IDownloadListener) UnicastRemoteObject.exportObject(downloadListener, 10001);

			// String name = "//127.0.0.1/IAuthenticationRequest";
			registry.rebind("IDownloadListener", modelInfoSkeleton);
			LOG.info("[RMI-MODULE] BaseSpaceModelImpl bound");
		} catch (Exception e) {
			LOG.error("[RMI-MODULE] Exception thrown while trying to bind RMI interfaces:");
			e.printStackTrace();
			LOG.error("[RMI-MODULE] Exit service thread");
		}
		
		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
