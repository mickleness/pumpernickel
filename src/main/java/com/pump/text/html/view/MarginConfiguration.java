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
package com.pump.text.html.view;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.pump.text.html.css.CssMarginValue;
import com.pump.text.html.css.CssMultiMarginParser;
import com.pump.text.html.css.CssSingleMarginParser;
import com.pump.text.html.css.CssValueCreationToken;

/**
 * This reviews all the margin properties and selects the most recently defined
 * values.
 * <p>
 * For example: the "margin" property and the "margin-right" property might
 * contain conflicting instructions. This class sorts the properties and makes
 * sure the most recently defined instruction "wins".
 */
public class MarginConfiguration {
	public CssMarginValue left, top, right, bottom;

	public MarginConfiguration(QViewHelper helper) {
		Collection<Entry<String, Object>> map = CssValueCreationToken
				.getOrderedProperties(helper,
						CssSingleMarginParser.PROPERTY_MARGIN_BOTTOM,
						CssSingleMarginParser.PROPERTY_MARGIN_LEFT,
						CssSingleMarginParser.PROPERTY_MARGIN_TOP,
						CssSingleMarginParser.PROPERTY_MARGIN_RIGHT,
						CssMultiMarginParser.PROPERTY_MARGIN);
		for (Entry<String, Object> entry : map) {
			if (entry.getKey()
					.equals(CssSingleMarginParser.PROPERTY_MARGIN_BOTTOM)) {
				bottom = (CssMarginValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssSingleMarginParser.PROPERTY_MARGIN_LEFT)) {
				left = (CssMarginValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssSingleMarginParser.PROPERTY_MARGIN_TOP)) {
				top = (CssMarginValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssSingleMarginParser.PROPERTY_MARGIN_RIGHT)) {
				right = (CssMarginValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssMultiMarginParser.PROPERTY_MARGIN)) {
				List<CssMarginValue> list = (List<CssMarginValue>) entry
						.getValue();
				if (list.size() == 1) {
					top = left = bottom = right = list.get(0);
				} else if (list.size() == 2) {
					top = bottom = list.get(0);
					left = right = list.get(1);
				} else if (list.size() == 3) {
					top = list.get(0);
					left = right = list.get(1);
					bottom = list.get(2);
				} else if (list.size() == 4) {
					top = list.get(0);
					right = list.get(1);
					bottom = list.get(2);
					left = list.get(3);
				} else {
					throw new RuntimeException(
							"Unsupported number of margin values: "
									+ list.size());
				}
			}
		}
	}
}