package com.pump.awt.serialization;

import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class LinearGradientPaintSerializationWrapper
		extends MultipleGradientPaintSerializationWrapper<LinearGradientPaint> {
	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof LinearGradientPaint) {
				LinearGradientPaint lgp = (LinearGradientPaint) object;
				return new LinearGradientPaintSerializationWrapper(lgp);
			}
			return null;
		}
	};

	protected static final String KEY_START = "start";
	protected static final String KEY_END = "end";

	public LinearGradientPaintSerializationWrapper(LinearGradientPaint lgp) {
		super(lgp);
		map.put(KEY_START,
				new Point2DSerializationWrapper(lgp.getStartPoint()));
		map.put(KEY_END, new Point2DSerializationWrapper(lgp.getEndPoint()));
	}

	@Override
	public LinearGradientPaint create() {
		Point2D start = ((Point2DSerializationWrapper) map.get(KEY_START))
				.create();
		Point2D end = ((Point2DSerializationWrapper) map.get(KEY_END)).create();
		return new LinearGradientPaint(start, end, getFractions(), getColors(),
				getCycleMethod(), getColorSpace(), getTransform());
	}
}