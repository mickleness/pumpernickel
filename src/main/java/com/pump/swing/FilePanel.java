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

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pump.icon.file.FileIcon;
import com.pump.io.SuffixFilenameFilter;

/**
 * A panel that helps select a file.
 * <p>
 * If launched in a JNLP, then this uses the local FileOpenService to select a
 * File. Otherwise this uses the standard FileDialog (which requires this
 * component be a descendant of Frame).
 */
public class FilePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * An abstract representation of a file.
	 * <p>
	 * This may be backed by a java.io.File object, or a FileContents object in
	 * JNLP sessions.
	 */
	public abstract static class FileData {
		/**
		 * Create an InputStream representing this file.
		 * 
		 * @return an InputStream representing this file.
		 */
		public abstract InputStream createInputStream() throws IOException;

		/** @return the name of this file. */
		public abstract String getName() throws IOException;

		/**
		 * @return the optional icon of this file (this may return null).
		 */
		public abstract Icon getIcon();
	}

	/*
	 * public static class FileContentsWrapper extends FileData {
	 * 
	 * final FileContents contents;
	 * 
	 * public FileContentsWrapper(FileContents contents) { this.contents =
	 * contents; }
	 * 
	 * @Override public InputStream createInputStream() throws IOException {
	 * return contents.getInputStream(); }
	 * 
	 * @Override public String getName() throws IOException { return
	 * contents.getName(); }
	 * 
	 * @Override public Icon getIcon() { return null; }
	 * 
	 * /** Return the FileContents this FileWrapper refers to.
	 * 
	 * @return the FileContents this FileWrapper refers to.
	 */
	/*
	 * public FileContents getFileContents() { return contents; }
	 * 
	 * }
	 */

	public static class FileWrapper extends FileData {
		final File file;

		public FileWrapper(File file) {
			this.file = file;
		}

		@Override
		public InputStream createInputStream() throws IOException {
			return new FileInputStream(file);
		}

		@Override
		public String getName() {
			return file.getName();
		}

		@Override
		public Icon getIcon() {
			return FileIcon.get().getIcon(file);
		}

		/**
		 * Return the File this FileWrapper refers to.
		 * 
		 * @return the File this FileWrapper refers to.
		 */
		public File getFile() {
			return file;
		}
	}

	/**
	 * The client property that maps to an array of Strings of acceptable file
	 * extensions.
	 */
	public static final String EXTENSIONS_KEY = FilePanel.class + ".extensions";

	/**
	 * The client property that maps to the current FileData object.
	 */
	public static final String FILE_DATA_KEY = FilePanel.class + ".fileData";

	JLabel idLabel;
	JLabel fileLabel = new JLabel();
	JButton browseButton = new JButton("Browse...");
	JPanel rightPanel = new JPanel(new GridBagLayout());

	/**
	 * Creates a FilePanel where the ID reads "File:"
	 */
	public FilePanel(String... extensions) {
		this("File:", extensions, new JComponent[] {});
	}

	/**
	 * Creates a FilePanel.
	 * 
	 * @param fileID
	 *            the text in the identifying label on the left. Usually this
	 *            will read "File:", or something similar.
	 * @param extensions
	 *            the file extensions this panel supports.
	 * @param otherComponents
	 *            additional components to put next to the "Browse..." button
	 *            (such as a throbber, or other buttons)
	 */
	public FilePanel(String fileID, String[] extensions,
			JComponent... otherComponents) {
		super(new GridBagLayout());
		idLabel = new JLabel(fileID);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(3, 3, 3, 3);
		add(idLabel, c);
		c.gridx++;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(rightPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		rightPanel.add(fileLabel, c);
		c.gridx++;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 5, 0, 0);
		rightPanel.add(browseButton, c);
		for (int a = 0; a < otherComponents.length; a++) {
			c.gridx++;
			rightPanel.add(otherComponents[a], c);
		}

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doBrowse();
			}
		});

		addPropertyChangeListener(FILE_DATA_KEY, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				FileData d = getFileData();
				String text;
				try {
					text = d == null ? "" : d.getName();
				} catch (IOException e) {
					e.printStackTrace();
					text = "Error; see console";
				}
				fileLabel.setText(text);
				fileLabel.setIcon(d == null ? null : d.getIcon());
			}

		});

		setExtensions(extensions);
	}

	/**
	 * @return the current FileData object.
	 */
	public FileData getFileData() {
		return (FileData) getClientProperty(FILE_DATA_KEY);
	}

	/**
	 * Set the file extensions accepted in file dialogs.
	 * 
	 * @param ext
	 *            may be null, in which case no file filter is applied.
	 */
	public void setExtensions(String... ext) {
		putClientProperty(EXTENSIONS_KEY, clone(ext));
	}

	/**
	 * Return the file extensions accepted in file dialogs. This array may be
	 * null if not file filter is applied.
	 */
	public String[] getExtensions() {
		return clone((String[]) getClientProperty(EXTENSIONS_KEY));
	}

	private static String[] clone(String[] array) {
		if (array == null)
			return null;

		String[] copy = new String[array.length];
		for (int a = 0; a < copy.length; a++) {
			copy[a] = array[a];
			if (copy[a] == null) {
				throw new NullPointerException("array[" + a + "]=null");
			}
		}
		return copy;
	}

	/**
	 * Show a browse dialog, using the current file extensions if they are
	 * defined.
	 * <p>
	 * In a JNLP session this uses a FileOpenService, but otherwise this uses an
	 * AWT file dialog.
	 */
	public synchronized void doBrowse() {
		FileData fd = doBrowse(browseButton, getExtensions());
		if (fd instanceof FileWrapper) {
			setFile(((FileWrapper) fd).file);
		} else if (fd != null) {
			putClientProperty(FILE_DATA_KEY, fd);
		}
	}

	/**
	 * Show a browse dialog, using the current file extensions if they are
	 * defined.
	 * <p>
	 * In a JNLP session this uses a FileOpenService, but otherwise this uses an
	 * AWT file dialog.
	 *
	 * @param component
	 *            a component that relates to the Frame the possible dialog may
	 *            be anchored to. This can be any component that is showing.
	 * @param extensions
	 *            a list of possible extensions (or null if all files are
	 *            accepted).
	 * @return the FileData the user selected, or null if the user cancelled
	 *         this operation.
	 */
	public static FileData doBrowse(Component component, String[] extensions) {
		/*
		 * if(JVM.isJNLP()) { try { FileOpenService fos =
		 * (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
		 * final FileContents contents = fos.openFileDialog(null, extensions);
		 * if(contents==null) return null; return new
		 * FileContentsWrapper(contents); } catch(Exception e) {
		 * e.printStackTrace(); } }
		 */
		Window w = SwingUtilities.getWindowAncestor(component);
		if (w instanceof Frame) {
			Frame f = (Frame) w;
			FileDialog fd = new FileDialog(f);
			String[] ext = extensions;
			if (ext != null && ext.length > 0) {
				fd.setFilenameFilter(new SuffixFilenameFilter(ext));
			}
			fd.pack();
			fd.setLocationRelativeTo(null);
			fd.setVisible(true);

			if (fd.getFile() == null)
				return null;

			File file = new File(fd.getDirectory() + fd.getFile());
			return new FileWrapper(file);
		} else {
			throw new RuntimeException("window ancestor: " + w);
		}
	}

	/**
	 * Assign a File to this FilePanel.
	 * 
	 */
	public void setFile(File file) {
		putClientProperty(FILE_DATA_KEY, new FileWrapper(file));
	}

	/**
	 * Return the right panel, containing the file label, text field, and browse
	 * button.
	 */
	public JPanel getRightPanel() {
		return rightPanel;
	}

	/**
	 * Return the identifying label on the left that labels this file. This
	 * should contain text like "File:" or "Source:", but is not going to
	 * mention the file name.
	 */
	public JLabel getIDLabel() {
		return idLabel;
	}

	/**
	 * Return the label representing the currently selected file. This will be
	 * visible if <code>isEditable()</code> returns false.
	 */
	public JLabel getFileLabel() {
		return fileLabel;
	}

	/**
	 * Returns the "Browse..." button. This will be enabled if it is attached to
	 * a Frame.
	 */
	public JButton getBrowseButton() {
		return browseButton;
	}
}