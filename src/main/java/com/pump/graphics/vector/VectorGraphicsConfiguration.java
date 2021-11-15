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
package com.pump.graphics.vector;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

/**
 * This is a GraphicsConfiguration for a VectorGraphics2D.
 * <p>
 * The color model is an ARGB <code>DirectColorModel</code>.
 * <p>
 * The bounds and device are currently <code>null</code>, and the transforms are
 * all identity transforms.
 */
public class VectorGraphicsConfiguration extends GraphicsConfiguration {

	/** Color model used for RGB */
	static ColorModel RGB_COLOR_MODEL = new DirectColorModel(24, 0x00ff0000,
			0x0000ff00, 0x000000ff);

	/** Color model used for ARGB */
	static ColorModel ARGB_COLOR_MODEL = new DirectColorModel(32, 0x00ff0000,
			0x0000ff00, 0x000000ff, 0xff000000);

	@Override
	public GraphicsDevice getDevice() {
		return null;
	}

	@Override
	public ColorModel getColorModel() {
		return ARGB_COLOR_MODEL;
	}

	@Override
	public ColorModel getColorModel(int transparency) {
		if (transparency == Transparency.OPAQUE)
			return RGB_COLOR_MODEL;
		return getColorModel();
	}

	@Override
	public AffineTransform getDefaultTransform() {
		return new AffineTransform();
	}

	@Override
	public AffineTransform getNormalizingTransform() {
		return new AffineTransform();
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}

}