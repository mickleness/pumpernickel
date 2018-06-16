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
import java.awt.geom.CubicCurve2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * This is a class that implements all <code>Shape</code> methods except for
 * <code>getPathIterator(AffineTransform)</code>. Because all the other methods
 * consult <code>getPathIterator()</code> it is essential that when subclasses
 * implement that method it be very light. See <code>MutablePath</code> as a
 * possible example.
 */
public abstract class AbstractShape implements Shape {

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public boolean contains(Point2D p, AffineTransform transform) {
		return contains(p.getX(), p.getY(), transform);
	}

	public boolean contains(Rectangle2D r, AffineTransform transform) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight(),
				transform);
	}

	public abstract int getWindingRule();

	@Override
	public boolean contains(double x, double y) {
		return contains(x, y, null);
	}

	public boolean contains(double x, double y, AffineTransform transform) {
		int windingRule = getWindingRule();
		int crossings = countCrossings(x, y, transform);

		if (windingRule == PathIterator.WIND_EVEN_ODD)
			return ((crossings & 1) != 0);
		return crossings != 0;
	}

	/**
	 * This counts the number of crossings from (-infinity,y) to (x,y). This
	 * total is used in combination with the winding rule to determine when a
	 * point is inside a shape.
	 */
	protected int countCrossings(double x, double y, AffineTransform transform) {
		// draw an imaginary line from (-infinity,y) to (x,y)
		// and count how many crossings we have

		int crossings = 0;
		double lastX = 0;
		double lastY = 0;

		double t2;

		double ay, ax, by, cy, bx, cx, dx, dy, det;
		double myX;
		int i;
		double curvature;

		PathIterator iter = getPathIterator(transform);

		double[] array = new double[4];
		double[] results = new double[4];
		/** The current segment data. */
		double[] data = new double[6];

		/** The last moveTo x-coordinate */
		double moveX = 0;
		/** The last moveTo y-coordinate */
		double moveY = 0;
		/** Are we inside the vertical bounds of the current segment? */
		boolean inside;
		/** The segment type of the current segment. */
		int type;

		while (iter.isDone() == false) {
			type = iter.currentSegment(data);
			inside = false;

			if (type == PathIterator.SEG_CLOSE) {
				// pretend there's a line back to the starting point:
				type = PathIterator.SEG_LINETO;
				data[0] = moveX;
				data[1] = moveY;
			}
			if (type == PathIterator.SEG_MOVETO) {
				moveX = data[0];
				moveY = data[1];
				lastX = data[0];
				lastY = data[1];
			} else {
				// constrain our search:
				if (type == PathIterator.SEG_LINETO) {
					inside = y >= Math.min(data[1], lastY)
							&& y <= Math.max(data[1], lastY);
					if (inside) {
						if (lastY > data[1]) {
							t2 = (y - data[1]) / (lastY - data[1]);
							myX = data[0] + t2 * (lastX - data[0]);
							if (myX < x) {
								crossings--;
							}
						} else {
							t2 = (y - lastY) / (data[1] - lastY);
							myX = lastX + t2 * (data[0] - lastX);
							if (myX < x) {
								crossings++;
							}
						}
					}
					lastX = data[0];
					lastY = data[1];
				} else if (type == PathIterator.SEG_QUADTO) {

					ay = lastY - 2 * data[1] + data[3];
					by = -2 * lastY + 2 * data[1];
					cy = lastY - y;

					// this is a partial test to see if we're inside
					double minY = Math.min(data[3], lastY);
					double maxY = Math.max(data[3], lastY);
					inside = y >= minY && y <= maxY;
					if (!inside) {
						// not inside yet? Then let's do the slightly harder
						// test:
						double focusT = -by / (2 * ay);
						if (focusT >= 0 && focusT <= 1) {
							double focusY = ay * focusT * focusT + by * focusT
									+ cy;
							minY = Math.min(minY, focusY);
							maxY = Math.max(maxY, focusY);
							inside = y >= minY && y <= maxY;
						}
					}
					if (inside) {
						det = by * by - 4 * ay * cy;
						if (det <= 0) {
							// do nothing; 1 1-solution parabola won't have
							// crossings
							i = 0;
						} else {
							det = Math.sqrt(det);
							i = 2;
							array[0] = (-by + det) / (2 * ay);
							array[1] = (-by - det) / (2 * ay);
						}

						ax = lastX - 2 * data[0] + data[2];
						bx = -2 * lastX + 2 * data[0];
						cx = lastX;

						for (int a = 0; a < i; a++) {
							if (array[a] >= 0 && array[a] <= 1) {
								myX = ((ax * array[a] + bx) * array[a] + cx);
								if (myX < x) {
									curvature = (2 * ay * array[a] + by);
									if (curvature > 0) {
										crossings++;
									} else if (curvature < 0) {
										crossings--;
									}
								}
							}
						}
					}
					lastX = data[2];
					lastY = data[3];
				} else if (type == PathIterator.SEG_CUBICTO) {
					ay = -lastY + 3 * data[1] - 3 * data[3] + data[5];
					by = 3 * lastY - 6 * data[1] + 3 * data[3];
					cy = -3 * lastY + 3 * data[1];
					dy = lastY - y;

					double minY = Math.min(data[5], lastY);
					double maxY = Math.max(data[5], lastY);
					inside = y >= minY && y <= maxY;
					if (!inside) {
						// not inside yet? Then let's do the harder test:
						// take the derivative: 3*ay*t*t+2*by*t+cy
						// now the determinant of the derivative:
						det = (4 * by * by - 12 * ay * cy);
						if (det < 0) {
							// there are no solutions! nothing to do here
						} else if (det == 0) {
							// there is 1 solution
							double t = -2 * by / (6 * ay);
							if (t > 0 && t < 1) {
								double newY = ay * t * t * t + by * t * t + cy
										* t + dy;
								minY = Math.min(minY, newY);
								maxY = Math.max(maxY, newY);
							}
						} else {
							// there are 2 solutions:
							det = (float) Math.sqrt(det);
							double t = (-2 * by + det) / (6 * ay);
							if (t > 0 && t < 1) {
								double newY = ay * t * t * t + by * t * t + cy
										* t + dy;
								minY = Math.min(minY, newY);
								maxY = Math.max(maxY, newY);
							}

							t = (-2 * by - det) / (6 * ay);
							if (t > 0 && t < 1) {
								double newY = ay * t * t * t + by * t * t + cy
										* t + dy;
								minY = Math.min(minY, newY);
								maxY = Math.max(maxY, newY);
							}
						}
						inside = y >= minY && y <= maxY;
					}

					if (inside) {
						array[3] = ay;
						array[2] = by;
						array[1] = cy;
						array[0] = dy;
						i = CubicCurve2D.solveCubic(array, results);

						ax = -lastX + 3 * data[0] - 3 * data[2] + data[4];
						bx = 3 * lastX - 6 * data[0] + 3 * data[2];
						cx = -3 * lastX + 3 * data[0];
						dx = lastX;

						for (int a = 0; a < i; a++) {
							if (results[a] >= 0 && results[a] <= 1) {
								myX = (((ax * results[a] + bx) * results[a] + cx)
										* results[a] + dx);
								if (myX < x) {
									curvature = ((3 * ay * results[a] + 2 * by)
											* results[a] + cy);

									if (curvature > 0) {
										crossings++;
									} else if (curvature < 0) {
										crossings--;
									}
								}
							}
						}
					}
					lastX = data[4];
					lastY = data[5];
				}
			}
			iter.next();
		}
		return crossings;
	}

	/**
	 * This checks to see if the rectangle argument ever crosses this shape.
	 * This method returns immediately when/if it finds proof of an
	 * intersection.
	 * 
	 */
	protected boolean identifyCrossings(double x, double y, double w, double h,
			AffineTransform transform) {

		/**
		 * We're going to look at each segment and see if it intersects any of
		 * the 4 sides of the argument rectangle.
		 */

		double x1;
		double x2;
		double y1;
		double y2;
		double lastX = 0;
		double lastY = 0;

		lastX = 0;
		lastY = 0;

		x1 = x;
		x2 = (x + w);
		y1 = y;
		y2 = (y + h);

		double y1e = y - .0001f;
		double y2e = (y + h) + .0001f;

		double t2;
		int i;

		double[] eqn = new double[4];
		double[] data = new double[6];
		double[] results = new double[3];
		double[] array = new double[12];
		PathIterator iter = getPathIterator(transform);
		int i2;
		double ay, ax, by, cy, dy, bx, cx, dx, det;
		double myX, myY;
		int state = -1;
		int myState;
		double moveX = 0;
		double moveY = 0;

		while (iter.isDone() == false) {
			int type = iter.currentSegment(data);
			if (type == PathIterator.SEG_CLOSE) {
				type = PathIterator.SEG_LINETO;
				data[0] = moveX;
				data[1] = moveY;
			} else if (type == PathIterator.SEG_MOVETO) {
				moveX = data[0];
				moveY = data[1];
				lastX = data[0];
				lastY = data[1];
			}

			if (type == PathIterator.SEG_LINETO) {
				ay = data[1] - lastY;
				ax = data[0] - lastX;

				// look at horizontal lines:
				if (ay != 0) {
					// top top line
					t2 = (y1 - lastY) / ay;
					if (t2 > 0 && t2 < 1) {
						x = ax * t2 + lastX;
						if (x1 < x && x < x2) {
							return true;
						}
					}

					// the bottom line:
					t2 = (y2 - lastY) / ay;
					if (t2 > 0 && t2 < 1) {
						x = ax * t2 + lastX;
						if (x1 < x && x < x2) {
							return true;
						}
					}
				}

				// look at vertical lines:
				if (ax != 0) {
					// left line:
					t2 = (x1 - lastX) / ax;
					if (t2 > 0 && t2 < 1) {
						y = ay * t2 + lastY;
						if (y1 < y && y < y2) {
							return true;
						}
					}

					// the right line:
					t2 = (x2 - lastX) / ax;
					if (t2 > 0 && t2 < 1) {
						y = ay * t2 + lastY;
						if (y1 < y && y < y2) {
							return true;
						}
					}
				}
				lastX = data[0];
				lastY = data[1];
			} else if (type == PathIterator.SEG_QUADTO) {
				ax = lastX - 2 * data[0] + data[2];
				bx = -2 * lastX + 2 * data[0];
				cx = lastX;

				ay = lastY - 2 * data[1] + data[3];
				by = -2 * lastY + 2 * data[1];
				cy = lastY;

				det = by * by - 4 * ay * (cy - y1);
				if (det > 0) {
					// if det < 0 we have no matched
					// if det == 0, the parabola just TOUCHES
					// on the boundary, and isn't grounds to return true

					det = Math.sqrt(det);

					// root #1:
					t2 = (-by + det) / (2 * ay);
					if (t2 > 0 && t2 < 1) {
						x = (ax * t2 + bx) * t2 + cx;
						if (x1 < x && x < x2) {
							return true;
						}
					}
					// root #2:
					t2 = (-by - det) / (2 * ay);
					if (t2 > 0 && t2 < 1) {
						x = (ax * t2 + bx) * t2 + cx;
						if (x1 < x && x < x2) {
							return true;
						}
					}
				}

				det = by * by - 4 * ay * (cy - y2);
				if (det > 0) {
					det = Math.sqrt(det);

					// root #1:
					t2 = (-by + det) / (2 * ay);
					if (t2 > 0 && t2 < 1) {
						x = (ax * t2 + bx) * t2 + cx;
						if (x1 < x && x < x2) {
							return true;
						}
					}
					// root #2:
					t2 = (-by - det) / (2 * ay);
					if (t2 > 0 && t2 < 1) {
						x = (ax * t2 + bx) * t2 + cx;
						if (x1 < x && x < x2) {
							return true;
						}
					}
				}

				// now the vertical lines:
				det = bx * bx - 4 * ax * (cx - x1);
				if (det > 0) {
					det = Math.sqrt(det);

					// root #1:
					t2 = (-bx + det) / (2 * ax);
					if (t2 > 0 && t2 < 1) {
						y = (ay * t2 + by) * t2 + cy;
						if (y1 < y && y < y2) {
							return true;
						}
					}
					// root #2:
					t2 = (-bx - det) / (2 * ax);
					if (t2 > 0 && t2 < 1) {
						y = (ay * t2 + by) * t2 + cy;
						if (y1 < y && y < y2) {
							return true;
						}
					}
				}

				det = bx * bx - 4 * ax * (cx - x2);
				if (det > 0) {
					det = Math.sqrt(det);

					// root #1:
					t2 = (-bx + det) / (2 * ax);
					if (t2 > 0 && t2 < 1) {
						y = (ay * t2 + by) * t2 + cy;
						if (y1 < y && y < y2) {
							return true;
						}
					}
					// root #2:
					t2 = (-bx - det) / (2 * ax);
					if (t2 > 0 && t2 < 1) {
						y = (ay * t2 + by) * t2 + cy;
						if (y1 < y && y < y2) {
							return true;
						}
					}
				}

				lastX = data[2];
				lastY = data[3];
			} else if (type == PathIterator.SEG_CUBICTO) {
				ay = -lastY + 3 * data[1] - 3 * data[3] + data[5];
				by = 3 * lastY - 6 * data[1] + 3 * data[3];
				cy = -3 * lastY + 3 * data[1];
				dy = lastY;

				ax = -lastX + 3 * data[0] - 3 * data[2] + data[4];
				bx = 3 * lastX - 6 * data[0] + 3 * data[2];
				cx = -3 * lastX + 3 * data[0];
				dx = lastX;

				array[0] = 0;
				i = 1;

				det = 4 * bx * bx - 12 * ax * cx;
				if (det == 0) {
					t2 = (-2 * bx) / (6 * ax);
					if (t2 > 0 && t2 < 1)
						array[i++] = t2;
				} else if (det > 0) {
					det = Math.sqrt(det);

					t2 = (-2 * bx - det) / (6 * ax);
					if (t2 > 0 && t2 < 1)
						array[i++] = t2;
					t2 = (-2 * bx + det) / (6 * ax);
					if (t2 > 0 && t2 < 1)
						array[i++] = t2;
				}

				det = 4 * by * by - 12 * ay * cy;
				if (det == 0) {
					t2 = (-2 * by) / (6 * ay);
					if (t2 > 0 && t2 < 1)
						array[i++] = t2;
				} else if (det > 0) {
					det = Math.sqrt(det);

					t2 = (-2 * by - det) / (6 * ay);
					if (t2 > 0 && t2 < 1)
						array[i++] = t2;
					t2 = (-2 * by + det) / (6 * ay);
					if (t2 > 0 && t2 < 1)
						array[i++] = t2;
				}

				eqn[0] = dy - y1;
				eqn[1] = cy;
				eqn[2] = by;
				eqn[3] = ay;

				i2 = CubicCurve2D.solveCubic(eqn, results);
				for (int a = 0; a < i2; a++) {
					if (results[a] > 0 && results[a] < 1) {
						array[i++] = results[a];
					}
				}

				eqn[0] = dy - y2;
				eqn[1] = cy;
				eqn[2] = by;
				eqn[3] = ay;

				i2 = CubicCurve2D.solveCubic(array, results);
				for (int a = 0; a < i2; a++) {
					if (results[a] > 0 && results[a] < 1) {
						array[i++] = results[a];
					}
				}

				array[i++] = 1;

				state = -1;
				// TODO: Arrays.sort() may allocate unnecessary memory?
				Arrays.sort(array, 0, i);
				for (int a = 0; a < i; a++) {
					myY = ((ay * array[a] + by) * array[a] + cy) * array[a]
							+ dy;
					if (myY >= y1e && myY <= y2e) {
						myX = ((ax * array[a] + bx) * array[a] + cx) * array[a]
								+ dx;
						if (myX < x1) {
							myState = 0;
						} else if (myX > x2) {
							myState = 2;
						} else {
							return true;
						}
						if (state == -1) {
							state = myState;
						} else if (state != myState) {
							return true;
						}
					} else {
						state = -1;
					}
				}

				lastX = data[4];
				lastY = data[5];
			}
			iter.next();
		}
		return false;
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return intersects(x, y, w, h, null);
	}

	public boolean intersects(double x, double y, double w, double h,
			AffineTransform transform) {
		if (identifyCrossings(x, y, w, h, transform)) {
			return true;
		}

		/**
		 * It may not intersect, but if the rectangle lies entirely inside this
		 * shape then we should also return true.
		 */
		if (contains(x + w / 2, y + h / 2) == false) {
			return true;
		}
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return contains(x, y, w, h, null);
	}

	public boolean contains(double x, double y, double w, double h,
			AffineTransform transform) {
		if (identifyCrossings(x, y, w, h, transform)) {
			return false;
		}

		/**
		 * We've established that this rectangle is either 100% inside or 100%
		 * outside of this shape. (There are no segments of this shape crossing
		 * this rectangle, or inside this rectangle.)
		 * 
		 * Last test: are we inside or outside?
		 */
		if (contains(x + w / 2, y + h / 2) == false) {
			// the rectangle is 100% outside this shape
			return false;
		}
		return true;
	}

	@Override
	public Rectangle getBounds() {
		Rectangle r = new Rectangle();
		getBounds(null, r);
		return r;
	}

	@Override
	public Rectangle2D getBounds2D() {
		Rectangle2D r = new Rectangle2D.Double();
		getBounds(null, r);
		return r;
	}

	public Rectangle getBounds(AffineTransform transform) {
		Rectangle r = new Rectangle();
		getBounds(transform, r);
		return r;
	}

	public Rectangle2D getBounds2D(AffineTransform transform) {
		Rectangle2D r = new Rectangle2D.Double();
		getBounds(transform, r);
		return r;
	}

	public Rectangle2D getBounds(AffineTransform transform, Rectangle2D r) {
		PathIterator iterator = getPathIterator(transform);
		return ShapeBounds.getBounds(iterator, r);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new FlatteningPathIterator(getPathIterator(at), flatness);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
}