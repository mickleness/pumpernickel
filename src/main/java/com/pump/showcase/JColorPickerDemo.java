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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pump.plaf.QPanelUI;
import com.pump.swing.JColorPicker;

public class JColorPickerDemo extends JPanel implements ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JColorPicker picker = new JColorPicker(true, false);
	JComboBox<String> comboBox = new JComboBox<>();
	JCheckBox alphaCheckbox = new JCheckBox("Include Alpha");
	JCheckBox hsbCheckbox = new JCheckBox("Include HSB Values");
	JCheckBox rgbCheckbox = new JCheckBox("Include RGB Values");
	JCheckBox modeCheckbox = new JCheckBox("Include Mode Controls", true);
	JButton button = new JButton("Show Dialog");
	JPanel controls = new JPanel(new GridBagLayout());
	JPanel pickerContainer = new JPanel(new GridBagLayout());

	public JColorPickerDemo() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		controls.add(comboBox, c);
		c.gridy++;
		controls.add(alphaCheckbox, c);
		c.gridy++;
		controls.add(hsbCheckbox, c);
		c.gridy++;
		controls.add(rgbCheckbox, c);
		c.gridy++;
		controls.add(modeCheckbox, c);
		c.gridy++;
		controls.add(button, c);

		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(controls, c);
		c.gridy++;
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		add(pickerContainer, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		pickerContainer.add(picker, c);
		c.gridx++;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		pickerContainer.add(picker.getExpertControls(), c);
		pickerContainer.setUI(QPanelUI.createBoxUI());

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
		return "ColorPicker Demo";
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
		return new String[] { "color", "ux", "gui", "dialog" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JColorPicker.class, JColorChooser.class };
	}

}