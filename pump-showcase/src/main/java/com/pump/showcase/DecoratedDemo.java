package com.pump.showcase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
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
import com.pump.plaf.DecoratedListUI;
import com.pump.plaf.DecoratedTreeUI;
import com.pump.plaf.DecoratedTreeUI.BasicTreeDecoration;
import com.pump.plaf.DecoratedTreeUI.RepaintingTreeDecoration;
import com.pump.plaf.DecoratedTreeUI.TreeDecoration;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.SectionContainer.Section;

public class DecoratedDemo extends JPanel implements ShowcaseDemo {

	private static final long serialVersionUID = 1L;

	CollapsibleContainer container = new CollapsibleContainer();
	Section listSection = container.addSection("DecoratedListUI",
			"DecoratedListUI");
	Section treeSection = container.addSection("DecoratedTreeUI",
			"DecoratedTreeUI");

	static class TreeDemo extends JPanel {
		private static final long serialVersionUID = 1L;

		/**
		 * Slowly load child nodes into the root node to demonstrate the
		 * progress indicator decoration.
		 */
		class LoadingThread extends Thread {
			public LoadingThread() {
				super("Loading Child Nodes");
			}

			private void add(final MutableTreeNode node) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						treeModel.insertNodeInto(node, root,
								root.getChildCount());
						tree.expandRow(0);
					}
				});
			}

			private void delay(long ms) {
				try {
					Thread.sleep(ms);
				} catch (Exception e) {
				}
			}

			private void repaintRoot() {
				tree.repaint(tree.getRowBounds(0));
			}

			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						root.removeAllChildren();
						treeModel.reload(root);
						repaintRoot();
					}
				});
				delay(750);
				add(playable);
				delay(750);
				add(warning);
				delay(750);
				add(rateable);
				delay(750);
				add(closeable);

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						loadingThread = null;
						repaintRoot();
					}
				});
			}
		}

		/**
		 * A RepaintingTreeDecoration that pulses between [50%, 100%] opacity.
		 */
		static class PulsingTreeDecoration extends RepaintingTreeDecoration {

			Icon icon;

			PulsingTreeDecoration(TreeDecoration treeDecoration,
					int repaintInterval) {
				super(treeDecoration, 20);
			}

			@Override
			public Icon getIcon(JTree tree, Object value, boolean selected,
					boolean expanded, boolean leaf, int row,
					boolean isRollover, boolean isPressed) {
				Icon returnValue = super.getIcon(tree, value, selected,
						expanded, leaf, row, isRollover, isPressed);

				long current = System.currentTimeMillis();
				float f = current % 2000;
				// convert f to [0, 1]
				if (f < 1000) {
					f = f / 1000f;
				} else {
					f = (2000 - f) / 1000f;
				}
				// convert f to [.5, 1]
				f = .5f * f + .5f;
				return new FadedIcon(returnValue, f);
			}
		}

		/**
		 * A decoration that paints a star. This edits/displays the
		 * DecoratedTreeDemo.starCount field.
		 *
		 */
		class StarDecoration extends TreeDecoration {
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					starCount = myStarCount;
				}
			};
			int myStarCount;

			StarDecoration(int starCount) {
				myStarCount = starCount;
			}

			@Override
			public ActionListener getActionListener(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				return actionListener;
			}

			@Override
			public Icon getIcon(JTree tree, Object value, boolean selected,
					boolean expanded, boolean leaf, int row,
					boolean isRollover, boolean isPressed) {
				if (starCount >= myStarCount) {
					return FULL_STAR_ICON;
				}
				return EMPTY_STAR_ICON;

			}

			@Override
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				return selected && value == rateable;
			}
		}

		static Icon EMPTY_STAR_ICON = new StarIcon(16, 16, true);
		static Icon FULL_STAR_ICON = new StarIcon(16, 16, false);
		/**
		 * A warning icon. Based on wonderful silk icon set, available here:
		 * http://www.famfamfam.com/lab/icons/silk/
		 * 
		 */
		static Icon WARNING_ICON = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(PaddedIcon.class.getResource("warning.png")));

		DefaultMutableTreeNode closeable, playable, warning, rateable;
		Insets iconInsets = new Insets(0, 4, 0, 4);

		/**
		 * This decoration is a close icon that removes a tree node when
		 * pressed.
		 */
		TreeDecoration closeDecoration = new BasicTreeDecoration(
				new PaddedIcon(new CloseIcon(12), iconInsets),
				new PaddedIcon(new CloseIcon(12, CloseIcon.State.ROLLOVER),
						iconInsets), new PaddedIcon(new CloseIcon(12,
						CloseIcon.State.PRESSED), iconInsets),
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						treeModel.removeNodeFromParent(closeable);
					}
				}) {

			@Override
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				return super.isVisible(tree, value, selected, expanded, leaf,
						row, hasFocus) && value == closeable;
			}
		};

		Thread loadingThread = null;

		/**
		 * This decoration toggles between a play and pause button.
		 */
		TreeDecoration playPauseDecoration = new TreeDecoration() {
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					play = !play;
				}
			};

			Insets myIconInsets = new Insets(iconInsets.top, iconInsets.left,
					iconInsets.bottom, iconInsets.right + 4);
			Icon pauseNormalIcon = new PaddedIcon(new PauseIcon(10, 10,
					Color.gray), myIconInsets);
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
					boolean expanded, boolean leaf, int row,
					boolean isRollover, boolean isPressed) {
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
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				return selected && value == playable;
			}
		};

		/**
		 * This decoration shows the spinning progress indicator as long as
		 * there is a thread loading the contents of the root node.
		 */
		TreeDecoration progressDecoration = new RepaintingTreeDecoration(
				new BasicTreeDecoration(new PaddedIcon(
						(new AquaThrobberUI().createIcon(null, null)),
						iconInsets)) {
					@Override
					public boolean isVisible(JTree tree, Object value,
							boolean selected, boolean expanded, boolean leaf,
							int row, boolean hasFocus) {
						return (value == root && loadingThread != null);
					}
				}, 50);

		/**
		 * This decoration re-loads the root node, causing the progress
		 * decoration to appear again. (While the thread is loading, this
		 * decoration is not available.)
		 */
		TreeDecoration refreshDecoration = new BasicTreeDecoration(
				new PaddedIcon(new RefreshIcon(14, Color.gray), iconInsets),
				new PaddedIcon(new RefreshIcon(14, Color.darkGray), iconInsets),
				new PaddedIcon(new RefreshIcon(14, Color.white), iconInsets),
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						loadChildNodes();
					}
				}) {

			@Override
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				return super.isVisible(tree, value, selected, expanded, leaf,
						row, hasFocus)
						&& loadingThread == null
						&& value == root;
			}
		};
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root Node");

		int starCount = 0;
		StarDecoration starDecoration1 = new StarDecoration(1);
		StarDecoration starDecoration2 = new StarDecoration(2);
		StarDecoration starDecoration3 = new StarDecoration(3);
		StarDecoration starDecoration4 = new StarDecoration(4);
		StarDecoration starDecoration5 = new StarDecoration(5);

		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		JTree tree = new JTree(treeModel);

		/** This decoration pulses a warning icon. */
		TreeDecoration warningDecoration = new PulsingTreeDecoration(
				new BasicTreeDecoration(
						new PaddedIcon(WARNING_ICON, iconInsets)) {
					@Override
					public boolean isVisible(JTree tree, Object value,
							boolean selected, boolean expanded, boolean leaf,
							int row, boolean hasFocus) {
						return super.isVisible(tree, value, selected, expanded,
								leaf, row, hasFocus) && value == warning;
					}
				}, 50);

		JCheckBox stretchHighlight = new JCheckBox("Stretch Highlight", true);

		public TreeDemo() {
			closeable = new DefaultMutableTreeNode("Deletable");
			playable = new DefaultMutableTreeNode("Playable");
			warning = new DefaultMutableTreeNode("Warning");
			rateable = new DefaultMutableTreeNode("Rateable");

			tree.putClientProperty(DecoratedTreeUI.KEY_DECORATIONS,
					new TreeDecoration[] { progressDecoration, starDecoration1,
							starDecoration2, starDecoration3, starDecoration4,
							starDecoration5, closeDecoration,
							playPauseDecoration, warningDecoration,
							refreshDecoration });

			tree.setUI(new DecoratedTreeUI());
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setBackgroundSelectionColor(SystemColor.textHighlight);
			renderer.setTextSelectionColor(SystemColor.textHighlightText);

			/**
			 * We want to call setCellRenderer(..) *after* setUI(..) in this
			 * demo app just to verify that they can work in this order...
			 */
			tree.setCellRenderer(renderer);
			// give the aqua progress indicator just a little bit more vertical
			// space
			tree.setRowHeight(24);

			loadChildNodes();
			tree.setPreferredSize(new Dimension(200, 150));

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			add(tree, c);
			c.weighty = 0;
			c.gridy++;
			add(stretchHighlight, c);

			stretchHighlight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tree.setUI(new DecoratedTreeUI(stretchHighlight
							.isSelected()));
				}
			});
			stretchHighlight.setBackground(tree.getBackground());
			stretchHighlight.setOpaque(true);
		}

		protected void loadChildNodes() {
			if (!SwingUtilities.isEventDispatchThread())
				throw new IllegalStateException();
			if (loadingThread != null)
				throw new IllegalStateException();
			loadingThread = new LoadingThread();
			loadingThread.start();
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
	}

	@Override
	public String getTitle() {
		return "DecoratedListUI, DecoratedTreeUI Demo";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "decoration", "list", "tree" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { DecoratedListUI.class, DecoratedTreeUI.class,
				JTree.class, JList.class };
	}

	@Override
	public boolean isSeparatorVisible() {
		return false;
	}

}
