package ui.components.customizedFrame;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import ui.components.titleBar.ITitleBarListener;
import ui.components.titleBar.CustomTitleBar;
import ui.lookAndFeel.FrameBorder;
import ui.lookAndFeel.looksEnums.TitleBarLook;
import ui.utility.ComponentMover;
import ui.utility.ComponentResizer;

public abstract class CustomizedDialog extends JDialog implements ITitleBarListener, FocusListener {

	private CustomTitleBar associateTitle;
	protected JPanel content;

	public CustomizedDialog(String title, boolean alwaysOnTop, boolean resizable, boolean modal, TitleBarLook titleBarLook, boolean minimize, boolean maximize, boolean close) {

		setLayout(new BorderLayout());
		setUndecorated(true);
		setTitle(title);
		setAlwaysOnTop(alwaysOnTop);
		setModal(modal);

		JPanel borderedPanel = new JPanel();
		// borderedPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.gray,Color.DARK_GRAY));
		borderedPanel.setBorder(FrameBorder.getDefaultborder());
		borderedPanel.setLayout(new BorderLayout());

		associateTitle = new CustomTitleBar(title, this, minimize, maximize, close);
		associateTitle.setLook(titleBarLook);
		borderedPanel.add(associateTitle, BorderLayout.NORTH);
		associateTitle.setSelected(true);

		content = new JPanel();
		content.setLayout(new BorderLayout());
		borderedPanel.add(content, BorderLayout.CENTER);

		getContentPane().add(borderedPanel, BorderLayout.CENTER);
		addFocusListener(this);

		new ComponentMover(this, associateTitle);
		if (resizable) {
			new ComponentResizer(this);
		}

		requestFocus();

	}

	public CustomTitleBar getAssociateTitle() {
		return associateTitle;
	}

	public void setIconImage(String path) {
		associateTitle.setIconImage(path);
	}

	public abstract void buildView(JPanel content);

	@Override
	public void focusGained(FocusEvent arg0) {
		associateTitle.setSelected(true);
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		System.out.println("focusLost");
		associateTitle.setSelected(false);
	}

}
