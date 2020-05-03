package com.pump.awt.serialization;

import java.awt.BasicStroke;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class BasicStrokeSerializationWrapper
		extends AbstractSerializationWrapper<BasicStroke> {
	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof BasicStroke) {
				BasicStroke bs = (BasicStroke) object;
				return new BasicStrokeSerializationWrapper(bs);
			}
			return null;
		}
	};

	protected static final String KEY_LINE_WIDTH = "lineWidth";
	protected static final String KEY_END_CAP = "endCap";
	protected static final String KEY_LINE_JOIN = "lineJoin";
	protected static final String KEY_MITER_LIMIT = "miterLimit";
	protected static final String KEY_DASH_ARRAY = "dashArray";
	protected static final String KEY_DASH_PHASE = "dashPhase";

	public BasicStrokeSerializationWrapper(BasicStroke bs) {
		map.put(KEY_LINE_WIDTH, bs.getLineWidth());
		map.put(KEY_END_CAP, bs.getEndCap());
		map.put(KEY_LINE_JOIN, bs.getLineJoin());
		map.put(KEY_MITER_LIMIT, bs.getMiterLimit());
		map.put(KEY_DASH_ARRAY, bs.getDashArray());
		map.put(KEY_DASH_PHASE, bs.getDashPhase());
	}

	@Override
	public BasicStroke create() {
		float lineWidth = ((Number) map.get(KEY_LINE_WIDTH)).floatValue();
		int endCap = ((Number) map.get(KEY_END_CAP)).intValue();
		int lineJoin = ((Number) map.get(KEY_LINE_JOIN)).intValue();
		float miterLimit = ((Number) map.get(KEY_MITER_LIMIT)).floatValue();
		float[] dashArray = (float[]) map.get(KEY_DASH_ARRAY);
		float dashPhase = ((Number) map.get(KEY_DASH_PHASE)).floatValue();

		return new BasicStroke(lineWidth, endCap, lineJoin, miterLimit,
				dashArray, dashPhase);
	}

}
