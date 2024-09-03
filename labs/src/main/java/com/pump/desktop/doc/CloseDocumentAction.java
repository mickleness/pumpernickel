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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;

public class CloseDocumentAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	DocumentControls controls;

	public CloseDocumentAction(DocumentControls controls) {
		Objects.requireNonNull(controls);
		this.controls = controls;

		DocumentCommand.CLOSE.install(this);
		controls.registerAction(this);
		controls.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refresh();
			}

		});
		refresh();
	}

	protected void refresh() {
		Document selectedDocument = controls.getSelectedDocument();
		if (selectedDocument == null) {
			setEnabled(false);
		} else if (controls.getOpenDocuments().size() <= controls
				.getMinimumDocumentCount()) {
			setEnabled(false);
		} else {
			setEnabled(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Document selectedDocument = controls.getSelectedDocument();
		prepareToClose(selectedDocument);

		if (controls.getOpenDocuments().size() == 1) {
			boolean isZeroAllowed = controls.getMinimumDocumentCount() == 0;
			if (isZeroAllowed) {
				controls.setDocuments(null, new Document[] {});
			} else {
				// instead of setting the document list to empty, just hide the
				// window. we're done here.
				controls.getFrame().setVisible(false);
			}
		} else {
			List<Document> d = new ArrayList<>(controls.getOpenDocuments());
			int i = controls.getOpenDocuments().indexOf(selectedDocument);
			d.remove(i);
			i = Math.min(i, d.size() - 1);

			controls.setDocuments(d.get(i), d.toArray(new Document[d.size()]));
		}
	}

	/**
	 * If a document is dirty this will show the appropriate confirmation dialog
	 * and may (depending on user feedback) attempt to save the document.
	 * <p>
	 * This method may throw a UserCancelledException, but otherwise it's
	 * assumed if this completes normally that we're ready to close the
	 * document.
	 */
	protected void prepareToClose(Document document) {
		boolean dirty = document.isDirty();
		if (dirty) {
			int response = askToClose(document);
			if (response == DialogFooter.CANCEL_OPTION) {
				throw new CancellationException();
			}

			controls.getAction(DocumentCommand.SAVE).save(document);
		}
	}

	/**
	 * This method is called when the Document is dirty and we need to ask the
	 * user if they need to save it or not in order to close it.
	 * 
	 * @return one of the DialogFooter constants: SAVE, DONT_SAVE or CANCEL.
	 */
	protected int askToClose(Document document) {
		return QDialog.showSaveChangesDialog(controls.getFrame(),
				document.getName(), true);
	}

	/**
	 * This is a special method targeted towards single document interfaces.
	 * <p>
	 * If there is an open document and the maximum limit of documents is one:
	 * then this method will call {@link #prepareToClose(Document)} to fully
	 * close out the one open document.
	 * <p>
	 * This method should be invoked when the user selected "New" or "Open...".
	 */
	protected void prepareToCloseSingleDocumentInterface() {
		Document selectedDoc = controls.getSelectedDocument();
		if (selectedDoc != null && controls.isSingleDocumentInterface()
				&& controls.getOpenDocuments().size() == 1) {
			// special case: opening a document requires closing the current
			// document.
			prepareToClose(selectedDoc);
		}
	}
}