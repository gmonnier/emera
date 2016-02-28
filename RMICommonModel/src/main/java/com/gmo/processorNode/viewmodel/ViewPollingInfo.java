package com.gmo.processorNode.viewmodel;

import java.util.List;

import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.network.ViewNetworkConfig;

public class ViewPollingInfo {

	private ViewNetworkConfig networkConfig;
	
	private List<ViewAnalysis> runningAnalysis;
	
	private List<ViewAnalysis> processedAnalysis;

	private ViewPollingInfo(){
		// Needed for deserialization
	}
	
	public ViewPollingInfo(ViewNetworkConfig networkCOnfig, List<ViewAnalysis> runningAnalysis,  List<ViewAnalysis> processedAnalysis) {
		super();
		this.networkConfig = networkCOnfig;
		this.runningAnalysis = runningAnalysis;
		this.processedAnalysis = processedAnalysis;
	}

	public ViewNetworkConfig getNetworkConfig() {
		return networkConfig;
	}

	public void setNetworkConfig(ViewNetworkConfig networkCOnfig) {
		this.networkConfig = networkCOnfig;
	}

	public List<ViewAnalysis> getRunningAnalysis() {
		return runningAnalysis;
	}

	public void setRunningAnalysis(List<ViewAnalysis> runningAnalysis) {
		this.runningAnalysis = runningAnalysis;
	}

	public List<ViewAnalysis> getProcessedAnalysis() {
		return processedAnalysis;
	}

	public void setProcessedAnalysis(List<ViewAnalysis> processedAnalysis) {
		this.processedAnalysis = processedAnalysis;
	}
}
