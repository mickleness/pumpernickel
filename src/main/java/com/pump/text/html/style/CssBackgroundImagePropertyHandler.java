package com.pump.text.html.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.html.CSS;

import com.pump.awt.GradientTexturePaint;
import com.pump.plaf.AnimationManager;

public class CssBackgroundImagePropertyHandler implements
		CssPropertyHandler<CssBackgroundImagePropertyHandler.BackgroundImage> {

	public static interface ImageValue {
		public void paintRectangle(Graphics2D g, int x, int y, int width,
				int height);
	}

	public static class URLImageValue implements ImageValue {
		String url;

		public URLImageValue(String url) {
			this.url = url;
			// TODO: also support data URI's
			// https://css-tricks.com/data-uris/
		}

		@Override
		public void paintRectangle(Graphics2D g, int x, int y, int width,
				int height) {
			// TODO Auto-generated method stub

		}

	}

	public static class NoneImageValue implements ImageValue {

		@Override
		public void paintRectangle(Graphics2D g, int x, int y, int width,
				int height) {
			// intentionally empty
		}

	}

	public static class LinearGradientImageValue implements ImageValue {

		static class Length {
			float value;
			String unit;

			public Length(String str) {
				StringBuilder sb = new StringBuilder();
				for (int a = str.length() - 1; a >= 0; a--) {
					char ch = str.charAt(a);
					if (Character.isLetter(ch) || ch == '%') {
						sb.insert(0, ch);
					} else {
						break;
					}
				}
				unit = sb.length() == 0 ? null : sb.toString();
				value = Float.parseFloat(
						str.substring(0, str.length() - sb.length()));
			}

			public Length(float value, String unit) {
				this.value = value;
				this.unit = unit;
			}

			@Override
			public Length clone() {
				return new Length(value, unit);
			}

			@Override
			public String toString() {
				return "Length[ " + toCSSString() + " ]";
			}

			public String toCSSString() {
				return value + unit;
			}
		}

		static class ColorStop {
			Color color;

			Length pos1, pos2;

			ColorStop(String colorArg, String stopArg) {
				color = new CssColorPropertyHandler().parse(colorArg);

				if (stopArg != null) {
					List<Length> lengths = parseLengths(stopArg);
					if (lengths.size() == 1) {
						pos1 = lengths.get(0);
					} else if (lengths.size() == 2) {
						pos1 = lengths.get(0);
						pos2 = lengths.get(1);
					} else if (lengths.size() == 0) {
						// not sure why we got here, but keep going
					} else {
						throw new IllegalArgumentException(
								"unsupported number of color stops ("
										+ lengths.size() + ") in \"" + stopArg
										+ "\"");
					}
				}
			}

			public ColorStop(Color color, Length pos1) {
				this.color = color;
				this.pos1 = pos1;
			}

			private List<Length> parseLengths(String str) {
				str = str.trim();
				List<Length> returnValue = new LinkedList<>();
				while (true) {
					int i = str.indexOf(' ');
					if (i == -1) {
						if (str.length() > 0)
							returnValue.add(new Length(str));
						return returnValue;
					} else {
						returnValue.add(new Length(str.substring(0, i)));
						str = str.substring(i + 1).trim();
					}
				}
			}

			@Override
			public String toString() {
				return "ColorStop[ " + toCSSString() + " ]";
			}

			public String toCSSString() {
				StringBuilder sb = new StringBuilder();
				sb.append("rgba(" + color.getRed() + "," + color.getGreen()
						+ "," + color.getBlue() + "," + color.getAlpha() + ")");
				if (pos1 != null) {
					sb.append(" " + pos1.toCSSString());
				}
				if (pos2 != null) {
					sb.append(" " + pos2.toCSSString());
				}
				return sb.toString();
			}
		}

		/**
		 * If the direction is defined as "to right" or "to bottom left" then
		 * this object defines that path.
		 */
		enum Direction {
			TO_TOP() {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x, y + height, x, y);
				}
			},
			TO_LEFT {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x + width, y, x, y);
				}
			},
			TO_BOTTOM {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x, y, x, y + height);
				}
			},
			TO_RIGHT {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x, y, x + width, y);
				}
			},
			TO_TOP_LEFT {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x + width, y + height, x, y);
				}
			},
			TO_TOP_RIGHT {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x, y + height, x + width, y);
				}
			},
			TO_BOTTOM_LEFT {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x + width, y, x, y + height);
				}
			},
			TO_BOTTOM_RIGHT {
				@Override
				Line2D createLine(int x, int y, int width, int height) {
					return new Line2D.Double(x, y, x + width, y + height);
				}
			};

			/**
			 * Create a line used for a gradient where the first point
			 * corresponds to the first color and the second point corresponds
			 * to the last color.
			 */
			abstract Line2D createLine(int x, int y, int width, int height);
		}

		/**
		 * The optional Direction this gradient uses. Either "direction" or
		 * "theta" should be populated.
		 */
		Direction direction;

		/**
		 * The optional angle (in radians) this gradient uses. Either
		 * "direction" or "theta" should be populated.
		 */
		Double theta = -Math.PI / 2;

		/**
		 * This is a combination of ColorStops and Lengths (as interpolation
		 * hints)
		 */
		List<Object> colorStopsAndHints = new ArrayList<>();

		boolean repeating;

		public LinearGradientImageValue(String str, boolean repeating) {
			this.repeating = repeating;
			try {
				List<String> args = CssParserUtils.splitCommaSeparatedList(str,
						true);
				int index = 0;
				String firstArg = args.get(0).toLowerCase();
				if (firstArg.startsWith("to ")) {
					direction = parseToDirection(
							firstArg.substring("to ".length()));
					index = 1;
				} else if (firstArg.endsWith("turn")) {
					theta = parseTheta(firstArg, "turn") * 2 * Math.PI
							+ Math.PI / 2;
					index = 1;
				} else if (firstArg.endsWith("deg") || firstArg.equals("0")) {
					theta = parseTheta(firstArg, "deg") * Math.PI / 180
							+ Math.PI / 2;
					index = 1;
				} else if (firstArg.endsWith("rad")) {
					theta = parseTheta(firstArg, "rad") + Math.PI / 2;
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
							colorStopsAndHints.add(new Length(arg));
						} catch (RuntimeException e) {
							// ... or it might be a color like "#ef2"
							colorStopsAndHints.add(new ColorStop(arg, null));
						}
					} else if (j1 == -1) {
						// we have a space
						colorStopsAndHints
								.add(new ColorStop(arg.substring(0, j2),
										arg.substring(j2 + 1).trim()));
					} else if (j2 == -1) {
						// we have a close parentheses but no space:
						colorStopsAndHints.add(new ColorStop(arg, null));
					} else {
						// we have both a space and a close parentheses
						colorStopsAndHints
								.add(new ColorStop(arg.substring(0, j1 + 1),
										arg.substring(j1 + 1).trim()));
					}
					index++;
				}

				// if we're going to throw an exception throw it during
				// construction:
				createPaint(0, 0, 100, 100);
			} catch (RuntimeException e) {
				throw new IllegalArgumentException("The linear-gradient \""
						+ str + "\" was not parsed correctly.", e);
			}
		}

		/**
		 * Return
		 * 
		 * @param arg
		 * @param unit
		 * @return
		 */
		private double parseTheta(String arg, String unit) {
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

		@Override
		public void paintRectangle(Graphics2D g, int x, int y, int width,
				int height) {
			Paint p = createPaint(x, y, width, height);
			g.setPaint(p);

			// this is used by GradientTexturePaint:
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

			g.fillRect(x, y, width, height);
		}

		protected Paint createPaint(int x, int y, int width, int height) {
			Line2D line;
			if (direction != null) {
				line = direction.createLine(x, y, width, height);
			} else {
				double centerX = x + width / 2.0;
				double centerY = y + height / 2.0;
				double length = Math.abs(width * Math.sin(theta + Math.PI / 2))
						+ Math.abs(height * Math.cos(theta + Math.PI / 2));
				line = new Line2D.Double(centerX + length / 2 * Math.cos(theta),
						centerY + length / 2 * Math.sin(theta),
						centerX - length / 2 * Math.cos(theta),
						centerY - length / 2 * Math.sin(theta));
			}

			/*
			 * https://drafts.csswg.org/css-images-4/#color-stop-syntax:
			 * 
			 * 3.5.3. Color Stop “Fixup”
			 * 
			 * When resolving the used positions of each color stop, the
			 * following steps must be applied in order:
			 * 
			 * 1. If the first color stop does not have a position, set its
			 * position to 0%. If the last color stop does not have a position,
			 * set its position to 100%.
			 * 
			 * 2. If a color stop or transition hint has a position that is less
			 * than the specified position of any color stop or transition hint
			 * before it in the list, set its position to be equal to the
			 * largest specified position of any color stop or transition hint
			 * before it.
			 * 
			 * 3. If any color stop still does not have a position, then, for
			 * each run of adjacent color stops without positions, set their
			 * positions so that they are evenly spaced between the preceding
			 * and following color stops with positions.
			 */

			double distance = line.getP1().distance(line.getP2());

			// we'll convert everything to a list of ColorStops with exactly 1
			// position
			List<ColorStop> stopSingletons = new ArrayList<>();

			// split up paired stops into singletons:
			Length interpolationHint = null;
			ColorStop lastColorStop = null;
			for (Object t : colorStopsAndHints) {
				if (t instanceof Length) {
					interpolationHint = (Length) t;
					// we only process/consume this hint next time we get a
					// ColorStop.
				} else if (t instanceof ColorStop) {
					ColorStop cs = (ColorStop) t;

					ColorStop newPos1, newPos2;

					if (cs.pos2 != null) {
						newPos1 = new ColorStop(cs.color,
								cs.pos1 == null ? null : cs.pos1.clone());
						newPos2 = new ColorStop(cs.color,
								cs.pos2 == null ? null : cs.pos2.clone());
					} else {
						newPos1 = new ColorStop(cs.color,
								cs.pos1 == null ? null : cs.pos1.clone());
						newPos2 = null;
					}

					if (interpolationHint != null) {
						// consume the hint

						// TODO: this should be "a smooth exponential curve
						// between the surrounding color stops", but the current
						// implementation is a plain linear gradient. Could we
						// simulate a close-enough replica by just adding
						// 10ish new stops? What does that math look like?

						// https://drafts.csswg.org/css-images-4/#color-stop-syntax

						Color newColor = AnimationManager
								.tween(lastColorStop.color, newPos1.color, .5);
						float newPos;
						if ("%".equals(interpolationHint.unit)) {
							newPos = interpolationHint.value;
						} else if ("px".equals(interpolationHint.unit)) {
							newPos = (float) (interpolationHint.value * 100
									/ distance);
						} else {
							throw new IllegalArgumentException(
									"this linear gradient implementation does not support \""
											+ cs.pos1.unit + "\" units");
						}
						stopSingletons.add(new ColorStop(newColor,
								new Length(newPos, "%")));
						interpolationHint = null;
					}

					stopSingletons.add(newPos1);
					if (newPos2 != null)
						stopSingletons.add(newPos2);

					lastColorStop = stopSingletons
							.get(stopSingletons.size() - 1);
				} else {
					// this shouldn't happen. If it does: something changed
					// regarding how we populate colorStopsAndHints
					throw new RuntimeException("Unexpected element: " + t);
				}
			}

			// convert each singleton to use %'s:
			for (ColorStop cs : stopSingletons) {
				if (cs.pos1 != null && cs.pos1.unit != null) {
					if (null == cs.pos1.unit && cs.pos1.value == 0) {
						cs.pos1.unit = "%";
					} else if ("px".equals(cs.pos1.unit)) {
						cs.pos1.unit = "%";
						cs.pos1.value = (float) (cs.pos1.value * 100
								/ distance);
					} else if ("%".equals(cs.pos1.unit)) {
						// do nothing, this is OK
					} else {
						throw new IllegalArgumentException(
								"this linear gradient implementation does not support \""
										+ cs.pos1.unit + "\" units");
					}
				}
			}

			// if we wanted to be super strict/safe, we could convert each
			// singleton to an int to prevent rounding errors. But that seems
			// like overkill until we have a need for it?

			// apply rule 1:
			if (stopSingletons.get(0).pos1 == null) {
				stopSingletons.get(0).pos1 = new Length(0f, "%");
			}

			if (stopSingletons.get(stopSingletons.size() - 1).pos1 == null) {
				stopSingletons.get(stopSingletons.size() - 1).pos1 = new Length(
						100, "%");
			}

			// apply rule 2:
			float runningMax = stopSingletons.get(0).pos1.value;
			for (ColorStop cs : stopSingletons) {
				if (cs.pos1 != null) {
					if (cs.pos1.value < runningMax)
						cs.pos1.value = runningMax;
					runningMax = Math.max(runningMax, cs.pos1.value);
				}
			}

			// apply rule 3:

			List<ColorStop> unknownRun = new ArrayList<>();
			for (ColorStop cs : stopSingletons) {
				if (cs.pos1 != null && !unknownRun.isEmpty()) {
					ColorStop first = unknownRun.get(0);
					ColorStop preceding = stopSingletons
							.get(stopSingletons.indexOf(first) - 1);
					ColorStop next = cs;

					float range = next.pos1.value - preceding.pos1.value;
					for (int k = 0; k < unknownRun.size(); k++) {
						unknownRun.get(k).pos1 = new Length(preceding.pos1.value
								+ range * (k + 1) / (unknownRun.size() + 1),
								"%");
					}

					unknownRun.clear();
				} else if (cs.pos1 == null) {
					unknownRun.add(cs);
				}
			}

			// after applying this rules: if there are three (or more)
			// consecutive stops with the same position, drop the middle one(s).
			float currentValue = -1;
			int currentValueCtr = 0;
			lastColorStop = null;
			for (ColorStop cs : stopSingletons
					.toArray(new ColorStop[stopSingletons.size()])) {
				if (cs.pos1.value == currentValue) {
					currentValueCtr++;
					if (currentValueCtr >= 3) {
						stopSingletons.remove(lastColorStop);
					}
				} else {
					currentValue = cs.pos1.value;
					currentValueCtr = 1;
				}
				lastColorStop = cs;
			}

			// prevent consecutive color stops from being equal
			lastColorStop = null;
			for (ColorStop cs : stopSingletons
					.toArray(new ColorStop[stopSingletons.size()])) {
				if (lastColorStop != null
						&& lastColorStop.pos1.value == cs.pos1.value) {
					cs.pos1.value += .00001f;
				}

				lastColorStop = cs;
			}

			if (stopSingletons.get(0).pos1.value < 0 || stopSingletons
					.get(stopSingletons.size() - 1).pos1.value > 100) {
				// the LinearGradientPaint requires fractional values not fall
				// outside [0,100]%. In the rare case that we're going to fall
				// outside of that range, let's just readjust our
				// line/percentages:
				float origin = stopSingletons.get(0).pos1.value;
				float range = stopSingletons
						.get(stopSingletons.size() - 1).pos1.value - origin;
				double theta = Math.atan2(line.getY2() - line.getY1(),
						line.getX2() - line.getX1());
				line.setLine(
						line.getX1()
								+ distance * origin / 100 * Math.cos(theta),
						line.getY1()
								+ distance * origin / 100 * Math.sin(theta),
						line.getX1() + distance * (origin + range) / 100
								* Math.cos(theta),
						line.getY1() + distance * (origin + range) / 100
								* Math.sin(theta));
				for (ColorStop cs : stopSingletons
						.toArray(new ColorStop[stopSingletons.size()])) {
					cs.pos1.value = (cs.pos1.value - origin) / range * 100;
				}

			}

			Color[] colors = new Color[stopSingletons.size()];
			float[] positions = new float[stopSingletons.size()];

			int ctr = 0;
			for (ColorStop cs : stopSingletons) {
				colors[ctr] = cs.color;
				positions[ctr] = cs.pos1.value / 100f;
				ctr++;
			}

			CycleMethod cycle = CycleMethod.NO_CYCLE;
			if (repeating) {
				cycle = CycleMethod.REPEAT;
				float lastFraction = positions[positions.length - 1];
				line = scaleLine(line, lastFraction);
				for (int a = 0; a < positions.length; a++) {
					positions[a] = positions[a] / lastFraction;
				}

				// GradientTexturePaint antialiases the seam of repeating tiles
				// better
				return new GradientTexturePaint(colors, positions, line.getP1(),
						line.getP2(), cycle);
			}

			return new LinearGradientPaint(line.getP1(), line.getP2(),
					positions, colors, cycle);
		}

		private Line2D scaleLine(Line2D line, float fraction) {
			double theta = Math.atan2(line.getY2() - line.getY1(),
					line.getX2() - line.getX1());
			double d = line.getP1().distance(line.getP2()) * fraction;
			return new Line2D.Double(line.getX1(), line.getY1(),
					line.getX1() + d * Math.cos(theta),
					line.getY1() + d * Math.sin(theta));
		}
	}

	public static class BackgroundImage implements ImageValue {
		List<ImageValue> images;

		public BackgroundImage(List<ImageValue> images) {
			this.images = new ArrayList<>(images);
		}

		public List<ImageValue> getImageValues() {
			return Collections.unmodifiableList(images);
		}

		@Override
		public void paintRectangle(Graphics2D g, int x, int y, int width,
				int height) {
			for (int a = images.size() - 1; a >= 0; a--) {
				Graphics2D g2 = (Graphics2D) g.create();
				images.get(a).paintRectangle(g2, x, y, width, height);
				g2.dispose();
			}
		}
	}

	@Override
	public String getPropertyName() {
		return CSS.Attribute.BACKGROUND_IMAGE.toString();
	}

	@Override
	public CssBackgroundImagePropertyHandler.BackgroundImage parse(
			String value) {
		int index = 0;

		// consume leading whitespace
		while (index < value.length()
				&& Character.isWhitespace(value.charAt(index))) {
			index++;
		}

		List<ImageValue> images = new ArrayList<>();
		while (index < value.length()) {
			index = parseImageValue(value, index, images);

			// consume trailing whitespace
			while (index < value.length()
					&& Character.isWhitespace(value.charAt(index))) {
				index++;
			}

			// consume possible comma
			if (index < value.length() && value.charAt(index) == ',') {
				index++;
			}

			// consume leading whitespace
			while (index < value.length()
					&& Character.isWhitespace(value.charAt(index))) {
				index++;
			}
		}
		return new CssBackgroundImagePropertyHandler.BackgroundImage(images);
	}

	protected int parseImageValue(String value, int index,
			List<ImageValue> dest) {
		String s = value.toLowerCase();

		s = s.substring(index);
		if (s.startsWith("url(\"")) {
			int i2 = s.indexOf("\")");
			dest.add(new URLImageValue(
					value.substring(index + "url(\"".length(), i2)));
			return i2 + "\")".length();
		} else if (s.startsWith("linear-gradient(")) {
			StringBuilder sb = new StringBuilder();
			int i2 = CssParserUtils.getClosingParentheses(value,
					index + "linear-gradient".length(), sb);
			dest.add(new LinearGradientImageValue(sb.toString(), false));
			return i2 + ")".length();
		} else if (s.startsWith("repeating-linear-gradient(")) {
			StringBuilder sb = new StringBuilder();
			int i2 = CssParserUtils.getClosingParentheses(value,
					index + "repeating-linear-gradient".length(), sb);
			dest.add(new LinearGradientImageValue(sb.toString(), true));
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
			dest.add(new NoneImageValue());
			return index + "none".length();
		} else if (s.startsWith("inherit") || s.startsWith("initial")) {
			throw new IllegalArgumentException(
					"the \"inherit\" and \"initial\" background fills are currently unsupported");
		}

		throw new IllegalArgumentException(
				"unsupported background image value \"" + value.substring(index)
						+ "\"");
	}

}
