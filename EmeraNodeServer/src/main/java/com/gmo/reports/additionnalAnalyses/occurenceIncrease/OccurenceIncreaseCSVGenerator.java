package com.gmo.reports.additionnalAnalyses.occurenceIncrease;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.gmo.fileGenerator.csv.GenericCSVGenerator;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneAndDataCouple;

public class OccurenceIncreaseCSVGenerator extends GenericCSVGenerator {

	private OccurencesIncreaseReport increaseReport;

	public OccurenceIncreaseCSVGenerator(File outputFile, OccurencesIncreaseReport increaseReport) {
		super(outputFile);
		this.increaseReport = increaseReport;
	}

	@Override
	protected void defineContent(BufferedWriter bw) throws IOException {

		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("Gene ID,GRNA Seq,Occurence Ref,Occurence Ref %,Occurence Init,Occurence Init %,Growth rate");
		bw.write(sb.toString());
		bw.newLine();

		List<ReferenceGeneAndDataCouple> listDataGRNA = increaseReport.getListResult();
		for (int i = 0; i < listDataGRNA.size(); i++) {

			ReferenceGeneAndDataCouple grna = listDataGRNA.get(i);

			sb.setLength(0);
			sb.append(grna.getgRNA().getName());
			sb.append(",");
			sb.append(grna.getgRNA().getAssociatedSequence());
			sb.append(",");
			int grnaOccurenceRef = grna.getgRNAData().getOccurenceRef();
			sb.append(Integer.toString(grnaOccurenceRef));
			sb.append(",");
			sb.append(formatSansExpo(grna.getgRNAData().getOccurencePercRef(), 4));
			sb.append(",");
			int grnaOccurenceComp = grna.getgRNAData().getOccurenceComp();
			sb.append(Integer.toString(grnaOccurenceComp));
			sb.append(",");
			sb.append(formatSansExpo(grna.getgRNAData().getOccurencePercComp(), 4));
			sb.append(",");
			sb.append(formatSansExpo(grna.getgRNAData().getGrowthRate(), 4));

			bw.write(sb.toString());
			bw.newLine();
		}

	}

}
