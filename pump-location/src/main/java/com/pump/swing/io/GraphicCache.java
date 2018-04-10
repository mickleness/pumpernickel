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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import com.pump.io.location.IOLocation;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;

/**
 * This caches icons and thumbnails of <code>IOLocations</code>. Calling
 * <code>clear()</code> empties all cached graphics.
 */
public class GraphicCache {
    public static final String ICON_PROPERTY = "icon";
    public static final String THUMBNAIL_PROPERTY = "thumbnail";

    private static BufferedImage scratchImage = new BufferedImage(16, 16,
	    BufferedImage.TYPE_INT_ARGB);

    List<ActionListener> exceptionHandlers = new ArrayList<ActionListener>();
    final Map<IOLocation, Icon> icons = new HashMap<>();
    final Map<IOLocation, BufferedImage> thumbnails = new HashMap<>();

    final List<IOLocation> requestIconList = new ArrayList<IOLocation>();
    final List<IOLocation> requestThumbnailList = new ArrayList<IOLocation>();
    final List<PropertyChangeListener> propertyListeners = new ArrayList<PropertyChangeListener>();
    Dimension thumbnailSize = new Dimension(64, 64);

    /**
     * An array of threads used to retrieve icons simultaneously.
     * <p>
     * This should probably not be greater than 4 if the AWT package is being
     * used to load images. Only 4 "Image Fetcher" threads are opened
     * simultaneously. The bric Scaling class uses the AWT classes.
     * 
     */
    final Thread[] iconThreads = new Thread[3];
    Cancellable cancellable = new BasicCancellable();

    static HashSet<String> noThumbnails = new HashSet<String>();
    static HashSet<String> noIcons = new HashSet<String>();

    final Runnable iconRunnable = new Runnable() {
	public void run() {
	    while (true) {
		IOLocation loc = null;
		synchronized (requestIconList) {
		    if (requestIconList.size() == 0)
			return;

		    loc = requestIconList.remove(requestIconList.size() - 1);
		}
		Cancellable myCancellable = cancellable;
		Icon icon = null;
		try {
		    icon = loc.isDirectory() ? IOLocation.FOLDER_ICON
			    : IOLocation.FILE_ICON; // loc.getIcon(myCancellable);
		} catch (Throwable e) {
		    handleUncaughtException(e);
		}
		if (icon != null) {
		    /**
		     * Unfortunately there's more. Macs would still often jam up
		     * in the event dispatch thread with this stack trace:
		     * AWT-EventQueue-0 (id = 13)
		     * apple.awt.CImage.getNativeFileSystemIconFor(Native
		     * Method) apple.awt.CImage.access$300(CImage.java:12)
		     * apple.
		     * awt.CImage$Creator.createImageOfFile(CImage.java:90)
		     * com.apple
		     * .laf.AquaIcon$FileIcon.createImage(AquaIcon.java:230)
		     * com.
		     * apple.laf.AquaIcon$CachingScalingIcon.getOptimizedImage
		     * (AquaIcon.java:133)
		     * com.apple.laf.AquaIcon$CachingScalingIcon
		     * .getImage(AquaIcon.java:126)
		     * com.apple.laf.AquaIcon$CachingScalingIcon
		     * .paintIcon(AquaIcon.java:168)
		     * 
		     * I'm interpreting this to mean: the Icon object exists,
		     * but the underlying mechanism still isn't ready to paint
		     * it. Which completely defeats the purpose of creating this
		     * object in a separate thread. So let's try to force the
		     * AquaIcon to prep itself:
		     */
		    Graphics2D g = scratchImage.createGraphics();
		    icon.paintIcon(null, g, 0, 0);
		    g.dispose();

		    icons.put(loc, icon);
		    firePropertyChangeListener(ICON_PROPERTY, loc, null, icon);
		} else if (myCancellable.isCancelled() == false) {
		    noIcons.add(loc.toString());
		}
	    }
	}
    };

    final Thread[] thumbnailThreads = new Thread[3];

    final Runnable thumbnailRunnable = new Runnable() {
	public void run() {
	    while (true) {
		IOLocation loc = null;
		synchronized (requestThumbnailList) {
		    if (requestThumbnailList.size() == 0)
			return;

		    loc = requestThumbnailList.remove(requestThumbnailList
			    .size() - 1);
		}
		Cancellable myCancellable = cancellable;
		BufferedImage image = null;
		try {
		    image = loc.getThumbnail(thumbnailSize, myCancellable);
		} catch (Throwable t) {
		    handleUncaughtException(t);
		}
		if (image != null) {
		    thumbnails.put(loc, image);
		    firePropertyChangeListener(THUMBNAIL_PROPERTY, loc, null,
			    image);
		} else if (myCancellable.isCancelled() == false) {
		    noThumbnails.add(loc.toString());
		}
	    }
	}
    };

