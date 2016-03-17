package com.gmo.sharedobjects.model.analysis;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum AnalysisStatus {
	IDLE,
	RETRIEVE_FILES,
	UPLOAD_ERROR,
	READY_FOR_PROCESSING,
	PREPROCESSING,
	RUNNING,
	RUNNING_ERROR,
	DONE
}
