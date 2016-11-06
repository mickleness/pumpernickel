/*
 * @(#)BasicShapeSegment.java
 *
 * $Date: 2014-04-14 02:05:51 -0400 (Mon, 14 Apr 2014) $
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

import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Stack;

/** This is a node in a double-linked list.
 * Each node describes a path segment in a shape.
 * 
 */
class BasicShapeSegment {
	private static final double[] EMPTY_DOUBLE_ARRAY = new double[] {};
	
	/** The previous node, or null if this is the head node. */
	BasicShapeSegment prev;
	/** The next node, or null if this is the tail node. */
	BasicShapeSegment next;
    
	/** The data for this segment.  This corresponds to the PathIterator model:
	 * <li>For a MOVETO or LINETO segment, there will be 2 elements.
	 * <li>For a QUADTO segment, there will be 4 elements.
	 * <li>For a CUBICTO segment, there will be 6 elements.
	 * <li>For a CLOSE segment, there will be zero elements.</LI>
	 * 
	 */
    double[] data;
    
    /** The list of intersections for this segment.
     * This is created during an operation (such as add/intersect/etc.)
     * and destroyed when that operation ends.
     */
    Intersection i;
    
    /** The type of segment.  This will be one of the PathIterator SEG constants. */
    int type;
    
    /** The minimum x-value this segment includes.
     * Note this value is not accurate unless updateBounds() was
     * recently called.
     */
    double minX;
    

    /** The maximum x-value this segment includes.
     * Note this value is not accurate unless updateBounds() was
     * recently called.
     */
    double maxX;
    
    /** The minimum y-value this segment includes.
     * Note this value is not accurate unless updateBounds() was
     * recently called.
     */
    double minY;
    

    /** The maximum y-value this segment includes.
     * Note this value is not accurate unless updateBounds() was
     * recently called.
     */
    double maxY;
    
    /** Appends a segment to this node.
     * As of this writing: it is assumed this is only used to append
     * nodes to the tail of this list.  An exception is thrown if you
     * try to insert an element arbitrarily in the list.
     * 
     * @param segment
     * @return the new tail.
     */
    protected BasicShapeSegment append(BasicShapeSegment segment) {
    	//if there is already a next node, this is not working as intended.
    	//it's OK to change this in the future, so long as there's a good reason.
    	if(next!=null)
    		throw new RuntimeException("illegal attempt to insert node with the append method");
    	
    	next = segment;
    	segment.prev = this;
    	segment.next = null;
    	
    	return segment;
    }
    
    /** Appends part of the path to this node.  It is assumed this node
     * is the tail of this the list.
     * 
     * <p>Note the starting time may be 1, and the ending time may be 0,
     * indicating that the argument segment should be written backwards.
     * 
     * @param newSegment the shape segment to append to this node.
     * @param t0 the starting time.  This will be within [0,1].
     * @param t1 the ending time.  This will be within [0,1].
     * @return the new tail of this shape
     */    
    protected BasicShapeSegment append(BasicShapeSegment newSegment,double t0,double t1) {
        double[] array = new double[8];
	    int i = newSegment.subdivide(t0,t1,array);
        if(i==2) {
            return append(new BasicShapeSegment(
            		PathIterator.SEG_LINETO, 
            		new double[] { array[2], array[3] } ));
        } else if(i==3) {
            return append(new BasicShapeSegment(
            		PathIterator.SEG_QUADTO,
            		new double[] { array[2], array[3],
                    array[4], array[5] }));
        } else if(i==4) {
            return append(new BasicShapeSegment(
            		PathIterator.SEG_CUBICTO, 
            		new double[] { array[2], array[3],
                    array[4], array[5],
                    array[6], array[7] }));
        } else {
            throw new RuntimeException("This segment cannot append.");
        }
    }
    
    private static Stack<Intersection> intersectionStack = new Stack<Intersection>();
    
    protected void clearIntersections() {
    	Intersection t = i;
    	while(t!=null) {
    		Intersection next = t.next;
    		
    		//nullify references so we don't
    		//hang out to objects that should be
    		//gc'ed:
    		t.next = null;
    		t.prev = null;
    		t.complement = null;
    		t.s1 = null;
    		t.s2 = null;
    		
    		synchronized(intersectionStack) {
    			intersectionStack.add(t);
    		}
    		
    		t = next;
    	}
    }
    
