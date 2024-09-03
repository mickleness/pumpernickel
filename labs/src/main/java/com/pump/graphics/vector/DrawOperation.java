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
package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import com.pump.geom.ShapeUtils;
import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for {@link Graphics2D#draw(Shape)}.
 * <p>
 * The shape is cloned.
 */
public class DrawOperation extends ShapeOperation {
	private static final long serialVersionUID = 1L;

	public DrawOperation(Graphics2DContext context, Shape shape) {
		super(context, shape);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		Shape shape = getShape();
		int[] line = getLine(shape);
		if (line != null) {
			// the unit tests show a very subtle difference between drawing a
			// Line2D vs a call to drawLine(..)
			g.drawLine(line[0], line[1], line[2], line[3]);
			return;
		}
		Rectangle rect = ShapeUtils.getRectangle(shape);
		if (rect != null) {
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			return;
		}
		Rectangle2D rect2D = ShapeUtils.getRectangle2D(shape);
		Shape localShape = rect2D == null ? getShape() : rect2D;
		g.draw(localShape);
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			// do nothing
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

	private static int[] getLine(Shape shape) {
		PathIterator pi = shape.getPathIterator(null);
		float[] coords = new float[6];
		int[] returnValue = new int[4];
		int stage = 0;
		while (!pi.isDone()) {
			int k = pi.currentSegment(coords);
			if (k == PathIterator.SEG_MOVETO && stage == 0) {
				returnValue[0] = (int) (coords[0] + .5);
				returnValue[1] = (int) (coords[1] + .5);
				if (Math.abs(coords[0] - returnValue[0]) > .00000001)
					return null;
				if (Math.abs(coords[1] - returnValue[1]) > .00000001)
					return null;
				stage = 1;
			} else if (k == PathIterator.SEG_LINETO && stage == 1) {
				returnValue[2] = (int) (coords[0] + .5);
				returnValue[3] = (int) (coords[1] + .5);
				if (Math.abs(coords[0] - returnValue[2]) > .00000001)
					return null;
				if (Math.abs(coords[1] - returnValue[3]) > .00000001)
					return null;
				stage = 2;
			} else {
				return null;
			}
			pi.next();
		}
		if (stage == 2)
			return returnValue;
		return null;
	}

	@Override
	public Shape getUnclippedOutline() {
		Shape strokedShape = getContext().getStroke()
				.createStrokedShape(getShape());
		AffineTransform tx = getContext().getTransform();
		return tx.createTransformedShape(strokedShape);
	}

	@Override
	public Operation[] toSoftClipOperation(Shape clippingShape) {
		FillOperation fillOp = toFillOperation();
		return fillOp.toSoftClipOperation(clippingShape);
	}

	/**
	 * Convert this DrawOperation to a FillOperation by invoking
	 * {@link java.awt.Stroke#createStrokedShape(Shape)}.
	 * <p>
	 * The resulting FillOperation should render identically to this
	 * DrawOperation if the stroke rendering hint is set to PURE. If the stroke
	 * rendering hint for this DrawOperation is not PURE: the graphics pipeline
	 * may introduce subtle differences (because it optimizes certain common
	 * strokes).
	 */
	public FillOperation toFillOperation() {
		Graphics2DContext context = getContext();
		Shape strokedShape = context.getStroke().createStrokedShape(getShape());
		return new FillOperation(context, strokedShape);
	}
}