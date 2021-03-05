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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.pump.inspector.Inspector;
import com.pump.swing.JColorPicker;

/**
 * This demos the JColorPicker.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/JColorPickerDemo.png"
 * alt="A screenshot of the JColorPickerDemo.">
 *
 */
public class JColorPickerDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JColorPicker picker = new JColorPicker(true, false);
	JComboBox<String> comboBox = new JComboBox<>();
	JCheckBox alphaCheckbox = new JCheckBox("Include Alpha");
	JCheckBox hsbCheckbox = new JCheckBox("Include HSB Values");
	JCheckBox rgbCheckbox = new JCheckBox("Include RGB Values");
	JCheckBox modeCheckbox = new JCheckBox("Include Mode Controls", true);
	JButton button = new JButton("Show Dialog");

	public JColorPickerDemo() {
		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(null, comboBox, false);
		inspector.addRow(null, alphaCheckbox, false);
		inspector.addRow(null, hsbCheckbox, false);
		inspector.addRow(null, rgbCheckbox, false);
		inspector.addRow(null, modeCheckbox, false);
		inspector.addRow(null, button, false);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		examplePanel.add(picker, c);
		c.gridx++;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		examplePanel.add(picker.getExpertControls(), c);

		picker.setPreferredSize(new Dimension(220, 200));

		comboBox.addItem("Hue");
		comboBox.addItem("Saturation");
		comboBox.addItem("Brightness");
		comboBox.addItem("Red");
		comboBox.addItem("Green");
		comboBox.addItem("Blue");

		ActionListener checkboxListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				if (src == alphaCheckbox) {
					picker.setOpacityVisible(alphaCheckbox.isSelected());
				} else if (src == hsbCheckbox) {
					picker.setHSBControlsVisible(hsbCheckbox.isSelected());
				} else if (src == rgbCheckbox) {
					picker.setRGBControlsVisible(rgbCheckbox.isSelected());
				} else if (src == modeCheckbox) {
					picker.setModeControlsVisible(modeCheckbox.isSelected());
				}
				revalidate();
				repaint();
			}
		};
		picker.setOpacityVisible(false);
		picker.setHSBControlsVisible(false);
		picker.setRGBControlsVisible(false);
		picker.setHexControlsVisible(false);
		picker.setPreviewSwatchVisible(false);

		picker.addPropertyChangeListener(JColorPicker.MODE_PROPERTY,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						int m = picker.getMode();
						if (m == JColorPicker.HUE) {
							comboBox.setSelectedIndex(0);
						} else if (m == JColorPicker.SAT) {
							comboBox.setSelectedIndex(1);
						} else if (m == JColorPicker.BRI) {
							comboBox.setSelectedIndex(2);
						} else if (m == JColorPicker.RED) {
							comboBox.setSelectedIndex(3);
						} else if (m == JColorPicker.GREEN) {
							comboBox.setSelectedIndex(4);
						} else if (m == JColorPicker.BLUE) {
							comboBox.setSelectedIndex(5);
						}
					}
				});

		alphaCheckbox.addActionListener(checkboxListener);
		hsbCheckbox.addActionListener(checkboxListener);
		rgbCheckbox.addActionListener(checkboxListener);
		modeCheckbox.addActionListener(checkboxListener);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = picker.getColor();
				Window window = SwingUtilities.getWindowAncestor(button);
				color = JColorPicker.showDialog(window, color, true);
				if (color != null)
					picker.setColor(color);
			}
		});

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = ((JComboBox) e.getSource()).getSelectedIndex();
				if (i == 0) {
					picker.setMode(JColorPicker.HUE);
				} else if (i == 1) {
					picker.setMode(JColorPicker.SAT);
				} else if (i == 2) {
					picker.setMode(JColorPicker.BRI);
				} else if (i == 3) {
					picker.setMode(JColorPicker.RED);
				} else if (i == 4) {
					picker.setMode(JColorPicker.GREEN);
				} else if (i == 5) {
					picker.setMode(JColorPicker.BLUE);
				}
			}
		});
		comboBox.setSelectedIndex(2);
	}

	@Override
	public String getTitle() {
		return "JColorPicker Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new Swing color-choosing widget that can be packaged either as a large dialog or a compact panel.";
	}

	@Override
	public URL getHelpURL() {
		return JColorPickerDemo.class.getResource("jcolorPickerDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "color", "ux", "gui", "dialog", "Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JColorPicker.class, JColorChooser.class };
	}

}