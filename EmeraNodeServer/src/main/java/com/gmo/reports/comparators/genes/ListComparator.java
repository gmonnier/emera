package com.gmo.reports.comparators.genes;

import java.util.Comparator;

import com.gmo.reports.UnfoundStartSeqMap;

public class ListComparator implements Comparator<UnfoundStartSeqMap> {

	@Override
	public int compare(UnfoundStartSeqMap o1, UnfoundStartSeqMap o2) {
		return Integer.compare(o2.getTotalCount(), o1.getTotalCount());
	}

}
