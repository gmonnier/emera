package com.gmo.ui.components.colorChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.gmo.ui.components.BackGroundComponent;
import com.gmo.ui.components.customizedFrame.CustomizedDialog;
import com.gmo.ui.components.toolBar.StrippedToolBar;
import com.gmo.ui.lookAndFeel.Fonts;
import com.gmo.ui.lookAndFeel.looksEnums.BackGroundLook;
import com.gmo.ui.lookAndFeel.looksEnums.TitleBarLook;

public class CustomColorChooser extends CustomizedDialog implements WindowListener{

	private ColorPicker colorPickerPan;
	private JButton ok = new JButton("OK");
	private JButton cancel = new JButton("Cancel");
	private int alpha;

	private Color initColor = null;
	private Color returnValue = null;
	private boolean showTransparency;

	private final static int WIDTH = 500;
	private final static int HEIGHT = 350;

	public CustomColorChooser(String title, Component owner, Color initColor, boolean showTransparency) {

		super(title, true, false, true, TitleBarLook.SMALL, false, false, true);

		this.showTransparency = showTransparency;
		this.initColor = initColor;

		setSize(WIDTH, HEIGHT);
		addWindowListener(this);
		
		if (owner != null) {
			setLocation(- WIDTH / 2, - HEIGHT / 2);
		} else {
			System.err.println("owner null");
		}

		buildView(content);
		
		setLocationRelativeTo(owner);
		
		setVisible(true);
	}

	@Override
	public void buildView(JPanel content) {

		BackGroundComponent backGround = new BackGroundComponent(BackGroundLook.HORIZONTAL_GRADIENT_GRAY);
		backGround.setLayout(new BorderLayout());

		colorPickerPan = new ColorPicker(true, showTransparency);
		colorPickerPan.setSize(WIDTH, HEIGHT - 100);
		colorPickerPan.setRGB(initColor.getRed(), initColor.getGreen(), initColor.getBlue());
		colorPickerPan.setOpacity(((float) initColor.getAlpha()) / 255f);
		backGround.add(colorPickerPan, BorderLayout.CENTER);

		StrippedToolBar toolBarPan = new StrippedToolBar();

		ok.setFont(Fonts.getFontButton());
		cancel.setFont(Fonts.getFontButton());
		ok.setPreferredSize(new Dimension(100, 25));
		cancel.setPreferredSize(new Dimension(100, 25));
		ok.addActionListener(buttonListener);
		cancel.addActionListener(buttonListener);
		toolBarPan.add(cancel);
		toolBarPan.add(ok);

		backGround.add(toolBarPan, BorderLayout.SOUTH);
		
		content.add(backGround);
		
	}

	/**
	 * @return the color committed when the user clicked 'OK'. Note this returns
	 *         <code>null</code> if the user canceled this dialog, or exited via
	 *         the close decoration.
	 */
	public Color getColor() {
		return returnValue;
	}

	ActionListener buttonListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == ok) {
				returnValue = colorPickerPan.getColor();
			}
			close("");
		}
	};

	@Override
	public void activated(String sourceName, boolean isActiveted) {
		// nothing to do here
	}

	@Override
	public void close(String sourceName) {
		dispose();
	}

	@Override
	public void maximize(String sourceName) {

	}

	@Override
	public void minimize(String sourceName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// Fix for Def-25.
		repaint();
	}

}
