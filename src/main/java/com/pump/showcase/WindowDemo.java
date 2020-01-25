package com.pump.showcase;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.pump.inspector.ControlGridLayout;
import com.pump.inspector.Inspector;
import com.pump.inspector.InspectorRowPanel;
import com.pump.plaf.QPanelUI;

/**
 * 
 */
public class WindowDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JComboBox<String> classComboBox = new JComboBox<>(new String[] { "JFrame",
			"JDialog", "JWindow" });
	JComboBox<Window.Type> typeComboBox = new JComboBox<>(Window.Type.values());
	JComboBox<String> styleComboBox = new JComboBox<>(new String[] { "none",
			"small", "textured", "unified", "hud" });
	JComboBox<Dialog.ModalExclusionType> modalExclusionComboBox = new JComboBox<>(
			Dialog.ModalExclusionType.values());
	JComboBox<ModalityType> modalityTypeComboBox = new JComboBox<>(
			ModalityType.values());

	// Swing options
	JCheckBox undecoratedCheckbox = new JCheckBox("Undecorated", false);
	JCheckBox autoRequestFocusCheckbox = new JCheckBox("Auto Focus", true);
	JCheckBox alwaysOnTopCheckbox = new JCheckBox("Always On Top", false);
	JCheckBox isTransparentCheckbox = new JCheckBox("Transparent", false);
	JCheckBox resizableCheckbox = new JCheckBox("Resizable", true);

	// mac options:
	JCheckBox documentModifiedCheckbox = new JCheckBox("Modified", false);
	JCheckBox shadowCheckbox = new JCheckBox("Shadow", true);
	JCheckBox minimizableCheckbox = new JCheckBox("Minimizable", true);
	JCheckBox closeableCheckbox = new JCheckBox("Closeable", true);
	JCheckBox zoomableCheckbox = new JCheckBox("Zoomable", true);
	JCheckBox documentFileCheckbox = new JCheckBox("File", false);
	JCheckBox fullScreenCheckbox = new JCheckBox("Full Screenable", false);
	JCheckBox draggableBackgroundCheckbox = new JCheckBox("Draggable", false);
	JCheckBox modalSheetCheckbox = new JCheckBox("Modal Sheet", false);
	JCheckBox hideOnDeactivateCheckbox = new JCheckBox("Auto Hide");

	JSlider windowAlpha = new ShowcaseSlider(0, 100, 100);

	JButton showWindowButton = new JButton("Show Window");
	JLabel optionsLabel = new JLabel("");
	InspectorRowPanel modalityTypeRow;

	public WindowDemo() {
		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("Window Class:"), classComboBox, false);
		inspector.addRow(new JLabel("Window Type:"), typeComboBox, false);
		modalityTypeRow = inspector.addRow(new JLabel("Modality Type:"),
				modalityTypeComboBox, false);
		inspector.addRow(new JLabel("Modal Exclusion Type:"),
				modalExclusionComboBox, false);
		ControlGridLayout gridLayout = new ControlGridLayout(3);
		JPanel swingOptions = gridLayout.createGrid(alwaysOnTopCheckbox,
				autoRequestFocusCheckbox, resizableCheckbox,
				isTransparentCheckbox, undecoratedCheckbox);
		inspector.addRow(optionsLabel, swingOptions);

		inspector.addSeparator();
		inspector.addRow(new JLabel("Mac Window Style:"), styleComboBox, false);
		inspector.addRow(new JLabel("Mac Alpha:"), windowAlpha, false);

		JPanel macControls = gridLayout.createGrid(hideOnDeactivateCheckbox,
				closeableCheckbox, draggableBackgroundCheckbox,
				documentFileCheckbox, fullScreenCheckbox, minimizableCheckbox,
				modalSheetCheckbox, documentModifiedCheckbox, shadowCheckbox,
				zoomableCheckbox);
		inspector.addRow(new JLabel("Mac Options:"), macControls);

		examplePanel.add(showWindowButton);
		showWindowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showWindow();
			}
		});

		ActionListener refreshListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshConfiguration();
			}
		};
		classComboBox.addActionListener(refreshListener);

		// the default:
		modalExclusionComboBox.setSelectedItem(ModalExclusionType.NO_EXCLUDE);
		modalityTypeComboBox.setSelectedItem(Dialog.DEFAULT_MODALITY_TYPE);
		refreshConfiguration();

		addSliderPopover(windowAlpha, "%");

		undecoratedCheckbox.setToolTipText("setUndecorated(..)");
		resizableCheckbox.setToolTipText("setResizable(..)");
		modalityTypeComboBox.setToolTipText("setModalityType(..)");
		typeComboBox.setToolTipText("setType(..)");
		autoRequestFocusCheckbox.setToolTipText("setAutoRequestFocus(..)");
		alwaysOnTopCheckbox.setToolTipText("setAlwaysOnTop(..)");
		modalExclusionComboBox.setToolTipText("setModalExclusionType(..)");
		isTransparentCheckbox.setToolTipText("setBackground(transparent)");
		documentModifiedCheckbox
				.setToolTipText("Client Property \"Window.documentModified\"");
		styleComboBox.setToolTipText("Client Property \"Window.style\"");
		shadowCheckbox.setToolTipText("Client Property \"Window.shadow\"");
		minimizableCheckbox
				.setToolTipText("Client Property \"Window.minimizable\"");
		closeableCheckbox
				.setToolTipText("Client Property \"Window.closeable\"");
		zoomableCheckbox.setToolTipText("Client Property \"Window.zoomable\"");
		fullScreenCheckbox
				.setToolTipText("Client Property \"apple.awt.fullscreenable\"");
		documentFileCheckbox
				.setToolTipText("Client Property \"Window.documentFile\"");
		modalSheetCheckbox
				.setToolTipText("Client Property \"apple.awt.documentModalSheet\"");
		draggableBackgroundCheckbox
				.setToolTipText("Client Property \"apple.awt.draggableWindowBackground\"");
		hideOnDeactivateCheckbox
				.setToolTipText("Client Property \"Window.hidesOnDeactivate\"");
	}

	private void refreshConfiguration() {
		if (classComboBox.getSelectedIndex() == 0) {
			optionsLabel.setText("Frame Options:");
			undecoratedCheckbox.setVisible(true);
			resizableCheckbox.setVisible(true);
			modalityTypeRow.setVisible(false);
		} else if (classComboBox.getSelectedIndex() == 1) {
			optionsLabel.setText("Dialog Options:");
			undecoratedCheckbox.setVisible(true);
			resizableCheckbox.setVisible(true);
			modalityTypeRow.setVisible(true);
		} else {
			optionsLabel.setText("Window Options:");
			undecoratedCheckbox.setVisible(false);
			resizableCheckbox.setVisible(false);
			modalityTypeRow.setVisible(false);
		}
	}

	Window currentWindow;

	private void showWindow() {
		if (currentWindow != null) {
			if (currentWindow.isShowing()) {
				currentWindow.setVisible(false);
			}
			try {
				currentWindow.dispose();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			currentWindow = null;
		}

		final Window w;
		Frame owner = (Frame) SwingUtilities
				.getWindowAncestor(showWindowButton);
		if (classComboBox.getSelectedIndex() == 0) {
			JFrame f = new JFrame("JFrame");
			f.setUndecorated(undecoratedCheckbox.isSelected());
			f.setResizable(resizableCheckbox.isSelected());
			w = f;
		} else if (classComboBox.getSelectedIndex() == 1) {
			JDialog d = new JDialog(owner, "JDialog");
			d.setModalityType((ModalityType) modalityTypeComboBox
					.getSelectedItem());
			d.setUndecorated(undecoratedCheckbox.isSelected());
			d.setResizable(resizableCheckbox.isSelected());
			w = d;
		} else {
			JWindow w2 = new JWindow(owner);
			w = w2;
		}

		w.setType((Window.Type) typeComboBox.getSelectedItem());
		w.setAutoRequestFocus(autoRequestFocusCheckbox.isSelected());
		w.setAlwaysOnTop(alwaysOnTopCheckbox.isSelected());
		w.setModalExclusionType((Dialog.ModalExclusionType) modalExclusionComboBox
				.getSelectedItem());
		if (isTransparentCheckbox.isSelected())
			w.setBackground(new Color(0, 0, 0, 0));
		currentWindow = w;

		if (styleComboBox.getSelectedIndex() != 0) {
			String style = (String) styleComboBox.getSelectedItem();
			((RootPaneContainer) w).getRootPane().putClientProperty(
					"Window.style", style);
		}

		((RootPaneContainer) w).getRootPane().putClientProperty(
				"Window.documentModified",
				documentModifiedCheckbox.isSelected());

		((RootPaneContainer) w).getRootPane().putClientProperty(
				"Window.shadow", shadowCheckbox.isSelected());

		((RootPaneContainer) w).getRootPane().putClientProperty(
				"Window.minimizable", minimizableCheckbox.isSelected());

		((RootPaneContainer) w).getRootPane().putClientProperty(
				"Window.closeable", closeableCheckbox.isSelected());

		((RootPaneContainer) w).getRootPane().putClientProperty(
				"Window.zoomable", zoomableCheckbox.isSelected());

		// alt: FullScreenUtilities.setWindowCanFullScreen(window, boolean);
		((RootPaneContainer) w).getRootPane().putClientProperty(
				"apple.awt.fullscreenable", fullScreenCheckbox.isSelected());

		if (documentFileCheckbox.isSelected()) {
			String filePath = System.getProperty("user.dir");
			File file = new File(filePath);
			((RootPaneContainer) w).getRootPane().putClientProperty(
					"Window.documentFile", file);
		}

		((RootPaneContainer) w).getRootPane()
				.putClientProperty("apple.awt.documentModalSheet",
						modalSheetCheckbox.isSelected());

		((RootPaneContainer) w).getRootPane().putClientProperty(
				"apple.awt.draggableWindowBackground",
				draggableBackgroundCheckbox.isSelected());

		((RootPaneContainer) w).getRootPane().putClientProperty(
				"Window.hidesOnDeactivate",
				hideOnDeactivateCheckbox.isSelected());

		Float alpha = windowAlpha.getValue() / 100f;
		((RootPaneContainer) w).getRootPane().putClientProperty("Window.alpha",
				alpha);

		JButton closeButton = new JButton("OK");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				w.setVisible(false);
			}

		});

		JPanel content = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(3, 3, 3, 3);
		content.add(
				new JLabel("This is your new "
						+ classComboBox.getSelectedItem() + "."), c);
		c.gridy++;
		c.insets = new Insets(13, 3, 3, 3);
		content.add(closeButton, c);
		content.setOpaque(false);

		if (isTransparentCheckbox.isSelected()) {
			QPanelUI ui = QPanelUI.createToolTipUI();
			ui.setCalloutSize(0);
			ui.setCornerSize(15);
			content.setUI(ui);

			JPanel wrapper = new JPanel();
			wrapper.setOpaque(false);
			wrapper.add(content);
			content = wrapper;
		}
		content.setBorder(new EmptyBorder(10, 10, 10, 10));

		((RootPaneContainer) w).getContentPane().add(content);

		w.pack();
		w.setLocationRelativeTo(owner);
		w.setVisible(true);
	}

	@Override
	public String getTitle() {
		return "Window Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates windows properties, including special Mac customizations.";
	}

	@Override
	public URL getHelpURL() {
		return WindowDemo.class.getResource("windowDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "window", "dialog", "frame", "swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JFrame.class, JWindow.class, JDialog.class };
	}

}
