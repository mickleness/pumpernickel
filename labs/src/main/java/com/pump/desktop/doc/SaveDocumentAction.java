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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.swing.DialogFooter;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.swing.FileDialogUtils;
import com.pump.swing.QDialog;
import com.pump.util.BasicConsumer;

public abstract class SaveDocumentAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	DocumentControls controls;
	String fileExtension;
	ChangeListener selectedDocumentChangeListener = new ChangeListener() {
		PropertyChangeListener documentPropertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refresh();
			}

		};

		Document lastSelectedDoc;

		@Override
		public void stateChanged(ChangeEvent e) {
			if (lastSelectedDoc != null)
				lastSelectedDoc
						.removePropertyChangeListener(documentPropertyChangeListener);
			lastSelectedDoc = controls.getSelectedDocument();
			if (lastSelectedDoc != null)
				lastSelectedDoc
						.addPropertyChangeListener(documentPropertyChangeListener);
			refresh();
		}

	};

	/**
	 * @param fileExtension
	 *            this optional file extension will be used by the default
	 *            implementation of {@link #browseFile(Document)}. If subclasses
	 *            override that method then this argument may not be used at
	 *            all.
	 */
	public SaveDocumentAction(DocumentControls controls, String fileExtension) {
		Objects.requireNonNull(controls);
		this.controls = controls;
		this.fileExtension = fileExtension;
		DocumentCommand.SAVE.install(this);
		controls.registerAction(this);

		controls.addChangeListener(selectedDocumentChangeListener);
		refresh();
	}

	protected void refresh() {
		Document doc = controls.getSelectedDocument();
		setEnabled(doc == null ? false : doc.isDirty());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Document doc = controls.getSelectedDocument();
		save(doc);
	}

	public void save(Document doc) {
		File file = doc.getFile();
		try {
			if (file == null) {
				file = browseFile(doc);
			}
			save(doc, file);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Browse for a file to save a Document to.
	 * <p>
	 * This method is called when this action is invoked but the document's
	 * {@link Document#getFile()} returns null.
	 * <p>
	 * This method uses the file extension provided in the constructor. The
	 * default implementation of this method will throw a NPE if that file
	 * extension is null.
	 */
	protected File browseFile(Document doc) {
		if (fileExtension == null)
			throw new IllegalStateException(
					"The file extension must be passed into the constructor, or subclasses must override this method.");

		String dialogTitle = "Save " + fileExtension.toUpperCase();
		return FileDialogUtils.showSaveDialog(controls.getFrame(), dialogTitle,
				fileExtension);
	}

	/**
	 * Save a document to a given file. If successfully this should also fire
	 * PropertyChangeListeners for the {@link Document#PROPERTY_FILE} property
	 * if that property changed.
	 */
	protected abstract void doSave(Document doc, File file,
			Cancellable cancellable) throws Exception;

	/**
	 * Save a document to a given file. If successfully this should also fire
	 * PropertyChangeListeners for the {@link Document#PROPERTY_FILE} property
	 * if that property changed.
	 * <p>
	 * This method can be called on any thread. If it is called on the EDT then
	 * an interdeterminate cancellable progress dialog is shown that blocks the
	 * EDT while another thread calls
	 * {@link #doSave(Document, File, Cancellable)}.
	 */
	public void save(final Document doc, final File file) throws Exception {
		final BasicCancellable cancellable = new BasicCancellable();
		if (!SwingUtilities.isEventDispatchThread()) {
			doSave(doc, file, cancellable);
			return;
		}

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

		final QDialog qd = new QDialog(controls.getFrame(), "Saving \"" + s
				+ "\"", QDialog.getIcon(QDialog.PLAIN_MESSAGE), content,
				DialogFooter.createDialogFooter(DialogFooter.CANCEL_OPTION,
						EscapeKeyBehavior.DOES_NOTHING), false); // closeable

		final BasicConsumer<Exception> saveThrowables = new BasicConsumer<>();

		ActionListener hideListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				qd.setVisible(false);
			}

		};
		cancellable.addCancelListener(hideListener);
		cancellable.addFinishListener(hideListener);

		Thread thread = new Thread("Saving \"" + s + "\"") {
			public void run() {
				// Users don't like it when a dialog flashes for 1/100th of a
				// second. We'll force this dialog to be visible for at least
				// one second.

				// This also resolves a peculiar bug with the dialog sometimes
				// persisting after the save operation finishes when the save is
				// very fast.

				long time = System.currentTimeMillis();
				try {
					doSave(doc, file, cancellable);
					pause(time, 1000);
					cancellable.finish();
				} catch (Exception t) {
					pause(time, 1000);
					cancellable.cancel();
					saveThrowables.accept(t);
				}
			}

			private void pause(long startTime, long millis) {
				long target = startTime + millis;
				while (true) {
					long remaining = target - System.currentTimeMillis();
					if (remaining > 0) {
						try {
							Thread.sleep(remaining);
						} catch (InterruptedException e) {
							Thread.yield();
						}
					} else {
						return;
					}
				}
			}
		};
		thread.start();
		qd.pack();
		qd.setLocationRelativeTo(null);

		// in rare cases the other thread might be so fast we're already done
		if (!cancellable.isFinished() && !cancellable.isFinished())
			qd.setVisible(true);

		/*
		 * If the save completed, then the dialog hid itself because the save
		 * thread wrapped up. Otherwise the dialog hid itself because the user
		 * intervened and insisted on canceling.
		 */
		cancellable.cancel();

		if (saveThrowables.getSize() > 0) {
			throw saveThrowables.get(0);
		}
	}
}