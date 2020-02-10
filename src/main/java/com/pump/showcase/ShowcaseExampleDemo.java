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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;

import com.pump.graphics.GraphicInstruction;
import com.pump.graphics.GraphicsWriter;
import com.pump.graphics.ImageInstruction;
import com.pump.plaf.QPanelUI;

/**
 * This is a ShowcaseDemo that includes a "Configuration" and "Example" panel.
 */
public abstract class ShowcaseExampleDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	static class ShowcaseSlider extends JSlider {
		private static final long serialVersionUID = 1L;

		public ShowcaseSlider(int min, int max, int value) {
			super(min, max, value);
		}

		public ShowcaseSlider() {
		}

		public ShowcaseSlider(int min, int max) {
			super(min, max);
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (getUI().getClass().getName()
					.endsWith("plaf.windows.WindowsSliderUI")) {
				paintDarkTrack(g);
			} else {
				super.paintComponent(g);
			}
		}

		/**
		 * Paint an extra shadow on top of the track. I wish there were an
		 * easier way to do this, but I looked through the WindowsSliderUI and
		 * didn't see a way to customize the track color.
		 */
		protected void paintDarkTrack(Graphics g0) {
			Graphics2D g = (Graphics2D) g0;
			GraphicsWriter w = new GraphicsWriter(false, Integer.MAX_VALUE);
			w.setRenderingHints(g.getRenderingHints());
			w.clipRect(0, 0, getWidth(), getHeight());
			super.paintComponent(w);
			GraphicInstruction[] instructions = w.getInstructions(true);
			for (int a = 0; a < instructions.length; a++) {
				GraphicInstruction i = instructions[a];
				i.paint((Graphics2D) g);
				if (i instanceof ImageInstruction) {
					Rectangle r = i.getBounds().getBounds();
					if (r.width > getWidth() * .8) {
						g.setColor(new Color(0, 0, 0, 40));
						((Graphics2D) g).fill(i.getBounds());
					}
				}
			}
		}
	}

	protected JLabel configurationLabel = new JLabel("Configuration:");
	protected JLabel exampleLabel = new JLabel("Example:");
	protected JPanel configurationPanel = new JPanel();
	protected JPanel examplePanel = new JPanel();

	private boolean stretchExampleToFillHoriz = false;
	private boolean stretchExampleToFillVert = false;
	private boolean stretchConfigurationToFillHoriz = false;

	public ShowcaseExampleDemo() {
		this(false, false, true);
	}

	public ShowcaseExampleDemo(boolean stretchExampleToFillHoriz,
			boolean stretchExampleToFillVert, boolean useRoundedCorners) {
		super();
		this.stretchExampleToFillHoriz = stretchExampleToFillHoriz;
		this.stretchExampleToFillVert = stretchExampleToFillVert;

		layoutComponents();

		Font font = getHeaderLabelFont();
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

	static Font getHeaderLabelFont() {
		Font font = UIManager.getFont("Label.font");
		font = font.deriveFont(font.getSize2D() * 6 / 5);
		return font;
	}

	private void layoutComponents() {
		removeAll();
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
		if (stretchConfigurationToFillHoriz) {
			c.fill = GridBagConstraints.HORIZONTAL;
		}
		add(configurationPanel, c);
		c.fill = GridBagConstraints.NONE;
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
	}
}