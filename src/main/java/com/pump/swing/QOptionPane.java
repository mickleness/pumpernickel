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
package com.pump.swing;

import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.pump.plaf.PanelImageUI;
import com.pump.plaf.QOptionPaneUI;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.util.JVM;

/**
 */
public class QOptionPane extends JComponent {
	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "QOptionPaneUI";

	public static final int ICON_WARNING = 0;
	public static final int ICON_ERROR = 1;
	public static final int ICON_QUESTION = 2;
	public static final int ICON_MESSAGE = 3;
	public static final int ICON_NONE = -1;

	public static final String KEY_MAIN_MESSAGE = "QOptionPane.mainMessage";
	public static final String KEY_SECONDARY_MESSAGE = "QOptionPane.secondaryMessage";
	public static final String KEY_ICON = "QOptionPane.icon";
	public static final String KEY_CUSTOM_COMPONENT = "QOptionPane.customComponent";
	public static final String KEY_DIALOG_FOOTER = "QOptionPane.dialogFooter";
	public static final String KEY_DIALOG_TITLE = "QOptionPane.dialogTitle";

	private static final Map<String, Class<?>> clientPropertyTypes = new HashMap<String, Class<?>>();
	{
		clientPropertyTypes.put(KEY_MAIN_MESSAGE, String.class);
		clientPropertyTypes.put(KEY_SECONDARY_MESSAGE, String.class);
		clientPropertyTypes.put(KEY_ICON, Icon.class);
		clientPropertyTypes.put(KEY_CUSTOM_COMPONENT, JComponent.class);
		clientPropertyTypes.put(KEY_DIALOG_FOOTER, DialogFooter.class);
		clientPropertyTypes.put(KEY_DIALOG_TITLE, String.class);
		clientPropertyTypes.put("debug.ghost.image", BufferedImage.class);
	}

	private static final PropertyChangeListener typeCheckPropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			Class<?> type = clientPropertyTypes.get(evt.getPropertyName());
			if (type == null)
				return;

			Object newValue = evt.getNewValue();

			// null values are allowed for all properties
			if (newValue == null)
				return;

