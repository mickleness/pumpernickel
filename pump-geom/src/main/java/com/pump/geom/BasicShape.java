/*
 * @(#)BasicShape.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
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
package com.pump.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedList;

import com.pump.geom.intersect.IntersectionIdentifier;
import com.pump.geom.intersect.IntersectionIdentifier.Return;
import com.pump.math.MathG;

/** A faster, open-source alternative to the Area class.
 *
 * It might even be finished done day, too.
 */
public class BasicShape implements Shape {
	@SuppressWarnings("unused")
	private static void main(String[] args) {
		Rectangle r1 = new Rectangle(0,50,100,20);
		Rectangle r2 = new Rectangle(50,0,20,100);
		BasicShape s1 = new BasicShape(r1);
		BasicShape s2 = new BasicShape(r2);
		System.out.println(s1);
		System.out.println(s2);
		s1.add(s2);
		System.out.println(s1);
	}

	//TODO: once methods stabilize, address synchronizing and thread safety

	/** A constant value just a tiny amount less than zero */
	private static double ZERO = -.000001;
	/** A constant value just a tiny amount greater than 1 */
	private static double ONE = 1.000001;

	private static final int ADD = 0;
	private static final int SUBTRACT = 1;
	private static final int INTERSECT = 2;

	/** Return true if two values are near each other.
	 * 
	 * @param v1 the first value
	 * @param v2 the second value
	 * @param tolerance the acceptable tolerance.  Must be positive.
	 * @return true if abs(v1-v2)&lt;=tolerance
	 */
	protected static boolean near(double v1,double v2,double tolerance) {
		double d = v1-v2;
		if(d<0) d = -d;
		return d<=tolerance;
	}

	protected static String toString(double[] array) {
		return toString(array,0,array.length);
	}

	/** This lists an array of double values as a String.
	 * Mostly used for debugging.
	 * @param array
	 * @param offset
	 * @param len
	 */
	protected static String toString(double[] array,int offset,int len) {
		StringBuffer sb = new StringBuffer();
		for(int a = 0; a<len; a++) {
			if(a==0) {
				sb.append("{ ");
				sb.append(array[offset+a]);
			} else {
				sb.append(", ");
				sb.append(array[offset+a]);
			}
		}
		sb.append("}");
		return sb.toString();
	}

	/** The segment data of this shape. */
	BasicShapeSegment list;

	/** These are WeakReferences to PathIterators that
	 * are relying on THIS object to iterate through data.
	 * If we're asked to transform, or add, or clip, or somehow
	 * modify this object's shape data, we need to grab
	 * these iterators and give them our cloned path data
	 * so they don't need this object anymore.
	 */
	LinkedList<WeakReference<BasicShapeIterator>> dependentIterators;

	/** The bounds of this shape. */
	double minX, maxX, minY, maxY;

	/** Creates a <code>BasicShape</code> from a shape.
	 * 
	 * <p>If the shape is a simple shape in the <code>java.awt.geom</code>
	 * package, then you should use a constructor that accepts a
	 * <code>Shape</code> argument instead of a <code>PathIterator</code>
	 * argument.
	 * 
	 * @param shape the shape data to copy.
	 */
	public BasicShape(Shape shape) {
		this(shape,null);
	}

	/** Creates a <code>BasicShape</code> from a shape.
	 * 
	 * <p>If the shape is a simple shape in the <code>java.awt.geom</code>
	 * package, then you should use a constructor that accepts a
	 * <code>Shape</code> argument instead of a <code>PathIterator</code>
	 * argument.
	 * 
	 * @param shape the shape data to copy.
	 * @param transform an optional transform to apply to the incoming shape.
	 */
	public BasicShape(Shape shape,AffineTransform transform) {
		this(shape.getPathIterator(transform),
				//some special shapes won't overlap themselves anyway,
				//so we never need to convert them from WIND_NON_ZERO
				//to WIND_EVEN_ODD
				(shape instanceof CubicCurve2D) ||
				(shape instanceof Line2D) ||
				(shape instanceof QuadCurve2D) ||
				(shape instanceof RectangularShape) //includes arc2d, ellipse2d, rectangle2d, and roundrectangle2D
		);
	}

	/** Creates a <code>BasicShape</code> from a path.
	 * 
	 * @param i path data.
	 */
	public BasicShape(PathIterator i) {
		this(i,false);
	}

	/** Creates a <code>BasicShape</code> from a path.
	 * 
	 * @param i path data.
	 * @param ignoreWinding if a path is WIND_NON_ZERO, then it normally
	 * will be converted.  However some shapes (rectangles, circles, etc)
	 * don't need to be converted, so if you pass <code>true</code> here
	 * then that conversion will be skipped.  By default you should pass
	 * <code>false</code> if you don't know the nature of the 
	 * <code>PathIterator</code> you're working with.
	 */
	protected BasicShape(PathIterator i,boolean ignoreWinding) {
		list = BasicShapeSegment.read(i);
		if(i.getWindingRule()==PathIterator.WIND_NON_ZERO && ignoreWinding==false) {
			convertToWindEvenOdd();
		}
		updateBounds();
	}

	protected void convertToWindEvenOdd() {
		//TODO
	}

	/** Creates a BasicShape from a serialized string.
	 *
	 * @param s this string should be the result of a former call to:
	 * <br><code>myBasicShape.toString()</code>
	 */
	public BasicShape(String s) {
		this(ShapeStringUtils.createPathIterator(s));
	}

	/** Clones a BasicShape.
	 */
	public BasicShape(BasicShape s) {
		if(s.list!=null)
			list = (BasicShapeSegment)s.list.clone();

		minX = s.minX;
		maxX = s.maxX;
		minY = s.minY;
		maxY = s.maxY;
	}

	/** This method should be called when this shape has
	 * undergone a transformation.
	 */
	protected void updateBounds() {
		if(list==null) {
			minX = maxX = minY = maxY = 0;
			return;
		}
		list.updateBounds();
		minX = list.minX;
		maxX = list.maxX;
		minY = list.minY;
		maxY = list.maxY;
		BasicShapeSegment current = list.next;
		while(current!=null) {
			if(current.type!=PathIterator.SEG_CLOSE) {
				if(current.minX<minX)
					minX = current.minX;
				if(current.maxX>maxX)
					maxX = current.maxX;
				if(current.minY<minY)
					minY = current.minY;
				if(current.maxY>maxY)
					maxY = current.maxY;
			}
			current = current.next;
		}
	}

