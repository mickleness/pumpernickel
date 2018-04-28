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
package com.pump.geom.area;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.NoSuchElementException;

import com.pump.geom.AreaX;
import com.pump.geom.EmptyPathException;
import com.pump.geom.Intersections;
import com.pump.geom.ShapeBounds;

/**
 * This is a list of <code>CurveX</code> segments that makes up the shape of an
 * <code>AreaX</code>.
 * <p>
 * This is based on the <code>Vector</code> named "curves" in the original
 * <code>java.awt.geom.Area</code> class. However this class has a few key
 * modifications:
 * <ul>
 * <li>The bounds are internally updated constantly.</li>
 * <li>The methods to add curves are protected. So while this list can be
 * iterated over via public methods, it is immutable to classes outside this
 * package. This is a deliberate choice to facilitate the
 * <code>AreaXRules</code> model.
 * <li>This object adds the concept of validation (more on this below).</li>
 * <li>This object is not synchronized. The <code>AreaX</code> class is, but
 * this class is not. This is for internal behind-the-scenes work: it is the
 * caller's responsibility to synchronize method calls safely.</li>
 * </ul>
 * <h3>Validation</h3>
 * This concept is new to the <code>AreaX</code> package, but the code is not
 * new. The original <code>Area</code> class automatically validated everything
 * all the time. Here I separated this process because -- as we start dealing
 * with complex <code>AreaXRules</code> -- we can get some performance
 * improvements if we don't validate all shapes.
 * <p>
 * A "valid" body is one that has been primed for geometric operations. An
 * "invalid" body is one that has not yet been primed -- it is a raw dump of
 * another shape.
 * <p>
 * In the end: all shapes that are involved in computing an add, xor, intersect
 * or subtract are validated. But we don't have to perform an expensive
 * validation in order to see whether Shape A contains Shape B.
 */
public class AreaXBody extends CurveList {

	private static AreaXBody emptyBody = new AreaXBody(null, 0);

	private Rectangle2D bounds = new Rectangle2D.Double();
	private final AreaOpX validationOperator;
	private AreaXBody validatedBody;

