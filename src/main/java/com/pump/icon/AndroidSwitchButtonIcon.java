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

public class AndroidSwitchButtonIcon extends AbstractSwitchButtonIcon {

	static ButtonTheme unselected = new ButtonTheme(0xb9b9b9, 0xb9b9b9,
			0xb9b9b9, 0xb9b9b9, 0xb9b9b9, 0xb9b9b9, 0xececec, 0xcccccc);
	static ButtonTheme selected = new ButtonTheme(0xb9dbd8, 0xb9dbd8, 0xb9dbd8,
			0xb9dbd8, 0xb9dbd8, 0xb9dbd8, 0x008275, 0x007265);

	public AndroidSwitchButtonIcon() {
		super(24, 10, 16, unselected, selected);
	}
}