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

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Manifest;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.pump.inspector.InspectorGridBagLayout;
import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;
import com.pump.swing.TextFieldPrompt;

/**
 * An editor for Manifests.
 *
 */
public class ManifestEditor extends JPanel {
	private static final long serialVersionUID = 1L;

	Manifest currentManifest;

	InspectorGridBagLayout layout = new InspectorGridBagLayout(this);
	JComboBox<String> permissionsComboBox = new JComboBox<String>(new String[] {
			"Undefined", "Sandbox", "All-Permissions" });
	JTextField codebaseField = new JTextField();
	JTextField appNameField = new JTextField();
	JTextField mainClassField = new JTextField();
	JTextField classPathField = new JTextField();
	JTextField createdByField = new JTextField();
	JTextField splashField = new JTextField();
	JTextField appLibAllowCodebaseField = new JTextField();
	JTextField callerAllowCodebaseField = new JTextField();
	JTextField entryPointField = new JTextField();
	JComboBox<String> trustedOnlyComboBox = new JComboBox<String>(new String[] {
			"Undefined", "True", "False" });
	JComboBox<String> trustedLibraryComboBox = new JComboBox<String>(
			new String[] { "Undefined", "True", "False" });

	public ManifestEditor() {
		addManifestAttribute(layout, "Main-Class", mainClassField,
				"com.foo.Widget");
		addManifestAttribute(layout, "Created-By", createdByField,
				"1.7.0_06 (Oracle Corporation)");
		addManifestAttribute(layout, "Class-Path", classPathField,
				"jar1-name directory-name/jar2-name");
		addManifestAttribute(layout, "SplashScreen-Image", splashField,
				"splashdemo/splash.png");
		layout.addRow(
				JarWriterApp
						.createHeader("Security Manifest Attributes for Applets / Web Start"),
				SwingConstants.CENTER, true);
		layout.addRow(
				new JLabel(
						"These attributes apply to signed applets and Java Web Start applications."),
				SwingConstants.CENTER, true);
		layout.addRow(
				new JLabel(
						"These attributes are ignored for stand-alone Java applications."),
				SwingConstants.CENTER, true);
		addManifestAttribute(layout, "Permissions", permissionsComboBox);
		addManifestAttribute(layout, "Codebase", codebaseField,
				"https://*.example.com");
		addManifestAttribute(layout, "Application-Name", appNameField,
				"Signed RIAs use this in security prompts as a title to inform user choices.");
		addManifestAttribute(layout, "Entry-Point", entryPointField,
				"apps.test.TestUI apps.test.TestCLI");
		addManifestAttribute(layout, "Trusted-Only", trustedOnlyComboBox);
		addManifestAttribute(layout, "Trusted-Library", trustedLibraryComboBox);

		JPanel miniPanel = new JPanel();
		InspectorGridBagLayout miniLayout = new InspectorGridBagLayout(
				miniPanel);
		addManifestAttribute(miniLayout,
				"Application-Library-Allowable-Codebase",
				appLibAllowCodebaseField, "https://*.example.com");
		addManifestAttribute(miniLayout, "Caller-Allowable-Codebase",
				callerAllowCodebaseField, "https://*.example.com");
		layout.addRow(miniPanel, SwingConstants.CENTER, true);
	}

	abstract class Attribute {
		public abstract void read(Manifest manifest);

		public abstract void write(Manifest manifest);
	}

	class FieldAttribute extends Attribute {
		JTextField field;
		String attrName;

		FieldAttribute(JTextField field, String attributeName) {
			this.field = field;
			this.attrName = attributeName;
		}

		public void read(Manifest manifest) {
			String s = manifest.getMainAttributes().getValue(attrName);
			if (s == null) {
				field.setText("");
			} else {
				field.setText(s);
			}
		}

		public void write(Manifest manifest) {
			String s = field.getText();
			if (s.length() == 0) {
				manifest.getMainAttributes().remove(attrName);
			} else {
				manifest.getMainAttributes().putValue(attrName, s);
			}
		}
	}

	class ComboBoxAttribute extends Attribute {
		JComboBox<String> comboBox;
		String attrName;

		ComboBoxAttribute(JComboBox<String> comboBox, String attributeName) {
			this.comboBox = comboBox;
			this.attrName = attributeName;
		}

		public void read(Manifest manifest) {
			String s = manifest.getMainAttributes().getValue(attrName);
			if (s == null || s.length() == 0) {
				if (comboBox.getModel().getElementAt(0).equals("Undefined")) {
					comboBox.setSelectedIndex(0);
				} else {
					comboBox.setSelectedIndex(-1);
				}
			} else {
				comboBox.setSelectedItem(s);
			}
		}

		public void write(Manifest manifest) {
			int i = comboBox.getSelectedIndex();
			if (i == -1
					|| "Undefined".equals(comboBox.getModel().getElementAt(i))) {
				manifest.getMainAttributes().remove(attrName);
			} else {
				manifest.getMainAttributes().putValue(attrName,
						comboBox.getModel().getElementAt(i));
			}
		}
	}

	Set<Attribute> attributes = new HashSet<Attribute>();

	private void addManifestAttribute(InspectorGridBagLayout layout,
			String attrName, JTextField field, String prompt) {
		attributes.add(new FieldAttribute(field, attrName));
		layout.addRow(new JLabel(attrName + ":"), field, true);
		new TextFieldPrompt(field, prompt);
	}

	private void addManifestAttribute(InspectorGridBagLayout layout,
			String attrName, JComboBox<String> comboBox) {
		attributes.add(new ComboBoxAttribute(comboBox, attrName));
		layout.addRow(new JLabel(attrName + ":"), comboBox, false);
	}

	/**
	 * Load settings from a Manifest into the UI.
	 * 
	 * @param manifest
	 */
	public void loadManifest(Manifest manifest) {
		currentManifest = new Manifest(manifest);
		for (Attribute a : attributes) {
			a.read(manifest);
		}
	}

	/**
	 * Create a Manifest based on the settings in the UI.
	 */
	public Manifest createManifest() {
		for (Attribute a : attributes) {
			a.write(currentManifest);
		}
		return currentManifest;
	}

	/**
	 * Use a modal dialog to edit a Manifest.
	 * 
	 * @param frame
	 *            the frame this dialog belongs to.
	 * @param existingManifest
	 *            the Manifest to edit.
	 * @return the clarified Manifest, or null if the user cancelled the dialog.
	 */
	public static Manifest showDialog(JFrame frame, Manifest existingManifest) {
		ManifestEditor editor = new ManifestEditor();
		editor.loadManifest(existingManifest);
		JScrollPane scrollPane = new JScrollPane(editor,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(600, 500));
		int choice = QDialog.showDialog(frame, "Manifest Editor",
				QDialog.PLAIN_MESSAGE, "", // boldMessage,
				"", // plainMessage
				scrollPane, null, // lowerLeftComponent,
				DialogFooter.OK_CANCEL_OPTION, DialogFooter.OK_OPTION, null, // dontShowKey,
				null, // alwaysApplyKey,
				DialogFooter.EscapeKeyBehavior.TRIGGERS_CANCEL);
		if (choice == DialogFooter.OK_OPTION) {
			return editor.createManifest();
		}
		return null;
	}
}