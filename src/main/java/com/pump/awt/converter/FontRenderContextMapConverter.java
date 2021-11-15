/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.awt.converter;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for FontRenderContexts.
 */
public class FontRenderContextMapConverter
		implements BeanMapConverter<FontRenderContext> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines {@link FontRenderContext#getAntiAliasingHint()}.
	 */
	public static final Key<String> PROPERTY_ANTIALIASING_HINT = new Key<>(
			String.class, "antialiasing-hint");

	/**
	 * This property defines
	 * {@link FontRenderContext#getFractionalMetricsHint()}.
	 */
	public static final Key<String> PROPERTY_FRACTIONAL_METRICS_HINT = new Key<>(
			String.class, "fractional-metrics-hint");

	/**
	 * This property defines {@link FontRenderContext#getTransform()}.
	 */
	public static final Key<AffineTransform> PROPERTY_TRANSFORM = new Key<>(
			AffineTransform.class, "transform");

	@Override
	public Class<FontRenderContext> getType() {
		return FontRenderContext.class;
	}

	@Override
	public Map<String, Object> createAtoms(FontRenderContext frc) {
		Map<String, Object> atoms = new HashMap<>(3);
		PROPERTY_ANTIALIASING_HINT.put(atoms,
				frc.getAntiAliasingHint().toString());
		PROPERTY_FRACTIONAL_METRICS_HINT.put(atoms,
				frc.getFractionalMetricsHint().toString());
		PROPERTY_TRANSFORM.put(atoms, frc.getTransform());
		return atoms;
	}

	@Override
	public FontRenderContext createFromAtoms(Map<String, Object> atoms) {
		Object antiAliasedHint = RenderingHintsMapConverter
				.getValue(PROPERTY_ANTIALIASING_HINT.get(atoms));
		Object fractionalMetricsHint = RenderingHintsMapConverter
				.getValue(PROPERTY_FRACTIONAL_METRICS_HINT.get(atoms));
		AffineTransform tx = PROPERTY_TRANSFORM.get(atoms);
		return new FontRenderContext(tx, antiAliasedHint,
				fractionalMetricsHint);
	}
}