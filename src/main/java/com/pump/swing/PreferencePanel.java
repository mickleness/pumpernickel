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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.SeparatorUI;

import com.pump.plaf.BevelButtonUI;
import com.pump.window.WindowDragger;

/**
 * A panel for managing several different preference components. Each preference
 * panel is associated with a single button. This is modeled after OS X's
 * preference system.
 * <P>
 * This component can be used with 1 row of buttons, in which case it behaves
 * similar to a JTabbedPane (where each tab is actually a toggle button). Ken
 * Orr has put together a great JTabbedPaneUI based on this concept:
 * http://code.google.com/p/macwidgets/ .
 * <P>
 * However this component also supports multiple rows of buttons, if you have
 * several different preference components to manage. Each row has a name, and
 * every element in a row should be vaguely related to that name. When this
 * panel shows multiple rows, the top of the panel has a browser-like header
 * with a back/forward button, and a "Show All" button (similar to a "Home"
 * button).
 * <P>
 * There are pros and cos to whether this widget should be displayed in a dialog
 * vs a frame. On the one hand, to get a really Mac-like experience (which is
 * the goal of this imitation), you should use a Frame. Also a Frame is the best
 * way to get the brushed-metal look (although in 10.5 the look in no way
 * resembles brushed-metal, that's just its name). However, dialogs are easier.
 * Dialogs can be modal. With a frame on a Mac, you need to provide some sort of
 * JMenuBar to give a native look-and-feel experience. And a frame requires your
 * preferences to all give live feedback: with a dialog you can wait until the
 * modal dialog is dismissed. Did I just make excuses for lazy programmers? Hard
 * to say. But I left the option in this class for you to create with a dialog
 * or frame for it.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2008/11/preferences-aqua-like-preferences.html">Preferences:
 *      Aqua-Like Preferences</a>
 */
