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
package com.pump.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * This context paints a BufferedImage using 4 corner points.
 * <p>
 * As of this writing: this is only expected to support RGB or ARGB images.
 * <p>
 * (The standard <code>java.awt.Graphics2D</code> supports
 * <code>AffineTransforms</code>, which effectively use 3 corner points. Using a
 * 4th point is mathematically harder to do, but is considered crucial for
 * projecting a plane as if in a 3D view. The <code>PerspectiveTransform</code>
 * class generally handles the math of the transform itself.)
 */
public abstract class ImageContext implements AutoCloseable {

	/**
	 * Create an <code>ImageContext</code> for a <code>BufferedImage</code>.
	 * <p>
	 * This is shorthand for: <br>
	 * <code>ImageContextFactory.get().create(image)</code>
	 */
	public static ImageContext create(BufferedImage image) {
		return ImageContextFactory.get().create(image);
	}

	/**
	 * The currently defined RenderingHints.
	 * 
	 */
	protected RenderingHints renderingHints = new RenderingHints(
			new HashMap<RenderingHints.Key, Object>());

	public abstract void drawImage(BufferedImage img, Point2D topLeft,
			Point2D topRight, Point2D bottomRight, Point2D bottomLeft);

	@Override
	public abstract void close();

	/** Define a rendering hint. */
	public void setRenderingHint(RenderingHints.Key key, Object value) {
		if (value == null)
			renderingHints.remove(key);
		renderingHints.put(key, value);
	}

	/** Replace all current rendering hints with the argument. */
	public void setRenderingHints(RenderingHints hints) {
		renderingHints = new RenderingHints(
				new HashMap<RenderingHints.Key, Object>());
		renderingHints.putAll(hints);
	}

	/**
	 * Return the interpolation rendering hint to use. This will not return
	 * null. If this is undefined, then the antialiasing and rendering hints are
	 * consulted (which will return either NEAREST_NEIGHBOR or BILINEAR). If
	 * nothing is defined then this returns BILINEAR.
	 */
	protected Object getInterpolationRenderingHint() {
		Object v = renderingHints.get(RenderingHints.KEY_INTERPOLATION);
		if (v != null)
			return v;

		v = renderingHints.get(RenderingHints.KEY_ANTIALIASING);
		if (RenderingHints.VALUE_ANTIALIAS_ON.equals(v)) {
			return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		} else if (RenderingHints.VALUE_ANTIALIAS_OFF.equals(v)) {
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}

		v = renderingHints.get(RenderingHints.KEY_RENDERING);
		if (RenderingHints.VALUE_RENDER_QUALITY.equals(v)) {
			return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		} else if (RenderingHints.VALUE_RENDER_SPEED.equals(v)) {
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}

		// nothing is defined:
		return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
	}
}