package com.gmo.configuration.xmljaxb;

import java.io.File;

import javax.management.InstanceAlreadyExistsException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

/**
 * 
 * Abstract class defining some basic behaviors used to handle configuration
 * management from xml files.
 *
 */
public abstract class AbstractConfigurationManager<MODEL> {

	static int src = 1;
	
	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private File contextXMLFile;

	private MODEL config;

	private JAXBContext jaxbContext;

	private ConfigurationWriter<MODEL> writer;

	/**
	 * 
	 * @param contextXMLFile
	 *            the input file to the xml configuration
	 * @param configInit
	 *            the init object for this configuration (Usually generated with
	 *            JAXB)
	 * @throws InstanceAlreadyExistsException
	 *             thrown if a configuration manager of the same type already
	 *             exists.
	 */
	protected AbstractConfigurationManager(File contextXMLFile, MODEL configInit) {

		this.config = configInit;
		this.contextXMLFile = contextXMLFile;

		// Check if a configuration manager has already been created for this
		// type of config.
		boolean configTypeValide = ConfigurationHandler.checkInstantiationValidity(config.getClass());
		if (!configTypeValide) {
			LOG.error("A configuration manager for the type : " + config.getClass() + " already exists! This application only allow one configuration manager by type.");
			return;
		}

		try {
			jaxbContext = JAXBContext.newInstance(config.getClass());
		} catch (JAXBException e) {
			LOG.error("Error when retrieving jaxb instance : " + contextXMLFile.getName(), e);
			return;
		}

		if (contextXMLFile.exists()) {
			LOG.debug("Unmarshal configuration file : " + contextXMLFile.getAbsolutePath());
			unmarshal();
		}

		// Instantiate the unique associated writer (manage writing in a
		// separate thread pool)
		this.writer = new ConfigurationWriter<MODEL>(jaxbContext, config, contextXMLFile);
		if (!contextXMLFile.exists()) {
			LOG.debug("Configuration file does not exists, create it : " + contextXMLFile.getAbsolutePath());
			writer.marshalXMLFileExternalThread();
		}

		//
		Thread shutdownWriter = new Thread(new ShutdownConfigurationHandler<MODEL>(writer));
		Runtime.getRuntime().addShutdownHook(shutdownWriter);

		// Notify configuration handler of successfully created manager for this
		// config type.
		ConfigurationHandler.instantiationDone(config.getClass());
	}

	/**
	 * Build the object tree from the xml input file.
	 */
	private void unmarshal() {
		try {

			LOG.debug("Return class : " + config.getClass());
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			config = (MODEL) jaxbUnmarshaller.unmarshal(contextXMLFile);

		} catch (JAXBException jbe) {
			LOG.error("Exception when extracting configuration from : " + contextXMLFile.getName(), jbe);
		}
	}

	/**
	 * 
	 * @return the associated configuration model associated with this manager.
	 */
	public MODEL getConfig() {
		return config;
	}

	public ConfigurationWriter<MODEL> getWriter() {
		return writer;
	}

}