public class PreferencePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	protected static boolean isMac = System.getProperty("os.name")
			.toLowerCase().indexOf("mac") != -1;

	public static void main(String[] args) {
		try {
			String lf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lf);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		PreferencePanel window1 = new PreferencePanel();
		PreferencePanel window2 = new PreferencePanel();
		Image[] icons = new Image[] {
				Toolkit.getDefaultToolkit().getImage("NSImage://NSBonjour"),
				Toolkit.getDefaultToolkit().getImage("NSImage://NSDotMac"),
				Toolkit.getDefaultToolkit().getImage("NSImage://NSComputer"),
				Toolkit.getDefaultToolkit().getImage(
						"NSImage://NSFolderBurnable"),
				Toolkit.getDefaultToolkit().getImage("NSImage://NSFolderSmart"),
				Toolkit.getDefaultToolkit().getImage(
						"NSImage://NSPreferencesGeneral"),
				Toolkit.getDefaultToolkit().getImage("NSImage://NSAdvanced") };
		int[] rows = new int[] { 7, 3, 2, 4, 5 };
		int ctr = 0;
		for (int i = 0; i < rows.length; i++) {
			AbstractButton[] buttons = new AbstractButton[rows[i]];
			JComponent[] components = new JComponent[rows[i]];
			for (int a = 0; a < rows[i]; a++) {
				Icon icon;

				if (isMac == false) {
					if (a % 4 == 0) {
						icon = UIManager.getIcon("OptionPane.informationIcon");
					} else if (a % 4 == 1) {
						icon = UIManager.getIcon("OptionPane.warningIcon");
					} else if (a % 4 == 2) {
						icon = UIManager.getIcon("OptionPane.questionIcon");
					} else {
						icon = UIManager.getIcon("OptionPane.errorIcon");
					}
				} else {
					icon = new ImageIcon(icons[a]);
				}
				buttons[a] = new JToggleButton(icon);
				buttons[a].setText("Button " + (ctr + 1));
				buttons[a].setBorderPainted(false);
				if (!isMac) {
					buttons[a].setBorder(null);
					buttons[a].setContentAreaFilled(false);
				}
				components[a] = new JPanel();
				components[a].setBackground(new Color(220, 220, 220));
				components[a].setOpaque(true);
				int w = (int) (300 + 100 * Math.random());
				int h = (int) (100 + 300 * Math.random());
				buttons[a].setHorizontalTextPosition(SwingConstants.CENTER);
				buttons[a].setVerticalTextPosition(SwingConstants.BOTTOM);

				JLabel label = new JLabel("Panel " + (ctr + 1));
				label.setPreferredSize(new Dimension(w, h));
				components[a].add(label);

				ctr++;
			}
			window1.addButtonRow(buttons, components, "Row " + (i + 1));
		}

		AbstractButton[] buttons = new AbstractButton[icons.length];
		JComponent[] components = new JComponent[icons.length];
		for (int a = 0; a < icons.length; a++) {
			Icon icon;
			if (isMac == false) {
				if (a % 4 == 0) {
					icon = UIManager.getIcon("OptionPane.informationIcon");
				} else if (a % 4 == 1) {
					icon = UIManager.getIcon("OptionPane.warningIcon");
				} else if (a % 4 == 2) {
					icon = UIManager.getIcon("OptionPane.questionIcon");
				} else {
					icon = UIManager.getIcon("OptionPane.errorIcon");
				}
			} else {
				icon = new ImageIcon(icons[a]);
			}
			buttons[a] = new JToggleButton(icon);
			buttons[a].setText("Button " + (a + 1));
			components[a] = new JPanel();
			components[a].setBackground(new Color(220, 220, 220));
			components[a].setOpaque(true);
			int w = (int) (300 + 100 * Math.random());
			int h = (int) (100 + 300 * Math.random());
			buttons[a].setHorizontalTextPosition(SwingConstants.CENTER);
			buttons[a].setVerticalTextPosition(SwingConstants.BOTTOM);
			JLabel label = new JLabel("Panel " + (a + 1));
			label.setPreferredSize(new Dimension(w, h));
			components[a].add(label);
		}
		window2.addButtonRow(buttons, components, "");

		JDialog dialog = window1.createDialog(null, "Preferences");
		dialog.pack();
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog = window2.createDialog(null, "Preferences");
		dialog.pack();
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JFrame frame = window1.createFrame("Preferences");
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		frame = window2.createFrame("Preferences");
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	ActionListener buttonListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AbstractButton button = (AbstractButton) e.getSource();
			show(button);
			for (int a = 0; a < rows.size(); a++) {
				Row row = rows.get(a);
				for (int b = 0; b < row.buttons.length; b++) {
					// only keep buttons selected if we're
					// showing 1 row of buttons:
					row.buttons[b].setSelected(rows.size() == 1
							&& row.buttons[b] == button);
				}
			}
		}
	};

	private AbstractButton selectedButton;
	private final JPanel header = new JPanel(new GridBagLayout());
	private final JSeparator separator = new JSeparator();
	private final FadingPanel contents = new FadingPanel(new GridBagLayout());

	private ArrayList<Row> rows = new ArrayList<Row>();
	private final JPanel homePanel = new JPanel(new GridBagLayout());
	private final JButton prevButton = NavigationButtons.createPrev();
	private final JButton nextButton = NavigationButtons.createNext();
	private final JButton homeButton = new JButton("Show All");
	private String defaultTitle = "";
	private final Stack<AbstractButton> navigationStack = new Stack<AbstractButton>();
	private int navigationPtr = 0;
	private boolean fixedSize = false;
	private Insets labelInsets = new Insets(6, 10, 2, 2);
	private Insets buttonInsets = new Insets(5, 5, 5, 5);

	ActionListener navigationListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == homeButton) {
				showHome();
			} else if (src == prevButton) {
				showPrev();
			} else if (src == nextButton) {
				showNext();
			}
		}
	};

	public PreferencePanel() {
		this("Preferences");
	}

	public PreferencePanel(String defaultTitle) {
		super();
		new WindowDragger(header);
		this.defaultTitle = defaultTitle;
		setLayout(new GridBagLayout());
		separator.setOpaque(false);
		separator.setUI(new HairSeparatorUI());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 2, 0);
		c.fill = GridBagConstraints.VERTICAL;
		header.add(prevButton, c);
		c.gridx++;
		c.insets = new Insets(2, 0, 2, 2);
		header.add(nextButton, c);
		c.gridx++;
		c.insets = new Insets(2, 2, 2, 2);
		header.add(homeButton, c);
		c.gridx++;
		JPanel fluff = new JPanel();
		fluff.setOpaque(false);
		c.weightx = 1;
		header.add(fluff, c);
		repack();

		if (isMac) {
			homeButton.putClientProperty("JButton.buttonType",
					"segmentedTextured");
		} else {
			homeButton.setUI(new BevelButtonUI());
		}

		prevButton.putClientProperty("JButton.segmentPosition", "first");
		nextButton.putClientProperty("JButton.segmentPosition", "last");
		homeButton.putClientProperty("JButton.segmentPosition", "only");

		navigationStack.add(homeButton);

		prevButton.addActionListener(navigationListener);
		nextButton.addActionListener(navigationListener);
		homeButton.addActionListener(navigationListener);

		if (isMac) {
			header.setUI(new GradientHeaderUI());
			separator.setForeground(new Color(64, 64, 64));
		}
	}

	/** Equivalent to clicking the "Forward" button. */
	public void showNext() {
		if (nextButton.isEnabled() == false)
			return;
		navigationPtr++;
		AbstractButton button = navigationStack.get(navigationPtr);
		show(button);
	}

	/** Equivalent to clicking the "Previous" button. */
	public void showPrev() {
		if (prevButton.isEnabled() == false)
			return;
		navigationPtr--;
		AbstractButton button = navigationStack.get(navigationPtr);
		show(button);
	}

	/** Equivalent to clicking the "Show All" button. */
	public void showHome() {
		show(homeButton);
	}

	/** Show the component associated with a particular button. */
	public void show(AbstractButton button) {
		if (selectedButton == button)
			return;

		selectedButton = button;

		String title = defaultTitle;
		if (selectedButton != homeButton) {
			title = selectedButton.getText();
		}
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Dialog) {
			((Dialog) w).setTitle(title);
		} else if (w instanceof Frame) {
			((Frame) w).setTitle(title);
		}

		if (navigationStack.get(navigationPtr) != button) {
			while (navigationStack.size() > navigationPtr + 1) {
				navigationStack.remove(navigationStack.size() - 1);
			}
			navigationStack.add(button);
			navigationPtr = navigationStack.size() - 1;
		}
		repack();
	}

	/**
	 * Add a row of buttons to this PreferencePanel. Each button corresponds to
	 * the component of the same index.
	 * 
	 * @param buttons
	 * @param components
	 * @param title
	 *            this is only used if more than 1 row is present
	 */
	public void addButtonRow(AbstractButton[] buttons, JComponent[] components,
			String title) {
		if (rows.size() == 1) {
			selectedButton = null;
		}
		Row newRow = new Row(buttons, components, title);
		rows.add(newRow);
		if (rows.size() == 1) {
			newRow.buttons[0].doClick();
		} else {
			show(homeButton);
			navigationStack.clear();
			navigationStack.add(homeButton);
			navigationPtr = 0;

			layoutHomePanel();
		}
		repack();
	}

	private void layoutHomePanel() {
		if (rows.size() <= 1) {
			return;
		}

		homePanel.removeAll();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;

		for (int a = 0; a < rows.size(); a++) {
			Row row = rows.get(a);
			JPanel rowBackground = new JPanel();
			rowBackground.setOpaque(true);
			if (a % 2 == 0) {
				rowBackground.setBackground(color1);
			} else {
				rowBackground.setBackground(color2);
			}

			c.gridheight = 1;
			c.insets = labelInsets;
			homePanel.add(row.label, c);
			c.gridy++;

			c.gridwidth = 1;
			c.insets = buttonInsets;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;
			for (int b = 0; b < row.buttons.length; b++) {
				c.gridx = b;
				homePanel.add(row.buttons[b], c);
				row.buttons[b].setSelected(false);
			}
			JPanel fluff = new JPanel();
			fluff.setOpaque(false);
			c.gridx = row.buttons.length;
			c.weightx = 1;
			c.insets = new Insets(0, 0, 0, 0);
			homePanel.add(fluff, c);

			c.gridx = 0;
			c.gridy--;

			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 2;
			homePanel.add(rowBackground, c);

			c.gridx = 0;
			c.gridy += 2;
			if (a < rows.size() - 1) {
				c.gridwidth = GridBagConstraints.REMAINDER;
				JSeparator newSeparator = new JSeparator();
				newSeparator.setUI(new HairSeparatorUI());
				newSeparator.setForeground(new Color(212, 212, 212));
				c.weighty = 0;
				homePanel.add(newSeparator, c);
				c.weighty = 1;
				c.gridy++;
			}
		}

		Dimension prefSize = homePanel.getPreferredSize();
		maxContentSize.width = Math.max(prefSize.width, maxContentSize.width);
		maxContentSize.height = Math
				.max(prefSize.height, maxContentSize.height);
	}

	// private boolean animateResizing = isMac;

	private void repack() {
		contents.animateStates(layoutRunnable);
		Window w = SwingUtilities.getWindowAncestor(this);

		if (w != null) {
			// TODO: this feature never really got off the ground, nor was it
			// essential.
			// if(animateResizing) {
			// contents.resizeWindow();
			// } else {
			w.pack();
			// }
		}
	}

	private static Color color1 = new Color(232, 232, 232);
	private static Color color2 = new Color(225, 225, 225);
	Runnable layoutRunnable = new Runnable() {
		public void run() {
			prevButton.setEnabled(navigationPtr > 0);
			nextButton.setEnabled(navigationPtr < navigationStack.size() - 1);

			if (rows.size() == 1) {
				/**
				 * We're supposed to show 1 row, and that row always stays
				 * visible.
				 */

				Row row = rows.get(0);
				if (row.getParent() == null) {
					// we haven't initialized this window yet:
					GridBagConstraints c = new GridBagConstraints();
					c.gridx = 0;
					c.gridy = 0;
					c.weightx = 1;
					c.fill = GridBagConstraints.HORIZONTAL;
					add(row, c);
					c.gridy++;
					add(separator, c);
					c.gridy++;
					c.weighty = 1;
					c.fill = GridBagConstraints.BOTH;
					add(contents, c);
				}

				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 0;
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 1;
				c.weighty = 1;
				contents.removeAll();
				JComponent component = row.getSelectedComponent();
				contents.add(component, c);

				if (fixedSize) {
					component.setPreferredSize(maxContentSize);
				} else {
					component.setPreferredSize(null);
					Dimension d = component.getPreferredSize();
					d.width = Math.max(d.width, row.getWidth());
					component.setPreferredSize(d);
				}
			} else if (rows.size() > 1) {
				// we either have several rows to show, or
				// show only the header + contents
				JComponent component = null;
				for (int a = 0; a < rows.size() && component == null; a++) {
					Row row = rows.get(a);
					if (component == null)
						component = row.getSelectedComponent();
				}

				Row firstRow = rows.get(0);
				if (firstRow.getParent() == PreferencePanel.this) {
					// we're adjusting from when there was only 1 row:
					// so we need to relayout this entire panel:
					removeAll();

					GridBagConstraints c = new GridBagConstraints();
					c.gridx = 0;
					c.gridy = 0;
					c.gridwidth = GridBagConstraints.REMAINDER;
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 1;
					c.weighty = 0;
					add(header, c);
					c.gridy++;
					add(separator, c);
					c.gridy++;
					c.weighty = 1;
					add(contents, c);
				}

				if (component == null) {
					contents.removeAll();
					GridBagConstraints c = new GridBagConstraints();
					c.gridx = 0;
					c.gridy = 0;
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 1;
					c.weighty = 1;
					contents.add(homePanel, c);

					if (fixedSize) {
						homePanel.setPreferredSize(maxContentSize);
					}
				} else {
					contents.removeAll();
					GridBagConstraints c = new GridBagConstraints();
					c.gridx = 0;
					c.gridy = 0;
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 1;
					c.weighty = 1;
					contents.add(component, c);

					if (fixedSize) {
						component.setPreferredSize(maxContentSize);
					}
				}
			}
			contents.refresh();
		}
	};

	/**
	 * Define the insets used to pad the component buttons. <BR>
	 * (This property is only used when more than 1 row is visible. If only 1
	 * row is used, then the buttons are wrapped in a JToolBar, and the default
	 * look-and-feel behavior for that toolbar is used.)
	 */
	public void setButtonInsets(Insets i) {
		buttonInsets = (Insets) i.clone();
		layoutHomePanel();
	}

	/**
	 * Define the insets used to pad the row labels. <BR>
	 * (This property is only used when labels are visible, which only occurs
	 * when there is more than 1 row.)
	 */
	public void setLabelInsets(Insets i) {
		labelInsets = (Insets) i.clone();
		layoutHomePanel();
	}

	/**
	 * Returns the insets used to pad the row labels. <BR>
	 * (This property is only used when labels are visible, which only occurs
	 * when there is more than 1 row.)
	 */
	public Insets getLabelInsets() {
		return (Insets) labelInsets.clone();
	}

	/**
	 * Returns the insets used to pad the component buttons. <BR>
	 * (This property is only used when more than 1 row is visible. If only 1
	 * row is used, then the buttons are wrapped in a JToolBar, and the default
	 * look-and-feel behavior for that toolbar is used.)
	 */
	public Insets getButtonInsets() {
		return (Insets) labelInsets.clone();
	}

	private final Dimension maxContentSize = new Dimension(0, 0);

	class Row extends JPanel {
		private static final long serialVersionUID = 1L;

		AbstractButton[] buttons;
		JComponent[] components;
		String title;
		JToolBar toolbar = new JToolBar();
		JLabel label = new JLabel();

		public Row(AbstractButton[] inButtons, JComponent[] inComponents,
				String inTitle) {
			super(new GridBagLayout());
			if (isMac) {
				setUI(new GradientHeaderUI());
			}
			if (inComponents.length != inButtons.length)
				throw new IllegalArgumentException("the number of buttons ("
						+ inButtons.length
						+ ") must equal the number of components ("
						+ components.length + ")");
			toolbar.setOpaque(false);
			buttons = new AbstractButton[inButtons.length];
			System.arraycopy(inButtons, 0, buttons, 0, buttons.length);
			components = new JComponent[inComponents.length];
			System.arraycopy(inComponents, 0, components, 0, buttons.length);
			title = inTitle;
			setOpaque(false);

			if (title != null)
				label.setText(title);

			label.setFont(label.getFont().deriveFont(Font.BOLD));

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.WEST;
			add(toolbar, c);
			for (int a = 0; a < buttons.length; a++) {
				if (buttons[a] == null)
					throw new NullPointerException("buttons[" + a + "] = null");
				if (components[a] == null)
					throw new NullPointerException("components[" + a
							+ "] = null");
				buttons[a].setOpaque(false);
				buttons[a].addActionListener(buttonListener);
				toolbar.add(buttons[a]);
				Dimension prefSize = components[a].getPreferredSize();
				maxContentSize.width = Math.max(maxContentSize.width,
						prefSize.width);
				maxContentSize.height = Math.max(maxContentSize.height,
						prefSize.height);
			}

			toolbar.setFloatable(false);
			new WindowDragger(this);
		}

		public JComponent getComponent(AbstractButton button) {
			for (int a = 0; a < buttons.length; a++) {
				if (button == buttons[a])
					return components[a];
			}
			return null;
		}

		public JComponent getSelectedComponent() {
			return getComponent(selectedButton);
		}
	}

	/**
	 * Indicates whether this panel should stay at a constant size, regardless
	 * of which preference is selected.
	 */
	public boolean isFixedSize() {
		return fixedSize;
	}

	/**
	 * Controls whether this panel is always sized to fit the largest possible
	 * component. If this is true, then no matter how the panel is adjusted it
	 * should not change its preferred size. If this is false, then depending on
	 * which control is visible the preferred size of this panel may change.
	 */
	public void setFixedSize(boolean b) {
		if (fixedSize == b)
			return;
		fixedSize = b;
		repack();
	}

	/** Creates a modal dialog displaying this PreferencePanel. */
	public JDialog createDialog(Frame parent, String name) {
		JDialog d = new JDialog(parent, name, true);

		if (isMac) {
			// brush metal isn't available for dialogs, only frames
			// d.getRootPane().putClientProperty("apple.awt.brushMetalLook",
			// Boolean.TRUE);
			separator.setForeground(new Color(64, 64, 64));
		}
		d.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		d.getContentPane().add(this, c);
		d.setResizable(false);
		InputMap inputMap = d.getRootPane().getInputMap(
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = d.getRootPane().getActionMap();

		inputMap.put(escapeKey, escapeKey);
		inputMap.put(commandPeriodKey, escapeKey);
		actionMap.put(escapeKey, closeDialogAndDisposeAction);

		return d;
	}

	/** Creates a modal dialog displaying this PreferencePanel. */
	public JFrame createFrame(String name) {
		JFrame f = new JFrame(name);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if (isMac) {
			f.getRootPane().putClientProperty("apple.awt.brushMetalLook",
					Boolean.TRUE);
			separator.setForeground(new Color(64, 64, 64));
		}
		f.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		f.getContentPane().add(this, c);
		f.setResizable(false);
		InputMap inputMap = f.getRootPane().getInputMap(
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = f.getRootPane().getActionMap();

		inputMap.put(escapeKey, escapeKey);
		inputMap.put(commandPeriodKey, escapeKey);
		actionMap.put(escapeKey, closeDialogAndDisposeAction);

		return f;
	}

	private static KeyStroke escapeKey = KeyStroke.getKeyStroke(
			KeyEvent.VK_ESCAPE, 0);
	private static KeyStroke commandPeriodKey = KeyStroke.getKeyStroke(
			KeyEvent.VK_PERIOD, Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask());

	/**
	 * This action takes the Window associated with the source of this event,
	 * hides it, and then calls <code>dispose()</code> on it.
	 * <P>
	 * (This will not throw an exception if there is no parent window, but it
	 * does nothing in that case...)
	 */
	public static Action closeDialogAndDisposeAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			Component src = (Component) e.getSource();
			Window w = SwingUtilities.getWindowAncestor(src);
			if (w == null)
				return;

			w.setVisible(false);
			w.dispose();
		}
	};
}

