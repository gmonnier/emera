package com.gmo.reports.additionnalAnalyses.common;

import java.io.File;

public interface AdditionnalAnalysisListener {

	public void additionnalAnalysisFailed(String reasonMessage);
	
	public void additionnalAnalysisPerformed(File outputPDF);
	
}
