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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.pump.awt.GradientStopHelper;

/**
 * This transition rotates a cube 90 degrees to reveal the next image. Here are
 * playback samples:
 * <p>
 * <table summary="Sample Animations of CubeTransition3D" cellspacing="50"
 * border="0">
 * <tr>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeLeft.gif"
 * alt="Cube Left">
 * <p>
 * Cube Left</td>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeRight.gif"
 * alt="Cube Right">
 * <p>
 * Cube Right</td>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeUp.gif"
 * alt="Cube Up">
 * <p>
 * Cube Up</td>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeDown.gif"
 * alt="Cube Down">
 * <p>
 * Cube Down</td>
 * </tr>
 * <tr>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeLeftFlush.gif"
 * alt="Cube Left Flush">
 * <p>
 * Cube Left Flush</td>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeRightFlush.gif"
 * alt="Cube Right Flush">
 * <p>
 * Cube Right Flush</td>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeUpFlush.gif"
 * alt="Cube UP Flush">
 * <p>
 * Cube Up Flush</td>
 * <td align="center"><img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/transition/CubeTransition3D/CubeDownFlush.gif"
 * alt="Cube Down Flush">
 * <p>
 * Cube Down Flush</td>
 * </tr>
 * </table>
 */
public class CubeTransition3D extends Transition3D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 * 
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new CubeTransition3D(UP, false),
				new CubeTransition3D(DOWN, false),
				new CubeTransition3D(LEFT, false),
				new CubeTransition3D(RIGHT, false),
				new CubeTransition3D(UP, true),
				new CubeTransition3D(DOWN, true),
				new CubeTransition3D(LEFT, true),
				new CubeTransition3D(RIGHT, true) };
	}

	int direction;
	boolean flush;
	Color background;

	/**
	 * Create a CubeTransition3D with a black background.
	 * 
	 * @param direction
	 *            one of the Transition constants: UP, DOWN, LEFT or RIGHT
	 * @param flush
	 *            whether the z-axis should remain flush with the target
	 *            destination. For example: if this is false then as a surface
	 *            turns to the right a vertical edge will be pulled towards the
	 *            camera and grow larger in height than than the target
	 *            destination. So the center of the rotation is constant. But if
	 *            this is true: then the center of the rotate pulls farther away
	 *            from the camera to make sure that vertical edge is never
	 *            larger than the height of the target graphics area.
	 */
	public CubeTransition3D(int direction, boolean flush) {
		this(direction, flush, Color.black);
	}

	/**
	 * Create a CubeTransition3D.
	 * 
	 * @param direction
	 *            one of the Transition constants: UP, DOWN, LEFT or RIGHT
	 * @param flush
	 *            whether the z-axis should remain flush with the target
	 *            destination. For example: if this is false then as a surface
	 *            turns to the right a vertical edge will be pulled towards the
	 *            camera and grow larger in height than than the target
	 *            destination. So the center of the rotation is constant. But if
	 *            this is true: then the center of the rotate pulls farther away
	 *            from the camera to make sure that vertical edge is never
	 *            larger than the height of the target graphics area.
	 * @param background
	 *            the optional background color to paint behind this transition.
	 */
	public CubeTransition3D(int direction, boolean flush, Color background) {
		if (!(direction == UP || direction == DOWN || direction == LEFT
				|| direction == RIGHT)) {
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
		Point3D topLeftA3D, topRightA3D, bottomLeftA3D, bottomRightA3D;
		Point3D topLeftB3D, topRightB3D, bottomLeftB3D, bottomRightB3D;
		double t = progress + 4f / 8f;
		double k = Math.PI / 2.0;
		if (direction == RIGHT) {
			double centerX = w / 2.0;
			double z = Point2D.distance(centerX, centerX, 0, 0);
			double j = -z * Math.sin(2 * Math.PI / 8);
			topLeftA3D = new Point3D.Double(centerX - z * Math.cos(k * t), 0,
					z * Math.sin(k * t) + j);
			topRightA3D = new Point3D.Double(centerX - z * Math.cos(k * t + k),
					0, z * Math.sin(k * t + k) + j);
			bottomLeftA3D = new Point3D.Double(centerX - z * Math.cos(k * t), h,
					z * Math.sin(k * t) + j);
			bottomRightA3D = new Point3D.Double(
					centerX - z * Math.cos(k * t + k), h,
					z * Math.sin(k * t + k) + j);
			topLeftB3D = new Point3D.Double(centerX - z * Math.cos(k * t - k),
					0, z * Math.sin(k * t - k) + j);
			topRightB3D = new Point3D.Double(centerX - z * Math.cos(k * t), 0,
					z * Math.sin(k * t) + j);
			bottomLeftB3D = new Point3D.Double(
					centerX - z * Math.cos(k * t - k), h,
					z * Math.sin(k * t - k) + j);
			bottomRightB3D = new Point3D.Double(centerX - z * Math.cos(k * t),
					h, z * Math.sin(k * t) + j);
		} else if (direction == LEFT) {
			double centerX = w / 2.0;
			double z = Point2D.distance(centerX, centerX, 0, 0);
			double j = -z * Math.sin(2 * Math.PI / 8);
			topRightA3D = new Point3D.Double(centerX + z * Math.cos(k * t), 0,
					z * Math.sin(k * t) + j);
			topLeftA3D = new Point3D.Double(centerX + z * Math.cos(k * t + k),
					0, z * Math.sin(k * t + k) + j);
			bottomRightA3D = new Point3D.Double(centerX + z * Math.cos(k * t),
					h, z * Math.sin(k * t) + j);
			bottomLeftA3D = new Point3D.Double(
					centerX + z * Math.cos(k * t + k), h,
					z * Math.sin(k * t + k) + j);
			topRightB3D = new Point3D.Double(centerX + z * Math.cos(k * t - k),
					0, z * Math.sin(k * t - k) + j);
			topLeftB3D = new Point3D.Double(centerX + z * Math.cos(k * t), 0,
					z * Math.sin(k * t) + j);
			bottomRightB3D = new Point3D.Double(
					centerX + z * Math.cos(k * t - k), h,
					z * Math.sin(k * t - k) + j);
			bottomLeftB3D = new Point3D.Double(centerX + z * Math.cos(k * t), h,
					z * Math.sin(k * t) + j);
		} else if (direction == DOWN) {
			double centerY = h / 2.0;
			double z = Point2D.distance(centerY, centerY, 0, 0);
			double j = -z * Math.sin(2 * Math.PI / 8);
			topLeftA3D = new Point3D.Double(0, centerY - z * Math.cos(k * t),
					z * Math.sin(k * t) + j);
			bottomLeftA3D = new Point3D.Double(0,
					centerY - z * Math.cos(k * t + k),
					z * Math.sin(k * t + k) + j);
			topRightA3D = new Point3D.Double(w, centerY - z * Math.cos(k * t),
					z * Math.sin(k * t) + j);
			bottomRightA3D = new Point3D.Double(w,
					centerY - z * Math.cos(k * t + k),
					z * Math.sin(k * t + k) + j);
			topLeftB3D = new Point3D.Double(0,
					centerY - z * Math.cos(k * t - k),
					z * Math.sin(k * t - k) + j);
			bottomLeftB3D = new Point3D.Double(0, centerY - z * Math.cos(k * t),
					z * Math.sin(k * t) + j);
			topRightB3D = new Point3D.Double(w,
					centerY - z * Math.cos(k * t - k),
					z * Math.sin(k * t - k) + j);
			bottomRightB3D = new Point3D.Double(w,
					centerY - z * Math.cos(k * t), z * Math.sin(k * t) + j);
		} else {
			double centerY = h / 2.0;
			double z = Point2D.distance(centerY, centerY, 0, 0);
			double j = -z * Math.sin(2 * Math.PI / 8);
			bottomLeftA3D = new Point3D.Double(0, centerY + z * Math.cos(k * t),
					z * Math.sin(k * t) + j);
			topLeftA3D = new Point3D.Double(0,
					centerY + z * Math.cos(k * t + k),
					z * Math.sin(k * t + k) + j);
			bottomRightA3D = new Point3D.Double(w,
					centerY + z * Math.cos(k * t), z * Math.sin(k * t) + j);
			topRightA3D = new Point3D.Double(w,
					centerY + z * Math.cos(k * t + k),
					z * Math.sin(k * t + k) + j);
			bottomLeftB3D = new Point3D.Double(0,
					centerY + z * Math.cos(k * t - k),
					z * Math.sin(k * t - k) + j);
			topLeftB3D = new Point3D.Double(0, centerY + z * Math.cos(k * t),
					z * Math.sin(k * t) + j);
			bottomRightB3D = new Point3D.Double(w,
					centerY + z * Math.cos(k * t - k),
					z * Math.sin(k * t - k) + j);
			topRightB3D = new Point3D.Double(w, centerY + z * Math.cos(k * t),
					z * Math.sin(k * t) + j);
		}

		if (flush)
			flushZCoordinateWithSurface(bottomLeftA3D, topLeftA3D,
					bottomRightA3D, topRightA3D, bottomLeftB3D, topLeftB3D,
					bottomRightB3D, topRightB3D);

		BufferedImage scratchImage = borrowScratchImage(w, h);
		try {
			Quadrilateral3D qb3 = new Quadrilateral3D(topLeftB3D, topRightB3D,
					bottomRightB3D, bottomLeftB3D);
			Quadrilateral2D qb = paint(scratchImage, g.getRenderingHints(),
					frameB, qb3, true, true);

			Quadrilateral3D qa3 = new Quadrilateral3D(topLeftA3D, topRightA3D,
					bottomRightA3D, bottomLeftA3D);
			Quadrilateral2D qa = paint(scratchImage, g.getRenderingHints(),
					frameA, qa3, true, true);

			// draw the shadows

			Color transBlack = new Color(0, 0, 0, 0);

			Paint paint;

			if (direction == UP) {
				GradientStopHelper gsh = new GradientStopHelper();

				if (qa != null) {
					int alphaA = (int) (255 * (qa.topLeft.getX() / w * 2));
					alphaA = Math.min(alphaA, 255);
					Color shadowA = new Color(0, 0, 0, alphaA);
					gsh.addStop(qa.topLeft.getY() / h, shadowA);
					gsh.addStop(qa.bottomLeft.getY() / h, transBlack);
				}

				if (qb != null) {
					int alphaB = (int) (255 * (qb.bottomLeft.getX() / w * 2));
					alphaB = Math.min(alphaB, 255);
					Color shadowB = new Color(0, 0, 0, alphaB);
					gsh.addStop(qb.topLeft.getY() / h, transBlack);
					gsh.addStop(qb.bottomLeft.getY() / h, shadowB);
				}

				paint = gsh.toPaint(0, 0, 0, h);
			} else if (direction == DOWN) {
				GradientStopHelper gsh = new GradientStopHelper();

				if (qa != null) {
					int alphaA = (int) (255 * (qa.bottomLeft.getX() / w * 2));
					alphaA = Math.min(alphaA, 255);
					Color shadowA = new Color(0, 0, 0, alphaA);
					gsh.addStop(qa.bottomLeft.getY() / h, shadowA);
					gsh.addStop(qa.topLeft.getY() / h, transBlack);
				}

				if (qb != null) {
					int alphaB = (int) (255 * (qb.topLeft.getX() / w * 2));
					alphaB = Math.min(alphaB, 255);
					Color shadowB = new Color(0, 0, 0, alphaB);
					gsh.addStop(qb.topLeft.getY() / h, shadowB);
					gsh.addStop(qb.bottomLeft.getY() / h, transBlack);
				}

				paint = gsh.toPaint(0, 0, 0, h);
			} else if (direction == LEFT) {
				GradientStopHelper gsh = new GradientStopHelper();

				if (qa != null) {
					int alphaA = (int) (255 * (qa.topLeft.getY() / h * 2));
					alphaA = Math.min(alphaA, 255);
					Color shadowA = new Color(0, 0, 0, alphaA);
					gsh.addStop(qa.topLeft.getX() / w, shadowA);
					gsh.addStop(qa.topRight.getX() / w, transBlack);
				}

				if (qb != null) {
					int alphaB = (int) (255 * (qb.topRight.getY() / h * 2));
					alphaB = Math.min(alphaB, 255);
					Color shadowB = new Color(0, 0, 0, alphaB);
					gsh.addStop(qb.topLeft.getX() / w, transBlack);
					gsh.addStop(qb.topRight.getX() / w, shadowB);
				}

				paint = gsh.toPaint(0, 0, w, 0);
			} else {
				// direction == RIGHT

				GradientStopHelper gsh = new GradientStopHelper();

				if (qa != null) {
					int alphaA = (int) (255 * (qa.topRight.getY() / h * 2));
					alphaA = Math.min(alphaA, 255);
					Color shadowA = new Color(0, 0, 0, alphaA);
					gsh.addStop(qa.topLeft.getX() / w, transBlack);
					gsh.addStop(qa.topRight.getX() / w, shadowA);
				}

				if (qb != null) {
					int alphaB = (int) (255 * (qb.topLeft.getY() / h * 2));
					alphaB = Math.min(alphaB, 255);
					Color shadowB = new Color(0, 0, 0, alphaB);
					gsh.addStop(qb.topLeft.getX() / w, shadowB);
					gsh.addStop(qb.topRight.getX() / w, transBlack);
				}

				paint = gsh.toPaint(0, 0, w, 0);
			}

			if (paint != null) {
				Graphics2D g2 = scratchImage.createGraphics();
				g2.setPaint(paint);
				g2.fillRect(0, 0, w, h);
				g2.dispose();
			}

			clearOutside(scratchImage, qb, qa);

			g.drawImage(scratchImage, 0, 0, null);
		} finally {
			releaseScratchImage(scratchImage);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Cube ");
		if (direction == UP) {
			sb.append("Up");
		} else if (direction == DOWN) {
			sb.append("Down");
		} else if (direction == LEFT) {
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