package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.analysismerger.AnalysisMerger;
import com.gmo.coreprocessing.fastQReaderDispatcher.ChunkQueueBuffer;
import com.gmo.coreprocessing.fastQReaderDispatcher.DataReaderDispatcher;
import com.gmo.coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import com.gmo.logger.Log4JLogger;
import com.gmo.modelconverters.ProcessConfigurationConverter;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.reports.ReportWriterProvider;
import com.gmo.reports.generation.ReportWriter;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.model.processconfiguration.ProcessConfiguration;
import com.gmo.sharedobjects.model.reports.Report;

public class AnalysisOccurence extends Analysis implements IAnalysisProcessingListener, IReaderDispatcherListener {

	/**
	 * InstantiateExecutor service for this . This service is used to start
	 * worker threads on data analysis.
	 */
	private final ExecutorService dataAnalysisService = Executors.newSingleThreadExecutor();

	// Configuration of the processing
	private final ProcessConfiguration processConfiguration;

	private List<ViewFile> additionalAnalyses;

	private GeneLibrary geneLibrary;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	// Start new thread to merge results
	private AnalysisMerger merger;

	// Associated report
	private Report report;

	// Start new thread to merge results
	private final ChunkQueueBuffer buffer = new ChunkQueueBuffer();

	public AnalysisOccurence(ProcessConfiguration configuration, String userID) {
		super(userID);
		geneLibrary = null;

		this.processConfiguration = configuration;
		this.additionalAnalyses = new ArrayList<>();
	}

	protected void startProcessing() {
		report = new Report(new ProcessConfigurationConverter().buildViewModelObject(processConfiguration), launchDate, id, userid, DataReaderDispatcher.CHUNK_SIZE);
		ReportWriter reportWriter = new ReportWriterProvider().getReportWriter(report);

		merger = new AnalysisMerger(report, reportWriter, buffer, this);
		// Start new reader in separate pool thread (manage by thread pool
		// executor)
		AnalysisWorker worker = new AnalysisWorker(processConfiguration, this, this, buffer);

		dataAnalysisService.execute(worker);
	}

	public GeneLibrary getGeneLibrary() {
		return geneLibrary;
	}

	public ProcessConfiguration getProcessConfiguration() {
		return processConfiguration;
	}

	@Override
	public void analysisStarted() {
		synchronized (this) {
			setStatus(AnalysisStatus.RUNNING);
		}
	}

	@Override
	public void analysisError() {
		synchronized (this) {
			setStatus(AnalysisStatus.RUNNING_ERROR);
		}
	}

	@Override
	public void analysisPreProcessStarted() {
		synchronized (this) {
			setStatus(AnalysisStatus.PREPROCESSING);
		}
	}

	@Override
	public void analysisDone(final long dateFinished) {
		synchronized (this) {
			LOG.debug("Analysis done request to change status accordingly");
			progress = 100;
			completionDate = dateFinished;
			setStatus(AnalysisStatus.DONE);
			AnalysisManager.getInstance().analyseFinished(this);
		}
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
		}
	}

	@Override
	public void librariesExtracted(GeneLibrary library) {
		LOG.warn("Lib extracted, add to report");
		report.setLibrary(library);
		this.geneLibrary = library;
	}

	@Override
	public void analysisResultsReceived(ChunkResult result) {
		synchronized (this) {
			if (merger == null) {
				LOG.warn("Merger is null, it seems results were received for analysis ID " + id + "  but merger has not been initialized yet, or has been destroyed");
				return;
			}
			// Start processing in a new thread
			merger.processChunckResult(result);
		}
	}

	public ChunkQueueBuffer getBuffer() {
		return buffer;
	}

	public void stopAnalyse() {
		merger = null;
		dataAnalysisService.shutdownNow();
		setStatus(AnalysisStatus.DONE);
		removeAllDistantResource();
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<ViewFile> getAdditionalAnalyses() {
		return additionalAnalyses;
	}

	public void setAdditionalAnalyses(List<ViewFile> additionalAnalyses) {
		this.additionalAnalyses = additionalAnalyses;
	}

	@Override
	protected void cleanupAfterResourceRelease(String resourceID) {
		if (buffer != null) {
			buffer.releaseChunks(resourceID);
		}
	}

	@Override
	public void fileCollected(InputType type, ModelFileStored mfs) {
		switch (type) {
		case LIBRARY: {
			processConfiguration.addToLibraries(mfs);
			break;
		}
		case DATA: {
			processConfiguration.addToData(mfs);
		}
		}
	}
}
