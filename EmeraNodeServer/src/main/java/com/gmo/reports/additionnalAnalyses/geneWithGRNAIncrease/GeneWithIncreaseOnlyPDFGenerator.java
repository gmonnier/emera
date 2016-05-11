package com.gmo.reports.additionnalAnalyses.geneWithGRNAIncrease;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.fileGenerator.pdf.GenericPDFGenerator;
import com.gmo.fileGenerator.pdf.util.PDFPageSize;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.reports.additionnalAnalyses.ReferenceGeneAndDataCouple;
import com.gmo.reports.additionnalAnalyses.occurenceIncrease.OccurencesIncreaseReport;
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

public class GeneWithIncreaseOnlyPDFGenerator extends GenericPDFGenerator {

	private static Logger LOG = Log4JLogger.logger;

	private OccurencesIncreaseReport increaseReport;

	public GeneWithIncreaseOnlyPDFGenerator(OutputStream outputStream, OccurencesIncreaseReport increaseReport) {

		super("Targeted genes with gRNA representation increase", outputStream);

		this.increaseReport = increaseReport;
		if (increaseReport == null) {
			LOG.error("Report file is null. abort");
		}
	}

	@Override
	protected void createChaptersTitles(List<String> listChapters) {
		listChapters.add("Table of content");
		listChapters.add("Results - Processing feature overview");
		listChapters.add("Results - Genes with at least two increasing gRNA occurences");
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

			List<ViewFile> listDataFiles = report.getAnalyseConfig().getSelectedDataFiles();
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

		String description = "This comparative analysis is intended to show all genes targeted with at least two gRNA that showed an increasing expression (i.e. a growth rate > 2.0 in this case)";
		Paragraph pDescription = new Paragraph(description, phraseFont);
		String description2 = "Following data table classify these targeted genes in alphabetical order ";
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

			int geneIndex = 0;
			LOG.debug("Pares list of grna results data size=" + listDataGRNA.size());
			for (int i = 0; i < listDataGRNA.size();) {
				ReferenceGeneAndDataCouple grna = listDataGRNA.get(i);

				int gRNAIncreasingCount = 0;
				List<ReferenceGeneAndDataCouple> currentList = new ArrayList<ReferenceGeneAndDataCouple>();
				while (i < listDataGRNA.size() && grna.getgRNA().getName().equals(listDataGRNA.get(i).getgRNA().getName())) {
					currentList.add(listDataGRNA.get(i));
					if (listDataGRNA.get(i).getgRNAData().getGrowthRate() > 2.0) {
						gRNAIncreasingCount++;
					}
					i++;
				}

				if (gRNAIncreasingCount < 2) {
					// current targeted gene does not get two gRNA with
					// increasing occurences between the two analyses
					continue;
				}

				geneIndex++;
				for (int j = 0; j < currentList.size(); j++) {

					ReferenceGeneAndDataCouple subgrna = currentList.get(j);

					String indexStr = j == 0 ? "" + geneIndex : "";
					Paragraph indexP = new Paragraph(indexStr, defaultFont);
					PdfPCell indexCell = new PdfPCell(indexP);

					String indexIDStr = j == 0 ? subgrna.getgRNA().getName() : "";
					Paragraph indexID = new Paragraph(indexIDStr, defaultFont);
					PdfPCell cellID = new PdfPCell(indexID);

					Paragraph indexSeq = new Paragraph(subgrna.getgRNA().getAssociatedSequence(), defaultFont);
					PdfPCell cellSeq = new PdfPCell(indexSeq);

					int grnaOccurenceRef = subgrna.getgRNAData().getOccurenceRef();
					Paragraph indexOccRef = new Paragraph(Integer.toString(grnaOccurenceRef), defaultFont);
					PdfPCell cellOccRef = new PdfPCell(indexOccRef);

					Paragraph indexOccPercentRef = new Paragraph(formatSansExpo(subgrna.getgRNAData().getOccurencePercRef(), 4), defaultFont);
					PdfPCell cellOccPercentRef = new PdfPCell(indexOccPercentRef);

					int grnaOccurenceComp = subgrna.getgRNAData().getOccurenceComp();
					Paragraph indexOccComp = new Paragraph(Integer.toString(grnaOccurenceComp), defaultFont);
					PdfPCell cellOccComp = new PdfPCell(indexOccComp);

					Paragraph indexOccPercentComp = new Paragraph(formatSansExpo(subgrna.getgRNAData().getOccurencePercComp(), 4), defaultFont);
					PdfPCell cellOccPercentComp = new PdfPCell(indexOccPercentComp);

					Paragraph growthRatePg = new Paragraph(formatSansExpo(subgrna.getgRNAData().getGrowthRate(), 4), defaultFont);
					PdfPCell cellgrowthRate = new PdfPCell(growthRatePg);

					cellID.setUseBorderPadding(true);
					indexCell.setUseBorderPadding(true);
					indexCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					cellID.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
					float thickBorderSize = 1.5f;
					if (j == 0 && geneIndex != 1) {
						indexCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
						cellID.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
						
						 /* indexCell.setBorderWidthTop(thickBorderSize);
						  cellID.setBorderWidthTop(thickBorderSize);
						  cellSeq.setBorderWidthTop(thickBorderSize);
						  cellOccRef.setBorderWidthTop(thickBorderSize);
						  cellOccPercentRef.setBorderWidthTop(thickBorderSize);
						  cellOccComp.setBorderWidthTop(thickBorderSize);
						  cellOccPercentComp.setBorderWidthTop(thickBorderSize);
						  cellgrowthRate.setBorderWidthTop(thickBorderSize);*/
						 
					} else if (j == (currentList.size() - 1)) {
						indexCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
						cellID.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
						/*
						 * indexCell.setBorderWidthBottom(thickBorderSize);
						 * cellID.setBorderWidthBottom(thickBorderSize);
						 * cellSeq.setBorderWidthBottom(thickBorderSize);
						 * cellOccRef.setBorderWidthBottom(thickBorderSize);
						 * cellOccPercentRef
						 * .setBorderWidthBottom(thickBorderSize);
						 * cellOccComp.setBorderWidthBottom(thickBorderSize);
						 * cellOccPercentComp
						 * .setBorderWidthBottom(thickBorderSize);
						 * cellgrowthRate.setBorderWidthBottom(thickBorderSize);
						 */
					}

					if (geneIndex % 2 == 0) {
						indexCell.setBackgroundColor(Colors.getImpairtablecolor());
						cellID.setBackgroundColor(Colors.getImpairtablecolor());

					}

					if (grnaOccurenceRef == 0) {
						cellSeq.setBackgroundColor(Colors.getRedtablecolor());
						cellOccRef.setBackgroundColor(Colors.getRedtablecolor());
						cellOccPercentRef.setBackgroundColor(Colors.getRedtablecolor());
						cellOccComp.setBackgroundColor(Colors.getRedtablecolor());
						cellOccPercentComp.setBackgroundColor(Colors.getRedtablecolor());
						cellgrowthRate.setBackgroundColor(Colors.getRedtablecolor());
					} else if (subgrna.getgRNAData().getGrowthRate() > 1) {
						cellSeq.setBackgroundColor(Colors.getGreentablecolor());
						cellOccRef.setBackgroundColor(Colors.getGreentablecolor());
						cellOccPercentRef.setBackgroundColor(Colors.getGreentablecolor());
						cellOccComp.setBackgroundColor(Colors.getGreentablecolor());
						cellOccPercentComp.setBackgroundColor(Colors.getGreentablecolor());
						cellgrowthRate.setBackgroundColor(Colors.getGreentablecolor());
					} else {
						cellSeq.setBackgroundColor(Colors.getOrangetablecolor());
						cellOccRef.setBackgroundColor(Colors.getOrangetablecolor());
						cellOccPercentRef.setBackgroundColor(Colors.getOrangetablecolor());
						cellOccComp.setBackgroundColor(Colors.getOrangetablecolor());
						cellOccPercentComp.setBackgroundColor(Colors.getOrangetablecolor());
						cellgrowthRate.setBackgroundColor(Colors.getOrangetablecolor());
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
