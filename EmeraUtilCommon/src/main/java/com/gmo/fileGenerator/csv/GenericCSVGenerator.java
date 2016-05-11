package com.gmo.fileGenerator.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

public abstract class GenericCSVGenerator {

	private static Logger LOG = Log4JLogger.logger;

	private OutputStream outputStream;

	public GenericCSVGenerator(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public final void generateCSVFile() throws IOException {

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));

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
