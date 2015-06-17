package rmiImplementations;

import interfaces.IBaseSpaceModel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import rmiconfig.SecurityPolicy;

public class RMIServer implements Runnable {

	private static Thread currentServiceServerThread;

	private final static Logger LOG = Log4JLogger.logger;

	private BaseSpaceModelImpl bsModel;

	public static Registry registry;

	public RMIServer() {
		try {
			bsModel = new BaseSpaceModelImpl();
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

		Policy.setPolicy(new SecurityPolicy());
		LOG.info("[RMI-MODULE] Apply security manager");

		LOG.info("[RMI-MODULE] Export RMI objects");

		try {

			registry = LocateRegistry.createRegistry(10000);

			UnicastRemoteObject.unexportObject(bsModel, true);
			IBaseSpaceModel modelInfoSkeleton = (IBaseSpaceModel) UnicastRemoteObject.exportObject(bsModel, 10000);

			// String name = "//127.0.0.1/IAuthenticationRequest";
			registry.rebind("IBaseSpaceModel", modelInfoSkeleton);
			LOG.info("[RMI-MODULE] BaseSpaceModelImpl bound");
		} catch (Exception e) {
			LOG.error("[RMI-MODULE] Exception thrown while trying to bind RMI interfaces:");
			e.printStackTrace();

			LOG.error("[RMI-MODULE] Exit service thread");
			System.exit(0);
		}

		/*while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}

}
