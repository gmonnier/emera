package com.gmo.reports.additionnalAnalyses.occurenceIncrease;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gmo.model.reports.Report;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneAndDataCouple;
import com.gmo.reports.additionnalAnalyses.common.GeneNameComparator;
import com.gmo.reports.additionnalAnalyses.common.OccurenceGrowthComparator;

public class OccurencesIncreaseReport {

	private List<ReferenceGeneAndDataCouple> listResult;
	
	private Report reportRef;
	
	private Report reportComp;

	public OccurencesIncreaseReport(Report reportRef, Report reportComp) {
		listResult = new ArrayList<ReferenceGeneAndDataCouple>();
		this.reportRef = reportRef;
		this.reportComp = reportComp;
	}

	public void addReferenceGeneData(ReferenceGeneAndDataCouple couple) {
		listResult.add(couple);
	}

	public void sortByGrowthRate() {
		Collections.sort(listResult,new OccurenceGrowthComparator());
	}

	public void sortByGeneName() {
		Collections.sort(listResult,new GeneNameComparator());
	}
	
	public List<ReferenceGeneAndDataCouple> getListResult() {
		return listResult;
	}

	public void setListResult(List<ReferenceGeneAndDataCouple> listResult) {
		this.listResult = listResult;
	}

	public Report getReportRef() {
		return reportRef;
	}

	public Report getReportComp() {
		return reportComp;
	}
	
	
}
