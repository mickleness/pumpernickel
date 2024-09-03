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
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.data.AttributeDataImpl;
import com.pump.data.Key;
import com.pump.desktop.temp.TempFileManager;
import com.pump.inspector.AnimatingInspectorPanel;
import com.pump.inspector.ControlGridLayout;
import com.pump.inspector.Inspector;
import com.pump.inspector.InspectorRowPanel;
import com.pump.plaf.QPanelUI;
import com.pump.showcase.demo.ShowcaseExampleDemo.ShowcaseSlider;
import com.pump.showcase.demo.WindowOptionsForm.ShapeType;
import com.pump.swing.DialogFooter;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.swing.popover.JPopover;
import com.pump.util.JVM;

/**
 * This demonstrates JFrame/JWindow/JDialog properties, including some
 * Mac-specific properties.
 */
public class WindowDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JButton showWindowButton = new JButton("Show Window");
	WindowOptionsForm newWindowForm = new WindowOptionsForm();

	public WindowDemo() {
		showWindowButton.setOpaque(false);
		JPanel animatingPanel = new AnimatingInspectorPanel();
		Inspector inspector = new Inspector(animatingPanel);
		configurationPanel.add(animatingPanel);
		WindowOptionsFormUI formUI = new WindowOptionsFormUI(newWindowForm,
				inspector, false);
		formUI.positionRow.setVisible(false);

		examplePanel.add(showWindowButton);
		showWindowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showWindow();
			}
		});
	}

	private void showWindow() {
		Frame owner = (Frame) SwingUtilities
				.getWindowAncestor(showWindowButton);

		Window w;
		Class z = newWindowForm
				.getAttribute(WindowOptionsForm.KEY_WINDOW_CLASS);
		if (z == JFrame.class) {
			w = new JFrame("JFrame");
		} else if (z == JDialog.class) {
			w = new JDialog(owner, "JDialog");
		} else {
			w = new JWindow(owner);
		}

		final WindowOptionsForm myForm = new WindowOptionsForm(newWindowForm);
		myForm.configureWindow(w);
		myForm.addAttributePropertyChangeListener(new PropertyChangeListener() {
			List<PropertyChangeEvent> changes = new LinkedList<>();

			Runnable configureWindowRunnable = new Runnable() {
				@Override
				public void run() {
					if (changes.isEmpty())
						return;

					try {
						myForm.configureWindow(w);
						changes.clear();
					} catch (RuntimeException e) {
						PropertyChangeEvent[] array = changes
								.toArray(new PropertyChangeEvent[0]);
						changes.clear();
						for (PropertyChangeEvent event : array) {
							myForm.setAttribute(event.getPropertyName(),
									event.getOldValue());
						}
						throw e;
					}
				}
			};

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				changes.add(evt);
				SwingUtilities.invokeLater(configureWindowRunnable);
			}
		});

		JPanel windowContent = new JPanel();
		Inspector inspector = new Inspector(windowContent);
		WindowOptionsFormUI formUI = new WindowOptionsFormUI(myForm, inspector,
				true);
		// once it's showing: you can't change the type of window
		formUI.windowClassRow.setVisible(false);

		w.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				Point loc = w.getLocation();
				myForm.setAttribute(WindowOptionsForm.KEY_X, loc.x);
				myForm.setAttribute(WindowOptionsForm.KEY_Y, loc.y);
			}
		});

		JButton toBackButton = new JButton("To Back");
		JButton toFrontButton = new JButton("To Front");
		toBackButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				w.toBack();
			}

		});
		toFrontButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				w.toFront();
			}

		});

		JComponent[] leftComponents = new JComponent[] { toBackButton,
				toFrontButton };
		DialogFooter footer = DialogFooter.createDialogFooter(leftComponents,
				DialogFooter.OK_OPTION, DialogFooter.OK_OPTION,
				EscapeKeyBehavior.TRIGGERS_DEFAULT);
		footer.setOpaque(false);

		RootPaneContainer rpc = (RootPaneContainer) w;
		rpc.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3, 3, 3, 3);
		rpc.getContentPane().add(windowContent, c);
		c.gridy++;
		c.weighty = 0;
		rpc.getContentPane().add(footer, c);

		windowContent.setUI(QPanelUI.createBoxUI());

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

