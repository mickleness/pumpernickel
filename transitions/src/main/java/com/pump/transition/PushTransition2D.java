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
 * This is the standard "push" transition. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of PushTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PushTransition2D/PushLeft.gif"
 * alt="Push Left">
 * <p>
 * Push Left</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PushTransition2D/PushRight.gif"
 * alt="Push Right">
 * <p>
 * Push Right</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PushTransition2D/PushUp.gif"
 * alt="Push Up">
 * <p>
 * Push Up</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PushTransition2D/PushDown.gif"
 * alt="Push Down">
 * <p>
 * Push Down</td>
 * </tr>
 * </table>
 */
public class PushTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new PushTransition2D(Transition.LEFT),
				new PushTransition2D(Transition.DOWN), new PushTransition2D(Transition.UP),
				new PushTransition2D(Transition.RIGHT) };
	}

	int type;

	/**
	 * Creates a push-right transition.
	 */
	public PushTransition2D() {
		this(Transition.RIGHT);
	}

	/**
	 * Creates a new PushTransition2D
	 * 
	 * @param type
	 *            must be LEFT, RIGHT, UP or DOWN
	 */
	public PushTransition2D(int type) {
		if (!(type == Transition.RIGHT || type == Transition.LEFT || type == Transition.UP || type == Transition.DOWN)) {
			throw new IllegalArgumentException(
					"The type must be LEFT, RIGHT, UP or DOWN");
		}
		this.type = type;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		AffineTransform transform1 = new AffineTransform();
		AffineTransform transform2 = new AffineTransform();

		if (type == Transition.LEFT) {
			transform2.translate(size.width * (1 - progress), 0);
			transform1.translate(size.width * (1 - progress) - size.width, 0);
		} else if (type == Transition.RIGHT) {
			transform2.translate(size.width * (progress - 1), 0);
			transform1.translate(size.width * (progress - 1) + size.width, 0);
		} else if (type == Transition.UP) {
			transform2.translate(0, size.height * (1 - progress));
			transform1.translate(0, size.height * (1 - progress) - size.height);
		} else {
			transform2.translate(0, size.height * (progress - 1));
			transform1.translate(0, size.height * (progress - 1) + size.height);
		}

		return new Transition2DInstruction[] {
				new ImageInstruction(true, transform1, null),
				new ImageInstruction(false, transform2, null) };
	}

	@Override
	public String toString() {
		if (type == Transition.RIGHT) {
			return "Push Right";
		} else if (type == Transition.LEFT) {
			return "Push Left";
		} else if (type == Transition.DOWN) {
			return "Push Down";
		} else {
			return "Push Up";
		}
	}
}