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
package com.pump.image.transition;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;

/**
 * This pivots a frame in/out from a specific corner, as if there is a hinge
 * involved. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of PivotTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotInTopLeft.gif"
 * alt="Pivot In Top Left">
 * <p>
 * Pivot In Top Left</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotInTopRight.gif"
 * alt="Pivot In Top Right">
 * <p>
 * Pivot In Top Right</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotInBottomLeft.gif"
 * alt="Pivot In Bottom Left">
 * <p>
 * Pivot In Bottom Left</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotInBottomRight.gif"
 * alt="Pivot In Bottom Right">
 * <p>
 * Pivot In Bottom Right</td>
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotOutTopLeft.gif"
 * alt="Pivot Out Top Left">
 * <p>
 * Pivot Out Top Left</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotOutTopRight.gif"
 * alt="Pivot Out Top Right">
 * <p>
 * Pivot Out Top Right</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotOutBottomLeft.gif"
 * alt="Pivot Out Bottom Left">
 * <p>
 * Pivot Out Bottom Left</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/PivotTransition2D/PivotOutBottomRight.gif"
 * alt="Pivot Out Top Right">
 * <p>
 * Pivot Out Bottom Right</td>
 * </tr>
 * </table>
 */
public class PivotTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new PivotTransition2D(TOP_LEFT, false),
				new PivotTransition2D(TOP_LEFT, true),
				new PivotTransition2D(TOP_RIGHT, false),
				new PivotTransition2D(TOP_RIGHT, true),
				new PivotTransition2D(BOTTOM_LEFT, false),
				new PivotTransition2D(BOTTOM_LEFT, true),
				new PivotTransition2D(BOTTOM_RIGHT, false),
				new PivotTransition2D(BOTTOM_RIGHT, true) };
	}

	boolean in;
	int type;

	/**
	 * Creates a new PivotTransition2D that pivots in from the top-left corner.
	 * 
	 */
	public PivotTransition2D() {
		this(TOP_LEFT, true);
	}

	/**
	 * Creates a new PivotTransition2D.
	 * 
	 * @param type
	 *            must be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT or BOTTOM_RIGHT
	 * @param in
	 *            whether the incoming frame is pivoting in on top of the old
	 *            frame, or whether the old frame is pivoting out revealing the
	 *            new frame.
	 */
	public PivotTransition2D(int type, boolean in) {
		if (!(type == TOP_LEFT || type == TOP_RIGHT || type == BOTTOM_LEFT || type == BOTTOM_RIGHT))
			throw new IllegalArgumentException(
					"Type must be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT or BOTTOM_RIGHT");
		this.type = type;
		this.in = in;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		AffineTransform transform;
		if (in) {
			if (type == TOP_LEFT) {
				transform = AffineTransform.getRotateInstance(
						(float) (-(1 - progress) * Math.PI / 2f), 0, 0);
			} else if (type == TOP_RIGHT) {
				transform = AffineTransform.getRotateInstance(
						(float) ((1 - progress) * Math.PI / 2f), size.width, 0);
			} else if (type == BOTTOM_LEFT) {
				transform = AffineTransform
						.getRotateInstance(
								(float) ((1 - progress) * Math.PI / 2f), 0,
								size.height);
			} else {
				transform = AffineTransform.getRotateInstance(
						(float) ((1 - progress) * Math.PI / 2f), size.width,
						size.height);
			}
			return new Transition2DInstruction[] { new ImageInstruction(true),
					new ImageInstruction(false, transform, null) };
		}

		// pivot out:
		if (type == TOP_LEFT) {
			transform = AffineTransform.getRotateInstance((float) ((progress)
					* Math.PI / 2f), 0, 0);
		} else if (type == TOP_RIGHT) {
			transform = AffineTransform.getRotateInstance((float) (-(progress)
					* Math.PI / 2f), size.width, 0);
		} else if (type == BOTTOM_LEFT) {
			transform = AffineTransform.getRotateInstance((float) (-(progress)
					* Math.PI / 2f), 0, size.height);
		} else {
			transform = AffineTransform.getRotateInstance((float) (-(progress)
					* Math.PI / 2f), size.width, size.height);
		}
		return new Transition2DInstruction[] { new ImageInstruction(false),
				new ImageInstruction(true, transform, null) };
	}

	@Override
	public String toString() {
		String s;
		if (in) {
			s = "Pivot In ";
		} else {
			s = "Pivot Out ";
		}
		if (type == TOP_LEFT) {
			return s + "Top Left";
		} else if (type == TOP_RIGHT) {
			return s + "Top Right";
		} else if (type == BOTTOM_LEFT) {
			return s + "Bottom Left";
		} else {
			return s + "Bottom Right";
		}
	}

}