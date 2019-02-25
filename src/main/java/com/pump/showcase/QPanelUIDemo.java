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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.net.URL;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.plaf.QPanelUI;
import com.pump.plaf.QPanelUI.CalloutType;
import com.pump.swing.JColorWell;

public class QPanelUIDemo extends JPanel implements ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JToggleButton presetBox = new JToggleButton("Box");
	JToggleButton presetToolTip = new JToggleButton("ToolTip");
	JColorWell fillColor1 = new JColorWell(new Color(0xffffff));
	JColorWell fillColor2 = new JColorWell(new Color(0xf9f9f9));
	JColorWell strokeColor1 = new JColorWell(new Color(0xffffff));
	JColorWell strokeColor2 = new JColorWell(new Color(0xf3f3f3));
	JRadioButton orientationNone = new JRadioButton("None", true);
	JRadioButton orientationVertical = new JRadioButton("Vertical", false);
	JRadioButton orientationHorizontal = new JRadioButton("Horizontal", false);
	JSlider cornerSize = new JSlider(0, 100, 10);
	JSlider calloutSize = new JSlider(0, 20, 10);
	JSlider shadowSize = new JSlider(0, 20, 5);
	JPanel controls = new JPanel();
	JComboBox<CalloutType> calloutTypeComboBox = new JComboBox<>();
	boolean rolloverPreview = false;

	JPanel preview = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g0) {
			super.paint(g0);
			if (rolloverPreview) {
				Graphics2D g = (Graphics2D) g0.create();
				g.setColor(new Color(0, 0, 0, 100));
				Insets i = getInsets();

				GeneralPath shape = new GeneralPath();
				shape.moveTo(getWidth() / 3 - 4, 0);
				shape.lineTo(getWidth() / 3 - 0, 0);
				shape.lineTo(getWidth() / 3 - 0, getHeight() - 1);
				shape.lineTo(getWidth() / 3 - 4, getHeight() - 1);
				g.draw(shape);
				g.drawLine(getWidth() / 3 - 2, i.top, getWidth() / 3, i.top);
				g.drawLine(getWidth() / 3 - 2, getHeight() - i.bottom,
						getWidth() / 3, getHeight() - i.bottom);

				shape.reset();
				shape.moveTo(0, getHeight() / 3 - 4);
				shape.lineTo(0, getHeight() / 3 - 0);
				shape.lineTo(getWidth() - 1, getHeight() / 3 - 0);
				shape.lineTo(getWidth() - 1, getHeight() / 3 - 4);
				g.draw(shape);
				g.drawLine(i.left, getHeight() / 3 - 2, i.left, getHeight() / 3);
				g.drawLine(getWidth() - i.right, getHeight() / 3 - 2,
						getWidth() - i.right, getHeight() / 3);
			}
		}

	};

	JLabel fillColor1Label = new JLabel();
	JLabel fillColor2Label = new JLabel("Fill Color 2:");
	JLabel strokeColor1Label = new JLabel();
	JLabel strokeColor2Label = new JLabel("Stroke Color 2:");

	public QPanelUIDemo() {
		InspectorLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("Preset:"), presetBox, presetToolTip);
		layout.addRow(fillColor1Label, fillColor1, false);
		layout.addRow(fillColor2Label, fillColor2, false);
		layout.addRow(strokeColor1Label, strokeColor1, false);
		layout.addRow(strokeColor2Label, strokeColor2, false);
		layout.addRow(new JLabel("Gradient Orientation:"), orientationNone,
				orientationVertical, orientationHorizontal);
		layout.addRow(new JLabel("Corner Size:"), cornerSize, false);
		layout.addRow(new JLabel("Callout Type:"), calloutTypeComboBox, false);
		layout.addRow(new JLabel("Callout Size:"), calloutSize, false);
		layout.addRow(new JLabel("Shadow Size:"), shadowSize, false);

		for (CalloutType t : CalloutType.values()) {
			calloutTypeComboBox.addItem(t);
		}

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDemo();
			}
		};

		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateDemo();
			}

		};

		presetBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateControlsForUI(QPanelUI.createBoxUI());
			}
		});

		presetToolTip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateControlsForUI(QPanelUI.createToolTipUI());
			}
		});
		presetToolTip.addActionListener(actionListener);
		calloutTypeComboBox.addActionListener(actionListener);
		orientationNone.addActionListener(actionListener);
		orientationHorizontal.addActionListener(actionListener);
		orientationVertical.addActionListener(actionListener);
		fillColor1.getColorSelectionModel().addChangeListener(changeListener);
		fillColor2.getColorSelectionModel().addChangeListener(changeListener);
		strokeColor1.getColorSelectionModel().addChangeListener(changeListener);
		strokeColor2.getColorSelectionModel().addChangeListener(changeListener);
		cornerSize.addChangeListener(changeListener);
		calloutSize.addChangeListener(changeListener);
		shadowSize.addChangeListener(changeListener);

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
		c.fill = GridBagConstraints.NONE;
		add(preview, c);

		ButtonGroup g = new ButtonGroup();
		g.add(orientationNone);
		g.add(orientationHorizontal);
		g.add(orientationVertical);

		preview.setLayout(new GridBagLayout());
		JPanel fluff = new JPanel();
		fluff.setOpaque(false);
		fluff.setPreferredSize(new Dimension(100, 100));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		preview.add(fluff, c);

		QPanelUI ui = new QPanelUI();
		updateUIFromControls(ui);
		preview.setUI(ui);

		preview.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				rolloverPreview = true;
				preview.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				rolloverPreview = false;
				preview.repaint();
			}

		});
	}

	/**
	 * Update all GUI controls to describe a given QPanelUI.
	 */
	protected void updateControlsForUI(QPanelUI ui) {
		fillColor1.getColorSelectionModel()
				.setSelectedColor(ui.getFillColor1());
		fillColor2.getColorSelectionModel()
				.setSelectedColor(ui.getFillColor2());
		strokeColor1.getColorSelectionModel().setSelectedColor(
				ui.getStrokeColor1());
		strokeColor2.getColorSelectionModel().setSelectedColor(
				ui.getStrokeColor2());
		if (Objects.equals(ui.getFillColor1(), ui.getFillColor2())
				&& Objects.equals(ui.getStrokeColor1(), ui.getStrokeColor2())) {
			orientationNone.setSelected(true);
			orientationHorizontal.setSelected(false);
			orientationVertical.setSelected(false);
		} else {
			int orientation = ui.getGradientOrientation();
			orientationNone.setSelected(false);
			orientationHorizontal
					.setSelected(orientation == SwingConstants.HORIZONTAL);
			orientationVertical
					.setSelected(orientation == SwingConstants.VERTICAL);
		}
		cornerSize.setValue((int) (ui.getCornerSize() * 2));
		calloutTypeComboBox.setSelectedItem(ui.getCalloutType());
		calloutSize.setValue(ui.getCalloutSize());
		shadowSize.setValue(ui.getShadowSize());
	}

	protected void updateDemo() {
		QPanelUI ui = (QPanelUI) preview.getUI();
		updateUIFromControls(ui);
	}

	/**
	 * Populate all the detalis of a QPanelUI based on the GUI controls in this
	 * demo.
	 */
	protected void updateUIFromControls(QPanelUI ui) {
		Color f1 = fillColor1.getColorSelectionModel().getSelectedColor();
		Color f2;
		Color s1 = strokeColor1.getColorSelectionModel().getSelectedColor();
		Color s2;
		if (orientationNone.isSelected()) {
			fillColor2.setVisible(false);
			strokeColor2.setVisible(false);
			fillColor2Label.setVisible(false);
			strokeColor2Label.setVisible(false);
			f2 = f1;
			s2 = s1;
			fillColor1Label.setText("Fill Color:");
			strokeColor1Label.setText("Stroke Color:");
		} else {
			fillColor2.setVisible(true);
			strokeColor2.setVisible(true);
			fillColor2Label.setVisible(true);
			strokeColor2Label.setVisible(true);
			f2 = fillColor2.getColorSelectionModel().getSelectedColor();
			s2 = strokeColor2.getColorSelectionModel().getSelectedColor();
			fillColor1Label.setText("Fill Color 1:");
			strokeColor1Label.setText("Stroke Color 2:");
		}

		ui.setFillColor1(f1);
		ui.setFillColor2(f2);
		if (orientationHorizontal.isSelected()) {
			ui.setGradientOrientation(SwingConstants.HORIZONTAL);
		} else {
			ui.setGradientOrientation(SwingConstants.VERTICAL);
		}
		ui.setStrokeColor1(s1);
		ui.setStrokeColor2(s2);
		ui.setCalloutType((CalloutType) calloutTypeComboBox.getSelectedItem());

		float s = cornerSize.getValue() / 2f;
		ui.setCornerSize(s);

		ui.setCalloutSize(calloutSize.getValue());
		ui.setShadowSize(shadowSize.getValue());

		presetBox.setSelected(ui.equals(QPanelUI.createBoxUI()));
		presetToolTip.setSelected(ui.equals(QPanelUI.createToolTipUI()));
	}

	@Override
	public String getTitle() {
		return "QPanelUI Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new PanelUI supporting optional gradients and a callout.";
	}

	@Override
	public URL getHelpURL() {
		return QPanelUIDemo.class.getResource("qpanelUIDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "panel", "ui", "ux", "flat", "gradient",
				"callout", "border", "swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { QPanelUI.class };
	}
}