package com.gmo.processorNode.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;

public interface IProcessorNotifications extends Remote {

	public void analysisCompleted(ViewAnalysis analysis) throws RemoteException;

}
