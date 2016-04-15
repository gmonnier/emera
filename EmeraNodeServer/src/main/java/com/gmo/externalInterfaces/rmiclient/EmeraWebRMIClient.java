package com.gmo.externalInterfaces.rmiclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.interfaces.IProcessorNotifications;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;

public class EmeraWebRMIClient implements IProcessorNotifications {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private IProcessorNotifications rmiProcessorNotif;

	private boolean connectionOk;

	private boolean firstConnectionAttempt;

	public EmeraWebRMIClient() {
		firstConnectionAttempt = true;
		initRMIConnection();
	}

	private synchronized void initRMIConnection() {

		if (connectionOk) {
			// Already initialized
			return;
		}

		String registryAddr = "localhost";
		int registryPort = 10000;
		LOG.info("Create new NodeRMI client on  " + registryAddr + "    port: " + registryPort);

		connectionOk = false;
		try {
			if (firstConnectionAttempt) {
				LOG.debug("Request for the rmi IProcessorNotifications interface");
			}
			Registry registry = LocateRegistry.getRegistry(registryAddr, registryPort);
			rmiProcessorNotif = (IProcessorNotifications) registry.lookup("IProcessorNotifications");
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
	public void analysisCompleted(ViewAnalysis analysis) throws RemoteException {
		if (rmiProcessorNotif != null) {
			try {
				rmiProcessorNotif.analysisCompleted(analysis);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}

}