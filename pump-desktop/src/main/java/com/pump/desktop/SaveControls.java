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
package com.pump.desktop;

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.pump.UserCancelledException;
import com.pump.io.SuffixFilenameFilter;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.swing.DialogFooter;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.swing.QDialog;
import com.pump.swing.RecentMenu;
import com.pump.swing.RecentMenu.Listener;
import com.pump.util.BasicReceiver;

public abstract class SaveControls {

	/**
	 * This client property of the JRootPane should map to a java.io.File. This
	 * is how this object identifies the File that we're using, and on Macs
	 * using this key correctly updates the titlebar of frames.
	 * 
	 * @see <a
	 *      href="https://developer.apple.com/library/mac/technotes/tn2007/tn2196.html#WINDOW_DOCUMENTFILE">Apple
	 *      Tech Note 2196</a>
	 */
	public static final String KEY_FILE = "Window.documentFile";

	protected static final String KEY_SAVE_CONTROLS = SaveControls.class
			.getName() + "#saveControls";

	public static SaveControls get(JFrame f) {
		return (SaveControls) f.getRootPane().getClientProperty(
				KEY_SAVE_CONTROLS);
	}

	protected AbstractAction openAction = new AbstractAction("Open...") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				openFile();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	};

	protected AbstractAction saveAction = new AbstractAction("Save...") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			File file = getFile();
			try {
				if (file == null) {
					doSaveAs();
				} else {
					saveFile(file, null);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	};

	protected JMenuItem openItem = new JMenuItem(openAction);
	protected JMenuItem saveItem = new JMenuItem(saveAction);
	protected RecentMenu recentMenu = new RecentMenu.Preference(true,
			getClass());
	protected JFrame frame;
	protected boolean multipleDocuments;

	public SaveControls(JFrame frame) {
		if (frame == null)
			throw new NullPointerException();

		this.frame = frame;

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				tryToClose();
			}
		});

		SaveControls oldControls = get(frame);
		if (oldControls != null)
			throw new IllegalStateException(
					"SaveControls were already registered for this frame.");
		frame.getRootPane().putClientProperty(KEY_SAVE_CONTROLS, this);

		openItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));

		recentMenu.addListener(new Listener() {
			@Override
			public void fileSelected(File file) {
				try {
					openFile(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void openFile() throws UserCancelledException, Exception {

		if (!isMultipleDocuments() && isDocumentDirty()) {
			File oldFile = getFile();
			String docName = oldFile == null ? null : oldFile.getName();
			int choice = QDialog.showSaveChangesDialog(frame, docName, true);
			if (choice == DialogFooter.DONT_SAVE_OPTION) {
				// do nothing
			} else if (choice == DialogFooter.CANCEL_OPTION) {
				throw new UserCancelledException();
			} else if (choice == DialogFooter.SAVE_OPTION) {
				try {
					if (oldFile == null) {
						oldFile = doSaveAs();
						// this will quickly change, but for the moment:
						setFile(oldFile);
					} else {
						showSaveDialogAndDoSave(oldFile);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
					// erg, don't replace our file if a problem came up saving:
					return;
				}
			}
		}

		File file = this.showFileDialog(FileDialog.LOAD, "Open File...", null,
				getReadableDocumentFileExtensions());
		if (file == null)
			throw new UserCancelledException();
		openFile(file);
		recentMenu.addFile(file);
		setFile(file);
	}

	/**
	 * Return the document file extensions for files this SaveControls interacts
	 * with. For example "jpg" or "txt".
	 * 
	 * @return the document file extensions for files this SaveControls
	 *         interacts with.
	 */
	public abstract String[] getReadableDocumentFileExtensions();

	/**
	 * Set whether this fram should support having multiple documents open at
	 * once.
	 */
	public void setMultipleDocuments(boolean multipleDocuments) {
		this.multipleDocuments = multipleDocuments;
	}

	/**
	 * Return true if this frame is capable of representing multiple documents
	 * at once.
	 * <p>
	 * When a frame can NOT display multiple documents: the "Open..." command
	 * needs to first close the current document before opening a new document.
	 * (But when a frame can support multiple documents: existing documents are
	 * not modified when opening a new file.)
	 */
	public boolean isMultipleDocuments() {
		return multipleDocuments;
	}

	/**
	 * Return the document file extension that should be used for new saves.
	 * This may dynamically change during runtime.
	 * 
	 * @return the document file extension that should be used for new saves.
	 */
	public abstract String getWriteableDocumentFileExtension();

	protected abstract void openFile(File file) throws UserCancelledException,
			Exception;

	public File getFile() {
		return (File) frame.getRootPane().getClientProperty(KEY_FILE);
	}

	public void setFile(File f) {
		frame.getRootPane().putClientProperty(KEY_FILE, f);
	}

	/**
	 * Returns whether this window contains unsaved changes. This may be called
	 * on or off the EDT, so it needs to be fast and thread-safe.
	 * 
	 * @return whether this window contains unsaved changes.
	 */
	public abstract boolean isDocumentDirty();

	/**
	 * Save the document this window represents to the argument provided. This
	 * should never be called on the EDT, because saving may take an undefined
	 * amount of time.
	 * 
	 * @param f
	 *            the file to save to. This should never be null, because that
	 *            would require prompting the user for a save-as dialog which
	 *            needs to be handled on the EDT (and this call needs to be off
	 *            the EDT). This argument may not always be the same as
	 *            <code>this.getFile()</code>. For example: calling
	 *            <code>saveFile(newFile)</code> can be used to implement a
	 *            "Save As..." function.
	 * @param cancellable
	 *            an optional argument. If non-null then this method needs to
	 *            constantly poll this object to see if the user indicated they
	 *            wanted to cancel this operation. In that case this method
	 *            should wrap up as cleanly as possible and throw a
	 *            UserCancelledException
	 * @throws UserCancelledException
	 *             if the user cancelled this operation
	 * @throws Exception
	 *             if an unknown problem came up saving.
	 * 
	 */
	public abstract void saveFile(File f, Cancellable cancellable)
			throws UserCancelledException, Exception;

	/**
	 * If true then the user saved their changes as this method was called. If
	 * false then the user did not choose to save anything, or there was nothing
	 * to save. This may also throw a {@code UserCancelledException}
	 * 
	 * @return
	 */
	public boolean tryToClose() throws UserCancelledException {
		if (!isDocumentDirty()) {
			frame.setVisible(false);
			return false;
		}

		File file = getFile();
		String docName = file == null ? null : file.getName();
		int choice = QDialog.showSaveChangesDialog(frame, docName, true);
		if (choice == DialogFooter.DONT_SAVE_OPTION) {
			frame.setVisible(false);
			return false;
		} else if (choice == DialogFooter.CANCEL_OPTION) {
			throw new UserCancelledException();
		}

		try {
			if (file == null) {
				file = doSaveAs();
				setFile(file);
			} else {
				showSaveDialogAndDoSave(file);
			}
			frame.setVisible(false);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return true;
	}

	/**
	 * This should be called on the EDT
	 * 
	 * @return the File that was saved.
	 */
	protected File doSaveAs() throws UserCancelledException, Exception {
		File defaultFile = getFile();
		String ext = getWriteableDocumentFileExtension();
		File file = showFileDialog(FileDialog.SAVE, "Save As", defaultFile, ext);
		showSaveDialogAndDoSave(file);
		setFile(file);
		return file;
	}

	/**
	 * Show a FileDialog.
	 * 
	 * @param dialogMode
	 *            must be FileDialog.LOAD or FileDialog.SAVE
	 * @param dialogTitle
	 *            the title of this dialog.
	 * @param defaultFile
	 *            an optional file to initially select.
	 * @param documentFileExtension
	 *            an optional extension to apply, or array of extensions to
	 *            accept. This is used both as a file filter for this dialog and
	 *            the zeroeth element is manually appended to the filename if
	 *            necessary during a save. It is OK to leave this null/empty for
	 *            an open dialog, but it is highly discouraged to leave this
	 *            null for a save dialog.
	 * @return the File the user chose.
	 * @throws UserCancelledException
	 *             if the user cancelled this dialog.
	 */
	protected File showFileDialog(int dialogMode, String dialogTitle,
			File defaultFile, String... documentFileExtension)
			throws UserCancelledException {
		if (!(dialogMode == FileDialog.LOAD || dialogMode == FileDialog.SAVE))
			throw new IllegalArgumentException(
					"dialogMode must be LOAD or SAVE");
		FileDialog fd = new FileDialog(frame, dialogTitle, dialogMode);
		FilenameFilter filter = getFilenameFilter(documentFileExtension);
		if (filter != null) {
			fd.setFilenameFilter(filter);
		}
		if (defaultFile != null) {
			fd.setFile(defaultFile.getAbsolutePath());
		}
		fd.pack();
		fd.setLocationRelativeTo(null);
		fd.setVisible(true);
		if (fd.getFile() == null)
			throw new UserCancelledException();
		String s = fd.getFile();
		// this is going to cause problems on a sandboxed Mac:
		File f = new File(fd.getDirectory(), s);
		if (filter != null && (!filter.accept(f.getParentFile(), f.getName()))) {
			// TODO: if we change the file path (even a little), we need to
			// run a new (separate) check to see if the file exists and offer
			// a new do-you-want-to-replace dialog
			s = s + "." + documentFileExtension[0];
		}
		File file = new File(fd.getDirectory() + s);
		return file;

	}

	/**
	 * Return a FilenameFilter for the document file extensions, or null if all
	 * files should be accepted.
	 * 
	 * @param documentFileExtension
	 *            an optional list of file extensions we should support.
	 * @return a FilenameFilter, or null.
	 */
	protected FilenameFilter getFilenameFilter(String... documentFileExtension) {
		FilenameFilter filter = null;
		if (documentFileExtension != null && documentFileExtension.length > 0) {
			filter = new SuffixFilenameFilter(documentFileExtension);
		}
		return filter;
	}

	/**
	 * This is called on the EDT to save to a File. This pulls up an
	 * indeterminate progress bar dialog with a cancel button while the save
	 * operation is in progress.
	 */
	protected void showSaveDialogAndDoSave(final File file) throws Exception {
		final Cancellable cancellable = new BasicCancellable();
		String s = file.getName();

		JPanel content = new JPanel(new GridBagLayout());
		JProgressBar pb = new JProgressBar();
		pb.setIndeterminate(true);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(new JLabel("Saving document to \"" + file.getName()
				+ "\"..."), c);
		c.gridy++;
		content.add(pb, c);

		final QDialog qd = new QDialog(frame, "Saving \"" + s + "\"",
				QDialog.getIcon(QDialog.PLAIN_MESSAGE), content,
				DialogFooter.createDialogFooter(DialogFooter.CANCEL_OPTION,
						EscapeKeyBehavior.DOES_NOTHING), false); // closeable

		final BasicReceiver<Exception> saveThrowables = new BasicReceiver<Exception>();

		Thread thread = new Thread("Saving \"" + s + "\"") {
			public void run() {
				try {
					saveFile(file, cancellable);
				} catch (Exception t) {
					saveThrowables.add(t);
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							qd.setVisible(false);
						}
					});
				}
			}
		};
		thread.start();
		qd.pack();
		qd.setLocationRelativeTo(null);
		qd.setVisible(true);

		/*
		 * If the save completed, then the dialog hid itself because the save
		 * thread wrapped up. Otherwise the dialog hid itself because the user
		 * intervened and insisted on canceling.
		 */
		cancellable.cancel();

		if (saveThrowables.getSize() > 0) {
			throw saveThrowables.getElementAt(0);
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	public JMenuItem getOpenMenuItem() {
		return openItem;
	}

	public AbstractAction getOpenAction() {
		return openAction;
	}

	public RecentMenu getRecentMenu() {
		return recentMenu;
	}

	public AbstractAction getSaveAction() {
		return saveAction;
	}

	public JMenuItem getSaveMenuItem() {
		return saveItem;
	}
}