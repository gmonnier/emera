package processorNode.model;

import java.util.List;

import processorNode.model.network.ViewNetworkConfig;

public class ViewPollingInfo {

	private ViewNetworkConfig networkConfig;
	
	private List<Analysis> runningAnalysis;
	
	private List<Analysis> processedAnalysis;

	private ViewPollingInfo(){
		// Needed for deserialization
	}
	
	public ViewPollingInfo(ViewNetworkConfig networkCOnfig, List<Analysis> runningAnalysis,  List<Analysis> processedAnalysis) {
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

	public List<Analysis> getRunningAnalysis() {
		return runningAnalysis;
	}

	public void setRunningAnalysis(List<Analysis> runningAnalysis) {
		this.runningAnalysis = runningAnalysis;
	}

	public List<Analysis> getProcessedAnalysis() {
		return processedAnalysis;
	}

	public void setProcessedAnalysis(List<Analysis> processedAnalysis) {
		this.processedAnalysis = processedAnalysis;
	}
}
