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
package com.pump.awt;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import com.pump.geom.GeneralPathWriter;
import com.pump.geom.PathWriter;
import com.pump.geom.ShapeBounds;

/**
 * This scribbles an existing path to create a casual, hand-drawn (scribbled)
 * look.
 */
public class Scribbler {

	/**
	 * 
	 * @param tol
	 *            a float from [0,1]
	 * @param movement
	 *            a float from [0,1]
	 */
	public static GeneralPath create(Shape s, float tol, float movement) {
		return create(s, tol, movement, 40);
	}

	/**
	 * 
	 * @param tol
	 *            a float from [0,1]
	 * @param movement
	 *            a float from [0,1]
	 */
	public static GeneralPath create(Shape s, float tol, float movement,
			long randomSeed) {
		GeneralPath dest = new GeneralPath();
		GeneralPathWriter writer = new GeneralPathWriter(dest);
		create(s, tol, movement, randomSeed, writer);
		return dest;
	}

	/**
	 * 
	 * @param tol
	 *            a float from [0,1]
	 * @param movement
	 *            a float from [0,1]
	 */
	public static void create(Shape s, float tol, float movement,
			long randomSeed, PathWriter writer) {
		Random random = new Random(randomSeed);
		PathIterator i = s.getPathIterator(null, .2);
		float[] f = new float[6];
		float x = 0;
		float y = 0;
		float dx = 0;
		float dy = 0;
		int segments;
		float startX;
		float startY;
		float progress;
		float newX, newY;
		while (i.isDone() == false) {
			int k = i.currentSegment(f);
			if (k == PathIterator.SEG_MOVETO) {
				x = f[0];
				y = f[1];
				writer.moveTo(x, y);
			} else if (k == PathIterator.SEG_LINETO) {
				random.setSeed(randomSeed);
				startX = x;
				startY = y;
				segments = (int) (Math.sqrt((f[0] - x) * (f[0] - x)
						+ (f[1] - y) * (f[1] - y)) / 10 + .5);
				if (segments <= 0)
					segments = 1;

				for (k = 0; k < segments; k++) {
					dx += movement / 2 - movement * random.nextFloat();
					if (dx > tol)
						dx = tol;
					if (dx < -tol)
						dx = -tol;
					dy += movement / 2 - movement * random.nextFloat();
					if (dy > tol)
						dy = tol;
					if (dy < -tol)
						dy = -tol;
					progress = ((float) k + 1) / (segments);
					newX = f[0] * progress + startX * (1 - progress) + dx;
					newY = f[1] * progress + startY * (1 - progress) + dy;
					writer.lineTo(newX, newY);
					x = newX;
					y = newY;
				}
				x = f[0];
				y = f[1];
			} else if (k == PathIterator.SEG_CLOSE) {
				writer.closePath();
			}
			i.next();
		}
	}

	/**
	 * 
	 * @param body
	 *            the shape to fill
	 * @param strokeWidth
	 *            the width of the stroke.
	 * @param angle
	 *            the angle (in radians) to scribble
	 * @param density
	 *            a float from [0,1]
	 * @return a shape that resembles a crayon scribbling the body
	 */
	public static Shape fillBody(Shape body, float strokeWidth, double angle,
			double density) {
		density = 1 - density;
		Rectangle2D bounds = ShapeBounds.getBounds(body);
		double cx = bounds.getCenterX();
		double cy = bounds.getCenterY();

		double k = strokeWidth;

		GeneralPath copy = new GeneralPath();
		copy.append(body, false);
		AffineTransform tx = AffineTransform.getRotateInstance(-angle, cx, cy);
		copy.transform(tx);

		GeneralPath result = new GeneralPath();

		Random r = new Random(0);
		boolean moved = false;
		Rectangle2D rotatedBounds = ShapeBounds.getBounds(copy);
		for (int y = (int) rotatedBounds.getY(); y < rotatedBounds.getMaxY(); y += k
				* (1 + density) / 2) {
			for (int x = (int) rotatedBounds.getX(); x < rotatedBounds
					.getMaxX(); x += 3) {
				if (copy.contains(x - k / 2, y - k / 2, k, k)) {
					if (!moved) {
						result.moveTo(x + 2 - 4 * r.nextDouble(),
								y + 2 - 4 * r.nextDouble());
						moved = true;
					} else {
						result.lineTo(x + 2 - 4 * r.nextDouble(),
								y + 2 - 4 * r.nextDouble());
					}
					int x2 = x;
					readRemainingLine: while (x2 < rotatedBounds.getMaxX()) {
						if (!copy.contains(x2 - k / 2, y - k / 2, k, k)) {
							break readRemainingLine;
						}
						x2 += 2;
					}
					x2 -= 2;
					result.lineTo(x2 + 2 - 4 * r.nextDouble(),
							y + 2 - 4 * r.nextDouble());
					x = x2;
				}
			}
		}

		tx = AffineTransform.getRotateInstance(angle, cx, cy);
		result.transform(tx);

		return new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND).createStrokedShape(result);
	}
}