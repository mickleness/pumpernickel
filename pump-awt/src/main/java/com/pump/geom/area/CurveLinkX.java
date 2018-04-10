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

final class CurveLinkX {
	CurveX curve;
	double ytop;
	double ybot;
	int etag;

	CurveLinkX next;

	public CurveLinkX(CurveX curve, double ystart, double yend, int etag) {
		this.curve = curve;
		this.ytop = ystart;
		this.ybot = yend;
		this.etag = etag;
		if (ytop < curve.getYTop() || ybot > curve.getYBot()) {
			throw new InternalError("bad curvelink [" + ytop + "=>" + ybot
					+ "] for " + curve);
		}
	}

	public boolean absorb(CurveLinkX link) {
		return absorb(link.curve, link.ytop, link.ybot, link.etag);
	}

	public boolean absorb(CurveX curve, double ystart, double yend, int etag) {
		if (this.curve != curve || this.etag != etag || ybot < ystart
				|| ytop > yend) {
			return false;
		}
		if (ystart < curve.getYTop() || yend > curve.getYBot()) {
			throw new InternalError("bad curvelink [" + ystart + "=>" + yend
					+ "] for " + curve);
		}
		this.ytop = Math.min(ytop, ystart);
		this.ybot = Math.max(ybot, yend);
		return true;
	}

	public boolean isEmpty() {
		return (ytop == ybot);
	}

	public CurveX getCurve() {
		return curve;
	}

	public CurveX getSubCurve() {
		if (ytop == curve.getYTop() && ybot == curve.getYBot()) {
			return curve.getWithDirection(etag);
		}
		return curve.getSubCurve(ytop, ybot, etag);
	}

	public CurveX getMoveto() {
		return new Order0X(getXTop(), getYTop());
	}

	public double getXTop() {
		return curve.XforY(ytop);
	}

	public double getYTop() {
		return ytop;
	}

	public double getXBot() {
		return curve.XforY(ybot);
	}

	public double getYBot() {
		return ybot;
	}

	public double getX() {
		return curve.XforY(ytop);
	}

	public int getEdgeTag() {
		return etag;
	}

	public void setNext(CurveLinkX link) {
		this.next = link;
	}

	public CurveLinkX getNext() {
		return next;
	}
}