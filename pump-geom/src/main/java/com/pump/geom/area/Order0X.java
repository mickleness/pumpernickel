/*
 * @(#)Order0X.java
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
// This file is originally based on sun.awt.geom.Order0 (c) 1998 by
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

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

final class Order0X extends CurveX {
	final private double x;
	final private double y;

	public Order0X(double x, double y) {
		super(INCREASING);
		this.x = x;
		this.y = y;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public double getXTop() {
		return x;
	}

	@Override
	public double getYTop() {
		return y;
	}

	@Override
	public double getXBot() {
		return x;
	}

	@Override
	public double getYBot() {
		return y;
	}

	@Override
	public double getXMin() {
		return x;
	}

	@Override
	public double getXMax() {
		return x;
	}

	@Override
	public double getX0() {
		return x;
	}

	@Override
	public double getY0() {
		return y;
	}

	@Override
	public double getX1() {
		return x;
	}

	@Override
	public double getY1() {
		return y;
	}

	@Override
	public double XforY(double y) {
		return y;
	}

	@Override
	public double TforY(double y) {
		return 0;
	}

	@Override
	public double XforT(double t) {
		return x;
	}

	@Override
	public double YforT(double t) {
		return y;
	}

	@Override
	public double dXforT(double t, int deriv) {
		return 0;
	}

	@Override
	public double dYforT(double t, int deriv) {
		return 0;
	}

	@Override
	public double nextVertical(double t0, double t1) {
		return t1;
	}

	@Override
	public int crossingsFor(double x, double y) {
		return 0;
	}

	@Override
	public boolean accumulateCrossings(CrossingsX c) {
		return (x > c.getXLo() &&
				x < c.getXHi() &&
				y > c.getYLo() &&
				y < c.getYHi());
	}

	@Override
	public void enlarge(Rectangle2D r) {
		r.add(x, y);
	}

	@Override
	public CurveX getSubCurve(double ystart, double yend, int dir) {
		return this;
	}

	@Override
	public CurveX getReversedCurve() {
		return this;
	}

	@Override
	public int getSegment(double coords[]) {
		coords[0] = x;
		coords[1] = y;
		return PathIterator.SEG_MOVETO;
	}
}
