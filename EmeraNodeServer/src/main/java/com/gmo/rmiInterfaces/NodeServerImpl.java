package com.gmo.rmiInterfaces;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import processorNode.interfaces.IProcessorNode;
import processorNode.viewmodel.ViewFile;
import processorNode.viewmodel.ViewPollingInfo;
import processorNode.viewmodel.network.ViewNetworkConfig;

import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.coreprocessing.AnalysisManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.model.inputs.ModelFileStored;
import com.gmo.modelconverters.FileStoredConverter;

public class NodeServerImpl extends UnicastRemoteObject implements IProcessorNode {

	private static final long serialVersionUID = 1L;
	
	private final static Logger LOG = Log4JLogger.logger;

	public NodeServerImpl() throws RemoteException {
		super();
	}

	@Override
	public void requestNodeProcessorClientRemove(String clientID) throws RemoteException {
		
	}
	
	@Override
	public List<ViewFile> getListStoredData() throws RemoteException {
		List<ModelFileStored> input = StorageConfigurationManager.getInstance().getListStoredData();
		List<ViewFile> listView = new ArrayList<ViewFile>();
		FileStoredConverter viewBuilder = new FileStoredConverter();

		for (Iterator<ModelFileStored> iterator = input.iterator(); iterator.hasNext();) {
			ModelFileStored inputFile = (ModelFileStored) iterator.next();
			listView.add(viewBuilder.buildViewModelObject(inputFile));
		}
		return listView;	
	}

	@Override
	public List<ViewFile> getListStoredLibraries() throws RemoteException {
		List<ModelFileStored> input = StorageConfigurationManager.getInstance().getListStoredLibraries();
		List<ViewFile> listView = new ArrayList<ViewFile>();
		FileStoredConverter viewBuilder = new FileStoredConverter();

		for (Iterator<ModelFileStored> iterator = input.iterator(); iterator.hasNext();) {
			ModelFileStored inputFile = (ModelFileStored) iterator.next();
			listView.add(viewBuilder.buildViewModelObject(inputFile));
		}
		return listView;	
	}

	@Override
	public ViewPollingInfo getViewPollingInfo(String userID) throws RemoteException {
		return new ViewPollingInfo(new ViewNetworkConfig(userID), AnalysisManager.getInstance().getUserRunningAnalysis(userID), AnalysisManager.getInstance().getUserProcessedAnalysis(userID));
	}
	
}
