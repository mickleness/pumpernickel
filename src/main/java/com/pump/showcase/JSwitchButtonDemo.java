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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.pump.icon.AndroidSwitchButtonIcon;
import com.pump.icon.AquaSwitchButtonIcon;
import com.pump.inspector.Inspector;
import com.pump.plaf.SwitchButtonUI;
import com.pump.swing.JSwitchButton;

public class JSwitchButtonDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JSwitchButton buttonA = new JSwitchButton("NIGHT SHIFT");
	JSwitchButton buttonB = new JSwitchButton("DO NOT DISTURB");

	JComboBox<String> iconType = new JComboBox<String>(new String[] { "Aqua",
			"Android" });
	JRadioButton enabledOn = new JRadioButton("Enabled", true);
	JRadioButton enabledOff = new JRadioButton("Disabled", false);

	public JSwitchButtonDemo() {
		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		examplePanel.add(buttonA, c);
		c.gridy++;
		examplePanel.add(buttonB, c);

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

		Inspector i = new Inspector(
				configurationPanel);
		i.addRow(new JLabel("Type:"), iconType);
		i.addRow(new JLabel("State:"), enabledOn, enabledOff);

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
		for (JSwitchButton b : new JSwitchButton[] { buttonA, buttonB }) {
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
		return "This demonstrates an alternative to checkboxes that is common on smartphones and tablets.";
	}

	@Override
	public URL getHelpURL() {
		return JSwitchButtonDemo.class.getResource("switchButtonDEmo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "button", "ux", "switch", "checkbox", "toggle",
				"Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { SwitchButtonUI.class };
	}

}