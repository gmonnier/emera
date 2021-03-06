package com.gmo.processorNode.viewmodel.analyses.preprocessing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.gmo.processorNode.viewmodel.ViewFile;

@XmlRootElement
public class ViewPreProcessingConfiguration implements Serializable {

	// List of data files
	private List<ViewFile> selectedDataFiles;

	private List<ViewDataSplitterModel> dataSplitterModels;

	public ViewPreProcessingConfiguration() {
		dataSplitterModels = new ArrayList<ViewDataSplitterModel>();
		selectedDataFiles = new ArrayList<ViewFile>();
	}

	public List<ViewDataSplitterModel> getDataSplitterModels() {
		return dataSplitterModels;
	}

	public void setDataSplitterModels(List<ViewDataSplitterModel> dataSplitterModels) {
		this.dataSplitterModels = dataSplitterModels;
	}

	public List<ViewFile> getSelectedDataFiles() {
		return selectedDataFiles;
	}

	public void setSelectedDataFiles(List<ViewFile> selectedDataFiles) {
		this.selectedDataFiles = selectedDataFiles;
	}

	@Override
	public String toString() {
		return "ViewPreProcessingConfiguration [selectedDataFiles=" + selectedDataFiles + "]";
	}
}
