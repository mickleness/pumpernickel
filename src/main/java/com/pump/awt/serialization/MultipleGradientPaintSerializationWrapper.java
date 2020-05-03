package com.pump.awt.serialization;

import java.awt.Color;
import java.awt.MultipleGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.AffineTransform;

import com.pump.io.serialization.AbstractSerializationWrapper;

public abstract class MultipleGradientPaintSerializationWrapper<T extends MultipleGradientPaint>
		extends AbstractSerializationWrapper<T> {
	private static final long serialVersionUID = 1L;

	protected static final String KEY_COLORS = "colors";
	protected static final String KEY_COLOR_SPACE_TYPE = "colorSpaceType";
	protected static final String KEY_CYCLE_METHOD = "cycleMethod";
	protected static final String KEY_FRACTIONS = "fractions";
	protected static final String KEY_TRANSFORM = "transform";

	public MultipleGradientPaintSerializationWrapper(
			MultipleGradientPaint mgp) {
		map.put(KEY_COLORS, mgp.getColors());
		map.put(KEY_COLOR_SPACE_TYPE, mgp.getColorSpace());
		map.put(KEY_CYCLE_METHOD, mgp.getCycleMethod());
		map.put(KEY_FRACTIONS, mgp.getFractions());
		map.put(KEY_TRANSFORM, mgp.getTransform());
	}

	protected Color[] getColors() {
		return (Color[]) map.get(KEY_COLORS);
	}

	protected ColorSpaceType getColorSpace() {
		return (ColorSpaceType) map.get(KEY_COLOR_SPACE_TYPE);
	}

	protected CycleMethod getCycleMethod() {
		return (CycleMethod) map.get(KEY_CYCLE_METHOD);
	}

	protected float[] getFractions() {
		return (float[]) map.get(KEY_FRACTIONS);
	}

	protected AffineTransform getTransform() {
		return (AffineTransform) map.get(KEY_TRANSFORM);
	}
}