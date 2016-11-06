/*
 * @(#)GraphicsWriterDebugger.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.graphics;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/** A simple debugger tool for graphics.
 */
public class GraphicsWriterDebugger extends JFrame {
	private static final long serialVersionUID = 1L;

	/** Install a debugging hotkey.  When that key is pressed: a new
	 * <code>GraphicsWriterDebugger</code> frame will appear that paints
	 * the argument.
	 * 
	 * @param component a component to debug.  The <code>paint()</code> method
	 * of this argument will be used.
	 * @param keyCode a <code>KeyEven.VK_X</code> key code.  When this key is
	 * pressed, the <code>GraphicsWriterDebugger</code> frame will be created.
	 */
	public static void installDebugHotkey(final Component component,final int keyCode) {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			public void eventDispatched(AWTEvent event) {
				KeyEvent e = (KeyEvent)event;
				if(e.getKeyCode()==keyCode && e.getID()==KeyEvent.KEY_PRESSED) {
					GraphicsWriter writer = new GraphicsWriter(true);
					component.paint(writer);
					GraphicsWriterDebugger d = new GraphicsWriterDebugger(writer);
					d.pack();
					d.setVisible(true);
				}
			}
			
		}, AWTEvent.KEY_EVENT_MASK);
	}
	
	class PreviewPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		AffineTransform transform = new AffineTransform();
		Rectangle2D clippedRect;
		
		MouseInputAdapter mouseListener = new MouseInputAdapter() {
			
			Point2D.Double clickLoc;
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getClickCount()>1) {
					clippedRect = null;
					repaint();
				}
				clickLoc = getAbstractPoint(e.getPoint());
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				clickLoc = null;
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				mouseMoved(e);
				if(clickLoc!=null) {
					Point2D newPoint = getAbstractPoint(e.getPoint());
					clippedRect = new Rectangle2D.Double(clickLoc.getX(),clickLoc.getY(),0,0);
					clippedRect.add(newPoint);
					repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				updateMouseLabel(e.getPoint());
			}
			
			DecimalFormat format = new DecimalFormat("#.##");
			private void updateMouseLabel(Point p) {
				Point2D.Double p2 = getAbstractPoint(p);

				pointLabel.setText("("+format.format(p2.getX())+", "+format.format(p2.getY())+")");
			}
			
			private Point2D.Double getAbstractPoint(Point p) {
				try {
					AffineTransform inverse = transform.createInverse();
					Point2D.Double p2 = new Point2D.Double();
					inverse.transform(p, p2);
					return p2;
				} catch(Throwable t) {
					t.printStackTrace();
				}
				return null;
			}
			
		};
		
		public PreviewPanel() {
			setPreferredSize(new Dimension(500,500));
			addMouseMotionListener(mouseListener);
			addMouseListener(mouseListener);
		}
		
		@Override
		public void paint(Graphics g0) {
			super.paint(g0);
			Graphics2D g = (Graphics2D)g0;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			Rectangle2D bounds = writer.getBounds();
			if(bounds==null)
				return;
			
			double wRatio = getWidth()/bounds.getWidth();
			double hRatio = getHeight()/bounds.getHeight();
			double min = Math.min(wRatio,hRatio);
			transform.setToScale(min, min);
			transform.translate(-bounds.getX(),-bounds.getY());
			g.transform(transform);

			if(clippedRect!=null)
				g.clip(clippedRect);
			
			TreePath[] paths = tree.getSelectionPaths();
			if(paths!=null) {
				for(int a = 0; a<paths.length; a++) {
					GraphicInstruction instr = (GraphicInstruction)paths[a].getLastPathComponent();
					instr.paint(g);
				}
			}
		}
	}
	
	JTree tree;
	GraphicsWriter writer;
	PreviewPanel preview = new PreviewPanel();
	JTextArea text = new JTextArea(GraphicsWriter.SOURCE_LINE_LIMIT, GraphicsWriter.SOURCE_LINE_LIMIT);
	JPanel details = new JPanel(new GridBagLayout());
	JLabel pointLabel = new JLabel(" ");
	
	public GraphicsWriterDebugger(GraphicsWriter root) {
		this.writer = root;
		tree = new JTree(root);
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getNewLeadSelectionPath();
				String s = " ";
				if(path!=null) {
					GraphicInstruction instr = (GraphicInstruction)path.getLastPathComponent();
					s = instr.getSource();
					if(s==null || s.length()==0) s = " ";
				}
				text.setText(s);
				preview.repaint();
			}
		});
		tree.setCellRenderer(new TreeCellRenderer() {
			JLabel label = new JLabel();
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean leaf, int row, boolean hasFocus)
			{
				label.setText("");
				if(value instanceof GraphicsWriter) {
					label.setText("Group");
				} else if(value instanceof TextBoxInstruction) {
					label.setText("Text");
				} else {
					StringBuffer sb = new StringBuffer();
					if(value instanceof FillInstruction) {
						sb.append("Fill");
					}
					if(value instanceof DrawInstruction) {
						if(sb.length()>0)
							sb.append(", ");
						sb.append("Draw");
					}
					if(value instanceof ImageInstruction) {
						if(sb.length()>0)
							sb.append(", ");
						sb.append("Image");
					}
					label.setText(sb.toString());
				}
				
				if(selected) {
					label.setForeground(UIManager.getColor("Tree.selectionForeground"));
					label.setBackground(UIManager.getColor("Tree.selectionBackground"));
				} else {
					label.setForeground(SystemColor.textText);
					label.setBackground(SystemColor.text);
				}
				
				label.setOpaque(true);
				
				if(label.getText().equals("")) {
					System.err.println("Unrecognized instruction ("+value.getClass().getName()+"): "+value);
				}
				return label;
			}
			
		});
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(150,150));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollPane,details);
		getContentPane().add(splitPane);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.BOTH;
		details.add(pointLabel,c);
		c.gridy++; c.weighty = 1;
		details.add(preview,c);
		c.gridy++; c.weighty = 0;
		details.add(text,c);
		
		text.setEditable(false);
		
		pack();
	}
}
