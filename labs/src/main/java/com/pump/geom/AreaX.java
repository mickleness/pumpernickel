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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;

import com.pump.geom.area.AreaXBody;
import com.pump.geom.area.CrossingsX;
import com.pump.geom.area.CurveX;

/**
 * This class is a direct branch from the <code>java.awt.geom.Area</code>.
 * <p>
 * An <code>AreaX</code> object stores and manipulates a resolution-independent
 * description of an enclosed area of 2-dimensional space. <code>AreaX</code>
 * objects can be transformed and can perform various Constructive Area Geometry
 * (CAG) operations when combined with other <code>AreaX</code> objects. The CAG
 * operations include area {@link #add addition}, {@link #subtract subtraction},
 * {@link #intersect intersection}, and {@link #exclusiveOr exclusive or}. See
 * the linked method documentation for examples of the various operations.
 * <p>
 * The <code>AreaX</code> class implements the <code>Shape</code> interface and
 * provides full support for all of its hit-testing and path iteration
 * facilities, but an <code>AreaX</code> is more specific than a generalized
 * path in a number of ways:
 * <ul>
 * <li>Only closed paths and sub-paths are stored. <code>AreaX</code> objects
 * constructed from unclosed paths are implicitly closed during construction as
 * if those paths had been filled by the <code>Graphics2D.fill</code> method.
 * <li>The interiors of the individual stored sub-paths are all non-empty and
 * non-overlapping. Paths are decomposed during construction into separate
 * component non-overlapping parts, empty pieces of the path are discarded, and
 * then these non-empty and non-overlapping properties are maintained through
 * all subsequent CAG operations. Outlines of different component sub-paths may
 * touch each other, as long as they do not cross so that their enclosed areas
 * overlap.
 * <li>The geometry of the path describing the outline of the <code>AreaX</code>
 * resembles the path from which it was constructed only in that it describes
 * the same enclosed 2-dimensional area, but may use entirely different types
 * and ordering of the path segments to do so.
 * </ul>
 * Interesting issues which are not always obvious when using the
 * <code>AreaX</code> include:
 * <ul>
 * <li>Creating an <code>AreaX</code> from an unclosed (open) <code>Shape</code>
 * results in a closed outline in the <code>AreaX</code> object.
 * <li>Creating an <code>AreaX</code> from a <code>Shape</code> which encloses
 * no area (even when "closed") produces an empty <code>AreaX</code>. A common
 * example of this issue is that producing an <code>AreaX</code> from a line
 * will be empty since the line encloses no area. An empty <code>AreaX</code>
 * will iterate no geometry in its <code>PathIterator</code> objects.
 * <li>A self-intersecting <code>Shape</code> may be split into two (or more)
 * sub-paths each enclosing one of the non-intersecting portions of the original
 * path.
 * <li>An <code>AreaX</code> may take more path segments to describe the same
 * geometry even when the original outline is simple and obvious. The analysis
 * that the <code>AreaX</code> class must perform on the path may not reflect
 * the same concepts of "simple and obvious" as a human being perceives.
 * </ul>
 * 
 * <p>
 * What is stated above is directly copied from the
 * <code>java.awt.geom.Area</code> javadocs. Below are notes specifically
 * related to the <code>AreaX</code> class:
 * <p>
 * The AreaX class is intended to achieve exactly the same visual results as the
 * Area class. However several possible optimizations have been carefully
 * implemented to reach those results faster.
 * <p>
 * The most customizable change is the implementation of the
 * <code>AreaXRules</code> object. Developers are encouraged to subclass this
 * object and experiment with it. (And if they make improvements: email them to
 * me to include in this project!)
 * <p>
 * Each <code>AreaX</code> object has a reference to a rules object that can be
 * changed by calling: <code>myArea.setRules()</code>. However you can also
 * change the <i>default</i> rules for all future <code>AreaX</code> objects by
 * calling: <code>UIManager.put("AreaX.rules", myRules)</code>.
 */
