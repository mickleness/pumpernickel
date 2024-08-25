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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.pump.geom.Clipper;
import com.pump.geom.MeasuredShape;
import com.pump.geom.RectangularTransform;

/**
 * This is a fun variation of a "Wipe" transition. The line that separates the
 * two frames spins as it slides. The circular wipe is especially interesting
 * because it begins and ends on the same side of the frame.
 * <P>
 * (This is loosely based on a transition I saw while watching Royal Pains on
 * Hulu one weekend...)
 * <p>
 * Here are playback samples:
 * <p>
 * <table summary="Sample Animations of FunkyWipeTransition2D" cellspacing="50" border="0">
 * <tr>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FunkyWipeTransition2D/FunkyWipeCircular.gif"
 * alt="Funky Wipe Circular">
 * <p>
 * Funky Wipe Circular</td>
 * <td align="center">
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FunkyWipeTransition2D/FunkyWipeAcross.gif"
 * alt="Funky Wipe Across">
 * <p>
 * Funky Wipe Across</td>
 * </tr>
 * </table>
 *
 */
public class FunkyWipeTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new FunkyWipeTransition2D(true),
				new FunkyWipeTransition2D(false) };
	}

	private static final GeneralPath pathCyclic = createPathCyclic();
	private static final MeasuredShape measuredPathCyclic = new MeasuredShape(
			pathCyclic);
	private static final GeneralPath pathAcross = createPathAcross();
	private static final MeasuredShape measuredPathAcross = new MeasuredShape(
			pathAcross);

	private static GeneralPath createPathCyclic() {
		GeneralPath p = new GeneralPath();
		p.moveTo(99.936f, 51.019f);
		p.curveTo(99.936f, 51.019f, 78.316f, 86.931f, 51.019f, 89.745f);
		p.curveTo(23.721f, 92.559f, -2.012f, 75.843f, 11.082f, 61.21f);
		p.curveTo(4.178f, 46.576f, 34.931f, 39.565f, 62.229f, 36.751f);
		p.curveTo(89.526f, 33.937f, 99.936f, 51.019f, 99.936f, 51.019f);
		return p;
	}

	private static GeneralPath createPathAcross() {
		GeneralPath p = new GeneralPath();
		p.moveTo(99.936f, 21.019f);
		p.curveTo(99.936f, 51.019f, 78.316f, 86.931f, 51.019f, 89.745f);
		p.curveTo(23.721f, 92.559f, -2.012f, 75.843f, 0, 61.21f);
		return p;
	}

	boolean circular;

	public FunkyWipeTransition2D(boolean fullCircle) {
		circular = fullCircle;
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Rectangle2D.Float frameRect = new Rectangle2D.Float(0, 0, size.width,
				size.height);
		Point2D p = new Point2D.Double();
		MeasuredShape path = circular ? measuredPathCyclic : measuredPathAcross;

		path.getPoint(progress * path.getOriginalDistance(), p);

		int m = circular ? 1 : 2;
		double angle = Math.PI / 2 + m * Math.PI * progress;

		float k = 10000;
		GeneralPath clip = new GeneralPath();
		clip.moveTo((float) p.getX(), (float) p.getY());
		clip.lineTo((float) (p.getX() + k * Math.cos(angle)),
				(float) (p.getY() + k * Math.sin(angle)));
		clip.lineTo(
				(float) (p.getX() + k * Math.cos(angle) + k
						* Math.cos(angle - Math.PI / 2)), (float) (p.getY() + k
						* Math.sin(angle) + k * Math.sin(angle - Math.PI / 2)));
		clip.lineTo(
				(float) (p.getX() - 100 * Math.cos(angle) + k
						* Math.cos(angle - Math.PI / 2)), (float) (p.getY() - k
						* Math.sin(angle) + k * Math.sin(angle - Math.PI / 2)));
		clip.lineTo((float) (p.getX() - k * Math.cos(angle)),
				(float) (p.getY() - k * Math.sin(angle)));
		clip.closePath();

		AffineTransform map = RectangularTransform.create(
				new Rectangle2D.Float(0, 0, 100, 100), frameRect);
		clip.transform(map);

		clip = Clipper.clipToRect(clip, frameRect);

		return new Transition2DInstruction[] {
				new ImageInstruction(true, 1, frameRect, size, null),
				new ImageInstruction(false, 1, frameRect, size, clip) };
	}

	@Override
	public String toString() {
		String s = (circular ? " Circular" : " Across");
		return "Funky Wipe " + s;
	}

}