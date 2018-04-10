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
 * The concept here was to resemble a deck of cards being shuffled. Here is a
 * playback sample:
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/transition/MeshShuffleTransition2D/MeshShuffle.gif"
 * alt="MeshShuffleTransition2D Demo">
 *
 */
public class MeshShuffleTransition2D extends Transition2D {

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		progress = (float) Math.pow(progress, .45);
		float stripHeight = size.height * 10 / 200;

		List<Rectangle2D> v = new ArrayList<Rectangle2D>();
		for (int y = size.height; y > -stripHeight; y -= stripHeight) {
			v.add(new Rectangle2D.Float(0, y, size.width, stripHeight));
		}
		Transition2DInstruction[] instr = new Transition2DInstruction[v.size()];
		instr[0] = new ImageInstruction(true);
		for (int a = 1; a < v.size(); a++) {
			Rectangle2D r = v.get(a);
			AffineTransform transform = new AffineTransform();
			float k = (1 - progress) * (a) / (v.size());
			float theta = (float) (Math.PI * k / 2 + (1 - progress) * Math.PI
					/ 2);
			if (theta > Math.PI / 2)
				theta = (float) (Math.PI / 2);
			if (a % 2 == 0) {
				transform.rotate(-theta, -size.width * (1 - progress) / 2,
						size.height * progress);
			} else {
				transform.rotate(theta, size.width + (1 - progress)
						* size.width / 2, size.height * progress);
			}
			instr[a] = new ImageInstruction(false, transform,
					transform.createTransformedShape(r));
		}
		return instr;
	}

	@Override
	public String toString() {
		return "Mesh Shuffle";
	}

}