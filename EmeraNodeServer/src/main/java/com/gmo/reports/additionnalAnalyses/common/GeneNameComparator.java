package com.gmo.reports.additionnalAnalyses.common;

import java.util.Comparator;

import com.gmo.reports.additionnalAnalyses.ReferenceGeneAndDataCouple;

public class GeneNameComparator implements Comparator<ReferenceGeneAndDataCouple> {

	@Override
	public int compare(ReferenceGeneAndDataCouple g1, ReferenceGeneAndDataCouple g2) {
		
		
		if (g1.getgRNA().getName().equals(g2.getgRNA().getName())) {
			
			// same name and ref occurence is 0
			if (g1.getgRNAData().getOccurenceRef() == 0 && g2.getgRNAData().getOccurenceRef() == 0) {
				return Double.compare(g2.getgRNAData().getOccurencePercComp(), g1.getgRNAData().getOccurencePercComp());
			}
			
			return Double.compare(g2.getgRNAData().getGrowthRate(), g1.getgRNAData().getGrowthRate());
			
		} else {
			return g1.getgRNA().getName().compareTo(g2.getgRNA().getName());
		}
	}
}
