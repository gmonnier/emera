package com.gmo.ui.DialogablePanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.gmo.ui.components.titleBar.CustomTitleBar;
import com.gmo.ui.components.titleBar.ITitleBarListener;
import com.gmo.ui.lookAndFeel.FrameBorder;
import com.gmo.ui.lookAndFeel.looksEnums.TitleBarLook;
import com.gmo.ui.utility.ComponentMover;
import com.gmo.ui.utility.ComponentResizer;
import com.sun.awt.AWTUtilities;

public abstract class DialogablePanel extends JComponent implements ITitleBarListener {

	public enum panelViewTypeEnum {
		DIALOG_MODE, PANEL_MODE, UNKNOWN
	}

	protected panelViewTypeEnum panelViewMode;
	String title;

	// Dialog Frame (if dialog is not into its parent panel)
	protected JFrame externalFrame;

	// Parent panel on which dialog will be displayed
	private JComponent container = null;

	private CustomTitleBar associateTitle;

	// Prefered panel size
	private Dimension preferredPanelSize;

	// Prefered Frame size
	private Dimension preferredFrameSize;

	// Parent frame to locate the external dialog frame
	private JFrame parentRelativeFrame;

	// Specify if external frame should be resizable
	private boolean resizable = true;

	/**
	 * 
	 * Constructor of a dialogable Panel, a panel that is either displayed into
	 * an external JDialog or into a classical JPanel
	 * 
	 * @param title
	 *            title of the frame or the panel
	 * 
	 */
	public DialogablePanel(String title, JFrame parentRelativeFrame, boolean minimize, boolean maximize, boolean close) {

		this.parentRelativeFrame = parentRelativeFrame;
		this.title = title;
		panelViewMode = panelViewTypeEnum.UNKNOWN;

		preferredFrameSize = new Dimension(700, 300);

		setPreferredSize(preferredPanelSize);

		setLayout(new BorderLayout());
		associateTitle = new CustomTitleBar(title, this, minimize, maximize, close);
		add(associateTitle, BorderLayout.NORTH);
		associateTitle.setSelected(true);
	}

	public void setTitleBarPanelLook(TitleBarLook look) {
		associateTitle.setLook(look);
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public abstract void panelClosed();

	public abstract void panelMaximized();

	public abstract void buildView();

	public void setPanelContainer(JComponent container) {
		this.container = container;
	}

	public void setPreferredPanelSize(Dimension d) {
		preferredPanelSize = d;
		setPreferredSize(preferredPanelSize);
	}

	public void setPreferredFrameSize(Dimension d) {
		preferredFrameSize = d;
	}

	public void setPanelViewMode(panelViewTypeEnum newMode) {
		if (this.panelViewMode != newMode) {

			if (newMode == panelViewTypeEnum.PANEL_MODE) {
				// set panel view as classical panel
				if (container != null) {
					externalFrame.getContentPane().remove(this);
					externalFrame.dispose();
					setBorder(null);
					container.add(this, BorderLayout.SOUTH);
					container.revalidate();
				} else {
					setPanelViewMode(panelViewTypeEnum.DIALOG_MODE);
					return;
				}
			} else if (newMode == panelViewTypeEnum.DIALOG_MODE) {
				// set panel view into a dialog box
				// Remove THIS component from its previous parent

				if (container != null) {
					for (int i = 0; i < container.getComponents().length; i++) {
						if (container.getComponents()[i] == this) {
							container.remove(this);
							container.revalidate();
							container.repaint();
							container.setBounds(container.getBounds());
							break;
						}
					}
				}

				// Display the dialog into a Frame
				externalFrame = new JFrame();

				externalFrame.setMinimumSize(getMinimumSize());
				externalFrame.setSize(preferredFrameSize);
				externalFrame.setLocationRelativeTo(parentRelativeFrame);

				new ComponentMover(externalFrame, associateTitle);
				if (resizable) {
					new ComponentResizer(externalFrame);
				}
				setBorder(FrameBorder.getDefaultborder());

				externalFrame.setUndecorated(true);

				externalFrame.setTitle(title);
				externalFrame.setAlwaysOnTop(true);
				externalFrame.getContentPane().add(this);
				externalFrame.setVisible(true);

			}
			this.panelViewMode = newMode;
		}
	}

	public void closeHandles() {
		if (panelViewMode == panelViewTypeEnum.DIALOG_MODE && externalFrame != null) {
			externalFrame.dispose();
		} else if (panelViewMode == panelViewTypeEnum.PANEL_MODE && container != null) {
			container.remove(this);
			container.revalidate();
		}
	}

	public void setFrameTransparent(float opacity) {
		AWTUtilities.setWindowOpacity(externalFrame, opacity);
	}

	public void setIconImage(String path) {
		associateTitle.setIconImage(path);
	}

	/*--------------- ITitleBarListener methods -----------------------*/
	@Override
	public void activated(String sourceName,boolean isActiveted) {
		// nothing to be done here
	}

	@Override
	public void close(String sourceName) {
		panelClosed();
	}

	@Override
	public void maximize(String sourceName) {
		if (panelViewMode == panelViewTypeEnum.DIALOG_MODE) {
			// parano�a
			if (externalFrame != null) {
				externalFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		} else {
			setPanelViewMode(panelViewTypeEnum.DIALOG_MODE);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panelMaximized();
			}
		});
	}

	@Override
	public void minimize(String sourceName) {
		setPanelViewMode(panelViewTypeEnum.PANEL_MODE);
	}
	/*--------------- ITitleBarListener methods -----------------------*/
}
