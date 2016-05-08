package com.gmo.modelconverters;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.coreprocessing.ConfigurationAnalysisSplitter;
import com.gmo.coreprocessing.fastQReaderSplitter.DataSplitterModel;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewDataSplitterModel;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewPreProcessingConfiguration;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public class PreProcessConfigurationConverter implements IViewModelConverter<ViewPreProcessingConfiguration, ConfigurationAnalysisSplitter> {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Override
	public ViewPreProcessingConfiguration buildViewModelObject(ConfigurationAnalysisSplitter input) {

		LOG.debug("Build view configuration from model configuration object");
		ViewPreProcessingConfiguration viewConf = new ViewPreProcessingConfiguration();

		DataSplitterConverter splitterModelConverter = new DataSplitterConverter();
		for (DataSplitterModel splitterModel : input.getDataSplitterModels()) {
			viewConf.getDataSplitterModels().add(splitterModelConverter.buildViewModelObject(splitterModel));
		}

		FileStoredConverter fileConverter = new FileStoredConverter();
		// Init list of data files that are already stored in the server
		for (ModelFileStored modelFile : input.getSelectedDataFiles()) {
			viewConf.getSelectedDataFiles().add(fileConverter.buildViewModelObject(modelFile));
		}

		return viewConf;
	}

	@Override
	public ConfigurationAnalysisSplitter buildDataModelObject(ViewPreProcessingConfiguration view) {

		ConfigurationAnalysisSplitter dataConfiguration = new ConfigurationAnalysisSplitter();

		// Init list of data files that are already stored in the server
		for (ViewFile viewFile : view.getSelectedDataFiles()) {
			if (viewFile.getOrigin() == ViewFileOrigin.STORED) {
				try {
					dataConfiguration.getSelectedDataFiles().add(StorageConfigurationManager.getInstance().getWithID(viewFile.getId()));
				} catch (NoSuchElementException e) {
					LOG.error("No element found in storage configuration manager with id " + viewFile.getId());
				}
			}
		}

		DataSplitterConverter fileConverter = new DataSplitterConverter();
		// Init list of data files that are already stored in the server
		for (ViewDataSplitterModel viewDataSplitterModel : view.getDataSplitterModels()) {
			dataConfiguration.getDataSplitterModels().add(fileConverter.buildDataModelObject(viewDataSplitterModel));
		}

		return dataConfiguration;
	}

}
