package ui.icones;

import java.awt.BasicStroke;
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
import ui.lookAndFeel.Colors;

public class StateIcone extends JComponent implements MouseListener, ISinusoidEffect {

	private final Collection<StateIconListener> iconListeners = new ArrayList<StateIconListener>();

	private boolean highlighted;
	private float lightningValue;
	private BufferedImage img = null;
	private BufferedImage img_grey = null;

	// Define if the image has to be displayed in grey
	private boolean isEnabled;

	// Used for clicked action
	private boolean isMousePressed;

	// State of the Icon
	public enum STATE {
		ON, OFF
	};

	private STATE state = STATE.OFF;

	// Associated command name
	String actionCommand = "";

	public StateIcone(String imageName, int dx, int dy) {
		this.setSize(dx, dy);
		lightningValue = 1.5f;

		isMousePressed = false;
		highlighted = false;
		isEnabled = true;

		setOpaque(false);
		setBackground(Color.WHITE);

		try {
			img = ImageIO.read(this.getClass().getResource(imageName));
		} catch (IOException e) {
			System.out.println("Impossible de charger l'icone " + imageName);
		} catch (Exception e) {
			System.out.println("Impossible de charger l'image associee " + imageName);
		}

		this.addMouseListener(this);
	}

	public void setImage(String imageName) {
		try {
			img = ImageIO.read(this.getClass().getResource(imageName));
		} catch (IOException e) {
			System.out.println("Impossible de charger l'icone " + imageName);
		}
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

			if (state == STATE.ON) {
				g2.setStroke(new BasicStroke(2.1f));
				g2.setColor(Colors.getGreenToolbar());
				g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 4, 4);
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
			getParent().repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (isEnabled) {
			highlighted = false;
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
		}
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
		repaint();
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

	public void addStateIconListener(StateIconListener listener) {
		iconListeners.add(listener);
	}

	public void addStateIconListener(StateIconListener listener, String commandName) {
		this.actionCommand = commandName;
		iconListeners.add(listener);
	}

	public void removeIconListener(IconListener listener) {
		iconListeners.remove(listener);
	}

	protected void fireIconClicked(MouseEvent arg0) {
		
		for (StateIconListener listener : iconListeners) {
			listener.iconeClickedPerformed(arg0);
		}
		highlighted = false;
		repaint();
	}

	@Override
	public void setEllapsedActionTimeValue(int value) {
		lightningValue = 1f + (float) (value / 255.0);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				revalidate();
				repaint();
			}
		});

	}

	public void setActionCommand(String string) {
		actionCommand = string;
	}

	public String getActionCommand() {
		return actionCommand;
	}

	@Override
	public String getComponentName() {
		return "Stated icon";
	}

}
