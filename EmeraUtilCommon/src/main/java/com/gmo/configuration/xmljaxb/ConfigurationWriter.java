package com.gmo.configuration.xmljaxb;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class ConfigurationWriter<MODEL> {

	/**
	 * Instantiate unique Executor service of the application. This service is
	 * used to save configuration files.
	 */
	private final Executor fileWriterService = Executors.newSingleThreadExecutor();
	
	private JAXBContext jaxbContext;
	
	private MODEL config;
	
	private File contextXMLFile;

	public ConfigurationWriter(JAXBContext jaxbContext, MODEL config, File contextXMLFile) {
		this.jaxbContext = jaxbContext;
		this.config = config;
		this.contextXMLFile = contextXMLFile;
	}

	/**
	 * Build the xml file from the object tree model.
	 */
	private void marshal() {
		try {
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			System.out.println("Marshalling object " + config.getClass());
			jaxbMarshaller.marshal(config, contextXMLFile);

		} catch (JAXBException e) {
			System.err.println("Exception when marshalling to following configuration file : " + contextXMLFile.getName() + "\n" + e);
		}
	}

	public synchronized void marshalXMLFileExternalThread() {

		System.out.println("Marshall XML file in file executor");

		fileWriterService.execute(new Runnable() {

			@Override
			public void run() {
				marshal();
			}
		});
	}

	public void marshalXMLFile() {
		marshal();
	}
	
	public File getContextXMLFile() {
		return contextXMLFile;
	}

}
