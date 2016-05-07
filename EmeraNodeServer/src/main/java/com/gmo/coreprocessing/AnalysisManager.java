package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.externalInterfaces.rmiclient.NodeNotificationsRMIClient;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.modelconverters.AnalysisConverter;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.analyses.standard.comparator.CompletionDateAnalysisComparator;
import com.gmo.sharedobjects.model.analysis.NoSuchAnalysisException;

public class AnalysisManager {

	private List<AnalysisOccurence> runningAnalysis;

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
		runningAnalysis = new ArrayList<AnalysisOccurence>();
		analysisResultsLocationType = LocationType.LOCAL;
		analysisResultsLocation = "results";
	}

	public static synchronized AnalysisManager getInstance() {
		return AnalysisManagerHolder.instance;
	}

	public List<AnalysisOccurence> getUserRunningAnalysis(String userID) {
		List<AnalysisOccurence> runningUsersList = new ArrayList<>();
		for (AnalysisOccurence analysis : runningAnalysis) {
			if (analysis.getUserid().equals(userID)) {
				runningUsersList.add(analysis);
			}
		}
		return runningUsersList;
	}

	public List<AnalysisOccurence> getAllRunningAnalysis() {
		return runningAnalysis;
	}

	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewConfig, String userID, String bsuserID, String bsuserSecret, String bsuserToken, LocationType analysisResultsLocationType, String analysisResultsLocation) {
		// Set the results/reports locations info
		this.analysisResultsLocationType = analysisResultsLocationType;
		this.analysisResultsLocation = analysisResultsLocation;
		// Create and start the analysis
		AnalysisOccurence newAnalyse = new AnalysisOccurence(viewConfig, userID);
		newAnalyse.init(bsuserID, bsuserSecret, bsuserToken);
		runningAnalysis.add(newAnalyse);
		return newAnalyse.getId();
	}

	public void analyseFinished(AnalysisOccurence analysis) {
		LOG.debug("Analysis detected as done. analysisID = " + analysis.getAnalysisID());
		runningAnalysis.remove(analysis);

		// Notify FE server of analysis completion
		AnalysisConverter analysisConverter = new AnalysisConverter();
		NodeNotificationsRMIClient.getInstance().analysisCompleted(analysisConverter.buildViewModelObject(analysis));
	}

	public void stopAllAnalyses() {
		LOG.debug("Request to stop all currently running analyses");
		for (Iterator<AnalysisOccurence> iterator = runningAnalysis.iterator(); iterator.hasNext();) {
			AnalysisOccurence analysis = (AnalysisOccurence) iterator.next();
			analysis.stopAnalyse();
		}
		runningAnalysis.clear();
	}

	public void stopAllAnalyses(String userID) {
		LOG.debug("Request to stop all currently running analyses of userID : " + userID);
		for (int i = runningAnalysis.size() - 1; i >= 0; i--) {
			AnalysisOccurence analysis = runningAnalysis.get(i);
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
			AnalysisOccurence analyse = getRunningAnalysis(ID);
			analyse.stopAnalyse();
			boolean removed = runningAnalysis.remove(analyse);
			LOG.debug("Analyse with ID " + ID + " successfully removed : " + removed);
		} catch (NoSuchAnalysisException e) {
			LOG.error("No analyse found to be stopped with ID : " + ID);
		}
	}

	public AnalysisOccurence getRunningAnalysis(String id) throws NoSuchAnalysisException {
		if (id == null || id.isEmpty()) {
			throw new NoSuchAnalysisException();
		}
		for (AnalysisOccurence analysis : runningAnalysis) {
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
