package com.pump.text.html.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.awt.HSLColor;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.VectorImage;
import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.border.CssBorderStyleValue;
import com.pump.text.html.css.border.CssBorderStyleValue.Value;

/**
 * This renders a box's border. This rendering object takes into account the
 * final Rectangle2D we're painting to, and it converts abstract calculations
 * (like percentages) into exact pixels.
 */
public class BorderRendering {
	enum Edge {
		LEFT, RIGHT, TOP, BOTTOM;
	}

	float topWidthValue, bottomWidthValue, leftWidthValue, rightWidthValue;
	CssBorderStyleValue.Value topStyleValue, leftStyleValue, rightStyleValue,
			bottomStyleValue;
	BorderRenderingConfiguration config;
	VectorImage imageRendering = new VectorImage();
	Rectangle2D bounds;

	public BorderRendering(QViewHelper helper, Rectangle2D bounds) {
		this(new BorderRenderingConfiguration(helper), bounds);
	}

	public BorderRendering(BorderRenderingConfiguration config,
			Rectangle2D bounds) {
		this.config = config;
		this.bounds = bounds;
		topWidthValue = config.topWidth == null ? 0
				: config.topWidth.getValue(bounds.getHeight());
		bottomWidthValue = config.bottomWidth == null ? 0
				: config.bottomWidth.getValue(bounds.getHeight());
		leftWidthValue = config.leftWidth == null ? 0
				: config.leftWidth.getValue(bounds.getWidth());
		rightWidthValue = config.rightWidth == null ? 0
				: config.rightWidth.getValue(bounds.getWidth());

		leftStyleValue = config.leftStyle == null ? Value.NONE
				: config.leftStyle.getValue();
		topStyleValue = config.topStyle == null ? Value.NONE
				: config.topStyle.getValue();
		rightStyleValue = config.rightStyle == null ? Value.NONE
				: config.rightStyle.getValue();
		bottomStyleValue = config.bottomStyle == null ? Value.NONE
				: config.bottomStyle.getValue();

		imageRendering.getOperations().addAll(renderDottedBorders());
		imageRendering.getOperations().addAll(renderDashedBorders());
		imageRendering.getOperations().addAll(renderTrapezoidBorders());
	}

	private double getWidth(Edge edge) {
		switch (edge) {
		case TOP:
			return topWidthValue;
		case LEFT:
			return leftWidthValue;
		case RIGHT:
			return rightWidthValue;
		case BOTTOM:
			return bottomWidthValue;
		}
		throw new IllegalStateException("edge: " + edge);
	}

	private CssBorderStyleValue.Value getStyle(Edge edge) {
		switch (edge) {
		case TOP:
			return topStyleValue;
		case LEFT:
			return leftStyleValue;
		case RIGHT:
			return rightStyleValue;
		case BOTTOM:
			return bottomStyleValue;
		}
		throw new IllegalStateException("edge: " + edge);
	}

	private CssColorValue getColor(Edge edge) {
		switch (edge) {
		case TOP:
			return config.topColor;
		case LEFT:
			return config.leftColor;
		case RIGHT:
			return config.rightColor;
		case BOTTOM:
			return config.bottomColor;
		}
		throw new IllegalStateException("edge: " + edge);
	}

	/**
	 * This renders borders that can be expressed as trapezoids.
	 */
	private List<Operation> renderTrapezoidBorders() {
		Map<Color, Area> areas = new HashMap<>();

		for (Edge edge : Edge.values()) {
			CssColorValue color = getColor(edge);
			if (getWidth(edge) > 0 && color != null) {
				switch (getStyle(edge)) {
				case SOLID:
					Path2D p1 = createTrapezoid(edge, 0, 1);
					addShape(areas, p1, color);
					break;
				case DOUBLE:
					Path2D p2a = createTrapezoid(edge, 0, .333f);
					Path2D p2b = createTrapezoid(edge, .666f, 1);
					addShape(areas, p2a, color);
					addShape(areas, p2b, color);
					break;
				case INSET:
					Path2D p3 = createTrapezoid(edge, 0, 1);
					Color c1;
					if (edge == Edge.TOP || edge == Edge.LEFT) {
						c1 = darken(color);
					} else {
						c1 = lighten(color);
					}
					addShape(areas, p3, c1);
					break;
				case OUTSET:
					Path2D p4 = createTrapezoid(edge, 0, 1);
					Color c2;
					if (edge == Edge.TOP || edge == Edge.LEFT) {
						c2 = lighten(color);
					} else {
						c2 = darken(color);
					}
					addShape(areas, p4, c2);
					break;
				case RIDGE:
					Path2D p5a = createTrapezoid(edge, .5f, 1);
					Path2D p5b = createTrapezoid(edge, 0, .5f);

					Color c3, c4;
					if (edge == Edge.TOP || edge == Edge.LEFT) {
						c3 = darken(color);
						c4 = lighten(color);
					} else {
						c3 = lighten(color);
						c4 = darken(color);
					}

					addShape(areas, p5a, c3);
					addShape(areas, p5b, c4);
					break;
				case GROOVE:
					Path2D p6a = createTrapezoid(edge, .5f, 1);
					Path2D p6b = createTrapezoid(edge, 0, .5f);

					Color c5, c6;
					if (edge == Edge.TOP || edge == Edge.LEFT) {
						c5 = lighten(color);
						c6 = darken(color);
					} else {
						c5 = darken(color);
						c6 = lighten(color);
					}

					addShape(areas, p6a, c5);
					addShape(areas, p6b, c6);
					break;
				default:
					// intentionally empty
					break;
				}
			}
		}

		VectorImage i = new VectorImage();
		Graphics2D g = i.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (Entry<Color, Area> area : areas.entrySet()) {
			g.setColor(area.getKey());
			Area v = area.getValue();
			g.fill(v);
		}

		return i.getOperations();
	}

