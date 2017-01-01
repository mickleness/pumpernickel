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
package com.pump.diagram;

import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JComponent;

import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;

public class Box implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static long ID_CTR = 0;

	public static Key<Rectangle> KEY_BOUNDS = new Key<>("bounds", Rectangle.class);
	public static Key<String> KEY_NAME = new Key<>("name", String.class);
	public static Key<JComponent> KEY_COMPONENT = new Key<>("component", JComponent.class);

	ObservableProperties properties = new ObservableProperties();

	transient long id = ID_CTR++;

	public Box() {
		this(new Rectangle(10, 10, 100, 100));
		set(KEY_NAME, Long.toString(id));
	}
	
	public JComponent getComponent() {
		return get(KEY_COMPONENT);
	}
	
	public void setComponent(JComponent jc) {
		set(KEY_COMPONENT, jc);
	}

	public Box(Rectangle newBounds) {
		setBounds(newBounds);
	}
	
	public String getName() {
		return properties.get(KEY_NAME);
	}
	
	public void setName(String newName) {
		properties.set(KEY_NAME, newName);
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.defaultWriteObject();

	}
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		id = ID_CTR++;
	}

	public long getID() {
		return id;
	}

	public boolean setBounds(Rectangle newBounds) {
		boolean returnValue = false;
		Rectangle oldValue =  properties.set(KEY_BOUNDS, newBounds);
		if(oldValue==null && newBounds==null) {
			returnValue = false;
		} else if(oldValue==null) {
			returnValue = true;
		} else {
			if(oldValue==newBounds) {
				//we're going to assume (guess) that the bounds WERE changed, even though
				//we weren't handed a deep copy of the Rectangle in question.
				returnValue = true;
				for(PropertyChangeListener pcl : properties.getPropertyListeners(ObservableProperties.DEFAULT)) {
					pcl.propertyChange(new PropertyChangeEvent(this, KEY_BOUNDS.getKeyName(), newBounds, newBounds));
				}
			} else {
				returnValue = !oldValue.equals(newBounds);
			}
		}
		return returnValue;
	}

	public Rectangle getBounds() {
		return properties.get(KEY_BOUNDS);
	}
	
	public <T> void set(Key<T> key, T newValue) {
		properties.set(key, newValue);
	}
	
	public <T> T get(Key<T> key) {
		return properties.get(key);
	}

	public void addPropertyChangeListener(PropertyChangeListener repaintListener)
	{
		properties.addListener(repaintListener);
	}

	public void removePropertyChangeListener(PropertyChangeListener repaintListener)
	{
		properties.removeListener(repaintListener);
	}
	
	@Override
	public String toString() {
		String name = get(KEY_NAME);
		return name;
	}

	public Point getCenter()
	{
		Rectangle bounds = properties.get(KEY_BOUNDS);
		return new Point(bounds.x+bounds.width/2, bounds.y+bounds.height/2);
	}
}