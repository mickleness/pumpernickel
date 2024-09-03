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
package com.pump.awt;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;

import com.pump.util.JVM;
import com.pump.util.Text;

public class TextSize {

	/**
	 * Truncate a String to fit within the number of pixels provided.
	 * 
	 * @param text
	 *            the text to consider truncating
	 * @param font
	 *            the font used to render the string
	 * @param frc
	 *            the FontRenderContext in use
	 * @param width
	 *            the width (in pixels) the String should fit within.
	 * @return the original String, or if necessary a subset of the string with
	 *         "..." appended.
	 */
	public static String truncate(String text, Font font,
			FontRenderContext frc, int width) {
		if (getWidth(text, font, frc) < width)
			return text;

		int min = 0;
		int max = text.length() - 1;
		String substring = text;
		while (max - min > 0) {
			int mid = (max + min) / 2;
			substring = text.substring(0, mid) + "\u2026";
			if (getWidth(substring, font, frc) > width) {
				max = mid;
			} else {
				min = mid + 1;
			}
		}

		return substring;
	}

	public static float getWidth(String text, Font font, FontRenderContext frc) {
		return (float) font.getStringBounds(text, frc).getWidth();
	}

	public static Dimension getPreferredSize(JTextArea textArea,
			int preferredWidth) {
		Font font = textArea.getFont();
		String text = textArea.getText();

		Map<Attribute, Object> attributes = new HashMap<Attribute, Object>();
		attributes.put(TextAttribute.FONT, font);

		/**
		 * It is crucial this be accurate! I used to have it always true/true,
		 * and XP sometimes failed because of it.
		 */
		Graphics2D g = ((Graphics2D) textArea.getGraphics());
		FontRenderContext frc = null;
		if (g != null)
			frc = g.getFontRenderContext();

		if (frc == null) {
			// on Mac "true, false" seemed to be the right combo.
			// try testing with QOptionPaneDemo in French and see the
			// external changes dialog
			frc = new FontRenderContext(null, true, false);
		}

		String[] paragraphs = Text.getParagraphs(text);
		int rows = 0;
		for (int a = 0; a < paragraphs.length; a++) {
			int textLength = paragraphs[a].length();
			if (Text.isWhiteSpace(paragraphs[a])) {
				rows++;
			} else {
				AttributedString attrString = new AttributedString(
						paragraphs[a], attributes);

				LineBreakMeasurer lbm = new LineBreakMeasurer(
						attrString.getIterator(), frc);

				int pos = 0;
				while (pos < textLength) {
					pos = lbm.nextOffset(preferredWidth);
					lbm.setPosition(pos);
					rows++;
				}
			}
		}
		int extra = 0;
		if (JVM.isWindowsXP) { // allow for descents
			extra = (int) (font.getLineMetrics("g", frc).getDescent() + 1);
		}

		FontMetrics metrics = textArea.getFontMetrics(font);
		int rowHeight = metrics.getHeight();

		return new Dimension(preferredWidth, rows * rowHeight + extra);
	}
}