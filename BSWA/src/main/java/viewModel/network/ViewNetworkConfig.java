package viewModel.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import com.amazonaws.services.ec2.model.Instance;

import processorserver.IDistantResource;
import processorserver.IResource;
import processorserver.ProcessorServerManager;
import awsinterfaceManager.AWSInterfaceManager;

@XmlRootElement
public class ViewNetworkConfig {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private List<ViewDistantResource> resources;

	private List<ViewAWSInstance> awsInstances;

	// current serve the application is running on.
	private ViewDistantResource thisServer;

	public ViewNetworkConfig() {

		Map<String, IDistantResource> listRes = ProcessorServerManager.getInstance().getMapResourcesConnected();
		resources = new ArrayList<>(listRes.size());
		
		Iterator<Map.Entry<String, IDistantResource>> it = listRes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, IDistantResource> pair = (Map.Entry<String, IDistantResource>) it.next();
			IDistantResource distResource = pair.getValue();
			ViewDistantResource res = new ViewDistantResource(distResource.getID(), distResource.getName(), distResource.getClientStatus().toString(), distResource.getLocation());
			resources.add(res);
		}

		IResource server = ProcessorServerManager.getInstance().getServerResource();
		thisServer = new ViewDistantResource(server.getID(), server.getName(), null, server.getLocation());

		List<Instance> listInstances = AWSInterfaceManager.getInstance().getAllInstances();
		awsInstances = new ArrayList<>(listInstances.size()); 

		for (int i = 0; i < listInstances.size(); i++) {
			Instance inst = listInstances.get(i);
			awsInstances.add(new ViewAWSInstance(inst.getInstanceId(), inst.getPrivateIpAddress(), inst.getPublicIpAddress(), inst.getState().getName(), inst.getInstanceType()));
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
