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
package com.pump.awt.dnd;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JLabel;

import com.pump.icon.file.FileIcon;

public class FileLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	/**
	 * @param file
	 *            a file this label should represent.
	 * @param actions
	 *            one of the DnDConstants.ACTION_X constants
	 */
	public FileLabel(File file, int actions) {
		this(actions);
		setFile(file);
	}

	/**
	 * 
	 * @param actions
	 *            one of the DnDConstants.ACTION_X constants. A good default is
	 *            COPY_OR_MOVE.
	 */
	public FileLabel(int actions) {
		addPropertyChangeListener(DnDUtils.KEY_FILE,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						File file = getFile();
						if (file == null) {
							setIcon(null);
							setText("");
						} else {
							setIcon(FileIcon.get().getIcon(file));
							setText(file.getName());
						}
					}

				});
		DnDUtils.setupFileDragSource(this, actions);
	}

	public File getFile() {
		return (File) getClientProperty(DnDUtils.KEY_FILE);
	}

	public void setFile(File file) {
		putClientProperty(DnDUtils.KEY_FILE, file);
	}
}