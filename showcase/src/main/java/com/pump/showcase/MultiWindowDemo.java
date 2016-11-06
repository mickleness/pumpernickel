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

/**
 * This showcases a few different JComponents in a JDesktopPane.
 */
public abstract class MultiWindowDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** When this client property on the root pane is attached to a String, that String is
	 * displayed when the mouse hovers over that root pane.
	 */
	public static final String KEY_DESCRIPTION = SwingComponentsDemo.class.getName()+"#description";


	protected JDesktopPane desktop = new JDesktopPane();
	protected JPanel bottomPanel = new JPanel(new GridBagLayout());
	protected MagnificationPanel zoomPanel = new MagnificationPanel(desktop, 20, 20, 8);
	protected JTextArea descriptionTextArea = new JTextArea();
	
	public MultiWindowDemo() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(desktop, c);
		c.gridy++; c.weighty = 0;
		add(bottomPanel, c);
		
		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4,4,4,4);
		c.gridheight = GridBagConstraints.REMAINDER;
		bottomPanel.add(descriptionTextArea, c);
		c.gridx++; c.weightx = 0; c.gridheight = 1;
		bottomPanel.add(zoomPanel, c);
		
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setOpaque(false);
		
		MouseTracker.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if(desktop.isShowing()) {
					Point p = new Point( MouseTracker.getX(), MouseTracker.getY());
					SwingUtilities.convertPointFromScreen(p, desktop);
					Component c0 = SwingUtilities.getDeepestComponentAt(desktop, p.x, p.y);
					String description = "";
					if(c0 instanceof JComponent) {
						JComponent c = (JComponent)c0;
						description = (String)c.getClientProperty(KEY_DESCRIPTION);
						while(description==null && c!=null) {
							description = (String)c.getClientProperty(KEY_DESCRIPTION);
							if(c.getParent() instanceof JComponent) {
								c = (JComponent)c.getParent();
							} else {
								c = null;
							}
						}
					}
					if(description==null)
						description = "";
					descriptionTextArea.setText(description);
				}
			}
			
		});
	}

	protected void addPane(JInternalFrame pane) {
		pane.setVisible(true);
		pane.pack();
		desktop.add(pane);
		desktop.setSelectedFrame(pane);
		desktop.repaint();
	}
}
