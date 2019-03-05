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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.SliderUI;

import com.pump.awt.BristleStroke;
import com.pump.awt.BrushStroke;
import com.pump.awt.CalligraphyStroke;
import com.pump.awt.CharcoalStroke;
import com.pump.geom.BasicMouseSmoothing;
import com.pump.geom.MouseSmoothing;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.plaf.AngleSliderUI;
import com.pump.plaf.PlafPaintUtils;

/**
 * This demo shows a few new Stroke implementations and a model to smooth out
 * freehand drawing.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/StrokeMouseSmoothingDemo.png"
 * alt="A screenshot of the StrokeMouseSmoothingDemo.">
 */
public class StrokeMouseSmoothingDemo implements ShowcaseDemo {

	static class StrokeMouseSmoothingDemoPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		/**
		 * This is a nugget of information added to a MouseSmoothing obect.
		 */
		static class Input {
			float x, y;
			long when;

			Input(MouseEvent e) {
				x = e.getX();
				y = e.getY();
				when = e.getWhen();
			}
		}

		/**
		 * This demonstrates how shapes look if you do NOT smooth them. This
		 * makes no attempt to beautify the input data.
		 */
		static class RawMouseSmoothing implements MouseSmoothing {

			GeneralPath path = new GeneralPath();
			boolean empty = true;

			@Override
			public void add(float x, float y, long t) {
				if (empty) {
					path.moveTo(x, y);
					empty = false;
				} else {
					path.lineTo(x, y);
				}
			}

			@Override
			public void reset() {
				path.reset();
				empty = true;
			}

			@Override
			public GeneralPath getShape() {
				return new GeneralPath(path);
			}

			@Override
			public void getShape(GeneralPath path) {
				path.append(path, false);
			}

			@Override
			public boolean isEmpty() {
				return empty;
			}

		}

		private class DrawingPanel extends JPanel {
			private static final long serialVersionUID = 1L;

			List<List<Input>> inputs = new ArrayList<>();

			List<Shape> shapes = new ArrayList<Shape>();
			JButton clearButton = new JButton("Clear");

			MouseInputAdapter mouseListener = new MouseInputAdapter() {
				BasicMouseSmoothing mouseSmoothing;

				@Override
				public void mousePressed(MouseEvent e) {
					mouseSmoothing = new BasicMouseSmoothing();
					List<Input> newInputs = new ArrayList<>();
					newInputs.add(new Input(e));
					inputs.add(newInputs);
					mouseSmoothing.add(e.getX(), e.getY(), e.getWhen());
					shapes.add(mouseSmoothing.getShape());
					refreshDrawingPanel();
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					if (mouseSmoothing != null) {
						inputs.get(inputs.size() - 1).add(new Input(e));
						mouseSmoothing.add(e.getX(), e.getY(), e.getWhen());
						shapes.set(shapes.size() - 1, mouseSmoothing.getShape());
						refreshDrawingPanel();
					}
				}

			};

