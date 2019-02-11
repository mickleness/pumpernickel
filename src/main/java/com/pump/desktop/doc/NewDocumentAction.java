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
package com.pump.desktop.doc;

import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class NewDocumentAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	DocumentControls controls;

	public NewDocumentAction(DocumentControls controls) {
		this(controls, null);
	}

	/**
	 * @param text
	 *            this is an optional action name. If this is null a default
	 *            name like "New Document" is used, but you can customize this
	 *            to resemble "New Spreadsheet" or "New Image".
	 */
	public NewDocumentAction(DocumentControls controls, String text) {
		Objects.requireNonNull(controls);
		this.controls = controls;

		DocumentCommand.NEW.install(this);
		controls.registerAction(this);
		if (text != null)
			putValue(AbstractAction.NAME, text);

		controls.getOpenDocuments().addSynchronizedChangeListener(
				new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						refresh();
					}
				});
		refresh();
	}

	protected void refresh() {
		setEnabled(controls.getOpenDocuments().size() == controls
				.getMaximumDocumentCount());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (controls.isSingleDocumentInterface()) {
			CloseDocumentAction closeAction = controls
					.getAction(DocumentCommand.CLOSE);
			closeAction.prepareToCloseSingleDocumentInterface();
		}

		Document newDocument = createNewDocument();

		if (controls.isSingleDocumentInterface()) {
			controls.setDocuments(newDocument, new Document[] { newDocument });
		} else {
			controls.getOpenDocuments().add(newDocument);
			controls.setSelectedDocument(newDocument);
		}
	}

	/**
	 * Create a new blank document.
	 */
	protected abstract Document createNewDocument();
}