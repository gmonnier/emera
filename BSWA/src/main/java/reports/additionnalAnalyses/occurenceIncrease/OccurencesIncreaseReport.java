package reports.additionnalAnalyses.occurenceIncrease;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import reports.Report;
import reports.additionnalAnalyses.ReferenceGeneAndDataCouple;
import reports.additionnalAnalyses.common.GeneNameComparator;
import reports.additionnalAnalyses.common.OccurenceGrowthComparator;

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
