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
package com.pump.text.html.css;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.text.html.CSS;

import com.pump.awt.HSLColor;

/**
 * This parses a CSS "color" attribute.
 * <p>
 * This is similar to the {@link CSS#stringToColor(String)} method, except that
 * method is based on an older CSS specification. This class supports 100+ more
 * named colors, alpha channels, the "hsl" and "hsla" identifier, and a few
 * additional features.
 */
public class CssColorParser implements CssPropertyParser<CssColorValue> {

	public static final String PROPERTY_COLOR = "color";

	private static Map<String, CssColorValue> namedColors = new HashMap<>();

	private static void initNamedColor(String name, int red, int green,
			int blue) {
		initNamedColor(name, red, green, blue, 255);
	}

	private static void initNamedColor(String name, int red, int green,
			int blue, int alpha) {
		namedColors.put(name, new CssColorValue(name, red, green, blue, alpha));
	}

	static {
		initNamedColor("lightsalmon", 255, 160, 122);
		initNamedColor("salmon", 250, 128, 114);
		initNamedColor("darksalmon", 233, 150, 122);
		initNamedColor("lightcoral", 240, 128, 128);
		initNamedColor("indianred", 205, 92, 92);
		initNamedColor("crimson", 220, 20, 60);
		initNamedColor("firebrick", 178, 34, 34);
		initNamedColor("red", 255, 0, 0);
		initNamedColor("darkred", 139, 0, 0);

		initNamedColor("coral", 255, 127, 80);
		initNamedColor("tomato", 255, 99, 71);
		initNamedColor("orangered", 255, 69, 0);
		initNamedColor("gold", 255, 215, 0);
		initNamedColor("orange", 255, 165, 0);
		initNamedColor("darkorange", 255, 140, 0);

		initNamedColor("lightyellow", 255, 255, 224);
		initNamedColor("lemonchiffon", 255, 250, 205);
		initNamedColor("lightgoldenrodyellow", 250, 250, 210);
		initNamedColor("papayawhip", 255, 239, 213);
		initNamedColor("moccasin", 255, 228, 181);
		initNamedColor("peachpuff", 255, 218, 185);
		initNamedColor("palegoldenrod", 238, 232, 170);
		initNamedColor("khaki", 240, 230, 140);
		initNamedColor("darkkhaki", 189, 183, 107);
		initNamedColor("yellow", 255, 255, 0);

		initNamedColor("lawngreen", 124, 252, 0);
		initNamedColor("chartreuse", 127, 255, 0);
		initNamedColor("limegreen", 50, 205, 50);
		initNamedColor("lime", 0, 255, 0);
		initNamedColor("forestgreen", 34, 139, 34);
		initNamedColor("green", 0, 128, 0);
		initNamedColor("darkgreen", 0, 100, 0);
		initNamedColor("greenyellow", 173, 255, 47);
		initNamedColor("yellowgreen", 154, 205, 50);
		initNamedColor("springgreen", 0, 255, 127);
		initNamedColor("mediumspringgreen", 0, 250, 154);
		initNamedColor("lightgreen", 144, 238, 144);
		initNamedColor("palegreen", 152, 251, 152);
		initNamedColor("darkseagreen", 143, 188, 143);
		initNamedColor("mediumseagreen", 60, 179, 113);
		initNamedColor("seagreen", 46, 139, 87);
		initNamedColor("olive", 128, 128, 0);
		initNamedColor("darkolivegreen", 85, 107, 47);
		initNamedColor("olivedrab", 107, 142, 35);

		initNamedColor("lightcyan", 224, 255, 255);
		initNamedColor("cyan", 0, 255, 255);
		initNamedColor("aqua", 0, 255, 255);
		initNamedColor("aquamarine", 127, 255, 212);
		initNamedColor("mediumaquamarine", 102, 205, 170);
		initNamedColor("paleturquoise", 175, 238, 238);
		initNamedColor("turquoise", 64, 224, 208);
		initNamedColor("mediumturquoise", 72, 209, 204);
		initNamedColor("darkturquoise", 0, 206, 209);
		initNamedColor("lightseagreen", 32, 178, 170);
		initNamedColor("cadetblue", 95, 158, 160);
		initNamedColor("darkcyan", 0, 139, 139);
		initNamedColor("teal", 0, 128, 128);

		initNamedColor("powderblue", 176, 224, 230);
		initNamedColor("lightblue", 173, 216, 230);
		initNamedColor("lightskyblue", 135, 206, 250);
		initNamedColor("skyblue", 135, 206, 235);
		initNamedColor("deepskyblue", 0, 191, 255);
		initNamedColor("lightsteelblue", 176, 196, 222);
		initNamedColor("dodgerblue", 30, 144, 255);
		initNamedColor("cornflowerblue", 100, 149, 237);
		initNamedColor("steelblue", 70, 130, 180);
		initNamedColor("royalblue", 65, 105, 225);
		initNamedColor("blue", 0, 0, 255);
		initNamedColor("mediumblue", 0, 0, 205);
		initNamedColor("darkblue", 0, 0, 139);
		initNamedColor("navy", 0, 0, 128);
		initNamedColor("midnightblue", 25, 25, 112);
		initNamedColor("mediumslateblue", 123, 104, 238);
		initNamedColor("slateblue", 106, 90, 205);
		initNamedColor("darkslateblue", 72, 61, 139);

		initNamedColor("lavender", 230, 230, 250);
		initNamedColor("thistle", 216, 191, 216);
		initNamedColor("plum", 221, 160, 221);
		initNamedColor("violet", 238, 130, 238);
		initNamedColor("orchid", 218, 112, 214);
		initNamedColor("fuchsia", 255, 0, 255);
		initNamedColor("magenta", 255, 0, 255);
		initNamedColor("mediumorchid", 186, 85, 211);
		initNamedColor("mediumpurple", 147, 112, 219);
		initNamedColor("blueviolet", 138, 43, 226);
		initNamedColor("darkviolet", 148, 0, 211);
		initNamedColor("darkorchid", 153, 50, 204);
		initNamedColor("darkmagenta", 139, 0, 139);
		initNamedColor("purple", 128, 0, 128);
		initNamedColor("indigo", 75, 0, 130);

		initNamedColor("pink", 255, 192, 203);
		initNamedColor("lightpink", 255, 182, 193);
		initNamedColor("hotpink", 255, 105, 180);
		initNamedColor("deeppink", 255, 20, 147);
		initNamedColor("palevioletred", 219, 112, 147);
		initNamedColor("mediumvioletred", 199, 21, 133);

		initNamedColor("white", 255, 255, 255);
		initNamedColor("snow", 255, 250, 250);
		initNamedColor("honeydew", 240, 255, 240);
		initNamedColor("mintcream", 245, 255, 250);
		initNamedColor("azure", 240, 255, 255);
		initNamedColor("aliceblue", 240, 248, 255);
		initNamedColor("ghostwhite", 248, 248, 255);
		initNamedColor("whitesmoke", 245, 245, 245);
		initNamedColor("seashell", 255, 245, 238);
		initNamedColor("beige", 245, 245, 220);
		initNamedColor("oldlace", 253, 245, 230);
		initNamedColor("floralwhite", 255, 250, 240);
		initNamedColor("ivory", 255, 255, 240);
		initNamedColor("antiquewhite", 250, 235, 215);
		initNamedColor("linen", 250, 240, 230);
		initNamedColor("lavenderblush", 255, 240, 245);
		initNamedColor("mistyrose", 255, 228, 225);

		initNamedColor("gainsboro", 220, 220, 220);
		initNamedColor("lightgray", 211, 211, 211);
		initNamedColor("silver", 192, 192, 192);
		initNamedColor("darkgray", 169, 169, 169);
		initNamedColor("gray", 128, 128, 128);
		initNamedColor("dimgray", 105, 105, 105);
		initNamedColor("lightslategray", 119, 136, 153);
		initNamedColor("slategray", 112, 128, 144);
		initNamedColor("darkslategray", 47, 79, 79);
		initNamedColor("black", 0, 0, 0);

		initNamedColor("cornsilk", 255, 248, 220);
		initNamedColor("blanchedalmond", 255, 235, 205);
		initNamedColor("bisque", 255, 228, 196);
		initNamedColor("navajowhite", 255, 222, 173);
		initNamedColor("wheat", 245, 222, 179);
		initNamedColor("burlywood", 222, 184, 135);
		initNamedColor("tan", 210, 180, 140);
		initNamedColor("rosybrown", 188, 143, 143);
		initNamedColor("sandybrown", 244, 164, 96);
		initNamedColor("goldenrod", 218, 165, 32);
		initNamedColor("peru", 205, 133, 63);
		initNamedColor("chocolate", 210, 105, 30);
		initNamedColor("saddlebrown", 139, 69, 19);
		initNamedColor("sienna", 160, 82, 45);
		initNamedColor("brown", 165, 42, 42);
		initNamedColor("maroon", 128, 0, 0);

		initNamedColor("rebeccapurple", 102, 51, 153);
		initNamedColor("transparent", 0, 0, 0, 0);
	}