class WindowOptionsForm extends AttributeDataImpl {
	private static final long serialVersionUID = 1L;

	public enum ShapeType {
		NONE() {

			@Override
			Shape getShape() {
				return null;
			}

		},
		JAGGED() {

			Path2D path = new Path2D.Float();

			{
				path.moveTo(0, 0);
				{
					int y = 0;
					for (int x = 0; x < 10000; x += 10) {
						path.lineTo(x, y);
						if (y == 0) {
							y += 10;
						} else {
							y -= 10;
						}
					}
				}
				path.lineTo(10000, 10000);
				path.lineTo(0, 10000);
				{
					int x = 0;
					for (int y = 10000; y >= 0; y -= 10) {
						path.lineTo(x, y);
						if (x == 0) {
							x += 10;
						} else {
							x -= 10;
						}
					}
				}
				path.closePath();
			}

			@Override
			Shape getShape() {
				return path;
			}
		};

		abstract Shape getShape();
	}

	/**
	 * The possible window styles for {@link WindowOptionsForm#KEY_MAC_STYLE}.
	 */
	public static final String[] MAC_STYLES = new String[] { "none", "small",
			"textured", "unified", "hud" };

	public static final Key<ModalExclusionType> KEY_MODAL_EXCLUSION_TYPE = new Key<>(
			ModalExclusionType.class, "modalExclusionType",
			ModalExclusionType.NO_EXCLUDE);
	public static final Key<ModalityType> KEY_MODALITY_TYPE = new Key<>(
			ModalityType.class, "modalityType", Dialog.DEFAULT_MODALITY_TYPE);
	public static final Key<Class> KEY_WINDOW_CLASS = new Key<>(Class.class,
			"windowClass", JFrame.class);
	public static final Key<Window.Type> KEY_WINDOW_TYPE = new Key<>(
			Window.Type.class, "type", Window.Type.NORMAL);
	public static final Key<ShapeType> KEY_SHAPE = new Key<>(ShapeType.class,
			"shape", ShapeType.NONE);

	public static final Key<Float> KEY_ALPHA = new Key<>(Float.class, "alpha",
			1f);

	public static final Key<Boolean> KEY_UNDECORATED = new Key<>(Boolean.class,
			"undecorated", false);
	public static final Key<Boolean> KEY_AUTO_REQUEST_FOCUS = new Key<>(
			Boolean.class, "autoRequestFocus", true);
	public static final Key<Boolean> KEY_ALWAYS_ON_TOP = new Key<>(
			Boolean.class, "alwaysOnTop", false);
	public static final Key<Boolean> KEY_TRANSPARENT = new Key<>(Boolean.class,
			"transparent", false);
	public static final Key<Boolean> KEY_RESIZABLE = new Key<>(Boolean.class,
			"resizable", true);