    /** Add an Intersection record to this segment.
     * 
     * This in no way modifies the otherSegment argument; that should
     * be done separately.  And once two Intersections are created,
     * they should identify each other as complements.
     * 
     * @param otherSegment
     * @param myTime
     * @param otherTime
     * @return the new segment added.
     */
    protected Intersection addIntersection(BasicShapeSegment otherSegment,double myTime,double otherTime) {
    	Intersection newIntersection;
    	synchronized(intersectionStack) {
	    	if(intersectionStack.size()==0) {
	    		newIntersection = new Intersection();
	    	} else {
	    		newIntersection = intersectionStack.pop();
	    	}
    	}
    	newIntersection.init(this, otherSegment, myTime, otherTime);
    	if(i==null) {
    		i = newIntersection;
    	} else {
    		i = i.add(newIntersection);
    	}
    	return newIntersection;
    }
    
    protected BasicShapeSegment close() {
    	BasicShapeSegment start = rewindToStartOfSubPath();
    	return closeTo(start.data[0], start.data[1] );
    }
    
    protected BasicShapeSegment moveTo(Point2D p) {
    	return moveTo(p.getX(),p.getY());
    }
    
    protected BasicShapeSegment moveTo(double x,double y) {
    	BasicShapeSegment newGuy = new BasicShapeSegment(
    		PathIterator.SEG_MOVETO,
    		new double[] {x, y}
    	);
    	append(newGuy);
    	return newGuy;
    }
    
    protected BasicShapeSegment lineTo(double x,double y) {
    	BasicShapeSegment newGuy = new BasicShapeSegment(
    		PathIterator.SEG_LINETO,
    		new double[] {x, y}
    	);
    	append(newGuy);
    	return newGuy;
    }
    
    protected BasicShapeSegment quadTo(double cx,double cy,double x,double y) {
    	BasicShapeSegment newGuy = new BasicShapeSegment(
    		PathIterator.SEG_QUADTO,
    		new double[] {cx, cy, x, y}
    	);
    	append(newGuy);
    	return newGuy;
    }
    
    protected BasicShapeSegment cubicTo(double cx1,double cy1,double cx2,double cy2,double x,double y) {
    	BasicShapeSegment newGuy = new BasicShapeSegment(
    		PathIterator.SEG_CUBICTO,
    		new double[] {cx1, cy1, cx2, cy2, x, y}
    	);
    	append(newGuy);
    	return newGuy;
    }
    
    /** If this node is not a SEG_CLOSE node, then this will 
     * append a SEG_CLOSE node.
     * <P>This may also append a SEG_LINETO node, if necessary,
     * before the SEG_CLOSE to bring the subpath back to the
     * desired point.
     * @param x the x-coordinate the subpath should end at.
     * @param y the y-coordinate the subpath should end at.
     * @return the new tail.
     */
    private BasicShapeSegment closeTo(double x,double y) {
    	if(type==PathIterator.SEG_CLOSE)
    		throw new RuntimeException("segment already closed");
    	BasicShapeSegment current = this;
        if(!(Math.abs(data[data.length-2]-x)<.00001 &&
                Math.abs(data[data.length-1]-y)<.00001)) {
        	//if necessary, add a line connecting things
        	BasicShapeSegment line = new BasicShapeSegment(
        			PathIterator.SEG_LINETO,
        			new double[] {x,y}
        	);
        	current = append(line);
        }

        //add the segment pointing out the shape is officially closed:
    	current = current.append( new BasicShapeSegment(
    			PathIterator.SEG_CLOSE,
    			EMPTY_DOUBLE_ARRAY
    	));
    	
    	return current;
    }
    
