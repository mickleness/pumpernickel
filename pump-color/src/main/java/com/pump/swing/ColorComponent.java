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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** This is an abstract <code>JComponent</code> that stores
 * and somehow presents a color.  When <code>setColor()</code>
 * is called this component's <code>ChangeListeners</code> are
 * notified.
 * <P>The initial color is black.
 */
public abstract class ColorComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	protected Color color = Color.black;
	List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	/** Add a <code>ChangeListener</code> to be notified when
	 * <code>setColor()</code> is called.
	 */
	public void addChangeListener(ChangeListener l) {
		if(changeListeners.contains(l))
			return;
		changeListeners.add(l);
	}
	
	/** Remove a <code>ChangeListener</code>.
	 * 
	 */
	public void removeChangeListener(ChangeListener l) {
		changeListeners.remove(l);
	}
	
	/** Returns the current color.
	 * 
	 * @return the current color.
	 */
	public Color getColor() {
		return color;
	}
	
	protected void fireChangeListeners() {
		for(int a = 0; a<changeListeners.size(); a++) {
			ChangeListener l = changeListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(this));
			} catch(RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Defines the current color.
	 * 
	 * @param c the new color.
	 * @return <code>true</code> if a change occurred.  Or <code>false</code>
	 * if the argument was already the current color and no listeners
	 * were notified.
	 */
	public boolean setColor(Color c) {
		if(c==null)
			throw new NullPointerException();
		if(color!=null && c!=null && c.equals(color))
			return false;
		Color oldColor = null;
		color = c;
		fireChangeListeners();
		firePropertyChange("color", oldColor, color);
		return true;
	}
	
	public void bind(final ColorComponent slave) {
		slave.setColor(getColor());
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ColorComponent c = (ColorComponent)e.getSource();
				if(c==ColorComponent.this) {
					slave.setColor( getColor() );
				} else {
					setColor( slave.getColor() );
				}
			}
		};
		
		addChangeListener(changeListener);
		slave.addChangeListener(changeListener);
	}
}