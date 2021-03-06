package com.gmo.processorNode.viewmodel;

import javax.xml.bind.annotation.XmlRootElement;

import com.gmo.sharedobjects.model.analysis.AnalysisStatus;

@XmlRootElement
public class StatusChangeRequest {

	private String analyseId;
	
	private AnalysisStatus newStatus;
	
	private StatusChangeRequest() {
		
	}

	public String getAnalyseId() {
		return analyseId;
	}

	public void setAnalyseId(String analyseId) {
		this.analyseId = analyseId;
	}

	public AnalysisStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(AnalysisStatus newStatus) {
		this.newStatus = newStatus;
	}

	@Override
	public String toString() {
		return "StatusChangeRequest [analyseId=" + analyseId + ", newStatus=" + newStatus + "]";
	}
}