    /** Creates a series of segments representing the path presented
     * in the argument.
     * <P>The segments will represent a safe, closed shape, regardless
     * of what the argument is.
     * <P>This may return <code>null</code> if the argument is empty.
     * @param i a path.
     * @return a series of segments representing the argument.
     */
    protected static BasicShapeSegment read(PathIterator i) {
        BasicShapeSegment head = null;
        BasicShapeSegment tail = null;
        double moveX = 0;
        double moveY = 0;
        double[] scratchArray = new double[6];
        
        while(i.isDone()==false) {
            int type = i.currentSegment(scratchArray);
            
            if(type==PathIterator.SEG_CLOSE || type==PathIterator.SEG_MOVETO) {
            	if(tail!=null && tail.type!=PathIterator.SEG_CLOSE) {
            		//if the last node were a SEG_CLOSE, then data.length would be zero.
            		//So we didn't close the last path.  Paths always must be closed.
            		tail = tail.closeTo(moveX,moveY);
            	}
            	
            	if(type==PathIterator.SEG_MOVETO) {
            		BasicShapeSegment moveto = new BasicShapeSegment(
            			PathIterator.SEG_MOVETO,
            			new double[] {scratchArray[0], scratchArray[1]}
            		);
            		if(head==null) {
            			head = moveto;
            			tail = moveto;
            		} else {
            			tail = tail.append(moveto);
            		}
            		//make a note of our starting point for future reference:
                    moveX = scratchArray[0];
                    moveY = scratchArray[1];
            	}
            } else if(type==PathIterator.SEG_LINETO || type==PathIterator.SEG_QUADTO || type==PathIterator.SEG_CUBICTO) {

            	//does this actually help?  See if it's missed
                //type = SimplifiedPathIterator.simplify(type,lastX,lastY,scratchArray);
                
            	double[] array;
            	if(type==PathIterator.SEG_LINETO) {
            		array = new double[] {scratchArray[0], scratchArray[1]};
            	} else if(type==PathIterator.SEG_QUADTO) {
            		array = new double[] {scratchArray[0], scratchArray[1], scratchArray[2], scratchArray[3]};
            	} else {
            		array = new double[] {scratchArray[0], scratchArray[1], scratchArray[2], scratchArray[3], scratchArray[4], scratchArray[5]};
            	}
            	
            	BasicShapeSegment segment = new BasicShapeSegment(type, array);
            	
            	if(head==null) {
            		System.err.println(segment);
            		throw new RuntimeException("Illegal segment data without a moveTo instruction.");
            	}
            	
            	tail = tail.append(segment);
            	
            }
            
            i.next();		
        }
        if(tail!=null && tail.type!=PathIterator.SEG_CLOSE) {
        	tail = tail.closeTo(moveX, moveY);
        }
        
        return head;
    }
    
    /** Creates a new segment.
     * 
     * @param segmentType the segment type
     * @param dataArray the array to use as the data.
     * Note this array is not cloned.
     */
    protected BasicShapeSegment(int segmentType,double[] dataArray) {
    	type = segmentType;
    	data = dataArray;
    }
    
    /** Returns the head of this path. */
    public BasicShapeSegment getHead() {
    	BasicShapeSegment t = this;
        while(t.prev!=null) {
            t = t.prev;
        }
        return t;
    }
    
    /** Subdivides this into a segment of the same type that maps t0 to 0 and t1 to 1.
     *  
     * @return the number of points included in dest.  This will be [2,4].
     */
    public int subdivide(double t0,double t1,double[] dest) {
        double lastX = prev.data[prev.data.length-2];
        double lastY = prev.data[prev.data.length-1];
        
        dest[0] = lastX;
        dest[1] = lastY;
        
        if(type==PathIterator.SEG_LINETO) {
            double ax = (data[0]-lastX);
            double bx = lastX;
            double ay = (data[1]-lastY);
            double by = lastY;
            
            dest[2] = ax*t1+bx;
            dest[3] = ay*t1+by;
            return 2;
        }
        
        if(type==PathIterator.SEG_QUADTO) {
            double ay = lastY-2*data[1]+data[3];
            double by = -2*lastY+2*data[1];
            double cy = lastY;
            
            double ax = lastX-2*data[0]+data[2];
            double bx = -2*lastX+2*data[0];
            double cx = lastX;
            
            double tZ = (t0+t1)/2.0;
            
            double f0 = ay*t0*t0+by*t0+cy;
            double f1 = ay*tZ*tZ+by*tZ+cy;
            double f2 = ay*t1*t1+by*t1+cy;
            
            double ay2 = 2*f2-4*f1+2*f0;
            double cy2 = f0;
            double by2 = f2-cy2-ay2;
            
            f0 = ax*t0*t0+bx*t0+cx;
            f1 = ax*tZ*tZ+bx*tZ+cx;
            f2 = ax*t1*t1+bx*t1+cx;
            
            double ax2 = 2*f2-4*f1+2*f0;
            double cx2 = f0;
            double bx2 = f2-cx2-ax2;
            
            lastY = cy2;
            double ctrlY = (2*lastY+by2)/2;
            double y1 = ay2-lastY+2*ctrlY;
            
            lastX = cx2;
            double ctrlX = (2*lastX+bx2)/2;
            double x1 = ax2-lastX+2*ctrlX;
            
            dest[2] = ctrlX;
            dest[3] = ctrlY;
            dest[4] = x1;
            dest[5] = y1;
            return 3;
        } else if(type==PathIterator.SEG_CUBICTO) {
            double ay = -lastY+3*data[1]-3*data[3]+data[5];
            double by = 3*lastY-6*data[1]+3*data[3];
            double cy = -3*lastY+3*data[1];
            double dy = lastY;
            
            double ax = -lastX+3*data[0]-3*data[2]+data[4];
            double bx = 3*lastX-6*data[0]+3*data[2];
            double cx = -3*lastX+3*data[0];
            double dx = lastX;
            
            double tW = 2.0*t0/3.0+t1/3.0;
            double tZ = t0/3.0+2.0*t1/3.0;
            
            double f0 = ay*t0*t0*t0+by*t0*t0+cy*t0+dy;
            double f1 = ay*tW*tW*tW+by*tW*tW+cy*tW+dy;
            double f2 = ay*tZ*tZ*tZ+by*tZ*tZ+cy*tZ+dy;
            double f3 = ay*t1*t1*t1+by*t1*t1+cy*t1+dy;
            
            double dy2 = f0;
            double cy2 = (-11*f0+18*f1-9*f2+2*f3)/2.0;
            double by2 = (-19*f0+27*f2-8*f3-10*cy2)/4;
            double ay2 = f3-by2-cy2-f0;
            
            f0 = ax*t0*t0*t0+bx*t0*t0+cx*t0+dx;
            f1 = ax*tW*tW*tW+bx*tW*tW+cx*tW+dx;
            f2 = ax*tZ*tZ*tZ+bx*tZ*tZ+cx*tZ+dx;
            f3 = ax*t1*t1*t1+bx*t1*t1+cx*t1+dx;
            
            double dx2 = f0;
            double cx2 = (-11*f0+18*f1-9*f2+2*f3)/2.0;
            double bx2 = (-19*f0+27*f2-8*f3-10*cx2)/4;
            double ax2 = f3-bx2-cx2-f0;
            
            lastY = dy2;
            double cy0 = (3*lastY+cy2)/3;
            double cy1 = (by2-3*lastY+6*cy0)/3;
            double y1 = ay2+lastY-3*cy0+3*cy1;
            
            lastX = dx2;
            double cx0 = (3*lastX+cx2)/3;
            double cx1 = (bx2-3*lastX+6*cx0)/3;
            double x1 = ax2+lastX-3*cx0+3*cx1;
            
            dest[2] = cx0;
            dest[3] = cy0;
            dest[4] = cx1;
            dest[5] = cy1;
            dest[6] = x1;
            dest[7] = y1;
            return 4;
        } 
        throw new RuntimeException("This segment cannot subdivide.");
    }
    