	public static CssColorValue getNamedColor(String colorName) {
		if (colorName == null)
			return null;
		return namedColors.get(colorName.toLowerCase());
	}

	String propertyName;

	/**
	 * This optional property may be defined when this is used to parse an
	 * explicit CSS.Attribute
	 */
	CSS.Attribute cssAttribute;

	public CssColorParser() {
		this(PROPERTY_COLOR);
	}

	public CssColorParser(String propertyName) {
		Objects.requireNonNull(propertyName);
		this.propertyName = propertyName;
	}

	public CssColorParser(CSS.Attribute cssAttribute) {
		this(cssAttribute.toString());
		this.cssAttribute = cssAttribute;
	}

	@Override
	public Object getAttributeKey() {
		return cssAttribute == null ? getPropertyName() : cssAttribute;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public CssColorValue parse(String cssValue) {
		return parse(cssValue, true);
	}

	/**
	 * 
	 * @param cssValue
	 * @param attemptBadFormatting
	 *            if true then we'll parse "ffffff" as a color, even though it
	 *            is formatted incorrectly.
	 * @return
	 */
	public CssColorValue parse(final String cssValue,
			boolean attemptBadFormatting) {
		String value = cssValue.toLowerCase().trim();
		CssColorValue namedColor = getNamedColor(value);
		if (namedColor != null)
			return namedColor;

		if (value.startsWith("rgb(") || value.startsWith("rgba(")) {
			int i1 = value.indexOf("(");
			int i2 = value.lastIndexOf(")");
			if (i2 != -1) {
				return parseRGB(cssValue, value.substring(i1 + 1, i2));
			}
			throw new IllegalArgumentException(
					"missing closing parentheses (\"" + value + "\"");
		} else if (value.startsWith("hsl(") || value.startsWith("hsla(")) {
			int i1 = value.indexOf("(");
			int i2 = value.lastIndexOf(")");
			if (i2 != -1) {
				return parseHSL(cssValue, value.substring(i1 + 1, i2));
			}
			throw new IllegalArgumentException(
					"missing closing parentheses (\"" + value + "\"");
		} else if (value.startsWith("#")) {
			return parseHex(cssValue, value.substring("#".length()));
		}

		if (attemptBadFormatting) {
			// CSS#stringToColor says "sometimes get specified without leading
			// #"
			try {
				return parseHex(cssValue, value);
			} catch (RuntimeException e) {
				// do nothing, this was a longshot anyway
			}
		}
		throw new IllegalArgumentException(
				"unsupported color \"" + value + "\"");
	}

	protected CssColorValue parseHex(String originalCssString, String hexStr) {
		if (hexStr.length() == 3) {
			hexStr = "" + hexStr.charAt(0) + hexStr.charAt(0) + hexStr.charAt(1)
					+ hexStr.charAt(1) + hexStr.charAt(2) + hexStr.charAt(2);
		} else if (hexStr.length() == 4) {
			hexStr = "" + hexStr.charAt(0) + hexStr.charAt(0) + hexStr.charAt(1)
					+ hexStr.charAt(1) + hexStr.charAt(2) + hexStr.charAt(2)
					+ hexStr.charAt(3) + hexStr.charAt(3);
		}
		if (hexStr.length() == 8) {
			int red = Integer.parseInt(hexStr.substring(0, 2), 16);
			int green = Integer.parseInt(hexStr.substring(2, 4), 16);
			int blue = Integer.parseInt(hexStr.substring(4, 6), 16);
			int alpha = Integer.parseInt(hexStr.substring(6, 8), 16);
			Color c = new Color(red, green, blue, alpha);
			return new CssColorValue(originalCssString, c);
		} else if (hexStr.length() == 7) {
			throw new RuntimeException("Unsupported hexadecimal: \"" + hexStr
					+ "\" (expected 6 or 8 chars)");
		}
		while (hexStr.length() < 6) {
			hexStr = "0" + hexStr;
		}
		Color c = new Color(Integer.parseInt(hexStr, 16), false);
		return new CssColorValue(originalCssString, c);
	}

	/**
	 * As of CSS Colors Level 4, rgba() is an alias for rgb(). In browsers that
	 * implement the Level 4 standard, they accept the same parameters and
	 * behave the same way.
	 * <p>
	 * R (red), G (green), and B (blue) can be either numbers or percentages,
	 * where the number 255 corresponds to 100%. A (alpha) can be a number
	 * between 0 and 1, or a percentage, where the number 1 corresponds to 100%
	 * (full opacity).
	 * <p>
	 * CSS Colors Level 4 adds support for space-separated values in the
	 * functional notation.
	 * <p>
	 * Values outside the device gamut should be clipped or mapped into the
	 * gamut when the gamut is known: the red, green, and blue values must be
	 * changed to fall within the range supported by the device.
	 * 
	 * @param rgbArgs
	 * @return
	 */
	protected CssColorValue parseRGB(String originalCssString, String rgbArgs) {
		List<String> args = parseArgs(rgbArgs);
		int red, green, blue, alpha;

		if (args.size() < 3)
			throw new IllegalArgumentException(
					"rgb requires 3-4 arguments (\"" + rgbArgs + "\")");

		String redArg = args.get(0);
		if (redArg.endsWith("%")) {
			redArg = redArg.substring(0, redArg.length() - 1);
			red = (int) (Float.parseFloat(redArg) * 255 / 100);
		} else {
			red = (int) (Float.parseFloat(redArg));
		}

		String greenArg = args.get(1);
		if (greenArg.endsWith("%")) {
			greenArg = greenArg.substring(0, greenArg.length() - 1);
			green = (int) (Float.parseFloat(greenArg) * 255 / 100);
		} else {
			green = (int) (Float.parseFloat(greenArg));
		}

		String blueArg = args.get(2);
		if (blueArg.endsWith("%")) {
			blueArg = blueArg.substring(0, blueArg.length() - 1);
			blue = (int) (Float.parseFloat(blueArg) * 255 / 100);
		} else {
			blue = (int) (Float.parseFloat(blueArg));
		}

		if (args.size() == 4) {
			String alphaArg = args.get(3);
			if (alphaArg.endsWith("%")) {
				alphaArg = alphaArg.substring(0, alphaArg.length() - 1);
				alpha = (int) (Float.parseFloat(alphaArg) * 255 / 100);
			} else {
				alpha = (int) (Float.parseFloat(alphaArg) * 255);
			}
		} else {
			alpha = 255;
		}

		red = Math.min(255, Math.max(0, red));
		green = Math.min(255, Math.max(0, green));
		blue = Math.min(255, Math.max(0, blue));
		alpha = Math.min(255, Math.max(0, alpha));

		return new CssColorValue(originalCssString, red, green, blue, alpha);
	}

	protected List<String> parseArgs(String args) {
		List<String> returnValue = new ArrayList<>(4);
		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < args.length(); a++) {
			char ch = args.charAt(a);
			// including "/" here is a little bit of a cheat. That char should
			// only be used to separate the fourth (alpha) term, so now our
			// parser is a little bit too generous
			// ... but in my mind using "/" is a fringe case, and we can tighten
			// up the parser later as needed.
			if (Character.isWhitespace(ch) || ch == ',' || ch == '/') {
				if (sb.length() > 0) {
					returnValue.add(sb.toString());
					sb.delete(0, sb.length());
				}
			} else {
				sb.append(ch);
			}
		}
		if (sb.length() > 0)
			returnValue.add(sb.toString());
		return returnValue;
	}

