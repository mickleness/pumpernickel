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
package com.pump.swing;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.text.JTextComponent;

/**
 * Static methods relating to contextual menu for text components.
 * 
 * TODO: integrate with the desktop project's edit menu controls. Functionally
 * this is easy; the hard part is the accompanying refactors.
 */
public class TextContextualMenuHelper {

	protected static ResourceBundle strings = ResourceBundle
			.getBundle("com.pump.swing.TextContextualMenu");

	private static void install(final JTextComponent jtc, final String keyName,
			final String javaCommandName) {
		String menuItemName = strings.getString(keyName);
		ContextualMenuHelper.add(jtc, menuItemName, new Runnable() {
			public void run() {
				jtc.getActionMap()
						.get(javaCommandName)
						.actionPerformed(
								new ActionEvent(jtc, 0, javaCommandName));
			}
		});
	}

	/**
	 * Install some basic contextual menu items for a JTextComponent. Note Swing
	 * already implements most basic commands, so the implementation is not
	 * written here. This only applies a simple UI to existing functionality,
	 * because some users won't know to try ctrl-C to copy.
	 * 
	 * @param jtc
	 *            the text component to install contextual menu items on.
	 * @param copy
	 *            whether "Copy" should be included.
	 * @param cut
	 *            whether "Cut" should be included.
	 * @param paste
	 *            whether "Paste" should be included.
	 * @param clear
	 *            whether "Clear" should be included.
	 */
	public static void install(final JTextComponent jtc, boolean copy,
			boolean cut, boolean paste, boolean clear) {
		if (cut) {
			install(jtc, "cut", "cut");
		}
		if (copy) {
			install(jtc, "copy", "copy");
		}
		if (paste) {
			install(jtc, "paste", "paste");
		}
		if (clear) {
			ContextualMenuHelper.add(jtc, strings.getString("clear"),
					new Runnable() {
						public void run() {
							jtc.setText("");
						}
					});
		}
	}
}