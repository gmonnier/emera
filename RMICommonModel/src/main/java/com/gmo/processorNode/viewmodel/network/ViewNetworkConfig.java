package com.gmo.processorNode.viewmodel.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.network.location.ClientLocation;
import com.gmo.sharedobjects.client.ClientStatus;

@XmlRootElement
public class ViewNetworkConfig {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private List<ViewDistantResource> resources;

	private List<ViewAWSInstance> awsInstances;

	// current serve the application is running on.
	private ViewDistantResource thisServer;
	
	public ViewNetworkConfig() {
		
	}

	public ViewNetworkConfig(ViewDistantResource thisServer, List<ViewDistantResource> resources, List<ViewAWSInstance> awsInstances) {

		this.thisServer = thisServer;
		this.resources = resources;
		this.awsInstances = awsInstances;
		IResource server = ProcessorServerManager.getInstance().getServerResource();
		thisServer = new ViewDistantResource(server.getID(), server.getName(), null, server.getLocation());
		
		Map<String, IDistantResource> listRes = ProcessorServerManager.getInstance().getMapResourcesConnected();
		resources = new ArrayList<>(listRes.size());
		
		List<Instance> listInstances = AWSInterfaceManager.getInstance().getAllInstances();
		awsInstances = new ArrayList<>(listInstances.size());

		if (userID.equals("guest")) {
			// Stub resources
			ViewDistantResource res = new ViewDistantResource("ID 1", "Resource name", ClientStatus.IDLE.toString() , ClientLocation.stubLocation("163.168.80.10", "Irvine", 33.6694, -117.8231));
			resources.add(res);
			ViewDistantResource res2 = new ViewDistantResource("ID 2", "Resource name 2", ClientStatus.IDLE.toString() , ClientLocation.stubLocation("184.168.85.3", "Zurich", 47.3667, 8.5500));
			resources.add(res2);
			ViewDistantResource res3 = new ViewDistantResource("ID 3", "Resource name 3", ClientStatus.PROCESSING.toString() , ClientLocation.stubLocation("183.143.7.31", "Farnay", 45.4942, 4.5986));
			resources.add(res3);
			ViewDistantResource res4 = new ViewDistantResource("ID 4", "Resource name 4", ClientStatus.PROCESSING.toString() , ClientLocation.stubLocation("145.143.7.31", "Baltimore", 39.2833, -76.6167));
			resources.add(res4);
			ViewDistantResource res5 = new ViewDistantResource("ID 5", "Resource name 5", ClientStatus.RETRIEVING_DATA.toString() , ClientLocation.stubLocation("145.143.7.31", "San Francisco", 37.7833, -122.4167));
			resources.add(res5);
		} else {
			Iterator<Map.Entry<String, IDistantResource>> it = listRes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, IDistantResource> pair = (Map.Entry<String, IDistantResource>) it.next();
				IDistantResource distResource = pair.getValue();
				ViewDistantResource res = new ViewDistantResource(distResource.getID(), distResource.getName(), distResource.getClientStatus().toString(), distResource.getLocation());
				resources.add(res);
			}

			for (int i = 0; i < listInstances.size(); i++) {
				Instance inst = listInstances.get(i);
				awsInstances.add(new ViewAWSInstance(inst.getInstanceId(), inst.getPrivateIpAddress(), inst.getPublicIpAddress(), inst.getState().getName(), inst.getInstanceType()));
			}
		}
	}

	public List<ViewDistantResource> getResources() {
		return resources;
	}

	public void setResources(List<ViewDistantResource> resources) {
		this.resources = resources;
	}

	public ViewDistantResource getThisServer() {
		return thisServer;
	}

	public void setThisServer(ViewDistantResource thisServer) {
		this.thisServer = thisServer;
	}

	public List<ViewAWSInstance> getAwsInstances() {
		return awsInstances;
	}

	public void setAwsInstances(List<ViewAWSInstance> awsInstances) {
		this.awsInstances = awsInstances;
	}

}
