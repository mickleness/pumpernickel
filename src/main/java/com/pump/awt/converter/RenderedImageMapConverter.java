package com.pump.awt.converter;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;
import com.pump.data.converter.ConverterUtils.ByteArray;

/**
 * This is a BeanMapConverter for RenderedImages.
 * <p>
 * This simply encodes images as a PNG file. If an image is dynamic/animated,
 * then this will result in an arbitrary snapshot being serialized/deserialized.
 * <p>
 * All deserialized images are BufferedImages.
 */
public class RenderedImageMapConverter
		implements BeanMapConverter<RenderedImage> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines a PNG image.
	 */
	public static final Key<ByteArray> PROPERTY_PNG = new Key<>(ByteArray.class,
			"png-data");

	@Override
	public Class<RenderedImage> getType() {
		return RenderedImage.class;
	}

	@Override
	public Map<String, Object> createAtoms(RenderedImage img) {
		Map<String, Object> atoms = new HashMap<>(1);

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "png", byteOut);
		} catch (IOException e) {
			// we shouldn't have a IOException, because we're writing to a
			// byte array
			throw new RuntimeException(e);
		}
		PROPERTY_PNG.put(atoms, ByteArray.get(byteOut.toByteArray()));

		return atoms;
	}

	@Override
	public BufferedImage createFromAtoms(Map<String, Object> atoms) {
		ByteArray byteArray = PROPERTY_PNG.get(atoms);
		byte[] pngData = byteArray.data;
		try (ByteArrayInputStream in = new ByteArrayInputStream(pngData)) {
			return ImageIO.read(in);
		} catch (IOException e) {
			// we shouldn't have a IOException, because we're reading from a
			// byte array
			throw new RuntimeException(e);
		}
	}
}
