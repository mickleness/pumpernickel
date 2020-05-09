package com.pump.awt.serialization;

import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for RadialGradientPaints.
 */
public class RadialGradientPaintSerializationWrapper
		extends MultipleGradientPaintSerializationWrapper<RadialGradientPaint> {

	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts a RadialGradientPaint into a
	 * RadialGradientPaintSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof RadialGradientPaint) {
				RadialGradientPaint rgp = (RadialGradientPaint) object;
				return new RadialGradientPaintSerializationWrapper(rgp);
			}
			return null;
		}
	};

	protected final static String KEY_CENTER = "center";
	protected final static String KEY_FOCUS = "focus";
	protected final static String KEY_RADIUS = "radius";

	public RadialGradientPaintSerializationWrapper(RadialGradientPaint rgp) {
		super(rgp);
		map.put(KEY_CENTER,
				new Point2DSerializationWrapper(rgp.getCenterPoint()));
		map.put(KEY_RADIUS, rgp.getRadius());
		map.put(KEY_FOCUS,
				new Point2DSerializationWrapper(rgp.getFocusPoint()));
	}

	@Override
	public RadialGradientPaint create() {
		Point2D center = ((Point2DSerializationWrapper) map.get(KEY_CENTER))
				.create();
		Point2D focus = ((Point2DSerializationWrapper) map.get(KEY_FOCUS))
				.create();
		float radius = ((Number) map.get(KEY_RADIUS)).floatValue();
		return new RadialGradientPaint(center, radius, focus, getFractions(),
				getColors(), getCycleMethod(), getColorSpace(), getTransform());
	}
}
