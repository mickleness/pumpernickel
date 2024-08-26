/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.awt.converter;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for Fonts.
 * <p>
 * Originally I tried just serializing the Font object directly, but I ran into
 * some weird unit test failures with GlyphVector equality. There are some
 * hidden-ish properties that can be set on a Font object that don't survive
 * being serialized and deserialized. (For ex: I had a GradientPaint assigned as
 * the fill.)
 */
public class FontMapConverter implements BeanMapConverter<Font> {
	private static final long serialVersionUID = 1L;

	/**
	 * This property defines the font family name, see {@link Font#getFamily()}
	 * and {@link TextAttribute#FAMILY}
	 */
	public static final Key<String> PROPERTY_FAMILY = new Key<>(String.class,
			"family");

	/**
	 * This property defines the weight (used for bold). See
	 * {@link TextAttribute#WEIGHT_BOLD}
	 */
	public static final Key<Number> PROPERTY_WEIGHT = new Key<>(Number.class,
			"weight");

	/**
	 * This property defines the posture (used for italic). See
	 * {@link TextAttribute#POSTURE}.
	 */
	public static final Key<Number> PROPERTY_POSTURE = new Key<>(Number.class,
			"posture");

	/**
	 * This property defines the font size, see {@link Font#getSize2D()} and
	 * {@link TextAttribute#SIZE}
	 */
	public static final Key<Number> PROPERTY_SIZE = new Key<>(Number.class,
			"size");

	/**
	 * This property defines the font transform, see
	 * {@link Font#getTransform()}.
	 */
	public static final Key<AffineTransform> PROPERTY_TRANSFORM = new Key<>(
			AffineTransform.class, "transform");

	@Override
	public Class<Font> getType() {
		return Font.class;
	}

	@Override
	public Map<String, Object> createAtoms(Font font) {
		Map<TextAttribute, ?> attributes = font.getAttributes();

		Map<String, Object> atoms = new HashMap<>(5);
		PROPERTY_FAMILY.put(atoms,
				(String) attributes.get(TextAttribute.FAMILY));
		PROPERTY_WEIGHT.put(atoms,
				(Number) attributes.get(TextAttribute.WEIGHT));
		PROPERTY_POSTURE.put(atoms,
				(Number) attributes.get(TextAttribute.POSTURE));
		PROPERTY_SIZE.put(atoms, (Number) attributes.get(TextAttribute.SIZE));
		PROPERTY_TRANSFORM.put(atoms,
				(AffineTransform) attributes.get(TextAttribute.TRANSFORM));

		// these are attributes we don't support yet (from
		// Font#getAvailableAttributes). We may add support for them as needed:

		// TextAttribute.WIDTH,
		// TextAttribute.SUPERSCRIPT,
		// TextAttribute.CHAR_REPLACEMENT,
		// TextAttribute.FOREGROUND,
		// TextAttribute.BACKGROUND,
		// TextAttribute.UNDERLINE,
		// TextAttribute.STRIKETHROUGH,
		// TextAttribute.RUN_DIRECTION,
		// TextAttribute.BIDI_EMBEDDING,
		// TextAttribute.JUSTIFICATION,
		// TextAttribute.INPUT_METHOD_HIGHLIGHT,
		// TextAttribute.INPUT_METHOD_UNDERLINE,
		// TextAttribute.SWAP_COLORS,
		// TextAttribute.NUMERIC_SHAPING,
		// TextAttribute.KERNING,
		// TextAttribute.LIGATURES,
		// TextAttribute.TRACKING,

		return atoms;
	}

	@Override
	public Font createFromAtoms(Map<String, Object> atoms) {
		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.FAMILY, PROPERTY_FAMILY.get(atoms));
		attributes.put(TextAttribute.POSTURE, PROPERTY_POSTURE.get(atoms));
		attributes.put(TextAttribute.SIZE, PROPERTY_SIZE.get(atoms));
		attributes.put(TextAttribute.WEIGHT, PROPERTY_WEIGHT.get(atoms));
		attributes.put(TextAttribute.TRANSFORM, PROPERTY_TRANSFORM.get(atoms));
		return new Font(attributes);
	}
}