package com.pump.awt.serialization;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class GradientPaintSerializationWrapper
		extends AbstractSerializationWrapper<GradientPaint> {
	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof GradientPaint) {
				GradientPaint gp = (GradientPaint) object;
				return new GradientPaintSerializationWrapper(gp);
			}
			return null;
		}
	};

	protected static final String KEY_X1 = "x1";
	protected static final String KEY_Y1 = "y1";
	protected static final String KEY_X2 = "x2";
	protected static final String KEY_Y2 = "y2";
	protected static final String KEY_COLOR1 = "color1";
	protected static final String KEY_COLOR2 = "color2";
	protected static final String KEY_CYCLIC = "cyclic";

	public GradientPaintSerializationWrapper(GradientPaint gp) {
		Point2D p1 = gp.getPoint1();
		Point2D p2 = gp.getPoint2();

		map.put(KEY_X1, p1.getX());
		map.put(KEY_Y1, p1.getY());
		map.put(KEY_X2, p2.getX());
		map.put(KEY_Y2, p2.getY());

		map.put(KEY_COLOR1, gp.getColor1());
		map.put(KEY_COLOR2, gp.getColor2());
		map.put(KEY_CYCLIC, gp.isCyclic());
	}

	@Override
	public GradientPaint create() {
		float x1 = ((Number) map.get(KEY_X1)).floatValue();
		float y1 = ((Number) map.get(KEY_Y1)).floatValue();
		float x2 = ((Number) map.get(KEY_X2)).floatValue();
		float y2 = ((Number) map.get(KEY_Y2)).floatValue();
		Color color1 = (Color) map.get(KEY_COLOR1);
		Color color2 = (Color) map.get(KEY_COLOR2);
		boolean cyclic = (Boolean) map.get(KEY_CYCLIC);
		return new GradientPaint(x1, y1, color1, x2, y2, color2, cyclic);
	}

}
