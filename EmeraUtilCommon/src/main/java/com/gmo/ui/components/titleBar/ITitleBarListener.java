package com.gmo.ui.components.titleBar;

public interface ITitleBarListener {
	
	// Called when clicked on the close title bar button
	public void close(String sourceName);
	// Called when clicked on the minimize title bar button
	public void minimize(String sourceName);
	// Called when clicked on the maximize title bar button
	public void maximize(String sourceName);
	// Called when clicked on the maximize title bar button
	public void activated(String sourceName, boolean isActiveted);
}
