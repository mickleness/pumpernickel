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

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pump.awt.ClickSensitivityDemo;
import com.pump.debug.AWTMonitorDemo;
import com.pump.desktop.DesktopApplication;
import com.pump.desktop.edit.EditCommand;
import com.pump.desktop.edit.EditMenuControls;
import com.pump.geom.AreaXTestPanel;
import com.pump.geom.knot.KnotDemo;
import com.pump.icon.button.MinimalDuoToneCloseIcon;
import com.pump.plaf.RoundTextFieldUI;
import com.pump.swing.HelpComponent;
import com.pump.swing.JFancyBox;
import com.pump.swing.ListSectionContainer;
import com.pump.swing.MagnificationPanel;
import com.pump.swing.SectionContainer.Section;
import com.pump.swing.TextFieldPrompt;
import com.pump.text.WildcardPattern;
import com.pump.window.WindowDragger;
import com.pump.window.WindowMenu;

public class PumpernickelShowcaseApp extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException {
		DesktopApplication.initialize("com.pump.showcase", "Showcase", "1.0",
				"jeremy.wood@mac.com", PumpernickelShowcaseApp.class);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PumpernickelShowcaseApp p = new PumpernickelShowcaseApp();
				p.pack();
				p.setLocationRelativeTo(null);
				p.setVisible(true);
				p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	enum Layout {
		STRETCH_TO_FIT, SCROLLPANE
	}

	JTextField searchField = new JTextField();
	List<Section> masterSectionList = new ArrayList<>();
	ListSectionContainer sectionContainer = new ListSectionContainer(true,
			null, searchField);
	JMenuBar menuBar = new JMenuBar();
	JMenu editMenu = createEditMenu();
	JMenu helpMenu = new JMenu("Help");
	JCheckBoxMenuItem magnifierItem = new JCheckBoxMenuItem("Magnifier");

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
			if (jc instanceof ShowcaseDemo) {
				ShowcaseDemo d = (ShowcaseDemo) jc;
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

	public PumpernickelShowcaseApp() {
		super("Pumpernickel Showcase");

		setJMenuBar(menuBar);
		menuBar.add(editMenu);
		menuBar.add(new WindowMenu(this, magnifierItem));

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
		new TextFieldPrompt(searchField, "Search...");
		searchField.setBorder(new CompoundBorder(new EmptyBorder(3, 3, 3, 3),
				searchField.getBorder()));

		searchField.getDocument().addDocumentListener(searchDocListener);

		getContentPane().setPreferredSize(new Dimension(800, 600));

		try {
			addSection("Transition2D", new Transition2DDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Transition3D", new Transition3DDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Brushed Metal", new BrushedMetalDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("BMP ImageIO Comparison", new BmpComparisonDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("AlphaComposite", new AlphaCompositeDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("TextEffect", new TextEffectDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("AWT Monitor", new AWTMonitorDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Gradient: Halftone", new HalftoneGradientDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Gradient: Color Band", new ColorBandDemo(this),
					Layout.STRETCH_TO_FIT);
			addSection("Click Sensitivity", new ClickSensitivityDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Shape Bounds", new ShapeBoundsDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Clipper", new ClipperDemo(), Layout.STRETCH_TO_FIT);
			addSection("AngleSliderUI", new AngleSliderUIDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("DecoratedPanelUI", new DecoratedPanelUIDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Spiral2D", new Spiral2DDemo(), Layout.STRETCH_TO_FIT);
			addSection("Swing: Components", new SwingComponentsDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Screen Capture", new ScreenCaptureDemo(this),
					Layout.SCROLLPANE);
			addSection("Swing: CollapsibleContainer",
					new CollapsibleContainerDemo(), Layout.STRETCH_TO_FIT);
			addSection("Swing: CustomizedToolbar", new CustomizedToolbarDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Images: Scaling", new ImageScalingDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Images: Quantization", new ImageQuantizationDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Swing: Color Components", new ColorDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("FilledButtonUIDemo", new FilledButtonUIDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Shapes: AreaX Tests", new AreaXTestPanel(),
					Layout.STRETCH_TO_FIT);
			addSection("Shapes: Knots", new KnotDemo(), Layout.STRETCH_TO_FIT);
			addSection("Graphics: Debugger", new GraphicsWriterDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Images: JPEG Metadata", new JPEGMetaDataDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("AudioPlayer", new AudioPlayerDemo(this),
					Layout.STRETCH_TO_FIT);
			addSection("Math: Gaussian Elimination", new EquationsDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("JavaTextComponentHighlighter",
					new JavaTextComponentHighlighterDemo(true),
					Layout.STRETCH_TO_FIT);
			addSection("XMLTextComponentHighlighter",
					new XMLTextComponentHighlighterDemo(true),
					Layout.STRETCH_TO_FIT);
			addSection("Text: Search Controls", new TextSearchDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Images: Creating Animated Gifs", new GifWriterDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("QuickTime: Writing Movies", new MovWriterDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("Highlighters, WildcardPattern",
					new WildcardPatternHighlighterDemo(), Layout.STRETCH_TO_FIT);
			addSection("BoxTabbedPaneUI", new BoxTabbedPaneUIDemo(),
					Layout.STRETCH_TO_FIT);
			addSection("CircularProgressBarUI",
					new CircularProgressBarUIDemo(), Layout.STRETCH_TO_FIT);
			addSection("Strokes, MouseSmoothing",
					new StrokeMouseSmoothingDemo(), Layout.STRETCH_TO_FIT);
			// TODO: add CubicIntersectionsPanel with 18-degree polynomial?

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private void addSection(String text, JComponent component, Layout layout) {
		if (component instanceof ShowcaseDemo) {
			ShowcaseDemo d = (ShowcaseDemo) component;
			component = wrapDemo(component, d.getTitle(), d.getHelpURL(),
					d.isSeparatorVisible());
		}
		Section section = sectionContainer.addSection(text, text);
		masterSectionList.add(section);
		JPanel body = section.getBody();
		if (layout == Layout.STRETCH_TO_FIT) {
			body.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			body.add(component, c);
		} else {
			body.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			JScrollPane scrollPane = new JScrollPane(component,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			body.add(scrollPane, c);
		}
	}

	public JEditorPane createTextPane(URL url) {
		JEditorPane textPane = new JEditorPane() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g0) {
				Graphics2D g = (Graphics2D) g0;
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

	private JComponent wrapDemo(JComponent component, String title,
			final URL helpURL, boolean includeSeparator) {
		ActionListener actionListener = new ActionListener() {
			JScrollPane scrollPane;
			JFancyBox box;
			JEditorPane textPane;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (scrollPane == null) {
					textPane = createTextPane(helpURL);
					scrollPane = new JScrollPane(textPane);

					updatePreferredSize();
					PumpernickelShowcaseApp.this
							.addComponentListener(new ComponentAdapter() {

								@Override
								public void componentResized(ComponentEvent e) {
									updatePreferredSize();
								}

							});

					try {
						textPane.setPage(helpURL);
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
		JLabel header = new JLabel(title);
		header.setFont(header.getFont().deriveFont(18f));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 3, 3, 3);
		replacement.add(header, c);
		JComponent jc = HelpComponent.createHelpComponent(actionListener, null,
				null);
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 3, 3, 3);
		replacement.add(jc, c);
		c.fill = GridBagConstraints.BOTH;
		if (includeSeparator) {
			jc.setVisible(helpURL != null);
			c.gridy++;
			replacement.add(new JSeparator(), c);
		}
		c.gridy++;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		replacement.add(component, c);
		return replacement;
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
}