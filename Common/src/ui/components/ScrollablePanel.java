package ui.components;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ScrollablePanel extends JComponent {

	private JScrollPane infoScroolPane;

	public ScrollablePanel(JComponent interieur) {

		setLayout(new BorderLayout());

		infoScroolPane = new JScrollPane(interieur);
		infoScroolPane.setOpaque(false);
		infoScroolPane.setBorder(null);
		infoScroolPane.setViewportBorder(null);

		add(infoScroolPane);

	}

}
