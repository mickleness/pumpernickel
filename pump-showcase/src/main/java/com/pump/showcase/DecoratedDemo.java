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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import com.pump.icon.CloseIcon;
import com.pump.icon.FadedIcon;
import com.pump.icon.PaddedIcon;
import com.pump.icon.PauseIcon;
import com.pump.icon.RefreshIcon;
import com.pump.icon.StarIcon;
import com.pump.icon.TriangleIcon;
import com.pump.plaf.AquaThrobberUI;
import com.pump.plaf.LabelCellRenderer;
import com.pump.plaf.decorate.BasicDecoration;
import com.pump.plaf.decorate.DecoratedListUI;
import com.pump.plaf.decorate.DecoratedListUI.ListDecoration;
import com.pump.plaf.decorate.DecoratedTreeUI;
import com.pump.plaf.decorate.DecoratedTreeUI.TreeDecoration;
import com.pump.plaf.decorate.ListTreeDecoration;
import com.pump.plaf.decorate.RepaintingDecoration;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.SectionContainer.Section;

public class DecoratedDemo extends JPanel implements ShowcaseDemo {

	private static final long serialVersionUID = 1L;

	static final String NAME_PLAYABLE = "Playable";
	static final String NAME_WARNING = "Warning";
	static final String NAME_RATEABLE = "Rateable";
	static final String NAME_DELETABLE = "Deletable";
	static final String NAME_REFRESHABLE = "Refreshable";

	static final Insets ICON_INSETS = new Insets(0, 4, 0, 4);

