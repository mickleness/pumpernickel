package com.pump.showcase;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.plaf.ColorWellUI;
import com.pump.plaf.ColorWellUI.ShowColorPaletteActionListener;
import com.pump.plaf.PaletteUI;
import com.pump.swing.JColorWell;
import com.pump.swing.JPalette;

public class JColorWellPaletteDemo extends JPanel implements ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JPalette palette = new JPalette();
	JPanel controls = new JPanel();
	JComboBox<String> typeComboBox = new JComboBox<>();
	JSlider cellSizeSlider = new JSlider(10, 50, 20);
	JComboBox<String> highlightComboBox = new JComboBox<>();
	JColorWell colorWell = new JColorWell();

	public JColorWellPaletteDemo() {
		super(new GridBagLayout());
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
		c.anchor = GridBagConstraints.NORTH;
		add(palette, c);

		InspectorLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("Type:"), typeComboBox);
		layout.addRow(new JLabel("Size:"), cellSizeSlider);
		layout.addRow(new JLabel("Highlight:"), highlightComboBox);
		layout.addRow(new JLabel("Color:"), colorWell);

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
	public URL getHelpURL() {
		return JColorWellPaletteDemo.class
				.getResource("jcolorWellPaletteDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "palette", "color" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { Color.class, JColorChooser.class, JPalette.class };
	}

}
