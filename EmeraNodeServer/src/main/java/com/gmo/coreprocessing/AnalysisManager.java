package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.View;

import org.apache.logging.log4j.Logger;

import com.gmo.externalInterfaces.rmiclient.NodeNotificationsRMIClient;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.modelconverters.AnalysisConverter;
import com.gmo.modelconverters.ProcessConfigurationConverter;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.analyses.standard.comparator.CompletionDateAnalysisComparator;
import com.gmo.sharedobjects.model.analysis.NoSuchAnalysisException;
import com.gmo.sharedobjects.model.processconfiguration.ProcessConfiguration;

public class AnalysisManager {

	private List<Analysis> runningAnalysis;

	// Those values should be initialized/synchronized by the FE server
	private LocationType analysisResultsLocationType;
	private String analysisResultsLocation;

	private static class AnalysisManagerHolder {
		public final static AnalysisManager instance = new AnalysisManager();
	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private AnalysisManager() {
		LOG.info("Instantiate Analyse manager");
		runningAnalysis = new ArrayList<Analysis>();
		analysisResultsLocationType = LocationType.LOCAL;
		analysisResultsLocation = "results";
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

	public List<Analysis> getAllRunningAnalysis() {
		return runningAnalysis;
	}

	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewConfig, String userID, String bsuserID, String bsuserSecret, String bsuserToken, LocationType analysisResultsLocationType, String analysisResultsLocation) {
		// Set the results/reports locations info
		this.analysisResultsLocationType = analysisResultsLocationType;
		this.analysisResultsLocation = analysisResultsLocation;
		// Create and start the analysis
		ProcessConfiguration processConfiguration = new ProcessConfigurationConverter().buildDataModelObject(viewConfig);
		Analysis newAnalyse = new AnalysisOccurence(processConfiguration, userID);
	
		newAnalyse.init(bsuserID, bsuserSecret, bsuserToken, viewConfig.getAllRequestedFiles());
		runningAnalysis.add(newAnalyse);
		return newAnalyse.getId();
	}

	public void analyseFinished(Analysis analysis) {
		LOG.debug("Analysis detected as done. analysisID = " + analysis.getId());
		runningAnalysis.remove(analysis);

		// Notify FE server of analysis completion
		AnalysisConverter analysisConverter = new AnalysisConverter();
		NodeNotificationsRMIClient.getInstance().analysisCompleted(analysisConverter.buildViewModelObject(analysis));
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
				LOG.debug("Stop analyse " + analysis.getId());
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

	public LocationType getAnalysisResultsLocationType() {
		return analysisResultsLocationType;
	}

	public String getAnalysisResultsLocation() {
		return analysisResultsLocation;
	}
}
