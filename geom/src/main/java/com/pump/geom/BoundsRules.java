/*
 * @(#)BoundsRules.java
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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.pump.geom.area.AreaXBody;

/** These are <code>AreaXRules</code> that are tested against
 * calculating the addition of several related shapes (as in clip art).
 * <p>This compares the bounds of the shapes in the queue, and
 * performs two possible optimization:
 * <ul><li>Unconnected Shapes: if the bounds of two operands don't touch
 * at all, then they may be merged.  ("Merging" is an optimized type
 * of addition.)</li>
 * <li>Inner Shapes: if an outer shape contains the bounds of an inner
 * shape, then the inner shape is dropped.  (Adding it would make no
 * difference to the final result.)</li></ul>
 * <p>These rules are only tested against the addition of shapes.  They
 * should work for other operations, but the performance gains might
 * be negligible.
 */
public class BoundsRules extends AreaXRules {
	boolean lookForDisconnectedShapes;
	boolean lookForInnerShapes;

	/** Creates a new <code>BoundsRules</code> object with all
	 * possible optimizations.
	 */
	public BoundsRules() {
		this(true, true);
	}
	
	/** Creates a new <code>BoundsRules</code> object.
	 * 
	 * @param lookForInnerShapes if this is true then these rules will
	 * look for inner shapes and ignore them.
	 * @param lookForDisconnectedShapes if this is true then these rules
	 * will look for disconnected shapes and merge them.
	 */
	public BoundsRules(boolean lookForInnerShapes,boolean lookForDisconnectedShapes) {
		this.lookForInnerShapes = lookForInnerShapes;
		this.lookForDisconnectedShapes = lookForDisconnectedShapes;
	}
	
	@Override
	public AreaXBody execute(AreaXBody body, AreaXOperation[] operations) {
		List<Object> vector = new ArrayList<Object>();
		int startOfRun = getStartingIndex(-1,operations);
		while(startOfRun!=-1 && startOfRun<operations.length) {
			int operator = operations[startOfRun].operator;
			int endOfRun = startOfRun;
			
			int k = startOfRun+1;
			while(k<operations.length) {
				if(operations[k]!=null) {
					if(operations[k].operator==operator) {
						endOfRun = k;
						k++;
					} else {
						endOfRun = k-1;
						k = operations.length;
					}
				} else {
					k++;
				}
			}
			body = executeRun(body, operator, startOfRun, endOfRun, operations, vector);
			startOfRun = getStartingIndex(endOfRun+1, operations);
		}
		return body;
	}
	
	private int getStartingIndex(int initialIndex,AreaXOperation[] operations) {
		int k = initialIndex;
		while(k<operations.length) {
			if(k>=0) {
				if(operations[k]!=null)
					return k;
			}
			k++;
		}
		return k;
	}

	private AreaXBody executeRun(AreaXBody body, int operator, int startIndex, int endIndex, AreaXOperation[] operations, List<Object> vector) {
		//The truck clip art (see AddRulesTest) performed very badly if we didn't convert everything to an AreaX up front:
		for(int a = startIndex; a<=endIndex; a++) {
			Shape shape = operations[a].getOperand();
			if(!(shape instanceof AreaX)) {
				AreaXBody newBody = AreaXBody.create( shape.getPathIterator( null ), false);
				operations[a] = new AreaXOperation( new AreaX( newBody ), operator);
			}
		}
		
		if(lookForDisconnectedShapes || lookForInnerShapes) {
			boolean canSearchInnerShapes = operator==AreaXOperation.ADD || operator==AreaXOperation.SUBTRACT;
			List<Object> listOfUnconnectedShapes = vector;
			if(operator!=AreaXOperation.INTERSECT) {
				for(int a = startIndex; a<endIndex; a++) {
					listOfUnconnectedShapes.clear();
					
					if(operations[a]!=null) {
						AreaX base = (AreaX)operations[a].getOperand();
						Rectangle2D baseBounds = operations[a].getBounds();
						vector.add(operations[a]);
						for(int b = a+1; b<=endIndex; b++) {
							Rectangle2D bBounds = operations[b]!=null ? operations[b].getBounds() : null;
							//here we remove shapes that lie inside operand A
							if(lookForInnerShapes && canSearchInnerShapes && operations[b]!=null && baseBounds.contains(bBounds)) {
								if(base.contains(operations[b].getBounds())) {
									operations[b] = null;
								}
							}
							
							//here we search for shapes that aren't connected:
							if(lookForDisconnectedShapes && operations[b]!=null) {
								boolean failed = false;
								for(int c = 0; failed==false && c<vector.size(); c++) {
									AreaXOperation op = (AreaXOperation)vector.get(c);
									if(bBounds.intersects(op.getBounds())) {
										failed = true;
									}
								}
								if(!failed) {
									listOfUnconnectedShapes.add(operations[b]);
									operations[b] = null;
								}
							}
						}
						if(listOfUnconnectedShapes.size()>1) {
							AreaXBody newBody;
							if(base!=null) {
								newBody = base.getBody();
							} else {
								newBody = ((AreaX)operations[a].getOperand()).getBody();
							}
							int size = listOfUnconnectedShapes.size();
							//(the zeroeth element is another copy of operations[a])
							for(int b = 1; b<size; b++) {
								Shape shape = ((AreaXOperation)listOfUnconnectedShapes.get(b)).getOperand();
								AreaXBody bodyB = ((AreaX)shape).getBody();
								vector.set(b, bodyB);
							}
							newBody = newBody.merge( vector, 1, size-1 );
							operations[a] = new AreaXOperation(new AreaX(newBody), operator);
						}
					}
				}
			}
		}
		
		return super.execute(body, startIndex, endIndex, operations);
	}
}
