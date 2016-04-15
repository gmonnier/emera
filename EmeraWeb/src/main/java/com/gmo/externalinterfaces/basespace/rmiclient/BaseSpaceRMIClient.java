package com.gmo.externalinterfaces.basespace.rmiclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IBaseSpaceModel;
import com.gmo.basespaceService.model.UserInfo;
import com.gmo.basespaceService.model.UserRun;
import com.gmo.configuration.ApplicationContextManager;
import com.gmo.configuration.BaseSpaceContextManager;
import com.gmo.logger.Log4JLogger;

public class BaseSpaceRMIClient {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private IBaseSpaceModel rmiBSModel;

	private boolean connectionOk;

	private String clientID;

	private String clientSecret;

	private String accessToken;

	public BaseSpaceRMIClient() {

		clientSecret = BaseSpaceContextManager.getInstance().getConfig().getBsClientSecret();
		clientID = BaseSpaceContextManager.getInstance().getConfig().getBsClientID();
		accessToken = BaseSpaceContextManager.getInstance().getConfig().getBsAccessToken();

		String registryAddr = ApplicationContextManager.getInstance().getConfig().getBasespaceConnectionConfiguration().getRmiRegistryAddress();
		int registryPort = ApplicationContextManager.getInstance().getConfig().getBasespaceConnectionConfiguration().getRmiRegistryPort();
		LOG.info("Create new BaseSpace RMI client on  " + registryAddr + "    port: " + registryPort);

		connectionOk = false;
		try {
			LOG.debug("Request for the rmi interface");
			Registry registry = LocateRegistry.getRegistry();
			rmiBSModel = (IBaseSpaceModel) registry.lookup(IBaseSpaceModel.class.getSimpleName());
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
				return rmiBSModel.getListUserRuns(clientID, clientSecret, accessToken);
			} catch (RemoteException e) {
				LOG.error("RemoteException ", e);
			}
		}
		return null;
	}

	public UserInfo requestUserInfo() {
		if (rmiBSModel != null) {
			try {
				return rmiBSModel.getUserInfo(clientID, clientSecret, accessToken);
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return null;
	}

	public boolean isConnectionOk() {
		return connectionOk;
	}

	public void setConnectionOk(boolean connectionOk) {
		this.connectionOk = connectionOk;
	}
}
