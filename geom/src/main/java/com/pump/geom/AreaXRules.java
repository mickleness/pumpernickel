/*
 * @(#)AreaXRules.java
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

import com.pump.geom.area.AreaXBody;

/** This executes several geometric operations at once on
 * a <code>AreaXBody</code> object.
 * <p>This class sequentially interprets each operation, but the reason
 * this class exists is for subclasses to come along and cleverly
 * optimize how these calls are made.
 * <p>For example: If it can be determined
 * that a new shape lies inside an existing shape, then that addition
 * doesn't have to performed.  Also adding is commutative, so if a filter
 * for redundant additions is in place <i>and</i> the add operations are
 * sorted from bigger-to-smaller shapes: 100 add operations could
 * -- in specific instances -- be simplified to just 1 or 2
 * operations.
 * <p>All AreaXRules should return the same final result when
 * asked to execute the same steps.
 * <p>Also an instance of this object may be applied to multiple
 * <code>AreaX</code> objects, so it is important that this object
 * not use its own class-level variables.  Several different
 * threads may call <code>execute(...)</code> for different
 * operations at the same time.
 */
public class AreaXRules {
	/**
	 * 
	 * @param body
	 * @param ops an array of operations to perform.  Null elements in this
	 * array are skipped.
	 */
	public AreaXBody execute(AreaXBody body,AreaXOperation[] ops) {
		return execute(body, 0, ops.length-1, ops);
	}

	/**
	 * 
	 * @param body
	 * @param ops an array of operations to perform.  Null elements in this
	 * array are skipped.
	 */
	public AreaXBody execute(AreaXBody body,int startIndex, int endIndex,AreaXOperation[] ops) {
		for(int a = startIndex; a<=endIndex; a++) {
			if(ops[a]!=null) {
				Shape operand = ops[a].getOperand();
				AreaXBody operandBody;
				if(operand instanceof AreaX) {
					operandBody = ((AreaX)operand).getBody();
				} else {
					operandBody = new AreaX(operand).getBody();
				}
				if(ops[a].getOperator()==AreaXOperation.ADD) {
					body = body.add( operandBody );
				} else if(ops[a].getOperator()==AreaXOperation.SUBTRACT) {
					body = body.subtract( operandBody );
				} else if(ops[a].getOperator()==AreaXOperation.INTERSECT) {
					body = body.intersect( operandBody );
				} else if(ops[a].getOperator()==AreaXOperation.XOR) {
					body = body.xor( operandBody );
				}
			}
		}
		return body;
	}
}
