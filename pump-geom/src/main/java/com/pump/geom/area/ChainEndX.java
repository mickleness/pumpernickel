/*
 * @(#)ChainEndX.java
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
// This file is originally based on sun.awt.geom.ChainEnd (c) 1998 by
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

final class ChainEndX {
	CurveLinkX head;
	CurveLinkX tail;
	ChainEndX partner;
	int etag;

	public ChainEndX(CurveLinkX first, ChainEndX partner) {
		this.head = first;
		this.tail = first;
		this.partner = partner;
		this.etag = first.getEdgeTag();
	}

	public CurveLinkX getChain() {
		return head;
	}

	public void setOtherEnd(ChainEndX partner) {
		this.partner = partner;
	}

	public ChainEndX getPartner() {
		return partner;
	}

	/*
	 * Returns head of a complete chain to be added to subcurves
	 * or null if the links did not complete such a chain.
	 */
	public CurveLinkX linkTo(ChainEndX that) {
		if (etag == AreaOpX.ETAG_IGNORE ||
				that.etag == AreaOpX.ETAG_IGNORE)
		{
			throw new InternalError("ChainEnd linked more than once!");
		}
		if (etag == that.etag) {
			throw new InternalError("Linking chains of the same type!");
		}
		ChainEndX enter, exit;
//		assert(partner.etag != that.partner.etag);
		if (etag == AreaOpX.ETAG_ENTER) {
			enter = this;
			exit = that;
		} else {
			enter = that;
			exit = this;
		}
//		Now make sure these ChainEnds are not linked to any others...
		etag = AreaOpX.ETAG_IGNORE;
		that.etag = AreaOpX.ETAG_IGNORE;
//		Now link everything up...
		enter.tail.setNext(exit.head);
		enter.tail = exit.tail;
		if (partner == that) {
//			Curve has closed on itself...
			return enter.head;
		}
//		Link this chain into one end of the chain formed by the partners
		ChainEndX otherenter = exit.partner;
		ChainEndX otherexit = enter.partner;
		otherenter.partner = otherexit;
		otherexit.partner = otherenter;
		if (enter.head.getYTop() < otherenter.head.getYTop()) {
			enter.tail.setNext(otherenter.head);
			otherenter.head = enter.head;
		} else {
			otherexit.tail.setNext(enter.head);
			otherexit.tail = enter.tail;
		}
		return null;
	}

	public void addLink(CurveLinkX newlink) {
		if (etag == AreaOpX.ETAG_ENTER) {
			tail.setNext(newlink);
			tail = newlink;
		} else {
			newlink.setNext(head);
			head = newlink;
		}
	}

	public double getX() {
		if (etag == AreaOpX.ETAG_ENTER) {
			return tail.getXBot();
		} else {
			return head.getXBot();
		}
	}
}
