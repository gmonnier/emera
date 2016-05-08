package com.gmo.coreprocessing.fastQReaderPrinter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import com.gmo.coreprocessing.fastQReaderDispatcher.ReadDispatchException;
import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public class DataReaderPrinter {

	private static Logger LOG = Log4JLogger.logger;

	private List<ModelFileStored> listInputData;

	private volatile long totalByteReads;
	private long totalByteToProcess;
	private int currentPercent;

	private IReaderDispatcherListener dispatcherListener;

	public DataReaderPrinter(List<ModelFileStored> listInputData, IReaderDispatcherListener processListener) {

		this.listInputData = listInputData;
		this.currentPercent = 0;
		this.dispatcherListener = processListener;

		// Get the total size in bytes to be treated from the data files
		totalByteToProcess = 0;
		for (ModelFileStored dataFile : listInputData) {
			totalByteToProcess += dataFile.getSize();
		}
	}

	/**
	 * Read the input Model file and split them into outputs model files
	 * depending on the provided input patterns
	 * 
	 * @throws ReadDispatchException
	 * @throws InterruptedException
	 */
	public void readAndPrint() throws ReadDispatchException, InterruptedException {

		LOG.info("Enter reading and printing fastq input");

		int totalCount = 0;
		long totalByteReads = 0;

		for (ModelFileStored modelFileStored : listInputData) {

			BufferedReader reader = null;

			try {

				reader = new BufferedReader(new FileReader(modelFileStored.getSystemFile()));
				// First line @SEQ_ID -->skip
				String line = reader.readLine();
				totalByteReads += line.length();

				while ((line = reader.readLine()) != null) {

					totalCount++;

					// process line
					LOG.debug(line);

					totalByteReads += line.length();
					totalByteReads += skipLine(reader);
					totalByteReads += skipLine(reader);
					totalByteReads += skipLine(reader);

					// Equivalent to totalByteToProcess%1024 == 0
					if ((totalByteReads & (1 << 10)) == 0) {
						int percent = (int) ((totalByteReads * 100) / totalByteToProcess);
						if (percent != currentPercent) {
							dispatcherListener.readProgress(totalCount, percent);
							currentPercent = percent;
						}
					}

					if (Thread.interrupted()) {
						LOG.debug("Thread interrupted, throw an interrupted exception");
						throw new InterruptedException();
					}
				}

			} catch (IOException e) {
				LOG.error("IO exception thrown : ", e);
				throw new ReadDispatchException();
			} catch (InterruptedException e) {
				throw e;
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					LOG.error("Unable to close the reader stream", e);
				}
			}
		}

		LOG.debug("Reading ended : totalByteReads = " + totalByteReads + "     totalByteToProcess = " + totalByteToProcess + "     totalLineProcessed = " + totalCount);
		currentPercent = (int) ((totalByteReads * 100) / totalByteToProcess);
		dispatcherListener.readDone(totalCount);
	}

	private int skipLine(BufferedReader reader) throws IOException {
		String skippedLine = reader.readLine();
		if (skippedLine != null) {
			return skippedLine.length();
		}
		return 0;
	}

	public long getTotalByteReads() {
		return totalByteReads;
	}
}
