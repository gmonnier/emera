package ui.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ui.utility.BufferedImageHelper;

public abstract class ShadowedComponent extends JComponent {

	private BufferedImage shadow;
	protected int shadowSize;
	private Thread imageThread;

	public ShadowedComponent() {
		initShadowSize();
	}

	protected void computeShadowImage() {

		shadow = null;
		
		if(imageThread != null && imageThread.isAlive()) {
			imageThread.stop();
		}
		
		imageThread = new Thread(){
			
			public void run() {
				
				int imageWidth = getWidth() + shadowSize * 2;
				int imageHeight = getHeight() + shadowSize * 2;
				BufferedImage shadowModel = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D rg = shadowModel.createGraphics();
				rg.translate(shadowSize, shadowSize);
				rg.setColor(Color.black);
				rg.fillRect(0, 0, getWidth(), getHeight());
				rg.dispose();

				shadow = BufferedImageHelper.createDropShadow(shadowModel, shadowSize, 1.0f, Color.black);
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						shadowComputationDone();
					}
				});
			}
			
		};
		
		imageThread.start();

	}
	
	protected void shadowComputationDone() {
		getParent().repaint();
	}

	protected abstract void initShadowSize();

	public Image getShadowImage() {
		return shadow;
	}

	public int getShadowSize() {
		return shadowSize;
	}

	public void drawShadow(Graphics2D g2) {
		if (shadow != null) {
			g2.drawImage(shadow, getX() - shadowSize, getY() - shadowSize, null);
		}
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		computeShadowImage();
	}

}
