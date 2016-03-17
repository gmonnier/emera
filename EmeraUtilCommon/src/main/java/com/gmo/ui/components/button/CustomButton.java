package com.gmo.ui.components.button;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;

public class CustomButton extends JComponent implements MouseListener {

	private JButton button;

	private BufferedImage associatedImage;

	private float lightningValue = 1.5f;

	public enum ImageLocationEnum {
		RIGHT, LEFT, CENTER, EXTERNAL_LEFT, EXTERNAL_RIGHT
		/*
		 * EXTERNAL_RIGHT and EXTERNAL_LEFT : image is located on the right/left
		 * of the button. Half of it is display on the button, and the other
		 * half outside.
		 */
	}

	private ImageLocationEnum imageLocation;

	private boolean highlighted;

	public CustomButton(String imagePath, String buttonLabel) {
		setLayout(null);
		imageLocation = ImageLocationEnum.EXTERNAL_RIGHT;
		try {
			associatedImage = ImageIO.read(this.getClass().getResource(imagePath));
		} catch (IOException e) {
			System.out.println("Impossible de charger l'image" + imagePath);
		}

		button = new JButton(buttonLabel);
		add(button);
		highlighted = false;

		addMouseListener(this);
		button.addMouseListener(this);
	}

	public void addActionListener(ActionListener listener) {
		button.addActionListener(listener);
	}
	
	public void setActionCommand(String actionCommand) {
		button.setActionCommand(actionCommand);
	}

	public String getActionCommand() {
		return button.getActionCommand();
	}
	
	@Override
	public void paint(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		super.paint(g);
		
		int locX = 0;
		int locY = 0;
		
		switch (imageLocation) {
		case CENTER: {
			locX = getWidth() / 2 - associatedImage.getWidth() / 2;
			locY = getHeight() / 2 - associatedImage.getHeight() / 2;
			break;
		}
		case EXTERNAL_LEFT:
		case LEFT: {
			locX = 0;
			locY = getHeight() / 2 - associatedImage.getHeight() / 2;
			break;
		}
		case EXTERNAL_RIGHT:
		case RIGHT: {
			locX = getWidth() - associatedImage.getWidth();
			locY = getHeight() / 2 - associatedImage.getHeight() / 2;
			break;
		}
		}

		if (highlighted) {

			int w = associatedImage.getWidth(null);
			int h = associatedImage.getHeight(null);
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics gim = bi.getGraphics();

			gim.drawImage(associatedImage, 0, 0, null);

			float[] scales = { lightningValue, lightningValue, lightningValue, 1.0f };
			float[] offsets = new float[4];
			RescaleOp rop = new RescaleOp(scales, offsets, null);

			g2.drawImage(bi, rop, locX, locY);

		} else {
			g2.drawImage(associatedImage, locX, locY, null);
		}

	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		
		button.setFont(new Font("Helvetica",Font.PLAIN,Math.min(width,height)/2));
		button.setForeground(Color.DARK_GRAY);
		switch (imageLocation) {
		case CENTER:
		case LEFT:
		case RIGHT: {
			button.setBounds(0, 0, width, height);
			break;
		}
		case EXTERNAL_LEFT: {
			button.setBounds(associatedImage.getWidth() / 2, 0, width - associatedImage.getWidth() / 2, height);
			break;
		}
		case EXTERNAL_RIGHT: {
			button.setBounds(0, 0, width - associatedImage.getWidth() / 2, height);
			break;
		}
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		highlighted = true;
		DefaultButtonModel model = (DefaultButtonModel) button.getModel();
		model.setRollover(true);
		if (model.isPressed()) {
			model.setArmed(true);
		}
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		highlighted = false;
		DefaultButtonModel model = (DefaultButtonModel) button.getModel();
		model.setRollover(false);
		model.setArmed(false);
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		DefaultButtonModel model = (DefaultButtonModel) button.getModel();
		model.setPressed(true);
		model.setArmed(true);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		DefaultButtonModel model = (DefaultButtonModel) button.getModel();
		model.setPressed(false);
	}

	public void setLightningValue(float lightningValue) {
		this.lightningValue = lightningValue;
	}
}
