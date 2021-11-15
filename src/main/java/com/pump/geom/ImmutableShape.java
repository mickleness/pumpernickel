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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * A simple wrapper for a shape that makes it immutable and serializable.
 * <p>
 * If we simply stored data as a Path2D or GeneralPath then other entities may
 * invoke <code>reset()</code>, <code>transform(..)</code> or other methods to
 * modify the shape data.
 * <p>
 * This is similar (or identical?) to <code>sun.font.DelegateShape</code>.
 */
public class ImmutableShape implements Shape, Serializable {
	private static final long serialVersionUID = 1L;

	protected Shape shape;

	public ImmutableShape(Shape shape) {
		Objects.requireNonNull(shape);
		this.shape = shape;
	}

	@Override
	public Rectangle getBounds() {
		return shape.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return shape.getBounds2D();
	}

	@Override
	public boolean contains(double x, double y) {
		return shape.contains(x, y);
	}

	@Override
	public boolean contains(Point2D p) {
		return shape.contains(p);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return shape.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return shape.intersects(r);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return shape.contains(x, y, w, h);
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return shape.contains(r);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return shape.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return shape.getPathIterator(at, flatness);
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);

		if (shape instanceof Serializable) {
			out.writeObject(shape);
		} else {
			PathIterator pi = shape.getPathIterator(null);
			String str = ShapeStringUtils.toString(pi);
			out.writeObject(str);
			out.writeInt(pi.getWindingRule());
		}

	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int internalVersion = in.readInt();
		if (internalVersion == 0) {
			Object v = in.readObject();
			if (v instanceof Shape) {
				shape = (Shape) v;
			} else {
				String str = (String) v;
				PathIterator pi = ShapeStringUtils.createPathIterator(str);
				int windingRule = in.readInt();
				Path2D p = new Path2D.Float(windingRule);
				p.append(pi, false);
				shape = p;
			}
		} else {
			throw new IOException(
					"Unsupported internal version: " + internalVersion);
		}
	}

}