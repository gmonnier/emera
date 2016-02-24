package com.gmo.reports.comparators.genes;

import java.util.Comparator;

import com.gmo.model.genelibrary.ReferenceGene;
import com.gmo.reports.Report;

public class OccurenceComparator implements Comparator<ReferenceGene> {

	private Report analyseResult;

	public OccurenceComparator(Report analyseResult) {
		this.analyseResult = analyseResult;
	}

	@Override
	public int compare(ReferenceGene g1, ReferenceGene g2) {

		int occg1 = analyseResult.getOccurencesFound().get(g1.getAssociatedSequence()) == null ? 0 : analyseResult.getOccurencesFound().get(g1.getAssociatedSequence());
		int occg2 = analyseResult.getOccurencesFound().get(g2.getAssociatedSequence()) == null ? 0 : analyseResult.getOccurencesFound().get(g2.getAssociatedSequence());

		return Integer.compare(occg2, occg1);
	}
}
