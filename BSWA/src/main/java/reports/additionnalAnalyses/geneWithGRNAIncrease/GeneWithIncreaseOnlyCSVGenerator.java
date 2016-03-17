package reports.additionnalAnalyses.geneWithGRNAIncrease;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import reports.additionnalAnalyses.ReferenceGeneAndDataCouple;
import reports.additionnalAnalyses.occurenceIncrease.OccurencesIncreaseReport;

import com.gmo.fileGenerator.csv.GenericCSVGenerator;

public class GeneWithIncreaseOnlyCSVGenerator extends GenericCSVGenerator {

	private OccurencesIncreaseReport increaseReport;

	public GeneWithIncreaseOnlyCSVGenerator(File outputFile, OccurencesIncreaseReport increaseReport) {
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
		for (int i = 0; i < listDataGRNA.size();) {

			ReferenceGeneAndDataCouple grna = listDataGRNA.get(i);

			int gRNAIncreasingCount = 0;
			List<ReferenceGeneAndDataCouple> currentList = new ArrayList<ReferenceGeneAndDataCouple>();
			while (i < listDataGRNA.size() && grna.getgRNA().getName().equals(listDataGRNA.get(i).getgRNA().getName())) {
				currentList.add(listDataGRNA.get(i));
				if (listDataGRNA.get(i).getgRNAData().getGrowthRate() > 2.0) {
					gRNAIncreasingCount++;
				}
				i++;
			}

			if (gRNAIncreasingCount < 2) {
				// current targeted gene does not get two gRNA with
				// increasing occurences between the two analyses
				continue;
			}

			for (int j = 0; j < currentList.size(); j++) {

				ReferenceGeneAndDataCouple subgrna = currentList.get(j);

				sb.setLength(0);
				sb.append(subgrna.getgRNA().getName());
				sb.append(",");
				sb.append(subgrna.getgRNA().getAssociatedSequence());
				sb.append(",");
				int grnaOccurenceRef = subgrna.getgRNAData().getOccurenceRef();
				sb.append(Integer.toString(grnaOccurenceRef));
				sb.append(",");
				sb.append(formatSansExpo(subgrna.getgRNAData().getOccurencePercRef(), 4));
				sb.append(",");
				int grnaOccurenceComp = subgrna.getgRNAData().getOccurenceComp();
				sb.append(Integer.toString(grnaOccurenceComp));
				sb.append(",");
				sb.append(formatSansExpo(subgrna.getgRNAData().getOccurencePercComp(), 4));
				sb.append(",");
				sb.append(formatSansExpo(subgrna.getgRNAData().getGrowthRate(), 4));
				
				bw.write(sb.toString());
				bw.newLine();
			}
		}

	}
}
