package com.gmo.nodes;

import org.apache.logging.log4j.Logger;

import com.gmo.externalinterfaces.nodes.rmiclient.NodeRMIClient;
import com.gmo.logger.Log4JLogger;

public class NodeManager {

	private static class ManagerHolder {
		public final static NodeManager instance = new NodeManager();
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	private NodeRMIClient rmiClient;

	private NodeManager() {
	}

	public static synchronized NodeManager getInstance() {
		return ManagerHolder.instance;
	}
	
	public NodeRMIClient getNodeRMIClient() {
		return rmiClient;
	}
}
