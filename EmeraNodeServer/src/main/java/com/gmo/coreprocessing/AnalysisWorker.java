package com.gmo.coreprocessing;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.fastQReaderDispatcher.ChunkQueueBuffer;
import com.gmo.coreprocessing.fastQReaderDispatcher.DataReaderDispatcher;
import com.gmo.coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import com.gmo.coreprocessing.fastQReaderDispatcher.ReadDispatchException;
import com.gmo.coreprocessing.libExtraction.GeneLibFileExtractor;
import com.gmo.coreprocessing.libExtraction.LibraryExtractionException;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorserver.ProcessorServerManager;
import com.gmo.sharedobjects.model.analysis.NoSuchAnalysisException;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;
import com.gmo.sharedobjects.model.processconfiguration.ProcessConfiguration;

public class AnalysisWorker implements Runnable {

	private IAnalysisProcessingListener processinglistener;
	
	private IReaderDispatcherListener dispatcherlistener;

	private ProcessConfiguration configuration;

	private ChunkQueueBuffer chunkBuffer;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public AnalysisWorker(ProcessConfiguration configuration, IAnalysisProcessingListener processinglistener,IReaderDispatcherListener dispatcherlistener, ChunkQueueBuffer chunkBuffer) {
		LOG.debug("Create analysis worker for analysis ID " + processinglistener.getAnalysisID());
		this.processinglistener = processinglistener;
		this.dispatcherlistener = dispatcherlistener;
		this.configuration = configuration;
		this.chunkBuffer = chunkBuffer;
	}

	@Override
	public void run() {

		LOG.debug("Entering run method in AnalysisWorker for analysis " + processinglistener.getAnalysisID());

		processinglistener.analysisPreProcessStarted();

		GeneLibFileExtractor libExtractor = new GeneLibFileExtractor(configuration.getSelectedLibraries());
		GeneLibrary library;

		// Extract libraries files
		try {
			library = libExtractor.extractLibrary();
			processinglistener.librariesExtracted(library);
		} catch (LibraryExtractionException e) {
			LOG.error("Exception catched while extraction libraries, switching state of current analysis to error");
			processinglistener.analysisError();
			return;
		} catch(InterruptedException ie) {
			LOG.debug("Library extraction interrupted");
			return;
		}

		processinglistener.analysisStarted();
		
		try {
			ProcessorServerManager.getInstance().requestAllAvailableResources(AnalysisManager.getInstance().getRunningAnalysis(processinglistener.getAnalysisID()));
		} catch (NoSuchAnalysisException e1) {
			LOG.error("No analysis found with given ID " + processinglistener.getAnalysisID());
		}

		try {
			new DataReaderDispatcher(configuration.getSelectedDataFiles(), dispatcherlistener, chunkBuffer).readAndDispatch();
		} catch (ReadDispatchException e) {
			LOG.debug("Exception catched while reading input data files.");
			processinglistener.analysisError();
			return;
		} catch(InterruptedException ie) {
			LOG.debug("DataReaderDispatcher interrupted, exit run method");
			return;
		}

	}

}
