package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import basespaceObjects.FastQFile;

public interface IDownloadListener extends Remote {
	
	public void applicationCrashed() throws RemoteException;

	public void downloadFailed(String analyseID, FastQFile inputFile) throws RemoteException;
	
	public void downloadSuccess(String analyseID, FastQFile inputFIle, String outputFIlePath) throws RemoteException;
	
	public void downloadProgress(int percentage,String analyseID, FastQFile inputFile) throws RemoteException;
	
}
