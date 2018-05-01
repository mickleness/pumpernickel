package com.pump.desktop;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

public class PumpCommand<T extends AbstractAction> {

	protected Map<String, Object> properties = new HashMap<>();
	Class<T> actionClass;

	protected PumpCommand(String text, Character accelerator,
			String commandName, Class<T> actionClass) {
		if (text != null) {
			properties.put(AbstractAction.NAME, text);
		}
		if (accelerator != null) {
			int modifiers = Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(
					accelerator.charValue(), modifiers);
			properties.put(AbstractAction.ACCELERATOR_KEY, keyStroke);
		}
		if (commandName != null) {
			properties.put(AbstractAction.ACTION_COMMAND_KEY, commandName);
		}
		this.actionClass = actionClass;
	}

	public Class<T> getActionClass() {
		return actionClass;
	}

	public void install(Action action) {
		for (String key : new String[] { AbstractAction.NAME,
				AbstractAction.ACCELERATOR_KEY,
				AbstractAction.ACTION_COMMAND_KEY }) {
			Object value = properties.get(key);
			if (value != null) {
				action.putValue(key, value);
			}
		}
	}

	public Object getValue(String key) {
		return properties.get(key);
	}
}
