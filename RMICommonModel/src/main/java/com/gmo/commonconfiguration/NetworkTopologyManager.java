package com.gmo.commonconfiguration;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.gmo.configuration.xmljaxb.AbstractConfigurationManager;
import com.gmo.generated.configuration.networktopology.NetworkTopology;
import com.gmo.generated.configuration.networktopology.RmiInterface;

public class NetworkTopologyManager extends AbstractConfigurationManager<NetworkTopology> {

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

	public RmiInterface getByRmiInterfaceName(String name) {

		List<RmiInterface> availableInterfaces = getConfig().getRmiNetworkConfig().getRmiInterfaces();

		for (Iterator<RmiInterface> iterator = availableInterfaces.iterator(); iterator.hasNext();) {
			RmiInterface rmiInterface = (RmiInterface) iterator.next();
			if (rmiInterface.getValue().equals(name)) {
				return rmiInterface;
			}
		}

		return null;
	}

}