	/**
	 * A warning icon. Based on wonderful silk icon set, available here:
	 * http://www.famfamfam.com/lab/icons/silk/
	 * 
	 */
	static Icon WARNING_ICON = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(PaddedIcon.class.getResource("warning.png")));

	/**
	 * A RepaintingTreeDecoration that pulses between [50%, 100%] opacity.
	 */
	static class PulsingDecoration extends RepaintingDecoration {

		Icon icon;

		PulsingDecoration(BasicDecoration decoration, int repaintInterval) {
			super(decoration, 20);
		}

		@Override
		public Icon getIcon(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus, boolean isRollover,
				boolean isPressed) {
			return pulse(super.getIcon(list, value, row, isSelected,
					cellHasFocus, isRollover, isPressed));
		}

		@Override
		public Icon getIcon(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean isRollover,
				boolean isPressed) {
			return pulse(super.getIcon(tree, value, selected, expanded, leaf,
					row, isRollover, isPressed));
		}

		private Icon pulse(Icon icon) {
			long current = System.currentTimeMillis();
			float f = current % 1500;
			f = f / 1500f;
			f = (float) (1 - Math.pow(
					.5 * Math.sin(2 * Math.PI * f - Math.PI / 2) + .5, 4));
			return new FadedIcon(icon, f);
		}
	}

	static class PlayPauseDecoration implements TreeDecoration, ListDecoration {
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				play = !play;
			}
		};

		Insets myIconInsets = new Insets(ICON_INSETS.top, ICON_INSETS.left,
				ICON_INSETS.bottom, ICON_INSETS.right + 4);
		Icon pauseNormalIcon = new PaddedIcon(
				new PauseIcon(10, 10, Color.gray), myIconInsets);
		Icon pausePressedIcon = new PaddedIcon(new PauseIcon(10, 10,
				Color.white), myIconInsets);
		Icon pauseRolloverIcon = new PaddedIcon(new PauseIcon(10, 10,
				Color.darkGray), myIconInsets);
		boolean play = true;
		Icon playNormalIcon = new PaddedIcon(new TriangleIcon(
				SwingConstants.EAST, 10, 10, Color.gray), myIconInsets);
		Icon playPressedIcon = new PaddedIcon(new TriangleIcon(
				SwingConstants.EAST, 10, 10, Color.white), myIconInsets);

		Icon playRolloverIcon = new PaddedIcon(new TriangleIcon(
				SwingConstants.EAST, 10, 10, Color.darkGray), myIconInsets);

		@Override
		public ActionListener getActionListener(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			return actionListener;
		}

		@Override
		public Icon getIcon(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean isRollover,
				boolean isPressed) {
			return getIcon(isPressed, isRollover);
		}

		private Icon getIcon(boolean isPressed, boolean isRollover) {
			if (play) {
				if (isPressed)
					return playPressedIcon;
				if (isRollover)
					return playRolloverIcon;
				return playNormalIcon;
			}
			if (isPressed)
				return pausePressedIcon;
			if (isRollover)
				return pauseRolloverIcon;
			return pauseNormalIcon;
		}

		@Override
		public boolean isVisible(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			value = ((DefaultMutableTreeNode) value).getUserObject();
			return isVisible(selected, value);
		}

		private boolean isVisible(boolean selected, Object value) {
			return selected && NAME_PLAYABLE.equals(value);
		}

		@Override
		public Icon getIcon(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus, boolean isRollover,
				boolean isPressed) {
			return getIcon(isPressed, isRollover);
		}

		@Override
		public boolean isVisible(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			return isVisible(isSelected, value);
		}

		@Override
		public ActionListener getActionListener(JList list, Object value,
				int row, boolean isSelected, boolean cellHasFocus) {
			return actionListener;
		}

		@Override
		public Point getLocation(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			Rectangle r = list.getUI().getCellBounds(list, row, row);
			Icon icon = getIcon(list, value, row, isSelected, cellHasFocus,
					false, false);
			return new Point(r.x + r.width - icon.getIconWidth(),
					list.getFixedCellHeight() / 2 - icon.getIconHeight() / 2);
		}
	}

	/**
	 * A decoration that paints a star. This edits/displays the
	 * DecoratedTreeDemo.starCount field.
	 *
	 */
	static class StarDecoration implements ListTreeDecoration {

		static final String PROPERTY_STAR_COUNT = StarDecoration.class
				.getName() + "#starCount";

		static Icon EMPTY_STAR_ICON = new StarIcon(16, 16, true);
		static Icon FULL_STAR_ICON = new StarIcon(16, 16, false);

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component.putClientProperty(PROPERTY_STAR_COUNT, myStarIndex);
			}
		};
		int myStarIndex;
		JComponent component;

		StarDecoration(JComponent jc, int starCount) {
			myStarIndex = starCount;
			component = jc;
		}

		@Override
		public ActionListener getActionListener(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			return actionListener;
		}

		@Override
		public Icon getIcon(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean isRollover,
				boolean isPressed) {
			return getIcon(tree);
		}

		private Icon getIcon(JComponent jc) {
			Number n = (Number) jc.getClientProperty(PROPERTY_STAR_COUNT);
			if (n == null)
				n = new Integer(0);
			if (n.intValue() >= myStarIndex) {
				return FULL_STAR_ICON;
			}
			return EMPTY_STAR_ICON;

		}

		@Override
		public boolean isVisible(JTree tree, Object value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			value = ((DefaultMutableTreeNode) value).getUserObject();
			return isVisible(selected, value);
		}

		private boolean isVisible(boolean selected, Object value) {
			return selected && NAME_RATEABLE.equals(value);
		}

		@Override
		public Icon getIcon(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus, boolean isRollover,
				boolean isPressed) {
			return getIcon(list);
		}

		@Override
		public boolean isVisible(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			return isVisible(isSelected, value);
		}

		@Override
		public ActionListener getActionListener(JList list, Object value,
				int row, boolean isSelected, boolean cellHasFocus) {
			return actionListener;
		}

		@Override
		public Point getLocation(JList list, Object value, int row,
				boolean isSelected, boolean cellHasFocus) {
			Rectangle r = list.getUI().getCellBounds(list, row, row);
			Icon icon = getIcon(list);
			return new Point(r.width - (6 - myStarIndex)
					* getIcon(list).getIconWidth(), list.getFixedCellHeight()
					/ 2 - icon.getIconHeight() / 2);
		}
	}

	CollapsibleContainer container = new CollapsibleContainer();
	Section listSection = container.addSection("DecoratedListUI",
			"DecoratedListUI");
	Section treeSection = container.addSection("DecoratedTreeUI",
			"DecoratedTreeUI");

	abstract static class AbstractDemo<T extends JComponent> extends JPanel {
		private static final long serialVersionUID = 1L;

		/**
		 * Slowly load child nodes into the root node to demonstrate the
		 * progress indicator decoration.
		 */
		class LoadingThread extends Thread {

			int delayBetweenElementAdditions;

			public LoadingThread(int delayBetweenElementAdditions) {
				super("Loading Child Nodes");
				this.delayBetweenElementAdditions = delayBetweenElementAdditions;
			}

			private void delay(long ms) {
				try {
					Thread.sleep(ms);
				} catch (Exception e) {
				}
			}

			@Override
			public void run() {
				Runnable r1 = new Runnable() {
					public void run() {
						removeElements(NAME_DELETABLE, NAME_PLAYABLE,
								NAME_RATEABLE, NAME_WARNING);
					}
				};
				if (SwingUtilities.isEventDispatchThread()) {
					r1.run();
				} else {
					SwingUtilities.invokeLater(r1);
				}
				delay(delayBetweenElementAdditions);
				addElement(NAME_PLAYABLE);
				delay(delayBetweenElementAdditions);
				addElement(NAME_WARNING);
				delay(delayBetweenElementAdditions);
				addElement(NAME_RATEABLE);
				delay(delayBetweenElementAdditions);
				addElement(NAME_DELETABLE);

				Runnable r2 = new Runnable() {
					public void run() {
						loadingThread = null;
						getDecoratedComponent().repaint();
					}
				};
				if (SwingUtilities.isEventDispatchThread()) {
					r2.run();
				} else {
					SwingUtilities.invokeLater(r2);
				}
			}
		}

		private Thread loadingThread = null;
		private T decoratedComponent;

		StarDecoration starDecoration1;
		StarDecoration starDecoration2;
		StarDecoration starDecoration3;
		StarDecoration starDecoration4;
		StarDecoration starDecoration5;

		/**
		 * This decoration is a close icon that removes a tree node when
		 * pressed.
		 */
		BasicDecoration closeDecoration = new BasicDecoration(new PaddedIcon(
				new CloseIcon(12), ICON_INSETS), new PaddedIcon(new CloseIcon(
				12, CloseIcon.State.ROLLOVER), ICON_INSETS), new PaddedIcon(
				new CloseIcon(12, CloseIcon.State.PRESSED), ICON_INSETS),
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeElements(NAME_DELETABLE);
					}
				}) {

			@Override
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				return super.isVisible(tree, value, selected, expanded, leaf,
						row, hasFocus) && isVisible(value);
			}

			@Override
			public boolean isVisible(JList list, Object value, int row,
					boolean isSelected, boolean cellHasFocus) {
				return super.isVisible(list, value, row, isSelected,
						cellHasFocus) && isVisible(value);
			}

			private boolean isVisible(Object value) {
				String name = (value instanceof DefaultMutableTreeNode) ? (String) ((DefaultMutableTreeNode) value)
						.getUserObject() : String.valueOf(value);
				return NAME_DELETABLE.equals(name);
			}
		};

		/**
		 * This decoration toggles between a play and pause button.
		 */
		PlayPauseDecoration playPauseDecoration = new PlayPauseDecoration();

		/** This decoration pulses a warning icon. */
		PulsingDecoration warningDecoration = new PulsingDecoration(
				new BasicDecoration(new PaddedIcon(WARNING_ICON, ICON_INSETS)) {
					@Override
					public boolean isVisible(JTree tree, Object value,
							boolean selected, boolean expanded, boolean leaf,
							int row, boolean hasFocus) {
						String name = (String) ((DefaultMutableTreeNode) value).getUserObject();
						return super.isVisible(tree, value, selected, expanded,
								leaf, row, hasFocus)
								&& NAME_WARNING.equals(name);
					}

					@Override
					public boolean isVisible(JList list, Object value, int row,
							boolean isSelected, boolean cellHasFocus) {
						return super.isVisible(list, value, row, isSelected,
								cellHasFocus) && NAME_WARNING.equals(value);
					}
				}, 50);

		/**
		 * This decoration shows the spinning progress indicator as long as
		 * there is a thread loading the contents of the root node.
		 */
		RepaintingDecoration progressDecoration = new RepaintingDecoration(
				new BasicDecoration(new PaddedIcon(
						(new AquaThrobberUI().createIcon(null, null)),
						ICON_INSETS)) {
					@Override
					public boolean isVisible(JTree tree, Object value,
							boolean selected, boolean expanded, boolean leaf,
							int row, boolean hasFocus) {
						return isVisible(value);
					}

					@Override
					public boolean isVisible(JList list, Object value, int row,
							boolean isSelected, boolean cellHasFocus) {
						return isVisible(value);
					}

					private boolean isVisible(Object value) {
						String name = (value instanceof DefaultMutableTreeNode) ? (String) ((DefaultMutableTreeNode) value).getUserObject()
								: String.valueOf(value);
						return (NAME_REFRESHABLE.equals(name) && loadingThread != null);
					}

				}, 50);

		/**
		 * This decoration re-loads the root node, causing the progress
		 * decoration to appear again. (While the thread is loading, this
		 * decoration is not available.)
		 */
		BasicDecoration refreshDecoration = new BasicDecoration(new PaddedIcon(
				new RefreshIcon(14, Color.gray), ICON_INSETS), new PaddedIcon(
				new RefreshIcon(14, Color.darkGray), ICON_INSETS),
				new PaddedIcon(new RefreshIcon(14, Color.white), ICON_INSETS),
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						loadChildNodes(false);
					}
				}) {

			@Override
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				return super.isVisible(tree, value, selected, expanded, leaf,
						row, hasFocus) && isVisible(value);
			}

			@Override
			public boolean isVisible(JList list, Object value, int row,
					boolean isSelected, boolean cellHasFocus) {
				return super.isVisible(list, value, row, isSelected,
						cellHasFocus) && isVisible(value);
			}

			private boolean isVisible(Object value) {
				String name = (value instanceof DefaultMutableTreeNode) ? (String) ((DefaultMutableTreeNode) value)
						.getUserObject() : String.valueOf(value);
				return loadingThread == null && NAME_REFRESHABLE.equals(name);
			}
		};

		public AbstractDemo() {
			T c = getDecoratedComponent();
			starDecoration1 = new StarDecoration(c, 1);
			starDecoration2 = new StarDecoration(c, 2);
			starDecoration3 = new StarDecoration(c, 3);
			starDecoration4 = new StarDecoration(c, 4);
			starDecoration5 = new StarDecoration(c, 5);

			loadChildNodes(true);
		}

		protected void loadChildNodes(boolean loadImmediately) {
			if (!SwingUtilities.isEventDispatchThread())
				throw new IllegalStateException();
			if (loadingThread != null)
				throw new IllegalStateException();
			loadingThread = new LoadingThread(loadImmediately ? 0 : 750);
			if (loadImmediately) {
				loadingThread.run();
			} else {
				loadingThread.start();
			}
		}

		abstract void addElement(String elementName);

		public abstract T createDecoratedComponent();

		abstract void removeElements(String... elementNames);

		public final T getDecoratedComponent() {
			if (decoratedComponent == null)
				decoratedComponent = createDecoratedComponent();
			return decoratedComponent;
		}
	}

	static class ListDemo extends AbstractDemo<JList<String>> {
		private static final long serialVersionUID = 1L;

		DefaultListModel<String> model;

		public ListDemo() {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			add(getDecoratedComponent(), c);

			getDecoratedComponent().putClientProperty(
					DecoratedListUI.KEY_DECORATIONS,
					new ListDecoration[] { progressDecoration, starDecoration1,
							starDecoration2, starDecoration3, starDecoration4,
							starDecoration5, closeDecoration,
							playPauseDecoration, warningDecoration,
							refreshDecoration });

			getDecoratedComponent().setUI(new DecoratedListUI());
			LabelCellRenderer<String> r = new LabelCellRenderer<String>() {

				@Override
				protected void formatLabelColors(JComponent jc,
						boolean isSelected, int rowNumber) {
					super.formatLabelColors(jc, isSelected, rowNumber);
					if (isSelected) {
						label.setBackground(SystemColor.textHighlight);
						label.setForeground(SystemColor.textHighlightText);
					}
				}
			};
			getDecoratedComponent().setCellRenderer(r);
			getDecoratedComponent().setFixedCellHeight(24);
			getDecoratedComponent().setPreferredSize(new Dimension(200, 150));
		}

		public JList<String> createDecoratedComponent() {
			model = new DefaultListModel<>();
			JList<String> l = new JList<>(model);
			model.addElement(NAME_REFRESHABLE);
			return l;
		}

		@Override
		void addElement(final String elementName) {
			Runnable r = new Runnable() {
				public void run() {
					model.addElement(elementName);
				}
			};
			if (SwingUtilities.isEventDispatchThread()) {
				r.run();
			} else {
				SwingUtilities.invokeLater(r);
			}
		}

		@Override
		void removeElements(String... elementNames) {
			for (String s : elementNames) {
				model.removeElement(s);
			}
		}
	}

	static class TreeDemo extends AbstractDemo<JTree> {
		private static final long serialVersionUID = 1L;

		DefaultMutableTreeNode closeable, playable, warning, rateable;

		JCheckBox stretchHighlight = new JCheckBox("Stretch Highlight", true);

		public TreeDemo() {
			getDecoratedComponent().putClientProperty(
					DecoratedTreeUI.KEY_DECORATIONS,
					new TreeDecoration[] { progressDecoration, starDecoration1,
							starDecoration2, starDecoration3, starDecoration4,
							starDecoration5, closeDecoration,
							playPauseDecoration, warningDecoration,
							refreshDecoration });

			getDecoratedComponent().setUI(new DecoratedTreeUI());
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setBackgroundSelectionColor(SystemColor.textHighlight);
			renderer.setTextSelectionColor(SystemColor.textHighlightText);

			/**
			 * We want to call setCellRenderer(..) *after* setUI(..) in this
			 * demo app just to verify that they can work in this order...
			 */
			getDecoratedComponent().setCellRenderer(renderer);
			// give the aqua progress indicator just a little bit more vertical
			// space
			getDecoratedComponent().setRowHeight(24);
			getDecoratedComponent().setPreferredSize(new Dimension(200, 150));

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			add(getDecoratedComponent(), c);
			c.weighty = 0;
			c.gridy++;
			add(stretchHighlight, c);

			stretchHighlight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getDecoratedComponent().setUI(
							new DecoratedTreeUI(stretchHighlight.isSelected()));
				}
			});
			stretchHighlight.setBackground(getDecoratedComponent()
					.getBackground());
			stretchHighlight.setOpaque(true);
		}

		DefaultMutableTreeNode root;
		DefaultTreeModel treeModel;

		@Override
		public JTree createDecoratedComponent() {
			root = new DefaultMutableTreeNode(NAME_REFRESHABLE);
			treeModel = new DefaultTreeModel(root);

			closeable = new DefaultMutableTreeNode(NAME_DELETABLE);
			playable = new DefaultMutableTreeNode(NAME_PLAYABLE);
			warning = new DefaultMutableTreeNode(NAME_WARNING);
			rateable = new DefaultMutableTreeNode(NAME_RATEABLE);

			return new JTree(treeModel);
		}

		@Override
		void removeElements(String... elementNames) {
			root.removeAllChildren();
			treeModel.reload(root);
			getDecoratedComponent().repaint();
		}

		@Override
		void addElement(String elementName) {
			final MutableTreeNode node;
			if (NAME_DELETABLE.equals(elementName)) {
				node = closeable;
			} else if (NAME_PLAYABLE.equals(elementName)) {
				node = playable;
			} else if (NAME_RATEABLE.equals(elementName)) {
				node = rateable;
			} else if (NAME_WARNING.equals(elementName)) {
				node = warning;
			} else {
				throw new IllegalArgumentException("Unrecognized name \""
						+ elementName + "\"");
			}
			Runnable r = new Runnable() {
				public void run() {
					treeModel.insertNodeInto(node, root, root.getChildCount());
					getDecoratedComponent().expandRow(0);
				}
			};
			if (SwingUtilities.isEventDispatchThread()) {
				r.run();
			} else {
				SwingUtilities.invokeLater(r);
			}
		}

	}

	public DecoratedDemo() {
		PumpernickelShowcaseApp.installSections(this, container, listSection,
				treeSection);

		treeSection.getBody().setLayout(new GridBagLayout());
		listSection.getBody().setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		treeSection.getBody().add(new TreeDemo(), c);
		listSection.getBody().add(new ListDemo(), c);
	}

	@Override
	public String getTitle() {
		return "DecoratedListUI, DecoratedTreeUI Demo";
	}

	@Override
	public URL getHelpURL() {
		return DecoratedDemo.class.getResource("decoratedDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "decoration", "decorate", "icon", "button",
				"list", "tree", "ux" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { DecoratedListUI.class, DecoratedTreeUI.class,
				JTree.class, JList.class };
	}
}