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
package com.pump.awt.converter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for GradientPaints.
 */
public class GradientPaintMapConverter
		implements BeanMapConverter<GradientPaint> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines {@link GradientPaint#getPoint1()}. See
	 * Point2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_POINT1 = new Key<>(Map.class,
			"point-1");

	/**
	 * This property defines {@link GradientPaint#getPoint2()}. See
	 * Point2DMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_POINT2 = new Key<>(Map.class,
			"point-2");

	/**
	 * This property defines {@link GradientPaint#getColor1()}.
	 */
	public static final Key<Color> PROPERTY_COLOR1 = new Key<>(Color.class,
			"color-1");

	/**
	 * This property defines {@link GradientPaint#getColor2()}.
	 */
	public static final Key<Color> PROPERTY_COLOR2 = new Key<>(Color.class,
			"color-2");

	/**
	 * This property defines {@link GradientPaint#isCyclic()}.
	 */
	public static final Key<Boolean> PROPERTY_IS_CYCLIC = new Key<>(
			Boolean.class, "is-cyclic");

	@Override
	public Class<GradientPaint> getType() {
		return GradientPaint.class;
	}

	@Override
	public Map<String, Object> createAtoms(GradientPaint gp) {
		Map<String, Object> atoms = new HashMap<>(5);

		Point2D p1 = gp.getPoint1();
		Point2D p2 = gp.getPoint2();

		PROPERTY_POINT1.put(atoms, new Point2DMapConverter().createAtoms(p1));
		PROPERTY_POINT2.put(atoms, new Point2DMapConverter().createAtoms(p2));

		PROPERTY_COLOR1.put(atoms, gp.getColor1());
		PROPERTY_COLOR2.put(atoms, gp.getColor2());
		PROPERTY_IS_CYCLIC.put(atoms, gp.isCyclic());

		return atoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GradientPaint createFromAtoms(Map<String, Object> atoms) {
		Point2D p1 = new Point2DMapConverter()
				.createFromAtoms(PROPERTY_POINT1.get(atoms));
		Point2D p2 = new Point2DMapConverter()
				.createFromAtoms(PROPERTY_POINT2.get(atoms));
		Color color1 = PROPERTY_COLOR1.get(atoms);
		Color color2 = PROPERTY_COLOR2.get(atoms);
		boolean cyclic = PROPERTY_IS_CYCLIC.get(atoms);

		return new GradientPaint(p1, color1, p2, color2, cyclic);
	}
}