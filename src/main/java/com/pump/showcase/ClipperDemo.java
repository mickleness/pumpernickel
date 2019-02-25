/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.RootPaneContainer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.geom.Clipper;
import com.pump.geom.TransformUtils;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.swing.JFancyBox;

/**
 * A simple demo program for the Clipper class.
 * <P>
 * This offers both a performance analysis and a GUI-based demo (click the
 * "Show Sample" button).
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/ClipperDemo.png"
 * alt="A screenshot of the ClipperDemo.">
 */
public class ClipperDemo extends ShowcaseChartDemo {
	static final GeneralPath[][] p = new GeneralPath[3][100];
	private static final long serialVersionUID = 1L;
	static {
		Random r = new Random(0);
		for (int degree = 0; degree < 3; degree++) {
			for (int a = 0; a < p[degree].length; a++) {
				p[degree][a] = new GeneralPath();
				p[degree][a].moveTo((int) (300 * r.nextDouble()),
						(int) (300 * r.nextDouble()));
				int size = 20;
				for (int b = 0; b < size; b++) {
					if (degree == 0) {
						p[degree][a].lineTo((int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()));
					} else if (degree == 1) {
						p[degree][a].quadTo((int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()));
					} else {
						p[degree][a].curveTo((int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()),
								(int) (300 * r.nextDouble()));
					}
				}
				p[degree][a].closePath();
			}
		}
	}

