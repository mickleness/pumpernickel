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
package com.pump.showcase;

import java.awt.RenderingHints;

import com.pump.image.transition.CubeTransition3D;
import com.pump.image.transition.FlipTransition3D;
import com.pump.image.transition.Transition;
import com.pump.image.transition.Transition3D;

/**
 * A demo of the {@link Transition3D} classes.
 * 
 * @see com.pump.showcase.Transition2DDemo
 */
public class Transition3DDemo extends TransitionDemo {
	private static final long serialVersionUID = 1L;

	static Transition[] transitions = new Transition[] {
			new CubeTransition3D(Transition.LEFT, false),
			new CubeTransition3D(Transition.RIGHT, false),
			new CubeTransition3D(Transition.UP, false),
			new CubeTransition3D(Transition.DOWN, false),
			new CubeTransition3D(Transition.LEFT, true),
			new CubeTransition3D(Transition.RIGHT, true),
			new CubeTransition3D(Transition.UP, true),
			new CubeTransition3D(Transition.DOWN, true),
			new FlipTransition3D(Transition.LEFT, false),
			new FlipTransition3D(Transition.RIGHT, false),
			new FlipTransition3D(Transition.UP, false),
			new FlipTransition3D(Transition.DOWN, false),
			new FlipTransition3D(Transition.LEFT, true),
			new FlipTransition3D(Transition.RIGHT, true),
			new FlipTransition3D(Transition.UP, true),
			new FlipTransition3D(Transition.DOWN, true) };

	public Transition3DDemo() {
		super(transitions, true);
	}

	@Override
	public RenderingHints getQualityHints() {
		RenderingHints rh = super.getQualityHints();
		rh.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		return rh;
	}
}