    /** Returns the SEG_MOVETO segment at the beginning of this subpath.
     * 
     */
    protected BasicShapeSegment rewindToStartOfSubPath() {
    	BasicShapeSegment s = this;
        while(s.type!=PathIterator.SEG_MOVETO) {
            s = s.prev;
        }
        return s;
    }

    /** Iterates one step forward in this subpath.
     * This will not return the SEG_CLOSE segment, it will skip
     * to the next segment.
     */
    protected BasicShapeSegment iterateNextInSubPath() {
    	BasicShapeSegment t = next;
        if(t==null || t.type==PathIterator.SEG_CLOSE) {
            t = rewindToStartOfSubPath(); //should be a MOVETO
            t = t.next;
        }
        return t;
    }

    /** Iterates one step backward in this subpath.
     * This will not return the SEG_CLOSE segment, it will skip
     * to the previous segment.
     */
    protected BasicShapeSegment iterateBackInSubPath() {
    	BasicShapeSegment t = prev;
        if(t==null || t.type==PathIterator.SEG_MOVETO) {
            t = fastForwardToEndOfSubPath(); // should be a SEG_CLOSE
            t = t.prev;
        }
        return t;
    }
    
    /** Returns the SEG_CLOSE segment at the end of this subpath.
     */
    protected BasicShapeSegment fastForwardToEndOfSubPath() {
    	BasicShapeSegment s = this;
        while(s.type!=PathIterator.SEG_CLOSE) {
            s = s.next;
        }
        return s;
    }
    
