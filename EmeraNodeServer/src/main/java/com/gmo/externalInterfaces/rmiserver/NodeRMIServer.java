package com.gmo.externalInterfaces.rmiserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IBaseSpaceModel;
import com.gmo.commonconfiguration.NetworkTopologyManager;
import com.gmo.generated.configuration.networktopology.RmiInterface;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.interfaces.IProcessorNodeControl;
import com.gmo.rmiconfig.SecurityPolicy;

public class NodeRMIServer implements Runnable {

	private static Thread currentServiceServerThread;

	private final static Logger LOG = Log4JLogger.logger;

	private NodeServerImpl nodeRMIServer;

	public NodeRMIServer() {
		try {
			nodeRMIServer = new NodeServerImpl();
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

			String registryAddress = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryAddress();
			int registryPort = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryPort();
			Registry registry= LocateRegistry.getRegistry(registryAddress, registryPort);

			RmiInterface rmiInterface = NetworkTopologyManager.getInstance().getByRmiInterfaceName(IProcessorNodeControl.class.getSimpleName());
			IProcessorNodeControl modelInfoSkeleton = (IProcessorNodeControl) UnicastRemoteObject.exportObject(nodeRMIServer, rmiInterface.getExportPort());

			registry.rebind(rmiInterface.getValue(), modelInfoSkeleton);
			LOG.info("[RMI-MODULE] Processor Node server bound");
		} catch (Exception e) {
			LOG.error("[RMI-MODULE] Exception thrown while trying to bind RMI interfaces:");
			e.printStackTrace();

			LOG.error("[RMI-MODULE] Exit service thread");
			System.exit(0);
		}
	}

}
