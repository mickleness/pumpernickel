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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * In this transition an image splits apart into several tiny circles that
 * shrink in size. Or, seen backwards, several incoming tiny circles swirl
 * together and merge into the new image.
 * <p>
 * Here are playback samples:
 * <p>
 * <table summary="Sample Animations of HalftoneTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/HalftoneTransition2D/HalftoneIn.gif"
 * alt="Halftone In">
 * <p>
 * Halftone In</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/HalftoneTransition2D/HalftoneOut.gif"
 * alt="Halftone Out">
 * <p>
 * Halftone Out</td>
 * </tr>
 * </table>
 *
 */
public class HalftoneTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new HalftoneTransition2D(IN),
				new HalftoneTransition2D(OUT) };
	}

	int type = OUT;

	/** Creates a new halftone transition that moves out */
	public HalftoneTransition2D() {
	}

	/**
	 * Creates a new halftone transition.
	 * 
	 * @param type
	 *            must be OUT or IN.
	 */
	public HalftoneTransition2D(int type) {
		if (type == IN || type == OUT) {
			this.type = type;
		} else {
			throw new IllegalArgumentException("The type must be IN or OUT");
		}
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		if (type == OUT) {
			progress = 1 - progress;
		}
		progress = (float) Math.pow(progress, .5);

		float ySize = (size.height) * .05f;
		float xSize = (size.width) * .05f;
		List<RectangularShape> v = new ArrayList<RectangularShape>();
		float angleProgress = (float) Math.pow(1 - Math.min(progress, 1), .5);
		// pop it over 1:
		float progressZ = 1.3f * progress;
		double w = xSize * progressZ;
		double h = ySize * progressZ;
		float min = (float) (Math.min(w, h));
		for (float y = 0; y < size.height; y += ySize) {
			for (float x = 0; x < size.width; x += xSize) {
				v.add(new RoundRectangle2D.Double(x + xSize / 2 - w / 2, y
						+ ySize / 2 - h / 2, w * progress + (1 - progress)
						* min, h * progress + (1 - progress) * min, w
						* angleProgress * progress + (1 - progress) * min
						* angleProgress, h * angleProgress * progress
						+ (1 - progress) * min * angleProgress));
			}
		}
		ImageInstruction[] instr = new ImageInstruction[v.size() + 1];
		instr[0] = new ImageInstruction(false);
		for (int a = 0; a < v.size(); a++) {
			float progress2 = progress; // (float)Math.pow(progress,
										// .9+.2*random.nextFloat());
			RectangularShape r = v.get(a);
			Point2D p1 = new Point2D.Double(r.getCenterX(), r.getCenterY());
			Point2D p2 = new Point2D.Double(r.getCenterX(), r.getCenterY());
			AffineTransform transform = new AffineTransform();
			transform.translate(r.getCenterX(), r.getCenterY());
			transform.scale(30 * (1 - progress) + 1, 30 * (1 - progress) + 1);
			transform.translate(-r.getCenterX(), -r.getCenterY());

			transform.rotate(.3 * (1 - progress2), size.width / 3,
					size.height / 2);

			transform.transform(p1, p2);
			transform.setToTranslation(p1.getX() - p2.getX(),
					p1.getY() - p2.getY());
			Shape shape = transform.createTransformedShape(r);
			instr[a + 1] = new ImageInstruction(true, transform, shape);
		}
		if (type == IN) {
			for (int a = 0; a < instr.length; a++) {
				instr[a].isFirstFrame = !instr[a].isFirstFrame;
			}
		}

		return instr;
	}

	@Override
	public String toString() {
		if (type == IN) {
			return "Halftone In";
		} else {
			return "Halftone Out";
		}
	}

}