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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.LabelUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import com.pump.awt.MouseTracker;
import com.pump.icon.CloseIcon;
import com.pump.icon.FadedIcon;
import com.pump.icon.PaddedIcon;
import com.pump.icon.PauseIcon;
import com.pump.icon.RefreshIcon;
import com.pump.icon.StarIcon;
import com.pump.icon.TriangleIcon;
import com.pump.plaf.AquaThrobberUI;
import com.pump.plaf.BreadCrumbUI;
import com.pump.plaf.ChasingArrowsThrobberUI;
import com.pump.plaf.DecoratedTreeUI;
import com.pump.plaf.DecoratedTreeUI.BasicTreeDecoration;
import com.pump.plaf.DecoratedTreeUI.RepaintingTreeDecoration;
import com.pump.plaf.DecoratedTreeUI.TreeDecoration;
import com.pump.plaf.DetachingArcThrobberUI;
import com.pump.plaf.PivotingCirclesThrobberUI;
import com.pump.plaf.PulsingCirclesThrobberUI;
import com.pump.plaf.SierpinskiThrobberUI;
import com.pump.plaf.ThrobberUI;
import com.pump.swing.JBreadCrumb;
import com.pump.swing.JBreadCrumb.BreadCrumbFormatter;
import com.pump.swing.JThrobber;
import com.pump.swing.MagnificationPanel;
import com.pump.util.JVM;

public class SwingComponentsDemo extends MultiWindowDemo {
	private static final long serialVersionUID = 1L;

	static class ThrobberDemo extends JInternalFrame {
		private static final long serialVersionUID = 1L;
		
		/** The optional real aqua UI */
		protected JProgressBar aquaIndicator = new JProgressBar();
		
		protected JPanel throbberContainer = new JPanel(new GridBagLayout());
		
		/** This slows each spinning UI down to help in bug testing. */
		protected JLabel throbberLabel = new JLabel("ThrobberUIs:");
		protected JLabel aquaLabel = new JLabel("Aqua Throbber:");
		protected JCheckBox slowMode = new JCheckBox("Slow Mode",false);
		protected List<JThrobber> throbbers = new ArrayList<JThrobber>();
		
