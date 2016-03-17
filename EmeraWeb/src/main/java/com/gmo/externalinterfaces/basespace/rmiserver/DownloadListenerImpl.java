package com.gmo.externalinterfaces.basespace.rmiserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IDownloadListener;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.logger.Log4JLogger;
import com.gmo.nodes.NodeManager;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.analysis.NoSuchAnalysisException;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public class DownloadListenerImpl extends UnicastRemoteObject implements IDownloadListener {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Log4JLogger.logger;

	public DownloadListenerImpl() throws RemoteException {
		super();
	}

	@Override
	public void applicationCrashed() throws RemoteException {
		LOG.debug("Application crashed ");
	}

	@Override
	public void downloadFailed(String analyseID, FastQFile inputFile) throws RemoteException {
		LOG.debug("Server side Download failed received for " + inputFile.getName());
		NodeManager.getInstance().getNodeRMIClient().requestRunningAnalysisChangeStatus(analyseID, AnalysisStatus.UPLOAD_ERROR);
		NodeManager.getInstance().getNodeRMIClient().requestDownloadDoneNotification(analyseID, inputFile);
	}

	@Override
	public void downloadProgress(int percentage, String analyseID, FastQFile inputFile) throws RemoteException {
		LOG.debug("Server side Download progress received : " + percentage + " % for " + inputFile.getName());
		NodeManager.getInstance().getNodeRMIClient().requestDownloadProgressNotification(percentage, analyseID, inputFile);
	}

	@Override
	public void downloadSuccess(String analyseID, FastQFile inputFile, String outputFileName) throws RemoteException {
		LOG.debug("Server side Download success received for " + inputFile.getName());
		NodeManager.getInstance().getNodeRMIClient().requestDownloadSuccessNotification(analyseID, inputFile, outputFileName);
	}
}