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
package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.inspector.Inspector;
import com.pump.plaf.QPanelUI;
import com.pump.plaf.QPanelUI.CalloutType;
import com.pump.plaf.button.BevelButtonUI;
import com.pump.swing.FontComboBox;
import com.pump.swing.popover.JPopover;
import com.pump.swing.popup.QPopup;
import com.pump.swing.popup.QPopupFactory;
import com.pump.util.BooleanProperty;

/**
 * This demos JPopover objects.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/JPopoverDemo.png"
 * alt="A screenshot of the JPopoverDemo.">
 */
public class JPopoverDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JPasswordField passwordField = new JPasswordField(15);
	FontComboBox fontComboBox = new FontComboBox();
	JTextField currencyField = new JTextField(15);
	JPopover<JToolTip> passwordPopover;
	JPopover<JToolTip> fontPopover;

	static BooleanProperty capsLock = new BooleanProperty("capsLock");
	static {
		Timer timer = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean b = Toolkit.getDefaultToolkit()
						.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
				capsLock.setValue(b);
			}

		});
		timer.start();
	}

	public JPopoverDemo() {
		configurationLabel.setVisible(false);
		configurationPanel.setVisible(false);

		Inspector layout = new Inspector(examplePanel);
		layout.addRow(new JLabel("Password:"), passwordField, false);
		layout.addRow(new JLabel("Font:"), fontComboBox, false);
		layout.addRow(new JLabel("Currency Input:"), currencyField, false);

		capsLock.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				passwordPopover.refreshPopup();
			}

		});

		passwordField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				passwordPopover.refreshPopup();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				passwordPopover.refreshPopup();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

		});

		passwordField.putClientProperty(QPopup.PROPERTY_CALLOUT_TYPE,
				new CalloutType[] { CalloutType.LEFT_CENTER });

		passwordPopover = new JPopover<JToolTip>(passwordField, new JToolTip(),
				false) {

			@Override
			protected void doRefreshPopup() {
				if (capsLock.getValue()) {
					updateCapsLockWarningToolTip();
				} else {
					updatePasswordStrengthMeterToolTip();
				}
			}

		};

		fontPopover = new JPopover<JToolTip>(fontComboBox, new JToolTip(),
				false) {

			@Override
			protected void doRefreshPopup() {
				getContents().setFont(fontComboBox.getSelectedFont());
			}

		};
		fontPopover.getContents()
				.setTipText("The quick brown fox jumps over the lazy dog.");

		JPanel currencyButtonGrid = new JPanel(new GridBagLayout());
		currencyButtonGrid.setBackground(Color.white);
		GridBagConstraints gbc = new GridBagConstraints();
		String[] currencySigns = new String[] { "\u0024", "\u00A2", "\u00A3",
				"\u00A4", "\u00A5", "\u20A0", "\u20AC", "\u20BF" };
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 4; x++) {
				gbc.gridx = x;
				gbc.gridy = y;
				gbc.weightx = 1;
				gbc.weighty = 1;
				final String s = currencySigns[y * 4 + x];
				JButton b = new JButton(s);
				b.setUI(new BevelButtonUI());
				b.setBorderPainted(false);
				b.setFocusPainted(true);
				b.setContentAreaFilled(false);
				b.setOpaque(false);
				b.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						currencyField.setText(currencyField.getText() + s);
					}

				});
				currencyButtonGrid.add(b, gbc);
				b.setFont(b.getFont().deriveFont(18f));
			}
		}

		new JPopover<JPanel>(currencyField, currencyButtonGrid, true);

	}

	private void updateCapsLockWarningToolTip() {
		passwordField.putClientProperty(QPopup.PROPERTY_CALLOUT_TYPE,
				CalloutType.TOP_LEFT);
		passwordPopover.getContents().setTipText("<html>"
				+ "<font size=\"4\" color=\"#000099\">Caps Lock is on</font><br>"
				+ "Having Caps Lock on may cause you to enter your password incorrectly.<br><br>"
				+ "Press Caps Lock to turn it off before entering your password.</html");
	}

	private void updatePasswordStrengthMeterToolTip() {
		passwordField.putClientProperty(QPopup.PROPERTY_CALLOUT_TYPE,
				CalloutType.LEFT_CENTER);

		// disclaimer: this is just meant to show off what tooltips/popovers
		// can
		// do, this is a flimsy strength meter
		String password = new String(passwordField.getPassword());

		boolean containsNumber = false;
		boolean containsUpperCase = false;
		boolean containsLowerCase = false;
		boolean containsSymbol = false;
		for (int a = 0; a < password.length(); a++) {
			char ch = password.charAt(a);
			if (Character.isDigit(ch)) {
				containsNumber = true;
			} else if (Character.isLetter(ch)) {
				if (Character.isUpperCase(ch)) {
					containsUpperCase = true;
				} else {
					containsLowerCase = true;
				}
			} else {
				containsSymbol = true;
			}
		}
		boolean containsMixedCase = containsUpperCase && containsLowerCase;

		List<String> recommendations = new ArrayList<>();
		if (password.length() <= 6) {
			recommendations.add("Using at least 6 characters.");
		}
		if (!containsNumber) {
			recommendations.add("Including at least one number.");
		}
		if (!containsMixedCase) {
			recommendations.add("Using both uppercase and lowercase.");
		}
		if (!containsSymbol) {
			recommendations.add("Using at least one symbol.");
		}

		String strength = null;
		if (password.length() > 0) {
			// I know the font tag is deprecated, but using a div tag with a
			// style pushed the div onto a new line. Swing's HTML renderer
			// pre-dates HTML5, so I'll just use the font tag and call it
			// done
			// for now.
			if (recommendations.size() == 0) {
				strength = "Password Strength: <font color=\"green\">Strong</font>";
			} else if (recommendations.size() <= 2) {
				strength = "Password Strength: <font color=\"orange\">Medium</font>";
			} else {
				strength = "Password Strength: <font color=\"red\">Weak</font>";
			}
		}

		String tooltip = "<html>\n";
		if (strength != null) {
			tooltip += strength + "\n";
		}

		if (!recommendations.isEmpty()) {
			tooltip += "<p>We recommend:\n<ol>";
			for (String r : recommendations) {
				tooltip += "<li>" + r + "\n";
			}
			tooltip += "</ol>";
		}
		tooltip += "</html>";

		passwordPopover.getContents().setTipText(tooltip);
	}

	@Override
	public String getTitle() {
		return "JPopover Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new Swing component that resembles an enhanced tooltip.\n\nBelow are some examples of components that use JPopovers. Move the mouse over a component or press the tab key to focus on a component to interact with the popover.";
	}

	@Override
	public URL getHelpURL() {
		return JPopoverDemo.class.getResource("jpopoverDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "popover", "balloon", "tooltip", "rollover",
				"hover", "focus", "ux", "ui", "Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { QPopup.class, QPopupFactory.class, JPopover.class,
				QPanelUI.class };
	}
}