		public ThrobberDemo() {
			super("JThrobber");
				
			throbberContainer.setOpaque(false);
			addSampleUI(new ChasingArrowsThrobberUI());
			addSampleUI(new AquaThrobberUI());
			addSampleUI(new PulsingCirclesThrobberUI());
			addSampleUI(new DetachingArcThrobberUI());
			addSampleUI(new SierpinskiThrobberUI());
			addSampleUI(new PivotingCirclesThrobberUI());
			
			getContentPane().setBackground(Color.white);
			getContentPane().setLayout(new GridBagLayout());
			
			slowMode.putClientProperty(KEY_DESCRIPTION, "When selected this slows all the ThrobberUIs down so you can better see each frame.");
			
			getRootPane().putClientProperty(KEY_DESCRIPTION, "This window demonstrates the JThrobber class and a few sample ThrobberUIs. A \"throbber\" is a (poorly named?) component that is used to indicate that something is happening."
					+ "\n\nAmong other things, it simply helps reassure the user that the UI/app isn't frozen. Functionally it is similar to an indeterminate JProgressBar, although it is more compact.");
			
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.insets = new Insets(5,5,5,5);
			c.weightx = 1;
			c.weighty = 1; c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			getContentPane().add(throbberLabel, c);
			c.gridy++;
			getContentPane().add(slowMode,c);
			c.gridy++; c.gridwidth = 1; c.fill = GridBagConstraints.NONE;
			getContentPane().add(throbberContainer,c);
			
			if(JVM.isMac) {
				aquaIndicator.putClientProperty("JProgressBar.style", "circular");
				aquaIndicator.setIndeterminate(true);
				
				c.gridy++;
				c.fill = GridBagConstraints.HORIZONTAL;
				getContentPane().add(aquaLabel, c);
				c.gridy++;
				getContentPane().add(aquaIndicator, c);
				aquaIndicator.putClientProperty(KEY_DESCRIPTION, "On Macs there is built-in support for throbbers. Take a JProgressBar, set it to indeterminate, and set the client property \"JProgressBar.style\" to \"circular\". For more details, google \"Apple Technical Note 2196\".");
			}
			
			slowMode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setSlowFactor( slowMode.isSelected() ? 4 : 1 );
				}
			});
		}
		
		protected void setSlowFactor(int factor) {
			for(Component c : throbberContainer.getComponents()) {
				if(c instanceof JThrobber) {
					((JThrobber)c).putClientProperty( ThrobberUI.PERIOD_MULTIPLIER_KEY, factor);
				}
			}
		}
		
		/** Add a JThrobber to the throbberContainer. */
		private void addSampleUI(ThrobberUI ui) {
			JThrobber throbber = new JThrobber();
			throbber.setUI(ui);
			throbbers.add(throbber);
			
			String name = ui.getClass().getName();
			name = "This is a sample of the "+name.substring(name.lastIndexOf('.')+1)+".";
			addSample(throbber, name);
		}

		/** Add a JComponent to the throbberContainer. */
		private void addSample(JComponent jc,final String labelText) {
			int index = throbberContainer.getComponentCount();
			int row = index / 8;
			int column = index % 8;
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = column; c.gridy = row;
			c.weightx = 1; c.weighty = 1;
			c.insets = new Insets(4,4,4,4);
			throbberContainer.add(jc, c);
			
			jc.putClientProperty(KEY_DESCRIPTION, labelText);
		}
	}
	
	static class DecoratedTreeDemo extends JInternalFrame {
		private static final long serialVersionUID = 1L;

		/** Slowly load child nodes into the root node to demonstrate the progress indicator decoration.
		 */
		class LoadingThread extends Thread {
			public LoadingThread() {
				super("Loading Child Nodes");
			}

			private void add(final MutableTreeNode node) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						treeModel.insertNodeInto(node, root, root.getChildCount());
						tree.expandRow(0);
					}
				});
			}

			private void delay(long ms) {
				try {
					Thread.sleep(ms);
				} catch(Exception e) {}
			}

			private void repaintRoot() {
				tree.repaint( tree.getRowBounds(0) );
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

		/** A RepaintingTreeDecoration that pulses between [50%, 100%] opacity.
		 */
		static class PulsingTreeDecoration extends RepaintingTreeDecoration {

			Icon icon;

			PulsingTreeDecoration(TreeDecoration treeDecoration,int repaintInterval) {
				super(treeDecoration, 20);
			}

			@Override
			public Icon getIcon(JTree tree, Object value, boolean selected,
					boolean expanded, boolean leaf, int row, boolean isRollover,
					boolean isPressed) {
				Icon returnValue = super.getIcon(tree, value, selected, expanded, leaf, row, isRollover, isPressed);

				long current = System.currentTimeMillis();
				float f = current%2000;
				//convert f to [0, 1]
				if(f<1000) {
					f = f/1000f;
				} else {
					f = (2000-f)/1000f;
				}
				//convert f to [.5, 1]
				f = .5f*f+.5f;
				return new FadedIcon(returnValue, f);
			}
		}

		/** A decoration that paints a star. This edits/displays the
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
					boolean expanded, boolean leaf, int row, boolean isRollover,
					boolean isPressed) {
				if(starCount>=myStarCount) {
					return FULL_STAR_ICON;
				}
				return EMPTY_STAR_ICON;

			}
			@Override
			public boolean isVisible(JTree tree, Object value, boolean selected,
					boolean expanded, boolean leaf, int row, boolean hasFocus) {
				return selected && value==rateable;
			}
		}
		
		static Icon EMPTY_STAR_ICON = new StarIcon(16, 16, true);
		static Icon FULL_STAR_ICON = new StarIcon(16, 16, false);
		/** A warning icon.
		 * Based on wonderful silk icon set, available here:
		 * http://www.famfamfam.com/lab/icons/silk/
		 * 
		 */
		static Icon WARNING_ICON = new ImageIcon( Toolkit.getDefaultToolkit().getImage( PaddedIcon.class.getResource("warning.png")));

		DefaultMutableTreeNode closeable, playable, warning, rateable;
		Insets iconInsets = new Insets(0,4,0,4);

		/** This decoration is a close icon that removes a tree node when pressed. */
		TreeDecoration closeDecoration = new BasicTreeDecoration(
				new PaddedIcon(new CloseIcon(12), iconInsets),
				new PaddedIcon(new CloseIcon(12, CloseIcon.State.ROLLOVER), iconInsets),
				new PaddedIcon(new CloseIcon(12, CloseIcon.State.PRESSED), iconInsets),
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						treeModel.removeNodeFromParent(closeable);
					}
				} ) {

			@Override
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				return super.isVisible(tree, value, selected, expanded, leaf, row, hasFocus) && 
				value==closeable;
			}
		};

		Thread loadingThread = null;
		
		/** This decoration toggles between a play and pause button.
		 */
		TreeDecoration playPauseDecoration = new TreeDecoration() {
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					play = !play;
				}
			};

			Insets myIconInsets = new Insets(iconInsets.top, iconInsets.left, iconInsets.bottom, iconInsets.right+4);
			Icon pauseNormalIcon = new PaddedIcon(new PauseIcon(10, 10, Color.gray), myIconInsets);
			Icon pausePressedIcon = new PaddedIcon(new PauseIcon(10, 10, Color.white), myIconInsets);
			Icon pauseRolloverIcon = new PaddedIcon(new PauseIcon(10, 10, Color.darkGray), myIconInsets);
			boolean play = true;
			Icon playNormalIcon = new PaddedIcon(new TriangleIcon(SwingConstants.EAST, 10, 10, Color.gray), myIconInsets);
			Icon playPressedIcon = new PaddedIcon(new TriangleIcon(SwingConstants.EAST, 10, 10, Color.white), myIconInsets);

			Icon playRolloverIcon = new PaddedIcon(new TriangleIcon(SwingConstants.EAST, 10, 10, Color.darkGray), myIconInsets);

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
				if(play) {
					if(isPressed)
						return playPressedIcon;
					if(isRollover)
						return playRolloverIcon;
					return playNormalIcon;
				}
				if(isPressed)
					return pausePressedIcon;
				if(isRollover)
					return pauseRolloverIcon;
				return pauseNormalIcon;
			}

			@Override
			public boolean isVisible(JTree tree, Object value, boolean selected,
					boolean expanded, boolean leaf, int row, boolean hasFocus) {
				return selected && value==playable;
			}
		};
		
		/** This decoration shows the spinning progress indicator as long as there is a thread loading
		 *  the contents of the root node.
		 */
		TreeDecoration progressDecoration = new RepaintingTreeDecoration( new BasicTreeDecoration(new PaddedIcon( (new AquaThrobberUI().createIcon(null, null)) , iconInsets)) {
			@Override
			public boolean isVisible(JTree tree, Object value,
					boolean selected, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				return (value==root && loadingThread!=null);
			}
		}, 50 );
		
		/** This decoration re-loads the root node, causing the progress decoration to appear again.
		 * (While the thread is loading, this decoration is not available.)
		 */
		TreeDecoration refreshDecoration = new BasicTreeDecoration( new PaddedIcon( new RefreshIcon(14, Color.gray), iconInsets),
				new PaddedIcon( new RefreshIcon(14, Color.darkGray), iconInsets), 
				new PaddedIcon( new RefreshIcon(14, Color.white), iconInsets),
				new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadChildNodes();
			}
		}) {

			@Override
			public boolean isVisible(JTree tree, Object value, boolean selected,
					boolean expanded, boolean leaf, int row, boolean hasFocus) {
				return super.isVisible(tree, value, selected, expanded, leaf, row, hasFocus) && 
				loadingThread==null && value==root;
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
		TreeDecoration warningDecoration = new PulsingTreeDecoration( new BasicTreeDecoration(new PaddedIcon( WARNING_ICON, iconInsets)) {
					@Override
					public boolean isVisible(JTree tree, Object value,
							boolean selected, boolean expanded, boolean leaf,
							int row, boolean hasFocus) {
						return super.isVisible(tree, value, selected, expanded, leaf, row, hasFocus) && 
						value==warning;
					}
			}, 50);
		
		JCheckBox stretchHighlight = new JCheckBox("Stretch Highlight", true);
		
		public DecoratedTreeDemo() {
			super("DecoratedTree");
			
			getRootPane().putClientProperty(KEY_DESCRIPTION, "The DecoratedTreeUI adds clickable decorations on the right side of a JTree. This is a handy way to provide shortcuts to functions, but because these are \"stamped\" components: they don't really exist in the tree hierarchy. Because of this they are not keyboard accessible, and therefore you need redundant ways to access these features for special needs users (probably keyboard shortcuts).");
			
			closeable = new DefaultMutableTreeNode("Deletable");
			playable = new DefaultMutableTreeNode("Playable");
			warning = new DefaultMutableTreeNode("Warning");
			rateable = new DefaultMutableTreeNode("Rateable");

			tree.putClientProperty( DecoratedTreeUI.KEY_DECORATIONS, new TreeDecoration[] { progressDecoration, 
					starDecoration1, starDecoration2, starDecoration3, starDecoration4, starDecoration5, 
					closeDecoration, playPauseDecoration, warningDecoration, refreshDecoration });

			tree.setUI(new DecoratedTreeUI());
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setBackgroundSelectionColor( SystemColor.textHighlight );
			renderer.setTextSelectionColor( SystemColor.textHighlightText  );

			/** We want to call setCellRenderer(..) *after* setUI(..) in this demo app
			 * just to verify that they can work in this order...
			 */
			tree.setCellRenderer(renderer);
			//give the aqua progress indicator just a little bit more vertical space
			tree.setRowHeight(24);

			loadChildNodes();
			tree.setPreferredSize(new Dimension(200, 150));

			getContentPane().setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			getContentPane().add(tree, c);
			c.weighty = 0; c.gridy++;
			getContentPane().add(stretchHighlight, c);
			
			stretchHighlight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tree.setUI(new DecoratedTreeUI(stretchHighlight.isSelected()));
				}
			});
			stretchHighlight.setBackground(tree.getBackground());
			stretchHighlight.setOpaque(true);
		}

		protected void loadChildNodes() {
			if(!SwingUtilities.isEventDispatchThread())
				throw new IllegalStateException();
			if(loadingThread!=null)
				throw new IllegalStateException();
			loadingThread = new LoadingThread();
			loadingThread.start();
		}
	}

	
	static class BreadCrumbDemo extends JInternalFrame {
		private static final long serialVersionUID = 1L;
		
		JBreadCrumb<String> crumbs1 = new JBreadCrumb<String>();
		JBreadCrumb<String> crumbs2 = new JBreadCrumb<String>();
		JPanel crumbs2Container = new JPanel(new GridBagLayout());

		
		/** Create a default BreadCrumbDemo with generous insets, a small width (to demonstrate collapsing) and a simple file path. */
		public BreadCrumbDemo() {
			this(15, true, "Macintosh HD", "Users", "Hercules", "Pictures", "Labour Selfies", "Cerberus");
		}
		
		/** Create a BreadCrumbDemo
		 * 
		 * @param insets the insets to apply to the content area
		 * @param collapse if true the then the width of this panel is reduced so collapsing is necessary.
		 * @param strings a demo path of bread crumbs
		 */
		public BreadCrumbDemo(int insets,boolean collapse,String... strings) {
			super("JBreadCrumb");
			
			getRootPane().putClientProperty(KEY_DESCRIPTION, "A breadcrumb component is often used to depict a path in a tree (such as a file path). This component has a variety of listeners, and a collapsing UI that helps squeeze long paths into compact spaces. (Try shrinking the width of the window!)");
			setResizable(true);
			
			// set up crumbs1:
			crumbs1.setPath(strings);
			crumbs1.setFormatter(new BreadCrumbFormatter<String>() {

				public void format(JBreadCrumb<String> container, JLabel label,
						String pathNode, int index) {
					label.setText(pathNode);
					label.setIcon(UIManager.getIcon("Tree.openIcon"));
				}
				
			});
			crumbs1.setBorder(new EmptyBorder(insets,insets,insets,insets));
			crumbs1.setOpaque(true);
			crumbs1.setBackground(Color.white);
			if(collapse) {
				Dimension d = crumbs1.getPreferredSize();
				d.width -= 100;
				crumbs1.setPreferredSize(d);
			}
			

			// set up crumbs2:
			Icon lankySeparator = new Icon() {
				
				int separatorWidth = 5;
				int leftPadding = 3;
				int rightPadding = 5;

				public void paintIcon(Component c, Graphics g, int x, int y) {
					Graphics2D g2 = (Graphics2D)g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					int h = getIconHeight()-1;
					GeneralPath arrow = new GeneralPath();
					arrow.moveTo(x + leftPadding, y);
					arrow.lineTo(x + leftPadding + separatorWidth, y + h / 2);
					arrow.lineTo(x + leftPadding, y + h);
					g2.setStroke(new BasicStroke(2));
					g2.setColor(new Color(0,0,0,10));
					g2.draw(arrow);
					g2.setStroke(new BasicStroke(1));
					g2.setColor(new Color(0,0,0,40));
					g2.draw(arrow);
					g2.dispose();
				}

				public int getIconWidth() {
					return separatorWidth + leftPadding + rightPadding;
				}

				public int getIconHeight() {
					return 22;
				}
				
			};
			
			crumbs2.setUI(new BreadCrumbUI() {
				@Override
				public void paint(Graphics g, JComponent c) {
					Graphics2D g2 = (Graphics2D)g.create();
					GradientPaint paint = new GradientPaint(0,0, new Color(0xFFFFFF), 0, c.getHeight(), new Color(0xDDDDDD));
					g2.setPaint(paint);
					g2.fillRect(0,0,c.getWidth(),c.getHeight());
					super.paint(g2, c);
					g2.dispose();
				}
				
				@Override
				protected LabelUI getLabelUI() {
					return null; //new EmphasizedLabelUI();
				}
			});
			crumbs2.setPath(strings);
			crumbs2.setFormatter(new BreadCrumbFormatter<String>() {

				public void format(JBreadCrumb<String> container, JLabel label,
						String pathNode, int index) {
					label.setText(pathNode);
				}
				
			});
			crumbs2.putClientProperty(BreadCrumbUI.SEPARATOR_ICON_KEY, lankySeparator);
			crumbs2.setBorder(new EmptyBorder(0,5,0,0));
			crumbs2.setOpaque(true);
			crumbs2.setBackground(Color.white);
			if(collapse) {
				Dimension d = crumbs2.getPreferredSize();
				d.width -= 100;
				crumbs2.setPreferredSize(d);
			}
			crumbs2Container.setBorder(new CompoundBorder(new EmptyBorder(insets,insets,insets,insets), new LineBorder(Color.gray) ));
			crumbs2Container.setBackground(Color.white);
			

			getContentPane().setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			crumbs2Container.add(crumbs2, c);
			getContentPane().add(crumbs1, c);
			c.gridy++;
			getContentPane().add(crumbs2Container, c);
		}
	}
	
	public SwingComponentsDemo() {
		addPane(new ThrobberDemo(), 0, 1, 1, 1, GridBagConstraints.NONE);
		addPane(new BreadCrumbDemo(), 0, 0, 2, 1, GridBagConstraints.NONE);
		addPane(new DecoratedTreeDemo(), 1, 1, 1, 1, GridBagConstraints.NONE);
	}
}