package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

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
}
