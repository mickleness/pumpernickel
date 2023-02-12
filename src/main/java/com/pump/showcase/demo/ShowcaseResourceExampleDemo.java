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
package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.icon.StrikeThroughIcon;
import com.pump.inspector.Inspector;
import com.pump.io.SuffixFilenameFilter;
import com.pump.swing.FileDialogUtils;

/**
 * This demo has a text field at the top of the configuration panel for a file
 * path or URL. Features include:
 * <ul>
 * <li>Whenever that text field is modified {@link #refreshFile(Object, String)}
 * is called.</li>
 * <li>A "Browse..." button for the user to select a file</li>
 * <li>The text field is stored in the user's preferences and auto-populated on
 * relaunch</li>
 * </ul>
 * 
 * @param <T>
 *            the type of resource this supports; either a File or URL.
 */
public abstract class ShowcaseResourceExampleDemo<R>
		extends ShowcaseExampleDemo {

	private static final long serialVersionUID = 1L;

	static FilenameFilter ALL_FILES = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			return true;
		}

	};

	private final String prefKeyLastResource;

	protected static Icon NO_ICON = new StrikeThroughIcon(Color.gray, 64);

	protected Inspector inspector;

	protected final JButton fileDialogButton = new JButton("Browse...");
	protected final JTextField resourcePathField;
	protected final String[] fileExtensions;
	protected final Class<R> resourceType;

	protected final Preferences prefs;

	/**
	 * @param fileExtensions
	 *            if null or empty then all files are accepted
	 */
	public ShowcaseResourceExampleDemo(Class<R> resourceType,
			boolean stretchExampleToFillWindow, String... fileExtensions) {
		super(stretchExampleToFillWindow, stretchExampleToFillWindow, true,
				!stretchExampleToFillWindow, false);
		this.fileExtensions = fileExtensions;
		this.resourceType = resourceType;

		fileDialogButton.setOpaque(false);

		if (resourceType.equals(File.class)) {
			resourcePathField = new JTextField(40);
		} else if (resourceType.equals(URL.class)) {
			resourcePathField = new JTextField(40);
		} else {
			throw new IllegalArgumentException(
					"The resource type must be File or URL.");
		}

		prefKeyLastResource = getClass().getSimpleName() + "#lastResource";

		inspector = new Inspector(configurationPanel);
		String resourceLabel = resourceType.equals(File.class) ? "File:"
				: "URL:";
		inspector.addRow(new JLabel(resourceLabel), resourcePathField,
				fileDialogButton);

		resourcePathField.getDocument()
				.addDocumentListener(new DocumentListener() {
					boolean dirty = false;
					Runnable refreshRunnable = new Runnable() {
						public void run() {
							if (!dirty)
								return;
							dirty = false;

							refreshFile();
							String str = resourcePathField.getText();
							prefs.put(prefKeyLastResource, str);

							try {
								prefs.sync();
							} catch (BackingStoreException e1) {
								e1.printStackTrace();
							}
						}
					};

					@Override
					public void insertUpdate(DocumentEvent e) {
						changedUpdate(e);
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						changedUpdate(e);
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						dirty = true;
						SwingUtilities.invokeLater(refreshRunnable);
					}

				});

		fileDialogButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Frame frame = (Frame) SwingUtilities
						.getWindowAncestor(configurationPanel);
				FilenameFilter filter;
				if (fileExtensions.length == 0) {
					filter = ALL_FILES;
				} else {
					filter = new SuffixFilenameFilter(fileExtensions);
				}
				File file = FileDialogUtils.showOpenDialog(frame, "Select",
						filter);
				if (file != null) {
					if (resourceType.equals(File.class)) {
						resourcePathField.setText(file.getPath());
					} else {
						resourcePathField.setText(file.toURI().toString());
					}
				}
			}

		});

		prefs = Preferences.userNodeForPackage(
				ShowcaseResourceExampleDemo.this.getClass());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String lastResource = prefs.get(prefKeyLastResource, null);

				// what if we're trying to restore a reference to a file that no
				// longer exists?
				// this
				if (lastResource != null && resourceType.equals(File.class)) {
					File file = new File(lastResource);
					if (!file.exists()) {
						lastResource = null;
					}
				}

				if (lastResource != null) {
					resourcePathField.setText(lastResource);
				} else {
					setDefaultResourcePath();
				}
			}
		});
	}

	/**
	 * This populates the resourcePathField with a default value. This
	 * implementation just sets the text field to an empty string, but
	 * subclasses may override this as needed.
	 */
	protected void setDefaultResourcePath() {
		resourcePathField.setText("");
	}

	protected final void refreshFile() {
		String str = resourcePathField.getText();
		R resource = getResource();
		refreshFile(resource, str);
	}

	public R getResource() {
		String str = resourcePathField.getText();
		if (resourceType.equals(File.class)) {
			File file = new File(str);
			return (R) file;
		} else if (resourceType.equals(URL.class)) {
			try {
				URL url = new URL(str);
				return (R) url;
			} catch (MalformedURLException e) {
				return null;
			}
		}
		throw new IllegalStateException("resourceType ("
				+ resourceType.getName() + ") should be File or URL");
	}

	/**
	 * Refresh this demo to interact with a given resource.
	 * 
	 * @param resource
	 *            the resource (either a File or URL) to load.
	 * @param resourceStr
	 *            the String used to construct the first argument. This is
	 *            especially useful for URLs if the text is malformed. In that
	 *            case: the first argument (the resource) will be null.
	 */
	protected abstract void refreshFile(R resource, String resourceStr);

}