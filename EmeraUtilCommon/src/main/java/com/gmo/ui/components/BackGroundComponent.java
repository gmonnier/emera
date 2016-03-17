package com.gmo.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.gmo.ui.lookAndFeel.Painting;
import com.gmo.ui.lookAndFeel.looksEnums.BackGroundLook;

public class BackGroundComponent extends JPanel {

	BackGroundLook look;

	public BackGroundComponent(BackGroundLook look) {
		this.look = look;
		setOpaque(true);
	}

	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		switch (look) {
		case HORIZONTAL_GRADIENT_GRAY: {
			Painting.dialogGrayGradientHorizontalBGPaint(g2, getWidth(), getHeight());
			break;
		}
		case VERTICAL_GRADIENT_GRAY_ROUND: {
			Painting.dialogGrayGradientVerticalBGPaint(g2, getWidth(), getHeight());
		}
		}

	}
}
