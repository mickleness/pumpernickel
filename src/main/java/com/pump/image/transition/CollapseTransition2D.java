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
 * In this transition the original image is split into 6 horizontal strips, and
 * they collapse downward to reveal the next image underneath. Here is a
 * playback sample:
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/CollapseTransition2D/Collapse.gif"
 * alt="CollapseTransition2D Demo">
 *
 */
public class CollapseTransition2D extends Transition2D {

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		progress = (float) Math.pow(progress, 2);
		float stripHeight = size.height / 6;

		List<Rectangle2D> v = new ArrayList<Rectangle2D>();
		for (int y = 0; y < size.height; y += stripHeight) {
			v.add(new Rectangle2D.Float(0, y, size.width, stripHeight));
		}
		ImageInstruction[] instr = new ImageInstruction[v.size() + 1];
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
			theta = theta / (1 + progress);
			k2 = 1 * progress;
			if (a % 2 == 0) {
				transform.rotate(theta, -size.width
						* (1 - xProgress * xProgress * xProgress) / 2,
						size.height * k2);
			} else {
				transform.rotate(-theta, size.width
						+ (1 - xProgress * xProgress * xProgress) * size.width
						/ 2, size.height * k2);
			}
			transform.translate(0, progress * progress * size.height * 1.5);
			instr[a + 1] = new ImageInstruction(true, transform,
					transform.createTransformedShape(r));
		}
		return instr;
	}

	@Override
	public String toString() {
		return "Collapse";
	}

}