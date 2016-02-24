package com.gmo.processorserver;

import java.net.ServerSocket;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.network.location.ClientLocation;
import com.gmo.network.location.LocationLookup;

public class ServerResourceImpl implements IResource {

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private ServerSocket serverSock;

	private final static String serverName = "Application Main Server";
	private ClientLocation serverLocation;

	public ServerResourceImpl(ServerSocket serverSocket) {

		this.serverSock = serverSocket;

		serverLocation = LocationLookup.getLocation(serverSock.getInetAddress());
	}

	@Override
	public String getID() {
		return serverSock.getInetAddress().getHostAddress();
	}

	@Override
	public String getName() {
		return serverName;
	}

	@Override
	public ClientLocation getLocation() {
		return serverLocation;
	}

}
