package com.gmo.externalinterfaces.nodes.rmiserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.interfaces.IProcessorNotifications;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.results.ResultsManager;
import com.gmo.rmiconfig.SecurityPolicy;

public class NodeNotificationsRMIServer extends UnicastRemoteObject implements IProcessorNotifications {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Log4JLogger.logger;

	public NodeNotificationsRMIServer() throws RemoteException {
		super();

		LOG.info("[RMI-MODULE] Start NodeNotificationsRMIServer service");

		Policy.setPolicy(new SecurityPolicy());
		LOG.info("[RMI-MODULE] Apply security manager");

		LOG.info("[RMI-MODULE] Export RMI objects");

		try {

			Registry registry = LocateRegistry.createRegistry(10000);

			UnicastRemoteObject.unexportObject(this, true);
			IProcessorNotifications modelInfoSkeleton = (IProcessorNotifications) UnicastRemoteObject.exportObject(this, 10000);

			// String name = "//127.0.0.1/IAuthenticationRequest";
			registry.rebind("IProcessorNodeControl", modelInfoSkeleton);
			LOG.info("[RMI-MODULE] Processor Node server bound");
		} catch (Exception e) {
			LOG.error("[RMI-MODULE] Exception thrown while trying to bind RMI interfaces:");
			e.printStackTrace();

			LOG.error("[RMI-MODULE] Exit service thread");
			System.exit(0);
		}

	}

	@Override
	public void analysisCompleted(ViewAnalysis analysis) throws RemoteException {
		ResultsManager.getInstance().addProcessedAnalysis(analysis);
	}

}
