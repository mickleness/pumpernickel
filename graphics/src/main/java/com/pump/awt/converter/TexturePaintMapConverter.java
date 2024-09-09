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

import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for TexturePaints.
 */
public class TexturePaintMapConverter
		implements BeanMapConverter<TexturePaint> {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * This property defines {@link TexturePaint#getImage()}. See
	 * RenderedImageMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_IMAGE = new Key<>(Map.class, "image");

	/**
	 * This property defines {@link TexturePaint#getAnchorRect()}. See
	 * Rectangle2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_RECTANGLE = new Key<>(Map.class,
			"anchor-rect");

	@Override
	public Class<TexturePaint> getType() {
		return TexturePaint.class;
	}

	@Override
	public Map<String, Object> createAtoms(TexturePaint tp) {
		Map<String, Object> atoms = new HashMap<>(2);

		Rectangle2D anchorRect = tp.getAnchorRect();
		BufferedImage img = tp.getImage();

		PROPERTY_IMAGE.put(atoms,
				new RenderedImageMapConverter().createAtoms(img));
		PROPERTY_RECTANGLE.put(atoms,
				new Rectangle2DMapConverter().createAtoms(anchorRect));

		return atoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TexturePaint createFromAtoms(Map<String, Object> atoms) {
		Map<String, Object> imgAtoms = PROPERTY_IMAGE.get(atoms);
		Map<String, Object> rectAtoms = PROPERTY_RECTANGLE.get(atoms);

		BufferedImage img = new RenderedImageMapConverter()
				.createFromAtoms(imgAtoms);
		Rectangle2D rect = new Rectangle2DMapConverter()
				.createFromAtoms(rectAtoms);
		return new TexturePaint(img, rect);
	}
}