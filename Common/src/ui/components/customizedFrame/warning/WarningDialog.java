package ui.components.customizedFrame.warning;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import ui.components.customizedFrame.CustomizedDialog;
import ui.lookAndFeel.looksEnums.TitleBarLook;

public class WarningDialog extends CustomizedDialog implements ActionListener {

	private String message;

	private String messageDetailed;

	// Associated inside panel
	private WarningPanel warnPanel;

	public WarningDialog(String title, String message, String messageDetailed, Component parent) {

		super(title, false, false, true, TitleBarLook.NORMAL, false, false, true);

		this.message = message;

		this.messageDetailed = messageDetailed;

		this.setSize(520, 145);
		setLocationRelativeTo(parent);

		buildView(content);

		setVisible(true);
	}

	@Override
	public void buildView(JPanel content) {
		warnPanel = new WarningPanel(this, message, messageDetailed, this);
		content.add(warnPanel);
	}

	@Override
	public void activated(String s,boolean isActiveted) {

	}

	@Override
	public void close(String s) {
		dispose();
	}

	@Override
	public void maximize(String s) {

	}

	@Override
	public void minimize(String s) {

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		close("");
	}
}
