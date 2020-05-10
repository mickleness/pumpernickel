package com.pump.awt.serialization;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for Rectangle2Ds.
 */
public class Rectangle2DSerializationWrapper
		extends AbstractSerializationWrapper<Rectangle2D> {

	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts a Rectangle2D into a
	 * Rectangle2DSerializationWrapper.
	 */
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

	protected static final String KEY_SERIALIZABLE = "serializable";
	protected static final String KEY_X = "x";
	protected static final String KEY_Y = "y";
	protected static final String KEY_WIDTH = "width";
	protected static final String KEY_HEIGHT = "height";

	public Rectangle2DSerializationWrapper(Rectangle2D r) {
		if (r instanceof Serializable) {
			map.put(KEY_SERIALIZABLE, r.clone());
		} else {
			map.put(KEY_X, r.getX());
			map.put(KEY_Y, r.getY());
			map.put(KEY_WIDTH, r.getWidth());
			map.put(KEY_HEIGHT, r.getHeight());
		}
	}

	@Override
	public Rectangle2D create() {
		Rectangle2D r = (Rectangle2D) map.get(KEY_SERIALIZABLE);
		if (r != null)
			return r;

		double x = ((Number) map.get(KEY_X)).doubleValue();
		double y = ((Number) map.get(KEY_Y)).doubleValue();
		double width = ((Number) map.get(KEY_WIDTH)).doubleValue();
		double height = ((Number) map.get(KEY_HEIGHT)).doubleValue();
		return new Rectangle2D.Double(x, y, width, height);
	}
}
