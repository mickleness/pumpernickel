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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * This clips to the shape of a circle zooming in/out. Here are playback
 * samples:
 * <p>
 * <table summary="Sample Animations of CircleTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/CircleTransition2D/CircleIn.gif"
 * alt="Circle In">
 * <p>
 * Circle In</td>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/CircleTransition2D/CircleOut.gif"
 * alt="Circle Out">
 * <p>
 * Circle Out</td>
 * </tr>
 * </table>
 */
public class CircleTransition2D extends AbstractShapeTransition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition2D[] getDemoTransitions() {
		return new Transition2D[] { new CircleTransition2D(IN),
				new CircleTransition2D(OUT) };
	}

	/**
	 * Creates a new CircleTransition2D that zooms out
	 * 
	 */
	public CircleTransition2D() {
		super();
	}

	/**
	 * Creates a new CircleTransition2D
	 * 
	 * @param type
	 *            must be IN or OUT
	 */
	public CircleTransition2D(int type) {
		super(type);
	}

	@Override
	public Shape getShape() {
		return new Ellipse2D.Float(0, 0, 100, 100);
	}

	@Override
	public String getShapeName() {
		return "Circle";
	}
}