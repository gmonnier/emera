package com.gmo.results.extractor;

import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.Logger;

import awsinterfaceManager.AWSS3InterfaceManager;

import com.gmo.configuration.ApplicationContextManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.results.ResultsManager;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.reports.Report;

public class AnalysisS3Extractor extends AnalysisExtractor {

	private static Logger LOG = Log4JLogger.logger;

	@Override
	public boolean isRootValid() {
		return AWSS3InterfaceManager.getInstance().isBucketValid(analysesDirectoryRoot);
	}

	@Override
	protected String getResultsRoot() {
		return ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation();
	}

	@Override
	protected void extractAnalyses() {

		List<String> userRepositories = AWSS3InterfaceManager.getInstance().listUsersRepositories(analysesDirectoryRoot);
		if (userRepositories.isEmpty()) {
			LOG.warn("No users results repository, exits extractor thread.");
			return;
		}

		for (int k = 0; k < userRepositories.size(); k++) {

			String userID = userRepositories.get(k);
			List<String> listAnalysisDir = AWSS3InterfaceManager.getInstance().listDirectories(analysesDirectoryRoot, userID);

			if (listAnalysisDir.isEmpty()) {
				LOG.warn("Analyses results directory for user " + userID + " is empty, exits extractor thread.");
			}

			LOG.warn("Extracting analyses for user " + userID);

			for (int i = 0; i < listAnalysisDir.size(); i++) {

				String analysePath = listAnalysisDir.get(i);
				LOG.warn("\tExtracting analysis " + i + "/" + listAnalysisDir.size() + analysePath);

				String[] path = analysePath.split("/");
				String analyseID = path[path.length - 1];

				List<Object[]> listFiles = AWSS3InterfaceManager.getInstance().listFiles(analysesDirectoryRoot, analysePath);

				String reportSerialized = analysePath + REPORT_FILENAME;
				InputStream reportStream = AWSS3InterfaceManager.getInstance().getObjectInputStream(analysesDirectoryRoot, reportSerialized);

				try {

					Report report = ReportReader.extractReport(reportStream, userID);

					ViewAnalysis analysisDone = new ViewAnalysis();
					analysisDone.setId(analyseID);
					analysisDone.setUserid(userID);
					analysisDone.setStatus(AnalysisStatus.DONE);
					analysisDone.setLaunchDate(report.getStartDate());
					analysisDone.setCompletionDate(report.getEndDate());
					analysisDone.setReport(report);
					analysisDone.setViewConfiguration(report.getAnalyseConfig());

					extractAdditionnalAnalyses(analysisDone, analysePath);

					ResultsManager.getInstance().addProcessedAnalysis(analysisDone);

				} catch (Throwable ex) {
					LOG.error("Unable to deserialize Report file", ex);
				}

			}
		}
		LOG.warn("Analysis extraction done");
	}

	private void extractAdditionnalAnalyses(ViewAnalysis analysisDone, String analysisPath) {

		String additionnalPath = analysisPath + ADDITIONAL_ANALYSIS_DIR;

		List<Object[]> listAdditionnalFiles = AWSS3InterfaceManager.getInstance().listFiles(analysesDirectoryRoot, additionnalPath);

		if (listAdditionnalFiles.isEmpty()) {
			LOG.debug("No additional analyses found for " + analysisDone.getId() + " in " + additionnalPath);
		} else {
			for (int i = 0; i < listAdditionnalFiles.size(); i++) {
				Object[] fileData = listAdditionnalFiles.get(i);
				String[] filePath = fileData[0].toString().split("/");
				String name = filePath[filePath.length - 1];
				analysisDone.getAdditionalAnalyses().add(new ViewFile(ViewFileOrigin.STORED, name, fileData[0].toString(), (long) fileData[2], (long) fileData[1]));
			}
		}
	}

}
