package rmiImplementations.clients;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import basespaceService.interfaces.IBaseSpaceModel;
import basespaceService.interfaces.IDownloadListener;

public class RMIDownloadNotifClient {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private IDownloadListener rmiDownloadListener;

	private boolean connectionOk;

	public RMIDownloadNotifClient() {

		connectionOk = false;
		try {
			LOG.debug("Request for the rmi interface");
			Registry registry = LocateRegistry.getRegistry(10001);
			rmiDownloadListener = (IDownloadListener) registry.lookup("IDownloadListener");
			LOG.debug("RMI Interface IDownloadListener retrieved from table : " + rmiDownloadListener);
			connectionOk = true;
		} catch (RemoteException e) {
			LOG.error("RemoteException " + e);
		} catch (NotBoundException e) {
			LOG.error("NotBoundException " + e);
		} catch (Throwable t) {
			LOG.error("Other exception " + t);
		}
		
	}

	public IDownloadListener getRmiDownloadListener() {
		return rmiDownloadListener;
	}

	public boolean isConnectionOk() {
		return connectionOk;
	}

}
