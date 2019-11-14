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
package com.pump.jar;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.inspector.Inspector;
import com.pump.io.SuffixFilenameFilter;
import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;
import com.pump.swing.TextFieldPrompt;

public class KeyStoreEditor extends JPanel {
	private static final long serialVersionUID = 1L;

	public static KeyStoreEditor create(Frame parentWindow) {
		KeyStoreEditor editor = new KeyStoreEditor();
		String dialogTitle = "Create Keystore";
		Icon icon = QDialog.getIcon(QDialog.PLAIN_MESSAGE);
		QDialog.showDialog(parentWindow, dialogTitle, icon, editor,
				editor.footer, true, null, null);
		JComponent button = editor.footer.getLastSelectedComponent();
		if (button == editor.footer.getButton(DialogFooter.OK_OPTION)) {
			FileDialog fd = new FileDialog(parentWindow, "Create Keystore",
					FileDialog.SAVE);
			fd.setFilenameFilter(new SuffixFilenameFilter("jks"));
			fd.setFile("keystore.jks");
			fd.pack();
			fd.setLocationRelativeTo(null);
			fd.setVisible(true);
			if (fd.getFile() == null)
				return null;
			editor.jksFile = new File(fd.getDirectory() + fd.getFile());
			editor.create(false);
			return editor;
		} else {
			return null;
		}
	}

	File jksFile = null;
	Inspector layout = new Inspector(this);
	JTextField alias = new JTextField(28);
	JTextField commonName = new JTextField(28);
	JTextField orgName = new JTextField(28);
	JTextField departmentName = new JTextField(28);
	JTextField city = new JTextField(28);
	JTextField state = new JTextField(28);
	JTextField country = new JTextField();
	JComboBox<Integer> keySize = new JComboBox<Integer>();
	JPasswordField keystorePassword1 = new JPasswordField();
	JPasswordField keystorePassword2 = new JPasswordField();
	JPasswordField aliasPassword1 = new JPasswordField();
	JPasswordField aliasPassword2 = new JPasswordField();
	JCheckBox selfsignCheckbox = new JCheckBox("Selfsign Certificate", true);
	DialogFooter footer = DialogFooter.createDialogFooter(
			DialogFooter.OK_CANCEL_OPTION,
			DialogFooter.EscapeKeyBehavior.TRIGGERS_CANCEL);
	JCheckBox aliasPasswordCheckbox = new JCheckBox("Alias Includes Password");

