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
package com.pump.text.html.css.border;

import java.util.List;
import java.util.Objects;

import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssParserUtils;
import com.pump.text.html.css.CssPropertyParser;

/**
 * The border property may be specified using one, two, or three of the values
 * listed below. The order of the values does not matter.
 * 
 * <ul>
 * <li>&lt;line-width&gt; Sets the thickness of the border. Defaults to medium
 * if absent. See border-width.</li>
 * <li>&lt;line-style&gt; Sets the style of the border. Defaults to none if
 * absent. See border-style.</li>
 * <li>&lt;color&gt; Sets the color of the border. See border-color.</li>
 * </ul>
 *
 */
public class CssBorderParser implements CssPropertyParser<CssBorderValue> {

	public static final String PROPERTY_BORDER = "border";

	String propertyName;

	public CssBorderParser() {
		this(PROPERTY_BORDER);
	}

	protected CssBorderParser(String propertyName) {
		Objects.requireNonNull(propertyName);
		this.propertyName = propertyName;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public CssBorderValue parse(String cssString) {
		CssLength width = null;
		CssBorderStyleValue style = null;
		CssColorValue color = null;

		CssBorderWidthParser widthParser = new CssBorderWidthParser();
		CssBorderStyleParser styleParser = new CssBorderStyleParser();
		CssBorderColorParser colorParser = new CssBorderColorParser();

		List<String> terms = CssParserUtils.splitSpaceSeparatedList(cssString,
				false);
		for (String term : terms) {
			boolean consumed = false;
			try {
				if (color == null) {
					color = colorParser.parse(term).get(0);
					consumed = true;
				}
			} catch (Exception e) {
			}

			try {
				if (width == null && !consumed) {
					width = widthParser.parse(term).get(0);
					consumed = true;
				}
			} catch (Exception e) {
			}

			try {
				if (style == null && !consumed) {
					style = styleParser.parse(term).get(0);
					consumed = true;
				}
			} catch (Exception e) {
			}
		}

		if (width == null)
			width = widthParser.parse("medium").get(0);
		if (style == null)
			style = styleParser.parse("none").get(0);
		if (color == null)
			color = colorParser.parse("black").get(0);

		return new CssBorderValue(cssString, width, style, color);
	}

}