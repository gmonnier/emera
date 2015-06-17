package ui.lookAndFeel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import ui.lookAndFeel.looksEnums.ComponentStateColors;
import ui.lookAndFeel.looksEnums.GeneralColorTheme;

import com.itextpdf.text.BaseColor;

public class Colors {

	final static GeneralColorTheme theme = GeneralColorTheme.BLUE;

	private static Color mainBackGroundColor;

	private final static Color transparent = new Color(0, 0, 0, 0);

	private static Color greenToolBar;
	private static Color greenToolBarTransparent100;
	private static Color greenToolBarTransparent200;

	private static final Color redErrorTransparent = new Color(140, 117, 117, 100);
	private static final Color greenProgressBar = new Color(111, 140, 15);

	private static final Color standardSelectionTreeColor = new Color(254, 230, 110, 200);
	private static final Color standardSelectionTreeTransparent = new Color(254, 230, 110, 100);

	private static final Color secondarySelectionTree = new Color(235, 200, 0);
	private static final Color secondarySelectionTreeTransparent = new Color(236, 218, 100);

	private static final Color plainGeometryColor = new Color(250, 50, 70);
	private static final Color selectedGeometryColor = new Color(50, 250, 70);
	private static final Color highlightGeometryColor = new Color(254, 207, 51);

	private static final Color detailedGeometryInfoCellColorSelected = new Color(50, 115, 150, 100);
	private static final Color detailedGeometryInfoCellColorHighlighted = new Color(192, 255, 62, 100);

	// Color of table cells into the file info view
	private static final Color fileInfoCellColor = new Color(50, 115, 150, 100);
	private static final Color fileInfoCellColor2 = new Color(150, 115, 150, 100);
	private final static Color tableSelectionBackground = new Color(234,255,183);

	private static final GradientPaint titleBarShadow = new GradientPaint(0, 0, Color.darkGray, 0, 5, new Color(0, 0, 0, 0), false);
	private static final GradientPaint titleBarShadowSmallDialog = new GradientPaint(0, 0, Color.gray, 0, 4, new Color(0, 0, 0, 0), false);

	// predefined toolBarGradiants
	private final static int NORMAL_HEIGHT = 35;
	private final static int BIG_HEIGHT = 50;

	// Step info panel background colors
	private static final Color colorStepInfo1 = new Color(123, 160, 201);
	private static final Color colorStepInfo2 = new Color(61, 91, 131);

	private static final Color bordeaux = new Color(170, 12, 25);
	private static final Color colorStepInfo2Select = bordeaux;
	private static final Color colorStepInfo1Select = new Color(220, 12, 25);

	private static Color colorGradToolBar1;
	private static Color colorGradToolBar2;

	// Colors for 2D options preset components
	private static Color presetNormal1;
	private static Color presetNormal2;

	private static Color presetHighlight1;
	private static Color presetHighlight2;

	private static Color presetSelect1;
	private static Color presetSelect2;

	private static Color presetError1 = new Color(230, 148, 90).darker();
	private static Color presetError2 = new Color(230, 148, 90);

	private static Color errorBar1 = new Color(50,0,0);
	private static Color errorBar2 = new Color(120,0,0);
	
	// PDF export colors
	private static final BaseColor impairTableColor = new BaseColor(240, 240, 240);
	private static final BaseColor errorTableColor = new BaseColor(240, 230, 230);
	private static final BaseColor okTableColor = new BaseColor(220, 240, 220);
	
	private static final BaseColor greenTableColor = new BaseColor(163, 239, 119);
	private static final BaseColor orangeTableColor = new BaseColor(239, 185, 119);
	private static final BaseColor redTableColor = new BaseColor(239, 119, 119);


	static {
		switch (theme) {
		case BLACK_AND_WHITE: {

			// black and white
			colorGradToolBar1 = Color.gray;
			colorGradToolBar2 = Color.black;
			mainBackGroundColor = new Color(214, 217, 223);

			// Preset components colors
			// normal - blue
			presetNormal1 = new Color(170, 185, 196).darker();
			presetNormal2 = new Color(170, 185, 196);
			// highlighted - yellow orange
			presetHighlight1 = new Color(254, 207, 51);
			presetHighlight2 = new Color(254, 207, 51, 100);
			// selected - green
			presetSelect1 = new Color(209, 240, 117);
			presetSelect2 = new Color(209, 240, 117, 100);

			// Green color
			greenToolBar = new Color(209, 240, 117);
			greenToolBarTransparent100 = new Color(209, 240, 117, 100);
			greenToolBarTransparent200 = new Color(209, 240, 117, 200);
			break;
		}
		case BLUE: {

			// blue
			colorGradToolBar1 = new Color(123, 160, 201);
			colorGradToolBar2 = new Color(61, 91, 131);

			// Kind of blue but more gray
			mainBackGroundColor = new Color(170, 185, 196);

			// Preset components colors
			// normal - blue
			presetNormal1 = new Color(170, 185, 196).darker();
			presetNormal2 = new Color(170, 185, 196);
			// highlighted - blue (lighter blue)
			presetHighlight1 = new Color(123, 160, 201);
			presetHighlight2 = new Color(61, 91, 131);
			// selected - yellow
			presetSelect1 = new Color(254, 207, 51);
			presetSelect2 = new Color(254, 207, 51, 100);

			
			// Green color
			greenToolBar = new Color(209, 240, 117);
			greenToolBarTransparent100 = new Color(209, 240, 117, 100);
			greenToolBarTransparent200 = new Color(209, 240, 117, 200);
			// yellow orange
			/*greenToolBar = new Color(254, 207, 51);
			greenToolBarTransparent100 = new Color(254, 207, 51, 100);
			greenToolBarTransparent200 = new Color(254, 207, 51, 200);*/
			break;
		}
		}
	}

