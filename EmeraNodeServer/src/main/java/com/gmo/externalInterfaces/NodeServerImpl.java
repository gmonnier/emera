package com.gmo.externalInterfaces;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.apache.logging.log4j.Logger;

import processorNode.interfaces.IProcessorNode;
import processorNode.model.ViewFile;

import com.gmo.logger.Log4JLogger;

public class NodeServerImpl extends UnicastRemoteObject implements IProcessorNode {

	private static final long serialVersionUID = 1L;
	
	private final static Logger LOG = Log4JLogger.logger;

	public NodeServerImpl() throws RemoteException {
		super();
	}

	public List<ViewFile> requestNodeProcessorClientRemove(String clientID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
