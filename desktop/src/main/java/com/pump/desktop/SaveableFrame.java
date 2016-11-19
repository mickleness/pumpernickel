package com.pump.desktop;

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.pump.UserCancelledException;
import com.pump.data.Key;
import com.pump.io.SuffixFilenameFilter;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.swing.DialogFooter;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.swing.QDialog;
import com.pump.swing.RecentMenu;
import com.pump.swing.RecentMenu.Listener;
import com.pump.util.BasicReceiver;
import com.pump.util.JVM;
import com.pump.window.WindowList;


/** This frame is associated with a file and automatically prompts the
 * user to save when the window is being closed.
 *
 */
public abstract class SaveableFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/** This client property of the JRootPane should map to a java.io.File.
	 * This is how this object identifies the File that we're using, and on
	 * Macs using this key correctly updates the titlebar of frames.
	 * @see <a href="https://developer.apple.com/library/mac/technotes/tn2007/tn2196.html#WINDOW_DOCUMENTFILE">Apple Tech Note 2196</a>
	 */
	public static final String FILE_KEY = "Window.documentFile";

	public static final Key<Rectangle> KEY_BOUNDS = new Key<Rectangle>(Rectangle.class, "frame-bounds");
	
	protected AbstractAction openAction = new AbstractAction("Open...") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				openFile();
			} catch(Exception e2) {
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
				if(file==null) {
					doSaveAs();
				} else {
					saveFile(file, null);
				}
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	};

	protected AbstractAction exitAction = new AbstractAction( JVM.isMac ? "Quit" : "Exit") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Window[] window = WindowList.getWindows(true, false);
			for(int a = window.length-1; a>=0; a--) {
				if(window[a] instanceof SaveableFrame) {
					if( !((SaveableFrame)window[a]).tryToClose() )
						return;
				} else {
					window[a].setVisible(false);
				}
			}
		}
	};
	protected JMenuItem openItem = new JMenuItem(openAction);
	protected JMenuItem saveItem = new JMenuItem(saveAction);
	protected JMenuItem exitItem = new JMenuItem(exitAction);
	protected RecentMenu recentMenu = new RecentMenu.Preference(true, getClass());
	protected Preferences prefs = Preferences.userNodeForPackage(getClass());
	
	private boolean shownYet = false;
	private boolean saveBoundsInPrefs;
	
	public SaveableFrame(boolean restoreBounds) {
		this("", restoreBounds);
	}
	
	@Override
	public void pack() {
		if(saveBoundsInPrefs && (!shownYet) ) {
			Rectangle bounds = KEY_BOUNDS.get(prefs, null);
			if(bounds!=null) {
				setBounds(bounds);
				return;
			}
		}
		super.pack();
	}
	
	public SaveableFrame(String title,boolean restoreBounds) {
		super(title);
		saveBoundsInPrefs = restoreBounds;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				tryToClose();
			}
			
			@Override
			public void windowOpened(WindowEvent e) {
				shownYet = true;
			}
		});
		openItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		char exitChar = JVM.isMac ? 'Q' : KeyEvent.VK_F4;
		exitItem.setAccelerator(KeyStroke.getKeyStroke(exitChar, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		recentMenu.addListener(new Listener() {
			@Override
			public void fileSelected(File file) {
				try {
					openFile(file);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				saveWindowLocation();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				saveWindowLocation();
			}
			
			private void saveWindowLocation() {
				if(saveBoundsInPrefs)
					KEY_BOUNDS.set(prefs, SaveableFrame.this.getBounds());
			}
			
		});
	}
	
	/** This should be called on the EDT
	 * 
	 * @return the File that was saved.
	 */
	protected File doSaveAs() throws UserCancelledException, Exception {
		File defaultFile = getFile();
		String ext = getDocumentFileExtension();
		File file = showFileDialog(FileDialog.SAVE, "Save As", defaultFile, ext);
		showSaveDialogAndDoSave(file);
		setFile(file);
		return file;
	}
	
	/** Show a FileDialog.
	 * 
	 * @param dialogMode must be FileDialog.LOAD or FileDialog.SAVE
	 * @param dialogTitle the title of this dialog.
	 * @param defaultFile an optional file to initially select.
	 * @param documentFileExtension an optional extension to apply, or array of
	 * extensions to accept.
	 * This is used both as a file filter for this dialog and the zeroeth element is manually
	 * appended to the filename if necessary during a save. It is OK to leave this null/empty for
	 * an open dialog, but it is highly discouraged to leave this null for
	 * a save dialog.
	 * @return the File the user chose.
	 * @throws UserCancelledException if the user cancelled this dialog.
	 */
	protected File showFileDialog(int dialogMode, String dialogTitle, File defaultFile,
			String... documentFileExtension) throws UserCancelledException {
		if(!(dialogMode==FileDialog.LOAD || dialogMode==FileDialog.SAVE))
			throw new IllegalArgumentException("dialogMode must be LOAD or SAVE");
		FileDialog fd = new FileDialog(this, dialogTitle, dialogMode);
		SuffixFilenameFilter filter = null;
		if(documentFileExtension!=null && documentFileExtension.length>0) {
			filter = new SuffixFilenameFilter(documentFileExtension);
			fd.setFilenameFilter(filter);
		}
		if(defaultFile!=null) {
			fd.setFile(defaultFile.getAbsolutePath());
		}
		fd.pack();
		fd.setLocationRelativeTo(null);
		fd.setVisible(true);
		if(fd.getFile()==null) throw new UserCancelledException();
		String s = fd.getFile();
		//this is going to cause problems on a sandboxed Mac:
		if(filter!=null && (!filter.accept( new File(fd.getDirectory(), s)))) {
			//TODO: if we change the file path (even a little), we need to
			//run a new (separate) check to see if the file exists and offer
			//a new do-you-want-to-replace dialog
			s = s+"."+documentFileExtension[0];
		}
		File file = new File(fd.getDirectory()+s);
		return file;
		
	}

	/** This is called on the EDT to save to a File. This pulls up an indeterminate progress
	 * bar dialog with a cancel button while the save operation is in progress.
	 */
	protected void showSaveDialogAndDoSave(final File file) throws Exception {
		final Cancellable cancellable = new BasicCancellable();
		String s = file.getName();

		JPanel content = new JPanel(new GridBagLayout());
		JProgressBar pb = new JProgressBar();
		pb.setIndeterminate(true);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.weighty = 0;
		c.insets = new Insets(4,4,4,4); c.fill = GridBagConstraints.HORIZONTAL;
		content.add(new JLabel("Saving document to \""+file.getName()+"\"..."), c);
		c.gridy++;
		content.add(pb, c);
		
		final QDialog qd = new QDialog(this, 
				"Saving \""+s+"\"", 
				QDialog.getIcon(QDialog.PLAIN_MESSAGE),
				content, 
				DialogFooter.createDialogFooter(DialogFooter.CANCEL_OPTION,EscapeKeyBehavior.DOES_NOTHING),
				false); //closeable
		
		final BasicReceiver<Exception> saveThrowables = new BasicReceiver<Exception>();
		
		Thread thread = new Thread("Saving \""+s+"\"") {
			public void run() {
				try {
					saveFile(file, cancellable);
				} catch(Exception t) {
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
		
		/* If the save completed, then the dialog hid itself because the save
		 * thread wrapped up. Otherwise the dialog hid itself because the user
		 * intervened and insisted on canceling.
		 */
		cancellable.cancel();
		
		if(saveThrowables.getSize()>0) {
			throw saveThrowables.getElementAt(0);
		}
	}
	
	/** Return the document file extension for files this SaveableFrame represents.
	 * For example "jpg" or "txt".
	 * @return the document file extension for files this SaveableFrame represents.
	 */
	public abstract String getDocumentFileExtension();


	public void openFile()
			throws UserCancelledException, Exception {

		if(isDocumentDirty()) {
			File oldFile = getFile();
			String docName = oldFile==null ? null : oldFile.getName();
			int choice = QDialog.showSaveChangesDialog(
					SaveableFrame.this,
					docName,
					true);
			if(choice==DialogFooter.DONT_SAVE_OPTION) {
				//do nothing
			} else if(choice==DialogFooter.CANCEL_OPTION) {
				throw new UserCancelledException();
			} else if(choice==DialogFooter.SAVE_OPTION) {
				try {
					if(oldFile==null) {
						oldFile = doSaveAs();
						//this will quickly change, but for the moment:
						setFile(oldFile);
					} else {
						showSaveDialogAndDoSave(oldFile);
					}
				} catch(Exception e2) {
					e2.printStackTrace();
					//erg, don't replace our file if a problem came up saving:
					return;
				}
			}
		}
		
		File file = this.showFileDialog(FileDialog.LOAD, "Open File...", null, getDocumentFileExtension());
		if(file==null) throw new UserCancelledException();
		openFile(file);
		recentMenu.addFile(file);
		setFile(file);
	}
	
	protected abstract void openFile(File file) throws UserCancelledException, Exception;
	
	public File getFile() {
		return (File)getRootPane().getClientProperty(FILE_KEY);
	}
	
	public void setFile(File f) {
		getRootPane().putClientProperty(FILE_KEY, f);
	}
	
	/** Returns whether this window contains unsaved changes. This may
	 * be called on or off the EDT, so it needs to be fast and
	 * thread-safe.
	 * 
	 * @return whether this window contains unsaved changes.
	 */
	public abstract boolean isDocumentDirty();
	
	/** Save the document this window represents to the argument provided.
	 * This should never be called on the EDT, because saving may take
	 * an undefined amount of time.
	 * @param f the file to save to. This should never be null, because
	 * that would require prompting the user for a save-as dialog
	 * which needs to be handled on the EDT (and this call needs to be
	 * off the EDT). This argument may not always be the same as 
	 * <code>this.getFile()</code>. For example: calling <code>saveFile(newFile)</code>
	 * can be used to implement a "Save As..." function. Note this method
	 * does not replace this SaveableFrame's file with the argument.
	 * @param cancellable an optional argument. If non-null then this method needs
	 * to constantly poll this object to see if the user indicated they wanted to cancel
	 * this operation. In that case this method should wrap up as cleanly as possible and
	 * throw a UserCancelledException
	 * @throws UserCancelledException if the user cancelled this operation
	 * @throws Exception if an unknown problem came up saving.
	 * 
	 */
	public abstract void saveFile(File f,Cancellable cancellable) throws UserCancelledException,
		Exception;

	/**
	 * If true then the user saved their changes as this method was called.
	 * If false then the user did not choose to save anything, or there was nothing
	 * to save.
	 * This may also throw a {@code UserCancelledException}
	 * 
	 * @return
	 */
	public boolean tryToClose() throws UserCancelledException {
		if(!isDocumentDirty()) {
			setVisible(false);
			return false;
		}
		
		File file = getFile();
		String docName = file==null ? null : file.getName();
		int choice = QDialog.showSaveChangesDialog(
				SaveableFrame.this,
				docName,
				true);
		if(choice==DialogFooter.DONT_SAVE_OPTION) {
			setVisible(false);
			return false;
		} else if(choice==DialogFooter.CANCEL_OPTION) {
			throw new UserCancelledException();
		}
		
		try {
			if(file==null) {
				file = doSaveAs();
				setFile(file);
			} else {
				showSaveDialogAndDoSave(file);
			}
			setVisible(false);
		} catch(Exception e2) {
			e2.printStackTrace();
		}
		return true;
	}
}
