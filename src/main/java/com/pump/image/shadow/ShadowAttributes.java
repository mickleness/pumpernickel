package com.pump.image.shadow;

import java.awt.Color;

import com.pump.data.AbstractAttributeDataImpl;
import com.pump.data.Key;

public class ShadowAttributes extends AbstractAttributeDataImpl {
	private static final long serialVersionUID = 1L;

	public static final Key<Float> KEY_SHADOW_OFFSET_RADIANS = new Key<>(
			Float.class, "shadowOffsetRadians", 1f);
	public static final Key<Float> KEY_SHADOW_OFFSET = new Key<>(Float.class,
			"shadowOffsetPixels", 0f);
	public static final Key<Float> KEY_SHADOW_KERNEL_RADIUS = new Key<>(
			Float.class, "shadowKernelRadius", 1f);
	public static final Key<Color> KEY_SHADOW_COLOR = new Key<>(Color.class,
			"shadowColor", Color.BLACK);

	/**
	 * 
	 * @param kernelRadius
	 *            a kernel radius that is positive. "1" may create a kernel
	 *            resembling [1]. "2" may create a kernel resembling [1, 2, 1].
	 *            "3" may create a kernel resembling: "1, 2, 2, 4, 2, 2, 1",
	 *            etc.
	 *            <p>
	 *            Some renderers may round this attribute to the nearest
	 *            integer.
	 * 
	 * @param color
	 *            the shadow color, such as Color.BLACK. This color can include
	 *            its own custom opacity, but remember that larger blur radiuses
	 *            also dilute the opacity.
	 */
	public ShadowAttributes(float kernelRadius, Color color) {
		setShadowKernelRadius(kernelRadius);
		setShadowColor(color);
	}

	public Color getShadowColor() {
		return getAttribute(KEY_SHADOW_COLOR);
	}

	public float getShadowKernelRadius() {
		return getAttribute(KEY_SHADOW_KERNEL_RADIUS);
	}

	public float getShadowOffsetAngle() {
		return getAttribute(KEY_SHADOW_OFFSET_RADIANS);
	}

	public float getShadowOffsetDistance() {
		return getAttribute(KEY_SHADOW_OFFSET);
	}

	public void setShadowColor(Color color) {
		setAttribute(KEY_SHADOW_COLOR, color);
	}

	public void setShadowKernelRadius(float kernelSize) {
		setAttribute(KEY_SHADOW_KERNEL_RADIUS, kernelSize);
	}

	public void setShadowOffsetAngle(float offsetRadians) {
		setAttribute(KEY_SHADOW_OFFSET_RADIANS, offsetRadians);
	}

	public void setShadowOffsetDistance(float shadowOffset) {
		setAttribute(KEY_SHADOW_OFFSET, shadowOffset);
	}
}
