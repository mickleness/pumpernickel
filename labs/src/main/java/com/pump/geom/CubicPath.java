/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an editable path based on cubic bezier segments. Each node has
 * "previous" and "next" control point.
 * <p>
 * The scale factor is used to limit the magnitude of the the control points.
 * <p>
 * Several methods have two sets of arguments:
 * <ul>
 * <li>One only accepting the <code>nodeIndex</code>. This looks at <i>all</i>
 * nodes in the entire shape.
 * <li>One accepting a <code>nodeIndex</code> and <code>pathIndex</code>. This
 * breaks the shape up into separate paths, and looks at all the nodes in a
 * given path.</li>
 * </ul>
 * 
 * @see com.pump.plaf.CubicPathCreationUI
 * 
 */
public final class CubicPath extends AbstractShape {
	private List<WeakReference<CubicPathIterator>> iterators = new ArrayList<WeakReference<CubicPathIterator>>();
	private int windingRule = PathIterator.WIND_EVEN_ODD;
	private final List<List<CubicNode>> paths = new ArrayList<List<CubicNode>>();
	private double scaleFactor = 1;

	/**
	 * Creates an empty <code>CubicPath</code>.
	 * 
	 */
	public CubicPath() {
	}

	/**
	 * Creates an empty <code>CubicPath</code>.
	 * 
	 * @param windingRule
	 *            the winding rule.
	 */
	public CubicPath(int windingRule) {
		this();
		setWindingRule(windingRule);
	}

	private CubicPath(int windingRule, List<List<CubicNode>> in_paths) {
		this(windingRule);

		for (int a = 0; a < in_paths.size(); a++) {
			List<CubicNode> in_nodes = in_paths.get(a);
			List<CubicNode> nodes = new ArrayList<>(in_nodes.size());
			for (int b = 0; b < in_nodes.size(); b++) {
				CubicNode in_node = in_nodes.get(a);
				nodes.add((CubicNode) in_node.clone());
			}
			paths.add(nodes);
		}
	}

	/**
	 * This sets the scale factor for this shape. The default value is 1.0.
	 * 
	 * @param sf
	 *            new the scale factor. This must be greater than zero.
	 * @return true if this changed the scale factor.
	 */
	public boolean setScaleFactor(double sf) {
		if (sf <= 0)
			throw new IllegalArgumentException("scaleFactor (" + sf
					+ ") must be greater than zero.");
		if (sf == scaleFactor)
			return false;
		scaleFactor = sf;
		return true;
	}

	/**
	 * Returns the scale factor.
	 * 
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	private synchronized void releaseIterators() {
		while (iterators.size() > 0) {
			WeakReference<CubicPathIterator> ref = iterators.remove(iterators
					.size() - 1);
			CubicPathIterator i = ref.get();
			if (i != null)
				i.release();
		}
	}

	/**
	 * Clones this <code>CubicPath</code>.
	 * 
	 */
	@Override
	public synchronized Object clone() {
		return new CubicPath(windingRule, paths);
	}

	/**
	 * A "sharp" node is a node that doesn't have a control points.
	 * 
	 * @param nodeIndex
	 *            the node to examine.
	 * @return true if the node is sharp.
	 */
	public synchronized boolean isNodeSharp(int nodeIndex) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return isNodeSharp(pathIndex, nodeIndex);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Return true if this path is empty.
	 */
	public synchronized boolean isEmpty() {
		return paths.size() == 0;
	}

