/*
 * @(#)Relationship.java
 *
 * $Date: 2015-09-13 14:46:53 -0400 (Sun, 13 Sep 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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
package com.pump.diagram;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public enum Relationship
{
	NONE, 
	MANY() {

		@Override
		public void appendDecoration(GeneralPath path, Point target, Point source) {
			double theta = Math.atan2(source.y - target.y, source.x - target.x);
			double k1 = 10;
			double k2 = 4;
			path.moveTo(target.x + k1*Math.cos(theta), target.y + k1*Math.sin(theta));
			path.lineTo(target.x + k2*Math.cos(theta+Math.PI/2.0), target.y + k2*Math.sin(theta+Math.PI/2.0));
			
			path.moveTo(target.x + k1*Math.cos(theta), target.y + k1*Math.sin(theta));
			path.lineTo(target.x + k2*Math.cos(theta-Math.PI/2.0), target.y + k2*Math.sin(theta-Math.PI/2.0));
		}
	}, 
	ONE() {

		@Override
		public void appendDecoration(GeneralPath path, Point target, Point source) {
			double theta = Math.atan2(source.y - target.y, source.x - target.x);
			double k1 = 8;
			double k2 = 6;
			Point2D p = new Point2D.Double(target.x + k1*Math.cos(theta), target.y+ k1*Math.sin(theta));
			path.moveTo(p.getX() + k1*Math.cos(theta+Math.PI/2.0), p.getY() + k2*Math.sin(theta+Math.PI/2.0));
			path.lineTo(p.getX() + k1*Math.cos(theta-Math.PI/2.0), p.getY() + k2*Math.sin(theta-Math.PI/2.0));
			
		}
	}, 
	PLAIN;
	

	public void appendDecoration(GeneralPath path, Point target, Point source)
	{

	}
}
