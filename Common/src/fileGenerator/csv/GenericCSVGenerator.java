package fileGenerator.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

public abstract class GenericCSVGenerator {

	private static Logger LOG = Log4JLogger.logger;

	private File outputFile;

	public GenericCSVGenerator(File outputFile) {
		this.outputFile = outputFile;
	}

	public final void generateCSVFile() throws IOException {
		// if file doesnt exists, then create it
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}

		FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		try {
			defineContent(bw);
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			bw.close();
		}
	}

	protected abstract void defineContent(BufferedWriter bw) throws IOException;
	
	protected static String formatSansExpo(double d, int nbchiffres) {

		if (Double.isInfinite(d)) {
			// return infinite unicode character
			return "\u221E";
		} else {
			String formatPattern = "%." + nbchiffres + "f";
			return String.format(formatPattern, d);
		}

	}
}
