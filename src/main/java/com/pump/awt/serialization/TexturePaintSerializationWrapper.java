package com.pump.awt.serialization;

import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for TexturePaints.
 */
public class TexturePaintSerializationWrapper
		extends AbstractSerializationWrapper<TexturePaint> {

	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts a TexturePaint into a
	 * TexturePaintSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof TexturePaint) {
				TexturePaint p = (TexturePaint) object;
				return new TexturePaintSerializationWrapper(p);
			}
			return null;
		}
	};

	protected static final String KEY_IMAGE = "image";
	protected static final String KEY_ANCHOR_RECT = "anchorRect";

	public TexturePaintSerializationWrapper(TexturePaint tp) {
		ImageSerializationWrapper image = new ImageSerializationWrapper(
				(RenderedImage) tp.getImage());
		map.put(KEY_IMAGE, image);

		Rectangle2DSerializationWrapper anchor = new Rectangle2DSerializationWrapper(
				tp.getAnchorRect());
		map.put(KEY_ANCHOR_RECT, anchor);
	}

	public BufferedImage getImage() {
		ImageSerializationWrapper image = (ImageSerializationWrapper) map
				.get(KEY_IMAGE);
		return image.create();
	}

	public Rectangle2D getAnchorRectangle() {
		Rectangle2DSerializationWrapper anchor = (Rectangle2DSerializationWrapper) map
				.get(KEY_ANCHOR_RECT);
		return anchor.create();
	}

	@Override
	public TexturePaint create() {
		return new TexturePaint(getImage(), getAnchorRectangle());
	}
}