public class AreaX implements Shape, Cloneable {
	public static final int RELATIONSHIP_LHS_CONTAINS = 0;
	public static final int RELATIONSHIP_RHS_CONTAINS = 1;
	public static final int RELATIONSHIP_COMPLEX = 2;
	public static final int RELATIONSHIP_NONE = 3;

	private static AreaXBody emptyBody = new AreaXBody(null, 0);
	private static AreaXRules minimalRules = new AreaXRules();
	private static Map<String, AreaXRules> rulesTable = new HashMap<String, AreaXRules>();

	private static AreaXRules getDefaultRules() {
		Object rulesObject = UIManager.get("AreaX.rules");
		if (rulesObject instanceof AreaXRules)
			return (AreaXRules) rulesObject;
		String className = (String) rulesObject;
		if (className == null)
			className = "com.pump.geom.BoundsRules";
		AreaXRules rules = rulesTable.get(className);
		try {
			if (rules == null) {
				Class<?> theClass = Class.forName(className);
				Constructor<?> constructor = theClass
						.getConstructor(new Class[] {});
				rules = (AreaXRules) constructor.newInstance(new Object[] {});
			}
			return rules;
		} catch (Throwable t) {
			t.printStackTrace();
			return minimalRules;
		}
	}

	private AreaXBody body;
	private List<AreaXOperation> queue = new LinkedList<AreaXOperation>();
	private AreaXRules rules = getDefaultRules();

	/**
	 * Default constructor which creates an empty area.
	 */
	public AreaX() {
		body = emptyBody;
	}

	/**
	 * Create a new <code>AreaX</code> pointing to a <code>AreaXBody</code>.
	 */
	public AreaX(AreaXBody body) {
		this.body = body;
	}

	/**
	 * The <code>AreaX</code> class creates an area geometry from the specified
	 * {@link Shape} object. The geometry is explicitly closed, if the
	 * <code>Shape</code> is not already closed. The fill rule (even-odd or
	 * winding) specified by the geometry of the <code>Shape</code> is used to
	 * determine the resulting enclosed area.
	 * 
	 * @param s
	 *            the <code>Shape</code> from which the area is constructed.
	 *            <p>
	 *            If this is an <code>AreaX</code>, then this constructor forces
	 *            any pending operations to be performed.
	 * @throws NullPointerException
	 *             if <code>s</code> is null
	 */
	public AreaX(Shape s) {
		if (s instanceof AreaX) {
			AreaX area = (AreaX) s;
			area.processQueue();
			body = area.body;
		} else {
			body = AreaXBody.create(s.getPathIterator(null), true);
		}
	}

	public synchronized void setRules(AreaXRules rules) {
		if (rules == null)
			rules = minimalRules;
		this.rules = rules;
	}

	public synchronized AreaXRules getRules() {
		return rules;
	}

	/**
	 * This immediately executes all pending operations on this
	 * <code>AreaX</code>. It should not be necessary for other objects to need
	 * to call this method, except to manager the time/cost of execution.
	 */
	public synchronized void processQueue() {
		int queueSize = queue.size();
		if (queueSize == 0)
			return;

		AreaXOperation[] ops = queue.toArray(new AreaXOperation[queueSize]);
		queue.clear();
		body = rules.execute(body, ops);
	}

	/**
	 * This returns the <code>AreaXBody</code> that currently expressed the data
	 * in this <code>AreaX</code>. This object will constantly be replaced as
	 * new operations are performed.
	 * <p>
	 * This forces any pending operations to be performed.
	 */
	public synchronized AreaXBody getBody() {
		processQueue();
		return body;
	}

