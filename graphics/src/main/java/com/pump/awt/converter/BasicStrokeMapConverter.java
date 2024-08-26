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

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;
import com.pump.data.converter.ConverterUtils.FloatArray;

/**
 * This is a BeanMapConverter for BasicStrokes.
 */
public class BasicStrokeMapConverter implements BeanMapConverter<BasicStroke> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines {@link BasicStroke#getLineWidth()}.
	 */
	public static final Key<Float> PROPERTY_LINE_WIDTH = new Key<>(Float.class,
			"line-width");

	/**
	 * This property defines {@link BasicStroke#getEndCap()}.
	 */
	public static final Key<Integer> PROPERTY_LINE_CAP = new Key<>(
			Integer.class, "line-cap");

	/**
	 * This property defines {@link BasicStroke#getLineJoin()}.
	 */
	public static final Key<Integer> PROPERTY_LINE_JOIN = new Key<>(
			Integer.class, "line-join");

	/**
	 * This property defines {@link BasicStroke#getMiterLimit()}.
	 */
	public static final Key<Float> PROPERTY_MITER_LIMIT = new Key<>(Float.class,
			"miter-limit");

	/**
	 * This property defines {@link BasicStroke#getDashArray()}.
	 */
	public static final Key<FloatArray> PROPERTY_DASH_ARRAY = new Key<>(
			FloatArray.class, "dash-array");

	/**
	 * This property defines {@link BasicStroke#getDashPhase()}.
	 */
	public static final Key<Float> PROPERTY_DASH_PHASE = new Key<>(Float.class,
			"dash-phase");

	@Override
	public Class<BasicStroke> getType() {
		return BasicStroke.class;
	}

	@Override
	public Map<String, Object> createAtoms(BasicStroke bs) {
		Map<String, Object> atoms = new HashMap<>(6);
		PROPERTY_LINE_WIDTH.put(atoms, bs.getLineWidth());
		PROPERTY_LINE_CAP.put(atoms, bs.getEndCap());
		PROPERTY_LINE_JOIN.put(atoms, bs.getLineJoin());
		PROPERTY_MITER_LIMIT.put(atoms, bs.getMiterLimit());
		PROPERTY_DASH_ARRAY.put(atoms, FloatArray.get(bs.getDashArray()));
		PROPERTY_DASH_PHASE.put(atoms, bs.getDashPhase());
		return atoms;
	}

	@Override
	public BasicStroke createFromAtoms(Map<String, Object> atoms) {
		float lineWidth = PROPERTY_LINE_WIDTH.get(atoms);
		int endCap = PROPERTY_LINE_CAP.get(atoms);
		int lineJoin = PROPERTY_LINE_JOIN.get(atoms);
		float miterLimit = PROPERTY_MITER_LIMIT.get(atoms);
		FloatArray dashArray = PROPERTY_DASH_ARRAY.get(atoms);
		float dashPhase = PROPERTY_DASH_PHASE.get(atoms);

		return new BasicStroke(lineWidth, endCap, lineJoin, miterLimit,
				dashArray == null ? null : dashArray.data, dashPhase);
	}

}