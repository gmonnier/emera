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

import com.gmo.configuration.ApplicationContextManager;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.interfaces.IProcessorNodeControl;
import com.gmo.processorNode.viewmodel.OutputFileType;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewNodePollingInfo;
import com.gmo.processorNode.viewmodel.ViewPollingInfo;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.network.ViewNetworkConfig;
import com.gmo.processorNode.viewmodel.network.ViewNodeNetworkConfig;
import com.gmo.results.ResultsManager;
import com.gmo.rmiconfig.RMIFileTransfertUtil;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.reports.Report;
import com.gmo.ws.exceptions.NodeStorageException;

public class NodeRMIClient implements IProcessorNodeControl {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private IProcessorNodeControl rmiNodeClient;

	private boolean connectionOk;

	private boolean firstConnectionAttempt;

	public NodeRMIClient() {
		firstConnectionAttempt = true;
		initRMIConnection();
	}

	private synchronized void initRMIConnection() {

		if (connectionOk) {
			// Already initialized
			return;
		}

		String registryAddr = ApplicationContextManager.getInstance().getConfig().getNodeConnectionConfiguration().getRmiRegistryAddress();
		int registryPort = ApplicationContextManager.getInstance().getConfig().getNodeConnectionConfiguration().getRmiRegistryPort();
		LOG.info("Create new NodeRMI client on  " + registryAddr + "    port: " + registryPort);

		connectionOk = false;
		try {
			if (firstConnectionAttempt) {
				LOG.debug("Request for the rmi ProcessorNode interface");
			}
			Registry registry = LocateRegistry.getRegistry(registryAddr, registryPort);
			rmiNodeClient = (IProcessorNodeControl) registry.lookup("IProcessorNodeControl");
			if (firstConnectionAttempt) {
				LOG.debug("RMI Interface IProcessorNodeControl retrieved from table : " + rmiNodeClient);
			}
			firstConnectionAttempt = true;
			connectionOk = true;
			return;
		} catch (RemoteException e) {
			if (firstConnectionAttempt) {
				LOG.error("Unable to instantiate ProcessorNode : RemoteException " + e);
			}
		} catch (NotBoundException e) {
			if (firstConnectionAttempt) {
				LOG.error("Unable to instantiate ProcessorNode : NotBoundException " + e);
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
	public ViewNodePollingInfo getViewNodePollingInfo(String userID) {
		if (rmiNodeClient != null) {
			try {
				return rmiNodeClient.getViewNodePollingInfo(userID);
			} catch (RemoteException e) {
				// Do nothng - don't flood logs
			}
		} else {
			initRMIConnection();
		}

		return new ViewNodePollingInfo(new ViewNodeNetworkConfig(), new ArrayList<ViewAnalysis>());
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
	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewProcessConfig, String userID, String bsClientID, String bsClientSecret, String bsAccessToken, LocationType locType, String locationPath) {
		if (rmiNodeClient != null) {
			try {
				return rmiNodeClient.enqueueNewAnalysis(viewProcessConfig, userID, bsClientID, bsClientSecret, bsAccessToken, locType, locationPath);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return null;
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
		if (rmiNodeClient != null) {
			try {
				return rmiNodeClient.getInputStream(f);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return null;
	}

	@Override
	public void uploadToNodeServerDone(InputType inputType, String analyseid, String fileName) {
		if (rmiNodeClient != null) {
			try {
				rmiNodeClient.uploadToNodeServerDone(inputType, analyseid, fileName);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}

	@Override
	public void requestOccurencesIncreaseAnalysis(Report refReport, Report compReport, OutputFileType outputFileType) {
		if (rmiNodeClient != null) {
			try {
				rmiNodeClient.requestOccurencesIncreaseAnalysis(refReport, compReport, outputFileType);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}

	public void uploadFileToNodeServer(InputType inputType, String fileName, InputStream uploadedInputStream, String analyseid) throws NodeStorageException, IOException {
		if (rmiNodeClient != null) {
			try {
				OutputStream outputStream = getOutputStream(fileName, inputType);

				if (outputStream != null) {
					new RMIFileTransfertUtil().copy(uploadedInputStream, outputStream);
					rmiNodeClient.uploadToNodeServerDone(inputType, analyseid, fileName);
				} else {
					throw new NodeStorageException();
				}
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}
}