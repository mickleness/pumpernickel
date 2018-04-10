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
package com.pump.geom.knot;

public class Intersection {
	RenderedShape shape1, shape2;
	double x, y;
	int seg1, seg2;
	double t1, t2;
	transient Boolean shape1Over;

	public Intersection(RenderedShape shape1, RenderedShape shape2, double x,
			double y, int seg1, double t1, int seg2, double t2) {
		this.shape1 = shape1;
		this.shape2 = shape2;
		this.x = x;
		this.y = y;
		this.seg1 = seg1;
		this.seg2 = seg2;
		this.t1 = t1;
		this.t2 = t2;
	}

	public Intersection createInverse() {
		Intersection i = new Intersection(shape2, shape1, x, y, seg2, t2, seg1,
				t1);
		if (shape1Over != null) {
			i.shape1Over = new Boolean(!shape1Over);
		}
		return i;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Intersection))
			return false;
		Intersection i = (Intersection) obj;
		if (i.shape1 != shape1)
			return false;
		if (i.shape2 != shape2)
			return false;

		if (i.seg1 != seg1)
			return false;
		if (i.seg2 != seg2)
			return false;

		if (Math.abs(i.t1 - t1) > .0001)
			return false;
		if (Math.abs(i.t2 - t2) > .0001)
			return false;
		if (Math.abs(i.x - x) > .0001)
			return false;
		if (Math.abs(i.y - y) > .0001)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return seg1 + seg2;
	}

}