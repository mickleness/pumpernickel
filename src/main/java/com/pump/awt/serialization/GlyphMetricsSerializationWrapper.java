package com.pump.awt.serialization;

import java.awt.font.GlyphMetrics;
import java.awt.geom.Rectangle2D;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for GlyphMetrics.
 */
public class GlyphMetricsSerializationWrapper
		extends AbstractSerializationWrapper<GlyphMetrics> {

	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts a GlyphMetrics into a
	 * GlyphMetricsSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof GlyphMetrics) {
				GlyphMetrics gm = (GlyphMetrics) object;
				return new GlyphMetricsSerializationWrapper(gm);
			}
			return null;
		}
	};

	protected final static String KEY_HORIZONTAL = "horizontal";
	protected final static String KEY_ADVANCE_X = "advanceX";
	protected final static String KEY_ADVANCE_Y = "advanceY";
	protected final static String KEY_BOUNDS = "bounds";
	protected final static String KEY_GLYPH_TYPE = "glyphType";

	public GlyphMetricsSerializationWrapper(GlyphMetrics gm) {
		// when in doubt, we're just going to guess that this is true. I don't
		// see a clear way to identify this property without reflection?
		boolean horizontal = gm.getAdvanceX() == gm.getAdvanceY() ? true
				: gm.getAdvanceX() == gm.getAdvance();
		map.put(KEY_HORIZONTAL, horizontal);
		map.put(KEY_ADVANCE_X, gm.getAdvanceX());
		map.put(KEY_ADVANCE_Y, gm.getAdvanceY());
		map.put(KEY_BOUNDS,
				new Rectangle2DSerializationWrapper(gm.getBounds2D()));
		map.put(KEY_GLYPH_TYPE, (byte) gm.getType());
	}

	@Override
	public GlyphMetrics create() {
		boolean horizontal = (Boolean) map.get(KEY_HORIZONTAL);
		float advanceX = ((Number) map.get(KEY_ADVANCE_X)).floatValue();
		float advanceY = ((Number) map.get(KEY_ADVANCE_Y)).floatValue();
		Rectangle2DSerializationWrapper boundsWrapper = (Rectangle2DSerializationWrapper) map
				.get(KEY_BOUNDS);
		Rectangle2D boundsRect = boundsWrapper.create();
		byte glyphType = ((Number) map.get(KEY_GLYPH_TYPE)).byteValue();

		return new GlyphMetrics(horizontal, advanceX, advanceY, boundsRect,
				glyphType);
	}

}
