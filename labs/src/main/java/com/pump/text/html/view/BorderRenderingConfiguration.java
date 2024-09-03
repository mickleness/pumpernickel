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
package com.pump.text.html.view;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssValueCreationToken;
import com.pump.text.html.css.border.CssBorderBottomColorParser;
import com.pump.text.html.css.border.CssBorderBottomLeftRadiusParser;
import com.pump.text.html.css.border.CssBorderBottomParser;
import com.pump.text.html.css.border.CssBorderBottomRightRadiusParser;
import com.pump.text.html.css.border.CssBorderBottomStyleParser;
import com.pump.text.html.css.border.CssBorderBottomWidthParser;
import com.pump.text.html.css.border.CssBorderColorParser;
import com.pump.text.html.css.border.CssBorderLeftColorParser;
import com.pump.text.html.css.border.CssBorderLeftParser;
import com.pump.text.html.css.border.CssBorderLeftStyleParser;
import com.pump.text.html.css.border.CssBorderLeftWidthParser;
import com.pump.text.html.css.border.CssBorderParser;
import com.pump.text.html.css.border.CssBorderRadiusParser;
import com.pump.text.html.css.border.CssBorderRadiusValue;
import com.pump.text.html.css.border.CssBorderRightColorParser;
import com.pump.text.html.css.border.CssBorderRightParser;
import com.pump.text.html.css.border.CssBorderRightStyleParser;
import com.pump.text.html.css.border.CssBorderRightWidthParser;
import com.pump.text.html.css.border.CssBorderStyleParser;
import com.pump.text.html.css.border.CssBorderStyleValue;
import com.pump.text.html.css.border.CssBorderTopColorParser;
import com.pump.text.html.css.border.CssBorderTopLeftRadiusParser;
import com.pump.text.html.css.border.CssBorderTopParser;
import com.pump.text.html.css.border.CssBorderTopRightRadiusParser;
import com.pump.text.html.css.border.CssBorderTopStyleParser;
import com.pump.text.html.css.border.CssBorderTopWidthParser;
import com.pump.text.html.css.border.CssBorderValue;
import com.pump.text.html.css.border.CssBorderWidthParser;
import com.pump.text.html.css.border.CssOutlineColorParser;
import com.pump.text.html.css.border.CssOutlineParser;
import com.pump.text.html.css.border.CssOutlineStyleParser;
import com.pump.text.html.css.border.CssOutlineWidthParser;

/**
 * This identifies the properties necessary to setup a BorderRendering.
 * <p>
 * For example: if your HTML defines "border" and "border-style" and
 * "border-right-style": all of those might contain information about the style.
 * This object flattens all of that into a unique set of attributes for each
 * edge.
 * <p>
 * When two CSS statements conflict (for ex: "border: solid red;" vs
 * "border-color: green;"), the most recently defined CSS statement takes
 * priority.
 */
public class BorderRenderingConfiguration {

	/**
	 * Create a configuration for an outline. This is much simpler than a
	 * border, because there are only a few CSS properties to consult.
	 */
	public static BorderRenderingConfiguration forOutline(QViewHelper helper) {
		Collection<Entry<String, Object>> properties = CssValueCreationToken
				.getOrderedProperties(helper, CssOutlineParser.PROPERTY_OUTLINE,
						CssOutlineColorParser.PROPERTY_OUTLINE_COLOR,
						CssOutlineWidthParser.PROPERTY_OUTLINE_WIDTH,
						CssOutlineStyleParser.PROPERTY_OUTLINE_STYLE);

		BorderRenderingConfiguration rv = new BorderRenderingConfiguration();
		for (Map.Entry<String, Object> entry : properties) {
			if (entry.getKey().equals(CssOutlineParser.PROPERTY_OUTLINE)) {
				CssBorderValue all = (CssBorderValue) entry.getValue();
				rv.leftWidth = rv.rightWidth = rv.topWidth = rv.bottomWidth = all
						.getWidth();
				rv.leftColor = rv.rightColor = rv.topColor = rv.bottomColor = all
						.getColor();
				rv.leftStyle = rv.rightStyle = rv.topStyle = rv.bottomStyle = all
						.getStyle();
			} else if (entry.getKey()
					.equals(CssOutlineColorParser.PROPERTY_OUTLINE_COLOR)) {
				CssColorValue color = (CssColorValue) entry.getValue();
				rv.leftColor = rv.rightColor = rv.topColor = rv.bottomColor = color;
			} else if (entry.getKey()
					.equals(CssOutlineStyleParser.PROPERTY_OUTLINE_STYLE)) {
				CssBorderStyleValue style = (CssBorderStyleValue) entry
						.getValue();
				rv.leftStyle = rv.rightStyle = rv.topStyle = rv.bottomStyle = style;
			} else if (entry.getKey()
					.equals(CssOutlineWidthParser.PROPERTY_OUTLINE_WIDTH)) {
				CssLength width = (CssLength) entry.getValue();
				rv.leftWidth = rv.rightWidth = rv.topWidth = rv.bottomWidth = width;
			}
		}

		return rv;
	}

