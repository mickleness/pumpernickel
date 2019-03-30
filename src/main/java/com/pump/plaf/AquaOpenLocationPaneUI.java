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
package com.pump.plaf;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.pump.io.location.FileLocation;
import com.pump.io.location.IOLocation;
import com.pump.io.location.LocationFactory;
import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;
import com.pump.swing.io.LocationPane;
import com.pump.util.CommonFiles;

public class AquaOpenLocationPaneUI extends OpenLocationPaneUI {
	public static final String KEY_INCLUDE_SIDEBAR = AquaOpenLocationPaneUI.class
			.getName() + "#includeSidebar";
	public static final String KEY_INCLUDE_FOOTER = AquaOpenLocationPaneUI.class
			.getName() + "#includeFooter";

	protected JSplitPane splitPane;
	protected final AquaLocationSourceList sourceList;
	protected final AquaLocationPaneControls controls;
	protected final DialogFooter footer;
	protected final JScrollPane sourceListScrollPane;

	PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			installGUI(locationPane);
		}

	};

	public AquaOpenLocationPaneUI(LocationPane p) {
		super(p);
		sourceList = new AquaLocationSourceList(p.getLocationHistory(),
				p.getGraphicCache());
		sourceListScrollPane = new JScrollPane(sourceList);
		controls = new AquaLocationPaneControls(this);
		browser.setPreferredSize(new Dimension(200, 200));

		commitButton.setText("Open");
		cancelButton.setText("Cancel");

		footer = new DialogFooter(new JComponent[] { newFolderButton },
				new JComponent[] { commitButton, cancelButton }, false,
				commitButton);

		newFolderButton.setText("New Folder");
		newFolderButton.setVisible(false);
	}

	@Override
	public IOLocation getDefaultDirectory() {
		// this is our preferred option:
		File documents = new File(System.getProperty("user.home")
				+ "/Documents");
		if (documents.exists())
			return LocationFactory.get().create(documents);

		// but it's possible either Documents doesn't exist, either because
		// this Mac is weird or this UI was constructed on a non-Mac:
		File home = new File(System.getProperty("user.home"));
		return LocationFactory.get().create(home);
	}

	protected static File[] combine(File[] array1, File[] array2) {
		File[] sum = new File[array1.length + array2.length];
		System.arraycopy(array1, 0, sum, 0, array1.length);
		System.arraycopy(array2, 0, sum, array1.length, array2.length);
		return sum;
	}

	@Override
	protected void installGUI(JComponent panel) {
		panel.addPropertyChangeListener(KEY_INCLUDE_SIDEBAR,
				propertyChangeListener);

		if (sourceList.isEmpty()) {
			File[] array1 = CommonFiles.getUserDirectories(true);
			IOLocation[] array2 = new FileLocation[array1.length];
			for (int a = 0; a < array1.length; a++) {
				array2[a] = LocationFactory.get().create(array1[a]);
			}
			sourceList.add(array2);
		}

		boolean includeSidebar = getBoolean(locationPane, KEY_INCLUDE_SIDEBAR,
				true);
		boolean includeFooter = getBoolean(locationPane, KEY_INCLUDE_FOOTER,
				true);

		panel.removeAll();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 0, 4, 0);
		panel.add(controls, c);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;

		if (includeSidebar) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					sourceListScrollPane, browser);
			panel.add(splitPane, c);
		} else {
			panel.add(browser, c);
		}

		if (includeFooter) {
			c.weighty = 0;
			c.gridy++;
			panel.add(footer, c);
		}
		sourceListScrollPane.setMinimumSize(new Dimension(100, 40));
		sourceListScrollPane.setPreferredSize(new Dimension(150, 40));
	}

	private boolean getBoolean(JComponent jc, String keyName,
			boolean defaultValue) {
		Boolean b = (Boolean) jc.getClientProperty(keyName);
		if (b == null)
			return defaultValue;
		return b.booleanValue();
	}

	@Override
	public void uninstallUI(JComponent c) {
		c.removePropertyChangeListener(KEY_INCLUDE_SIDEBAR,
				propertyChangeListener);
		super.uninstallUI(c);
	}

	@Override
	protected String getNewFolderName() {
		Frame frame = null;
		Window parent = SwingUtilities.getWindowAncestor(locationPane);
		while (frame == null && parent != null) {
			if (parent instanceof Frame) {
				frame = (Frame) parent;
			}
			parent = parent.getOwner();
		}
		JButton create = DialogFooter.createOKButton();
		create.setText("Create");
		JButton cancel = DialogFooter.createCancelButton(true);

		JTextField textField = new JTextField("untitled folder");

		JPanel content = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 3, 3, 3);
		content.add(new JLabel("Name of new folder:"), c);
		c.gridy++;
		content.add(textField, c);

		// just to give some extra width:
		JPanel fluff = new JPanel();
		fluff.setPreferredSize(new Dimension(50, 5));
		fluff.setOpaque(false);

		DialogFooter footer = new DialogFooter(new JComponent[] { fluff },
				new JComponent[] { create, cancel }, true, create);
		QDialog dialog = new QDialog(frame, "New Folder", null, // icon
				content, footer, true);
		dialog.pack();
		dialog.setModal(true);

		Window parentWindow = SwingUtilities.getWindowAncestor(locationPane);
		if (parentWindow != null)
			dialog.setLocationRelativeTo(parentWindow);
		dialog.setVisible(true);

		if (dialog.getSelectedButton() == create)
			return textField.getText();

		return null;
	}

}