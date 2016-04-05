package com.gmo.processorNode.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.gmo.basespaceService.model.FastQFile;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewPollingInfo;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.inputs.InputType;

public interface IProcessorNode extends Remote {

	public void requestNodeProcessorClientRemove(String clientID) throws RemoteException;

	public List<ViewFile> getListStoredData() throws RemoteException;

	public List<ViewFile> getListStoredLibraries() throws RemoteException;

	// ------ Analyses management -----

	public ViewPollingInfo getViewPollingInfo(String userID) throws RemoteException;

	public void stopAllAnalyses(String userID) throws RemoteException;

	public void requestRunningAnalysisChangeStatus(String id, AnalysisStatus newStatus) throws RemoteException;

	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewProcessConfig, String userID) throws RemoteException;
	
	public void uploadToNodeServerDone(InputType inputType, String analyseid, String fileName) throws RemoteException;

	// ------- File transfert ------
	public OutputStream getOutputStream(String fileName, InputType inputType) throws IOException;

	public InputStream getInputStream(File f) throws IOException;

	// ------- Transfert Notifications ------
	public void requestDownloadDoneNotification(String analyseID, FastQFile inputFile) throws RemoteException;

	public void requestDownloadProgressNotification(int percentage, String analyseID, FastQFile inputFile) throws RemoteException;

	public void requestDownloadSuccessNotification(String analyseID, FastQFile inputFile, String outputFileName) throws RemoteException;

}