	private Color darken(Color color) {
		float[] hsl = HSLColor.fromRGB(color, null);
		hsl[2] = Math.max(0, hsl[2] - .125f);
		int rgb = HSLColor.toRGB(hsl);
		int argb = (color.getAlpha() << 24) + rgb;
		return new Color(argb, true);
	}

	private Color lighten(Color color) {
		float[] hsl = HSLColor.fromRGB(color, null);
		hsl[2] = Math.min(1, hsl[2] + .125f);
		int rgb = HSLColor.toRGB(hsl);
		int argb = (color.getAlpha() << 24) + rgb;
		return new Color(argb, true);
	}

	private void addShape(Map<Color, Area> areas, Path2D newShape,
			Color color) {
		// clone the Color just to be sure CssColorValue won't act up
		// as a key in our map
		Color key = new Color(color.getRGB(), true);
		Area oldArea = areas.get(key);
		Area newArea = new Area(newShape);
		if (oldArea == null) {
			areas.put(key, newArea);
		} else {
			oldArea.add(newArea);
		}
	}

	/**
	 * Create a trapezoid representing part of an edge.
	 * 
	 * @param edge
	 *            the edge to represent
	 * @param startFraction
	 *            a value from [0,1], where 0 = outer edge, 1 = inner edge, and
	 *            .5 = halfway in-between
	 * @param endFraction
	 *            a value from [0,1], like startFraction
	 * @return a trapezoidal shape representing the edge/fractions.
	 */
	private Path2D createTrapezoid(Edge edge, float startFraction,
			float endFraction) {
		Path2D p = new Path2D.Double();
		switch (edge) {
		case TOP:
			p.moveTo(bounds.getMinX() + leftWidthValue * startFraction,
					bounds.getMinY() + topWidthValue * startFraction);
			p.lineTo(bounds.getMaxX() - rightWidthValue * startFraction,
					bounds.getMinY() + topWidthValue * startFraction);
			p.lineTo(bounds.getMaxX() - rightWidthValue * endFraction,
					bounds.getMinY() + topWidthValue * endFraction);
			p.lineTo(bounds.getMinX() + leftWidthValue * endFraction,
					bounds.getMinY() + topWidthValue * endFraction);
			break;
		case LEFT:
			p.moveTo(bounds.getMinX() + leftWidthValue * startFraction,
					bounds.getMaxY() - bottomWidthValue * startFraction);
			p.lineTo(bounds.getMinX() + leftWidthValue * startFraction,
					bounds.getMinY() + topWidthValue * startFraction);
			p.lineTo(bounds.getMinX() + leftWidthValue * endFraction,
					bounds.getMinY() + topWidthValue * endFraction);
			p.lineTo(bounds.getMinX() + leftWidthValue * endFraction,
					bounds.getMaxY() - bottomWidthValue * endFraction);
			break;
		case RIGHT:
			p.moveTo(bounds.getMaxX() - rightWidthValue * startFraction,
					bounds.getMinY() + topWidthValue * startFraction);
			p.lineTo(bounds.getMaxX() - rightWidthValue * startFraction,
					bounds.getMaxY() - bottomWidthValue * startFraction);
			p.lineTo(bounds.getMaxX() - rightWidthValue * endFraction,
					bounds.getMaxY() - bottomWidthValue * endFraction);
			p.lineTo(bounds.getMaxX() - rightWidthValue * endFraction,
					bounds.getMinY() + topWidthValue * endFraction);
			break;
		case BOTTOM:
			p.moveTo(bounds.getMinX() + leftWidthValue * startFraction,
					bounds.getMaxY() - bottomWidthValue * startFraction);
			p.lineTo(bounds.getMaxX() - rightWidthValue * startFraction,
					bounds.getMaxY() - bottomWidthValue * startFraction);
			p.lineTo(bounds.getMaxX() - rightWidthValue * endFraction,
					bounds.getMaxY() - bottomWidthValue * endFraction);
			p.lineTo(bounds.getMinX() + leftWidthValue * endFraction,
					bounds.getMaxY() - bottomWidthValue * endFraction);
			break;
		default:
			throw new IllegalStateException("edge: " + edge);
		}
		p.closePath();
		return p;
	}

