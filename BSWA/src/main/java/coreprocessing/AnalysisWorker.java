package coreprocessing;

import logger.Log4JLogger;
import model.analyses.NoSuchAnalysisException;
import model.genelibrary.GeneLibrary;
import model.processconfig.ProcessConfiguration;

import org.apache.logging.log4j.Logger;

import processorserver.ProcessorServerManager;
import coreprocessing.fastQReaderDispatcher.ChunkQueueBuffer;
import coreprocessing.fastQReaderDispatcher.DataReaderDispatcher;
import coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import coreprocessing.fastQReaderDispatcher.ReadDispatchException;
import coreprocessing.libExtraction.GeneLibFileExtractor;
import coreprocessing.libExtraction.LibraryExtractionException;

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
