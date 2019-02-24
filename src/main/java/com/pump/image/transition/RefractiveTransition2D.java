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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This resembles a lens with several faces, showing several fractured copies of
 * the same image. As the transition progresses the incoming image becomes more
 * focused and opaque. Here is a playback sample:
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/RefractiveTransition2D/Refractive.gif"
 * alt="RefractiveTransition2D Demo">
 *
 */
public class RefractiveTransition2D extends Transition2D {

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		List<Rectangle2D> v1 = new ArrayList<Rectangle2D>();
		List<Transition2DInstruction> v2 = new ArrayList<Transition2DInstruction>();

		float factor = .05f;

		float ySize = (size.height) * factor;
		float xSize = (size.width) * factor;
		for (float y = 0; y < size.height; y += ySize) {
			for (float x = 0; x < size.width; x += xSize) {
				v1.add(new Rectangle2D.Float(x, y, xSize, ySize));
			}
		}

		Point2D p1, p2;
		// 1 -> 0, 0 -> PI,
		float angleProgress = (float) ((1 - Math.pow(progress, .2)));
		v2.add(new ImageInstruction(true));
		for (int a = 0; a < v1.size(); a++) {
			try {
				Rectangle2D r = v1.get(a);
				p1 = new Point2D.Double(r.getCenterX(), r.getCenterY());
				AffineTransform transform = new AffineTransform();

				transform.setToRotation(-2 * Math.PI * angleProgress,
						size.width / 2, size.height / 2);
				transform.translate(size.width / 2, size.height / 2);
				transform.scale(progress, progress);
				transform.translate(-size.width / 2, -size.height / 2);

				p2 = new Point2D.Double();
				transform.transform(p1, p2);
				transform.setToTranslation(p2.getX() - p1.getX(), p2.getY()
						- p1.getY());
				v2.add(new ImageInstruction(false, (float) (Math.pow(progress,
						.4)), transform.createInverse(), r));

				transform.setToRotation(2 * Math.PI * angleProgress,
						size.width / 2, size.height / 2);

				p2 = new Point2D.Double();
				transform.transform(p1, p2);
				transform.setToTranslation(p2.getX() - p1.getX(), p2.getY()
						- p1.getY());
				v2.add(new ImageInstruction(false, progress * progress,
						transform.createInverse(), r));
			} catch (Exception e) {

			}
		}
		return v2.toArray(new Transition2DInstruction[v2.size()]);
	}

	@Override
	public String toString() {
		return "Refractive";
	}

}