package com.gmo.ui.lookAndFeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

public class FrameBorder extends AbstractBorder {

	private int thickness;

	private static final FrameBorder defaultBorder = new FrameBorder(2);

	public static FrameBorder getDefaultborder() {
		return defaultBorder;
	}

	private FrameBorder(int thick) {
		this.thickness = thick;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i = 0; i < thickness; i++) {
			int colorValue = 150 / (i + 1);
			g2d.setColor(new Color(colorValue, colorValue, colorValue));

			g2d.drawRect(x + i, y + i, width - 2 * i - 1, height - 2 * i - 1);
		}

	}

	public Insets getBorderInsets(Component c) {
		// return the top, bottom, left, right spacing in pixels the border will
		// occupy
		return new Insets(thickness, thickness, thickness, thickness);
	}

	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.top = insets.right = insets.bottom = thickness;
		return insets;
	}
}