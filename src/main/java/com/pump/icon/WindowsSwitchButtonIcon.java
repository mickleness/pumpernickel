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
package com.pump.icon;

public class WindowsSwitchButtonIcon extends AbstractSwitchButtonIcon {

	static ButtonTheme UNSELECTED = new ButtonTheme(0x0, 0x0, 0x0, 0x0,
			0xffffff, 0xffffff, 0xdddddd, 0xdddddd, 0x0, 0x0);

	static ButtonTheme SELECTED = new ButtonTheme(0x0F64B7, 0x0F64B7, 0xffffff,
			0xffffff, 0x0F64B7, 0x0F64B7, 0x0F54C7, 0x0F54C7, 0xffffff,
			0xffffff);

	public WindowsSwitchButtonIcon() {
		super(44, 20, 10, UNSELECTED, SELECTED);
		setStrokeWidth(2);
		setHandlePadding(5);
	}

}