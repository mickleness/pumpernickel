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

import java.awt.geom.Point2D;

/**
 * In this 3D-ish transition the 2 frames slide forward, as if the viewer is at
 * the end of a conveyer belt. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of ZoomTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/ZoomTransition2D/ZoomLeft.gif"
 * alt="Zoom Left">
 * <p>
 * Zoom Left</td>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/ZoomTransition2D/ZoomRight.gif"
 * alt="Zoom Right">
 * <p>
 * Zoom Right</td>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/ZoomTransition2D/ZoomLeftStationary.gif"
 * alt="Zoom Left Stationary">
 * <p>
 * Zoom Left Stationary</td>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/ZoomTransition2D/ZoomRightStationary.gif"
 * alt="Zoom Right Stationary">
 * <p>
 * Zoom Right Stationary</td>
 * </tr>
 * </table>
 */
public class ZoomTransition2D extends AbstractPlanarTransition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new ZoomTransition2D(LEFT, false),
				new ZoomTransition2D(LEFT, true),
				new ZoomTransition2D(RIGHT, false),
				new ZoomTransition2D(RIGHT, true) };
	}

	int multiplier;
	boolean stationary;

	/** Creates a zoom transition that moves to the right */
	public ZoomTransition2D() {
		this(RIGHT);
	}

	/**
	 * Creates a zoom transition.
	 * 
	 * @param direction
	 *            move be RIGHT or LEFT.
	 */
	public ZoomTransition2D(int direction) {
		this(direction, true);
	}

	/**
	 * Creates a zoom transition.
	 * 
	 * @param direction
	 *            move be RIGHT or MOVE_LEFT.
	 * @param stationary
	 *            if true the background image stays stationary
	 */
	public ZoomTransition2D(int direction, boolean stationary) {
		this.stationary = stationary;
		if (direction == RIGHT) {
			multiplier = 1;
		} else if (direction == LEFT) {
			multiplier = -1;
		} else {
			throw new IllegalArgumentException(
					"The direction must be LEFT or RIGHT");
		}
	}

	@Override
	public float getFrameBOpacity(float p) {
		if (stationary)
			return 1;
		return .5f + .5f * p;
	}

	@Override
	public float getFrameAOpacity(float p) {
		return 1;
	}

	@Override
	public Point2D getFrameBLocation(float p) {
		if (stationary)
			return new Point2D.Double(.5, .9999999);
		double y = p;
		double x = .5 - multiplier * (1 - p);
		return new Point2D.Double(x, y);
	}

	@Override
	public Point2D getFrameALocation(float p) {
		double y = p + 1;
		double x = .5 + multiplier * (p);
		return new Point2D.Double(x, y);
	}

	@Override
	public String toString() {
		if (!stationary) {
			if (multiplier == 1) {
				return "Zoom Right";
			} else {
				return "Zoom Left";
			}
		}
		if (multiplier == 1) {
			return "Zoom Right Stationary";
		} else {
			return "Zoom Left Stationary";
		}
	}
}