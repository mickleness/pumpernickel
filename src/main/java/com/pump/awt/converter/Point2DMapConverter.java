package com.pump.awt.converter;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for Point2Ds.
 */
public class Point2DMapConverter implements BeanMapConverter<Point2D> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property maps to {@link Integer#TYPE} or {@link Float#TYPE} or
	 * {@link Double#TYPE}, depending on other this is a Point, Point2D.Float,
	 * or any other Point2D.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Class> PROPERTY_TYPE = new Key<>(Class.class,
			"type");

	/**
	 * This property defines the x-coordinate. It should be an Integer, Float or
	 * Double depending on PROPERTY_TYPE.
	 */
	public static final Key<Number> PROPERTY_X = new Key<>(Number.class, "x");

	/**
	 * This property defines the y-coordinate. It should be an Integer, Float or
	 * Double depending on PROPERTY_TYPE.
	 */
	public static final Key<Number> PROPERTY_Y = new Key<>(Number.class, "y");

	@Override
	public Class<Point2D> getType() {
		return Point2D.class;
	}

	@Override
	public Map<String, Object> createAtoms(Point2D point) {
		Map<String, Object> atoms = new HashMap<>(3);
		if (point instanceof Point) {
			Point p = (Point) point;
			PROPERTY_TYPE.put(atoms, Integer.TYPE);
			PROPERTY_X.put(atoms, p.x);
			PROPERTY_Y.put(atoms, p.y);
		} else if (point instanceof Point2D.Float) {
			Point2D.Float p = (Point2D.Float) point;
			PROPERTY_TYPE.put(atoms, Float.TYPE);
			PROPERTY_X.put(atoms, p.x);
			PROPERTY_Y.put(atoms, p.y);
		} else {
			PROPERTY_TYPE.put(atoms, Double.TYPE);
			PROPERTY_X.put(atoms, point.getX());
			PROPERTY_Y.put(atoms, point.getY());
		}
		return atoms;
	}

	@Override
	public Point2D createFromAtoms(Map<String, Object> atoms) {
		if (Integer.TYPE == PROPERTY_TYPE.get(atoms)) {
			int x = PROPERTY_X.get(atoms).intValue();
			int y = PROPERTY_Y.get(atoms).intValue();
			return new Point(x, y);
		} else if (Float.TYPE == PROPERTY_TYPE.get(atoms)) {
			float x = PROPERTY_X.get(atoms).floatValue();
			float y = PROPERTY_Y.get(atoms).floatValue();
			return new Point2D.Float(x, y);
		}
		double x = PROPERTY_X.get(atoms).doubleValue();
		double y = PROPERTY_Y.get(atoms).doubleValue();
		return new Point2D.Double(x, y);
	}
}