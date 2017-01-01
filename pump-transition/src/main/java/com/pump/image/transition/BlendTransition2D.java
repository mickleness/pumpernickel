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

/** Also know as the "Fade" transition, this simply fades in the opacity of the incoming
 * frame. Here is a playback sample:
 * <p><img src="https://javagraphics.java.net/resources/transition/BlendTransition2D/Blend.gif" alt="BlendTransition2D demo">
 *
 */
public class BlendTransition2D extends Transition2D {

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		return new Transition2DInstruction[] {
				new ImageInstruction(true),
				new ImageInstruction(false,progress,null,null)
		};
	}
	
	@Override
	public String toString() {
		return "Blend";
	}

}