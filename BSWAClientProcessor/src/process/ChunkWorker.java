package process;

import java.util.List;
import java.util.Map;

import logger.Log4JLogger;
import model.data.ChunkResult;
import model.data.DataChunk;
import model.genelibrary.GeneLibrary;
import model.genelibrary.ReferenceGene;
import model.parameters.PartialProcessConfiguration;

import org.apache.logging.log4j.Logger;

public class ChunkWorker implements Runnable {

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private IWorkerProgress listener;

	private DataChunk chunk;

	private GeneLibrary library;

	private PartialProcessConfiguration configuration;

	private boolean errorAllowed;

	private boolean allowShift;

	private int minIndex;
	
	private int maxIndex;
	
	private ChunkResult finalResult;
	
	public ChunkWorker(DataChunk chunk, ChunkResult finalResult, int minIndex, int maxIndex, IWorkerProgress listener, GeneLibrary lib, PartialProcessConfiguration configuration) {
		this.chunk = chunk;
		this.listener = listener;
		this.library = lib;
		this.configuration = configuration;
		this.minIndex = minIndex;
		this.maxIndex = maxIndex;
		this.finalResult = finalResult;

		errorAllowed = configuration.getPatternAttributes().isAllowOneMismatch();
		allowShift = configuration.getPatternAttributes().isCheckForShifted();
	}

	@Override
	public void run() {

		LOG.debug("Start processing sub chunk " + chunk.getChunkID() + " from i = " + minIndex + "   to   " + maxIndex);

		Map<String, Integer> occurences = finalResult.getOccurences();
		int skippedLength = configuration.getPattern().getSkippedCharCount();
		int grnaLength = configuration.getPattern().getGrnaSubSequenceLength();
		List<ReferenceGene> listgRNALib = library.getGenes();
		List<String> linesToProcess = chunk.getLinesToProcess();

		for (int k = minIndex; k < maxIndex; k++) {

			String line = linesToProcess.get(k);

			grna: for (int i = 0; i < listgRNALib.size(); i++) {

				// grna : for (int i = 0; i < grnaGenes.size(); i++) {
				String grnaSeq = listgRNALib.get(i).getAssociatedSequence();

				// check if strings are equals
				boolean strEqu = true;
				boolean strShiftEqu = true;

				boolean errAllowedCurrent = errorAllowed;
				boolean errAllowedShiftCurrent = errorAllowed;

				patternlook: for (int j = 0; j < grnaLength; j++) {

					char currentgrnaChar = grnaSeq.charAt(j);
					int lineIndex = skippedLength + j;
					try {
						char currentCharRead = line.charAt(lineIndex);

						if (strEqu && currentCharRead != currentgrnaChar) {
							if (!errAllowedCurrent) {
								strEqu = false;
							} else {
								// One error occured in the sequence.
								errAllowedCurrent = false;
							}
						}

						if (allowShift) {
							char currentCharShiftedRead = line.charAt(lineIndex + 1);
							if (strShiftEqu && currentCharShiftedRead != currentgrnaChar) {
								if (!errAllowedShiftCurrent) {
									strShiftEqu = false;
								} else {
									// One error occured in shifted the
									// sequence.
									errAllowedShiftCurrent = false;
								}
							}
						}

						if (!strEqu && !strShiftEqu) {
							break patternlook;
						}

					} catch (IndexOutOfBoundsException ooe) {
						LOG.error("Invalid line with current pattern: " + line);
						break patternlook;
					}
				}

				if (strEqu || (allowShift && strShiftEqu)) {
					Integer occ = occurences.get(grnaSeq);
					if (occ != null) {
						occurences.put(grnaSeq, occ + 1);
					} else {
						occurences.put(grnaSeq, 1);
					}
					break grna;
				}
			}
			
			if (Thread.interrupted()) {
		        // We've been interrupted: exit run method
				LOG.debug("Current worker thread has been interrupted. Exit run method");
		        return;
		    }
		}

		finalResult.incrementLinesProcessed(maxIndex - minIndex);
		listener.workerDone();

	}

}
