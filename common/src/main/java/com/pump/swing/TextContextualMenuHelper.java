/*
 * @(#)TextContextualMenuHelper.java
 *
 * $Date$
 *
 * Copyright (c) 2015 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.swing;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.text.JTextComponent;

/** Static methods relating to contextual menu for text components.
 */
public class TextContextualMenuHelper {
	
	protected static ResourceBundle strings = ResourceBundle.getBundle("com.pump.swing.TextContextualMenu");

	private static void install(final JTextComponent jtc,final String keyName,final String javaCommandName) {
		String menuItemName = strings.getString(keyName);
		ContextualMenuHelper.add(jtc, menuItemName, new Runnable() {
			public void run() {
				jtc.getActionMap().get(javaCommandName).actionPerformed(new ActionEvent(jtc, 0, javaCommandName));
			}
		});
	}

	/** Install some basic contextual menu items for a JTextComponent.
	 * Note Swing already implements most basic commands, so the implementation
	 * is not written here. This only applies a simple UI to existing functionality, because
	 * some users won't know to try ctrl-C to copy.
	 * 
	 * @param jtc the text component to install contextual menu items on.
	 * @param copy whether "Copy" should be included.
	 * @param cut whether "Cut" should be included.
	 * @param paste whether "Paste" should be included.
	 * @param clear whether "Clear" should be included.
	 */
	public static void install(final JTextComponent jtc,boolean copy,boolean cut,boolean paste,boolean clear) {
		if(cut) {
			install(jtc, "cut", "cut");
		}
		if(copy) {
			install(jtc, "copy", "copy");
		}
		if(paste) {
			install(jtc, "paste", "paste");
		}
		if(clear) {
			ContextualMenuHelper.add(jtc, strings.getString("clear"), new Runnable() {
				public void run() {
					jtc.setText("");
				}
			});
		}
	}
}
