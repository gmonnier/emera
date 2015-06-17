package ui.components.titleBar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import ui.icones.IconListener;
import ui.icones.Icone;
import ui.lookAndFeel.Colors;
import ui.lookAndFeel.Fonts;
import ui.lookAndFeel.looksEnums.TitleBarLook;
import ui.utility.BufferedImageHelper;
import ui.utility.GraphicsUtility;

public class CustomTitleBar extends JComponent implements MouseListener, IconListener {

	private static Color cSelected = new Color(214, 217, 223);
	private static Color cUnSelected = new Color(230, 230, 230);

	private GradientPaint gradientPaint = new GradientPaint(0, 0, cSelected, 0, 25, Color.gray.darker(), true);

	private boolean isHighlighted = false;
	private boolean isSelected = false;
	private Color cInter = Color.white;
	private int c_height = 17;

	private String title = "";
	private Color coul = cUnSelected;
	
	// Use to specify source of events
	private String sourceName = "";

	// Title bar action listener
	private ITitleBarListener titleBarListener;

	// ToolBar icon
	private BufferedImage iconImage = null;

	// Frame controls
	private int iconWidth = 20;
	private Icone closeIc = new Icone("/DataViewPanel/close_small.png", iconWidth, iconWidth);
	private Icone minimizeIc = new Icone("/DataViewPanel/minimize_small.png", iconWidth, iconWidth);
	private Icone maximizeIc = new Icone("/DataViewPanel/maximize_small.png", iconWidth, iconWidth);

	// Associated Look
	private TitleBarLook look;

	public CustomTitleBar(String title, ITitleBarListener titleBarListener, boolean minimize, boolean maximize, boolean close) {

		this.title = title;
		this.titleBarListener = titleBarListener;

		setLook(TitleBarLook.SMALL);

		setOpaque(false);
		addMouseListener(this);

		closeIc.addIconListener(this);
		minimizeIc.addIconListener(this);
		maximizeIc.addIconListener(this);

		if (close) {
			add(closeIc);
		}
		if (minimize) {
			add(minimizeIc);
		}
		if (maximize) {
			add(maximizeIc);
		}
		
		setSize(0, c_height);

	}

	public String getSourceName() {
		return sourceName;
	}



	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}



	public void setIconImage(String path) {
		try {
			iconImage = BufferedImageHelper.resizeImageWithHint(ImageIO.read(this.getClass().getResource(path)), iconWidth - 1, iconWidth - 1);
		} catch (IOException e) {
			System.err.println("Impossible to load icon image for title bar with title = " + title);
		}
	}

	public void setLook(TitleBarLook look) {
		switch (look) {
		case NORMAL: {
			c_height = 25;
			iconWidth = 25;

			closeIc.setImage("/DataViewPanel/close_normal.png");
			closeIc.setSize(iconWidth, iconWidth);
			minimizeIc.setImage("/DataViewPanel/minimize_normal.png");
			minimizeIc.setSize(iconWidth, iconWidth);
			maximizeIc.setImage("/DataViewPanel/maximize_normal.png");
			maximizeIc.setSize(iconWidth, iconWidth);

			setPreferredSize(new Dimension(0, c_height));
			break;
		}
		case SMALL: {
			c_height = 17;
			iconWidth = 20;

			closeIc.setImage("/DataViewPanel/close_small.png");
			closeIc.setSize(iconWidth, iconWidth);
			minimizeIc.setImage("/DataViewPanel/minimize_small.png");
			minimizeIc.setSize(iconWidth, iconWidth);
			maximizeIc.setImage("/DataViewPanel/maximize_small.png");
			maximizeIc.setSize(iconWidth, iconWidth);

			setPreferredSize(new Dimension(0, c_height));
			break;
		}
		}
		this.look = look;
	}

	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		cInter = coul.darker();
		coul = Colors.getMainbackgroundcolor();
		cInter = Color.DARK_GRAY.darker();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		Stroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f);
		g2.setStroke(stroke);

		if (look == TitleBarLook.SMALL) {
			g2.setPaint(new GradientPaint(0, 5, new Color(214, 217, 223), 0, c_height, cInter, true));
			g2.fillRect(0, 0, this.getSize().width, this.getSize().height);

			int positionTextX = 10;
			if (iconImage != null) {
				positionTextX += iconWidth;
				g2.drawImage(iconImage, 1, 0, null);
			}

			GraphicsUtility.paintBlurredText(g2, title, positionTextX, 12, 12, Color.BLACK, Color.WHITE, 15, 25, "Monospaced");

		} else if (look == TitleBarLook.NORMAL) {
			g2.setPaint(gradientPaint);
			g2.fillRect(0, 0, this.getSize().width, this.getSize().height);

			int positionTextX = 10;
			if (iconImage != null) {
				positionTextX += iconWidth;
				g2.drawImage(iconImage, 2, 0, null);
			}

			GraphicsUtility.paintBlurredText(g2, title, positionTextX, 17, 14, Color.BLACK, Color.WHITE, 10, 20, Fonts.getDefaultTitleBarFontName());
		}

		g2.setColor(new Color(20, 51, 73));
		g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

		paintComponents(g2);
	}

	public void setSelected(boolean isSelected) {
		if (isSelected) {
			coul = cSelected;
		} else {
			coul = cUnSelected;
		}
		this.isSelected = isSelected;

	}

	public void setTitle(String title) {
		this.title = title;
		repaint();
	}

	private void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
		if (!isSelected) {
			if (!this.isHighlighted) {
				coul = cUnSelected;
			} else {
				coul = Color.black;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Double clicked
		if (arg0.getClickCount() == 2) {
			titleBarListener.maximize(sourceName);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		setHighlighted(true);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		setHighlighted(false);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		titleBarListener.activated(sourceName,true);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBounds(int arg0, int arg1, int arg2, int arg3) {
		super.setBounds(arg0, arg1, arg2, arg3);

		int yPosition = (c_height - 17) / 2 - 1;
		int spacing = iconWidth;
		int offset = 0;
		if (iconWidth == 25) {
			yPosition = (c_height - 25) / 2;
			spacing = iconWidth - 4;
			offset = -5;
		}

		closeIc.setLocation(getWidth() - spacing + offset, yPosition);
		maximizeIc.setLocation(getWidth() - 2 * spacing + offset, yPosition);
		minimizeIc.setLocation(getWidth() - 3 * spacing + offset, yPosition);
	}

	@Override
	public void iconePressedPerformed(MouseEvent arg0) {

	}

	@Override
	public void iconeClickedPerformed(MouseEvent arg0) {
		if (arg0.getSource() == closeIc) {
			titleBarListener.close(sourceName);
		} else if (arg0.getSource() == minimizeIc) {
			titleBarListener.minimize(sourceName);
		} else if (arg0.getSource() == maximizeIc) {
			titleBarListener.maximize(sourceName);
		}
	}

}
