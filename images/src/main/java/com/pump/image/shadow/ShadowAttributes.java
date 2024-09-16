/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.shadow;

import java.awt.Color;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * This is an immutable set of attributes used to render a shadow.
 */
public class ShadowAttributes implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	Color shadowColor;
	float xOffset, yOffset, shadowKernelRadius;


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
		setShadowColor(color);
		setShadowKernelRadius(kernelRadius);
		setShadowXOffset(xOffset);
		setShadowYOffset(yOffset);
	}

	public Color getShadowColor() {
		return shadowColor;
	}

	public float getShadowKernelRadius() {
		return shadowKernelRadius;
	}

	public float getShadowXOffset() {
		return xOffset;
	}

	public float getShadowYOffset() {
		return yOffset;
	}


	public void setShadowColor(Color shadowColor) {
		this.shadowColor = Objects.requireNonNull(shadowColor);
	}

	public void setShadowKernelRadius(float shadowKernelRadius) {
		this.shadowKernelRadius = shadowKernelRadius;
	}

	public void setShadowXOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	public void setShadowYOffset(float yOffset) {
		this.yOffset = yOffset;
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
		StringBuilder colorHex;
		if (c.getAlpha() == 255) {
			colorHex = new StringBuilder(Integer.toHexString(c.getRGB() & 0xFFFFFF));
			while (colorHex.length() < 6)
				colorHex.insert(0, "0");
		} else {
			colorHex = new StringBuilder(Integer.toHexString(c.getRGB()));
			while (colorHex.length() < 8)
				colorHex.insert(0, "0");
		}
		return getShadowXOffset() + "px " + getShadowYOffset() + "px "
				+ getShadowKernelRadius() + "px #" + colorHex;
	}

	@Serial
	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeFloat(getShadowXOffset());
		out.writeFloat(getShadowYOffset());
		out.writeFloat(getShadowKernelRadius());
		out.writeObject(getShadowColor());
	}

	@Serial
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			setShadowXOffset(in.readFloat());
			setShadowYOffset(in.readFloat());
			setShadowKernelRadius(in.readFloat());
			setShadowColor((Color) in.readObject());
		} else {
			throw new IOException(
					"unsupported internal version " + version);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShadowAttributes that = (ShadowAttributes) o;
		return Float.compare(that.xOffset, xOffset) == 0 && Float.compare(that.yOffset, yOffset) == 0 && Float.compare(that.shadowKernelRadius, shadowKernelRadius) == 0 && Objects.equals(shadowColor, that.shadowColor);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shadowColor, xOffset, yOffset, shadowKernelRadius);
	}
}