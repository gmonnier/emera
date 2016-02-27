package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import processorNode.viewmodel.ViewCreateProcessConfiguration;

import com.gmo.logger.Log4JLogger;
import com.gmo.model.analysis.NoSuchAnalysisException;
import com.gmo.reports.comparators.analyses.CompletionDateAnalysisComparator;

public class AnalysisManager {

	private List<Analysis> runningAnalysis;

	private List<Analysis> processedAnalysis;

	private static class AnalysisManagerHolder {
		public final static AnalysisManager instance = new AnalysisManager();
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private AnalysisManager() {
		LOG.info("Instantiate Analyse manager");
		runningAnalysis = new ArrayList<Analysis>();
		processedAnalysis = new ArrayList<Analysis>();
	}

	public static synchronized AnalysisManager getInstance() {
		return AnalysisManagerHolder.instance;
	}

	public List<Analysis> getUserRunningAnalysis(String userID) {
		List<Analysis> runningUsersList = new ArrayList<>();
		for (Analysis analysis : runningAnalysis) {
			if (analysis.getUserid().equals(userID)) {
				runningUsersList.add(analysis);
			}
		}
		return runningUsersList;
	}

	public List<Analysis> getUserProcessedAnalysis(String userID) {
		List<Analysis> processedUsersList = new ArrayList<>();
		for (Analysis analysis : processedAnalysis) {
			if (analysis.getUserid().equals(userID)) {
				processedUsersList.add(analysis);
			}
		}
		return processedUsersList;
	}

	public List<Analysis> getAllRunningAnalysis() {
		return runningAnalysis;
	}

	public List<Analysis> getAllProcessedAnalysis() {
		return processedAnalysis;
	}

	public String enqueueNewAnalysis(ViewCreateProcessConfiguration processConfig, String userID) {
		
		// Update storage of default values for next analysisconfigurations
		ApplicationContextManager.getInstance().getConfig().setAllowCharacterError(processConfig.getPatternAttributes().isAllowOneMismatch());
		ApplicationContextManager.getInstance().getConfig().setAllowShifting(processConfig.getPatternAttributes().isCheckForShifted());
		ApplicationContextManager.getInstance().getConfig().setCheckForUnfoundEntries(processConfig.getOutputAttributes().isGenerateStatisticsOnUnfoundgRNA());
		ApplicationContextManager.getInstance().getConfig().setGenerateCSVOutput(processConfig.getOutputAttributes().isGenerateCSV());
		ApplicationContextManager.getInstance().getConfig().setGeneratePDFOutput(processConfig.getOutputAttributes().isGeneratePDF());
		ApplicationContextManager.getInstance().updateDefaultPatternIndex(processConfig.getPattern());
		ApplicationContextManager.getInstance().getWriter().marshalXMLFileExternalThread();

		// Create and start the analysis
		Analysis newAnalyse = new Analysis(processConfig, userID);
		newAnalyse.init();
		runningAnalysis.add(newAnalyse);

		return newAnalyse.getId();
	}

	public void analyseFinished(Analysis analysis) {
		LOG.debug("Analysis detected as done. analysisID = " + analysis.getAnalysisID());
		runningAnalysis.remove(analysis);

		processedAnalysis.add(analysis);
		Collections.sort(processedAnalysis, new CompletionDateAnalysisComparator());
	}

	public void stopAllAnalyses() {
		LOG.debug("Request to stop all currently running analyses");
		for (Iterator<Analysis> iterator = runningAnalysis.iterator(); iterator.hasNext();) {
			Analysis analysis = (Analysis) iterator.next();
			analysis.stopAnalyse();
		}
		runningAnalysis.clear();
	}

	public void stopAllAnalyses(String userID) {
		LOG.debug("Request to stop all currently running analyses of userID : " + userID);
		for (int i = runningAnalysis.size() - 1; i >= 0; i--) {
			Analysis analysis = runningAnalysis.get(i);
			if (analysis.getUserid().equals(userID)) {
				LOG.debug("Stop analyse " + analysis.getAnalysisID());
				analysis.stopAnalyse();
				runningAnalysis.remove(i);
			}
		}
	}

	public void stopAnalyse(String ID) {
		LOG.debug("Request to stop analyse with ID " + ID);
		try {
			Analysis analyse = getRunningAnalysis(ID);
			analyse.stopAnalyse();
			boolean removed = runningAnalysis.remove(analyse);
			LOG.debug("Analyse with ID " + ID + " successfully removed : " + removed);
		} catch (NoSuchAnalysisException e) {
			LOG.error("No analyse found to be stopped with ID : " + ID);
		}
	}

	public Analysis getRunningAnalysis(String id) throws NoSuchAnalysisException {
		if (id == null || id.isEmpty()) {
			throw new NoSuchAnalysisException();
		}
		for (Analysis analysis : runningAnalysis) {
			if (analysis.getId().equals(id)) {
				return analysis;
			}
		}
		throw new NoSuchAnalysisException();
	}

	public Analysis getProcessedAnalysis(String id) throws NoSuchAnalysisException {
		if (id == null || id.isEmpty()) {
			throw new NoSuchAnalysisException();
		}
		for (Analysis analysis : processedAnalysis) {
			if (analysis.getId().equals(id)) {
				return analysis;
			}
		}
		throw new NoSuchAnalysisException();
	}

}
