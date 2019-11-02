package com.pump.showcase;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.pump.icon.AndroidSwitchButtonIcon;
import com.pump.icon.AquaSwitchButtonIcon;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.plaf.QPanelUI;
import com.pump.plaf.SwitchButtonUI;
import com.pump.swing.SwitchButton;

public class SwitchButtonUIDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	SwitchButton buttonA = new SwitchButton("NIGHT SHIFT");
	SwitchButton buttonB = new SwitchButton("DO NOT DISTURB");

	JComboBox<String> iconType = new JComboBox<String>(new String[] { "Aqua",
			"Android" });
	JRadioButton enabledOn = new JRadioButton("On", true);
	JRadioButton enabledOff = new JRadioButton("Off", false);

	public SwitchButtonUIDemo() {
		JPanel example = new JPanel(new GridBagLayout());
		QPanelUI panelUI = new QPanelUI();
		example.setUI(panelUI);
		panelUI.setCornerSize(10);
		panelUI.setFillColor(Color.white);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		example.add(buttonA, c);
		c.gridy++;
		example.add(buttonB, c);

		buttonA.setIconTextGap(15);
		buttonB.setIconTextGap(15);
		buttonA.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonB.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonA.setHorizontalTextPosition(SwingConstants.LEFT);
		buttonB.setHorizontalTextPosition(SwingConstants.LEFT);

		Font font = buttonA.getFont();
		font = font.deriveFont(font.getSize2D() - 1).deriveFont(Font.BOLD);
		buttonA.setFont(font);
		buttonB.setFont(font);
		buttonA.setForeground(new Color(90, 90, 90));
		buttonB.setForeground(new Color(90, 90, 90));

		examplePanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		examplePanel.add(example, c);

		InspectorGridBagLayout i = new InspectorGridBagLayout(
				configurationPanel);
		i.addRow(new JLabel("Type:"), iconType);
		i.addRow(new JLabel("Enabled:"), enabledOn, enabledOff);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshExample();
			}

		};

		enabledOn.addActionListener(actionListener);
		enabledOff.addActionListener(actionListener);
		iconType.addActionListener(actionListener);

		ButtonGroup g = new ButtonGroup();
		g.add(enabledOn);
		g.add(enabledOff);

		refreshExample();
	}

	protected void refreshExample() {
		Icon icon = iconType.getSelectedIndex() == 0 ? new AquaSwitchButtonIcon()
				: new AndroidSwitchButtonIcon();
		for (SwitchButton b : new SwitchButton[] { buttonA, buttonB }) {
			b.setIcon(icon);
			b.setEnabled(enabledOn.isSelected());
		}
	}

	@Override
	public String getTitle() {
		return "SwitchButtonUI";
	}

	@Override
	public String getSummary() {
		return "This is an alternative to checkboxes that is common on smartphones and tablets.";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "button", "ux", "switch", "checkbox", "toggle" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { SwitchButtonUI.class };
	}

}
