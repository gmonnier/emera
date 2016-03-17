package com.gmo.fileGenerator.pdf;

import java.io.File;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.InvalidPdfException;
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

public abstract class GenericPDFGenerator extends PdfPageEventHelper {

	private static Logger LOG = Log4JLogger.logger;

	/** The template with the total number of pages. */
	private PdfTemplate total;

	private BaseFont baseFont;

	protected final Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.NORMAL);

	protected final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new BaseColor(31, 57, 119));

	protected final Font phraseFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new BaseColor(31, 57, 119));

	protected final Font phraseFontRed = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new BaseColor(180, 5, 5));

	protected final BaseColor lightGreen = new BaseColor(210, 225, 160);

	protected final BaseColor lightBorderGreen = new BaseColor(120, 150, 4);

	protected final BaseColor baseBackgroundCellColor = new BaseColor(240, 240, 255);

	protected final BaseColor baseBackgroundCellColorImportant = new BaseColor(240, 220, 220);

	protected final static Rectangle standardA4Size = PDFPageSize.A4.getITextFormatPdfPageSize();

	/** The header text. */
	private String header;

	// table to store placeholder for all chapters and sections
	private final Map<String, PdfTemplate> tocPlaceholder = new HashMap<>();

	// store the chapters and sections with their title here.
	private final Map<String, Integer> pageByTitle = new HashMap<>();

	private Document document;

	private PdfWriter writer;

	private List<String> chapterTitles;

	private boolean invalidOperation;

	public GenericPDFGenerator(String header, File outputFile) {

		this.invalidOperation = false;
		this.header = header;
		this.chapterTitles = new ArrayList<>();

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
			document = new Document(standardA4Size);
			this.writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
			writer.setPageEvent(this);

		} catch (Exception e) {
			LOG.info("delete file " + outputFile.delete());
			invalidOperation = true;
		}
	}

	public final void generatePDFFile() throws InvalidPdfException, DocumentException {
		if (invalidOperation) {
			throw new InvalidPdfException("Impossible to generate PDF file");
		} else {
			try {
				document.open();
				createChaptersTitles(chapterTitles);
				createTOC(chapterTitles);
				createChaptersContent();
			} catch (Throwable e) {

			} finally {
				if (document != null) {
					document.close();
				}
				if (writer != null) {
					writer.close();
				}
			}
		}

	}

	/**
	 * Abstract method used to dsefine chapter titles. Children class should
	 * implement this method and define in the given List all chapters titles.
	 * 
	 * @param listChapters
	 *            list of titles for all chapters in the document, including the
	 *            first item which is the table of content.
	 */
	protected abstract void createChaptersTitles(List<String> listChapters);

	/**
	 * This method should be defined by children class and use createChapter
	 * method to define the content of EVERY chapters defined in the title list.
	 * Be aware that it is not needed to define content for index 0, since this
	 * will contains the table of content for this document.
	 * 
	 * @throws DocumentException
	 *             if an error occurs while writting the toc.
	 */
	protected abstract void createChaptersContent() throws DocumentException;

	/**
	 * Create the table of content page with the given chapters titles list.
	 * 
	 * @param chaptersTitles
	 *            the list of the chapters titles.
	 * @throws DocumentException
	 *             if an error occurs while writting the toc.
	 */
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

	/**
	 * Write the content of chapter at index chapterIndex with a default
	 * standard A4 page size.
	 * 
	 * @param chapterIndex
	 *            index of chapter to be filled.
	 * @param chapterContent
	 *            the content to be put into this chapter
	 * @throws DocumentException
	 *             if an error occured while writing document.
	 */
	protected void createChapter(int chapterIndex, List<Paragraph> chapterContent) throws DocumentException {
		createChapter(chapterIndex, chapterContent, standardA4Size);
	}

	/**
	 * Write the content of chapter at index chapterIndex with a specified page
	 * size.
	 * 
	 * @param chapterIndex
	 *            the index of the chapter to be filled
	 * @param chapterContent
	 *            the content to be put into this chapter
	 * @param pageSize
	 *            the size of the pages that will be used for this chapter
	 * @throws DocumentException
	 *             if an error occured while writing document.
	 */
	protected void createChapter(int chapterIndex, List<Paragraph> chapterContent, Rectangle pageSize) throws DocumentException {

		document.setPageSize(pageSize);
		String title = chapterTitles.get(chapterIndex);
		// append the chapter
		final Chunk chunk = new Chunk(title, this.chapterFont).setLocalDestination(title);
		final Chapter chapter = new Chapter(new Paragraph(chunk), chapterIndex);
		chapter.setNumberDepth(0);

		for (Iterator<Paragraph> iterator = chapterContent.iterator(); iterator.hasNext();) {
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
				table.setTotalWidth(writer.getPageSize().getWidth() - 50);
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

	protected static String formatSansExpo(double d, int nbchiffres) {

		if (Double.isInfinite(d)) {
			// return infinite unicode character
			return "\u221E";
		} else {
			String formatPattern = "%." + nbchiffres + "f";
			return String.format(formatPattern, d);
		}

	}

	protected PdfPHeaderCell getHeaderCell(String title, Font headerFont) {

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

}
