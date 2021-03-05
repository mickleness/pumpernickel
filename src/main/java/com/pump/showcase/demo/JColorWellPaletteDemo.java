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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.inspector.Inspector;
import com.pump.plaf.ColorWellUI;
import com.pump.plaf.ColorWellUI.ShowColorPaletteActionListener;
import com.pump.plaf.PaletteUI;
import com.pump.swing.JColorWell;
import com.pump.swing.JPalette;

/**
 * This demos the JColorWell and JPalette.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/JColorWellPaletteDemo.png"
 * alt="A screenshot of the JColorWellPaletteDemo.">
 */
public class JColorWellPaletteDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JPalette palette = new JPalette();
	JComboBox<String> typeComboBox = new JComboBox<>();
	JSlider cellSizeSlider = new ShowcaseSlider(10, 50, 20);
	JComboBox<String> highlightComboBox = new JComboBox<>();
	JColorWell colorWell = new JColorWell();

	public JColorWellPaletteDemo() {
		addSliderPopover(cellSizeSlider, " pixels");

		Inspector layout1 = new Inspector(configurationPanel);
		layout1.addRow(new JLabel("Type:"), typeComboBox);
		layout1.addRow(new JLabel("Size:"), cellSizeSlider);
		layout1.addRow(new JLabel("Highlight:"), highlightComboBox);

		Inspector layout2 = new Inspector(examplePanel);
		layout2.addRow(new JLabel("Color Well:"), colorWell);
		layout2.addRow(new JLabel("Color Palette:"), palette);

		palette.setColorSelectionModel(colorWell.getColorSelectionModel());

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshPalette();
			}
		};
		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshPalette();
			}

		};
		highlightComboBox.addActionListener(actionListener);
		cellSizeSlider.addChangeListener(changeListener);
		typeComboBox.addActionListener(actionListener);

		typeComboBox.addItem("Flat UI");
		typeComboBox.addItem("Small");
		typeComboBox.addItem("Large");
		typeComboBox.addItem("Fluent");
		typeComboBox.addItem("Metro");

		highlightComboBox.addItem("None");
		highlightComboBox.addItem("Scribble");
		highlightComboBox.addItem("Bevel");

		palette.getColorSelectionModel().addChangeListener(changeListener);
		changeListener.stateChanged(null);
	}

	protected void refreshPalette() {
		palette.setUI(new PaletteUI());
		Color[][] colors;
		if (typeComboBox.getSelectedIndex() == 2) {
			colors = JPalette.get150Colors();
		} else if (typeComboBox.getSelectedIndex() == 1) {
			colors = JPalette.get54Colors();
		} else if (typeComboBox.getSelectedIndex() == 3) {
			colors = JPalette.getFluentColors();
		} else if (typeComboBox.getSelectedIndex() == 4) {
			colors = JPalette.getMetroColors();
		} else {
			colors = JPalette.getFlatUIColors();
		}
		palette.setColors(colors);

		if (highlightComboBox.getSelectedIndex() == 0) {
			palette.putClientProperty(PaletteUI.PROPERTY_HIGHLIGHT, null);
		} else if (highlightComboBox.getSelectedIndex() == 1) {
			palette.putClientProperty(PaletteUI.PROPERTY_HIGHLIGHT,
					PaletteUI.VALUE_HIGHLIGHT_SCRIBBLE);
		} else {
			palette.putClientProperty(PaletteUI.PROPERTY_HIGHLIGHT,
					PaletteUI.VALUE_HIGHLIGHT_BEVEL);
		}

		palette.setCellSize(cellSizeSlider.getValue());
		palette.setPreferredSize(palette.getUI().getPreferredSize(palette));

		colorWell.putClientProperty(ColorWellUI.DOWN_KEY_ACTION_PROPERTY,
				new ShowColorPaletteActionListener(colors));
		colorWell.putClientProperty(ColorWellUI.SINGLE_CLICK_ACTION_PROPERTY,
				new ShowColorPaletteActionListener(colors));
	}

	@Override
	public String getTitle() {
		return "JColorWell, JPalette Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates two new compact Swing components to select a color.";
	}

	@Override
	public URL getHelpURL() {
		return JColorWellPaletteDemo.class
				.getResource("jcolorWellPaletteDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "palette", "color", "Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { Color.class, JColorChooser.class, JPalette.class };
	}

}