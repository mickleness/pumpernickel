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
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/** This collects a series of points and creates a smooth curve.
 * <P>For example, if the user drags the mouse and a shape is made from
 * those points it will appear jagged and unappealing.  This class will
 * collect those points and return nice curved shapes.  Some points will
 * be "dropped" for the sake of creating a smoother curve.
 *
 * @see com.bric.plaf.MouseSmoothingShapeCreationUI
 */
public class BasicMouseSmoothing implements MouseSmoothing {
	/** The amount of error that is allowed when creating segments. */
	protected float errorTolerance;
	
	/** The multiplier used to determine how far bezier control points are
	 * from the end points.
	 */
	protected float bezierFactor;
	
	/** A list of <code>VNode</code> objects, representing the points to be smoothed. */
	protected List<VNode> nodes = new ArrayList<VNode>();
	
	/** Creates a default <code>BasicMouseSmoothing</code> with the recommended settings. */
	public BasicMouseSmoothing() {
		this(.2f, 1f/3f);
	}
	
	/**
	 * 
	 * @param errorTolerance The error tolerance is used to control how much the new shape
	 * can deviate from the original.  A recommended value is .2.
	 * @param bezierFactor The bezier factor is used to control how "strong" the
	 * bezier control points are.  That is: how far away they are
	 * from the endpoints of each arc.  A recommended value is 1/3.
	 */
	public BasicMouseSmoothing(float errorTolerance,float bezierFactor) {
		setErrorTolerance(errorTolerance);
		setBezierFactor(bezierFactor);
	}

	/** Returns the error tolerance.
	 * 
	 * @see #setErrorTolerance(float)
	 */
	public float getErrorTolerance() {
		return errorTolerance;
	}
	
	/** Returns the bezier factor.
	 * 
	 * @see #setBezierFactor(float)
	 */
	public float getBezierFactor() {
		return bezierFactor;
	}
	
	/** Sets the error tolerance for this <code>BasicMouseSmoothing</code>.
	 * 
	 * 
	 * @param f The error tolerance is used to control how much the new shape
	 * can deviate from the original.  A recommended value is .2.
	 */
	public void setErrorTolerance(float f) {
		errorTolerance = f;
	}

	/** Sets the bezier factor for this <code>BasicMouseSmoothing</code>.
	 * 
	 * 
	 * @param f The bezier factor is used to control how "strong" the
	 * bezier control points are.  That is: how far away they are
	 * from the endpoints of each arc.  A recommended value is 1/3.
	 */
	public void setBezierFactor(float f) {
		bezierFactor = f;
	}
	
	public void reset() {
		nodes.clear();
	}
	
	/** This constructor accepts a String created by BasicMouseSmoothing.toString()
	 * to recreate a BasicMouseSmoothing object.
	 * <P>This is intended for debugging.
	 */
	protected BasicMouseSmoothing(String s) {
		if(s.indexOf('[')!=-1)
			s = s.substring(s.indexOf('[')+1);
		if(s.indexOf(']')!=-1)
			s = s.substring(0,s.indexOf(']'));
		
		StringTokenizer st = new StringTokenizer(s);
		while(st.hasMoreTokens()) {
			String s2 = st.nextToken();
			float x = Float.parseFloat(s2);
			s2 = st.nextToken();
			float y = Float.parseFloat(s2);
			s2 = st.nextToken();
			long t = Long.parseLong(s2);
			add(x,y,t);
		}
	}
	
	/** Adds a point to be smoothed.
	 * 
	 * @param x the x-coordinate of the new point
	 * @param y the y-coordinate of the new point
	 * @param time the time this point was added
	 */
	public synchronized void add(float x,float y,long time) {
		if(nodes.size()>0) {
			VNode last = nodes.get(nodes.size()-1);
			last.deltaT = time-last.t;
		}
		nodes.add(new VNode(x,y,time));
	}
	
