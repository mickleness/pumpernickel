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
import java.awt.geom.Rectangle2D;

/**
 * This clips to the shape of a square zooming in/out. Here are playback
 * samples:
 * <p>
 * <table summary="Sample Animations of BoxTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/BoxTransition2D/BoxIn.gif"
 * alt="Box In">
 * <p>
 * Box In</td>
 * <td align="center">
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/BoxTransition2D/BoxOut.gif"
 * alt="Box Out">
 * <p>
 * Box Out</td>
 * </tr>
 * </table>
 */
public class BoxTransition2D extends AbstractShapeTransition2D {

	/**
	 * TODO: remove all getDemoTransitions() methods or create a new tool to invoke them.
	 * 
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new BoxTransition2D(IN),
				new BoxTransition2D(OUT) };
	}

	/**
	 * Creates a new BoxTransition2D that zooms out
	 */
	public BoxTransition2D() {
	}

	/**
	 * Creates a new BoxTransition2D
	 * 
	 * @param type
	 *            must be IN or OUT
	 */
	public BoxTransition2D(int type) {
		super(type);
	}

	@Override
	public Shape getShape() {
		return new Rectangle2D.Float(0, 0, 100, 100);
	}

	@Override
	public String getShapeName() {
		return "Box";
	}

}