class HairSeparatorUI extends SeparatorUI {
	@Override
	public Dimension getMaximumSize(JComponent c) {
		return getPreferredSize(c);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		return new Dimension(1, 1);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		if (((JSeparator) c).getOrientation() == SwingConstants.HORIZONTAL) {
			return new Dimension(100, 1);
		} else {
			return new Dimension(1, 100);
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		Color foreground = Color.gray;
		c.setForeground(foreground);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		g.setColor(c.getForeground());
		if (((JSeparator) c).getOrientation() == SwingConstants.HORIZONTAL) {
			g.drawLine(0, 0, c.getWidth(), 0);
		} else {
			g.drawLine(0, 0, 0, c.getHeight());
		}
	}
}

class GradientHeaderUI extends PanelUI {

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Window w = SwingUtilities.getWindowAncestor(c);
		if (w instanceof JFrame) {
			JFrame frame = (JFrame) w;
			Object obj = frame.getRootPane().getClientProperty(
					"apple.awt.brushMetalLook");
			if (obj != null && obj.toString().equals("true"))
				return;
		}
		Graphics2D g2 = (Graphics2D) g;
		GradientPaint paint = new GradientPaint(0, 0, new Color(151, 151, 151),
				0, c.getHeight(), new Color(121, 121, 121));
		g2.setPaint(paint);
		g2.fillRect(0, 0, c.getWidth(), c.getHeight());
	}
}