	/** An array of thetas, each entry corresponds with a
	 * point in <code>nodes</codes>.
	 */
	private synchronized float[] getThetas() {
		float[] array = new float[nodes.size()];
		for(int a = 0; a<array.length; a++) {
			VNode n = nodes.get(a);
			if(n.hasTheta) {
				array[a] = (float)n.theta;
			}
		}
		return array;
	}

	
	private void calculateTheta(int index) {
		VNode node = nodes.get(index);
		//if(node.deltaT>250) {
		//	node.hasTheta = false;
		//	return;
		//}
		/** Find the neighboring nodes, and get the slope
		 * between them.  But if the adjacent nodes are
		 * physically really close, expand our search a little bit
		 */
		
		int k = index-1;
		float dx = 0;
		float dy = 0;
		boolean significant = true;
		while(k>0 && significant) {
			VNode prevNode = nodes.get(k);
			double distanceSq = (node.x-prevNode.x)*(node.x-prevNode.x)+
				(node.y-prevNode.y)*(node.y-prevNode.y);
			if(distanceSq>0) {
				dx -= (prevNode.x-node.x)/distanceSq;
				dy -= (prevNode.y-node.y)/distanceSq;
			}
			if(distanceSq>5*5) {
				significant = false;
			}
			k--;
		}

		k = index+1;
		significant = true;
		while(k<nodes.size() && significant) {
			VNode nextNode = nodes.get(k);
			double distanceSq = (node.x-nextNode.x)*(node.x-nextNode.x)+
				(node.y-nextNode.y)*(node.y-nextNode.y);
			if(distanceSq>0) {
				dx += (nextNode.x-node.x)/distanceSq;
				dy += (nextNode.y-node.y)/distanceSq;
			}
			if(distanceSq>5*5) {
				significant = false;
			}
			k++;
		}
		
		node.theta = Math.atan2(dy,dx);
		node.hasTheta = true;
	}
	
	/** Calculates the theta value for every node.
	 * 
	 */
	protected synchronized void calculateThetas() {
		for(int a = 1; a<nodes.size()-1; a++) {
			calculateTheta(a);
		}
		
		/** Iron out the angles even more:
		 * Let the angle for node n be the average of its
		 * original angle, plus the original angle of its neighbors
		 */
		float[] thetas = getThetas();
		for(int a = 1; a<nodes.size()-1; a++) {
			VNode n0 = nodes.get(a-1);
			VNode n1 = nodes.get(a);
			VNode n2 = nodes.get(a+1);
			
			if(n1.hasTheta) {
				double dx = 100*Math.cos(thetas[a]);
				double dy = 100*Math.sin(thetas[a]);
				if(n0.hasTheta) {
					dx = dx+100*Math.cos(thetas[a-1]);
					dy = dy+100*Math.sin(thetas[a-1]);
				}
				if(n2.hasTheta) {
					dx = dx+100*Math.cos(thetas[a+1]);
					dy = dy+100*Math.sin(thetas[a+1]);
				}
				n1.theta = Math.atan2(dy,dx);
			}
		}
	}
	
	public synchronized boolean isEmpty() {
		return nodes.size()<=1;
	}
	
	public GeneralPath getShape() {
		GeneralPath path = new GeneralPath();
		getShape(path);
		return path;
	}
	
	public synchronized void getShape(GeneralPath path) {
		calculateThetas();
		
		List<VNode> list = nodes;
		
		/** Now we simply make segments.  Make the segment as large
		 * as we possible can until we exceed our error tolerance.
		 */
		
		int i = 0;
		int lastI = -1;
		while(i<list.size()-1) {
			VNode n = list.get(i);
			if(i==0) {
				path.moveTo( n.x, n.y);
				lastI = 0;
				i++;
			} else {
				float error = 0;
				AbstractSegment lastArc = null;
				AbstractSegment arc = null;
				do {
					lastArc = arc;
					i++;
					arc = new CubicSegment(lastI,i);
					error = arc.getError();
				} while(error<errorTolerance && i<nodes.size()-1);
				
				if(lastArc==null) {
					arc.append(path);
				} else {
					i--;
					lastArc.append(path);
				}
				lastI = i;
			}
		}
		
		if(nodes.size()==2) { //this is a special case
			VNode n = nodes.get(1);
			path.lineTo(n.x, n.y);
		}
	}
	
