package com.gmo.coreprocessing;

public interface IAnalysisProcessingListener {
	
	public void analysisPreProcessStarted();

	public void analysisStarted();

	public void analysisError();

	public void analysisDone(long completionDate);
	
}