	public boolean contains(Point2D p) {
		return contains(p.getX(),p.getY(),PathIterator.WIND_EVEN_ODD);
	}

	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public boolean contains(double x, double y) {
		return contains(x,y,PathIterator.WIND_EVEN_ODD);
	}

	protected static boolean contains(BasicShapeSegment path,double x,double y,int windingRule,boolean includeEdges) {

		int crossings = countCrossings(path,x,y,includeEdges);

		if(windingRule==PathIterator.WIND_EVEN_ODD)
			return ((crossings & 1) != 0);
		return crossings!=0;
	}
	
	protected boolean contains(double x,double y,int windingRule) {
		if(!(minX<=x && x<=maxX && minY<=y && y<=maxY)) {
			return false;
		}
		
		return contains(list,x,y,windingRule,true);
	}

	/** Adds the argument shape to this shape.
	 * <P>If shape B is added to shape A, then
	 * the resulting shape will contain points in either
	 * A or B.
	 * @param shape the shape to add.
	 */
	public void add(BasicShape shape) {
		if(list==null) {
			if(shape.list==null)
				return;
			list = (BasicShapeSegment)shape.list.clone();
			updateBounds();
			return;
		}

		performOperation(shape,ADD);
	}
	/** Subtracts the argument shape from this shape.
	 * <P>If shape B is subtracted from shape A, then
	 * the resulting shape will contain only points in
	 * A that do not also lie in B.
	 * @param shape the shape to subtract.  For best performance
	 * this should be a BasicShape.
	 */
	public void subtract(BasicShape shape) {
		if(list==null)
			return;

		performOperation(shape,SUBTRACT);
	}

	/** Intersects the argument shape with this shape.
	 * This is also referred to as "clipping".
	 * <P>If shape A is intersected with shape B, then
	 * the resulting shape will contain only points in
	 * both A and B.
	 * @param shape the shape to intersect with.  For best performance
	 * this should be a BasicShape.
	 */
	public void intersect(BasicShape shape) {
		if(list==null)
			return;

		performOperation(shape,INTERSECT);
	}

	private static final int UNDEFINED = 0;
	private static final int FORWARD = 1;
	private static final int BACKWARD = 2;
	private static final int AVOID = 3;
	
	static class Crossroad {
		Intersection center;
		
		/** An arbitrary point behind the intersection
		 * Here "behind" the intersection means
		 * "as t decreases".
		 */
		Point2D prevP = new Point2D.Double();

		/** An arbitrary point in front of the intersection
		 * Here "in front of" the intersection means
		 * "as t increases".
		 */
		Point2D nextP = new Point2D.Double();
		
		void init(Intersection newCenter) {
			double prevT, nextT;
			BasicShapeSegment nextS, prevS;
			
			center = newCenter;
			if(center.next==null) {
				nextS = center.s1;
				nextT = (center.t1+1)/2;
				if(nextT>.9999) {
					//this intersection is dangerously close to
					//one, so let's look ahead a little:

					nextS = center.s1.iterateNextInSubPath();
					if(nextS.i==null) {
						nextT = .5;
					} else {
						nextT = nextS.i.t1/2.0;
					}
					//TODO: if nextS.i.t1=0, do we have a problem?
				}
			} else {
				nextS = center.s1;
				nextT = (center.t1*17+center.next.t1*19)/36;
			}
			
			if(center.prev==null) {
				prevS = center.s1;
				prevT = (center.t1+0)/2;
				if(prevT<.0001) {
					//this intersection is dangerously close to
					//zero, so let's look further back:

					prevS = center.s1.iterateBackInSubPath();
					if(prevS.i==null) {
						prevT = .5;
					} else {
						prevT = (nextS.i.getTail().t1+1)/2.0;
					}
					//TODO: if nextS.i.getTail().t1=1, do we have a problem?
				}
			} else {
				prevS = center.s1;
				prevT = (center.t1*17+center.prev.t1*19)/36;
			}
			
			nextS.getPoint(nextP, nextT);
			prevS.getPoint(prevP, prevT);
		}
	}
    
    private boolean containsForAdd( BasicShapeSegment s, Point2D p) {
    	return contains(s, p.getX(), p.getY(), PathIterator.WIND_EVEN_ODD, true);
    }

