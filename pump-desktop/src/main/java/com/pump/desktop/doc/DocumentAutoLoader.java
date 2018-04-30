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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This saves a list of open documents and re-loads those documents in new
 * sessions.
 * <p>
 * The default implementation relies on a {@link OpenDocumentAction}, so that
 * needs to be added to a DocumentControls before this object is instantiated.
 */
public class DocumentAutoLoader {
	private static final String PREFERENCE_OPEN_DOC_PREFIX = "openDoc";
	private static final String PREFERENCE_ACTIVE = "active";

	protected final Preferences prefs;
	protected final DocumentControls controls;

	protected ChangeListener autoLoadChangeListener = new ChangeListener() {

		public void stateChanged(ChangeEvent e) {
			updatePreferences();
		}
	};

	public DocumentAutoLoader(DocumentControls controls) {
		prefs = Preferences.userNodeForPackage(getClass());
		this.controls = controls;
		loadDocumentsFromPreviousSession();
	}

	/**
	 * This is called once during the constructor to consult the preferences and
	 * load all documents from the previous session.
	 */
	protected void loadDocumentsFromPreviousSession() {
		if (!isActive())
			return;

		controls.getOpenDocuments().removeSynchronizedChangeListener(
				autoLoadChangeListener);
		try {
			int ctr = 0;
			List<Document> runningList = new ArrayList<>(
					controls.getOpenDocuments());
			while (true) {
				String value = prefs.get(PREFERENCE_OPEN_DOC_PREFIX + (ctr++),
						null);
				if (value == null)
					return;
				Document doc = deserialize(value);
				if (doc != null) {
					runningList.add(doc);
					controls.setDocuments(doc, runningList
							.toArray(new Document[runningList.size()]));
				}
			}
		} finally {
			controls.getOpenDocuments().addSynchronizedChangeListener(
					autoLoadChangeListener);
		}
	}

	/**
	 * Toggle the preference controlling whether this feature is active.
	 */
	public void setActive(boolean newActive) {
		prefs.putBoolean(PREFERENCE_ACTIVE, newActive);
		if (!updatePreferences()) {
			try {
				prefs.flush();
			} catch (BackingStoreException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	/**
	 * Return true if this feature is active. By default this returns true until
	 * you've explicitly turned off this feature. (You can also just not
	 * construct this object at all if you don't like this feature.)
	 */
	public boolean isActive() {
		return prefs.getBoolean(PREFERENCE_ACTIVE, true);
	}

	/**
	 * Convert a Document into a String. The default implementation just
	 * converts the Document's File into a file path.
	 * <p>
	 * If this returns null (which it will do for Documents that don't have a
	 * File) then nothing is saved.
	 */
	protected String serializeDocument(Document doc) {
		File file = doc.getFile();
		String path = file == null ? null : file.getAbsolutePath();
		return path;
	}

	/**
	 * Convert a String created by {@link #serializeDocument(Document)} back
	 * into a Document.
	 * <p>
	 * Presumably the original String was created in a previous session/JVM, so
	 * this needs to be something that makes sense across sessions.
	 * <p>
	 * The default implementation assumes the String is a filepath. If that file
	 * no longer exists, then this returns null.
	 * 
	 * @return a Document from the serialized representation, or null if the
	 *         argument isn't recognizable or usable.
	 */
	protected Document deserialize(String value) {
		File file = new File(value);
		if (file.exists()) {
			for (Document doc : controls.getOpenDocuments()) {
				if (file.equals(doc.getFile())) {
					return null;
				}
			}
			try {
				Document doc = controls.getAction(DocumentCommand.OPEN)
						.createDocument(file);
				return doc;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/**
	 * Save all the current open documents to the preferences and flush the
	 * preferences.
	 * 
	 * @return true if flush was needed and called, false otherwise.
	 */
	protected boolean updatePreferences() {
		int prefCtr = 0;
		boolean resave = false;
		if (isActive()) {
			for (int a = 0; a < controls.getOpenDocuments().size(); a++) {
				Document d = controls.getOpenDocuments().get(a);
				String value = serializeDocument(d);
				if (value != null) {
					resave = setPreference(PREFERENCE_OPEN_DOC_PREFIX
							+ (prefCtr++), value)
							| resave;
				}
			}
		}
		resave = setPreference(PREFERENCE_OPEN_DOC_PREFIX + (prefCtr++), null)
				| resave;
		if (resave) {
			try {
				prefs.flush();
			} catch (BackingStoreException e1) {
				throw new RuntimeException(e1);
			}
		}
		return resave;
	}

	/**
	 * Set a preference.
	 * 
	 * @return false if no change was needed/made, true otherwise.
	 */
	private boolean setPreference(String key, String value) {
		Object oldValue = prefs.get(key, null);
		if (!Objects.equals(oldValue, value)) {
			if (value == null) {
				prefs.remove(key);
			} else {
				prefs.put(key, value);
			}
			return true;
		}

		return false;
	}
}