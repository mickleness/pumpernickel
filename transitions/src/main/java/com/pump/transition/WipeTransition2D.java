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
import java.awt.geom.Rectangle2D;

/**
 * This is the standard "wipe" transition. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of WipeTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/WipeTransition2D/WipeLeft.gif"
 * alt="Wipe Left">
 * <p>
 * Wipe Left</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/WipeTransition2D/WipeRight.gif"
 * alt="Wipe Right">
 * <p>
 * Wipe Right</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/WipeTransition2D/WipeUp.gif"
 * alt="Wipe Up">
 * <p>
 * Wipe Up</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/WipeTransition2D/WipeDown.gif"
 * alt="Wipe Down">
 * <p>
 * Wipe Down</td>
 * </tr>
 * </table>
 */
public class WipeTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new WipeTransition2D(UP),
				new WipeTransition2D(LEFT), new WipeTransition2D(DOWN),
				new WipeTransition2D(RIGHT) };
	}

	int direction;

	/**
	 * Creates a wipe transition that wipes to the right.
	 * 
	 */
	public WipeTransition2D() {
		this(RIGHT);
	}

	/**
	 * Creates a wipe transition
	 * 
	 * @param direction
	 *            must be LEFT, UP, DOWN or RIGHT
	 */
	public WipeTransition2D(int direction) {
		this.direction = direction;
		if (!(direction == LEFT || direction == UP || direction == RIGHT || direction == DOWN))
			throw new IllegalArgumentException();
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Rectangle2D clipping = null;
		if (direction == RIGHT) {
			clipping = new Rectangle2D.Double(0, 0, progress * size.width,
					size.height);
		} else if (direction == LEFT) {
			double x = (1 - progress) * size.width;
			clipping = new Rectangle2D.Double(x, 0, size.width - x, size.height);
		} else if (direction == DOWN) {
			clipping = new Rectangle2D.Double(0, 0, size.width, progress
					* size.width);
		} else if (direction == UP) {
			double y = (1 - progress) * size.height;
			clipping = new Rectangle2D.Double(0, y, size.width, size.height - y);
		}
		return new Transition2DInstruction[] { new ImageInstruction(true),
				new ImageInstruction(false, null, clipping) };
	}

	@Override
	public String toString() {
		if (direction == RIGHT) {
			return "Wipe Right";
		} else if (direction == LEFT) {
			return "Wipe Left";
		} else if (direction == DOWN) {
			return "Wipe Down";
		} else {
			return "Wipe Up";
		}
	}
}