package com.pump.showcase;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pump.plaf.QPanelUI;

public abstract class ShowcaseExampleDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	protected JLabel configurationLabel = new JLabel("Configuration:");
	protected JLabel exampleLabel = new JLabel("Example:");
	protected JPanel configurationPanel = new JPanel();
	protected JPanel examplePanel = new JPanel();

	public ShowcaseExampleDemo() {
		this(false, false, true);
	}

	public ShowcaseExampleDemo(boolean stretchExampleToFillHoriz,
			boolean stretchExampleToFillVert, boolean useRoundedCorners) {
		super();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(10, 3, 3, 3);
		c.anchor = GridBagConstraints.NORTHWEST;
		add(configurationLabel, c);
		c.gridy++;
		c.insets = new Insets(3, 3, 3, 3);
		add(configurationPanel, c);
		c.gridy++;
		c.insets = new Insets(10, 3, 3, 3);
		add(exampleLabel, c);
		c.gridy++;
		c.weighty = 1;
		c.weightx = 1;
		c.insets = new Insets(3, 3, 3, 3);
		if (stretchExampleToFillHoriz && stretchExampleToFillVert) {
			c.fill = GridBagConstraints.BOTH;
		} else if (stretchExampleToFillVert) {
			c.fill = GridBagConstraints.VERTICAL;
		} else if (stretchExampleToFillHoriz) {
			c.fill = GridBagConstraints.HORIZONTAL;
		}
		add(examplePanel, c);

		Font font = exampleLabel.getFont();
		font = font.deriveFont(font.getSize2D() * 6 / 5);
		exampleLabel.setFont(font);
		configurationLabel.setFont(font);

		QPanelUI panelUI = QPanelUI.createBoxUI();
		configurationPanel.setUI(panelUI);

		if (useRoundedCorners) {
			examplePanel.setUI(panelUI);
		} else {
			panelUI = QPanelUI.createBoxUI();
			panelUI.setCornerSize(0);
			configurationPanel.setUI(panelUI);
		}

		exampleLabel.setLabelFor(examplePanel);
		configurationLabel.setLabelFor(configurationPanel);
	}
}
