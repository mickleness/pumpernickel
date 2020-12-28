package com.pump.text.html.view;

import java.util.List;

import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.border.CssBorderBottomColorParser;
import com.pump.text.html.css.border.CssBorderBottomParser;
import com.pump.text.html.css.border.CssBorderBottomStyleParser;
import com.pump.text.html.css.border.CssBorderBottomWidthParser;
import com.pump.text.html.css.border.CssBorderColorParser;
import com.pump.text.html.css.border.CssBorderLeftColorParser;
import com.pump.text.html.css.border.CssBorderLeftParser;
import com.pump.text.html.css.border.CssBorderLeftStyleParser;
import com.pump.text.html.css.border.CssBorderLeftWidthParser;
import com.pump.text.html.css.border.CssBorderParser;
import com.pump.text.html.css.border.CssBorderRightColorParser;
import com.pump.text.html.css.border.CssBorderRightParser;
import com.pump.text.html.css.border.CssBorderRightStyleParser;
import com.pump.text.html.css.border.CssBorderRightWidthParser;
import com.pump.text.html.css.border.CssBorderStyleParser;
import com.pump.text.html.css.border.CssBorderStyleValue;
import com.pump.text.html.css.border.CssBorderTopColorParser;
import com.pump.text.html.css.border.CssBorderTopParser;
import com.pump.text.html.css.border.CssBorderTopStyleParser;
import com.pump.text.html.css.border.CssBorderTopWidthParser;
import com.pump.text.html.css.border.CssBorderValue;
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
		{
			CssBorderValue all = (CssBorderValue) helper
					.getAttribute(CssBorderParser.PROPERTY_BORDER, false);
			if (all != null) {
				leftWidth = rightWidth = topWidth = bottomWidth = all
						.getWidth();
				leftColor = rightColor = topColor = bottomColor = all
						.getColor();
				leftStyle = rightStyle = topStyle = bottomStyle = all
						.getStyle();
			}
		}

		{
			CssBorderValue left = (CssBorderValue) helper.getAttribute(
					CssBorderLeftParser.PROPERTY_BORDER_LEFT, false);
			if (left != null) {
				leftWidth = left.getWidth();
				leftColor = left.getColor();
				leftStyle = left.getStyle();
			}
		}

		{
			CssBorderValue right = (CssBorderValue) helper.getAttribute(
					CssBorderRightParser.PROPERTY_BORDER_RIGHT, false);
			if (right != null) {
				rightWidth = right.getWidth();
				rightColor = right.getColor();
				rightStyle = right.getStyle();
			}
		}

		{
			CssBorderValue top = (CssBorderValue) helper.getAttribute(
					CssBorderTopParser.PROPERTY_BORDER_TOP, false);
			if (top != null) {
				topWidth = top.getWidth();
				topColor = top.getColor();
				topStyle = top.getStyle();
			}
		}

		{
			CssBorderValue bottom = (CssBorderValue) helper.getAttribute(
					CssBorderBottomParser.PROPERTY_BORDER_BOTTOM, false);
			if (bottom != null) {
				bottomWidth = bottom.getWidth();
				bottomColor = bottom.getColor();
				bottomStyle = bottom.getStyle();
			}
		}

		initWidths(helper);
		initColors(helper);
		initStyles(helper);
	}

	private void initStyles(QViewHelper helper) {
		List<CssBorderStyleValue> styles = (List<CssBorderStyleValue>) helper
				.getAttribute(CssBorderStyleParser.PROPERTY_BORDER_STYLE,
						false);
		if (styles != null && styles.size() == 1) {
			topStyle = styles.get(0);
			rightStyle = styles.get(0);
			bottomStyle = styles.get(0);
			leftStyle = styles.get(0);
		} else if (styles != null && styles.size() == 2) {
			topStyle = styles.get(0);
			rightStyle = styles.get(1);
			bottomStyle = styles.get(0);
			leftStyle = styles.get(1);
		} else if (styles != null && styles.size() == 3) {
			topStyle = styles.get(0);
			rightStyle = styles.get(1);
			bottomStyle = styles.get(2);
			leftStyle = styles.get(1);
		} else if (styles != null && styles.size() == 4) {
			topStyle = styles.get(0);
			rightStyle = styles.get(1);
			bottomStyle = styles.get(2);
			leftStyle = styles.get(3);
		} else if (styles != null) {
			// TODO: somehow make this exception during parsing, not rendering
			throw new RuntimeException(
					CssBorderStyleParser.PROPERTY_BORDER_STYLE
							+ " must be 1-4 elements");
		}

		CssBorderStyleValue t = (CssBorderStyleValue) helper.getAttribute(
				CssBorderTopStyleParser.PROPERTY_BORDER_TOP_STYLE, false);
		CssBorderStyleValue r = (CssBorderStyleValue) helper.getAttribute(
				CssBorderRightStyleParser.PROPERTY_BORDER_RIGHT_STYLE, false);
		CssBorderStyleValue b = (CssBorderStyleValue) helper.getAttribute(
				CssBorderBottomStyleParser.PROPERTY_BORDER_BOTTOM_STYLE, false);
		CssBorderStyleValue l = (CssBorderStyleValue) helper.getAttribute(
				CssBorderLeftStyleParser.PROPERTY_BORDER_LEFT_STYLE, false);

		if (t != null)
			topStyle = t;
		if (l != null)
			leftStyle = l;
		if (r != null)
			rightStyle = r;
		if (b != null)
			bottomStyle = b;
	}

	private void initWidths(QViewHelper helper) {
		List<CssLength> widths = (List<CssLength>) helper.getAttribute(
				CssBorderWidthParser.PROPERTY_BORDER_WIDTH, false);
		if (widths != null && widths.size() == 1) {
			topWidth = widths.get(0);
			rightWidth = widths.get(0);
			bottomWidth = widths.get(0);
			leftWidth = widths.get(0);
		} else if (widths != null && widths.size() == 2) {
			topWidth = widths.get(0);
			rightWidth = widths.get(1);
			bottomWidth = widths.get(0);
			leftWidth = widths.get(1);
		} else if (widths != null && widths.size() == 3) {
			topWidth = widths.get(0);
			rightWidth = widths.get(1);
			bottomWidth = widths.get(2);
			leftWidth = widths.get(1);
		} else if (widths != null && widths.size() == 4) {
			topWidth = widths.get(0);
			rightWidth = widths.get(1);
			bottomWidth = widths.get(2);
			leftWidth = widths.get(3);
		} else if (widths != null) {
			// TODO: somehow make this exception during parsing, not rendering
			throw new RuntimeException(
					CssBorderWidthParser.PROPERTY_BORDER_WIDTH
							+ " must be 1-4 elements");
		}
		CssLength t = (CssLength) helper.getAttribute(
				CssBorderTopWidthParser.PROPERTY_BORDER_TOP_WIDTH, false);
		CssLength r = (CssLength) helper.getAttribute(
				CssBorderRightWidthParser.PROPERTY_BORDER_RIGHT_WIDTH, false);
		CssLength b = (CssLength) helper.getAttribute(
				CssBorderBottomWidthParser.PROPERTY_BORDER_BOTTOM_WIDTH, false);
		CssLength l = (CssLength) helper.getAttribute(
				CssBorderLeftWidthParser.PROPERTY_BORDER_LEFT_WIDTH, false);

		if (t != null)
			topWidth = t;
		if (l != null)
			leftWidth = l;
		if (r != null)
			rightWidth = r;
		if (b != null)
			bottomWidth = b;
	}

	private void initColors(QViewHelper helper) {

		List<CssColorValue> colors = (List<CssColorValue>) helper.getAttribute(
				CssBorderColorParser.PROPERTY_BORDER_COLOR, false);
		if (colors != null && colors.size() == 1) {
			topColor = colors.get(0);
			rightColor = colors.get(0);
			bottomColor = colors.get(0);
			leftColor = colors.get(0);
		} else if (colors != null && colors.size() == 2) {
			topColor = colors.get(0);
			rightColor = colors.get(1);
			bottomColor = colors.get(0);
			leftColor = colors.get(1);
		} else if (colors != null && colors.size() == 3) {
			topColor = colors.get(0);
			rightColor = colors.get(1);
			bottomColor = colors.get(2);
			leftColor = colors.get(1);
		} else if (colors != null && colors.size() == 4) {
			topColor = colors.get(0);
			rightColor = colors.get(1);
			bottomColor = colors.get(2);
			leftColor = colors.get(3);
		} else if (colors != null) {
			// TODO: somehow make this exception during parsing, not rendering
			throw new RuntimeException(
					CssBorderColorParser.PROPERTY_BORDER_COLOR
							+ " must be 1-4 elements");
		}

		CssColorValue t = (CssColorValue) helper.getAttribute(
				CssBorderTopColorParser.PROPERTY_BORDER_TOP_COLOR, false);
		CssColorValue r = (CssColorValue) helper.getAttribute(
				CssBorderRightColorParser.PROPERTY_BORDER_RIGHT_COLOR, false);
		CssColorValue b = (CssColorValue) helper.getAttribute(
				CssBorderBottomColorParser.PROPERTY_BORDER_BOTTOM_COLOR, false);
		CssColorValue l = (CssColorValue) helper.getAttribute(
				CssBorderLeftColorParser.PROPERTY_BORDER_LEFT_COLOR, false);

		if (t != null)
			topColor = t;
		if (l != null)
			leftColor = l;
		if (r != null)
			rightColor = r;
		if (b != null)
			bottomColor = b;
	}
}
