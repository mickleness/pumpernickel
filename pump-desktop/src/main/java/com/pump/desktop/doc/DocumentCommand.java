package com.pump.desktop.doc;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

public class DocumentCommand<T extends AbstractAction> {
	public static final DocumentCommand<NewDocumentAction> NEW = new DocumentCommand<NewDocumentAction>(
			"New", 'N', "new-document", NewDocumentAction.class);
	public static final DocumentCommand<OpenDocumentAction> OPEN = new DocumentCommand<OpenDocumentAction>(
			"Open...", 'O', "open-document", OpenDocumentAction.class);
	public static final DocumentCommand<SaveDocumentAction> SAVE = new DocumentCommand<SaveDocumentAction>(
			"Save", 'S', "save-document", SaveDocumentAction.class);
	public static final DocumentCommand<CloseDocumentAction> CLOSE = new DocumentCommand<CloseDocumentAction>(
			"Close", 'W', "close-document", CloseDocumentAction.class);

	protected Map<String, Object> properties = new HashMap<>();
	Class<T> actionClass;

	public DocumentCommand(String text, Character accelerator,
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