	/**
	 * Create a configuration for a border.
	 */
	public static BorderRenderingConfiguration forBorder(QViewHelper helper) {
		Collection<Entry<String, Object>> properties = CssValueCreationToken
				.getOrderedProperties(helper,
						CssBorderBottomColorParser.PROPERTY_BORDER_BOTTOM_COLOR,
						CssBorderBottomLeftRadiusParser.PROPERTY_BORDER_BOTTOM_LEFT_RADIUS,
						CssBorderBottomParser.PROPERTY_BORDER_BOTTOM,
						CssBorderBottomRightRadiusParser.PROPERTY_BORDER_BOTTOM_RIGHT_RADIUS,
						CssBorderBottomStyleParser.PROPERTY_BORDER_BOTTOM_STYLE,
						CssBorderBottomWidthParser.PROPERTY_BORDER_BOTTOM_WIDTH,
						CssBorderColorParser.PROPERTY_BORDER_COLOR,
						CssBorderLeftColorParser.PROPERTY_BORDER_LEFT_COLOR,
						CssBorderLeftParser.PROPERTY_BORDER_LEFT,
						CssBorderLeftStyleParser.PROPERTY_BORDER_LEFT_STYLE,
						CssBorderLeftWidthParser.PROPERTY_BORDER_LEFT_WIDTH,
						CssBorderParser.PROPERTY_BORDER,
						CssBorderRadiusParser.PROPERTY_BORDER_RADIUS,
						CssBorderRightColorParser.PROPERTY_BORDER_RIGHT_COLOR,
						CssBorderRightParser.PROPERTY_BORDER_RIGHT,
						CssBorderRightStyleParser.PROPERTY_BORDER_RIGHT_STYLE,
						CssBorderRightWidthParser.PROPERTY_BORDER_RIGHT_WIDTH,
						CssBorderStyleParser.PROPERTY_BORDER_STYLE,
						CssBorderTopColorParser.PROPERTY_BORDER_TOP_COLOR,
						CssBorderTopLeftRadiusParser.PROPERTY_BORDER_TOP_LEFT_RADIUS,
						CssBorderTopParser.PROPERTY_BORDER_TOP,
						CssBorderTopRightRadiusParser.PROPERTY_BORDER_TOP_RIGHT_RADIUS,
						CssBorderTopStyleParser.PROPERTY_BORDER_TOP_STYLE,
						CssBorderTopWidthParser.PROPERTY_BORDER_TOP_WIDTH,
						CssBorderWidthParser.PROPERTY_BORDER_WIDTH);

		BorderRenderingConfiguration rv = new BorderRenderingConfiguration();
		for (Map.Entry<String, Object> entry : properties) {
			if (entry.getKey().equals(
					CssBorderBottomColorParser.PROPERTY_BORDER_BOTTOM_COLOR)) {
				rv.bottomColor = (CssColorValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderBottomLeftRadiusParser.PROPERTY_BORDER_BOTTOM_LEFT_RADIUS)) {
				rv.bottomLeftRadius = (CssBorderRadiusValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssBorderBottomParser.PROPERTY_BORDER_BOTTOM)) {
				rv.bottomWidth = ((CssBorderValue) entry.getValue()).getWidth();
				rv.bottomColor = ((CssBorderValue) entry.getValue()).getColor();
				rv.bottomStyle = ((CssBorderValue) entry.getValue()).getStyle();
			} else if (entry.getKey().equals(
					CssBorderBottomRightRadiusParser.PROPERTY_BORDER_BOTTOM_RIGHT_RADIUS)) {
				rv.bottomRightRadius = (CssBorderRadiusValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderBottomStyleParser.PROPERTY_BORDER_BOTTOM_STYLE)) {
				rv.bottomStyle = (CssBorderStyleValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderBottomWidthParser.PROPERTY_BORDER_BOTTOM_WIDTH)) {
				rv.bottomWidth = (CssLength) entry.getValue();
			} else if (entry.getKey()
					.equals(CssBorderColorParser.PROPERTY_BORDER_COLOR)) {
				List<CssColorValue> colors = (List<CssColorValue>) entry
						.getValue();
				if (colors.size() == 1) {
					rv.topColor = colors.get(0);
					rv.rightColor = colors.get(0);
					rv.bottomColor = colors.get(0);
					rv.leftColor = colors.get(0);
				} else if (colors.size() == 2) {
					rv.topColor = colors.get(0);
					rv.rightColor = colors.get(1);
					rv.bottomColor = colors.get(0);
					rv.leftColor = colors.get(1);
				} else if (colors.size() == 3) {
					rv.topColor = colors.get(0);
					rv.rightColor = colors.get(1);
					rv.bottomColor = colors.get(2);
					rv.leftColor = colors.get(1);
				} else if (colors.size() == 4) {
					rv.topColor = colors.get(0);
					rv.rightColor = colors.get(1);
					rv.bottomColor = colors.get(2);
					rv.leftColor = colors.get(3);
				} else {
					// TODO: somehow make this exception during parsing, not
					// rendering
					throw new RuntimeException(
							CssBorderColorParser.PROPERTY_BORDER_COLOR
									+ " must be 1-4 elements");
				}
			} else if (entry.getKey().equals(
					CssBorderLeftColorParser.PROPERTY_BORDER_LEFT_COLOR)) {
				rv.leftColor = (CssColorValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssBorderLeftParser.PROPERTY_BORDER_LEFT)) {
				rv.leftWidth = ((CssBorderValue) entry.getValue()).getWidth();
				rv.leftColor = ((CssBorderValue) entry.getValue()).getColor();
				rv.leftStyle = ((CssBorderValue) entry.getValue()).getStyle();
			} else if (entry.getKey().equals(
					CssBorderLeftStyleParser.PROPERTY_BORDER_LEFT_STYLE)) {
				rv.leftStyle = (CssBorderStyleValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderLeftWidthParser.PROPERTY_BORDER_LEFT_WIDTH)) {
				rv.leftWidth = (CssLength) entry.getValue();
			} else if (entry.getKey().equals(CssBorderParser.PROPERTY_BORDER)) {
				CssBorderValue all = (CssBorderValue) entry.getValue();
				rv.leftWidth = rv.rightWidth = rv.topWidth = rv.bottomWidth = all
						.getWidth();
				rv.leftColor = rv.rightColor = rv.topColor = rv.bottomColor = all
						.getColor();
				rv.leftStyle = rv.rightStyle = rv.topStyle = rv.bottomStyle = all
						.getStyle();
			} else if (entry.getKey()
					.equals(CssBorderRadiusParser.PROPERTY_BORDER_RADIUS)) {
				List<CssBorderRadiusValue> radii = (List<CssBorderRadiusValue>) entry
						.getValue();
				rv.topLeftRadius = radii.get(0);
				rv.topRightRadius = radii.get(1);
				rv.bottomRightRadius = radii.get(2);
				rv.bottomLeftRadius = radii.get(3);
			} else if (entry.getKey().equals(
					CssBorderRightColorParser.PROPERTY_BORDER_RIGHT_COLOR)) {
				rv.rightColor = (CssColorValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssBorderRightParser.PROPERTY_BORDER_RIGHT)) {
				rv.rightWidth = ((CssBorderValue) entry.getValue()).getWidth();
				rv.rightColor = ((CssBorderValue) entry.getValue()).getColor();
				rv.rightStyle = ((CssBorderValue) entry.getValue()).getStyle();
			} else if (entry.getKey().equals(
					CssBorderRightStyleParser.PROPERTY_BORDER_RIGHT_STYLE)) {
				rv.rightStyle = (CssBorderStyleValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderRightWidthParser.PROPERTY_BORDER_RIGHT_WIDTH)) {
				rv.rightWidth = (CssLength) entry.getValue();
			} else if (entry.getKey()
					.equals(CssBorderStyleParser.PROPERTY_BORDER_STYLE)) {
				List<CssBorderStyleValue> styles = (List<CssBorderStyleValue>) entry
						.getValue();
				if (styles.size() == 1) {
					rv.topStyle = styles.get(0);
					rv.rightStyle = styles.get(0);
					rv.bottomStyle = styles.get(0);
					rv.leftStyle = styles.get(0);
				} else if (styles.size() == 2) {
					rv.topStyle = styles.get(0);
					rv.rightStyle = styles.get(1);
					rv.bottomStyle = styles.get(0);
					rv.leftStyle = styles.get(1);
				} else if (styles.size() == 3) {
					rv.topStyle = styles.get(0);
					rv.rightStyle = styles.get(1);
					rv.bottomStyle = styles.get(2);
					rv.leftStyle = styles.get(1);
				} else if (styles.size() == 4) {
					rv.topStyle = styles.get(0);
					rv.rightStyle = styles.get(1);
					rv.bottomStyle = styles.get(2);
					rv.leftStyle = styles.get(3);
				} else {
					// TODO: somehow make this exception during parsing, not
					// rendering
					throw new RuntimeException(
							CssBorderStyleParser.PROPERTY_BORDER_STYLE
									+ " must be 1-4 elements");
				}
			} else if (entry.getKey().equals(
					CssBorderTopColorParser.PROPERTY_BORDER_TOP_COLOR)) {
				rv.topColor = (CssColorValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderTopLeftRadiusParser.PROPERTY_BORDER_TOP_LEFT_RADIUS)) {
				rv.topLeftRadius = (CssBorderRadiusValue) entry.getValue();
			} else if (entry.getKey()
					.equals(CssBorderTopParser.PROPERTY_BORDER_TOP)) {
				rv.topWidth = ((CssBorderValue) entry.getValue()).getWidth();
				rv.topColor = ((CssBorderValue) entry.getValue()).getColor();
				rv.topStyle = ((CssBorderValue) entry.getValue()).getStyle();
			} else if (entry.getKey().equals(
					CssBorderTopRightRadiusParser.PROPERTY_BORDER_TOP_RIGHT_RADIUS)) {
				rv.topRightRadius = (CssBorderRadiusValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderTopStyleParser.PROPERTY_BORDER_TOP_STYLE)) {
				rv.topStyle = (CssBorderStyleValue) entry.getValue();
			} else if (entry.getKey().equals(
					CssBorderTopWidthParser.PROPERTY_BORDER_TOP_WIDTH)) {
				rv.topWidth = (CssLength) entry.getValue();
			} else if (entry.getKey()
					.equals(CssBorderWidthParser.PROPERTY_BORDER_WIDTH)) {
				List<CssLength> widths = (List<CssLength>) entry.getValue();
				if (widths.size() == 1) {
					rv.topWidth = widths.get(0);
					rv.rightWidth = widths.get(0);
					rv.bottomWidth = widths.get(0);
					rv.leftWidth = widths.get(0);
				} else if (widths.size() == 2) {
					rv.topWidth = widths.get(0);
					rv.rightWidth = widths.get(1);
					rv.bottomWidth = widths.get(0);
					rv.leftWidth = widths.get(1);
				} else if (widths.size() == 3) {
					rv.topWidth = widths.get(0);
					rv.rightWidth = widths.get(1);
					rv.bottomWidth = widths.get(2);
					rv.leftWidth = widths.get(1);
				} else if (widths.size() == 4) {
					rv.topWidth = widths.get(0);
					rv.rightWidth = widths.get(1);
					rv.bottomWidth = widths.get(2);
					rv.leftWidth = widths.get(3);
				} else {
					// TODO: somehow make this exception during parsing, not
					// rendering
					throw new RuntimeException(
							CssBorderWidthParser.PROPERTY_BORDER_WIDTH
									+ " must be 1-4 elements");
				}
			}
		}

		return rv;
	}

	public CssLength leftWidth, rightWidth, topWidth, bottomWidth;
	public CssColorValue leftColor, topColor, rightColor, bottomColor;
	public CssBorderStyleValue leftStyle, topStyle, rightStyle, bottomStyle;
	public CssBorderRadiusValue topLeftRadius, topRightRadius,
			bottomRightRadius, bottomLeftRadius;
}