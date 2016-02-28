package com.gmo.processorNode.viewmodel.analyses.standard.comparator;

import java.util.Comparator;

import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;

public class CompletionDateAnalysisComparator implements Comparator<ViewAnalysis> {

	@Override
	public int compare(ViewAnalysis o1, ViewAnalysis o2) {
		return Long.compare(o2.getCompletionDate(), o1.getCompletionDate());
	}

}