    /** Stores the (x,y) value of this segment at time t.
     * Note this throws an exception if this segment is a MOVETO or CLOSE segment.
     * @param p the destination to store the point in
     * @param t the time
     */
    protected Point2D getPoint(Point2D p,double t) {
        if(p==null) p = new Point2D.Double();
        double lastX = prev.data[prev.data.length-2];
        double lastY = prev.data[prev.data.length-1];
        if(type==PathIterator.SEG_LINETO) {
            double ax = (data[0]-lastX);
            double bx = lastX;
            double ay = (data[1]-lastY);
            double by = lastY;
            p.setLocation( ax*t+bx, ay*t+by );
        } else if(type==PathIterator.SEG_QUADTO) {
            double ay = lastY-2*data[1]+data[3];
            double by = -2*lastY+2*data[1];
            double cy = lastY;
            
            double ax = lastX-2*data[0]+data[2];
            double bx = -2*lastX+2*data[0];
            double cx = lastX;
            
            p.setLocation( (ax*t+bx)*t+cx, (ay*t+by)*t+cy );
            
        } else if(type==PathIterator.SEG_CUBICTO) {
            double ay = -lastY+3*data[1]-3*data[3]+data[5];
            double by = 3*lastY-6*data[1]+3*data[3];
            double cy = -3*lastY+3*data[1];
            double dy = lastY;
            
            double ax = -lastX+3*data[0]-3*data[2]+data[4];
            double bx = 3*lastX-6*data[0]+3*data[2];
            double cx = -3*lastX+3*data[0];
            double dx = lastX;
            
            p.setLocation( ((ax*t+bx)*t+cx)*t+dx, ((ay*t+by)*t+cy)*t+dy );
        } else if(type==PathIterator.SEG_CLOSE) {
        	throw new RuntimeException("unexpect close");
        } else if(type==PathIterator.SEG_MOVETO) {
        	throw new RuntimeException("unexpect move");
        } else {
            throw new RuntimeException("Unexpected type: "+type);
        }
        return p;
    }

    /** Returns the x-value for this segment at the specified time.
     * Note this throws an exception if this segment is a MOVETO or CLOSE segment.
     * 
     */
    protected double getX(double t) {
        double lastX = prev.data[prev.data.length-2];
        if(type==PathIterator.SEG_LINETO) {
            double ax = (data[0]-lastX);
            double bx = lastX;
            return ax*t+bx;
        } else if(type==PathIterator.SEG_QUADTO) {
            double ax = lastX-2*data[0]+data[2];
            double bx = -2*lastX+2*data[0];
            double cx = lastX;
            
            return (ax*t+bx)*t+cx;
        } else if(type==PathIterator.SEG_CUBICTO) {
            double ax = -lastX+3*data[0]-3*data[2]+data[4];
            double bx = 3*lastX-6*data[0]+3*data[2];
            double cx = -3*lastX+3*data[0];
            double dx = lastX;
            
            return ((ax*t+bx)*t+cx)*t+dx;
        } else if(type==PathIterator.SEG_CLOSE) {
        	throw new RuntimeException("unexpect close");
        } else if(type==PathIterator.SEG_MOVETO) {
        	throw new RuntimeException("unexpect move");
        } else {
            throw new RuntimeException("Unexpected type: "+type);
        }
    }

    /** Returns the y-value for this segment at the specified time.
     * Note this throws an exception if this segment is a MOVETO or CLOSE segment.
     * 
     */
    protected double getY(double t) {
        double lastY = prev.data[prev.data.length-1];
        if(type==PathIterator.SEG_LINETO) {
            double ay = (data[1]-lastY);
            double by = lastY;
           	return ay*t+by;
        } else if(type==PathIterator.SEG_QUADTO) {
            double ay = lastY-2*data[1]+data[3];
            double by = -2*lastY+2*data[1];
            double cy = lastY;
            
            return (ay*t+by)*t+cy;
            
        } else if(type==PathIterator.SEG_CUBICTO) {
            double ay = -lastY+3*data[1]-3*data[3]+data[5];
            double by = 3*lastY-6*data[1]+3*data[3];
            double cy = -3*lastY+3*data[1];
            double dy = lastY;
            
            return ((ay*t+by)*t+cy)*t+dy;
        } else if(type==PathIterator.SEG_CLOSE) {
        	throw new RuntimeException("unexpect close");
        } else if(type==PathIterator.SEG_MOVETO) {
        	throw new RuntimeException("unexpect move");
        } else {
            throw new RuntimeException("Unexpected type: "+type);
        }
    }

