package com.gmo.processorNode.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewPollingInfo;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;

public interface IProcessorNode extends Remote {

	public void requestNodeProcessorClientRemove(String clientID) throws RemoteException;

	public List<ViewFile> getListStoredData() throws RemoteException;

	public List<ViewFile> getListStoredLibraries() throws RemoteException;

	// ------ Analyses management -----

	public ViewPollingInfo getViewPollingInfo(String userID) throws RemoteException;

	public void stopAllAnalyses(String userID) throws RemoteException;

	public void requestRunningAnalysisChangeStatus(String id, AnalysisStatus newStatus) throws RemoteException;

	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewProcessConfig, String userID) throws RemoteException;

}
