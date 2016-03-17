package com.gmo.reports.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.genelibrary.ReferenceGene;
import com.gmo.sharedobjects.model.reports.Report;
import com.gmo.sharedobjects.model.reports.UnfoundStartSeqMap;

public class CSVOutputGenerator {

	private static Logger LOG = Log4JLogger.logger;

	public static void writeOutput(File outputFile, Report report) throws IOException {

		StringBuilder sb = new StringBuilder();
		List<ReferenceGene> genesIds = report.getLibrary().getGenes();

		// if file doesnt exists, then create it
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}

		FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (Iterator<ReferenceGene> iterator = genesIds.iterator(); iterator.hasNext();) {
			ReferenceGene gene = (ReferenceGene) iterator.next();
			sb.append(gene.getName());
			sb.append(",");
			sb.append(gene.getAssociatedSequence());
			sb.append(",");
			sb.append(report.getOccurenceCount(gene.getAssociatedSequence()));
			sb.append(",");
			sb.append(report.getOccurencePercent(gene.getAssociatedSequence()));
			bw.write(sb.toString());
			if (iterator.hasNext()) {
				bw.newLine();
			}
			sb.setLength(0);
		}

		bw.newLine();
		bw.newLine();
		bw.write("Unfound entries -->");
		bw.newLine();
		bw.newLine();

		Map<String, UnfoundStartSeqMap> unfoundEntries = report.getUncorrespondingEntry();
		Iterator<Map.Entry<String, UnfoundStartSeqMap>> it = unfoundEntries.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry<String, UnfoundStartSeqMap> pairs = (Map.Entry<String, UnfoundStartSeqMap>) it.next();
			HashMap<String, Integer> values = pairs.getValue();

			Iterator<Map.Entry<String, Integer>> itSubseqstart = values.entrySet().iterator();

			int index = 0;
			while (itSubseqstart.hasNext()) {
				Map.Entry<String, Integer> subPair = (Map.Entry<String, Integer>) itSubseqstart.next();
				if (index == 0) {
					sb.append(pairs.getKey());
					sb.append(",");
					sb.append(values.size());
					sb.append(",");
					sb.append(subPair.getKey());
					sb.append(",");
					sb.append(subPair.getValue());
				} else {
					sb.append(",");
					sb.append(",");
					sb.append(subPair.getKey());
					sb.append(",");
					sb.append(subPair.getValue());
				}

				bw.newLine();
				bw.write(sb.toString());
				sb.setLength(0);
				index++;

			}

			if (it.hasNext()) {
				bw.newLine();
			}
		}

		bw.close();

		LOG.info("output successfully written");
	}
}
