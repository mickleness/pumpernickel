/*
 * @(#)QuadraticFlattenedPathIterator.java
 *
 * $Date: 2016-01-30 18:40:21 -0500 (Sat, 30 Jan 2016) $
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

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/** A <code>PathIterator</code> that flattens all cubic
 * segments into a series of quadratic segments.
 * <P>The number of quadratic segments used to emulate
 * a cubic segment will vary.  It may even be accurate.  :)
 * 
 */
public class QuadraticFlattenedPathIterator implements PathIterator {
	/** The maximum number of divisions between extrema this will calculate */
	protected static int MAX_DIVISIONS = 10;
	/** The underlying path information this iterates over */
	PathIterator i;
	/** Used for debugging.  If non-null, this keeps a list of
	 * the most frequently used edges of quadratic arcs.
	 */
	protected static List<Point2D> pointsVector;
	/** Used for debugging.  If non-null, this keeps a list of
	 * the extrema found in the cubic curves.
	 */
	protected static List<Point2D> extremaVector;
	/** A static repository for double arrays.
	 * Even in cases where several of these objects
	 * are created, odds are they were probably be processed
	 * one at a time, so we probably only have to create
	 * 1 double array.
	 */
	private static Stack<double[]> stack = new Stack<double[]>();
	
	/** The extra quadratic data we're iterating over that replaces a cubic curve. */
	double[] pts = null;
	/** The number of quadratic arcs to iterate in "pts" to iterate over */
	int ptSize;
	/** The current quadratic arc in "pts" we're at */
	int ptCtr;
	
	/** The most recent x and y coordinates */
	double lastX, lastY;
	
	/** Constructs a <code>QuadraticflattenedPathIterator</code>.
	 * 
	 * @param i
	 */
	public QuadraticFlattenedPathIterator(PathIterator i) {
		this.i = i;
		if(extremaVector!=null)
			extremaVector.clear();
		if(extremaVector!=null)
			pointsVector.clear();
	}
	
	/** Constructs a <code>QuadraticFlattenedPathIterator</code> */
	public QuadraticFlattenedPathIterator(Shape s) {
		this(s.getPathIterator(null));
	}
	
	public int currentSegment(double[] d) {
		if(pts!=null) {
			int base = ptCtr*4;
			d[0] = pts[base+0];
			d[1] = pts[base+1];
			d[2] = pts[base+2];
			d[3] = pts[base+3];
			
			lastX = d[2];
			lastY = d[3];
			return PathIterator.SEG_QUADTO;
		}
		int k = i.currentSegment(d);
		if(k==PathIterator.SEG_CUBICTO) {
			split(lastX,lastY,d[0],d[1],d[2],d[3],d[4],d[5]);
			return currentSegment(d);
		} else if(k==PathIterator.SEG_QUADTO) {
			lastX = d[2];
			lastY = d[3];
		} else if(k==PathIterator.SEG_LINETO || k==PathIterator.SEG_MOVETO) {
			lastX = d[0];
			lastY = d[1];
		}
		return k;
	}

	/** Split this cubic curve into lots of little quadratic curves taking on its general shape */
	private void split(double x0,double y0,double cx0,double cy0,double cx1,double cy1,double x1,double y1) {
		//define the hermitic variables:
		double ay = -y0+3*cy0-3*cy1+y1;
		double by = 3*y0-6*cy0+3*cy1;
		double cy = -3*y0+3*cy0;
		double dy = y0;
		
		double ax = -x0+3*cx0-3*cx1+x1;
		double bx = 3*x0-6*cx0+3*cx1;
		double cx = -3*x0+3*cx0;
		double dx = x0;

		pts = null;
		synchronized(stack) {
			if(stack.size()>0) {
				pts = stack.pop();
			}
		}
		if(pts==null || extremaVector!=null) {
			pts = new double[4*(8+7*MAX_DIVISIONS)];
		} else {
			for(int a = 0; a<pts.length; a++) {
				pts[a] = 0;
			}
		}
		int ctr = 2;
		pts[0] = 0; //these are guaranteed to be points of interest
		pts[1] = 1;
		ctr = study(pts,ctr,ay,by,cy); //grab the points of interest in the y-equation
		ctr = study(pts,ctr,ax,bx,cx); //now the x-equation
		Arrays.sort(pts,0,ctr);
		if(extremaVector!=null) {
			for(int a = 0; a<ctr; a++) {
				extremaVector.add(new Point2D.Double(ax*pts[a]*pts[a]*pts[a]+bx*pts[a]*pts[a]+cx*pts[a]+dx,
						ay*pts[a]*pts[a]*pts[a]+by*pts[a]*pts[a]+cy*pts[a]+dy));
			}
		}
		
		//we have a list of "interesting times".  Divide each of those into
		//DIVISIONS-many segments.
		
		//now, let's recycle the array, to minimize allocation:
		//we're going to fill it in from the right end inward
		int z = pts.length-1; //this is the index we write to
		double lastTime = 0;
		for(int a = ctr-1; a>=0; a--) {
			if(pts[a]>=0 && pts[a]<=1) {
				//this is a valid time
				if(pts[a]==1) {
					//its the first time
					pts[z--] = 1;
				} else {
					double deltaX = (ax*pts[a]*pts[a]*pts[a]+bx*pts[a]*pts[a]+cx*pts[a]+dx);
					double deltaY = (ay*pts[a]*pts[a]*pts[a]+by*pts[a]*pts[a]+cy*pts[a]+dy);
					deltaX = deltaX-(ax*lastTime*lastTime*lastTime+bx*lastTime*lastTime+cx*lastTime+dx);
					deltaY = deltaY-(ay*lastTime*lastTime*lastTime+by*lastTime*lastTime+cy*lastTime+dy);
					double dSquared = deltaX*deltaX+deltaY*deltaY;
					int div = (int)(Math.sqrt(dSquared)/5);
					if(div>MAX_DIVISIONS)
						div = MAX_DIVISIONS;
					for(int b = 0; b<div; b++) {
						pts[z--] = (div-b)*lastTime/(div+1)+(b+1)*pts[a]/(div+1);
					}
					pts[z--] = pts[a];
				}
				lastTime = pts[a];
			}
		}
		z++;
		if(pointsVector!=null) {
			for(int a = z; a<pts.length; a++) {
				pointsVector.add(new Point2D.Double(ax*pts[a]*pts[a]*pts[a]+bx*pts[a]*pts[a]+cx*pts[a]+dx,
						ay*pts[a]*pts[a]*pts[a]+by*pts[a]*pts[a]+cy*pts[a]+dy));
			}
		}
		
		//OK, so from [z,pts.length-1] inclusive are the times we're interested in.
		ptCtr = 0;
		ptSize = pts.length-z-1;
		for(int a = z; a<pts.length-1; a++) {
			try {
				split(ax,bx,cx,dx,pts,a-z,0,pts[a],pts[a+1]);
			} catch(RuntimeException e) {
				System.err.println("a = "+a+"\nz = "+z+"\npts.length = "+pts.length);
				throw e;
			}
			split(ay,by,cy,dy,pts,a-z,1,pts[a],pts[a+1]);
		}
	}
	
