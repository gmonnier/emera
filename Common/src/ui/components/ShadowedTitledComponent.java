package ui.components;

import java.awt.BorderLayout;

import ui.components.titleBar.CustomTitleBar;
import ui.components.titleBar.ITitleBarListener;

public abstract class ShadowedTitledComponent extends ShadowedComponent {

	protected CustomTitleBar associatedTitle;

	public ShadowedTitledComponent(String title, boolean minimize, boolean maximize, boolean close, ITitleBarListener titleBarList) {

		setLayout(new BorderLayout());

		associatedTitle = new CustomTitleBar(title, titleBarList, minimize, maximize, close);
		add(associatedTitle, BorderLayout.NORTH);
		associatedTitle.setSelected(true);

	}

}
