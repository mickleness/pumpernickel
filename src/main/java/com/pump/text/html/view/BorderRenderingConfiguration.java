package com.pump.text.html.view;

import java.util.List;

import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.border.CssBorderBottomColorParser;
import com.pump.text.html.css.border.CssBorderBottomStyleParser;
import com.pump.text.html.css.border.CssBorderBottomWidthParser;
import com.pump.text.html.css.border.CssBorderColorParser;
import com.pump.text.html.css.border.CssBorderLeftColorParser;
import com.pump.text.html.css.border.CssBorderLeftStyleParser;
import com.pump.text.html.css.border.CssBorderLeftWidthParser;
import com.pump.text.html.css.border.CssBorderRightColorParser;
import com.pump.text.html.css.border.CssBorderRightStyleParser;
import com.pump.text.html.css.border.CssBorderRightWidthParser;
import com.pump.text.html.css.border.CssBorderStyleParser;
import com.pump.text.html.css.border.CssBorderStyleValue;
import com.pump.text.html.css.border.CssBorderTopColorParser;
import com.pump.text.html.css.border.CssBorderTopStyleParser;
import com.pump.text.html.css.border.CssBorderTopWidthParser;
import com.pump.text.html.css.border.CssBorderWidthParser;

/**
 * This identifies the properties necessary to setup a BorderRendering.
 * <p>
 * For example: if your HTML defines "border" and "border-style" and
 * "border-right-style": all of those might contain information about the style.
 * This object flattens all of that into a unique set of attributes for each
 * edge.
 */
public class BorderRenderingConfiguration {

	public CssLength leftWidth, rightWidth, topWidth, bottomWidth;
	public CssColorValue leftColor, topColor, rightColor, bottomColor;
	public CssBorderStyleValue leftStyle, topStyle, rightStyle, bottomStyle;

	public BorderRenderingConfiguration(QViewHelper helper) {
		initWidths(helper);
		initColors(helper);
		initStyles(helper);
	}

	private void initStyles(QViewHelper helper) {
		topStyle = (CssBorderStyleValue) helper.getAttribute(
				CssBorderTopStyleParser.PROPERTY_BORDER_TOP_STYLE);
		rightStyle = (CssBorderStyleValue) helper.getAttribute(
				CssBorderRightStyleParser.PROPERTY_BORDER_RIGHT_STYLE);
		bottomStyle = (CssBorderStyleValue) helper.getAttribute(
				CssBorderBottomStyleParser.PROPERTY_BORDER_BOTTOM_STYLE);
		leftStyle = (CssBorderStyleValue) helper.getAttribute(
				CssBorderLeftStyleParser.PROPERTY_BORDER_LEFT_STYLE);

		List<CssBorderStyleValue> styles = (List<CssBorderStyleValue>) helper
				.getAttribute(CssBorderStyleParser.PROPERTY_BORDER_STYLE);
		if (styles != null && styles.size() == 1) {
			if (topStyle == null)
				topStyle = styles.get(0);
			if (rightStyle == null)
				rightStyle = styles.get(0);
			if (bottomStyle == null)
				bottomStyle = styles.get(0);
			if (leftStyle == null)
				leftStyle = styles.get(0);
		} else if (styles != null && styles.size() == 2) {
			if (topStyle == null)
				topStyle = styles.get(0);
			if (rightStyle == null)
				rightStyle = styles.get(1);
			if (bottomStyle == null)
				bottomStyle = styles.get(0);
			if (leftStyle == null)
				leftStyle = styles.get(1);
		} else if (styles != null && styles.size() == 3) {
			if (topStyle == null)
				topStyle = styles.get(0);
			if (rightStyle == null)
				rightStyle = styles.get(1);
			if (bottomStyle == null)
				bottomStyle = styles.get(2);
			if (leftStyle == null)
				leftStyle = styles.get(1);
		} else if (styles != null && styles.size() == 4) {
			if (topStyle == null)
				topStyle = styles.get(0);
			if (rightStyle == null)
				rightStyle = styles.get(1);
			if (bottomStyle == null)
				bottomStyle = styles.get(2);
			if (leftStyle == null)
				leftStyle = styles.get(3);
		} else if (styles != null) {
			// TODO: somehow make this exception during parsing, not rendering
			throw new RuntimeException(
					CssBorderStyleParser.PROPERTY_BORDER_STYLE
							+ " must be 1-4 elements");
		}
	}

