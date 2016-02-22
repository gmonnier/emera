package com.gmo.externalinterfaces.nodes.rmiclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import processorNode.interfaces.IProcessorNode;
import processorNode.viewmodel.ViewFile;

import com.gmo.logger.Log4JLogger;

public class NodeRMIClient implements IProcessorNode{

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private IProcessorNode rmiNodeClient;

	private boolean connectionOk;

	public NodeRMIClient() {

		connectionOk = false;
		try {
			LOG.debug("Request for the rmi ProcessorNode interface");
			Registry registry = LocateRegistry.getRegistry(8081);
			rmiNodeClient = (IProcessorNode) registry.lookup("IProcessorNode");
			LOG.debug("RMI Interface IProcessorNode retrieved from table : " + rmiNodeClient);
			connectionOk = true;
		} catch (RemoteException e) {
			LOG.error("RemoteException " + e);
		} catch (NotBoundException e) {
			LOG.error("NotBoundException " + e);
		}
	}
	
	public boolean isConnectionOk() {
		return connectionOk;
	}

	public void setConnectionOk(boolean connectionOk) {
		this.connectionOk = connectionOk;
	}

	@Override
	public void requestNodeProcessorClientRemove(String clientID) {
		if (rmiNodeClient != null) {
			try {
				rmiNodeClient.requestNodeProcessorClientRemove(clientID);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}
	
	@Override
	public List<ViewFile> getListStoredData() {
		if (rmiNodeClient != null) {
			try {
				return rmiNodeClient.getListStoredData();
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return new ArrayList<ViewFile>();
	}

	@Override
	public List<ViewFile> getListStoredLibraries() {
		if (rmiNodeClient != null) {
			try {
				return rmiNodeClient.getListStoredLibraries();
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return new ArrayList<ViewFile>();
	}

}