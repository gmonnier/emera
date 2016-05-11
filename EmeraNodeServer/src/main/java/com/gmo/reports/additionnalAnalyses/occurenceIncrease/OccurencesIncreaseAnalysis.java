package com.gmo.reports.additionnalAnalyses.occurenceIncrease;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.coreprocessing.AnalysisManager;
import com.gmo.externalInterfaces.rmiclient.NodeNotificationsRMIClient;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.OutputFileType;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneAndDataCouple;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneData;
import com.gmo.reports.additionnalAnalyses.common.AdditionnalAnalysisListener;
import com.gmo.reports.additionnalAnalyses.geneWithGRNAIncrease.GeneWithIncreaseOnlyCSVGenerator;
import com.gmo.reports.additionnalAnalyses.geneWithGRNAIncrease.GeneWithIncreaseOnlyPDFGenerator;
import com.gmo.sharedobjects.model.genelibrary.ReferenceGene;
import com.gmo.sharedobjects.model.reports.Report;

import awsinterfaceManager.AWSS3InterfaceManager;

public class OccurencesIncreaseAnalysis extends Thread implements AdditionnalAnalysisListener {

	private static Logger LOG = Log4JLogger.logger;

	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");;

	private Report reference;

	private Report comparison;

	private OutputFileType outputType;

	public OccurencesIncreaseAnalysis(Report reference, Report comparison, OutputFileType outputType) {
		this.reference = reference;
		this.comparison = comparison;
		this.outputType = outputType;
		this.setName("OccurencesIncreaseAnalysis Generator");
		this.start();
	}

