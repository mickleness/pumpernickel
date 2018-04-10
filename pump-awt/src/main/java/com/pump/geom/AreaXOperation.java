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
package com.pump.geom;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * An unperformed operation to an <code>AreaX</code>.
 */
public class AreaXOperation {
	public static final int ADD = 0;
	public static final int SUBTRACT = 1;
	public static final int XOR = 2;
	public static final int INTERSECT = 3;

	protected final Shape operand;
	protected final int operator;

	Rectangle2D bounds;

	public AreaXOperation(Shape shape, int operator) {
		if (!(operator == ADD || operator == SUBTRACT || operator == XOR || operator == INTERSECT))
			throw new IllegalArgumentException("unrecognized operator ("
					+ operator + ")");
		if (shape == null)
			throw new NullPointerException();

		this.operand = shape;
		this.operator = operator;
	}

	public int getOperator() {
		return operator;
	}

	public Shape getOperand() {
		return operand;
	}

	public Rectangle2D getBounds() {
		if (bounds == null) {
			if (operand instanceof AreaX || operand instanceof Area) {
				bounds = operand.getBounds2D();
			} else {
				bounds = ShapeBounds.getBounds(operand);
			}
		}
		return bounds;
	}
}