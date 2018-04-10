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

import javax.swing.text.JTextComponent;

/**
 * A very simple search dialog.
 * 
 */
public class TextSearchDialog extends AbstractSearchDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * This creates a <code>TextSearchDialog</code> for the user to search a
	 * text component.
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
	protected boolean doNextSearch(boolean forward) {
		return SwingSearch.find(textComponent, textField.getText(), forward,
				false);
	}
}