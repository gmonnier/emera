package com.gmo.reports.comparators.analyses;

import java.util.Comparator;

import com.gmo.coreprocessing.Analysis;

public class CompletionDateAnalysisComparator implements Comparator<Analysis> {

	@Override
	public int compare(Analysis o1, Analysis o2) {
		return Long.compare(o2.getCompletionDate(), o1.getCompletionDate());
	}

}
