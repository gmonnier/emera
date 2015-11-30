package rmiImplementations;

import interfaces.IBaseSpaceModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import logger.Log4JLogger;
import main.BaseSpacePlatformManager;

import org.apache.logging.log4j.Logger;

import basespaceObjects.FastQFile;
import basespaceObjects.UserInfo;
import basespaceObjects.UserRun;

public class BaseSpaceModelImpl extends UnicastRemoteObject implements IBaseSpaceModel {

	private static final long serialVersionUID = 1L;
	
	private final static Logger LOG = Log4JLogger.logger;

	public BaseSpaceModelImpl() throws RemoteException {
		super();
	}

	@Override
	public List<UserRun> getListUserRuns() throws RemoteException {
		LOG.info("Asking for list of current user run");
		return BaseSpacePlatformManager.getInstance().getBSUserModel().getListRunsCurrentUser();
	}

	@Override
	public UserInfo getUserInfo() throws RemoteException {
		LOG.info("Asking for current user info");
		return BaseSpacePlatformManager.getInstance().getBSUserModel().getCurrentUserInfo();
	}

	@Override
	public void requestDownload(String path, FastQFile file, String analyseID) throws RemoteException {
		LOG.info("Request to downoad " + path + " to : " + file + "     for analyse : " + analyseID);
		BaseSpacePlatformManager.getInstance().requestNewDownload(path, file, analyseID);
	}

	
}