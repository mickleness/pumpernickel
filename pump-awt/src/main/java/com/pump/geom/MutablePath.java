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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A shape that lets you modify individual segments.
 * <P>
 * This shape has the notion of several distinct paths where each path begins
 * with a separator <code>moveoTo()</code> instruction. There are some methods
 * that treat this object as one long string of segments, and some methods that
 * recognize that you can access each path separately.
 * <P>
 * Several methods here match the signatures of <code>GeneralPath</code> methods
 * for convenience. Internally field and methods are made private/final because
 * of the thorough verification that happens as you manipulate this object:
 * subclasses shouldn't let you position anything other than a SEG_MOVETO after
 * a SEG_CLOSE, for example.
 */
public class MutablePath extends AbstractShape {

	private List<List<MutablePathSegment>> paths = new ArrayList<>();
	int windingRule = PathIterator.WIND_NON_ZERO;
	private List<WeakReference<MutablePathIterator>> iterators = new ArrayList<>();

	public MutablePath(Shape s) {
		append(s);
	}

	public MutablePath() {
	}

	public synchronized void setWindingRule(int newRule) {
		if (!(newRule == PathIterator.WIND_EVEN_ODD || newRule == PathIterator.WIND_NON_ZERO))
			throw new IllegalArgumentException(
					"the rule must be WIND_EVEN_ODD or WIND_NON_ZERO.");
		windingRule = newRule;
	}

	protected synchronized MutablePathSegment peek(int pathIndex) {
		if (paths.size() == 0)
			return null;
		List<MutablePathSegment> segments = paths.get(pathIndex);
		return segments.get(segments.size() - 1);
	}

	public synchronized void close() {
		int i = paths.size();
		if (i == 0)
			throw new RuntimeException(
					"all paths must begin with a moveTo statement");
		close(paths.size() - 1);
	}

