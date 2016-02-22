package processorNode.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import processorNode.viewmodel.ViewFile;

public interface IProcessorNode extends Remote {

	public void requestNodeProcessorClientRemove(String clientID) throws RemoteException;
	
	public List<ViewFile> getListStoredData() throws RemoteException;
	
	public List<ViewFile> getListStoredLibraries() throws RemoteException;

}
