/*
 * @(#)AreaXOperation.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/** An unperformed operation to an <code>AreaX</code>.
 */
public class AreaXOperation {
	public static final int ADD = 0;
	public static final int SUBTRACT = 1;
	public static final int XOR = 2;
	public static final int INTERSECT = 3;
	
	protected final Shape operand;
	protected final int operator;
	
	Rectangle2D bounds;
	
	public AreaXOperation(Shape shape,int operator) {
		if(!(operator==ADD || operator==SUBTRACT || operator==XOR || operator==INTERSECT))
			throw new IllegalArgumentException("unrecognized operator ("+operator+")");
		if(shape==null)
			throw new NullPointerException();
		
		this.operand = shape;
		this.operator = operator;
	}
	
	public int getOperator() {
		return operator;
	}
	
	public Shape getOperand() {
		return operand;
	}
	
	public Rectangle2D getBounds() {
		if(bounds==null) {
			if(operand instanceof AreaX || operand instanceof Area) {
				bounds = operand.getBounds2D();
			} else {
				bounds = ShapeBounds.getBounds(operand);
			}
		}
		return bounds;
	}
}
