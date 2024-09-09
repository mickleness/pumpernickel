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
package com.pump.awt.converter;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serial;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;
import com.pump.data.converter.ConverterUtils;
import com.pump.data.converter.ConverterUtils.FloatArray;

/**
 * This is a BeanMapConverter for LinearGradientPaints.
 */
public class LinearGradientPaintMapConverter
		implements BeanMapConverter<LinearGradientPaint> {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * This property defines {@link LinearGradientPaint#getColors()}.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<List> PROPERTY_COLORS = new Key<>(List.class,
			"colors");

	/**
	 * This property defines {@link LinearGradientPaint#getColorSpace()}.
	 */
	public static final Key<ColorSpaceType> PROPERTY_COLORSPACE = new Key<>(
			ColorSpaceType.class, "color-space");

	/**
	 * This property defines {@link LinearGradientPaint#getCycleMethod()}.
	 */
	public static final Key<CycleMethod> PROPERTY_CYCLEMETHOD = new Key<>(
			CycleMethod.class, "cycle-method");

	/**
	 * This property defines {@link LinearGradientPaint#getFractions()}.
	 */
	public static final Key<FloatArray> PROPERTY_FRACTIONS = new Key<>(
			FloatArray.class, "fractions");

	/**
	 * This property defines {@link LinearGradientPaint#getTransform()}
	 */
	public static final Key<AffineTransform> PROPERTY_TRANSFORM = new Key<>(
			AffineTransform.class, "transform");

	/**
	 * This property defines {@link LinearGradientPaint#getStartPoint()}. See
	 * Point2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_POINT_START = new Key<>(Map.class,
			"point-start");

	/**
	 * This property defines {@link LinearGradientPaint#getEndPoint()}. See
	 * Point2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_POINT_END = new Key<>(Map.class,
			"point-end");

	@Override
	public Class<LinearGradientPaint> getType() {
		return LinearGradientPaint.class;
	}

	@Override
	public Map<String, Object> createAtoms(LinearGradientPaint lgp) {
		Map<String, Object> atoms = new HashMap<>(7);
		PROPERTY_COLORS.put(atoms, Arrays.asList(lgp.getColors()));
		PROPERTY_COLORSPACE.put(atoms, lgp.getColorSpace());
		PROPERTY_CYCLEMETHOD.put(atoms, lgp.getCycleMethod());
		PROPERTY_FRACTIONS.put(atoms,
				ConverterUtils.FloatArray.get(lgp.getFractions()));
		PROPERTY_TRANSFORM.put(atoms, lgp.getTransform());
		PROPERTY_POINT_START.put(atoms,
				new Point2DMapConverter().createAtoms(lgp.getStartPoint()));
		PROPERTY_POINT_END.put(atoms,
				new Point2DMapConverter().createAtoms(lgp.getEndPoint()));
		return atoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LinearGradientPaint createFromAtoms(Map<String, Object> atoms) {
		List<Color> colorsList = PROPERTY_COLORS.get(atoms);
		Color[] colors = colorsList.toArray(new Color[0]);

		ColorSpaceType colorSpaceType = PROPERTY_COLORSPACE.get(atoms);
		CycleMethod cycleMethod = PROPERTY_CYCLEMETHOD.get(atoms);
		float[] fractions = PROPERTY_FRACTIONS.get(atoms).data;
		AffineTransform tx = PROPERTY_TRANSFORM.get(atoms);
		Point2D start = new Point2DMapConverter()
				.createFromAtoms(PROPERTY_POINT_START.get(atoms));
		Point2D end = new Point2DMapConverter()
				.createFromAtoms(PROPERTY_POINT_END.get(atoms));

		return new LinearGradientPaint(start, end, fractions, colors,
				cycleMethod, colorSpaceType, tx);
	}
}