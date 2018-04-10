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

final class EdgeX {
	static final int INIT_PARTS = 4;
	static final int GROW_PARTS = 10;

	CurveX curve;
	int ctag;
	int etag;
	double activey;
	int equivalence;

	public EdgeX(CurveX c, int ctag) {
		this(c, ctag, AreaOpX.ETAG_IGNORE);
	}

	public EdgeX(CurveX c, int ctag, int etag) {
		this.curve = c;
		this.ctag = ctag;
		this.etag = etag;
	}

	public CurveX getCurve() {
		return curve;
	}

	public int getCurveTag() {
		return ctag;
	}

	public int getEdgeTag() {
		return etag;
	}

	public void setEdgeTag(int etag) {
		this.etag = etag;
	}

	public int getEquivalence() {
		return equivalence;
	}

	public void setEquivalence(int eq) {
		equivalence = eq;
	}

	private EdgeX lastEdge;
	private int lastResult;
	private double lastLimit;

	public int compareTo(EdgeX other, double yrange[]) {
		if (other == lastEdge && yrange[0] < lastLimit) {
			if (yrange[1] > lastLimit) {
				yrange[1] = lastLimit;
			}
			return lastResult;
		}
		if (this == other.lastEdge && yrange[0] < other.lastLimit) {
			if (yrange[1] > other.lastLimit) {
				yrange[1] = other.lastLimit;
			}
			return 0 - other.lastResult;
		}
		// long start = System.currentTimeMillis();
		int ret = curve.compareTo(other.curve, yrange);
		// long end = System.currentTimeMillis();
		/*
		 * System.out.println("compare: "+ ((System.identityHashCode(this) <
		 * System.identityHashCode(other)) ? this+" to "+other :
		 * other+" to "+this)+ " == "+ret+" at "+yrange[1]+
		 * " in "+(end-start)+"ms");
		 */
		lastEdge = other;
		lastLimit = yrange[1];
		lastResult = ret;
		return ret;
	}

	public void record(double yend, int etag) {
		this.activey = yend;
		this.etag = etag;
	}

	public boolean isActiveFor(double y, int etag) {
		return (this.etag == etag && this.activey >= y);
	}

	@Override
	public String toString() {
		return ("Edge["
				+ curve
				+ ", "
				+ (ctag == AreaOpX.CTAG_LEFT ? "L" : "R")
				+ ", "
				+ (etag == AreaOpX.ETAG_ENTER ? "I"
						: (etag == AreaOpX.ETAG_EXIT ? "O" : "N")) + "]");
	}
}