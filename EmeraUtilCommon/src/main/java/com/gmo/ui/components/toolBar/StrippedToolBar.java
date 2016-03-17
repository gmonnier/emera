package com.gmo.ui.components.toolBar;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.gmo.ui.lookAndFeel.Painting;

public class StrippedToolBar extends JPanel {
	
	@Override
	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		Painting.strippedToolBarPaint(g2, getWidth(), getHeight());

		paintComponents(g2);

	}

}
