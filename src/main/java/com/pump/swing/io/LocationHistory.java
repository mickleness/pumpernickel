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
package com.pump.swing.io;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.io.location.IOLocation;

/**
 * This manages the history of displayed directories.
 */
public class LocationHistory {
    List<IOLocation> list = new ArrayList<IOLocation>();
    int index = -1;

    List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

    public LocationHistory() {
    }

    /** Returns all the IOLocations in this history. */
    public IOLocation[] getList() {
	return list.toArray(new IOLocation[list.size()]);
    }

    public void addChangeListener(ChangeListener l) {
	if (changeListeners.contains(l))
	    return;
	changeListeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
	changeListeners.remove(l);
    }

    @Override
    public String toString() {
	return "LocationHistory[ index = " + index + ", list = " + list + " ]";
    }

    protected void fireChangeListeners() {
	for (int a = 0; a < changeListeners.size(); a++) {
	    ChangeListener l = changeListeners.get(a);
	    try {
		l.stateChanged(new ChangeEvent(this));
	    } catch (RuntimeException e) {
		e.printStackTrace();
	    }
	}
    }

    public IOLocation getLocation() {
	synchronized (this) {
	    if (index == -1)
		return null;
	    return list.get(index);
	}
    }

    public IOLocation peekNext() {
	synchronized (this) {
	    if (index + 1 < list.size() - 1) {
		return list.get(index + 1);
	    }
	    return null;
	}
    }

    public void replaceAll(IOLocation loc) {
	if (loc == null)
	    throw new NullPointerException();

	synchronized (this) {
	    list.clear();
	    list.add(loc);
	    index = 0;
	}
	fireChangeListeners();
    }

    public void replace(IOLocation loc) {
	if (loc == null)
	    throw new NullPointerException();

	synchronized (this) {
	    while (index - 1 < list.size() - 1) {
		list.remove(list.size() - 1);
	    }
	    list.add(loc);
	    index = list.size() - 1;
	}
	fireChangeListeners();
    }

    public void append(IOLocation loc) {
	if (loc == null)
	    throw new NullPointerException();

	if (loc.isNavigable() == false)
	    throw new IllegalArgumentException(loc.toString());

	synchronized (this) {
	    if (getLocation() == loc) {
		return;
	    } else if (peekNext() == loc) {
		index++;
		return;
	    }
	    while (index < list.size() - 1) {
		list.remove(list.size() - 1);
	    }
	    list.add(loc);
	    index = list.size() - 1;
	}
	fireChangeListeners();
    }

    public IOLocation back() {
	boolean changed = false;
	synchronized (this) {
	    if (index > 0) {
		index--;
		changed = true;
	    }
	}
	if (changed)
	    fireChangeListeners();
	return list.get(index);
    }

    public synchronized IOLocation next() {
	boolean changed = false;
	synchronized (this) {
	    if (index < list.size() - 1) {
		index++;
		changed = true;
	    }
	}
	if (changed)
	    fireChangeListeners();
	return list.get(index);
    }

    public boolean hasNext() {
	synchronized (this) {
	    return index < list.size() - 1;
	}
    }

    public boolean hasBack() {
	synchronized (this) {
	    return index > 0;
	}
    }
}