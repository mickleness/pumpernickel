package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.awt.Dimension2D;
import com.pump.icon.IconUtils;
import com.pump.icon.StrikeThroughIcon;
import com.pump.io.icon.AquaFileIcon;
import com.pump.io.icon.FileIcon;
import com.pump.io.icon.FileSystemViewFileIcon;
import com.pump.io.icon.FileViewFileIcon;
import com.pump.util.JVM;

/**
 * This demonstrates the FileIcon class
 */
public class FileIconDemo extends ShowcaseResourceExampleDemo<File> {

	private static final long serialVersionUID = 1L;

	JCheckBox sizeCheckBox = new JCheckBox("Custom Size:");
	JSlider sizeSlider = new ShowcaseSlider(10, 300);

	JComboBox<String> fileIconComboBox;
	boolean sizeSliderUsed = false;

	JLabel demoLabel = new JLabel();
	AquaFileIcon aquaFileIcon;
	FileViewFileIcon fileViewFileIcon;
	FileSystemViewFileIcon fileSystemViewFileIcon;
	List<FileIcon> fileIcons;

	public FileIconDemo() {
		super(File.class, false);
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
		if (aquaFileIcon != null)
			fileIcons.add(aquaFileIcon);
		if (fileViewFileIcon != null)
			fileIcons.add(fileViewFileIcon);
		if (fileSystemViewFileIcon != null)
			fileIcons.add(fileSystemViewFileIcon);

		int selectedIndex = -1;
		String aquaName = "Aqua";
		String fvName = "FileView";
		String sfvName = "FileSystemView";
		if (FileIcon.get() instanceof AquaFileIcon) {
			aquaName += " (Default)";
			selectedIndex = 0;
		} else if (FileIcon.get() instanceof FileViewFileIcon) {
			fvName += " (Default)";
			selectedIndex = aquaFileIcon == null ? 0 : 1;
		} else if (FileIcon.get() instanceof FileSystemViewFileIcon) {
			sfvName += " (Default)";
			selectedIndex = aquaFileIcon == null ? 1 : 2;
		}

		fileIconComboBox = new JComboBox<String>();
		if (aquaFileIcon != null)
			fileIconComboBox.addItem(aquaName);
		if (fileViewFileIcon != null)
			fileIconComboBox.addItem(fvName);
		if (fileSystemViewFileIcon != null)
			fileIconComboBox.addItem(sfvName);

		fileIconComboBox.setSelectedIndex(selectedIndex);

		inspector.addRow(new JLabel("FileIcon Class:"), fileIconComboBox,
				false);
		inspector.addRow(sizeCheckBox, sizeSlider, false);
		examplePanel.add(demoLabel);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshControls();
			}

		};
		fileIconComboBox.addActionListener(actionListener);
		sizeCheckBox.addActionListener(actionListener);

		sizeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				sizeSliderUsed = true;
				refreshFile();
			}

		});

		addSliderPopover(sizeSlider, " pixels");

		refreshControls();

		demoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		demoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
	}

	@Override
	protected void setDefaultResourcePath() {
		resourcePathField.setText(System.getProperty("user.home"));
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

	@Override
	protected void refreshFile(File file, String str) {
		Icon icon;
		FileIcon fileIcon = getFileIcon();
		demoLabel.setText("");
		if (!file.exists()) {
			icon = new StrikeThroughIcon(Color.gray, 20);
			demoLabel.setText("File Missing");
		} else if (fileIcon != null) {
			icon = fileIcon.getIcon(file, false);
		} else {
			icon = UIManager.getIcon("FileView.fileIcon");
			demoLabel.setText("FileView.fileIcon");
		}
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		if (sizeSlider.isEnabled()) {
			int m = sizeSlider.getValue();
			Dimension d = Dimension2D.scaleProportionally(new Dimension(w, h),
					new Dimension(m, m));
			if (d.width != w || d.height != h) {
				icon = IconUtils.createScaledIcon(icon, d.width, d.height);
				if (demoLabel.getText().length() == 0) {
					demoLabel.setText("Scaled");
				} else {
					demoLabel.setText(demoLabel.getText() + " - Scaled");
				}
			}
		} else if (!sizeSliderUsed) {
			sizeSlider.setValue(Math.max(w, h));
		}
		demoLabel.setIcon(icon);
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
