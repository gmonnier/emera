package com.gmo.sharedobjects.model.processconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.util.FileCollectorListener;
import com.gmo.sharedobjects.util.StringSerializable;

public class ProcessConfiguration extends PartialProcessConfiguration implements Serializable, StringSerializable {
	
	// List of library files
	private List<ModelFileStored> selectedLibraries;
	
	// List of library files
	private List<ModelFileStored> selectedDataFiles;
	
	// Listener. Notified each time a file is added to this configuration.
	private transient FileCollectorListener uploadListener;
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	public ProcessConfiguration() {
		LOG.debug("Create new process config ");
		selectedLibraries = new ArrayList<ModelFileStored>();
		selectedDataFiles = new ArrayList<ModelFileStored>();
		
	}

	public void setUploadListener(FileCollectorListener uploadListener) {
		this.uploadListener = uploadListener;
		uploadListener.checkCollectedFiles();
	}

	public List<ModelFileStored> getSelectedLibraries() {
		return selectedLibraries;
	}

	public void setSelectedLibraries(List<ModelFileStored> selectedLibraries) {
		this.selectedLibraries = selectedLibraries;
	}

	public List<ModelFileStored> getSelectedDataFiles() {
		return selectedDataFiles;
	}

	public void setSelectedDataFiles(List<ModelFileStored> selectedDataFiles) {
		this.selectedDataFiles = selectedDataFiles;
	}

	@Override
	public String toString() {
		return "ProcessConfiguration -----> \n pattern=" + pattern + "\npatternAttributes=" + patternAttributes + "\noutputAttributes=" + outputAttributes + "\nselectedLibraries=" + selectedLibraries
				+ ", selectedDataFiles=" + selectedDataFiles + "]";
	}

	public void addToLibraries(ModelFileStored modelUploaded) {
		selectedLibraries.add(modelUploaded);
		if(uploadListener != null) {
			LOG.debug("Add new file to process configuration libraries : " + modelUploaded.getName());
			uploadListener.fileCollected(modelUploaded);
		}
	}
	
	public void addToData(ModelFileStored modelUploaded) {
		selectedDataFiles.add(modelUploaded);
		if(uploadListener != null) {
			LOG.debug("Add new file to process configuration data : " + modelUploaded.getName());
			uploadListener.fileCollected(modelUploaded);
		}
	}
}