	/**
	 * This method only renders DOTTED borders.
	 */
	private List<Operation> renderDottedBorders() {
		VectorImage i = new VectorImage();
		Graphics2D g = i.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (topStyleValue == Value.DOTTED) {
			// paint top row of dots:
			g.setColor(config.topColor);
			double minX, maxX;
			if (leftStyleValue == Value.DOTTED) {
				double z = Math.min(leftWidthValue, topWidthValue);
				g.fill(new Ellipse2D.Double(bounds.getMinX(), bounds.getMinY(),
						z, z));
				minX = bounds.getMinX() + z;
			} else {
				minX = bounds.getMinX() + leftWidthValue;
			}

			if (rightStyleValue == Value.DOTTED) {
				double z = Math.min(rightWidthValue, topWidthValue);
				g.fill(new Ellipse2D.Double(bounds.getMaxX() - z,
						bounds.getMinY(), z, z));
				maxX = bounds.getMaxX() - z;
			} else {
				maxX = bounds.getMaxX() - rightWidthValue;
			}
			renderHorizontalDots(g, minX, maxX, bounds.getMinY(),
					topWidthValue);
		}

		if (bottomStyleValue == Value.DOTTED) {
			// paint bottom row of dots:
			g.setColor(config.bottomColor);
			double minX, maxX;
			if (leftStyleValue == Value.DOTTED) {
				double z = Math.min(leftWidthValue, bottomWidthValue);
				g.fill(new Ellipse2D.Double(bounds.getMinX(),
						bounds.getMaxY() - z, z, z));
				minX = bounds.getMinX() + z;
			} else {
				minX = bounds.getMinX() + leftWidthValue;
			}
			if (rightStyleValue == Value.DOTTED) {
				double z = Math.min(rightWidthValue, bottomWidthValue);
				g.fill(new Ellipse2D.Double(bounds.getMaxX() - z,
						bounds.getMaxY() - bottomWidthValue, z, z));
				maxX = bounds.getMaxX() - z;
			} else {
				maxX = bounds.getMaxX() - rightWidthValue;
			}
			renderHorizontalDots(g, minX, maxX,
					bounds.getMaxY() - bottomWidthValue, bottomWidthValue);
		}

		if (leftStyleValue == Value.DOTTED) {
			// paint left column of dots:
			g.setColor(config.leftColor);
			double minY, maxY;
			if (topStyleValue == Value.DOTTED) {
				double z = Math.min(topWidthValue, leftWidthValue);
				minY = bounds.getMinY() + z;
			} else {
				minY = bounds.getMinY() + topWidthValue;
			}
			if (bottomStyleValue == Value.DOTTED) {
				double z = Math.min(bottomWidthValue, leftWidthValue);
				maxY = bounds.getMaxY() - z;
			} else {
				maxY = bounds.getMaxY() - bottomWidthValue;
			}
			renderVerticalDots(g, minY, maxY, bounds.getMinX(), leftWidthValue);
		}

		if (rightStyleValue == Value.DOTTED) {
			// paint right column of dots:
			g.setColor(config.rightColor);
			double minY, maxY;
			if (topStyleValue == Value.DOTTED) {
				double z = Math.min(topWidthValue, rightWidthValue);
				minY = bounds.getMinY() + z;
			} else {
				minY = bounds.getMinY() + topWidthValue;
			}
			if (bottomStyleValue == Value.DOTTED) {
				double z = Math.min(bottomWidthValue, rightWidthValue);
				maxY = bounds.getMaxY() - z;
			} else {
				maxY = bounds.getMaxY() - bottomWidthValue;
			}
			renderVerticalDots(g, minY, maxY,
					bounds.getMaxX() - rightWidthValue, rightWidthValue);
		}
		g.dispose();

		return i.getOperations();
	}

	private void renderHorizontalDots(Graphics2D g, double minX, double maxX,
			double y, float height) {
		height = Math.max(1, height);
		double space = maxX - minX;
		double width = height;
		double padding = height;
		List<RectangularShape> dots = new ArrayList<>(
				(int) (Math.ceil(space / (width + padding)) + 1.5));
		double x = minX + width;
		while (x + width + padding < maxX) {
			dots.add(new Ellipse2D.Double(x, y, width, height));
			x += width + padding;
		}

		distributePaddingAndPaint(g, dots, maxX - x, true);
	}

