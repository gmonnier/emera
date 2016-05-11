package com.gmo.processorNode.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.gmo.generated.configuration.applicationcontext.ResultLocation;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;

public interface IProcessorNotifications extends Remote {

	public void analysisCompleted(ViewAnalysis analysis) throws RemoteException;

	public void additionnalAnalysisCompleted(String analysisID, ViewFile outputPDF) throws RemoteException;

	public void additionnalAnalysisFailed(String analysisID, String reasonMessage) throws RemoteException;

	public ResultLocation fetchResultsLocation() throws RemoteException;

}
