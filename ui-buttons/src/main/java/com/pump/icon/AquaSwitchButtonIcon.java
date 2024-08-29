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
package com.pump.icon;

public class AquaSwitchButtonIcon extends AbstractSwitchButtonIcon {

	static ButtonTheme unselected = new ButtonTheme(0xd8d8d8, 0xededed,
			0xd8d8d8, 0xededed, 0xebebeb, 0xf4f4f4, 0xe0e0e0, 0xeaeaea,
			0xffffff, 0xf0f0f0);
	static ButtonTheme selected = new ButtonTheme(0x275aea, 0x4684f6, 0x275aea,
			0x4684f6, 0x2963ff, 0x4787fe, 0x2963ff, 0x4787fe, 0xffffff,
			0xf0f0f0);

	public AquaSwitchButtonIcon() {
		super(38, 21, 21, unselected, selected);
	}
}