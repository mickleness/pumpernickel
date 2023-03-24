/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io.location;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.TreePath;

import com.pump.awt.Dimension2D;
import com.pump.image.ImageSize;
import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.Scaling;
import com.pump.image.thumbnail.BasicThumbnail;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.BasicReceiver;
import com.pump.util.JVM;
import com.pump.util.Receiver;
import com.pump.util.list.ObservableList;

/**
 * An abstract representation for file systems, URLs, archives, search results,
 * etc.
 */
public abstract class IOLocation {

	private static final String[] bundleSuffixes = new String[] { ".app",
			".download", ".package", ".pkg", ".rtfd", ".framework", ".plugin",
			".band" };

	/**
	 * This indicates that Mac bundles should not be navigable. For example: the
	 * file "Safari.app" is technically a directory, but because it ends with a
	 * bundle-identifying suffix (".app"): the method <code>isNavigable()</code>
	 * will (by default) return <code>false</code>.
	 */
	public static boolean omitMacBundles = JVM.isMac;

	/**
	 * This is used to indicate a call to mkdir or mkdirs failed.
	 */
	public static class MakeDirectoryException extends IOException {
		private static final long serialVersionUID = 1L;

		public MakeDirectoryException() {
		}

		public MakeDirectoryException(String s) {
			super(s);
		}
	}

	/**
	 * This is used to indicate the parent of a location was expected and not
	 * available.
	 */
	public static class MissingParentException extends IOException {
		private static final long serialVersionUID = 1L;

		public MissingParentException() {
		}

		public MissingParentException(String s) {
			super(s);
		}
	}

	/**
	 * This is used to indicate that creating a file failed.
	 */
	public static class FileCreationException extends IOException {
		private static final long serialVersionUID = 1L;

		public FileCreationException() {
		}

		public FileCreationException(String s) {
			super(s);
		}
	}

	/**
	 * This is used to indicate that changing the name of a location failed.
	 */
	public static class SetNameException extends IOException {
		private static final long serialVersionUID = 1L;

		public SetNameException() {
		}

		public SetNameException(String s) {
			super(s);
		}
	}

	/**
	 * This is used to indicate that deleting a location failed.
	 */
	public static class DeleteException extends IOException {
		private static final long serialVersionUID = 1L;

		public DeleteException() {
		}

		public DeleteException(String s) {
			super(s);
		}
	}

	public enum Contains {
		/**
		 * The <code>contains()</code> method returns this if no resource is
		 * found with a given name.
		 */
		DOES_NOT_CONTAIN,
		/**
		 * The <code>contains()</code> method returns this if a directory is
		 * found with a given name.
		 */
		CONTAINS_AS_DIRECTORY,
		/**
		 * The <code>contains()</code> method returns this if a file is found
		 * with a given name.
		 */
		CONTAINS_AS_FILE
	}

	/**
	 * This returns a unique path for this location. This could be used for
	 * sorting, hashtables, or otherwise uniquely identifying this object.
	 * <P>
	 * Note this needs to <i>uniquely</i> the path, so different location types
	 * should include their unique prefixes. (a file-based location should start
	 * with something like "file://", and a ftp-based location should start with
	 * "ftp://", etc.)
	 */
	public abstract String getPath();

