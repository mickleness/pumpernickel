/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.transition;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;

/**
 * This is basically a "slide down" transition, but with a bounce at the bottom.
 * Here is a playback sample:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/DropTransition2D/Drop.gif"
 * alt="DropTransition2D Demo">
 */
public class DropTransition2D extends Transition2D {

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		float dy;
		if (progress < .8) {
			progress = progress / .8f;
			dy = -progress * progress + 1;
			dy = 1 - dy;
		} else {
			progress = (progress - .8f) / .2f;
			dy = -4 * (progress - .5f) * (progress - .5f) + 1;
			dy = 1 - dy * .1f;
		}
		AffineTransform transform = AffineTransform.getTranslateInstance(0, dy
				* size.height - size.height);

		return new ImageInstruction[] { new ImageInstruction(true),
				new ImageInstruction(false, transform, null) };
	}

	@Override
	public String toString() {
		return "Drop";
	}
}