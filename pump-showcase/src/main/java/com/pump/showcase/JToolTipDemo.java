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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.awt.HSLColor;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.plaf.QPanelUI.CalloutType;
import com.pump.swing.ColorWell;
import com.pump.swing.FontComboBox;
import com.pump.swing.QPopup;
import com.pump.swing.QPopupFactory;

public class JToolTipDemo extends JPanel implements ShowcaseDemo {

	private static final long serialVersionUID = 1L;

	private static final String ANY = "Any";
	private static final String NONE = "None";

	QPopupFactory qPopupFactory;

	JPanel controls = new JPanel();
	JComboBox<String> toolTipTypeComboBox = new JComboBox<>();
	JLabel fontLabel = new JLabel("Font:");
	JLabel fontSizeLabel = new JLabel("Font Size:");
	FontComboBox fontComboBox = new FontComboBox();
	JSlider fontSizeSlider = new JSlider(10, 20, 12);
	JLabel colorLabel = new JLabel("Color:");
	ColorWell color = new ColorWell(true, Color.white);
	JLabel calloutTypeLabel = new JLabel("Callout:");
	JButton sampleButton = new JButton("Sample Button");
	JComboBox<String> calloutTypeComboBox = new JComboBox<>();

	public JToolTipDemo() {

		toolTipTypeComboBox.addItem("Use QPopupFactory");
		toolTipTypeComboBox.addItem("Use Default PopupFactory");
		toolTipTypeComboBox.addItem("Off");

		calloutTypeComboBox.addItem(ANY);
		calloutTypeComboBox.addItem(NONE);
		for (CalloutType t : CalloutType.values()) {
			calloutTypeComboBox.addItem(t.name());
		}

		InspectorLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("Tooltip Type:"), toolTipTypeComboBox, false);
		layout.addRow(fontLabel, fontComboBox, false);
		layout.addRow(fontSizeLabel, fontSizeSlider, false);
		layout.addRow(colorLabel, color, false);
		layout.addRow(calloutTypeLabel, calloutTypeComboBox);

		Font font = UIManager.getFont("ToolTip.font");
		fontComboBox.selectFont(font.getName());
		fontSizeSlider.setValue(font.getSize());

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshUI();
			}

		};
		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshUI();
			}

		};

		calloutTypeComboBox.addActionListener(actionListener);
		color.addChangeListener(changeListener);
		fontSizeSlider.addChangeListener(changeListener);
		fontComboBox.addActionListener(actionListener);
		toolTipTypeComboBox.addActionListener(actionListener);

		sampleButton.setToolTipText("Sample ToolTip");

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		add(controls, c);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(10, 10, 10, 10);
		add(sampleButton, c);

		refreshUI();
	}

	@Override
	public String getTitle() {
		return "JToolTip Demo";
	}

	@Override
	public URL getHelpURL() {
		return JToolTipDemo.class.getResource("jtooltipDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "tooltip", "popup", "callout", "gui", "ui", "ux" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JToolTip.class, PopupFactory.class, Popup.class };
	}

	@Override
	public boolean isSeparatorVisible() {
		return true;
	}

	protected void refreshUI() {
		if (qPopupFactory == null) {
			qPopupFactory = new QPopupFactory(PopupFactory.getSharedInstance());
		}
		if (toolTipTypeComboBox.getSelectedIndex() == 1) {
			PopupFactory.setSharedInstance(qPopupFactory.getParentDelegate());
		} else {
			qPopupFactory.setToolTipCallout(!NONE.equals(calloutTypeComboBox
					.getSelectedItem()));
			PopupFactory.setSharedInstance(qPopupFactory);
		}

		boolean tooltipsActive = toolTipTypeComboBox.getSelectedIndex() != 2;
		if (tooltipsActive) {
			Font font = (Font) fontComboBox.getSelectedItem();
			float size = fontSizeSlider.getValue();
			font = font.deriveFont(size);
			UIManager.getDefaults().put("ToolTip.font", font);

			Color background = color.getColor();
			Color foreground;
			float[] hsl = HSLColor.fromRGB(background, null);
			if (hsl[2] < .5) {
				foreground = Color.white;
			} else {
				foreground = Color.black;
			}

			UIManager.getDefaults().put("ToolTip.background", background);
			UIManager.getDefaults().put("ToolTip.foreground", foreground);
		}

		ToolTipManager.sharedInstance().setEnabled(
				toolTipTypeComboBox.getSelectedIndex() != 2);

		colorLabel.setVisible(tooltipsActive);
		color.setVisible(tooltipsActive);
		fontSizeLabel.setVisible(tooltipsActive);
		fontSizeSlider.setVisible(tooltipsActive);
		fontLabel.setVisible(tooltipsActive);
		fontComboBox.setVisible(tooltipsActive);

		calloutTypeLabel
				.setVisible(toolTipTypeComboBox.getSelectedIndex() == 0);
		calloutTypeComboBox
				.setVisible(toolTipTypeComboBox.getSelectedIndex() == 0);

		CalloutType type = null;
		try {
			type = CalloutType.valueOf((String) calloutTypeComboBox
					.getSelectedItem());
		} catch (Exception e) {
		}
		sampleButton.putClientProperty(QPopup.PROPERTY_CALLOUT_TYPE, type);
	}

}