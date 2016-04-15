package com.gmo.coreprocessing.analysismerger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.IAnalysisProcessingListener;
import com.gmo.coreprocessing.fastQReaderDispatcher.ChunkQueueBuffer;
import com.gmo.logger.Log4JLogger;
import com.gmo.reports.ReportWriterProvider;
import com.gmo.reports.generation.ReportWriter;
import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.reports.Report;

public class AnalysisMerger implements IMergerInfo {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private ChunkQueueBuffer chunkBuffer;

	private IAnalysisProcessingListener processinglistener;

	/**
	 * Instantiate Unique Executor service for this . This service is used to
	 * start worker threads on data analysis.
	 */
	private final static Executor dataMergerService = Executors.newSingleThreadExecutor();
	
	private Report report;
	
	// The writer used at the end of the merge process
	private ReportWriter reportWriter;

	public AnalysisMerger(Report report, ReportWriter reportWriter, ChunkQueueBuffer chunkBuffer, IAnalysisProcessingListener processinglistener) {
		this.chunkBuffer = chunkBuffer;
		this.reportWriter = reportWriter;
		this.report = report;
		this.processinglistener = processinglistener;
		
	}

	public void processChunckResult(ChunkResult chunkresult) {
		// Enqueue worker to executor service
		dataMergerService.execute(new AnalysisMergerWorker(report, chunkresult, this));
	}

	@Override
	public void mergeDone(String chunkID) {
		// Will be executed in worker thread.
		chunkBuffer.remove(chunkID);

		if (chunkBuffer.isBufferTerminated()) {
			LOG.debug("Chunk buffer terminated. All chunks received and processed : generate output files");
			long completionDate = System.currentTimeMillis();
			report.setEndDate(completionDate);
			
			dataMergerService.execute(reportWriter);

			// Analysis is finished
			if (processinglistener != null) {
				LOG.debug("Analysis done in analysis merger");
				processinglistener.analysisDone(completionDate);
			}
		}
	}

}