    /** Calculates the tangent angle at a certain time.
     * @param t the time
     */
    protected double getAngle(double t) {
        double lastX = prev.data[prev.data.length-2];
        double lastY = prev.data[prev.data.length-1];
        if(type==PathIterator.SEG_LINETO) {
            double ax = (data[0]-lastX);
            double ay = (data[1]-lastY);
            return Math.atan2(ay,ax);
        } else if(type==PathIterator.SEG_QUADTO) {
            double ay = lastY-2*data[1]+data[3];
            double by = -2*lastY+2*data[1];
            
            double ax = lastX-2*data[0]+data[2];
            double bx = -2*lastX+2*data[0];

            return Math.atan2(2*ay*t+by,2*ax*t+bx);
        } else if(type==PathIterator.SEG_CUBICTO) {
            double ay = -lastY+3*data[1]-3*data[3]+data[5];
            double by = 3*lastY-6*data[1]+3*data[3];
            double cy = -3*lastY+3*data[1];
            
            double ax = -lastX+3*data[0]-3*data[2]+data[4];
            double bx = 3*lastX-6*data[0]+3*data[2];
            double cx = -3*lastX+3*data[0];

            return Math.atan2(3*ay*t*t+2*by*t+cy,3*ax*t*t+2*bx*t+cx);
        } else if(type==PathIterator.SEG_CLOSE) {
        	throw new RuntimeException("unexpect close");
        } else if(type==PathIterator.SEG_MOVETO) {
        	throw new RuntimeException("unexpect move");
        } else {
            throw new RuntimeException("Unexpected type: "+type);
        }
    }
    
    
    /** Finds the values of t at which this segment contains the point (x,y).
     * Returns 0 if no such time is found.
     * <P>This does NOT sort the times, eliminate duplicate times, or filter
     * for times exclusively between [0,1].  This just list the times.
     * 
     * @param x
     * @param y
     * @param dest the array to store the t-values in
     * @return the number of t-values found
     */
    protected int getTimes(double x, double y,double[] dest) {
        if(type==PathIterator.SEG_MOVETO) {
            if(BasicShape.near(x,data[0],.0001) && BasicShape.near(y,data[1],.0001)) {
                dest[0] = 1;
                return 1;
            }
            return 0;
        } else if(type==PathIterator.SEG_LINETO) {
            double ax = data[0]-prev.data[prev.data.length-2];
            double bx = prev.data[prev.data.length-2];
            double ay = data[1]-prev.data[prev.data.length-1];
            double by = prev.data[prev.data.length-1];
            if(Math.abs(ax)<Math.abs(ay)) {
                //ax is small, so we work with ay:
                double t = (y-by)/ay;
                if(BasicShape.near(ax*t+bx,x,.0001)) {
                    dest[0] = t;
                    return 1;
                }
                return 0;
            }
            double t = (x-bx)/ax;
            if(BasicShape.near(ay*t+by,y,.0001)) {
                dest[0] = t;
                return 1;
            }
            return 0;
        } else if(type==PathIterator.SEG_QUADTO) {
            double ax = prev.data[prev.data.length-2]-2*data[0]+data[2];
            double bx = -2*prev.data[prev.data.length-2]+2*data[0];
            double cx = prev.data[prev.data.length-2];
            double ay = prev.data[prev.data.length-1]-2*data[1]+data[3];
            double by = -2*prev.data[prev.data.length-1]+2*data[1];
            double cy = prev.data[prev.data.length-1];
            
            double det;
            int ctr = 0;
            if(Math.abs(ax)<Math.abs(ay)) {
                det = by*by-4*ay*(cy-y);
                if(det<0) {
                    if(det>-.000001) {
                        det = 0;
                    } else {
                        return 0;
                    }
                }
                
                det = Math.sqrt(det);
                double t = (-by+det)/(2*ay);
                if(BasicShape.near( (ax*t+bx)*t+cx, x, .0001 )) {
                    dest[ctr++] = t;
                }
                t = (-by-det)/(2*ay);
                if(BasicShape.near( (ax*t+bx)*t+cx, x, .0001 )) {
                    dest[ctr++] = t;
                }
                return ctr;
            }
            det = bx*bx-4*ax*(cx-x);
            if(det<0) {
                if(det>-.000001) {
                    det = 0;
                } else {
                    return 0;
                }
            }
            
            det = Math.sqrt(det);
            double t = (-bx+det)/(2*ax);
            if(BasicShape.near( (ay*t+by)*t+cy, y, .0001 )) {
                dest[ctr++] = t;
            }
            t = (-bx-det)/(2*ax);
            if(BasicShape.near( (ay*t+by)*t+cy, y, .0001 ))
                dest[ctr++] = t;
            return ctr;
        } else if(type==PathIterator.SEG_CUBICTO) {
            
            double ax = -prev.data[prev.data.length-2]+3*data[0]-3*data[2]+data[4];
            double bx = 3*prev.data[prev.data.length-2]-6*data[0]+3*data[2];
            double cx = -3*prev.data[prev.data.length-2]+3*data[0];
            double dx = prev.data[prev.data.length-2];
            
            double ay = -prev.data[prev.data.length-1]+3*data[1]-3*data[3]+data[5];
            double by = 3*prev.data[prev.data.length-1]-6*data[1]+3*data[3];
            double cy = -3*prev.data[prev.data.length-1]+3*data[1];
            double dy = prev.data[prev.data.length-1];
            
            double[] equation = new double[4];
            double[] results = new double[3];
            
            int ctr = 0;
            equation[0] = dy-y;
            equation[1] = cy;
            equation[2] = by;
            equation[3] = ay;
            int i = CubicCurve2D.solveCubic(equation, results);
            for(int a = 0; a<i; a++) {
                double myX = ((ax*results[a]+bx)*results[a]+cx)*results[a]+dx;
                if( BasicShape.near( myX, x, .0001) ) {
                    dest[ctr++] = results[a];
                }
            }
            equation[0] = dx-x;
            equation[1] = cx;
            equation[2] = bx;
            equation[3] = ax;
            i = CubicCurve2D.solveCubic(equation, results);
            for(int a = 0; a<i; a++) {
                double myY = ((ay*results[a]+by)*results[a]+cy)*results[a]+dy;
                if( BasicShape.near( myY, y, .0001) ) {
                    dest[ctr++] = results[a];
                }
            }
            return ctr;
        }
        return 0;
    }
    
