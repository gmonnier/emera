package com.gmo.reports;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.reports.generation.FileReportWriter;
import com.gmo.reports.generation.OutputWriterListener;
import com.gmo.reports.generation.ReportWriter;
import com.gmo.reports.generation.S3ReportWriter;
import com.gmo.sharedobjects.model.reports.Report;

public class ReportWriterProvider implements OutputWriterListener {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public ReportWriter getReportWriter(Report report, LocationType locType, String analysisResultsLocation) {
		switch (locType) {
		case LOCAL: {
			return new FileReportWriter(report, this, analysisResultsLocation);
		}
		case S_3: {
			return new S3ReportWriter(report, this, analysisResultsLocation);
		}
		}
		return null;
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
	public void pdfOutputGenerationSucceeded(String resultPath) {
		LOG.error("PDF report success to : " + resultPath);
	}

	@Override
	public void csvOutputGenerationSucceeded(String resultPath) {
		LOG.error("CSV report success to : " + resultPath);
	}
}
