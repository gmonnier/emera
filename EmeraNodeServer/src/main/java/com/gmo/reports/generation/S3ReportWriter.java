package com.gmo.reports.generation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.reports.Report;
import com.gmo.systemUtil.SystemCommand;

import awsinterfaceManager.AWSS3InterfaceManager;

public class S3ReportWriter extends ReportWriter {

	private static Logger LOG = Log4JLogger.logger;

	public S3ReportWriter(Report report, OutputWriterListener writerListener, String analysisResultsLocation) {
		super(report, writerListener, analysisResultsLocation);
	}

	@Override
	public void run() {
		
		report.finalizeReport();

		LOG.error("Start S3 Report writting in " + analysisResultsLocation);

		if (report.getAnalyseID() == null || report.getAnalyseID().isEmpty()) {
			LOG.error("No analyse ID associated with current report. Exit writing output process");
			return;
		}

		String outputRelativeLocation = report.getUserID() + "/" + report.getAnalyseID();
		File tmpOutputDir = new File("tmp" + "/" + outputRelativeLocation);
		if (!tmpOutputDir.exists()) {
			boolean mkDirResult = tmpOutputDir.mkdirs();
			if (!mkDirResult) {
				LOG.error("Temporary directory creation failed");
				return;
			}
		}

		if (report.getAnalyseConfig().getOutputAttributes().isGenerateCSV()) {

			String filename = "csv_report.csv";
			try {
				File csvOutputTmpFile = new File(tmpOutputDir, filename);
				// if file doesnt exists, then create it
				if (!csvOutputTmpFile.exists()) {
					try {
						csvOutputTmpFile.createNewFile();
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				CSVOutputGenerator.writeOutput(new FileOutputStream(csvOutputTmpFile), report);

				String csvS3Key = outputRelativeLocation + "/" + filename;
				AWSS3InterfaceManager.getInstance().uploadFile(analysisResultsLocation, csvS3Key, csvOutputTmpFile);

				writerListener.csvOutputGenerationSucceeded("https://s3.amazonaws.com/" + analysisResultsLocation + "/" + csvS3Key);
			} catch (Throwable e) {
				LOG.error("CSV report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.csvOutputGenerationFailed();
			}
		}

		if (report.getAnalyseConfig().getOutputAttributes().isGeneratePDF()) {

			String filename = "pdf_report.pdf";
			try {
				File pdfOutputTmpFile = new File(tmpOutputDir, filename);
				// if file doesnt exists, then create it
				if (!pdfOutputTmpFile.exists()) {
					try {
						pdfOutputTmpFile.createNewFile();
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				new PDFOutputGenerator(new FileOutputStream(pdfOutputTmpFile), report);

				String pdfS3Key = outputRelativeLocation + "/" + filename;
				AWSS3InterfaceManager.getInstance().uploadFile(analysisResultsLocation, pdfS3Key, pdfOutputTmpFile);

				writerListener.pdfOutputGenerationSucceeded("https://s3.amazonaws.com/" + analysisResultsLocation + "/" + pdfS3Key);
			} catch (Throwable e) {
				LOG.error("PDF report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.pdfOutputGenerationFailed();
			}
		}

		// Serialize result object into a file
		File serializedOutputTmpFile = new File(tmpOutputDir, REPORT_FILENAME);
		try {
			ReportSerializer.writeReport(report, new FileOutputStream(serializedOutputTmpFile));

			String serializationS3OutputKey = outputRelativeLocation + "/" + REPORT_FILENAME;
			AWSS3InterfaceManager.getInstance().uploadFile(analysisResultsLocation, serializationS3OutputKey, serializedOutputTmpFile);
		} catch (Throwable e) {
			LOG.error("Unable to serialize report to file " + report.getAnalyseID(), e);
			return;
		}

		new SystemCommand().removeAllINDirectory("tmp");
	}

}
