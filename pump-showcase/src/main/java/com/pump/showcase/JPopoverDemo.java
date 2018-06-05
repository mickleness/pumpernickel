package com.pump.showcase;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.plaf.BevelButtonUI;
import com.pump.plaf.QPanelUI;
import com.pump.plaf.QPanelUI.CalloutType;
import com.pump.swing.FontComboBox;
import com.pump.swing.JPopover;
import com.pump.swing.QPopup;
import com.pump.swing.QPopupFactory;

public class JPopoverDemo extends JPanel implements ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JTextComponent textBox = new JTextPane();

	JPanel samples = new JPanel();
	JPasswordField passwordField = new JPasswordField(15);
	FontComboBox fontComboBox = new FontComboBox();
	JTextField currencyField = new JTextField(15);
	JPopover<JToolTip> passwordPopover;
	JPopover<JToolTip> fontPopover;

	public JPopoverDemo() {

		samples.setUI(QPanelUI.createBoxUI());
		InspectorLayout layout = new InspectorGridBagLayout(samples);
		layout.addRow(new JLabel("Password:"), passwordField, false);
		layout.addRow(new JLabel("Font:"), fontComboBox, false);
		layout.addRow(new JLabel("Currency Input:"), currencyField, false);

		passwordField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updatePasswordTooltip();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updatePasswordTooltip();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(textBox, c);
		c.gridy++;

		JLabel sampleLabel = new JLabel("Samples:");
		sampleLabel.setFont(sampleLabel.getFont().deriveFont(
				sampleLabel.getFont().getSize2D() - 2));
		c.insets = new Insets(15, 4, 0, 4);
		add(sampleLabel, c);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(0, 4, 4, 4);
		add(samples, c);

		passwordField.putClientProperty(QPopup.PROPERTY_CALLOUT_TYPE,
				new CalloutType[] { CalloutType.LEFT_CENTER });

		passwordPopover = new JPopover<JToolTip>(passwordField, new JToolTip(),
				false) {

			@Override
			protected void refreshPopup() {
				updatePasswordTooltip();
			}

		};

		fontPopover = new JPopover<JToolTip>(fontComboBox, new JToolTip(),
				false) {

			@Override
			protected void refreshPopup() {
				getContents().setTipText(
						"The quick brown fox jumps over the lazy dog.");
				getContents().setFont((Font) fontComboBox.getSelectedItem());
			}

		};

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

		textBox.setText("Below are some examples of components that use JPopovers. Move the mouse over a component or press the tab key to focus on a component to interact with the popover.");
		textBox.setEditable(false);
		textBox.setOpaque(false);

	}

	private void updatePasswordTooltip() {
		// disclaimer: this is just meant to show off what tooltips/popovers can
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
			// pre-dates HTML5, so I'll just use the font tag and call it done
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
			tooltip += "<p>We recommend:\n<ul>";
			for (String r : recommendations) {
				tooltip += "<li>" + r + "\n";
			}
			tooltip += "</ul>";
		}
		tooltip += "</html>";

		passwordPopover.getContents().setTipText(tooltip);
	}

	@Override
	public String getTitle() {
		return "JPopover Demo";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public boolean isSeparatorVisible() {
		return true;
	}

}