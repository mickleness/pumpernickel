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
package com.pump.io.location;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.pump.image.ImageLoader;
import com.pump.image.thumbnail.BasicThumbnail;
import com.pump.swing.Cancellable;
import com.pump.swing.io.DefaultSearchResults;
import com.pump.util.ObservableList;
import com.pump.util.Receiver;

public class Pics4LearningLocation extends CachedLocation {

    private static String ROOT = "http://www.pics4learning.com/query.xml?category=root";
    IOLocation parent;
    IOLocation[] children;
    String name;
    String urlText;
    String thumbnailURLText;

    public Pics4LearningLocation() {
	this(null, "Pics4Learning", ROOT);
    }

    @Override
    protected String doGetPath() {
	return urlText;
    }

    private Pics4LearningLocation(IOLocation parent, String name, String url) {
	this(parent, name, url, null);
    }

    private Pics4LearningLocation(IOLocation parent, String name, String url,
	    String thumbnailURL) {
	this.parent = parent;
	this.name = name;
	if (url != null)
	    this.urlText = url.replace(" ", "%20");
	if (thumbnailURL != null)
	    this.thumbnailURLText = thumbnailURL.replace(" ", "%20");
    }

    @Override
    protected boolean doCanRead() {
	return true;
    }

    @Override
    protected boolean doCanWrite() {
	return false;
    }

    @Override
    public BufferedImage getThumbnail(Dimension size, Cancellable cancellable) {
	try {
	    if (thumbnailURLText != null) {
		URL thumbnailURL = new URL(thumbnailURLText);
		BufferedImage bi = ImageLoader.createImage(thumbnailURL);
		return BasicThumbnail.Aqua.create(bi, size);
	    }
	} catch (IOException e) {
	}
	return super.getThumbnail(size, cancellable);
    }

    public void mkdir() throws IOException {
	throw new IOException("operation not supported");
    }

    @Override
    public InputStream createInputStream() throws IOException {
	throw new IOException();
    }

    @Override
    public OutputStream createOutputStream() throws IOException {
	throw new IOException();
    }

    public void delete() throws IOException {
	throw new IOException();
    }

    @Override
    protected boolean doExists() {
	return true;
    }

    @Override
    protected long doGetModificationDate() {
	return -1;
    }

    @Override
    protected String doGetName() {
	return name;
    }

    public IOLocation getParent() {
	return parent;
    }

    @Override
    protected boolean doIsAlias() {
	return false;
    }

    @Override
    protected boolean doIsDirectory() {
	return thumbnailURLText == null;
    }

    @Override
    protected boolean doIsHidden() {
	return false;
    }

    @Override
    protected long doLength() {
	return 0;
    }

    @Override
    protected void doListChildren(Receiver<IOLocation> receiver,
	    Cancellable cancellable) {
	synchronized (this) {
	    if (children == null) {
		ObservableList<IOLocation> destination = new ObservableList<IOLocation>();
		if (parent == null && urlText.equals(ROOT)) {
		    loadRoot(destination, cancellable);
		    if (cancellable.isCancelled() == false)
			children = destination
				.toArray(new IOLocation[destination.size()]);
		} else {
		    loadImages(destination, cancellable);
		    if (cancellable.isCancelled() == false)
			children = destination
				.toArray(new IOLocation[destination.size()]);
		}
	    }
	    receiver.add(children);
	}
    }

    @Override
    public int hashCode() {
	return urlText.hashCode();
    }

    @Override
    public String toString() {
	return "Pics4LearningLocation[ url = \"" + urlText + "\"]";
    }

    protected int getMaxChildren() {
	return Integer.MAX_VALUE;
    }

