package com.gmo.externalinterfaces.nodes.rmiclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.interfaces.IProcessorNode;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewPollingInfo;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.network.ViewNetworkConfig;
import com.gmo.results.ResultsManager;
import com.gmo.rmiconfig.RMIFileTransfertUtil;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.ws.exceptions.NodeStorageException;

public class NodeRMIClient implements IProcessorNode {

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

	@Override
	public ViewPollingInfo getViewPollingInfo(String userID) {
		if (rmiNodeClient != null) {
			try {
				ViewPollingInfo pollingInfo = rmiNodeClient.getViewPollingInfo(userID);
				pollingInfo.setProcessedAnalysis(ResultsManager.getInstance().getUserProcessedAnalysis(userID));
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return new ViewPollingInfo(new ViewNetworkConfig(), new ArrayList<ViewAnalysis>(), new ArrayList<ViewAnalysis>());
	}

	@Override
	public void requestRunningAnalysisChangeStatus(String id, AnalysisStatus newStatus) {
		if (rmiNodeClient != null) {
			try {
				rmiNodeClient.requestRunningAnalysisChangeStatus(id, newStatus);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}

	@Override
	public void stopAllAnalyses(String userID) {
		if (rmiNodeClient != null) {
			try {
				rmiNodeClient.stopAllAnalyses(userID);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}

	@Override
	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewProcessConfig, String userID) {
		if (rmiNodeClient != null) {
			try {
				return rmiNodeClient.enqueueNewAnalysis(viewProcessConfig, userID);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return null;
	}

	public void uploadDataFile(String fileName, InputStream uploadedInputStream, String analyseid) throws NodeStorageException, IOException {
		if (rmiNodeClient != null) {
			try {
				OutputStream outputStream = getOutputStream(fileName, InputType.DATA);

				if (outputStream != null) {
					new RMIFileTransfertUtil().copy(uploadedInputStream, outputStream);
				} else {
					throw new NodeStorageException();
				}
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}

	@Override
	public OutputStream getOutputStream(String fileName, InputType inputType) throws IOException {
		if (rmiNodeClient != null) {
			try {
				return rmiNodeClient.getOutputStream(fileName, inputType);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return null;
	}

	@Override
	public InputStream getInputStream(File f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}