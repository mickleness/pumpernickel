package com.pump.desktop.edit;

import javax.swing.AbstractAction;

import com.pump.desktop.PumpCommand;

public class EditCommand<T extends AbstractAction> extends PumpCommand<T> {
	public static final EditCommand<AbstractAction> CUT = new EditCommand<AbstractAction>(
			"Cut", 'X', "cut", AbstractAction.class);
	public static final EditCommand<AbstractAction> COPY = new EditCommand<AbstractAction>(
			"Copy", 'C', "copy", AbstractAction.class);
	public static final EditCommand<AbstractAction> PASTE = new EditCommand<AbstractAction>(
			"Paste", 'V', "paste", AbstractAction.class);
	public static final EditCommand<AbstractAction> SELECT_ALL = new EditCommand<AbstractAction>(
			"Select All", 'A', "select-all", AbstractAction.class);

	public EditCommand(String text, Character accelerator, String commandName,
			Class<T> actionClass) {
		super(text, accelerator, commandName, actionClass);
	}
}