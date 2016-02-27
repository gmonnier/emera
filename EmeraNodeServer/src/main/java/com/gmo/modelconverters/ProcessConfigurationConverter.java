package com.gmo.modelconverters;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import processorNode.viewmodel.ViewCreateProcessConfiguration;
import processorNode.viewmodel.ViewFile;
import processorNode.viewmodel.ViewFileOrigin;

import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.model.inputs.ModelFileStored;
import com.gmo.model.processconfiguration.ProcessConfiguration;

public class ProcessConfigurationConverter implements IViewModelConverter<ViewCreateProcessConfiguration, ProcessConfiguration> {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Override
	public ViewCreateProcessConfiguration buildViewModelObject(ProcessConfiguration dataConfigurations) {
		LOG.debug("Build view configuration from model configuration object");
		ViewCreateProcessConfiguration = new ViewCreateProcessConfiguration();

		viewConf.setOutputAttributes(dataConfigurations.getOutputAttributes());
		viewConf.setPatternAttributes(dataConfigurations.getPatternAttributes());
		viewConf.setPattern(dataConfigurations.getPattern());

		// Init list of data files that are already stored in the server
		for (ModelFileStored modelFile : conf.getSelectedDataFiles()) {
			viewConf.getSelectedDataFiles().add(new ViewFile(modelFile));
		}

		// Init list of libraries files that are already stored in the server
		for (ModelFileStored modelFile : conf.getSelectedLibraries()) {
			viewConf.getSelectedLibraries().add(new ViewFile(modelFile));
		}
	}

	@Override
	public ProcessConfiguration buildDataModelObject(ViewCreateProcessConfiguration view) {

		ProcessConfiguration dataConfiguration = new ProcessConfiguration();
		dataConfiguration.setOutputAttributes(view.getOutputAttributes());
		dataConfiguration.setPatternAttributes(view.getPatternAttributes());
		dataConfiguration.setPattern(view.getPattern());

		// Init list of data files that are already stored in the server
		for (ViewFile viewFile : view.getSelectedDataFiles()) {
			if (viewFile.getOrigin() == ViewFileOrigin.STORED) {
				try {
					dataConfiguration.addToData(StorageConfigurationManager.getInstance().getWithID(viewFile.getId()));
				} catch (NoSuchElementException e) {
					LOG.error("No element found in storage configuration manager with id " + viewFile.getId());
				}
			}
		}

		for (ViewFile viewFile : view.getSelectedLibraries()) {
			if (viewFile.getOrigin() == ViewFileOrigin.STORED) {
				try {
					dataConfiguration.addToLibraries(StorageConfigurationManager.getInstance().getWithID(viewFile.getId()));
				} catch (NoSuchElementException e) {
					LOG.error("No element found in storage configuration manager with id " + viewFile.getId());
				}
			}
		}
	}
}
