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
package com.pump.plaf;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.plaf.ComponentUI;

import com.pump.swing.DialogFooter;
import com.pump.swing.QOptionPane;

/**
 * <img src=
 * "https://docs.google.com/drawings/pub?id=12ZjxXP-FsULwFNDAsy9sA-XnTfT1XKp6TJ70QIZaVvE&w=472&h=339"
 * alt="Layout Diagram">
 *
 */
public abstract class QOptionPaneUI extends ComponentUI {
	private static final String KEY_MAIN_MESSAGE_COMPONENT = "QOptionPaneUI.mainMessageComponent";
	private static final String KEY_SECONDARY_MESSAGE_COMPONENT = "QOptionPaneUI.secondaryMessageComponent";
	private static final String KEY_ICON_COMPONENT = "QOptionPaneUI.iconComponent";
	private static final String KEY_CUSTOM_COMPONENT_CONTAINER = "QOptionPaneUI.customComponentContainer";
	private static final String KEY_FOOTER_CONTAINER = "QOptionPaneUI.dialogFooterContainer";
	private static final String KEY_FOOTER_SEPARATOR = "QOptionPaneUI.dialogFooterSeparator";
	private static final String KEY_UPPER_BODY = "QOptionPaneUI.upperBody";

	PropertyChangeListener optionPanePropertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			QOptionPane pane = (QOptionPane) evt.getSource();
			Dimension originalPreferredSize = pane.getPreferredSize();
			if (QOptionPane.KEY_CUSTOM_COMPONENT.equals(evt.getPropertyName())) {
				updateCustomComponent(pane);
			} else if (QOptionPane.KEY_DIALOG_FOOTER.equals(evt
					.getPropertyName())) {
				updateFooter(pane);
			} else if (QOptionPane.KEY_DIALOG_TITLE.equals(evt
					.getPropertyName())) {
				updateDialogTitle(pane);
			} else if (QOptionPane.KEY_ICON.equals(evt.getPropertyName())) {
				updateIcon(pane);
			} else if (QOptionPane.KEY_MAIN_MESSAGE.equals(evt
					.getPropertyName())) {
				updateMainMessage(pane);
			} else if (QOptionPane.KEY_SECONDARY_MESSAGE.equals(evt
					.getPropertyName())) {
				updateSecondaryMessage(pane);
			}
			Dimension newPreferredSize = pane.getPreferredSize();
			considerResizingOptionPane(pane, originalPreferredSize,
					newPreferredSize);
		}
	};

	public QOptionPaneUI() {
	}

	protected void considerResizingOptionPane(QOptionPane pane,
			Dimension oldSize, Dimension newSize) {
		// TODO: implement this
	}

	public JTextArea getMainMessageTextArea(QOptionPane optionPane) {
		JTextArea textArea = (JTextArea) optionPane
				.getClientProperty(KEY_MAIN_MESSAGE_COMPONENT);
		if (textArea == null) {
			// subclasses should customize the width
			textArea = new JTextArea("");
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			optionPane.putClientProperty(KEY_MAIN_MESSAGE_COMPONENT, textArea);
		}
		return textArea;
	}

	public JPanel getCustomComponentContainer(QOptionPane optionPane) {
		JPanel panel = (JPanel) optionPane
				.getClientProperty(KEY_CUSTOM_COMPONENT_CONTAINER);
		if (panel == null) {
			panel = new JPanel();
			panel.setOpaque(false);
			optionPane.putClientProperty(KEY_CUSTOM_COMPONENT_CONTAINER, panel);
		}
		return panel;
	}

	public JPanel getFooterContainer(QOptionPane optionPane) {
		JPanel panel = (JPanel) optionPane
				.getClientProperty(KEY_FOOTER_CONTAINER);
		if (panel == null) {
			panel = new JPanel();
			panel.setOpaque(false);
			optionPane.putClientProperty(KEY_FOOTER_CONTAINER, panel);
		}
		return panel;
	}

	public JPanel getUpperBody(QOptionPane optionPane) {
		JPanel panel = (JPanel) optionPane.getClientProperty(KEY_UPPER_BODY);
		if (panel == null) {
			panel = new JPanel();
			panel.setOpaque(false);
			optionPane.putClientProperty(KEY_UPPER_BODY, panel);
		}
		return panel;
	}

	public JSeparator getFooterSeparator(QOptionPane optionPane) {
		JSeparator s = (JSeparator) optionPane
				.getClientProperty(KEY_FOOTER_SEPARATOR);
		if (s == null) {
			s = new JSeparator();
			s.setOpaque(false);
			optionPane.putClientProperty(KEY_FOOTER_SEPARATOR, s);
		}
		return s;
	}

	public JTextArea getSecondaryMessageTextArea(QOptionPane optionPane) {
		JTextArea textArea = (JTextArea) optionPane
				.getClientProperty(KEY_SECONDARY_MESSAGE_COMPONENT);
		if (textArea == null) {
			// subclasses should customize the width
			textArea = new JTextArea("");
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			optionPane.putClientProperty(KEY_SECONDARY_MESSAGE_COMPONENT,
					textArea);
		}
		return textArea;
	}

	public JLabel getIconLabel(QOptionPane optionPane) {
		JLabel label = (JLabel) optionPane
				.getClientProperty(KEY_ICON_COMPONENT);
		if (label == null) {
			label = new JLabel(optionPane.getIcon());
			optionPane.putClientProperty(KEY_ICON_COMPONENT, label);
		}
		return label;
	}

	protected abstract void installComponents(QOptionPane optionPane);

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.addPropertyChangeListener(optionPanePropertyListener);
		QOptionPane optionPane = (QOptionPane) c;
		installComponents(optionPane);
		updateCustomComponent(optionPane);
		updateIcon(optionPane);
		updateMainMessage(optionPane);
		updateSecondaryMessage(optionPane);
		updateFooter(optionPane);
		updateDialogTitle(optionPane);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removePropertyChangeListener(optionPanePropertyListener);
	}

	protected void updateCustomComponent(QOptionPane optionPane) {
		JPanel customCompContainer = getCustomComponentContainer(optionPane);
		JComponent comp = optionPane.getCustomComponent();
		customCompContainer.removeAll();
		if (comp == null) {
			customCompContainer.setVisible(false);
		} else {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			customCompContainer.setLayout(new GridBagLayout());
			customCompContainer.add(comp, gbc);
			customCompContainer.setVisible(true);
		}
	}

	protected void updateDialogTitle(QOptionPane optionPane) {
	}

	protected void updateFooter(QOptionPane optionPane) {
		JPanel footerContainer = getFooterContainer(optionPane);
		DialogFooter dialogFooter = optionPane.getDialogFooter();
		footerContainer.removeAll();
		if (dialogFooter == null) {
			footerContainer.setVisible(false);
		} else {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			footerContainer.setLayout(new GridBagLayout());
			footerContainer.add(dialogFooter, gbc);
			footerContainer.setVisible(true);
			dialogFooter.setOpaque(false);
		}
	}

	protected void updateIcon(QOptionPane pane) {
		Icon icon = pane.getIcon();
		JLabel label = getIconLabel(pane);
		if (icon == null) {
			label.setIcon(null);
			label.setVisible(false);
		} else {
			label.setIcon(icon);
			label.setVisible(true);
		}
	}

	protected void updateMainMessage(QOptionPane pane) {
		String mainMessage = pane.getMainMessage();
		JTextArea textArea = getMainMessageTextArea(pane);
		if (mainMessage == null) {
			textArea.setText("");
			textArea.setVisible(false);
		} else {
			textArea.setText(mainMessage);
			textArea.setVisible(true);
		}
	}

	protected void updateSecondaryMessage(QOptionPane pane) {
		String secondaryMessage = pane.getSecondaryMessage();
		JTextArea textArea = getSecondaryMessageTextArea(pane);
		if (secondaryMessage == null) {
			textArea.setText("");
			textArea.setVisible(false);
		} else {
			textArea.setText(secondaryMessage);
			textArea.setVisible(true);
		}
	}
}