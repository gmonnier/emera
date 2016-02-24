package com.gmo.reports.comparators.genes;

import java.util.Comparator;

import com.gmo.model.genelibrary.ReferenceGene;

public class SequenceComparator implements Comparator<ReferenceGene> {
	@Override
	public int compare(ReferenceGene g1, ReferenceGene g2) {
		return g1.getAssociatedSequence().compareTo(g2.getAssociatedSequence());
	}
}
