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

/**
 * This is a node in a double-linked list of intersections. This list will be
 * sorted by ascending t1 values.
 * 
 * Each node represents where two segments intersect. Each segment has to be
 * recorded with their respective t-values for the intersection.
 */
class Intersection {
	/**
	 * This segment will have a pointer to the head of a list of Intersections.
	 * All intersections in this list have the same s1 object.
	 */
	BasicShapeSegment s1;
	BasicShapeSegment s2;
	double t1;
	double t2;
	Intersection prev;
	Intersection next;
	boolean claimed;

	/**
	 * This contains the same data, except the s1 and s2 elements are switched.
	 */
	Intersection complement;

	/**
	 * This will add the new intersection at the correct index in this sorted
	 * list.
	 * <p>
	 * This should be called on the head, and will return the head. Note this
	 * operation may make the argument the new head if its t1 value is smallest.
	 * 
	 * @param newGuy
	 *            the node to add
	 * @return the head of this list.
	 */
	public Intersection add(Intersection newGuy) {
		Intersection head = this;
		while (head.prev != null)
			head = head.prev;

		if (newGuy.t1 < head.t1) {
			newGuy.next = head;
			newGuy.prev = null;
			head.prev = newGuy;
			return newGuy;
		}

		Intersection i = head;

		while (true) {
			if (i.next == null) {
				i.next = newGuy;
				newGuy.prev = i;
				newGuy.next = null;
				return head;
			}

			if (newGuy.t1 < i.next.t1) {
				Intersection otherNext = i.next;
				i.next = newGuy;
				newGuy.prev = i;
				newGuy.next = otherNext;
				otherNext.prev = newGuy;
				return head;
			}

			i = i.next;
		}
	}

	/** Initializes this intersection. */
	protected void init(BasicShapeSegment s1, BasicShapeSegment s2, double t1,
			double t2) {
		this.s1 = s1;
		this.s2 = s2;
		this.t1 = t1;
		this.t2 = t2;
		claimed = false;
	}

	protected Intersection getTail() {
		Intersection i = this;
		while (i.next != null)
			i = i.next;
		return i;
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean includeAllNodes) {
		if (includeAllNodes == false) {
			return "(" + t1 + ", " + t2 + ")";
		}

		StringBuffer sb = new StringBuffer();
		Intersection i = this;
		while (i != null) {
			if (sb.length() != 0)
				sb.append(" ");
			sb.append(i.toString(false));
			i = i.next;
		}
		return sb.toString();
	}
}