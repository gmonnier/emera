package com.gmo.processorNode.viewmodel;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.network.ViewNetworkConfig;
import com.gmo.processorNode.viewmodel.network.ViewNodeNetworkConfig;

@XmlRootElement
public class ViewNodePollingInfo implements Serializable {

	private ViewNodeNetworkConfig networkConfig;

	private List<ViewAnalysis> runningAnalysis;

	private ViewNodePollingInfo() {
		// Needed for deserialization
	}

	public ViewNodePollingInfo(ViewNodeNetworkConfig networkConfig, List<ViewAnalysis> runningAnalysis) {
		super();
		this.networkConfig = networkConfig;
		this.runningAnalysis = runningAnalysis;
	}

	public ViewNodeNetworkConfig getNetworkConfig() {
		return networkConfig;
	}

	public void setNetworkConfig(ViewNodeNetworkConfig networkCOnfig) {
		this.networkConfig = networkCOnfig;
	}

	public List<ViewAnalysis> getRunningAnalysis() {
		return runningAnalysis;
	}

	public void setRunningAnalysis(List<ViewAnalysis> runningAnalysis) {
		this.runningAnalysis = runningAnalysis;
	}
}