	public synchronized void close(int pathIndex) {
		releaseIterators();

		MutablePathSegment tail = peek(pathIndex);
		if (tail.type == PathIterator.SEG_CLOSE) {
			throw new RuntimeException(
					"all paths must begin with a moveTo statement.  Two close instructions cannot be adjacent.");
		}

		List<MutablePathSegment> segments = paths.get(pathIndex);
		segments.add(new MutablePathSegment());
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return the index of this new path
	 */
	public synchronized int moveTo(double x, double y) {
		releaseIterators();

		List<MutablePathSegment> newSegments = new ArrayList<MutablePathSegment>();
		newSegments.add(new MutablePathSegment(PathIterator.SEG_MOVETO, x, y));
		paths.add(newSegments);
		return paths.size() - 1;
	}

	public synchronized void lineTo(double x, double y) {
		int i = paths.size() - 1;
		if (i < 0)
			throw new RuntimeException(
					"all paths must begin with a moveTo statement");
		lineTo(i, x, y);
	}

	public synchronized void lineTo(int pathIndex, double x, double y) {
		releaseIterators();

		MutablePathSegment tail = peek(pathIndex);
		if (tail.type == PathIterator.SEG_CLOSE)
			throw new RuntimeException(
					"all paths must begin with a moveTo statement");
		List<MutablePathSegment> segments = paths.get(pathIndex);
		segments.add(new MutablePathSegment(PathIterator.SEG_LINETO, x, y));
	}

	public synchronized void quadTo(double cx, double cy, double x, double y) {
		int i = paths.size() - 1;
		if (i < 0)
			throw new RuntimeException(
					"all paths must begin with a moveTo statement");
		quadTo(i, cx, cy, x, y);
	}

	public synchronized void quadTo(int pathIndex, double cx, double cy,
			double x, double y) {
		releaseIterators();

		MutablePathSegment tail = peek(pathIndex);
		if (tail.type == PathIterator.SEG_CLOSE)
			throw new RuntimeException(
					"all paths must begin with a moveTo statement");
		List<MutablePathSegment> segments = paths.get(pathIndex);
		segments.add(new MutablePathSegment(cx, cy, x, y));
	}

	public synchronized void curveTo(double cx0, double cy0, double cx1,
			double cy1, double x, double y) {
		int i = paths.size() - 1;
		if (i < 0)
			throw new RuntimeException(
					"all paths must begin with a moveTo statement");
		curveTo(i, cx0, cy0, cx1, cy1, x, y);
	}

	public synchronized void curveTo(int pathIndex, double cx0, double cy0,
			double cx1, double cy1, double x, double y) {
		releaseIterators();

		MutablePathSegment tail = peek(pathIndex);
		if (tail.type == PathIterator.SEG_CLOSE)
			throw new RuntimeException(
					"all paths must begin with a moveTo statement");
		List<MutablePathSegment> segments = paths.get(pathIndex);
		segments.add(new MutablePathSegment(cx0, cy0, cx1, cy1, x, y));
	}

	public synchronized void delete(int pathIndex, int segmentIndex) {
		List<MutablePathSegment> segments = paths.get(pathIndex);
		if (segmentIndex == 0 && segments.size() > 1) {
			MutablePathSegment newHead = segments.get(1);
			if (newHead.type == PathIterator.SEG_CLOSE) {
				segments.remove(1);
			} else if (newHead.type == PathIterator.SEG_LINETO) {
				newHead.setData(PathIterator.SEG_MOVETO, newHead.data);
			} else if (newHead.type == PathIterator.SEG_QUADTO) {
				newHead.setData(PathIterator.SEG_MOVETO, new double[] {
						newHead.data[2], newHead.data[3] });
			} else if (newHead.type == PathIterator.SEG_CUBICTO) {
				newHead.setData(PathIterator.SEG_MOVETO, new double[] {
						newHead.data[4], newHead.data[5] });
			} else {
				throw new RuntimeException("unexpected segment type: "
						+ newHead.type);
			}
		}
		segments.remove(segmentIndex);

		// housekeeping:
		if (segments.size() == 0) {
			paths.remove(pathIndex);
		}
	}

	public synchronized void append(Shape s) {
		append(s, null);
	}

	public synchronized void append(Shape s, AffineTransform transform) {
		PathIterator i = s.getPathIterator(transform);
		double[] coords = new double[6];
		while (i.isDone() == false) {
			int k = i.currentSegment(coords);
			if (k == PathIterator.SEG_CLOSE) {
				close();
			} else if (k == PathIterator.SEG_CUBICTO) {
				curveTo(coords[0], coords[1], coords[2], coords[3], coords[4],
						coords[5]);
			} else if (k == PathIterator.SEG_QUADTO) {
				quadTo(coords[0], coords[1], coords[2], coords[3]);
			} else if (k == PathIterator.SEG_LINETO) {
				lineTo(coords[0], coords[1]);
			} else if (k == PathIterator.SEG_MOVETO) {
				moveTo(coords[0], coords[1]);
			} else {
				throw new RuntimeException("unexpected segment type (" + k
						+ ")");
			}
			i.next();
		}
	}

	protected synchronized void releaseIterators() {
		while (iterators.size() > 0) {
			WeakReference<MutablePathIterator> ref = iterators.remove(iterators
					.size() - 1);
			MutablePathIterator mpi = ref.get();
			if (mpi != null)
				mpi.release();
		}
	}

	public synchronized int getPathCount() {
		return paths.size();
	}

	public synchronized int getSegmentCount(int pathIndex) {
		List<MutablePathSegment> segments = paths.get(pathIndex);
		return segments.size();
	}

	public synchronized int getSegmentCount() {
		int sum = 0;
		for (int a = 0; a < paths.size(); a++) {
			List<MutablePathSegment> segments = paths.get(a);
			sum += segments.size();
		}
		return sum;
	}

	public synchronized int getSegment(int pathIndex, int segmentIndex,
			double[] coords) {
		List<MutablePathSegment> segments = paths.get(pathIndex);
		MutablePathSegment s = segments.get(segmentIndex);
		for (int b = 0; b < s.data.length; b++) {
			coords[b] = s.data[b];
		}
		return s.type;
	}

	public synchronized int getSegment(int pathIndex, int segmentIndex,
			float[] coords) {
		List<MutablePathSegment> segments = paths.get(pathIndex);
		MutablePathSegment s = segments.get(segmentIndex);
		for (int b = 0; b < s.data.length; b++) {
			coords[b] = (float) s.data[b];
		}
		return s.type;
	}

	public synchronized int getSegment(int index, double[] coords) {
		final int originalIndex = index;
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<MutablePathSegment> segments = paths.get(pathIndex);
			int segmentCount = segments.size();
			if (index < segmentCount) {
				return getSegment(pathIndex, index, coords);
			}
			index -= segmentCount;
		}
		throw new IllegalArgumentException("no segment available at index "
				+ originalIndex);
	}

