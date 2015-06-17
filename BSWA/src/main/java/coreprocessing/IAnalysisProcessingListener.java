package coreprocessing;

import model.data.ChunkResult;
import model.genelibrary.GeneLibrary;

public interface IAnalysisProcessingListener {
	
	public void analysisPreProcessStarted();

	public void analysisStarted();

	public void analysisError();

	public void analysisDone(long completionDate);
	
	public void analysisResultsReceived(ChunkResult result);
	
	public String getAnalysisID();

	public void librariesExtracted(GeneLibrary library);
	
}
