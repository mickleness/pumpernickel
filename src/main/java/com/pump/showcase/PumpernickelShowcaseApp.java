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
package com.pump.showcase;

import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pump.desktop.DesktopApplication;
import com.pump.desktop.edit.EditCommand;
import com.pump.desktop.edit.EditMenuControls;
import com.pump.icon.button.MinimalDuoToneCloseIcon;
import com.pump.io.FileTreeIterator;
import com.pump.plaf.RoundTextFieldUI;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.FileDialogUtils;
import com.pump.swing.HelpComponent;
import com.pump.swing.JFancyBox;
import com.pump.swing.ListSectionContainer;
import com.pump.swing.MagnificationPanel;
import com.pump.swing.SectionContainer.Section;
import com.pump.swing.TextFieldPrompt;
import com.pump.swing.ThrobberManager;
import com.pump.text.WildcardPattern;
import com.pump.util.JVM;
import com.pump.window.WindowDragger;
import com.pump.window.WindowMenu;

public class PumpernickelShowcaseApp extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException {
		DesktopApplication.initialize("com.pump.showcase", "Showcase", "1.01",
				"jeremy.wood@mac.com", PumpernickelShowcaseApp.class);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PumpernickelShowcaseApp p = new PumpernickelShowcaseApp();
				p.pack();
				p.setLocationRelativeTo(null);
				p.setVisible(true);
				p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				p.loadDemos();
			}
		});
	}

	JTextField searchField = new JTextField();
	JPanel searchFieldPanel = new JPanel(new GridBagLayout());
	TextFieldPrompt searchPrompt;
	List<Section> masterSectionList = new ArrayList<>();
	ListSectionContainer sectionContainer = new ListSectionContainer(true,
			null, searchFieldPanel);
	JMenuBar menuBar = new JMenuBar();
	JMenu editMenu = createEditMenu();
	JMenu helpMenu = new JMenu("Help");
	JCheckBoxMenuItem magnifierItem = new JCheckBoxMenuItem("Magnifier");
	JMenuItem saveScreenshotItem = new JMenuItem("Save Screenshot...");
	ThrobberManager loadingThrobberManager = new ThrobberManager();

	ActionListener magnifierListener = new ActionListener() {

		JWindow magnifierWindow;
		JButton closeButton = new JButton();
		Timer repaintTimer;
		MagnificationPanel p;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (magnifierWindow == null) {
				magnifierWindow = createWindow();
			}
			magnifierWindow.setVisible(magnifierItem.isSelected());
		}

		private JWindow createWindow() {
			// TODO: this is OK for now, but eventually let's:
			// 1. Update to a resizable dialog (the MagnificationPanel doesn't
			// handle resizes yet.)
			// 2. Support zooming in/out of the MagnificationPanel
			// 3. Fix MagnificationPanel.setPixelated(false), offer
			// checkbox/context menu to toggle

			JWindow w = new JWindow(PumpernickelShowcaseApp.this);
			// on Macs this gives the window a certain look, plus it hides
			// the window when the app loses focus.
			w.getRootPane().putClientProperty("Window.style", "small");
			p = new MagnificationPanel(PumpernickelShowcaseApp.this, 40, 40, 4);
			// on Mac the window shadows show the boundaries well enough.
			// Otherwise let's paint it clearly:
			if (!JVM.isMac)
				p.setBorder(new LineBorder(Color.gray));
			w.setLayout(new GridBagLayout());
			w.setAlwaysOnTop(true);
			w.setLocationRelativeTo(PumpernickelShowcaseApp.this);
			new WindowDragger(p).setActive(true);
			w.setFocusableWindowState(false);

			closeButton.setIcon(new MinimalDuoToneCloseIcon(closeButton));
			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					magnifierItem.doClick();
				}

			});
			closeButton.setContentAreaFilled(false);
			closeButton.setBorderPainted(false);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(0, 0, 0, 0);
			c.anchor = GridBagConstraints.NORTHWEST;
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.NONE;
			w.add(closeButton, c);

			c.insets = new Insets(0, 0, 0, 0);
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			w.add(p, c);

			w.pack();

			repaintTimer = new Timer(25, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.refresh();
				}
			});
			repaintTimer.start();

			return w;
		}

	};
	private DocumentListener searchDocListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			change();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			change();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		private void change() {
			closeFancyBoxes();
			String str = searchField.getText();

			Comparator<Section> comparator = new Comparator<Section>() {
				@Override
				public int compare(Section o1, Section o2) {
					return o1.getName().toLowerCase()
							.compareTo(o2.getName().toLowerCase());
				}
			};

			Collection<Section> matches = new TreeSet<>(comparator);
			for (Section section : masterSectionList) {
				if (isMatching(section, str))
					matches.add(section);
			}

			sectionContainer.getSections().setAll(
					matches.toArray(new Section[matches.size()]));
		}

		private boolean isMatching(Section section, String phrase) {
			if (phrase == null || phrase.trim().length() == 0)
				return true;
			phrase = phrase.toLowerCase();
			String[] terms = phrase.split("\\s");
			for (String term : terms) {
				if (section.getName().toLowerCase().contains(term))
					return true;
				List<String> keywords = getKeywords(section.getBody());
				for (String keyword : keywords) {
					if (keyword.contains(term))
						return true;
				}

				if (term.contains("*") || term.contains("?")
						|| term.contains("[")) {
					WildcardPattern pattern = new WildcardPattern(term);

					if (pattern.matches(section.getName().toLowerCase()))
						return true;
					for (String keyword : keywords) {
						if (pattern.matches(keyword))
							return true;
					}
				}
			}
			return false;
		}

		private List<String> getKeywords(JComponent jc) {
			List<String> returnValue = new ArrayList<>();
			if (jc instanceof LazyDemoPanel) {
				LazyDemoPanel ldp = (LazyDemoPanel) jc;
				ShowcaseDemo d = ldp.getShowcaseDemo();
				if (d.getKeywords() == null)
					throw new NullPointerException(jc.getClass().getName());
				for (String keyword : d.getKeywords()) {
					returnValue.add(keyword.toLowerCase());
				}
				for (Class z : d.getClasses()) {
					int layer = 0;
					/*
					 * Include at least 4 layers: CircularProgressBarUI ->
					 * BasicProgressBarUI -> ProgressBarUI -> ComponentUI
					 */
					while (z != null && !z.equals(Object.class) && layer < 4) {
						returnValue.add(z.getSimpleName().toLowerCase());
						returnValue.add(z.getName().toLowerCase());
						z = z.getSuperclass();
						layer++;
					}
				}
			}
			for (int a = 0; a < jc.getComponentCount(); a++) {
				if (jc.getComponent(a) instanceof JComponent)
					returnValue.addAll(getKeywords((JComponent) jc
							.getComponent(a)));
			}
			return returnValue;
		}
	};

	ActionListener saveScreenshotActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				createScreenshot(null);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	};

	public PumpernickelShowcaseApp() {
		super("Pumpernickel Showcase");

		setJMenuBar(menuBar);
		menuBar.add(editMenu);
		menuBar.add(new WindowMenu(this, magnifierItem, saveScreenshotItem));
		saveScreenshotItem.addActionListener(saveScreenshotActionListener);
		saveScreenshotItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		// TODO: add help menu/about menu item
		// menuBar.add(helpMenu);

		magnifierItem.addActionListener(magnifierListener);

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridy++;
		c.weighty = 1;
		getContentPane().add(sectionContainer, c);

		searchField.setUI(new RoundTextFieldUI());
		searchField.putClientProperty("JTextField.variant", "search");
		searchPrompt = new TextFieldPrompt(searchField, "Loading...");
		searchField.setBorder(new CompoundBorder(new EmptyBorder(3, 3, 3, 3),
				searchField.getBorder()));

		searchField.getDocument().addDocumentListener(searchDocListener);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		searchFieldPanel.add(searchField, c);
		c.gridx++;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		searchFieldPanel.add(loadingThrobberManager.createThrobber(), c);

		getContentPane().setPreferredSize(new Dimension(800, 600));

		try {
			addSection("Transition2D", "Transition2DDemo");
			addSection("Transition3D", "Transition3DDemo");
			addSection("BmpEncoder, BmpDecoder", "BmpComparisonDemo");
			addSection("AlphaComposite", "AlphaCompositeDemo");
			addSection("TextEffect", "TextEffectDemo");
			addSection("AWTMonitor", "AWTMonitorDemo");
			addSection("GradientTexturePaint", "GradientTexturePaintDemo");
			addSection("ClickSensitivityControl", "ClickSensitivityControlDemo");
			addSection("ShapeBounds", "ShapeBoundsDemo");
			addSection("Clipper", "ClipperDemo");
			addSection("AngleSliderUI", "AngleSliderUIDemo");
			addSection("Spiral2D", "Spiral2DDemo");
			addSection("DecoratedListUI, DecoratedTreeUI", "DecoratedDemo");
			addSection("JThrobber", "ThrobberDemo");
			addSection("JBreadCrumb", "BreadCrumbDemo");
			addSection("CollapsibleContainer", "CollapsibleContainerDemo");
			addSection("CustomizedToolbar", "CustomizedToolbarDemo");
			addSection("JToolTip, QPopupFactory", "JToolTipDemo");
			addSection("JPopover", "JPopoverDemo");
			addSection("Scaling", "ScalingDemo");
			// addSection("ImageQuantization", new ImageQuantizationDemo());
			addSection("JColorPicker", "JColorPickerDemo");
			addSection("QButtonUI", "QButtonUIDemo");
			// addSection("Shapes: AreaX Tests", new AreaXTestPanel());
			addSection("GraphicsWriterDebugger", "GraphicsWriterDebuggerDemo");
			addSection("JPEGMetaData", "JPEGMetaDataDemo");
			addSection("QPanelUI", "QPanelUIDemo");
			addSection("AudioPlayer", "AudioPlayerDemo");
			addSection("JavaTextComponentHighlighter",
					"JavaTextComponentHighlighterDemo");
			addSection("XMLTextComponentHighlighter",
					"XMLTextComponentHighlighterDemo");
			// addSection("Text: Search Controls", new TextSearchDemo());
			// addSection("QuickTime: Writing Movies", new MovWriterDemo());
			addSection("Highlighters, WildcardPattern",
					"WildcardPatternHighlighterDemo");
			addSection("BoxTabbedPaneUI", "BoxTabbedPaneUIDemo");
			addSection("CircularProgressBarUI", "CircularProgressBarUIDemo");
			addSection("Strokes, MouseSmoothing", "StrokeMouseSmoothingDemo");
			addSection("JColorWell, JPalette", "JColorWellPaletteDemo");
			addSection("JEyeDropper", "JEyeDropperDemo");
			addSection("JSwitchButton", "JSwitchButtonDemo");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Type the F1 key to take a screenshot that is automatically
		// file away in the resources/showcase directory.
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			File showcaseScreenshotDir;

			@Override
			public void eventDispatched(AWTEvent event) {
				KeyEvent k = (KeyEvent) event;
				if (k.getID() == KeyEvent.KEY_RELEASED
						&& k.getKeyCode() == KeyEvent.VK_F1) {
					try {
						File dir = getShowcaseScreenshotDirectory();
						Section section = getSelectedSection();
						String defaultName = getDemoName(section.getBody());
						createScreenshot(new File(dir, defaultName + ".png"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					k.consume();
				}
			}

			private File getShowcaseScreenshotDirectory() throws Exception {
				if (showcaseScreenshotDir == null) {
					Collection<File> candidates = new LinkedHashSet<>();
					File dir = new File(System.getProperty("user.dir"));
					File[] resourceDirs = FileTreeIterator.findAll(
							new File[] { dir }, "resources");
					for (File resourceDir : resourceDirs) {
						File showcaseDir = new File(resourceDir, "showcase");
						if (showcaseDir.exists()) {
							candidates.add(showcaseDir);
						}
					}
					if (candidates.size() == 1) {
						showcaseScreenshotDir = candidates.iterator().next();
					} else if (candidates.size() == 0) {
						throw new IOException(
								"The directory \"resources/showcase\" was not found in "
										+ dir.getAbsolutePath());
					} else {
						throw new IOException(
								"Multiple candidate target directories were found: "
										+ candidates);
					}
				}
				return showcaseScreenshotDir;
			}

		}, AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * Create a worker thread that loads all the demos. The search field is
	 * disabled until this thread completes.
	 */
	public void loadDemos() {
		searchField.setEnabled(false);
		Thread thread = new Thread("Loading demos") {
			@Override
			public void run() {
				ThrobberManager.Token token = loadingThrobberManager
						.createToken();
				try {
					loadSections();
				} finally {
					loadingThrobberManager.returnToken(token);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							searchField.setEnabled(true);
							searchPrompt.setText("Search...");
						}
					});
				}
			}

			private void loadSections() {
				for (Section section : sectionContainer.getSections()) {
					try {
						final LazyDemoPanel p = (LazyDemoPanel) section
								.getBody().getComponent(0);
						final AtomicBoolean loaded = new AtomicBoolean(false);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								try {
									p.getShowcaseDemo();
								} finally {
									loaded.set(true);
								}
							}
						});
						while (!loaded.get()) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								Thread.yield();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}

	/**
	 * Take a screenshot of the currently selected demo panel.
	 * 
	 * @param destFile
	 *            an optional target PNG file to write to. If null then the user
	 *            is prompted with a file dialog to choose the file destination.
	 */
	protected File createScreenshot(File destFile) throws Exception {
		Section section = getSelectedSection();
		BufferedImage bi = getScreenshot(section.getBody());
		if (destFile == null) {
			String defaultName = getDemoName(section.getBody());
			destFile = FileDialogUtils.showSaveDialog(
					PumpernickelShowcaseApp.this, "Export as...", defaultName
							+ ".png", "png");
		}
		ImageIO.write(bi, "png", destFile);
		System.out.println("Saved screenshot as " + destFile.getAbsolutePath());
		return destFile;
	}

	/**
	 * Return the name of the showcase demo panel in the given component.
	 */
	private String getDemoName(Component c) {
		Class z = c.getClass();
		if (z.getName().contains("pump.showcase."))
			return z.getSimpleName();
		if (c instanceof Container) {
			Container c2 = (Container) c;
			for (Component child : c2.getComponents()) {
				String n = getDemoName(child);
				if (n != null)
					return n;
			}
		}
		return null;
	}

	/**
	 * Capture a screenshot based on the position of the given panel.
	 * <p>
	 * This uses a Robot to actually capture the real screenshot in case other
	 * floating layers/windows are meant to be captured.
	 */
	private BufferedImage getScreenshot(JPanel panel) throws Exception {
		Robot robot = new Robot();
		Point p = panel.getLocationOnScreen();
		Rectangle screenRect = new Rectangle(p.x, p.y, panel.getWidth(),
				panel.getHeight());
		return robot.createScreenCapture(screenRect);
	}

	/**
	 * Return the selected Section, or throw a NullPointerException if no
	 * selection exists.
	 */
	protected Section getSelectedSection() {
		Section section = sectionContainer.getSelectedSection();
		if (section == null)
			throw new NullPointerException(
					"Please select a topic in the list on the left to capture a screenshot.");
		return section;
	}

	/**
	 * Create an edit menu.
	 * 
	 * Admittedly: this current implementation doesn't actually achieve very
	 * much. It's just a standard cut/copy/paste that is automatically enabled
	 * for text components. The real purpose of this menu is to help legitimize
	 * this window/menubar so this app doesn't feel like it's out of place.
	 */
	private JMenu createEditMenu() {
		JMenu editMenu = new JMenu("Edit");
		EditMenuControls editControls = new EditMenuControls(true, true, true,
				true);
		JMenuItem cutItem = new JMenuItem(
				editControls.getAction(EditCommand.CUT));
		JMenuItem copyItem = new JMenuItem(
				editControls.getAction(EditCommand.COPY));
		JMenuItem pasteItem = new JMenuItem(
				editControls.getAction(EditCommand.PASTE));
		JMenuItem selectAllItem = new JMenuItem(
				editControls.getAction(EditCommand.SELECT_ALL));
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(selectAllItem);

		return editMenu;
	}

	private void addSection(String text, String demoClassName) {
		Section section = sectionContainer.addSection(text, text);
		masterSectionList.add(section);
		JPanel body = section.getBody();
		body.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		body.add(new LazyDemoPanel(demoClassName), c);
	}

	class LazyDemoPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		CardLayout cardLayout = new CardLayout();
		JPanel loadingPanel = new JPanel();
		String demoClassName;
		ShowcaseDemo showcaseDemo;

		public LazyDemoPanel(String demoClassName) {
			super();
			this.demoClassName = "com.pump.showcase." + demoClassName;
			setLayout(cardLayout);
			add(loadingPanel, "loading");
			cardLayout.show(this, "loading");

			// loadingPanel is never really shown to the user,
			// so there's no point in putting a throbber or other content in it
			addHierarchyListener(new HierarchyListener() {

				@Override
				public void hierarchyChanged(HierarchyEvent e) {
					if (loadingPanel.isShowing()) {
						add(createDemoPanel(), "demo");
						cardLayout.show(LazyDemoPanel.this, "demo");
						removeHierarchyListener(this);
					}
				}

			});
		}

		ShowcaseDemo getShowcaseDemo() {
			if (showcaseDemo == null) {
				try {
					Class demoClass = Class.forName(demoClassName);
					showcaseDemo = (ShowcaseDemo) demoClass.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			return showcaseDemo;
		}

		private JComponent createDemoPanel() {
			ActionListener actionListener = new ActionListener() {
				JScrollPane scrollPane;
				JFancyBox box;
				JEditorPane textPane;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (scrollPane == null) {
						textPane = createTextPane(getShowcaseDemo()
								.getHelpURL());
						scrollPane = new JScrollPane(textPane);

						updatePreferredSize();
						PumpernickelShowcaseApp.this
								.addComponentListener(new ComponentAdapter() {

									@Override
									public void componentResized(
											ComponentEvent e) {
										updatePreferredSize();
									}

								});

						try {
							textPane.setPage(getShowcaseDemo().getHelpURL());
							box = new JFancyBox(PumpernickelShowcaseApp.this,
									scrollPane);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					box.setVisible(true);
				}

				private void updatePreferredSize() {
					Dimension d = PumpernickelShowcaseApp.this.getSize();
					d.width = Math.max(200, d.width - 100);
					d.height = Math.max(200, d.height - 100);
					scrollPane.setMinimumSize(d);
					scrollPane.setPreferredSize(d);
					textPane.setMinimumSize(d);
					textPane.setPreferredSize(d);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							scrollPane.revalidate();
						}
					});
				}

			};

			JPanel replacement = new JPanel(new GridBagLayout());

			JTextArea headerTextArea = createTextArea(getShowcaseDemo()
					.getTitle(), 18);
			JTextArea descriptionTextArea = createTextArea(getShowcaseDemo()
					.getSummary(), 14);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(10, 3, 3, 3);
			replacement.add(headerTextArea, c);
			c.gridx++;
			c.weightx = 0;
			JComponent jc = HelpComponent.createHelpComponent(actionListener,
					null, null);
			c.anchor = GridBagConstraints.EAST;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(6, 3, 3, 3);
			replacement.add(jc, c);
			jc.setVisible(getShowcaseDemo().getHelpURL() != null);
			c.gridx = 0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.BOTH;
			c.gridy++;
			replacement.add(descriptionTextArea, c);
			c.gridy++;
			replacement.add(new JSeparator(), c);

			c.gridy++;
			c.weighty = 1;
			c.insets = new Insets(3, 3, 3, 3);
			replacement.add((JPanel) getShowcaseDemo(), c);
			return replacement;
		}
	}

	private JTextArea createTextArea(String str, float fontSize) {
		JTextArea t = new JTextArea(str);
		t.setFont(t.getFont().deriveFont(fontSize));
		t.setEditable(false);
		t.setOpaque(false);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		return t;
	}

	public JEditorPane createTextPane(URL url) {
		JEditorPane textPane = new JEditorPane() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g0) {
				Graphics2D g = (Graphics2D) g0;
				// for text bullets:
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				super.paint(g);
			}
		};
		textPane.setEditable(false);
		HTMLEditorKit kit = new HTMLEditorKit();
		textPane.setEditorKit(kit);

		StyleSheet styleSheet = kit.getStyleSheet();

		styleSheet
				.addRule("body {  padding: 12em 12em 12em 12em;  margin: 0;  font-family: sans-serif;  color: black;  background: white;  background-position: top left;  background-attachment: fixed;  background-repeat: no-repeat;}");

		styleSheet.addRule("h1, h2, h3, h4, h5, h6 { text-align: left }");
		styleSheet.addRule("h1, h2, h3 { color: #005a9c }");
		styleSheet.addRule("h1 { font: 160% sans-serif }");
		styleSheet.addRule("h2 { font: 140% sans-serif }");
		styleSheet.addRule("h3 { font: 120% sans-serif }");
		styleSheet.addRule("h4 { font: bold 100% sans-serif }");
		styleSheet.addRule("h5 { font: italic 100% sans-serif }");
		styleSheet.addRule("h6 { font: small-caps 100% sans-serif }");

		textPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					URL url = e.getURL();
					String str = e.getDescription();
					if (str != null && str.startsWith("resource:")) {
						str = str.substring("resource:".length());
						searchField.setText(str);
						return;
					}
					try {
						Desktop.getDesktop().browse(url.toURI());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		return textPane;
	}

	void closeFancyBoxes() {
		closeFancyBoxes(getLayeredPane());
	}

	void closeFancyBoxes(Container container) {
		for (int a = 0; a < container.getComponentCount(); a++) {
			Component c = container.getComponent(a);
			if (c instanceof JFancyBox) {
				c.setVisible(false);
			} else if (c instanceof Container) {
				closeFancyBoxes((Container) c);
			}
		}
	}

	/**
	 * Add a CollapsibleContainer to a panel so it fills the space and gives
	 * equal vertical weight to non-closable sections.
	 * 
	 * @param panel
	 * @param collapsibleContainer
	 * @param sections
	 */
	public static void installSections(JPanel panel,
			CollapsibleContainer collapsibleContainer, Section... sections) {
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3, 3, 3, 3);
		panel.add(collapsibleContainer, c);

		for (Section section : sections) {
			section.setProperty(CollapsibleContainer.VERTICAL_WEIGHT, 1);
			collapsibleContainer.getHeader(section).putClientProperty(
					CollapsibleContainer.COLLAPSIBLE, Boolean.FALSE);
		}
	}
}