package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import ui.lookAndFeel.Painting;
import ui.utility.GraphicsUtility;

public class PanelMiddle extends JPanel {

	private DisplayInfo currentInfos;

	public PanelMiddle() {
		currentInfos = new DisplayInfo();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		Painting.dialogGrayGradientVerticalBGPaint(g2, getWidth(), getHeight());
		
		GraphicsUtility.ombrageHorizontal(g2, 0, 0, getWidth(), 5, Color.lightGray, 15, true);
		
		g2.setColor(Color.BLACK);
		int starty = 50;
		for (Info info : currentInfos.getListinfos()) {
			g.drawString(info.toString(), 10, starty);
			starty += 20;
		}

	}

	public void setCurrentInfos(DisplayInfo currentInfos) {
		this.currentInfos = currentInfos;
		repaint();
	}

	public DisplayInfo getCurrentInfos() {
		return currentInfos;
	}
	
	
}
