package com.pump.showcase;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.pump.plaf.QPanelUI;
import com.pump.plaf.SwitchButtonUI;
import com.pump.swing.SwitchButton;

public class SwitchButtonUIDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	SwitchButton buttonA = new SwitchButton("NIGHT SHIFT");
	SwitchButton buttonB = new SwitchButton("DO NOT DISTURB");

	public SwitchButtonUIDemo() {
		JPanel panel = new JPanel(new GridBagLayout());
		QPanelUI panelUI = new QPanelUI();
		panel.setUI(panelUI);
		panelUI.setCornerSize(10);
		panelUI.setFillColor(Color.white);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(buttonA, c);
		c.gridy++;
		panel.add(buttonB, c);

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

		add(panel);
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