	// mac options:
	public static final Key<String> KEY_MAC_STYLE = new Key<>(String.class,
			"Window.style", MAC_STYLES[0]);
	public static final Key<Boolean> KEY_MAC_DOCUMENT_MODIFIED = new Key<>(
			Boolean.class, "Window.documentModified", false);
	public static final Key<Boolean> KEY_MAC_SHADOW = new Key<>(Boolean.class,
			"Window.shadow", true);
	public static final Key<Boolean> KEY_MAC_MINIMIZABLE = new Key<>(
			Boolean.class, "Window.minimizable", true);
	public static final Key<Boolean> KEY_MAC_CLOSEABLE = new Key<>(
			Boolean.class, "Window.closeable", true);
	public static final Key<Boolean> KEY_MAC_ZOOMABLE = new Key<>(Boolean.class,
			"Window.zoomable", true);
	public static final Key<File> KEY_MAC_DOCUMENT_FILE = new Key<>(File.class,
			"Window.documentFile", null);
	public static final Key<Boolean> KEY_MAC_FULLSCREENABLE = new Key<>(
			Boolean.class, "apple.awt.fullscreenable", false);
	public static final Key<Boolean> KEY_MAC_DRAGGABLE = new Key<>(
			Boolean.class, "apple.awt.draggableWindowBackground", false);
	public static final Key<Boolean> KEY_MAC_MODAL_SHEET = new Key<>(
			Boolean.class, "apple.awt.documentModalSheet", false);
	public static final Key<Boolean> KEY_MAC_HIDES_ON_DEACTIVATE = new Key<>(
			Boolean.class, "Window.hidesOnDeactivate", false);
	public static final Key<Boolean> KEY_MAC_FULL_WINDOW_CONTENT = new Key<>(
			Boolean.class, "apple.awt.fullWindowContent", false);
	public static final Key<Boolean> KEY_MAC_TRANSPARENT_TITLE_BAR = new Key<>(
			Boolean.class, "apple.awt.transparentTitleBar", false);

	public static final Key<String> KEY_TITLE = new Key<>(String.class, "title", "My Title");
	public static final Key<Integer> KEY_X = new Key<>(Integer.class, "x", -1);
	public static final Key<Integer> KEY_Y = new Key<>(Integer.class, "y", -1);

	public WindowOptionsForm() {
	}

	/**
	 * Copy the data from one form to another.
	 */
	public WindowOptionsForm(WindowOptionsForm otherForm) {
		putAllAttributes(otherForm.data, true);
	}

	/**
	 * Override to make public
	 */
	@Override
	public Object setAttribute(String attributeName, Object value) {
		return super.setAttribute(attributeName, value);
	}