    private void loadImages(List<IOLocation> images, Cancellable cancellable) {
	InputStream in = null;
	try {
	    URL url = new URL(urlText);
	    in = url.openStream();

	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String s;
	    int max = getMaxChildren();
	    int ctr = 0;
	    while ((s = br.readLine()) != null) {
		if (cancellable != null && cancellable.isCancelled())
		    return;

		s = s.trim();
		if (s.equals("<image>")) {
		    String name = null;
		    String src = null;
		    String thumbnail = null;
		    readProperties: while ((s = br.readLine()) != null) {
			s = s.trim();
			if (s.startsWith("<name>")) {
			    s = s.substring("<name>".length());
			    s = s.substring(0, s.length() - "<name>".length()
				    - 1);
			    name = s;
			} else if (s.startsWith("<src>")) {
			    s = s.substring("<src>".length());
			    s = s.substring(0, s.length() - "<src>".length()
				    - 1);
			    src = s;
			} else if (s.startsWith("<thumbsrc>")) {
			    s = s.substring("<thumbsrc>".length());
			    s = s.substring(0,
				    s.length() - "<thumbsrc>".length() - 1);
			    thumbnail = s;
			} else if (s.equals("</image>")) {
			    break readProperties;
			}
		    }

		    Pics4LearningLocation imageNode = new Pics4LearningLocation(
			    this, name, src, thumbnail);
		    imageNode.children = new IOLocation[] {};
		    images.add(imageNode);
		    ctr++;
		    if (ctr >= max)
			return;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		in.close();
	    } catch (IOException e) {
	    }
	}
    }

    private void loadRoot(List<IOLocation> categoryNodes,
	    Cancellable cancellable) {
	InputStream in = null;
	try {
	    URL url = new URL(urlText);
	    in = url.openStream();

	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String s;
	    while ((s = br.readLine()) != null) {
		if (cancellable != null && cancellable.isCancelled())
		    return;

		if (s.indexOf("<category name=") != -1) {
		    String categoryName = parseString(s);
		    Pics4LearningLocation categoryNode = new Pics4LearningLocation(
			    this,
			    categoryName,
			    ("http://www.pics4learning.com/query.xml?category=" + categoryName));

		    List<Pics4LearningLocation> subcategoryNodes = null;
		    readSubcategories: while ((s = br.readLine()) != null) {
			if (s.indexOf("<subcategory name=") != -1) {
			    if (subcategoryNodes == null)
				subcategoryNodes = new ArrayList<Pics4LearningLocation>();
			    String subname = parseString(s);
			    Pics4LearningLocation subcategoryNode = new Pics4LearningLocation(
				    categoryNode,
				    subname,
				    ("http://www.pics4learning.com/query.xml?category="
					    + categoryName + "&subcategory=" + subname));
			    subcategoryNodes.add(subcategoryNode);
			} else if (s.indexOf("</category>") != -1) {
			    break readSubcategories;
			}
		    }
		    if (subcategoryNodes != null) {
			IOLocation[] subcategoryNodeArray = subcategoryNodes
				.toArray(new IOLocation[subcategoryNodes.size()]);
			categoryNode.children = subcategoryNodeArray;
		    }

		    // don't add until the subcategories (if any) are added,
		    // since this may be added to a WatchableVector and go live
		    // immediately:
		    categoryNodes.add(categoryNode);
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		in.close();
	    } catch (IOException e) {
	    }
	}
    }

    private static String parseString(String s) {
	String original = s;
	try {
	    int i = s.indexOf('"');
	    s = s.substring(i + 1);
	    i = s.indexOf('"');
	    s = s.substring(0, i);
	    return s;
	} catch (RuntimeException e) {
	    System.err.println("parsing \'" + original + "'");
	    throw e;
	}
    }

    public IOLocation setName(String s) throws IOException {
	throw new IOException();
    }

    // TODO: revisit searching
    public IOLocation search(SearchParameters parameters) {
	return new Pics4LearningLocationSearchResults(this, parameters.input);
    }

    static class Pics4LearningLocationSearchResults extends
	    Pics4LearningLocation implements SearchResults {
	IOLocation caller;
	String searchText;

	public Pics4LearningLocationSearchResults(IOLocation caller,
		String searchText) {
	    super(null, "Search",
		    "http://www.pics4learning.com/query.xml?query="
			    + searchText);
	    this.searchText = searchText;
	    this.caller = caller;
	}

	public IOLocation getSearchDirectory() {
	    return caller;
	}

	public String getSearchText() {
	    return searchText;
	}

	@Override
	protected int getMaxChildren() {
	    return DefaultSearchResults.MAX_RESULTS;
	}
    }

    public URL getURL() {
	// TODO: implement this method.
	return null;
    }

    @Override
    protected boolean doIsNavigable() {
	return isDirectory();
    }
}