	JPanel previewPanel = new JPanel() {
		private static final long serialVersionUID = 1L;

		Rectangle2D r = new Rectangle2D.Float(100, 100, 100, 100);

		{
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			int min = Math.min(getWidth(), getHeight());

			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.transform(TransformUtils.createAffineTransform(new Rectangle(0,
					0, 300, 300), new Rectangle(getWidth() / 2 - min / 2,
					getHeight() / 2 - min / 2, min, min)));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int shapeIndex = ((Number) shapeIndexSpinner.getValue()).intValue() - 1;
			int type = typeComboBox.getSelectedIndex();
			GeneralPath s = p[type][shapeIndex];
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 300, 300);
			g2.setColor(Color.blue);
			g2.fill(s);
			GeneralPath s2 = Clipper.clipToRect(s, null, r);
			g2.setColor(new Color(0, 255, 0, 120));
			g2.fill(s2);
			PathIterator i = s.getPathIterator(null);
			float[] f2 = new float[6];
			g.setColor(Color.red);
			while (i.isDone() == false) {
				int k = i.currentSegment(f2);
				if (k == PathIterator.SEG_MOVETO) {
					g2.fill(new Ellipse2D.Float(f2[0] - 2, f2[1] - 2, 4, 4));
				} else if (k == PathIterator.SEG_LINETO) {
					g2.draw(new Ellipse2D.Float(f2[0] - 2, f2[1] - 2, 4, 4));
				} else if (k == PathIterator.SEG_QUADTO) {
					g2.draw(new Ellipse2D.Float(f2[2] - 2, f2[3] - 2, 4, 4));
				} else if (k == PathIterator.SEG_CUBICTO) {
					g2.draw(new Ellipse2D.Float(f2[4] - 2, f2[5] - 2, 4, 4));
				}
				i.next();
			}
			g2.setColor(new Color(0, 0, 0, 120));
			g2.draw(r);
		}
	};

	JButton showSample = new JButton("Show Sample");
	JComboBox<String> typeComboBox = new JComboBox<>();
	JSpinner shapeIndexSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100,
			1));
	JPanel inspector = new JPanel();
	JPanel demoPanel = new JPanel(new GridBagLayout());

	public ClipperDemo() {
		lowerControls.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		lowerControls.add(showSample, c);

		typeComboBox.addItem("Linear");
		typeComboBox.addItem("Quadratic");
		typeComboBox.addItem("Cubic");

		InspectorLayout layout = new InspectorGridBagLayout(inspector);

		layout.addRow(new JLabel("Segment Type:"), typeComboBox, false);
		layout.addRow(new JLabel("Shape:"), shapeIndexSpinner, false);

		typeComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				previewPanel.repaint();
			}

		});

		shapeIndexSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				previewPanel.repaint();
			}
		});

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		demoPanel.add(inspector, c);
		c.gridy++;
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 1;
		demoPanel.add(previewPanel, c);

		inspector.setOpaque(false);
		demoPanel.setOpaque(false);

		showSample.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RootPaneContainer rpc = (RootPaneContainer) SwingUtilities
						.getWindowAncestor(showSample);
				JFancyBox b = new JFancyBox(rpc, demoPanel);
				b.setVisible(true);
			}

		});
	}

	@Override
	public String getTitle() {
		return "Clipper Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new class that outperforms the Area class when clipping a shape to a rectangle.\n\nAs of this writing the Clipper class outperforms the Area class in every scenario/measurement.";
	}

	@Override
	public URL getHelpURL() {
		return ClipperDemo.class.getResource("clipperDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "clip", "performance", "graphics", "rectangle" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { Clipper.class };
	}

	private static final String CLIP_TIME = "Time";
	private static final String CLIP_MEMORY = "Memory";
	private static final String LABEL_AREA_LINEAR = "java.awt.geom.Area class (Linear)";
	private static final String LABEL_CLIPPER_LINEAR = "com.pump.geom.Clipper class (Linear)";
	private static final String LABEL_AREA_QUADRATIC = "java.awt.geom.Area class (Quadratic)";
	private static final String LABEL_CLIPPER_QUADRATIC = "com.pump.geom.Clipper class (Quadratic)";
	private static final String LABEL_AREA_CUBIC = "java.awt.geom.Area class (Cubic)";
	private static final String LABEL_CLIPPER_CUBIC = "com.pump.geom.Clipper class (Cubic)";
	private static final int SAMPLE_COUNT = 10;

	Map<String, Map<String, Long>> data;
	long[] timeSamples = new long[SAMPLE_COUNT];
	long[] memorySamples = new long[SAMPLE_COUNT];
	int sampleCtr = 0;

	@Override
	protected Map<String, Map<String, Long>> collectData(int... params)
			throws Exception {
		if (data == null) {
			data = new LinkedHashMap<>();
			data.put(CLIP_TIME, new LinkedHashMap<String, Long>());
			data.put(CLIP_MEMORY, new LinkedHashMap<String, Long>());
		}

		int sampleIndex = params[0];
		// invert so we go from slowest to fastest
		int degree = 2 - params[1];
		boolean useArea = params[2] == 0;

		Rectangle2D rect = new Rectangle(100, 100, 100, 100);
		Area rArea = new Area(rect);

		System.runFinalization();
		System.gc();
		System.runFinalization();
		System.gc();
		long time = System.currentTimeMillis();
		long memory = Runtime.getRuntime().freeMemory();
		for (int a = 0; a < p[degree].length; a++) {
			if (useArea) {
				Area area = new Area(p[degree][a]);
				area.intersect(rArea);
			} else {
				Clipper.clipToRect(p[degree][a], rect);
			}
		}
		time = System.currentTimeMillis() - time;
		memory = memory - Runtime.getRuntime().freeMemory();

		timeSamples[sampleIndex] = time;
		memorySamples[sampleIndex] = memory;

		if (sampleIndex == timeSamples.length - 1) {
			// we just populated all our samples, so let's record the latest
			// figures:
			Arrays.sort(timeSamples);
			Arrays.sort(memorySamples);
			String label;

			switch (degree) {
			case 1:
				label = useArea ? LABEL_AREA_QUADRATIC
						: LABEL_CLIPPER_QUADRATIC;
				break;
			case 2:
				label = useArea ? LABEL_AREA_CUBIC : LABEL_CLIPPER_CUBIC;
				break;
			default:
				label = useArea ? LABEL_AREA_LINEAR : LABEL_CLIPPER_LINEAR;
				break;
			}
			data.get(CLIP_TIME).put(label, timeSamples[timeSamples.length / 2]);
			data.get(CLIP_MEMORY).put(label,
					memorySamples[memorySamples.length / 2]);
		}

		return data;
	}

	@Override
	protected int[] getCollectDataParamLimits() {
		return new int[] { SAMPLE_COUNT, 3, 2 };
	}

}