package com.gmo.modelconverters;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.Analysis;
import com.gmo.coreprocessing.AnalysisOccurence;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;

public class AnalysisConverter implements IViewModelConverter<ViewAnalysis, Analysis> {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	@Override
	public ViewAnalysis buildViewModelObject(Analysis input) {
		ViewAnalysis viewObject = new ViewAnalysis();
		viewObject.setId(input.getId());
		viewObject.setProgress(input.getProgress());
		viewObject.setStatus(input.getStatus());
		viewObject.setUserid(input.getUserid());
		viewObject.setViewConfiguration(input.getConfiguration());
		viewObject.setReport(input.getReport());
		viewObject.setLaunchDate(input.getLaunchDate());
		viewObject.setCompletionDate(input.getCompletionDate());
		viewObject.setDownloadInfo(input.getDownloadInfo());
		
		return viewObject;
	}

	@Override
	public AnalysisOccurence buildDataModelObject(ViewAnalysis input) {
		// Should not occurs this way
		LOG.error("buildDataModelObject not supported for Analysis object");
		throw new UnsupportedOperationException();
	}

}
