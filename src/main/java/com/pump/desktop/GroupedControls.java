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
package com.pump.desktop;

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;

public class GroupedControls {

	protected Map<String, AbstractAction> actionMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T extends AbstractAction> T getAction(PumpCommand<T> command) {
		return (T) actionMap.get(command
				.getValue(AbstractAction.ACTION_COMMAND_KEY));
	}

	public void registerAction(AbstractAction action) {
		String cmd = (String) action
				.getValue(AbstractAction.ACTION_COMMAND_KEY);
		AbstractAction oldValue = actionMap.put(cmd, action);
		if (oldValue != null)
			throw new IllegalStateException(
					"Multiple actions registered for \"" + cmd + "\"");
	}
}