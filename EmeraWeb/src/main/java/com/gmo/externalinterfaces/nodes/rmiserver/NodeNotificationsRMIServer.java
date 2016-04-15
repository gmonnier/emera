package com.gmo.externalinterfaces.nodes.rmiserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;

import org.apache.logging.log4j.Logger;

import com.gmo.commonconfiguration.NetworkTopologyManager;
import com.gmo.generated.configuration.networktopology.RmiInterface;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.interfaces.IProcessorNodeControl;
import com.gmo.processorNode.interfaces.IProcessorNotifications;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.results.ResultsManager;
import com.gmo.rmiconfig.SecurityPolicy;

public class NodeNotificationsRMIServer implements IProcessorNotifications {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Log4JLogger.logger;

	public NodeNotificationsRMIServer() throws RemoteException {
		super();

	}

	public void initConnection() {
		LOG.info("[RMI-MODULE] Start NodeNotificationsRMIServer service");

		Policy.setPolicy(new SecurityPolicy());
		LOG.info("[RMI-MODULE] Apply security manager");

		LOG.info("[RMI-MODULE] Export RMI objects");

		try {

			RmiInterface rmiInterface = NetworkTopologyManager.getInstance().getByRmiInterfaceName(IProcessorNotifications.class.getSimpleName());
			IProcessorNotifications modelInfoSkeleton = (IProcessorNotifications) UnicastRemoteObject.exportObject(this, rmiInterface.getExportPort());
			
			String registryAddress = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryAddress();
			int registryPort = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryPort();

			Registry registry = LocateRegistry.getRegistry(registryAddress, registryPort);
			registry.rebind(rmiInterface.getValue(), modelInfoSkeleton);
			LOG.info("[RMI-MODULE] IProcessorNotifications server bound");
		} catch (Exception e) {
			LOG.error("[RMI-MODULE] Exception thrown while trying to bind RMI interfaces:");
			e.printStackTrace();

			LOG.error("[RMI-MODULE] Exit Application");
			System.exit(0);
		}
	}

	@Override
	public void analysisCompleted(ViewAnalysis analysis) throws RemoteException {
		ResultsManager.getInstance().addProcessedAnalysis(analysis);
	}

}
