package com.gmo.reports;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.reports.generation.OutputWriterListener;
import com.gmo.reports.generation.ReportWriter;
import com.gmo.sharedobjects.model.reports.Report;

public class ReportWriterProvider implements OutputWriterListener {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public ReportWriter getReportWriter(Report report, LocationType locType, String analysisResultsLocation) {
		switch (locType) {
		case LOCAL: {
			return new FileReportWriter
			break;
		}
		case S_3: {
			break;
		}
		}
	}

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
