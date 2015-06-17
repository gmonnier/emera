//*******************************************************************
//
//      Oce Engineering Creteil: PRODUCT DOCUMENT        CONFIDENTIAL
//
//       R&D-filename    :   Icone.java
//
//       Title           :
//       Abstract        :
//
//
//
//
//       Keywords        :
//
//       %version:       4 %
//       %created_by:    qgmo %
//       %date_created:  Mon Dec 06 16:12:55 2010 %
//       %full_filespec: Icone.java~4:java:CRCOBALT#1 %
//
//   Copyright 2004 Oce-Technologies B.V., Venlo, The Netherlands
//
//*******************************************************************
package ui.icones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ui.ISinusoidEffect;
import ui.ThreadSinusoid;
import ui.utility.BufferedImageHelper;

public class Icone extends JComponent implements MouseListener, ISinusoidEffect {

	private final Collection<IconListener> iconListeners = new ArrayList<IconListener>();

	private boolean highlighted;
	private boolean isInteractive;
	private boolean isAnimated;
	private float lightningValue;
	private BufferedImage img = null;
	private BufferedImage img_grey = null;
	private ThreadSinusoid threadAnim;

	// Define if the image has to be displayed in grey
	private boolean isEnabled;

	// Used for clicked action
	private boolean isMousePressed;

	// Draw a select shadow if mouse is pressed and mouse is inside the
	// component
	private boolean drawSelectShadow;

	// Associated command name
	private String actionCommand = "";

	// Associated image path
	private String associatedPathImage = "Unknown";

	public Icone(String imageName) {
		this.setSize(25, 25);
		this.setOpaque(false);
		setVisible(true);
		isMousePressed = false;
		drawSelectShadow = false;
		highlighted = false;
		lightningValue = 1.5f;

		isInteractive = true;
		isAnimated = false;
		isEnabled = true;

		associatedPathImage = imageName;

		try {
			img = ImageIO.read(this.getClass().getResource(imageName));
		} catch (IOException e) {
			System.out.println("Impossible de charger l'icone" + imageName);
		}

		this.addMouseListener(this);
	}

	public Icone(String imageName, int dx, int dy) {
		this.setSize(dx, dy);
		lightningValue = 1.5f;

		isInteractive = true;
		isAnimated = false;

		isMousePressed = false;
		drawSelectShadow = false;
		highlighted = false;
		isEnabled = true;

		associatedPathImage = imageName;

		setOpaque(false);
		setBackground(Color.WHITE);

		try {
			img = ImageIO.read(this.getClass().getResource(imageName));
			img = BufferedImageHelper.resizeImageWithHint(img, dx, dy);
		} catch (IOException e) {
			System.out.println("Impossible de charger l'icone " + imageName);
		} catch (Exception e) {
			System.out.println("Impossible de charger l'image associee " + imageName);
		}

		this.addMouseListener(this);
	}

	public Icone(String imageName, int dx, int dy, boolean addmouselistener) {
		this.setSize(dx, dy);
		lightningValue = 1.5f;

		isInteractive = true;
		isAnimated = false;

		isMousePressed = false;
		drawSelectShadow = false;
		highlighted = false;
		isEnabled = true;

		associatedPathImage = imageName;

		setOpaque(false);
		setBackground(Color.WHITE);

		try {
			img = ImageIO.read(this.getClass().getResource(imageName));
			img = BufferedImageHelper.resizeImageWithHint(img, dx, dy);
		} catch (IOException e) {
			System.out.println("Impossible de charger l'icone " + imageName);
		} catch (Exception e) {
			System.out.println("Impossible de charger l'image associee " + imageName);
		}

		if (addmouselistener) {
			this.addMouseListener(this);
		}
	}

	public void setImage(String imageName) {
		try {
			img = ImageIO.read(this.getClass().getResource(imageName));
			associatedPathImage = imageName;
		} catch (IOException e) {
			System.out.println("Impossible de charger l'icone " + imageName);
		}
	}

	/**
	 * 
	 * @param interactive
	 *            vrai si l'icone est associe e un evenement souris, faux s'il
	 *            s'agit uniquement s'une image.
	 */
	public void setInteractive(boolean interactive) {
		isInteractive = interactive;
		repaint();
	}

	/**
	 * 
	 * @param interactive
	 *            vrai si l'icone est anime (eclaircissement), faux sinon
	 */
	public void setAnimated(boolean animated) {
		isAnimated = animated;
		repaint();
	}

	public boolean isAnimated() {
		return isAnimated;
	}

