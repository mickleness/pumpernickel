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
	public static final Key<Integer> KEY_SHADOW_KERNEL_SIZE = new Key<>(
			Integer.class, "shadowKernelSize", 1);

	public ShadowAttributes(int kernelSize, float opacity) {
		setShadowKernelSize(kernelSize);
		setShadowOpacity(opacity);
	}

	public float getShadowOpacity() {
		return getAttribute(KEY_SHADOW_OPACITY);
	}

	public int getShadowKernelSize() {
		return getAttribute(KEY_SHADOW_KERNEL_SIZE);
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

	public void setShadowKernelSize(int kernelSize) {
		setAttribute(KEY_SHADOW_KERNEL_SIZE, kernelSize);
	}

	public void setShadowOffsetAngle(float offsetRadians) {
		setAttribute(KEY_SHADOW_OFFSET_RADIANS, offsetRadians);
	}

	public void setShadowOffsetDistance(float shadowOffset) {
		setAttribute(KEY_SHADOW_OFFSET, shadowOffset);
	}
}
