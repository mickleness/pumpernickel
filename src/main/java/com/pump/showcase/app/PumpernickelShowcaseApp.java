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
package com.pump.showcase.app;

import java.awt.AWTEvent;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.pump.desktop.AboutControl;
import com.pump.desktop.DesktopApplication;
import com.pump.desktop.edit.EditCommand;
import com.pump.desktop.edit.EditMenuControls;
import com.pump.icon.button.MinimalDuoToneCloseIcon;
import com.pump.io.FileTreeIterator;
import com.pump.plaf.QPanelUI;
import com.pump.plaf.QPanelUI.CalloutType;
import com.pump.plaf.RoundTextFieldUI;
import com.pump.showcase.demo.ShowcaseExampleDemo;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.FileDialogUtils;
import com.pump.swing.JSwitchButton;
import com.pump.swing.MagnificationPanel;
import com.pump.swing.SectionContainer.Section;
import com.pump.swing.TextFieldPrompt;
import com.pump.swing.ThrobberManager;
import com.pump.util.JVM;
import com.pump.util.Property;
import com.pump.window.WindowDragger;
import com.pump.window.WindowMenu;

/**
 * This app shows off some (but by no means all) of the neat stuff in the
 * Pumpernickel codebase.
 */
public class PumpernickelShowcaseApp extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final String VERSION = "1.02";

	public static void main(String[] args) throws Exception {
		DesktopApplication app = new DesktopApplication("com.pump.showcase",
				"Pumpernickel Showcase", VERSION, "jeremy.wood@mac.com");
		app.setFrameClass(PumpernickelShowcaseApp.class);
		app.setCopyright(2018, "Jeremy Wood");
		app.setURL(new URL("https://mickleness.github.io/pumpernickel/"));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BufferedImage bi = createAppImage();
				DesktopApplication.get().setImage(bi);
			}
		});

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
			collapsibleContainer.getHeader(section)
					.setBorder(new EmptyBorder(3, 2, 3, 2));
			collapsibleContainer.getHeader(section)
					.setFont(ShowcaseExampleDemo.getHeaderLabelFont());
		}
	}

	static ShowcaseDemoInfo[] getDemos() {
		SortedSet<ShowcaseDemoInfo> returnValue = new TreeSet<>();
		returnValue
				.add(new ShowcaseDemoInfo("Transition2D", "Transition2DDemo"));
		returnValue
				.add(new ShowcaseDemoInfo("Transition3D", "Transition3DDemo"));
		returnValue.add(new ShowcaseDemoInfo("BmpEncoder, BmpDecoder",
				"BmpComparisonDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("AlphaComposite", "AlphaCompositeDemo"));
		returnValue.add(new ShowcaseDemoInfo("TextEffect", "TextEffectDemo"));
		returnValue.add(new ShowcaseDemoInfo("AWTMonitor", "AWTMonitorDemo"));
		returnValue.add(new ShowcaseDemoInfo("ClickSensitivityControl",
				"ClickSensitivityControlDemo"));
		returnValue.add(new ShowcaseDemoInfo("ShapeBounds", "ShapeBoundsDemo"));
		returnValue.add(new ShowcaseDemoInfo("Clipper", "ClipperDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("AngleSliderUI", "AngleSliderUIDemo"));
		returnValue.add(new ShowcaseDemoInfo("Spiral2D", "Spiral2DDemo"));
		returnValue.add(new ShowcaseDemoInfo("DecoratedListUI, DecoratedTreeUI",
				"DecoratedDemo"));
		returnValue.add(new ShowcaseDemoInfo("JThrobber", "ThrobberDemo"));
		returnValue.add(new ShowcaseDemoInfo("JBreadCrumb", "BreadCrumbDemo"));
		returnValue.add(new ShowcaseDemoInfo("CollapsibleContainer",
				"CollapsibleContainerDemo"));
		returnValue.add(new ShowcaseDemoInfo("CustomizedToolbar",
				"CustomizedToolbarDemo"));
		returnValue.add(new ShowcaseDemoInfo("JToolTip, QPopupFactory",
				"JToolTipDemo"));
		returnValue.add(new ShowcaseDemoInfo("JPopover", "JPopoverDemo"));
		returnValue.add(new ShowcaseDemoInfo("Scaling", "ScalingDemo"));
		// add(new DemoListElement("ImageQuantization", new
		// ImageQuantizationDemo());
		returnValue
				.add(new ShowcaseDemoInfo("JColorPicker", "JColorPickerDemo"));
		// add(new DemoListElement("Shapes: AreaX Tests", new AreaXTestPanel());
		returnValue
				.add(new ShowcaseDemoInfo("JPEGMetaData", "JPEGMetaDataDemo"));
		returnValue.add(new ShowcaseDemoInfo("QPanelUI", "QPanelUIDemo"));
		returnValue.add(new ShowcaseDemoInfo("AudioPlayer", "AudioPlayerDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("JavaFormatter", "JavaFormatterDemo"));
		returnValue
				.add(new ShowcaseDemoInfo("XMLFormatter", "XMLFormatterDemo"));
		// add(new DemoListElement("Text: Search Controls", new
		// TextSearchDemo());
		// add(new DemoListElement("QuickTime: Writing Movies", new
		// MovWriterDemo());
		returnValue.add(new ShowcaseDemoInfo("Highlighters, WildcardPattern",
				"WildcardPatternHighlighterDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("BoxTabbedPaneUI", "BoxTabbedPaneUIDemo"));
		returnValue.add(new ShowcaseDemoInfo("CircularProgressBarUI",
				"CircularProgressBarUIDemo"));
		returnValue.add(new ShowcaseDemoInfo("Strokes, MouseSmoothing",
				"StrokeMouseSmoothingDemo"));
		returnValue.add(new ShowcaseDemoInfo("JColorWell, JPalette",
				"JColorWellPaletteDemo"));
		returnValue.add(new ShowcaseDemoInfo("JEyeDropper", "JEyeDropperDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("JSwitchButton", "JSwitchButtonDemo"));
		returnValue
				.add(new ShowcaseDemoInfo("JButton, QButtonUI", "JButtonDemo"));
		returnValue.add(new ShowcaseDemoInfo("MixedCheckBoxState",
				"MixedCheckBoxStateDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("JFrame, JDialog, JWindow", "WindowDemo"));
		returnValue.add(new ShowcaseDemoInfo("System Properties",
				"SystemPropertiesDemo"));
		returnValue.add(new ShowcaseDemoInfo("FileIcon", "FileIconDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("DesktopHelper", "DesktopHelperDemo"));
		returnValue.add(new ShowcaseDemoInfo("VectorImage", "VectorImageDemo"));
		returnValue.add(new ShowcaseDemoInfo("StarPolygon", "StarPolygonDemo"));
		returnValue.add(
				new ShowcaseDemoInfo("ShadowRenderer", "ShadowRendererDemo"));
		returnValue
				.add(new ShowcaseDemoInfo("HTML, QHTMLEditorKit", "HTMLDemo"));
		returnValue.add(new ShowcaseDemoInfo("ThumbnailGenerator",
				"ThumbnailGeneratorDemo"));
		if (JVM.isMac) {
			returnValue.add(new ShowcaseDemoInfo("AquaIcon", "AquaIconDemo"));
			returnValue.add(new ShowcaseDemoInfo("NSImage", "NSImageDemo"));
		} else if (JVM.isWindows) {
			returnValue.add(
					new ShowcaseDemoInfo("WindowsIcon", "WindowsIconDemo"));
		}
		return returnValue.toArray(new ShowcaseDemoInfo[returnValue.size()]);
	}

	/**
	 * Create the application icon.
	 * 
	 * This should be called on the EDT, because it creates/renders JComponents.
	 */
	private static BufferedImage createAppImage() {
		BufferedImage bi = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();

		JPanel p = new JPanel();
		p.setOpaque(false);
		QPanelUI qui = new QPanelUI();
		qui.setCalloutSize(10);
		qui.setCornerSize(10);
		qui.setCalloutType(CalloutType.BOTTOM_CENTER);
		qui.setFillColor1(Color.white);
		qui.setFillColor2(new Color(0xececec));
		qui.setShadowSize(5);
		qui.setStrokeColor(new Color(0x787878));
		p.setUI(qui);
		JSwitchButton switchButton1 = new JSwitchButton(true);
		JSwitchButton switchButton2 = new JSwitchButton(false);
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);
		p.add(switchButton1, c);
		c.gridy++;
		p.add(switchButton2, c);
		Dimension d = p.getPreferredSize();
		d.width = Math.max(d.width, d.height);
		d.height = Math.max(d.width, d.height);
		p.setSize(d);
		p.getLayout().layoutContainer(p);
		p.paint(g);
		g.dispose();
		return bi;
	}

	JTextField searchField = new JTextField();
	JPanel searchFieldPanel = new JPanel(new GridBagLayout());
	TextFieldPrompt searchPrompt;
	List<Section> masterSectionList = new ArrayList<>();
	JPanel rightContainer = new JPanel(new GridBagLayout());
	JMenuBar menuBar = new JMenuBar();
	JMenu editMenu = createEditMenu();
	JMenu helpMenu = new JMenu("Help");
	JCheckBoxMenuItem magnifierItem = new JCheckBoxMenuItem("Magnifier");
	JMenuItem saveScreenshotItem = new JMenuItem("Save Screenshot...");
	ThrobberManager loadingThrobberManager = new ThrobberManager();
	Property<String> searchPhrase = new Property<>("search-phrase", "");

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

	CardLayout demoCardLayout = new CardLayout();
	JPanel demoCardPanel = new JPanel(demoCardLayout);
	DemoLoadThread loadingThread;
	HeaderRow headerRow;

	private final static String PROPERTY_SELECTED_DEMO = PumpernickelShowcaseApp.class
			.getName() + "#selectedDemo";

	public PumpernickelShowcaseApp() {
		super("Pumpernickel Showcase");
		ShowcaseDemoInfo[] allDemos = getDemos();

		loadingThread = new DemoLoadThread(allDemos);
		loadingThread.start();

		getRootPane().addPropertyChangeListener(PROPERTY_SELECTED_DEMO,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						ShowcaseDemoInfo e = getSelectedDemo();
						if (e.getPanel() == null) {
							ShowcaseDemoPanel panel = new ShowcaseDemoPanel(e,
									searchPhrase);
							e.setPanel(panel);

							demoCardPanel.add(panel,
									e.getDemoSimpleClassName());
						}
						demoCardLayout.show(demoCardPanel,
								e.getDemoSimpleClassName());
						demoCardPanel.validate();
						loadingThread.request(e);
					}
				});

		headerRow = new HeaderRow(this, allDemos);

		setJMenuBar(menuBar);
		menuBar.add(editMenu);
		AboutControl aboutControl = new AboutControl();
		JMenuItem aboutItem = JVM.isMac ? null : aboutControl.getMenuItem();
		menuBar.add(new WindowMenu(this, aboutItem, magnifierItem,
				saveScreenshotItem));
		saveScreenshotItem.addActionListener(saveScreenshotActionListener);
		saveScreenshotItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		// add this awkward universal listener to also help capture screenshots
		// of the eyedropper when it is showing a modal dialog
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			public void eventDispatched(AWTEvent event) {
				KeyEvent e = (KeyEvent) event;
				if (e.getKeyCode() == saveScreenshotItem.getAccelerator()
						.getKeyCode()
						&& e.getModifiers() == Toolkit.getDefaultToolkit()
								.getMenuShortcutKeyMask()
						&& e.getID() == KeyEvent.KEY_PRESSED) {
					saveScreenshotActionListener.actionPerformed(null);
					e.consume();
				}
			}

		}, AWTEvent.KEY_EVENT_MASK);

		// TODO: add help menu/about menu item
		// menuBar.add(helpMenu);

		magnifierItem.addActionListener(magnifierListener);

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		getContentPane().add(headerRow, c);
		c.gridy++;
		c.weighty = 1;
		getContentPane().add(rightContainer, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		rightContainer.add(demoCardPanel, c);

		searchField.setUI(new RoundTextFieldUI());
		searchField.putClientProperty("JTextField.variant", "search");
		searchPrompt = new TextFieldPrompt(searchField, "Loading...");
		searchField.setBorder(new CompoundBorder(new EmptyBorder(3, 3, 3, 3),
				searchField.getBorder()));

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
						ShowcaseDemoInfo demo = getSelectedDemo();
						String defaultName = demo.getDemoName();
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
					File[] resourceDirs = FileTreeIterator
							.findAll(new File[] { dir }, "resources");
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
	 * Take a screenshot of the currently selected demo panel.
	 * 
	 * @param destFile
	 *            an optional target PNG file to write to. If null then the user
	 *            is prompted with a file dialog to choose the file destination.
	 */
	protected File createScreenshot(File destFile) throws Exception {
		ShowcaseDemoInfo e = getSelectedDemo();
		BufferedImage bi = getScreenshot(e.getDemo());
		if (destFile == null) {
			String defaultName = e.getDemoName();
			destFile = FileDialogUtils.showSaveDialog(
					PumpernickelShowcaseApp.this, "Export as...",
					defaultName + ".png", "png");
		}
		ImageIO.write(bi, "png", destFile);
		System.out.println("Saved screenshot as " + destFile.getAbsolutePath());
		return destFile;
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

	public ShowcaseDemoInfo getSelectedDemo() {
		ShowcaseDemoInfo sdi = (ShowcaseDemoInfo) getRootPane()
				.getClientProperty(PROPERTY_SELECTED_DEMO);
		return sdi;
	}

	public void setSelectedDemo(ShowcaseDemoInfo sdi) {
		getRootPane().putClientProperty(PROPERTY_SELECTED_DEMO, sdi);
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
}