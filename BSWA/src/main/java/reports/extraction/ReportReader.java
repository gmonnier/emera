package reports.extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import model.processconfig.ProcessConfiguration;
import model.processconfig.files.ModelFileStored;

import org.apache.logging.log4j.Logger;

import reports.Report;

import com.gmo.logger.Log4JLogger;
import com.gmo.model.genelibrary.GeneLibrary;

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
			ProcessConfiguration config = new ProcessConfiguration(null);
			config.convertStringToObject(configSer);

			String dataFilesSer = reader.readLine();
			String[] splitted = dataFilesSer.split("##");
			if (splitted != null && splitted.length != 0) {
				// Loop over list of data files
				for (int j = 0; j < splitted.length; j++) {
					String mfsSer = splitted[j];
					String[] splitted2 = mfsSer.split("#");
					ModelFileStored mfs = new ModelFileStored(new File(splitted2[2]));
					mfs.setId(splitted2[0]);
					mfs.setLastModified(new Date(Long.parseLong(splitted2[3])));
					config.addToData(mfs);
				}
			}

			String libsFilesSer = reader.readLine();
			splitted = libsFilesSer.split("##");
			if (splitted != null && splitted.length != 0) {
				// Loop over list of data files
				for (int j = 0; j < splitted.length; j++) {
					String mfsSer = splitted[j];
					String[] splitted2 = mfsSer.split("#");
					ModelFileStored mfs = new ModelFileStored(new File(splitted2[2]));
					mfs.setId(splitted2[0]);
					mfs.setLastModified(new Date(Long.parseLong(splitted2[3])));
					config.addToLibraries(mfs);
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
