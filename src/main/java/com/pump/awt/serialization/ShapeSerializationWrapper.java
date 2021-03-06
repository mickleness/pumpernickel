package com.pump.awt.serialization;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import com.pump.geom.ShapeStringUtils;
import com.pump.geom.ShapeUtils;
import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for Shapes.
 */
public class ShapeSerializationWrapper
		extends AbstractSerializationWrapper<Shape> {
	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts a Shape into a ShapeSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof Shape) {
				Shape s = (Shape) object;
				return new ShapeSerializationWrapper(s);
			}
			return null;
		}
	};

	protected static final String KEY_PATH = "path";
	protected static final String KEY_WINDING_RULE = "windingRule";

	public ShapeSerializationWrapper(Shape shape) {
		map.put(KEY_PATH, ShapeStringUtils.toString(shape));
		map.put(KEY_WINDING_RULE, shape.getPathIterator(null).getWindingRule());

		// the create() method may convert a shape to a Rectangle/Rectangle2D,
		// which can change the order the PathIterators walks over points.
		// To serialize/deserialize the exact same order with no discrepancy:
		// we'll call create() and then save THAT shape. (This is required to
		// pass a VectorGraphics2DTest.)
		shape = create();
		map.put(KEY_PATH, ShapeStringUtils.toString(shape));
		map.put(KEY_WINDING_RULE, shape.getPathIterator(null).getWindingRule());
	}

	@Override
	public Shape create() {
		int windingRule = ((Number) map.get(KEY_WINDING_RULE)).intValue();
		String pathStr = (String) map.get(KEY_PATH);
		Path2D returnValue = new Path2D.Double(windingRule);
		returnValue.append(ShapeStringUtils.createPathIterator(pathStr), false);

		// offer more targeted classes if possible:

		Rectangle rect = ShapeUtils.getRectangle(returnValue);
		if (rect != null)
			return rect;
		Rectangle2D rect2D = ShapeUtils.getRectangle2D(returnValue);
		if (rect2D != null)
			return rect2D;

		return returnValue;
	}
}
