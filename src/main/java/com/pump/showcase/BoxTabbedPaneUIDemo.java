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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.pump.awt.SplayedLayout;
import com.pump.icon.IconUtils;
import com.pump.icon.PlusIcon;
import com.pump.inspector.Inspector;
import com.pump.plaf.BoxTabbedPaneUI;
import com.pump.swing.PartialLineBorder;

/**
 * This demos the BoxTabbedPaneUI.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/BoxTabbedPaneUIDemo.png"
 * alt="A screenshot of the BoxTabbedPaneUIDemo.">
 *
 */
public class BoxTabbedPaneUIDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JTabbedPane tabs = new JTabbedPane();
	JButton addButton = new JButton(IconUtils.createPaddedIcon(new PlusIcon(12,
			12, 1, null), new Dimension(22, 22)));
	JComboBox<String> tabPlacementComboBox = new JComboBox<>();
	JRadioButton closeableActive = new JRadioButton("Active");
	JRadioButton closeableInactive = new JRadioButton("Inactive");
	JRadioButton hideSingleActive = new JRadioButton("Active");
	JRadioButton hideSingleInactive = new JRadioButton("Inactive");

	public BoxTabbedPaneUIDemo() {
		super(true, true, false);
		Inspector layout = new Inspector(configurationPanel);
		layout.addRow(new JLabel("Tab Placement:"), tabPlacementComboBox);
		layout.addRow(new JLabel("Closeable Tabs:"), closeableActive,
				closeableInactive);
		layout.addRow(new JLabel("Hide Single Tab:"), hideSingleActive,
				hideSingleInactive);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		examplePanel.add(tabs, c);

		tabs.setPreferredSize(new Dimension(500, 250));

		ButtonGroup g1 = new ButtonGroup();
		g1.add(hideSingleActive);
		g1.add(hideSingleInactive);

		hideSingleActive
				.setToolTipText("Hide row of tabs when there is only one tab showing.");

		closeableActive
				.setToolTipText("Include a button to close tabs (visible only on rollover).");

		ButtonGroup g2 = new ButtonGroup();
		g2.add(closeableActive);
		g2.add(closeableInactive);

		ActionListener closeableListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabs.putClientProperty(BoxTabbedPaneUI.PROPERTY_CLOSEABLE_TABS,
						closeableActive.isSelected());
			}

		};
		closeableInactive.addActionListener(closeableListener);
		closeableActive.addActionListener(closeableListener);
		closeableActive.doClick();

		ActionListener hideSingleListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabs.putClientProperty(
						BoxTabbedPaneUI.PROPERTY_HIDE_SINGLE_TAB,
						hideSingleActive.isSelected());
			}

		};
		hideSingleActive.addActionListener(hideSingleListener);
		hideSingleInactive.addActionListener(hideSingleListener);
		hideSingleInactive.doClick();

		tabPlacementComboBox.addItem("Top");
		tabPlacementComboBox.addItem("Left");
		tabPlacementComboBox.addItem("Bottom");
		tabPlacementComboBox.addItem("Right");

		addButton.setBorder(new PartialLineBorder(Color.gray, new Insets(1, 0,
				1, 1)));
		tabPlacementComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int tabPlacement;
				if (tabPlacementComboBox.getSelectedIndex() == 1) {
					tabPlacement = SwingConstants.LEFT;
					addButton.setBorder(new PartialLineBorder(Color.gray,
							new Insets(1, 1, 0, 1)));
				} else if (tabPlacementComboBox.getSelectedIndex() == 2) {
					tabPlacement = SwingConstants.BOTTOM;
					addButton.setBorder(new PartialLineBorder(Color.gray,
							new Insets(1, 0, 1, 1)));
				} else if (tabPlacementComboBox.getSelectedIndex() == 3) {
					tabPlacement = SwingConstants.RIGHT;
					addButton.setBorder(new PartialLineBorder(Color.gray,
							new Insets(0, 1, 1, 1)));
				} else {
					tabPlacement = SwingConstants.TOP;
					addButton.setBorder(new PartialLineBorder(Color.gray,
							new Insets(1, 0, 1, 1)));
				}
				tabs.setTabPlacement(tabPlacement);
			}
		});

		tabs.setUI(new BoxTabbedPaneUI());
		tabs.putClientProperty(BoxTabbedPaneUI.PROPERTY_TRAILING_COMPONENTS,
				Arrays.asList(addButton));

		addButton.setMargin(new Insets(0, 0, 0, 0));
		BoxTabbedPaneUI.getStyle(tabs).formatControlRowButton(tabs, addButton,
				-1);

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel label = new JLabel("Tab "
						+ (char) ('A' + tabs.getTabCount()));
				JPanel p = new JPanel();
				p.add(label);
				p.setBackground(Color.orange);
				tabs.addTab("Tab " + (tabs.getTabCount() + 1), p);
			}

		});
	}

	@Override
	public String getTitle() {
		return "BoxTabbedPaneUI Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new TabbedPaneUI that resembles modern (closeable) browser tabs.";
	}

	@Override
	public URL getHelpURL() {
		return BoxTabbedPaneUIDemo.class
				.getResource("boxTabbedPaneUIDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "tabs", "document", "MDI", "ui", "Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { BoxTabbedPaneUIDemo.class, JTabbedPane.class,
				SplayedLayout.class };
	}
}