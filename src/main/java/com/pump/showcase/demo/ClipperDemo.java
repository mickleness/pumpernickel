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
package com.pump.showcase.demo;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import com.pump.inspector.Inspector;
import com.pump.swing.JFancyBox;

/**
 * A simple demo program for the Clipper class.
 * <P>
 * This offers both a performance analysis and a GUI-based demo (click the "Show
 * Sample" button).
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/ClipperDemo.png"
 * alt="A screenshot of the ClipperDemo.">
 */
public class ClipperDemo extends ShowcaseChartDemo {
	private static final long serialVersionUID = 1L;

	static final GeneralPath[][] p = new GeneralPath[3][100];

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
			g2.transform(TransformUtils.createAffineTransform(
					new Rectangle(0, 0, 300, 300),
					new Rectangle(getWidth() / 2 - min / 2,
							getHeight() / 2 - min / 2, min, min)));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int shapeIndex = ((Number) shapeIndexSpinner.getValue()).intValue()
					- 1;
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
	JSpinner shapeIndexSpinner = new JSpinner(
			new SpinnerNumberModel(1, 1, 100, 1));
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

		Inspector layout = new Inspector(inspector);

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

	private static final int SAMPLE_COUNT = 10;

	private static final String OPERATION_CUBIC = "Cubic";
	private static final String OPERATION_QUADRATIC = "Quadratic";
	private static final String OPERATION_LINEAR = "Linear";

	private static final String IMPLEMENTATION_AREA = "Area";
	private static final String IMPLEMENTATION_CLIPPER = "Clipper";

	static class MeasurementRunnable extends TimeMemoryMeasurementRunnable {

		Rectangle2D rect = new Rectangle(100, 100, 100, 100);
		Area rArea = new Area(rect);

		public MeasurementRunnable(Map<String, Map<String, SampleSet>> data,
				String operation, String implementation) {
			super(data, operation, implementation);
		}

		@Override
		protected void runSample() {
			int degree;
			if (operation.equals(OPERATION_CUBIC)) {
				degree = 2;
			} else if (operation.equals(OPERATION_QUADRATIC)) {
				degree = 1;
			} else {
				degree = 0;
			}
			for (int a = 0; a < p[degree].length; a++) {
				if (implementation.equals(IMPLEMENTATION_AREA)) {
					Area area = new Area(p[degree][a]);
					area.intersect(rArea);
				} else {
					Clipper.clipToRect(p[degree][a], rect);
				}
			}
		}
	}

	@Override
	protected Collection<Runnable> getMeasurementRunnables(
			Map<String, Map<String, SampleSet>> data) {
		String[] operations = new String[] { OPERATION_CUBIC,
				OPERATION_QUADRATIC, OPERATION_LINEAR };
		String[] implementations = new String[] { IMPLEMENTATION_AREA,
				IMPLEMENTATION_CLIPPER };
		List<Runnable> returnValue = new ArrayList<>(
				SAMPLE_COUNT * operations.length * implementations.length);
		for (String operation : operations) {
			for (String implementation : implementations) {
				Runnable r = new MeasurementRunnable(data, operation,
						implementation);
				for (int sample = 0; sample < SAMPLE_COUNT; sample++) {
					returnValue.add(r);
				}
			}
		}
		return returnValue;
	}
}