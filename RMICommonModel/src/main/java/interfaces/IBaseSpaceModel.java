package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import basespaceObjects.FastQFile;
import basespaceObjects.UserInfo;
import basespaceObjects.UserRun;

public interface IBaseSpaceModel extends Remote {

	public List<UserRun> getListUserRuns() throws RemoteException;
	
	public UserInfo getUserInfo() throws RemoteException;
	
	public void requestDownload(String path, FastQFile file, String analyseID) throws RemoteException;

}