	public void configureWindow(Window w) {
		boolean alphaSupported;
		if (w instanceof JFrame) {
			JFrame f = (JFrame) w;
			if (getAttribute(KEY_UNDECORATED) != f.isUndecorated())
				f.setUndecorated(getAttribute(KEY_UNDECORATED));
			f.setResizable(getAttribute(KEY_RESIZABLE));
			alphaSupported = f.isUndecorated();
			f.setTitle(getAttribute(KEY_TITLE));
		} else if (w instanceof JDialog) {
			JDialog d = (JDialog) w;
			if (getAttribute(KEY_UNDECORATED) != d.isUndecorated())
				d.setUndecorated(getAttribute(KEY_UNDECORATED));
			d.setModalityType(getAttribute(KEY_MODALITY_TYPE));
			d.setResizable(getAttribute(KEY_RESIZABLE));
			alphaSupported = d.isUndecorated();
			d.setTitle(getAttribute(KEY_TITLE));
		} else {
			alphaSupported = true;
		}

		if (w.getType() != getAttribute(KEY_WINDOW_TYPE))
			w.setType(getAttribute(KEY_WINDOW_TYPE));
		w.setAutoRequestFocus(getAttribute(KEY_AUTO_REQUEST_FOCUS));
		w.setAlwaysOnTop(getAttribute(KEY_ALWAYS_ON_TOP));
		w.setModalExclusionType(getAttribute(KEY_MODAL_EXCLUSION_TYPE));
		if (alphaSupported && getAttribute(KEY_TRANSPARENT)) {
			w.setBackground(new Color(0, 0, 0, 0));
		} else {
			w.setBackground(UIManager.getColor("Panel.background"));
		}

		ShapeType shape = getAttribute(KEY_SHAPE);
		w.setShape(shape.getShape());

		if (w.getOpacity() != getAttribute(KEY_ALPHA).floatValue())
			w.setOpacity(getAttribute(KEY_ALPHA).floatValue());

		w.setLocation(getAttribute(KEY_X), getAttribute(KEY_Y));

		RootPaneContainer rpc = (RootPaneContainer) w;
		JRootPane rootPane = rpc.getRootPane();
		rootPane.putClientProperty("Window.style", getAttribute(KEY_MAC_STYLE));
		rootPane.putClientProperty("Window.documentModified",
				getAttribute(KEY_MAC_DOCUMENT_MODIFIED));
		rootPane.putClientProperty("Window.shadow",
				getAttribute(KEY_MAC_SHADOW));
		rootPane.putClientProperty("Window.minimizable",
				getAttribute(KEY_MAC_MINIMIZABLE));
		rootPane.putClientProperty("Window.closeable",
				getAttribute(KEY_MAC_CLOSEABLE));
		rootPane.putClientProperty("Window.zoomable",
				getAttribute(KEY_MAC_ZOOMABLE));

		// alt: FullScreenUtilities.setWindowCanFullScreen(window, boolean);
		rootPane.putClientProperty("apple.awt.fullscreenable",
				getAttribute(KEY_MAC_FULLSCREENABLE));

		rootPane.putClientProperty("Window.documentFile",
				getAttribute(KEY_MAC_DOCUMENT_FILE));

		rootPane.putClientProperty("apple.awt.documentModalSheet",
				getAttribute(KEY_MAC_MODAL_SHEET));

		rootPane.putClientProperty("apple.awt.draggableWindowBackground",
				getAttribute(KEY_MAC_DRAGGABLE));

		rootPane.putClientProperty("apple.awt.fullWindowContent",
				getAttribute(KEY_MAC_FULL_WINDOW_CONTENT));

		rootPane.putClientProperty("apple.awt.transparentTitleBar",
				getAttribute(KEY_MAC_TRANSPARENT_TITLE_BAR));

		rootPane.putClientProperty("Window.hidesOnDeactivate",
				getAttribute(KEY_MAC_HIDES_ON_DEACTIVATE));

	}

	public boolean isDecoratable() {
		if (getAttribute(KEY_WINDOW_CLASS) == JWindow.class)
			return false;
		return !getAttribute(KEY_UNDECORATED);
	}

}

class WindowOptionsFormUI {
	WindowOptionsForm form;

	InspectorRowPanel modalityTypeRow, positionRow, windowClassRow,
			windowTypeRow, macTitleBarRow, titleRow;

	// Swing options
	JComboBox<String> classComboBox = new JComboBox<>(
			new String[] { "JFrame", "JDialog", "JWindow" });
	JTextField titleField = new JTextField();
	JComboBox<Window.Type> typeComboBox = new JComboBox<>(Window.Type.values());
	JComboBox<Dialog.ModalExclusionType> modalExclusionComboBox = new JComboBox<>(
			Dialog.ModalExclusionType.values());
	JComboBox<ModalityType> modalityTypeComboBox = new JComboBox<>(
			ModalityType.values());
	JComboBox<String> shapeComboBox = new JComboBox<>(
			new String[] { "None", "Jagged" });
	JCheckBox undecoratedCheckbox = new JCheckBox("Undecorated");
	JCheckBox autoRequestFocusCheckbox = new JCheckBox("Auto Focus");
	JCheckBox alwaysOnTopCheckbox = new JCheckBox("Always On Top");
	JCheckBox isTransparentCheckbox = new JCheckBox("Transparent");
	JCheckBox resizableCheckbox = new JCheckBox("Resizable");

	JSlider windowAlpha = new ShowcaseSlider(0, 100, 100);
	JLabel optionsLabel = new JLabel("");