	private void performOperation(BasicShape shape,int op) {

		releaseIterators();

		// This may be the bulk of the hard work:
		Intersection i = identifyIntersections(shape);

		//TODO: memory allocation
		Crossroad current = new Crossroad();
		Crossroad other = new Crossroad();
		Point2D p = new Point2D.Double();

		int lastDirection = UNDEFINED;
		
		try {
			int direction = UNDEFINED;

			BasicShapeSegment path = null;
			boolean pathStarted = false;
			while(i!=null) {
				boolean newPath = true;
				while(i.claimed==false) {
					if(direction==UNDEFINED) {
						/** Every intersection is like a crossroads.
						 * In the typical case, there are 4 directions
						 * in an intersection.  We just came from 1,
						 * so there are 3 choices to work with.
						 * 
						 * However.  There are always 4 paths, but
						 * sometimes those paths will completely overlap.
						 * This means, visually, we're looking at 3
						 * paths, maybe 2.  I guess 1 path in
						 * an extreme case?
						 * 
						 * So in this method, "i" is the dot that
						 * is the intersection of these crossroads.
						 */

						current.init(i);
						other.init(i.complement);

						/////////// Now pick which crossroad to follow, based on the operation:

						if(op==ADD) {
							//always pick one that the other doesn't contain: that way you're adding
							//as much as possible
							if(containsForAdd( i.s2, current.nextP )==false ) {
								direction = FORWARD;
							} else if(containsForAdd( i.s2, current.prevP )==false ) {
								direction = BACKWARD;
							} else if(containsForAdd(i.s1, other.nextP )==false && lastDirection!=BACKWARD ){
								i = i.complement;
								direction = FORWARD;
							} else if(containsForAdd(i.s1, other.prevP )==false && lastDirection!=FORWARD ){
								i = i.complement;
								direction = BACKWARD;
							} else if(lastDirection!=UNDEFINED) {
								//this case can occur with overlapping segments
								i = i.complement;
								direction = lastDirection;
							} else {
								//forward
								direction = FORWARD;
							}
						} //TODO: other ops (intersect, subtract)
						
						if(newPath && direction!=AVOID) {
							newPath = false;
							if(pathStarted) {
								path = path.close();
							}
							i.s1.getPoint(p,i.t1);
							if(path==null) {
								path = new BasicShapeSegment(PathIterator.SEG_MOVETO,
										new double[] {p.getX(), p.getY()});
							} else {
								path = path.moveTo(p);
							}
							pathStarted = true;
						}
					}
					
					if(direction==FORWARD || direction==BACKWARD)
						lastDirection = direction;
					
					/** This is the easy-ish part.
					 * We know where we are (at the
					 * Intersection "i"), and we know
					 * what direction we should go:
					 * FORWARD or BACKWARD.
					 * 
					 */
					
					
					if(direction==FORWARD) {
						if(i.next!=null) {
							//very simple: append just a small
							//snippet of a segment from t = [t1, i.next.t1].
							i.claimed = true;
							i.complement.claimed = true;
							
							path = path.append(i.s1,i.t1,i.next.t1);

							i = i.next;
							i = i.complement;
							direction = UNDEFINED;
						} else {
							//we just keep charging forward until
							//we find a new Intersection.
							path = path.append(i.s1,i.t1,1);

							i.claimed = true;
							i.complement.claimed = true;
							
							//Intersection originalI = i;
							BasicShapeSegment t = i.s1;

							appendContinuousPaths : while(true) {
								t = t.next;
								if(t.type==PathIterator.SEG_CLOSE) {
									t = t.rewindToStartOfSubPath().next;
								}
								if(t.i!=null)
									break appendContinuousPaths;
								path = path.append(t,0,1);
							}

							//so now we're at a t with an i:
							i = t.i;
							
							//Pasted from first draft, I forget
							//why this is here.  Anyone?
							//Commented out unless someone finds a
							//use for it.
							//If identified, be sure to see identical piece
							//below in the BACKWARD movement.
							//if(i==originalI) {
							//	i.claimed = true;
							//	i.complement.claimed = false;
							//}
								
							
							path = path.append(i.s1,0,i.t1);
							
							//small but vital part:
							//switch paths!
							i = i.complement;
							direction = UNDEFINED;
						}
					} else if(direction==BACKWARD) {
						//same as above, except backwards
						if(i.prev!=null) {
							i.claimed = true;
							i.complement.claimed = true;
							
							path = path.append(i.s1,i.t1,i.prev.t1);

							i = i.prev;
							i = i.complement;
							direction = UNDEFINED;
						} else {
							path = path.append(i.s1,i.t1,0);
							
							i.claimed = true;
							i.complement.claimed = true;

							//Intersection originalI = i;
							BasicShapeSegment t = i.s1;

							appendContinuousPaths : while(true) {
								t = t.prev;
								if(t.type==PathIterator.SEG_MOVETO) {
									t = t.fastForwardToEndOfSubPath().prev;
								}
								if(t.i!=null)
									break appendContinuousPaths;
								path = path.append(t,1,0);
							}
							
							i = t.i.getTail();
							
							//see comments above for the FORWARD movement
							//if(i==originalI) {
							//	i.claimed = true;
							//	i.complement.claimed = false;
							//}
							path = path.append(i.s1,1,i.t1);
							i = i.complement;
							direction = UNDEFINED;
						}
					}
				}
				i = findUnclaimedIntersection(); //find other paths
				direction = UNDEFINED;
			}
			if(pathStarted)
				path = path.close();

			if(op==ADD || op==SUBTRACT) {
				path = addUntouchedPaths(path,shape,this,TYPE_UNTOUCHED_NOT_IN_OTHER);
			} else if(op==INTERSECT) {
				path = addUntouchedPaths(path,shape,this,TYPE_UNTOUCHED_ONLY_IN_OTHER);
			}
			if(shape!=this) {
				if(op==ADD) {
					path = addUntouchedPaths(path,this,shape,TYPE_UNTOUCHED_NOT_IN_OTHER);
				} else if(op==SUBTRACT || op==INTERSECT) {
					path = addUntouchedPaths(path,this,shape,TYPE_UNTOUCHED_ONLY_IN_OTHER);
				}
			}

			//TODO: replacing the "list" field and calling updateBounds()
			//might be re-thinked if this method creates a NEW BasicShape
			//instead of modifying this one.
			if(path==null) {
				list = null;
			} else {
				list = path.getHead();
			}
			//TODO: release all intersections, too.
		} finally {
			updateBounds();
		}
	}
	

    private static final int TYPE_UNTOUCHED_ALL = 0;
    private static final int TYPE_UNTOUCHED_NOT_IN_OTHER = 1;
    private static final int TYPE_UNTOUCHED_ONLY_IN_OTHER = 2;
    
    /** Go through all the paths of a shape and if we find one without an
     * Intersection, add that path to dest.
     * @param s
     * @param other if the path in s starts with a point in "other", then it is NOT added
     * @param dest
     */
    private static BasicShapeSegment addUntouchedPaths(BasicShapeSegment path, BasicShape thisShape,BasicShape otherShape,int addType) {
        BasicShapeSegment t = thisShape.list;
        BasicShapeSegment lastMoveTo = null;
        boolean foundIntersection = false;
        boolean sameShape = thisShape==otherShape;
        while(t!=null) {
            if(t.type==PathIterator.SEG_MOVETO || t.next==null) {
                //we just finished a full path
                if(foundIntersection==false && lastMoveTo!=null) {
                    //we just finished a path that didn't intersect anything!
                    //we have to add it to dest.
                	BasicShapeSegment t2 = lastMoveTo;
                    boolean addThis = sameShape;
                    if(addType==TYPE_UNTOUCHED_ALL) {
                        addThis = true;
                    } else if(addType==TYPE_UNTOUCHED_NOT_IN_OTHER) {
                        addThis = sameShape || (otherShape.contains(t2.data[0],t2.data[1])==false);
                    } else if(addType==TYPE_UNTOUCHED_ONLY_IN_OTHER) {
                        addThis = sameShape || (otherShape.contains(t2.data[0],t2.data[1]));
                    }
                    if(addThis) {
                    	if(path==null) {
                    		path = new BasicShapeSegment(PathIterator.SEG_MOVETO,
                    				new double[] {t2.data[0], t2.data[1]});
                    	} else {
                    		path = path.moveTo(t2.data[0], t2.data[1]);
                    	}
                        t2 = t2.next;
                        boolean closed = false;
                        
                        while(!(t2.type==PathIterator.SEG_MOVETO || t2.next==null)) {
                            if(t2.type==PathIterator.SEG_LINETO) {
                            	path = path.lineTo(t2.data[0], t2.data[1]);
                            } else if(t2.type==PathIterator.SEG_QUADTO) {
                            	path = path.quadTo(t2.data[0],t2.data[1],
                                        t2.data[2],t2.data[3] );
                            } else if(t2.type==PathIterator.SEG_CUBICTO) {
                                path = path.cubicTo(t2.data[0], t2.data[1],
                                        t2.data[2], t2.data[3],
                                        t2.data[4], t2.data[5] );
                            } else if(t2.type==PathIterator.SEG_CLOSE) {
                                path = path.close();
                                closed = true;
                            }
                            t2 = t2.next;
                        }
                        if(!closed)
                        	path = path.close();
                    }
                }
                
                lastMoveTo = t;
                foundIntersection = false;
            } else {
                if(t.i!=null)
                    foundIntersection = true;
            }
            t = t.next;
        }
        return path;
    }

