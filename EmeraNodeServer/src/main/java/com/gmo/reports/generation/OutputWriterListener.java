package com.gmo.reports.generation;

import java.io.File;

public interface OutputWriterListener {

	public void pdfOutputGenerationFailed();

	public void csvOutputGenerationFailed();

	public void pdfOutputGenerationSucceeded(File result);

	public void csvOutputGenerationSucceeded(File result);

}
