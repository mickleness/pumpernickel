package com.pump.awt.serialization;

import java.awt.Point;
import java.awt.geom.Point2D;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class Point2DSerializationWrapper
		extends AbstractSerializationWrapper<Point2D> {
	private static final long serialVersionUID = 1L;

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

	protected static final String KEY_X = "x";
	protected static final String KEY_Y = "y";
	protected static final String KEY_TYPE = "type";

	public Point2DSerializationWrapper(Point2D p) {
		map.put(KEY_X, p.getX());
		map.put(KEY_Y, p.getY());
		if (p instanceof Point) {
			map.put(KEY_TYPE, 0);
		} else if (p instanceof Point2D.Float) {
			map.put(KEY_TYPE, 1);
		} else {
			map.put(KEY_TYPE, 2);
		}
	}

	@Override
	public Point2D create() {
		double x = ((Number) map.get(KEY_X)).doubleValue();
		double y = ((Number) map.get(KEY_Y)).doubleValue();
		int type = ((Number) map.get(KEY_TYPE)).intValue();

		if (type == 0) {
			return new Point((int) (x + .5), (int) (y + .5));
		} else if (type == 1) {
			return new Point2D.Float((float) x, (float) y);
		}
		return new Point2D.Double(x, y);
	}
}