	/**
	 * This flushes any cached information about this location. Some locations
	 * may cache several properties: the children, isHidden, exists, etc. This
	 * method should be called when we know that something has changed and all
	 * cached values should be purged.
	 * 
	 */
	public void flush() {
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public boolean equals(Object t) {
		if (!(t instanceof IOLocation))
			return false;
		IOLocation l = (IOLocation) t;
		return l.getPath().equals(getPath());
	}

	/**
	 * This returns the file name of this location.
	 * <P>
	 * This should be the end of <code>getPath()</code>.
	 */
	public abstract String getName();

	/**
	 * Returns DOES_NOT_CONTAIN or CONTAINS_AS_DIRECTORY or CONTAINS_AS_FILE.
	 */
	public Contains contains(final String name) {
		final BasicCancellable cancellable = new BasicCancellable();
		final Contains[] returnValue = new Contains[] { null };
		Receiver<IOLocation> receiver = new Receiver<IOLocation>() {

			public void add(IOLocation... element) {
				for (IOLocation child : element) {
					if (child.getName().equalsIgnoreCase(name)) {
						if (child.isDirectory())
							returnValue[0] = Contains.CONTAINS_AS_DIRECTORY;
						returnValue[0] = Contains.CONTAINS_AS_FILE;
						cancellable.cancel();
						return;
					}
				}
			}

		};

		listChildren(receiver, cancellable);
		if (returnValue[0] != null)
			return returnValue[0];
		return Contains.DOES_NOT_CONTAIN;
	}

	/**
	 * Returns true if this is a location you can write to. If this returns
	 * true, then directories should be able to call
	 * <code>createDirectory()</code> and <code>createFile()</code> and files
	 * should be able to call <code>createOutputStream()</code>.
	 */
	public abstract boolean canWrite();

	/**
	 * Returns true if this is a directory.
	 */
	public abstract boolean isDirectory();

	/**
	 * Returns the parent of this node.
	 */
	public abstract IOLocation getParent();

	/**
	 * Returns the parent path of this node.
	 * <p>
	 * This should be equivalent to calling: <code>getParent().getPath()</code>.
	 */
	public String getParentPath() {
		IOLocation parent = getParent();
		if (parent == null)
			return null;
		return parent.getPath();
	}

	/**
	 * Returns a <code>TreePath</code> of this node and all its parents.
	 * 
	 * @param stringsOnly
	 *            if true then the TreePath should contains <code>String</code>
	 *            objects; if <code>false</code> then this should return
	 *            <code>IOLocation</code> objects.
	 */
	public TreePath getTreePath(boolean stringsOnly) {
		List<Object> path = new ArrayList<Object>();
		IOLocation loc = this;
		while (loc != null) {
			if (stringsOnly) {
				path.set(0, loc.getName());
			} else {
				path.set(0, loc);
			}
			IOLocation parent = loc.getParent();
			if (parent != null && parent.equals(loc)) {
				loc = null;
			} else {
				loc = parent;
			}
		}
		return new TreePath(path.toArray(new Object[path.size()]));
	}

	/**
	 * This returns a reference to a child of this location. This may return
	 * null if this location cannot contain children (either because it is a
	 * file and not a directory, or because it is a directory that doesn't have
	 * write privileges).
	 * <p>
	 * Also note that this can return a child that does not exist.
	 * <p>
	 * The default implementation only polls existing children: subclasses are
	 * encouraged to override this to return locations that do not yet exist.
	 * 
	 * @param name
	 *            the name of the child.
	 * @return the child requested, or <code>null</code>.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public IOLocation getChild(final String name) throws IOException {
		final BasicCancellable cancellable = new BasicCancellable();
		final IOLocation[] returnValue = new IOLocation[] { null };
		Receiver<IOLocation> receiver = new Receiver<IOLocation>() {

			public void add(IOLocation... element) {
				for (IOLocation child : element) {
					if (child.getName().equalsIgnoreCase(name)) {
						returnValue[0] = child;
						cancellable.cancel();
						return;
					}
				}
			}

		};

		listChildren(receiver, cancellable);
		if (returnValue[0] != null)
			return returnValue[0];
		return null;
	}

	/**
	 * Creates a directory at this location. This will throw an exception if a
	 * file already exists at this location.
	 * 
	 * @throws MakeDirectoryException
	 *             if an error occurs making a directory.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public abstract void mkdir() throws IOException, MakeDirectoryException;

	/**
	 * Creates a directory at this location and creates all necessary parent
	 * directories.
	 * 
	 * @throws MakeDirectoryException
	 *             if an error occurs making a directory.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public void mkdirs() throws IOException, MakeDirectoryException {
		flush();
		if (!exists()) {
			IOLocation parent = getParent();
			if (parent != null) {
				parent.mkdirs();
			}
			mkdir();
		}
	}

	/**
	 * If possible this will return a URL pointing to this location. This may
	 * return <code>null</code> if no such URL exists. (For example: a
	 * collection of search results is a directory of valid IOLocations that
	 * does not have a URL.)
	 * 
	 * @return a URL to this location, if it exists.
	 */
	public abstract URL getURL();

	/**
	 * This lists the children of this directory.
	 * 
	 * @param receiver
	 *            an optional <code>Receiver</code> to be notified as each child
	 *            is processed.
	 * @param cancellable
	 *            an optional <code>Cancellable</code>. If non-null: classes
	 *            that extend this class should constantly poll it to see if
	 *            <code>isCancelled()</code> is <code>true</code>.
	 * 
	 * @return all the children (if any) of this object. This should not return
	 *         null, but may return an empty array for non-directories.
	 */
	public abstract IOLocation[] listChildren(Receiver<IOLocation> receiver,
			BasicCancellable cancellable);

	public static final Icon FOLDER_ICON = UIManager.getIcon("Tree.closedIcon");
	public static final Icon FILE_ICON = UIManager.getIcon("Tree.leafIcon");

	/**
	 * Returns the optional icon for this location. This may return
	 * <code>null</code>.
	 * 
	 * @param cancellable
	 *            it is crucial to call <code>finish()</code> on this object
	 *            when this method finishes. Also you should constantly poll it
	 *            to see if <code>isCancelled()</code> is <code>true</code>.
	 * @return the optional icon for this location.
	 */
	public Icon getIcon(BasicCancellable cancellable) {
		try {
			// TODO: FileView.directoryIcon and FileView.fileIcon might also
			// work
			if (isDirectory()) {
				return FOLDER_ICON;
			}
			return FILE_ICON;
		} finally {
			if (cancellable != null)
				cancellable.finish();
		}
	}

	/**
	 * Returns the optional thumbnail for this location. This may return
	 * <code>null</code>.
	 * 
	 * @param maxSize
	 *            the maximum size of this thumbnail. Preferably one of the
	 *            dimensions of the returned image should match one of the
	 *            dimensions of this argument. (that is, if you pass 128x128,
	 *            the the width or height of the returned thumbnail should be
	 *            128)
	 * @param cancellable
	 *            it is crucial to call <code>finish()</code> on this object
	 *            when this method finishes. Also you should constantly poll it
	 *            to see if <code>isCancelled()</code> is <code>true</code>.
	 * @return the optional icon for this location.
	 */
	public BufferedImage getThumbnail(Dimension maxSize,
			Cancellable cancellable) {
		if (cancellable.isCancelled())
			return null;

		Dimension d = new Dimension(maxSize.width * 2, maxSize.height * 2);
		BufferedImage bi = createUnframedThumbnail(d);
		if (bi == null && isDirectory()) {
			return createDirectoryThumbnail(maxSize, cancellable);
		} else if (bi == null) {
			return null;
		}

		return getThumbnail().create(bi, maxSize);
	}

	/**
	 * Return the BasicThumbnail to apply to thumbnails.
	 */
	protected BasicThumbnail getThumbnail() {
		return BasicThumbnail.Aqua;
	}

	private static Comparator<BufferedImage> thumbnailComparator = new Comparator<BufferedImage>() {
		public int compare(BufferedImage bi1, BufferedImage bi2) {
			int h1 = bi1.getHeight();
			int h2 = bi2.getHeight();
			return h1 - h2;
		}
	};

	/**
	 * Returns a thumbnail composed of smaller thumbnails.
	 * 
	 * @param size
	 *            the size this thumbnail will be.
	 * @param cancellable
	 *            an optional cancellable.
	 * @return a thumbnail, or null if the smaller thumbnails that are required
	 *         were not found.
	 */
	protected BufferedImage createDirectoryThumbnail(final Dimension size,
			final Cancellable cancellable) {
		/**
		 * This is a complicated chain reaction of listeners and vectors.
		 */

		// add a directory to this to begin searching its files.
		final ObservableList<IOLocation> pendingDirectories = new ObservableList<IOLocation>();
		// store those files in this directory
		final BasicReceiver<IOLocation> files = new BasicReceiver<IOLocation>();
		// when a thumbnail is found, store it here.
		final ObservableList<BufferedImage> thumbnails = new ObservableList<BufferedImage>();

		// a monitor for all the activity in this method. It should NOT
		// have an effect on the cancellable passed to this method.
		final BasicCancellable retrievalCancellable = new BasicCancellable();

		// if the bigger stronger cancellable is canceled, everything
		// needs to grind to a halt: so we should cancel our local cancellable:
		ActionListener cancellableListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retrievalCancellable.cancel();
			}
		};

