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
 * This takes the current frame and slides it away to reveal the new frame
 * underneath. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of RevealTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/RevealTransition2D/RevealLeft.gif"
 * alt="Reveal Left">
 * <p>
 * Reveal Left</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/RevealTransition2D/RevealRight.gif"
 * alt="Reveal Right">
 * <p>
 * Reveal Right</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/RevealTransition2D/RevealUp.gif"
 * alt="Reveal Up">
 * <p>
 * Reveal Up</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/RevealTransition2D/RevealDown.gif"
 * alt="Reveal Down">
 * <p>
 * Reveal Down</td>
 * </tr>
 * </table>
 */
public class RevealTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new RevealTransition2D(Transition.LEFT),
				new RevealTransition2D(Transition.RIGHT), new RevealTransition2D(Transition.UP),
				new RevealTransition2D(Transition.DOWN) };
	}

	int direction;

	/**
	 * Creates a new RevealTransition2D that slides to the left.
	 * 
	 */
	public RevealTransition2D() {
		this(Transition.LEFT);
	}

	/**
	 * Creates a new RevealTransition2D
	 * 
	 * @param direction
	 *            must be LEFT, RIGHT, UP or DOWN
	 */
	public RevealTransition2D(int direction) {
		if (!(direction == Transition.LEFT || direction == Transition.RIGHT || direction == Transition.UP || direction == Transition.DOWN))
			throw new IllegalArgumentException(
					"Direction must be LEFT, UP, RIGHT or DOWN");
		this.direction = direction;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		AffineTransform transform;

		if (direction == Transition.LEFT) {
			transform = AffineTransform.getTranslateInstance(-progress
					* size.width, 0);
		} else if (direction == Transition.RIGHT) {
			transform = AffineTransform.getTranslateInstance(progress
					* size.width, 0);
		} else if (direction == Transition.UP) {
			transform = AffineTransform.getTranslateInstance(0, -progress
					* size.height);
		} else {
			transform = AffineTransform.getTranslateInstance(0, progress
					* size.height);
		}

		return new ImageInstruction[] { new ImageInstruction(false),
				new ImageInstruction(true, transform, null) };
	}

	@Override
	public String toString() {
		if (direction == Transition.UP) {
			return "Reveal Up";
		} else if (direction == Transition.LEFT) {
			return "Reveal Left";
		} else if (direction == Transition.RIGHT) {
			return "Reveal Right";
		} else {
			return "Reveal Down";
		}
	}
}