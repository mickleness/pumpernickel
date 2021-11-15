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
package com.pump.image.shadow;

import java.awt.Color;

import com.pump.data.AbstractAttributeDataImpl;
import com.pump.data.Key;

/**
 * This is an immutable set of attributes used to render a shadow.
 */
public class ShadowAttributes extends AbstractAttributeDataImpl {
	private static final long serialVersionUID = 1L;

	public static final Key<Float> KEY_SHADOW_X_OFFSET = new Key<>(Float.class,
			"x", 2f);
	public static final Key<Float> KEY_SHADOW_Y_OFFSET = new Key<>(Float.class,
			"y", 2f);
	public static final Key<Float> KEY_SHADOW_KERNEL_RADIUS = new Key<>(
			Float.class, "shadowKernelRadius", 1f);
	public static final Key<Color> KEY_SHADOW_COLOR = new Key<>(Color.class,
			"shadowColor", Color.BLACK);

	/**
	 * 
	 * @param xOffset
	 *            the x offset for this shadow. Most of the time this should be
	 *            an integer. ShadowRenderers are not guaranteed to support
	 *            fractional values, and if they do it may come at a significant
	 *            performance cost.
	 * @param yOffset
	 *            the y offset for this shadow. Most of the time this should be
	 *            an integer. ShadowRenderers are not guaranteed to support
	 *            fractional values, and if they do it may come at a significant
	 *            performance cost.
	 * @param kernelRadius
	 *            a kernel radius that is positive. The length of the kernel
	 *            should always be (2 * r + 1). The "+1" comes from the center
	 *            element (so even a radius of zero has a kernel of [n]).
	 *            <p>
	 *            Renderers are not guaranteed to support decimal precision
	 *            radii, but the three current renderers (BoxShadowRenderer,
	 *            DoubleBoxShadowRenderer and GaussianShadowRenderer) support
	 *            decimal precision. Decimal precision may be especially using
	 *            during animation (so the radius doesn't jump from 1.0 to 2.0),
	 *            but it may also come with a performance cost.
	 * @param color
	 *            the shadow color, such as Color.BLACK. This color can include
	 *            its own custom opacity, but remember that larger blur radiuses
	 *            also dilute the opacity.
	 */
	public ShadowAttributes(float xOffset, float yOffset, float kernelRadius,
			Color color) {
		setAttribute(KEY_SHADOW_KERNEL_RADIUS, kernelRadius);
		setAttribute(KEY_SHADOW_COLOR, color);
		setAttribute(KEY_SHADOW_X_OFFSET, xOffset);
		setAttribute(KEY_SHADOW_Y_OFFSET, yOffset);
	}

	public Color getShadowColor() {
		return getAttribute(KEY_SHADOW_COLOR);
	}

	public float getShadowKernelRadius() {
		return getAttribute(KEY_SHADOW_KERNEL_RADIUS);
	}

	public float getShadowXOffset() {
		return getAttribute(KEY_SHADOW_X_OFFSET);
	}

	public float getShadowYOffset() {
		return getAttribute(KEY_SHADOW_Y_OFFSET);
	}

	@Override
	public String toString() {
		return "ShadowAttribute[ " + toCSSString() + "]";
	}

	/**
	 * Produce a CSS-like string representing this object like "0px 0px 3px
	 * #FF00FF"
	 */
	public String toCSSString() {
		Color c = getShadowColor();
		String colorHex;
		if (c.getAlpha() == 255) {
			colorHex = Integer.toHexString(c.getRGB() & 0xFFFFFF);
			while (colorHex.length() < 6)
				colorHex = "0" + colorHex;
		} else {
			colorHex = Integer.toHexString(c.getRGB());
			while (colorHex.length() < 8)
				colorHex = "0" + colorHex;
		}
		return getShadowXOffset() + "px " + getShadowYOffset() + "px "
				+ getShadowKernelRadius() + "px #" + colorHex;
	}
}