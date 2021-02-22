package com.pump.awt.converter;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for Rectangle2Ds.
 */
public class Rectangle2DMapConverter implements BeanMapConverter<Rectangle2D> {

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
	 * This property defines the top-left x-coordinate. It should be an Integer,
	 * Float or Double depending on PROPERTY_TYPE.
	 */
	public static final Key<Number> PROPERTY_X = new Key<>(Number.class, "x");

	/**
	 * This property defines the top-left y-coordinate. It should be an Integer,
	 * Float or Double depending on PROPERTY_TYPE.
	 */
	public static final Key<Number> PROPERTY_Y = new Key<>(Number.class, "y");

	/**
	 * This property defines the width. It should be an Integer, Float or Double
	 * depending on PROPERTY_TYPE.
	 */
	public static final Key<Number> PROPERTY_WIDTH = new Key<>(Number.class,
			"width");

	/**
	 * This property defines the height. It should be an Integer, Float or
	 * Double depending on PROPERTY_TYPE.
	 */
	public static final Key<Number> PROPERTY_HEIGHT = new Key<>(Number.class,
			"height");

	@Override
	public Class<Rectangle2D> getType() {
		return Rectangle2D.class;
	}

	@Override
	public Map<String, Object> createAtoms(Rectangle2D rect) {
		Map<String, Object> atoms = new HashMap<>(5);
		if (rect instanceof Rectangle) {
			Rectangle r = (Rectangle) rect;
			PROPERTY_TYPE.put(atoms, Integer.TYPE);
			PROPERTY_X.put(atoms, r.x);
			PROPERTY_Y.put(atoms, r.y);
			PROPERTY_WIDTH.put(atoms, r.width);
			PROPERTY_HEIGHT.put(atoms, r.height);
		} else if (rect instanceof Rectangle2D.Float) {
			Rectangle2D.Float r = (Rectangle2D.Float) rect;
			PROPERTY_TYPE.put(atoms, Float.TYPE);
			PROPERTY_X.put(atoms, r.x);
			PROPERTY_Y.put(atoms, r.y);
			PROPERTY_WIDTH.put(atoms, r.width);
			PROPERTY_HEIGHT.put(atoms, r.height);
		} else {
			PROPERTY_TYPE.put(atoms, Double.TYPE);
			PROPERTY_X.put(atoms, rect.getX());
			PROPERTY_Y.put(atoms, rect.getY());
			PROPERTY_WIDTH.put(atoms, rect.getWidth());
			PROPERTY_HEIGHT.put(atoms, rect.getHeight());
		}
		return atoms;
	}

	@Override
	public Rectangle2D createFromAtoms(Map<String, Object> atoms) {
		if (Integer.TYPE == PROPERTY_TYPE.get(atoms)) {
			int x = PROPERTY_X.get(atoms).intValue();
			int y = PROPERTY_Y.get(atoms).intValue();
			int w = PROPERTY_WIDTH.get(atoms).intValue();
			int h = PROPERTY_HEIGHT.get(atoms).intValue();
			return new Rectangle(x, y, w, h);
		} else if (Float.TYPE == PROPERTY_TYPE.get(atoms)) {
			float x = PROPERTY_X.get(atoms).floatValue();
			float y = PROPERTY_Y.get(atoms).floatValue();
			float w = PROPERTY_WIDTH.get(atoms).floatValue();
			float h = PROPERTY_HEIGHT.get(atoms).floatValue();
			return new Rectangle2D.Float(x, y, w, h);
		}
		double x = PROPERTY_X.get(atoms).doubleValue();
		double y = PROPERTY_Y.get(atoms).doubleValue();
		double w = PROPERTY_WIDTH.get(atoms).doubleValue();
		double h = PROPERTY_HEIGHT.get(atoms).doubleValue();
		return new Rectangle2D.Double(x, y, w, h);

	}
}
