package ui.utility;

import java.awt.geom.GeneralPath;

public class GeneralPathEx {

	private GeneralPath associatedPath;
	
	private boolean isDashedPath;

	public GeneralPathEx(GeneralPath associatedPath, boolean isDashedPath) {
		this.associatedPath = associatedPath;
		this.isDashedPath = isDashedPath;
	}

	public GeneralPath getAssociatedPath() {
		return associatedPath;
	}

	public void setAssociatedPath(GeneralPath associatedPath) {
		this.associatedPath = associatedPath;
	}

	public boolean isDashedPath() {
		return isDashedPath;
	}

	public void setDashedPath(boolean isDashedPath) {
		this.isDashedPath = isDashedPath;
	}
	
	
}
