package com.gmo.reports.extraction;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;

import processorNode.viewmodel.ViewCreateProcessConfiguration;
import processorNode.viewmodel.ViewFile;

import com.gmo.coreprocessing.Analysis;
import com.gmo.coreprocessing.AnalysisManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.model.analysis.AnalysisStatus;
import com.gmo.model.reports.Report;
import com.gmo.reports.generation.ReportWriter;

public class AnalysisExtractor implements Runnable {

	private static Logger LOG = Log4JLogger.logger;

	public final static String ADDITIONAL_ANALYSIS_DIR = "Additional";

	private File analysesDirectoryRoot;

	private static final Executor fileExtractorService = Executors.newSingleThreadExecutor();

	public AnalysisExtractor() {
		analysesDirectoryRoot = new File(ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation());

		if (!analysesDirectoryRoot.exists()) {
			LOG.warn("Analyses storage directory does not exists, abort analyses extractions");
		} else {
			fileExtractorService.execute(this);
		}
	}

	@Override
	public void run() {

		File[] listUsersDir = analysesDirectoryRoot.listFiles();
		if (listUsersDir == null) {
			LOG.warn("No users directory, exits extractor thread.");
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
							if (listFiles[j].getName().equals(ReportWriter.REPORT_FILENAME)) {
								reportSerialized = listFiles[j];
							}
						}
					}

					if (reportSerialized != null) {

						try {

							Report report = ReportReader.extractReport(reportSerialized, userID);

							Analysis analysisDone = new Analysis();
							analysisDone.setId(analyseID);
							analysisDone.setUserid(userID);
							analysisDone.setStatus(AnalysisStatus.DONE);
							analysisDone.setLaunchDate(report.getStartDate());
							analysisDone.setCompletionDate(report.getEndDate());
							analysisDone.setReport(report);

							ViewCreateProcessConfiguration viewConfig = new ViewCreateProcessConfiguration();
							ConfigurationBuilder.initViewConfigurationFromModel(report.getAnalyseConfig(), viewConfig);
							analysisDone.setConfiguration(viewConfig);

							extractAdditionnalAnalyses(analysisDone, new File(analyseDir, ADDITIONAL_ANALYSIS_DIR));

							AnalysisManager.getInstance().analyseFinished(analysisDone);

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

	private void extractAdditionnalAnalyses(Analysis analysisDone, File additionalAnalysesDirectory) {
		LOG.debug(additionalAnalysesDirectory.getAbsolutePath());
		if (additionalAnalysesDirectory.exists()) {
			File[] listAdditional = additionalAnalysesDirectory.listFiles();
			if (listAdditional == null || listAdditional.length == 0) {
				LOG.debug("No additional analyses found for " + analysisDone.getAnalysisID() + " in " + additionalAnalysesDirectory.getAbsolutePath());
			} else {
				for (int i = 0; i < listAdditional.length; i++) {
					analysisDone.getAdditionalAnalyses().add(new ViewFile(listAdditional[i]));
				}
			}
		} else {
			LOG.debug("No additional analyses found for " + analysisDone.getAnalysisID());
		}
	}

}
