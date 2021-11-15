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
package com.pump.awt.converter;

import java.awt.Color;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;
import com.pump.data.converter.ConverterUtils;
import com.pump.data.converter.ConverterUtils.FloatArray;

/**
 * This is a BeanMapConverter for RadialGradientPaints.
 */
public class RadialGradientPaintMapConverter
		implements BeanMapConverter<RadialGradientPaint> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines {@link RadialGradientPaint#getColors()}.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<List> PROPERTY_COLORS = new Key<>(List.class,
			"colors");

	/**
	 * This property defines {@link RadialGradientPaint#getColorSpace()}.
	 */
	public static final Key<ColorSpaceType> PROPERTY_COLORSPACE = new Key<>(
			ColorSpaceType.class, "color-space-type");

	/**
	 * This property defines {@link RadialGradientPaint#getCycleMethod()}.
	 */
	public static final Key<CycleMethod> PROPERTY_CYCLEMETHOD = new Key<>(
			CycleMethod.class, "cycle-method");

	/**
	 * This property defines {@link RadialGradientPaint#getFractions()}.
	 */
	public static final Key<FloatArray> PROPERTY_FRACTIONS = new Key<>(
			FloatArray.class, "fractions");

	/**
	 * This property defines {@link RadialGradientPaint#getCenterPoint()}. See
	 * Point2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_POINT_CENTER = new Key<>(Map.class,
			"center");

	/**
	 * This property defines {@link RadialGradientPaint#getTransform()}.
	 */
	public static final Key<AffineTransform> PROPERTY_TRANSFORM = new Key<>(
			AffineTransform.class, "transform");

	/**
	 * This property defines {@link RadialGradientPaint#getFocusPoint()}. See
	 * Point2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_POINT_FOCUS = new Key<>(Map.class,
			"focus");

	/**
	 * This property defines the gradient radius.
	 */
	public static final Key<Float> PROPERTY_RADIUS = new Key<>(Float.class,
			"radius");

	@Override
	public Class<RadialGradientPaint> getType() {
		return RadialGradientPaint.class;
	}

	@Override
	public Map<String, Object> createAtoms(RadialGradientPaint rgp) {
		Map<String, Object> atoms = new HashMap<>(8);
		PROPERTY_COLORS.put(atoms, Arrays.asList(rgp.getColors()));
		PROPERTY_COLORSPACE.put(atoms, rgp.getColorSpace());
		PROPERTY_CYCLEMETHOD.put(atoms, rgp.getCycleMethod());
		PROPERTY_FRACTIONS.put(atoms,
				ConverterUtils.FloatArray.get(rgp.getFractions()));
		PROPERTY_TRANSFORM.put(atoms, rgp.getTransform());
		PROPERTY_POINT_CENTER.put(atoms,
				new Point2DMapConverter().createAtoms(rgp.getCenterPoint()));
		PROPERTY_POINT_FOCUS.put(atoms,
				new Point2DMapConverter().createAtoms(rgp.getFocusPoint()));
		PROPERTY_RADIUS.put(atoms, rgp.getRadius());
		return atoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RadialGradientPaint createFromAtoms(Map<String, Object> atoms) {
		List<Color> colorsList = PROPERTY_COLORS.get(atoms);
		Color[] colors = colorsList.toArray(new Color[colorsList.size()]);

		ColorSpaceType colorSpaceType = PROPERTY_COLORSPACE.get(atoms);
		CycleMethod cycleMethod = PROPERTY_CYCLEMETHOD.get(atoms);
		float[] fractions = PROPERTY_FRACTIONS.get(atoms).data;
		AffineTransform tx = PROPERTY_TRANSFORM.get(atoms);
		Point2D center = new Point2DMapConverter()
				.createFromAtoms(PROPERTY_POINT_CENTER.get(atoms));
		Point2D focus = new Point2DMapConverter()
				.createFromAtoms(PROPERTY_POINT_FOCUS.get(atoms));
		float radius = PROPERTY_RADIUS.get(atoms);

		return new RadialGradientPaint(center, radius, focus, fractions, colors,
				cycleMethod, colorSpaceType, tx);
	}
}