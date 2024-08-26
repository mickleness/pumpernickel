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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This paints the incoming frame in thin horizontal strips that slide into
 * place. Here is a playback sample:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/WeaveTransition2D/Weave.gif"
 * alt="WeaveTransition2D Demo">
 * 
 */
public class WeaveTransition2D extends Transition2D {

	/** Creates a new weave transition */
	public WeaveTransition2D() {
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		float stripHeight = 5;
		progress = (float) (-1.6666666666666186 * progress * progress + 2.6666666666666203 * progress);

		float progress2 = (float) Math.pow(1 - progress, 3) * .5f
				+ (1 - progress) * .5f;
		if (progress > 1)
			progress2 = (float) Math.pow(1 - progress, 2);
		float dip = -(2 * progress - 1) * (2 * progress - 1) + 1;
		List<Rectangle2D> v = new ArrayList<Rectangle2D>();
		for (int y = size.height; y > -stripHeight; y -= stripHeight) {
			v.add(new Rectangle2D.Float(0, y, size.width, stripHeight));
		}
		Transition2DInstruction[] instr = new Transition2DInstruction[v.size() + 1];
		instr[0] = new ImageInstruction(true);
		for (int a = 0; a < v.size(); a++) {
			Rectangle2D r = v.get(a);
			AffineTransform transform = new AffineTransform();
			float dx = (float) (Math.sin(.5 * Math.PI * (1 - progress)) * size.width);
			float k = (progress2) * (1000 * dip) * (a) / (v.size());
			dx = dx + k;
			if (a % 2 == 0) {
				transform.translate(dx, 0);
			} else {
				transform.translate(-dx, 0);
			}
			instr[a + 1] = new ImageInstruction(false, transform,
					transform.createTransformedShape(r));
		}
		return instr;
	}

	@Override
	public String toString() {
		return "Weave";
	}

}