    /** Finds a value t between [0,1] at which this segment contains the point (x,y).
     * Returns -1 if no such time is found.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return a value of t between [0,1], or -1 if no such time was found.
     */
    protected double getTime(double x, double y) {
    	double[] array = new double[20];
		int k = getTimes(x,y,array);
		for(int a = 0; a<k; a++) {
			if(array[a]>=0 && array[a]<=1)
				return array[a];
		}
		return -1;
    }
    
    /** A string representation for this segment only.
     * Intended only for debugging.
     */
    @Override
	public String toString() {
        return toString(false);
    }
    
    /** A string representation for this segment.
     * Intended only for debugging.
     * 
     * @param allSubsequentNodes if <code>true</code>, then all the future nodes will
     * also be listed in this string.
     */
    public String toString(boolean allSubsequentNodes) {
        if(allSubsequentNodes) {
            BasicShapeSegment t = this;
            String s = "";
            while(t!=null) {
                s = s+t.toString()+"\n";
                t = t.next;
            }
            return s.trim();
        }
        String t = "unknown";
        if(type==PathIterator.SEG_LINETO)
            t = "LINETO";
        if(type==PathIterator.SEG_MOVETO)
            t = "MOVETO";
        if(type==PathIterator.SEG_CLOSE)
            t = "CLOSE";
        if(type==PathIterator.SEG_QUADTO)
            t = "QUADTO";
        if(type==PathIterator.SEG_CUBICTO)
            t = "CUBICTO";
        String s = "Segment[ type="+t+" data="+BasicShape.toString(data)+"]";
        return s;
    }
    
    /** Clones this segment and all its subsequent segments,
     * but not all its previous segments.
     */
    @Override
	public Object clone() {
    	//don't use recursion in this method, that could
    	//lead to unnecessary overhead
    	//(possibly even to unnecessary StackOverflowErrors in
    	//extreme cases)
    	BasicShapeSegment head = null;
    	BasicShapeSegment tail = null;
    	
    	BasicShapeSegment current = this;
    	while(current!=null) {
    		double[] cloneData = new double[current.data.length];
    		for(int a = 0; a<current.data.length; a++) {
    			cloneData[a] = current.data[a];
    		}
    		BasicShapeSegment clone = new BasicShapeSegment(current.type,cloneData);
    		
    		clone.maxX = current.maxX;
    		clone.maxY = current.maxY;
    		clone.minX = current.minX;
    		clone.minY = current.minY;
    		
    		if(head==null) {
    			head = clone;
    			tail = clone;
    		} else {
    			tail = tail.append(clone);
    		}
    	}
    	
    	return head;
    }
    
