package com.gmo.reports;

import java.io.File;

import com.gmo.reports.generation.OutputWriterListener;

public class ReportBuilder implements OutputWriterListener {

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
