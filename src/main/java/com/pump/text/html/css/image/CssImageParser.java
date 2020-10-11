package com.pump.text.html.css.image;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.CSS;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssParserUtils;
import com.pump.text.html.css.CssPropertyParser;
import com.pump.text.html.css.image.CssLinearGradientImageValue.ColorStop;
import com.pump.text.html.css.image.CssLinearGradientImageValue.Direction;

public class CssImageParser implements CssPropertyParser<CssImageValue> {

	@Override
	public String getPropertyName() {
		return CSS.Attribute.BACKGROUND_IMAGE.toString();
	}

	@Override
	public CssImageValue parse(final String cssString) {
		int index = 0;

		// consume leading whitespace
		while (index < cssString.length()
				&& Character.isWhitespace(cssString.charAt(index))) {
			index++;
		}

		List<CssImageValue> images = new ArrayList<>();
		while (index < cssString.length()) {
			index = parseImageValue(cssString, index, images);

			// consume trailing whitespace
			while (index < cssString.length()
					&& Character.isWhitespace(cssString.charAt(index))) {
				index++;
			}

			// consume possible comma
			if (index < cssString.length() && cssString.charAt(index) == ',') {
				index++;
			}

			// consume leading whitespace
			while (index < cssString.length()
					&& Character.isWhitespace(cssString.charAt(index))) {
				index++;
			}
		}
		if (images.size() == 1)
			return images.get(0);
		return new CssMultipleImageValue(cssString, images);
	}

	protected int parseImageValue(String cssString, int index,
			List<CssImageValue> dest) {
		String s = cssString.toLowerCase();

		s = s.substring(index);
		if (s.startsWith("url(\"")) {
			int i2 = s.indexOf("\")");
			String cssStr = cssString.substring(index, i2 + "\")".length());
			String urlStr = cssString.substring(index + "url(\"".length(), i2);
			dest.add(new CssUrlImageValue(cssStr, urlStr));
			return index + i2 + "\")".length();
		} else if (s.startsWith("url(")) {
			// I'm not sure why, but quotations appear to be automatically
			// stripped away.
			int i2 = s.indexOf(")");

			// look for ")," or ") " or ")\n"
			while (true) {
				char nextChar = i2 + 1 < s.length() ? s.charAt(i2 + 1)
						: CharacterIterator.DONE;
				if (nextChar == ',' || Character.isWhitespace(nextChar)
						|| nextChar == CharacterIterator.DONE) {
					break;
				}
				i2 = s.indexOf(")", i2 + 1);
			}
			String cssStr = cssString.substring(index,
					index + i2 + ")".length());
			String urlStr = cssString.substring(index + "url(".length(),
					index + i2);
			dest.add(new CssUrlImageValue(cssStr, urlStr));
			return index + i2 + ")".length();
		} else if (s.startsWith("linear-gradient(")) {
			StringBuilder sb = new StringBuilder();
			int i2 = CssParserUtils.getClosingParentheses(cssString,
					index + "linear-gradient".length(), sb);
			String cssStr = cssString.substring(index, i2 + ")".length());
			CssLinearGradientImageValue gradient = parseLinearGradient(cssStr,
					sb.toString(), false);
			dest.add(gradient);
			return i2 + ")".length();
		} else if (s.startsWith("repeating-linear-gradient(")) {
			StringBuilder sb = new StringBuilder();
			int i2 = CssParserUtils.getClosingParentheses(cssString,
					index + "repeating-linear-gradient".length(), sb);
			String cssStr = cssString.substring(index, i2 + ")".length());
			CssLinearGradientImageValue gradient = parseLinearGradient(cssStr,
					sb.toString(), true);
			dest.add(gradient);
			return i2 + ")".length();
			// TODO: explore conic gradients, see
			// https://developer.mozilla.org/en-US/docs/Web/CSS/conic-gradient
			// TODO:
			// } else if (s.startsWith("radial-gradient(")) {
			// StringBuilder sb = new StringBuilder();
			// int i2 = CssParserUtils.getClosingParentheses(value,
			// index + "radial-gradient".length(), sb);
			// dest.add(new RadialGradientImageValue(sb.toString());
			// return i2 + ")".length();
			// } else if (s.startsWith("repeating-radial-gradient(")) {
			// StringBuilder sb = new StringBuilder();
			// int i2 = CssParserUtils.getClosingParentheses(value,
			// index + "repeating-radial-gradient".length(), sb);
			// dest.add(new RepeatingRadialGradientImageValue(sb.toString()));
			// return i2 + ")".length();
		} else if (s.startsWith("none")) {
			dest.add(new CssNoneImageValue());
			return index + "none".length();
		} else if (s.startsWith("inherit") || s.startsWith("initial")) {
			throw new IllegalArgumentException(
					"the \"inherit\" and \"initial\" background fills are currently unsupported");
		}

		throw new IllegalArgumentException(
				"unsupported background image value \""
						+ cssString.substring(index) + "\"");
	}

