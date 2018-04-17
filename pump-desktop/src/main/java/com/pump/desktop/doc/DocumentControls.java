package com.pump.desktop.doc;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.swing.RecentMenu;
import com.pump.swing.RecentMenu.Listener;
import com.pump.util.ObservableList;

public class DocumentControls {

	/**
	 * This client property of the JRootPane should map to a java.io.File. This
	 * is how this object identifies the File that we're using, and on Macs
	 * using this key correctly updates the titlebar of frames.
	 * 
	 * @see <a
	 *      href="https://developer.apple.com/library/mac/technotes/tn2007/tn2196.html#WINDOW_DOCUMENTFILE">Apple
	 *      Tech Note 2196</a>
	 */
	public static final String PROPERTY_DOCUMENT_FILE = "Window.documentFile";

	/**
	 * This client property of the JRootPane should map to a Boolean. This is
	 * used on Macs to correctly render the title bar.
	 * 
	 * @see <a
	 *      href="https://developer.apple.com/library/content/technotes/tn2007/tn2196.html#WINDOW_DOCUMENTMODIFIED">Apple
	 *      Tech Note 2196</a>
	 */
	public static final String PROPERTY_DOCUMENT_MODIFIED = "Window.documentModified";

	protected final ObservableList<Document> openDocuments = new ObservableList<>();
	Document selectedDocument = null;
	Map<String, AbstractAction> actionMap = new HashMap<>();