	/**
	 * Note: As of CSS Colors Level 4, hsla() is an alias for hsl(). In browsers
	 * that implement the Level 4 standard, they accept the same parameters and
	 * behave the same way.
	 * <p>
	 * Functional notation: hsl[a](H, S, L[, A]) <br>
	 * H (hue) is an angle of the color circle given in degs, rads, grads, or
	 * turns in CSS Color Module Level 4. When written as a unitless number, it
	 * is interpreted as degrees, as specified in CSS Color Module Level 3. By
	 * definition, red=0deg=360deg, with the other colors spread around the
	 * circle, so green=120deg, blue=240deg, etc. As an angle, it implicitly
	 * wraps around such that -120deg=240deg, 480deg=120deg, -1turn=1turn, etc.
	 * <br>
	 * S (saturation) and L (lightness) are percentages. 100% saturation is
	 * completely saturated, while 0% is completely unsaturated (gray). 100%
	 * lightness is white, 0% lightness is black, and 50% lightness is “normal.”
	 * A (alpha) can be a number between 0 and 1, or a percentage, where the
	 * number 1 corresponds to 100% (full opacity).
	 * <p>
	 * CSS Colors Level 4 adds support for space-separated values in the
	 * functional notation.
	 * 
	 * @param hslArgs
	 * @return
	 */
	protected CssColorValue parseHSL(String originalCssString, String hslArgs) {
		List<String> args = parseArgs(hslArgs);
		if (args.size() < 3 || args.size() > 4)
			throw new IllegalArgumentException(
					"HSLA() expects 3-4 arguments, but received " + args.size()
							+ " (" + hslArgs + ")");
		float h, s, l;

		String hueArg = args.get(0);
		String saturationArg = args.get(1);
		String lightnessArg = args.get(2);
		if (hueArg.endsWith("rad")) {
			hueArg = hueArg.substring(0, hueArg.length() - "rad".length())
					.trim();
			h = (float) (Float.parseFloat(hueArg) / (2 * Math.PI));
		} else if (hueArg.endsWith("turn")) {
			hueArg = hueArg.substring(0, hueArg.length() - "turn".length())
					.trim();
			h = Float.parseFloat(hueArg);
		} else {
			if (hueArg.endsWith("deg"))
				hueArg = hueArg.substring(0, hueArg.length() - "deg".length())
						.trim();
			h = Float.parseFloat(hueArg) / 360f;
		}

		if (saturationArg.endsWith("%")) {
			saturationArg = saturationArg.substring(0,
					saturationArg.length() - 1);
			s = Float.parseFloat(saturationArg) / 100f;
		} else {
			throw new IllegalArgumentException(
					"the saturation argument must end with a % (" + hslArgs
							+ ")");
		}

		if (lightnessArg.endsWith("%")) {
			lightnessArg = lightnessArg.substring(0, lightnessArg.length() - 1);
			l = Float.parseFloat(lightnessArg) / 100f;
		} else {
			throw new IllegalArgumentException(
					"the lightness argument must end with a % (" + hslArgs
							+ ")");
		}

		float alpha;
		if (args.size() == 3) {
			alpha = 1;
		} else {
			String alphaArg = args.get(3);
			if (alphaArg.endsWith("%")) {
				alphaArg = alphaArg.substring(0, alphaArg.length() - 1);
				alpha = Float.parseFloat(alphaArg) / 100f;
			} else {
				alpha = Float.parseFloat(alphaArg);
			}
		}

		Color c = new Color(HSLColor.toRGB(h, s, l, alpha), true);
		return new CssColorValue(originalCssString, c);
	}

	@Override
	public int hashCode() {
		return propertyName == null ? 0 : propertyName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CssColorParser))
			return false;
		CssColorParser other = (CssColorParser) obj;
		if (!(Objects.equals(cssAttribute, other.cssAttribute)))
			return false;
		if (!(Objects.equals(propertyName, other.propertyName)))
			return false;
		return true;
	}
}