	private CssLinearGradientImageValue parseLinearGradient(String cssStr,
			String linearGradientArgs, boolean repeating) {

		List<String> args = CssParserUtils
				.splitCommaSeparatedList(linearGradientArgs, true);
		int index = 0;
		String firstArg = args.get(0).toLowerCase();
		Direction direction = null;
		double theta = -Math.PI / 2;
		List<Object> colorStopsAndHints = new ArrayList<>();

		if (firstArg.startsWith("to ")) {
			direction = parseToDirection(firstArg.substring("to ".length()));
			index = 1;
		} else if (firstArg.endsWith("turn")) {
			theta = parseValueWithoutUnit(firstArg, "turn") * 2 * Math.PI
					+ Math.PI / 2;
			index = 1;
		} else if (firstArg.endsWith("deg") || firstArg.equals("0")) {
			theta = parseValueWithoutUnit(firstArg, "deg") * Math.PI / 180
					+ Math.PI / 2;
			index = 1;
		} else if (firstArg.endsWith("rad")) {
			theta = parseValueWithoutUnit(firstArg, "rad") + Math.PI / 2;
			index = 1;
		}

		while (index < args.size()) {
			String arg = args.get(index);
			int j1 = arg.indexOf(')');
			int j2 = arg.indexOf(' ');
			if (j1 == -1 && j2 == -1) {
				// we have no space or close parentheses
				try {
					// this might be an interpolation hint like "50%"
					colorStopsAndHints.add(new CssLength(arg));
				} catch (RuntimeException e) {
					// ... or it might be a color like "#ef2"
					colorStopsAndHints.add(new ColorStop(arg, null));
				}
			} else if (j1 == -1) {
				// we have a space
				colorStopsAndHints.add(new ColorStop(arg.substring(0, j2),
						arg.substring(j2 + 1).trim()));
			} else if (j2 == -1) {
				// we have a close parentheses but no space:
				colorStopsAndHints.add(new ColorStop(arg, null));
			} else {
				// we have both a space and a close parentheses
				colorStopsAndHints.add(new ColorStop(arg.substring(0, j1 + 1),
						arg.substring(j1 + 1).trim()));
			}
			index++;
		}

		if (direction != null)
			return new CssLinearGradientImageValue(cssStr, direction,
					colorStopsAndHints, repeating);
		return new CssLinearGradientImageValue(cssStr, theta,
				colorStopsAndHints, repeating);
	}

	/**
	 * Parse a numeric value and strip away the unit
	 * 
	 * @param arg
	 *            a value such as "13deg" or ".5rad"
	 * @param unit
	 *            a unit such as "rad" or "deg"
	 * @return the numeric value
	 */
	private double parseValueWithoutUnit(String arg, String unit) {
		arg = arg.replace(unit, "").trim();
		return Double.parseDouble(arg);
	}

	/**
	 * Populate the "direction" field based on the argument
	 * 
	 * @param arg
	 *            "left" or "top left"
	 */
	private Direction parseToDirection(String arg) {
		arg = arg.toLowerCase();
		boolean top = arg.contains("top");
		boolean left = arg.contains("left");
		boolean bottom = arg.contains("bottom");
		boolean right = arg.contains("right");
		if (top && left && !bottom && !right) {
			return Direction.TO_TOP_LEFT;
		} else if (top && !left && !bottom && right) {
			return Direction.TO_TOP_RIGHT;
		} else if (top && !left && !bottom && !right) {
			return Direction.TO_TOP;
		} else if (!top && left && !bottom && !right) {
			return Direction.TO_LEFT;
		} else if (!top && !left && bottom && !right) {
			return Direction.TO_BOTTOM;
		} else if (!top && left && bottom && !right) {
			return Direction.TO_BOTTOM_LEFT;
		} else if (!top && !left && bottom && right) {
			return Direction.TO_BOTTOM_RIGHT;
		} else if (!top && left && !bottom && right) {
			return Direction.TO_RIGHT;
		}
		throw new IllegalArgumentException(
				"unrecognized direction \"" + arg + "\"");
	}

}