	/** A segment connecting two VNodes together.
	 * 
	 * Originally this was made abstract because I tried two types
	 * of segments: quadratic and cubic.  But the quadratic one
	 * had problems about 5% of the time, so now only use the
	 * CubicSegment.
	 *
	 */
	abstract class AbstractSegment {
		/** The index in nodes that this arc begins at. */
		final int index0;
		/** The index in nodes that this arc ends at. */
		final int index1;
		
		/** The length of each line segment along the path from
		 * index0 to index1.  This has nothing to do with calculating
		 * a curve: this refers to the polygon that would be made
		 * if there is no curve.
		 */
		float[] segmentLengths;
		
		/** The sum of the values in segmentLengths */
		float totalLength;
		
		public AbstractSegment(int index0,int index1) {
			this.index0 = index0;
			this.index1 = index1;
			segmentLengths = new float[index1-index0];
			totalLength = 0;
			//measure the distance the polyline takes from index0 to index1:
			for(int i = 0; i<segmentLengths.length; i++) {
				VNode n2 = nodes.get(i+1+index0);
				VNode n1 = nodes.get(i+index0);
				segmentLengths[i] = n2.distance(n1);
				totalLength += segmentLengths[i];
			}
		}
		
		/** The length of this segment.
		 * (This can be calculated approximately with a FlatteningPathIterator.)
		 */
		public abstract float getArcLength();
		
		/** This appends this arc to the path provided.
		 * (It is assumed the path provided ends at index0.)
		 */
		public abstract void append(GeneralPath path);
		
		/** This returns the error of this segment.
		 * The larger the error the more this segment deviates from
		 * the polyline that the source points represent.
		 */
		public abstract float getError();

		/** Returns the error of this arc (that is, the larger this
		 * value the more this segment deviates from the original polyline.
		 * 
		 * @param ax the t^3 coefficient in the x equation
		 * @param bx the t^2 coefficient in the x equation
		 * @param cx the t coefficient in the x equation
		 * @param dx the constant in the x equation
		 * @param ay the t^3 coefficient in the y equation
		 * @param by the t^2 coefficient in the y equation
		 * @param cy the t coefficient in the y equation
		 * @param dy the constant in the y equation
		 * @return the error of this arc
		 */
		public float getError(float ax,float bx,float cx, float dx, 
				float ay,float by,float cy, float dy) {
			
			float errorSum = 0;
			float fraction = 0;
			/** As we iterate over the polyline, let the fraction
			 * we have iterated represent t in the parametric curve
			 * the arguments define.
			 * This is not a perfect solution, but it works.  And
			 * there should be zero error when the segments exactly
			 * resemble the polyline, so in that respect this approach
			 * makes sense.
			 */
			for(int i = index0+1; i<index1; i++) {
				if(totalLength>0)
					fraction += segmentLengths[i-index0-1] / totalLength;
				VNode n = nodes.get(i);
				errorSum += Point2D.distance(n.x, n.y, 
						((ax*fraction+bx)*fraction+cx)*fraction+dx ,
						((ay*fraction+by)*fraction+cy)*fraction+dy );
				
			}
			if(totalLength==0) return 0;
			
			return errorSum / totalLength;
		}
	}

	/** Flattens this shape and calculates its length */
	protected static float getLength(Shape shape) {
		PathIterator i = shape.getPathIterator(null,.5);
		float[] s = new float[6];
		float sum = 0;
		float lastX = -1;
		float lastY = -1;
		while(i.isDone()==false) {
			int k = i.currentSegment(s);
			if(k==PathIterator.SEG_LINETO) {
				sum+= (float)Math.sqrt( (lastX-s[0])*(lastX-s[0]) + (lastY-s[1])*(lastY-s[1]) );
			}
			lastX = s[0];
			lastY = s[1];
			i.next();
		}
		return sum;
	}
	
	/** A scratch object used to measure distances. */
	private static CubicCurve2D cubicCurve = new CubicCurve2D.Double();
	
	/** A segment that uses cubic bezier curves.
	 * 
	 */
	class CubicSegment extends AbstractSegment {
		final float x1, y1, cx1, cy1, cx2, cy2, x2, y2;
		final boolean isLine;
		
