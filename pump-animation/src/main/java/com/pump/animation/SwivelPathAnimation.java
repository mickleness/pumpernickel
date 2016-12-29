/*
 * @(#)SwivelPathAnimation.java
 *
 * $Date: 2016-01-30 18:40:21 -0500 (Sat, 30 Jan 2016) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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
package com.pump.animation;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.pump.awt.Dimension2D;
import com.pump.geom.MeasuredShape;
import com.pump.geom.TransformUtils;
import com.pump.math.function.Function;
import com.pump.math.function.PiecewiseFunction;
import com.pump.math.function.PolynomialFunction;

public class SwivelPathAnimation implements TransformAnimation {
	public static class Highlight {
		double centerX, centerY, width, height, angle;
		double duration;

		public Highlight(Point2D p) {
			this(p.getX(), p.getY());
		}
		
		public Highlight(double centerX, double centerY) {
			setCenter(centerX, centerY);
		}
		
		public Highlight(Rectangle2D r,double duration) {
			setCenter( r.getCenterX(), r.getCenterY() );
			setDuration(duration);
			setWidth(width);
			setHeight(height);
			setAngle(0);
		}
		
		/** Create a highlight from a serialized toString() output.
		 * @param fromString previously serialized output.
		 */
		public Highlight(String fromString) {
			try {
				String[] words = fromString.split(" ");
				if(words.length==0) throw new IllegalArgumentException();
				if("Highlight[".equals(words[0])==false)
					throw new IllegalArgumentException();
				centerX = findTerm("centerX", words);
				centerY = findTerm("centerY", words);
				width = findTerm("width", words);
				height = findTerm("height", words);
				angle = findTerm("angle", words);
				duration = findTerm("duration", words);
			} catch(RuntimeException e) {
				System.err.println("source string = \""+fromString+"\"");
				throw e;
			}
		}
		
		private double findTerm(String name,String[] words) {
			for(int a = 0; a<words.length; a++) {
				if(words[a].startsWith(name+"=")) {
					String s = words[a].substring( (name+"=").length() );
					if(s.endsWith(","))
						s = s.substring(0, s.length()-1);
					return Double.parseDouble(s);
				}
			}
			throw new IllegalArgumentException("could not identify \""+name+"\"");
		}
		
		@Override
		public String toString() {
			return "Highlight[ centerX="+centerX+", centerY="+centerY+", width="+width+", height="+height+", angle="+angle+" duration="+duration+" ]";
		}
		
		public double getCenterY() {
			return centerY;
		}
		
		public double getCenterX() {
			return centerX;
		}
		
		public Point2D getTopLeft() {
			Point2D p = new Point2D.Double(centerX - width/2, centerY - height/2);
			AffineTransform t = AffineTransform.getRotateInstance(angle, centerX, centerY);
			return t.transform(p, null);
		}
		
		public Point2D getTopRight() {
			Point2D p = new Point2D.Double(centerX + width/2, centerY - height/2);
			AffineTransform t = AffineTransform.getRotateInstance(angle, centerX, centerY);
			return t.transform(p, null);
		}
		
		public Point2D getBottomLeft() {
			Point2D p = new Point2D.Double(centerX - width/2, centerY + height/2);
			AffineTransform t = AffineTransform.getRotateInstance(angle, centerX, centerY);
			return t.transform(p, null);
		}
		
		public Point2D getBottomRight() {
			Point2D p = new Point2D.Double(centerX + width/2, centerY + height/2);
			AffineTransform t = AffineTransform.getRotateInstance(angle, centerX, centerY);
			return t.transform(p, null);
		}
		
		public Shape getOutline() {
			GeneralPath path = new GeneralPath();
			path.append(new Rectangle2D.Double(centerX-width/2, centerY-height/2, width, height), false);
			path.transform( AffineTransform.getRotateInstance(angle, centerX, centerY));
			return path;
		}
		
		public Point2D getCenter() {
			return new Point2D.Double( centerX, centerY );
		}
		
		public void setCenter(Point2D center) {
			setCenter( center.getX(), center.getY() );
		}
		
		public void setCenter(double x,double y) {
			centerX = x;
			centerY = y;
		}

		public double getWidth() {
			return width;
		}

		public void setWidth(double width) {
			this.width = width;
		}

		public double getHeight() {
			return height;
		}

		public void setHeight(double height) {
			this.height = height;
		}

		public double getAngle() {
			return angle;
		}

		public void setAngle(double angle) {
			this.angle = angle;
		}

		public double getDuration() {
			return duration;
		}

		public void setDuration(double duration) {
			this.duration = duration;
		}
	}
	
	Stage[] stages;
	double totalDuration = 0;
	
	public SwivelPathAnimation(Highlight[] highlights,double swivelDuration) {
		MeasuredShape[] paths = new MeasuredShape[Math.max(0, highlights.length-1)];
		
		if(highlights.length==2) {
			double x0 = highlights[0].getCenterX();
			double y0 = highlights[0].getCenterY();
			double x1 = highlights[1].getCenterX();
			double y1 = highlights[1].getCenterY();
			double distance = Point2D.distance(x0, y0, x1, y1)/2.0;
			double theta = Math.atan2( y1-y0, x1-x0)+Math.PI/2;
			
			double cx0 = x0+distance*Math.cos(theta);
			double cy0 = y0+distance*Math.sin(theta);
			double cx1 = x1+distance*Math.cos(theta);
			double cy1 = y1+distance*Math.sin(theta);
			paths[0] = new MeasuredShape(new CubicCurve2D.Double(x0, y0, cx0, cy0, cx1, cy1, x1, y1));
		} else {
			double[][] pathPoints = new double[paths.length][8];
			//this creates a linear model:
			for(int a = 0; a<pathPoints.length; a++) {
				pathPoints[a][0] = highlights[a].getCenterX();
				pathPoints[a][1] = highlights[a].getCenterY();
				pathPoints[a][2] = highlights[a].getCenterX();
				pathPoints[a][3] = highlights[a].getCenterY();
				pathPoints[a][4] = highlights[a+1].getCenterX();
				pathPoints[a][5] = highlights[a+1].getCenterY();
				pathPoints[a][6] = highlights[a+1].getCenterX();
				pathPoints[a][7] = highlights[a+1].getCenterY();
			}
			
			//now add some curvature:
			for(int a = 1; a<pathPoints.length; a++) {
				Highlight lastHighlight = highlights[a-1];
				Highlight h = highlights[a];
				Highlight nextHighlight = highlights[a+1];
				double x0 = lastHighlight.getCenterX();
				double y0 = lastHighlight.getCenterY();
				double x1 = nextHighlight.getCenterX();
				double y1 = nextHighlight.getCenterY();
				double x = h.getCenterX();
				double y = h.getCenterY();
				double distance0 = Point2D.distance(x0, y0, x, y)/2.0;
				double distance1 = Point2D.distance(x, y, x1, y1)/2.0;
				double theta = Math.atan2( y1-y0, x1-x0);
				pathPoints[a-1][4] = h.getCenterX()-distance0*Math.cos(theta);
				pathPoints[a-1][5] = h.getCenterY()-distance0*Math.sin(theta);
				pathPoints[a][2] = h.getCenterX()+distance1*Math.cos(theta);
				pathPoints[a][3] = h.getCenterY()+distance1*Math.sin(theta);
			}
			
			//convert them into their final form:
			for(int a = 0; a<pathPoints.length; a++) {
				CubicCurve2D curve = new CubicCurve2D.Double( 
						pathPoints[a][0],
						pathPoints[a][1],
						pathPoints[a][2],
						pathPoints[a][3],
						pathPoints[a][4],
						pathPoints[a][5],
						pathPoints[a][6],
						pathPoints[a][7] );
				paths[a] = new MeasuredShape(curve);
			}
		}

		List<Stage> stages = new ArrayList<Stage>( highlights.length*2 );
		if(highlights.length==1) {
			stages.add(new StillStage(0, highlights[0]) );
			totalDuration += stages.get(0).getDuration();
		} else if(highlights.length>1) {
			for(int a = 0; a<highlights.length-1; a++) {
				Highlight h1 = highlights[a];
				Highlight h2 = highlights[a+1];
				double duration1 = h1.getDuration()*.5;
				double duration2 = h2.getDuration()*.5;
				if(a==0)
					duration1 = h1.getDuration();
				if(a==highlights.length-2)
					duration2 = h2.getDuration();
				TransitionStage transitionStage = new TransitionStage( totalDuration, paths[a], h1, h2, duration1, swivelDuration, duration2 );
				stages.add( transitionStage );
				totalDuration += transitionStage.getDuration();
			}
		}
		
		this.stages = stages.toArray(new Stage[stages.size()]);
	}

	
	static abstract class Stage {
		double t0;
		double t1;
		
		Stage(double t0,double t1) {
			this.t0 = t0;
			this.t1 = t1;
		}
		
		double getDuration() {
			return t1-t0;
		}
		
		@Override
		public String toString() {
			return "Stage[ t0 = "+t0+", t1 = "+t1+" ]";
		}

		abstract AffineTransform getTransform(float progress,int viewWidth,int viewHeight);
	}
	
	static class StillStage extends Stage {
		Highlight h;
		
		StillStage(double startingTime, Highlight highlight) {
			super(startingTime, startingTime+highlight.getDuration());
			h = highlight;
		}

		@Override
		AffineTransform getTransform(float progress, int viewWidth,
				int viewHeight) {
			if(progress<0) progress = 0;
			if(progress>1) progress = 1;
			float k = 1-progress;
			double zoomMultiplier = .1*(k*Math.sin(Math.PI*k))+1;
			double width = h.getWidth()*zoomMultiplier;
			double height = h.getHeight()*zoomMultiplier;
			Point2D topLeft = new Point2D.Double( h.getCenterX() - width/2, h.getCenterY()-height/2 );
			Point2D topRight = new Point2D.Double( h.getCenterX() + width/2, h.getCenterY()-height/2 );
			Point2D bottomLeft = new Point2D.Double( h.getCenterX() - width/2, h.getCenterY()+height/2 );
			AffineTransform rotation = AffineTransform.getRotateInstance( h.getAngle()+progress/10.0, h.getCenterX(), h.getCenterY());
			rotation.transform(topLeft, topLeft);
			rotation.transform(topRight, topRight);
			rotation.transform(bottomLeft, bottomLeft);
			
			java.awt.geom.Dimension2D scaledSize = Dimension2D.scaleProportionally( new Dimension2D(width, height), new Dimension2D(viewWidth, viewHeight));
			
			Point2D topLeft1 = new Point2D.Double( viewWidth/2-scaledSize.getWidth()/2, 
					viewHeight/2-scaledSize.getHeight()/2 );
			Point2D topRight1 = new Point2D.Double( viewWidth/2+scaledSize.getWidth()/2, 
					viewHeight/2-scaledSize.getHeight()/2 );
			Point2D bottomLeft1 = new Point2D.Double( viewWidth/2-scaledSize.getWidth()/2, 
					viewHeight/2+scaledSize.getHeight()/2 );
			
			return TransformUtils.createAffineTransform(
					topLeft, topRight, bottomLeft,
					topLeft1, topRight1, bottomLeft1 );
		}
	}
	
	static class TransitionStage extends Stage {
		Highlight h1, h2;
		MeasuredShape shape;
		Function timeFunction;

		TransitionStage(double startingTime, 
				MeasuredShape measuredShape,
				Highlight highlight1,
				Highlight highlight2,
				double highlight1Duration,
				double swivelDuration,
				double highlight2Duration ) {
			super(startingTime, startingTime+swivelDuration+highlight1Duration+highlight2Duration);
			h1 = highlight1;
			h2 = highlight2;
			shape = measuredShape;
			
			double totalTime = highlight1Duration + swivelDuration + highlight2Duration;
			highlight1Duration /= totalTime;
			swivelDuration /= totalTime;
			highlight2Duration /= totalTime;
			
			double k = .01;
		
			timeFunction = new PiecewiseFunction( 
					new Function[] {
					PolynomialFunction.createFit(0, 0, highlight1Duration, k),
					PolynomialFunction.createFit(new double[] {
							highlight1Duration,
							highlight1Duration+swivelDuration/2,
							1-highlight2Duration
					}, new double[] {
							k, .5, 1-k
					}),
					PolynomialFunction.createFit(1-highlight2Duration, 1-k, 1, 1) },
					new double[] { 
							highlight1Duration, 1-highlight2Duration
					} );
		}

		@Override
		AffineTransform getTransform(float progress, int viewWidth,
				int viewHeight) {
			if(progress<0) progress = 0;
			if(progress>1) progress = 1;
			
			//float pointProgress = (float)(Math.cbrt(2*progress-1)/2+.5);
			double pointProgress = timeFunction.evaluate(progress);
			Point2D p = shape.getPoint( (float)(pointProgress*shape.getOriginalDistance()), null);
			double width = h1.width*(1.0-pointProgress)+h2.width*pointProgress;
			double height = h1.height*(1.0-pointProgress)+h2.height*pointProgress;
			
			//scale it smaller mid-swivel:
			double z = .25;
			double zoomMultiplier = z*Math.sin( (pointProgress-.25)*(Math.PI/.5) )+(1+z);
			width = width*zoomMultiplier;
			height = height*zoomMultiplier;
			
			double angle = tweenAngle(h1.angle, h2.angle, pointProgress);
			Point2D topLeft = new Point2D.Double( p.getX() - width/2, p.getY()-height/2 );
			Point2D topRight = new Point2D.Double( p.getX() + width/2, p.getY()-height/2 );
			Point2D bottomLeft = new Point2D.Double( p.getX() - width/2, p.getY()+height/2 );
			AffineTransform rotation = AffineTransform.getRotateInstance(angle, p.getX(), p.getY());
			rotation.transform(topLeft, topLeft);
			rotation.transform(topRight, topRight);
			rotation.transform(bottomLeft, bottomLeft);
			
			java.awt.geom.Dimension2D scaledSize = Dimension2D.scaleProportionally( new Dimension2D(width, height), new Dimension2D(viewWidth, viewHeight));
			
			Point2D topLeft1 = new Point2D.Double( viewWidth/2-scaledSize.getWidth()/2, 
					viewHeight/2-scaledSize.getHeight()/2 );
			Point2D topRight1 = new Point2D.Double( viewWidth/2+scaledSize.getWidth()/2, 
					viewHeight/2-scaledSize.getHeight()/2 );
			Point2D bottomLeft1 = new Point2D.Double( viewWidth/2-scaledSize.getWidth()/2, 
					viewHeight/2+scaledSize.getHeight()/2 );
			
			return TransformUtils.createAffineTransform(
					topLeft, topRight, bottomLeft,
					topLeft1, topRight1, bottomLeft1 );
		}
	}
	
	public Shape getPath() {
		GeneralPath p = new GeneralPath();
		for(int a = 0; a<stages.length; a++) {
			if(stages[a] instanceof TransitionStage) {
				TransitionStage st = (TransitionStage)stages[a];
				float t = st.shape.getOriginalDistance()/st.shape.getClosedDistance();
				p.append( st.shape.getShape(0, t), false);
			}
		}
		return p;
	}
	
	public float getDuration() {
		return (float)totalDuration;
	}

	public AffineTransform getTransform(float progress,int viewWidth,int viewHeight) {
		if(progress<0) progress = 0;
		if(progress>1) progress = 1;
		progress = (float)(progress*totalDuration);
		for(int a = 0; a<stages.length; a++) {
			if(stages[a].t0<=progress && progress<=stages[a].t1) {
				double stageDuration = stages[a].t1 - stages[a].t0;
				float stageProgress = (float)( (progress-stages[a].t0)/stageDuration );
				return stages[a].getTransform(  stageProgress, viewWidth, viewHeight);
			}
		}
		
		return stages[stages.length-1].getTransform(1, viewWidth, viewHeight);
	}

	private static double tweenAngle(double angle1, double angle2, double progress) {
		if( angle1-angle2 > Math.PI ) {
			angle1 -= 2*Math.PI;
		} else if( angle2-angle1 > Math.PI ) {
			angle2 -= 2*Math.PI;
		}
		return angle1*(1-progress)+angle2*progress;
	}
}