	public synchronized int getSegmentType(int pathIndex, int segmentIndex) {
		List<MutablePathSegment> segments = paths.get(pathIndex);
		MutablePathSegment s = segments.get(segmentIndex);
		return s.type;
	}

	public synchronized int getSegmentType(int index) {
		final int originalIndex = index;
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<MutablePathSegment> segments = paths.get(pathIndex);
			int segmentCount = segments.size();
			if (index < segmentCount) {
				return getSegmentType(pathIndex, index);
			}
			index -= segmentCount;
		}
		throw new IllegalArgumentException("no segment available at index "
				+ originalIndex);
	}

	public synchronized int getSegment(int index, float[] coords) {
		final int originalIndex = index;
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<MutablePathSegment> segments = paths.get(pathIndex);
			int segmentCount = segments.size();
			if (index < segmentCount) {
				return getSegment(pathIndex, index, coords);
			}
			index -= segmentCount;
		}
		throw new IllegalArgumentException("no segment available at index "
				+ originalIndex);
	}

	public synchronized boolean setSegment(int pathIndex, int segmentIndex,
			int type, double[] coords) {
		List<MutablePathSegment> segments = paths.get(pathIndex);
		MutablePathSegment s = segments.get(segmentIndex);
		if (s.equals(type, coords))
			return false;

		releaseIterators();
		int oldType = s.type;
		if (oldType == PathIterator.SEG_MOVETO
				&& type != PathIterator.SEG_MOVETO) {
			// this used to mark the start of a new list of segments,
			// but now it does not:
			if (pathIndex == 0) {
				throw new IllegalArgumentException(
						"the first segment must be a MOVETO segment.");
			} else {
				List<MutablePathSegment> previousSegments = paths
						.get(pathIndex - 1);
				MutablePathSegment tail = previousSegments.get(previousSegments
						.size() - 1);
				if (tail.type == PathIterator.SEG_CLOSE) {
					throw new IllegalArgumentException(
							"only a MOVETO segment can follow a CLOSE segment");
				}
				previousSegments.addAll(segments);
				paths.remove(pathIndex);
			}
		} else if (oldType != PathIterator.SEG_MOVETO
				&& type == PathIterator.SEG_MOVETO) {
			// this used to be a regular segment, but not it needs to mark the
			// beginning
			// of a new list of segments:
			List<MutablePathSegment> newSegments = new ArrayList<MutablePathSegment>();
			for (int b = segmentIndex; b < segments.size(); b++) {
				newSegments.add(segments.remove(segmentIndex));
			}
			paths.add(pathIndex, newSegments);
		} else if (oldType != PathIterator.SEG_CLOSE
				&& type == PathIterator.SEG_CLOSE) {
			if (segmentIndex != segments.size() - 1)
				throw new IllegalArgumentException(
						"only a MOVETO segment can follow a CLOSE segment");
		}
		s.setData(type, coords);
		return true;
	}

	/**
	 * Redefine a segment. This lets you do anything without an exception: you
	 * could technically make a path begin a SEG_CLOSE, or place ten SEG_MOVETOs
	 * next to each other.
	 * 
	 * @param index
	 * @param type
	 * @param coords
	 */
	public synchronized boolean setSegment(int index, int type, double[] coords) {
		final int originalIndex = index;
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<MutablePathSegment> segments = paths.get(pathIndex);
			int segmentCount = segments.size();
			if (index < segmentCount) {
				setSegment(pathIndex, index, type, coords);
			}
			index -= segmentCount;
		}
		throw new IllegalArgumentException("no segment available at index "
				+ originalIndex);
	}

	/**
	 * Redefine a segment. This lets you do anything without an exception: you
	 * could technically make a path begin a SEG_CLOSE, or place ten SEG_MOVETOs
	 * next to each other.
	 * 
	 * @param index
	 * @param type
	 * @param coords
	 */
	public synchronized boolean setSegment(int index, int type, float[] coords) {
		int size = Math.min(6, coords.length);
		double[] dCoords = new double[size];
		for (int a = 0; a < size; a++) {
			dCoords[a] = coords[a];
		}
		return setSegment(index, type, dCoords);
	}

	public synchronized boolean isEmpty() {
		for (int a = 0; a < paths.size(); a++) {
			List<MutablePathSegment> segments = paths.get(a);
			if (segments.size() > 0)
				return false;
		}
		return true;
	}

	public synchronized void transform(AffineTransform transform) {
		if (transform == null)
			return;

		releaseIterators();

		for (int a = 0; a < paths.size(); a++) {
			List<MutablePathSegment> segments = paths.get(a);
			for (int b = 0; b < segments.size(); b++) {
				MutablePathSegment s = segments.get(b);
				transform.transform(s.data, 0, s.data, 0, s.data.length / 2);
			}
		}
	}

	public synchronized void reset() {
		releaseIterators();
		paths.clear();
	}

	@Override
	public synchronized int getWindingRule() {
		return windingRule;
	}

	public synchronized PathIterator getPathIterator(AffineTransform at) {
		MutablePathIterator i = new MutablePathIterator(paths, windingRule, at,
				this);
		iterators.add(new WeakReference<MutablePathIterator>(i));
		return i;
	}
}