			if (type.isInstance(newValue) == false) {
				throw new ClassCastException("the property \""
						+ evt.getPropertyName() + "\" is supposed to map to a "
						+ type.getName());
			}
		}
	};

	/**
	 * Return an icon.
	 * <p>
	 * This is based on the <code>JOptionPane</code> icons.
	 * 
	 * @param type
	 *            one of the five ICON constants: NONE, MESSAGE, WARNING,
	 *            QUESTION, ERROR.
	 * @return an icon representing the indicated message type.
	 */
	public static Icon getIcon(int type) {
		if (type == ICON_NONE) {
			return null;
		} else if (type == ICON_WARNING) {
			return UIManager.getIcon("OptionPane.warningIcon");
		} else if (type == ICON_ERROR) {
			return UIManager.getIcon("OptionPane.errorIcon");
		} else if (type == ICON_MESSAGE) {
			return UIManager.getIcon("OptionPane.messageIcon");
		} else if (type == ICON_QUESTION) {
			return UIManager.getIcon("OptionPane.questionIcon");
		}
		throw new IllegalArgumentException("unrecognized icon type " + type);
	}

	public QOptionPane() {
		addPropertyChangeListener(typeCheckPropertyListener);
		updateUI();
	}

	public QOptionPane(String mainMessage, String secondaryMessage,
			int iconType) {
		this(mainMessage, secondaryMessage, getIcon(iconType),
				DialogFooter.UNDEFINED_OPTION, null);
	}

	public QOptionPane(String mainMessage, String secondaryMessage,
			int iconType, int options, String dialogTitle) {
		this(mainMessage, secondaryMessage, getIcon(iconType), options,
				dialogTitle);
	}

	public QOptionPane(String mainMessage, String secondaryMessage, Icon icon,
			int options, String dialogTitle) {
		this();
		setMainMessage(mainMessage);
		setSecondaryMessage(secondaryMessage);
		setIcon(icon);
		if (options != DialogFooter.UNDEFINED_OPTION) {
			setDialogFooter(DialogFooter.createDialogFooter(options,
					EscapeKeyBehavior.TRIGGERS_CANCEL));
		}
		setDialogTitle(dialogTitle);
	}

	public void setMainMessage(String mainMessage) {
		putClientProperty(KEY_MAIN_MESSAGE, mainMessage);
	}

	public String getMainMessage() {
		return (String) getClientProperty(KEY_MAIN_MESSAGE);
	}

	public void setDialogTitle(String dialogTitle) {
		putClientProperty(KEY_DIALOG_TITLE, dialogTitle);
	}

	public String getDialogTitle() {
		return (String) getClientProperty(KEY_DIALOG_TITLE);
	}

	public void setDialogFooter(DialogFooter dialogFooter) {
		putClientProperty(KEY_DIALOG_FOOTER, dialogFooter);
	}

	public DialogFooter getDialogFooter() {
		return (DialogFooter) getClientProperty(KEY_DIALOG_FOOTER);
	}

	public void setSecondaryMessage(String secondaryMessage) {
		putClientProperty(KEY_SECONDARY_MESSAGE, secondaryMessage);
	}

	public String getSecondaryMessage() {
		return (String) getClientProperty(KEY_SECONDARY_MESSAGE);
	}

	public void setIcon(Icon icon) {
		putClientProperty(KEY_ICON, icon);
	}

	public Icon getIcon() {
		return (Icon) getClientProperty(KEY_ICON);
	}

	public void setCustomComponent(JComponent component) {
		putClientProperty(KEY_CUSTOM_COMPONENT, component);
	}

	public JComponent getCustomComponent() {
		return (JComponent) getClientProperty(KEY_CUSTOM_COMPONENT);
	}

	@Override
	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID,
					"com.pump.plaf.BasicQOptionPaneUI");
		}
		try {
			String className = UIManager.getDefaults().getString(uiClassID);
			Class<?> classObject = Class.forName(className);
			Constructor<?> constructor = classObject
					.getConstructor(new Class[] {});
			setUI((QOptionPaneUI) constructor.newInstance(new Object[] {}));
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable t) {
			RuntimeException e = new RuntimeException();
			e.initCause(t);
			throw e;
		}
	}

	public void setUI(QOptionPaneUI ui) {
		super.setUI(ui);
	}

	public QOptionPaneUI getUI() {
		return (QOptionPaneUI) ui;
	}

	/**
	 * 
	 * @param owner
	 * @param useSheets
	 *            if this is true then sheets should be used to display this
	 *            dialog. Note this is only supported in Java 1.6+.
	 */
	public int showDialog(Frame owner, boolean useSheets) {
		DialogFooter footer = getDialogFooter();
		if (footer != null)
			footer.reset();
		String dialogTitle = getDialogTitle();
		JDialog dialog = new JDialog(owner, dialogTitle, true);
		if (useSheets && JVM.getMajorJavaVersion() >= 1.6f) {
			dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
			dialog.getRootPane().putClientProperty(
					"apple.awt.documentModalSheet", Boolean.valueOf(useSheets));
		}
		dialog.getContentPane().add(createDebugPanel());
		dialog.setResizable(false);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		if (footer != null) {
			JComponent jc = footer.getLastSelectedComponent();
			Integer i = (Integer) jc
					.getClientProperty(DialogFooter.PROPERTY_OPTION);
			if (i != null)
				return i.intValue();
		}
		return JComponent.UNDEFINED_CONDITION;
	}

	public JInternalFrame showDialog(JDesktopPane desktopPane) {
		String title = getDialogTitle();
		DialogFooter footer = getDialogFooter();
		boolean closeable = true;
		if (footer != null) {
			closeable = footer.containsButton(DialogFooter.CANCEL_OPTION);
		}
		JInternalFrame dialog = new JInternalFrame(title, false, closeable);
		dialog.getContentPane().add(createDebugPanel());
		dialog.pack();
		dialog.setVisible(true);
		desktopPane.add(dialog);
		return dialog;
	}

	private JComponent createDebugPanel() {
		BufferedImage img = (BufferedImage) getClientProperty(
				"debug.ghost.image");

		if (img == null)
			return this;

		final CardLayout cardLayout = new CardLayout();
		final JPanel panel = new JPanel(cardLayout);

		JPanel imagePanel = new JPanel();
		imagePanel.setUI(new PanelImageUI(img));

		JPanel paneWrapper = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		paneWrapper.add(this, c);

		panel.add(paneWrapper, "real");
		panel.add(imagePanel, "debug");

		Timer timer = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String mode = (System.currentTimeMillis() % 4000) > 2000
						? "real"
						: "debug";
				cardLayout.show(panel, mode);
			}
		});
		timer.start();

		return panel;
	}
}