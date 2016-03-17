package com.gmo.fileGenerator.pdf.util;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

public class PDFPageSize {

	public static final PDFPageSize A4 = new PDFPageSize(PageSize.A4, "A4");
	public static final PDFPageSize A3 = new PDFPageSize(PageSize.A3, "A3");
	public static final PDFPageSize A2 = new PDFPageSize(PageSize.A2, "A2");
	public static final PDFPageSize A1 = new PDFPageSize(PageSize.A1, "A1");
	public static final PDFPageSize A0 = new PDFPageSize(PageSize.A0, "A0");
	public static final PDFPageSize LEGAL = new PDFPageSize(PageSize.LEGAL, "LEGAL");
	public static final PDFPageSize LETTER = new PDFPageSize(PageSize.LETTER, "LETTER");
	public static final PDFPageSize TABLOID = new PDFPageSize(PageSize.TABLOID, "TABLOID");
	public static final PDFPageSize ARCH_A = new PDFPageSize(PageSize.ARCH_A, "ARCH_A");
	public static final PDFPageSize ARCH_B = new PDFPageSize(PageSize.ARCH_B, "ARCH_B");

	private Rectangle pdfPageSize;
	private String displayValue;

	public PDFPageSize(Rectangle pdfPageSize, String disp) {
		this.pdfPageSize = pdfPageSize;
		this.displayValue = disp;
	}

	public Rectangle getITextFormatPdfPageSize() {
		return pdfPageSize;
	}
	
	@Override
	public String toString() {
		return displayValue;
	}


}
