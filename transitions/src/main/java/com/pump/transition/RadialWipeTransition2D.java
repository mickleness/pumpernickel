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
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * This uses clipping to sweep out a circular path, revealing the new frame.
 * Here are playback samples:
 * <p>
 * <table summary="Sample Animations of RadialWipeTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/RadialWipeTransition2D/RadialWipeClockwise.gif"
 * alt="Radial Wipe Clockwise">
 * <p>
 * Radial Wipe Clockwise</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/RadialWipeTransition2D/RadialWipeCounterclockwise.gif"
 * alt="Radial Wipe Counterclockwise">
 * <p>
 * Radial Wipe Counterclockwise</td>
 * </tr>
 * </table>
 *
 */
public class RadialWipeTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new RadialWipeTransition2D(CLOCKWISE),
				new RadialWipeTransition2D(COUNTER_CLOCKWISE) };
	}

	int type;

	/**
	 * Creates a new RadialTransition2D
	 * 
	 */
	public RadialWipeTransition2D() {
		this(CLOCKWISE);
	}

	/**
	 * Creates a new RadialTransition2D.
	 * 
	 * @param type
	 *            must be CLOCKWISE or COUNTER_CLOCKWISE
	 */
	public RadialWipeTransition2D(int type) {
		if (!(type == CLOCKWISE || type == COUNTER_CLOCKWISE)) {
			throw new IllegalArgumentException(
					"Type must be CLOCKWISE or COUNTER_CLOCKWISE.");
		}
		this.type = type;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {

		int multiplier2 = -1;
		if (type == COUNTER_CLOCKWISE)
			multiplier2 = 1;
		// for a good time, don't make multiplier1 = 0
		int multiplier1 = 0; // multiplier2;
		int k = Math.max(size.width, size.height);
		Area area = new Area(new Arc2D.Double(new Rectangle2D.Double(size.width
				/ 2 - 2 * k, size.height / 2 - 2 * k, k * 4, k * 4), 90
				+ multiplier1 * progress * 360, multiplier2 * progress * 360,
				Arc2D.PIE));
		area.intersect(new Area(new Rectangle(0, 0, size.width, size.height)));

		return new ImageInstruction[] { new ImageInstruction(true),
				new ImageInstruction(false, null, area) };
	}

	@Override
	public String toString() {
		if (type == CLOCKWISE) {
			return "Radial Wipe Clockwise";
		}
		return "Radial Wipe Counterclockwise";
	}

}