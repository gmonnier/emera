package com.gmo.reports.generation;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.reports.Report;

public class S3ReportWriter extends ReportWriter {
	
	private static Logger LOG = Log4JLogger.logger;

	public S3ReportWriter(Report report, OutputWriterListener writerListener, String analysisResultsLocation) {
		super(report, writerListener, analysisResultsLocation);
	}

	@Override
	public void run() {
		
		LOG.error("Start S3 Report writting in " + analysisResultsLocation);
		
		if (report.getAnalyseID() == null || report.getAnalyseID().isEmpty()) {
			LOG.error("No analyse ID associated with current report. Exit writing output process");
			return;
		}

		String outputLocation = analysisResultsLocation + File.separator + report.getUserID() + File.separator + report.getAnalyseID();

		if (report.getAnalyseConfig().getOutputAttributes().isGenerateCSV()) {
			String csvOutput = outputLocation + File.separator + "csv_report.csv";
			try {
				CSVOutputGenerator.writeOutput(csvOutput, report);
				writerListener.csvOutputGenerationSucceeded(csvOutput);
			} catch (Throwable e) {
				LOG.error("CSV report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.csvOutputGenerationFailed();
			}
		}

		if (report.getAnalyseConfig().getOutputAttributes().isGeneratePDF()) {
			String pdfOutput = outputLocation + File.separator + "pdf_report.pdf";
			try {
				new PDFOutputGenerator(pdfOutput, report);
				writerListener.pdfOutputGenerationSucceeded(pdfOutput);
			} catch (Throwable e) {
				LOG.error("PDF report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.pdfOutputGenerationFailed();
			}
		}

		// Serialize result object into a file
		String serializationOutput = outputLocation + REPORT_FILENAME;
		try {
			ReportSerializer.writeReport(report, serializationOutput);
		} catch (Throwable e) {
			LOG.error("Unable to serialize report to file " + report.getAnalyseID(), e);
		}
	}

}
