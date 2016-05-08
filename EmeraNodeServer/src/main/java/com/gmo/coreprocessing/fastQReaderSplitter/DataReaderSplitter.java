package com.gmo.coreprocessing.fastQReaderSplitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.ConfigurationAnalysisSplitter;
import com.gmo.coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import com.gmo.coreprocessing.fastQReaderDispatcher.ReadDispatchException;
import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

public class DataReaderSplitter {

	private static Logger LOG = Log4JLogger.logger;

	private volatile long totalByteReads;
	private long totalByteToProcess;
	private int currentPercent;

	private ConfigurationAnalysisSplitter processConfiguration;
	private IReaderDispatcherListener dispatcherListener;

	public DataReaderSplitter(ConfigurationAnalysisSplitter processConfiguration, IReaderDispatcherListener processListener) {

		this.currentPercent = 0;
		this.dispatcherListener = processListener;
		this.processConfiguration = processConfiguration;

		// Get the total size in bytes to be treated from the data files
		totalByteToProcess = 0;
		for (ModelFileStored dataFile : processConfiguration.getSelectedDataFiles()) {
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
	public void readAndSplit() throws ReadDispatchException, InterruptedException {

		LOG.info("Enter reading and splitting fastq input");

		Map<String, BufferedWriter> writters = new HashMap<String, BufferedWriter>();

		int totalCount = 0;
		int ignoredLinesCount = 0;
		long totalByteReads = 0;

		List<ModelFileStored> listInputData = processConfiguration.getSelectedDataFiles();
		List<DataSplitterModel> splitModel = processConfiguration.getDataSplitterModels();

		for (ModelFileStored modelFileStored : listInputData) {

			BufferedReader reader = null;

			try {

				reader = new BufferedReader(new FileReader(modelFileStored.getSystemFile()));
				// First line @SEQ_ID -->skip
				String line = reader.readLine();
				for (int i = 0; i < splitModel.size(); i++) {
					String outputName = splitModel.get(i).getOutputName();
					BufferedWriter writter = new BufferedWriter(new FileWriter(outputName));
					writters.put(outputName, writter);
					// Writte first line
					writter.write(line);
					writter.newLine();
				}

				totalByteReads += line.length();
				DataSplitterModel modelFound = null;

				while ((line = reader.readLine()) != null) {

					totalCount++;

					// process line
					modelFound = null;
					for (int i = 0; i < splitModel.size(); i++) {
						if (splitModel.get(i).fitPattern(line)) {
							modelFound = splitModel.get(i);
							modelFound.incrementSequenceCount();
							break;
						}
					}

					BufferedWriter writter = writters.get(modelFound.getOutputName());
					if (modelFound != null && writter != null) {
						totalByteReads += line.length();
						writter.write(line);
						writter.newLine();
						totalByteReads += skipAndWriteLine(reader, writter);
						totalByteReads += skipAndWriteLine(reader, writter);
						totalByteReads += skipAndWriteLine(reader, writter);
					} else {
						ignoredLinesCount++;
						totalByteReads += line.length();
						totalByteReads += skipLine(reader);
						totalByteReads += skipLine(reader);
						totalByteReads += skipLine(reader);
					}

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
		LOG.debug("Split results -->");
		for (int i = 0; i < splitModel.size(); i++) {
			LOG.debug("     model result --> nbSequence = " + splitModel.get(i).getAssociatedSequencesCount() + "   on output : " + splitModel.get(i).getOutputName());
		}
		LOG.debug("     Ignored lines count : " + ignoredLinesCount);
		LOG.debug("<-- Split results");
		currentPercent = (int) ((totalByteReads * 100) / totalByteToProcess);
		dispatcherListener.readDone(totalCount);
	}

	private int skipAndWriteLine(BufferedReader reader, BufferedWriter writer) throws IOException {
		String skippedLine = reader.readLine();
		if (skippedLine != null) {
			writer.write(skippedLine);
			writer.newLine();
			return skippedLine.length();
		}
		return 0;
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
