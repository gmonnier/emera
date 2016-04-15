package com.gmo.reports.generation;

import java.io.File;

public interface OutputWriterListener {

	public void pdfOutputGenerationFailed();

	public void csvOutputGenerationFailed();

	public void pdfOutputGenerationSucceeded(String resultPath);

	public void csvOutputGenerationSucceeded(String resultPath);

}
