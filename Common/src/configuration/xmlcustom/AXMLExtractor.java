package configuration.xmlcustom;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class AXMLExtractor<T> {
	
	private File file;

	/**
	 * Extract a campaign from a file name
	 * 
	 * @param listePresetFiles
	 *            file name to look at
	 * @return campaign
	 */
	public T extractFromFile(File inputFile) throws IOException {

		this.file = inputFile;
		Document document;

		document = parseFile(inputFile);
		Node root = document.getDocumentElement();
		return extractData(root);

	}

	// Return the object to be extracted from the file
	protected abstract T extractData(Node root);

	public File getFile() {
		return file;
	}

	/**
	 * Parses XML file and returns XML document.
	 * 
	 * @param fileName
	 *            XML file to parse
	 * @return XML document or <B>null</B> if error occured
	 */
	protected static Document parseFile(File file) throws IOException {
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("Wrong parser configuration: " + e.getMessage());
			return null;
		}
		File sourceFile = file;
		try {
			doc = docBuilder.parse(sourceFile);
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		}
		return doc;

	}

	/**
	 * Returns element value
	 * 
	 * @param elem
	 *            element (it is XML tag)
	 * @return Element value otherwise empty String
	 */
	protected static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * 
	 * @param node
	 *            Node on which we want to get the attribute value
	 * @param AttributeName
	 *            attibute name
	 * @return The value corresponding to the attribute name. Return an empty
	 *         String if attribute is not found.
	 */
	public static String getAttributeValue(Node node, String AttributeName) {
		// retrieving attributes
		NamedNodeMap node_attributes = node.getAttributes();

		for (int k = 0; k < node_attributes.getLength(); k++) {

			Node attribute = node_attributes.item(k);

			if (attribute.getNodeName().toUpperCase().equals(AttributeName.toUpperCase())) {
				return attribute.getNodeValue();
			}
		}
		return "";
	}
}
