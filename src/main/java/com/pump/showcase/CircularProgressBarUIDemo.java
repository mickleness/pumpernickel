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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.inspector.InspectorGridBagLayout;
import com.pump.plaf.CircularProgressBarUI;
import com.pump.swing.JColorWell;

public class CircularProgressBarUIDemo extends JPanel implements ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JPanel controls = new JPanel();
	JSlider sizeSlider = new JSlider(10, 120, 90);
	JRadioButton indeterminateButton = new JRadioButton("Indeterminate", false);
	JRadioButton determinateButton = new JRadioButton("Determinate", true);
	JProgressBar progressBar = new JProgressBar(0, 100);
	JSpinner progressSpinner = new JSpinner(
			new SpinnerNumberModel(0, 0, 100, 5));
	JRadioButton animateOnButton = new JRadioButton("On");
	JRadioButton animateOffButton = new JRadioButton("Off", true);
	JPanel progressBarContainer = new JPanel();
	JColorWell foregroundColor = new JColorWell(
			CircularProgressBarUI.COLOR_DEFAULT_FOREGROUND);
	JColorWell backgroundColor = new JColorWell(
			CircularProgressBarUI.COLOR_DEFAULT_BACKGROUND);
	JRadioButton stringOnButton = new JRadioButton("On", true);
	JRadioButton stringOffButton = new JRadioButton("Off", false);
	JCheckBox pulseCheckBox = new JCheckBox("Pulse", true);
	JCheckBox sparkCheckBox = new JCheckBox("Spark", true);
	JCheckBox transitionCheckBox = new JCheckBox("Transition", true);
	JCheckBox accelerateCheckBox = new JCheckBox("Accelerate", false);
	JSlider strokeSlider = new JSlider(1, 50, 4);
	JCheckBox strokeCheckBox = new JCheckBox("Custom Stroke Width:");

	ChangeListener sizeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int i = sizeSlider.getValue();
			progressBar.setPreferredSize(new Dimension(i, i));
			progressBar.getParent().revalidate();
		}

	};

	ActionListener determinateListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			progressBar.setIndeterminate(indeterminateButton.isSelected());
		}

	};

	ActionListener stringListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			progressBar.setStringPainted(stringOnButton.isSelected());
		}

	};

	ActionListener effectsListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			progressBar.putClientProperty(
					CircularProgressBarUI.PROPERTY_PULSE_COMPLETION_ACTIVE,
					pulseCheckBox.isSelected());
			progressBar.putClientProperty(
					CircularProgressBarUI.PROPERTY_SPARK_ACTIVE,
					sparkCheckBox.isSelected());
			progressBar.putClientProperty(
					CircularProgressBarUI.PROPERTY_ACCELERATE,
					accelerateCheckBox.isSelected());
			progressBar.putClientProperty(
					CircularProgressBarUI.PROPERTY_TRANSITION,
					transitionCheckBox.isSelected());
		}

	};

	ActionListener animateListener = new ActionListener() {

		/**
		 * This increments the progress bar until we reach 100%, then we wait 2
		 * seconds and restart.
		 */
		Timer timer = new Timer(15, new ActionListener() {
			long timeWhen100Reached = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = ((Number) progressSpinner.getValue()).intValue();
				if (i == 100) {
					if (timeWhen100Reached == 0) {
						timeWhen100Reached = System.currentTimeMillis();
					} else {
						long elapsed = System.currentTimeMillis()
								- timeWhen100Reached;
						if (elapsed > 2000) {
							timeWhen100Reached = 0;
							i = 0;
						}
					}
				} else {
					i += 1;
				}
				progressSpinner.setValue(i);
			}

		});

		@Override
		public void actionPerformed(ActionEvent e) {
			if (animateOnButton.isSelected()) {
				if (!timer.isRunning())
					timer.start();
			} else {
				if (timer.isRunning())
					timer.stop();
			}
		}
	};

	ChangeListener spinnerListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int i = ((Number) progressSpinner.getValue()).intValue();
			progressBar.setValue(i);
		}

	};

	ChangeListener colorListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			progressBar.setForeground(foregroundColor.getColorSelectionModel()
					.getSelectedColor());
			progressBar.setBackground(backgroundColor.getColorSelectionModel()
					.getSelectedColor());
		}

	};

	ChangeListener strokeSliderListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			Number w;
			if (strokeCheckBox.isSelected()) {
				w = strokeSlider.getValue();
			} else {
				w = null;
			}
			progressBar.putClientProperty(
					CircularProgressBarUI.PROPERTY_STROKE_WIDTH, w);
		}

	};

	ActionListener strokeCheckboxListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			strokeSlider.setEnabled(strokeCheckBox.isSelected());
			strokeSliderListener.stateChanged(null);
		}

	};

	public CircularProgressBarUIDemo() {
		super(new GridBagLayout());

		InspectorGridBagLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("Size:"), sizeSlider, true);
		layout.addRow(new JLabel("Style:"), indeterminateButton,
				determinateButton);
		layout.addRow(new JLabel("Value:"), progressSpinner, false);
		layout.addRow(new JLabel("String Painted:"), stringOnButton,
				stringOffButton);
		layout.addRow(new JLabel("Animate Value:"), animateOnButton,
				animateOffButton);
		layout.addRow(new JLabel("Foreground:"), foregroundColor, false);
		layout.addRow(new JLabel("Background:"), backgroundColor, false);
		layout.addRow(new JLabel("Effects:"), pulseCheckBox, transitionCheckBox);
		layout.addRow(null, sparkCheckBox, accelerateCheckBox);
		layout.addRow(strokeCheckBox, strokeSlider, true);

		ButtonGroup g1 = new ButtonGroup();
		g1.add(indeterminateButton);
		g1.add(determinateButton);

		ButtonGroup g2 = new ButtonGroup();
		g2.add(animateOnButton);
		g2.add(animateOffButton);

		ButtonGroup g3 = new ButtonGroup();
		g3.add(stringOnButton);
		g3.add(stringOffButton);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(controls, c);
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy++;
		c.weighty++;
		add(progressBarContainer, c);

		progressBarContainer.add(progressBar);

		progressBar.setUI(new CircularProgressBarUI());
		progressBar.setValue(33);

		sizeSlider.addChangeListener(sizeListener);
		sizeListener.stateChanged(null);

		indeterminateButton.addActionListener(determinateListener);
		determinateButton.addActionListener(determinateListener);
		determinateListener.actionPerformed(null);

		animateOnButton.addActionListener(animateListener);
		animateOffButton.addActionListener(animateListener);
		animateListener.actionPerformed(null);

		stringOnButton.addActionListener(stringListener);
		stringOffButton.addActionListener(stringListener);
		stringListener.actionPerformed(null);

		backgroundColor.getColorSelectionModel().addChangeListener(
				colorListener);
		foregroundColor.getColorSelectionModel().addChangeListener(
				colorListener);
		colorListener.stateChanged(null);

		strokeSlider.addChangeListener(strokeSliderListener);
		strokeCheckBox.addActionListener(strokeCheckboxListener);
		strokeCheckboxListener.actionPerformed(null);

		pulseCheckBox.addActionListener(effectsListener);
		sparkCheckBox.addActionListener(effectsListener);
		accelerateCheckBox.addActionListener(effectsListener);
		transitionCheckBox.addActionListener(effectsListener);
		// do NOT call actionPerformed(null) here; prove in the demo that the
		// defaults are interpreted as true when undefined.
		// effectsListener.actionPerformed(null);

		progressSpinner.addChangeListener(spinnerListener);
	}

	@Override
	public String getTitle() {
		return "CircularProgressBarUI Demo";
	}

	@Override
	public URL getHelpURL() {
		return getClass().getResource("circularProgressBarUIDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "ux", "ui", "gui", "progress-bar", "determinate",
				"indeterminate", "progress", "feedback" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { CircularProgressBarUI.class, JProgressBar.class };
	}
}