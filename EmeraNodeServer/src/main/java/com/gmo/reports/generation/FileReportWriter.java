package com.gmo.reports.generation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.reports.Report;

public class FileReportWriter extends ReportWriter {

	private static Logger LOG = Log4JLogger.logger;

	public FileReportWriter(Report report, OutputWriterListener writerListener, String analysisResultsLocation) {
		super(report, writerListener, analysisResultsLocation);
	}

	@Override
	public void run() {

		LOG.error("Start Local File Report writting in " + analysisResultsLocation);

		if (report.getAnalyseID() == null || report.getAnalyseID().isEmpty()) {
			LOG.error("No analyse ID associated with current report. Exit writing output process");
			return;
		}

		File outputLocation = new File(analysisResultsLocation + File.separator + report.getUserID());
		File analysisDir = new File(outputLocation, report.getAnalyseID());
		if (!analysisDir.exists()) {
			boolean mkdirres = analysisDir.mkdirs();
			if (!mkdirres) {
				LOG.warn("Uable to create directory " + analysisDir.getAbsolutePath());
			}
		} else {
			LOG.warn("Analyse results already exists with same ID!");
		}

		if (report.getAnalyseConfig().getOutputAttributes().isGenerateCSV()) {
			File csvOutput = new File(analysisDir, "csv_report.csv");
			try {
				// if file doesnt exists, then create it
				if (!csvOutput.exists()) {
					try {
						csvOutput.createNewFile();
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				CSVOutputGenerator.writeOutput(new FileOutputStream(csvOutput), report);
				writerListener.csvOutputGenerationSucceeded(csvOutput.getAbsolutePath());
			} catch (Throwable e) {
				LOG.error("CSV report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.csvOutputGenerationFailed();
			}
		}

		if (report.getAnalyseConfig().getOutputAttributes().isGeneratePDF()) {
			File pdfOutput = new File(analysisDir, "pdf_report.pdf");
			try {
				// if file doesnt exists, then create it
				if (!pdfOutput.exists()) {
					try {
						pdfOutput.createNewFile();
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				new PDFOutputGenerator(new FileOutputStream(pdfOutput), report);
				writerListener.pdfOutputGenerationSucceeded(pdfOutput.getAbsolutePath());
			} catch (Throwable e) {
				LOG.error("PDF report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.pdfOutputGenerationFailed();
			}
		}

		// Serialize result object into a file
		File serializationOutput = new File(analysisDir, REPORT_FILENAME);
		try {
			ReportSerializer.writeReport(report, new FileOutputStream(serializationOutput));
		} catch (Throwable e) {
			LOG.error("Unable to serialize report to file " + report.getAnalyseID(), e);
		}
	}
}