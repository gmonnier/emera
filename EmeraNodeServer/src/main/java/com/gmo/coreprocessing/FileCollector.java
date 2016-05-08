package com.gmo.coreprocessing;

import java.util.List;

import com.gmo.processorNode.viewmodel.ViewFile;

public class FileCollector {

	private List<ViewFile> collectedFiles;
	
	private List<ViewFile> requestedFiles;

	public FileCollector(List<ViewFile> collectedFiles, List<ViewFile> requestedFiles) {
		super();
		this.collectedFiles = collectedFiles;
		this.requestedFiles = requestedFiles;
	}
	
	
}
