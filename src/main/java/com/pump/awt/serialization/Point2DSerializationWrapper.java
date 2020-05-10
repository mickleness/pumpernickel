package com.pump.awt.serialization;

import java.awt.geom.Point2D;
import java.io.Serializable;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for Point2Ds.
 */
public class Point2DSerializationWrapper
		extends AbstractSerializationWrapper<Point2D> {
	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts a Point2D into a Point2DSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof Point2D) {
				Point2D p = (Point2D) object;
				return new Point2DSerializationWrapper(p);
			}
			return null;
		}
	};

	protected static final String KEY_SERIALIZABLE = "serializable";
	protected static final String KEY_X = "x";
	protected static final String KEY_Y = "y";

	public Point2DSerializationWrapper(Point2D p) {
		if (p instanceof Serializable) {
			map.put(KEY_SERIALIZABLE, p.clone());
		} else {
			map.put(KEY_X, p.getX());
			map.put(KEY_Y, p.getY());
		}
	}

	@Override
	public Point2D create() {
		Point2D p = (Point2D) map.get(KEY_SERIALIZABLE);
		if (p != null)
			return p;

		double x = ((Number) map.get(KEY_X)).doubleValue();
		double y = ((Number) map.get(KEY_Y)).doubleValue();
		return new Point2D.Double(x, y);
	}
}