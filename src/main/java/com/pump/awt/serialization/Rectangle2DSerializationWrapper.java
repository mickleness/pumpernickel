package com.pump.awt.serialization;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class Rectangle2DSerializationWrapper
		extends AbstractSerializationWrapper<Rectangle2D> {

	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof Rectangle2D) {
				Rectangle2D p = (Rectangle2D) object;
				return new Rectangle2DSerializationWrapper(p);
			}
			return null;
		}
	};

	protected static final String KEY_X = "x";
	protected static final String KEY_Y = "y";
	protected static final String KEY_WIDTH = "width";
	protected static final String KEY_HEIGHT = "height";
	protected static final String KEY_TYPE = "type";

	public Rectangle2DSerializationWrapper(Rectangle2D r) {
		map.put(KEY_X, r.getX());
		map.put(KEY_Y, r.getY());
		map.put(KEY_WIDTH, r.getWidth());
		map.put(KEY_HEIGHT, r.getHeight());

		if (r instanceof Rectangle) {
			map.put(KEY_TYPE, 0);
		} else if (r instanceof Rectangle2D.Float) {
			map.put(KEY_TYPE, 1);
		} else {
			map.put(KEY_TYPE, 0);
		}
	}

	@Override
	public Rectangle2D create() {
		double x = ((Number) map.get(KEY_X)).doubleValue();
		double y = ((Number) map.get(KEY_Y)).doubleValue();
		double width = ((Number) map.get(KEY_WIDTH)).doubleValue();
		double height = ((Number) map.get(KEY_HEIGHT)).doubleValue();
		int type = ((Number) map.get(KEY_TYPE)).intValue();
		if (type == 0) {
			return new Rectangle((int) (x + .5), (int) (y + .5),
					(int) (width + .5), (int) (height + .5));
		} else if (type == 1) {
			return new Rectangle2D.Float((float) x, (float) y, (float) width,
					(float) height);
		}
		return new Rectangle2D.Double(x, y, width, height);
	}
}
