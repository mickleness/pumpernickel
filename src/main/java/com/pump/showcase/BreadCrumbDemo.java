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

import java.awt.AlphaComposite;
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
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.pump.awt.SplayedLayout;
import com.pump.icon.EmptyIcon;
import com.pump.inspector.Inspector;
import com.pump.plaf.BreadCrumbUI;
import com.pump.swing.JBreadCrumb;
import com.pump.swing.JBreadCrumb.BreadCrumbFormatter;

/**
 * This demos the JBreadCrumb.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/BreadCrumbDemo.png"
 * alt="A screenshot of the BreadCrumbDemo.">
 */
public class BreadCrumbDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	static class DoubleSlashIcon implements Icon {

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.gray);
			int shear = 3;
			int distance = 5;
			g2.setStroke(new BasicStroke(1));
			int x1 = x + getIconWidth() / 2 - distance / 2;
			int x2 = x + getIconWidth() / 2 - distance / 2 + distance;
			int y1 = y + 3;
			int y2 = y + getIconHeight() - 3;
			g2.drawLine(x1 + shear, y1, x1 - shear, y2);
			g2.drawLine(x2 + shear, y1, x2 - shear, y2);
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return 30;
		}

		@Override
		public int getIconHeight() {
			return 20;
		}

	}

	static class RoundedShadowIcon implements Icon {

		BufferedImage scratch;

		public RoundedShadowIcon() {
			int separatorWidth = 5;
			int leftPadding = 6;
			int rightPadding = 10;

			scratch = new BufferedImage(separatorWidth + leftPadding
					+ rightPadding, 26, BufferedImage.TYPE_INT_ARGB);
			Color darkShadow = new Color(0, 0, 0, 50);
			Color lightShadow = new Color(0, 0, 0, 0);
			Graphics2D g2 = scratch.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.Clear);
			g2.fillRect(0, 0, scratch.getWidth(), scratch.getHeight());
			g2.setComposite(AlphaComposite.SrcOver);
			g2.setPaint(new GradientPaint(0, 0, darkShadow,
					scratch.getWidth() - 3, 0, lightShadow));
			g2.fillRect(0, 0, scratch.getWidth(), scratch.getHeight());

			g2.setComposite(AlphaComposite.Clear);
			GeneralPath chunk = new GeneralPath();
			chunk.moveTo(leftPadding, 0);
			int k = 5;
			chunk.curveTo(leftPadding + k, k, leftPadding + k, getIconHeight()
					- k, leftPadding, getIconHeight());
			chunk.lineTo(0, getIconHeight());
			chunk.lineTo(0, 0);
			g2.fill(chunk);
			g2.dispose();
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.drawImage(scratch, x, y, null);
		}

		@Override
		public int getIconWidth() {
			return scratch.getWidth();
		}

		@Override
		public int getIconHeight() {
			return scratch.getHeight();
		}

	};

	public static TreeNode createTree() {
		DefaultMutableTreeNode fictionBooks = new DefaultMutableTreeNode(
				"Fiction Books");
		DefaultMutableTreeNode douglasAdams = add(fictionBooks, "Douglas Adams");

		DefaultMutableTreeNode hitchhikersSeries = add(douglasAdams,
				"Hitchhiker's Series");
		add(hitchhikersSeries, "The Hitchhiker's Guide to the Galaxy");
		add(hitchhikersSeries, "The Restaurant at the End of the Universe");
		add(hitchhikersSeries, "Life, the Universe and Everything");
		add(hitchhikersSeries, "So Long, and Thanks for All the Fish");
		add(hitchhikersSeries, "Mostly Harmless");

		DefaultMutableTreeNode dirkGentlySeries = add(douglasAdams,
				"Dirk Gently Series");
		add(dirkGentlySeries, "Dirk Gently's Holistic Detective Agency");
		add(dirkGentlySeries, "The Long Dark Tea-Time of the Soul");
		add(dirkGentlySeries, "The Salmon of Doubt");

		return fictionBooks;
	}

	private static DefaultMutableTreeNode add(DefaultMutableTreeNode parent,
			String childName) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(childName);
		parent.add(child);
		return child;
	}

	DefaultTreeModel treeModel;
	JTree tree;
	JScrollPane treeScrollPane;
	JBreadCrumb<String> breadCrumb = new JBreadCrumb<>();
	JRadioButton fileIconOff = new JRadioButton("Off", false);
	JRadioButton fileIconOn = new JRadioButton("On", true);
	JComboBox<String> separatorComboBox = new JComboBox<>();

	public BreadCrumbDemo() {
		super(true, false, false);
		Inspector layout = new Inspector(configurationPanel);

		ButtonGroup g1 = new ButtonGroup();
		g1.add(fileIconOff);
		g1.add(fileIconOn);

		separatorComboBox.addItem("Triangle (Default)");
		separatorComboBox.addItem("Rounded Shadow");
		separatorComboBox.addItem("Double Slash");
		separatorComboBox.addItem("None");

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshBreadCrumb();
			}
		};
		fileIconOff.addActionListener(actionListener);
		fileIconOn.addActionListener(actionListener);
		separatorComboBox.addActionListener(actionListener);

		treeModel = new DefaultTreeModel(createTree());
		tree = new JTree(treeModel);
		treeScrollPane = new JScrollPane(tree);
		for (int a = 0; a < tree.getRowCount(); a++) {
			tree.expandRow(a);
		}

		// layout.addRow(new JLabel("Style:"), defaultStyle, customStyle);
		layout.addRow(new JLabel("File Icons:"), fileIconOff, fileIconOn);
		layout.addRow(new JLabel("Separator:"), separatorComboBox);
		layout.addRow(treeScrollPane, true);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		examplePanel.add(breadCrumb, c);

		breadCrumb.setBorder(new CompoundBorder(
				new LineBorder(Color.lightGray), new EmptyBorder(0, 4, 0, 4)));

		// don't let the scrollpane hog all the space, it's not our primary
		// focus
		treeScrollPane.setPreferredSize(new Dimension(400, 200));

		tree.getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener() {

					@Override
					public void valueChanged(TreeSelectionEvent e) {
						refreshBreadCrumb();
					}

				});

		refreshBreadCrumb();
	}

	private void refreshBreadCrumb() {
		Icon separatorIcon;
		if (separatorComboBox.getSelectedIndex() == 3) {
			separatorIcon = new EmptyIcon(8, 8);
		} else if (separatorComboBox.getSelectedIndex() == 1) {
			separatorIcon = new RoundedShadowIcon();
		} else if (separatorComboBox.getSelectedIndex() == 2) {
			separatorIcon = new DoubleSlashIcon();
		} else {
			separatorIcon = BreadCrumbUI.DEFAULT_SEPARATOR_ICON;
		}
		breadCrumb.putClientProperty(BreadCrumbUI.PROPERTY_SEPARATOR_ICON,
				separatorIcon);
		breadCrumb.setFormatter(new BreadCrumbFormatter<String>() {

			@Override
			public void format(JBreadCrumb<String> container, JLabel label,
					String pathNode, int index) {
				if (fileIconOn.isSelected()) {
					if (index == 3) {
						label.setIcon(UIManager.getIcon("Tree.leafIcon"));
					} else {
						label.setIcon(UIManager.getIcon("Tree.openIcon"));
					}
				} else {
					label.setIcon(null);
				}
				label.setText(pathNode);
			}

		});

		TreePath path = tree.getSelectionPath();
		if (path == null) {
			breadCrumb.setPath(new String[] {});
		} else {
			List<String> l = new ArrayList<>();
			for (Object k : path.getPath()) {
				l.add((String) ((DefaultMutableTreeNode) k).getUserObject());
			}
			breadCrumb.setPath(l.toArray(new String[l.size()]));
		}

		// changing the icon can change breadCrumb's preferred size:
		breadCrumb.getParent().revalidate();
		breadCrumb.setOpaque(true);
		breadCrumb.setBackground(Color.white);

		/*
		 * Give it a small preferred size so the GridBagLayout chooses to expand
		 * it on its own terms.
		 * 
		 * If we don't do this: sometimes the width will exceed the available
		 * space. This is OK for the JBreadCrumb, but it affects other areas of
		 * the UI. In this demo's case: when this BreadCrumbDemo panel has a
		 * preferred size that exceeds its available size, then its container
		 * suffers and the JSeparator above it disappears. (Which is weird,
		 * because the deficit is horizontal, so why are we giving up vertical
		 * space?).
		 * 
		 * Just try commenting it out and see what happens, basically...
		 */
		int k = Math.max(22, separatorIcon.getIconHeight());
		breadCrumb.setPreferredSize(new Dimension(k, k));
	}

	@Override
	public String getTitle() {
		return "JBreadCrumb Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new Swing component for rendering/selecting navigation nodes.";
	}

	@Override
	public URL getHelpURL() {
		return BreadCrumbDemo.class.getResource("breadCrumbDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "breadcrumb", "navigation", "ux", "Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JBreadCrumb.class, SplayedLayout.class,
				BreadCrumbUI.class, JLabel.class };
	}
}