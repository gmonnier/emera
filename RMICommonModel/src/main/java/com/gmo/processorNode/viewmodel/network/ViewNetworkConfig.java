package com.gmo.processorNode.viewmodel.network;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

@XmlRootElement
public class ViewNetworkConfig implements Serializable {

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
