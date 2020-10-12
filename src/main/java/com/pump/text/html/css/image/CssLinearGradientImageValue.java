package com.pump.text.html.css.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.pump.awt.GradientTexturePaint;
import com.pump.plaf.AnimationManager;
import com.pump.text.html.css.CssColorParser;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.view.QViewHelper;

public class CssLinearGradientImageValue implements CssImageValue {

	public static class ColorStop {
		Color color;

		CssLength pos1, pos2;

		/**
		 * @param colorArg
		 *            the color argument, such as "#3F9" or "red"
		 * @param stopArg
		 *            the optional stop argument(s), such as "30% 50%" or "0" or
		 *            "10px"
		 */
		public ColorStop(String colorArg, String stopArg) {
			color = new CssColorParser().parse(colorArg);

			if (stopArg != null) {
				List<CssLength> lengths = parseLengths(stopArg);
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

		public ColorStop(Color color, CssLength pos1) {
			this.color = color;
			this.pos1 = pos1;
		}

		private List<CssLength> parseLengths(String str) {
			str = str.trim();
			List<CssLength> returnValue = new LinkedList<>();
			while (true) {
				int i = str.indexOf(' ');
				if (i == -1) {
					if (str.length() > 0)
						returnValue.add(new CssLength(str));
					return returnValue;
				} else {
					returnValue.add(new CssLength(str.substring(0, i)));
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
			sb.append("rgba(" + color.getRed() + "," + color.getGreen() + ","
					+ color.getBlue() + "," + color.getAlpha() + ")");
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
	 * If the direction is defined as "to right" or "to bottom left" then this
	 * object defines that path.
	 */
	public enum Direction {
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
		 * Create a line used for a gradient where the first point corresponds
		 * to the first color and the second point corresponds to the last
		 * color.
		 */
		abstract Line2D createLine(int x, int y, int width, int height);
	}

	/**
	 * The optional Direction this gradient uses. Either "direction" or "theta"
	 * should be populated.
	 */
	protected Direction direction;

	/**
	 * The optional angle (in radians) this gradient uses. Either "direction" or
	 * "theta" should be populated.
	 */
	protected Double theta = -Math.PI / 2;

	/**
	 * This is a combination of ColorStops and Lengths (as interpolation hints)
	 */
	protected List<Object> colorStopsAndHints = new ArrayList<>();

	protected boolean repeating;

	private final String cssString;

	/**
	 * 
	 * @param cssString
	 * @param theta
	 *            the angle in radians
	 * @param colorStopsAndHints
	 *            a list of ColorStops and CssLengths as interpolation hints.
	 */
	public CssLinearGradientImageValue(String cssString, double theta,
			List<Object> colorStopsAndHints, boolean repeating) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(colorStopsAndHints);
		this.cssString = cssString;
		this.theta = theta;
		this.colorStopsAndHints = colorStopsAndHints;
		this.repeating = repeating;
		validate();
	}

	/**
	 * 
	 * @param cssString
	 * @param direction
	 * @param colorStopsAndHints
	 *            a list of ColorStops and CssLengths as interpolation hints.
	 */
	public CssLinearGradientImageValue(String cssString, Direction direction,
			List<Object> colorStopsAndHints, boolean repeating) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(direction);
		Objects.requireNonNull(colorStopsAndHints);
		this.cssString = cssString;
		this.direction = direction;
		this.colorStopsAndHints = colorStopsAndHints;
		this.repeating = repeating;
		validate();
	}

	/**
	 * Validate the contents of colorStopsAndHints.
	 */
	private void validate() {
		for (Object e : colorStopsAndHints) {
			if (e instanceof ColorStop) {
				ColorStop cs = (ColorStop) e;
				cs.pos1 = validateColorStopPosition(cs.pos1);
				cs.pos2 = validateColorStopPosition(cs.pos2);
			} else if (e instanceof CssLength) {
				if (e != validateColorStopPosition((CssLength) e))
					throw new IllegalArgumentException(
							"the interpolation hint (\"" + e
									+ "\" must have a unit");
			} else {
				throw new IllegalArgumentException("unsupported element " + e);
			}
		}

		try {
			// make sure we can interpret everything without incident:
			createPaint(0, 0, 100, 100);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException(
					"The linear-gradient \"" + cssString
							+ "\" was not parsed correctly or is unsupported.",
					e);
		}
	}

	private CssLength validateColorStopPosition(CssLength pos) {
		if (pos == null)
			return null;
		if (pos.getUnit().isEmpty() && pos.getValue() == 0)
			return new CssLength(pos.toCSSString(), 0, "%");
		if (pos.getUnit().equals("%") || pos.getUnit().equals("px"))
			return pos;

		throw new IllegalArgumentException(
				"this linear gradient implementation does not support \""
						+ pos.getUnit() + "\" units");
	}

	@Override
	public void paintRectangle(Graphics2D g, QViewHelper viewHelper,
			int layerIndex, int x, int y, int width, int height) {
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

		List<ColorStop> stopSingletons = createSingletonStops(line);

		Color[] colors = new Color[stopSingletons.size()];
		float[] positions = new float[stopSingletons.size()];

		int ctr = 0;
		for (ColorStop cs : stopSingletons) {
			colors[ctr] = cs.color;
			positions[ctr] = cs.pos1.getValue() / 100f;
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

		return new LinearGradientPaint(line.getP1(), line.getP2(), positions,
				colors, cycle);
	}

	/**
	 * Create a list of single-position ColorStops that are all in percent
	 * units.
	 * 
	 * @param line
	 *            the line representing the gradient. In rare cases this method
	 *            may adjust the value of this line.
	 */
	private List<ColorStop> createSingletonStops(Line2D line) {
		List<ColorStop> returnValue = new ArrayList<>(
				colorStopsAndHints.size());

		double distance = line.getP1().distance(line.getP2());

		/*
		 * https://drafts.csswg.org/css-images-4/#color-stop-syntax:
		 * 
		 * 3.5.3. Color Stop “Fixup”
		 * 
		 * When resolving the used positions of each color stop, the following
		 * steps must be applied in order:
		 * 
		 * 1. If the first color stop does not have a position, set its position
		 * to 0%. If the last color stop does not have a position, set its
		 * position to 100%.
		 * 
		 * 2. If a color stop or transition hint has a position that is less
		 * than the specified position of any color stop or transition hint
		 * before it in the list, set its position to be equal to the largest
		 * specified position of any color stop or transition hint before it.
		 * 
		 * 3. If any color stop still does not have a position, then, for each
		 * run of adjacent color stops without positions, set their positions so
		 * that they are evenly spaced between the preceding and following color
		 * stops with positions.
		 */

		// split up paired stops into singletons:
		CssLength interpolationHint = null;
		ColorStop lastColorStop = null;
		for (Object t : colorStopsAndHints) {
			if (t instanceof CssLength) {
				interpolationHint = (CssLength) t;
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

					Color newColor = AnimationManager.tween(lastColorStop.color,
							newPos1.color, .5);
					float newPos;
					if ("%".equals(interpolationHint.getUnit())) {
						newPos = interpolationHint.getValue();
					} else if ("px".equals(interpolationHint.getUnit())) {
						newPos = (float) (interpolationHint.getValue() * 100
								/ distance);
					} else {
						// this should not have passed validate()
						throw new IllegalArgumentException(
								"this linear gradient implementation does not support \""
										+ cs.pos1.getUnit() + "\" units");
					}
					returnValue.add(new ColorStop(newColor,
							new CssLength(newPos, "%")));
					interpolationHint = null;
				}

				returnValue.add(newPos1);
				if (newPos2 != null)
					returnValue.add(newPos2);

				lastColorStop = returnValue.get(returnValue.size() - 1);
			} else {
				// this should not have passed validate()
				throw new RuntimeException("Unexpected element: " + t);
			}
		}

		// convert each singleton to use %'s:
		for (ColorStop cs : returnValue) {
			if (cs.pos1 != null && cs.pos1.getUnit() != null) {
				if ("px".equals(cs.pos1.getUnit())) {
					cs.pos1 = new CssLength(
							(float) (cs.pos1.getValue() * 100 / distance), "%");
				} else if ("%".equals(cs.pos1.getUnit())) {
					// do nothing, this is OK
				} else {
					// this should not have passed validate()
					throw new IllegalArgumentException(
							"this linear gradient implementation does not support \""
									+ cs.pos1.getUnit() + "\" units");
				}
			}
		}

		// if we wanted to be super strict/safe, we could convert each
		// singleton to an int to prevent rounding errors. But that seems
		// like overkill until we have a need for it?

		// apply rule 1:
		if (returnValue.get(0).pos1 == null) {
			returnValue.get(0).pos1 = new CssLength(0f, "%");
		}

		if (returnValue.get(returnValue.size() - 1).pos1 == null) {
			returnValue.get(returnValue.size() - 1).pos1 = new CssLength(100,
					"%");
		}

		// apply rule 2:
		float runningMax = returnValue.get(0).pos1.getValue();
		for (ColorStop cs : returnValue) {
			if (cs.pos1 != null) {
				if (cs.pos1.getValue() < runningMax)
					cs.pos1 = new CssLength(runningMax, cs.pos1.getUnit());
				runningMax = Math.max(runningMax, cs.pos1.getValue());
			}
		}

		// apply rule 3:

		List<ColorStop> unknownRun = new ArrayList<>();
		for (ColorStop cs : returnValue) {
			if (cs.pos1 != null && !unknownRun.isEmpty()) {
				ColorStop first = unknownRun.get(0);
				ColorStop preceding = returnValue
						.get(returnValue.indexOf(first) - 1);
				ColorStop next = cs;

				float range = next.pos1.getValue() - preceding.pos1.getValue();
				for (int k = 0; k < unknownRun.size(); k++) {
					unknownRun.get(k).pos1 = new CssLength(
							preceding.pos1.getValue()
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
		for (ColorStop cs : returnValue
				.toArray(new ColorStop[returnValue.size()])) {
			if (cs.pos1.getValue() == currentValue) {
				currentValueCtr++;
				if (currentValueCtr >= 3) {
					returnValue.remove(lastColorStop);
				}
			} else {
				currentValue = cs.pos1.getValue();
				currentValueCtr = 1;
			}
			lastColorStop = cs;
		}

		// prevent consecutive color stops from being equal
		lastColorStop = null;
		for (ColorStop cs : returnValue
				.toArray(new ColorStop[returnValue.size()])) {
			if (lastColorStop != null
					&& lastColorStop.pos1.getValue() == cs.pos1.getValue()) {
				cs.pos1 = new CssLength(cs.pos1.getValue() + .00001f,
						cs.pos1.getUnit());
			}

			lastColorStop = cs;
		}

		if (returnValue.get(0).pos1.getValue() < 0
				|| returnValue.get(returnValue.size() - 1).pos1
						.getValue() > 100) {
			// the LinearGradientPaint requires fractional values not fall
			// outside [0,100]%. In the rare case that we're going to fall
			// outside of that range, let's just readjust our
			// line/percentages:
			float origin = returnValue.get(0).pos1.getValue();
			float range = returnValue.get(returnValue.size() - 1).pos1
					.getValue() - origin;
			double theta = Math.atan2(line.getY2() - line.getY1(),
					line.getX2() - line.getX1());
			line.setLine(
					line.getX1() + distance * origin / 100 * Math.cos(theta),
					line.getY1() + distance * origin / 100 * Math.sin(theta),
					line.getX1() + distance * (origin + range) / 100
							* Math.cos(theta),
					line.getY1() + distance * (origin + range) / 100
							* Math.sin(theta));
			for (ColorStop cs : returnValue
					.toArray(new ColorStop[returnValue.size()])) {
				cs.pos1 = new CssLength(
						(cs.pos1.getValue() - origin) / range * 100,
						cs.pos1.getUnit());
			}
		}

		return returnValue;
	}

	private Line2D scaleLine(Line2D line, float fraction) {
		double theta = Math.atan2(line.getY2() - line.getY1(),
				line.getX2() - line.getX1());
		double d = line.getP1().distance(line.getP2()) * fraction;
		return new Line2D.Double(line.getX1(), line.getY1(),
				line.getX1() + d * Math.cos(theta),
				line.getY1() + d * Math.sin(theta));
	}

	@Override
	public String toCSSString() {
		return cssString;
	}
}
