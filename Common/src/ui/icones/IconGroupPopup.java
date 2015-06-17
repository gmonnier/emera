package ui.icones;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class IconGroupPopup implements IconListener {

	ArrayList<Icone> subIconList;
	JPopupMenu popup;
	PanelPopup panel;
	Icone sourceIcon;

	GradientPaint gpBackground;

	public IconGroupPopup(Icone sourceIcon) {
		this.sourceIcon = sourceIcon;
		sourceIcon.addIconListener(this);
		subIconList = new ArrayList<Icone>();
		
		popup = new JPopupMenu();
		popup.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

		panel = new PanelPopup();
		popup.add(panel);
	}

	public void addIconeToGroup(Icone subIcone) {
		subIconList.add(subIcone);
		subIcone.addIconListener(this);
		actualizePopup();
	}

	private void actualizePopup() {

		panel.setPreferredSize(new Dimension(30, subIconList.get(0).getHeight() * subIconList.size() + (subIconList.size()+1) * 4));
		panel.removeAll();

		int posx = 3;
		int posy = 4;

		for (int i = 0; i < subIconList.size(); i++) {
			subIconList.get(i).setLocation(posx, posy);

			posy += 30;

			panel.add(subIconList.get(i));
		}
	}

	@Override
	public void iconePressedPerformed(MouseEvent arg0) {
		if (arg0.getSource() == sourceIcon) {
			popup.show(sourceIcon, -34, 0);
		}else{
			// An icone belonging to the group has been pressed : closing the popup
			sourceIcon.setHighlighted(false);
			popup.setVisible(false);
		}
	}

	private class PanelPopup extends JPanel {

		public PanelPopup() {

			setLayout(null);
			setPreferredSize(new Dimension(10, 10));

		}

		@Override
		public void paint(Graphics arg0) {

			Graphics2D g2 = (Graphics2D) arg0;
			Color color1Back = Color.lightGray;
			Color color2Back = Color.GRAY;
			Color colorShadow = new Color(0, 0, 0, 100);

			// paint background
			gpBackground = new GradientPaint((int) (getWidth() * (1.0 / 3)), 0, color1Back, getWidth(), 0, color2Back, true);
			g2.setPaint(gpBackground);
			g2.fillRect(0, 0, getWidth(), getHeight());

			// paint shadow
			/*GradientPaint gpBackgroundShadow = new GradientPaint(0, 0, colorShadow, getWidth() / 8, 0, new Color(0, 0, 0, 0), false);
			g2.setPaint(gpBackgroundShadow);
			g2.fillRect(0, 0, getWidth(), getHeight());*/

			paintComponents(g2);
		}
	}

	@Override
	public void iconeClickedPerformed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