	/** Used only for the merge method. */
	private AreaXBody() {
		super(0);
		validationOperator = null;
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param validationOperator
	 *            if this body is not yet valid, this is the operation that will
	 *            validate it. If this is null then this body is considered
	 *            valid.
	 * @param initialCapacity
	 *            the initial capacity of the list.
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 */
	public AreaXBody(AreaOpX validationOperator, int initialCapacity) {
		super(initialCapacity);
		this.validationOperator = validationOperator;
	}

	public boolean contains(double x, double y) {
		if (bounds.contains(x, y) == false)
			return false;

		int crossings = 0;
		int size = size();
		for (int a = 0; a < size; a++) {
			CurveX c = elementData[a];
			crossings += c.crossingsFor(x, y);
		}
		return ((crossings & 1) == 1);
	}

	/**
	 * Studies this object (the LHS) and the argument (the RHS) to determine the
	 * relationship.
	 * 
	 * @param rhs
	 *            a shape to iterate over.
	 * @param at
	 *            an optional AffineTransform to apply to the rhs operand.
	 * @return one of the RELATIONSHIP constants.
	 */
	public int getRelationship(Shape rhs, AffineTransform at) {
		return getRelationship(null, rhs, at);
	}

	/**
	 * Studies this object (the LHS) and the argument (the RHS) to determine the
	 * relationship.
	 * 
	 * @param lhsTransform
	 *            an optional AffineTransform to apply to this operand.
	 * @param rhs
	 *            a shape to iterate over.
	 * @param at
	 *            an optional AffineTransform to apply to the rhs operand.
	 * @return one of the RELATIONSHIP constants.
	 */
	public int getRelationship(AffineTransform lhsTransform, Shape rhs,
			AffineTransform at) {
		// MAKE SURE ANY CHANGES YOU MAKE IN THIS METHOD YOU MAKE TO THE
		// OTHER GETRELATIONSHIP METHOD
		PathIterator rhsi = rhs.getPathIterator(at);
		Rectangle2D rhsBounds;
		if (rhs instanceof AreaXIterator) {
			AreaXIterator axi = (AreaXIterator) rhs;
			rhsBounds = axi.curves.bounds;
		}

		try {
			rhsBounds = ShapeBounds.getBounds(rhs, at);
			if (lhsTransform == null || lhsTransform.isIdentity()) {
				if (rhsBounds.intersects(bounds) == false)
					return AreaX.RELATIONSHIP_NONE;
			}
		} catch (EmptyPathException e) {
			return AreaX.RELATIONSHIP_NONE;
		}

		double[] coords = new double[6];
		int relationship = -1; // -1 = unknown
		double moveX = 0;
		double moveY = 0;
		double lastX = 0;
		double lastY = 0;
		boolean closed = false;
		AreaXIterator axi = new AreaXIterator();

		while (rhsi.isDone() == false) {
			int i = rhsi.currentSegment(coords);

			if (i == PathIterator.SEG_CLOSE) {
				if (lastX != moveX && lastY != moveY) {
					if (intersectsLine(lastX, lastY, moveX, moveY, axi,
							lhsTransform))
						return AreaX.RELATIONSHIP_COMPLEX;
				}
				closed = true;
			}
			if (i == PathIterator.SEG_MOVETO && lastX != moveX
					&& lastY != moveY && closed == false) {
				if (intersectsLine(lastX, lastY, moveX, moveY, axi,
						lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
			}

			if (i == PathIterator.SEG_MOVETO) {
				lastX = moveX = coords[0];
				lastY = moveY = coords[1];
				boolean contains = contains(moveX, moveY);
				int newRelationship = contains ? AreaX.RELATIONSHIP_LHS_CONTAINS
						: AreaX.RELATIONSHIP_NONE;
				if (relationship == -1) {
					relationship = newRelationship;
				} else if (relationship != newRelationship) {
					return AreaX.RELATIONSHIP_COMPLEX;
				}
				closed = false;
			} else if (i == PathIterator.SEG_LINETO) {
				if (intersectsLine(lastX, lastY, coords[0], coords[1], axi,
						lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
				lastX = coords[0];
				lastY = coords[1];
			} else if (i == PathIterator.SEG_QUADTO) {
				if (intersectsQuad(lastX, lastY, coords[0], coords[1],
						coords[2], coords[3], axi, lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
				lastX = coords[2];
				lastY = coords[3];
			} else if (i == PathIterator.SEG_CUBICTO) {
				if (intersectsCubic(lastX, lastY, coords[0], coords[1],
						coords[2], coords[3], coords[4], coords[5], axi,
						lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
				lastX = coords[4];
				lastY = coords[5];
			}
			rhsi.next();
		}

		if (moveX != lastX || moveY != lastY) {
			if (intersectsLine(lastX, lastY, moveX, moveY, axi, lhsTransform))
				return AreaX.RELATIONSHIP_COMPLEX;
		}

		if (relationship == AreaX.RELATIONSHIP_NONE) {
			// at this point we know the LHS doesn't contain the RHS, and that
			// they don't intersect.

			// this could be RHS_CONTAINS, too? Or Complex?
			if (elementCount == 0)
				return AreaX.RELATIONSHIP_NONE;

			relationship = -1; // start over
			for (int a = 0; a < elementCount; a++) {
				if (elementData[a] instanceof Order0X) {
					int newRelationship;
					if (rhs.contains(elementData[a].getX0(),
							elementData[a].getY0())) {
						newRelationship = AreaX.RELATIONSHIP_RHS_CONTAINS;
					} else {
						newRelationship = AreaX.RELATIONSHIP_NONE;
					}
					if (relationship == -1) {
						relationship = newRelationship;
					} else if (relationship != newRelationship) {
						return AreaX.RELATIONSHIP_COMPLEX;
					}
				}
			}
		}
		return relationship;
	}

	/**
	 * Studies this object (the LHS) and the argument (the RHS) to determine the
	 * relationship.
	 * 
	 * @param rhs
	 *            a shape to iterate over.
	 * @param at
	 *            an optional AffineTransform to apply to the rhs operand.
	 * @return one of the RELATIONSHIP constants.
	 */
	public int getRelationship(AreaXBody rhs, AffineTransform at) {
		return getRelationship(null, rhs, at);
	}

	/**
	 * Studies this object (the LHS) and the argument (the RHS) to determine the
	 * relationship.
	 * 
	 * @param lhsTransform
	 *            an optional AffineTransform to apply to this operand.
	 * @param rhs
	 *            a shape to iterate over.
	 * @param at
	 *            an optional AffineTransform to apply to the rhs operand.
	 * @return one of the RELATIONSHIP constants.
	 */
	public int getRelationship(AffineTransform lhsTransform, AreaXBody rhs,
			AffineTransform at) {
		// MAKE SURE ANY CHANGES YOU MAKE IN THIS METHOD YOU MAKE TO THE
		// OTHER GETRELATIONSHIP METHOD

		if (rhs.bounds.intersects(bounds) == false)
			return AreaX.RELATIONSHIP_NONE;

		PathIterator rhsi = rhs.getPathIterator(at);

		double[] coords = new double[6];
		int relationship = -1; // -1 = unknown
		double moveX = 0;
		double moveY = 0;
		double lastX = 0;
		double lastY = 0;
		boolean closed = false;
		AreaXIterator axi = new AreaXIterator();

		while (rhsi.isDone() == false) {
			int i = rhsi.currentSegment(coords);

			if (i == PathIterator.SEG_CLOSE) {
				if (lastX != moveX && lastY != moveY) {
					if (intersectsLine(lastX, lastY, moveX, moveY, axi,
							lhsTransform))
						return AreaX.RELATIONSHIP_COMPLEX;
				}
				closed = true;
			}
			if (i == PathIterator.SEG_MOVETO && lastX != moveX
					&& lastY != moveY && closed == false) {
				if (intersectsLine(lastX, lastY, moveX, moveY, axi,
						lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
			}

			if (i == PathIterator.SEG_MOVETO) {
				lastX = moveX = coords[0];
				lastY = moveY = coords[1];
				boolean contains = contains(moveX, moveY);
				int newRelationship = contains ? AreaX.RELATIONSHIP_LHS_CONTAINS
						: AreaX.RELATIONSHIP_NONE;
				if (relationship == -1) {
					relationship = newRelationship;
				} else if (relationship != newRelationship) {
					return AreaX.RELATIONSHIP_COMPLEX;
				}
				closed = false;
			} else if (i == PathIterator.SEG_LINETO) {
				if (intersectsLine(lastX, lastY, coords[0], coords[1], axi,
						lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
				lastX = coords[0];
				lastY = coords[1];
			} else if (i == PathIterator.SEG_QUADTO) {
				if (intersectsQuad(lastX, lastY, coords[0], coords[1],
						coords[2], coords[3], axi, lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
				lastX = coords[2];
				lastY = coords[3];
			} else if (i == PathIterator.SEG_CUBICTO) {
				if (intersectsCubic(lastX, lastY, coords[0], coords[1],
						coords[2], coords[3], coords[4], coords[5], axi,
						lhsTransform))
					return AreaX.RELATIONSHIP_COMPLEX;
				lastX = coords[4];
				lastY = coords[5];
			}
			rhsi.next();
		}

		if (moveX != lastX || moveY != lastY) {
			if (intersectsLine(lastX, lastY, moveX, moveY, axi, lhsTransform))
				return AreaX.RELATIONSHIP_COMPLEX;
		}

		if (relationship == AreaX.RELATIONSHIP_NONE) {
			// at this point we know the LHS doesn't contain the RHS, and that
			// they don't intersect.

			// this could be RHS_CONTAINS, too? Or Complex?
			if (elementCount == 0)
				return AreaX.RELATIONSHIP_NONE;

			relationship = -1; // start over
			for (int a = 0; a < elementCount; a++) {
				if (elementData[a] instanceof Order0X) {
					int newRelationship;
					if (rhs.contains(elementData[a].getX0(),
							elementData[a].getY0())) {
						newRelationship = AreaX.RELATIONSHIP_RHS_CONTAINS;
					} else {
						newRelationship = AreaX.RELATIONSHIP_NONE;
					}
					if (relationship == -1) {
						relationship = newRelationship;
					} else if (relationship != newRelationship) {
						return AreaX.RELATIONSHIP_COMPLEX;
					}
				}
			}
		}
		return relationship;
	}

	private boolean intersectsLine(double x0, double y0, double x1, double y1,
			AreaXIterator axi, AffineTransform axiTx) {
		axi.reset(this, axiTx);
		double lastX = 0;
		double lastY = 0;
		double moveX = 0;
		double moveY = 0;
		double[] coords = new double[6];
		while (axi.isDone() == false) {
			int k = axi.currentSegment(coords);
			if (k == PathIterator.SEG_CLOSE) {
				if (lastX != moveX || lastY != moveY) {
					k = PathIterator.SEG_LINETO;
					coords[0] = moveX;
					coords[1] = moveY;
				}
			}
			if (k == PathIterator.SEG_MOVETO) {
				moveX = lastX = coords[0];
				moveY = lastY = coords[1];
			} else if (k == PathIterator.SEG_LINETO) {
				if (Intersections.lineLine(x0, y0, x1, y1, lastX, lastY,
						coords[0], coords[1]))
					return true;
				lastX = coords[0];
				lastY = coords[1];
			} else if (k == PathIterator.SEG_QUADTO) {
				if (Intersections.lineQuad(x0, y0, x1, y1, lastX, lastY,
						coords[0], coords[1], coords[2], coords[3]))
					return true;
				lastX = coords[2];
				lastY = coords[3];
			} else if (k == PathIterator.SEG_CUBICTO) {
				if (Intersections.lineCubic(x0, y0, x1, y1, lastX, lastY,
						coords[0], coords[1], coords[2], coords[3], coords[4],
						coords[5]))
					return true;
				lastX = coords[4];
				lastY = coords[5];
			}
			axi.next();
		}
		return false;
	}

	private boolean intersectsQuad(double x0, double y0, double cx, double cy,
			double x1, double y1, AreaXIterator axi, AffineTransform axiTx) {
		axi.reset(this, axiTx);
		double lastX = 0;
		double lastY = 0;
		double moveX = 0;
		double moveY = 0;
		double[] coords = new double[6];
		while (axi.isDone() == false) {
			int k = axi.currentSegment(coords);
			if (k == PathIterator.SEG_CLOSE) {
				if (lastX != moveX || lastY != moveY) {
					k = PathIterator.SEG_LINETO;
					coords[0] = moveX;
					coords[1] = moveY;
				}
			}
			if (k == PathIterator.SEG_MOVETO) {
				moveX = lastX = coords[0];
				moveY = lastY = coords[1];
			} else if (k == PathIterator.SEG_LINETO) {
				if (Intersections.lineQuad(lastX, lastY, coords[0], coords[1],
						x0, y0, cx, cy, x1, y1))
					return true;
				lastX = coords[0];
				lastY = coords[1];
			} else if (k == PathIterator.SEG_QUADTO) {
				if (Intersections.quadQuad(x0, y0, cx, cy, x1, y1, lastX,
						lastY, coords[0], coords[1], coords[2], coords[3]))
					return true;
				lastX = coords[2];
				lastY = coords[3];
			} else if (k == PathIterator.SEG_CUBICTO) {
				if (Intersections.quadCubic(x0, y0, cx, cy, x1, y1, lastX,
						lastY, coords[0], coords[1], coords[2], coords[3],
						coords[4], coords[5]))
					return true;
				lastX = coords[4];
				lastY = coords[5];
			}
			axi.next();
		}
		return false;
	}

	private boolean intersectsCubic(double x0, double y0, double cx0,
			double cy0, double cx1, double cy1, double x1, double y1,
			AreaXIterator axi, AffineTransform axiTx) {
		axi.reset(this, axiTx);
		double lastX = 0;
		double lastY = 0;
		double moveX = 0;
		double moveY = 0;
		double[] coords = new double[6];
		while (axi.isDone() == false) {
			int k = axi.currentSegment(coords);
			if (k == PathIterator.SEG_CLOSE) {
				if (lastX != moveX || lastY != moveY) {
					k = PathIterator.SEG_LINETO;
					coords[0] = moveX;
					coords[1] = moveY;
				}
			}
			// ignore SEG_CLOSE instructions, because the AreaXIterator will
			// always
			// nicely end a subpath with a (redundant) lineTo instruction.
			if (k == PathIterator.SEG_MOVETO) {
				moveX = lastX = coords[0];
				moveY = lastY = coords[1];
			} else if (k == PathIterator.SEG_LINETO) {
				if (Intersections.lineCubic(lastX, lastY, coords[0], coords[1],
						x0, y0, cx0, cy0, cx1, cy1, x1, y1))
					return true;
				lastX = coords[0];
				lastY = coords[1];
			} else if (k == PathIterator.SEG_QUADTO) {
				if (Intersections.quadCubic(lastX, lastY, coords[0], coords[1],
						coords[2], coords[3], x0, y0, cx0, cy0, cx1, cy1, x1,
						y1))
					return true;
				lastX = coords[2];
				lastY = coords[3];
			} else if (k == PathIterator.SEG_CUBICTO) {
				if (Intersections.cubicCubic(x0, y0, cx0, cy0, cx1, cy1, x1,
						y1, lastX, lastY, coords[0], coords[1], coords[2],
						coords[3], coords[4], coords[5]))
					return true;
				lastX = coords[4];
				lastY = coords[5];
			}
			axi.next();
		}
		return false;
	}

	@Override
	protected void add(CurveX o) {
		if (size() == 0) {
			bounds.setFrame(o.getX0(), o.getY0(), 0, 0);
		}
		super.add(o);
		o.enlarge(bounds);
	}

	/**
	 * Returns whether this body is validated or not.
	 * <p>
	 * Validation is an expensive process of restructuring a primitive
	 * <code>AreaXBody</code> so it is primed for geometric operations.
	 */
	public boolean isValidated() {
		return validationOperator == null;
	}

	/**
	 * This returns a validated copy of this object. If this body is already
	 * validated, then this object is returned.
	 * <p>
	 * If this object is invalid but this method has already been performed,
	 * then the previously created object is returned.
	 * <p>
	 * Otherwise: this body is validated. This is expensive but required for
	 * other operations.
	 */
	public AreaXBody validate() {
		if (validationOperator == null)
			return this;
		if (validatedBody != null)
			return validatedBody;

		validatedBody = validationOperator.calculate(this, emptyBody);
		return validatedBody;
	}

	/** Only returns true if the argument is the same object as this object. */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj == this))
			return false;

		AreaXBody rhs = (AreaXBody) obj;
		AreaXBody lhs = this;

		lhs = lhs.validate();
		rhs = rhs.validate();

		if (lhs.bounds.equals(rhs.bounds) == false)
			return false;

		AreaXBody c = new AreaOpX.XorOp().calculate(lhs, rhs);
		return c.isEmpty();
	}

	/**
	 * Returns a hashcode based on the bounds of this body. This way a validated
	 * and unvalidated body will nearly always have the same hashcode.
	 */
	@Override
	public int hashCode() {
		int k = 0;
		k += Math.round(bounds.getX());
		k += Math.round(bounds.getY());
		k += Math.round(bounds.getWidth());
		k += Math.round(bounds.getHeight());
		return k;
	}

	public static AreaXBody create(PathIterator pi, boolean validate) {
		int windingRule = pi.getWindingRule();

		AreaOpX operator;
		if (windingRule == PathIterator.WIND_EVEN_ODD) {
			operator = new AreaOpX.EOWindOp();
		} else {
			operator = new AreaOpX.NZWindOp();
		}
		AreaXBody body = new AreaXBody(operator, 0);

		// coords array is big enough for holding:
		// coordinates returned from currentSegment (6)
		// OR
		// two subdivided quadratic curves (2+4+4=10)
		// AND
		// 0-1 horizontal splitting parameters
		// OR
		// 2 parametric equation derivative coefficients
		// OR
		// three subdivided cubic curves (2+6+6+6=20)
		// AND
		// 0-2 horizontal splitting parameters
		// OR
		// 3 parametric equation derivative coefficients
		double coords[] = new double[23];
		double movx = 0, movy = 0;
		double curx = 0, cury = 0;
		double newx, newy;
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				CurveX.insertLine(body, curx, cury, movx, movy);
				curx = movx = coords[0];
				cury = movy = coords[1];
				CurveX.insertMove(body, movx, movy);
				break;
			case PathIterator.SEG_LINETO:
				newx = coords[0];
				newy = coords[1];
				CurveX.insertLine(body, curx, cury, newx, newy);
				curx = newx;
				cury = newy;
				break;
			case PathIterator.SEG_QUADTO:
				newx = coords[2];
				newy = coords[3];
				CurveX.insertQuad(body, curx, cury, coords);
				curx = newx;
				cury = newy;
				break;
			case PathIterator.SEG_CUBICTO:
				newx = coords[4];
				newy = coords[5];
				CurveX.insertCubic(body, curx, cury, coords);
				curx = newx;
				cury = newy;
				break;
			case PathIterator.SEG_CLOSE:
				CurveX.insertLine(body, curx, cury, movx, movy);
				curx = movx;
				cury = movy;
				break;
			}
			pi.next();
		}
		CurveX.insertLine(body, curx, cury, movx, movy);

		if (validate)
			return body.validate();

		return body;
	}

	public Rectangle2D getBounds2D(Rectangle2D dest) {
		if (dest == null)
			dest = new Rectangle2D.Double();
		dest.setFrame(bounds);
		return dest;
	}

	public Rectangle getBounds(Rectangle dest) {
		if (dest == null)
			dest = new Rectangle();
		int x1 = (int) Math.floor(bounds.getMinX());
		int y1 = (int) Math.floor(bounds.getMinY());
		int x2 = (int) Math.ceil(bounds.getMaxX());
		int y2 = (int) Math.ceil(bounds.getMaxY());
		dest.setBounds(x1, y1, x2 - x1, y2 - y1);
		return dest;
	}

	public boolean boundsContains(double x, double y, double w, double h) {
		return bounds.contains(x, y, w, h);
	}

	public boolean boundsIntersects(double x, double y, double w, double h) {
		return bounds.intersects(x, y, w, h);
	}

	@Override
	public CurveX get(int index) {
		return super.get(index);
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public int size() {
		return super.size();
	}

	public AreaXBody transform(AffineTransform t) {
		if (Math.abs(t.getShearX()) < .0000001
				&& Math.abs(t.getShearY()) < .0000001) {
			int size = size();

			double scaleX = t.getScaleX();
			double scaleY = t.getScaleY();
			double translateX = t.getTranslateX();
			double translateY = t.getTranslateY();
			AreaXBody newBody = new AreaXBody(validationOperator, size);
			boolean failed = false;
			int increasing = scaleY > 0 ? CurveX.INCREASING : CurveX.DECREASING;
			for (int a = 0; failed == false && a < size; a++) {
				CurveX curve = get(a);
				CurveX newCurve;
				if (curve instanceof Order0X) {
					double x0 = scaleX * curve.getX0() + translateX;
					double y0 = scaleY * curve.getY0() + translateY;
					newCurve = new Order0X(x0, y0);
				} else if (curve instanceof Order1X) {
					double x0 = scaleX * curve.getX0() + translateX;
					double y0 = scaleY * curve.getY0() + translateY;
					double x1 = scaleX * curve.getX1() + translateX;
					double y1 = scaleY * curve.getY1() + translateY;
					if (curve.getDirection() == increasing) {
						newCurve = new Order1X(x0, y0, x1, y1,
								curve.getDirection());
					} else {
						newCurve = new Order1X(x1, y1, x0, y0,
								curve.getDirection());
					}
				} else if (curve instanceof Order2X) {
					Order2X quad = (Order2X) curve;
					double x0 = scaleX * curve.getX0() + translateX;
					double y0 = scaleY * curve.getY0() + translateY;
					double cx0 = scaleX * quad.getCX0() + translateX;
					double cy0 = scaleY * quad.getCY0() + translateY;
					double x1 = scaleX * curve.getX1() + translateX;
					double y1 = scaleY * curve.getY1() + translateY;
					if (curve.getDirection() == increasing) {
						newCurve = new Order2X(x0, y0, cx0, cy0, x1, y1,
								curve.getDirection());
					} else {
						newCurve = new Order2X(x1, y1, cx0, cy0, x0, y0,
								curve.getDirection());
					}
				} else { // if(curve instanceof Order3X) {
					Order3X cubic = (Order3X) curve;
					double x0 = scaleX * curve.getX0() + translateX;
					double y0 = scaleY * curve.getY0() + translateY;
					double cx0 = scaleX * cubic.getCX0() + translateX;
					double cy0 = scaleY * cubic.getCY0() + translateY;
					double cx1 = scaleX * cubic.getCX1() + translateX;
					double cy1 = scaleY * cubic.getCY1() + translateY;
					double x1 = scaleX * curve.getX1() + translateX;
					double y1 = scaleY * curve.getY1() + translateY;
					if (curve.getDirection() == increasing) {
						newCurve = new Order3X(x0, y0, cx0, cy0, cx1, cy1, x1,
								y1, curve.getDirection());
					} else {
						newCurve = new Order3X(x1, y1, cx1, cy1, cx0, cy0, x0,
								y0, curve.getDirection());
					}
				}
				if ((newCurve.getY0() == newCurve.getY1()) != (curve.getY0() == curve
						.getY1())) {
					/**
					 * This can happen as a rare result of computer error when
					 * multiplying.
					 * 
					 * Unfortunately this condition will lead to an
					 * InternalError in subsequent AreaOpX calls, so we need to
					 * abandon ship here.
					 */
					failed = true;
				}
				newBody.add(newCurve);
			}
			if (!failed) {
				return newBody;
			}
		}
		return AreaXBody.create(getPathIterator(t), true);
	}

	/**
	 * Creates a {@link PathIterator} for the outline of this <code>AreaX</code>
	 * data.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the
	 *            coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @return the <code>PathIterator</code> object that returns the geometry of
	 *         the outline of this <code>AreaX</code>, one segment at a time.
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new AreaXIterator(this, at);
	}

	/**
	 * Calculates the addition of the specified <code>AreaXBody</code> with this
	 * <code>AreaXBody</code>. The resulting shape will include the union of
	 * both shapes, or all areas that were contained in either this or the
	 * specified <code>AreaX</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.add(a2);
	 * 
	 *        a1(before)     +         a2         =     a1(after)
	 * 
	 *     ################     ################     ################
	 *     ##############         ##############     ################
	 *     ############             ############     ################
	 *     ##########                 ##########     ################
	 *     ########                     ########     ################
	 *     ######                         ######     ######    ######
	 *     ####                             ####     ####        ####
	 *     ##                                 ##     ##            ##
	 * </pre>
	 * 
	 * @param rhs
	 *            the <code>AreaXBody</code> to be added to the this body.
	 * @return the new <code>AreaXBody</code> of this operation. This object and
	 *         the operator are not modified.
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public AreaXBody add(AreaXBody rhs) {
		if (this.isEmpty())
			return rhs;
		if (rhs.isEmpty())
			return this;
		return new AreaOpX.AddOp().calculate(this, rhs);
	}

	/**
	 * This simply merges two bodies together.
	 * <p>
	 * WARNING: use this only when it is guaranteed that the LHS and RHS do not
	 * overlap in any way. This is much more efficient than calling
	 * <code>add()</code>, but it will be very dangerous if misused.
	 */
	public AreaXBody merge(AreaXBody rhs) {
		if (this.isEmpty())
			return rhs;
		if (rhs.isEmpty())
			return this;

		AreaXBody newBody = new AreaXBody();

		AreaXBody lhs = this.validate();
		rhs = rhs.validate();

		int lhSize = lhs.size();
		int rhSize = rhs.size();
		newBody.ensureCapacity(lhSize + rhSize);
		for (int a = 0; a < lhSize; a++) {
			newBody.add(lhs.elementData[a]);
		}
		for (int a = 0; a < rhSize; a++) {
			newBody.add(rhs.elementData[a]);
		}
		return newBody;
	}

	/**
	 * This simply merges multiple bodies together.
	 * <p>
	 * WARNING: use this only when it is guaranteed that the LHS and RHS do not
	 * overlap in any way. This is much more efficient than calling
	 * <code>add()</code>, but it will be very dangerous if misused.
	 */
	public AreaXBody merge(List<Object> rhs, int startIndex, int endIndex) {
		AreaXBody newBody = new AreaXBody();

		AreaXBody lhs = this.validate();

		int lhSize = lhs.size();
		int rhSize = 0;
		for (int a = startIndex; a <= endIndex; a++) {
			AreaXBody t = (AreaXBody) rhs.get(a);
			AreaXBody t2 = t.validate();
			rhSize += t2.elementCount;
			if (t2 != t) {
				rhs.set(a, t2);
			}
		}
		newBody.ensureCapacity(lhSize + rhSize);
		for (int a = 0; a < lhSize; a++) {
			newBody.add(lhs.elementData[a]);
		}
		for (int a = startIndex; a <= endIndex; a++) {
			AreaXBody t = (AreaXBody) rhs.get(a);
			for (int b = 0; b < t.elementCount; b++) {
				newBody.add(t.elementData[b]);
			}
		}
		return newBody;
	}

	/**
	 * Subtracts the shape of the specified <code>AreaXBody</code> from the
	 * shape of this <code>AreaXBody</code>. The resulting
	 * <code>AreaXBody</code> will include areas that were contained only in
	 * this <code>AreaXBody</code> and not in the argument
	 * <code>AreaXBody</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.subtract(a2);
	 * 
	 *        a1(before)     -         a2         =     a1(after)
	 * 
	 *     ################     ################
	 *     ##############         ##############     ##
	 *     ############             ############     ####
	 *     ##########                 ##########     ######
	 *     ########                     ########     ########
	 *     ######                         ######     ######
	 *     ####                             ####     ####
	 *     ##                                 ##     ##
	 * </pre>
	 * 
	 * @param rhs
	 *            the <code>AreaXBody</code> to be subtracted from the current
	 *            shape
	 * @return the new <code>AreaXBody</code> of this operation. This object and
	 *         the operator are not modified.
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public AreaXBody subtract(AreaXBody rhs) {
		if (this.isEmpty())
			return this;
		if (rhs.isEmpty())
			return this;
		return new AreaOpX.SubOp().calculate(this, rhs);
	}

	/**
	 * Calculate the <code>AreaXBody</code> of the intersection of its current
	 * shape and the shape of the specified <code>AreaXBody</code>. The
	 * resulting shape will include only areas that were contained in both this
	 * <code>AreaXBody</code> and also in the specified <code>AreaXBody</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.intersect(a2);
	 * 
	 *      a1(before)   intersect     a2         =     a1(after)
	 * 
	 *     ################     ################     ################
	 *     ##############         ##############       ############
	 *     ############             ############         ########
	 *     ##########                 ##########           ####
	 *     ########                     ########
	 *     ######                         ######
	 *     ####                             ####
	 *     ##                                 ##
	 * </pre>
	 * 
	 * @param rhs
	 *            the <code>AreaXBody</code> to be intersected with this
	 *            <code>AreaXBody</code>
	 * @return the new <code>AreaXBody</code> of this operation. This object and
	 *         the operator are not modified.
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public AreaXBody intersect(AreaXBody rhs) {
		if (this.isEmpty())
			return this;
		if (rhs.isEmpty())
			return rhs;
		return new AreaOpX.IntOp().calculate(this, rhs);
	}

	/**
	 * The resulting shape of this operation will include only areas that were
	 * contained in either this <code>AreaXBody</code> or in the specified
	 * <code>AreaXBody</code>, but not in both.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.exclusiveOr(a2);
	 * 
	 *        a1(before)    xor        a2         =     a1(after)
	 * 
	 *     ################     ################
	 *     ##############         ##############     ##            ##
	 *     ############             ############     ####        ####
	 *     ##########                 ##########     ######    ######
	 *     ########                     ########     ################
	 *     ######                         ######     ######    ######
	 *     ####                             ####     ####        ####
	 *     ##                                 ##     ##            ##
	 * </pre>
	 * <p>
	 * This call immediately executes. It executes any pending operations first,
	 * and then executes this add.
	 * 
	 * @param rhs
	 *            the <code>AreaXBody</code> to be exclusive ORed with this
	 *            <code>AreaXBody</code>.
	 * @return the new <code>AreaXBody</code> of this operation. This object and
	 *         the operator are not modified.
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public AreaXBody xor(AreaXBody rhs) {
		if (this.isEmpty())
			return rhs;
		if (rhs.isEmpty())
			return this;
		return new AreaOpX.XorOp().calculate(this, rhs);
	}

}

class AreaXIterator implements PathIterator {
	private AffineTransform transform;
	protected AreaXBody curves;
	private int index;
	private CurveX prevcurve;
	private CurveX curve;

	/**
	 * This creates an empty iterator. This is generally useless unless you call
	 * <code>reset()</code> to define a body to iterate over.
	 */
	protected AreaXIterator() {
	}

	/**
	 * Creates a <code>AreaXIterator</code> for a given <code>AreaXBody</code>.
	 * 
	 */
	public AreaXIterator(AreaXBody curves, AffineTransform at) {
		reset(curves, at);
	}

	protected void reset(AreaXBody curves, AffineTransform at) {
		this.curves = curves;
		this.transform = at;
		if (curves.size() >= 1) {
			curve = curves.get(0);
		} else {
			curve = null;
		}
		prevcurve = null;
		index = 0;
	}

	public int getWindingRule() {
		// REMIND: Which is better, EVEN_ODD or NON_ZERO?
		// The paths calculated could be classified either way.
		// return WIND_EVEN_ODD;
		return WIND_NON_ZERO;
	}

	public boolean isDone() {
		return (prevcurve == null && curve == null);
	}

	public void next() {
		if (prevcurve != null) {
			prevcurve = null;
		} else {
			prevcurve = curve;
			index++;
			if (index < curves.size()) {
				curve = curves.get(index);
				if (curve.getOrder() != 0 && prevcurve.getX1() == curve.getX0()
						&& prevcurve.getY1() == curve.getY0()) {
					prevcurve = null;
				}
			} else {
				curve = null;
			}
		}
	}

	public int currentSegment(float coords[]) {
		double dcoords[] = new double[6];
		int segtype = currentSegment(dcoords);
		int numpoints = (segtype == SEG_CLOSE ? 0 : (segtype == SEG_QUADTO ? 2
				: (segtype == SEG_CUBICTO ? 3 : 1)));
		for (int i = 0; i < numpoints * 2; i++) {
			coords[i] = (float) dcoords[i];
		}
		return segtype;
	}

	public int currentSegment(double coords[]) {
		int segtype;
		int numpoints;
		if (prevcurve != null) {
			// Need to finish off junction between curves
			if (curve == null || curve.getOrder() == 0) {
				return SEG_CLOSE;
			}
			coords[0] = curve.getX0();
			coords[1] = curve.getY0();
			segtype = SEG_LINETO;
			numpoints = 1;
		} else if (curve == null) {
			throw new NoSuchElementException("area iterator out of bounds");
		} else {
			segtype = curve.getSegment(coords);
			numpoints = curve.getOrder();
			if (numpoints == 0) {
				numpoints = 1;
			}
		}
		if (transform != null) {
			transform.transform(coords, 0, coords, 0, numpoints);
		}
		return segtype;
	}
}