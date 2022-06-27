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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * This transition flips the viewing surface over 180 degrees to reveal the
 * inverted back. Here are playback samples:
 * <p>
 * <table summary="Sample Animations of FlipTransition3D" cellspacing="50"
 * border="0">
 * <tr>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipLeft.gif"
 * alt="Flip Left">
 * <p>
 * Flip Left</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipRight.gif"
 * alt="Flip Right">
 * <p>
 * Flip Right</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipUp.gif"
 * alt="Flip Up">
 * <p>
 * Flip Up</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipDown.gif"
 * alt="Flip Down">
 * <p>
 * Flip Down</td>
 * </tr>
 * <tr>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipLeftFlush.gif"
 * alt="Flip Left Flush">
 * <p>
 * Flip Left Flush</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipRightFlush.gif"
 * alt="Flip Right Flush">
 * <p>
 * Flip Right Flush</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipUpFlush.gif"
 * alt="Flip Up Flush">
 * <p>
 * Flip Up Flush</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FlipTransition3D/FlipDownFlush.gif"
 * alt="Flip Down Flush">
 * <p>
 * Flip Down Flush</td>
 * </tr>
 * </table>
 */
public class FlipTransition3D extends Transition3D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new FlipTransition3D(UP, false),
				new FlipTransition3D(DOWN, false),
				new FlipTransition3D(LEFT, false),
				new FlipTransition3D(RIGHT, false),
				new FlipTransition3D(UP, true),
				new FlipTransition3D(DOWN, true),
				new FlipTransition3D(LEFT, true),
				new FlipTransition3D(RIGHT, true) };
	}

	int direction;
	boolean flush;
	Color background;

	/**
	 * Create a FlipTransition3D with a black background.
	 * 
	 * @param direction
	 *            one of the Transition constants: UP, DOWN, LEFT or RIGHT
	 * @param flush
	 *            whether the z-axis should remain flush with the target
	 *            destination. For example: if this is false then as a surface
	 *            flips to the right a vertical edge will be pulled towards the
	 *            camera and grow larger in height than than the target
	 *            destination. So the center of the flip is constant. But if
	 *            this is true: then the center of the flip pulls farther away
	 *            from the camera to make sure that vertical edge is never
	 *            larger than the height of the target graphics area.
	 */
	public FlipTransition3D(int direction, boolean flush) {
		this(direction, flush, Color.black);
	}

	/**
	 * Create a FlipTransition3D.
	 * 
	 * @param direction
	 *            one of the Transition constants: UP, DOWN, LEFT or RIGHT
	 * @param flush
	 *            whether the z-axis should remain flush with the target
	 *            destination. For example: if this is false then as a surface
	 *            flips to the right a vertical edge will be pulled towards the
	 *            camera and grow larger in height than than the target
	 *            destination. So the center of the flip is constant. But if
	 *            this is true: then the center of the flip pulls farther away
	 *            from the camera to make sure that vertical edge is never
	 *            larger than the height of the target graphics area.
	 * @param background
	 *            the optional background color to paint behind this transition.
	 */
	public FlipTransition3D(int direction, boolean flush, Color background) {
		if (!(direction == Transition.UP || direction == Transition.DOWN
				|| direction == Transition.LEFT
				|| direction == Transition.RIGHT)) {
			throw new IllegalArgumentException(
					"direction must be UP, DOWN, LEFT or RIGHT");
		}
		this.flush = flush;
		this.direction = direction;
		this.background = background;
	}

	@Override
	protected void doPaint(Graphics2D g, BufferedImage frameA,
			BufferedImage frameB, float progress) {
		int h = frameA.getHeight();
		int w = frameB.getWidth();
		if (background != null) {
			g.setColor(background);
			g.fillRect(0, 0, w, h);
		}

		boolean vert;
		Point3D topLeft3D, topRight3D, bottomLeft3D, bottomRight3D;
		if (direction == UP) {
			topLeft3D = new Point3D.Double(0,
					h / 2 - h / 2 * Math.cos(Math.PI * progress),
					-h / 2 * Math.sin(Math.PI * progress));
			topRight3D = new Point3D.Double(w,
					h / 2 - h / 2 * Math.cos(Math.PI * progress),
					-h / 2 * Math.sin(Math.PI * progress));
			bottomLeft3D = new Point3D.Double(0,
					h / 2 + h / 2 * Math.cos(Math.PI * progress),
					h / 2 * Math.sin(Math.PI * progress));
			bottomRight3D = new Point3D.Double(w,
					h / 2 + h / 2 * Math.cos(Math.PI * progress),
					h / 2 * Math.sin(Math.PI * progress));
			vert = true;
		} else if (direction == DOWN) {
			topLeft3D = new Point3D.Double(0,
					h / 2 - h / 2 * Math.cos(Math.PI * progress),
					h / 2 * Math.sin(Math.PI * progress));
			topRight3D = new Point3D.Double(w,
					h / 2 - h / 2 * Math.cos(Math.PI * progress),
					h / 2 * Math.sin(Math.PI * progress));
			bottomLeft3D = new Point3D.Double(0,
					h / 2 + h / 2 * Math.cos(Math.PI * progress),
					-h / 2 * Math.sin(Math.PI * progress));
			bottomRight3D = new Point3D.Double(w,
					h / 2 + h / 2 * Math.cos(Math.PI * progress),
					-h / 2 * Math.sin(Math.PI * progress));
			vert = true;
		} else if (direction == LEFT) {
			topLeft3D = new Point3D.Double(
					w / 2 - w / 2 * Math.cos(Math.PI * progress), 0,
					-w / 2 * Math.sin(Math.PI * progress));
			topRight3D = new Point3D.Double(
					w / 2 + w / 2 * Math.cos(Math.PI * progress), 0,
					w / 2 * Math.sin(Math.PI * progress));
			bottomLeft3D = new Point3D.Double(
					w / 2 - w / 2 * Math.cos(Math.PI * progress), h,
					-w / 2 * Math.sin(Math.PI * progress));
			bottomRight3D = new Point3D.Double(
					w / 2 + w / 2 * Math.cos(Math.PI * progress), h,
					w / 2 * Math.sin(Math.PI * progress));
			vert = false;
		} else {
			topLeft3D = new Point3D.Double(
					w / 2 - w / 2 * Math.cos(Math.PI * progress), 0,
					w / 2 * Math.sin(Math.PI * progress));
			topRight3D = new Point3D.Double(
					w / 2 + w / 2 * Math.cos(Math.PI * progress), 0,
					-w / 2 * Math.sin(Math.PI * progress));
			bottomLeft3D = new Point3D.Double(
					w / 2 - w / 2 * Math.cos(Math.PI * progress), h,
					w / 2 * Math.sin(Math.PI * progress));
			bottomRight3D = new Point3D.Double(
					w / 2 + w / 2 * Math.cos(Math.PI * progress), h,
					-w / 2 * Math.sin(Math.PI * progress));
			vert = false;
		}
		if (flush)
			flushZCoordinateWithSurface(topLeft3D, topRight3D, bottomLeft3D,
					bottomRight3D);

		BufferedImage scratchImage = borrowScratchImage(w, h);
		try {
			Quadrilateral2D q2;
			if (progress > .5) {
				if (vert) {
					Quadrilateral3D q3 = new Quadrilateral3D(bottomLeft3D,
							bottomRight3D, topRight3D, topLeft3D);
					q2 = paint(scratchImage, g.getRenderingHints(), frameB, q3,
							false, false);
				} else {
					Quadrilateral3D q3 = new Quadrilateral3D(topRight3D,
							topLeft3D, bottomLeft3D, bottomRight3D);
					q2 = paint(scratchImage, g.getRenderingHints(), frameB, q3,
							false, false);
				}
			} else {
				Quadrilateral3D q3 = new Quadrilateral3D(topLeft3D, topRight3D,
						bottomRight3D, bottomLeft3D);
				q2 = paint(scratchImage, g.getRenderingHints(), frameA, q3,
						false, false);
			}

			Graphics2D g3 = scratchImage.createGraphics();
			g3.setComposite(AlphaComposite.SrcAtop);
			double maxDarkness = 150;
			double z = vert
					? Math.abs(q2.bottomLeft.getY() - q2.topLeft.getY()) / h
					: Math.abs(q2.bottomLeft.getX() - q2.bottomRight.getX())
							/ w;
			int alpha = (int) (maxDarkness * (1 - z));
			alpha = Math.min(Math.max(0, alpha), 255);
			if (vert) {
				if (direction == UP) {
					g3.setPaint(new GradientPaint(0, (float) q2.topLeft.getY(),
							new Color(0, 0, 0, alpha), 0,
							(float) q2.bottomLeft.getY(),
							new Color(0, 0, 0, 0)));
				} else {
					g3.setPaint(new GradientPaint(0,
							(float) q2.bottomLeft.getY(),
							new Color(0, 0, 0, alpha), 0,
							(float) q2.topLeft.getY(), new Color(0, 0, 0, 0)));
				}
			} else {
				if (direction == LEFT) {
					g3.setPaint(new GradientPaint((float) q2.topLeft.getX(), 0,
							new Color(0, 0, 0, alpha),
							(float) q2.topRight.getX(), 0,
							new Color(0, 0, 0, 0)));
				} else {
					g3.setPaint(new GradientPaint((float) q2.topRight.getX(), 0,
							new Color(0, 0, 0, alpha),
							(float) q2.topLeft.getX(), 0,
							new Color(0, 0, 0, 0)));
				}
			}
			g3.fillRect(0, 0, w, h);
			g3.dispose();

			clearOutside(scratchImage, q2);

			g.drawImage(scratchImage, 0, 0, null);
		} finally {
			releaseScratchImage(scratchImage);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Flip ");
		if (direction == Transition.UP) {
			sb.append("Up");
		} else if (direction == Transition.DOWN) {
			sb.append("Down");
		} else if (direction == Transition.LEFT) {
			sb.append("Left");
		} else {
			sb.append("Right");
		}
		if (flush) {
			sb.append(" Flush");
		}
		return sb.toString();
	}

}