	/**
	 * A "sharp" node is a node that doesn't have a control points.
	 * 
	 * @param pathIndex
	 *            the path to examine.
	 * @param nodeIndex
	 *            the node to examine.
	 * @return true if the node is sharp.
	 */
	public synchronized boolean isNodeSharp(int pathIndex, int nodeIndex) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		return node.isSharp();
	}

	/**
	 * A "smooth" node is a node where the two control points are collinear.
	 * 
	 * @param nodeIndex
	 *            the node to examine.
	 * @param equalDistance
	 *            if this is true then the control points have to be collinear
	 *            <i>and</i> equidistant to the node point.
	 * @return true if the node is smooth.
	 */
	public synchronized boolean isNodeSmooth(int nodeIndex,
			boolean equalDistance) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return isNodeSmooth(pathIndex, nodeIndex, equalDistance);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * A "smooth" node is a node where the two control points are collinear.
	 * 
	 * @param pathIndex
	 *            the path to examine.
	 * @param nodeIndex
	 *            the node to examine.
	 * @param equalDistance
	 *            if this is true then the control points have to be collinear
	 *            <i>and</i> equidistant to the node point.
	 * @return true if the node is smooth.
	 */
	public synchronized boolean isNodeSmooth(int pathIndex, int nodeIndex,
			boolean equalDistance) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		return node.isSmooth(equalDistance);
	}

	/**
	 * Sets a node's location.
	 * 
	 * @param nodeIndex
	 *            the node to change.
	 * @param p
	 *            the new coordinates of the node.
	 * @param translateControlPoints
	 *            whether the control points should translate similarly.
	 * @return true if data was modified by this call.
	 */
	public synchronized boolean setNode(int nodeIndex, Point2D p,
			boolean translateControlPoints) {
		return setNode(nodeIndex, p.getX(), p.getY(), translateControlPoints);
	}

	/**
	 * Sets a node's location.
	 * 
	 * @param nodeIndex
	 *            the node to change.
	 * @param x
	 *            the new x-coordinate of the node.
	 * @param y
	 *            the new y-coordinate of the node.
	 * @param translateControlPoints
	 *            whether the control points should translate similarly.
	 * @return true if data was modified by this call.
	 */
	public synchronized boolean setNode(int nodeIndex, double x, double y,
			boolean translateControlPoints) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return setNode(pathIndex, nodeIndex, x, y,
						translateControlPoints);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Sets a node's location.
	 * 
	 * @param pathIndex
	 *            the path to change.
	 * @param nodeIndex
	 *            the node to change.
	 * @param p
	 *            the new coordinates of the node.
	 * @param translateControlPoints
	 *            whether the control points should translate similarly.
	 * @return true if data was modified by this call.
	 */
	public synchronized boolean setNode(int pathIndex, int nodeIndex,
			Point2D p, boolean translateControlPoints) {
		return setNode(pathIndex, nodeIndex, p.getX(), p.getY(),
				translateControlPoints);
	}

	/**
	 * Sets a node's location.
	 * 
	 * @param pathIndex
	 *            the path to change.
	 * @param nodeIndex
	 *            the node to change.
	 * @param x
	 *            the new x-coordinate of the node.
	 * @param y
	 *            the new y-coordinate of the node.
	 * @param translateControlPoints
	 *            whether the control points should translate similarly.
	 * @return true if data was modified by this call.
	 */
	public synchronized boolean setNode(int pathIndex, int nodeIndex, double x,
			double y, boolean translateControlPoints) {
		releaseIterators();

		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		return node.setPoint(x, y, translateControlPoints);
	}

	/**
	 * Return the number of paths in this shape.
	 * 
	 */
	public synchronized int getPathCount() {
		return paths.size();
	}

	/**
	 * Return the number of nodes in a given path.
	 * 
	 * @param pathIndex
	 *            the path to get the node count of.
	 * @return the number of nodes in a given path.
	 */
	public synchronized int getNodeCount(int pathIndex) {
		List<CubicNode> nodes = paths.get(pathIndex);
		return nodes.size();
	}

	/**
	 * Return the number of nodes in all paths.
	 * 
	 * @return the number of nodes in all paths.
	 */
	public synchronized int getNodeCount() {
		int sum = 0;
		for (int a = 0; a < paths.size(); a++) {
			List<CubicNode> nodes = paths.get(a);
			sum += nodes.size();
		}
		return sum;
	}

	/** Removes a node from the last path in this shape. */
	public synchronized void removeNode(int nodeIndex) {
		removeNode(paths.size() - 1, nodeIndex);
	}

	/** Removes a node from a specified path. */
	public synchronized void removeNode(int pathIndex, int nodeIndex) {
		List<CubicNode> nodes = paths.get(pathIndex);
		nodes.remove(nodeIndex);
	}

	/**
	 * Returns the coordinates of a node.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the object to store the coordinates in. May be null.
	 * @return this will return the <code>dest</code> argument if it wasn't
	 *         null.
	 */
	public synchronized Point2D getNode(int nodeIndex, Point2D dest) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return getNode(pathIndex, nodeIndex, dest);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Empties this shape.
	 * 
	 */
	public synchronized void clear() {
		releaseIterators();

		paths.clear();
	}

	/**
	 * Returns the coordinates of a node.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the object to store the coordinates in. May be null.
	 * @return this will return the <code>dest</code> argument if it wasn't
	 *         null.
	 */
	public synchronized Point2D getNode(int pathIndex, int nodeIndex,
			Point2D dest) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);

		if (dest == null)
			dest = new Point2D.Double();
		node.getPoint(dest);
		return dest;
	}

	/**
	 * Sets the coordinates of the previous control point for a node.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param p
	 *            the object to store the coordinates in. May be null.
	 * @return this will return <code>null</code> if there is no previous
	 *         control point for this node. Or this will return <code>p</code>
	 *         if it wasn't null.
	 */
	public synchronized boolean setPrevControlForNode(int nodeIndex, Point2D p) {
		return setPrevControlForNode(nodeIndex, p.getX(), p.getY());
	}

	/**
	 * Sets the coordinates of the next control point for a node based on the
	 * previous control point.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param includeDistance
	 *            whether the distances between control points and the node
	 *            should be equal.
	 * @return true if this changed the shape.
	 */
	public synchronized boolean setNextControlForNodeFromPrev(int pathIndex,
			int nodeIndex, boolean includeDistance) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		double dx = node.x - node.prevControl.getX();
		double dy = node.y - node.prevControl.getY();
		double angle = Math.atan2(dy, dx);
		double distance = Math.sqrt(dy * dy + dx * dx) * scaleFactor;
		if (includeDistance) {
			return setNextControlForNode(pathIndex, nodeIndex, node.x
					+ distance * Math.cos(angle),
					node.y + distance * Math.sin(angle));
		} else {
			if (node.nextControl == null)
				node.nextControl = new Point2D.Double(node.x, node.y);
			distance = Point2D.distance(node.x, node.y,
					node.nextControl.getX(), node.nextControl.getY())
					* scaleFactor;
			return setNextControlForNode(pathIndex, nodeIndex, node.x
					+ distance * Math.cos(angle),
					node.y + distance * Math.sin(angle));
		}
	}

	/**
	 * Sets the coordinates of the next control point for a node based on the
	 * previous control point.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param includeDistance
	 *            whether the distances between control points and the node
	 *            should be equal.
	 * @return true if this changed the shape.
	 */
	public synchronized boolean setNextControlForNodeFromPrev(int nodeIndex,
			boolean includeDistance) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return setNextControlForNodeFromPrev(pathIndex, nodeIndex,
						includeDistance);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Sets the coordinates of the previous control point for a node based on
	 * the next control point.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param includeDistance
	 *            whether the distances between control points and the node
	 *            should be equal.
	 * @return true if this changed the shape.
	 */
	public synchronized boolean setPrevControlForNodeFromNext(int pathIndex,
			int nodeIndex, boolean includeDistance) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		double dx = node.x - node.nextControl.getX();
		double dy = node.y - node.nextControl.getY();
		double angle = Math.atan2(dy, dx);
		double distance = Math.sqrt(dy * dy + dx * dx) * scaleFactor;
		if (includeDistance) {
			return setPrevControlForNode(pathIndex, nodeIndex, node.x
					+ distance * Math.cos(angle),
					node.y + distance * Math.sin(angle));
		} else {
			if (node.prevControl == null)
				node.prevControl = new Point2D.Double(node.x, node.y);
			distance = Point2D.distance(node.x, node.y,
					node.prevControl.getX(), node.prevControl.getY())
					* scaleFactor;
			return setPrevControlForNode(pathIndex, nodeIndex, node.x
					+ distance * Math.cos(angle),
					node.y + distance * Math.sin(angle));
		}
	}

	/**
	 * Sets the coordinates of the previous control point for a node based on
	 * the next control point.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param includeDistance
	 *            whether the distances between control points and the node
	 *            should be equal.
	 * @return true if this changed the shape.
	 */
	public synchronized boolean setPrevControlForNodeFromNext(int nodeIndex,
			boolean includeDistance) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return setPrevControlForNodeFromNext(pathIndex, nodeIndex,
						includeDistance);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Sets the previous control point for a node.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param x
	 *            the new x-coordinate of the control point.
	 * @param y
	 *            the new y-coordinate of the control point.
	 * @return true if this changed this shape.
	 */
	public synchronized boolean setPrevControlForNode(int nodeIndex, double x,
			double y) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return setPrevControlForNode(pathIndex, nodeIndex, x, y);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Sets the next control point for a node.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param p
	 *            the new coordinates of the control point.
	 * @return true if this changed this shape.
	 */
	public synchronized boolean setNextControlForNode(int nodeIndex, Point2D p) {
		return setNextControlForNode(nodeIndex, p.getX(), p.getY());
	}

	/**
	 * Sets the next control point for a node.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param x
	 *            the new x-coordinate of the control point.
	 * @param y
	 *            the new y-coordinate of the control point.
	 * @return true if this changed this shape.
	 */
	public synchronized boolean setNextControlForNode(int nodeIndex, double x,
			double y) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return setNextControlForNode(pathIndex, nodeIndex, x, y);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Gets the next node after the indicated node. This may be useful if
	 * <code>nodeIndex</code> is the last node of a path and the "next" node is
	 * the zeroth node in this path.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination for the coordinates.
	 * @return the coordinates of the next node.
	 */
	public synchronized Point2D getNextNode(int nodeIndex, Point2D dest) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return getNextNode(pathIndex, nodeIndex, dest);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Gets the next node after the indicated node. This may be useful if
	 * <code>nodeIndex</code> is the last node of a path and the "next" node is
	 * the zeroth node in this path.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination for the coordinates.
	 * @return the coordinates of the next node.
	 */
	public synchronized Point2D getNextNode(int pathIndex, int nodeIndex,
			Point2D dest) {
		List<CubicNode> nodes = paths.get(pathIndex);
		int i = (nodeIndex + 1) % nodes.size();
		return getNode(pathIndex, i, dest);
	}

	/**
	 * Gets the previous node before the indicated node. This may be useful if
	 * <code>nodeIndex</code> is the zeroth node of a path and the "previous"
	 * node is the last node in this path.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination for the coordinates.
	 * @return the coordinates of the previous node.
	 */
	public synchronized Point2D getPrevNode(int nodeIndex, Point2D dest) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return getPrevNode(pathIndex, nodeIndex, dest);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Gets the previous node before the indicated node. This may be useful if
	 * <code>nodeIndex</code> is the zeroth node of a path and the "previous"
	 * node is the last node in this path.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination for the coordinates.
	 * @return the coordinates of the previous node.
	 */
	public synchronized Point2D getPrevNode(int pathIndex, int nodeIndex,
			Point2D dest) {
		List<CubicNode> nodes = paths.get(pathIndex);
		int i = (nodeIndex + nodes.size() - 1) % nodes.size();
		return getNode(pathIndex, i, dest);
	}

	/**
	 * Sets the previous control point for a node.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param p
	 *            the new coordinates of the control point.
	 * @return true if this changed this shape.
	 */
	public synchronized boolean setPrevControlForNode(int pathIndex,
			int nodeIndex, Point2D p) {
		return setPrevControlForNode(pathIndex, nodeIndex, p.getX(), p.getY());
	}

	/**
	 * Sets the previous control point for a node.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param x
	 *            the new x-coordinate of the control point.
	 * @param y
	 *            the new y-coordinate of the control point.
	 * @return true if this changed this shape.
	 */
	public synchronized boolean setPrevControlForNode(int pathIndex,
			int nodeIndex, double x, double y) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		return node.setPrevControl(x, y, scaleFactor);
	}

	/**
	 * Sets the next control point for a node.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param p
	 *            the new coordinates of the control point.
	 * @return true if this changed this shape.
	 */
	public synchronized boolean setNextControlForNode(int pathIndex,
			int nodeIndex, Point2D p) {
		return setNextControlForNode(pathIndex, nodeIndex, p.getX(), p.getY());
	}

	/**
	 * Sets the next control point for a node.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param x
	 *            the new x-coordinate of the control point.
	 * @param y
	 *            the new y-coordinate of the control point.
	 * @return true if this changed this shape.
	 */
	public synchronized boolean setNextControlForNode(int pathIndex,
			int nodeIndex, double x, double y) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		return node.setNextControl(x, y, scaleFactor);
	}

	/**
	 * Returns the next control point.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination to store the control point in.
	 * @return may return <code>null</code> if there is no next control point
	 *         for a node. Otherwise this will return <code>dest</code> if it is
	 *         non-null.
	 */
	public synchronized Point2D getNextControlForNode(int nodeIndex,
			Point2D dest) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return getNextControlForNode(pathIndex, nodeIndex, dest);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Returns whether a node has a next control point.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @return whether a node has a next control point.
	 */
	public synchronized boolean hasNextControlForNode(int nodeIndex) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return hasNextControlForNode(pathIndex, nodeIndex);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Returns whether a node has a previous control point.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @return whether a node has a next control point.
	 */
	public synchronized boolean hasPrevControlForNode(int nodeIndex) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return hasPrevControlForNode(pathIndex, nodeIndex);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Returns whether a node has a next control point.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @return whether a node has a next control point.
	 */
	public synchronized boolean hasNextControlForNode(int pathIndex,
			int nodeIndex) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		return node.nextControl != null;
	}

	/**
	 * Returns whether a node has a previous control point.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @return whether a node has a next control point.
	 */
	public synchronized boolean hasPrevControlForNode(int pathIndex,
			int nodeIndex) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);
		return node.prevControl != null;
	}

	/**
	 * Returns the previous control point.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination to store the control point in.
	 * @return may return <code>null</code> if there is no next control point
	 *         for a node. Otherwise this will return <code>dest</code> if it is
	 *         non-null.
	 */
	public synchronized Point2D getPrevControlForNode(int nodeIndex,
			Point2D dest) {
		for (int pathIndex = 0; pathIndex < paths.size(); pathIndex++) {
			List<CubicNode> nodes = paths.get(pathIndex);
			int s = nodes.size();
			if (nodeIndex < s) {
				return getPrevControlForNode(pathIndex, nodeIndex, dest);
			}
			nodeIndex -= s;
		}
		System.err.println(this);
		throw new IllegalArgumentException("no node found at index "
				+ nodeIndex);
	}

	/**
	 * Returns the previous control point.
	 * 
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination to store the control point in.
	 * @return may return <code>null</code> if there is no next control point
	 *         for a node. Otherwise this will return <code>dest</code> if it is
	 *         non-null.
	 */
	public synchronized Point2D getPrevControlForNode(int pathIndex,
			int nodeIndex, Point2D dest) {
		return getPrevControlForNode(pathIndex, nodeIndex, dest, scaleFactor);
	}

	/**
	 * Returns the previous control point.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination to store the control point in.
	 * @return may return <code>null</code> if there is no next control point
	 *         for a node. Otherwise this will return <code>dest</code> if it is
	 *         non-null.
	 */
	private synchronized Point2D getPrevControlForNode(int pathIndex,
			int nodeIndex, Point2D dest, double scaleFactor) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);

		if (node.hasPrevControl() == false)
			return null;

		if (dest == null)
			dest = new Point2D.Double();
		node.getPrevControl(dest, scaleFactor);
		return dest;
	}

	/**
	 * Returns the next control point.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param nodeIndex
	 *            the node index.
	 * @param dest
	 *            the optional destination to store the control point in.
	 * @return may return <code>null</code> if there is no next control point
	 *         for a node. Otherwise this will return <code>dest</code> if it is
	 *         non-null.
	 */
	public synchronized Point2D getNextControlForNode(int pathIndex,
			int nodeIndex, Point2D dest) {
		return getNextControlForNode(pathIndex, nodeIndex, dest, scaleFactor);
	}

	private synchronized Point2D getNextControlForNode(int pathIndex,
			int nodeIndex, Point2D dest, double scaleFactor) {
		List<CubicNode> nodes = paths.get(pathIndex);
		CubicNode node = nodes.get(nodeIndex);

		if (node.hasNextControl() == false)
			return null;

		if (dest == null)
			dest = new Point2D.Double();
		node.getNextControl(dest, scaleFactor);
		return dest;
	}

	/**
	 * This starts a new path.
	 * 
	 * @param x
	 *            the x-coordinate to move to.
	 * @param y
	 *            the y-coordinate to move to.
	 * @return the path index of this new path.
	 */
	public synchronized int moveTo(float x, float y) {
		releaseIterators();
		List<CubicNode> newPath = new ArrayList<CubicNode>();
		paths.add(newPath);
		CubicNode newNode = new CubicNode(x, y);
		newPath.add(newNode);
		return paths.size() - 1;
	}

	/**
	 * This is analogous to <code>GeneralPath.curveTo()</code>.
	 * 
	 * @param cx1
	 *            the x-coordinate of the first control point.
	 * @param cy1
	 *            the y-coordinate of the first control point.
	 * @param cx2
	 *            the x-coordinate of the second control point.
	 * @param cy2
	 *            the y-coordinate of the second control point.
	 * @param x2
	 *            the x-coordinate of the end point.
	 * @param y2
	 *            the y-coordinate of the end point.
	 */
	public synchronized void curveTo(float cx1, float cy1, float cx2,
			float cy2, float x2, float y2) {
		curveTo(paths.size() - 1, cx1, cy1, cx2, cy2, x2, y2);
	}

	/**
	 * This is analogous to <code>GeneralPath.curveTo()</code>.
	 * 
	 * @param pathIndex
	 *            the path to append this curve to.
	 * @param cx1
	 *            the x-coordinate of the first control point.
	 * @param cy1
	 *            the y-coordinate of the first control point.
	 * @param cx2
	 *            the x-coordinate of the second control point.
	 * @param cy2
	 *            the y-coordinate of the second control point.
	 * @param x2
	 *            the x-coordinate of the end point.
	 * @param y2
	 *            the y-coordinate of the end point.
	 */
	public synchronized void curveTo(int pathIndex, float cx1, float cy1,
			float cx2, float cy2, float x2, float y2) {
		releaseIterators();
		List<CubicNode> path = paths.get(pathIndex);
		CubicNode lastNode = path.get(path.size() - 1);
		lastNode.nextControl = new Point2D.Float(cx1, cy1);
		CubicNode newNode = new CubicNode(x2, y2);
		newNode.prevControl = new Point2D.Float(cx2, cy2);
		path.add(newNode);
	}

	/**
	 * This is analogous to <code>GeneralPath.lineTo()</code>.
	 * 
	 * @param x
	 *            the x-coordinate of the end point.
	 * @param y
	 *            the y-coordinate of the end point.
	 */
	public synchronized void lineTo(float x, float y) {
		lineTo(paths.size() - 1, x, y);
	}

	/**
	 * This is analogous to <code>GeneralPath.lineTo()</code>.
	 * 
	 * @param pathIndex
	 *            the path index.
	 * @param x
	 *            the x-coordinate of the end point.
	 * @param y
	 *            the y-coordinate of the end point.
	 */
	public synchronized void lineTo(int pathIndex, float x, float y) {
		releaseIterators();
		List<CubicNode> path = paths.get(pathIndex);
		CubicNode newNode = new CubicNode(x, y);
		path.add(newNode);
	}

	/**
	 * This will append the argument to this shape.
	 * 
	 * @param s
	 *            the shape to append.
	 */
	public final synchronized void append(Shape s) {
		releaseIterators();

		PathIterator i = s.getPathIterator(null);
		double[] coords = new double[6];
		List<CubicNode> newPath = null;
		CubicNode lastSegment = null;
		CubicNode moveSegment = null;
		while (i.isDone() == false) {
			int k = i.currentSegment(coords);
			if (k == PathIterator.SEG_MOVETO) {
				newPath = new ArrayList<CubicNode>();
				CubicNode newSegment = new CubicNode(coords[0], coords[1]);
				newPath.add(newSegment);
				lastSegment = newSegment;
				moveSegment = lastSegment;
			} else if (k == PathIterator.SEG_LINETO) {
				if (newPath == null)
					throw new IllegalArgumentException(
							"a lineTo instruction must be preceded by a moveTo instruction");

				CubicNode newSegment = new CubicNode(coords[0], coords[1]);
				newPath.add(newSegment);

				lastSegment.nextControl = new Point2D.Double(lastSegment.x,
						lastSegment.y);
				newSegment.prevControl = new Point2D.Double(newSegment.x,
						newSegment.y);

				lastSegment = newSegment;
			} else if (k == PathIterator.SEG_QUADTO) {
				if (newPath == null)
					throw new IllegalArgumentException(
							"a quadTo instruction must be preceded by a moveTo instruction");

				CubicNode newSegment = new CubicNode(coords[2], coords[3]);
				newPath.add(newSegment);

				lastSegment.nextControl = new Point2D.Double(lastSegment.x,
						lastSegment.y);
				newSegment.prevControl = new Point2D.Double(newSegment.x,
						newSegment.y);

				// FIXME: handle control points

				lastSegment = newSegment;
			} else if (k == PathIterator.SEG_CUBICTO) {
				if (newPath == null)
					throw new IllegalArgumentException(
							"a cubicTo instruction must be preceded by a moveTo instruction");

				CubicNode newSegment = new CubicNode(coords[4], coords[5]);
				newPath.add(newSegment);

				lastSegment.nextControl = new Point2D.Double(coords[0],
						coords[1]);
				newSegment.prevControl = new Point2D.Double(coords[2],
						coords[3]);

				lastSegment = newSegment;
			} else if (k == PathIterator.SEG_CLOSE) {
				if (newPath == null)
					throw new IllegalArgumentException(
							"a close instruction must be preceded by a moveTo instruction");

				if (lastSegment.pointEquals(moveSegment) == false) {
					// we made a path with two far-away endpoints:
					moveSegment.prevControl = new Point2D.Double(moveSegment.x,
							moveSegment.y);
					lastSegment.nextControl = new Point2D.Double(lastSegment.x,
							lastSegment.y);
				} else {
					// our last segment redundantly overlaps with the
					// moveSegment:
					moveSegment.prevControl = lastSegment.prevControl;
					newPath.remove(newPath.size() - 1);
				}

				paths.add(newPath);
				newPath = null;
				moveSegment = null;
				lastSegment = null;
			}
			i.next();
		}
		if (newPath != null) {
			paths.add(newPath);
		}
	}

	/**
	 * Returns a <code>String</code> representation of this shape.
	 * 
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CubicPath[ ");
		for (int a = 0; a < paths.size(); a++) {
			List<CubicNode> nodes = paths.get(a);

			if (a != 0) {
				sb.append(", ");
			}
			sb.append("path = [");
			for (int b = 0; b < nodes.size(); b++) {
				CubicNode node = nodes.get(b);

				if (b != 0) {
					sb.append(", ");
				}
				sb.append(node.toString());
			}
			sb.append("]");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Returns the winding rule.
	 * 
	 * @return this will be <code>WIND_NON_ZERO</code> or
	 *         <code>WIND_EVEN_ODD</code>.
	 */
	@Override
	public int getWindingRule() {
		return windingRule;
	}

	/**
	 * Sets the winding rule.
	 * 
	 * @param newRule
	 *            the new winding rule. This must be <code>WIND_NON_ZERO</code>
	 *            or <code>WIND_EVEN_ODD</code>.
	 */
	public void setWindingRule(int newRule) {
		if (!(newRule == PathIterator.WIND_EVEN_ODD || newRule == PathIterator.WIND_NON_ZERO))
			throw new IllegalArgumentException("the rule (" + newRule
					+ ") must be WIND_EVEN_ODD or WIND_NON_ZERO");
		// this doesn't have to release the iterators, because
		// they cache their own value of windingRule on construction.
		if (windingRule == newRule)
			return;

		windingRule = newRule;
	}

	/**
	 * Returns a <code>PathIterator</code> that iterates over this shape.
	 * 
	 */
	public synchronized PathIterator getPathIterator(AffineTransform transform) {
		CubicPathIterator i = new CubicPathIterator(this, transform);
		iterators.add(new WeakReference<CubicPathIterator>(i));
		return i;
	}

	static class CubicNode {
		static final double TOLERANCE = .00001;
		double x, y;
		Point2D prevControl;
		Point2D nextControl;

		public CubicNode(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + toString(prevControl) + ", (" + x + ", " + y + "), "
					+ toString(nextControl) + ")";
		}

		private String toString(Point2D p) {
			if (p == null)
				return "null";
			return "(" + p.getX() + ", " + p.getY() + ")";
		}

		public boolean isSharp() {
			double d1 = 0;
			double d2 = 0;
			if (prevControl != null) {
				d1 = prevControl.distance(x, y);
			}
			if (nextControl != null) {
				d2 = nextControl.distance(x, y);
			}
			return d1 < TOLERANCE && d2 < TOLERANCE;
		}

		public boolean isSmooth(boolean equalDistance) {
			if (nextControl == null || prevControl == null)
				return false;
			double angle1 = Math.atan2(nextControl.getY() - y,
					nextControl.getX() - x);
			double angle2 = Math.atan2(y - prevControl.getY(),
					x - prevControl.getX());
			// TODO: does this work? Do we need to account for border cases?
			boolean anglesEqual = Math.atan(angle1 - angle2) < TOLERANCE;

			if (!anglesEqual)
				return false;

			if (equalDistance == false)
				return anglesEqual;

			double d1 = 0;
			double d2 = 0;
			if (prevControl != null) {
				d1 = prevControl.distance(x, y);
			}
			if (nextControl != null) {
				d2 = nextControl.distance(x, y);
			}
			return (Math.abs(d1 - d2) < TOLERANCE);
		}

		public boolean setPoint(double x, double y, boolean translateControls) {
			double dx = x - this.x;
			double dy = y - this.y;
			if (Math.abs(dx) < TOLERANCE && Math.abs(dy) < TOLERANCE)
				return false;
			this.x += dx;
			this.y += dy;

			if (translateControls && prevControl != null) {
				prevControl.setLocation(prevControl.getX() + dx,
						prevControl.getY() + dy);
			}
			if (translateControls && nextControl != null) {
				nextControl.setLocation(nextControl.getX() + dx,
						nextControl.getY() + dy);
			}

			return true;
		}

		@Override
		public Object clone() {
			CubicNode newNode = new CubicNode(x, y);
			if (prevControl != null)
				newNode.prevControl = (Point2D) prevControl.clone();
			if (nextControl != null)
				newNode.nextControl = (Point2D) nextControl.clone();
			return newNode;
		}

		public boolean pointEquals(CubicNode s) {
			return Math.abs(s.x - x) < TOLERANCE
					&& Math.abs(s.y - y) < TOLERANCE;
		}

		public void getPoint(Point2D dest) {
			dest.setLocation(x, y);
		}

		public void getPrevControl(Point2D dest, double distanceScale) {
			if (distanceScale == 1) {
				dest.setLocation(prevControl);
			} else {
				double dy = prevControl.getY() - y;
				double dx = prevControl.getX() - x;
				double theta = Math.atan2(dy, dx);
				double distance = Math.sqrt(dy * dy + dx * dx);
				dest.setLocation(
						x + distance * distanceScale * Math.cos(theta), y
								+ distance * distanceScale * Math.sin(theta));
			}
		}

		public void getNextControl(Point2D dest, double distanceScale) {
			if (distanceScale == 1) {
				dest.setLocation(nextControl);
			} else {
				double dy = nextControl.getY() - y;
				double dx = nextControl.getX() - x;
				double theta = Math.atan2(dy, dx);
				double distance = Math.sqrt(dy * dy + dx * dx);
				dest.setLocation(
						x + distance * distanceScale * Math.cos(theta), y
								+ distance * distanceScale * Math.sin(theta));
			}
		}

		public boolean setPrevControl(double x, double y, double distanceScale) {
			double newX, newY;
			if (distanceScale == 1) {
				newX = x;
				newY = y;
			} else {
				double dy = y - this.y;
				double dx = x - this.x;
				double theta = Math.atan2(dy, dx);
				double distance = Math.sqrt(dy * dy + dx * dx);
				newX = this.x + distance / distanceScale * Math.cos(theta);
				newY = this.y + distance / distanceScale * Math.sin(theta);
			}
			if (prevControl == null) {
				prevControl = new Point2D.Double(newX, newY);
				return true;
			}
			if (Math.abs(prevControl.getX() - newX) < TOLERANCE
					&& Math.abs(prevControl.getY() - newY) < TOLERANCE)
				return false;
			prevControl.setLocation(newX, newY);
			return true;
		}

		public boolean setNextControl(double x, double y, double distanceScale) {
			double newX, newY;
			if (distanceScale == 1) {
				newX = x;
				newY = y;
			} else {
				double dy = y - this.y;
				double dx = x - this.x;
				double theta = Math.atan2(dy, dx);
				double distance = Math.sqrt(dy * dy + dx * dx);
				newX = this.x + distance / distanceScale * Math.cos(theta);
				newY = this.y + distance / distanceScale * Math.sin(theta);
			}
			if (nextControl == null) {
				nextControl = new Point2D.Double(newX, newY);
				return true;
			}
			if (Math.abs(nextControl.getX() - newX) < TOLERANCE
					&& Math.abs(nextControl.getY() - newY) < TOLERANCE)
				return false;
			nextControl.setLocation(newX, newY);
			return true;
		}

		public boolean hasPrevControl() {
			return prevControl != null;
		}

		public boolean hasNextControl() {
			return nextControl != null;
		}
	}

	static class CubicPathIterator implements PathIterator {
		int windingRule;
		CubicPath src;
		int pathPtr;
		int nodePtr;
		AffineTransform transform;

		public CubicPathIterator(CubicPath src, AffineTransform transform) {
			windingRule = src.getWindingRule();
			if (src.getPathCount() == 0) {
				this.src = null;
			} else {
				this.src = src;
			}
			pathPtr = 0;
			nodePtr = 0;
			if (transform != null) {
				this.transform = (AffineTransform) transform.clone();
			}
		}

		public void release() {
			if (src != null)
				src = (CubicPath) src.clone();
		}

		private double[] d;

		public int currentSegment(float[] coords) {
			// TODO: once testing has gelled, maybe copy & paste the
			// code from the double[] method to this one
			if (d == null)
				d = new double[6];
			int returnType = currentSegment(d);
			for (int a = 0; a < d.length; a++) {
				coords[a] = (float) d[a];
			}
			return returnType;
		}

		Point2D point = new Point2D.Double();

		public int currentSegment(double[] coords) {
			if (nodePtr == 0) {
				src.getNode(pathPtr, nodePtr, point);
				coords[0] = point.getX();
				coords[1] = point.getY();
				if (transform != null)
					transform.transform(coords, 0, coords, 0, 1);
				return PathIterator.SEG_MOVETO;
			}
			int nodeCount = src.getNodeCount(pathPtr);
			if (nodePtr < nodeCount) {

				src.getNode(pathPtr, nodePtr, point);
				coords[4] = point.getX();
				coords[5] = point.getY();

				if (src.getPrevControlForNode(pathPtr, nodePtr, point, 1) == null) {
					src.getNode(pathPtr, nodePtr, point);
					coords[2] = point.getX();
					coords[3] = point.getY();
				} else {
					coords[2] = point.getX();
					coords[3] = point.getY();
				}

				if (src.getNextControlForNode(pathPtr, nodePtr - 1, point, 1) == null) {
					src.getNode(pathPtr, nodePtr - 1, point);
					coords[0] = point.getX();
					coords[1] = point.getY();
				} else {
					coords[0] = point.getX();
					coords[1] = point.getY();
				}
				src.getNode(pathPtr, nodePtr - 1, point);

				if (transform != null)
					transform.transform(coords, 0, coords, 0, 3);
				return SimplifiedPathIterator.simplify(
						PathIterator.SEG_CUBICTO, point.getX(), point.getY(),
						coords);
			} else if (nodePtr == nodeCount) {
				// this is the special case where we're trying to close the
				// shape
				// so this cubic curve will arc from node (nodeCount-1) to node
				// 0
				src.getNode(pathPtr, 0, point);
				coords[4] = point.getX();
				coords[5] = point.getY();

				if (src.getPrevControlForNode(pathPtr, 0, point, 1) == null) {
					src.getNode(pathPtr, 0, point);
					coords[2] = point.getX();
					coords[3] = point.getY();
				}
				coords[2] = point.getX();
				coords[3] = point.getY();

				if (src.getNextControlForNode(pathPtr, nodeCount - 1, point, 1) == null) {
					src.getNode(pathPtr, nodeCount - 1, point);
					coords[0] = point.getX();
					coords[1] = point.getY();
				}
				coords[0] = point.getX();
				coords[1] = point.getY();

				src.getNode(pathPtr, nodeCount - 1, point);

				if (transform != null)
					transform.transform(coords, 0, coords, 0, 3);
				return SimplifiedPathIterator.simplify(
						PathIterator.SEG_CUBICTO, point.getX(), point.getY(),
						coords);
			} else if (nodePtr == nodeCount + 1) {
				return PathIterator.SEG_CLOSE;
			}
			System.err.println("pathPtr = " + pathPtr + ", nodePtr = "
					+ nodePtr);
			System.err.println(src);
			throw new RuntimeException("unexpected condition");
		}

		public int getWindingRule() {
			return windingRule;
		}

		public boolean isDone() {
			return src == null;
		}

		public void next() {
			nodePtr++;

			int nodeTotal = src.getNodeCount(pathPtr);
			if (nodePtr == nodeTotal
					&& src.getNextControlForNode(pathPtr, nodeTotal - 1, point,
							1) != null) {
				// we have 2 more segments to write, so this is OK.
			} else if (nodePtr == nodeTotal + 1
					&& src.getNextControlForNode(pathPtr, nodeTotal - 1, point,
							1) != null) {
				// we have to write the SEG_CLOSE still, so this is OK.
			} else if (nodePtr < nodeTotal) {
				// definitely OK here
			} else {
				// not OK here; we need to move on to the next path

				nodePtr = 0;
				pathPtr++;
				if (pathPtr == src.getPathCount()) {
					src = null;
					nodePtr = -1;
					pathPtr = -1;
				}
			}
		}
	}
}