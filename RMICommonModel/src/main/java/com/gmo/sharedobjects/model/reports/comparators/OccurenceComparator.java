package com.gmo.sharedobjects.model.reports.comparators;

import java.util.Comparator;

import com.gmo.sharedobjects.model.genelibrary.ReferenceGene;
import com.gmo.sharedobjects.model.reports.Report;

public class OccurenceComparator implements Comparator<ReferenceGene> {

	private Report report;

	public OccurenceComparator(Report report) {
		this.report = report;
	}

	@Override
	public int compare(ReferenceGene g1, ReferenceGene g2) {

		int occg1 = report.getOccurencesFound().get(g1.getAssociatedSequence()) == null ? 0 : report.getOccurencesFound().get(g1.getAssociatedSequence());
		int occg2 = report.getOccurencesFound().get(g2.getAssociatedSequence()) == null ? 0 : report.getOccurencesFound().get(g2.getAssociatedSequence());

		return Integer.compare(occg2, occg1);
	}
}
