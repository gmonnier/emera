package com.gmo.ui.components.customizedFrame.warning;

import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gmo.ui.components.DefaultDialogBackgroundPanel;
import com.gmo.ui.lookAndFeel.Colors;

public class WarningPanel extends DefaultDialogBackgroundPanel {

	private JButton close;

	public WarningPanel(Window parent, String messageLevel1, String messageLevel2, ActionListener listener) {

		super(parent, messageLevel1, messageLevel2, true);

		close = new JButton("OK");
		close.setActionCommand("OK");
		close.setSize(120, 23);
		close.addActionListener(listener);

		close.registerKeyboardAction(close.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
		close.registerKeyboardAction(close.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);

		add(close);

	}

	/**
	 * Test main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					UIManager.put("nimbusOrange", Colors.getGreenprogressbar());
					UIManager.put("ProgressBar[Enabled+Indeterminate].progressPadding", new Integer(10));

					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						UIManager.put("ProgressBar.tileWidth", 16);
						UIManager.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", UIManager.get("ProgressBar[Enabled+Finished].foregroundPainter"));
					} catch (InstantiationException e) {
					} catch (ClassNotFoundException e) {
					} catch (UnsupportedLookAndFeelException e) {
					} catch (IllegalAccessException e) {
					}
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WarningDialog warningDial = new WarningDialog("Un test titre", "Sed ut perspiciatis unde omnis iste", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt", null);
		warningDial.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	@Override
	public void setBounds(int arg0, int arg1, int arg2, int arg3) {
		super.setBounds(arg0, arg1, arg2, arg3);
		close.setLocation(getWidth() - close.getWidth() - 50, getHeight() - close.getHeight() - 15);
	}
}