	// mac options:
	JComboBox<String> styleComboBox = new JComboBox<>(
			WindowOptionsForm.MAC_STYLES);
	JCheckBox documentModifiedCheckbox = new JCheckBox("Modified");
	JCheckBox shadowCheckbox = new JCheckBox("Shadow");
	JCheckBox minimizableCheckbox = new JCheckBox("Minimizable");
	JCheckBox closeableCheckbox = new JCheckBox("Closeable");
	JCheckBox zoomableCheckbox = new JCheckBox("Zoomable");
	JCheckBox documentFileCheckbox = new JCheckBox("File");
	JCheckBox fullScreenCheckbox = new JCheckBox("Full Screenable");
	JCheckBox draggableBackgroundCheckbox = new JCheckBox("Draggable");
	JCheckBox modalSheetCheckbox = new JCheckBox("Modal Sheet");
	JCheckBox hideOnDeactivateCheckbox = new JCheckBox("Auto Hide");
	JCheckBox fullWindowCheckbox = new JCheckBox("Full Window");
	JCheckBox transparentTitleBarCheckbox = new JCheckBox(
			"Transparent Title Bar");

	PropertyChangeListener formListener = new PropertyChangeListener() {

		boolean dirty = false;
		Runnable refreshRunnable = new Runnable() {

			@Override
			public void run() {
				if (!dirty)
					return;
				dirty = false;
				refreshControls();
			}
		};

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			dirty = true;
			SwingUtilities.invokeLater(refreshRunnable);
		}

	};

	JSpinner xSpinner = new JSpinner(
			new SpinnerNumberModel(0, -10000, 10000, 10));
	JSpinner ySpinner = new JSpinner(
			new SpinnerNumberModel(0, -10000, 10000, 10));

	ChangeListener locSpinnerListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			int x = ((SpinnerNumberModel) xSpinner.getModel()).getNumber()
					.intValue();
			int y = ((SpinnerNumberModel) ySpinner.getModel()).getNumber()
					.intValue();
			form.setAttribute(WindowOptionsForm.KEY_X, x);
			form.setAttribute(WindowOptionsForm.KEY_Y, y);
		}
	};

	ChangeListener alphaChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			float f = windowAlpha.getValue() / 100f;
			form.setAttribute(WindowOptionsForm.KEY_ALPHA, f);
		}

	};

	public WindowOptionsFormUI(WindowOptionsForm form, Inspector inspector,
			boolean small) {
		this.form = form;

		JPopover.add(windowAlpha, "%");

		shapeComboBox.setToolTipText("setShape(Shape)");
		undecoratedCheckbox.setToolTipText("setUndecorated(boolean)");
		resizableCheckbox.setToolTipText("setResizable(boolean)");
		titleField.setToolTipText("setTitle(String)");
		modalityTypeComboBox.setToolTipText("setModalityType(ModalityType)");
		typeComboBox.setToolTipText("setType(Window.Type)");
		autoRequestFocusCheckbox.setToolTipText("setAutoRequestFocus(boolean)");
		alwaysOnTopCheckbox.setToolTipText("setAlwaysOnTop(boolean)");
		modalExclusionComboBox.setToolTipText("setModalExclusionType(ModalExclusionType)");
		isTransparentCheckbox.setToolTipText("setBackground(Color)");
		documentModifiedCheckbox
				.setToolTipText("Client Property \"Window.documentModified\"");
		transparentTitleBarCheckbox.setToolTipText(
				"Client Property \"apple.awt.transparentTitleBar\", not supported in JDK 8");
		fullWindowCheckbox.setToolTipText(
				"Client Property \"apple.awt.fullWindowContent\", not supported in JDK 8");
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
		modalSheetCheckbox.setToolTipText(
				"Client Property \"apple.awt.documentModalSheet\"");
		draggableBackgroundCheckbox.setToolTipText(
				"Client Property \"apple.awt.draggableWindowBackground\"");
		hideOnDeactivateCheckbox
				.setToolTipText("Client Property \"Window.hidesOnDeactivate\"");

		classComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Class<?> z;
				if (classComboBox.getSelectedIndex() == 0) {
					z = JFrame.class;
				} else if (classComboBox.getSelectedIndex() == 1) {
					z = JDialog.class;
				} else {
					z = JWindow.class;
				}
				form.setAttribute(WindowOptionsForm.KEY_WINDOW_CLASS, z);
			}

		});

		typeComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				form.setAttribute(WindowOptionsForm.KEY_WINDOW_TYPE,
						(Window.Type) typeComboBox.getSelectedItem());
			}
		});

		modalExclusionComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				form.setAttribute(WindowOptionsForm.KEY_MODAL_EXCLUSION_TYPE,
						(Dialog.ModalExclusionType) modalExclusionComboBox
								.getSelectedItem());
			}
		});

		titleField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				form.setAttribute(WindowOptionsForm.KEY_TITLE, titleField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				form.setAttribute(WindowOptionsForm.KEY_TITLE, titleField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {}
		});

		modalityTypeComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				form.setAttribute(WindowOptionsForm.KEY_MODALITY_TYPE,
						(ModalityType) modalityTypeComboBox.getSelectedItem());
			}
		});

		shapeComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ShapeType shapeType = shapeComboBox.getSelectedIndex() == 0
						? ShapeType.NONE
						: ShapeType.JAGGED;
				form.setAttribute(WindowOptionsForm.KEY_SHAPE, shapeType);
			}
		});

		attachListener(undecoratedCheckbox, WindowOptionsForm.KEY_UNDECORATED);
		attachListener(autoRequestFocusCheckbox,
				WindowOptionsForm.KEY_AUTO_REQUEST_FOCUS);
		attachListener(alwaysOnTopCheckbox,
				WindowOptionsForm.KEY_ALWAYS_ON_TOP);
		attachListener(isTransparentCheckbox,
				WindowOptionsForm.KEY_TRANSPARENT);
		attachListener(resizableCheckbox, WindowOptionsForm.KEY_RESIZABLE);

		styleComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				form.setAttribute(WindowOptionsForm.KEY_MAC_STYLE,
						(String) styleComboBox.getSelectedItem());
			}
		});

		attachListener(documentModifiedCheckbox,
				WindowOptionsForm.KEY_MAC_DOCUMENT_MODIFIED);
		attachListener(shadowCheckbox, WindowOptionsForm.KEY_MAC_SHADOW);
		attachListener(minimizableCheckbox,
				WindowOptionsForm.KEY_MAC_MINIMIZABLE);
		attachListener(closeableCheckbox, WindowOptionsForm.KEY_MAC_CLOSEABLE);
		attachListener(zoomableCheckbox, WindowOptionsForm.KEY_MAC_ZOOMABLE);
		attachListener(fullScreenCheckbox,
				WindowOptionsForm.KEY_MAC_FULLSCREENABLE);
		attachListener(draggableBackgroundCheckbox,
				WindowOptionsForm.KEY_MAC_DRAGGABLE);
		attachListener(modalSheetCheckbox,
				WindowOptionsForm.KEY_MAC_MODAL_SHEET);
		attachListener(hideOnDeactivateCheckbox,
				WindowOptionsForm.KEY_MAC_HIDES_ON_DEACTIVATE);
		attachListener(fullWindowCheckbox,
				WindowOptionsForm.KEY_MAC_FULL_WINDOW_CONTENT);
		attachListener(transparentTitleBarCheckbox,
				WindowOptionsForm.KEY_MAC_TRANSPARENT_TITLE_BAR);

		documentFileCheckbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = null;
				if (documentFileCheckbox.isSelected())
					file = TempFileManager.get().createFile("throwaway", "tmp");
				form.setAttribute(WindowOptionsForm.KEY_MAC_DOCUMENT_FILE,
						file);
			}
		});

		JPanel positionControls = new JPanel(new FlowLayout());
		positionControls.add(xSpinner);
		positionControls.add(ySpinner);
		positionControls.setOpaque(false);

		positionRow = inspector.addRow(new JLabel("Position:"),
				positionControls, false);
		windowClassRow = inspector.addRow(new JLabel("Window Class:"),
				classComboBox, false);
		windowTypeRow = inspector.addRow(new JLabel("Window Type:"),
				typeComboBox, false);
		modalityTypeRow = inspector.addRow(new JLabel("Modality Type:"),
				modalityTypeComboBox, false);
		titleRow = inspector.addRow(new JLabel("Window Title:"), titleField, true);
		inspector.addRow(new JLabel("Modal Exclusion Type:"),
				modalExclusionComboBox, false);
		ControlGridLayout gridLayout = new ControlGridLayout(3);
		JPanel swingOptions = gridLayout.createGrid(alwaysOnTopCheckbox,
				autoRequestFocusCheckbox, resizableCheckbox,
				isTransparentCheckbox, undecoratedCheckbox);
		inspector.addRow(optionsLabel, swingOptions);
		inspector.addRow(new JLabel("Alpha:"), windowAlpha, false);
		inspector.addRow(new JLabel("Shape:"), shapeComboBox, false);

		inspector.addSeparator();
		inspector.addRow(new JLabel("Mac Window Style:"), styleComboBox, false);

		if (JVM.isMac) {
			JPanel macTitleControls = gridLayout.createGrid(closeableCheckbox,
					documentFileCheckbox, fullScreenCheckbox,
					minimizableCheckbox, documentModifiedCheckbox,
					zoomableCheckbox);
			JPanel macOtherControls = gridLayout.createGrid(
					hideOnDeactivateCheckbox, draggableBackgroundCheckbox,
					fullWindowCheckbox, modalSheetCheckbox, shadowCheckbox,
					transparentTitleBarCheckbox);
			macTitleBarRow = inspector.addRow(
					new JLabel("Mac Title Bar Options:"), macTitleControls);
			inspector.addRow(new JLabel("Other Mac Options:"),
					macOtherControls);
		}

		refreshControls();

		if (small) {
			shrink(inspector.getPanel());
		}
	}

	/**
	 * Reduce the size / font size of a JComponent and its descendants.
	 */
	private void shrink(JComponent jc) {
		if (JVM.isMac) {
			jc.putClientProperty("JComponent.sizeVariant", "small");
		} else {
			Font font = jc.getFont();
			font = font
					.deriveFont((float) Math.round(font.getSize2D() * 4 / 5));
			jc.setFont(font);
		}
		for (Component child : jc.getComponents()) {
			if (child instanceof JComponent)
				shrink((JComponent) child);
		}
	}

	private void attachListener(JCheckBox checkbox, Key<Boolean> key) {
		checkbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				form.setAttribute(key, checkbox.isSelected());
			}
		});
	}

	private void refreshControls() {
		form.removeAttributePropertyChangeListener(formListener);
		xSpinner.removeChangeListener(locSpinnerListener);
		ySpinner.removeChangeListener(locSpinnerListener);
		windowAlpha.removeChangeListener(alphaChangeListener);
		try {
			Class z = form.getAttribute(WindowOptionsForm.KEY_WINDOW_CLASS);
			int i = 2;
			if (z == JFrame.class)
				i = 0;
			else if (z == JDialog.class)
				i = 1;
			classComboBox.setSelectedIndex(i);

			typeComboBox.setSelectedItem(
					form.getAttribute(WindowOptionsForm.KEY_WINDOW_TYPE));
			modalExclusionComboBox.setSelectedItem(form
					.getAttribute(WindowOptionsForm.KEY_MODAL_EXCLUSION_TYPE));
			modalityTypeComboBox.setSelectedItem(
					form.getAttribute(WindowOptionsForm.KEY_MODALITY_TYPE));
			titleField.setText(form.getAttribute(WindowOptionsForm.KEY_TITLE));

			WindowOptionsForm.ShapeType shape = form
					.getAttribute(WindowOptionsForm.KEY_SHAPE);
			i = shape == WindowOptionsForm.ShapeType.NONE ? 0 : 1;
			shapeComboBox.setSelectedIndex(i);

			xSpinner.setValue(form.getAttribute(WindowOptionsForm.KEY_X));
			ySpinner.setValue(form.getAttribute(WindowOptionsForm.KEY_Y));

			undecoratedCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_UNDECORATED));
			autoRequestFocusCheckbox.setSelected(form
					.getAttribute(WindowOptionsForm.KEY_AUTO_REQUEST_FOCUS));
			alwaysOnTopCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_ALWAYS_ON_TOP));
			isTransparentCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_TRANSPARENT));
			resizableCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_RESIZABLE));

			float alpha = form.getAttribute(WindowOptionsForm.KEY_ALPHA);
			windowAlpha.setValue((int) (alpha * 100));

			styleComboBox.setSelectedIndex(
					Arrays.asList(WindowOptionsForm.MAC_STYLES).indexOf(form
							.getAttribute(WindowOptionsForm.KEY_MAC_STYLE)));

			documentModifiedCheckbox.setSelected(form
					.getAttribute(WindowOptionsForm.KEY_MAC_DOCUMENT_MODIFIED));
			shadowCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_MAC_SHADOW));
			minimizableCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_MAC_MINIMIZABLE));
			closeableCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_MAC_CLOSEABLE));
			zoomableCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_MAC_ZOOMABLE));
			documentFileCheckbox.setSelected(form.getAttribute(
					WindowOptionsForm.KEY_MAC_DOCUMENT_FILE) != null);
			fullScreenCheckbox.setSelected(form
					.getAttribute(WindowOptionsForm.KEY_MAC_FULLSCREENABLE));
			draggableBackgroundCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_MAC_DRAGGABLE));
			modalSheetCheckbox.setSelected(
					form.getAttribute(WindowOptionsForm.KEY_MAC_MODAL_SHEET));
			hideOnDeactivateCheckbox.setSelected(form.getAttribute(
					WindowOptionsForm.KEY_MAC_HIDES_ON_DEACTIVATE));
			fullWindowCheckbox.setSelected(form.getAttribute(
					WindowOptionsForm.KEY_MAC_FULL_WINDOW_CONTENT));
			transparentTitleBarCheckbox.setSelected(form.getAttribute(
					WindowOptionsForm.KEY_MAC_TRANSPARENT_TITLE_BAR));
		} finally {
			form.addAttributePropertyChangeListener(formListener);
			xSpinner.addChangeListener(locSpinnerListener);
			ySpinner.addChangeListener(locSpinnerListener);
			windowAlpha.addChangeListener(alphaChangeListener);
			refreshVisibleControls();
		}
	}

	private void refreshVisibleControls() {
		if (form.getAttribute(
				WindowOptionsForm.KEY_WINDOW_CLASS) == JFrame.class) {
			optionsLabel.setText("Frame Options:");
			undecoratedCheckbox.setVisible(true);
			resizableCheckbox.setVisible(true);
			modalityTypeRow.setVisible(false);
			titleRow.setVisible(true);
		} else if (form.getAttribute(
				WindowOptionsForm.KEY_WINDOW_CLASS) == JDialog.class) {
			optionsLabel.setText("Dialog Options:");
			undecoratedCheckbox.setVisible(true);
			resizableCheckbox.setVisible(true);
			modalityTypeRow.setVisible(true);
			titleRow.setVisible(true);
		} else {
			optionsLabel.setText("Window Options:");
			undecoratedCheckbox.setVisible(false);
			resizableCheckbox.setVisible(false);
			modalityTypeRow.setVisible(false);
			titleRow.setVisible(false);
		}
		if (macTitleBarRow != null) {
			macTitleBarRow.setVisible(form.isDecoratable());
		}
	}
}