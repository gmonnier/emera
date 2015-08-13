package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import ui.lookAndFeel.Painting;
import ui.utility.GraphicsUtility;
import client.ClientStatus;

public class PanelTop extends JPanel {

	private ClientStatus status = null;

	private Info serverInfo;

	public PanelTop() {
		setPreferredSize(new Dimension(0, 50));

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Painting.paintTopToolBar(g2, getWidth(), getHeight());

		g2.setColor(Color.WHITE);
		if (status != null) {
			// g.drawString(status.toString(), 10, getHeight() / 2);
			GraphicsUtility.paintBlurredText(g2, status.toString(), 10, 25, 18, Color.BLACK, Color.WHITE, 15, 25, "Monospaced");
		} else {
			g.drawString("Unknown status", 10, getHeight() / 2);
		}

		if (serverInfo == null) {
			g.drawString("Disconnected", getWidth() - 150, getHeight() / 2);
		} else {
			Point t = GraphicsUtility.getTailleText(g2, g2.getFont(), serverInfo.toString());
			g2.drawString(serverInfo.toString(), getWidth() - t.x - 15, getHeight() / 2);
		}

		g2.setStroke(new BasicStroke(3f));
		g2.setColor(new Color(136, 209, 0));
		g2.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
	}

	public void setStatus(ClientStatus status) {
		this.status = status;
		repaint();
	}

	public void setServerInfo(Info serverInfo) {
		this.serverInfo = serverInfo;
		repaint();
	}
}
