package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import com.pump.geom.Clipper;
import com.pump.geom.ShapeUtils;
import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for {@link Graphics2D#fill(Shape)}.
 * <p>
 * The shape is cloned.
 */
public class FillOperation extends ShapeOperation {
	private static final long serialVersionUID = 1L;

	public FillOperation(Graphics2DContext context, Shape shape) {
		super(context, shape);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		Shape shape = getShape();
		Rectangle rect = ShapeUtils.getRectangle(shape);
		if (rect != null) {
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			return;
		}
		Rectangle2D rect2D = ShapeUtils.getRectangle2D(shape);
		Shape localShape = rect2D == null ? getShape() : rect2D;
		g.fill(localShape);
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

	@Override
	public Shape getUnclippedOutline() {
		return getContext().getTransform().createTransformedShape(getShape());
	}

	@Override
	public Operation[] toSoftClipOperation(Shape clippingShape) {
		Graphics2DContext context = getContext();
		try {
			clippingShape = context.getTransform().createInverse()
					.createTransformedShape(clippingShape);
		} catch (NoninvertibleTransformException e) {
			return new Operation[] {};
		}

		Rectangle2D r1 = ShapeUtils.getRectangle2D(clippingShape);
		Rectangle2D r2 = ShapeUtils.getRectangle2D(getShape());
		Shape intersection;
		if (r1 != null && r2 != null) {
			intersection = r1.createIntersection(r2);
		} else if (r1 != null) {
			intersection = Clipper.clipToRect(getShape(), r1);
		} else if (r2 != null) {
			intersection = Clipper.clipToRect(clippingShape, r2);
		} else {
			Area a1 = new Area(ShapeUtils.flatten(getShape(), .01f));
			Area a2 = new Area(ShapeUtils.flatten(clippingShape, .01f));
			a1.intersect(a2);
			intersection = a1;
		}

		if (ShapeUtils.isEmpty(intersection))
			return new Operation[] {};

		context.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		return new Operation[] { new FillOperation(context, intersection) };
	}
}
