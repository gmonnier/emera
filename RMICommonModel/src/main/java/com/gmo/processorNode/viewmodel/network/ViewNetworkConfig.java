package com.gmo.processorNode.viewmodel.network;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

@XmlRootElement
public class ViewNetworkConfig implements Serializable {

	private ViewNodeNetworkConfig resources;

	// current serve the application is running on.
	private ViewDistantResource frontEndServer;

	public ViewNetworkConfig() {

	}

	public ViewNetworkConfig(ViewDistantResource frontEndServer, ViewNodeNetworkConfig resources) {
		this.frontEndServer = frontEndServer;
		this.resources = resources;
	}

	public ViewNodeNetworkConfig getResources() {
		return resources;
	}

	public void setResources(ViewNodeNetworkConfig resources) {
		this.resources = resources;
	}

	public ViewDistantResource getFrontEndServer() {
		return frontEndServer;
	}

	public void setFrontEndServer(ViewDistantResource frontEndServer) {
		this.frontEndServer = frontEndServer;
	}

}
