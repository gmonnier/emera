package com.gmo.ui.utility;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BufferedImageHelper {

	public static BufferedImage createDropShadow(BufferedImage image, int shadowSize, float shadowOpacity, Color shadowColor) {

		// BufferedImage subject = prepareImage(image, shadowSize);
		BufferedImage shadow = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage shadowMask = createShadowMask(image, shadowOpacity, shadowColor);
		getLinearBlurOp(shadowSize).filter(shadowMask, shadow);
		return shadow;

	}

	private static BufferedImage prepareImage(BufferedImage image, int shadowSize) {
		BufferedImage subject = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = subject.createGraphics();
		g2.drawImage(image, null, shadowSize, shadowSize);
		g2.dispose();

		return subject;
	}

	private static BufferedImage createShadowMask(BufferedImage image, float shadowOpacity, Color shadowColor) {
		BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = mask.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		// Ar = As*Ad - Cr = Cs*Ad -> extract 'Ad'
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, shadowOpacity));
		g2d.setColor(shadowColor);
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2d.dispose();
		return mask;
	}

	private static ConvolveOp getLinearBlurOp(int size) {
		float[] data = new float[size * size];
		float value = 1.0f / (float) (size * size);
		for (int i = 0; i < data.length; i++) {
			data[i] = value;
		}
		return new ConvolveOp(new Kernel(size, size, data));
	}

	public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {

		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
		g.dispose();

		return resizedImage;
	}

	/**
	 * Resizes an image with better quality than the previous resizeImage
	 * method.
	 * 
	 * @param originalImage
	 *            original buffered image to be resized
	 * @param newWidth
	 *            new width of the image
	 * @param newHeight
	 *            new height of the image
	 * @return
	 */
	public static BufferedImage resizeImageWithHint(BufferedImage originalImage, int newWidth, int newHeight) {

		int type = BufferedImage.TYPE_INT_ARGB;

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}
}
