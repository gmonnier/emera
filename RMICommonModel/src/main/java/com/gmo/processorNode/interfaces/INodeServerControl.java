package com.gmo.processorNode.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.gmo.basespaceService.model.FastQFile;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.processorNode.viewmodel.OutputFileType;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewNodePollingInfo;
import com.gmo.processorNode.viewmodel.ViewPollingInfo;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewPreProcessingConfiguration;
import com.gmo.processorNode.viewmodel.network.ViewNodeNetworkConfig;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.reports.Report;

public interface INodeServerControl extends Remote {

	public void requestNodeProcessorClientRemove(String clientID) throws RemoteException;

	public List<ViewFile> getListStoredData() throws RemoteException;

	public List<ViewFile> getListStoredLibraries() throws RemoteException;

	// ------ Analyses management -----

	public ViewNodePollingInfo getViewNodePollingInfo(String userID) throws RemoteException;

	public void stopAllAnalyses(String userID) throws RemoteException;

	public void requestRunningAnalysisChangeStatus(String id, AnalysisStatus newStatus) throws RemoteException;

	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewProcessConfig, String userID, String bsClientID, String bsClientSecret, String bsAccessToken, LocationType resultLocType, String resultLocation) throws RemoteException;

	public String enqueueNewPreprocessingAnalysis(ViewPreProcessingConfiguration jsonConfig, String userID, String bsClientID, String bsClientSecret, String bsAccessToken) throws RemoteException;
	
	public void uploadToNodeServerDone(InputType inputType, String analyseid, String fileName) throws RemoteException;

	public void requestOccurencesIncreaseAnalysis(Report refReport, Report compReport, OutputFileType outputFileType) throws RemoteException;

	// ------- File transfert ------
	public OutputStream getOutputStream(String fileName, InputType inputType) throws IOException;

	public InputStream getInputStream(File f) throws IOException;

}
