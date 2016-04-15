package com.gmo.commonconfiguration;

import java.io.File;

import com.gmo.configuration.xmljaxb.AbstractConfigurationManager;
import com.gmo.generated.configuration.basespace.BaseSpaceConfiguration;
import com.gmo.generated.configuration.networktopology.NetworkTopology;

public class NetworkTopologyManager extends
		AbstractConfigurationManager<NetworkTopology> {

	private final static String contextFile = "conf/NetworkTopology.xml";

	private static NetworkTopologyManager instance;

	private NetworkTopologyManager() {
		super(new File(contextFile), new NetworkTopology());
	}

	public static NetworkTopologyManager getInstance() {
		if (instance == null) {
			instance = new NetworkTopologyManager();
		}
		return instance;
	}

}
