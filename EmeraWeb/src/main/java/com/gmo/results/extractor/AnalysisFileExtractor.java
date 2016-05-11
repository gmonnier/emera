package com.gmo.results.extractor;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.ApplicationContextManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.results.ResultsManager;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.reports.Report;

public class AnalysisFileExtractor extends AnalysisExtractor {
	
	private static Logger LOG = Log4JLogger.logger;

	@Override
	public boolean isRootValid() {
		return new File(analysesDirectoryRoot).exists();
	}
	
	@Override
	protected String getResultsRoot() {
		return ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation().getAnalysisResultsLocationRoot();
	}

	@Override
	protected void extractAnalyses() {
		File[] listUsersDir = new File(analysesDirectoryRoot).listFiles();
		if (listUsersDir == null) {
			LOG.warn("No users results repository, exits extractor thread.");
			return;
		}

		for (int k = 0; k < listUsersDir.length; k++) {

			if (!listUsersDir[k].isDirectory()) {
				continue;
			}

			String userID = listUsersDir[k].getName();
			File[] listAnalysisDir = listUsersDir[k].listFiles();

			if (listAnalysisDir == null) {
				LOG.warn("Analyses directory is empty, exits extractor thread.");
			}

			for (int i = 0; i < listAnalysisDir.length; i++) {
				File analyseDir = listAnalysisDir[i];
				if (analyseDir.isDirectory()) {

					String analyseID = analyseDir.getName();
					LOG.debug("Extract new analyse with ID " + analyseID);
					File[] listFiles = analyseDir.listFiles();
					File reportSerialized = null;
					if (listFiles != null) {
						for (int j = 0; j < listFiles.length; j++) {
							if (listFiles[j].getName().equals(REPORT_FILENAME)) {
								reportSerialized = listFiles[j];
							}
						}
					}

					if (reportSerialized != null) {

						try {

							Report report = ReportReader.extractReport(new FileInputStream(reportSerialized), userID);

							ViewAnalysis analysisDone = new ViewAnalysis();
							analysisDone.setId(analyseID);
							analysisDone.setUserid(userID);
							analysisDone.setStatus(AnalysisStatus.DONE);
							analysisDone.setLaunchDate(report.getStartDate());
							analysisDone.setCompletionDate(report.getEndDate());
							analysisDone.setReport(report);
							analysisDone.setViewConfiguration(report.getAnalyseConfig());

							extractAdditionnalAnalyses(analysisDone, new File(analyseDir, ADDITIONAL_ANALYSIS_DIR));

							ResultsManager.getInstance().addProcessedAnalysis(analysisDone);

						} catch (Throwable ex) {
							LOG.error("Unable to deserialize Report file", ex);
						}
					} else {
						LOG.warn("No serialized report found in " + analyseDir.getAbsolutePath());
					}
				}
			}
		}
	}

	private void extractAdditionnalAnalyses(ViewAnalysis analysisDone, File additionalAnalysesDirectory) {
		LOG.debug(additionalAnalysesDirectory.getAbsolutePath());
		if (additionalAnalysesDirectory.exists()) {
			File[] listAdditional = additionalAnalysesDirectory.listFiles();
			if (listAdditional == null || listAdditional.length == 0) {
				LOG.debug("No additional analyses found for " + analysisDone.getId() + " in " + additionalAnalysesDirectory.getAbsolutePath());
			} else {
				for (int i = 0; i < listAdditional.length; i++) {
					analysisDone.getAdditionalAnalyses().add(new ViewFile(listAdditional[i]));
				}
			}
		} else {
			LOG.debug("No additional analyses found for " + analysisDone.getId());
		}
	}

}