class MutablePathSegment {
	private static final double[] EMPTY_ARRAY = new double[0];

	private static boolean equals(double[] array1, double[] array2) {
		if (array1 == null && array2 == null)
			return true;
		if (array1 == null || array2 == null)
			return false;
		if (array1.length != array2.length)
			return false;
		for (int a = 0; a < array1.length; a++) {
			if (array1[a] != array2[a])
				return false;
		}
		return true;
	}

	private static String toString(double[] array) {
		StringBuffer sb = new StringBuffer(array.length * 8);
		sb.append("[ ");
		for (int a = 0; a < array.length; a++) {
			if (a != 0) {
				sb.append(", ");
			}
			sb.append(Double.toString(array[a]));
		}
		sb.append(" ]");
		return sb.toString();
	}

	double[] data;
	int type;

	public MutablePathSegment() {
		setData(PathIterator.SEG_CLOSE, EMPTY_ARRAY);
	}

	public boolean equals(int type, double[] data) {
		if (this.type != type)
			return false;
		for (int a = 0; a < this.data.length; a++) {
			if (data[a] != this.data[a])
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MutablePathSegment))
			return false;
		MutablePathSegment s = (MutablePathSegment) obj;
		return equals(s.type, s.data);
	}

	@Override
	public Object clone() {
		return cloneSegment();
	}

	public MutablePathSegment cloneSegment() {
		double[] array = new double[data.length];
		System.arraycopy(data, 0, array, 0, data.length);
		return new MutablePathSegment(type, data);
	}

	public boolean setData(int type, double[] data) {
		if (!(type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_CLOSE
				|| type == PathIterator.SEG_CUBICTO
				|| type == PathIterator.SEG_LINETO || type == PathIterator.SEG_QUADTO)) {
			throw new IllegalArgumentException(
					"type must be SEG_MOVETO, SEG_CLOSE, SEG_CUBICTO, SEG_LINETO or SEG_QUADTO");
		}
		if (this.type == type && equals(this.data, data)) {
			return false;
		}
		this.type = type;
		this.data = data;

		if (type == PathIterator.SEG_CLOSE) {
			if (data.length > 0)
				throw new IllegalArgumentException(
						"close segments cannot have data (" + toString(data)
								+ ")");
		} else if (type == PathIterator.SEG_MOVETO) {
			if (data.length < 2)
				throw new IllegalArgumentException(
						"moveTo segments must have 2 coordinates ("
								+ toString(data) + ")");
		} else if (type == PathIterator.SEG_LINETO) {
			if (data.length < 2)
				throw new IllegalArgumentException(
						"lineTo segments must have 2 coordinates ("
								+ toString(data) + ")");
		} else if (type == PathIterator.SEG_QUADTO) {
			if (data.length < 4)
				throw new IllegalArgumentException(
						"quadTo segments must have 4 coordinates ("
								+ toString(data) + ")");
		} else if (type == PathIterator.SEG_CUBICTO) {
			if (data.length < 6)
				throw new IllegalArgumentException(
						"cubicTo segments must have 6 coordinates ("
								+ toString(data) + ")");
		}
		return true;
	}

	public MutablePathSegment(int type, double[] array) {
		setData(type, array);
	}

	public MutablePathSegment(int type, double x, double y) {
		setData(type, new double[] { x, y });
	}

	public MutablePathSegment(double cx, double cy, double x, double y) {
		setData(PathIterator.SEG_QUADTO, new double[] { cx, cy, x, y });
	}

	public MutablePathSegment(double cx0, double cy0, double cx1, double cy1,
			double x, double y) {
		setData(PathIterator.SEG_CUBICTO, new double[] { cx0, cy0, cx1, cy1, x,
				y });
	}

	public int get(double[] coords) {
		for (int a = 0; a < data.length; a++) {
			coords[a] = data[a];
		}
		return type;
	}
}

