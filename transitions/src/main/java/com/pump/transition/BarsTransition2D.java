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
import java.util.Random;

/**
 * This is a series of random bars that increase in frequency, slowly revealing
 * the new frame. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of BarsTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BarsTransition2D/BarsHorizontalRandom.gif"
 * alt="Bars Horizontal Random">
 * <p>
 * Bars Horizontal Random</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BarsTransition2D/BarsHorizontal.gif"
 * alt="Bars Horizontal">
 * <p>
 * Bars Horizontal</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BarsTransition2D/BarsVerticalRandom.gif"
 * alt="Bars Vertical Random">
 * <p>
 * Bars Vertical Random</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/BarsTransition2D/BarsVertical.gif"
 * alt="Bars Vertical">
 * <p>
 * Bars Vertical</td>
 * </tr>
 * </table>
 *
 */
public class BarsTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new BarsTransition2D(Transition.HORIZONTAL, true),
				new BarsTransition2D(Transition.HORIZONTAL, false),
				new BarsTransition2D(Transition.VERTICAL, true),
				new BarsTransition2D(Transition.VERTICAL, false) };
	}

	/**
	 * Keep a truly random seed; constantly creating a new Random object with
	 * the current time as its seed created several non-random frames generated
	 * in the same millisecond
	 * 
	 */
	static Random random = new Random(System.currentTimeMillis());

	int type;
	boolean isRandom;

	/**
	 * Creates a randomized horizontal BarsTransition2D
	 * 
	 */
	public BarsTransition2D() {
		this(Transition.HORIZONTAL, true);
	}

	/**
	 * Creates a BarsTransition2D.
	 * 
	 * @param type
	 *            must be HORIZONTAL or VERTICAL
	 * @param random
	 *            whether each frame is 100% random, or whether the bars are
	 *            cumulative as the transition progresses.
	 */
	public BarsTransition2D(int type, boolean random) {
		if (!(type == Transition.HORIZONTAL || type == Transition.VERTICAL)) {
			throw new IllegalArgumentException(
					"Type must be HORIZONTAL or VERTICAL.");
		}
		this.type = type;
		this.isRandom = random;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		boolean[] k;
		if (type == Transition.HORIZONTAL) {
			k = new boolean[size.height];
		} else {
			k = new boolean[size.width];
		}
		Random r;
		if (isRandom) {
			r = random;
		} else {
			r = new Random(0);
		}
		for (int a = 0; a < k.length; a++) {
			k[a] = r.nextFloat() > progress;
		}
		List<Transition2DInstruction> v = new ArrayList<Transition2DInstruction>();
		v.add(new ImageInstruction(false));
		if (type == Transition.HORIZONTAL) {
			int a = 0;
			while (a < k.length) {
				int run = 0;
				while (a + run < k.length && k[a + run]) {
					run++;
				}
				if (run != 0) {
					Rectangle2D r2 = new Rectangle2D.Float(0, a, size.width,
							run);
					v.add(new ImageInstruction(true, null, r2));
					a += run;
				}
				a++;
			}
		} else {
			int a = 0;
			while (a < k.length) {
				int run = 0;
				while (a + run < k.length && k[a + run]) {
					run++;
				}
				if (run != 0) {
					Rectangle2D r2 = new Rectangle2D.Float(a, 0, run,
							size.height);
					v.add(new ImageInstruction(true, null, r2));
					a += run;
				}
				a++;
			}
		}
		return v.toArray(new Transition2DInstruction[v.size()]);
	}

	@Override
	public String toString() {
		if (type == Transition.HORIZONTAL) {
			if (isRandom) {
				return "Bars Horizontal Random";
			}
			return "Bars Horizontal";
		}
		if (isRandom) {
			return "Bars Vertical Random";
		}
		return "Bars Vertical";
	}
}