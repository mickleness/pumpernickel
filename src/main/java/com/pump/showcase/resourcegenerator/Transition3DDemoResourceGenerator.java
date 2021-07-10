package com.pump.showcase.resourcegenerator;

import com.pump.image.transition.Transition;
import com.pump.showcase.demo.Transition3DDemo;

public class Transition3DDemoResourceGenerator
		extends Transition2DDemoResourceGenerator {

	@Override
	protected Transition[][] getTransitions() {
		return Transition3DDemo.getTransitions();
	}

}
