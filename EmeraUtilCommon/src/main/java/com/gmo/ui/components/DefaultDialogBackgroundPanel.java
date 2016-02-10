package com.gmo.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.gmo.ui.icones.Icone;
import com.gmo.ui.lookAndFeel.Colors;
import com.gmo.ui.lookAndFeel.Painting;
import com.gmo.ui.lookAndFeel.looksEnums.ComponentStateColors;
import com.gmo.ui.utility.BufferedImageHelper;
import com.gmo.ui.utility.GraphicsUtility;

public class DefaultDialogBackgroundPanel extends JComponent implements WindowListener {

	protected Icone associatedIcone = null;

	private String messageLevel1;

	private String messageLevel2;

	private JLabel description;

	// Shadow related objects
	private int level1SizeY = 40;
	private final static int shadowSize = 5;
	private BufferedImage shadow;
	private GeneralPath toolBarPath;
	private int startLevel1X = 60;
	private Thread imageThread;

	// Kind of background
	private ComponentStateColors componentStateColor;
	private Color backGroundColor1;
	private Color backGroundColor2;

	public DefaultDialogBackgroundPanel(Window inputWin, String messageLevel1, String messageLevel2, boolean animated) {

		associatedIcone = new Icone("/WarningMessages/warning.png");
		startLevel1X = associatedIcone.getWidth();

		setLayout(null);

		this.messageLevel1 = messageLevel1;
		this.messageLevel2 = messageLevel2;

		// init back colors
		setComponentStateColor(ComponentStateColors.NORMAL);

		toolBarPath = new GeneralPath();

		description = new JLabel();
		description.setForeground(Color.white);
		description.setFocusable(false);
		description.setFont(new Font("Times", Font.PLAIN, 13));
		add(description);

		String labelText = String.format("<html>%s<html>", messageLevel2);
		description.setText(labelText);

		add(associatedIcone);
		if (animated) {
			associatedIcone.startAnimation(5, 255);
		} else {
			associatedIcone.setInteractive(false);
		}

		inputWin.addWindowListener(this);
	}

	public DefaultDialogBackgroundPanel(Window parentWin, String msgsummary, String msgdetail, String imgPath, boolean animated) {

		if (!imgPath.isEmpty()) {
			associatedIcone = new Icone(imgPath);
			startLevel1X = associatedIcone.getWidth();
		}

		setLayout(null);

		this.messageLevel1 = msgsummary;
		this.messageLevel2 = msgdetail;

		// init back colors
		setComponentStateColor(ComponentStateColors.NORMAL);

		toolBarPath = new GeneralPath();

		description = new JLabel();
		description.setForeground(Color.white);
		description.setFocusable(false);
		description.setFont(new Font("Times", Font.PLAIN, 13));
		add(description);

		String labelText = String.format("<html>%s<html>", messageLevel2);
		description.setText(labelText);

		if (associatedIcone != null) {
			add(associatedIcone);
			if (animated) {
				associatedIcone.startAnimation(5, 255);
			} else {
				associatedIcone.setInteractive(false);
			}
		}

		if (animated && parentWin != null) {
			parentWin.addWindowListener(this);
		}
	}

	public void setComponentStateColor(ComponentStateColors componentStateColor) {
		this.componentStateColor = componentStateColor;

		backGroundColor1 = Colors.getPresetComponentGradient(getHeight(), componentStateColor).getColor1();
		backGroundColor2 = Colors.getPresetComponentGradient(getHeight(), componentStateColor).getColor2();

		repaint();
	}

	public void setMessageLevel1(String messageLevel1) {
		this.messageLevel1 = messageLevel1;
		repaint();
	}

	public void setMessageLevel2(String messageLevel2) {
		this.messageLevel2 = messageLevel2;

		String labelText = String.format("<html>%s<html>", messageLevel2);
		description.setText(labelText);
		repaint();
	}

	public void setStartLevel1X(int startLevel1X) {
		this.startLevel1X = startLevel1X;
	}

	public void setLevel1SizeY(int level1SizeY) {
		this.level1SizeY = level1SizeY;
	}

	@Override
	public void setBounds(int arg0, int arg1, int arg2, int arg3) {

		super.setBounds(arg0, arg1, arg2, arg3);

		if (associatedIcone != null) {
			associatedIcone.setLocation(15, getHeight() / 2 - associatedIcone.getHeight() / 2);
		}

		double angle = Math.PI / 6;
		toolBarPath.moveTo(startLevel1X, 0);
		toolBarPath.lineTo(getWidth(), 0);
		toolBarPath.lineTo(getWidth(), level1SizeY);
		toolBarPath.lineTo(startLevel1X + Math.tan(angle) * level1SizeY, level1SizeY);
		toolBarPath.closePath();

		description.setBounds(startLevel1X + 40, level1SizeY - 15, getWidth() - 150, getHeight() - level1SizeY - 10);

		computeShadowImage();
		
		repaint();
	}

	@Override
	protected void paintComponent(Graphics arg0) {

		super.paintComponent(arg0);

		Graphics2D g2 = (Graphics2D) arg0;
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Stroke stroke = new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f);
		g2.setStroke(stroke);

		// Paint background
		g2.setPaint(new GradientPaint(0, 0, backGroundColor1, 0, 3 * getHeight() / 2, backGroundColor2, true));
		g2.fillRect(0, 0, getWidth(), getHeight());

		// Paint main description bar
		drawShadow(g2);
		Painting.paintTopToolBarSpecificPath(g2, 200, 0, getWidth(), level1SizeY, toolBarPath, componentStateColor);

		// Paint description
		GraphicsUtility.paintBlurredText(g2, messageLevel1, startLevel1X + 40, (level1SizeY / 2) + 6, Color.black, new Color(240, 240, 255), 25, 25, new Font("Times", Font.PLAIN, 12).deriveFont(Font.BOLD));

	}

	private void computeShadowImage() {

		shadow = null;

		if (imageThread != null && imageThread.isAlive()) {
			imageThread.stop();
		}

		imageThread = new Thread() {

			public void run() {

				int imageWidth = getWidth() + shadowSize * 2;
				int imageHeight = getHeight() + shadowSize * 2;
				BufferedImage shadowModel = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D rg = shadowModel.createGraphics();
				rg.translate(shadowSize, shadowSize);
				rg.setStroke(new BasicStroke(shadowSize));
				rg.draw(toolBarPath);
				rg.dispose();

				shadow = BufferedImageHelper.createDropShadow(shadowModel, shadowSize, 1.0f, new Color(50, 50, 50));

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaint();
					}
				});
			}

		};

		imageThread.start();

	}

	public void drawShadow(Graphics2D g2) {
		if (shadow != null) {
			g2.drawImage(shadow, getLocation().x - shadowSize, getLocation().y - shadowSize, null);
		}
	}

	private void release() {
		// stop the animation thread
		if (associatedIcone != null) {
			associatedIcone.stopAnimation();
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		release();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		release();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
