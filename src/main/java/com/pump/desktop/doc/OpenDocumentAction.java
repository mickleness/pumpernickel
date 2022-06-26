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
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.UserCancelledException;
import com.pump.swing.FileDialogUtils;

public abstract class OpenDocumentAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	DocumentControls controls;
	String[] supportedFileExtensions;

	/**
	 * 
	 * @param supportedFileExtensions
	 *            this optional set of file extensions will be used by the
	 *            default implementation of {@link #browseFile()}. If subclasses
	 *            override that method, then this argument may not be used at
	 *            all.
	 */
	public OpenDocumentAction(DocumentControls controls,
			String... supportedFileExtensions) {
		Objects.requireNonNull(controls);
		// you can leave this empty so we get a zero-length array, but don't
		// explicitly pass in null.
		Objects.requireNonNull(supportedFileExtensions);
		this.controls = controls;
		this.supportedFileExtensions = supportedFileExtensions;
		DocumentCommand.OPEN.install(this);
		controls.registerAction(this);

		controls.getOpenDocuments().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				refresh();
			}
		}, true);
		refresh();
	}

	protected void refresh() {
		int max = controls.getMaximumDocumentCount();
		if (controls.isSingleDocumentInterface()) {
			// this is a special case. Here let's assume "Open" means
			// "Close and then open"
			setEnabled(true);
		} else {
			setEnabled(controls.getOpenDocuments().size() < max);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		openFile(null);
	}

	/**
	 * Open a file, or throw a UserCancelledException.
	 * 
	 * @param documentFile
	 *            if null then this will invoke {@link #browseFile()} to choose
	 *            a File. If non-null then this is the document we'll try to
	 *            open.
	 * 
	 * @return true if this call actually opened a new Document; false if the
	 *         Document was already opened and this method only called
	 *         {@link DocumentControls#setSelectedDocument(Document)}.
	 */
	public boolean openFile(File documentFile) {
		for (Document d : controls.getOpenDocuments()) {
			if (documentFile != null && documentFile.equals(d.getFile())) {
				controls.setSelectedDocument(d);
				return false;
			}
		}

		if (controls.isSingleDocumentInterface()) {
			CloseDocumentAction closeAction = controls
					.getAction(DocumentCommand.CLOSE);
			closeAction.prepareToCloseSingleDocumentInterface();
		}

		if (documentFile == null)
			documentFile = browseFile();
		if (documentFile == null)
			throw new UserCancelledException(true);

		Document newDocument = null;
		try {
			newDocument = createDocument(documentFile);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if (newDocument != null) {
			if (controls.isSingleDocumentInterface()) {
				controls.setDocuments(newDocument,
						new Document[] { newDocument });
			} else {
				controls.getOpenDocuments().add(newDocument);
				controls.setSelectedDocument(newDocument);
			}
		}
		return true;
	}

	protected File browseFile() {
		if (supportedFileExtensions.length == 0)
			throw new IllegalStateException(
					"File extensions must be passed into the constructor, or subclasses must override this method.");

		String dialogTitle;
		if (supportedFileExtensions.length == 1) {
			dialogTitle = "Open " + supportedFileExtensions[0].toUpperCase();
		} else {
			dialogTitle = "Open Document";
		}
		return FileDialogUtils.showOpenDialog(controls.getFrame(), dialogTitle,
				supportedFileExtensions);
	}

	/**
	 * Create a Document from a File.
	 * <p>
	 * If something goes wrong, this may either throw an exception or return
	 * null. If this returns null it is assumed that the subclass appropriately
	 * handled the interaction and informed the user about the problem(s).
	 */
	protected abstract Document createDocument(File file) throws IOException;
}