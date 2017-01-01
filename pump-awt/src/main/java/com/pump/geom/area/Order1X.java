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

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

final class Order1X extends CurveX {
	final private double x0;
	final private double y0;
	final private double x1;
	final private double y1;
	final private double xmin;
	final private double xmax;

	public Order1X(double x0, double y0,
			double x1, double y1,
			int direction)
	{
		super(direction);
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		if (x0 < x1) {
			this.xmin = x0;
			this.xmax = x1;
		} else {
			this.xmin = x1;
			this.xmax = x0;
		}
	}

	@Override
	public int getOrder() {
		return 1;
	}

	@Override
	public double getXTop() {
		return x0;
	}

	@Override
	public double getYTop() {
		return y0;
	}

	@Override
	public double getXBot() {
		return x1;
	}

	@Override
	public double getYBot() {
		return y1;
	}

	@Override
	public double getXMin() {
		return xmin;
	}

	@Override
	public double getXMax() {
		return xmax;
	}

	@Override
	public double getX0() {
		return (direction == INCREASING) ? x0 : x1;
	}

	@Override
	public double getY0() {
		return (direction == INCREASING) ? y0 : y1;
	}

	@Override
	public double getX1() {
		return (direction == DECREASING) ? x0 : x1;
	}

	@Override
	public double getY1() {
		return (direction == DECREASING) ? y0 : y1;
	}

	@Override
	public double XforY(double y) {
		if (x0 == x1 || y <= y0) {
			return x0;
		}
		if (y >= y1) {
			return x1;
		}
//		assert(y0 != y1); /* No horizontal lines... */
		return (x0 + (y - y0) * (x1 - x0) / (y1 - y0));
	}

	@Override
	public double TforY(double y) {
		if (y <= y0) {
			return 0;
		}
		if (y >= y1) {
			return 1;
		}
		return (y - y0) / (y1 - y0);
	}

	@Override
	public double XforT(double t) {
		return x0 + t * (x1 - x0);
	}

	@Override
	public double YforT(double t) {
		return y0 + t * (y1 - y0);
	}

	@Override
	public double dXforT(double t, int deriv) {
		switch (deriv) {
		case 0:
			return x0 + t * (x1 - x0);
		case 1:
			return (x1 - x0);
		default:
			return 0;
		}
	}

	@Override
	public double dYforT(double t, int deriv) {
		switch (deriv) {
		case 0:
			return y0 + t * (y1 - y0);
		case 1:
			return (y1 - y0);
		default:
			return 0;
		}
	}

	@Override
	public double nextVertical(double t0, double t1) {
		return t1;
	}

	@Override
	public boolean accumulateCrossings(CrossingsX c) {
		double xlo = c.getXLo();
		double ylo = c.getYLo();
		double xhi = c.getXHi();
		double yhi = c.getYHi();
		if (xmin >= xhi) {
			return false;
		}
		double xstart, ystart, xend, yend;
		if (y0 < ylo) {
			if (y1 <= ylo) {
				return false;
			}
			ystart = ylo;
			xstart = XforY(ylo);
		} else {
			if (y0 >= yhi) {
				return false;
			}
			ystart = y0;
			xstart = x0;
		}
		if (y1 > yhi) {
			yend = yhi;
			xend = XforY(yhi);
		} else {
			yend = y1;
			xend = x1;
		}
		if (xstart >= xhi && xend >= xhi) {
			return false;
		}
		if (xstart > xlo || xend > xlo) {
			return true;
		}
		c.record(ystart, yend, direction);
		return false;
	}

	@Override
	public void enlarge(Rectangle2D r) {
		r.add(x0, y0);
		r.add(x1, y1);
	}

	@Override
	public CurveX getSubCurve(double ystart, double yend, int dir) {
		if (ystart == y0 && yend == y1) {
			return getWithDirection(dir);
		}
		if (x0 == x1) {
			return new Order1X(x0, ystart, x1, yend, dir);
		}
		double num = x0 - x1;
		double denom = y0 - y1;
		double xstart = (x0 + (ystart - y0) * num / denom);
		double xend = (x0 + (yend - y0) * num / denom);
		return new Order1X(xstart, ystart, xend, yend, dir);
	}

	@Override
	public CurveX getReversedCurve() {
		return new Order1X(x0, y0, x1, y1, -direction);
	}

