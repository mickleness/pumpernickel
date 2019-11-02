package com.pump.showcase;

import java.net.URL;

import com.pump.plaf.SwitchButtonUI;
import com.pump.swing.SwitchButton;

public class SwitchButtonUIDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	SwitchButton buttonA = new SwitchButton("Option A");
	SwitchButton buttonB = new SwitchButton("Option B");

	public SwitchButtonUIDemo() {
		add(buttonA);
		add(buttonB);
	}

	@Override
	public String getTitle() {
		return "SwitchButtonUI";
	}

	@Override
	public String getSummary() {
		return "This is an alternative to checkboxes that is common on smartphones and tablets.";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "button", "ux", "switch", "checkbox", "toggle" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { SwitchButtonUI.class };
	}

}
