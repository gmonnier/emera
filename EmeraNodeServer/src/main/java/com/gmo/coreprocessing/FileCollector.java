package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.List;

import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.util.FileCollectorListener;

public class FileCollector {

	private List<ViewFile> collectedFiles;

	private List<ViewFile> requestedFiles;

	// Listener. Notified each time a file is added to this configuration.
	private transient FileCollectorListener uploadListener;

	public FileCollector(List<ViewFile> requestedFiles) {
		super();
		this.collectedFiles = new ArrayList<>();
		this.requestedFiles = requestedFiles;
	}

	public void setUploadListener(FileCollectorListener uploadListener) {
		this.uploadListener = uploadListener;
		uploadListener.checkCollectedFiles();
	}

	public void fileCollected(InputType data, ModelFileStored mfs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkCollectedFiles() {
		
		boolean allFilesUploaded = requestedFiles.size() == collectedFiles.size();

		if (allFilesUploaded) {
			LOG.debug("All files successfully collected, analysis is ready to start.");
			setStatus(AnalysisStatus.READY_FOR_PROCESSING);
		}
	}

}