		public CubicSegment(int index0, int index1) {
			super(index0,index1);
			VNode n1 = nodes.get(index0);
			VNode n2 = nodes.get(index1);

			//the distance from each end point the control
			//points should be:
			float k = totalLength*bezierFactor;
			
			x1 = n1.x;
			y1 = n1.y;
			x2 = n2.x;
			y2 = n2.y;
			
			if(n1.hasTheta==false && n2.hasTheta==false) {
				//straight line:
				cx1 = n1.x*1/3+n2.x*2/3;
				cy1 = n1.y*1/3+n2.y*2/3;
				cx2 = n1.x*2/3+n2.x*1/3;
				cy2 = n1.y*2/3+n2.y*1/3;
				isLine = true;
			} else if(n1.hasTheta && n2.hasTheta) {
				//one curve on each side:
				cx1 = (float)(n1.x+k*Math.cos(n1.theta));
				cy1 = (float)(n1.y+k*Math.sin(n1.theta));
				cx2 = (float)(n2.x-k*Math.cos(n2.theta));
				cy2 = (float)(n2.y-k*Math.sin(n2.theta));
				
				isLine = false;
			} else {
				//either prev or next does not have an angle
				
				isLine = false;
				
				//so calculate the angle we would use if we
				//wanted a straight line between index0 and index1:
				float ax0 = n2.x-n1.x;
				float ay0 = n2.y-n1.y;
				float theta = (float)(Math.atan2(ay0, ax0));
				
				if(n1.hasTheta==false) {
					cx1 = (float)(n1.x+k*Math.cos(theta));
					cy1 = (float)(n1.y+k*Math.sin(theta));
					cx2 = (float)(n2.x-k*Math.cos(n2.theta));
					cy2 = (float)(n2.y-k*Math.sin(n2.theta));
				} else {
					cx1 = (float)(n1.x+k*Math.cos(n1.theta));
					cy1 = (float)(n1.y+k*Math.sin(n1.theta));
					cx2 = (float)(n2.x-k*Math.cos(theta));
					cy2 = (float)(n2.y-k*Math.sin(theta));
				}
			}
		}
		
		@Override
		public float getError() {
			//convert from bezier control points to parametric coefficients:
            float ay = -y1+3*cy1-3*cy2+y2;
            float by = 3*y1-6*cy1+3*cy2;
            float cy = -3*y1+3*cy1;
            float dy = y1;
            float ax = -x1+3*cx1-3*cx2+x2;
            float bx = 3*x1-6*cx1+3*cx2;
            float cx = -3*x1+3*cx1;
            float dx = x1;
            return getError(ax,bx,cx,dx,ay,by,cy,dy);
		}
		
		@Override
		public float getArcLength() {
			synchronized(cubicCurve) {
				cubicCurve.setCurve(x1, y1, cx1, cy1, cx2, cy2, x2, y2);
				return getLength(cubicCurve);
			}
		}

		@Override
		public void append(GeneralPath p) {
			p.curveTo(cx1, cy1, cx2, cy2, x2, y2);
		}
	}
		
	/** A string representation of this object used for debugging. */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int a = 0; a<nodes.size(); a++) {
			VNode n = nodes.get(a);
			if(a!=0) {
				sb.append(' ');
			}
			sb.append(n.x);
			sb.append(' ');
			sb.append(n.y);
			sb.append(' ');
			sb.append(n.t);
		}
		return "BasicMouseSmoothing["+sb.toString()+"]";
	}
	
	/** The data collected for each point. */
	class VNode {
		float x;
		float y;
		long t;
		long deltaT;
		boolean hasTheta = false;
		double theta;
		
		public VNode(float x,float y,long time) {
			this.x = x;
			this.y = y;
			this.t = time;
		}
		
		@Override
		public String toString() {
			return "VNode[ x = "+x+", y = "+y+" theta = "+(theta*180/Math.PI)+"]";
		}
		
		/** Calculates the distance from one node to another.
		 * 
		 * @param n another node
		 * @return the distance from this node to the argument
		 */
		protected float distance(VNode n) {
			double dx = n.x-x;
			double dy = n.y-y;
			return (float)Math.sqrt(dx*dx+dy*dy);
		}
	}
}