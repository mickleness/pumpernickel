package com.pump.showcase.app;

import java.util.LinkedList;

import com.pump.util.JVM;

public class DemoList extends LinkedList<DemoListElement> {
	private static final long serialVersionUID = 1L;

	public DemoList() {
		add(new DemoListElement("Transition2D", "Transition2DDemo"));
		add(new DemoListElement("Transition3D", "Transition3DDemo"));
		add(new DemoListElement("BmpEncoder, BmpDecoder", "BmpComparisonDemo"));
		add(new DemoListElement("AlphaComposite", "AlphaCompositeDemo"));
		add(new DemoListElement("TextEffect", "TextEffectDemo"));
		add(new DemoListElement("AWTMonitor", "AWTMonitorDemo"));
		add(new DemoListElement("GradientTexturePaint",
				"GradientTexturePaintDemo"));
		add(new DemoListElement("ClickSensitivityControl",
				"ClickSensitivityControlDemo"));
		add(new DemoListElement("ShapeBounds", "ShapeBoundsDemo"));
		add(new DemoListElement("Clipper", "ClipperDemo"));
		add(new DemoListElement("AngleSliderUI", "AngleSliderUIDemo"));
		add(new DemoListElement("Spiral2D", "Spiral2DDemo"));
		add(new DemoListElement("DecoratedListUI, DecoratedTreeUI",
				"DecoratedDemo"));
		add(new DemoListElement("JThrobber", "ThrobberDemo"));
		add(new DemoListElement("JBreadCrumb", "BreadCrumbDemo"));
		add(new DemoListElement("CollapsibleContainer",
				"CollapsibleContainerDemo"));
		add(new DemoListElement("CustomizedToolbar", "CustomizedToolbarDemo"));
		add(new DemoListElement("JToolTip, QPopupFactory", "JToolTipDemo"));
		add(new DemoListElement("JPopover", "JPopoverDemo"));
		add(new DemoListElement("Scaling", "ScalingDemo"));
		// add(new DemoListElement("ImageQuantization", new
		// ImageQuantizationDemo());
		add(new DemoListElement("JColorPicker", "JColorPickerDemo"));
		// add(new DemoListElement("Shapes: AreaX Tests", new AreaXTestPanel());
		add(new DemoListElement("JPEGMetaData", "JPEGMetaDataDemo"));
		add(new DemoListElement("QPanelUI", "QPanelUIDemo"));
		add(new DemoListElement("AudioPlayer", "AudioPlayerDemo"));
		add(new DemoListElement("JavaTextComponentHighlighter",
				"JavaTextComponentHighlighterDemo"));
		add(new DemoListElement("XMLTextComponentHighlighter",
				"XMLTextComponentHighlighterDemo"));
		// add(new DemoListElement("Text: Search Controls", new
		// TextSearchDemo());
		// add(new DemoListElement("QuickTime: Writing Movies", new
		// MovWriterDemo());
		add(new DemoListElement("Highlighters, WildcardPattern",
				"WildcardPatternHighlighterDemo"));
		add(new DemoListElement("BoxTabbedPaneUI", "BoxTabbedPaneUIDemo"));
		add(new DemoListElement("CircularProgressBarUI",
				"CircularProgressBarUIDemo"));
		add(new DemoListElement("Strokes, MouseSmoothing",
				"StrokeMouseSmoothingDemo"));
		add(new DemoListElement("JColorWell, JPalette",
				"JColorWellPaletteDemo"));
		add(new DemoListElement("JEyeDropper", "JEyeDropperDemo"));
		add(new DemoListElement("JSwitchButton", "JSwitchButtonDemo"));
		add(new DemoListElement("JButton, QButtonUI", "JButtonDemo"));
		add(new DemoListElement("MixedCheckBoxState",
				"MixedCheckBoxStateDemo"));
		add(new DemoListElement("JFrame, JDialog, JWindow", "WindowDemo"));
		add(new DemoListElement("System Properties", "SystemPropertiesDemo"));
		add(new DemoListElement("FileIcon", "FileIconDemo"));
		add(new DemoListElement("DesktopHelper", "DesktopHelperDemo"));
		add(new DemoListElement("VectorImage", "VectorImageDemo"));
		add(new DemoListElement("StarPolygon", "StarPolygonDemo"));
		add(new DemoListElement("ShadowRenderer", "ShadowRendererDemo"));
		add(new DemoListElement("HTML, QHTMLEditorKit", "HTMLDemo"));
		if (JVM.isMac) {
			add(new DemoListElement("AquaIcon", "AquaIconDemo"));
			add(new DemoListElement("NSImage", "NSImageDemo"));
		} else if (JVM.isWindows) {
			add(new DemoListElement("WindowsIcon", "WindowsIconDemo"));
		}
	}
}
