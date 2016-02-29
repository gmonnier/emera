package com.gmo.results.extractor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

public abstract class AnalysisExtractor implements Runnable {

	private static Logger LOG = Log4JLogger.logger;
	
	protected static final String REPORT_FILENAME = "report.ser_utf8";

	protected final static String ADDITIONAL_ANALYSIS_DIR = "Additional";

	protected String analysesDirectoryRoot;

	private static final Executor fileExtractorService = Executors.newSingleThreadExecutor();

	public AnalysisExtractor() {

		analysesDirectoryRoot = getResultsRoot();

		if (isRootValid()) {
			LOG.warn("Analyses storage directory does not exists, abort analyses extractions");
		} else {
			fileExtractorService.execute(this);
		}
	}
	
	protected abstract String getResultsRoot();

	protected abstract boolean isRootValid();
	
	protected abstract void extractAnalyses();
	
	@Override
	public void run() {
		extractAnalyses();
	}

}
