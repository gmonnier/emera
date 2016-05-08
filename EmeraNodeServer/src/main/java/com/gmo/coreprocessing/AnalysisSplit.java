package com.gmo.coreprocessing;


import java.util.Date;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import com.gmo.coreprocessing.fastQReaderDispatcher.ReadDispatchException;
import com.gmo.coreprocessing.fastQReaderSplitter.DataReaderSplitter;
import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public class AnalysisSplit extends Analysis implements IReaderDispatcherListener {
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	private DataReaderSplitter splitter;
	
	// Configuration of the processing
	private final ConfigurationAnalysisSplitter processConfiguration;

	public AnalysisSplit(ConfigurationAnalysisSplitter processConfiguration, String userID) {
		super(userID);
		this.processConfiguration = processConfiguration;
		
		splitter = new DataReaderSplitter(processConfiguration, this);
	}

	@Override
	public void fileCollected(InputType type, ModelFileStored mfs) {
		processConfiguration.getSelectedDataFiles().add(mfs);
	}

	@Override
	protected void cleanupAfterResourceRelease(String resourceID) {
		// Nothing to cleanup here
	}

	@Override
	protected void startProcessing() {
		try {
			splitter.readAndSplit();
		} catch (ReadDispatchException e) {
			LOG.error("Error while reading : ", e);
			setStatus(AnalysisStatus.RUNNING_ERROR);
		} catch (InterruptedException e) {
			LOG.error("Interrupted exception caught : ", e);
			setStatus(AnalysisStatus.RUNNING_ERROR);
		}
	}

	@Override
	public void stopAnalyse() {
		setStatus(AnalysisStatus.DONE);
		removeAllDistantResource();
	}

	@Override
	public void analysisResultsReceived(ChunkResult result) {
		// No remote computation for this kind of analyses
	}

	@Override
	public void readProgress(int lineRead, int percent) {
		synchronized (this) {
			progress = percent;
		}
	}

	@Override
	public void readDone(int totalCount) {
		LOG.debug("Reading done on associated data file. Number of lines read : " + totalCount);
		synchronized (this) {
			progress = 100;
			completionDate = new Date().getTime();
			setStatus(AnalysisStatus.DONE);
			AnalysisManager.getInstance().analyseFinished(this);
		}
	}

}
