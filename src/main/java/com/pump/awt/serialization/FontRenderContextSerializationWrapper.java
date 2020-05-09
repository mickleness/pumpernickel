package com.pump.awt.serialization;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for FontRenderContexts.
 */
public class FontRenderContextSerializationWrapper
		extends AbstractSerializationWrapper<FontRenderContext> {
	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts a FontRenderContext into a
	 * FontRenderContextSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof FontRenderContext) {
				FontRenderContext frc = (FontRenderContext) object;
				return new FontRenderContextSerializationWrapper(frc);
			}
			return null;
		}
	};

	protected static final String KEY_TRANSFORM = "transform";
	protected static final String KEY_ANTIALIAS_HINT = "antialiasHint";
	protected static final String KEY_FRACTIONAL_METRICS_HINT = "fractionalMetricsHints";

	public FontRenderContextSerializationWrapper(FontRenderContext frc) {
		map.put(KEY_ANTIALIAS_HINT, frc.getAntiAliasingHint().toString());
		map.put(KEY_FRACTIONAL_METRICS_HINT,
				frc.getFractionalMetricsHint().toString());
		map.put(KEY_TRANSFORM, frc.getTransform());
	}

	@Override
	public FontRenderContext create() {
		Object fractionalMetricsHint = RenderingHintsSerializationWrapper
				.getValue((String) map.get(KEY_FRACTIONAL_METRICS_HINT));
		Object antiAliasedHint = RenderingHintsSerializationWrapper
				.getValue((String) map.get(KEY_ANTIALIAS_HINT));
		AffineTransform tx = (AffineTransform) map.get(KEY_TRANSFORM);
		return new FontRenderContext(tx, antiAliasedHint,
				fractionalMetricsHint);
	}

}
