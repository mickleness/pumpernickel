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
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

/**
 * This creates a pattern of growing diamonds. (The new frame is clipped to
 * these diamonds.) Here are playback samples:
 * <p>
 * <table summary="Sample Animations of DiamondsTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/DiamondsTransition2D/Diamonds(10).gif"
 * alt="Diamonds (10)">
 * <p>
 * Diamonds (10)</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/DiamondsTransition2D/Diamonds(20).gif"
 * alt="Diamonds (20)">
 * <p>
 * Diamonds (20)</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/DiamondsTransition2D/Diamonds(40).gif"
 * alt="Diamonds (40)">
 * <p>
 * Diamonds (40)</td>
 * </tr>
 * </table>
 */
public class DiamondsTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new DiamondsTransition2D(10),
				new DiamondsTransition2D(20), new DiamondsTransition2D(40) };
	}

	int diamondSize;

	/**
	 * Creates a new DiamondsTransition2D with a diamond size of 50.
	 * 
	 */
	public DiamondsTransition2D() {
		this(50);
	}

	/**
	 * Creates a new DiamondsTransition2D.
	 * 
	 * @param diamondSize
	 *            the width of the diamonds. It is not recommended that this
	 *            value is less than 40, as that can really hurt performance in
	 *            some situations.
	 */
	public DiamondsTransition2D(int diamondSize) {
		if (diamondSize <= 0)
			throw new IllegalArgumentException("size (" + diamondSize
					+ ") must be greater than 4");
		this.diamondSize = diamondSize;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {

		GeneralPath clipping = new GeneralPath(Path2D.WIND_NON_ZERO);

		float dx = size.width / 2f;
		float dy = size.height / 2f;
		while (dx > 0 + diamondSize)
			dx -= diamondSize;
		while (dy > 0 + diamondSize)
			dy -= diamondSize;

		int ctr = 0;
		progress = progress / 2f;
		for (float y = -dy; y < size.height + diamondSize; y += diamondSize / 2) {
			float z = 0;
			if (ctr % 2 == 0)
				z = diamondSize / 2f;

			for (float x = -dx; x < size.width + diamondSize; x += diamondSize) {
				clipping.moveTo(x + z, y - diamondSize * progress);
				clipping.lineTo(x + diamondSize * progress + z, y);
				clipping.lineTo(x + z, y + diamondSize * progress);
				clipping.lineTo(x - diamondSize * progress + z, y);
				clipping.lineTo(x + z, y - diamondSize * progress);
				clipping.closePath();
			}
			ctr++;
		}

		return new Transition2DInstruction[] { new ImageInstruction(true),
				new ImageInstruction(false, null, clipping) };
	}

	@Override
	public String toString() {
		return "Diamonds (" + diamondSize + ")";
	}

}