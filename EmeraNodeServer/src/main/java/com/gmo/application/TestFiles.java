package com.gmo.application;

import java.io.File;
import java.io.IOException;

public class TestFiles {

	public static void main(String[] args) {
		File pdfOutputTmpFile = new File("tmp" + "/" + "userName" + "/" + "analyseID", "pdf_report");
		// if file doesnt exists, then create it
		if (!pdfOutputTmpFile.exists()) {
			try {
				pdfOutputTmpFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
