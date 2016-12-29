/*
 * @(#)CurveLinkX.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
// This file is originally based on sun.awt.geom.CurveLink (c) 1998 by
// Sun Microsystems, Inc.  The license of the original document is
// as follows:

/*
 * Copyright 1998 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
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
			throw new InternalError("bad curvelink ["+ytop+"=>"+ybot+"] for "+curve);
		}
	}

	public boolean absorb(CurveLinkX link) {
		return absorb(link.curve, link.ytop, link.ybot, link.etag);
	}

	public boolean absorb(CurveX curve, double ystart, double yend, int etag) {
		if (this.curve != curve || this.etag != etag ||
				ybot < ystart || ytop > yend)
		{
			return false;
		}
		if (ystart < curve.getYTop() || yend > curve.getYBot()) {
			throw new InternalError("bad curvelink ["+ystart+"=>"+yend+"] for "+curve);
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
