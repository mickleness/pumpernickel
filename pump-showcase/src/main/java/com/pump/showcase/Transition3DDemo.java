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

	static Transition[][] transitions = new Transition[][] {
			CubeTransition3D.getDemoTransitions(),
			FlipTransition3D.getDemoTransitions() };

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