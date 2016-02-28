package processorNode.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import processorNode.viewmodel.ViewFile;
import processorNode.viewmodel.ViewPollingInfo;

public interface IProcessorNode extends Remote {

	public void requestNodeProcessorClientRemove(String clientID) throws RemoteException;
	
	public List<ViewFile> getListStoredData() throws RemoteException;
	
	public List<ViewFile> getListStoredLibraries() throws RemoteException;
	
	// ------ Analyzes management -----

	public ViewPollingInfo getViewPollingInfo(String userID) throws RemoteException;
}
