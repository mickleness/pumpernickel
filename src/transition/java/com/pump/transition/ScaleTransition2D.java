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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.pump.geom.RectangularTransform;

/**
 * This zooms one frame in/out from the center. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of ScaleTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/ScaleTransition2D/ScaleIn.gif"
 * alt="Scale In">
 * <p>
 * Scale In</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/ScaleTransition2D/ScaleOut.gif"
 * alt="Scale Out">
 * <p>
 * Scale Out</td>
 * </tr>
 * </table>
 *
 */
public class ScaleTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new ScaleTransition2D(IN),
				new ScaleTransition2D(OUT) };
	}

	int type;

	/** Creates a new ScaleTransition2D that scales out */
	public ScaleTransition2D() {
		this(OUT);
	}

	/**
	 * Creates a new ScaleTransition2D
	 * 
	 * @param type
	 *            must be IN or OUT
	 */
	public ScaleTransition2D(int type) {
		if (!(type == IN || type == OUT))
			throw new IllegalArgumentException("type must be IN or OUT");
		this.type = type;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Point2D center = new Point2D.Float(size.width / 2f, size.height / 2f);

		AffineTransform transform;
		if (type == OUT) {
			progress = 1 - progress;
		}

		float w = size.width * progress;
		float h = size.height * progress;
		transform = RectangularTransform.create(new Rectangle2D.Float(0, 0,
				size.width, size.height), new Rectangle2D.Double(center.getX()
				- w / 2, center.getY() - h / 2, w, h));

		return new ImageInstruction[] { new ImageInstruction(type == IN),
				new ImageInstruction(type != IN, transform, null) };
	}

	@Override
	public String toString() {
		if (type == IN) {
			return "Scale In";
		} else {
			return "Scale Out";
		}
	}
}