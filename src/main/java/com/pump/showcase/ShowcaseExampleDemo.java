package com.pump.showcase;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class ShowcaseExampleDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	protected JLabel configurationLabel = new JLabel("Configuration:");
	protected JLabel exampleLabel = new JLabel("Example:");
	protected JPanel configurationPanel = new JPanel();
	protected JPanel examplePanel = new JPanel();

	public ShowcaseExampleDemo() {
		super();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3, 3, 3, 3);
		add(configurationLabel, c);
		c.gridy++;
		add(configurationPanel, c);
		c.gridy++;
		add(exampleLabel, c);
		c.gridy++;
		c.weighty = 1;
		add(examplePanel, c);

		Font font = exampleLabel.getFont();
		font = font.deriveFont(font.getSize2D() * 6 / 5);
		exampleLabel.setFont(font);
		configurationLabel.setFont(font);
	}
}