	public void setLightningValue(float value) {
		lightningValue = value;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// Icone disabled (gray painted)
		if (!isEnabled) {
			g2.drawImage(img_grey, 0, 0, null);
		} else {
			// Icone classique
			if (!isAnimated && isInteractive) {

				if (drawSelectShadow) {
					g2.drawImage(BufferedImageHelper.createDropShadow(img, 5, 1.0f, new Color(180, 255, 110)), 0, 0, null);
				}

				if (highlighted) {

					int w = img.getWidth(null);
					int h = img.getHeight(null);
					BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					Graphics gim = bi.getGraphics();

					gim.drawImage(img, 0, 0, null);

					float[] scales = { lightningValue, lightningValue, lightningValue, 1.0f };
					float[] offsets = new float[4];
					RescaleOp rop = new RescaleOp(scales, offsets, null);

					g2.drawImage(bi, rop, 0, 0);

				} else {
					g2.drawImage(img, 0, 0, null);
				}

				// Image animee
			} else if (isAnimated) {

				BufferedImage toPaint;
				toPaint = img;

				// On anime l'icone
				int w = toPaint.getWidth(null);
				int h = toPaint.getHeight(null);
				BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics gim = bi.getGraphics();
				gim.drawImage(toPaint, 0, 0, null);

				float[] scales = { lightningValue, lightningValue, lightningValue, 1.0f };
				float[] offsets = new float[4];
				RescaleOp rop = new RescaleOp(scales, offsets, null);

				g2.drawImage(bi, rop, 0, 0);

				// Image simple
			} else if (!isAnimated && !isInteractive) {
				g2.drawImage(img, 0, 0, null);
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (isEnabled) {
			highlighted = true;
			if (isMousePressed) {
				drawSelectShadow = true;
			}
			getParent().repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (isEnabled) {
			highlighted = false;
			drawSelectShadow = false;
			getParent().repaint();
		}
	}

	public void setHighlighted(boolean selected) {
		highlighted = selected;
		repaint();
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		if (this.isEnabled != isEnabled) {
			// Compute the grayscaled image
			ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			img_grey = op.filter(img, null);

			this.isEnabled = isEnabled;
			repaint();
		}
	}

	public void setEnabledWithLightingProperties(boolean isEnabled, float lightingValue) {
		if (this.isEnabled != isEnabled) {

			float[] scales = { lightingValue, lightingValue, lightingValue, 1.0f };
			float[] offsets = new float[4];
			RescaleOp rop = new RescaleOp(scales, offsets, null);
			BufferedImage imgTmp = rop.filter(img, null);

			// Compute the grayscaled image
			ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			img_grey = op.filter(imgTmp, null);

			this.isEnabled = isEnabled;
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (isEnabled) {
			isMousePressed = true;
			drawSelectShadow = true;
			fireIconPressed(arg0);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (isEnabled) {
			// Generate an iconeClicked event if mouse pressed and release
			// inside
			// the component area
			if (isMousePressed && getVisibleRect().contains(arg0.getPoint())) {
				fireIconClicked(arg0);
			}
			isMousePressed = false;
			repaint();
		}
	}

	public void addIconListener(IconListener listener) {
		iconListeners.add(listener);
	}

	public void addIconListener(IconListener listener, String commandName) {
		this.actionCommand = commandName;
		iconListeners.add(listener);
	}

	public void removeIconListener(IconListener listener) {
		iconListeners.remove(listener);
	}

	protected void fireIconPressed(MouseEvent arg0) {
		for (IconListener listener : iconListeners) {
			listener.iconePressedPerformed(arg0);
		}
		repaint();
	}

	protected void fireIconClicked(MouseEvent arg0) {
		for (IconListener listener : iconListeners) {
			listener.iconeClickedPerformed(arg0);
		}
		highlighted = false;
		drawSelectShadow = false;
		repaint();
	}

	@Override
	public void setEllapsedActionTimeValue(int value) {
		lightningValue = 1f + (float) (value / 255.0);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint();
			}
		});

	}

	/**
	 * 
	 * @param vitesse
	 *            vitesse d'avancement de l'animation. comprise entre 0 et 360
	 *            degres.
	 * @param maxLightning
	 *            valeur comprise entre 0 et 255.
	 */
	public void startAnimation(int vitesse, int maxLightning) {
		setAnimated(true);

		if (threadAnim != null && threadAnim.isAlive()) {
			threadAnim.stop();
		}
		threadAnim = new ThreadSinusoid(this, vitesse, maxLightning);

		threadAnim.start();

	}

	public void stopAnimation() {
		setAnimated(false);
		if (threadAnim != null && threadAnim.isAlive()) {
			threadAnim.stop();
		}
	}

	public void setActionCommand(String string) {
		actionCommand = string;
	}

	public String getActionCommand() {
		return actionCommand;
	}

	@Override
	public String getComponentName() {
		return associatedPathImage;
	}

}
