/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.awt.converter;

import java.awt.font.GlyphMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for GlyphMetrics.
 */
public class GlyphMetricsMapConverter
		implements BeanMapConverter<GlyphMetrics> {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * This property defines first constructor of GlyphMetrics.
	 */
	public static final Key<Boolean> PROPERTY_HORIZONTAL = new Key<>(
			Boolean.class, "is-horizontal");

	/**
	 * This property defines {@link GlyphMetrics#getAdvanceX()}.
	 */
	public static final Key<Float> PROPERTY_ADVANCE_X = new Key<>(Float.class,
			"advance-x");

	/**
	 * This property defines {@link GlyphMetrics#getAdvanceY()}.
	 */
	public static final Key<Float> PROPERTY_ADVANCE_Y = new Key<>(Float.class,
			"advance-y");

	/**
	 * This property defines {@link GlyphMetrics#getBounds2D()}. See
	 * Rectangle2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_BOUNDS = new Key<>(Map.class,
			"bounds");

	/**
	 * This property defines {@link GlyphMetrics#getType()}
	 */
	public static final Key<Byte> PROPERTY_GLYPH_TYPE = new Key<>(Byte.class,
			"glyph-type");

	@Override
	public Class<GlyphMetrics> getType() {
		return GlyphMetrics.class;
	}

	@Override
	public Map<String, Object> createAtoms(GlyphMetrics gm) {
		Map<String, Object> atoms = new HashMap<>(5);

		boolean horizontal = gm.getAdvanceX() == gm.getAdvanceY() || gm.getAdvanceX() == gm.getAdvance();
		PROPERTY_HORIZONTAL.put(atoms, horizontal);
		PROPERTY_ADVANCE_X.put(atoms, gm.getAdvanceX());
		PROPERTY_ADVANCE_Y.put(atoms, gm.getAdvanceY());
		PROPERTY_BOUNDS.put(atoms,
				new Rectangle2DMapConverter().createAtoms(gm.getBounds2D()));
		PROPERTY_GLYPH_TYPE.put(atoms, (byte) gm.getType());

		return atoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GlyphMetrics createFromAtoms(Map<String, Object> atoms) {
		boolean horizontal = PROPERTY_HORIZONTAL.get(atoms);
		float advanceX = PROPERTY_ADVANCE_X.get(atoms);
		float advanceY = PROPERTY_ADVANCE_Y.get(atoms);
		Rectangle2D boundsRect = new Rectangle2DMapConverter()
				.createFromAtoms(PROPERTY_BOUNDS.get(atoms));
		byte glyphType = PROPERTY_GLYPH_TYPE.get(atoms);

		return new GlyphMetrics(horizontal, advanceX, advanceY, boundsRect,
				glyphType);
	}
}