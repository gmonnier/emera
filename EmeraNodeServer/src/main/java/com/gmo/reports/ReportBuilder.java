package com.gmo.reports;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.reports.generation.OutputWriterListener;

public class ReportBuilder implements OutputWriterListener {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Override
	public void pdfOutputGenerationFailed() {
		LOG.error("Unable to add pdf file to output file list");
	}

	@Override
	public void csvOutputGenerationFailed() {
		LOG.error("Unable to add csv file to output file list");
	}

	@Override
	public void pdfOutputGenerationSucceeded(File result) {
		outputReportFiles.add(result);
	}

	@Override
	public void csvOutputGenerationSucceeded(File result) {
		outputReportFiles.add(result);
	}
}
