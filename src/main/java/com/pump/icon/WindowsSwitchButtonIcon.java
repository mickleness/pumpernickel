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