	Set<JTextField> requiredFields = new HashSet<JTextField>();
	DocumentListener docListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			refreshUI();
		}

	};

	public KeyStoreEditor() {
		layout.addRow(new JLabel("Common Name:"), commonName, true);
		layout.addRow(new JLabel("Organization:"), orgName, true);
		layout.addRow(new JLabel("Department:"), departmentName, true);
		layout.addRow(new JLabel("City:"), city, true);
		layout.addRow(new JLabel("State / Province:"), state, true);
		layout.addRow(new JLabel("Country:"), country, true);
		layout.addRow(new JLabel("Key Size:"), keySize, false);
		layout.addRow(new JLabel("Keystore Password:"), keystorePassword1, true);
		layout.addRow(new JLabel("Verify Password:"), keystorePassword2, true);
		layout.addSeparator();
		layout.addRow(new JLabel("Alias:"), alias, true);
		layout.addRow(null, aliasPasswordCheckbox, true);
		layout.addRow(new JLabel("Alias Password:"), aliasPassword1, true);
		layout.addRow(new JLabel("Verify Password:"), aliasPassword2, true);
		layout.addSeparator();
		layout.addRow(null, selfsignCheckbox, false);

		new TextFieldPrompt(commonName, "www.example.com or *.example.com");
		new TextFieldPrompt(orgName, "Example, Inc or Jane Doe");
		new TextFieldPrompt(departmentName, "Web Security, or leave blank");
		new TextFieldPrompt(city, "Dallas");
		new TextFieldPrompt(state, "Texas");
		new TextFieldPrompt(country, "USA");
		keySize.addItem(2048);
		keySize.setEditable(true);
		new TextFieldPrompt(alias, "susan");

		requiredFields.add(commonName);
		requiredFields.add(orgName);
		requiredFields.add(city);
		requiredFields.add(state);
		requiredFields.add(country);
		requiredFields.add(alias);
		requiredFields.add(keystorePassword1);
		requiredFields.add(keystorePassword2);
		requiredFields.add(aliasPassword1);
		requiredFields.add(aliasPassword2);

		aliasPasswordCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshUI();
			}
		});

		for (JTextField f : requiredFields) {
			f.getDocument().addDocumentListener(docListener);
		}
		refreshUI();
	}

	protected void refreshUI() {

		boolean password1 = new String(keystorePassword1.getPassword())
				.equals(new String(keystorePassword2.getPassword()));
		keystorePassword2.setForeground(password1 ? Color.black : Color.red);

		boolean password2 = new String(aliasPassword1.getPassword())
				.equals(new String(aliasPassword2.getPassword()));

		aliasPassword1.setEnabled(aliasPasswordCheckbox.isSelected());
		aliasPassword2.setEnabled(aliasPasswordCheckbox.isSelected());
		if (aliasPasswordCheckbox.isSelected()) {
			aliasPassword1.setForeground(Color.black);
			aliasPassword2.setForeground(password2 ? Color.black : Color.red);
			requiredFields.add(aliasPassword1);
			requiredFields.add(aliasPassword2);
		} else {
			aliasPassword1.setForeground(Color.gray);
			aliasPassword2.setForeground(Color.gray);
			requiredFields.remove(aliasPassword1);
			requiredFields.remove(aliasPassword2);
		}

		boolean ok = true;
		for (JTextField f : requiredFields) {
			String s = f.getText();
			if (s.length() == 0) {
				ok = false;
			}
		}

		footer.getButton(DialogFooter.OK_OPTION).setEnabled(ok);
	}

	public void create(boolean blocking) {
		ProcessBuilderThread keyGenBuilder = new ProcessBuilderThread(
				"keytool", true);
		List<String> cmd = new ArrayList<String>();
		cmd.add("keytool");
		cmd.add("-genkeypair");
		cmd.add("-storepass");
		cmd.add(new String(keystorePassword1.getPassword()));
		cmd.add("-keypass");
		if (aliasPasswordCheckbox.isSelected()) {
			cmd.add(new String(aliasPassword1.getPassword()));
		} else {
			// I'm unclear if this is effectively what the keytool command does
			// when you
			// press the return key to skip this step?
			cmd.add(new String(keystorePassword1.getPassword()));
		}
		cmd.add("-dname");
		cmd.add("CN=" + commonName.getText() + ", O=" + orgName.getText()
				+ ", L=" + city.getText() + ", ST=" + state.getText() + ", C="
				+ country.getText());
		cmd.add("-alias");
		cmd.add(alias.getText());
		cmd.add("-keyalg");
		cmd.add("RSA");
		cmd.add("-keysize");
		cmd.add(keySize.getSelectedItem().toString());
		cmd.add("-keystore");
		cmd.add(jksFile.getAbsolutePath());

		keyGenBuilder.processBuilder
				.command(cmd.toArray(new String[cmd.size()]));

		if (selfsignCheckbox.isSelected()) {
			keyGenBuilder.postRunnable = new Runnable() {
				ProcessBuilderThread selfCertBuilder = new ProcessBuilderThread(
						"keytool", true);

				public void run() {
					selfCertBuilder.processBuilder.command("keytool",
							"-selfcert", "-keystore",
							jksFile.getAbsolutePath(), "-alias", alias
									.getText(), "-storepass", new String(
									keystorePassword1.getPassword()),
							"-keypass",
							new String(aliasPassword1.getPassword()));
					selfCertBuilder.start(true);
				}
			};
		}
		keyGenBuilder.start(blocking);
	}

	public File getKeyStoreFile() {
		return jksFile;
	}

	public String getAlias() {
		return alias.getText();
	}
}