    /** This updates the min/max values cached in this segment,
     * and all future (but not previous) segments.
     * <P>This segment itself has no concept of when these values
	 * are accurate or need to be updated.
     */
    public void updateBounds() {
        BasicShapeSegment t = this;
        double lastX = 0;
        double lastY = 0;
        double a, b, c, d,  t2, v, det;
        while(t!=null) {
            if(t.type==PathIterator.SEG_CLOSE) {
                //do nothing
            } else if(t.type==PathIterator.SEG_MOVETO) {
                t.minX = t.data[0];
                t.maxX = t.data[0];
                t.minY = t.data[1];
                t.maxY = t.data[1];
                lastX = t.data[0];
                lastY = t.data[1];
            } else {
                t.minX = lastX;
                t.minY = lastY;
                t.maxX = lastX;
                t.maxY = lastY;
                if(t.type==PathIterator.SEG_QUADTO) {
                    a = lastX-2*t.data[0]+t.data[2];
                    b = -2*lastX+2*t.data[0];
                    c = lastX;
                    t2 = -b/(2*a);
                    if(t2>0 && t2<1) {
                        v = (a*t2+b)*t2+c;
                        if(v<t.minX) {
                            t.minX = v;
                        } else if(v>t.maxX) {
                            t.maxX = v;
                        }
                    }
                    a = lastY-2*t.data[1]+t.data[3];
                    b = -2*lastY+2*t.data[1];
                    c = lastY;
                    t2 = -b/(2*a);
                    if(t2>0 && t2<1) {
                        v = (a*t2+b)*t2+c;
                        if(v<t.minY) {
                            t.minY = v;
                        } else if(v>t.maxY) {
                            t.maxY = v;
                        }
                    }
                } else if(t.type==PathIterator.SEG_CUBICTO) {
                    a = -lastX+3*t.data[0]-3*t.data[2]+t.data[4];
                    b = 3*lastX-6*t.data[0]+3*t.data[2];
                    c = -3*lastX+3*t.data[0];
                    d = lastX;
                    
                    //x = a*t*t*t+b*t*t+c*t+d
                    //dx/dt = 3*a*t*t+2*b*t+c
                    //t = [-B+-sqrt(B^2-4*A*C)]/(2A)
                    //A = 3*a
                    //B = 2*b
                    //C = c
                    //t = (-2*b+-sqrt(4*b*b-12*a*c)]/(6*a)
                    det = (4*b*b-12*a*c);
                    if(det<0) {
                        //there are no solutions!  nothing to do here
                    } else if(det==0) {
                        //there is 1 solution
                        t2 = -b/(3*a);
                        if(t2>0 && t2<1) {
                            v = ((a*t2+b)*t2+c)*t2+d;
                            if(v<t.minX) {
                                t.minX = v;
                            } else if(v>t.maxX) {
                                t.maxX = v;
                            }
                        }
                    } else {
                        //there are 2 solutions:
                        det = Math.sqrt(det);
                        t2 = (-2*b+det)/(6*a);
                        if(t2>0 && t2<1) {
                            v = ((a*t2+b)*t2+c)*t2+d;
                            if(v<t.minX) {
                                t.minX = v;
                            } else if(v>t.maxX) {
                                t.maxX = v;
                            }
                        }
                        
                        t2 = (-2*b-det)/(6*a);
                        if(t2>0 && t2<1) {
                            v = ((a*t2+b)*t2+c)*t2+d;
                            if(v<t.minX) {
                                t.minX = v;
                            } else if(v>t.maxX) {
                                t.maxX = v;
                            }
                        }
                    }
                    
                    //same thing for the y's:
                    a = -lastY+3*t.data[1]-3*t.data[3]+t.data[5];
                    b = 3*lastY-6*t.data[1]+3*t.data[3];
                    c = -3*lastY+3*t.data[1];
                    d = lastY;
                    
                    det = (4*b*b-12*a*c);
                    if(det<0) {
                    } else if(det==0) {
                        t2 = -b/(3*a);
                        if(t2>0 && t2<1) {
                            v = ((a*t2+b)*t2+c)*t2+d;
                            if(v<t.minY) {
                                t.minY = v;
                            } else if(v>t.maxY) {
                                t.maxY = v;
                            }
                        }
                    } else {
                        det = Math.sqrt(det);
                        t2 = (-2*b+det)/(6*a);
                        if(t2>0 && t2<1) {
                            v = ((a*t2+b)*t2+c)*t2+d;
                            if(v<t.minY) {
                                t.minY = v;
                            } else if(v>t.maxY) {
                                t.maxY = v;
                            }
                        }
                        
                        t2 = (-2*b-det)/(6*a);
                        if(t2>0 && t2<1) {
                            v = ((a*t2+b)*t2+c)*t2+d;
                            if(v<t.minY) {
                                t.minY = v;
                            } else if(v>t.maxY) {
                                t.maxY = v;
                            }
                        }
                    }
                } else if(t.type!=PathIterator.SEG_LINETO)  {
                    throw new RuntimeException("Unexpected path iterator.");
                }
                
                
                lastX = t.data[t.data.length-2];
                lastY = t.data[t.data.length-1];
                if(lastX<t.minX) {
                    t.minX = lastX;
                } else if(lastX>t.maxX) {
                    t.maxX = lastX;
                }
                if(lastY<t.minY) {
                    t.minY = lastY;
                } else if(lastY>t.maxY) {
                    t.maxY = lastY;
                }
            }
            t = t.next;
        }
    }
}
