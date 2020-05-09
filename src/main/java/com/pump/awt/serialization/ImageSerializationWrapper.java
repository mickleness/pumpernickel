package com.pump.awt.serialization;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pump.image.ImageLoader;
import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for Images and RenderedImages.
 * <p>
 * This simply encodes images as a PNG file. If an image is dynamic/animated,
 * then this will result in an arbitrary snapshot being serialized/deserialized.
 * <p>
 * All deserialized images are BufferedImages.
 */
public class ImageSerializationWrapper
		extends AbstractSerializationWrapper<BufferedImage> {
	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts an Image or a RenderedImage into a
	 * ImageSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof RenderedImage) {
				RenderedImage img = (RenderedImage) object;
				return new ImageSerializationWrapper(img);
			}
			if (object instanceof Image) {
				Image img = (Image) object;
				return new ImageSerializationWrapper(img);
			}
			return null;
		}
	};

	protected final static String KEY_PNG_DATA = "pngData";

	public ImageSerializationWrapper(RenderedImage img) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "png", byteOut);
		} catch (IOException e) {
			// we shouldn't have a IOException, because we're writing to a
			// byte array
			throw new RuntimeException(e);
		}
		map.put(KEY_PNG_DATA, byteOut.toByteArray());
	}

	public ImageSerializationWrapper(Image img) {
		this((RenderedImage) ImageLoader.createImage(img));
	}

	@Override
	public BufferedImage create() {
		byte[] pngData = (byte[]) map.get(KEY_PNG_DATA);
		try (ByteArrayInputStream in = new ByteArrayInputStream(pngData)) {
			return ImageIO.read(in);
		} catch (IOException e) {
			// we shouldn't have a IOException, because we're reading from a
			// byte array
			throw new RuntimeException(e);
		}
	}
}