	private static GradientPaint toolBarGradient_NORMAL = new GradientPaint(0, 0, colorGradToolBar1, 0, (int) (NORMAL_HEIGHT * 2.5 / 3), colorGradToolBar2, true);
	private static GradientPaint toolBarGradient_BIG = new GradientPaint(0, 0, colorGradToolBar1, 0, (int) (BIG_HEIGHT * 2.5 / 3), colorGradToolBar2, true);

	public static Color getGreenToolbar() {
		return greenToolBar;
	}

	public static Color getBordeaux() {
		return bordeaux;
	}

	public static Color getGreentoolbartransparent100() {
		return greenToolBarTransparent100;
	}

	public static Color getGreentoolbartransparent200() {
		return greenToolBarTransparent200;
	}

	public static Color getRederrortransparent() {
		return redErrorTransparent;
	}

	public static Color getGreenprogressbar() {
		return greenProgressBar;
	}

	public static Color getStandardSelectionTreeColor() {
		return standardSelectionTreeColor;
	}

	public static Color getTransparent() {
		return transparent;
	}

	public static Color getStandardSelectionTreeTransparent() {
		return standardSelectionTreeTransparent;
	}

	public static Color getSecondarySelectionTreeColor() {
		return secondarySelectionTree;
	}

	public static Color getSecondarySelectionTreeColorTransparent() {
		return secondarySelectionTreeTransparent;
	}

	public static GradientPaint getTitleBarShadow() {
		return titleBarShadow;
	}

	public static GradientPaint getTitlebarshadowsmalldialog() {
		return titleBarShadowSmallDialog;
	}

	public static Color getMainbackgroundcolor() {
		return mainBackGroundColor;
	}

	public static Color getPlaingeometrycolor() {
		return plainGeometryColor;
	}

	public static Color getSelectedgeometrycolor() {
		return selectedGeometryColor;
	}

	public static Color getHighlightgeometrycolor() {
		return highlightGeometryColor;
	}

	public static Color getDetailedgeometryinfocellcolorselected() {
		return detailedGeometryInfoCellColorSelected;
	}

	public static Color getDetailedgeometryinfocellcolorhighlighted() {
		return detailedGeometryInfoCellColorHighlighted;
	}

	public static Color getFileInfoCellColor() {
		return fileInfoCellColor;
	}
	
	public static Color getFileInfoCellColor2() {
		return fileInfoCellColor2;
	}

	public static GradientPaint getToolBarGradient(int height) {

		if (height == NORMAL_HEIGHT) {
			return toolBarGradient_NORMAL;
		} else if (height == BIG_HEIGHT) {
			return toolBarGradient_BIG;
		} else {
			return new GradientPaint(0, 0, colorGradToolBar1, 0, (int) (height * 2.5 / 3), colorGradToolBar2, true);
		}

	}

	public static GradientPaint getPresetComponentGradient(int height, ComponentStateColors colorsType) {

		switch (colorsType) {
		case NORMAL: {
			return new GradientPaint(0, 0, presetNormal1, 0, (int) (height * 2.5 / 3), presetNormal2, true);
		}
		case HIGHLIGHTED: {
			return new GradientPaint(0, 0, presetHighlight1, 0, (int) (height * 2.5 / 3), presetHighlight2, true);
		}
		case SELECTED: {
			return new GradientPaint(0, 0, presetSelect1, 0, (int) (height * 2.5 / 3), presetSelect2, true);
		}
		case ERROR: {
			return new GradientPaint(0, 0, presetError1, 0, (int) (height * 2.5 / 3), presetError2, true);
		}
		default: {
			return new GradientPaint(0, 0, presetNormal1, 0, (int) (height * 2.5 / 3), presetNormal2, true);
		}
		}

	}

	public static GradientPaint getToolBarVerticalGradient(int x, int y, int width, int height, ComponentStateColors colorsType) {

		switch (colorsType) {
		case ERROR: {
			return new GradientPaint(x, y, errorBar1, width, height, errorBar2, true);
		}
		default: {
			return new GradientPaint(x, y, presetNormal1, width, height, presetNormal2, true);
		}
		}

	}

	public static GradientPaint getStepInfoBackGroundGradient(int height) {
		return new GradientPaint(0, 0, getGreenprogressbar(), 0, (int) (height * 2.5 / 3), getGreenprogressbar().brighter(), true);
	}

	public static Paint getStepItemGradient(int height, boolean selected) {
		Color colorStepInfo1Tmp = colorStepInfo1;
		Color colorStepInfo2Tmp = colorStepInfo2;
		if (selected) {
			colorStepInfo1Tmp = colorStepInfo1Select;
			colorStepInfo2Tmp = colorStepInfo2Select;
		}
		return new GradientPaint(0, 0, colorStepInfo1Tmp, 0, (int) (height * 2.5 / 3), colorStepInfo2Tmp, true);
	}

	public static Color getTableSelectionBackground() {
		return tableSelectionBackground;
	}
	
	public static BaseColor getImpairtablecolor() {
		return impairTableColor;
	}

	public static BaseColor getErrortablecolor() {
		return errorTableColor;
	}

	public static BaseColor getOktablecolor() {
		return okTableColor;
	}

	public static BaseColor getGreentablecolor() {
		return greenTableColor;
	}

	public static BaseColor getOrangetablecolor() {
		return orangeTableColor;
	}

	public static BaseColor getRedtablecolor() {
		return redTableColor;
	}

}
