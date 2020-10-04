package com.pump.text.html.style;

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
public class CssColorPropertyHandler implements CssPropertyHandler<Color> {

	public static final String PROPERTY_COLOR = "color";

	private static Map<String, Color> namedColors = new HashMap<>();

	static {
		namedColors.put("lightsalmon", new Color(255, 160, 122));
		namedColors.put("salmon", new Color(250, 128, 114));
		namedColors.put("darksalmon", new Color(233, 150, 122));
		namedColors.put("lightcoral", new Color(240, 128, 128));
		namedColors.put("indianred", new Color(205, 92, 92));
		namedColors.put("crimson", new Color(220, 20, 60));
		namedColors.put("firebrick", new Color(178, 34, 34));
		namedColors.put("red", new Color(255, 0, 0));
		namedColors.put("darkred", new Color(139, 0, 0));

		namedColors.put("coral", new Color(255, 127, 80));
		namedColors.put("tomato", new Color(255, 99, 71));
		namedColors.put("orangered", new Color(255, 69, 0));
		namedColors.put("gold", new Color(255, 215, 0));
		namedColors.put("orange", new Color(255, 165, 0));
		namedColors.put("darkorange", new Color(255, 140, 0));

		namedColors.put("lightyellow", new Color(255, 255, 224));
		namedColors.put("lemonchiffon", new Color(255, 250, 205));
		namedColors.put("lightgoldenrodyellow", new Color(250, 250, 210));
		namedColors.put("papayawhip", new Color(255, 239, 213));
		namedColors.put("moccasin", new Color(255, 228, 181));
		namedColors.put("peachpuff", new Color(255, 218, 185));
		namedColors.put("palegoldenrod", new Color(238, 232, 170));
		namedColors.put("khaki", new Color(240, 230, 140));
		namedColors.put("darkkhaki", new Color(189, 183, 107));
		namedColors.put("yellow", new Color(255, 255, 0));

		namedColors.put("lawngreen", new Color(124, 252, 0));
		namedColors.put("chartreuse", new Color(127, 255, 0));
		namedColors.put("limegreen", new Color(50, 205, 50));
		namedColors.put("lime", new Color(0, 255, 0));
		namedColors.put("forestgreen", new Color(34, 139, 34));
		namedColors.put("green", new Color(0, 128, 0));
		namedColors.put("darkgreen", new Color(0, 100, 0));
		namedColors.put("greenyellow", new Color(173, 255, 47));
		namedColors.put("yellowgreen", new Color(154, 205, 50));
		namedColors.put("springgreen", new Color(0, 255, 127));
		namedColors.put("mediumspringgreen", new Color(0, 250, 154));
		namedColors.put("lightgreen", new Color(144, 238, 144));
		namedColors.put("palegreen", new Color(152, 251, 152));
		namedColors.put("darkseagreen", new Color(143, 188, 143));
		namedColors.put("mediumseagreen", new Color(60, 179, 113));
		namedColors.put("seagreen", new Color(46, 139, 87));
		namedColors.put("olive", new Color(128, 128, 0));
		namedColors.put("darkolivegreen", new Color(85, 107, 47));
		namedColors.put("olivedrab", new Color(107, 142, 35));

		namedColors.put("lightcyan", new Color(224, 255, 255));
		namedColors.put("cyan", new Color(0, 255, 255));
		namedColors.put("aqua", new Color(0, 255, 255));
		namedColors.put("aquamarine", new Color(127, 255, 212));
		namedColors.put("mediumaquamarine", new Color(102, 205, 170));
		namedColors.put("paleturquoise", new Color(175, 238, 238));
		namedColors.put("turquoise", new Color(64, 224, 208));
		namedColors.put("mediumturquoise", new Color(72, 209, 204));
		namedColors.put("darkturquoise", new Color(0, 206, 209));
		namedColors.put("lightseagreen", new Color(32, 178, 170));
		namedColors.put("cadetblue", new Color(95, 158, 160));
		namedColors.put("darkcyan", new Color(0, 139, 139));
		namedColors.put("teal", new Color(0, 128, 128));

		namedColors.put("powderblue", new Color(176, 224, 230));
		namedColors.put("lightblue", new Color(173, 216, 230));
		namedColors.put("lightskyblue", new Color(135, 206, 250));
		namedColors.put("skyblue", new Color(135, 206, 235));
		namedColors.put("deepskyblue", new Color(0, 191, 255));
		namedColors.put("lightsteelblue", new Color(176, 196, 222));
		namedColors.put("dodgerblue", new Color(30, 144, 255));
		namedColors.put("cornflowerblue", new Color(100, 149, 237));
		namedColors.put("steelblue", new Color(70, 130, 180));
		namedColors.put("royalblue", new Color(65, 105, 225));
		namedColors.put("blue", new Color(0, 0, 255));
		namedColors.put("mediumblue", new Color(0, 0, 205));
		namedColors.put("darkblue", new Color(0, 0, 139));
		namedColors.put("navy", new Color(0, 0, 128));
		namedColors.put("midnightblue", new Color(25, 25, 112));
		namedColors.put("mediumslateblue", new Color(123, 104, 238));
		namedColors.put("slateblue", new Color(106, 90, 205));
		namedColors.put("darkslateblue", new Color(72, 61, 139));

		namedColors.put("lavender", new Color(230, 230, 250));
		namedColors.put("thistle", new Color(216, 191, 216));
		namedColors.put("plum", new Color(221, 160, 221));
		namedColors.put("violet", new Color(238, 130, 238));
		namedColors.put("orchid", new Color(218, 112, 214));
		namedColors.put("fuchsia", new Color(255, 0, 255));
		namedColors.put("magenta", new Color(255, 0, 255));
		namedColors.put("mediumorchid", new Color(186, 85, 211));
		namedColors.put("mediumpurple", new Color(147, 112, 219));
		namedColors.put("blueviolet", new Color(138, 43, 226));
		namedColors.put("darkviolet", new Color(148, 0, 211));
		namedColors.put("darkorchid", new Color(153, 50, 204));
		namedColors.put("darkmagenta", new Color(139, 0, 139));
		namedColors.put("purple", new Color(128, 0, 128));
		namedColors.put("indigo", new Color(75, 0, 130));

		namedColors.put("pink", new Color(255, 192, 203));
		namedColors.put("lightpink", new Color(255, 182, 193));
		namedColors.put("hotpink", new Color(255, 105, 180));
		namedColors.put("deeppink", new Color(255, 20, 147));
		namedColors.put("palevioletred", new Color(219, 112, 147));
		namedColors.put("mediumvioletred", new Color(199, 21, 133));

		namedColors.put("white", new Color(255, 255, 255));
		namedColors.put("snow", new Color(255, 250, 250));
		namedColors.put("honeydew", new Color(240, 255, 240));
		namedColors.put("mintcream", new Color(245, 255, 250));
		namedColors.put("azure", new Color(240, 255, 255));
		namedColors.put("aliceblue", new Color(240, 248, 255));
		namedColors.put("ghostwhite", new Color(248, 248, 255));
		namedColors.put("whitesmoke", new Color(245, 245, 245));
		namedColors.put("seashell", new Color(255, 245, 238));
		namedColors.put("beige", new Color(245, 245, 220));
		namedColors.put("oldlace", new Color(253, 245, 230));
		namedColors.put("floralwhite", new Color(255, 250, 240));
		namedColors.put("ivory", new Color(255, 255, 240));
		namedColors.put("antiquewhite", new Color(250, 235, 215));
		namedColors.put("linen", new Color(250, 240, 230));
		namedColors.put("lavenderblush", new Color(255, 240, 245));
		namedColors.put("mistyrose", new Color(255, 228, 225));

		namedColors.put("gainsboro", new Color(220, 220, 220));
		namedColors.put("lightgray", new Color(211, 211, 211));
		namedColors.put("silver", new Color(192, 192, 192));
		namedColors.put("darkgray", new Color(169, 169, 169));
		namedColors.put("gray", new Color(128, 128, 128));
		namedColors.put("dimgray", new Color(105, 105, 105));
		namedColors.put("lightslategray", new Color(119, 136, 153));
		namedColors.put("slategray", new Color(112, 128, 144));
		namedColors.put("darkslategray", new Color(47, 79, 79));
		namedColors.put("black", new Color(0, 0, 0));

		namedColors.put("cornsilk", new Color(255, 248, 220));
		namedColors.put("blanchedalmond", new Color(255, 235, 205));
		namedColors.put("bisque", new Color(255, 228, 196));
		namedColors.put("navajowhite", new Color(255, 222, 173));
		namedColors.put("wheat", new Color(245, 222, 179));
		namedColors.put("burlywood", new Color(222, 184, 135));
		namedColors.put("tan", new Color(210, 180, 140));
		namedColors.put("rosybrown", new Color(188, 143, 143));
		namedColors.put("sandybrown", new Color(244, 164, 96));
		namedColors.put("goldenrod", new Color(218, 165, 32));
		namedColors.put("peru", new Color(205, 133, 63));
		namedColors.put("chocolate", new Color(210, 105, 30));
		namedColors.put("saddlebrown", new Color(139, 69, 19));
		namedColors.put("sienna", new Color(160, 82, 45));
		namedColors.put("brown", new Color(165, 42, 42));
		namedColors.put("maroon", new Color(128, 0, 0));

		namedColors.put("rebeccapurple", new Color(102, 51, 153));
		namedColors.put("transparent", new Color(0, 0, 0, 0));
	}

