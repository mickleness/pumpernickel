package com.pump.showcase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.pump.icon.PaddedIcon;
import com.pump.icon.PlusIcon;
import com.pump.plaf.BoxTabbedPaneUI;
import com.pump.swing.PartialLineBorder;

/**
 * TODO: include controls to manipulate UI: change L&F entirely, change tab
 * placement
 */
public class BoxTabbedPaneUIDemo extends JComponent {

	private static final long serialVersionUID = 1L;

	JTabbedPane tabs = new JTabbedPane();
	JButton addButton = new JButton(new PaddedIcon(
			new PlusIcon(12, 12, 1, null), new Dimension(22, 22)));
	JComboBox<String> tabPlacementComboBox = new JComboBox<>();
	JCheckBox closeableCheckBox = new JCheckBox("Closeable Tabs");
	JCheckBox hideSingleCheckBox = new JCheckBox("Hide Single Tab", true);

	public BoxTabbedPaneUIDemo() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(tabs, c);
		c.gridy++;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 3, 3, 3);
		add(tabPlacementComboBox, c);
		c.gridy++;
		add(closeableCheckBox, c);
		c.gridy++;
		add(hideSingleCheckBox, c);

		closeableCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabs.putClientProperty(BoxTabbedPaneUI.PROPERTY_CLOSEABLE_TABS,
						closeableCheckBox.isSelected());
			}

		});

		hideSingleCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabs.putClientProperty(
						BoxTabbedPaneUI.PROPERTY_HIDE_SINGLE_TAB,
						hideSingleCheckBox.isSelected());
			}

		});

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
}
