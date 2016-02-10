package com.gmo.configuration.xmlcustom;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public abstract class AXMLGenerator<T> {
	
	/**
	 * Instantiate unique Executor service of the application. This service is used to save configuration files.
	 */
	private static final Executor fileWriterService = Executors.newSingleThreadExecutor();
	
	public void saveXMLFile(T configObject,final File outputFile) {

		// generate document
		final Document doc = buildDocument(configObject);
		
		// write into the file (into a separate thread);
		if (doc != null) {

			fileWriterService.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						writeXmlDocument(doc, outputFile);
					} catch (IOException e) {
						System.err.println("Error while trying to save XML config file : IOException when writting into " + outputFile.getName());
					}
				}
			});
			
		} else {
			System.err.println("Error while trying to save XML config file : document has not been correctly generated");
		}
	}

	/**
	 * 
	 * @param document
	 *            Document object to be written in file
	 * @param fichier
	 *            output file name
	 * @throws IOException
	 *             Whether file can not be created
	 */
	private static void writeXmlDocument(Document document, File file) throws IOException {
		// Creation de la source DOM
		Source source = new DOMSource(document);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException exc) {
				System.err.println("Impossible to create XML file : " + file.getAbsolutePath());
			}
		}

		Result resultat = new StreamResult(file);

		try {
			// Configuration du transformer
			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer;

			transformer = fabrique.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			transformer.transform(source, resultat);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Return the object to be extracted from the file
	protected abstract Document buildDocument(T configObject);
}
