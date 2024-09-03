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
package com.pump.showcase.resourcegenerator;

import com.pump.transition.Transition;
import com.pump.showcase.demo.Transition3DDemo;

public class Transition3DDemoResourceGenerator
		extends Transition2DDemoResourceGenerator {

	@Override
	protected Transition[][] getTransitions() {
		return Transition3DDemo.getTransitions();
	}

}