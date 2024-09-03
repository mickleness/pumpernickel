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
package com.pump.desktop.doc;

import javax.swing.AbstractAction;

import com.pump.desktop.PumpCommand;

public class DocumentCommand<T extends AbstractAction> extends PumpCommand<T> {
	public static final DocumentCommand<NewDocumentAction> NEW = new DocumentCommand<NewDocumentAction>(
			"New", 'N', "new-document", NewDocumentAction.class);
	public static final DocumentCommand<OpenDocumentAction> OPEN = new DocumentCommand<OpenDocumentAction>(
			"Open...", 'O', "open-document", OpenDocumentAction.class);
	public static final DocumentCommand<SaveDocumentAction> SAVE = new DocumentCommand<SaveDocumentAction>(
			"Save", 'S', "save-document", SaveDocumentAction.class);
	public static final DocumentCommand<CloseDocumentAction> CLOSE = new DocumentCommand<CloseDocumentAction>(
			"Close", 'W', "close-document", CloseDocumentAction.class);

	public DocumentCommand(String text, Character accelerator,
			String commandName, Class<T> actionClass) {
		super(text, accelerator, commandName, actionClass);
	}
}