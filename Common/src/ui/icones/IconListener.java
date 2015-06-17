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
package ui.icones;

import java.awt.event.MouseEvent;

public interface IconListener {
	
	public abstract void iconePressedPerformed(MouseEvent arg0);
	
	public void iconeClickedPerformed(MouseEvent arg0);
	
}
