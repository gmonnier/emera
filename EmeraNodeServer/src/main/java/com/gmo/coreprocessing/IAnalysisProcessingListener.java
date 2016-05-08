package com.gmo.coreprocessing;

import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;

public interface IAnalysisProcessingListener {
	
	public void analysisPreProcessStarted();

	public void analysisStarted();

	public void analysisError();

	public void analysisDone(long completionDate);
	
	public void analysisResultsReceived(ChunkResult result);
	
	public String getId();

	public void librariesExtracted(GeneLibrary library);
	
}
