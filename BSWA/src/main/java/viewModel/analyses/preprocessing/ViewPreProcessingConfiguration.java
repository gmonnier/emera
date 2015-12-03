package viewModel.analyses.preprocessing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import viewModel.ViewFile;

@XmlRootElement
public class ViewPreProcessingConfiguration {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	// List of data files
	private List<ViewFile> selectedDataFiles;
	
	private List<ViewDataSplitterModel> dataSplitterModel;

	public ViewPreProcessingConfiguration() {

		selectedDataFiles = new ArrayList<ViewFile>();

	}

	public List<ViewFile> getSelectedDataFiles() {
		return selectedDataFiles;
	}

	public void setSelectedDataFiles(List<ViewFile> selectedDataFiles) {
		this.selectedDataFiles = selectedDataFiles;
	}

	public List<ViewDataSplitterModel> getDataSplitterModel() {
		return dataSplitterModel;
	}

	public void setDataSplitterModel(List<ViewDataSplitterModel> dataSplitterModel) {
		this.dataSplitterModel = dataSplitterModel;
	}

	@Override
	public String toString() {
		return "ViewPreProcessingConfiguration [selectedDataFiles=" + selectedDataFiles + "]";
	}
}
