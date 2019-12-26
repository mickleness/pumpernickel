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
package com.pump.plaf;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.pump.util.JVM;

public class LabelCellRenderer<T> implements ListCellRenderer<T>,
		TreeCellRenderer, CellRendererConstants {

	/**
	 * If this client property is defined on a JComponent this informs whether
	 * this renderer will apply alternating ("zebra") stripes to list elements.
	 * <p>
	 * For example, see <a href=
	 * "https://ux.stackexchange.com/questions/3562/to-use-or-not-to-use-zebra-stripes-or-alternating-row-colors-for-tables"
	 * >articles like this</a>.
	 */
	public static final String PROPERTY_ALTERNATING_BACKGROUND = LabelCellRenderer.class
			.getName() + "#alternatingBackground";

	protected JLabel label;
	protected JComboBox<?> comboBox;
	protected Collection<WeakReference<JList>> lists = new HashSet<>();
	protected boolean addKeyListener;

	public LabelCellRenderer() {
		this(null, false);
	}

	/**
	 * 
	 * @param comboBox
	 *            an optional reference to the JComboBox. This may be used to
	 *            render certain aspects (such as keyboard focus), and it is
	 *            required for the argument "addKeyListener" to work.
	 * @param addKeyListener
	 *            if true then key listeners are added both to JLists and the
	 *            JComboBox argument to help select items based on the user's
	 *            typing.
	 */
	public LabelCellRenderer(JComboBox<?> comboBox, boolean addKeyListener) {
		this.addKeyListener = addKeyListener;
		if (comboBox != null) {
			this.comboBox = comboBox;
			if (addKeyListener) {
				comboBox.addKeyListener(new ListKeyListener(comboBox));
			}
		}
		label = createLabel();
		label.setBorder(EMPTY_BORDER);
	}

	/**
	 * This is provided as a hook for subclasses to create their own JLabels.
	 */
	protected JLabel createLabel() {
		return new JLabel();
	}

	/**
	 * Adjust the text and the icon of the <code>label</code> field.
	 */
	protected void formatLabel(T value) {
		String str = value == null ? "" : value.toString();
		label.setText(str);
		label.setIcon(null);
	}

	/** Return the label this renderer uses. */
	public JLabel getLabel() {
		return label;
	}

	@Override
	public JLabel getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		// TODO: this is missing the key listener

		formatLabel((T) value);

		formatLabelColors(tree, isSelected, row);
		label.setOpaque(row != -1);

		if (isFocusBorderActive()) {
			if (comboBox != null && comboBox.hasFocus() && row == -1) {
				label.setBorder(FOCUS_BORDER);
			} else {
				label.setBorder(EMPTY_BORDER);
			}
		}
		return label;
	}

	@Override
	public JLabel getListCellRendererComponent(JList<? extends T> list,
			T value, int index, boolean isSelected, boolean cellHasFocus) {
		if (addKeyListener && list != null && registerList(list)) {
			list.addKeyListener(new ListKeyListener(list));
		}

		formatLabel(value);

		formatLabelColors(list, isSelected, index);
		label.setOpaque(index != -1);

		if (isFocusBorderActive()) {
			if (comboBox != null && comboBox.hasFocus() && index == -1) {
				label.setBorder(FOCUS_BORDER);
			} else {
				label.setBorder(EMPTY_BORDER);
			}
		}
		return label;
	}

	private boolean registerList(JList<? extends T> list) {
		Iterator<WeakReference<JList>> iter = lists.iterator();
		while (iter.hasNext()) {
			WeakReference<JList> ref = iter.next();
			JList knownList = ref.get();
			if (knownList == null) {
				iter.remove();
			}
			if (knownList == list) {
				return false;
			}
		}
		lists.add(new WeakReference<JList>(list));
		return true;
	}

	protected void formatLabelColors(JComponent jc, boolean isSelected,
			int rowNumber) {
		if (comboBox != null) {
			if (isSelected) {
				label.setBackground(UIManager
						.getColor("ComboBox.selectionBackground"));
				label.setForeground(UIManager
						.getColor("ComboBox.selectionForeground"));
			} else {
				label.setBackground(UIManager.getColor("ComboBox.background"));
				label.setForeground(UIManager.getColor("ComboBox.foreground"));
			}
		} else {
			if (isSelected) {
				if (jc instanceof JList) {
					label.setBackground(UIManager
							.getColor("List.selectionBackground"));
					label.setForeground(UIManager
							.getColor("List.selectionForeground"));
				} else {
					label.setBackground(UIManager
							.getColor("Menu.selectionBackground"));
					label.setForeground(UIManager
							.getColor("Menu.selectionForeground"));
				}
			} else {
				Color background;
				if (jc != null) {
					background = jc.getBackground();
					if (rowNumber % 2 == 1 && isAlternatingBackround(jc)) {
						background = darken(background);
					}
				} else {
					background = UIManager.getColor("Menu.background");
				}
				label.setBackground(background);
				label.setForeground(UIManager.getColor("Menu.foreground"));
			}
		}
	}

	private Color darken(Color c) {
		return new Color((int) (c.getRed() * .985),
				(int) (c.getGreen() * .985), (int) (c.getBlue() * .985),
				c.getAlpha());
	}

	private boolean isAlternatingBackround(JComponent jc) {
		Boolean b = (Boolean) jc
				.getClientProperty(PROPERTY_ALTERNATING_BACKGROUND);
		if (b == null) {
			b = jc instanceof JTree;
		}
		return b.booleanValue();
	}

	/**
	 * If this returns true then the border of the label may be modified to
	 * depict focus.
	 * 
	 * @return if true then the border of the label may be modified to depict
	 *         focus. The default implementation tries to figure out if the if
	 *         UI actually renders the focus through the border.
	 */
	protected boolean isFocusBorderActive() {
		return !JVM.isMac;
	}

}