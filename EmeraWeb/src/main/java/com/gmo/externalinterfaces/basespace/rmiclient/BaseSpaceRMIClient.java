package com.gmo.externalinterfaces.basespace.rmiclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IBaseSpaceModel;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.basespaceService.model.UserInfo;
import com.gmo.basespaceService.model.UserRun;
import com.gmo.logger.Log4JLogger;

public class BaseSpaceRMIClient {
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private IBaseSpaceModel rmiBSModel;
	
	private boolean connectionOk;

	public BaseSpaceRMIClient() {

		connectionOk = false;
		try {
			LOG.debug("Request for the rmi interface");
			Registry registry = LocateRegistry.getRegistry(8081);
			rmiBSModel = (IBaseSpaceModel) registry.lookup("IBaseSpaceModel");
			LOG.debug("RMI Interface IBaseSpaceModel retrieved from table : " + rmiBSModel);
			connectionOk = true;
		} catch (RemoteException e) {
			LOG.error("RemoteException " + e);
		} catch (NotBoundException e) {
			LOG.error("NotBoundException " + e);
		}
	}
	
	public List<UserRun> requestListCurrentUserRuns() {
		if (rmiBSModel != null) {
			try {
				return rmiBSModel.getListUserRuns();
			} catch (RemoteException e) {
				LOG.error("RemoteException ", e);
			}
		}
		return null;
	}
	
	public UserInfo requestUserInfo() {
		if (rmiBSModel != null) {
			try {
				return rmiBSModel.getUserInfo();
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return null;
	}
	
	public void requestStartNewDownload(String analyseID, FastQFile fastqRequest) {
		if (rmiBSModel != null) {
			try {
				String fileName = fastqRequest.getName().replaceAll(".gz", "");
				rmiBSModel.requestDownload(fileName, fastqRequest, analyseID);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
	}

	public boolean isConnectionOk() {
		return connectionOk;
	}

	public void setConnectionOk(boolean connectionOk) {
		this.connectionOk = connectionOk;
	}
}