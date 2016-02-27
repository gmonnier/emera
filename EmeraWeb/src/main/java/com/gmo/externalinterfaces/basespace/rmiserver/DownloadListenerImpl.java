package com.gmo.externalinterfaces.basespace.rmiserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import basespaceService.interfaces.IDownloadListener;
import basespaceService.model.FastQFile;

import com.gmo.logger.Log4JLogger;
import com.gmo.model.analysis.AnalysisStatus;
import com.gmo.model.analysis.NoSuchAnalysisException;
import com.gmo.model.inputs.ModelFileStored;

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
		try {
			Analysis analysis = AnalysisManager.getInstance().getRunningAnalysis(analyseID);
			analysis.setStatus(AnalysisStatus.UPLOAD_ERROR);
			analysis.getDownloadInfo().downloadDone(inputFile);
		} catch (NoSuchAnalysisException e) {
			LOG.debug("No analysis found with id " + analyseID);
		}
	}

	@Override
	public void downloadProgress(int percentage, String analyseID, FastQFile inputFile) throws RemoteException {
		LOG.debug("Server side Download progress received : " + percentage + " % for " + inputFile.getName());
		try {
			Analysis analysis = AnalysisManager.getInstance().getRunningAnalysis(analyseID);
			analysis.getDownloadInfo().update(inputFile, percentage);
		} catch (NoSuchAnalysisException e) {
			LOG.debug("No analysis found with id " + analyseID);
		}
	}

	@Override
	public void downloadSuccess(String analyseID, FastQFile inputFile, String outputFilePath) throws RemoteException {
		LOG.debug("Server side Download success received for " + inputFile.getName());
		// Update the storage model to retrieve new file recently downloaded from BS
		StorageConfigurationManager.getInstance().updateModel();
		try {
			Analysis analysis = AnalysisManager.getInstance().getRunningAnalysis(analyseID);
			try {
				ModelFileStored mfs = StorageConfigurationManager.getInstance().getWithPath(outputFilePath);
				analysis.getProcessConfiguration().addToData(mfs);
				analysis.getDownloadInfo().downloadDone(inputFile);
			} catch(NoSuchElementException nse) {
				LOG.debug("No element found with path " + outputFilePath);
			}
		} catch (NoSuchAnalysisException e) {
			LOG.debug("No analysis found with id " + analyseID);
		}
	}

}