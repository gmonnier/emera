package com.gmo.coreprocessing.fastQReaderPrinter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.rmi.CORBA.Util;

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

	 class OccVal {
		 
		int occurence;
		 
		String value;

		public OccVal(int occurence, String value) {
			super();
			this.occurence = occurence;
			this.value = value;
		}

		public int getOccurence() {
			return occurence;
		}

		public void setOccurence(int occurence) {
			this.occurence = occurence;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void incrementCount() {
			occurence++;
		}

		@Override
		public String toString() {
			return "OccVal [occurence=" + occurence + ", value=" + value + "]";
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
			Map<String, OccVal> list = new HashMap<>();
			try {

				reader = new BufferedReader(new FileReader(modelFileStored.getSystemFile()));
				// First line @SEQ_ID -->skip
				String line = reader.readLine();
				totalByteReads += line.length();

				while ((line = reader.readLine()) != null) {

					totalCount++;

					// process line
					if (line.length() > 61) {
						String att = line.substring(1, 9);

						OccVal count = list.get(att);
						if (count == null) {
							list.put(att, new OccVal(1, att));
						} else {
							count.incrementCount();
						}
					} else {
						// LOG.debug("Ignore line : " + line);
					}
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

			Iterator it = list.entrySet().iterator();
			List<OccVal> sorted = new ArrayList<OccVal>();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				LOG.debug(pair.getKey() + " = " + pair.getValue());
				sorted.add((OccVal) pair.getValue());
			}
			Collections.sort(sorted, new Comparator<OccVal>() {

				@Override
				public int compare(OccVal o1, OccVal o2) {
					return Integer.compare(o1.getOccurence(), o2.getOccurence());
				}
			});
			LOG.debug("SORTED VALUES : " + sorted);
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
