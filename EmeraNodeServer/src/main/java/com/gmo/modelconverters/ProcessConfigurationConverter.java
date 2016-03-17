package com.gmo.modelconverters;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.model.processconfiguration.ProcessConfiguration;

public class ProcessConfigurationConverter implements IViewModelConverter<ViewCreateProcessConfiguration, ProcessConfiguration> {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Override
	public ViewCreateProcessConfiguration buildViewModelObject(ProcessConfiguration dataConfigurations) {
		
		LOG.debug("Build view configuration from model configuration object");
		ViewCreateProcessConfiguration viewConf = new ViewCreateProcessConfiguration(null);

		viewConf.setOutputAttributes(dataConfigurations.getOutputAttributes());
		viewConf.setPatternAttributes(dataConfigurations.getPatternAttributes());
		viewConf.setPattern(dataConfigurations.getPattern());
		
		FileStoredConverter fileConverter = new FileStoredConverter();

		// Init list of data files that are already stored in the server
		for (ModelFileStored modelFile : dataConfigurations.getSelectedDataFiles()) {
			viewConf.getSelectedDataFiles().add(fileConverter.buildViewModelObject(modelFile));
		}

		// Init list of libraries files that are already stored in the server
		for (ModelFileStored modelFile : dataConfigurations.getSelectedLibraries()) {
			viewConf.getSelectedDataFiles().add(fileConverter.buildViewModelObject(modelFile));
		}
		
		return viewConf;
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
		
		return dataConfiguration;
	}
}