    /** @return the first available unclaimed Intersection object in this shape */
    private Intersection findUnclaimedIntersection() {
        BasicShapeSegment t = list;
        while(t!=null) {
            if(t.i!=null) {
                Intersection i = t.i;
                while(i!=null && i.claimed==true) {
                    i = i.next;
                }
                if(i!=null && i.claimed==false)
                    return i;
            }
            t = t.next;
        }
        return null;
    }

	/** This counts the number of crossings from (-infinity,y) to (x,y).
	 * <P>This total is used in combination with the winding rule to determine
	 * when a point is inside a shape.
	 * 
	 * @param includeEdges whether a point exactly on the edge of the shape counts.
	 */
	protected static int countCrossings(BasicShapeSegment t,double x,double y,boolean includeEdges) {
		t = t.getHead();

		//draw an imaginary line from (-infinity,y) to (x,y)
		//and count how many crossings we have

		int crossings = 0;
		double lastX = 0;
		double lastY = 0;	

		double t2;

		double ay, ax, by, cy, bx, cx, dx, dy, det;
		double myX;
		int i;
		double curvature;

		double[] array = new double[4];
		double[] results = new double[4];

		while(t!=null) {
			if(t.type==PathIterator.SEG_MOVETO || t.type==PathIterator.SEG_CLOSE) {
				//not an issue: movetos, well, they're nothing
				//and closes: when a BasicShape is constructed we close all the subpaths
				//with lines, so this is moot too.
			} else {
				//constrain our search:
				if( (includeEdges && y>=t.minY && y<=t.maxY && x>=t.minX) ||
						((!includeEdges) && y>t.minY && y<t.maxY && x>t.minX) ) {
					//now we have to start caring about the segment itself
					if(t.type==PathIterator.SEG_LINETO) {
						if (lastY > t.data[1]) {
							t2 = (y - t.data[1]) / (lastY - t.data[1]);
							myX = t.data[0] + t2 * (lastX - t.data[0]);
							if( (includeEdges && myX <= x) ||
									((!includeEdges) && myX < x)) {
								crossings--;
							}
						} else {
							t2 = (y - lastY) / (t.data[1] - lastY);
							myX = lastX + t2 * (t.data[0] - lastX);
							if( (includeEdges && myX <= x) ||
									((!includeEdges) && myX < x) ) {
								crossings++;
							}
						}
					} else if(t.type==PathIterator.SEG_QUADTO) {
						ay = lastY-2*t.data[1]+t.data[3];
						by = -2*lastY+2*t.data[1];
						cy = lastY-y;

						ax = lastX-2*t.data[0]+t.data[2];
						bx = -2*lastX+2*t.data[0];
						cx = lastX;

						det = by*by-4*ay*cy;
						if(det<=0) {
							//do nothing; 1 1-solution parabola won't have crossings
							i = 0;
						} else {
							det = Math.sqrt(det);
							i = 2;
							array[0] = (-by+det)/(2*ay);
							array[1] = (-by-det)/(2*ay);
						}
						for(int a = 0; a<i; a++) {
							if(array[a]>=0 && array[a]<=1) {
								myX = ((ax*array[a]+bx)*array[a]+cx);
								if ( (includeEdges && myX <= x) ||
										((!includeEdges) && myX < x) ) {
									curvature = (2*ay*array[a] + by);
									if (curvature > 0) {
										crossings++;
									} else if (curvature < 0) {
										crossings--;
									}
								}
							}
						}
					} else if(t.type==PathIterator.SEG_CUBICTO) {
						ay = -lastY+3*t.data[1]-3*t.data[3]+t.data[5];
						by = 3*lastY-6*t.data[1]+3*t.data[3];
						cy = -3*lastY+3*t.data[1];
						dy = lastY-y;

						array[3] = ay;
						array[2] = by;
						array[1] = cy;
						array[0] = dy;
						i = CubicCurve2D.solveCubic(array,results);

						ax = -lastX+3*t.data[0]-3*t.data[2]+t.data[4];
						bx = 3*lastX-6*t.data[0]+3*t.data[2];
						cx = -3*lastX+3*t.data[0];
						dx = lastX;

						for(int a = 0; a<i; a++) {
							if(results[a]>=ZERO && results[a]<=ONE) {
								myX = (((ax*results[a]+bx)*results[a]+cx)*results[a]+dx);
								if ( (includeEdges && myX <= x) ||
										((!includeEdges) && myX < x) ) {
									curvature = ((3*ay*results[a] + 2*by)*results[a]+cy);

									if (curvature > 0) {
										crossings++;
									} else if (curvature < 0) {
										crossings--;
									}
								}
							}
						}
					}
				}
			}
			if(t.data.length>0) {
				lastX = t.data[t.data.length-2];
				lastY = t.data[t.data.length-1];
			}
			t = t.next;
		}

		return crossings;
	}

