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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.image.thumbnail.generator.*;
import com.pump.swing.popover.JPopover;

public class ThumbnailGeneratorDemo extends ShowcaseResourceExampleDemo<File> {
	private static final long serialVersionUID = 1L;

	public final static ThumbnailGenerator[] GENERATORS = new ThumbnailGenerator[] {
			new BasicThumbnailGenerator(), new JPEGMetaDataThumbnailGenerator(),
			new ScalingThumbnailGenerator(), new ImageIOThumbnailGenerator(),
//			new PythonPillowThumbnailGenerator(),
			new MacQuickLookThumbnailGenerator() };

	JLabel previewLabel = new JLabel();
	JSlider sizeSlider = new ShowcaseSlider(10, 300);
	JCheckBox sizeCheckBox = new JCheckBox("Requested Size:");
	JComboBox<String> generatorComboBox = new JComboBox<>();

	public ThumbnailGeneratorDemo() {
		super(File.class, false);

		for (ThumbnailGenerator g : GENERATORS) {
			generatorComboBox.addItem(g.getClass().getSimpleName());
		}

		inspector.addRow(new JLabel("Generator:"), generatorComboBox);
		inspector.addRow(sizeCheckBox, sizeSlider, false);

		examplePanel.add(previewLabel);

		sizeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshFile();
			}

		});

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshControls();
			}

		};

		sizeCheckBox.addActionListener(actionListener);
		generatorComboBox.addActionListener(actionListener);

		JPopover.add(sizeSlider, " pixels");

		previewLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		previewLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		refreshControls();
	}

	protected void refreshControls() {
		sizeSlider.setEnabled(sizeCheckBox.isSelected());
		refreshFile();
	}

	@Override
	protected void refreshFile(File file, String filePath) {
		Icon icon;
		if (!file.exists()) {
			icon = NO_ICON;
			previewLabel.setText("File Missing");
		} else {
			ThumbnailGenerator g = GENERATORS[generatorComboBox
					.getSelectedIndex()];
			int requestedSize = sizeCheckBox.isSelected()
					? sizeSlider.getValue()
					: -1;
			try {
				Image img = g.createThumbnail(file, requestedSize);
				if (img == null) {
					icon = NO_ICON;
					previewLabel.setText("No Preview");
				} else {
					icon = new ImageIcon(img);
					previewLabel.setText("");
				}
			} catch (Exception e) {
				e.printStackTrace();
				icon = NO_ICON;
				previewLabel.setText("Error");
			}
		}

		previewLabel.setIcon(icon);
	}

	@Override
	public String getTitle() {
		return "ThumbnailGenerator Demo";
	}

	@Override
	public String getSummary() {
		return "This demos the ThumbnailGenerator interface.";
	}

	@Override
	public URL getHelpURL() {
		return getClass().getResource("thumbnailGeneratorDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "image", "preview", "thumbnail", "file" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ThumbnailGenerator.class,
				BasicThumbnailGenerator.class, ScalingThumbnailGenerator.class,
				JPEGMetaDataThumbnailGenerator.class,
				MacQuickLookThumbnailGenerator.class };
	}
}