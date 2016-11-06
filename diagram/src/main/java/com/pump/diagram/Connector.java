/*
 * @(#)Connector.java
 *
 * $Date: 2016-04-23 10:22:41 +0500 (Sat, 23 Apr 2016) $
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
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;

public class Connector implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final Key<Box> KEY_BOX1 = new Key<>("box-1", Box.class);
	public static final Key<Box> KEY_BOX2 = new Key<>("box-2", Box.class);
	public static final Key<BinaryRelationship> KEY_RELATIONSHIP = new Key<>("relationship", BinaryRelationship.class);
	public static final Key<Point> KEY_CONTROL_POINT = new Key<>("control-point", Point.class);
	

	ObservableProperties properties = new ObservableProperties();
	
	public Connector(Box box1,Box box2,BinaryRelationship relationship) {
		setBox1(box1);
		setBox2(box2);
		setRelationship(relationship);
	}
	
	public void setBox1(Box box1) {
		properties.set(KEY_BOX1, box1);
	}
	
	public void setBox2(Box box2) {
		properties.set(KEY_BOX2, box2);
	}
	
	public void setRelationship(BinaryRelationship relationship) {
		properties.set(KEY_RELATIONSHIP, relationship);
	}
	
	public Box getBox1() {
		return properties.get(KEY_BOX1);
	}
	
	public Box getBox2() {
		return properties.get(KEY_BOX2);
	}

	public Point getControlPoint(Point dest) {
		Point p = properties.get(KEY_CONTROL_POINT);
		if(p!=null) {
			dest.x = p.x;
			dest.y = p.y;
		} else {
			Rectangle r1 = getBox1().getBounds();
			Rectangle r2 = getBox2().getBounds();
			dest.x = (r1.x+r1.width/2+r2.x+r2.width/2)/2;
			dest.y = (r1.y+r1.height/2+r2.y+r2.height/2)/2;
		}
		return dest;
	}
	
	public Point getControlPoint(boolean canReturnNull) {
		Point p = properties.get(KEY_CONTROL_POINT);
		if(p!=null)
			return new Point(p.x, p.y);
		if(canReturnNull)
			return null;
		
		Rectangle r1 = getBox1().getBounds();
		Rectangle r2 = getBox2().getBounds();
		
		int x, y;
		
		if(r1.getMaxX() < r2.getMinX()) {
			x = (int)(r1.getMaxX()/2.0 + r2.getMinX()/2.0);
		} else if(r2.getMaxX() < r1.getMinX()) {
			x = (int)(r2.getMaxX()/2.0 + r1.getMinX()/2.0);
		} else {
			x = (int)(r1.getCenterX()/2.0 + r2.getCenterX()/2.0);
		}
		
		if(r1.getMaxY() < r2.getMinY()) {
			y = (int)(r1.getMaxY()/2.0 + r2.getMinY()/2.0);
		} else if(r2.getMaxY() < r1.getMinY()) {
			y = (int)(r2.getMaxY()/2.0 + r1.getMinY()/2.0);
		} else {
			y = (int)(r1.getCenterY()/2.0 + r2.getCenterY()/2.0);
		}
		
		p = new Point(x,y);
		
		return p;
	}
	
	public void setControlPoint(Point p) {
		properties.set(KEY_CONTROL_POINT, p);
	}
	
	public BinaryRelationship getRelationship() {
		return properties.get(KEY_RELATIONSHIP);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		properties.removeListener(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		properties.addListener(listener);
	}

	public <T> T get(Key<T> key)
	{
		return properties.get(key);
	}
}
