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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** A Transition that plays another transition in reverse.
 */
public class ReversedTransition implements Transition {
	Transition transition;
	
	public ReversedTransition(Transition t) {
		transition = t;
	}

	@Override
	public void paint(Graphics2D g, BufferedImage frameA, BufferedImage frameB,
			float progress) {
		transition.paint(g, frameB, frameA, 1-progress);
	}
	
}