	public boolean contains(double x, double y, double w, double h) {


		/** We'll calculate this by looking at 3 criteria:
		 * 1.  Do any path segments cross the edges of the rectangle?  If so, we return false.
		 * 2.  Do any path segments lie entirely inside the rectangle?  If so, we return false.
		 * 3.  Is at least 1 point inside this rectangle?  If not, we return false.
		 */

		boolean contains = (x >= minX &&
				y >= minY &&
				(x + w) <= maxX &&
				(y + h) <= maxY );

		if(contains==false) {
			return false;
		}

		//this handles the first 2 criteria
		if(identifyCrossings(x,y,w,h)) {
			return false;
		}

		/** We've established
		 * that this rectangle is either 100% inside or 100% outside
		 * of this shape.  (There are no segments of this shape crossing
		 * this rectangle, or inside this rectangle.)
		 * 
		 * Last test: are we inside or outside?
		 */
		if(contains(x+w/2,y+h/2)==false) {
			return false;
		}
		return true;
	}


	/** This checks 2 things:
	 * 1.  Does this shape have any segments that lie completely inside the bounds provided?
	 * 2.  Does this shape have any segments that cross the bounds provided?
	 * 
	 */
	protected boolean identifyCrossings(double x,double y,double w,double h) {
		if(list==null)
			return false;

		BasicShapeSegment t;
		double x1;
		double x2;
		double y1;
		double y2;
		double lastX = 0;
		double lastY = 0;

		lastX = 0;
		lastY = 0;

		x1 = x;
		x2 = (x+w);
		y1 = y;
		y2 = (y+h);

		double y1e = y-.0001f;
		double y2e = (y+h)+.0001f;

		double t2;
		int i;

		double[] eqn = new double[4];
		double[] results = new double[3];
		double[] array = new double[12];

		//first, it'll be simplest to do the check based on bounds.
		//The only thing we HAVE to catch now is the case
		//when a segment is 100% inside the rectangle we're checking.
		//If a segment crosses the rectangle we'll see that in the next
		//check.
		//HOWEVER, there are some very light calls we can make right here
		//that can save us the trouble later...
		t = list;
		while(t!=null) {
			if(t.type!=PathIterator.SEG_CLOSE) {
				if(x1<t.minX && t.maxX<x2) {
					if(y1<t.minY && t.minY<y2) {
						return true;
					}
					if(y1<t.maxY && t.maxY<y2) {
						return true;
					}
					if(t.minY<=y1 && y2<=t.maxY) {
						return true;
					}
				}
				if(y1<t.minY && t.maxY<y2) {
					if(x1<t.minX && t.minX<x2) {
						return true;
					}
					if(x1<t.maxX && t.maxX<x2) {
						return true;
					}
					if(t.minX<=x1 && x2<=t.maxX) {
						return true;
					}
				}
			}
			t = t.next;
		}
		int i2;
		t = list;
		double ay, ax, by, cy, dy, bx, cx, dx, det;
		double myX, myY;
		int state = -1;
		int myState;

		double bx0 = minX;
		double bx1 = maxX;
		double by0 = minY;
		double by1 = maxY;
		while(t!=null) {
			if(t.maxX > bx0 && t.maxY > by0 &&
					t.minX < bx1 && t.minY < by1) {
				//only study a segment if we know it intersects our rectangle of interest

				if(t.type==PathIterator.SEG_LINETO) {
					ay = t.data[1]-lastY;
					ax = t.data[0] - lastX;

					//look at horizontal lines:
						if(ay!=0) {
							// if they were equal, we'd have a horizontal line, which
							// -- when you're looking for vertical intersections --
							// is not grounds to say this shape doesn't lie inside a rectangle

							t2 = (y1-lastY)/ay;
							if(t2>0 && t2<1) {
								x = ax*t2+lastX;
								if(x1<x && x<x2) {
									return true;
								}
							}

							//the bottom line:
								t2 = (y2-lastY)/ay;
							if(t2>0 && t2<1) {
								x = ax*t2+lastX;
								if(x1<x && x<x2) {
									return true;
								}
							}
						}

						//look at vertical lines:
						if(ax!=0) {
							//left line:
							t2 = (x1-lastX)/ax;
							if(t2>0 && t2<1) {
								y = ay*t2+lastY;
								if(y1<y && y<y2) {
									return true;
								}
							}

							//the right line:
								t2 = (x2-lastX)/ax;
							if(t2>0 && t2<1) {
								y = ay*t2+lastY;
								if(y1<y && y<y2) {
									return true;
								}
							}
						}
				} else if(t.type==PathIterator.SEG_QUADTO) {
					ax = lastX-2*t.data[0]+t.data[2];
					bx = -2*lastX+2*t.data[0];
					cx = lastX;

					ay = lastY-2*t.data[1]+t.data[3];
					by = -2*lastY+2*t.data[1];
					cy = lastY;

					det = by*by-4*ay*(cy-y1);
					if(det>0) {
						//if det < 0 we have no matched
						//if det == 0, the parabola just TOUCHES
						//on the boundary, and isn't grounds to return true

						det = Math.sqrt(det);

						//root #1:
						t2 = (-by+det)/(2*ay);
						if(t2>0 && t2<1) {
							x = (ax*t2+bx)*t2+cx;
							if(x1<x && x<x2) {
								return true;
							}
						}
						//root #2:
							t2 = (-by-det)/(2*ay);
						if(t2>0 && t2<1) {
							x = (ax*t2+bx)*t2+cx;
							if(x1<x && x<x2) {
								return true;
							}
						}
					}

					det = by*by-4*ay*(cy-y2);
					if(det>0) {
						det = Math.sqrt(det);

						//root #1:
							t2 = (-by+det)/(2*ay);
							if(t2>0 && t2<1) {
								x = (ax*t2+bx)*t2+cx;
								if(x1<x && x<x2) {
									return true;
								}
							}
							//root #2:
								t2 = (-by-det)/(2*ay);
							if(t2>0 && t2<1) {
								x = (ax*t2+bx)*t2+cx;
								if(x1<x && x<x2) {
									return true;
								}
							}
					}

					//now the vertical lines:
					det = bx*bx-4*ax*(cx-x1);
					if(det>0) {
						det = Math.sqrt(det);

						//root #1:
							t2 = (-bx+det)/(2*ax);
							if(t2>0 && t2<1) {
								y = (ay*t2+by)*t2+cy;
								if(y1<y && y<y2) {
									return true;
								}
							}
							//root #2:
								t2 = (-bx-det)/(2*ax);
							if(t2>0 && t2<1) {
								y = (ay*t2+by)*t2+cy;
								if(y1<y && y<y2) {
									return true;
								}
							}
					}

					det = bx*bx-4*ax*(cx-x2);
					if(det>0) {
						det = Math.sqrt(det);

						//root #1:
							t2 = (-bx+det)/(2*ax);
							if(t2>0 && t2<1) {
								y = (ay*t2+by)*t2+cy;
								if(y1<y && y<y2) {
									return true;
								}
							}
							//root #2:
								t2 = (-bx-det)/(2*ax);
							if(t2>0 && t2<1) {
								y = (ay*t2+by)*t2+cy;
								if(y1<y && y<y2) {
									return true;
								}
							}
					}
				} else if(t.type==PathIterator.SEG_CUBICTO) {
					ay = -lastY+3*t.data[1]-3*t.data[3]+t.data[5];
					by = 3*lastY-6*t.data[1]+3*t.data[3];
					cy = -3*lastY+3*t.data[1];
					dy = lastY;

					ax = -lastX+3*t.data[0]-3*t.data[2]+t.data[4];
					bx = 3*lastX-6*t.data[0]+3*t.data[2];
					cx = -3*lastX+3*t.data[0];
					dx = lastX;

					array[0] = 0;
					i = 1;

					if(t.minX<x1 || t.maxX>x2) {
						det = 4*bx*bx-12*ax*cx;
						if(det==0) {
							t2 = (-2*bx)/(6*ax);
							if(t2>0 && t2<1)
								array[i++] = t2;
						} else if(det>0) {
							det = Math.sqrt(det);

							t2 =  (-2*bx-det)/(6*ax);
							if(t2>0 && t2<1)
								array[i++] = t2;
							t2 =  (-2*bx+det)/(6*ax);
							if(t2>0 && t2<1)
								array[i++] = t2;
						}
					}

					if(t.minY<y1 || t.maxY>y2) {
						det = 4*by*by-12*ay*cy;
						if(det==0) {
							t2 = (-2*by)/(6*ay);
							if(t2>0 && t2<1)
								array[i++] = t2;
						} else if(det>0) {
							det = Math.sqrt(det);

							t2 =  (-2*by-det)/(6*ay);
							if(t2>0 && t2<1)
								array[i++] = t2;
							t2 =  (-2*by+det)/(6*ay);
							if(t2>0 && t2<1)
								array[i++] = t2;
						}
					}

					if(t.minY<y1) {
						eqn[0] = dy-y1;
						eqn[1] = cy;
						eqn[2] = by;
						eqn[3] = ay;

						i2 = CubicCurve2D.solveCubic(eqn,results);
						for(int a = 0; a<i2; a++) {
							if(results[a]>0 && results[a]<1) {
								array[i++] = results[a];
							}
						}
					}

					if(t.maxY>y2) {
						eqn[0] = dy-y2;
						eqn[1] = cy;
						eqn[2] = by;
						eqn[3] = ay;

						i2 = CubicCurve2D.solveCubic(array,results);
						for(int a = 0; a<i2; a++) {
							if(results[a]>0 && results[a]<1) {
								array[i++] = results[a];
							}
						}
					}

					array[i++] = 1;

					state = -1;
					//TODO: Arrays.sort() may allocate unnecessary memory?
							Arrays.sort(array,0,i);
					for(int a = 0; a<i; a++) {
						myY = ((ay*array[a]+by)*array[a]+cy)*array[a]+dy;
						if(myY>=y1e && myY<=y2e) {
							myX = ((ax*array[a]+bx)*array[a]+cx)*array[a]+dx;
							if(myX<x1) {
								myState = 0;
							} else if(myX>x2){
								myState = 2;
							} else {
								return true;
							}
							if(state==-1) {
								state = myState;
							} else if(state!=myState) {
								return true;
							}
						} else {
							state = -1;
						}
					}
				}
			}
			if(t.data.length!=0) { //SEG_CLOSE segments will have no data
				lastX = t.data[t.data.length-2];
				lastY = t.data[t.data.length-1];
			}
			t = t.next;
		}
		return false;
	}

