package com.pump.showcase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.awt.Dimension2D;
import com.pump.icon.IconUtils;
import com.pump.icon.StrikeThroughIcon;
import com.pump.inspector.Inspector;
import com.pump.io.icon.AquaFileIcon;
import com.pump.io.icon.FileIcon;
import com.pump.io.icon.FileSystemViewFileIcon;
import com.pump.io.icon.FileViewFileIcon;
import com.pump.swing.FileDialogUtils;
import com.pump.util.JVM;

/**
 * This demonstrates the FileIcon class
 */
public class FileIconDemo extends ShowcaseExampleDemo {

	private static final long serialVersionUID = 1L;

	static FilenameFilter ALL_FILES = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			return true;
		}

	};

	JLabel fileLabel = new JLabel();
	JButton fileDialogButton = new JButton("Browse...");
	JTextField filePathField = new JTextField(20);
	JCheckBox sizeCheckBox = new JCheckBox("Custom Size:");
	JSlider sizeSlider = new ShowcaseSlider(10, 300);

	JComboBox<String> fileIconComboBox;
	boolean sizeSliderUsed = false;

	AquaFileIcon aquaFileIcon;
	FileViewFileIcon fileViewFileIcon;
	FileSystemViewFileIcon fileSystemViewFileIcon;
	List<FileIcon> fileIcons;

	public FileIconDemo() {
		if (JVM.isMac) {
			try {
				aquaFileIcon = new AquaFileIcon();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		fileViewFileIcon = new FileViewFileIcon();
		fileSystemViewFileIcon = new FileSystemViewFileIcon();
		fileIcons = new ArrayList<>();
		if(aquaFileIcon!=null)
			fileIcons.add(aquaFileIcon);
		if(fileViewFileIcon!=null)
			fileIcons.add(fileViewFileIcon);
		if(fileSystemViewFileIcon!=null)
			fileIcons.add(fileSystemViewFileIcon);

		filePathField.setText(System.getProperty("user.home"));

		int selectedIndex = -1;
		String aquaName = "Aqua";
		String fvName = "FileView";
		String sfvName = "FileSystemView";
		if (FileIcon.get() instanceof AquaFileIcon) {
			aquaName += " (Default)";
			selectedIndex = 0;
		} else if (FileIcon.get() instanceof FileViewFileIcon) {
			fvName += " (Default)";
			selectedIndex = aquaFileIcon==null ? 0 : 1;
		} else if (FileIcon.get() instanceof FileSystemViewFileIcon) {
			sfvName += " (Default)";
			selectedIndex = aquaFileIcon==null ? 1 : 2;
		}

		fileIconComboBox = new JComboBox<String>();
		if (aquaFileIcon != null)
			fileIconComboBox.addItem(aquaName);
		if (fileViewFileIcon != null)
			fileIconComboBox.addItem(fvName);
		if (fileSystemViewFileIcon != null)
			fileIconComboBox.addItem(sfvName);

		fileIconComboBox.setSelectedIndex(selectedIndex);

		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("File:"), filePathField, fileDialogButton);
		inspector
				.addRow(new JLabel("FileIcon Class:"), fileIconComboBox, false);
		inspector.addRow(sizeCheckBox, sizeSlider, false);
		examplePanel.add(fileLabel);

		filePathField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				refreshFile();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				refreshFile();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				refreshFile();
			}

		});

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshControls();
			}

		};
		fileIconComboBox.addActionListener(actionListener);
		sizeCheckBox.addActionListener(actionListener);
		fileDialogButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Frame frame = (Frame) SwingUtilities
						.getWindowAncestor(configurationPanel);
				File file = FileDialogUtils.showOpenDialog(frame, "Select",
						ALL_FILES);
				if (file != null)
					filePathField.setText(file.getPath());
			}

		});

		sizeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				sizeSliderUsed = true;
				refreshFile();
			}

		});

		addSliderPopover(sizeSlider, " pixels");

		refreshControls();

		fileLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		fileLabel.setHorizontalTextPosition(SwingConstants.CENTER);
	}

	protected void refreshControls() {
		sizeSlider.setEnabled(sizeCheckBox.isSelected());
		refreshFile();
	}

	protected FileIcon getFileIcon() {
		int i = fileIconComboBox.getSelectedIndex();
		if (i >= 0)
			return fileIcons.get(i);
		return null;
	}

	protected void refreshFile() {
		File file = new File(filePathField.getText());
		Icon icon;
		FileIcon fileIcon = getFileIcon();
		fileLabel.setText("");
		if (!file.exists()) {
			icon = new StrikeThroughIcon(Color.gray, 20);
			fileLabel.setText("File Missing");
		} else if (fileIcon != null) {
			icon = fileIcon.getIcon(file, false);
		} else {
			icon = UIManager.getIcon("FileView.fileIcon");
			fileLabel.setText("FileView.fileIcon");
		}
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		if (sizeSlider.isEnabled()) {
			int m = sizeSlider.getValue();
			Dimension d = Dimension2D.scaleProportionally(new Dimension(w, h),
					new Dimension(m, m));
			if (d.width != w || d.height != h) {
				icon = IconUtils.createScaledIcon(icon, d.width, d.height);
				if (fileLabel.getText().length() == 0) {
					fileLabel.setText("Scaled");
				} else {
					fileLabel.setText(fileLabel.getText() + " - Scaled");
				}
			}
		} else if (!sizeSliderUsed) {
			sizeSlider.setValue(Math.max(w, h));
		}
		fileLabel.setIcon(icon);
	}

	@Override
	public String getTitle() {
		return "FileIcon Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates the Icons created by the FileIcon class.";
	}

	@Override
	public URL getHelpURL() {
		return FileIconDemo.class.getResource("fileIconDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "File", "icon", "filesystem", "filepath" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { FileIcon.class };
	}

}
