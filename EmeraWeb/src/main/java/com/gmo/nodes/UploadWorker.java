package com.gmo.nodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

public class UploadWorker implements Runnable {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private String writeLocation;

	private InputStream inputStream;

	private File outputFile;

	public UploadWorker(String writeLocation, InputStream inputStream) {
		LOG.debug("Create new upload worker --> writing destination : " + writeLocation);
		this.writeLocation = writeLocation;
		this.inputStream = inputStream;
	}

	public void run() {

		LOG.debug("Upload worker started to write to " + writeLocation);

		outputFile = new File(writeLocation);
		OutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(writeLocation));
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
		} catch (IOException e) {
			outputFile = null;
			LOG.error("Error while writing file to " + writeLocation, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOG.error("Impossible to close the file output stream for " + writeLocation);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOG.error("Impossible to close the file input stream for " + writeLocation);
				}
			}
		}

	}

	public File getOutputFile() {
		return outputFile;
	}

}
