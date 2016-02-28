package com.gmo.basespaceService.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.gmo.basespaceService.model.FastQFile;
import com.gmo.basespaceService.model.UserInfo;
import com.gmo.basespaceService.model.UserRun;

public interface IBaseSpaceModel extends Remote {

	public List<UserRun> getListUserRuns() throws RemoteException;
	
	public UserInfo getUserInfo() throws RemoteException;
	
	public void requestDownload(String path, FastQFile file, String analyseID) throws RemoteException;

}
