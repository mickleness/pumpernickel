/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.math.Fraction;
import com.pump.plaf.RoundTextFieldUI;
import com.pump.plaf.SubtleScrollBarUI;
import com.pump.plaf.button.QButtonUI;
import com.pump.plaf.button.RoundRectButtonUI;
import com.pump.plaf.combobox.QComboBoxUI;
import com.pump.showcase.demo.ShowcaseDemo;
import com.pump.swing.HelpButton;
import com.pump.swing.JFancyBox;
import com.pump.util.list.ObservableList;

public class HeaderRow extends JPanel {
	private static final long serialVersionUID = 1L;

	static int HEADER_FONT_SIZE = 18;

	static class DemoCellRenderer extends JPanel
			implements ListCellRenderer<ShowcaseDemoInfo> {
		private static final long serialVersionUID = 1L;

		JLabel nameLabel = new JLabel();

		Color selectedBackground, unselectedBackground, selectedTextForeground,
				unselectedTextForeground;

		DecimalFormat format = new DecimalFormat("###");

		public DemoCellRenderer() {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(3, 3, 3, 3);
			add(nameLabel, c);

			unselectedBackground = UIManager.getColor("List.background");
			selectedBackground = UIManager.getColor("List.selectionBackground");

			unselectedTextForeground = UIManager.getColor("List.foreground");
			selectedTextForeground = UIManager
					.getColor("List.selectionForeground");

			Font font = UIManager.getFont("List.font");
			if (font != null) {
				nameLabel.setFont(font);
			}
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends ShowcaseDemoInfo> list, ShowcaseDemoInfo value,
				int index, boolean isSelected, boolean cellHasFocus) {

			Font font = UIManager.getFont("Label.font");
			if (index == -1)
				font = font.deriveFont((float) HEADER_FONT_SIZE);

			if (isSelected) {
				setBackground(selectedBackground);
			} else {
				setBackground(unselectedBackground);
			}

			nameLabel.setFont(font);

			String name = value == null ? "" : value.getDemoName();
			nameLabel.setText(name);

			boolean isLoaded = value == null ? false : value.isDemoLoaded();
			formatLabel(nameLabel, isLoaded, isSelected, 1);

			return this;
		}

		private void formatLabel(JLabel label, boolean isLoaded,
				boolean isSelected, float opacity) {
			if (isLoaded) {
				if (isSelected) {
					label.setForeground(fade(selectedTextForeground, opacity));
				} else {
					label.setForeground(
							fade(unselectedTextForeground, opacity));
				}
			} else {
				if (isSelected) {
					label.setForeground(
							fade(selectedTextForeground, opacity * .5f));
				} else {
					label.setForeground(
							fade(unselectedTextForeground, opacity * .5f));
				}
			}
		}

		private Color fade(Color c, float opacity) {
			return new Color(c.getRed(), c.getGreen(), c.getBlue(),
					(int) (opacity * 255));
		}

	}

