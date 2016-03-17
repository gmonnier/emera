package com.gmo.ui.lookAndFeel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import com.gmo.ui.lookAndFeel.looksEnums.ComponentStateColors;
import com.gmo.ui.lookAndFeel.looksEnums.GeneralColorTheme;
import com.gmo.ui.utility.GraphicsUtility;

public class Painting {

	private final static Color darktransparent = new Color(0, 0, 0, 50);

	private final static Color DEFAULT_BACK_1 = Color.white;
	private final static Color DEFAULT_BACK_2 = Color.lightGray;

	public static void strippedToolBarPaint(Graphics2D g2, int width, int height) {

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		GradientPaint gp = new GradientPaint(0, 0, Color.gray, 0, height, Color.darkGray, true);
		g2.setPaint(gp);
		g2.fillRect(0, 0, width, height);

		GraphicsUtility.paint45StripedTexture(g2, 0, 0, width, height, 5, darktransparent);

	}

	public static void dialogGrayGradientVerticalBGPaint(Graphics2D g2, int width, int height) {

		GradientPaint deg = new GradientPaint(0, 0, DEFAULT_BACK_1, 0, (int) (height * 2.5 / 3), DEFAULT_BACK_2, true);
		g2.setPaint(deg);
		g2.fillRect(0, 0, width, height);
	}
	
	
	public static void dialogGrayGradientHorizontalBGPaint(Graphics2D g2, int width, int height) {

		GradientPaint deg = new GradientPaint(-500, 0, Color.gray, width, height, Color.white, true);
		g2.setPaint(deg);
		g2.fillRect(0, 0, width, height);

	}

	public static void dialogGrayGradiantVerticalBGPaint(Graphics2D g2, int width, int height) {

		GradientPaint deg = new GradientPaint(0, 0, DEFAULT_BACK_1, 0, (int) (height * 2.5 / 3), DEFAULT_BACK_2, true);
		g2.setPaint(deg);
		g2.fillRect(0, 0, width, height);
	}

	public static void paintTopToolBar(Graphics2D g2, int width, int height) {
		paintTopToolBar(g2, 0, 0, width, height);
	}

	public static void paintTopToolBar(Graphics2D g2, int x, int y, int width, int height) {

		if (Colors.theme == GeneralColorTheme.BLUE) {

			g2.setPaint(new GradientPaint(x, y, new Color(37, 47, 64), width, height, new Color(50, 86, 113)));
			g2.fillRect(x, y, width, height);

		} else if (Colors.theme == GeneralColorTheme.BLACK_AND_WHITE) {

			g2.setPaint(new GradientPaint(x, y, new Color(45, 45, 45), width, height, new Color(90, 90, 90)));
			g2.fillRect(x, y, width, height);

		}
	}

	public static void paintTopToolBarSpecificPath(Graphics2D g2, int x, int y, int width, int height, GeneralPath p, ComponentStateColors colorsType) {

		switch (colorsType) {
		case NORMAL: {
			paintTopToolBarSpecificPath(g2, x, y, width, height, p);
			break;
		}
		default: {
			g2.setPaint(Colors.getToolBarVerticalGradient(x, y, width, height, colorsType));
			g2.fill(p);
		}
		}
	}
	
	public static void dialogGrayGradientVerticalBGPaintInv(Graphics2D g2, int width, int height) {

		GradientPaint deg = new GradientPaint(0, 0, DEFAULT_BACK_2, 0, (int) (height * 2.5 / 3), DEFAULT_BACK_1, true);
		g2.setPaint(deg);
		g2.fillRect(0, 0, width, height);
	}

	public static void paintTopToolBarSpecificPath(Graphics2D g2, int x, int y, int width, int height, GeneralPath p) {

		if (Colors.theme == GeneralColorTheme.BLUE) {
			g2.setPaint(new GradientPaint(x, y, new Color(37, 47, 64), width, height, new Color(50, 86, 113)));
			g2.fill(p);
		} else if (Colors.theme == GeneralColorTheme.BLACK_AND_WHITE) {
			g2.setPaint(new GradientPaint(x, y, new Color(45, 45, 45), width, height, new Color(90, 90, 90)));
			g2.fill(p);
		}
	}

	public static void paintGlossyVerticalBlackToolBar(Graphics2D g2, int width, int height) {
		g2.setPaint(new GradientPaint(0, 0, new Color(130, 130, 130), width / 2, 0, new Color(40, 40, 40)));
		g2.fillRect(0, 0, width / 2, height);
		g2.setPaint(new GradientPaint(width / 2, 0, new Color(20, 20, 20), width, 0, new Color(100, 100, 100)));
		g2.fillRect(width / 2, 0, width, height);

	}

}
