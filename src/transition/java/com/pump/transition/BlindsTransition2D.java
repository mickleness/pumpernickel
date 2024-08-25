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
import java.util.ArrayList;
import java.util.List;

/**
 * Also known as "Venetian Blinds", this creates several horizontal/vertical
 * strips that grow in width/height respectively to reveal the new frame. Here
 * are playback samples:
 * <p>
 * <table summary="Sample Animations of BlindsTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BlindsTransition2D/BlindsLeft(4).gif"
 * alt="Blinds Left (4)">
 * <p>
 * Blinds Left (4)</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BlindsTransition2D/BlindsLeft(10).gif"
 * alt="Blinds Left (10)">
 * <p>
 * Blinds Left (10)</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BlindsTransition2D/BlindsLeft(20).gif"
 * alt="Blinds Left (20)">
 * <p>
 * Blinds Left (20)</td>
 * </tr>
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BlindsTransition2D/BlindsRight(10).gif"
 * alt="Blinds Right (10)">
 * <p>
 * Blinds Right (10)</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BlindsTransition2D/BlindsUp(10).gif"
 * alt="Blinds Up (10)">
 * <p>
 * Blinds Up (10)</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BlindsTransition2D/BlindsDown(10).gif"
 * alt="Blinds Down (10)">
 * <p>
 * Blinds Down (10)</td>
 * </tr>
 * </table>
 *
 */
public class BlindsTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new BlindsTransition2D(LEFT, 4),
				new BlindsTransition2D(LEFT, 10),
				new BlindsTransition2D(LEFT, 20),
				new BlindsTransition2D(RIGHT), new BlindsTransition2D(UP),
				new BlindsTransition2D(DOWN) };
	}

	int type;
	int blinds;

	/**
	 * Creates a BlindsTransition2D that moves to the right with 10 blinds.
	 * 
	 */
	public BlindsTransition2D() {
		this(RIGHT);
	}

	/**
	 * Creates a new BlindsTransition2D with 10 blinds.
	 * 
	 * @param type
	 *            must be LEFT, RIGHT, UP or DOWN.
	 */
	public BlindsTransition2D(int type) {
		this(type, 10);
	}

	/**
	 * Creates a BlindsTransition2D.
	 * 
	 * @param type
	 *            must be LEFT, RIGHT, UP or DOWN
	 * @param numberOfBlinds
	 *            the number of blinds. Must be 4 or greater.
	 */
	public BlindsTransition2D(int type, int numberOfBlinds) {
		if (!(type == LEFT || type == RIGHT || type == UP || type == DOWN)) {
			throw new IllegalArgumentException(
					"The type must be LEFT, RIGHT, UP or DOWN");
		}
		if (numberOfBlinds < 4)
			throw new IllegalArgumentException("The number of blinds ("
					+ numberOfBlinds + ") must be greater than 3.");
		this.type = type;
		blinds = numberOfBlinds;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		List<Transition2DInstruction> v = new ArrayList<Transition2DInstruction>();
		v.add(new ImageInstruction(type == RIGHT || type == DOWN));
		float k;
		if (type == LEFT || type == RIGHT) {
			k = ((float) size.width) / ((float) blinds);
		} else {
			k = ((float) size.height) / ((float) blinds);
		}
		for (int a = 0; a < blinds; a++) {
			Rectangle2D r;
			if (type == DOWN) {
				r = new Rectangle2D.Float(0, a * k, size.width, progress * k);
			} else if (type == UP) {
				r = new Rectangle2D.Float(0, a * k, size.width, k - progress
						* k);
			} else if (type == RIGHT) {
				r = new Rectangle2D.Float(a * k, 0, progress * k, size.height);
			} else {
				r = new Rectangle2D.Float(a * k, 0, k - progress * k,
						size.height);
			}
			v.add(new ImageInstruction(type == UP || type == LEFT, null, r));
		}
		return v.toArray(new Transition2DInstruction[v.size()]);
	}

	@Override
	public String toString() {
		if (type == LEFT) {
			return "Blinds Left (" + blinds + ")";
		} else if (type == RIGHT) {
			return "Blinds Right (" + blinds + ")";
		} else if (type == UP) {
			return "Blinds Up (" + blinds + ")";
		} else {
			return "Blinds Down (" + blinds + ")";
		}
	}
}