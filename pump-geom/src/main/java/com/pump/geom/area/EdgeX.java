/*
 * @(#)EdgeX.java
 *
 * $Date: 2014-03-13 03:15:48 -0500 (Thu, 13 Mar 2014) $
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
// This file is originally based on sun.awt.geom.Edge (c) 1998 by
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
			return 0-other.lastResult;
		}
//		long start = System.currentTimeMillis();
		int ret = curve.compareTo(other.curve, yrange);
//		long end = System.currentTimeMillis();
		/*
System.out.println("compare: "+
((System.identityHashCode(this) <
System.identityHashCode(other))
? this+" to "+other
: other+" to "+this)+
" == "+ret+" at "+yrange[1]+
" in "+(end-start)+"ms");
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
		return ("Edge["+curve+
				", "+
				(ctag == AreaOpX.CTAG_LEFT ? "L" : "R")+
				", "+
				(etag == AreaOpX.ETAG_ENTER ? "I" :
					(etag == AreaOpX.ETAG_EXIT ? "O" : "N"))+
		"]");
	}
}
