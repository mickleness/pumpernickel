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
package com.pump.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pump.plaf.LabelCellRenderer;

public class ListSectionContainer extends SectionContainer {

	private static final long serialVersionUID = 1L;

	public static class SectionListCellRenderer extends
			LabelCellRenderer<Section> {

		@Override
		protected void formatLabel(Section value) {
			label.setText(value.getName());
		}
	}

	protected JSplitPane splitPane;
	protected JList<Section> list;
	protected JPanel content = new JPanel(new GridBagLayout());
	protected JScrollPane listScrollPane;
	protected JPanel noSelectionPanel = new JPanel();

	/**
	 * If true then we'll automatically select the first element in the list.
	 */
	protected boolean autoSelectActive = true;

	public ListSectionContainer(boolean alphabetize) {
		this(alphabetize, null, null);
	}

	/**
	 * 
	 * @param alphabetize
	 * @param aboveList
	 *            an optional component placed above the JList
	 * @param belowList
	 *            an optional component placed below the JList
	 */
	public ListSectionContainer(boolean alphabetize, JComponent aboveList,
			JComponent belowList) {
		super(alphabetize);
		list = new JList<Section>(getSections().getListModelEDTMirror());
		listScrollPane = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel leftHandSide = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		if (aboveList != null) {
			leftHandSide.add(aboveList, c);
		}
		c.gridy++;
		c.weighty = 1;
		leftHandSide.add(listScrollPane, c);
		c.gridy++;
		c.weighty = 0;
		if (belowList != null) {
			leftHandSide.add(belowList, c);
		}

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftHandSide,
				content);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(splitPane, c);

		getSections().addUnsynchronizedChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (list.getSelectedIndex() == -1 && isAutoSelectActive()) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (list.getModel().getSize() > 0
									&& list.getSelectedIndex() == -1) {
								list.setSelectedIndex(0);
							}
						}
					});
				}
			}

		}, false);

		list.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					int lastSelectedIndex = -1;

					@Override
					public void valueChanged(ListSelectionEvent e) {
						int selectedIndex = list.getSelectedIndex();

						try {
							if (isAutoSelectActive()) {
								// we're trying to avoid an empty selection:
								if (selectedIndex == -1
										&& lastSelectedIndex != -1
										&& lastSelectedIndex < list.getModel()
												.getSize()) {
									list.setSelectedIndex(lastSelectedIndex);
								}
							}
							updateContentPanel();
						} finally {
							lastSelectedIndex = selectedIndex;
						}
					}

				});

		list.setCellRenderer(new SectionListCellRenderer());

		updateContentPanel();
	}

	protected void updateContentPanel() {
		Section section = list.getSelectedValue();
		JPanel child;
		if (section == null) {
			child = noSelectionPanel;
		} else {
			child = section.getBody();
		}

		content.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		content.add(child, c);

		content.invalidate();
		content.revalidate();
		content.setPreferredSize(new Dimension(50, 50));
		content.repaint();
	}

	/**
	 * Return true if we should automatically select the first element in the
	 * list.
	 * 
	 * @return true if we should automatically select the first element in the
	 *         list.
	 */
	public boolean isAutoSelectActive() {
		return autoSelectActive;
	}
}