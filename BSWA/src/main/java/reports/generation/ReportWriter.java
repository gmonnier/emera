package reports.generation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import reports.Report;
import applicationconfig.ApplicationContextManager;

public class ReportWriter implements Runnable {

	private static Logger LOG = Log4JLogger.logger;
	
	public static final String REPORT_FILENAME = "report.ser_utf8";

	private Report report;

	private OutputWriterListener writerListener;
	private File outputLocation;

	public ReportWriter(Report report, OutputWriterListener writerListener) {
		this.report = report;
		report.finalizeReport();
		this.writerListener = writerListener;
		this.outputLocation = new File(ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation() + File.separator + report.getUserID());
	}

	@Override
	public void run() {

		if (report.getAnalyseID() == null || report.getAnalyseID().isEmpty()) {
			LOG.error("No analyse ID associated with current report. Exit writing output process");
			return;
		}
		
		File analysisDir = new File(outputLocation, report.getAnalyseID());
		if (!analysisDir.exists()) {
			boolean mkdirres = analysisDir.mkdirs();
			if(!mkdirres){
				LOG.warn("Uable to create directory " + analysisDir.getAbsolutePath());
			}
		} else {
			LOG.warn("Analyse results already exists with same ID!");
		}

		if (report.getAnalyseConfig().getOutputAttributes().isGenerateCSV()) {
			File csvOutput = new File(analysisDir, "csv_report.csv");
			try {
				CSVOutputGenerator.writeOutput(csvOutput, report);
				writerListener.csvOutputGenerationSucceeded(csvOutput);
			} catch (Throwable e) {
				LOG.error("CSV report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.csvOutputGenerationFailed();
			}
		}

		if (report.getAnalyseConfig().getOutputAttributes().isGeneratePDF()) {
			File pdfOutput = new File(analysisDir, "pdf_report.pdf");
			try {
				new PDFOutputGenerator(pdfOutput, report);
				writerListener.pdfOutputGenerationSucceeded(pdfOutput);
			} catch (Throwable e) {
				LOG.error("PDF report Generation Failed for analyse : " + report.getAnalyseID(), e);
				writerListener.pdfOutputGenerationFailed();
			}
		}
		
		// Serialize result object into a file
		File serializationOutput = new File(analysisDir, REPORT_FILENAME);
		try {
			ReportSerializer.writeReport(report, serializationOutput);
		} catch (Throwable e) {
			LOG.error("Unable to serialize report to file " + report.getAnalyseID(), e);
		}
	}

}
