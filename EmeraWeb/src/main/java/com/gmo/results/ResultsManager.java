package com.gmo.results;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import processorNode.viewmodel.analyses.standard.ViewAnalysis;

import com.gmo.logger.Log4JLogger;
import com.gmo.model.analysis.NoSuchAnalysisException;

public class ResultsManager {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.connectionlogger;
	
	private List<ViewAnalysis> processedAnalysis;

	private static class ResultsManagerHolder {
		public final static ResultsManager instance = new ResultsManager();
	}

	public static synchronized ResultsManager getInstance() {
		return ResultsManagerHolder.instance;
	}

	// Constructor
	private ResultsManager() {
		processedAnalysis = new ArrayList<ViewAnalysis>();
	}
	
	public List<ViewAnalysis> getAllProcessedAnalysis() {
		return processedAnalysis;
	}
	
	public List<ViewAnalysis> getUserProcessedAnalysis(String userID) {
		List<ViewAnalysis> processedUsersList = new ArrayList<>();
		for (ViewAnalysis analysis : processedAnalysis) {
			if (analysis.getUserid().equals(userID)) {
				processedUsersList.add(analysis);
			}
		}
		return processedUsersList;
	}

	public ViewAnalysis getProcessedAnalysis(String id) throws NoSuchAnalysisException {
		if (id == null || id.isEmpty()) {
			throw new NoSuchAnalysisException();
		}
		for (ViewAnalysis analysis : processedAnalysis) {
			if (analysis.getId().equals(id)) {
				return analysis;
			}
		}
		throw new NoSuchAnalysisException();
	}

}