    public GraphicCache() {
	this(null);
    }

    public GraphicCache(Dimension thumbnailSize) {
	if (thumbnailSize != null) {
	    this.thumbnailSize = new Dimension(thumbnailSize);
	}
    }

    protected void firePropertyChangeListener(String propertyName, Object src,
	    Object oldValue, Object newValue) {
	for (int a = 0; a < propertyListeners.size(); a++) {
	    PropertyChangeListener propertyListener = propertyListeners.get(a);
	    try {
		propertyListener.propertyChange(new PropertyChangeEvent(src,
			propertyName, oldValue, newValue));
	    } catch (RuntimeException e) {
		e.printStackTrace();
	    }
	}
    }

    public void setThumbnailSize(Dimension d) {
	thumbnailSize = new Dimension(d);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
	if (propertyListeners.contains(l))
	    return;
	propertyListeners.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
	propertyListeners.remove(l);
    }

    /**
     * This returns a cached icon or null immediately. If no graphic was cached:
     * then background threads will begin retrieving a icon. If they succeed,
     * they will fire <code>PropertyChangeListeners</code> with the
     * <code>IOLocation</code> as the source.
     */
    public Icon requestIcon(IOLocation loc) {
	synchronized (requestIconList) {
	    Icon icon = icons.get(loc);
	    if (icon != null)
		return icon;

	    if (noIcons.contains(loc.toString()))
		return null;

	    int i = requestIconList.indexOf(loc);
	    if (i != -1)
		requestIconList.remove(i);
	    requestIconList.add(loc);

	    // guarantee the thread(s) are active:
	    int requestedThreads = Math.min(requestIconList.size(),
		    iconThreads.length);
	    for (int a = 0; a < requestedThreads; a++) {
		if (iconThreads[a] == null || iconThreads[a].isAlive() == false) {
		    iconThreads[a] = new Thread(iconRunnable, "Icon Fetcher "
			    + a);
		    iconThreads[a].start();
		}
	    }
	    return null;
	}
    }

    /**
     * This returns a cached thumbnail or null immediately. If no graphic was
     * cached: then background threads will begin retrieving a thumbnail. If
     * they succeed, they will fire <code>PropertyChangeListeners</code> with
     * the <code>IOLocation</code> as the source.
     */
    public BufferedImage requestThumbnail(IOLocation loc) {
	synchronized (requestThumbnailList) {
	    BufferedImage thumbnail = thumbnails.get(loc);
	    if (thumbnail != null)
		return thumbnail;

	    if (noThumbnails.contains(loc.toString()))
		return null;

	    int i = requestThumbnailList.indexOf(loc);
	    if (i != -1)
		requestThumbnailList.remove(i);
	    requestThumbnailList.add(loc);

	    // guarantee the thread(s) are active:
	    int requestedThreads = Math.min(requestThumbnailList.size(),
		    thumbnailThreads.length);
	    for (int a = 0; a < requestedThreads; a++) {
		if (thumbnailThreads[a] == null
			|| thumbnailThreads[a].isAlive() == false) {
		    thumbnailThreads[a] = new Thread(thumbnailRunnable,
			    "Thumbnail Fetcher " + a);
		    thumbnailThreads[a].start();
		}
	    }
	    return null;
	}
    }

    /**
     * Add a listener to handle exceptions.
     * <p>
     * When this ActionListener is notified the Throwable will be the source.
     * 
     * @param l
     */
    public void addExceptionHandler(ActionListener l) {
	// TODO: in Java 1.5 there's an uncaughtexceptionhandler we should use.
	// To be 1.4 friendly we need another listener
	if (exceptionHandlers.contains(l) == false)
	    exceptionHandlers.add(l);
    }

    public void removeExceptionHandler(ActionListener l) {
	exceptionHandlers.remove(l);
    }

    protected void handleUncaughtException(Throwable t) {
	if (exceptionHandlers.size() > 0) {
	    for (int a = 0; a < exceptionHandlers.size(); a++) {
		ActionListener actionListener = exceptionHandlers.get(a);
		try {
		    actionListener.actionPerformed(new ActionEvent(t,
			    ActionEvent.ACTION_PERFORMED, "throwable"));
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	} else {
	    t.printStackTrace();
	}
    }

    public void clear() {
	synchronized (requestThumbnailList) {
	    synchronized (requestIconList) {
		thumbnails.clear();
		icons.clear();
		requestThumbnailList.clear();
		requestIconList.clear();
		cancellable.cancel();
		cancellable = new BasicCancellable();
	    }
	}
    }
}