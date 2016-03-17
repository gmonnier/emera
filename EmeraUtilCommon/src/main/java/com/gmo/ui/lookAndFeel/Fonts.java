package com.gmo.ui.lookAndFeel;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Fonts {

	private static final Font fontButton = new Font("Times", Font.BOLD, 14);

	private static final Font fontGridSpacingOpt = new Font("Times", Font.BOLD, 32);

	private static final Font fontBackGroundMessage = new Font("Times", Font.BOLD, 46);

	private static final Font fontTextInfo = new Font("Times", Font.PLAIN, 12);

	private static final Font fontTableCell = new Font("Times", Font.PLAIN, 12);

	private static final Font fontPresetList = new Font("Times", Font.BOLD, 20);

	public static ArrayList<String> fonttest = new ArrayList<String>();
	
	public static void loadCustomFont() {
		// Load all font from custom font directory
		File fontDir = new File("Fonts");
		File[] fontfiles = fontDir.listFiles();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		if (fontfiles != null) {
			for (int i = 0; i < fontfiles.length; i++) {
				try {
					Font f = Font.createFont(Font.TRUETYPE_FONT, fontfiles[i]);
					ge.registerFont(f);
					fonttest.add(f.getFontName());
				} catch (FontFormatException e) {
					System.err.println("FontFormatException Error while extracting custom fonts for " + fontfiles[i]);
				} catch (IOException e) {
					System.err.println("I/O Error while extracting custom fonts for " + fontfiles[i]);
				}
			}
		} else {
			System.out.println("No folder where fonts may be extracted. Use default.");
		}
	}
	
	public static String getDefaultTitleBarFontName() {
		return "Monospaced";
	}

	public static Font getFontButton() {
		return fontButton;
	}

	public static Font getFontbackgroundmessage() {
		return fontBackGroundMessage;
	}

	public static Font getFontGridSpacingOpt() {
		return fontGridSpacingOpt;
	}

	public static Font getFonttextinfo() {
		return fontTextInfo;
	}

	public static Font getFontpresetlist() {
		return fontPresetList;
	}

	public static Font getFonttablecell() {
		return fontTableCell;
	}

	public static Font getDefaultBold(int size) {
		return new Font("Monospaced", Font.BOLD, size);
	}
	
	public static Font getDefault(int size) {
		return new Font("Monospaced", Font.PLAIN, size);
	}
	
	public static Font getDefaultTimesBold(int size) {
		return new Font("Times", Font.BOLD, size);
	}
	
	public static Font getDefaultTimes(int size) {
		return new Font("Times", Font.PLAIN, size);
	}



}
