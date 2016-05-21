package com.gmo.externalInterfaces.rmiclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.logging.log4j.Logger;

import com.gmo.commonconfiguration.NetworkTopologyManager;
import com.gmo.generated.configuration.applicationcontext.ResultLocation;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.interfaces.INodeServerNotifications;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;

public class NodeNotificationsRMIClient implements INodeServerNotifications {

	private static NodeNotificationsRMIClient instance;

	public static synchronized NodeNotificationsRMIClient getInstance() {
		if (instance == null) {
			instance = new NodeNotificationsRMIClient();
		}
		if (!instance.isConnectionOk()) {
			instance.initRMIConnection();
		}
		return instance;
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private INodeServerNotifications rmiProcessorNotif;

	private boolean connectionOk;

	private boolean firstConnectionAttempt;

	private NodeNotificationsRMIClient() {
		connectionOk = false;
		firstConnectionAttempt = true;
		initRMIConnection();
	}

	private synchronized void initRMIConnection() {

		if (connectionOk) {
			// Already initialized
			return;
		}

		connectionOk = false;
		try {
			if (firstConnectionAttempt) {
				LOG.debug("Request for the rmi IProcessorNotifications interface");
			}

			String registryAddress = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryAddress();
			int registryPort = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryPort();
			LOG.info("Retrieve RMI registry on  " + registryAddress + "    port: " + registryPort);
			Registry registry = LocateRegistry.getRegistry(registryAddress, registryPort);

			rmiProcessorNotif = (INodeServerNotifications) registry.lookup(INodeServerNotifications.class.getSimpleName());
			if (firstConnectionAttempt) {
				LOG.debug("RMI Interface IProcessorNotifications retrieved from table : " + rmiProcessorNotif);
			}
			firstConnectionAttempt = true;
			connectionOk = true;
			return;
		} catch (RemoteException e) {
			if (firstConnectionAttempt) {
				LOG.error("Unable to instantiate IProcessorNotifications : RemoteException " + e);
			}
		} catch (NotBoundException e) {
			if (firstConnectionAttempt) {
				LOG.error("Unable to instantiate IProcessorNotifications : NotBoundException " + e);
			}
		}
		firstConnectionAttempt = false;
	}

	public boolean isConnectionOk() {
		return connectionOk;
	}

	public void setConnectionOk(boolean connectionOk) {
		this.connectionOk = connectionOk;
	}

	@Override
	public void analysisCompleted(ViewAnalysis analysis) {
		if (rmiProcessorNotif != null) {
			try {
				rmiProcessorNotif.analysisCompleted(analysis);
			} catch (RemoteException e) {
				LOG.error("Unable to notify WEB Server of analysis completion!! RemoteException " + e);
			}
		}
	}

	@Override
	public void additionnalAnalysisCompleted(String analysisID, ViewFile outputPDF) {
		if (rmiProcessorNotif != null) {
			try {
				rmiProcessorNotif.additionnalAnalysisCompleted(analysisID, outputPDF);
			} catch (RemoteException e) {
				LOG.error("Unable to notify WEB Server of additionnal analysis completion!! RemoteException " + e);
			}
		}
	}

	@Override
	public ResultLocation fetchResultsLocation() {
		if (rmiProcessorNotif != null) {
			try {
				return rmiProcessorNotif.fetchResultsLocation();
			} catch (RemoteException e) {
				LOG.error("Unable to fetch results location parameters " + e);
			}
		}
		return null;
	}

	@Override
	public void additionnalAnalysisFailed(String analysisID, String reasonMessage) {
		if (rmiProcessorNotif != null) {
			try {
				rmiProcessorNotif.additionnalAnalysisFailed(analysisID, reasonMessage);
			} catch (RemoteException e) {
				LOG.error("Unable to send additionnal analysis failed message" + e);
			}
		}
	}

}