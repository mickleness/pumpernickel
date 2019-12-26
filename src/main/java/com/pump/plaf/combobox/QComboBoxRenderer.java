package com.pump.plaf.combobox;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.pump.icon.EmptyIcon;
import com.pump.icon.GlyphIcon;
import com.pump.plaf.LabelCellRenderer;

/**
 * This renders a checkmark next to a selected list item when it is used in a
 * JList.
 * <p>
 * When this is used to render the JComboBox no checkmark is shown.
 * 
 * @param <T>
 */
public class QComboBoxRenderer<T> extends LabelCellRenderer<T> {
	final QComboBoxUI ui;

	Font lastFont = null;
	GlyphIcon checkmarkIcon = null;

	public QComboBoxRenderer(QComboBoxUI ui) {
		super(ui.getComboBox(), true);
		this.ui = ui;
	}

	@Override
	protected void formatLabelColors(JComponent jc, boolean isSelected,
			int rowNumber) {
		super.formatLabelColors(jc, isSelected, rowNumber);
		if (!ui.isPaintingComboBox()) {
			if (isSelected) {
				label.setBackground(UIManager
						.getColor("List.selectionBackground"));
				label.setForeground(UIManager
						.getColor("List.selectionForeground"));
			} else {
				label.setBackground(UIManager.getColor("List.background"));
				label.setForeground(UIManager.getColor("List.foreground"));
			}
			if (checkmarkIcon == null || !label.getFont().equals(lastFont)) {
				lastFont = label.getFont();
				checkmarkIcon = new GlyphIcon(lastFont, '✓',
						(int) lastFont.getSize2D(), label.getForeground());
			} else {
				checkmarkIcon.setColor(label.getForeground());
			}
			label.setIconTextGap(10);
			if (comboBox.getSelectedIndex() != rowNumber) {
				label.setIcon(new EmptyIcon(checkmarkIcon.getIconWidth(),
						checkmarkIcon.getIconHeight()));
			} else {
				label.setIcon(checkmarkIcon);
			}
		} else {
			label.setIcon(null);
		}
	}

	@Override
	protected boolean isFocusBorderActive() {
		return false;
	}

}