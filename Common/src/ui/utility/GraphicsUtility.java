//*******************************************************************
//
//      Oce Engineering Creteil: PRODUCT DOCUMENT        CONFIDENTIAL
//
//       R&D-filename    :   GraphicsUtility.java
//
//       Title           :
//       Abstract        :
//
//
//
//
//       Keywords        :
//
//       %version:       3 %
//       %created_by:    qgmo %
//       %date_created:  Tue Dec 07 15:10:40 2010 %
//       %full_filespec: GraphicsUtility.java~3:java:CRCOBALT#1 %
//
//   Copyright 2004 Oce-Technologies B.V., Venlo, The Netherlands
//
//*******************************************************************
package ui.utility;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GraphicsUtility {

	private static Font atcFont = new Font("Times", Font.CENTER_BASELINE, 12);

	public static void paint45StripedTexture(Graphics2D g2, int x, int y, int width, int height, int spacing, Paint paintType) {

		g2.setPaint(paintType);

		int depx = 0;
		int depy = 0;
		int endx = 0;
		int endy = 0;

		// Coordonnees e l'interieur du rectangle
		int depInx = 0;
		int depIny = 0;
		int endInx = 0;
		int endIny = 0;

		while (depIny < height) {

			depx += spacing;
			depy = 0;
			endx = 0;
			endy += spacing;

			// Point de depart
			depInx = depx;
			depIny = depy;
			if (depx > width) {
				depInx = width;
				depIny = depx - width;
			}

			// point d'arrivee
			endInx = endx;
			endIny = endy;

			/*
			 * --- AJOUT DE CORRECTION SUR DEBORDEMENT DE CASE ---
			 */
			int depYTmp = depIny;
			int endYTmp = endIny;
			int depXTmp = depInx;
			int endXTmp = endInx;

			if (depIny > height) {
				depYTmp = height;
				depXTmp = depInx - height;
			}

			if (endIny > height) {
				endYTmp = height;
				endXTmp = endIny - height;
			}
			// --- Fin Ajout ---
			if (depXTmp != endXTmp && depYTmp != endYTmp)
				g2.drawLine(depXTmp + x, depYTmp + y, endXTmp + x, endYTmp + y);
		}
	}

	public static void paintHorizontalStripedTexture(Graphics2D g2, int x, int y, int width, int height, int spacing, Paint paintType) {
		int tmpY = y;
		g2.setPaint(paintType);

		while (tmpY < y + height) {
			g2.drawLine(x, tmpY, x + width, tmpY);
			tmpY += spacing;
		}
	}

	public static void paintVerticalStripedTexture(Graphics2D g2, int x, int y, int width, int height, int spacing, Paint paintType) {
		int tmpX = x;
		g2.setPaint(paintType);

		while (tmpX < x + width) {
			g2.drawLine(tmpX, y, tmpX, y + height);
			tmpX += spacing;
		}
	}

	public static void ombrerContourInterieur(Graphics2D g2, int x, int y, int dx, int dy, Color c, int sizeOmbre) {

		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		// Cote haut
		g2.setPaint(new GradientPaint(x, y, c, x, y + sizeOmbre, new Color(0, 0, 0, 0)));
		GeneralPath pathHaut = new GeneralPath();
		pathHaut.moveTo(x - 1, y - 1);
		pathHaut.lineTo(x + dx + 1, y - 1);
		pathHaut.lineTo(x + dx - sizeOmbre + 1, y + sizeOmbre);
		pathHaut.lineTo(x + sizeOmbre - 1, y + sizeOmbre);
		pathHaut.closePath();
		g2.fill(pathHaut);

		// Cote gauche
		g2.setPaint(new GradientPaint(x, y, c, x + sizeOmbre, y, new Color(0, 0, 0, 0)));
		GeneralPath pathGauche = new GeneralPath();
		pathGauche.moveTo(x, y);
		pathGauche.lineTo(x + sizeOmbre, y + sizeOmbre);
		pathGauche.lineTo(x + sizeOmbre, y + dy - sizeOmbre);
		pathGauche.lineTo(x, y + dy);
		pathGauche.closePath();
		g2.fill(pathGauche);

		// Cote bas
		g2.setPaint(new GradientPaint(x, y + dy, c, x, y + dy - sizeOmbre, new Color(0, 0, 0, 0)));
		GeneralPath pathBas = new GeneralPath();
		pathBas.moveTo(x - 1, y + dy);
		pathBas.lineTo(x + sizeOmbre - 1, y + dy - sizeOmbre);
		pathBas.lineTo(x + dx - sizeOmbre + 1, y + dy - sizeOmbre);
		pathBas.lineTo(x + dx + 1, y + dy);
		pathBas.closePath();
		g2.fill(pathBas);

		// Cote droit
		g2.setPaint(new GradientPaint(dx + x, y, c, x + dx - sizeOmbre, y, new Color(0, 0, 0, 0)));
		GeneralPath pathDroit = new GeneralPath();
		pathDroit.moveTo(x + dx, y + dy);
		pathDroit.lineTo(x + dx - sizeOmbre, y + dy - sizeOmbre);
		pathDroit.lineTo(x + dx - sizeOmbre, y + sizeOmbre);
		pathDroit.lineTo(x + dx, y);
		pathDroit.closePath();
		g2.fill(pathDroit);
	}
	
	public static void ombrerContourExterieur2(Graphics2D g2, int x, int y, int dx, int dy, Color c, int sizeOmbre) {

		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		// Cote haut
		g2.setPaint(new GradientPaint(x, y, c, x, y - sizeOmbre, new Color(0, 0, 0, 0)));
		GeneralPath pathHaut = new GeneralPath();
		pathHaut.moveTo(x , y );
		pathHaut.lineTo(x + dx , y );
		pathHaut.lineTo(x + dx + sizeOmbre , y - sizeOmbre);
		pathHaut.lineTo(x - sizeOmbre , y - sizeOmbre);
		pathHaut.closePath();
		g2.fill(pathHaut);

		// Cote gauche
		g2.setPaint(new GradientPaint(x, y, c, x - sizeOmbre, y, new Color(0, 0, 0, 0)));
		GeneralPath pathGauche = new GeneralPath();
		pathGauche.moveTo(x, y);
		pathGauche.lineTo(x - sizeOmbre, y - sizeOmbre);
		pathGauche.lineTo(x - sizeOmbre, y + dy + sizeOmbre);
		pathGauche.lineTo(x, y + dy);
		pathGauche.closePath();
		g2.fill(pathGauche);

		// Cote bas
		g2.setPaint(new GradientPaint(x, y + dy, c, x, y + dy + sizeOmbre, new Color(0, 0, 0, 0)));
		GeneralPath pathBas = new GeneralPath();
		pathBas.moveTo(x , y + dy);
		pathBas.lineTo(x - sizeOmbre, y + dy + sizeOmbre);
		pathBas.lineTo(x + dx + sizeOmbre , y + dy + sizeOmbre);
		pathBas.lineTo(x + dx , y + dy);
		pathBas.closePath();
		g2.fill(pathBas);

		// Cote droit
		g2.setPaint(new GradientPaint(dx + x, y, c, x + dx + sizeOmbre, y, new Color(0, 0, 0, 0)));
		GeneralPath pathDroit = new GeneralPath();
		pathDroit.moveTo(x + dx, y + dy);
		pathDroit.lineTo(x + dx + sizeOmbre, y + dy + sizeOmbre);
		pathDroit.lineTo(x + dx + sizeOmbre, y - sizeOmbre);
		pathDroit.lineTo(x + dx, y);
		pathDroit.closePath();
		g2.fill(pathDroit);
	}
	
	public static void ombrerContourExterieur(Graphics2D g2, int x, int y, int dx, int dy, Color c, int sizeOmbre) {

		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		// Cote haut
		g2.setPaint(new GradientPaint(x, y, c, x, y - sizeOmbre, new Color(0, 0, 0, 0)));
		GeneralPath pathHaut = new GeneralPath();
		pathHaut.moveTo(x , y );
		pathHaut.lineTo(x + dx , y );
		pathHaut.lineTo(x + dx , y - sizeOmbre);
		pathHaut.lineTo(x , y - sizeOmbre);
		pathHaut.closePath();
		g2.fill(pathHaut);
		

		//Corner haut gauche
		g2.setPaint(new GradientPaint(x, y, c, x - sizeOmbre/2, y - sizeOmbre/2, new Color(0, 0, 0, 0)));
		GeneralPath pathCornerHautGauche = new GeneralPath();
		pathCornerHautGauche.moveTo(x , y );
		pathCornerHautGauche.lineTo(x , y - sizeOmbre );
		pathCornerHautGauche.lineTo(x - sizeOmbre, y - sizeOmbre);
		pathCornerHautGauche.lineTo(x - sizeOmbre, y );
		pathCornerHautGauche.closePath();
		g2.fill(pathCornerHautGauche);

		// Cote gauche
		g2.setPaint(new GradientPaint(x, y, c, x - sizeOmbre, y, new Color(0, 0, 0, 0)));
		GeneralPath pathGauche = new GeneralPath();
		pathGauche.moveTo(x, y);
		pathGauche.lineTo(x - sizeOmbre, y );
		pathGauche.lineTo(x - sizeOmbre, y + dy);
		pathGauche.lineTo(x, y + dy);
		pathGauche.closePath();
		g2.fill(pathGauche);

		// Cote bas
		g2.setPaint(new GradientPaint(x, y + dy, c, x, y + dy + sizeOmbre, new Color(0, 0, 0, 0)));
		GeneralPath pathBas = new GeneralPath();
		pathBas.moveTo(x , y + dy);
		pathBas.lineTo(x, y + dy + sizeOmbre);
		pathBas.lineTo(x + dx, y + dy + sizeOmbre);
		pathBas.lineTo(x + dx , y + dy);
		pathBas.closePath();
		g2.fill(pathBas);

		// Cote droit
		g2.setPaint(new GradientPaint(dx + x, y, c, x + dx + sizeOmbre, y, new Color(0, 0, 0, 0)));
		GeneralPath pathDroit = new GeneralPath();
		pathDroit.moveTo(x + dx, y + dy);
		pathDroit.lineTo(x + dx + sizeOmbre, y + dy );
		pathDroit.lineTo(x + dx + sizeOmbre, y );
		pathDroit.lineTo(x + dx, y);
		pathDroit.closePath();
		g2.fill(pathDroit);
	}

	public static void ombrageHorizontal(Graphics2D g2, int x, int y, int dx, int dy, Color c, int sizeOmbre, boolean top) {

		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		if (top) {
			
			g2.setPaint(new GradientPaint(x, y, c, x, y + sizeOmbre, new Color(0, 0, 0, 0)));
			GeneralPath pathHaut = new GeneralPath();
			pathHaut.moveTo(x, y);
			pathHaut.lineTo(x + dx, y );
			pathHaut.lineTo(x + dx, y + sizeOmbre);
			pathHaut.lineTo(x, y + sizeOmbre);
			pathHaut.closePath();
			g2.fill(pathHaut);

		} else {
			g2.setPaint(new GradientPaint(x, y + dy, c, x, y + dy - sizeOmbre, new Color(0, 0, 0, 0)));
			GeneralPath pathBas = new GeneralPath();
			pathBas.moveTo(x, y + dy);
			pathBas.lineTo(x , y + dy - sizeOmbre);
			pathBas.lineTo(x + dx, y + dy - sizeOmbre);
			pathBas.lineTo(x + dx , y + dy);
			pathBas.closePath();
			g2.fill(pathBas);
		}
	}
	
	public static void ombrageVertical(Graphics2D g2, int x, int y, int dx, int dy, Color c, int sizeOmbre, boolean right) {

		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		if (right) {
			
			// Cote droit
			g2.setPaint(new GradientPaint(dx + x, y, c, x + dx - sizeOmbre, y, new Color(0, 0, 0, 0)));
			GeneralPath pathDroit = new GeneralPath();
			pathDroit.moveTo(x + dx, y + dy);
			pathDroit.lineTo(x + dx - sizeOmbre, y + dy);
			pathDroit.lineTo(x + dx - sizeOmbre, y);
			pathDroit.lineTo(x + dx, y);
			pathDroit.closePath();
			g2.fill(pathDroit);

		} else {
			
			// Cote gauche
			g2.setPaint(new GradientPaint(x, y, c, x + sizeOmbre, y, new Color(0, 0, 0, 0)));
			GeneralPath pathGauche = new GeneralPath();
			pathGauche.moveTo(x, y);
			pathGauche.lineTo(x + sizeOmbre, y );
			pathGauche.lineTo(x + sizeOmbre, y + dy);
			pathGauche.lineTo(x, y + dy);
			pathGauche.closePath();
			g2.fill(pathGauche);
		}
	}

	public static void paintBlurredText(Graphics2D g, String text, int x, int y, Color couleurExt, Color couleurInt, float strokeDive,
			int kernelSize, Font f) {

		atcFont = f;

		g.setFont(atcFont);
		int height = g.getFontMetrics().getHeight();
		int width = g.getFontMetrics().stringWidth(text);
		BufferedImage ATCNameImage = new BufferedImage(2 * atcFont.getSize() + width, 2 * atcFont.getSize() + height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D atcNameGraphics = (Graphics2D) ATCNameImage.getGraphics().create();
		atcNameGraphics.setColor(new Color(0, 0, 0, 0));
		atcNameGraphics.setComposite(AlphaComposite.Src);
		atcNameGraphics.fillRect(0, 0, width, height);
		atcNameGraphics.setComposite(AlphaComposite.SrcOver);
		atcNameGraphics.setFont(atcFont);
		atcNameGraphics.setColor(couleurExt);
		atcNameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GlyphVector gv = atcFont.createGlyphVector(atcNameGraphics.getFontRenderContext(), text);
		atcNameGraphics.translate(atcFont.getSize(), 2 * atcFont.getSize());
		for (int i = 0; i < gv.getNumGlyphs(); i++) {
			Shape glyph = gv.getGlyphOutline(i);
			atcNameGraphics.setStroke(new BasicStroke(atcFont.getSize() / strokeDive));
			atcNameGraphics.draw(glyph);
		}

		float[] kernel = new float[kernelSize];
		for (int i = 0; i < kernel.length; i++)
			kernel[i] = 1.0f / kernel.length;
		ConvolveOp cOp = new ConvolveOp(new Kernel((int) Math.sqrt(kernelSize), (int) Math.sqrt(kernelSize), kernel));
		BufferedImage blurred = cOp.filter(ATCNameImage, null);
		atcNameGraphics.dispose();

		g.drawImage(blurred, x - atcFont.getSize(), y - 2 * atcFont.getSize(), null);
		g.setFont(atcFont);
		g.setColor(couleurInt);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(text, x, y);

	}

	public static void paintBlurredText(Graphics2D g, String text, int x, int y, int textSize, Color couleurExt, Color couleurInt, float strokeDive,
			int kernelSize, String fontName) {

		atcFont = new Font(fontName, Font.CENTER_BASELINE, textSize);
		g.setFont(atcFont);
		int height = g.getFontMetrics().getHeight();
		int width = g.getFontMetrics().stringWidth(text);
		BufferedImage ATCNameImage = new BufferedImage(2 * atcFont.getSize() + width, 2 * atcFont.getSize() + height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D atcNameGraphics = (Graphics2D) ATCNameImage.getGraphics().create();
		atcNameGraphics.setColor(new Color(0, 0, 0, 0));
		atcNameGraphics.setComposite(AlphaComposite.Src);
		atcNameGraphics.fillRect(0, 0, width, height);
		atcNameGraphics.setComposite(AlphaComposite.SrcOver);
		atcNameGraphics.setFont(atcFont);
		atcNameGraphics.setColor(couleurExt);
		atcNameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GlyphVector gv = atcFont.createGlyphVector(atcNameGraphics.getFontRenderContext(), text);
		atcNameGraphics.translate(atcFont.getSize(), 2 * atcFont.getSize());
		for (int i = 0; i < gv.getNumGlyphs(); i++) {
			Shape glyph = gv.getGlyphOutline(i);
			atcNameGraphics.setStroke(new BasicStroke(atcFont.getSize() / strokeDive));
			atcNameGraphics.draw(glyph);
		}

		float[] kernel = new float[kernelSize];
		for (int i = 0; i < kernel.length; i++)
			kernel[i] = 1.0f / kernel.length;
		ConvolveOp cOp = new ConvolveOp(new Kernel((int) Math.sqrt(kernelSize), (int) Math.sqrt(kernelSize), kernel));
		BufferedImage blurred = cOp.filter(ATCNameImage, null);
		atcNameGraphics.dispose();

		g.drawImage(blurred, x - atcFont.getSize(), y - 2 * atcFont.getSize(), null);
		g.setFont(atcFont);
		g.setColor(couleurInt);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawString(text, x, y);

	}

	public static void paintBlurredLine(Graphics2D g, int x1, int y1, int x2, int y2, Color couleurExt, Color couleurInt, float stroke, int kernelSize) {

		float widthStr;
		try {
			widthStr = ((BasicStroke) g.getStroke()).getLineWidth();
		} catch (Exception e) {
			widthStr = 10;
		}
		int width = Math.max((int) Math.max(stroke, widthStr), Math.abs(x2 - x1));
		int height = Math.max((int) Math.max(stroke, widthStr), Math.abs(y2 - y1));

		BufferedImage ATCNameImage = new BufferedImage(2 * width, 2 * height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D atcNameGraphics = (Graphics2D) ATCNameImage.getGraphics().create();
		atcNameGraphics.setColor(new Color(0, 0, 0, 0));
		atcNameGraphics.setComposite(AlphaComposite.Src);
		atcNameGraphics.fillRect(0, 0, width, height);
		atcNameGraphics.setComposite(AlphaComposite.SrcOver);
		atcNameGraphics.setFont(atcFont);
		atcNameGraphics.setColor(couleurExt);
		atcNameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		atcNameGraphics.setStroke(new BasicStroke(stroke));
		atcNameGraphics.drawLine(x1, y1, x2, y2);

		float[] kernel = new float[kernelSize];
		for (int i = 0; i < kernel.length; i++)
			kernel[i] = 1.0f / kernel.length;
		ConvolveOp cOp = new ConvolveOp(new Kernel((int) Math.sqrt(kernelSize), (int) Math.sqrt(kernelSize), kernel));
		BufferedImage blurred = cOp.filter(ATCNameImage, null);
		atcNameGraphics.dispose();

		g.drawImage(blurred, 0, 0, null);
		g.setColor(couleurInt);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawLine(x1, y1, x2, y2);

	}

	public static void paintBlurredText(Graphics2D g, String text, int x, int y, int textSize, Color couleurExt, Color couleurInt) {

		paintBlurredText(g, text, x, y, textSize, couleurExt, couleurInt, 5.5f, 25, "Times");

	}

	public static void paintBlurredText(Graphics2D g, String text, int x, int y, int maxWidth, int textSize) {

		atcFont = new Font("Times", Font.CENTER_BASELINE, textSize);
		FontMetrics metrics = g.getFontMetrics(atcFont);

		int posYtmp = y;

		// On Peint dans un premier temps le numero de l'atc
		String reste = "";
		String[] splitted1 = text.split("-");

		if (splitted1.length == 0) {
			reste = text;
		} else {
			paintBlurredText(g, splitted1[0], x, posYtmp, atcFont.getSize(), Color.black, Color.white);
		}

		for (int i = 0; i < splitted1.length; i++) {
			if (i > 0) {
				reste += splitted1[i];
				if (i != (splitted1.length - 1))
					reste += "-";
			}
		}

		posYtmp += 25;

		// Diminution de la taille de police
		if (splitted1.length > 0) {
			atcFont = new Font("Times", Font.CENTER_BASELINE, (int) (textSize / 1.7));
			metrics = g.getFontMetrics(atcFont);
		}

		int espaceDroiteLibre = -100;
		// Si le texte depasse du bord
		if (metrics.stringWidth(reste) > (maxWidth - espaceDroiteLibre)) {
			String[] splitted = reste.split("_");
			String tmp = "";
			for (int i = 0; i < splitted.length; i++) {

				int adv = metrics.stringWidth(tmp + splitted[i] + "_");
				if (adv > (maxWidth - espaceDroiteLibre)) {
					paintBlurredText(g, tmp, x, posYtmp, atcFont.getSize(), Color.black, Color.white);
					// Diminution de taille de texte
					atcFont = new Font("Times", Font.CENTER_BASELINE, (int) (textSize / 1.7));
					metrics = g.getFontMetrics(atcFont);
					posYtmp += 20;
					tmp = "";
				}

				tmp += splitted[i];
				if (i != (splitted.length - 1))
					tmp += "_";
			}
			paintBlurredText(g, tmp, x, posYtmp, atcFont.getSize(), Color.black, Color.white);

		} else {
			paintBlurredText(g, reste, x, posYtmp, atcFont.getSize(), Color.black, Color.white);
		}
	}

	public static Point getTailleText(Graphics2D g, Font font, String text) {
		FontMetrics metrics = g.getFontMetrics(font);
		// Si le texte depasse du bord
		return new Point(metrics.stringWidth(text), font.getSize());
	}
	
	public static int getTailleTextWidth(Graphics2D g, Font font, String text) {
		FontMetrics metrics = g.getFontMetrics(font);
		// Si le texte depasse du bord
		return new Point(metrics.stringWidth(text), font.getSize()).x;
	}
}
