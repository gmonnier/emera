package com.gmo.processorNode.viewmodel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewCompareAnalysisParam {

	private String analysisID1;

	private String analysisID2;
	
	private OutputFileType outputFileType;

	public ViewCompareAnalysisParam() {
		outputFileType = OutputFileType.PDF;
	}

	public String getAnalysisID1() {
		return analysisID1;
	}

	public void setAnalysisID1(String analysisID1) {
		this.analysisID1 = analysisID1;
	}

	public String getAnalysisID2() {
		return analysisID2;
	}

	public void setAnalysisID2(String analysisID2) {
		this.analysisID2 = analysisID2;
	}

	public OutputFileType getOutputFileType() {
		return outputFileType;
	}

	public void setOutputFileType(OutputFileType outputFileType) {
		this.outputFileType = outputFileType;
	}
}
