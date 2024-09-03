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
package com.pump.swing;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JComboBox;

import com.pump.plaf.LabelCellRenderer;
import com.pump.util.HumanStringComparator;

/**
 * A JComboBox for selecting Fonts that includes a renderer to preview the
 * fonts.
 *
 */
public class FontComboBox extends JComboBox<Map.Entry<String, Font>> {
	private static final long serialVersionUID = 1L;

	/**
	 * A factory that produces the Fonts for a FontComboBox.
	 * <p>
	 * The default implementation consults the local GraphicsEnvironment for
	 * available fonts, but it is easy to load Fonts from files or zip archives
	 * as well.
	 */
	public static class FontFactory {

		/**
		 * Create the Fonts this combobox should display, in the order they'll
		 * be listed. Subclasses may override this to provide their own special
		 * set of Fonts.
		 */
		protected SortedMap<String, Font> createFonts() {
			Comparator<String> comparator = new HumanStringComparator();
			SortedMap<String, Font> returnValue = new TreeMap<>(comparator);

			// first, populate the fonts on this computer:
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			String[] s = ge.getAvailableFontFamilyNames();
			for (String fontName : s) {
				returnValue.put(fontName, new Font(fontName, 0, 16));
			}

			return returnValue;
		}
	}

	Map<String, Font> fontMap;

	/**
	 * Create a FontComboBox that uses the default FontFactory.
	 */
	public FontComboBox() {
		this(new FontFactory());
	}

	public FontComboBox(FontFactory fontFactory) {
		setRenderer(new LabelCellRenderer<Map.Entry<String, Font>>() {

			@Override
			protected void formatLabel(Entry<String, Font> value) {
				Font font = value.getValue();
				String text = value.getKey();
				if (font != null) {
					label.setText(text);
					label.setFont(font);
				} else {
					label.setText(" ");
				}
			}
		});

		fontMap = fontFactory.createFonts();
		for (Map.Entry<String, Font> fontEntry : fontMap.entrySet()) {
			addItem(fontEntry);
		}
	}

	/**
	 * Select a Font based on its font name, or set the selected font to null to
	 * this font is not found.
	 * 
	 * @param fontName
	 *            the name of the Font to select.
	 * @return true if this Font was successfully selected.
	 */
	public boolean selectFont(String fontName) {
		int fontComboBoxIndex = -1;
		for (int a = 0; a < getItemCount() && fontComboBoxIndex == -1; a++) {
			Entry<String, Font> entry = getItemAt(a);
			if (entry.getKey().equals(fontName)) {
				fontComboBoxIndex = a;
				break;
			}
		}
		setSelectedIndex(fontComboBoxIndex);

		return fontComboBoxIndex != -1;
	}

	public Font getSelectedFont() {
		Entry<String, Font> entry = (Entry<String, Font>) getSelectedItem();
		if (entry == null)
			return null;
		return entry.getValue();
	}
}