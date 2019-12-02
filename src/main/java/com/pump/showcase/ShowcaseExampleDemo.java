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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.plaf.basic.BasicScrollPaneUI;

import com.pump.inspector.Inspector;
import com.pump.plaf.QPanelUI;
import com.pump.plaf.SubtleScrollBarUI;
import com.pump.swing.JPopover;

/**
 * This is a ShowcaseDemo that includes a "Configuration" and "Example" panel.
 */
public abstract class ShowcaseExampleDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

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

	/**
	 * Create a scrolling inspector with a fixed height.
	 * 
	 * @param height
	 * @return
	 */
	protected Inspector createConfigurationInspector(int height) {
		JPanel inspectorPanel = new JPanel();
		inspectorPanel.setOpaque(false);
		JScrollPane scrollPane = new JScrollPane(inspectorPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		// the width will auto-fill; it's the height we want to limit:
		scrollPane.setUI(new BasicScrollPaneUI());
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUI(new SubtleScrollBarUI());
		Inspector inspector = new Inspector(inspectorPanel);

		configurationPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		configurationPanel.add(scrollPane, c);
		scrollPane.setPreferredSize(new Dimension(10, height));

		stretchConfigurationToFillHoriz = true;
		layoutComponents();

		return inspector;
	}

	/**
	 * Add a popover labeling a slider.
	 * 
	 * @param suffix
	 *            the text to append after the numeric value, such as "%" or
	 *            " pixels".
	 */
	protected void addSliderPopover(JSlider slider, final String suffix) {
		new JPopover<JToolTip>(slider, new JToolTip(), false) {

			@Override
			protected void doRefreshPopup() {
				JSlider js = (JSlider) getOwner();
				int v = js.getValue();
				String newText;
				if (v == 1 && suffix.startsWith(" ") && suffix.endsWith("s")) {
					newText = v + suffix.substring(0, suffix.length() - 1);
				} else {
					newText = v + suffix;
				}
				getContents().setTipText(newText);

				// this is only because we have the JToolTipDemo so
				// colors might change:
				getContents().updateUI();
				getContents().setBorder(null);
			}
		};
	}
}