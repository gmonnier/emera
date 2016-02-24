package com.gmo.reports.additionnalAnalyses;

public class ReferenceGeneData {

	private int occurenceRef;

	private int occurenceComp;

	private double occurencePercRef;

	private double occurencePercComp;

	private double growthRate;

	public ReferenceGeneData(int occurenceRef, int occurenceComp, double occurencePercRef, double occurencePercComp) {
		super();
		this.occurenceRef = occurenceRef;
		this.occurenceComp = occurenceComp;
		this.occurencePercRef = occurencePercRef;
		this.occurencePercComp = occurencePercComp;
		
		/*if(occurencePercRef != 0) {
			this.growthRate = occurencePercComp / occurencePercRef;
		} else {
			this.growthRate = -1.0;
		}*/
		if(occurencePercComp != 0) {
			this.growthRate = occurencePercRef / occurencePercComp;
		} else {
			this.growthRate = -1.0;
		}
	}

	public int getOccurenceRef() {
		return occurenceRef;
	}

	public void setOccurenceRef(int occurenceRef) {
		this.occurenceRef = occurenceRef;
	}

	public int getOccurenceComp() {
		return occurenceComp;
	}

	public void setOccurenceComp(int occurenceComp) {
		this.occurenceComp = occurenceComp;
	}

	public double getOccurencePercRef() {
		return occurencePercRef;
	}

	public void setOccurencePercRef(double occurencePercRef) {
		this.occurencePercRef = occurencePercRef;
	}

	public double getOccurencePercComp() {
		return occurencePercComp;
	}

	public void setOccurencePercComp(double occurencePercComp) {
		this.occurencePercComp = occurencePercComp;
	}

	public double getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

}
