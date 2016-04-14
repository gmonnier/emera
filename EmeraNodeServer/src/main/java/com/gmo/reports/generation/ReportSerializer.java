package com.gmo.reports.generation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.model.reports.Report;

public class ReportSerializer {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void writeReport(Report report, OutputStream outputStream) throws IOException {

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));
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
			List<ViewFile> mfsdata = report.getAnalyseConfig().getSelectedDataFiles();
			for (int i = 0; i < mfsdata.size(); i++) {
				ViewFile current = mfsdata.get(i);
				writer.write(current.getId());
				writer.write("#");
				writer.write(current.getName());
				writer.write("#");
				writer.write(current.getSystemFile().getAbsolutePath());
				writer.write("#");
				writer.write(Long.toString(current.getLastModified()));
				if (i != mfsdata.size() - 1) {
					writer.write("##");
				}
			}
			writer.newLine();
			List<ViewFile> mfslib = report.getAnalyseConfig().getSelectedLibraries();
			for (int i = 0; i < mfslib.size(); i++) {
				ViewFile current = mfslib.get(i);
				writer.write(current.getId());
				writer.write("#");
				writer.write(current.getName());
				writer.write("#");
				writer.write(current.getSystemFile().getAbsolutePath());
				writer.write("#");
				writer.write(Long.toString(current.getLastModified()));
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