	/** "convert" this cubic curve from cubic to quadratic */
	private void split(double a,double b,double c,double d,double[] dest,int pt,int offset,double t0,double t1) {
		double tm = (t0+t1)/2;
		double f1 = a*t1*t1*t1+b*t1*t1+c*t1+d;
		double fm = a*tm*tm*tm+b*tm*tm+c*tm+d;
		double f0 = a*t0*t0*t0+b*t0*t0+c*t0+d;
		
		//now we want to solve for a', b' and c' such that:
		// f(t) = (a')*t*t+(b')*t+(c') when t = [0,1]
		// so more specifically:
		// f(0) = f0 = c'
		// f(.5) = fm = .25*a'+.5*b'+c'
		// f(1) = f1 = a'+b'+c'
		// so:
		// f1-f0-b' = a'
		// fm = .25*(f1-f0-b')+.5*b'+f0
		// fm - .25*f1+.25*f0-f0 = -.25*b'+.5*b'
		// 4*fm - f1+f0-4*f0 = b'
		
		double newC = f0;
		double newB = 4*fm-f1+f0-4*f0;
		double newA = f1-f0-newB;
		
		dest[pt*4+offset] = (newB+2*newC)/2;
		dest[pt*4+offset+2] = newA-newC+2*dest[pt*4+offset];
	}
	
	/** Collect the times when this curve has a critical point, or when it's second
	 * derivative changes curvature.
	 * <P>Note this does interact with a cubic curve, but we don't need the (t^0) term,
	 * since we're only studying derivatives.
	 * 
	 * @param dst the array to store everything in
	 * @param size the current size of the array
	 * @param a the coefficient of the t^3 term
	 * @param b the coefficient of the t^2 term
	 * @param c the coefficient of the t^1 term
	 * @return the size of the array, having added values to it.
	 */
	private static int study(double[] dst,int size,double a,double b,double c) {
		if(a==0) return size; //um, very bad.  But what'd be worse is to give
		//divide-by-zeros problems later...
		
		// f(t) = a*t^3+b*t^2+c*t+d
		// f'(t) = 3*a*t^2+2*b*t+c;
		
		a = 3*a;
		b = 2*b;
		
		double det = b*b-4*a*c;
		if(det==0) {
			dst[size] = -b/(2*a);
			if(dst[size]>=0 && dst[size]<=1) {
				size++; //it's a keeper
			} // else: overwrite it.  ignore we put it there.
			
		} else if(det>0) {
			det = Math.sqrt(det);
			dst[size] = (-b+det)/(2*a);
			if(dst[size]>=0 && dst[size]<=1)
				size++;

			dst[size] = (-b-det)/(2*a);

			if(dst[size]>=0 && dst[size]<=1)
				size++;
		}
	
		//a cubic curve can only change concavity once:
		// f(t) = a*t^2+b*t+c
		// f'(t) = 2*a*t+b
		
		a = 2*a;
		dst[size] = -b/a;
		if(dst[size]>=0 && dst[size]<=1)
			size++;
		
		return size;
	}

	private double[] d2;
	public int currentSegment(float[] f) {
		if(d2==null) d2 = new double[6];
		int k = currentSegment(d2);
		f[0] = (float)d2[0];
		f[1] = (float)d2[1];
		f[2] = (float)d2[2];
		f[3] = (float)d2[3];
		f[4] = (float)d2[4];
		f[5] = (float)d2[5];
		return k;
	}

	public int getWindingRule() {
		return i.getWindingRule();
	}

	public boolean isDone() {
		if(pts!=null && ptCtr<ptSize)
			return false;
		return i.isDone();
	}

	public void next() {
		if(pts!=null) {
			ptCtr++;
			if(ptCtr==ptSize) {
				synchronized(stack) {
					stack.push(pts);
					pts = null;
				}
				i.next();
			}
			return;
		}
		i.next();
	}
}