		if (cancellable != null) { // the argument is optional
			cancellable.addCancelListener(cancellableListener);
		}
		try {
			final IOLocation base = this;

			// when 3 thumbnails are collected, we cancel the local Cancellable
			// and everything stops.
			thumbnails.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (thumbnails.size() >= 3)
						retrievalCancellable.cancel();
				}
			}, false);

			// as files are added, try to get thumbnails.
			files.addListDataListener(new ListDataListener() {
				Set<IOLocation> processed = new HashSet<IOLocation>();

				public void intervalAdded(ListDataEvent e) {
					List<IOLocation> directories = new ArrayList<IOLocation>();
					for (int a = 0; a < files.getSize(); a++) {
						if (retrievalCancellable.isCancelled())
							return;
						IOLocation loc = files.getElementAt(a);
						if (processed.contains(loc) == false) {
							processed.add(loc);
							if (loc.isDirectory() == false) {
								BufferedImage bi = loc.getThumbnail(size,
										cancellable);
								if (bi != null) {
									bi = BasicThumbnail.Aqua.create(bi, size);
									thumbnails.add(bi);
								}
							} else {
								directories.add(loc);
							}
						}
					}

					try {
						pendingDirectories.addAll(directories);
					} catch (Throwable throwable) {
						throwable.printStackTrace();
						retrievalCancellable.cancel();
					}
				}

				public void intervalRemoved(ListDataEvent e) {
				}

				public void contentsChanged(ListDataEvent e) {
				}
			});

			// as directories are added: expand the files we're searching.
			pendingDirectories.addChangeListener(new ChangeListener() {
				int inside = 0; // so we don't redundantly listen to the

				// events this listener triggers

				public void stateChanged(ChangeEvent e) {
					if (inside > 0)
						return;

					while (pendingDirectories.size() > 0) {
						if (retrievalCancellable.isCancelled())
							return;

						inside++;
						IOLocation directory = pendingDirectories.remove(0);
						inside--;

						// only comb through directories if they're
						// actually near this one
						boolean closeBy = false;
						int ctr = 0;
						IOLocation t = directory;
						while (closeBy == false && ctr < 2 && t != null) {
							if (t.equals(base)) {
								closeBy = true;
							}
							t = t.getParent();
							ctr++;
						}
						if (closeBy) {
							inside++;
							try {
								directory.listChildren(files,
										retrievalCancellable);
							} catch (Throwable throwable) {
								throwable.printStackTrace();
								retrievalCancellable.cancel();
							} finally {
								inside--;
							}
						}
					}
				}
			}, true);

			// this is the little domino that sets everything else off.
			// remember this is a blocking call:
			pendingDirectories.add(this);

			if (cancellable.isCancelled())
				return null;
		} finally {
			if (cancellable != null) {
				cancellable.removeCancelListener(cancellableListener);
			}
		}

		// ok. we've searched everything. Do we have 3 thumbnails?

		if (thumbnails.size() < 3) {
			return null;
		}

		BufferedImage[] thumbnailArray = thumbnails
				.toArray(new BufferedImage[thumbnails.size()]);

		Arrays.sort(thumbnailArray, thumbnailComparator);

		BufferedImage image = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		int padding = 5;
		double sx = ((double) size.width)
				/ ((double) (size.width + padding * thumbnails.size()));
		double sy = ((double) size.height)
				/ ((double) (size.height + padding * thumbnails.size()));
		double s = Math.min(sx, sy);
		g.scale(s, s);
		for (int a = 0; a < thumbnailArray.length; a++) {
			BufferedImage nextImage = thumbnailArray[a];
			g.drawImage(nextImage,
					size.width / 2 - nextImage.getWidth() / 2 + a * padding,
					size.height / 2 - nextImage.getHeight() / 2 + a * padding,
					null);
		}
		g.dispose();
		return image;
	}

	protected BufferedImage createUnframedThumbnail(Dimension maxSize) {
		if (isDirectory() == true || canRead() == false)
			return null;

		String suffix = getExtension();
		if (suffix == null || suffix.length() == 0)
			return null;

		suffix = suffix.toLowerCase();

		try {
			if (suffix.equals("jpeg") || suffix.equals("jpg")) {
				try (InputStream in = createInputStream()) {
					BufferedImage bi = JPEGMetaData.getThumbnail(in);
					if (bi != null)
						return bi;
				}
			}

			if (suffix.equals("jpg") || suffix.equals("jpeg")
					|| suffix.equals("png") || suffix.equals("gif")
					|| suffix.equals("bmp")) {

				URL url = getURL();
				if (url != null) {
					try {
						Dimension d = ImageSize.get(url);
						d = Dimension2D.scaleProportionally(d, maxSize);
						return Scaling.scale(url, ImageType.INT_ARGB,
								d);
					} catch (Throwable t) {
						// do nothing. Pity this failed.
					}
					// TODO: we could do more here. For example: we can parse
					// our own BMPs.
				}

				InputStream in = createInputStream();
				try {
					return ImageIO.read(in);
				} finally {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		} catch (Throwable t) {
			System.err.println(this);
			t.printStackTrace();
		}
		return null;
	}

	/**
	 * Return the file extension of this resource, or null if it does not exist
	 * (as will often be the case for directories).
	 */
	public String getExtension() {
		String name = getName();
		int i = name.lastIndexOf('.');
		if (i == -1)
			return null;
		return name.substring(i + 1);
	}

	/**
	 * This changes the name of this location. If there are resources currently
	 * at this location: those resources should be moved if
	 * <code>getName().equals(s)</code> is <code>false</code> and no exceptions
	 * occur.
	 * <P>
	 * This method is not capable of moving a resource to another directory
	 * structure.
	 * 
	 * @param s
	 *            the name name of this resource.
	 * @return the new IOLocation the resource formerly at this location can now
	 *         be accessed from.
	 * @throws SetNameException
	 *             if an error occurred renaming this file.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public abstract IOLocation setName(String s)
			throws IOException, SetNameException;

	/**
	 * Creates an <code>InputStream</code> to read from this location -- if
	 * possible.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public abstract InputStream createInputStream() throws IOException;

	/**
	 * Creates an <code>OutputStream</code> to write to this location -- if
	 * possible.
	 * 
	 * @throws FileCreationException
	 *             if an error occurred creating this resource.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public abstract OutputStream createOutputStream()
			throws IOException, FileCreationException;

	/**
	 * Returns the size of this resource.
	 * 
	 * @return the size of this resource, or zero if it does not exist.
	 * @throws IOException
	 *             a generic exception related to this operation.
	 */
	public abstract long length() throws IOException;

	/**
	 * Deletes the resources at this location.
	 * 
	 * @throws DeleteException
	 *             an exception related to deleting.
	 * @throws IOException
	 *             a generic exception related to this operation.
	 */
	public abstract void delete() throws IOException, DeleteException;

	/**
	 * Indicates whether this resource exists.
	 * <p>
	 * Generally <code>IOLocation</code> objects are hard to come across if they
	 * aren't associated with a resource, but if you delete or rename a location
	 * successfully this will return <code>false</code>.
	 * 
	 * @return true if this location represents a resource that exists.
	 */
	public abstract boolean exists();

	/**
	 * @return the last modified time (in milliseconds) of this resource.
	 * 
	 * @throws IOException
	 *             a generic exception related to this operation.
	 */
	public abstract long getModificationDate() throws IOException;

	/**
	 * Indicates if this resource is visible to the user.
	 * 
	 * @return true if this resource is hidden from the user.
	 */
	public abstract boolean isHidden();

	/**
	 * Indicates whether this resource is an alias or not.
	 * 
	 * @return true if this resource is an alias.
	 */
	public boolean isAlias() {
		return false;
	}

	/**
	 * This indicates if the user is encouraged to navigate inside this
	 * location.
	 * <P>
	 * For example, on Macs certain folders end in ".app". Although technically
	 * these are directories, users shouldn't navigate inside them.
	 * <p>
	 * Also: archive files (like the ZipArchiveLocation) are a file (and not a
	 * directory), but they are navigable.
	 * <p>
	 * By default this returns <code>isDirectory()</code>, but if
	 * <code>omitMacBundles</code> is true this will return false if this
	 * location's name ends with a suffix like ".app".
	 * 
	 * @return true if this resource is a directory that should be navigable.
	 * @see #omitMacBundles
	 */
	public boolean isNavigable() {
		return isDefaultNavigable(this);
	}

	/**
	 * This indicates if the user is encouraged to navigate inside this location
	 * by default.
	 * 
	 * @see #omitMacBundles
	 */
	protected static boolean isDefaultNavigable(IOLocation loc) {
		boolean isDirectory = loc.isDirectory();
		if (!isDirectory)
			return false;

		if (omitMacBundles) {
			String name = loc.getName();
			for (int a = 0; a < bundleSuffixes.length; a++) {
				if (name.endsWith(bundleSuffixes[a]))
					return false;
			}
			return true;
		}
		return true;
	}

	/**
	 * If this returns true then <code>createInputStream()</code> should return
	 * successfully.
	 * 
	 * @return true if this is a resource you have permission to read.
	 */
	public abstract boolean canRead();

	/**
	 * This may return a group shared between related IOLocations. For example:
	 * if this location is accessed via a connection with a name and password,
	 * then all related locations should return the same object. When the user
	 * switches to a location in a different group: then the former group's
	 * <code>dispose()</code> method is called.
	 * <p>
	 * This may return null if the <code>IOLocationGroup</code> is not
	 * necessary.
	 * 
	 * @return a IOLocationGroup for this resource
	 * 
	 */
	public IOLocationGroup getGroup() {
		return null;
	}
}