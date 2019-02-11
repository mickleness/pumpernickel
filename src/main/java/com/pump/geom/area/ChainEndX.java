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
	 * Returns head of a complete chain to be added to subcurves or null if the
	 * links did not complete such a chain.
	 */
	public CurveLinkX linkTo(ChainEndX that) {
		if (etag == AreaOpX.ETAG_IGNORE || that.etag == AreaOpX.ETAG_IGNORE) {
			throw new InternalError("ChainEnd linked more than once!");
		}
		if (etag == that.etag) {
			throw new InternalError("Linking chains of the same type!");
		}
		ChainEndX enter, exit;
		// assert(partner.etag != that.partner.etag);
		if (etag == AreaOpX.ETAG_ENTER) {
			enter = this;
			exit = that;
		} else {
			enter = that;
			exit = this;
		}
		// Now make sure these ChainEnds are not linked to any others...
		etag = AreaOpX.ETAG_IGNORE;
		that.etag = AreaOpX.ETAG_IGNORE;
		// Now link everything up...
		enter.tail.setNext(exit.head);
		enter.tail = exit.tail;
		if (partner == that) {
			// Curve has closed on itself...
			return enter.head;
		}
		// Link this chain into one end of the chain formed by the partners
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