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

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * This transition resembles two still images on a turntable. The table spins
 * clockwise or counter-clockwise, and the foremost image rotates to the
 * background and the new image rotates forward. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of SwivelTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/SwivelTransition2D/SwivelCounterclockwise.gif"
 * alt="Swivel Counterclockwise">
 * <p>
 * Swivel Counterclockwise</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/SwivelTransition2D/SwivelClockwise.gif"
 * alt="Swivel Clockwise">
 * <p>
 * Swivel Clockwise</td>
 * </tr>
 * </table>
 *
 */
public class SwivelTransition2D extends AbstractPlanarTransition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new SwivelTransition2D(COUNTER_CLOCKWISE),
				new SwivelTransition2D(CLOCKWISE) };
	}

	int multiplier;

	/**
	 * Creates a new swivel transition that moves clockwise.
	 * 
	 */
	public SwivelTransition2D() {
		this(CLOCKWISE);
	}

	/**
	 * Creates a new swivel transition against a black background.
	 * 
	 * @param direction
	 *            must be CLOCKWISE or MOVE_COUNTERCLOCKWISE.
	 */
	public SwivelTransition2D(int direction) {
		this(Color.black, direction);
	}

	/**
	 * Creates a new swivel transition.
	 * 
	 * @param direction
	 *            must be CLOCKWISE or MOVE_COUNTERCLOCKWISE.
	 */
	public SwivelTransition2D(Color background, int direction) {
		super(background);
		if (direction == CLOCKWISE) {
			multiplier = 1;
		} else if (direction == COUNTER_CLOCKWISE) {
			multiplier = -1;
		} else {
			throw new IllegalArgumentException(
					"The direction must be CLOCKWISE or COUNTER_CLOCKWISE");
		}
	}

	@Override
	public String toString() {
		if (multiplier == -1) {
			return "Swivel Counterclockwise";
		}
		return "Swivel Clockwise";
	}

	@Override
	public float getFrameAOpacity(float p) {
		if (p < .5f) {
			return 1f;
		}
		p = 1 - (p - .5f) / .5f;
		p = (float) Math.sqrt(p);
		return p;
	}

	@Override
	public float getFrameBOpacity(float p) {
		if (p > .5f)
			return 1f;
		p = p / .5f;
		p = (float) Math.pow(p, .5);
		return p;
	}

	@Override
	public Point2D getFrameALocation(float p) {
		p = multiplier * p;
		return new Point2D.Double(
				.5 * Math.cos(Math.PI * p + Math.PI / 2) + .5,
				.5 * Math.sin(Math.PI * p + Math.PI / 2) + .5);
	}

	@Override
	public Point2D getFrameBLocation(float p) {
		p = multiplier * p;
		return new Point2D.Double(
				.5 * Math.cos(Math.PI * p + 3 * Math.PI / 2) + .5,
				.5 * Math.sin(Math.PI * p + 3 * Math.PI / 2) + .5);
	}
}