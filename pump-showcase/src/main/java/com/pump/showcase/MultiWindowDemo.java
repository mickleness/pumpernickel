package com.pump.showcase;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.awt.MouseTracker;
import com.pump.swing.MagnificationPanel;

/**
 * This showcases a few different JComponents in a JDesktopPane.
 */
public abstract class MultiWindowDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** When this client property on the root pane is attached to a String, that String is
	 * displayed when the mouse hovers over that root pane.
	 */
	public static final String KEY_DESCRIPTION = SwingComponentsDemo.class.getName()+"#description";
	
	/** Maps to a GridBagConstraints used for tile positioning.
	 */
	private static final String KEY_CONSTRAINTS = SwingComponentsDemo.class.getName()+"#constraints";


	protected JDesktopPane desktop = new JDesktopPane();
	protected JPanel bottomPanel = new JPanel(new GridBagLayout());
	protected MagnificationPanel zoomPanel = new MagnificationPanel(desktop, 20, 20, 8);
	protected JTextArea descriptionTextArea = new JTextArea();
	private boolean desktopShown = false;
	
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
		
		desktop.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				layoutWindows();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				desktopShown = true;
			}
			
		});
	}

	/**
	 * 
	 * @param pane
	 * @param gridx
	 * @param gridy
	 * @param gridwidth
	 * @param gridheight
	 * @param fill GridBagConstraints.HORIZONTAL, VERTICAL, BOTH or NONE. This is only applied if
	 * the JDesktopPane is resizable.
	 */
	protected void addPane(JInternalFrame pane,int gridx,int gridy,int gridwidth,int gridheight,int fill) {
		pane.setVisible(true);
		pane.pack();
		setConstraint(pane, gridx, gridy, gridwidth, gridheight, fill);
		desktop.add(pane);
		desktop.setSelectedFrame(pane);
		desktop.repaint();
		
		layoutWindows();
	}
	
	protected void layoutWindows() {
		if(desktopShown)
			return;
		
		Map<JInternalFrame, GridBagConstraints> map = new HashMap<>();
		int maxX = 0;
		int maxY = 0;
		for(JInternalFrame frame : getFrames()) {
			GridBagConstraints c = getConstraints(frame);
			map.put(frame, c);
			maxX = Math.max(maxX, c.gridx + c.gridwidth);
			maxY = Math.max(maxY, c.gridy + c.gridheight);
		}
		
		int width = desktop.getWidth();
		int height = desktop.getHeight();
		Insets insets = new Insets(5,5,5,5);
		for(Entry<JInternalFrame, GridBagConstraints> entry : map.entrySet()) {
			int x1 = entry.getValue().gridx;
			int x2 = x1 + entry.getValue().gridwidth;
			int y1 = entry.getValue().gridy;
			int y2 = y1 + entry.getValue().gridheight;
			Rectangle r = new Rectangle( x1 * width / maxX, y1 * height / maxY, 
					(x2 - x1) * width / maxX, (y2 - y1) * height / maxY );
			//apply insets
			r.setBounds(r.x + insets.left, r.y + insets.top, 
					r.width - insets.left - insets.right, 
					r.height - insets.top - insets.bottom);
			
			//calculate window bounds
			Rectangle bounds = null;
			Dimension d = entry.getKey().getPreferredSize();
			if(entry.getKey().isResizable()) {
				if(entry.getValue().fill==GridBagConstraints.HORIZONTAL) {
					bounds = new Rectangle(r.x, r.y - r.height/2 + d.height/2, r.width, d.height);
				} else if(entry.getValue().fill==GridBagConstraints.VERTICAL) {
					bounds = new Rectangle(r.x - r.width/2 + d.width/2, r.y, d.width, r.height);
				} else if(entry.getValue().fill==GridBagConstraints.BOTH) {
					bounds = r;
				}
			}
			if(bounds==null) {
				bounds = new Rectangle(r.x + r.width/2 - d.width/2, 
						r.y + r.height/2 - d.height/2, d.width, d.height);
			}
			
			entry.getKey().setBounds(bounds);
		}
	}
	
	private GridBagConstraints getConstraints(JComponent jc) {
		return (GridBagConstraints) jc.getClientProperty(KEY_CONSTRAINTS);
	}
	
	private void setConstraint(JComponent jc,int gridx,int gridy,int width,int height,int fill) {
		if(!(fill==GridBagConstraints.BOTH || fill==GridBagConstraints.HORIZONTAL ||
				fill==GridBagConstraints.VERTICAL || fill==GridBagConstraints.NONE))
			throw new IllegalArgumentException("fill should be BOTH, HORIZONTAL, VERTICAL, or NONE");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = width;
		c.gridheight = height;
		c.fill = fill;
		jc.putClientProperty(KEY_CONSTRAINTS, c);
	}
	
	protected List<JInternalFrame> getFrames() {
		List<JInternalFrame> frames = new ArrayList<>();
		for(int a = 0; a<desktop.getComponentCount(); a++) {
			if(desktop.getComponent(a) instanceof JInternalFrame)
				frames.add( (JInternalFrame)desktop.getComponent(a) );
		}
		return frames;
	}
}