/**
 * This iterates over a <code>MutablePath</code>.
 * <P>
 * This uses a peculiar approach to accessing the path data: the data is not
 * cloned unless <code>release()</code> is called. Until then this points to the
 * data in the <code>MutablePath</code> it was created from. This is designed to
 * avoid large memory demands unless absolutely necessary.
 * <P>
 * (<code>release()</code> is called when the parent <code>MutablePath</code> is
 * modified. So as long as the parent remains unchanged there is no need to
 * clone all its segment data.)
 */
class MutablePathIterator implements PathIterator {
	final AffineTransform t;
	final int winding;
	int pathPtr = 0;
	int segPtr = 0;
	List<List<MutablePathSegment>> paths;
	List<MutablePathSegment> linkedList;

	Object synchronizeAgainst;

	public MutablePathIterator(List<List<MutablePathSegment>> pathList,
			int windingRule, AffineTransform transform, Object syncLock) {
		winding = windingRule;
		if (transform != null) {
			t = (AffineTransform) transform.clone();
		} else {
			t = null;
		}
		synchronizeAgainst = syncLock;
		paths = pathList;
	}

	public synchronized void release() {
		// have we already released?
		if (synchronizeAgainst == null)
			return;

		synchronized (synchronizeAgainst) {
			linkedList = new LinkedList<MutablePathSegment>();
			for (int p = pathPtr; p < paths.size(); p++) {
				int start = (p == pathPtr) ? segPtr : 0;
				List<MutablePathSegment> segments = paths.get(p);
				for (int s = start; s < segments.size(); s++) {
					MutablePathSegment segment = segments.get(s);
					linkedList.add(segment.cloneSegment());
				}
			}
			synchronizeAgainst = null;
			paths = null;
			pathPtr = -1;
			segPtr = 0;
		}
	}

	public int currentSegment(float[] coords) {
		int type;
		if (synchronizeAgainst != null) {
			synchronized (synchronizeAgainst) {
				List<MutablePathSegment> segments = paths.get(pathPtr);
				MutablePathSegment s = segments.get(segPtr);
				for (int a = 0; a < s.data.length; a++) {
					coords[a] = (float) s.data[a];
				}
				type = s.type;
			}
		} else {
			MutablePathSegment s = linkedList.get(segPtr);
			for (int a = 0; a < s.data.length; a++) {
				coords[a] = (float) s.data[a];
			}
			type = s.type;
		}
		if (t != null) {
			t.transform(coords, 0, coords, 0, 3);
		}
		return type;
	}

	public int currentSegment(double[] coords) {
		int type;
		if (synchronizeAgainst != null) {
			synchronized (synchronizeAgainst) {
				List<MutablePathSegment> segments = paths.get(pathPtr);
				MutablePathSegment s = segments.get(segPtr);
				for (int a = 0; a < s.data.length; a++) {
					coords[a] = s.data[a];
				}
				type = s.type;
			}
		} else {
			MutablePathSegment s = linkedList.get(segPtr);
			for (int a = 0; a < s.data.length; a++) {
				coords[a] = s.data[a];
			}
			type = s.type;
		}
		if (t != null) {
			t.transform(coords, 0, coords, 0, 3);
		}
		return type;
	}

	public int getWindingRule() {
		return winding;
	}

	public synchronized boolean isDone() {
		return linkedList == null && paths == null;
	}

	public synchronized void next() {
		if (linkedList != null) {
			linkedList.remove(0);
			if (linkedList.size() == 0)
				linkedList = null;
		}
		if (synchronizeAgainst != null) {
			synchronized (synchronizeAgainst) {
				if (paths != null) {
					List<MutablePathSegment> segments = paths.get(pathPtr);
					segPtr++;
					if (segPtr == segments.size()) {
						segPtr = 0;
						pathPtr++;
						if (pathPtr == paths.size()) {
							// we're all done:
							paths = null;
							synchronizeAgainst = null;
						}
					}
				}
			}
		}
	}
}