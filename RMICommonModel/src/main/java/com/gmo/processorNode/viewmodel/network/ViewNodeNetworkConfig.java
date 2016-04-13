package com.gmo.processorNode.viewmodel.network;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

@XmlRootElement
public class ViewNodeNetworkConfig implements Serializable {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private List<ViewDistantResource> resources;

	private List<ViewAWSInstance> awsInstances;
	
	private ViewDistantResource nodeServer;

	public ViewNodeNetworkConfig() {

	}

	public ViewNodeNetworkConfig(ViewDistantResource nodeServer, List<ViewDistantResource> resources, List<ViewAWSInstance> awsInstances) {
		this.resources = resources;
		this.nodeServer = nodeServer;
		this.awsInstances = awsInstances;
	}

	public List<ViewDistantResource> getResources() {
		return resources;
	}

	public void setResources(List<ViewDistantResource> resources) {
		this.resources = resources;
	}

	public ViewDistantResource getNodeServer() {
		return nodeServer;
	}

	public void setNodeServer(ViewDistantResource nodeServer) {
		this.nodeServer = nodeServer;
	}

	public List<ViewAWSInstance> getAwsInstances() {
		return awsInstances;
	}

	public void setAwsInstances(List<ViewAWSInstance> awsInstances) {
		this.awsInstances = awsInstances;
	}

}
