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
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.pump.UserCancelledException;
import com.pump.blog.Blurb;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.io.ConsoleLogger;
import com.pump.io.IOUtils;
import com.pump.io.SuffixFilenameFilter;
import com.pump.swing.AutocompleteTextField.DefaultSuggestionModel;
import com.pump.swing.BasicConsole;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.ContainerProperties;
import com.pump.swing.ContextualMenuHelper;
import com.pump.swing.DialogFooter;
import com.pump.swing.FilePathTextField;
import com.pump.swing.JThrobber;
import com.pump.swing.QDialog;
import com.pump.swing.SectionContainer.Section;
import com.pump.swing.TextFieldPrompt;

/**
 * A simple app that writes jars from a large codebase, adding only the class
 * files that are necessary for a particular main method.
 */
@Blurb(title = "Jars: Building Concise Jars", releaseDate = "January 2009", summary = "If I used an ant script or Eclipse's built-in jar-exporting widget: I'd need to "
		+ "specify up front the classes I want to put into a jar.\n"
		+ "<p>This class uses the <code>javac</code> command to figure that out for me.")
public class JarWriterApp extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final String KEY_SETUP_ENTRY = "META-INF/BRIC/JAR-SETUP.TXT";

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ConsoleLogger("JarWriter Log.txt");
				try {
					String lf = UIManager.getSystemLookAndFeelClassName();
					UIManager.setLookAndFeel(lf);
				} catch (Throwable e) {
					e.printStackTrace();
				}

				JarWriterApp app = new JarWriterApp();
				app.pack();
				app.setVisible(true);
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	static SourcePathCollection sourcepathCollection = new SourcePathCollection();
	static KeyStoreCollection keyStoreCollection = new KeyStoreCollection();

	List<File> auxiliarySourcepaths = new ArrayList<File>();
	List<File> auxiliaryJars = new ArrayList<File>();
	JThrobber sourcepathProgress = new JThrobber();
	JTextField classesField = new JTextField();
	FilePathTextField sourcepathField = new FilePathTextField();
	FilePathTextField keyStoreField = new FilePathTextField();
	JButton createKeyStoreButton = new JButton("Create...");
	JTextField aliasField = new JTextField(20);
	JTextField tsaField = new JTextField("http://timestamp.digicert.com", 20);
	JPasswordField keyStorePassword = new JPasswordField(20);
	JPasswordField aliasPassword = new JPasswordField(20);
	JPanel setupPanel = new JPanel();
	JPanel signPanel = new JPanel();
	InspectorGridBagLayout signLayout = new InspectorGridBagLayout(signPanel);
	InspectorGridBagLayout setupLayout = new InspectorGridBagLayout(setupPanel);
	JCheckBox sealed = new JCheckBox("Seal JAR");
	JCheckBox includeJava = new JCheckBox(".java Files", true);
	JCheckBox includeClass = new JCheckBox(".class Files", true);
	JCheckBox includeOther = new JCheckBox("Other Files", true);
	JCheckBox includeConfig = new JCheckBox("Config", true);
	JCheckBox includeBricLicense = new JCheckBox("com.bric BSD License", true);
	JComboBox<String> jvmComboBox = new JComboBox<String>(
			new String[] { "1.8" });
	JButton createJarButton = new JButton("Create...");
	JThrobber createJarThrobber = new JThrobber();
	JPanel manifestPanel = new JPanel(new GridBagLayout());
	JButton editManifestButton = new JButton("More...");
	JTextArea manifestTextArea = new JTextArea("Manifest-Version: 1.0");
	CollapsibleContainer collapsibleContainer = new CollapsibleContainer();
	Section setupSection, manifestSection, signSection, consoleSection;
	JButton browseClassesButton = new JButton("Browse...");
	JRadioButton jarChoiceAskRadioButton = new JRadioButton(
			"Prompt User for Choice", true);
	JRadioButton jarChoiceBundleRadioButton = new JRadioButton(
			"Always Bundle All Jars", false);

	DocumentListener sourcepathFieldListener = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			String s = sourcepathField.getText();
			File file = new File(s);
			if (file.exists()) {
				setSourcePath(file);
			}
		}

		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
	};

	public JarWriterApp() {
		super("JarWriterApp");

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		getContentPane().add(collapsibleContainer, c);
		c.gridy++;
		c.weighty = 0;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		JPanel bottomRow = new JPanel(new GridBagLayout());
		getContentPane().add(bottomRow, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.CENTER;
		bottomRow.add(createJarButton, c);
		c.anchor = GridBagConstraints.EAST;
		bottomRow.add(createJarThrobber, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.SOUTHEAST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(4, 4, 4, 4);
		manifestPanel.add(editManifestButton, c);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		manifestPanel.add(new JScrollPane(manifestTextArea), c);

		includeBricLicense.setVisible(false);

		setupSection = collapsibleContainer.addSection("setup", "Setup");
		manifestSection = collapsibleContainer.addSection("Manifest",
				"Manifest");
		signSection = collapsibleContainer.addSection("sign", "JAR Signing");
		consoleSection = collapsibleContainer.addSection("console", "Console");

		setupSection.setProperty(CollapsibleContainer.VERTICAL_WEIGHT, 0);
		manifestSection.setProperty(CollapsibleContainer.VERTICAL_WEIGHT, 1);
		signSection.setProperty(CollapsibleContainer.VERTICAL_WEIGHT, 0);
		consoleSection.setProperty(CollapsibleContainer.VERTICAL_WEIGHT, 2);

		install(setupSection, setupPanel, false);
		install(manifestSection, manifestPanel, false);
		install(signSection, signPanel, false);
		install(consoleSection, new BasicConsole(true, false), true);

		// TODO: also add drag-and-drop for jars to invoke loadJar
		ContextualMenuHelper.add(setupPanel, "Setup using existing jar...",
				new Runnable() {
					public void run() {
						try {
							loadJar();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		setupLayout.addRow(new JLabel("Source Path:"), sourcepathField, true,
				sourcepathProgress);
		setupLayout.addRow(
				new JLabel("Include:"),
				wrap(includeClass, includeJava, includeOther, includeConfig,
						includeBricLicense), false);
		setupLayout.addRow(new JLabel("Jar Dependencies:"),
				wrap(jarChoiceAskRadioButton, jarChoiceBundleRadioButton),
				false);
		setupLayout.addRow(new JLabel("Compiler Version:"), jvmComboBox, false);
		setupLayout.addRow(new JLabel("Classes:"), classesField, true,
				browseClassesButton);

		ButtonGroup bg = new ButtonGroup();
		bg.add(jarChoiceAskRadioButton);
		bg.add(jarChoiceBundleRadioButton);

		jarChoiceAskRadioButton.setName("jar-choice-prompt-user");
		jarChoiceBundleRadioButton.setName("jar-choice-always-bundle");

		includeClass.setToolTipText("Include all compiled .class files.");
		includeJava.setToolTipText("Include all required .java files.");
		includeOther
				.setToolTipText("Include other files, such as .png or .resources files.");
		includeConfig
				.setToolTipText("Include a resource named META-INF/BRIC/JAR-SETUP.TXT to help recreate/update this jar.");
		includeBricLicense
				.setToolTipText("Include the modified BSD license for com.bric files.");

		signLayout.addRow(new JLabel("Keystore:"), keyStoreField, true,
				createKeyStoreButton);
		signLayout.addRow(new JLabel("Keystore Password:"), keyStorePassword,
				false);
		signLayout.addRow(new JLabel("Alias:"), aliasField, false);
		signLayout.addRow(new JLabel("Alias Password:"), aliasPassword, false);
		signLayout.addRow(new JLabel("Timestamp Authority:"), tsaField, false);

		new TextFieldPrompt(classesField, "com.foo.MyClass com.goo.OtherClass");
		new TextFieldPrompt(sourcepathField,
				"/Users/admin/Documents/workspace/MyProject/src/");

		sourcepathField.getDocument().addDocumentListener(
				sourcepathFieldListener);

		sourcepathCollection.addChangeListener(sourcepathListener);
		refreshSourcepathSuggestions.run();
		refreshKeyStoreSuggestions.run();

		jvmComboBox.setEditable(true);

		editManifestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Manifest newManifest = ManifestEditor.showDialog(
						JarWriterApp.this, createManifest());
				if (newManifest != null)
					loadManifest(newManifest);
			}
		});

		createJarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createJar(false);
			}
		});

		createKeyStoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createKeyStore();
			}
		});

		setSourcePath(null);

		classesField.getDocument().addDocumentListener(new DocumentListener() {

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
				updateUIState(true);
			}

		});

		createJarButton.setEnabled(false);

		setPreferredSize(new Dimension(600, 600));

		FileCollection.search(null, new Runnable() {
			public void run() {
				sourcepathProgress.setVisible(false);
			}
		}, new FileCollection[] { sourcepathCollection, keyStoreCollection });

		browseClassesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showBrowseClassDialog();
			}
		});

		aliasField.setName("alias");
		classesField.setName("classnames");
		includeBricLicense.setName("include-bric-license");
		includeClass.setName("include-class");
		includeConfig.setName("include-config");
		includeJava.setName("include-java");
		includeOther.setName("include-other");
		jvmComboBox.setName("jvm-version");
		keyStoreField.setName("keystore");
		manifestTextArea.setName("manifest");
		sealed.setName("is-sealed");
		sourcepathField.setName("source-path");
		tsaField.setName("time-stamp-authority");
	}

	protected void updateUIState(boolean classFieldJustChanged) {
		List<String> classes = getClasses();
		if (classes.size() >= 1 && classFieldJustChanged) {
			Manifest currentManifest = createManifest();
			if (currentManifest.getMainAttributes().getValue("Main-Class") == null) {
				currentManifest.getMainAttributes().putValue("Main-Class",
						classes.get(0));
				loadManifest(currentManifest);
			}
		}

		boolean containsBric = false;
		for (String classname : classes) {
			if (classname.startsWith("com.bric"))
				containsBric = true;
		}
		includeBricLicense.setVisible(containsBric);

		createJarButton.setEnabled(classes.size() > 0 && createJarCtr == 0);
		createJarThrobber.setVisible(createJarCtr > 0);
		Section[] sections = new Section[] { setupSection, manifestSection,
				signSection };
		for (Section s : sections) {
			if (createJarCtr > 0) {
				collapsibleContainer.getHeader(s).putClientProperty(
						CollapsibleContainer.COLLAPSED, true);
				collapsibleContainer.getHeader(s).putClientProperty(
						CollapsibleContainer.COLLAPSIBLE, false);
			} else {
				collapsibleContainer.getHeader(s).putClientProperty(
						CollapsibleContainer.COLLAPSIBLE, true);
			}
		}
	}

	/**
	 * Convert the JTextArea into a Manifest.
	 * 
	 * @return the current Manifest
	 */
	public Manifest createManifest() {
		String s = manifestTextArea.getText();
		if (!s.endsWith("\n"))
			s = s + "\n";

		try (InputStream in = new ByteArrayInputStream(
				s.getBytes(StandardCharsets.UTF_8))) {
			Manifest manifest = new Manifest(in);
			return manifest;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Load a Manifest into the JTextArea for the Manifest. */
	public void loadManifest(Manifest manifest) {
		try {
			ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
			manifest.write(byteOS);
			String s = new String(byteOS.toByteArray(),
					Charset.forName("UTF-8"));
			manifestTextArea.setText(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showBrowseClassDialog() {
		final ClassCheckList mainClassList = new ClassCheckList(true);
		final ClassCheckList otherClassList = new ClassCheckList(false);

		final String[] currentClasses = getClasses().toArray(
				new String[getClasses().size()]);
		/** Map simple names to canonical names */
		final Map<String, String> classNameMap = new HashMap<>();

		for (String classname : currentClasses) {
			String simplename = classname;
			// use the simple class name instead of the full canonical name:
			int i = simplename.lastIndexOf('.');
			if (i != -1)
				simplename = simplename.substring(i + 1);
			classNameMap.put(simplename, classname);
		}

		class SelectEnteredClassnames implements ListDataListener {
			ClassCheckList list;

			SelectEnteredClassnames(ClassCheckList list) {
				this.list = list;
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				contentsChanged(e);
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				contentsChanged(e);
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				for (File file : list.getVisibleFiles()) {
					String filename = file.getName();

					// strip the file extension away:
					int k = filename.lastIndexOf('.');
					if (k != -1)
						filename = filename.substring(0, k);

					if (classNameMap.containsKey(filename)) {
						list.getSelection().add(file);
					}
				}
			}
		}
		;

		mainClassList.addListDataListener(new SelectEnteredClassnames(
				mainClassList));
		otherClassList.addListDataListener(new SelectEnteredClassnames(
				otherClassList));

		JScrollPane mainClassScrollPane = new JScrollPane(mainClassList);
		JScrollPane otherClassScrollPane = new JScrollPane(otherClassList);
		mainClassScrollPane.setPreferredSize(new Dimension(750, 300));
		otherClassScrollPane.setPreferredSize(new Dimension(750, 300));

		mainClassList.setDirectories(new File[] { currentSourcepath });
		otherClassList.setDirectories(new File[] { currentSourcepath });

		mainClassList.setColumnCount(2);
		otherClassList.setColumnCount(2);

		mainClassList.unselectAll();
		otherClassList.unselectAll();
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		panel.add(CollapsibleContainer.createCollapsibleButton(
				"Classes with a main() method:", false), c);
		c.gridy++;
		c.weighty = 1;
		panel.add(mainClassScrollPane, c);
		c.gridy++;
		c.weighty = 0;
		panel.add(CollapsibleContainer.createCollapsibleButton(
				"Classes without a main() method:", false), c);
		c.gridy++;
		c.weighty = 1;
		panel.add(otherClassScrollPane, c);
		DialogFooter footer = DialogFooter.createDialogFooter(
				DialogFooter.OK_CANCEL_OPTION,
				DialogFooter.EscapeKeyBehavior.TRIGGERS_CANCEL);
		QDialog.showDialog(JarWriterApp.this, "Browse Classes", null, panel,
				footer, true, null, null);
		if (footer.getLastSelectedComponent() != null
				&& footer.getLastSelectedComponent()
						.getClientProperty(DialogFooter.PROPERTY_OPTION)
						.equals(DialogFooter.OK_OPTION)) {
			String text = "";
			for (File file : mainClassList.getSelection()) {
				String className = JarWriter.getClassName(file);
				text = text + " " + className;
			}
			for (File file : otherClassList.getSelection()) {
				String className = JarWriter.getClassName(file);
				text = text + " " + className;
			}
			classesField.setText(text.trim());
		}
	}

	static JComponent createHeader(String text) {
		JButton button = CollapsibleContainer.createCollapsibleButton();
		button.setText(text);
		button.putClientProperty(CollapsibleContainer.COLLAPSIBLE, false);
		return button;
	}

	private JComponent wrap(JComponent... components) {
		JPanel p = new JPanel(new FlowLayout());
		p.setOpaque(false);
		for (JComponent c : components) {
			p.add(c);
		}
		return p;
	}

	private void install(Section section, JComponent component,
			boolean wrapInScrollPane) {
		section.getBody().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		if (wrapInScrollPane) {
			JScrollPane scroll = new JScrollPane(component);
			component = scroll;
		}
		section.getBody().add(component, c);

	}

	Runnable refreshSourcepathSuggestions = new Runnable() {
		public void run() {
			File[] files = sourcepathCollection.getFiles();
			sourcepathField
					.setSuggestionModel(new DefaultSuggestionModel<File>(files));
		}
	};

	ChangeListener sourcepathListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(refreshSourcepathSuggestions);
		}
	};

	Runnable refreshKeyStoreSuggestions = new Runnable() {
		public void run() {
			File[] files = keyStoreCollection.getFiles();
			keyStoreField.setSuggestionModel(new DefaultSuggestionModel<File>(
					files));
		}
	};

	ChangeListener keyStoreListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(refreshKeyStoreSuggestions);
		}
	};

	File currentSourcepath;

	public void loadJar() throws IOException {
		FileDialog fd = new FileDialog(new Frame());
		fd.setFilenameFilter(new SuffixFilenameFilter("jar"));
		fd.pack();
		fd.setVisible(true);
		if (fd.getFile() == null)
			return;
		File jarFile = new File(fd.getDirectory() + fd.getFile());
		try (ZipFile zf = new ZipFile(jarFile)) {
			try (InputStream in = zf.getInputStream(new ZipEntry(
					KEY_SETUP_ENTRY))) {
				if (in == null)
					throw new IOException(
							"This jar does not appear to be built with a recent version of JarWriterApp.");
				ContainerProperties p = new ContainerProperties();
				p.load(in);
				p.install(getContentPane(), null);
			}
			zf.close();
		}
	}

	public void createKeyStore() {
		KeyStoreEditor editor = KeyStoreEditor.create(JarWriterApp.this);
		if (editor != null) {
			File keyStoreFile = editor.getKeyStoreFile();
			keyStoreCollection.process(keyStoreFile);
			keyStoreField.setText(keyStoreFile.getAbsolutePath());
			aliasField.setText(editor.getAlias());
		}
	}

	private int createJarCtr = 0;

	class CreateJarThread extends Thread {
		File jarFile;
		List<String> classes = getClasses();
		boolean cancelled = false;
		Runnable updateUIRunnable = new Runnable() {
			public void run() {
				updateUIState(false);
			}
		};

		public CreateJarThread() {
			super("Create Jar Thread");
		}

		public void run() {
			createJarCtr++;
			SwingUtilities.invokeLater(updateUIRunnable);
			List<File> filesToDelete = new ArrayList<>();
			try {
				System.out.println("Preparing JarWriter...");
				String s = (String) jvmComboBox.getSelectedItem();
				try {
					float jvmVersion = Float.parseFloat(s);
					JarWriter writer = new JarWriter();
					writer.addSourcepath(currentSourcepath);

					for (File jar : auxiliaryJars) {
						writer.addJar(jar);
						System.out.println("\tAdding " + jar.getAbsolutePath());
					}
					for (File sourcepath : auxiliarySourcepaths) {
						writer.addSourcepath(sourcepath);
						System.out.println("\tAdding "
								+ sourcepath.getAbsolutePath());
					}

					if (includeConfig.isSelected()) {
						File setupFile = IOUtils.getUniqueTempFile("setup.txt");
						ContainerProperties properties = new ContainerProperties(
								getContentPane());
						try (OutputStream out = new FileOutputStream(setupFile)) {
							properties.store(out, "These are the settings the "
									+ JarWriterApp.class.getName()
									+ " used to create this jar.");
						}
						writer.addResource(KEY_SETUP_ENTRY, setupFile);
						filesToDelete.add(setupFile);
					}

					JarWriterFileFilter filter = new JarWriterFileFilter(
							includeJava.isSelected(),
							includeClass.isSelected(),
							includeOther.isSelected());

					boolean containsBricClasses = false;
					for (int a = 0; a < classes.size(); a++) {
						String className = classes.get(a);
						if (className.startsWith("com.bric"))
							containsBricClasses = true;
					}

					if (containsBricClasses && includeBricLicense.isSelected()
							&& includeBricLicense.isShowing()) {
						File licenseFile = PumpLicense.createLicenseFile();
						writer.addResource("License (com.pump).html",
								licenseFile);
						filesToDelete.add(licenseFile);
					}

					while (jarFile == null) {
						if (cancelled)
							throw new UserCancelledException();
						synchronized (JarWriterApp.this) {
							JarWriterApp.this.wait();
						}
					}

					MissingJarResponseManager jarDependencyChoice = null;
					if (jarChoiceBundleRadioButton.isSelected()) {
						jarDependencyChoice = new MissingJarConstantResponseManager(
								MissingJarResponse.BUNDLE_ENTIRE_JAR, true);
					} else {
						try {
							jarDependencyChoice = new MissingJarUserResponseManager(
									classes.toString());
						} catch (Exception e) {
							// TODO: what better solution can we come up with
							// here?
							e.printStackTrace();
							jarDependencyChoice = new MissingJarConstantResponseManager(
									MissingJarResponse.BUNDLE_ENTIRE_JAR, false);
						}
					}

					String errors = writer.createJar(
							classes.toArray(new String[classes.size()]),
							jvmVersion, createManifest(), jarFile, filter,
							jarDependencyChoice);
					if (errors != null) {
						throw new RuntimeException(
								"Compilation errors occurred, see console for details.");
					}

					File keyStoreFile = keyStoreField.getText().length() > 0 ? new File(
							keyStoreField.getText()) : null;
					if (keyStoreFile != null) {
						if (!keyStoreFile.exists()) {
							System.err
									.println("The keystore file provided does not exist: "
											+ keyStoreField.getText());
						} else {
							JarSigner signer = new JarSigner(keyStoreFile,
									new String(keyStorePassword.getPassword()),
									aliasField.getText(), new String(
											aliasPassword.getPassword()),
									tsaField.getText());
							signer.sign(jarFile, true);
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				System.out.println("Finished");
			} finally {
				createJarCtr--;
				SwingUtilities.invokeLater(updateUIRunnable);
				for (File file : filesToDelete) {
					try {
						if (!IOUtils.delete(file))
							throw new IOException("could not delete \""
									+ file.getAbsolutePath() + "\"");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void promptJarFile() {
			String filename = classes.get(0);
			int i = filename.lastIndexOf('.');
			if (i != -1) {
				filename = filename.substring(i + 1) + ".jar";
			} else {
				filename = filename + ".jar";
			}

			jarFile = promptFile(filename, "jar");
			if (jarFile == null)
				cancelled = true;

			synchronized (JarWriterApp.this) {
				JarWriterApp.this.notifyAll();
			}
		}
	}

	public void createJar(boolean blocking) {
		CreateJarThread thread = new CreateJarThread();

		if (blocking) {
			thread.promptJarFile();
			thread.run();
		} else {
			thread.start();
			thread.promptJarFile();
		}
	}

	public File promptFile(String name, String suffix) {
		if (suffix.startsWith(".") == false)
			suffix = '.' + suffix;
		FileDialog fd = new FileDialog(JarWriterApp.this);
		fd.setFile(name);
		fd.setMode(FileDialog.SAVE);
		fd.setVisible(true);
		String s = fd.getFile();
		if (s == null)
			return null;
		File returnValue = new File(fd.getDirectory() + s);
		if (s.toLowerCase().endsWith(suffix.toLowerCase()) == false) {
			s = s + suffix;
			returnValue = new File(fd.getDirectory() + s);
			if (returnValue.exists()) {
				String dialogTitle = "File Error";
				int type = QDialog.ERROR_MESSAGE;
				String boldMessage = "The file \"" + s + "\" already exists.";
				String plainMessage = "Please export again using a unique name.";
				QDialog.showDialog(JarWriterApp.this, dialogTitle, type,
						boldMessage, plainMessage, null, // innerComponent,
						null, // lowerLeftComponent,
						DialogFooter.OK_OPTION, DialogFooter.OK_OPTION, null, // dontShowKey,
						null, // alwaysApplyKey,
						DialogFooter.EscapeKeyBehavior.TRIGGERS_DEFAULT); // escapeKeyBehavior)
				throw new RuntimeException(boldMessage + " " + plainMessage);
			}
		}
		return returnValue;
	}

	public synchronized void setSourcePath(final File file) {
		if (currentSourcepath != null && file != null
				&& currentSourcepath.equals(file))
			return;

		currentSourcepath = file;
		auxiliarySourcepaths.clear();
		auxiliaryJars.clear();
		browseClassesButton.setEnabled(file != null);
		classesField.setEnabled(file != null);

		if (currentSourcepath != null) {
			try {
				processSourcePathDirectory(currentSourcepath, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This fishes around for a ".classpath" file, and if found it further
	 * identifies files to add to auxiliaryJars and auxiliarySourcepaths.
	 * 
	 * @param dir
	 *            the directory to search for a ".classpath" file in.
	 * @param processedXMLFiles
	 *            initially null, this is used by recursive calls to monitor
	 *            redundancy
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	protected void processSourcePathDirectory(File dir,
			Set<File> processedXMLFiles) throws IOException,
			ParserConfigurationException, SAXException {

		File xmlFile = new File(dir, ".classpath");
		while (xmlFile != null && xmlFile.exists() == false) {
			if (xmlFile.getParentFile() != null
					&& xmlFile.getParentFile().getParentFile() != null) {
				xmlFile = new File(xmlFile.getParentFile().getParentFile(),
						".classpath");
			} else {
				xmlFile = null;
			}
		}

		if (processedXMLFiles == null)
			processedXMLFiles = new HashSet<File>();
		if (xmlFile != null && xmlFile.exists()
				&& processedXMLFiles.add(xmlFile)) {
			EclipseClasspathFile ecf = new EclipseClasspathFile(xmlFile, true);
			for (File srcpath : ecf.srcpaths) {
				auxiliarySourcepaths.add(srcpath);
				processSourcePathDirectory(srcpath, processedXMLFiles);
			}
			for (File lib : ecf.libs) {
				// I forget why I originally added this? But now that I use
				// "javaws.jar" for jnlp
				// classes: this needs to go. (I'm leaving it commented out
				// though in case I ever
				// need to revisit this decision...?)
				// if(!lib.getAbsolutePath().contains("JavaVirtualMachines")) {
				auxiliaryJars.add(lib);
				// } else {
				// System.out.println("\tSkipped classpathentry lib: \""+lib.getAbsolutePath()+"\"");
				// }
			}
		}
	}

	protected List<String> getClasses() {
		List<String> list = new ArrayList<String>();
		String text = classesField.getText();
		String[] names = text.split(" ");
		for (String name : names) {
			// TODO apply pattern validation here
			list.add(name);
		}
		return list;
	}

	protected static File getClasspath(File workspace) {
		File defFile = new File(workspace, ".classpath");
		if (!defFile.exists())
			return null;

		try (InputStream in = new FileInputStream(defFile);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in))) {

			String s = br.readLine();
			while (s != null) {
				s = s.trim();

				if (s.indexOf("kind=\"output\"") != -1) {
					s = s.substring(s.indexOf("path=") + 5);
					String[] strings = getStrings(s);
					File output = new File(workspace.getAbsolutePath()
							+ File.separator + strings[0]);
					if (output.exists() == false)
						throw new IOException();
					return output;
				}
				s = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}

	protected static File getSourcepath(File workspace) {
		File defFile = new File(workspace, ".classpath");
		if (!defFile.exists())
			return null;

		try (InputStream in = new FileInputStream(defFile);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in))) {
			String s = br.readLine();
			while (s != null) {
				s = s.trim();

				if (s.indexOf("kind=\"src\"") != -1) {
					s = s.substring(s.indexOf("path=") + 5);
					String[] strings = getStrings(s);
					File output = new File(workspace.getAbsolutePath()
							+ File.separator + strings[0]);
					if (output.exists() == false)
						throw new IOException();
					return output;
				}
				s = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}

	protected static File[] getDirectories(File file) {
		InputStream in = null;
		List<File> directories = new ArrayList<File>();
		try {
			in = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String s = br.readLine();
			while (s != null) {
				s = s.trim();

				String[] strings = getStrings(s);
				for (int a = 0; a < strings.length; a++) {
					if (strings[a].indexOf(File.separator) != -1
							&& strings[a].length() > 1) {
						strings[a] = strings[a].substring(1);

						File parent = file.getParentFile();
						while (parent != null) {
							File f = new File(parent, strings[a]);
							if (f.exists())
								directories.add(f);
							parent = parent.getParentFile();
						}
					}
				}

				s = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Throwable t) {
			}
		}
		return directories.toArray(new File[directories.size()]);
	}

	protected static File[] getJars(File file) {
		List<File> jars = new ArrayList<File>();
		try (InputStream in = new FileInputStream(file);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in))) {
			String s = br.readLine();
			while (s != null) {
				s = s.trim();

				String[] strings = getStrings(s);
				for (int a = 0; a < strings.length; a++) {
					File f = new File(file.getParent() + File.separator
							+ strings[a]);
					if (f.exists()
							&& f.getName().toLowerCase().endsWith(".jar"))
						jars.add(f);
				}

				s = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jars.toArray(new File[jars.size()]);
	}

	protected static String[] getStrings(String s) {
		List<String> strings = new ArrayList<String>();
		while (s.indexOf('"') != -1) {
			s = s.substring(s.indexOf('"') + 1);
			if (s.indexOf('"') != -1) {
				String t = s.substring(0, s.indexOf('"'));
				strings.add(t);
				s = s.substring(t.length() + 1);
			}
		}
		return strings.toArray(new String[strings.size()]);
	}

	static class JarWriterFileFilter implements FileFilter {
		final boolean javaF, classF, otherF;

		public JarWriterFileFilter(boolean javaFiles, boolean classFiles,
				boolean otherFiles) {
			javaF = javaFiles;
			classF = classFiles;
			otherF = otherFiles;
		}

		public boolean accept(File pathname) {
			String suffix = getSuffix(pathname);
			if (suffix.equalsIgnoreCase("java")) {
				return javaF;
			} else if (suffix.equalsIgnoreCase("class")) {
				return classF;
			} else {
				return otherF;
			}
		}

		public static String getSuffix(File file) {
			String s = file.getAbsolutePath();
			int i = s.lastIndexOf('.');
			if (i == -1)
				return "";
			s = s.substring(i + 1);
			if (s.indexOf(File.separator) != -1)
				return "";
			return s;
		}
	}
}