	/**
	 * Adds the shape of the specified <code>Shape</code> to the shape of this
	 * <code>AreaX</code>. The resulting shape of this <code>AreaX</code> will
	 * include the union of both shapes, or all areas that were contained in
	 * either this or the specified <code>AreaX</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.add(a2);
	 * 
	 *        a1(before)     +         a2         =     a1(after)
	 * 
	 *     ################     ################     ################
	 *     ##############         ##############     ################
	 *     ############             ############     ################
	 *     ##########                 ##########     ################
	 *     ########                     ########     ################
	 *     ######                         ######     ######    ######
	 *     ####                             ####     ####        ####
	 *     ##                                 ##     ##            ##
	 * </pre>
	 * <p>
	 * This call does not immediately execute. It adds this operation to a queue
	 * of operations that are processed as required.
	 * 
	 * @param rhs
	 *            the <code>Shape</code> to be added to the current shape
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void add(Shape rhs) {
		queue.add(new AreaXOperation(rhs, AreaXOperation.ADD));
	}

	/**
	 * This uses <code>getRelationship()</code> to determine if the argument is
	 * inside this shape.
	 * 
	 * @param shape
	 * @return true whether the argument is completely contained inside this
	 *         shape.
	 */
	public synchronized boolean contains(Shape shape) {
		return getRelationship(null, shape, null) == RELATIONSHIP_LHS_CONTAINS;
	}

	/**
	 * This uses <code>getRelationship()</code> to determine if the argument is
	 * inside this shape.
	 * 
	 * @param shape
	 * @param transform
	 *            a transform to apply to the argument shape.
	 * @return true whether the argument (when transformed) is completely
	 *         contained inside this shape.
	 */
	public synchronized boolean contains(Shape shape, AffineTransform transform) {
		return getRelationship(null, shape, transform) == RELATIONSHIP_LHS_CONTAINS;
	}

	/**
	 * This uses <code>getRelationship()</code> to determine if the argument is
	 * inside this shape.
	 * 
	 * @param lhsTransform
	 *            the optional transform to apply to this operand.
	 * @param shape
	 * @param transform
	 *            an optional transform to apply to the argument shape.
	 * @return true whether the argument (when transformed) is completely
	 *         contained inside this shape.
	 */
	public synchronized boolean contains(AffineTransform lhsTransform,
			Shape shape, AffineTransform transform) {
		return getRelationship(lhsTransform, shape, transform) == RELATIONSHIP_LHS_CONTAINS;
	}

	/**
	 * This uses <code>getRelationship()</code> to determine if the argument
	 * intersects this shape.
	 * 
	 * @param shape
	 * @return true whether the argument (when transformed) intersects this
	 *         shape.
	 */
	public synchronized boolean intersects(Shape shape) {
		int r = getRelationship(null, shape, null);
		return r != RELATIONSHIP_NONE;
	}

	/**
	 * This uses <code>getRelationship()</code> to determine if the argument
	 * intersects this shape.
	 * 
	 * @param shape
	 * @param transform
	 *            a transform to apply to the argument shape.
	 * @return true whether the argument (when transformed) intersects this
	 *         shape.
	 */
	public synchronized boolean intersects(Shape shape,
			AffineTransform transform) {
		int r = getRelationship(null, shape, transform);
		return r != RELATIONSHIP_NONE;
	}

	/**
	 * This uses <code>getRelationship()</code> to determine if the argument
	 * intersects this shape.
	 * 
	 * @param lhsTransform
	 *            the optional transform to apply to this operand.
	 * @param shape
	 * @param transform
	 *            an optional transform to apply to the argument shape.
	 * @return true whether the argument (when transformed) intersects this
	 *         shape.
	 */
	public synchronized boolean intersects(AffineTransform lhsTransform,
			Shape shape, AffineTransform transform) {
		int r = getRelationship(lhsTransform, shape, transform);
		return r != RELATIONSHIP_NONE;
	}

	/**
	 * Returns one of the relationship constants to indicate the relationship
	 * between this shape and the argument (with the optional transform).
	 * 
	 * @param lhsTransform
	 *            the optional transform to apply to this operand.
	 * @param shape
	 * @param transform
	 *            an optional transform to apply to the argument.
	 * @return one of the 4 relationship constants: RELATIONSHIP_LHS_CONTAINS,
	 *         RELATIONSHIP_RHS_CONTAINS, RELATIONSHIP_NONE, or
	 *         RELATIONSHIP_COMPLEX.
	 */
	public synchronized int getRelationship(AffineTransform lhsTransform,
			Shape shape, AffineTransform transform) {
		return getBody().getRelationship(lhsTransform, shape, transform);
	}