	private void initWidths(QViewHelper helper) {
		topWidth = (CssLength) helper.getAttribute(
				CssBorderTopWidthParser.PROPERTY_BORDER_TOP_WIDTH);
		rightWidth = (CssLength) helper.getAttribute(
				CssBorderRightWidthParser.PROPERTY_BORDER_RIGHT_WIDTH);
		bottomWidth = (CssLength) helper.getAttribute(
				CssBorderBottomWidthParser.PROPERTY_BORDER_BOTTOM_WIDTH);
		leftWidth = (CssLength) helper.getAttribute(
				CssBorderLeftWidthParser.PROPERTY_BORDER_LEFT_WIDTH);

		List<CssLength> widths = (List<CssLength>) helper
				.getAttribute(CssBorderWidthParser.PROPERTY_BORDER_WIDTH);
		if (widths != null && widths.size() == 1) {
			if (topWidth == null)
				topWidth = widths.get(0);
			if (rightWidth == null)
				rightWidth = widths.get(0);
			if (bottomWidth == null)
				bottomWidth = widths.get(0);
			if (leftWidth == null)
				leftWidth = widths.get(0);
		} else if (widths != null && widths.size() == 2) {
			if (topWidth == null)
				topWidth = widths.get(0);
			if (rightWidth == null)
				rightWidth = widths.get(1);
			if (bottomWidth == null)
				bottomWidth = widths.get(0);
			if (leftWidth == null)
				leftWidth = widths.get(1);
		} else if (widths != null && widths.size() == 3) {
			if (topWidth == null)
				topWidth = widths.get(0);
			if (rightWidth == null)
				rightWidth = widths.get(1);
			if (bottomWidth == null)
				bottomWidth = widths.get(2);
			if (leftWidth == null)
				leftWidth = widths.get(1);
		} else if (widths != null && widths.size() == 4) {
			if (topWidth == null)
				topWidth = widths.get(0);
			if (rightWidth == null)
				rightWidth = widths.get(1);
			if (bottomWidth == null)
				bottomWidth = widths.get(2);
			if (leftWidth == null)
				leftWidth = widths.get(3);
		} else if (widths != null) {
			// TODO: somehow make this exception during parsing, not rendering
			throw new RuntimeException(
					CssBorderWidthParser.PROPERTY_BORDER_WIDTH
							+ " must be 1-4 elements");
		}
	}

	private void initColors(QViewHelper helper) {
		topColor = (CssColorValue) helper.getAttribute(
				CssBorderTopColorParser.PROPERTY_BORDER_TOP_COLOR);
		rightColor = (CssColorValue) helper.getAttribute(
				CssBorderRightColorParser.PROPERTY_BORDER_RIGHT_COLOR);
		bottomColor = (CssColorValue) helper.getAttribute(
				CssBorderBottomColorParser.PROPERTY_BORDER_BOTTOM_COLOR);
		leftColor = (CssColorValue) helper.getAttribute(
				CssBorderLeftColorParser.PROPERTY_BORDER_LEFT_COLOR);

		List<CssColorValue> colors = (List<CssColorValue>) helper
				.getAttribute(CssBorderColorParser.PROPERTY_BORDER_COLOR);
		if (colors != null && colors.size() == 1) {
			if (topColor == null)
				topColor = colors.get(0);
			if (rightColor == null)
				rightColor = colors.get(0);
			if (bottomColor == null)
				bottomColor = colors.get(0);
			if (leftColor == null)
				leftColor = colors.get(0);
		} else if (colors != null && colors.size() == 2) {
			if (topColor == null)
				topColor = colors.get(0);
			if (rightColor == null)
				rightColor = colors.get(1);
			if (bottomColor == null)
				bottomColor = colors.get(0);
			if (leftColor == null)
				leftColor = colors.get(1);
		} else if (colors != null && colors.size() == 3) {
			if (topColor == null)
				topColor = colors.get(0);
			if (rightColor == null)
				rightColor = colors.get(1);
			if (bottomColor == null)
				bottomColor = colors.get(2);
			if (leftColor == null)
				leftColor = colors.get(1);
		} else if (colors != null && colors.size() == 4) {
			if (topColor == null)
				topColor = colors.get(0);
			if (rightColor == null)
				rightColor = colors.get(1);
			if (bottomColor == null)
				bottomColor = colors.get(2);
			if (leftColor == null)
				leftColor = colors.get(3);
		} else if (colors != null) {
			// TODO: somehow make this exception during parsing, not rendering
			throw new RuntimeException(
					CssBorderColorParser.PROPERTY_BORDER_COLOR
							+ " must be 1-4 elements");
		}
	}
}