	private ChangeListener validationChangeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			validate(openDocuments.toArray(new Document[openDocuments.size()]));
		}
	};

	private ChangeListener updateSelectedDocumentChangeListener = new ChangeListener() {

		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshFrameProperties();
			}

		};

		Document lastSelectedDoc;

		@Override
		public void stateChanged(ChangeEvent e) {
			if (lastSelectedDoc != null) {
				lastSelectedDoc
						.removePropertyChangeListener(propertyChangeListener);
			}
			Document doc = getSelectedDocument();
			if (doc != null) {
				doc.addPropertyChangeListener(propertyChangeListener);
			}
			lastSelectedDoc = doc;
			refreshFrameProperties();
		}
	};

	public void registerAction(AbstractAction action) {
		String cmd = (String) action
				.getValue(AbstractAction.ACTION_COMMAND_KEY);
		AbstractAction oldValue = actionMap.put(cmd, action);
		if (oldValue != null)
			throw new IllegalStateException(
					"Multiple actions registered for \"" + cmd + "\"");
	}

	/**
	 * Refresh the {@link PROPERTY_DOCUMENT_FILE} and
	 * {@link PROPERTY_DOCUMENT_MODIFIED}.
	 */
	protected void refreshFrameProperties() {
		File file;
		Boolean dirty;
		Document doc = getSelectedDocument();
		if (doc == null) {
			file = null;
			dirty = Boolean.FALSE;
		} else {
			file = doc.getFile();
			dirty = doc.isDirty();
		}
		frame.getRootPane().putClientProperty(PROPERTY_DOCUMENT_FILE, file);
		frame.getRootPane()
				.putClientProperty(PROPERTY_DOCUMENT_MODIFIED, dirty);
	}

	private int minimumDocumentCount;
	private int maximumDocumentCount;

	protected final RecentMenu recentMenu;
	private List<ChangeListener> changeListeners = new ArrayList<>();
	private JFrame frame;

	public DocumentControls(JFrame frame, int minimumDocumentCount,
			int maximumDocumentCount, Document... documents) {
		Objects.requireNonNull(frame);
		if (minimumDocumentCount < 0)
			throw new IllegalArgumentException("minimumDocumentCount = "
					+ minimumDocumentCount);
		if (maximumDocumentCount < minimumDocumentCount)
			throw new IllegalArgumentException("minimumDocumentCount = "
					+ minimumDocumentCount + ", maximumDocumentCount = "
					+ maximumDocumentCount);
		for (Document d : documents)
			if (d == null)
				throw new NullPointerException();

		this.minimumDocumentCount = minimumDocumentCount;
		this.maximumDocumentCount = maximumDocumentCount;
		this.frame = frame;

		Document newSelectedDocument = documents.length == 0 ? null
				: documents[documents.length - 1];
		setDocuments(newSelectedDocument, documents);

		addChangeListener(validationChangeListener);
		openDocuments.addSynchronizedChangeListener(validationChangeListener);
		addChangeListener(updateSelectedDocumentChangeListener);

		validate(openDocuments.toArray(new Document[openDocuments.size()]));

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				tryToCloseWindow();
			}
		});
		recentMenu = new RecentMenu.Preference(true, frame.getClass());

		recentMenu.addListener(new Listener() {
			@Override
			public void fileSelected(File file) {
				try {
					getAction(DocumentCommand.OPEN).openFile(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// constantly update the RecentMenu as new files are opened/resaved
		getOpenDocuments().addSynchronizedChangeListener(new ChangeListener() {
			PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (Document.PROPERTY_FILE.equals(evt.getPropertyName())) {
						File file = ((Document) evt.getSource()).getFile();
						if (file != null)
							getRecentMenu().addFile(file);
					}
				}

			};
			Collection<Document> knownDocs = new HashSet<>();

			@Override
			public void stateChanged(ChangeEvent e) {
				for (Document doc : getOpenDocuments()) {
					if (knownDocs.add(doc)) {
						doc.addPropertyChangeListener(propertyChangeListener);
						File file = doc.getFile();
						if (file != null)
							getRecentMenu().addFile(file);
					}
				}
				Iterator<Document> iter = knownDocs.iterator();
				while (iter.hasNext()) {
					Document doc = iter.next();
					if (!getOpenDocuments().contains(doc)) {
						iter.remove();
						doc.removePropertyChangeListener(propertyChangeListener);
					}
				}
			}

		});

		refreshFrameProperties();
	}

	/**
	 * This is consulted every time the list of open Documents is modified. This
	 * may throw a MultipleDocumentsException or a ZeroDocumentsException, or
	 * any exception subclasses may dream up.
	 */
	protected void validate(Document[] documents) {
		for (int a = 0; a < documents.length; a++) {
			if (documents[a] == null)
				throw new NullPointerException("i = " + a);
		}
		Document selectedDocument = getSelectedDocument();
		if (selectedDocument != null) {
			int i = Arrays.asList(documents).indexOf(selectedDocument);
			if (i == -1)
				throw new MissingSelectedDocumentException(selectedDocument,
						documents);
		}
		if (openDocuments.size() > getMaximumDocumentCount()
				|| openDocuments.size() < getMinimumDocumentCount())
			throw new DocumentLimitException(openDocuments.size(),
					getMinimumDocumentCount(), getMaximumDocumentCount());
	}

	public int getMaximumDocumentCount() {
		return maximumDocumentCount;
	}

	public int getMinimumDocumentCount() {
		return minimumDocumentCount;
	}

	public synchronized Document getSelectedDocument() {
		return selectedDocument;
	}

	public synchronized void setSelectedDocument(Document newSelectedDocument) {
		if (!Objects.equals(selectedDocument, newSelectedDocument)) {
			selectedDocument = newSelectedDocument;
			fireChangeListeners();
		}
	}

	protected void fireChangeListeners() {
		ChangeListener[] listenerArray;
		synchronized (changeListeners) {
			listenerArray = this.changeListeners
					.toArray(new ChangeListener[changeListeners.size()]);
		}
		for (ChangeListener l : listenerArray) {
			try {
				l.stateChanged(new ChangeEvent(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addChangeListener(ChangeListener l) {
		synchronized (changeListeners) {
			changeListeners.add(l);
		}
	}

	public void removeChangeListener(ChangeListener l) {
		synchronized (changeListeners) {
			changeListeners.remove(l);
		}
	}

	public synchronized ObservableList<Document> getOpenDocuments() {
		return openDocuments;
	}

	public Frame getFrame() {
		return frame;
	}

	public RecentMenu getRecentMenu() {
		return recentMenu;
	}

	/**
	 * This sets both the current selected document and the list of open
	 * documents at the same time.
	 * <p>
	 * If you call <code>setSelectedDocument(..)</code> and then
	 * <code>getOpenDocuments().setAll(..)</code> then one call will trigger
	 * listeners before the other call is complete. This could lead to funky
	 * results (for example: the selected document may not exist in the list of
	 * currently open documents). This method will not fire any listeners until
	 * <i>both</i> the selected document and the list of currently open
	 * documents has been updated.
	 * 
	 * @param newSelectedDocument
	 * @param openDocuments
	 */
	public synchronized void setDocuments(Document newSelectedDocument,
			Document[] newOpenDocuments) {
		if (newSelectedDocument != null) {
			if (!Arrays.asList(openDocuments).contains(newSelectedDocument))
				throw new IllegalArgumentException("The selected document \""
						+ newSelectedDocument.getName()
						+ "\" is not one of the open documents provided.");
		}

		boolean fireChangeListeners = false;
		if (!Objects.equals(selectedDocument, newSelectedDocument)) {
			selectedDocument = newSelectedDocument;
			fireChangeListeners = true;
		}
		getOpenDocuments().setAll(newOpenDocuments);
		if (fireChangeListeners) {
			fireChangeListeners();
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractAction> T getAction(DocumentCommand<T> command) {
		return (T) actionMap.get(command
				.getValue(AbstractAction.ACTION_COMMAND_KEY));

	}

	/**
	 * This indicates whether this application allows more than 1 document per
	 * window.
	 * 
	 * @return true if <code>getMaximumDocumentCount()==1</code>.
	 */
	public boolean isSingleDocumentInterface() {
		return getMaximumDocumentCount() == 1;
	}

	/**
	 * This method is called when the user tries to close the JFrame these
	 * controls relate to.
	 * <p>
	 * The default implementation here walks through every document. If it is
	 * dirty, the CloseDocumentAction is used to walk the user through saving
	 * (or not).
	 * <p>
	 * This method may throw a UserCancelledException, but if it finishes
	 * successfully it should call getFrame().setVisible(false) at the end.
	 */
	public void tryToCloseWindow() {
		// TODO: implement special behavior if you have N-many dirty documents
		// ("Save All"? "Review All"? There are lots of UX patterns to choose
		// from...)

		Document t = getSelectedDocument();
		while (t != null) {
			getAction(DocumentCommand.CLOSE).prepareToClose(t);
			if (isSingleDocumentInterface()) {
				frame.setVisible(false);
				return;
			} else {
				if (getOpenDocuments().size() == 1) {
					setDocuments(null, new Document[] {});
				} else {
					List<Document> l = new ArrayList<>(getOpenDocuments());
					int i = l.indexOf(t);
					l.remove(t);
					i = Math.min(i, l.size() - 1);
					Document newSelectedDocument = l.get(i);
					setDocuments(newSelectedDocument,
							l.toArray(new Document[l.size()]));
				}

				t = getSelectedDocument();
			}
		}

		getFrame().setVisible(false);
	}
}