	/**
	 * Adds the shape of the specified <code>AreaX</code> to the shape of this
	 * <code>AreaX</code>. The resulting shape of this <code>AreaX</code> will
	 * include the union of both shapes, or all areas that were contained in
	 * either this or the specified <code>AreaX</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.add(a2);
	 * 
	 *        a1(before)     +         a2         =     a1(after)
	 * 
	 *     ################     ################     ################
	 *     ##############         ##############     ################
	 *     ############             ############     ################
	 *     ##########                 ##########     ################
	 *     ########                     ########     ################
	 *     ######                         ######     ######    ######
	 *     ####                             ####     ####        ####
	 *     ##                                 ##     ##            ##
	 * </pre>
	 * <p>
	 * This call immediately executes. It executes any pending operations first,
	 * and then executes this add.
	 * 
	 * @param rhs
	 *            the <code>AreaX</code> to be added to the current shape
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void executeAdd(AreaX rhs) {
		body = getBody().add(rhs.getBody());
	}

	/**
	 * Subtracts the shape of the specified <code>Shape</code> from the shape of
	 * this <code>AreaX</code>. The resulting shape of this <code>AreaX</code>
	 * will include areas that were contained only in this <code>AreaX</code>
	 * and not in the specified <code>AreaX</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.subtract(a2);
	 * 
	 *        a1(before)     -         a2         =     a1(after)
	 * 
	 *     ################     ################
	 *     ##############         ##############     ##
	 *     ############             ############     ####
	 *     ##########                 ##########     ######
	 *     ########                     ########     ########
	 *     ######                         ######     ######
	 *     ####                             ####     ####
	 *     ##                                 ##     ##
	 * </pre>
	 * <p>
	 * This call does not immediately execute. It adds this operation to a queue
	 * of operations that are processed as required.
	 * 
	 * @param rhs
	 *            the <code>AreaX</code> to be subtracted from the current shape
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void subtract(Shape rhs) {
		queue.add(new AreaXOperation(rhs, AreaXOperation.SUBTRACT));
	}

	/**
	 * Subtracts the shape of the specified <code>AreaX</code> from the shape of
	 * this <code>AreaX</code>. The resulting shape of this <code>AreaX</code>
	 * will include areas that were contained only in this <code>AreaX</code>
	 * and not in the specified <code>AreaX</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.subtract(a2);
	 * 
	 *        a1(before)     -         a2         =     a1(after)
	 * 
	 *     ################     ################
	 *     ##############         ##############     ##
	 *     ############             ############     ####
	 *     ##########                 ##########     ######
	 *     ########                     ########     ########
	 *     ######                         ######     ######
	 *     ####                             ####     ####
	 *     ##                                 ##     ##
	 * </pre>
	 * <p>
	 * This call immediately executes. It executes any pending operations first,
	 * and then executes this add.
	 * 
	 * @param rhs
	 *            the <code>AreaX</code> to be subtracted from the current shape
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void executeSubtract(AreaX rhs) {
		body = getBody().subtract(rhs.getBody());
	}

	/**
	 * Sets the shape of this <code>AreaX</code> to the intersection of its
	 * current shape and the shape of the specified <code>Shape</code>. The
	 * resulting shape of this <code>AreaX</code> will include only areas that
	 * were contained in both this <code>AreaX</code> and also in the specified
	 * <code>AreaX</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.intersect(a2);
	 * 
	 *      a1(before)   intersect     a2         =     a1(after)
	 * 
	 *     ################     ################     ################
	 *     ##############         ##############       ############
	 *     ############             ############         ########
	 *     ##########                 ##########           ####
	 *     ########                     ########
	 *     ######                         ######
	 *     ####                             ####
	 *     ##                                 ##
	 * </pre>
	 * <p>
	 * This call does not immediately execute. It adds this operation to a queue
	 * of operations that are processed as required.
	 * 
	 * @param rhs
	 *            the <code>AreaX</code> to be intersected with this
	 *            <code>AreaX</code>
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void intersect(Shape rhs) {
		queue.add(new AreaXOperation(rhs, AreaXOperation.INTERSECT));
	}

	/**
	 * Sets the shape of this <code>AreaX</code> to the intersection of its
	 * current shape and the shape of the specified <code>AreaX</code>. The
	 * resulting shape of this <code>AreaX</code> will include only areas that
	 * were contained in both this <code>AreaX</code> and also in the specified
	 * <code>AreaX</code>.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.intersect(a2);
	 * 
	 *      a1(before)   intersect     a2         =     a1(after)
	 * 
	 *     ################     ################     ################
	 *     ##############         ##############       ############
	 *     ############             ############         ########
	 *     ##########                 ##########           ####
	 *     ########                     ########
	 *     ######                         ######
	 *     ####                             ####
	 *     ##                                 ##
	 * </pre>
	 * <p>
	 * This call immediately executes. It executes any pending operations first,
	 * and then executes this add.
	 * 
	 * @param rhs
	 *            the <code>AreaX</code> to be intersected with this
	 *            <code>AreaX</code>
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void executeIntersect(AreaX rhs) {
		body = getBody().intersect(rhs.getBody());
	}

	/**
	 * Sets the shape of this <code>AreaX</code> to be the combined area of its
	 * current shape and the shape of the specified <code>Shape</code>, minus
	 * their intersection. The resulting shape of this <code>AreaX</code> will
	 * include only areas that were contained in either this <code>AreaX</code>
	 * or in the specified <code>AreaX</code>, but not in both.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.exclusiveOr(a2);
	 * 
	 *        a1(before)    xor        a2         =     a1(after)
	 * 
	 *     ################     ################
	 *     ##############         ##############     ##            ##
	 *     ############             ############     ####        ####
	 *     ##########                 ##########     ######    ######
	 *     ########                     ########     ################
	 *     ######                         ######     ######    ######
	 *     ####                             ####     ####        ####
	 *     ##                                 ##     ##            ##
	 * </pre>
	 * <p>
	 * This call does not immediately execute. It adds this operation to a queue
	 * of operations that are processed as required.
	 * 
	 * @param rhs
	 *            the <code>AreaX</code> to be exclusive ORed with this
	 *            <code>AreaX</code>.
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void exclusiveOr(Shape rhs) {
		queue.add(new AreaXOperation(rhs, AreaXOperation.XOR));
	}

	/**
	 * Sets the shape of this <code>AreaX</code> to be the combined area of its
	 * current shape and the shape of the specified <code>AreaX</code>, minus
	 * their intersection. The resulting shape of this <code>AreaX</code> will
	 * include only areas that were contained in either this <code>AreaX</code>
	 * or in the specified <code>AreaX</code>, but not in both.
	 * 
	 * <pre>
	 *     // Example:
	 *     Area a1 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 0,8]);
	 *     Area a2 = new Area([triangle 0,0 =&gt; 8,0 =&gt; 8,8]);
	 *     a1.exclusiveOr(a2);
	 * 
	 *        a1(before)    xor        a2         =     a1(after)
	 * 
	 *     ################     ################
	 *     ##############         ##############     ##            ##
	 *     ############             ############     ####        ####
	 *     ##########                 ##########     ######    ######
	 *     ########                     ########     ################
	 *     ######                         ######     ######    ######
	 *     ####                             ####     ####        ####
	 *     ##                                 ##     ##            ##
	 * </pre>
	 * <p>
	 * This call immediately executes. It executes any pending operations first,
	 * and then executes this add.
	 * 
	 * @param rhs
	 *            the <code>AreaX</code> to be exclusive ORed with this
	 *            <code>AreaX</code>.
	 * @throws NullPointerException
	 *             if <code>rhs</code> is null
	 */
	public synchronized void executeExclusiveOr(AreaX rhs) {
		body = getBody().xor(rhs.getBody());
	}

