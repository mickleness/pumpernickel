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
package com.pump.text.html.css.border;

import java.util.List;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssListParser;

/**
 * The border-width property may be specified using one, two, three, or four
 * values.
 * 
 * <ul>
 * <li>When one value is specified, it applies the same width to all four
 * sides.</li>
 * <li>When two values are specified, the first width applies to the top and
 * bottom, the second to the left and right.</li>
 * <li>When three values are specified, the first width applies to the top, the
 * second to the left and right, the third to the bottom.</li>
 * <li>When four values are specified, the widths apply to the top, right,
 * bottom, and left in that order (clockwise).</li>
 * </ul>
 */
public class CssBorderWidthParser extends CssListParser<CssLength> {

	AbstractWidthParser widthParser = new AbstractWidthParser() {
		@Override
		public String getPropertyName() {
			return null;
		}
	};

	public static final String PROPERTY_BORDER_WIDTH = "border-width";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_WIDTH;
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssLength> dest) {
		String str = cssString.substring(index);
		CssLength newLength = widthParser.parse(str);
		dest.add(newLength);
		int i = cssString.indexOf(newLength.toCSSString(), index);
		return i + newLength.toCSSString().length();
	}
}