	public Rectangle getBounds() {
		return getBounds(null);
	}

	/** This identifies all the intersections of this shape and the argument.
	 * This relies on an <code>IntersectionIdentifier</code> to find
	 * those intersections.
	 * <p>This replaces any previously existing intersection information
	 * in the two BasicShapes, so you cannot call this method on different
	 * threads simultaneously for the same shapes.
	 * @param shape the other shape to identify intersections with.
	 * @return any arbitrary Intersection object, if one was found.
	 */
	protected Intersection identifyIntersections(BasicShape shape) {
		BasicShapeSegment seg1 = list;
		double lastX1 = 0;
		double lastY1 = 0;
		IntersectionIdentifier id = new StubIdentifier();
		double[] dest = new double[10];

		double ax1 = 0;
		double bx1 = 0;
		double cx1 = 0;
		double dx1 = 0;
		double ay1 = 0;
		double by1 = 0;
		double cy1 = 0;
		double dy1 = 0;
		double ax2 = 0;
		double bx2 = 0;
		double cx2 = 0;
		double dx2 = 0;
		double ay2 = 0;
		double by2 = 0;
		double cy2 = 0;
		double dy2 = 0;

		Intersection returnValue = null;

		//TODO: We're simply comparing each segment from
		//Shape A against each segment from Shape B.  This
		//would lend itself well to multithreading, right?

		BasicShapeSegment seg2 = shape.list;
		while(seg2!=null) {
			seg2.clearIntersections();
			seg2 = seg2.next;
		}

		while(seg1!=null) {
			seg1.clearIntersections();

			if(seg1.type==PathIterator.SEG_LINETO) {
				ax1 = (seg1.data[0]-lastX1);
				bx1 = lastX1;
				ay1 = (seg1.data[1]-lastY1);
				by1 = lastY1;
			} else if(seg1.type==PathIterator.SEG_QUADTO) {
				ay1 = lastY1-2*seg1.data[1]+seg1.data[3];
				by1 = -2*lastY1+2*seg1.data[1];
				cy1 = lastY1;

				ax1 = lastX1-2*seg1.data[0]+seg1.data[2];
				bx1 = -2*lastX1+2*seg1.data[0];
				cx1 = lastX1;
			} else if(seg1.type==PathIterator.SEG_CUBICTO) {
				ay1 = -lastY1+3*seg1.data[1]-3*seg1.data[3]+seg1.data[5];
				by1 = 3*lastY1-6*seg1.data[1]+3*seg1.data[3];
				cy1 = -3*lastY1+3*seg1.data[1];
				dy1 = lastY1;

				ax1 = -lastX1+3*seg1.data[0]-3*seg1.data[2]+seg1.data[4];
				bx1 = 3*lastX1-6*seg1.data[0]+3*seg1.data[2];
				cx1 = -3*lastX1+3*seg1.data[0];
				dx1 = lastX1;
			}

			if(seg1.type==PathIterator.SEG_LINETO ||
					seg1.type==PathIterator.SEG_QUADTO ||
					seg1.type==PathIterator.SEG_CUBICTO) {
				double lastX2 = 0;
				double lastY2 = 0;
				
				seg2 = shape.list;
				while(seg2!=null) {
					if(seg2.type==PathIterator.SEG_LINETO) {
						ax2 = (seg2.data[0]-lastX2);
						bx2 = lastX2;
						ay2 = (seg2.data[1]-lastY2);
						by2 = lastY2;
					} else if(seg2.type==PathIterator.SEG_QUADTO) {
						ay2 = lastY2-2*seg2.data[1]+seg2.data[3];
						by2 = -2*lastY2+2*seg2.data[1];
						cy2 = lastY2;

						ax2 = lastX2-2*seg2.data[0]+seg2.data[2];
						bx2 = -2*lastX2+2*seg2.data[0];
						cx2 = lastX2;
					} else if(seg2.type==PathIterator.SEG_CUBICTO) {
						ay2 = -lastY2+3*seg2.data[1]-3*seg2.data[3]+seg2.data[5];
						by2 = 3*lastY2-6*seg2.data[1]+3*seg2.data[3];
						cy2 = -3*lastY2+3*seg2.data[1];
						dy2 = lastY2;

						ax2 = -lastX2+3*seg2.data[0]-3*seg2.data[2]+seg2.data[4];
						bx2 = 3*lastX2-6*seg2.data[0]+3*seg2.data[2];
						cx2 = -3*lastX2+3*seg2.data[0];
						dx2 = lastX2;
					}

					int hits = 0;
					boolean swap = false;
					if(seg1.type==PathIterator.SEG_LINETO && 
							seg2.type==PathIterator.SEG_LINETO) {
						hits = id.lineLine(ax1, bx1, ay1, by1, 
								ax2, bx2, ay2, by2, dest, 0, Return.T1_T2);
					} else if(seg1.type==PathIterator.SEG_LINETO && 
							seg2.type==PathIterator.SEG_QUADTO) {
						hits = id.lineQuadratic(ax1, bx1, ay1, by1, 
								ax2, bx2, cx2, ay2, by2, cy2, dest, 0, Return.T1_T2);
					} else if(seg1.type==PathIterator.SEG_LINETO && 
							seg2.type==PathIterator.SEG_CUBICTO) {
						hits = id.lineCubic(ax1, bx1, ay1, by1, 
								ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, dest, 0, Return.T1_T2);
					} else if(seg1.type==PathIterator.SEG_QUADTO && 
							seg2.type==PathIterator.SEG_LINETO) {
						hits = id.lineQuadratic(ax2, bx2, ay2, by2, 
								ax1, bx1, cx1, ay1, by1, cy1, dest, 0, Return.T1_T2);
						swap = true;
					} else if(seg1.type==PathIterator.SEG_QUADTO && 
							seg2.type==PathIterator.SEG_QUADTO) {
						hits = id.quadraticQuadratic(ax1, bx1, cx1, ay1, by1, cy1, 
								ax2, bx2, cx2, ay2, by2, cy2, dest, 0, Return.T1_T2);
					} else if(seg1.type==PathIterator.SEG_QUADTO && 
							seg2.type==PathIterator.SEG_CUBICTO) {
						hits = id.quadraticCubic(ax1, bx1, cx1, ay1, by1, cy1, 
								ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, dest, 0, Return.T1_T2);
					} else if(seg1.type==PathIterator.SEG_CUBICTO && 
							seg2.type==PathIterator.SEG_LINETO) {
						hits = id.lineCubic(ax2, bx2, ay2, by2, 
								ax1, bx1, cx1, dx1, ay1, by1, cy1, dy1, dest, 0, Return.T1_T2);
						swap = true;
					} else if(seg1.type==PathIterator.SEG_CUBICTO && 
							seg2.type==PathIterator.SEG_QUADTO) {
						hits = id.quadraticCubic(ax2, bx2, cx2, ay2, by2, cy2,
								ax1, bx1, cx1, dx1, ay1, by1, cy1, dy1, dest, 0, Return.T1_T2);
						swap = true;
					} else if(seg1.type==PathIterator.SEG_CUBICTO && 
							seg2.type==PathIterator.SEG_CUBICTO) {
						hits = id.cubicCubic(ax1, bx1, cx1, dx1, ay1, by1, cy1, cy1,
								ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, dest, 0, Return.T1_T2);
					}

					if(hits>0) {
						for(int a = 0; a<hits; a++) {
							Intersection i1, i2;
							if(swap==false) {
								i1 = seg1.addIntersection(seg2,dest[2*a],dest[2*a+1]);
								i2 = seg2.addIntersection(seg1,dest[2*a+1],dest[2*a]);
							} else {
								i1 = seg1.addIntersection(seg2,dest[2*a+1],dest[2*a]);
								i2 = seg2.addIntersection(seg1,dest[2*a],dest[2*a+1]);
							}
							i1.complement = i2;
							i2.complement = i1;
							returnValue = i1;
						}
					}

					if(seg2.data.length>0) {
						lastX2 = seg2.data[seg2.data.length-2];
						lastY2 = seg2.data[seg2.data.length-1];
					}
					seg2 = seg2.next;
				}
			}

			if(seg1.data.length>0) {
				lastX1 = seg1.data[seg1.data.length-2];
				lastY1 = seg1.data[seg1.data.length-1];
			}
			seg1 = seg1.next;
		}
		return returnValue;
	}