	@Override
	public void run() {

		boolean checkLibOK = checkLibraries(reference, comparison);
		if (!checkLibOK) {
			additionnalAnalysisFailed(reference.getAnalyseID(), "Reference analysis libraries needs to be used in the compared library, Analyses are incompatible for this type of calculation.");
			return;
		}

		OccurencesIncreaseReport report = new OccurencesIncreaseReport(reference, comparison);

		List<ReferenceGene> refLib = reference.getLibrary().getGenes();

		for (Iterator<ReferenceGene> iterator = refLib.iterator(); iterator.hasNext();) {
			ReferenceGene referenceGene = (ReferenceGene) iterator.next();
			ReferenceGeneAndDataCouple dataCouple = new ReferenceGeneAndDataCouple(referenceGene,
					new ReferenceGeneData(reference.getOccurenceCount(referenceGene.getAssociatedSequence()), comparison.getOccurenceCount(referenceGene.getAssociatedSequence()),
							reference.getOccurencePercent(referenceGene.getAssociatedSequence()), comparison.getOccurencePercent(referenceGene.getAssociatedSequence())));
			report.addReferenceGeneData(dataCouple);

		}

		// context to store additionnal results here
		LocationType locType = AnalysisManager.getInstance().getAnalysisResultsLocationType();
		String analysisResultsLocation = AnalysisManager.getInstance().getAnalysisResultsLocation();

		String tmpRootDir = locType == LocationType.S_3 ? "tmp" : analysisResultsLocation;
		String outputDir = reference.getUserID() + "/" + reference.getAnalyseID() + "/" + "Additional";
		File outputTmpDirectory = new File(tmpRootDir + "/" + outputDir);
		if (!outputTmpDirectory.exists()) {
			boolean dirCreated = outputTmpDirectory.mkdirs();
			if (!dirCreated) {
				LOG.error("Unable to create " + outputTmpDirectory.getAbsolutePath());
				additionnalAnalysisFailed(reference.getAnalyseID(), "File error on server side. Unable to create the report.");
				return;
			}
		}

		Date currentTime = new Date();

		String fileNameSimple = dateFormat.format(currentTime) + "_occurence-increase_Simple";
		String fileNameGeneWithIncrease = dateFormat.format(currentTime) + "_occurence-increase_GeneFocus";

		switch (outputType) {
		case CSV:
			fileNameSimple += ".csv";
			fileNameGeneWithIncrease += ".csv";
			break;
		case PDF:
			fileNameSimple += ".pdf";
			fileNameGeneWithIncrease += ".pdf";
			break;
		}

		File outputSimple = new File(outputTmpDirectory, fileNameSimple);
		File outputGeneFocus = new File(outputTmpDirectory, fileNameGeneWithIncrease);

		// if file doesnt exists, then create it
		if (!outputSimple.exists()) {
			try {
				outputSimple.createNewFile();
				outputGeneFocus.createNewFile();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				additionnalAnalysisFailed(reference.getAnalyseID(), "Unable to create additionnal analysis file");
				return;
			}
		}

		switch (outputType) {
		case CSV: {
			try {
				report.sortByGrowthRate();
				new OccurenceIncreaseCSVGenerator(new FileOutputStream(outputSimple), report).generateCSVFile();
				if (locType == LocationType.S_3) {
					AWSS3InterfaceManager.getInstance().uploadFile(analysisResultsLocation, outputDir + "/" + fileNameSimple, outputSimple);

					ViewFile outputS3ViewFile = new ViewFile(ViewFileOrigin.S3, fileNameSimple, outputDir + "/" + fileNameSimple, outputSimple.lastModified(), outputSimple.length());
					additionnalAnalysisPerformed(reference.getAnalyseID(), outputS3ViewFile);
				} else {
					additionnalAnalysisPerformed(reference.getAnalyseID(), new ViewFile(outputSimple));
				}

				report.sortByGeneName();
				new GeneWithIncreaseOnlyCSVGenerator(new FileOutputStream(outputGeneFocus), report).generateCSVFile();
				if (locType == LocationType.S_3) {
					AWSS3InterfaceManager.getInstance().uploadFile(analysisResultsLocation, outputDir + "/" + fileNameGeneWithIncrease, outputGeneFocus);

					ViewFile outputS3ViewFile = new ViewFile(ViewFileOrigin.S3, fileNameGeneWithIncrease, outputDir + "/" + fileNameGeneWithIncrease, outputGeneFocus.lastModified(),
							outputGeneFocus.length());
					additionnalAnalysisPerformed(reference.getAnalyseID(), outputS3ViewFile);
				} else {
					additionnalAnalysisPerformed(reference.getAnalyseID(), new ViewFile(outputGeneFocus));
				}
			} catch (Exception e) {
				LOG.error("Exception caught: ", e);
				additionnalAnalysisFailed(reference.getAnalyseID(), "Error while generating csv file. Abort report generation.");
				return;
			}
			break;
		}
		case PDF: {
			try {
				report.sortByGrowthRate();
				new OccurenceIncreasePDFGenerator(new FileOutputStream(outputSimple), report).generatePDFFile();
				if (locType == LocationType.S_3) {
					AWSS3InterfaceManager.getInstance().uploadFile(analysisResultsLocation, outputDir + "/" + fileNameSimple, outputSimple);

					ViewFile outputS3ViewFile = new ViewFile(ViewFileOrigin.S3, fileNameSimple, outputDir + "/" + fileNameSimple, outputSimple.lastModified(), outputSimple.length());
					additionnalAnalysisPerformed(reference.getAnalyseID(), outputS3ViewFile);
				} else {
					additionnalAnalysisPerformed(reference.getAnalyseID(), new ViewFile(outputSimple));
				}

				report.sortByGeneName();
				new GeneWithIncreaseOnlyPDFGenerator(new FileOutputStream(outputGeneFocus), report).generatePDFFile();
				if (locType == LocationType.S_3) {
					AWSS3InterfaceManager.getInstance().uploadFile(analysisResultsLocation, outputDir + "/" + fileNameGeneWithIncrease, outputGeneFocus);

					ViewFile outputS3ViewFile = new ViewFile(ViewFileOrigin.S3, fileNameGeneWithIncrease, outputDir + "/" + fileNameGeneWithIncrease, outputGeneFocus.lastModified(),
							outputGeneFocus.length());
					additionnalAnalysisPerformed(reference.getAnalyseID(), outputS3ViewFile);
				} else {
					additionnalAnalysisPerformed(reference.getAnalyseID(), new ViewFile(outputGeneFocus));
				}

			} catch (Exception e) {
				LOG.error("Exception caught: ", e);
				additionnalAnalysisFailed(reference.getAnalyseID(), "Error while generating pdf file. Abort report generation.");
				return;
			}
			break;
		}
		default:
			LOG.error("Unable to write on an unknown output type");
			additionnalAnalysisFailed(reference.getAnalyseID(), "Unsupported output type.");
		}

	}

	/**
	 * 
	 * @param r1
	 *            reference report
	 * @param r2
	 *            report to compare with.
	 * @return true if r2 contains all libraries that are in the reference,
	 *         false otherwise. Note this is not reciproque.
	 */
	private boolean checkLibraries(Report r1, Report r2) {
		LOG.debug("Check for library compatibility : " + r1.getLibrary().getGenes().size() + "    and    " + r2.getLibrary().getGenes().size());
		return r1.getLibrary().getGenes().size() == r2.getLibrary().getGenes().size();
	}

	@Override
	public void additionnalAnalysisFailed(String analysisID, String reasonMessage) {
		NodeNotificationsRMIClient.getInstance().additionnalAnalysisFailed(analysisID, reasonMessage);
		LOG.error("Additionnal analysis generation failed : " + reasonMessage);
	}

	@Override
	public void additionnalAnalysisPerformed(String analysisID, ViewFile outputPDF) {
		NodeNotificationsRMIClient.getInstance().additionnalAnalysisCompleted(analysisID, outputPDF);
		LOG.debug("Additionnal analysis generation succeeded");
	}
}
