package com.gmo.nodes;

import org.apache.logging.log4j.Logger;

import com.gmo.externalinterfaces.nodes.rmiclient.NodeRMIClient;
import com.gmo.logger.Log4JLogger;
import com.gmo.network.location.ClientLocation;
import com.gmo.network.location.LocationLookup;
import com.gmo.processorNode.viewmodel.network.ViewDistantResource;

public class NodeManager {

	private static class ManagerHolder {
		public final static NodeManager instance = new NodeManager();
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

df

	private ViewDistantResource frontEndServer;

	private NodeManager() {
		rmiClient = new NodeRMIClient();
		ClientLocation serverLocation = LocationLookup.getLocation("127.0.0.1");
		frontEndServer = new ViewDistantResource("127.0.0.1", "FE Server", "-", serverLocation);
	}

	public static synchronized NodeManager getInstance() {
		return ManagerHolder.instance;
	}

	public NodeRMIClient getNodeRMIClient() {
		return rmiClient;
	}

	public ViewDistantResource getFrontEndServer() {
		return frontEndServer;
	}
}
