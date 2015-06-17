package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import ui.lookAndFeel.Painting;
import client.ClientStatus;

public class PanelTop extends JPanel {

	private ClientStatus status = null;

	private Info serverInfo;

	public PanelTop() {
		setPreferredSize(new Dimension(0, 30));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		Painting.paintTopToolBar(g2, getWidth(), getHeight());
		
		g2.setColor(Color.WHITE);
		if (status != null) {
			g.drawString(status.toString(), 10, getHeight() / 2);
		} else {
			g.drawString("Unknown status", 10, getHeight() / 2);
		}

		if (serverInfo == null) {
			g.drawString("Disconnected", getWidth() - 150, getHeight() / 2);
		} else {
			g.drawString(serverInfo.toString(), getWidth() - 150, getHeight() / 2);
		}
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
