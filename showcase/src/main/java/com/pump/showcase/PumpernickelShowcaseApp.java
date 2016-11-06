package com.pump.showcase;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.pump.awt.ClickSensitivityDemo;
import com.pump.debug.AWTMonitorDemo;
import com.pump.desktop.DesktopApplication;
import com.pump.geom.AreaXTestPanel;
import com.pump.geom.knot.KnotDemo;
import com.pump.swing.ListSectionContainer;
import com.pump.swing.SectionContainer.Section;

public class PumpernickelShowcaseApp extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException {
	    DesktopApplication.initialize("com.pump.showcase", "Showcase", "1.0", "jeremy.wood@mac.com", PumpernickelShowcaseApp.class);
	    
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PumpernickelShowcaseApp p = new PumpernickelShowcaseApp();
				p.pack();
				p.setLocationRelativeTo(null);
				p.setVisible(true);
				p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
	
	enum Layout {
		STRETCH_TO_FIT, SCROLLPANE
	}
	
	ListSectionContainer sectionContainer = new ListSectionContainer(true);
	
	public PumpernickelShowcaseApp() {
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		getContentPane().add(sectionContainer, c);
		
		getContentPane().setPreferredSize(new Dimension(800, 600));
		
		try {
			sectionContainer.addSection("transition2d", "Animation: Transitions (2D)").getBody().add(new Transition2DDemo());
			sectionContainer.addSection("transition3d", "Animation: Transitions (3D)").getBody().add(new Transition3DDemo());
			sectionContainer.addSection("brushedMetal", "Brushed Metal").getBody().add(new BrushedMetalDemo());
			sectionContainer.addSection("bmpComparison", "BMP ImageIO Comparison").getBody().add(new BmpComparisonDemo());
			sectionContainer.addSection("alphaComposite", "AlphaComposite").getBody().add(new AlphaCompositeDemo());
			sectionContainer.addSection("bristleStroke", "Strokes: Bristle").getBody().add(new BristleStrokeDemo());
			sectionContainer.addSection("brushStroke", "Strokes: Brush").getBody().add(new BrushStrokeDemo());
			sectionContainer.addSection("calligraphyStroke", "Strokes: Calligraphy").getBody().add(new CalligraphyStrokeDemo());
			sectionContainer.addSection("charcoalStroke", "Strokes: Charcoal").getBody().add(new CharcoalStrokeDemo());
			sectionContainer.addSection("swivelAnim", "Animation: Swivel Path").getBody().add(new SwivelPathAnimationDemo(this));
			sectionContainer.addSection("textEffectAnim", "Animation: Text Effect").getBody().add(new TextEffectDemo());
			sectionContainer.addSection("writingShapeDemo", "Animation: Writing Shape").getBody().add(new WritingShapeDemo());
			addSection("awtMonitor", "AWT Monitor", new AWTMonitorDemo(), Layout.STRETCH_TO_FIT);
			addSection("halftoneGradient", "Gradient: Halftone", new HalftoneGradientDemo(), Layout.STRETCH_TO_FIT);
			addSection("colorBandDemo", "Gradient: Color Band", new ColorBandDemo(this), Layout.STRETCH_TO_FIT);
			addSection("clickSensitivity", "Click Sensitivity", new ClickSensitivityDemo(), Layout.STRETCH_TO_FIT);
			addSection("shapeBounds", "Shape Bounds", new ShapeBoundsDemo(), Layout.STRETCH_TO_FIT);
			addSection("resourcePool", "Resource Pool", new ResourcePoolDemo(), Layout.STRETCH_TO_FIT);
			addSection("clipper", "Clipper", new ClipperDemo(), Layout.STRETCH_TO_FIT);
			addSection("hsbInline", "HSB Inline", new HSBInlineDemo(), Layout.STRETCH_TO_FIT);
			addSection("angeSliderUI", "AngleSliderUI", new AngleSliderUIDemo(), Layout.SCROLLPANE);
			addSection("decoratedPanelUI", "DecoratedPanelUI", new DecoratedPanelUIDemo(), Layout.STRETCH_TO_FIT);
			addSection("spiral2D", "Shapes: Spiral2D", new Spiral2DDemo(), Layout.STRETCH_TO_FIT);
			addSection("swingComponents", "Swing: Components", new SwingComponentsDemo(), Layout.STRETCH_TO_FIT);
			addSection("screenCapture", "Screen Capture", new ScreenCaptureDemo(this), Layout.SCROLLPANE);
			addSection("collapsibleContainer", "Swing: CollapsibleContainer", new CollapsibleContainerDemo(), Layout.STRETCH_TO_FIT);
			addSection("customizedToolbar", "Swing: CustomizedToolbar", new CustomizedToolbarDemo(), Layout.STRETCH_TO_FIT);
			addSection("scaling", "Images: Scaling", new ImageScalingDemo(), Layout.STRETCH_TO_FIT);
			addSection("quantization", "Images: Quantization", new ImageQuantizationDemo(), Layout.STRETCH_TO_FIT);
			addSection("color", "Swing: Color Components", new ColorDemo(), Layout.STRETCH_TO_FIT);
			addSection("filledButtons", "Swing: FilledButtonUI", new FilledButtonUIDemo(), Layout.STRETCH_TO_FIT);
			addSection("areaXTests", "Shapes: AreaX Tests", new AreaXTestPanel(), Layout.STRETCH_TO_FIT);
			addSection("knots", "Shapes: Knots", new KnotDemo(), Layout.STRETCH_TO_FIT);
			addSection("graphicsWriterDebugger", "Graphics: Debugger", new GraphicsWriterDemo(), Layout.STRETCH_TO_FIT);
			addSection("jpegMetaData", "Images: JPEG Metadata", new JPEGMetaDataDemo(), Layout.STRETCH_TO_FIT);
			addSection("audioPlayerComponents", "Audio: Swing Components", new AudioComponentsDemo(), Layout.STRETCH_TO_FIT);
			addSection("mathGDemo", "Math: MathG Comparison", new MathGDemo(), Layout.STRETCH_TO_FIT);
			addSection("mathEquations", "Math: Gaussian Elimination", new EquationsDemo(), Layout.STRETCH_TO_FIT);
			addSection("textSourceCode", "Text: Java Source Code Component", new JavaTextComponentHighlighterDemo(true), Layout.STRETCH_TO_FIT);
			addSection("textSearchDemo", "Text: Search Controls", new TextSearchDemo(), Layout.STRETCH_TO_FIT);
			//TODO: add CubicIntersectionsPanel with 18-degree polynomial?
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void addSection(String id, String text,
			JComponent component, Layout layout) {
		Section section = sectionContainer.addSection(id, text);
		JPanel body = section.getBody();
		if(layout==Layout.STRETCH_TO_FIT) {
			body.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			body.add(component, c);
		} else {
			body.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			JScrollPane scrollPane = new JScrollPane(component, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			body.add(scrollPane, c);
		}
	}
}