			DrawingPanel() {
				addMouseListener(mouseListener);
				addMouseMotionListener(mouseListener);
				setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				c.weighty = 1;
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.SOUTHEAST;
				c.insets = new Insets(5, 5, 5, 5);
				add(clearButton, c);

				clearButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						shapes.clear();
						refreshDrawingPanel();
					}

				});

				refreshDrawingPanel();
			}

			protected void refreshDrawingPanel() {
				clearButton.setVisible(!shapes.isEmpty());
				repaint();
			}

			@Override
			protected void paintComponent(Graphics g0) {
				Graphics2D g = (Graphics2D) g0;
				g0.setColor(Color.white);
				g0.fillRect(0, 0, getWidth(), getHeight());
				g0.setColor(Color.black);

				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
						RenderingHints.VALUE_STROKE_PURE);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				if (shapes.size() == 0) {
					Font font = UIManager.getFont("Label.font");
					g.setColor(Color.darkGray);
					String str = "Click and drag here to draw.";
					PlafPaintUtils.paintCenteredString(g, str, font,
							getWidth() / 2, getHeight() / 2);
				}
				g.setStroke(createStroke());
				for (Shape shape : shapes) {
					g.draw(shape);
				}
			}

			public void setSmoothingActive(boolean active) {
				shapes.clear();
				for (List<Input> s : inputs) {
					MouseSmoothing ms = active ? new BasicMouseSmoothing()
							: new RawMouseSmoothing();
					for (Input i : s) {
						ms.add(i.x, i.y, i.when);
					}
					shapes.add(ms.getShape());
				}
				refreshDrawingPanel();
			}
		}

		JPanel controls = new JPanel();
		DrawingPanel drawingPanel = new DrawingPanel();

		JLabel thicknessLabel = new JLabel("Thickness:");
		JLabel crackSizeLabel = new JLabel("Crack Size:");
		JLabel angleLabel = new JLabel("Angle:");
		JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(5, .05, 20,
				1));
		JSlider crackSizeSlider = new JSlider(0, 100, 45);
		JSlider thicknessSlider = new JSlider(0, 100, 45);
		JSlider angleSlider = new JSlider();
		JComboBox<String> strokeTypeComboBox = new JComboBox<>();
		JRadioButton smoothingOnButton = new JRadioButton("On", true);
		JRadioButton smoothingOffButton = new JRadioButton("Off", false);

		public StrokeMouseSmoothingDemoPanel() {
			InspectorLayout layout = new InspectorGridBagLayout(controls);
			layout.addRow(new JLabel("Smoothing:"), smoothingOnButton,
					smoothingOffButton);
			layout.addRow(new JLabel("Stroke Type:"), strokeTypeComboBox);
			layout.addRow(new JLabel("Width:"), widthSpinner, false);
			layout.addRow(thicknessLabel, thicknessSlider, false);
			layout.addRow(crackSizeLabel, crackSizeSlider, false);
			layout.addRow(angleLabel, angleSlider, false);

			ButtonGroup g = new ButtonGroup();
			g.add(smoothingOnButton);
			g.add(smoothingOffButton);

			angleSlider.setUI((SliderUI) AngleSliderUI.createUI(angleSlider));

			ChangeListener changeListener = new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					drawingPanel.repaint();
				}

			};

			widthSpinner.addChangeListener(changeListener);
			thicknessSlider.addChangeListener(changeListener);
			crackSizeSlider.addChangeListener(changeListener);
			angleSlider.addChangeListener(changeListener);

			strokeTypeComboBox.addItem("BasicStroke");
			strokeTypeComboBox.addItem("BristleStroke");
			strokeTypeComboBox.addItem("BrushStroke");
			strokeTypeComboBox.addItem("CalligraphyStroke");
			strokeTypeComboBox.addItem("CharcoalStroke");

			strokeTypeComboBox.setSelectedItem("CalligraphyStroke");

			strokeTypeComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					refresh();
				}

			});

			ActionListener smoothingListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					drawingPanel.setSmoothingActive(smoothingOnButton
							.isSelected());
				}

			};
			smoothingOnButton.addActionListener(smoothingListener);
			smoothingOffButton.addActionListener(smoothingListener);

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0;
			c.fill = GridBagConstraints.BOTH;
			add(controls, c);
			c.gridy++;
			c.weighty = 1;
			c.insets = new Insets(20, 20, 20, 20);
			add(drawingPanel, c);

			drawingPanel.setBorder(new LineBorder(Color.gray));

			refresh();
		}

		private void refresh() {
			if ("BristleStroke".equals(strokeTypeComboBox.getSelectedItem())
					|| "BrushStroke".equals(strokeTypeComboBox
							.getSelectedItem())) {
				angleLabel.setEnabled(false);
				angleSlider.setEnabled(false);
				crackSizeLabel.setEnabled(false);
				crackSizeSlider.setEnabled(false);
				thicknessLabel.setEnabled(true);
				thicknessSlider.setEnabled(true);
			} else if ("CalligraphyStroke".equals(strokeTypeComboBox
					.getSelectedItem())) {
				angleLabel.setEnabled(true);
				angleSlider.setEnabled(true);
				crackSizeLabel.setEnabled(false);
				crackSizeSlider.setEnabled(false);
				thicknessLabel.setEnabled(false);
				thicknessSlider.setEnabled(false);
			} else if ("CharcoalStroke".equals(strokeTypeComboBox
					.getSelectedItem())) {
				angleLabel.setEnabled(true);
				angleSlider.setEnabled(true);
				crackSizeLabel.setEnabled(true);
				crackSizeSlider.setEnabled(true);
				thicknessLabel.setEnabled(false);
				thicknessSlider.setEnabled(false);
			} else if ("BasicStroke".equals(strokeTypeComboBox
					.getSelectedItem())) {
				angleLabel.setEnabled(false);
				angleSlider.setEnabled(false);
				crackSizeLabel.setEnabled(false);
				crackSizeSlider.setEnabled(false);
				thicknessLabel.setEnabled(false);
				thicknessSlider.setEnabled(false);
			}
			drawingPanel.repaint();
		}

		public Stroke createStroke() {
			float radians = ((Number) angleSlider.getValue()).floatValue();
			radians = (float) (radians * 2 * Math.PI / (angleSlider
					.getMaximum() - angleSlider.getMinimum()));

			if ("BristleStroke".equals(strokeTypeComboBox.getSelectedItem())) {
				return new BristleStroke(
						((Number) widthSpinner.getValue()).floatValue(),
						((Number) thicknessSlider.getValue()).floatValue() / 100f);
			} else if ("BrushStroke".equals(strokeTypeComboBox
					.getSelectedItem())) {
				return new BrushStroke(
						((Number) widthSpinner.getValue()).floatValue(),
						((Number) thicknessSlider.getValue()).floatValue() / 100f);
			} else if ("CalligraphyStroke".equals(strokeTypeComboBox
					.getSelectedItem())) {
				return new CalligraphyStroke(
						((Number) widthSpinner.getValue()).floatValue(),
						radians);
			} else if ("CharcoalStroke".equals(strokeTypeComboBox
					.getSelectedItem())) {
				radians = (float) (Math.PI / 2 + radians);
				BasicStroke bs = new BasicStroke(
						((Number) widthSpinner.getValue()).floatValue());
				return new CharcoalStroke(
						bs,
						((Number) crackSizeSlider.getValue()).floatValue() / 100f,
						radians, 0);
			} else {
				return new BasicStroke(
						((Number) widthSpinner.getValue()).floatValue());
			}
		}
	}

	@Override
	public JPanel createPanel(PumpernickelShowcaseApp psa) {
		return new StrokeMouseSmoothingDemoPanel();
	}

	@Override
	public String getTitle() {
		return "Strokes, MouseSmoothing";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a set new strokes and a freehand pencil drawing tool.";
	}

	@Override
	public URL getHelpURL() {
		return StrokeMouseSmoothingDemo.class
				.getResource("strokeMouseSmoothingDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "pencil", "freehand", "mouse", "smooth",
				"stroke", "render", "draw", "calligraphy", "brush", "bristle",
				"charcoal" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { BristleStroke.class, BrushStroke.class,
				CalligraphyStroke.class, CharcoalStroke.class,
				BasicMouseSmoothing.class };
	}
}