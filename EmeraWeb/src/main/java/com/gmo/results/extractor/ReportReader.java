package com.gmo.results.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;
import com.gmo.sharedobjects.model.reports.Report;

public class ReportReader {

	private static Logger LOG = Log4JLogger.logger;

	public static Report extractReport(File input, String userID) throws Throwable {

		BufferedReader reader = null;
		Report ret;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF8"));

			String analyseID = reader.readLine();
			long startDate = Long.parseLong(reader.readLine());
			long endDate = Long.parseLong(reader.readLine());
			long lineProcessed = Long.parseLong(reader.readLine());
			long totaloccurencesfound = Long.parseLong(reader.readLine());
			int totalchunksProcessed = Integer.parseInt(reader.readLine());
			int chunkSize = Integer.parseInt(reader.readLine());

			String libSer = reader.readLine();
			GeneLibrary lib = new GeneLibrary();
			lib.convertStringToObject(libSer);

			String configSer = reader.readLine();
			ViewCreateProcessConfiguration config = new ViewCreateProcessConfiguration(null);
			config.convertStringToObject(configSer);

			String dataFilesSer = reader.readLine();
			String[] splitted = dataFilesSer.split("##");
			if (splitted != null && splitted.length != 0) {
				// Loop over list of data files
				for (int j = 0; j < splitted.length; j++) {
					String mfsSer = splitted[j];
					String[] splitted2 = mfsSer.split("#");
					ViewFile dataFile = new ViewFile(new File(splitted2[2]));
					dataFile.setId(splitted2[0]);
					dataFile.setLastModified(Long.parseLong(splitted2[3]));
					config.getSelectedDataFiles().add(dataFile);
				}
			}

			String libsFilesSer = reader.readLine();
			splitted = libsFilesSer.split("##");
			if (splitted != null && splitted.length != 0) {
				// Loop over list of data files
				for (int j = 0; j < splitted.length; j++) {
					String mfsSer = splitted[j];
					String[] splitted2 = mfsSer.split("#");
					ViewFile libFile = new ViewFile(new File(splitted2[2]));
					libFile.setId(splitted2[0]);
					libFile.setLastModified(Long.parseLong(splitted2[3]));
					config.getSelectedLibraries().add(libFile);
				}
			}

			ret = new Report(config, startDate, analyseID, userID);

			String occurencesSer = reader.readLine();
			splitted = occurencesSer.split("##");
			if (splitted != null) {
				for (int i = 0; i < splitted.length; i++) {
					String[] subsplit = splitted[i].split("#");
					ret.getOccurencesFound().put(subsplit[0], Integer.parseInt(subsplit[1]));
				}
			} else {
				LOG.error("No occurences found for this repoert!");
			}

			ret.setEndDate(endDate);
			ret.setTotalLineProcessed(lineProcessed);
			ret.setTotalOccurencesFound(totaloccurencesfound);
			ret.setTotalChunksProcessed(totalchunksProcessed);
			ret.setChunkSize(chunkSize);
			ret.setLibrary(lib);

		} catch (Throwable e) {
			throw e;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LOG.warn("Not able to close input stream : ", e);
			}
		}

		return ret;

	}

}
