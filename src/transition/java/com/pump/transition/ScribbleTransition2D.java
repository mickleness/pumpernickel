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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.pump.geom.RectangularTransform;
import com.pump.geom.ShapeStringUtils;
import com.pump.geom.ShapeUtils;
import com.pump.geom.TransformUtils;

/**
 * This reveals the next frame as if a giant eraser is scribbling away the
 * current frame. (Or as if a coin is scratching a scratch-and-win game ticket.)
 * Here are playback samples:
 * <p>
 * <table summary="Sample Animations of ScribbleTransition2D" cellspacing="50"
 * border="0">
 * <tr>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/ScribbleTransition2D/Scribble.gif"
 * alt="Scribble">
 * <p>
 * Scribble</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/ScribbleTransition2D/ScribbleTwice.gif"
 * alt="Scribble Twice">
 * <p>
 * Scribble Twice</td>
 * </tr>
 * </table>
 * 
 */
public class ScribbleTransition2D extends Transition2D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new ScribbleTransition2D(false),
				new ScribbleTransition2D(true) };
	}

	Shape scribble;
	Rectangle2D shapeBounds;

	/**
	 * Meh. I don't really like the left-to-right option...
	 */
	int direction = RIGHT;

	boolean twoPasses;

	/**
	 * Creates a new scribble transition.
	 * 
	 * @param twoPasses
	 *            whether this transition is depicted in two scribbles or one.
	 */
	public ScribbleTransition2D(boolean twoPasses) {
		scribble = ShapeStringUtils.createPath(
				"m -6.286 19.763 l 10.03 -2.175 l 2.183 58.034 l 24.854 -3.692 l 18.468 60.811 l 39.862 -5.543 l 35.711 58.651 l 53.273 -4.926 l 48.934 54.989 l 55.852 27.084 l 60.936 55.565 z");
		this.twoPasses = twoPasses;
		shapeBounds = new Rectangle2D.Float(0, 0, 60, 60);
	}

	@Override
	public Transition2DInstruction[] getInstructions(float progress,
			Dimension size) {
		Rectangle2D bigRect = new Rectangle2D.Float(0, 0, size.width,
				size.height);
		AffineTransform t;
		if (direction == RIGHT) {
			t = RectangularTransform.create(shapeBounds, bigRect);
		} else {
			// flip horizontal
			Rectangle2D r1 = shapeBounds;
			t = TransformUtils.createAffineTransform(r1.getX(), r1.getY(),
					r1.getX() + r1.getWidth(), r1.getY(), r1.getX(),
					r1.getY() + r1.getHeight(),
					bigRect.getX() + bigRect.getWidth(), bigRect.getY(),
					bigRect.getX(), bigRect.getY(),
					bigRect.getX() + bigRect.getWidth(),
					bigRect.getY() + bigRect.getHeight());
		}

		if (twoPasses == false) {
			BasicStroke stroke = new BasicStroke(16.7f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			Shape subShape = ShapeUtils.traceShape(scribble, progress);
			subShape = stroke.createStrokedShape(subShape);
			subShape = t.createTransformedShape(subShape);

			return new Transition2DInstruction[] { new ImageInstruction(true),
					new ImageInstruction(false, null, subShape) };
		} else {
			float progress1 = progress / .5f;
			if (progress1 > 1)
				progress1 = 1;
			Shape subShape1 = ShapeUtils.traceShape(scribble, progress1);
			BasicStroke stroke = new BasicStroke(16.7f * .5f,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			subShape1 = stroke.createStrokedShape(subShape1);
			t.translate(2, 5);
			subShape1 = t.createTransformedShape(subShape1);
			t.translate(-2, -5);

			stroke = new BasicStroke(16.7f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			float progress2 = (progress - .5f) / .5f;
			if (progress2 < 0)
				progress2 = 0;
			Shape subShape2 = ShapeUtils.traceShape(scribble, progress2);
			subShape2 = stroke.createStrokedShape(subShape2);
			subShape2 = t.createTransformedShape(subShape2);

			return new Transition2DInstruction[] { new ImageInstruction(true),
					new ImageInstruction(false, null, subShape1),
					new ImageInstruction(false, null, subShape2) };
		}
	}

	@Override
	public String toString() {
		if (twoPasses)
			return "Scribble Twice";
		return "Scribble";
	}

}