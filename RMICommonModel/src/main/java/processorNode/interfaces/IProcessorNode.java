package processorNode.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import processorNode.model.ViewFile;

public interface IProcessorNode extends Remote {

	public List<ViewFile> requestNodeProcessorClientRemove(String clientID) throws RemoteException;

}