	public static Color getNamedColor(String colorName) {
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

	public CssColorPropertyHandler() {
		this(PROPERTY_COLOR);
	}

	public CssColorPropertyHandler(String propertyName) {
		Objects.requireNonNull(propertyName);
		this.propertyName = propertyName;
	}

	public CssColorPropertyHandler(CSS.Attribute cssAttribute) {
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
	public Color parse(String value) {
		value = value.toLowerCase().trim();
		Color namedColor = getNamedColor(value);
		if (namedColor != null)
			return namedColor;

		if (value.startsWith("rgb(") || value.startsWith("rgba(")) {
			int i1 = value.indexOf("(");
			int i2 = value.lastIndexOf(")");
			if (i2 != -1) {
				return parseRGB(value.substring(i1 + 1, i2));
			}
			throw new IllegalArgumentException(
					"missing closing parentheses (\"" + value + "\"");
		} else if (value.startsWith("hsl(") || value.startsWith("hsla(")) {
			int i1 = value.indexOf("(");
			int i2 = value.lastIndexOf(")");
			if (i2 != -1) {
				return parseHSL(value.substring(i1 + 1, i2));
			}
			throw new IllegalArgumentException(
					"missing closing parentheses (\"" + value + "\"");
		} else if (value.startsWith("#")) {
			return parseHex(value.substring("#".length()));
		}

		// CSS#stringToColor says "sometimes get specified without leading #"
		try {
			parseHex(value);
		} catch (RuntimeException e) {
			// do nothing, this was a longshot anyway
		}
		throw new IllegalArgumentException(
				"unsupported color \"" + value + "\"");
	}

	protected Color parseHex(String hexStr) {
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
			return new Color(red, green, blue, alpha);
		} else if (hexStr.length() == 7) {
			throw new RuntimeException("Unsupported hexadecimal: \"" + hexStr
					+ "\" (expected 6 or 8 chars)");
		}
		while (hexStr.length() < 6) {
			hexStr = "0" + hexStr;
		}
		return new Color(Integer.parseInt(hexStr, 16), false);
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
	protected Color parseRGB(String rgbArgs) {
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

		return new Color(red, green, blue, alpha);
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
	protected Color parseHSL(String hslArgs) {
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

		return new Color(HSLColor.toRGB(h, s, l, alpha), true);
	}

	@Override
	public int hashCode() {
		return propertyName == null ? 0 : propertyName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CssColorPropertyHandler))
			return false;
		CssColorPropertyHandler other = (CssColorPropertyHandler) obj;
		if (!(Objects.equals(cssAttribute, other.cssAttribute)))
			return false;
		if (!(Objects.equals(propertyName, other.propertyName)))
			return false;
		return true;
	}
}
