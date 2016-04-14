package com.gmo.reports.generation;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.reports.Report;

public abstract class ReportWriter implements Runnable {

	private static Logger LOG = Log4JLogger.logger;

	public static final String REPORT_FILENAME = "report.ser_utf8";

	protected Report report;
	protected OutputWriterListener writerListener;
	protected String analysisResultsLocation;

	public ReportWriter(Report report, OutputWriterListener writerListener, String analysisResultsLocation) {
		this.report = report;
		this.analysisResultsLocation = analysisResultsLocation;
		this.writerListener = writerListener;

		report.finalizeReport();
	}

}