	/**
	 * Removes all of the geometry from this <code>AreaX</code> and restores it
	 * to an empty area.
	 */
	public synchronized void reset() {
		queue.clear();
		body = emptyBody;
	}

	/**
	 * Tests whether this <code>AreaX</code> object encloses any area.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @return <code>true</code> if this <code>AreaX</code> object represents an
	 *         empty area; <code>false</code> otherwise.
	 */
	public synchronized boolean isEmpty() {
		processQueue();
		return (body.size() == 0);
	}

	/**
	 * Tests whether this <code>AreaX</code> consists entirely of straight edged
	 * polygonal geometry.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @return <code>true</code> if the geometry of this <code>AreaX</code>
	 *         consists entirely of line segments; <code>false</code> otherwise.
	 */
	public synchronized boolean isPolygonal() {
		processQueue();
		for (int a = 0; a < body.size(); a++) {
			if (body.get(a).getOrder() > 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether this <code>AreaX</code> is rectangular in shape.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @return <code>true</code> if the geometry of this <code>AreaX</code> is
	 *         rectangular in shape; <code>false</code> otherwise.
	 */
	public synchronized boolean isRectangular() {
		processQueue();
		int size = body.size();
		if (size == 0) {
			return true;
		}
		if (size > 3) {
			return false;
		}
		CurveX c1 = body.get(1);
		CurveX c2 = body.get(2);
		if (c1.getOrder() != 1 || c2.getOrder() != 1) {
			return false;
		}
		if (c1.getXTop() != c1.getXBot() || c2.getXTop() != c2.getXBot()) {
			return false;
		}
		if (c1.getYTop() != c2.getYTop() || c1.getYBot() != c2.getYBot()) {
			// One might be able to prove that this is impossible...
			return false;
		}
		return true;
	}

	/**
	 * Tests whether this <code>AreaX</code> is comprised of a single closed
	 * subpath. This method returns <code>true</code> if the path contains 0 or
	 * 1 subpaths, or <code>false</code> if the path contains more than 1
	 * subpath. The subpaths are counted by the number of
	 * {@link PathIterator#SEG_MOVETO SEG_MOVETO} segments that appear in the
	 * path.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @return <code>true</code> if the <code>AreaX</code> is comprised of a
	 *         single basic geometry; <code>false</code> otherwise.
	 */
	public synchronized boolean isSingular() {
		processQueue();
		if (body.size() < 3) {
			return true;
		}
		for (int a = 1; a < body.size(); a++) {
			if (body.get(a).getOrder() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a high precision bounding {@link Rectangle2D} that completely
	 * encloses this <code>AreaX</code>.
	 * <p>
	 * The Area class will attempt to return the tightest bounding box possible
	 * for the Shape. The bounding box will not be padded to include the control
	 * points of curves in the outline of the Shape, but should tightly fit the
	 * actual geometry of the outline itself.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @return the bounding <code>Rectangle2D</code> for the <code>AreaX</code>.
	 */
	public synchronized Rectangle2D getBounds2D() {
		processQueue();
		return body.getBounds2D(null);
	}

	/**
	 * Returns a bounding {@link Rectangle} that completely encloses this
	 * <code>AreaX</code>.
	 * <p>
	 * The Area class will attempt to return the tightest bounding box possible
	 * for the Shape. The bounding box will not be padded to include the control
	 * points of curves in the outline of the Shape, but should tightly fit the
	 * actual geometry of the outline itself. Since the returned object
	 * represents the bounding box with integers, the bounding box can only be
	 * as tight as the nearest integer coordinates that encompass the geometry
	 * of the Shape.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @return the bounding <code>Rectangle</code> for the <code>AreaX</code>.
	 */
	public synchronized Rectangle getBounds() {
		processQueue();
		return body.getBounds(null);
	}

	/**
	 * Returns an exact copy of this <code>AreaX</code> object.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @return Created clone object
	 */
	@Override
	public synchronized AreaX clone() {
		processQueue();
		return new AreaX(this);
	}

	/**
	 * Tests whether the geometries of the two <code>AreaX</code> objects are
	 * equal. This method will return false if the argument is null.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @param other
	 *            the <code>AreaX</code> to be compared to this
	 *            <code>AreaX</code>
	 * @return <code>true</code> if the two geometries are equal;
	 *         <code>false</code> otherwise.
	 */
	public synchronized boolean equals(AreaX other) {
		return getBody().equals(other.getBody());
	}

	/**
	 * Transforms the geometry of this <code>AreaX</code> using the specified
	 * {@link AffineTransform}. The geometry is transformed in place, which
	 * permanently changes the enclosed area defined by this object.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @param t
	 *            the transformation used to transform the area
	 * @throws NullPointerException
	 *             if <code>t</code> is null
	 */
	public synchronized void transform(AffineTransform t) {
		if (t == null) {
			throw new NullPointerException("transform must not be null");
		}

		body = getBody().transform(t);
	}

	/**
	 * Creates a new <code>AreaX</code> object that contains the same geometry
	 * as this <code>AreaX</code> transformed by the specified
	 * <code>AffineTransform</code>. This <code>AreaX</code> object is
	 * unchanged.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @param t
	 *            the specified <code>AffineTransform</code> used to transform
	 *            the new <code>AreaX</code>
	 * @throws NullPointerException
	 *             if <code>t</code> is null
	 * @return a new <code>AreaX</code> object representing the transformed
	 *         geometry.
	 */
	public synchronized AreaX createTransformedArea(AffineTransform t) {
		AreaX a = new AreaX(this);
		a.transform(t);
		return a;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This forces any pending operations to be performed.
	 */
	public synchronized boolean contains(double x, double y) {
		return getBody().contains(x, y);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This forces any pending operations to be performed.
	 */
	public synchronized boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This forces any pending operations to be performed.
	 */
	public synchronized boolean contains(double x, double y, double w, double h) {
		if (w < 0 || h < 0) {
			return false;
		}
		processQueue();
		if (!body.boundsContains(x, y, w, h)) {
			return false;
		}
		CrossingsX c = CrossingsX.findCrossings(body, x, y, x + w, y + h);
		return (c != null && c.covers(y, y + h));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This forces any pending operations to be performed.
	 */
	public synchronized boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This forces any pending operations to be performed.
	 */
	public synchronized boolean intersects(double x, double y, double w,
			double h) {
		if (w < 0 || h < 0) {
			return false;
		}
		processQueue();
		if (!body.boundsIntersects(x, y, w, h)) {
			return false;
		}
		CrossingsX c = CrossingsX.findCrossings(body, x, y, x + w, y + h);
		return (c == null || !c.isEmpty());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This forces any pending operations to be performed.
	 */
	public synchronized boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Creates a {@link PathIterator} for the outline of this <code>AreaX</code>
	 * object. This <code>AreaX</code> object is unchanged.
	 * <p>
	 * This forces any pending operations to be performed.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the
	 *            coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @return the <code>PathIterator</code> object that returns the geometry of
	 *         the outline of this <code>AreaX</code>, one segment at a time.
	 */
	public synchronized PathIterator getPathIterator(AffineTransform at) {
		processQueue();
		return body.getPathIterator(at);
	}

	/**
	 * Creates a <code>PathIterator</code> for the flattened outline of this
	 * <code>AreaX</code> object. Only uncurved path segments represented by the
	 * SEG_MOVETO, SEG_LINETO, and SEG_CLOSE point types are returned by the
	 * iterator. This <code>AreaX</code> object is unchanged.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the
	 *            coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @param flatness
	 *            the maximum amount that the control points for a given curve
	 *            can vary from colinear before a subdivided curve is replaced
	 *            by a straight line connecting the end points
	 * @return the <code>PathIterator</code> object that returns the geometry of
	 *         the outline of this <code>AreaX</code>, one segment at a time.
	 */
	public synchronized PathIterator getPathIterator(AffineTransform at,
			double flatness) {
		return new FlatteningPathIterator(getPathIterator(at), flatness);
	}
}