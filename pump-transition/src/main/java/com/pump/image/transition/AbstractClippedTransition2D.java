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
package com.pump.image.transition;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Area;

/** This transition merges several shapes, and simply shows the incoming image
 * as clipped through that collective area.
 *
 */
public abstract class AbstractClippedTransition2D extends Transition2D {

	/** This returns a list of the shapes used to make the clipped area.
	 * @param progress the progress (from 0 to 1) of this transition
	 * @param size the dimensions of the frame
	 * @return the shapes that make up the clipped area. */
	public abstract Shape[] getShapes(float progress,Dimension size);
	
	/** An optional stroke width.
	 * This may be zero, but you might be surprised at the different
	 * a little stroke makes...
	 * @param progress the progress of the transition
	 * @return  the width of the stroke
	 */
	public abstract float getStrokeWidth(float progress, Dimension size);
	
	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Shape[] data = getShapes(progress,size);
		Area area = new Area();
		for(int a = 0; a<data.length; a++) {
			Area newShape = new Area(data[a]);
			area.add(newShape);
		}

		float w = getStrokeWidth(progress, size);
		if(w==0) {
			return new Transition2DInstruction[] {
					new ImageInstruction(true),	
					new ImageInstruction(false,null,area)
			};
		} else {
			return new Transition2DInstruction[] {
					new ImageInstruction(true),	
					new ImageInstruction(false,null,area),
					new ShapeInstruction(area,null,Color.black,w)
			};
		}
	}
}