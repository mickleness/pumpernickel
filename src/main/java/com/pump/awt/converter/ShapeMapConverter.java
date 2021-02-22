package com.pump.awt.converter;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;
import com.pump.data.converter.ConverterUtils;

/**
 * This is a BeanMapConverter for Shapes.
 */
public class ShapeMapConverter implements BeanMapConverter<Shape> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines all the segment types of the shape
	 * ({@link PathIterator#SEG_LINETO}, {@link PathIterator#SEG_CUBICTO}, etc.)
	 */
	public static final Key<ConverterUtils.ByteArray> PROPERTY_SEGMENT_TYPES = new Key<>(
			ConverterUtils.ByteArray.class, "segment-types");

	/**
	 * This property defines all the (x,y) coordinate data of the shape.
	 */
	public static final Key<ConverterUtils.FloatArray> PROPERTY_SEGMENT_COORDINATES = new Key<>(
			ConverterUtils.FloatArray.class, "segment-coordinates");

	/**
	 * This property defines {@link PathIterator#getWindingRule()}. This should
	 * be {@link PathIterator#WIND_EVEN_ODD} or
	 * {@link PathIterator#WIND_NON_ZERO}.
	 */
	public static final Key<Integer> PROPERTY_WINDING_RULE = new Key<>(
			Integer.class, "winding-rule");

	@Override
	public Class<Shape> getType() {
		return Shape.class;
	}

	@Override
	public Map<String, Object> createAtoms(Shape object) {
		Map<String, Object> atoms = new HashMap<>(3);

		List<Byte> segmentTypes = new ArrayList<>();
		List<Float> segmentCoordinates = new ArrayList<>();

		PathIterator pi = object.getPathIterator(null);
		PROPERTY_WINDING_RULE.put(atoms, pi.getWindingRule());
		float[] coords = new float[6];
		while (!pi.isDone()) {
			byte k = (byte) pi.currentSegment(coords);
			segmentTypes.add(k);
			if (k == PathIterator.SEG_MOVETO || k == PathIterator.SEG_LINETO) {
				segmentCoordinates.add(coords[0]);
				segmentCoordinates.add(coords[1]);
			} else if (k == PathIterator.SEG_QUADTO) {
				segmentCoordinates.add(coords[0]);
				segmentCoordinates.add(coords[1]);
				segmentCoordinates.add(coords[2]);
				segmentCoordinates.add(coords[3]);
			} else if (k == PathIterator.SEG_CUBICTO) {
				segmentCoordinates.add(coords[0]);
				segmentCoordinates.add(coords[1]);
				segmentCoordinates.add(coords[2]);
				segmentCoordinates.add(coords[3]);
				segmentCoordinates.add(coords[4]);
				segmentCoordinates.add(coords[5]);
			} else if (k == PathIterator.SEG_CLOSE) {
				// intentionally empty
			} else {
				throw new RuntimeException("Illegal segment type: " + k);
			}
			pi.next();
		}

		byte[] segmentTypesArray = new byte[segmentTypes.size()];
		int i = 0;
		for (Byte segmentType : segmentTypes) {
			segmentTypesArray[i++] = segmentType.byteValue();
		}

		float[] segmentCoordinatesArray = new float[segmentCoordinates.size()];
		i = 0;
		for (Float segmentCoordinate : segmentCoordinates) {
			segmentCoordinatesArray[i++] = segmentCoordinate.floatValue();
		}

		PROPERTY_SEGMENT_TYPES.put(atoms,
				ConverterUtils.ByteArray.get(segmentTypesArray));
		PROPERTY_SEGMENT_COORDINATES.put(atoms,
				ConverterUtils.FloatArray.get(segmentCoordinatesArray));

		return atoms;
	}

	@Override
	public Shape createFromAtoms(Map<String, Object> atoms) {
		int windingRule = PROPERTY_WINDING_RULE.get(atoms);
		byte[] segmentTypes = PROPERTY_SEGMENT_TYPES.get(atoms).data;
		float[] segmentCoordinates = PROPERTY_SEGMENT_COORDINATES
				.get(atoms).data;

		int segmentCoordIndex = 0;

		Path2D returnValue = new Path2D.Float(windingRule);
		for (int segmentIndex = 0; segmentIndex < segmentTypes.length; segmentIndex++) {
			if (segmentTypes[segmentIndex] == PathIterator.SEG_MOVETO) {
				returnValue.moveTo(segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++]);
			} else if (segmentTypes[segmentIndex] == PathIterator.SEG_LINETO) {
				returnValue.lineTo(segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++]);
			} else if (segmentTypes[segmentIndex] == PathIterator.SEG_QUADTO) {
				returnValue.quadTo(segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++]);
			} else if (segmentTypes[segmentIndex] == PathIterator.SEG_CUBICTO) {
				returnValue.curveTo(segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++],
						segmentCoordinates[segmentCoordIndex++]);
			} else if (segmentTypes[segmentIndex] == PathIterator.SEG_CLOSE) {
				returnValue.closePath();
			} else {
				throw new RuntimeException(
						"Illegal segment type: " + segmentTypes[segmentIndex]);
			}
		}

		return returnValue;
	}
}
