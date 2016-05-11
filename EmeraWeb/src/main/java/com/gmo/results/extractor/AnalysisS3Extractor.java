package com.gmo.results.extractor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.ApplicationContextManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.results.ResultsManager;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.reports.Report;

import awsinterfaceManager.AWSS3InterfaceManager;

public class AnalysisS3Extractor extends AnalysisExtractor {

	private static Logger LOG = Log4JLogger.logger;

	@Override
	public boolean isRootValid() {
		return AWSS3InterfaceManager.getInstance().isBucketValid(analysesDirectoryRoot);
	}

	@Override
	protected String getResultsRoot() {
		return ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation().getAnalysisResultsLocationRoot();
	}

	@Override
	protected void extractAnalyses() {

		List<Object[]> bucketContent = AWSS3InterfaceManager.getInstance().listAllFilesInBucket(analysesDirectoryRoot);

		Map<String, AnalysisS3Model> extractedAnalyses = new HashMap<>();
		for (int k = 0; k < bucketContent.size(); k++) {
			Object[] fileInfo = bucketContent.get(k);
			String filePath = (String) fileInfo[0];
			String[] decomposedPath = filePath.split("/");

			try {
				String filename = decomposedPath[decomposedPath.length - 1];
				String userID = decomposedPath[0];
				String analysisID = decomposedPath[1];

				AnalysisS3Model analysisModel = extractedAnalyses.get(analysisID);
				if (analysisModel == null) {
					analysisModel = new AnalysisS3Model();
					extractedAnalyses.put(analysisID, analysisModel);
				}

				// Extract main report
				if (filename.equalsIgnoreCase(REPORT_FILENAME)) {
					analysisModel.extractReport(filePath, userID, analysisID);
				} else if (decomposedPath.length > 3 && decomposedPath[2].equals(ADDITIONAL_ANALYSIS_DIR)) {
					analysisModel.addAdditionnalAnalysisResultFile(fileInfo, filename);
				}
				// No need to extract csv and pdf files. Paths are generated
				// from the View configuration
			} catch (ArrayIndexOutOfBoundsException aio) {
				LOG.error("Extracted key is not valid for results extraction : " + fileInfo[0]);
			}
		}

		Iterator<Map.Entry<String, AnalysisS3Model>> it = extractedAnalyses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, AnalysisS3Model> pair = (Map.Entry<String, AnalysisS3Model>) it.next();
			pair.getValue().addToProcessedAnalyses();
		}

		LOG.warn("Analysis extraction done");
	}

	class AnalysisS3Model {

		private ViewAnalysis analysisDone;

		private boolean isAnalysisValid;

		public AnalysisS3Model() {
			isAnalysisValid = false;
			this.analysisDone = new ViewAnalysis();
		}

		public void addAdditionnalAnalysisResultFile(Object[] fileData, String fileName) {
			analysisDone.getAdditionalAnalyses().add(new ViewFile(ViewFileOrigin.S3, fileName, fileData[0].toString(), (long) fileData[2], (long) fileData[1]));
		}

		public void extractReport(String pathToReport, String userID, String analyseID) {

			InputStream reportStream = AWSS3InterfaceManager.getInstance().getObjectInputStream(analysesDirectoryRoot, pathToReport);

			try {

				Report report = ReportReader.extractReport(reportStream, userID);

				analysisDone.setId(analyseID);
				analysisDone.setUserid(userID);
				analysisDone.setStatus(AnalysisStatus.DONE);
				analysisDone.setLaunchDate(report.getStartDate());
				analysisDone.setCompletionDate(report.getEndDate());
				analysisDone.setReport(report);
				analysisDone.setViewConfiguration(report.getAnalyseConfig());

			} catch (Throwable ex) {
				LOG.error("Unable to deserialize Report file for analysis id " + analyseID, ex);
			}

			isAnalysisValid = true;
		}

		public void addToProcessedAnalyses() {
			if (isAnalysisValid) {
				LOG.info("Analysis extraction done for " + analysisDone.getId() + " assossiated with user : " + analysisDone.getUserid() + ". Add to processed analysis list");
				ResultsManager.getInstance().addProcessedAnalysis(analysisDone);
			} else {
				LOG.error("Extracted analysis is not valid - check whether all appropriates files are presents " + analysisDone.getId());
			}
		}
	}

}