	ActionListener helpButtonListener = new ActionListener() {
		JScrollPane scrollPane;
		JFancyBox box;
		JEditorPane textPane;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (scrollPane == null) {
				textPane = app.getSelectedDemo().getPanel().createTextPane();
				scrollPane = new JScrollPane(textPane);
				scrollPane.getVerticalScrollBar()
						.setUI(new SubtleScrollBarUI());
				scrollPane.getHorizontalScrollBar()
						.setUI(new SubtleScrollBarUI());

				updatePreferredSize();

				scrollPane.addHierarchyBoundsListener(
						new HierarchyBoundsListener() {

							@Override
							public void ancestorMoved(HierarchyEvent e) {
								updatePreferredSize();
							}

							@Override
							public void ancestorResized(HierarchyEvent e) {
								updatePreferredSize();
							}

						});
				RootPaneContainer rpc = (RootPaneContainer) SwingUtilities
						.getWindowAncestor(helpButton);
				box = new JFancyBox(rpc, scrollPane, JLayeredPane.PALETTE_LAYER,
						0);
			}

			try {
				textPane.setPage(app.getSelectedDemo().getDemo().getHelpURL());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			box.setVisible(true);
		}

		private void updatePreferredSize() {
			Window window = SwingUtilities.getWindowAncestor(helpButton);
			Dimension d = window.getSize();
			d.width = Math.max(200, d.width - 100);
			d.height = Math.max(200, d.height - 100);
			scrollPane.setMinimumSize(d);
			scrollPane.setPreferredSize(d);
			textPane.setMinimumSize(d);
			textPane.setPreferredSize(d);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scrollPane.revalidate();
				}
			});
		}

	};
	PropertyChangeListener infoListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// our worker thread might trigger updates, but only repaint on EDT
			// (I observed at least one deadlock without this check)
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						propertyChange(evt);
					}
				});
				return;
			}

			ShowcaseDemoInfo src = (ShowcaseDemoInfo) evt.getSource();
			JList list = comboBoxUI.getList();
			if (list.isShowing()) {
				repaint(list, src);
			}
			if (demoComboBox.getSelectedItem() == src)
				demoComboBox.repaint();
		}

		/**
		 * Repaint a row in a JList, if the element is found.
		 * 
		 * @param list
		 *            the list to repaint a row of
		 * @param element
		 *            the element that requires repainting
		 */
		private void repaint(JList list, Object element) {
			int index = -1;
			for (int a = 0; a < list.getModel().getSize() && index == -1; a++) {
				if (list.getModel().getElementAt(a) == element)
					index = a;
			}

			if (index != -1) {
				Rectangle cellBounds = list.getUI().getCellBounds(list, index,
						index);
				list.repaint(cellBounds);
			}

		}
	};

	JComboBox<ShowcaseDemoInfo> demoComboBox = new JComboBox<>();
	JButton helpButton;
	QButtonUI buttonUI = new RoundRectButtonUI(4);
	QComboBoxUI comboBoxUI = buttonUI.createComboBoxUI();
	PumpernickelShowcaseApp app;
	JTextField searchPhraseField = new JTextField(15);
	ObservableList<ShowcaseDemoInfo> activeDemos = new ObservableList<>();
	ShowcaseDemoInfo[] allDemos;

	DocumentListener searchFieldDocListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			app.searchPhrase.setValue(searchPhraseField.getText());
		}

	};

	PropertyChangeListener searchPhraseListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String newValue = app.searchPhrase.getValue();

			if (!newValue.equals(searchPhraseField.getText())) {
				searchPhraseField.getDocument()
						.removeDocumentListener(searchFieldDocListener);
				searchPhraseField.setText(app.searchPhrase.getValue());
				searchPhraseField.getDocument()
						.addDocumentListener(searchFieldDocListener);
			}

			ShowcaseDemoInfo selection = (ShowcaseDemoInfo) demoComboBox
					.getSelectedItem();
			if (newValue.isEmpty()) {
				activeDemos.setAll(allDemos);
			} else {
				Collection<ShowcaseDemoInfo> newList = new LinkedHashSet<>();

				// TODO: use WildcardPattern

				newValue = newValue.toLowerCase();
				for (ShowcaseDemoInfo demo : allDemos) {
					if (demo.getDemoName().toLowerCase().contains(newValue)) {
						newList.add(demo);
					}
					if (demo.isDemoLoaded()) {
						ShowcaseDemo d = demo.getDemo();
						for (String keyword : d.getKeywords()) {
							keyword = keyword.toLowerCase();
							if (keyword.contains(newValue)) {
								newList.add(demo);
							}
						}
					}
				}

				// maybe we should handle this differently, but for now let's
				// not have an empty list:
				if (newList == null) {
					newList.addAll(Arrays.asList(allDemos));
				}

				activeDemos.setAll(newList);
			}

			// make sure we have a selection
			if (selection == null) {
				demoComboBox.setSelectedIndex(0);
			} else {
				int i = activeDemos.indexOf(selection);
				if (i >= 0) {
					demoComboBox.setSelectedIndex(i);
				}
			}
		}
	};

	public HeaderRow(PumpernickelShowcaseApp app, ShowcaseDemoInfo[] allDemos) {
		super(new GridBagLayout());
		this.app = app;
		this.allDemos = allDemos;

		app.searchPhrase.addPropertyChangeListener(searchPhraseListener);

		activeDemos.addAll(allDemos);

		demoComboBox = new JComboBox<ShowcaseDemoInfo>(
				activeDemos.createUIMirror(null)) {
			private static final long serialVersionUID = 1L;

			@Override
			public int getMaximumRowCount() {
				// we're high up in construction; this doesn't matter
				if (comboBoxUI.getList() == null)
					return super.getMaximumRowCount();

				// make as tall as the window
				Rectangle rowBounds = comboBoxUI.getList().getCellBounds(0, 0);
				if (rowBounds == null)
					return 0;
				Rectangle comboBoxBounds = new Rectangle(0, 0,
						demoComboBox.getWidth(), demoComboBox.getHeight());
				SwingUtilities.convertRectangle(demoComboBox, comboBoxBounds,
						app);
				int comboBoxBottomY = comboBoxBounds.y + comboBoxBounds.height;
				int remainingHeight = app.getContentPane().getHeight()
						- comboBoxBottomY;
				return Math.max(remainingHeight / rowBounds.height, 1);
			}
		};
		searchPhraseListener.propertyChange(null);

		helpButton = HelpButton.create(helpButtonListener, buttonUI,
				HEADER_FONT_SIZE, null);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 10, 5, 5);
		add(demoComboBox, c);
		c.gridx++;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5, 5, 5, 10);
		add(searchPhraseField, c);
		c.weightx = 0;
		c.gridx++;
		add(helpButton, c);

		searchPhraseField.getDocument()
				.addDocumentListener(searchFieldDocListener);

		demoComboBox.setUI(comboBoxUI);
		demoComboBox.setRenderer(new DemoCellRenderer());

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ShowcaseDemoInfo sdi = (ShowcaseDemoInfo) demoComboBox
						.getSelectedItem();
				app.setSelectedDemo(sdi);
			}

		};
		demoComboBox.addActionListener(actionListener);

		// trigger listeners just to set up first time UI:
		actionListener.actionPerformed(null);

		for (ShowcaseDemoInfo i : allDemos) {
			i.addPropertyChangeListener(infoListener);
		}
		demoComboBox.putClientProperty(QComboBoxUI.PROPERTY_IS_POP_DOWN,
				Boolean.TRUE);

		searchPhraseField.setUI(new RoundTextFieldUI());

		Font font = searchPhraseField.getFont();
		font = font.deriveFont((float) HEADER_FONT_SIZE);
		searchPhraseField.setFont(font);
	}

}