package coreprocessing.analysismerger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import reports.Report;
import reports.generation.ReportWriter;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.data.ChunkResult;

import coreprocessing.IAnalysisProcessingListener;
import coreprocessing.fastQReaderDispatcher.ChunkQueueBuffer;

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

	public AnalysisMerger(Report report, ChunkQueueBuffer chunkBuffer, IAnalysisProcessingListener processinglistener) {
		this.chunkBuffer = chunkBuffer;
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
			LOG.debug("Chunk buffer terminated. All chunks recived and processed : generate output files");
			long completionDate = System.currentTimeMillis();
			report.setEndDate(completionDate);
			
			dataMergerService.execute(new ReportWriter(report, report));

			// Analysis is finished
			if (processinglistener != null) {
				LOG.debug("Analysis done in analysis merger");
				processinglistener.analysisDone(completionDate);
			}
		}
	}

}
