package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.data.AttributeDataImpl;
import com.pump.data.Key;
import com.pump.desktop.temp.TempFileManager;
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

	/**
	 * This paints a margin with a Paint.
	 */
	private static class PaintBorder implements Border {
		Paint paint;
		Insets insets;

		public PaintBorder(int margin, Paint paint) {
			Objects.requireNonNull(paint);

			insets = new Insets(margin, margin, margin, margin);
			this.paint = paint;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			Graphics2D g2 = (Graphics2D) g.create();
			Area area = new Area(new Rectangle(x, y, width, height));
			area.subtract(new Area(new Rectangle(x + insets.left,
					y + insets.top, width - insets.left - insets.right,
					height - insets.top - insets.bottom)));
			g2.setPaint(paint);
			g2.fill(area);
			g2.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(insets.top, insets.left, insets.bottom,
					insets.right);
		}

		@Override
		public boolean isBorderOpaque() {
			return paint.getTransparency() == Transparency.OPAQUE;
		}
	}

	JButton showWindowButton = new JButton("Show Window");
	WindowOptionsForm newWindowForm = new WindowOptionsForm();

	public WindowDemo() {
		Inspector inspector = new Inspector(configurationPanel);
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
		currentWindow = w;

		final WindowOptionsForm myForm = new WindowOptionsForm(newWindowForm);
		myForm.configureWindow(w);
		myForm.addAttributePropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				myForm.configureWindow(w);
			}
		});

		JPanel windowContent = new JPanel();
		Inspector inspector = new Inspector(windowContent);
		WindowOptionsFormUI formUI = new WindowOptionsFormUI(myForm, inspector,
				true);
		// you can't change these properties on a currently-displaying window:
		formUI.windowClassRow.setVisible(false);
		formUI.windowTypeRow.setVisible(false);
		formUI.undecoratedCheckbox.setEnabled(false);
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
		//
		//
		// JPanel content = new JPanel(new GridBagLayout());
		//
		// c.insets = new Insets(3, 3, 3, 3);
		// content.add(new JLabel(
		// "This is your new " + classComboBox.getSelectedItem() + "."),
		// c);
		// c.gridy++;
		// c.insets = new Insets(13, 3, 3, 3);
		// content.add(toBackButton, c);
		// c.gridx++;
		// content.add(toFrontButton, c);
		// c.gridx++;
		// content.add(closeButton, c);
		// content.setOpaque(false);
		//
		// if (isTransparentCheckbox.isSelected()) {
		// QPanelUI ui = QPanelUI.createToolTipUI();
		// ui.setCalloutSize(0);
		// ui.setCornerSize(15);
		// content.setUI(ui);
		//
		// JPanel wrapper = new JPanel();
		// wrapper.setOpaque(false);
		// wrapper.add(content);
		// content = wrapper;
		// }
		// content.setBorder(new PaintBorder(2, PlafPaintUtils
		// .getDiagonalStripes(2, new Color(0, 0, 0, 0), Color.gray)));
		//
		// ((RootPaneContainer) w).getContentPane().add(content);

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
	};

	/**
	 * The possible window styles for {@link #KEY_STYLE}.
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

	public void configureWindow(Window w) {
		boolean alphaSupported;
		if (w instanceof JFrame) {
			JFrame f = (JFrame) w;
			if (getAttribute(KEY_UNDECORATED) != f.isUndecorated())
				f.setUndecorated(getAttribute(KEY_UNDECORATED));
			f.setResizable(getAttribute(KEY_RESIZABLE));
			alphaSupported = f.isUndecorated();
		} else if (w instanceof JDialog) {
			JDialog d = (JDialog) w;
			if (getAttribute(KEY_UNDECORATED) != d.isUndecorated())
				d.setUndecorated(getAttribute(KEY_UNDECORATED));
			d.setModalityType(getAttribute(KEY_MODALITY_TYPE));
			d.setResizable(getAttribute(KEY_RESIZABLE));
			alphaSupported = d.isUndecorated();
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

		if (alphaSupported)
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

}

class WindowOptionsFormUI {
	WindowOptionsForm form;

	InspectorRowPanel modalityTypeRow, positionRow, windowClassRow,
			windowTypeRow;

	// Swing options
	JComboBox<String> classComboBox = new JComboBox<>(
			new String[] { "JFrame", "JDialog", "JWindow" });
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

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			refreshControls();
		}

	};

	JSpinner xSpinner = new JSpinner(
			new SpinnerNumberModel(0, -10000, 10000, 10));
	JSpinner ySpinner = new JSpinner(
			new SpinnerNumberModel(0, -10000, 10000, 10));

	public WindowOptionsFormUI(WindowOptionsForm form, Inspector inspector,
			boolean small) {
		this.form = form;

		JPopover.add(windowAlpha, "%");

		shapeComboBox.setToolTipText("setShape(..)");
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

		windowAlpha.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				float f = windowAlpha.getValue() / 100f;
				form.setAttribute(WindowOptionsForm.KEY_ALPHA, f);
			}

		});

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
		xSpinner.addChangeListener(locSpinnerListener);
		ySpinner.addChangeListener(locSpinnerListener);

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

		JPanel macControls = gridLayout.createGrid(hideOnDeactivateCheckbox,
				closeableCheckbox, draggableBackgroundCheckbox,
				documentFileCheckbox, fullScreenCheckbox, fullWindowCheckbox,
				minimizableCheckbox, modalSheetCheckbox,
				documentModifiedCheckbox, shadowCheckbox,
				transparentTitleBarCheckbox, zoomableCheckbox);
		inspector.addRow(new JLabel("Mac Options:"), macControls);

		refreshControls();

		if (small) {
			shrink(inspector.getPanel());
		}
	}

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

			WindowOptionsForm.ShapeType shape = form
					.getAttribute(WindowOptionsForm.KEY_SHAPE);
			i = shape == WindowOptionsForm.ShapeType.NONE ? 0 : 1;
			shapeComboBox.setSelectedItem(i);

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
		} else if (form.getAttribute(
				WindowOptionsForm.KEY_WINDOW_CLASS) == JDialog.class) {
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
}