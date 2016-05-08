package com.gmo.coreprocessing;

import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;

public interface IAnalysisOccurenceProcessingListener extends IAnalysisProcessingListener{
	
	public void analysisResultsReceived(ChunkResult result);
	
	public String getId();

	public void librariesExtracted(GeneLibrary library);

}
