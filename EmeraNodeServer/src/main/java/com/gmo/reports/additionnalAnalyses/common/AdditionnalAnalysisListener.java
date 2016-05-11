package com.gmo.reports.additionnalAnalyses.common;

import com.gmo.processorNode.viewmodel.ViewFile;

public interface AdditionnalAnalysisListener {

	public void additionnalAnalysisFailed(String analysisID, String reasonMessage);

	public void additionnalAnalysisPerformed(String analysisID, ViewFile outputPDF);

}
