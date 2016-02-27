package com.gmo.model.processconfig;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import processorNode.viewmodel.ViewCreateProcessConfiguration;
import processorNode.viewmodel.ViewFile;
import processorNode.viewmodel.ViewFileOrigin;

import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.model.analyses.FileUploadListener;
import com.gmo.model.inputs.ModelFileStored;
import com.gmo.model.processconfiguration.ProcessConfiguration;

public class ConfigurationBuilder {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void initConfigurationFromView(ProcessConfiguration conf, ViewCreateProcessConfiguration viewConf, FileUploadListener uploadListener) {

		conf.setOutputAttributes(viewConf.getOutputAttributes());
		conf.setPatternAttributes(viewConf.getPatternAttributes());
		conf.setPattern(viewConf.getPattern());

		// Init list of data files that are already stored in the server
		for (ViewFile viewFile : viewConf.getSelectedDataFiles()) {
			if (viewFile.getOrigin() == ViewFileOrigin.STORED) {
				try {
					conf.addToData(StorageConfigurationManager.getInstance().getWithID(viewFile.getId()));
				} catch (NoSuchElementException e) {
					LOG.error("No element found in storage configuration manager with id " + viewFile.getId());
				}
			}
		}

		for (ViewFile viewFile : viewConf.getSelectedLibraries()) {
			if (viewFile.getOrigin() == ViewFileOrigin.STORED) {
				try {
					conf.addToLibraries(StorageConfigurationManager.getInstance().getWithID(viewFile.getId()));
				} catch (NoSuchElementException e) {
					LOG.error("No element found in storage configuration manager with id " + viewFile.getId());
				}
			}
		}
	}
	
	public static void initViewConfigurationFromModel(ProcessConfiguration conf, ViewCreateProcessConfiguration viewConf) {

		LOG.debug("Build view configuration from model configuration object");
		
		viewConf.setOutputAttributes(conf.getOutputAttributes());
		viewConf.setPatternAttributes(conf.getPatternAttributes());
		viewConf.setPattern(conf.getPattern());

		// Init list of data files that are already stored in the server
		for (ModelFileStored modelFile : conf.getSelectedDataFiles()) {
			viewConf.getSelectedDataFiles().add(new ViewFile(modelFile));
		}
		
		// Init list of libraries files that are already stored in the server
		for (ModelFileStored modelFile : conf.getSelectedLibraries()) {
			viewConf.getSelectedLibraries().add(new ViewFile(modelFile));
		}

	}
}
