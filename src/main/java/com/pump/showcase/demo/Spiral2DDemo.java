/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import com.pump.geom.AbstractShape;
import com.pump.geom.ParametricPathIterator;
import com.pump.geom.Spiral2D;
import com.pump.inspector.Inspector;
import com.pump.swing.popover.JPopover;

/**
 * This demos the {@link Spiral2D} class
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/Spiral2DDemo.png"
 * alt="A screenshot of the Spiral2DDemo.">
 */
public class Spiral2DDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JLabel coilGapLabel = new JLabel("Coil Gap:");
	JSpinner coilGap = new JSpinner(new SpinnerNumberModel(50, 1, 300, 1));

	JLabel coilsLabel = new JLabel("Coils:");
	JSpinner coils = new JSpinner(new SpinnerNumberModel(1, .1, 100, .1));

	JLabel angleOffsetLabel = new JLabel("Angle Offset:");
	JLabel coilOffsetLabel = new JLabel("Coil Offset:");
	JSlider angleOffset = new ShowcaseSlider(0, 359);
	JSpinner coilOffset = new JSpinner(new SpinnerNumberModel(0, 0, 10, .01));

	JCheckBox outward = new JCheckBox("Outward");
	JCheckBox clockwise = new JCheckBox("Clockwise");

	PreviewPanel preview = new PreviewPanel();
	int timerChange = 0;

	public Spiral2DDemo() {
		super(true, true, true);

		JPopover.add(angleOffset, "\u00B0");

		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(coilGapLabel, coilGap);
		inspector.addRow(coilsLabel, coils);
		inspector.addRow(angleOffsetLabel, angleOffset);
		inspector.addRow(coilOffsetLabel, coilOffset);
		inspector.addRow(null, clockwise);
		inspector.addRow(null, outward);
		outward.setToolTipText("This should produce no visual difference.");

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		examplePanel.add(preview, c);

		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview.repaint();
			}
		};
		outward.addChangeListener(changeListener);
		angleOffset.addChangeListener(changeListener);
		coilOffset.addChangeListener(changeListener);
		coilGap.addChangeListener(changeListener);
		coils.addChangeListener(changeListener);
		clockwise.addChangeListener(changeListener);

		coilGap.setEditor(new JSpinner.NumberEditor(coilGap, "#.00"));
		coils.setEditor(new JSpinner.NumberEditor(coils, "#.00"));
		coilOffset.setEditor(new JSpinner.NumberEditor(coilOffset, "0.00"));
	}

	class PreviewPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		MouseInputAdapter mouseListener = new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				double coilGapValue = ((Number) coilGap.getValue())
						.doubleValue();
				double coilsValue = ((Number) coils.getValue()).doubleValue();
				double centerX = getWidth() / 2.0;
				double centerY = getHeight() / 2.0;
				double endX = e.getX();
				double endY = e.getY();
				Spiral2D spiral;
				if (e.isShiftDown()) {
					spiral = Spiral2D.createWithFixedCoilCount(centerX, centerY,
							endX, endY, coilsValue);
				} else {
					spiral = Spiral2D.createWithFixedCoilGap(centerX, centerY,
							endX, endY, coilGapValue);
				}
				setSpiral(spiral);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mousePressed(e);
			}
		};

		public PreviewPanel() {
			setPreferredSize(new Dimension(200, 200));
			addMouseListener(mouseListener);
			addMouseMotionListener(mouseListener);
			setOpaque(false);
		}

		protected void setSpiral(Spiral2D spiral) {
			double fraction = ((spiral.getAngularOffset() + 4 * Math.PI)
					/ (2 * Math.PI)) % 1.0;
			angleOffset.setValue((int) (fraction * 360));
			coilOffset.setValue(spiral.getCoilOffset());
			coilGap.setValue(new Double(spiral.getCoilGap()));
			coils.setValue(new Double(spiral.getCoils()));
			clockwise.setSelected(spiral.isClockwise());
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			double coilGapValue = ((Number) coilGap.getValue()).doubleValue();
			double coilsValue = ((Number) coils.getValue()).doubleValue();
			double angleOffsetValue = (angleOffset.getValue()
					- angleOffset.getMinimum()) * Math.PI * 2
					/ (angleOffset.getMaximum() - angleOffset.getMinimum());
			double coilOffsetValue = ((Number) coilOffset.getValue())
					.doubleValue();
			Spiral2D spiral = new Spiral2D(getWidth() / 2, getHeight() / 2,
					coilGapValue, coilsValue, angleOffsetValue, coilOffsetValue,
					clockwise.isSelected(), outward.isSelected());
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor(Color.blue);
			g2.draw(spiral);
		}
	}

	@Override
	public String getTitle() {
		return "Spiral2D Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new Shape class that represents a spiral.";
	}

	@Override
	public URL getHelpURL() {
		return Spiral2DDemo.class.getResource("spiral2Ddemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "spiral", "shape", "bezier", "parametric" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { Spiral2D.class, AbstractShape.class,
				ParametricPathIterator.class };
	}

}