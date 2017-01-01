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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


/** This uses Java2D operations to transition between two images.
 * Each operation is wrapped up in a Transition2DInstruction object.
 * 
 */
public abstract class Transition2D extends AbstractTransition {

	/** This determines how to transition from A to B.
	 * @param progress a float from [0,1].  When this is zero, these instructions should
	 * render the initial frame.  When this is one, these instructions should render
	 * the final frame.
	 *
	 */
	public abstract Transition2DInstruction[] getInstructions(float progress,Dimension size);

	/** This calls Transition2DInstruction.paint() for each instruction
	 * in this transition.
	 * <P>It is made final to reinforce that the instructions should be
	 * all that is needed to implement these transitions.
	 */
	protected final void doPaint(Graphics2D g,BufferedImage frameA,BufferedImage frameB,float progress) {
		Transition2DInstruction[] i = getInstructions(progress,new Dimension(frameA.getWidth(),frameA.getHeight()));
		for(int a = 0; a<i.length; a++) {
			i[a].paint(g, frameA, frameB);
		}
	}
}