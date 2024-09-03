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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class PanelCellRenderer<T> implements ListCellRenderer<T>,
		CellRendererConstants {
	protected JPanel panel = new JPanel();
	protected JLabel label = new JLabel();

	boolean alternateRows;

	public PanelCellRenderer() {
		this(false);
	}

	public PanelCellRenderer(boolean alternateRows) {
		this.alternateRows = alternateRows;
	}

	protected void formatPanelColors(JList<? extends T> list,
			boolean isSelected, int rowIndex) {

		if (isSelected) {
			panel.setBackground(UIManager
					.getColor("ComboBox.selectionBackground"));
			setForeground(panel,
					UIManager.getColor("ComboBox.selectionForeground"));
		} else {
			Color background;
			if (list != null) {
				background = list.getBackground();
			} else {
				background = UIManager.getColor("ComboBox.background");
			}
			if (alternateRows && rowIndex >= 0 && rowIndex % 2 == 0) {
				int r = Math.max(0, background.getRed() - 20);
				int g = Math.max(0, background.getGreen() - 20);
				int b = Math.max(0, background.getBlue() - 20);
				background = new Color(r, g, b);
			}
			panel.setBackground(background);
			setForeground(panel, UIManager.getColor("ComboBox.foreground"));
		}
		panel.setOpaque(rowIndex != -1);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list,
			T value, int index, boolean isSelected, boolean cellHasFocus) {

		formatPanel(value);
		formatPanelColors(list, isSelected, index);
		return panel;
	}

	protected void setForeground(Component c, Color color) {
		c.setForeground(color);
		if (c instanceof Container) {
			Container container = (Container) c;
			for (int a = 0; a < container.getComponentCount(); a++) {
				Component child = container.getComponent(a);
				setForeground(child, color);
			}
		}

	}

	/**
	 * Fill/update the <code>panel</code> field.
	 */
	protected void formatPanel(T value) {
		label.setText(value.toString());
		label.setIcon(null);
		panel.removeAll();
		panel.add(label);
	}

	/** Return the panel this renderer uses. */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * If this returns true then the border of the panel may be modified to
	 * depict focus.
	 * 
	 * @return if true then the border of the panel may be modified to depict
	 *         focus. The default implementation tries to figure out if the if
	 *         UI actually renders the focus through the border.
	 */
	protected boolean isFocusBorderActive() {
		return false;
	}

}