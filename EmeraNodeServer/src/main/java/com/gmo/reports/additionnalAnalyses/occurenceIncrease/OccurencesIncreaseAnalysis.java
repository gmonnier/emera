package com.gmo.reports.additionnalAnalyses.occurenceIncrease;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import processorNode.viewmodel.OutputFileType;
import processorNode.viewmodel.ViewFile;

import com.gmo.coreprocessing.Analysis;
import com.gmo.logger.Log4JLogger;
import com.gmo.model.genelibrary.ReferenceGene;
import com.gmo.model.processconfig.files.ModelFileStored;
import com.gmo.reports.Report;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneAndDataCouple;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneData;
import com.gmo.reports.additionnalAnalyses.common.AdditionnalAnalysisListener;
import com.gmo.reports.additionnalAnalyses.geneWithGRNAIncrease.GeneWithIncreaseOnlyCSVGenerator;
import com.gmo.reports.additionnalAnalyses.geneWithGRNAIncrease.GeneWithIncreaseOnlyPDFGenerator;
import com.gmo.reports.extraction.AnalysisExtractor;

public class OccurencesIncreaseAnalysis extends Thread implements AdditionnalAnalysisListener {

	private static Logger LOG = Log4JLogger.logger;

	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");;

	private Analysis reference;

	private Analysis comparison;

	private OutputFileType outputType;

	public OccurencesIncreaseAnalysis(Analysis reference, Analysis comparison, OutputFileType outputType) {
		this.reference = reference;
		this.comparison = comparison;
		this.outputType = outputType;
		this.setName("OccurencesIncreaseAnalysis Generator");
		this.start();
	}

	@Override
	public void run() {

		Report refReport = reference.getReport();
		Report compReport = comparison.getReport();

		boolean checkLibOK = checkLibraries(refReport, compReport);
		if (!checkLibOK) {
			additionnalAnalysisFailed("Reference analysis libraries needs to be used in the compared library, Analyses are incompatible for this type of calculation.");
			return;
		}

		OccurencesIncreaseReport report = new OccurencesIncreaseReport(refReport, compReport);

		List<ReferenceGene> refLib = refReport.getLibrary().getGenes();

		for (Iterator<ReferenceGene> iterator = refLib.iterator(); iterator.hasNext();) {
			ReferenceGene referenceGene = (ReferenceGene) iterator.next();
			ReferenceGeneAndDataCouple dataCouple = new ReferenceGeneAndDataCouple(referenceGene, new ReferenceGeneData(refReport.getOccurenceCount(referenceGene.getAssociatedSequence()),
					compReport.getOccurenceCount(referenceGene.getAssociatedSequence()), refReport.getOccurencePercent(referenceGene.getAssociatedSequence()), compReport.getOccurencePercent(referenceGene
							.getAssociatedSequence())));
			report.addReferenceGeneData(dataCouple);

		}

		String resultLocation = ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation();
		String outputDir = resultLocation + File.separator + reference.getUserid() + File.separator + reference.getAnalysisID() + File.separator + AnalysisExtractor.ADDITIONAL_ANALYSIS_DIR;
		File outputDirectory = new File(outputDir);
		if (!outputDirectory.exists()) {
			boolean dirCreated = outputDirectory.mkdirs();
			if (!dirCreated) {
				LOG.error("Unable to create " + outputDirectory.getAbsolutePath());
				additionnalAnalysisFailed("File error on server side. Unable to create the report.");
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

		File outputSimple = new File(outputDir, fileNameSimple);
		File outputGeneFocus = new File(outputDir, fileNameGeneWithIncrease);

		switch (outputType) {
		case CSV: {
			try {
				report.sortByGrowthRate();
				new OccurenceIncreaseCSVGenerator(outputSimple, report).generateCSVFile();
				additionnalAnalysisPerformed(outputSimple);

				report.sortByGeneName();
				new GeneWithIncreaseOnlyCSVGenerator(outputGeneFocus, report).generateCSVFile();
				additionnalAnalysisPerformed(outputGeneFocus);
			} catch (Exception e) {
				additionnalAnalysisFailed("Error while generating csv file. Abort report generation.");
				return;
			}
			break;
		}
		case PDF: {
			try {
				report.sortByGrowthRate();
				new OccurenceIncreasePDFGenerator(outputSimple, report).generatePDFFile();
				additionnalAnalysisPerformed(outputSimple);

				report.sortByGeneName();
				new GeneWithIncreaseOnlyPDFGenerator(outputGeneFocus, report).generatePDFFile();
				additionnalAnalysisPerformed(outputGeneFocus);
			} catch (Exception e) {
				additionnalAnalysisFailed("Error while generating pdf file. Abort report generation.");
				return;
			}
			break;
		}
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
		List<ModelFileStored> listLibsInReference = r1.getAnalyseConfig().getSelectedLibraries();
		for (Iterator<ModelFileStored> iterator = listLibsInReference.iterator(); iterator.hasNext();) {
			ModelFileStored modelFileStored = (ModelFileStored) iterator.next();
			if (!r2.getAnalyseConfig().getSelectedLibraries().contains(modelFileStored)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void additionnalAnalysisFailed(String reasonMessage) {
		LOG.error("Additionnal analysis generation failed : " + reasonMessage);
	}

	@Override
	public void additionnalAnalysisPerformed(File outputPDF) {
		reference.getAdditionalAnalyses().add(new ViewFile(outputPDF));
		LOG.debug("Additionnal analysis generation succeeded");
	}
}
