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
import java.awt.geom.AffineTransform;

/**
 * This spins one frame in/out from the center. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of RotateTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/RotateTransition2D/RotateIn.gif"
 * alt="Rotate In">
 * <p>
 * Rotate In</td>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/RotateTransition2D/RotateOut.gif"
 * alt="Rotate Out">
 * <p>
 * Rotate Out</td>
 * </tr>
 * </table>
 */
public class RotateTransition2D extends Transition2D {

	/**
	 * TODO: remove all getDemoTransitions() methods or create a new tool to invoke them.
	 * 
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new RotateTransition2D(IN),
				new RotateTransition2D(OUT) };
	}

	int type;

	/**
	 * Creates a new RotateTransition2D that rotates in.
	 * 
	 */
	public RotateTransition2D() {
		this(IN);
	}

	/**
	 * Creates a new RotateTransition2D
	 * 
	 * @param type
	 *            must be IN or OUT
	 */
	public RotateTransition2D(int type) {
		if (!(type == IN || type == OUT)) {
			throw new IllegalArgumentException("type must be IN or OUT");
		}
		this.type = type;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		if (type == OUT) {
			progress = 1 - progress;
		}
		AffineTransform transform = new AffineTransform();
		transform.translate(size.width / 2, size.height / 2);
		transform.scale(progress, progress);
		transform.rotate((1 - progress) * 6);
		transform.translate(-size.width / 2, -size.height / 2);

		return new ImageInstruction[] { new ImageInstruction(type == IN),
				new ImageInstruction(type != IN, transform, null) };
	}

	@Override
	public String toString() {
		if (type == IN) {
			return "Rotate In";
		} else {
			return "Rotate Out";
		}
	}
}