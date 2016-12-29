/*
 * @(#)TextSearchDialog.java
 *
 * $Date: 2015-06-20 06:55:39 -0400 (Sat, 20 Jun 2015) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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

import javax.swing.text.JTextComponent;

/** A very simple search dialog.
 * 
 */
public class TextSearchDialog extends AbstractSearchDialog {
	private static final long serialVersionUID = 1L;

	/** This creates a <code>TextSearchDialog</code> for the user
	 * to search a text component.
	 */
	public static void search(JTextComponent text) {
		TextSearchDialog dialog = new TextSearchDialog(text);
		dialog.setVisible(true);
	}
	
	protected JTextComponent textComponent;

	public TextSearchDialog(JTextComponent text) {
		super(text);
		this.textComponent = text;
		
	}

	@Override
	protected boolean doNextSearch(boolean forward)
	{
		return SwingSearch.find(textComponent, textField.getText(), forward, false);
	}
}
