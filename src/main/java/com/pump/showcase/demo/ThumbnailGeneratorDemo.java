package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.icon.StrikeThroughIcon;
import com.pump.image.thumbnail.generator.BasicThumbnailGenerator;
import com.pump.image.thumbnail.generator.JPEGMetaDataThumbnailGenerator;
import com.pump.image.thumbnail.generator.MacCImageThumbnailGenerator;
import com.pump.image.thumbnail.generator.MacQuickLookThumbnailGenerator;
import com.pump.image.thumbnail.generator.ScalingThumbnailGenerator;
import com.pump.image.thumbnail.generator.ThumbnailGenerator;
import com.pump.inspector.Inspector;
import com.pump.swing.FileDialogUtils;

public class ThumbnailGeneratorDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	private static Icon NO_ICON = new StrikeThroughIcon(Color.gray, 64);

	public final static ThumbnailGenerator[] GENERATORS = new ThumbnailGenerator[] {
			new BasicThumbnailGenerator(), new JPEGMetaDataThumbnailGenerator(),
			new MacCImageThumbnailGenerator(), new ScalingThumbnailGenerator(),
			new MacQuickLookThumbnailGenerator() };

	JLabel previewLabel = new JLabel();
	JButton fileDialogButton = new JButton("Browse...");
	JTextField filePathField = new JTextField(20);
	JSlider sizeSlider = new ShowcaseSlider(10, 300);
	JCheckBox sizeCheckBox = new JCheckBox("Requested Size:");
	JComboBox<String> generatorComboBox = new JComboBox<>();

	public ThumbnailGeneratorDemo() {
		for (ThumbnailGenerator g : GENERATORS) {
			generatorComboBox.addItem(g.getClass().getSimpleName());
		}

		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("File:"), filePathField, fileDialogButton);
		inspector.addRow(new JLabel("Generator:"), generatorComboBox);
		inspector.addRow(sizeCheckBox, sizeSlider, false);

		examplePanel.add(previewLabel);

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

		fileDialogButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Frame frame = (Frame) SwingUtilities
						.getWindowAncestor(configurationPanel);
				File file = FileDialogUtils.showOpenDialog(frame, "Select",
						FileIconDemo.ALL_FILES);
				if (file != null)
					filePathField.setText(file.getPath());
			}

		});

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

		addSliderPopover(sizeSlider, " pixels");

		previewLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		previewLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		refreshControls();
	}

	protected void refreshControls() {
		sizeSlider.setEnabled(sizeCheckBox.isSelected());
		refreshFile();
	}

	protected void refreshFile() {
		File file = new File(filePathField.getText());

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
				MacCImageThumbnailGenerator.class,
				MacQuickLookThumbnailGenerator.class };
	}
}
