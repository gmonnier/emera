package coreprocessing.libExtraction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.processconfig.files.ModelFileStored;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.model.genelibrary.GeneLibrary;
import com.gmo.model.genelibrary.ReferenceGene;

public class GeneLibFileExtractor {

	private static Logger LOG = Log4JLogger.logger;

	// Used to automatically extract columns indexes from csv file
	private final static String[][] SEQS = new String[][] { { "Gene", "gRNA sequence" }, { "gene_id", "seq" } };

	// Files to be extracted
	private List<ModelFileStored> input;

	public GeneLibFileExtractor(List<ModelFileStored> list) {
		this.input = list;
	}

	public GeneLibrary extractLibrary() throws LibraryExtractionException, InterruptedException {

		GeneLibrary library = new GeneLibrary();
		// Used to check for duplicates only
		Set<ReferenceGene> refGenesSet = new HashSet<ReferenceGene>();

		int duplicateCount = 0;
		BufferedReader reader = null;

		for (ModelFileStored inputFile : input) {
			
			try {
				reader = new BufferedReader(new FileReader(inputFile.getSystemFile()));

				// Extract columns indexes from first line
				String line = reader.readLine();
				int[] extractionIndexes = extractIndexes(line);

				if (extractionIndexes == null) {
					LOG.error("Unable to extract indexes parameters for " + inputFile.getSystemFile().getAbsolutePath());
					throw new LibraryExtractionException();
				}

				LOG.info("Extract library files with indes : " + Arrays.toString(extractionIndexes));

				int seqIDColumnIndex = extractionIndexes[0];
				int seqStringColumnIndex = extractionIndexes[1];
				int maxIDcolumn = Math.max(seqIDColumnIndex, seqStringColumnIndex);

				while ((line = reader.readLine()) != null) {
					String[] splitted = line.split(",");
					if (splitted != null && splitted.length >= maxIDcolumn) {
						ReferenceGene refGene = new ReferenceGene(splitted[seqIDColumnIndex], splitted[seqStringColumnIndex]);
						if (!refGenesSet.contains(refGene)) {
							library.add(refGene);
							refGenesSet.add(refGene);
						} else {
							LOG.warn(refGene + " already present in gene library, ignore it");
							duplicateCount++;
						}
					} else {
						LOG.error("Error when extracting gene library. Column index does not fit");
						throw new LibraryExtractionException();
					}
					
					if(Thread.interrupted()) {
						throw new InterruptedException();
					}
				}

			} catch (IOException e) {
				LOG.error("IOException while reading library : ", e);
				throw new LibraryExtractionException();
			} catch (Exception e) {
				LOG.error("Exception while reading library : ", e);
				throw new LibraryExtractionException();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		LOG.info("Libraries extracted : " + library.size() + " unique entries found");
		LOG.info("Duplicate ignored : " + duplicateCount);

		return library;
	}

	private int[] extractIndexes(String readLine) {

		int[] indexes = new int[] { -1, -1 };
		String[] splitted = readLine.split(",");

		for (int i = 0; i < SEQS.length; i++) {

			int currentRetIndex = 0;

			String[] requestedStrings = SEQS[i];
			Set<String> remaining = new HashSet<>();

			// Init hashset that contains all Strings of columns headers we have
			// to find
			for (int j = 0; j < requestedStrings.length; j++) {
				remaining.add(requestedStrings[j]);
			}

			for (int j = 0; j < splitted.length; j++) {
				if (remaining.contains(splitted[j])) {
					indexes[currentRetIndex++] = j;
					remaining.remove(splitted[j]);
				}
			}

			if (remaining.isEmpty()) {
				// All columns headers strings have been found.
				return indexes;
			}

		}
		return null;
	}

}
