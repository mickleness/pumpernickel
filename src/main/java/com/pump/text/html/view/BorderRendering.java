package com.pump.text.html.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
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
	RectangularShape shape;

	public BorderRendering(BorderRenderingConfiguration config,
			Rectangle2D bounds) {
		this.config = config;
		shape = getShape(bounds);
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

		if (shape instanceof Rectangle2D) {
			imageRendering.getOperations()
					.addAll(renderRectangleDottedBorders());
			imageRendering.getOperations()
					.addAll(renderRectangleDashedBorders());
		}
		imageRendering.getOperations().addAll(renderTrapezoidBorders());
	}

	private RectangularShape getShape(Rectangle2D bounds) {
		CssRoundRectangle2D r = new CssRoundRectangle2D(bounds,
				config.topLeftRadius, config.topRightRadius,
				config.bottomRightRadius, config.bottomLeftRadius);
		if (r.isRectangle())
			return (Rectangle2D) bounds.clone();
		return r;
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
	 * This renders borders that can be expressed as trapezoids (SOLID, DOUBLE,
	 * INSET, OUTSET, RIDGE, GROOVE).
	 */
	private List<Operation> renderTrapezoidBorders() {
		Map<Color, Area> areas = new HashMap<>();

		for (Edge edge : Edge.values()) {
			CssColorValue color = getColor(edge);
			if (getWidth(edge) > 0 && color != null) {
				switch (getStyle(edge)) {
				case SOLID:
					Shape p1 = createEdgeShape(edge, 0, 1);
					addShape(areas, p1, color);
					break;
				case DOUBLE:
					Shape p2a = createEdgeShape(edge, 0, .333f);
					Shape p2b = createEdgeShape(edge, .666f, 1);
					addShape(areas, p2a, color);
					addShape(areas, p2b, color);
					break;
				case INSET:
					Shape p3 = createEdgeShape(edge, 0, 1);
					Color c1;
					if (edge == Edge.TOP || edge == Edge.LEFT) {
						c1 = darken(color);
					} else {
						c1 = lighten(color);
					}
					addShape(areas, p3, c1);
					break;
				case OUTSET:
					Shape p4 = createEdgeShape(edge, 0, 1);
					Color c2;
					if (edge == Edge.TOP || edge == Edge.LEFT) {
						c2 = lighten(color);
					} else {
						c2 = darken(color);
					}
					addShape(areas, p4, c2);
					break;
				case RIDGE:
					Shape p5a = createEdgeShape(edge, .5f, 1);
					Shape p5b = createEdgeShape(edge, 0, .5f);

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
					Shape p6a = createEdgeShape(edge, .5f, 1);
					Shape p6b = createEdgeShape(edge, 0, .5f);

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

	private void addShape(Map<Color, Area> areas, Shape newShape, Color color) {
		// clone the Color just to be sure CssColorValue won't act up
		// as a key in our map
		Color key = new Color(color.getRGB(), true);
		Area oldArea = areas.get(key);
		Area newArea = newShape instanceof Area ? (Area) newShape
				: new Area(newShape);
		if (oldArea == null) {
			areas.put(key, newArea);
		} else {
			oldArea.add(newArea);
		}
	}

	/**
	 * Create a shape representing an edge.
	 * <p>
	 * When the parent shape is rectangular this returns a trapezoid Path2D. But
	 * when curvature is applied this returns a (flattened) Area.
	 * 
	 * @param edge
	 *            the edge to represent
	 * @param startFractionDepth
	 *            a value from [0,1], where 0 = outer edge, 1 = inner edge, and
	 *            .5 = halfway in-between
	 * @param endFractionDepth
	 *            a value from [0,1], like startFractionDepth
	 * @return a trapezoidal shape representing the edge/fractions.
	 */
	private Shape createEdgeShape(Edge edge, float startFractionDepth,
			float endFractionDepth) {
		Shape returnValue;
		if (shape instanceof Rectangle2D) {
			Path2D p = new Path2D.Double();
			switch (edge) {
			case TOP:
				p.moveTo(shape.getMinX() + leftWidthValue * startFractionDepth,
						shape.getMinY() + topWidthValue * startFractionDepth);
				p.lineTo(shape.getMaxX() - rightWidthValue * startFractionDepth,
						shape.getMinY() + topWidthValue * startFractionDepth);
				p.lineTo(shape.getMaxX() - rightWidthValue * endFractionDepth,
						shape.getMinY() + topWidthValue * endFractionDepth);
				p.lineTo(shape.getMinX() + leftWidthValue * endFractionDepth,
						shape.getMinY() + topWidthValue * endFractionDepth);
				break;
			case LEFT:
				p.moveTo(shape.getMinX() + leftWidthValue * startFractionDepth,
						shape.getMaxY()
								- bottomWidthValue * startFractionDepth);
				p.lineTo(shape.getMinX() + leftWidthValue * startFractionDepth,
						shape.getMinY() + topWidthValue * startFractionDepth);
				p.lineTo(shape.getMinX() + leftWidthValue * endFractionDepth,
						shape.getMinY() + topWidthValue * endFractionDepth);
				p.lineTo(shape.getMinX() + leftWidthValue * endFractionDepth,
						shape.getMaxY() - bottomWidthValue * endFractionDepth);
				break;
			case RIGHT:
				p.moveTo(shape.getMaxX() - rightWidthValue * startFractionDepth,
						shape.getMinY() + topWidthValue * startFractionDepth);
				p.lineTo(shape.getMaxX() - rightWidthValue * startFractionDepth,
						shape.getMaxY()
								- bottomWidthValue * startFractionDepth);
				p.lineTo(shape.getMaxX() - rightWidthValue * endFractionDepth,
						shape.getMaxY() - bottomWidthValue * endFractionDepth);
				p.lineTo(shape.getMaxX() - rightWidthValue * endFractionDepth,
						shape.getMinY() + topWidthValue * endFractionDepth);
				break;
			case BOTTOM:
				p.moveTo(shape.getMinX() + leftWidthValue * startFractionDepth,
						shape.getMaxY()
								- bottomWidthValue * startFractionDepth);
				p.lineTo(shape.getMaxX() - rightWidthValue * startFractionDepth,
						shape.getMaxY()
								- bottomWidthValue * startFractionDepth);
				p.lineTo(shape.getMaxX() - rightWidthValue * endFractionDepth,
						shape.getMaxY() - bottomWidthValue * endFractionDepth);
				p.lineTo(shape.getMinX() + leftWidthValue * endFractionDepth,
						shape.getMaxY() - bottomWidthValue * endFractionDepth);
				break;
			default:
				throw new IllegalStateException("edge: " + edge);
			}
			p.closePath();
			returnValue = p;
		} else {
			Area shapeArea = getWedgeArea(edge);

			BasicStroke outerStroke = new BasicStroke(
					(float) (endFractionDepth * 2 * getWidth(edge)));
			Area strokeArea = flatten(outerStroke.createStrokedShape(shape));
			strokeArea.intersect(shapeArea);

			if (startFractionDepth != 0) {
				BasicStroke innerStroke = new BasicStroke(
						(float) (startFractionDepth * 2 * getWidth(edge)));
				Area innerStrokeArea = flatten(
						innerStroke.createStrokedShape(shape));
				strokeArea.subtract(innerStrokeArea);
			}
			returnValue = strokeArea;
		}
		return returnValue;
	}

	private Area shapeAsArea = null;
	private Map<Edge, Area> shapeWedgesByEdge = new HashMap<>();

	/**
	 * Return the intersection of the parent shape and createWedge.
	 * <p>
	 * That is: if the parent shape is a pie divided into 4 pieces, this returns
	 * one piece as an Area. The Area is flattened (so it is made up of small
	 * line segments).
	 * <p>
	 * This caches values. Bevels, ridges, doubles, etc reuse the same wedge
	 * area.
	 */
	private Area getWedgeArea(Edge edge) {
		Area returnValue = shapeWedgesByEdge.get(edge);
		if (returnValue == null) {
			if (shapeAsArea == null)
				shapeAsArea = flatten(shape);

			returnValue = new Area(shapeAsArea);
			returnValue.intersect(createWedge(edge));
			shapeWedgesByEdge.put(edge, returnValue);
		}
		return returnValue;
	}

	/**
	 * Create the triangular wedge shape that is guaranteed to encompass an
	 * edge.
	 */
	private Area createWedge(Edge edge) {
		// always fudge k pixels on all sides to be 100% sure areas will err on
		// the side of overlapping instead of underlapping. Without this we can
		// see a faint hairline separation between two edges.
		float k = .5f;

		Path2D wedge = new Path2D.Double();
		switch (edge) {
		case TOP:
			wedge.moveTo(shape.getCenterX() - k, shape.getCenterY());
			wedge.lineTo(shape.getMinX() - k, shape.getMinY());
			wedge.lineTo(shape.getMaxX() + k, shape.getMinY());
			wedge.lineTo(shape.getCenterX() + k, shape.getCenterY());
			break;
		case LEFT:
			wedge.moveTo(shape.getCenterX(), shape.getCenterY() - k);
			wedge.lineTo(shape.getMinX(), shape.getMinY() - k);
			wedge.lineTo(shape.getMinX(), shape.getMaxY() + k);
			wedge.moveTo(shape.getCenterX(), shape.getCenterY() + k);
			break;
		case RIGHT:
			wedge.moveTo(shape.getCenterX(), shape.getCenterY() - k);
			wedge.lineTo(shape.getMaxX(), shape.getMinY() - k);
			wedge.lineTo(shape.getMaxX(), shape.getMaxY() + k);
			wedge.moveTo(shape.getCenterX(), shape.getCenterY() + k);
			break;
		case BOTTOM:
			wedge.moveTo(shape.getCenterX() - k, shape.getCenterY());
			wedge.lineTo(shape.getMinX() - k, shape.getMaxY());
			wedge.lineTo(shape.getMaxX() + k, shape.getMaxY());
			wedge.lineTo(shape.getCenterX() + k, shape.getCenterY());
			break;
		default:
			throw new IllegalStateException("edge: " + edge);
		}
		wedge.closePath();

		return new Area(wedge);
	}

	private Area flatten(Shape shape) {
		PathIterator flattenedPI = shape.getPathIterator(null, .01);
		Path2D p = new Path2D.Double(flattenedPI.getWindingRule());
		p.append(flattenedPI, false);
		return new Area(p);
	}

	/**
	 * This method only renders DOTTED borders around Rectangle2D borders.
	 */
	private List<Operation> renderRectangleDottedBorders() {
		VectorImage i = new VectorImage();
		Graphics2D g = i.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (topStyleValue == Value.DOTTED && config.topColor != null) {
			// paint top row of dots:
			g.setColor(config.topColor);
			double minX, maxX;
			if (leftStyleValue == Value.DOTTED) {
				double z = Math.min(leftWidthValue, topWidthValue);
				g.fill(new Ellipse2D.Double(shape.getMinX(), shape.getMinY(), z,
						z));
				minX = shape.getMinX() + z;
			} else {
				minX = shape.getMinX() + leftWidthValue;
			}

			if (rightStyleValue == Value.DOTTED) {
				double z = Math.min(rightWidthValue, topWidthValue);
				g.fill(new Ellipse2D.Double(shape.getMaxX() - z,
						shape.getMinY(), z, z));
				maxX = shape.getMaxX() - z;
			} else {
				maxX = shape.getMaxX() - rightWidthValue;
			}
			renderHorizontalDots(g, minX, maxX, shape.getMinY(), topWidthValue);
		}

		if (bottomStyleValue == Value.DOTTED && config.bottomColor != null) {
			// paint bottom row of dots:
			g.setColor(config.bottomColor);
			double minX, maxX;
			if (leftStyleValue == Value.DOTTED) {
				double z = Math.min(leftWidthValue, bottomWidthValue);
				g.fill(new Ellipse2D.Double(shape.getMinX(),
						shape.getMaxY() - z, z, z));
				minX = shape.getMinX() + z;
			} else {
				minX = shape.getMinX() + leftWidthValue;
			}
			if (rightStyleValue == Value.DOTTED) {
				double z = Math.min(rightWidthValue, bottomWidthValue);
				g.fill(new Ellipse2D.Double(shape.getMaxX() - z,
						shape.getMaxY() - bottomWidthValue, z, z));
				maxX = shape.getMaxX() - z;
			} else {
				maxX = shape.getMaxX() - rightWidthValue;
			}
			renderHorizontalDots(g, minX, maxX,
					shape.getMaxY() - bottomWidthValue, bottomWidthValue);
		}

		if (leftStyleValue == Value.DOTTED && config.leftColor != null) {
			// paint left column of dots:
			g.setColor(config.leftColor);
			double minY, maxY;
			if (topStyleValue == Value.DOTTED) {
				double z = Math.min(topWidthValue, leftWidthValue);
				minY = shape.getMinY() + z;
			} else {
				minY = shape.getMinY() + topWidthValue;
			}
			if (bottomStyleValue == Value.DOTTED) {
				double z = Math.min(bottomWidthValue, leftWidthValue);
				maxY = shape.getMaxY() - z;
			} else {
				maxY = shape.getMaxY() - bottomWidthValue;
			}
			renderVerticalDots(g, minY, maxY, shape.getMinX(), leftWidthValue);
		}

		if (rightStyleValue == Value.DOTTED && config.rightColor != null) {
			// paint right column of dots:
			g.setColor(config.rightColor);
			double minY, maxY;
			if (topStyleValue == Value.DOTTED) {
				double z = Math.min(topWidthValue, rightWidthValue);
				minY = shape.getMinY() + z;
			} else {
				minY = shape.getMinY() + topWidthValue;
			}
			if (bottomStyleValue == Value.DOTTED) {
				double z = Math.min(bottomWidthValue, rightWidthValue);
				maxY = shape.getMaxY() - z;
			} else {
				maxY = shape.getMaxY() - bottomWidthValue;
			}
			renderVerticalDots(g, minY, maxY, shape.getMaxX() - rightWidthValue,
					rightWidthValue);
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
	 * This method only renders DASHED borders around Rectangle2D borders.
	 */
	private List<Operation> renderRectangleDashedBorders() {
		VectorImage i = new VectorImage();
		Graphics2D g = i.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (topStyleValue == Value.DASHED && config.topColor != null) {
			g.setColor(config.topColor);
			if (leftStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(shape.getMinX(), shape.getMinY(),
						leftWidthValue, topWidthValue));
			}
			if (rightStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(shape.getMaxX() - rightWidthValue,
						shape.getMinY(), rightWidthValue, topWidthValue));
			}
			double minX = shape.getMinX() + leftWidthValue;
			double maxX = shape.getMaxX() - rightWidthValue;

			renderHorizontalDashes(g, minX, maxX, shape.getMinY(),
					topWidthValue);
		}

		if (bottomStyleValue == Value.DASHED && config.bottomColor != null) {
			g.setColor(config.bottomColor);
			if (leftStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(shape.getMinX(),
						shape.getMaxY() - bottomWidthValue, leftWidthValue,
						bottomWidthValue));
			}
			if (rightStyleValue == Value.DASHED) {
				g.fill(new Rectangle2D.Double(shape.getMaxX() - rightWidthValue,
						shape.getMaxY() - bottomWidthValue, rightWidthValue,
						bottomWidthValue));
			}
			double minX = shape.getMinX() + leftWidthValue;
			double maxX = shape.getMaxX() - rightWidthValue;
			renderHorizontalDashes(g, minX, maxX,
					shape.getMaxY() - bottomWidthValue, bottomWidthValue);
		}

		if (leftStyleValue == Value.DASHED && config.leftColor != null) {
			g.setColor(config.leftColor);
			double minY = shape.getMinY() + topWidthValue;
			double maxY = shape.getMaxY() - bottomWidthValue;
			renderVerticalDashes(g, minY, maxY, shape.getMinX(),
					leftWidthValue);
		}

		if (rightStyleValue == Value.DASHED && config.rightColor != null) {
			g.setColor(config.rightColor);
			double minY = shape.getMinY() + topWidthValue;
			double maxY = shape.getMaxY() - bottomWidthValue;
			renderVerticalDashes(g, minY, maxY,
					shape.getMaxX() - rightWidthValue, rightWidthValue);
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