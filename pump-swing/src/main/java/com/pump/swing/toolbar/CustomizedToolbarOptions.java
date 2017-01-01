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
package com.pump.swing.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.pump.swing.MockComponent;


/** This is the heart of the dialog that appears when you select
 * "Customize Toolbar..."
 * 
 */
class CustomizedToolbarOptions extends JPanel {
	private static final long serialVersionUID = 1L;

	/** Analogous to the toolbar's list of components, except
	 * a MockComponent is used for each object.
	 */
	MockComponent[] componentList;
	
	JPanel componentPanel = new JPanel(new GridBagLayout());
	
	CustomizedToolbar toolbar;
	
	JButton done = new JButton("Done");
	
	private static DragSource dragSource = DragSource.getDefaultDragSource();
	private DragSourceListener dragSourceListener = new DragSourceAdapter() {
		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			toolbar.endDrag(dsde);
		}
	};
	DragGestureListener dragGestureListener = new DragGestureListener() {

		public void dragGestureRecognized(DragGestureEvent dge) {
			
			Point p = dge.getDragOrigin();
			MockComponent mc = (MockComponent)dge.getComponent();
			Transferable transferable = new MockComponentTransferable(mc);
			BufferedImage bi = mc.getBufferedImage();
			if(mc instanceof MockDefaultToolbar) {
				toolbar.draggingComponent = "";
			} else if(mc.getName().equals("-")) {
				toolbar.draggingComponent = toolbar.getNewSeparatorName();
			} else if(mc.getName().equals(" ")) {
				toolbar.draggingComponent = toolbar.getNewSpaceName();
			} else if(mc.getName().equals("\t")) {
				toolbar.draggingComponent = toolbar.getNewFlexibleSpaceName();
			} else {
				toolbar.draggingComponent = mc.getName();
			}
			toolbar.draggingDefaults = (mc instanceof MockDefaultToolbar);
			toolbar.draggingFromToolbar = false;
			dge.startDrag(DragSource.DefaultMoveDrop, 
					bi, 
					new Point(-p.x, -p.y), 
					transferable, 
					dragSourceListener);
		}
		
	};
	
	public CustomizedToolbarOptions(CustomizedToolbar t,int maxWidth) {
		toolbar = t;
		JComponent[] options = t.getPossibleComponents();
		componentList = new MockComponent[options.length+3];
		for(int a = 0; a<options.length; a++) {
			componentList[a] = (new MockComponent(options[a]));
		}
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setUI(new MacToolbarSeparatorUI());
		separator.setName("-");
		Dimension separatorSize = separator.getPreferredSize();
		separatorSize.height = toolbar.minimumHeight;
		separator.setSize(separatorSize);
		separator.setPreferredSize(separatorSize);
		componentList[componentList.length-3] = new MockComponent(separator);
		
		SpaceComponent space = new SpaceComponent(toolbar,false);
		space.setName(" ");
		componentList[componentList.length-2] = new MockComponent(space);

		SpaceComponent flexSpace = new SpaceComponent(toolbar,true);
		flexSpace.setName("\t");
		componentList[componentList.length-1] = new MockComponent(flexSpace);
		
		for(int a = 0; a<componentList.length; a++) {	
			dragSource.createDefaultDragGestureRecognizer(componentList[a], DnDConstants.ACTION_MOVE, dragGestureListener);
		}
		
		GridBagConstraints c = new GridBagConstraints();
		int a = 0;
		while(a<componentList.length) {
			JPanel row = new JPanel(new GridBagLayout());
			c.gridy = componentPanel.getComponentCount();
			c.gridx = 0; c.weightx = 1; c.weighty = 1; 
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0,0,0,0);
			componentPanel.add(row,c);
			int width = 0;
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			c.insets = new Insets(5, 5, 5, 5);
			layoutRow : while(width<maxWidth && a<componentList.length) {
				Dimension d = componentList[a].getPreferredSize();
				if(d.width+6>maxWidth) {
					break layoutRow;
				}
				row.add(componentList[a],c);
				c.gridx++;
				width += d.width + c.insets.left+c.insets.right;
				a++;
			}
		}
		
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; 
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new Shadow(),c);
		c.gridy++;
		c.insets = new Insets(10, 20, 15, 20);
		add(label1, c);
		c.gridy++; c.insets = new Insets(0,20,20,20);
		add(componentPanel,c);
		c.gridy++; c.insets = new Insets(0, 20, 5, 20);
		add(label2,c);
		c.gridy++; c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(10,20,10,20);
		MockDefaultToolbar mockDefaults = new MockDefaultToolbar(toolbar);
		add(mockDefaults,c);
		dragSource.createDefaultDragGestureRecognizer(mockDefaults, DnDConstants.ACTION_MOVE, dragGestureListener);
		
		c.fill = GridBagConstraints.NONE;
		c.gridy++; c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(20,20,20,20);
		add(done,c);
		
		label1.setFont(label1.getFont().deriveFont(Font.BOLD));
		label2.setFont(label2.getFont().deriveFont(Font.BOLD));
		
		setOpaque(true);
		setBackground(this,Color.white);
	}
	
	private static void setBackground(Component c,Color color) {
		c.setBackground(color);
		if(c instanceof Container) {
			Container c2 = (Container)c;
			for(int a = 0; a<c2.getComponentCount(); a++) {
				setBackground(c2.getComponent(a),color);
			}
		}
	}
	
	JLabel label1 = new JLabel("Drag your favorite items into the toolbar\u2026"); //\u2026 is unicode for an ellipsis
	JLabel label2 = new JLabel("\u2026 or drag the default set into the toolbar.");
	
	class Shadow extends JComponent {
		private static final long serialVersionUID = 1L;
		
		public Shadow() {
			setPreferredSize(new Dimension(5,10));
			setMinimumSize(new Dimension(5,10));
			setOpaque(false);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			boolean dark = CustomizedToolbar.isDarkBackground( SwingUtilities.getWindowAncestor(this) );
			for(int a = 0; a<getHeight(); a++) {
				float alpha;
				if(dark) {
					alpha = ((float)(getHeight()-a-1))/((float)getHeight())*.35f;
				} else {
					alpha = ((float)(getHeight()-a-1))/((float)getHeight())*.15f;
				}
				Color color = new Color(0,0,0,alpha);
				g.setColor(color);
				g.drawLine(0,a,getWidth(),a);
			}
		}
	}
}