	private void distributePaddingAndPaint(Graphics2D g,
			List<RectangularShape> shapes, double spaceToDistribute,
			boolean distributeHorizontally) {
		for (int a = 0; a < shapes.size(); a++) {
			RectangularShape shape = shapes.get(a);
			if (distributeHorizontally) {
				shape.setFrame(
						shape.getX() + (a + 1) * spaceToDistribute
								/ (shapes.size() + 1),
						shape.getY(), shape.getWidth(), shape.getHeight());
			} else {
				shape.setFrame(shape.getX(),
						shape.getY() + (a + 1) * spaceToDistribute
								/ (shapes.size() + 1),
						shape.getWidth(), shape.getHeight());
			}
			g.fill(shape);
		}
	}

	private void renderVerticalDots(Graphics2D g, double minY, double maxY,
			double x, float width) {
		width = Math.max(1, width);
		double space = maxY - minY;
		double height = width;
		double padding = width;
		List<RectangularShape> dots = new ArrayList<>(
				(int) (Math.ceil(space / (height + padding)) + 1.5));
		double y = minY + height;
		while (y + height + padding < maxY) {
			dots.add(new Ellipse2D.Double(x, y, width, height));
			y += height + padding;
		}

		distributePaddingAndPaint(g, dots, maxY - y, false);
	}

	/**
	 * This method only renders DASHED borders.
	 */
	private List<Operation> renderDashedBorders() {
		VectorImage i = new VectorImage();
		Graphics2D g = i.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (topStyleValue == Value.DASHED) {
			g.setColor(config.topColor);
			if (leftStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(bounds.getMinX(),
						bounds.getMinY(), leftWidthValue, topWidthValue));
			}
			if (rightStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(
						bounds.getMaxX() - rightWidthValue, bounds.getMinY(),
						rightWidthValue, topWidthValue));
			}
			double minX = bounds.getMinX() + leftWidthValue;
			double maxX = bounds.getMaxX() - rightWidthValue;

			renderHorizontalDashes(g, minX, maxX, bounds.getMinY(),
					topWidthValue);
		}

		if (bottomStyleValue == Value.DASHED) {
			g.setColor(config.bottomColor);
			if (leftStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(bounds.getMinX(),
						bounds.getMaxY() - bottomWidthValue, leftWidthValue,
						bottomWidthValue));
			}
			if (rightStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(
						bounds.getMaxX() - rightWidthValue,
						bounds.getMaxY() - bottomWidthValue, rightWidthValue,
						bottomWidthValue));
			}
			double minX = bounds.getMinX() + leftWidthValue;
			double maxX = bounds.getMaxX() - rightWidthValue;
			renderHorizontalDashes(g, minX, maxX,
					bounds.getMaxY() - bottomWidthValue, bottomWidthValue);
		}

		if (leftStyleValue == Value.DASHED) {
			g.setColor(config.leftColor);
			double minY = bounds.getMinY() + topWidthValue;
			double maxY = bounds.getMaxY() - bottomWidthValue;
			renderVerticalDashes(g, minY, maxY, bounds.getMinX(),
					leftWidthValue);
		}

		if (rightStyleValue == Value.DASHED) {
			g.setColor(config.rightColor);
			double minY = bounds.getMinY() + topWidthValue;
			double maxY = bounds.getMaxY() - bottomWidthValue;
			renderVerticalDashes(g, minY, maxY,
					bounds.getMaxX() - rightWidthValue, rightWidthValue);
		}
		g.dispose();

		return i.getOperations();
	}

	private void renderHorizontalDashes(Graphics2D g, double minX, double maxX,
			double y, float height) {
		height = Math.max(1, height);
		double space = maxX - minX;
		double width = 2 * height;
		double padding = 2 * height;
		List<RectangularShape> dashes = new ArrayList<>(
				(int) (Math.ceil(space / (width + padding)) + 1.5));
		double x = minX + padding;
		while (x + width + padding < maxX) {
			dashes.add(new Rectangle2D.Double(x, y, width, height));
			x += width + padding;
		}

		distributePaddingAndPaint(g, dashes, maxX - x, true);
	}

	private void renderVerticalDashes(Graphics2D g, double minY, double maxY,
			double x, float width) {
		width = Math.max(1, width);
		double space = maxY - minY;
		double height = 2 * width;
		double padding = 2 * width;
		List<RectangularShape> dashes = new ArrayList<>(
				(int) (Math.ceil(space / (height + padding)) + 1.5));
		double y = minY + padding;
		while (y + height + padding < maxY) {
			dashes.add(new Rectangle2D.Double(x, y, width, height));
			y += height + padding;
		}

		distributePaddingAndPaint(g, dashes, maxY - y, false);
	}

	/**
	 * Paint this BorderRendering.
	 */
	public void paint(Graphics2D g) {
		imageRendering.paint(g);
	}
}