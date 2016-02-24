package com.gmo.reports.additionnalAnalyses;

import com.gmo.model.genelibrary.ReferenceGene;

public class ReferenceGeneAndDataCouple {

	private ReferenceGene gRNA;

	private ReferenceGeneData gRNAData;

	private ReferenceGeneAndDataCouple() {

	}

	public ReferenceGeneAndDataCouple(ReferenceGene gRNA, ReferenceGeneData gRNADataInterface) {
		super();
		this.gRNA = gRNA;
		this.gRNAData = gRNADataInterface;
	}

	public ReferenceGene getgRNA() {
		return gRNA;
	}

	public void setgRNA(ReferenceGene gRNA) {
		this.gRNA = gRNA;
	}

	public ReferenceGeneData getgRNAData() {
		return gRNAData;
	}

	public void setgRNAData(ReferenceGeneData gRNAData) {
		this.gRNAData = gRNAData;
	}

}
