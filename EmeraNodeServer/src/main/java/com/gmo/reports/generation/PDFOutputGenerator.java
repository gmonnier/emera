package com.gmo.reports.generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.gmo.fileGenerator.pdf.util.PDFPageSize;
import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.genelibrary.ReferenceGene;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.model.processconfiguration.ExtractionPattern;
import com.gmo.sharedobjects.model.reports.Report;
import com.gmo.sharedobjects.model.reports.UnfoundStartSeqMap;
import com.gmo.ui.lookAndFeel.Colors;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

public class PDFOutputGenerator extends PdfPageEventHelper {

	private static Logger LOG = Log4JLogger.logger;

	private Report report;

	private final Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.NORMAL);

	private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new BaseColor(31, 57, 119));

	private final Font phraseFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new BaseColor(31, 57, 119));

	private final Font phraseFontRed = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new BaseColor(180, 5, 5));

	private final BaseColor lightGreen = new BaseColor(210, 225, 160);

	private final BaseColor lightBorderGreen = new BaseColor(120, 150, 4);

	private final BaseColor baseBackgroundCellColor = new BaseColor(240, 240, 255);

	private final BaseColor baseBackgroundCellColorImportant = new BaseColor(240, 220, 220);

	private BaseFont baseFont;

	private Document document;

	private PdfWriter writer;

	private List<String> chapterTitles;

	/** The header text. */
	String header;

	// table to store placeholder for all chapters and sections
	private final Map<String, PdfTemplate> tocPlaceholder = new HashMap<>();

	// store the chapters and sections with their title here.
	private final Map<String, Integer> pageByTitle = new HashMap<>();

	public PDFOutputGenerator(File outputFile, Report report) throws Exception {

		header = "Sub sequences analysis";
		this.report = report;
		if (report == null) {
			LOG.error("The report file is null. abort");
		}

		try {
			this.baseFont = BaseFont.createFont();

			// if file doesnt exists, then create it
			if (!outputFile.exists()) {
				try {
					outputFile.createNewFile();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}

			generatePDFOutput(outputFile.getAbsolutePath());
		} catch (Exception e) {
			LOG.info("delete file " + outputFile.delete());
			throw e;
		}

	}

	private void generatePDFOutput(String outputFile) throws FileNotFoundException, DocumentException {

		Rectangle standardA4Size = PDFPageSize.A4.getITextFormatPdfPageSize();
		Rectangle pageSizeTable = new Rectangle(PDFPageSize.A4.getITextFormatPdfPageSize().getWidth(), PDFPageSize.A1.getITextFormatPdfPageSize().getHeight());
		Rectangle pageSizeTableUnfound = new Rectangle(PDFPageSize.A3.getITextFormatPdfPageSize().getWidth(), PDFPageSize.A1.getITextFormatPdfPageSize().getHeight());
		document = new Document(standardA4Size);
		this.writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));

		writer.setPageEvent(this);
		document.open();

		try {
			// Create result table
			LOG.info("Generate GRNA results PDF file...");

			chapterTitles = new ArrayList<String>();
			chapterTitles.add("Table of content");
			chapterTitles.add("Results - Processing details overview");
			chapterTitles.add("Results - GRNA Library subsequences occurences");

			if (!report.getUncorrespondingEntry().isEmpty()) {
				chapterTitles.add("Results - Entries without correspondance");
			}

			createTOC(chapterTitles);

			createChapters(chapterTitles.get(1), createProcessingInfo(), 1);

			document.setPageSize(pageSizeTable);
			createChapters(chapterTitles.get(2), createGRNAOccurencesTable(), 2);

			if (!report.getUncorrespondingEntry().isEmpty()) {
				document.setPageSize(pageSizeTableUnfound);
				createChapters(chapterTitles.get(3), createNotFoundTable(), 3);
			}

			LOG.info("Table generated");
		} catch (java.lang.OutOfMemoryError ome) {
			LOG.info("No more memory available");
			ome.printStackTrace();
		} finally {
			document.close();
		}

	}

	private List<Paragraph> createNotFoundTable() {
		Paragraph p = new Paragraph("Unfound entries table");

		PdfPTable table = new PdfPTable(4);

		Map<String, UnfoundStartSeqMap> unfoundEntries = report.getUncorrespondingEntry();
		LOG.info("Generate unfound table entries : " + unfoundEntries.size());
		Iterator<Map.Entry<String, UnfoundStartSeqMap>> it = unfoundEntries.entrySet().iterator();

		try {
			/* CREATION DE LA TABLE */

			// Fonts
			Font defaultFont = new Font(FontFamily.COURIER, 8, Font.BOLD);
			defaultFont.setColor(50, 50, 100);

			// Create the headers
			Font headerFont = new Font(FontFamily.COURIER, 10, Font.BOLD);
			headerFont.setColor(50, 100, 100);
			table.addCell(getHeaderCell("Index", headerFont));
			table.addCell(getHeaderCell("#", headerFont));
			table.addCell(getHeaderCell("Sub sequence", headerFont));
			table.addCell(getHeaderCell("Starting sequence", headerFont));
			table.setHeaderRows(1);

			float[] columnWidths = { 0.5f, 0.1f, 0.5f, 1.5f };
			table.setWidths(columnWidths);

			int count = 0;
			while (it.hasNext() && count <= 100000) {

				count++;
				Map.Entry<String, UnfoundStartSeqMap> pairs = (Map.Entry<String, UnfoundStartSeqMap>) it.next();
				UnfoundStartSeqMap values = pairs.getValue();

				Paragraph indexP = new Paragraph(Integer.toString(count), defaultFont);
				PdfPCell indexCell = new PdfPCell(indexP);

				Paragraph pSubseqOcc = new Paragraph(Integer.toString(values.getTotalCount()), defaultFont);
				PdfPCell subSeqCellOcc = new PdfPCell(pSubseqOcc);

				Paragraph pSubseq = new Paragraph(pairs.getKey(), defaultFont);
				PdfPCell subSeqCell = new PdfPCell(pSubseq);

				PdfPCell startSeqCell = new PdfPCell();
				Iterator<Map.Entry<String, Integer>> itSubseqstart = values.entrySet().iterator();
				while (itSubseqstart.hasNext()) {
					Map.Entry<String, Integer> subPair = (Map.Entry<String, Integer>) itSubseqstart.next();
					startSeqCell.addElement(new Phrase(String.format("%3s", subPair.getValue()) + "-" + subPair.getKey(), defaultFont));
				}

				if (count % 2 == 0) {
					indexCell.setBackgroundColor(Colors.getImpairtablecolor());
					subSeqCellOcc.setBackgroundColor(Colors.getImpairtablecolor());
					subSeqCell.setBackgroundColor(Colors.getImpairtablecolor());
					startSeqCell.setBackgroundColor(Colors.getImpairtablecolor());
				}

				table.addCell(indexCell);
				table.addCell(subSeqCellOcc);
				table.addCell(subSeqCell);
				table.addCell(startSeqCell);

				values.clear();
				if (count % 1000 == 0) {
					System.gc();
				}

			}

			p.add(table);
			table.setTotalWidth((document.getPageSize().getWidth() + document.leftMargin() + document.rightMargin()) / 7);

		} catch (DocumentException e) {
			LOG.error("Error when generating pdf table", e);
		}

		List<Paragraph> listPar = new ArrayList<Paragraph>();
		listPar.add(p);
		return listPar;
	}

	private List<Paragraph> createProcessingInfo() {
		List<Paragraph> listPar = new ArrayList<Paragraph>();

		Paragraph p1 = new Paragraph("Library description");
		p1.setIndentationLeft(20);

		Paragraph pTableContainer = new Paragraph();

		PdfPTable tableLib = new PdfPTable(2);
		try {
			float[] columnWidths = { 0.5f, 1f };
			tableLib.setWidths(columnWidths);

			// Input Library files
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

			// Input Library size
			PdfPCell libSizeCell = new PdfPCell(new Paragraph("GRNA library size", phraseFont));
			libSizeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell.setBackgroundColor(baseBackgroundCellColor);
			tableLib.addCell(libSizeCell);
			PdfPCell libSizeCell2 = new PdfPCell(new Paragraph(Integer.toString(report.getLibrary().size()), phraseFont));
			libSizeCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell2.setBackgroundColor(baseBackgroundCellColor);
			tableLib.addCell(libSizeCell2);

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		pTableContainer.add(tableLib);
		p1.add(pTableContainer);
		listPar.add(p1);

		Paragraph p2 = new Paragraph("Input FastQ description");
		p2.setIndentationLeft(20);

		Paragraph pTableContainer2 = new Paragraph();
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
			e.printStackTrace();
		}
		pTableContainer2.add(tableFastq);
		p2.add(pTableContainer2);
		listPar.add(p2);

		Paragraph p3 = new Paragraph("Computations properties");
		p3.setIndentationLeft(20);

		Paragraph pTableContainer3 = new Paragraph();
		PdfPTable tableOptions = new PdfPTable(2);
		try {
			float[] columnWidths = { 0.5f, 1f };
			tableOptions.setWidths(columnWidths);

			// Number of sequences by chunk file
			PdfPCell fileIndex = new PdfPCell(new Paragraph("Sequences / chunk file", phraseFont));
			fileIndex.setBackgroundColor(baseBackgroundCellColor);
			fileIndex.setVerticalAlignment(Element.ALIGN_MIDDLE);
			tableOptions.addCell(fileIndex);

			String fastqFile = Integer.toString(report.getChunkSize());
			Paragraph pFilePath = new Paragraph(fastqFile, phraseFont);
			PdfPCell filePath = new PdfPCell(pFilePath);
			filePath.setVerticalAlignment(Element.ALIGN_MIDDLE);
			filePath.setBackgroundColor(baseBackgroundCellColor);
			tableOptions.addCell(filePath);

			// Extraction pattern string
			PdfPCell libSizeCell = new PdfPCell(new Paragraph("Extraction Pattern", phraseFont));
			libSizeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell.setBackgroundColor(baseBackgroundCellColor);
			tableOptions.addCell(libSizeCell);

			ExtractionPattern model = report.getAnalyseConfig().getPattern();
			String pattern = model.getExtractionSequence().toUpperCase();
			PdfPCell extractionPatternCell = new PdfPCell();

			Phrase p = new Phrase();
			p.add(new Phrase(pattern.substring(0, model.getSkippedCharCount()), phraseFont));
			p.add(new Phrase(pattern.substring(model.getSkippedCharCount(), model.getSkippedCharCount() + model.getGrnaSubSequenceLength()), phraseFontRed));
			p.add(new Phrase(pattern.substring(model.getSkippedCharCount() + model.getGrnaSubSequenceLength(), pattern.length()), phraseFont));

			extractionPatternCell.addElement(p);

			// Pattern properties table
			PdfPTable tablePatternProps = new PdfPTable(2);

			// Number of skipped chars
			PdfPCell skCell = new PdfPCell(new Paragraph("Skipped characters", phraseFont));

			skCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			String skValueStr = Integer.toString(model.getSkippedCharCount());
			Paragraph pSkValue = new Paragraph(skValueStr, phraseFont);
			PdfPCell skValueCell = new PdfPCell(pSkValue);
			skValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			// Length of subsequence
			PdfPCell lengthCell = new PdfPCell(new Paragraph("GRNA Subsequence length", phraseFont));

			lengthCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			String lengthValueStr = Integer.toString(model.getGrnaSubSequenceLength());
			Paragraph pLengthValue = new Paragraph(lengthValueStr, phraseFont);
			PdfPCell lengthValueCell = new PdfPCell(pLengthValue);
			lengthValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			skCell.setBorderColor(lightBorderGreen);
			skCell.setBackgroundColor(lightGreen);
			skValueCell.setBorderColor(lightBorderGreen);
			skValueCell.setBackgroundColor(lightGreen);
			lengthCell.setBorderColor(lightBorderGreen);
			lengthCell.setBackgroundColor(lightGreen);
			lengthValueCell.setBorderColor(lightBorderGreen);
			lengthValueCell.setBackgroundColor(lightGreen);

			tablePatternProps.addCell(skCell);
			tablePatternProps.addCell(skValueCell);
			tablePatternProps.addCell(lengthCell);
			tablePatternProps.addCell(lengthValueCell);

			// Add the subtable to the current cell
			extractionPatternCell.addElement(new Phrase(" "));
			extractionPatternCell.addElement(tablePatternProps);

			extractionPatternCell.setBackgroundColor(baseBackgroundCellColor);
			extractionPatternCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			tableOptions.addCell(extractionPatternCell);

			// Number of sequences by chunk file
			PdfPCell toleranceCell = new PdfPCell(new Paragraph("1 character mismatch allowed", phraseFont));
			toleranceCell.setBackgroundColor(baseBackgroundCellColor);
			toleranceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			tableOptions.addCell(toleranceCell);

			String toleranceCellValStr = Boolean.toString(report.getAnalyseConfig().getPatternAttributes().isAllowOneMismatch());
			Paragraph pToleranceVal = new Paragraph(toleranceCellValStr, phraseFont);
			PdfPCell cellTolerVal = new PdfPCell(pToleranceVal);
			cellTolerVal.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cellTolerVal.setBackgroundColor(baseBackgroundCellColor);
			tableOptions.addCell(cellTolerVal);

			// Number of sequences by chunk file
			PdfPCell shiftCell = new PdfPCell(new Paragraph("Check for shifted sub sequences (1 > right)", phraseFont));
			shiftCell.setBackgroundColor(baseBackgroundCellColor);
			shiftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			tableOptions.addCell(shiftCell);

			String shiftCellValStr = Boolean.toString(report.getAnalyseConfig().getPatternAttributes().isCheckForShifted());
			Paragraph pShiftVal = new Paragraph(shiftCellValStr, phraseFont);
			PdfPCell cellShiftVal = new PdfPCell(pShiftVal);
			cellShiftVal.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cellShiftVal.setBackgroundColor(baseBackgroundCellColor);
			tableOptions.addCell(cellShiftVal);

		} catch (DocumentException e) {
			LOG.error("Error when generating pdf table", e);
		}
		pTableContainer3.add(tableOptions);
		p3.add(pTableContainer3);
		listPar.add(p3);

		Paragraph p4 = new Paragraph("Calculation results");
		p4.setIndentationLeft(20);

		Paragraph pTableContainer4 = new Paragraph();

		PdfPTable tableInfo = new PdfPTable(2);
		try {
			float[] columnWidths = { 0.5f, 1f };
			tableInfo.setWidths(columnWidths);

			// Duration info
			PdfPCell fileIndex = new PdfPCell(new Paragraph("Processing time", phraseFont));
			fileIndex.setBackgroundColor(baseBackgroundCellColor);
			fileIndex.setVerticalAlignment(Element.ALIGN_MIDDLE);
			tableInfo.addCell(fileIndex);

			PdfPCell filePath = new PdfPCell(new Paragraph(report.getProcessingTimeFormatted(), phraseFont));
			filePath.setVerticalAlignment(Element.ALIGN_MIDDLE);
			filePath.setBackgroundColor(baseBackgroundCellColor);
			tableInfo.addCell(filePath);

			// Number of processed chunks
			PdfPCell libSizeCell = new PdfPCell(new Paragraph("Chunks processed", phraseFont));
			libSizeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell.setBackgroundColor(baseBackgroundCellColor);
			tableInfo.addCell(libSizeCell);

			String str = Integer.toString(report.getTotalChunksProcessed());
			PdfPCell libSizeCell2 = new PdfPCell(new Paragraph(str, phraseFont));
			libSizeCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			libSizeCell2.setBackgroundColor(baseBackgroundCellColor);
			tableInfo.addCell(libSizeCell2);

			// Occurences found
			PdfPCell occCell = new PdfPCell(new Paragraph("Match occurence", phraseFont));
			occCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			occCell.setBackgroundColor(baseBackgroundCellColorImportant);
			tableInfo.addCell(occCell);

			PdfPCell occCell2 = new PdfPCell(new Paragraph(Long.toString(report.getTotalOccurencesFound()) + "  (" + formatSansExpo(100.0 * report.getTotalOccurencesFound() / (report.getTotalLineProcessed()), 2) + " %)", phraseFont));
			occCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			occCell2.setBackgroundColor(baseBackgroundCellColorImportant);
			tableInfo.addCell(occCell2);

			// Occurences NOT found
			PdfPCell occNotFoundCell = new PdfPCell(new Paragraph("Unmatch occurence", phraseFont));
			occNotFoundCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			occNotFoundCell.setBackgroundColor(baseBackgroundCellColorImportant);
			tableInfo.addCell(occNotFoundCell);

			long unfoundOccurence = report.getTotalLineProcessed() - report.getTotalOccurencesFound();
			PdfPCell occNotFoundCell2 = new PdfPCell(new Paragraph(unfoundOccurence + "  (" + formatSansExpo(100.0 * unfoundOccurence / (report.getTotalLineProcessed()), 2) + " %)", phraseFont));
			occNotFoundCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			occNotFoundCell2.setBackgroundColor(baseBackgroundCellColorImportant);
			tableInfo.addCell(occNotFoundCell2);

		} catch (DocumentException e) {
			LOG.error("Error when generating pdf table", e);
		}
		pTableContainer4.add(tableInfo);
		p4.add(pTableContainer4);

		listPar.add(p4);
		return listPar;
	}

	private List<Paragraph> createGRNAOccurencesTable() {

		Paragraph p = new Paragraph("grna occurences table");

		PdfPTable table = null;

		List<ReferenceGene> listGRNA = report.getLibrary().getGenes();

		try {
			/* CREATION DE LA TABLE */
			table = new PdfPTable(5);

			// Fonts
			Font defaultFont = new Font(FontFamily.COURIER, 8, Font.BOLD);
			defaultFont.setColor(50, 50, 100);

			// Create the headers
			Font headerFont = new Font(FontFamily.COURIER, 10, Font.BOLD);
			headerFont.setColor(50, 100, 100);
			table.addCell(getHeaderCell("Index", headerFont));
			table.addCell(getHeaderCell("Gene ID", headerFont));
			table.addCell(getHeaderCell("GRNA Seq", headerFont));
			table.addCell(getHeaderCell("Occurence", headerFont));
			table.addCell(getHeaderCell("Occurence %", headerFont));
			table.setHeaderRows(1);

			float[] columnWidths = { 0.5f, 0.5f, 1f, 0.5f, 0.5f };
			table.setWidths(columnWidths);

			for (int i = 0; i < listGRNA.size(); i++) {
				ReferenceGene grna = listGRNA.get(i);

				Paragraph indexP = new Paragraph("" + (i + 1), defaultFont);
				PdfPCell indexCell = new PdfPCell(indexP);

				Paragraph indexID = new Paragraph(grna.getName(), defaultFont);
				PdfPCell cellID = new PdfPCell(indexID);

				Paragraph indexSeq = new Paragraph(grna.getAssociatedSequence(), defaultFont);
				PdfPCell cellSeq = new PdfPCell(indexSeq);

				int grnaOccurence = report.getOccurenceCount(grna.getAssociatedSequence());
				Paragraph indexOcc = new Paragraph(Integer.toString(grnaOccurence), defaultFont);
				PdfPCell cellOcc = new PdfPCell(indexOcc);

				Paragraph indexOccPercent = new Paragraph(formatSansExpo(report.getOccurencePercent(grna.getAssociatedSequence()), 4), defaultFont);
				PdfPCell cellOccPercent = new PdfPCell(indexOccPercent);

				if (grnaOccurence == 0) {
					indexCell.setBackgroundColor(Colors.getErrortablecolor());
					cellID.setBackgroundColor(Colors.getErrortablecolor());
					cellSeq.setBackgroundColor(Colors.getErrortablecolor());
					cellOcc.setBackgroundColor(Colors.getErrortablecolor());
					cellOccPercent.setBackgroundColor(Colors.getErrortablecolor());
				} else {
					if (i % 2 == 0) {
						indexCell.setBackgroundColor(Colors.getImpairtablecolor());
						cellID.setBackgroundColor(Colors.getImpairtablecolor());
						cellSeq.setBackgroundColor(Colors.getImpairtablecolor());
						cellOcc.setBackgroundColor(Colors.getImpairtablecolor());
						cellOccPercent.setBackgroundColor(Colors.getImpairtablecolor());
					}
				}

				table.addCell(indexCell);
				table.addCell(cellID);
				table.addCell(cellSeq);
				table.addCell(cellOcc);
				table.addCell(cellOccPercent);

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

	private PdfPHeaderCell getHeaderCell(String title, Font headerFont) {

		Paragraph contentParagraphe = new Paragraph(title, headerFont);
		contentParagraphe.setAlignment(Paragraph.ALIGN_CENTER);

		Float fontSize = headerFont.getSize();

		Float capHeight = 0f;
		if (headerFont.getBaseFont() != null) {
			capHeight = headerFont.getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize);
		}

		PdfPHeaderCell cell = new PdfPHeaderCell();
		cell.addElement(contentParagraphe);
		cell.setPaddingBottom(fontSize - capHeight);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		return cell;
	}

	public static String formatSansExpo(double d, int nbchiffres) {

		if (Double.isInfinite(d)) {
			// return infinite unicode character
			return "\u221E";
		} else {
			String formatPattern = "%." + nbchiffres + "f";
			return String.format(formatPattern, d);
		}

	}

	private void createTOC(List<String> chaptersTitles) throws DocumentException {
		// add a small introduction chapter the shouldn't be counted.
		Paragraph tableContentPar = new Paragraph(chapterTitles.get(0), this.chapterFont);
		tableContentPar.setAlignment(Paragraph.ALIGN_CENTER);
		final Chapter intro = new Chapter(tableContentPar, 0);
		intro.setNumberDepth(0);

		this.document.add(intro);
		this.document.add(new Paragraph(" "));
		this.document.add(new Paragraph(" "));
		this.document.add(new Paragraph(" "));

		for (int i = 1; i < chaptersTitles.size(); i++) {

			final String title = chaptersTitles.get(i);
			final Chunk chunk = new Chunk(title).setLocalGoto(title);
			this.document.add(new Paragraph(chunk));

			// Add a placeholder for the page reference
			this.document.add(new VerticalPositionMark() {
				@Override
				public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx, final float ury, final float y) {
					final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
					tocPlaceholder.put(title, createTemplate);

					canvas.addTemplate(createTemplate, urx - 50, y);
				}
			});

			this.document.add(new Paragraph(" "));
		}
	}

	private void createChapters(String title, List<Paragraph> paragraphList, int chapterCount) throws DocumentException {
		// append the chapter
		final Chunk chunk = new Chunk(title, this.chapterFont).setLocalDestination(title);
		final Chapter chapter = new Chapter(new Paragraph(chunk), chapterCount);
		chapter.setNumberDepth(0);

		for (Iterator<Paragraph> iterator = paragraphList.iterator(); iterator.hasNext();) {
			Paragraph element = (Paragraph) iterator.next();
			chapter.addSection(element);
		}

		// When we wrote the chapter, we now the pagenumber
		final PdfTemplate template = this.tocPlaceholder.get(title);
		template.beginText();
		template.setFontAndSize(this.baseFont, 12);
		template.setTextMatrix(50 - this.baseFont.getWidthPoint(String.valueOf(this.writer.getPageNumber()), 12), 0);
		template.showText(String.valueOf(this.writer.getPageNumber() + 1));
		template.endText();

		this.document.add(chapter);
	}

	@Override
	public void onChapter(final PdfWriter writer, final Document document, final float paragraphPosition, final Paragraph title) {
		this.pageByTitle.put(title.getContent(), writer.getPageNumber());
	}

	@Override
	public void onSection(final PdfWriter writer, final Document document, final float paragraphPosition, final int depth, final Paragraph title) {
		this.pageByTitle.put(title.getContent(), writer.getPageNumber());
	}

	/** The template with the total number of pages. */
	PdfTemplate total;

	/**
	 * Allows us to change the content of the header.
	 * 
	 * @param header
	 *            The new header String
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Creates the PdfTemplate that will hold the total number of pages.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
		total = writer.getDirectContent().createTemplate(30, 14);
	}

	/**
	 * Adds a header to every page
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onEndPage(PdfWriter writer, Document document) {
		if (writer.getPageNumber() != 1) {
			PdfPTable table = new PdfPTable(3);
			try {
				table.setWidths(new int[] { 24, 24, 2 });
				table.setTotalWidth(527);
				table.setLockedWidth(true);
				table.getDefaultCell().setFixedHeight(20);
				table.getDefaultCell().setBorder(Rectangle.BOTTOM);
				table.addCell(new Phrase(header, headerFont));
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), headerFont));

				PdfPCell cell = new PdfPCell(Image.getInstance(total));
				cell.setBorder(Rectangle.BOTTOM);
				table.addCell(cell);

				table.writeSelectedRows(0, -1, 34, writer.getPageSize().getHeight() - 10, writer.getDirectContent());
			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}
	}

	/**
	 * Fills out the total number of pages before the document is closed.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onCloseDocument(PdfWriter writer, Document document) {
		ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1), headerFont), 2, 2, 0);
	}
}
