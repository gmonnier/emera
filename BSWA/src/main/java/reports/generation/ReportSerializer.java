package reports.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import logger.Log4JLogger;
import model.processconfig.files.ModelFileStored;

import org.apache.logging.log4j.Logger;

import reports.Report;

public class ReportSerializer {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void writeReport(Report report, File output) throws IOException {

		LOG.debug("Start writing in " + output.getAbsolutePath());
		
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF8"));
			writer.write(report.getAnalyseID());
			writer.newLine();
			writer.write(Long.toString(report.getStartDate()));
			writer.newLine();
			writer.write(Long.toString(report.getEndDate()));
			writer.newLine();
			writer.write(Long.toString(report.getTotalLineProcessed()));
			writer.newLine();
			writer.write(Long.toString(report.getTotalOccurencesFound()));
			writer.newLine();
			writer.write(Integer.toString(report.getTotalChunksProcessed()));
			writer.newLine();
			writer.write(Integer.toString(report.getChunkSize()));
			writer.newLine();
			writer.write(report.getLibrary().getObjectAsString());
			writer.newLine();
			writer.write(report.getAnalyseConfig().getObjectAsString());

			// Add associated files that are note defined into config
			// serialization
			writer.newLine();
			List<ModelFileStored> mfsdata = report.getAnalyseConfig().getSelectedDataFiles();
			for (int i = 0; i < mfsdata.size(); i++) {
				ModelFileStored current = mfsdata.get(i);
				writer.write(current.getId());
				writer.write("#");
				writer.write(current.getName());
				writer.write("#");
				writer.write(current.getSystemFile().getAbsolutePath());
				writer.write("#");
				writer.write(Long.toString(current.getLastModified().getTime()));
				if (i != mfsdata.size() - 1) {
					writer.write("##");
				}
			}
			writer.newLine();
			List<ModelFileStored> mfslib = report.getAnalyseConfig().getSelectedLibraries();
			for (int i = 0; i < mfslib.size(); i++) {
				ModelFileStored current = mfslib.get(i);
				writer.write(current.getId());
				writer.write("#");
				writer.write(current.getName());
				writer.write("#");
				writer.write(current.getSystemFile().getAbsolutePath());
				writer.write("#");
				writer.write(Long.toString(current.getLastModified().getTime()));
				if (i != mfslib.size() - 1) {
					writer.write("##");
				}
			}
			writer.newLine();

			Iterator<Map.Entry<String, Integer>> it = report.getOccurencesFound().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();
				writer.write(pair.getKey());
				writer.write("#");
				writer.write(pair.getValue().toString());
				if (it.hasNext()) {
					writer.write("##");
				}
			}
			
			LOG.debug("Written successfully into " + output.getAbsolutePath());
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}

	}

}
