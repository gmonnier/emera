package com.gmo.externalinterfaces.basespace.rmiclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IBaseSpaceModel;
import com.gmo.basespaceService.model.UserInfo;
import com.gmo.basespaceService.model.UserRun;
import com.gmo.commonconfiguration.NetworkTopologyManager;
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

	private String registryAddress;

	private int registryPort;

	public BaseSpaceRMIClient() {

		clientSecret = BaseSpaceContextManager.getInstance().getConfig().getBsClientSecret();
		clientID = BaseSpaceContextManager.getInstance().getConfig().getBsClientID();
		accessToken = BaseSpaceContextManager.getInstance().getConfig().getBsAccessToken();

		registryAddress = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryAddress();
		registryPort = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryPort();

		initConnection();
	}

	private void initConnection() {
		connectionOk = false;
		try {
			LOG.debug("Request for the rmi interface");
			Registry registry = LocateRegistry.getRegistry(registryAddress, registryPort);
			rmiBSModel = (IBaseSpaceModel) registry.lookup(IBaseSpaceModel.class.getSimpleName());
			LOG.debug("RMI Interface IBaseSpaceModel retrieved from table : " + rmiBSModel);
			connectionOk = true;
		} catch (RemoteException e) {
			LOG.error("RemoteException " + e);
		} catch (NotBoundException e) {
			LOG.error("NotBoundException " + e);
		}
	}

	private void unbindFromRegistry() {
		try {
			Registry registry = LocateRegistry.getRegistry(registryAddress, registryPort);
			registry.unbind(IBaseSpaceModel.class.getSimpleName());
		} catch (RemoteException | NotBoundException e) {
			LOG.error("Unable to unbind from registry");
		}
	}

	public List<UserRun> requestListCurrentUserRuns() {
		if (rmiBSModel != null) {
			try {
				return rmiBSModel.getListUserRuns(clientID, clientSecret, accessToken);
			} catch (java.rmi.ConnectException ce) {
				LOG.error("Connect exception while trying to attempt RMI server - remove interface from registry");
				unbindFromRegistry();
			} catch (RemoteException e) {
				LOG.error("Unable to fetch List of current users run", e);
			}
		}
		return new ArrayList<UserRun>();
	}

	public UserInfo requestUserInfo() {
		if (rmiBSModel != null) {
			try {
				return rmiBSModel.getUserInfo(clientID, clientSecret, accessToken);
			} catch (java.rmi.ConnectException ce) {
				LOG.error("Connect exception while trying to attempt RMI server - remove interface from registry");
				unbindFromRegistry();
			} catch (RemoteException e) {
				LOG.error("RemoteException " + e);
			}
		}
		return null;
	}

	public boolean isConnectionOk() {
		if (!connectionOk) {
			// Reattempt connection
			initConnection();
		}
		return connectionOk;
	}
}
