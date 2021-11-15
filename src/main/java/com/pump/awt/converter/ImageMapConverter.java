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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.converter.BeanMapConverter;
import com.pump.image.ImageLoader;

/**
 * This is a BeanMapConverter for Images.
 * <p>
 * This simply encodes images as a PNG file. If an image is dynamic/animated,
 * then this will result in an arbitrary snapshot being serialized/deserialized.
 * <p>
 * All deserialized images are BufferedImages.
 */
public class ImageMapConverter implements BeanMapConverter<Image> {

	private static final long serialVersionUID = 1L;

	@Override
	public Class<Image> getType() {
		return Image.class;
	}

	@Override
	public Map<String, Object> createAtoms(Image img) {
		RenderedImage rendered = ImageLoader.createImage(img);
		Map<String, Object> atoms = new HashMap<>(1);
		atoms.putAll(new RenderedImageMapConverter().createAtoms(rendered));
		return atoms;
	}

	@Override
	public BufferedImage createFromAtoms(Map<String, Object> atoms) {
		return new RenderedImageMapConverter().createFromAtoms(atoms);
	}
}