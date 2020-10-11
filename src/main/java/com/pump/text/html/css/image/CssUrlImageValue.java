package com.pump.text.html.css.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Objects;

import javax.swing.text.Document;
import javax.swing.text.View;

import com.pump.image.ImageLoader;
import com.pump.util.Cache;

public class CssUrlImageValue implements CssImageValue {
	private static final String PROPERTY_IMAGE_CACHE = CssUrlImageValue.class
			.getName() + "#imageCache";

	/**
	 * This wrapper contains a BufferedImage, or null if an error occurred
	 * trying to load that image.
	 *
	 */
	private static class LoadedImage {
		/**
		 * This may be null if an error occurred trying to load this URL.
		 */
		private BufferedImage bi;

		private LoadedImage(BufferedImage bi) {
			this.bi = bi;
		}
	}

	private final String cssStr, urlStr;

	public CssUrlImageValue(String cssStr, String urlStr) {
		Objects.requireNonNull(cssStr);
		this.cssStr = cssStr;
		this.urlStr = urlStr;

		// TODO: also support data URI's
		// https://css-tricks.com/data-uris/
	}

	@Override
	public void paintRectangle(Graphics2D g, View view, int x, int y, int width,
			int height) {
		Cache<String, LoadedImage> imageCache = getCache(view.getDocument());
		LoadedImage img = imageCache.get(cssStr);
		if (img == null) {
			img = new LoadedImage(createBufferedImage());
			imageCache.put(cssStr, img);
		}
		if (img.bi != null) {
			g.drawImage(img.bi, x, y, width, height, null);
		} else {
			// this probably means an error occurred loading the image
		}
	}

	protected BufferedImage createBufferedImage() {
		BufferedImage bi = null;
		try {
			URL url = new URL(urlStr);
			bi = ImageLoader.createImage(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bi;
	}

	@SuppressWarnings("unchecked")
	protected Cache<String, LoadedImage> getCache(Document doc) {
		Cache<String, LoadedImage> cache = (Cache<String, LoadedImage>) doc
				.getProperty(PROPERTY_IMAGE_CACHE);
		if (cache == null) {
			cache = new Cache<>(100, 10000, 500);
			doc.putProperty(PROPERTY_IMAGE_CACHE, cache);
		}
		return cache;
	}

	@Override
	public String toString() {
		return "CssUrlImageValue[ " + toCSSString() + "]";
	}

	@Override
	public String toCSSString() {
		return cssStr;
	}

}