package com.gmo.reports.additionnalAnalyses.occurenceIncrease;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.fileGenerator.pdf.GenericPDFGenerator;
import com.gmo.fileGenerator.pdf.util.PDFPageSize;
import com.gmo.logger.Log4JLogger;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneAndDataCouple;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.model.reports.Report;
import com.gmo.ui.lookAndFeel.Colors;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class OccurenceIncreasePDFGenerator extends GenericPDFGenerator {

	private static Logger LOG = Log4JLogger.logger;

	private OccurencesIncreaseReport increaseReport;

	public OccurenceIncreasePDFGenerator(File outputFile, OccurencesIncreaseReport increaseReport) {

		super("gRNA occurences growth rate analysis", outputFile);

		this.increaseReport = increaseReport;
		if (increaseReport == null) {
			LOG.error("Report file is null. abort");
		}

	}

	@Override
	protected void createChaptersTitles(List<String> listChapters) {
		listChapters.add("Table of content");
		listChapters.add("Results - Processing feature overview");
		listChapters.add("Results - Occurences evolution details");
	}

	@Override
	protected void createChaptersContent() throws DocumentException {
		createChapter(1, createProcessingInfo());

		Rectangle pageSizeTable = new Rectangle(PDFPageSize.A3.getITextFormatPdfPageSize().getWidth(), PDFPageSize.A1.getITextFormatPdfPageSize().getHeight());
		createChapter(2, createGRNAOccurencesIncreaseTable(), pageSizeTable);
	}

	private PdfPTable generateLibraryFilesTable(Report report) {
		PdfPTable tableLib = new PdfPTable(2);
		try {
			float[] columnWidths = { 0.5f, 1f };
			tableLib.setWidths(columnWidths);

			// Input Library files REFERENCE
			PdfPCell fileIndex = new PdfPCell(new Paragraph("Library file(s) ", phraseFont));
			fileIndex.setBackgroundColor(baseBackgroundCellColor);
			fileIndex.setVerticalAlignment(Element.ALIGN_MIDDLE);
			tableLib.addCell(fileIndex);

			PdfPCell filePath = new PdfPCell();
			for (int i = 0; i < report.getAnalyseConfig().getSelectedLibraries().size(); i++) {
				Paragraph pFilePath = new Paragraph(report.getAnalyseConfig().getSelectedLibraries().get(i).getName(), phraseFont);
				filePath.addElement(pFilePath);
			}
			filePath.setVerticalAlignment(Element.ALIGN_MIDDLE);
			filePath.setBackgroundColor(baseBackgroundCellColor);
			tableLib.addCell(filePath);

			// Input Library size REFERENCE
			PdfPCell libSizeCell = new PdfPCell(new Paragraph("GRNA library size", phraseFont));
			libSizeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell.setBackgroundColor(baseBackgroundCellColor);
			tableLib.addCell(libSizeCell);
			PdfPCell libSizeCell2 = new PdfPCell(new Paragraph(Integer.toString(report.getLibrary().size()), phraseFont));
			libSizeCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell2.setBackgroundColor(baseBackgroundCellColor);
			tableLib.addCell(libSizeCell2);

		} catch (DocumentException e) {
			LOG.error("Unable to generate pdf table for libs");
		}
		return tableLib;
	}

	private PdfPTable generateDataInputTable(Report report) {
		PdfPTable tableFastq = new PdfPTable(2);
		try {
			float[] columnWidths = { 0.5f, 1f };
			tableFastq.setWidths(columnWidths);

			// Input FASTQ file
			PdfPCell fileIndex = new PdfPCell(new Paragraph("FastQ file", phraseFont));
			fileIndex.setBackgroundColor(baseBackgroundCellColor);
			fileIndex.setVerticalAlignment(Element.ALIGN_MIDDLE);
			tableFastq.addCell(fileIndex);

			List<ModelFileStored> listDataFiles = report.getAnalyseConfig().getSelectedDataFiles();
			String fastqFile = "";
			for (int i = 0; i < listDataFiles.size(); i++) {
				fastqFile += listDataFiles.get(i).getName();
				if (i != listDataFiles.size() - 1) {
					fastqFile += "\n";
				}
			}
			Paragraph pFilePath = new Paragraph(fastqFile, phraseFont);
			PdfPCell filePath = new PdfPCell(pFilePath);
			filePath.setVerticalAlignment(Element.ALIGN_MIDDLE);
			filePath.setBackgroundColor(baseBackgroundCellColor);
			tableFastq.addCell(filePath);

			// Entries count
			PdfPCell libSizeCell = new PdfPCell(new Paragraph("Sequences count", phraseFont));
			libSizeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell.setBackgroundColor(baseBackgroundCellColor);
			tableFastq.addCell(libSizeCell);

			String count = Long.toString(report.getTotalLineProcessed());
			PdfPCell libSizeCell2 = new PdfPCell(new Paragraph(count, phraseFont));
			libSizeCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell2.setBackgroundColor(baseBackgroundCellColor);
			tableFastq.addCell(libSizeCell2);

		} catch (DocumentException e) {
			LOG.error("Unable to generate pdf table for input data");
		}
		return tableFastq;
	}

	private List<Paragraph> createProcessingInfo() {
		List<Paragraph> listPar = new ArrayList<Paragraph>();

		Paragraph p1 = new Paragraph("Library description");
		p1.setIndentationLeft(20);

		Paragraph pTableContainer1 = new Paragraph("Reference libraries", phraseFont);
		Paragraph pTableContainer2 = new Paragraph("Initial libraries", phraseFont);

		PdfPTable tableLibsRef = generateLibraryFilesTable(increaseReport.getReportRef());
		PdfPTable tableLibsComp = generateLibraryFilesTable(increaseReport.getReportComp());
		pTableContainer1.add(tableLibsRef);
		pTableContainer2.add(tableLibsComp);

		p1.add(pTableContainer1);
		p1.add(pTableContainer2);
		listPar.add(p1);

		Paragraph p2 = new Paragraph("Input FastQ description");
		p2.setIndentationLeft(20);

		Paragraph pTableContainer3 = new Paragraph("Reference input", phraseFont);
		PdfPTable tableDataRef = generateDataInputTable(increaseReport.getReportRef());
		pTableContainer3.add(tableDataRef);

		Paragraph pTableContainer4 = new Paragraph("Initial input", phraseFont);
		PdfPTable tableDataComp = generateDataInputTable(increaseReport.getReportComp());
		pTableContainer4.add(tableDataComp);

		p2.add(pTableContainer3);
		p2.add(pTableContainer4);

		listPar.add(p2);

		Paragraph p3 = new Paragraph("Intent and analysis description");
		p3.setIndentationLeft(20);

		String description = "This comparative analysis intended to show gRNA occurences growth between two raw analyses. Using a reference raw analysis result, a growth rate is computed with a second analysis that reflect whether each guide RNA has a representation gain compared to the initial representation count.";
		Paragraph pDescription = new Paragraph(description, phraseFont);
		String description2 = "Obvioulsy, following data will display both representation percentages in both reference analysis and initial analysis, as well as the calculated representation growth rate.";
		Paragraph pDescription2 = new Paragraph(description2, phraseFont);
		p3.add(pDescription);
		p3.add(pDescription2);

		listPar.add(p3);

		return listPar;
	}

	private List<Paragraph> createGRNAOccurencesIncreaseTable() {

		Paragraph p = new Paragraph("gRNA occurences comparison table");

		PdfPTable table = null;

		List<ReferenceGeneAndDataCouple> listDataGRNA = increaseReport.getListResult();

		try {
			/* Table creation */
			table = new PdfPTable(8);

			// Fonts
			Font defaultFont = new Font(FontFamily.COURIER, 8, Font.BOLD);
			defaultFont.setColor(50, 50, 100);

			// Create the headers
			Font headerFont = new Font(FontFamily.COURIER, 10, Font.BOLD);
			headerFont.setColor(50, 100, 100);
			table.addCell(getHeaderCell("Index", headerFont));
			table.addCell(getHeaderCell("Gene ID", headerFont));
			table.addCell(getHeaderCell("GRNA Seq", headerFont));
			table.addCell(getHeaderCell("Occurence Ref", headerFont));
			table.addCell(getHeaderCell("Occurence Ref %", headerFont));
			table.addCell(getHeaderCell("Occurence Init", headerFont));
			table.addCell(getHeaderCell("Occurence Init %", headerFont));
			table.addCell(getHeaderCell("Growth rate", headerFont));
			table.setHeaderRows(1);

			float[] columnWidths = { 0.3f, 0.5f, 1f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f };
			table.setWidths(columnWidths);

			for (int i = 0; i < listDataGRNA.size(); i++) {
				ReferenceGeneAndDataCouple grna = listDataGRNA.get(i);

				Paragraph indexP = new Paragraph("" + (i + 1), defaultFont);
				PdfPCell indexCell = new PdfPCell(indexP);

				Paragraph indexID = new Paragraph(grna.getgRNA().getName(), defaultFont);
				PdfPCell cellID = new PdfPCell(indexID);

				Paragraph indexSeq = new Paragraph(grna.getgRNA().getAssociatedSequence(), defaultFont);
				PdfPCell cellSeq = new PdfPCell(indexSeq);

				int grnaOccurenceRef = grna.getgRNAData().getOccurenceRef();
				Paragraph indexOccRef = new Paragraph(Integer.toString(grnaOccurenceRef), defaultFont);
				PdfPCell cellOccRef = new PdfPCell(indexOccRef);

				Paragraph indexOccPercentRef = new Paragraph(formatSansExpo(grna.getgRNAData().getOccurencePercRef(), 4), defaultFont);
				PdfPCell cellOccPercentRef = new PdfPCell(indexOccPercentRef);

				int grnaOccurenceComp = grna.getgRNAData().getOccurenceComp();
				Paragraph indexOccComp = new Paragraph(Integer.toString(grnaOccurenceComp), defaultFont);
				PdfPCell cellOccComp = new PdfPCell(indexOccComp);

				Paragraph indexOccPercentComp = new Paragraph(formatSansExpo(grna.getgRNAData().getOccurencePercComp(), 4), defaultFont);
				PdfPCell cellOccPercentComp = new PdfPCell(indexOccPercentComp);

				Paragraph growthRatePg = new Paragraph(formatSansExpo(grna.getgRNAData().getGrowthRate(), 4), defaultFont);
				PdfPCell cellgrowthRate = new PdfPCell(growthRatePg);

				if (grnaOccurenceRef == 0) {
					indexCell.setBackgroundColor(Colors.getErrortablecolor());
					cellID.setBackgroundColor(Colors.getErrortablecolor());
					cellSeq.setBackgroundColor(Colors.getErrortablecolor());
					cellOccRef.setBackgroundColor(Colors.getErrortablecolor());
					cellOccPercentRef.setBackgroundColor(Colors.getErrortablecolor());
					cellOccComp.setBackgroundColor(Colors.getErrortablecolor());
					cellOccPercentComp.setBackgroundColor(Colors.getErrortablecolor());
					cellgrowthRate.setBackgroundColor(Colors.getErrortablecolor());
				} else {
					if (i % 2 == 0) {
						indexCell.setBackgroundColor(Colors.getImpairtablecolor());
						cellID.setBackgroundColor(Colors.getImpairtablecolor());
						cellSeq.setBackgroundColor(Colors.getImpairtablecolor());
						cellOccRef.setBackgroundColor(Colors.getImpairtablecolor());
						cellOccPercentRef.setBackgroundColor(Colors.getImpairtablecolor());
						cellOccComp.setBackgroundColor(Colors.getImpairtablecolor());
						cellOccPercentComp.setBackgroundColor(Colors.getImpairtablecolor());
						cellgrowthRate.setBackgroundColor(Colors.getImpairtablecolor());
					}
				}

				table.addCell(indexCell);
				table.addCell(cellID);
				table.addCell(cellSeq);
				table.addCell(cellOccRef);
				table.addCell(cellOccPercentRef);
				table.addCell(cellOccComp);
				table.addCell(cellOccPercentComp);
				table.addCell(cellgrowthRate);

			}

			p.add(table);

			// table.setTotalWidth((document.getPageSize().getWidth() +
			// document.leftMargin() + document.rightMargin()) / 7);

		} catch (DocumentException e) {
			LOG.error("Error when generating pdf table", e);
		}

		List<Paragraph> listPar = new ArrayList<Paragraph>();
		listPar.add(p);
		return listPar;

	}

}
