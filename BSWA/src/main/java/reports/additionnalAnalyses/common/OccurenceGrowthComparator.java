package reports.additionnalAnalyses.common;

import java.util.Comparator;

import reports.additionnalAnalyses.ReferenceGeneAndDataCouple;

public class OccurenceGrowthComparator implements Comparator<ReferenceGeneAndDataCouple> {

	@Override
	public int compare(ReferenceGeneAndDataCouple g1, ReferenceGeneAndDataCouple g2) {
		if (g1.getgRNAData().getOccurenceRef() == 0 && g2.getgRNAData().getOccurenceRef() == 0) {
			return Double.compare(g2.getgRNAData().getOccurencePercComp(), g1.getgRNAData().getOccurencePercComp());
		}
		return Double.compare(g2.getgRNAData().getGrowthRate(), g1.getgRNAData().getGrowthRate());
	}
}
