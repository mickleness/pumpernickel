package com.pump.text.html.view;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssPropertyParser;
import com.pump.text.html.css.CssValue;
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
	 * This helps sort values according to their creation timestamps, so we can
	 * safely identify the last (most recently defined) CSS statement.
	 */
	static class Attribute {
		CssValueCreationToken creationToken;
		CssPropertyParser<?> parser;
		Object value;

		Attribute(CssPropertyParser<?> parser) {
			this.parser = parser;
		}

		/**
		 * Populate the value field and creationToken
		 */
		void resolve(QViewHelper helper) {
			value = helper.getAttribute(parser.getPropertyName(), false);
			if (value instanceof CssValue) {
				creationToken = ((CssValue) value).getCreationToken();
			} else if (value instanceof List) {
				creationToken = ((CssValue) ((List) value).get(0))
						.getCreationToken();
			}
		}
	}

	/**
	 * This sorts Attributes according to their creation token timestamp's. That
	 * is: the Attributes are sorted into the order they are parsed.
	 */
	private static Comparator<Attribute> ATTRIBUTE_COMPARATOR = new Comparator<Attribute>() {

		@Override
		public int compare(Attribute o1, Attribute o2) {
			return o1.creationToken.compareTo(o2.creationToken);
		}

	};

	/**
	 * Create a configuration for an outline. This is much simpler than a
	 * border, because there are only a few CSS properties to consult.
	 */
	public static BorderRenderingConfiguration forOutline(QViewHelper helper) {
		Collection<Attribute> attrs = new HashSet<>();
		attrs.add(new Attribute(new CssOutlineParser()));
		attrs.add(new Attribute(new CssOutlineColorParser()));
		attrs.add(new Attribute(new CssOutlineStyleParser()));
		attrs.add(new Attribute(new CssOutlineWidthParser()));

		// sort all properties by timestamp so the most recent one "wins":
		Collection<Attribute> sortedAttrs = new TreeSet<>(ATTRIBUTE_COMPARATOR);

		for (Attribute attr : attrs) {
			attr.resolve(helper);
			if (attr.creationToken != null)
				sortedAttrs.add(attr);
		}

		BorderRenderingConfiguration rv = new BorderRenderingConfiguration();
		for (Attribute attr : sortedAttrs) {
			if (attr.parser instanceof CssOutlineParser) {
				CssBorderValue all = (CssBorderValue) helper
						.getAttribute(CssOutlineParser.PROPERTY_OUTLINE, false);
				rv.leftWidth = rv.rightWidth = rv.topWidth = rv.bottomWidth = all
						.getWidth();
				rv.leftColor = rv.rightColor = rv.topColor = rv.bottomColor = all
						.getColor();
				rv.leftStyle = rv.rightStyle = rv.topStyle = rv.bottomStyle = all
						.getStyle();
			} else if (attr.parser instanceof CssOutlineColorParser) {
				CssColorValue color = (CssColorValue) helper.getAttribute(
						CssOutlineColorParser.PROPERTY_OUTLINE_COLOR, false);
				rv.leftColor = rv.rightColor = rv.topColor = rv.bottomColor = color;
			} else if (attr.parser instanceof CssOutlineStyleParser) {
				CssBorderStyleValue style = (CssBorderStyleValue) helper
						.getAttribute(
								CssOutlineStyleParser.PROPERTY_OUTLINE_STYLE,
								false);
				rv.leftStyle = rv.rightStyle = rv.topStyle = rv.bottomStyle = style;
			} else if (attr.parser instanceof CssOutlineWidthParser) {
				CssLength width = (CssLength) helper.getAttribute(
						CssOutlineWidthParser.PROPERTY_OUTLINE_WIDTH, false);
				rv.leftWidth = rv.rightWidth = rv.topWidth = rv.bottomWidth = width;
			}
		}

		return rv;
	}

	/**
	 * Create a configuration for a border.
	 */
	public static BorderRenderingConfiguration forBorder(QViewHelper helper) {

		Collection<Attribute> attrs = new HashSet<>();
		attrs.add(new Attribute(new CssBorderBottomColorParser()));
		attrs.add(new Attribute(new CssBorderBottomLeftRadiusParser()));
		attrs.add(new Attribute(new CssBorderBottomParser()));
		attrs.add(new Attribute(new CssBorderBottomRightRadiusParser()));
		attrs.add(new Attribute(new CssBorderBottomStyleParser()));
		attrs.add(new Attribute(new CssBorderBottomWidthParser()));
		attrs.add(new Attribute(new CssBorderColorParser()));
		attrs.add(new Attribute(new CssBorderLeftColorParser()));
		attrs.add(new Attribute(new CssBorderLeftParser()));
		attrs.add(new Attribute(new CssBorderLeftStyleParser()));
		attrs.add(new Attribute(new CssBorderLeftWidthParser()));
		attrs.add(new Attribute(new CssBorderParser()));
		attrs.add(new Attribute(new CssBorderRadiusParser()));
		attrs.add(new Attribute(new CssBorderRightColorParser()));
		attrs.add(new Attribute(new CssBorderRightParser()));
		attrs.add(new Attribute(new CssBorderRightStyleParser()));
		attrs.add(new Attribute(new CssBorderRightWidthParser()));
		attrs.add(new Attribute(new CssBorderStyleParser()));
		attrs.add(new Attribute(new CssBorderTopColorParser()));
		attrs.add(new Attribute(new CssBorderTopLeftRadiusParser()));
		attrs.add(new Attribute(new CssBorderTopParser()));
		attrs.add(new Attribute(new CssBorderTopRightRadiusParser()));
		attrs.add(new Attribute(new CssBorderTopStyleParser()));
		attrs.add(new Attribute(new CssBorderTopWidthParser()));
		attrs.add(new Attribute(new CssBorderWidthParser()));

		// sort all properties by timestamp so the most recent one "wins":
		Collection<Attribute> sortedAttrs = new TreeSet<>(ATTRIBUTE_COMPARATOR);

		for (Attribute attr : attrs) {
			attr.resolve(helper);
			if (attr.creationToken != null)
				sortedAttrs.add(attr);
		}

		BorderRenderingConfiguration rv = new BorderRenderingConfiguration();
		for (Attribute attr : sortedAttrs) {
			if (attr.parser instanceof CssBorderBottomColorParser) {
				rv.bottomColor = (CssColorValue) attr.value;
			} else if (attr.parser instanceof CssBorderBottomLeftRadiusParser) {
				rv.bottomLeftRadius = (CssBorderRadiusValue) attr.value;
			} else if (attr.parser instanceof CssBorderBottomParser) {
				rv.bottomWidth = ((CssBorderValue) attr.value).getWidth();
				rv.bottomColor = ((CssBorderValue) attr.value).getColor();
				rv.bottomStyle = ((CssBorderValue) attr.value).getStyle();
			} else if (attr.parser instanceof CssBorderBottomRightRadiusParser) {
				rv.bottomRightRadius = (CssBorderRadiusValue) attr.value;
			} else if (attr.parser instanceof CssBorderBottomStyleParser) {
				rv.bottomStyle = (CssBorderStyleValue) attr.value;
			} else if (attr.parser instanceof CssBorderBottomWidthParser) {
				rv.bottomWidth = (CssLength) attr.value;
			} else if (attr.parser instanceof CssBorderColorParser) {
				List<CssColorValue> colors = (List<CssColorValue>) attr.value;
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
			} else if (attr.parser instanceof CssBorderLeftColorParser) {
				rv.leftColor = (CssColorValue) attr.value;
			} else if (attr.parser instanceof CssBorderLeftParser) {
				rv.leftWidth = ((CssBorderValue) attr.value).getWidth();
				rv.leftColor = ((CssBorderValue) attr.value).getColor();
				rv.leftStyle = ((CssBorderValue) attr.value).getStyle();
			} else if (attr.parser instanceof CssBorderLeftStyleParser) {
				rv.leftStyle = (CssBorderStyleValue) attr.value;
			} else if (attr.parser instanceof CssBorderLeftWidthParser) {
				rv.leftWidth = (CssLength) attr.value;
			} else if (attr.parser instanceof CssBorderParser) {
				CssBorderValue all = (CssBorderValue) attr.value;
				rv.leftWidth = rv.rightWidth = rv.topWidth = rv.bottomWidth = all
						.getWidth();
				rv.leftColor = rv.rightColor = rv.topColor = rv.bottomColor = all
						.getColor();
				rv.leftStyle = rv.rightStyle = rv.topStyle = rv.bottomStyle = all
						.getStyle();
			} else if (attr.parser instanceof CssBorderRadiusParser) {
				List<CssBorderRadiusValue> radii = (List<CssBorderRadiusValue>) attr.value;
				rv.topLeftRadius = radii.get(0);
				rv.topRightRadius = radii.get(1);
				rv.bottomRightRadius = radii.get(2);
				rv.bottomLeftRadius = radii.get(3);
			} else if (attr.parser instanceof CssBorderRightColorParser) {
				rv.rightColor = (CssColorValue) attr.value;
			} else if (attr.parser instanceof CssBorderRightParser) {
				rv.rightWidth = ((CssBorderValue) attr.value).getWidth();
				rv.rightColor = ((CssBorderValue) attr.value).getColor();
				rv.rightStyle = ((CssBorderValue) attr.value).getStyle();
			} else if (attr.parser instanceof CssBorderRightStyleParser) {
				rv.rightStyle = (CssBorderStyleValue) attr.value;
			} else if (attr.parser instanceof CssBorderRightWidthParser) {
				rv.rightWidth = (CssLength) attr.value;
			} else if (attr.parser instanceof CssBorderStyleParser) {
				List<CssBorderStyleValue> styles = (List<CssBorderStyleValue>) attr.value;
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
			} else if (attr.parser instanceof CssBorderTopColorParser) {
				rv.topColor = (CssColorValue) attr.value;
			} else if (attr.parser instanceof CssBorderTopLeftRadiusParser) {
				rv.topLeftRadius = (CssBorderRadiusValue) attr.value;
			} else if (attr.parser instanceof CssBorderTopParser) {
				rv.topWidth = ((CssBorderValue) attr.value).getWidth();
				rv.topColor = ((CssBorderValue) attr.value).getColor();
				rv.topStyle = ((CssBorderValue) attr.value).getStyle();
			} else if (attr.parser instanceof CssBorderTopRightRadiusParser) {
				rv.topRightRadius = (CssBorderRadiusValue) attr.value;
			} else if (attr.parser instanceof CssBorderTopStyleParser) {
				rv.topStyle = (CssBorderStyleValue) attr.value;
			} else if (attr.parser instanceof CssBorderTopWidthParser) {
				rv.topWidth = (CssLength) attr.value;
			} else if (attr.parser instanceof CssBorderWidthParser) {
				List<CssLength> widths = (List<CssLength>) attr.value;
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
