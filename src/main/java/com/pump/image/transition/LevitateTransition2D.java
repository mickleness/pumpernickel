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

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This breaks the existing image into 6 horizontal strips, and then (after a
 * gently wobbly start) that levitate/accelerate up and out of the frame,
 * revealing the next image. Here is a playback sample:
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/LevitateTransition2D/Levitate.gif"
 * alt="LevitateTransition2D Demo">
 * 
 */
public class LevitateTransition2D extends Transition2D {

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		progress = (float) Math.pow(progress, 2);
		float stripHeight = size.height / 6f;

		List<Rectangle2D> v = new ArrayList<Rectangle2D>();
		for (int y = 0; y < size.height; y += stripHeight) {
			v.add(new Rectangle2D.Float(0, y, size.width, stripHeight));
		}
		Transition2DInstruction[] instr = new Transition2DInstruction[v.size() + 1];
		instr[0] = new ImageInstruction(false);
		for (int a = 0; a < v.size(); a++) {
			Rectangle2D r = v.get(a);
			AffineTransform transform = new AffineTransform();
			float angleProgress = (float) Math.pow(progress, .6);
			float xProgress = 1.0f / (1.0f + progress);
			float k = (angleProgress) * (a) / (v.size());
			float theta = (float) (Math.PI * k / 2 + (progress) * Math.PI / 2);
			if (theta > Math.PI / 2)
				theta = (float) (Math.PI / 2);
			float k2;
			theta = .2f + (float) (.2 * Math.cos(-3 * theta)); // theta*(.5f-progress);
			k2 = (1 - progress);
			theta = theta * progress;
			if (a % 2 == 0) {
				transform.rotate(theta, -size.width
						* (1 - xProgress * xProgress * xProgress) / 2,
						size.height * k2);
			} else {
				transform.rotate(-theta, size.width
						+ (1 - xProgress * xProgress * xProgress) * size.width
						/ 2, size.height * k2);
			}
			transform.translate(0, -progress * progress * 1.5f * size.height);

			instr[a + 1] = new ImageInstruction(true, transform,
					transform.createTransformedShape(r));
		}
		return instr;
	}

	@Override
	public String toString() {
		return "Levitate";
	}

}