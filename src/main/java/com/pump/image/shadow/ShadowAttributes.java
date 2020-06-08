package com.pump.image.shadow;

import com.pump.data.AbstractAttributeDataImpl;
import com.pump.data.Key;

public class ShadowAttributes extends AbstractAttributeDataImpl {
	private static final long serialVersionUID = 1L;

	public static final Key<Float> KEY_SHADOW_OPACITY = Key
			.createBoundedKey("shadowOpacity", 1f, 0f, 1f);
	public static final Key<Float> KEY_SHADOW_OFFSET_RADIANS = new Key<>(
			Float.class, "shadowOffsetRadians", 1f);
	public static final Key<Float> KEY_SHADOW_OFFSET = new Key<>(Float.class,
			"shadowOffsetPixels", 0f);
	public static final Key<Float> KEY_SHADOW_KERNEL_RADIUS = new Key<>(
			Float.class, "shadowKernelRadius", 1f);

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
	 * @param opacity
	 *            a float from 0-1.
	 */
	public ShadowAttributes(float kernelRadius, float opacity) {
		setShadowKernelRadius(kernelRadius);
		setShadowOpacity(opacity);
	}

	public float getShadowOpacity() {
		return getAttribute(KEY_SHADOW_OPACITY);
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

	public void setShadowOpacity(float opacity) {
		setAttribute(KEY_SHADOW_OPACITY, opacity);
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
