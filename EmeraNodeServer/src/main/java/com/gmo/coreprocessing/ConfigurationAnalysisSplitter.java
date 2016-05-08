package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.List;

import com.gmo.coreprocessing.fastQReaderSplitter.DataSplitterModel;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public class ConfigurationAnalysisSplitter {

	// List of library files
	private List<ModelFileStored> selectedDataFiles;

	private List<DataSplitterModel> dataSplitterModels;

	public ConfigurationAnalysisSplitter() {
		this.selectedDataFiles = new ArrayList<>();
		this.dataSplitterModels = new ArrayList<>();
	}

	public List<ModelFileStored> getSelectedDataFiles() {
		return selectedDataFiles;
	}

	public void setSelectedDataFiles(List<ModelFileStored> selectedDataFiles) {
		this.selectedDataFiles = selectedDataFiles;
	}

	public List<DataSplitterModel> getDataSplitterModels() {
		return dataSplitterModels;
	}

	public void setDataSplitterModels(List<DataSplitterModel> dataSplitterModels) {
		this.dataSplitterModels = dataSplitterModels;
	}
	
	
}