	@Override
	public int compareTo(CurveX other, double yrange[]) {
		if (!(other instanceof Order1X)) {
			return super.compareTo(other, yrange);
		}
		Order1X c1 = (Order1X) other;
		if (yrange[1] <= yrange[0]) {
			throw new InternalError("yrange already screwed up...");
		}
		yrange[1] = Math.min(Math.min(yrange[1], y1), c1.y1);
		if (yrange[1] <= yrange[0]) {
			throw new InternalError("backstepping from "+yrange[0]+" to "+yrange[1]);
		}
		if (xmax <= c1.xmin) {
			return (xmin == c1.xmax) ? 0 : -1;
		}
		if (xmin >= c1.xmax) {
			return 1;
		}
		/*
		 * If "this" is curve A and "other" is curve B, then...
		 * xA(y) = x0A + (y - y0A) (x1A - x0A) / (y1A - y0A)
		 * xB(y) = x0B + (y - y0B) (x1B - x0B) / (y1B - y0B)
		 * xA(y) == xB(y)
		 * x0A + (y - y0A) (x1A - x0A) / (y1A - y0A)
		 *    == x0B + (y - y0B) (x1B - x0B) / (y1B - y0B)
		 * 0 == x0A (y1A - y0A) (y1B - y0B) + (y - y0A) (x1A - x0A) (y1B - y0B)
		 *    - x0B (y1A - y0A) (y1B - y0B) - (y - y0B) (x1B - x0B) (y1A - y0A)
		 * 0 == (x0A - x0B) (y1A - y0A) (y1B - y0B)
		 *    + (y - y0A) (x1A - x0A) (y1B - y0B)
		 *    - (y - y0B) (x1B - x0B) (y1A - y0A)
		 * If (dxA == x1A - x0A), etc...
		 * 0 == (x0A - x0B) * dyA * dyB
		 *    + (y - y0A) * dxA * dyB
		 *    - (y - y0B) * dxB * dyA
		 * 0 == (x0A - x0B) * dyA * dyB
		 *    + y * dxA * dyB - y0A * dxA * dyB
		 *    - y * dxB * dyA + y0B * dxB * dyA
		 * 0 == (x0A - x0B) * dyA * dyB
		 *    + y * dxA * dyB - y * dxB * dyA
		 *    - y0A * dxA * dyB + y0B * dxB * dyA
		 * 0 == (x0A - x0B) * dyA * dyB
		 *    + y * (dxA * dyB - dxB * dyA)
		 *    - y0A * dxA * dyB + y0B * dxB * dyA
		 * y == ((x0A - x0B) * dyA * dyB
		 *       - y0A * dxA * dyB + y0B * dxB * dyA)
		 *    / (-(dxA * dyB - dxB * dyA))
		 * y == ((x0A - x0B) * dyA * dyB
		 *       - y0A * dxA * dyB + y0B * dxB * dyA)
		 *    / (dxB * dyA - dxA * dyB)
		 */
		double dxa = x1 - x0;
		double dya = y1 - y0;
		double dxb = c1.x1 - c1.x0;
		double dyb = c1.y1 - c1.y0;
		double denom = dxb * dya - dxa * dyb;
		double y;
		if (denom != 0) {
			double num = ((x0 - c1.x0) * dya * dyb
					- y0 * dxa * dyb
					+ c1.y0 * dxb * dya);
			y = num / denom;
			if (y <= yrange[0]) {
//				intersection is above us
//				Use bottom-most common y for comparison
				y = Math.min(y1, c1.y1);
			} else {
//				intersection is below the top of our range
				if (y < yrange[1]) {
//					If intersection is in our range, adjust valid range
					yrange[1] = y;
				}
//				Use top-most common y for comparison
				y = Math.max(y0, c1.y0);
			}
		} else {
//			lines are parallel, choose any common y for comparison
//			Note - prefer an endpoint for speed of calculating the X
//			(see shortcuts in Order1.XforY())
			y = Math.max(y0, c1.y0);
		}
		return orderof(XforY(y), c1.XforY(y));
	}

	@Override
	public int getSegment(double coords[]) {
		if (direction == INCREASING) {
			coords[0] = x1;
			coords[1] = y1;
		} else {
			coords[0] = x0;
			coords[1] = y0;
		}
		return PathIterator.SEG_LINETO;
	}
}