//*******************************************************************
//
//      Oce Engineering Creteil: PRODUCT DOCUMENT        CONFIDENTIAL
//
//       R&D-filename    :   %name%
//
//       Title           :
//       Abstract        :
//
//
//
//
//       Keywords        :
//
//       %version:       %
//       %created_by:    %
//       %date_created:  %
//       %full_filespec: %
//
//   Copyright 2004 Oce-Technologies B.V., Venlo, The Netherlands
//
//*******************************************************************
package ui;

import javax.swing.JComponent;

public interface ISinusoidEffect {
	
	public void setEllapsedActionTimeValue(int value);
	
	/**
	 * This function is used for naming the thread.
	 * @return the name of the component that used this thread.
	 */
	public String getComponentName();
}