	public Rectangle getBounds(Rectangle dest) {
		if(dest==null)
			dest = new Rectangle();
		int minXi = MathG.floorInt(minX);
		int minYi = MathG.floorInt(minY);
		int maxXi = MathG.ceilInt(maxX);
		int maxYi = MathG.ceilInt(maxY);
		dest.setBounds(minXi,minYi,maxXi-minXi,maxYi-minYi);
		return dest;
	}

	public Rectangle2D getBounds2D() {
		return getBounds2D(null);
	}

	public Rectangle2D getBounds2D(Rectangle2D dest) {
		if(dest==null)
			dest = new Rectangle2D.Double();
		dest.setFrame(minX,minY,maxX-minX,maxY-minY);
		return dest;
	}

	/** Creates a <code>PathIterator</code> for the outline of this object.
	 * @param at an optional <code>AffineTransform</code> to apply to this shape data
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		BasicShapeIterator i = new BasicShapeIterator(this,at);
		if(dependentIterators==null)
			dependentIterators = new LinkedList<WeakReference<BasicShapeIterator>>();
		dependentIterators.add(new WeakReference<BasicShapeIterator>(i));
		return i;
	}

	/** Creates a <code>FlatteningPathIterator</code> for the outline of this object.
	 * @param at an optional <code>AffineTransform</code> to apply to this shape data
	 * @param flatness the maximum amount that the control points for a given curve can
	 * vary from colinear before a subdivided curve is replaced by a straight line
	 * connecting the endpoints
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new FlatteningPathIterator(getPathIterator(at),flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(),r.getY(),r.getWidth(),r.getHeight());
	}

	public boolean intersects(double x, double y, double w, double h) {
		boolean contains = (x >= minX &&
				y >= minY &&
				(x + w) <= maxX &&
				(y + h) <= maxY );

		if(contains==false) {
			return false;
		}

		//this handles the first 2 criteria
		if(identifyCrossings(x,y,w,h)) {
			return true;
		}

		/** It may not intersect, but if the rectangle lies
		 * entirely inside this shape then we should also
		 * return true.
		 */
		if(contains(x+w/2,y+h/2)==false) {
			return true;
		}
		return false;
	}

	/** Transforms this shape through the <code>AffineTransform</code> provided.
	 * 
	 * @param transform the transform to apply to this shape.
	 */
	public void transform(AffineTransform transform) {
		if(transform==null || transform.isIdentity())
			return;

		releaseIterators();

		double[] m = new double[6];
		transform.getMatrix(m);
		double m00, m10, m01, m11, m02, m12;
		m00 = m[0];
		m10 = m[1];
		m01 = m[2];
		m11 = m[3];
		m02 = m[4];
		m12 = m[5];

		BasicShapeSegment t = list;
		double x, y;
		while(t!=null) {
			for(int a = 0; a<t.data.length; a+=2) {
				x = t.data[a];
				y = t.data[a+1];
				t.data[a] = x*m00+y*m01+m02;
				t.data[a+1] = x*m10+y*m11+m12;
			}
			t = t.next;
		}

		updateBounds();
	}

	private int getPointCount() {
		BasicShapeSegment t = list;
		int sum = 0;
		while(t!=null) {
			if(t.data!=null)
				sum+=t.data.length;
			t = t.next;
		}
		return sum;
	}

	/** A <code>java.lang.String</code> representation of this shape.
	 * <P>Note that this <code>String</code> can later be passed as
	 * an argument to the constructor to re-create this shape, so this is
	 * a very efficient way to serialize this data.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getPointCount()*15); //guess how much space we'll want
		sb.append("BasicShape[");
		BasicShapeSegment t = list;
		while(t!=null) {
			if(t.type==PathIterator.SEG_MOVETO) {
				sb.append('m');
			} else if(t.type==PathIterator.SEG_LINETO) {
				sb.append('l');
			} else if(t.type==PathIterator.SEG_QUADTO) {
				sb.append('q');
			} else if(t.type==PathIterator.SEG_CUBICTO) {
				sb.append('c');
			} else if(t.type==PathIterator.SEG_CLOSE) {
				sb.append('z');
			}
			if(t.data.length>0) {
				sb.append(' ');
				for(int a = 0; a<t.data.length; a++) {
					sb.append(Float.toString((float)t.data[a]));
					if(a<t.data.length-1)
						sb.append(' ');
				}
			}
			t = t.next;
			if(t!=null) {
				sb.append(' ');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	/** When we are about to modify this object we need
	 * to synch on this object and call this method.
	 * This will make sure any iterators that
	 * still depend on this shape have a copy of their
	 * original data in tact, and they don't try to
	 * iterate over our modified data.
	 *
	 */
	protected void releaseIterators() {
		if(dependentIterators==null)
			return;
		
		while(dependentIterators.size()>0) {
			WeakReference<BasicShapeIterator> r = dependentIterators.removeFirst();
			BasicShapeIterator i2 = r.get();
			if(i2!=null)
				i2.releaseOriginal();
		}
	}

	/** TODO: only the line-line method is (minimally) implemented.
	 */
	class StubIdentifier extends IntersectionIdentifier {

		@Override
		public int lineQuadratic(double ax1, double bx1, double ay1, double by1,
				double ax2, double bx2, double cx2, double ay2, double by2,
				double cy2, double[] dest, int offset, Return returnType) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int lineCubic(double ax1, double bx1, double ay1, double by1,
				double ax2, double bx2, double cx2, double dx2, double ay2,
				double by2, double cy2, double dy2, double[] dest, int offset,
				Return returnType) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int quadraticQuadratic(double ax1, double bx1, double cx1,
				double ay1, double by1, double cy1, double ax2, double bx2,
				double cx2, double ay2, double by2, double cy2, double[] dest,
				int offset, Return returnType) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int quadraticCubic(double ax1, double bx1, double cx1, double ay1,
				double by1, double cy1, double ax2, double bx2, double cx2,
				double dx2, double ay2, double by2, double cy2, double dy2,
				double[] dest, int offset, Return returnType) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int cubicCubic(double ax1, double bx1, double cx1, double dx1,
				double ay1, double by1, double cy1, double dy1, double ax2,
				double bx2, double cx2, double dx2, double ay2, double by2,
				double cy2, double dy2, double[] dest, int offset, Return returnType) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
