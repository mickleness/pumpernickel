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
package com.pump.text.html.css.image;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JViewport;
import javax.swing.text.Document;

import com.pump.awt.BufferedImagePaintable;
import com.pump.awt.Paintable;
import com.pump.graphics.vector.VectorImage;
import com.pump.graphics.vector.VectorImagePaintable;
import com.pump.image.ImageLoader;
import com.pump.text.html.css.AbstractCssValue;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.background.CssBackgroundAttachmentValue;
import com.pump.text.html.css.background.CssBackgroundPositionValue;
import com.pump.text.html.css.background.CssBackgroundRepeatValue;
import com.pump.text.html.css.background.CssBackgroundRepeatValue.Span;
import com.pump.text.html.css.background.CssBackgroundSizeValue;
import com.pump.text.html.view.QViewHelper;
import com.pump.util.Cache;

public class CssUrlImageValue extends AbstractCssValue
		implements CssImageValue {
	private static final String PROPERTY_IMAGE_CACHE = CssUrlImageValue.class
			.getName() + "#imageCache";

	/**
	 * This is guaranteed to have a non-null Paintable, but the BufferedImage is
	 * optional. When the BufferedImage exists, the Paintable is just a
	 * BufferedImagePaintable.
	 * <p>
	 * So this accommodates both vector graphics (which can be a Paintable) and
	 * a plain BufferedImage.
	 */
	public static class ImageDataValue {
		public Paintable paintable;
		public BufferedImage bufferedImage;

		public ImageDataValue(BufferedImage img) {
			Objects.requireNonNull(img);
			bufferedImage = img;
			paintable = new BufferedImagePaintable(img);
		}

		public ImageDataValue(Paintable paintable) {
			Objects.requireNonNull(paintable);
			this.paintable = paintable;
		}
	}

	/**
	 * Create a ImageDataValue from a url that begins with something like:
	 * "data:image/png;base64,". This currently throws an exception for any
	 * image that isn't a PNG, JPG of JVG. (We could add support for GIFs, but
	 * since that requires supporting animation it's a whole separate project.)
	 * 
	 * @param urlStr
	 *            a URL of base-64 encoded data that begins with something like
	 *            "data:image/png;base64,"
	 */
	public static ImageDataValue createPaintableFromDataUrl(String urlStr) {
		if (!urlStr.startsWith("data:"))
			throw new IllegalArgumentException();

		int i1 = urlStr.indexOf(";", 5);
		int i2 = urlStr.indexOf(",", i1 + 1);
		String mimeType = urlStr.substring(5, i1);
		String encodingType = urlStr.substring(i1 + 1, i2);
		String data = urlStr.substring(i2 + 1);

		if (!mimeType.startsWith("image/"))
			throw new IllegalArgumentException(
					"unsupported mime type: " + mimeType);
		String fileFormat = mimeType.substring("image/".length());

		byte[] bytes;
		if (encodingType.equals("base64")) {
			bytes = Base64.getDecoder().decode(data);
		} else {
			throw new IllegalArgumentException(
					"Unsupported encoding type: " + encodingType);
		}

		if (fileFormat.equals("jpg") || fileFormat.equals("jpeg")
				|| fileFormat.equals("png")) {
			BufferedImage img = ImageLoader.createImage(
					Toolkit.getDefaultToolkit().createImage(bytes),
					ImageLoader.TYPE_DEFAULT);
			return new ImageDataValue(img);
		} else if (fileFormat.equalsIgnoreCase(VectorImage.FILE_EXTENSION)) {
			VectorImage vi;
			try (InputStream in = new ByteArrayInputStream(bytes)) {
				vi = new VectorImage(in);
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			return new ImageDataValue(new VectorImagePaintable(vi));
		} else {
			// regarding gifs: we could support them, we just haven't
			// bothered yet.
			throw new UnsupportedOperationException(
					"This decoder does not support bas64 encoded " + fileFormat
							+ ".");
		}
	}

	private final String cssStr, urlStr;

	public CssUrlImageValue(String cssStr, String urlStr) {
		Objects.requireNonNull(cssStr);
		this.cssStr = cssStr;
		this.urlStr = urlStr;
	}

	@Override
	public void paintRectangle(Graphics2D g, QViewHelper viewHelper,
			int layerIndex, int x, int y, int width, int height) {
		Cache<String, AtomicReference<ImageDataValue>> imageCache = getCache(
				viewHelper.getView().getDocument());
		AtomicReference<ImageDataValue> ref = imageCache.get(cssStr);
		if (ref == null) {
			ref = new AtomicReference<>(createPaintable());
			imageCache.put(cssStr, ref);
		}
		ImageDataValue p = ref.get();
		if (p != null) {
			paintImage(g, p, viewHelper, layerIndex, x, y, width, height);
		} else {
			// this probably means an error occurred loading the image, so we
			// give up
		}
	}

	@SuppressWarnings("unchecked")
	protected void paintImage(Graphics2D g, ImageDataValue p,
			QViewHelper viewHelper, int layerIndex, int x, int y, int width,
			int height) {

		g.clipRect(x, y, width, height);

		// TODO refactor away this list
		List<CssBackgroundRepeatValue> allRepeats = (List<CssBackgroundRepeatValue>) viewHelper
				.getAttribute("background-repeat", false);
		List<CssBackgroundPositionValue> allPositions = (List<CssBackgroundPositionValue>) viewHelper
				.getAttribute("background-position", false);
		List<CssBackgroundSizeValue> allSizes = (List<CssBackgroundSizeValue>) viewHelper
				.getAttribute("background-size", false);
		List<CssBackgroundAttachmentValue> allAttachments = (List<CssBackgroundAttachmentValue>) viewHelper
				.getAttribute(
						CssBackgroundAttachmentValue.PROPERTY_BACKGROUND_ATTACHMENT,
						false);

		CssBackgroundRepeatValue repeatValue = getLayerValue(layerIndex,
				allRepeats);
		CssBackgroundPositionValue positionValue = getLayerValue(layerIndex,
				allPositions);
		CssBackgroundAttachmentValue attachmentValue = getLayerValue(layerIndex,
				allAttachments);
		CssBackgroundSizeValue sizeValue = getLayerValue(layerIndex, allSizes);

		if (attachmentValue != null && attachmentValue
				.getMode() == CssBackgroundAttachmentValue.Mode.FIXED) {
			Component c = viewHelper.getView().getContainer();
			JViewport viewport = null;
			while (c != null && viewport == null) {
				if (c instanceof JViewport)
					viewport = (JViewport) c;
				c = c.getParent();
			}
			if (viewport != null) {
				// if you try scrolling in the default (blitting) mode:
				// you see some terrible repaints.
				viewport.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
				g.translate(viewport.getViewPosition().x,
						viewport.getViewPosition().y);
			}
		}

		if (repeatValue == null) {
			repeatValue = new CssBackgroundRepeatValue(
					CssBackgroundRepeatValue.Mode.REPEAT);
		}

		if (positionValue == null) {
			positionValue = new CssBackgroundPositionValue("",
					new CssLength(0, "px"), true, new CssLength(0, "px"), true);
		}

		Dimension imageSize = new Dimension(p.paintable.getWidth(),
				p.paintable.getHeight());
		imageSize = sizeValue == null ? imageSize
				: sizeValue.getCalculator().getSize(width, height, imageSize);

		for (Span xSpan : repeatValue.getHorizontalMode().getSpans(x, width,
				imageSize.width,
				positionValue == null ? null
						: positionValue.getHorizontalPosition(),
				positionValue == null ? true
						: positionValue.isHorizontalPositionFromLeft())) {
			for (Span ySpan : repeatValue.getVerticalMode().getSpans(y, height,
					imageSize.height,
					positionValue == null ? null
							: positionValue.getVerticalPosition(),
					positionValue == null ? true
							: positionValue.isVerticalPositionFromTop())) {
				if (p.bufferedImage != null) {
					g.drawImage(p.bufferedImage, xSpan.position, ySpan.position,
							xSpan.length, ySpan.length, null);
				} else {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.translate(xSpan.position, ySpan.position);
					g2.scale(
							((double) xSpan.length)
									/ ((double) imageSize.width),
							((double) ySpan.length)
									/ ((double) imageSize.height));
					p.paintable.paint(g2);
					g2.dispose();
				}
			}
		}

		// TODO: once more dust settles evaluate the relationship of the "image"
		// package to the "background" package

	}

	private <T> T getLayerValue(int layerIndex, List<T> allLayers) {
		if (allLayers != null && layerIndex < allLayers.size()) {
			return allLayers.get(layerIndex);
		} else if (allLayers == null || allLayers.isEmpty()) {
			return null;
		}
		// erg, not sure what to do here?
		return allLayers.get(allLayers.size() - 1);
	}

	protected ImageDataValue createPaintable() {
		ImageDataValue paintable = null;
		try {
			if (urlStr.startsWith("data:")) {
				paintable = createPaintableFromDataUrl(urlStr);
			} else {
				URL url = new URL(urlStr);
				BufferedImage bi = ImageLoader.createImage(url,
						ImageLoader.TYPE_DEFAULT);
				paintable = new ImageDataValue(bi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paintable;
	}

	@SuppressWarnings("unchecked")
	protected Cache<String, AtomicReference<ImageDataValue>> getCache(
			Document doc) {
		Cache<String, AtomicReference<ImageDataValue>> cache = (Cache<String, AtomicReference<ImageDataValue>>) doc
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