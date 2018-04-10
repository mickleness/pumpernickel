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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.tree.TreePath;

import com.pump.io.location.CachedLocation;
import com.pump.io.location.IOLocation;
import com.pump.io.location.IOLocationTreeIterator;
import com.pump.io.location.SearchParameters;
import com.pump.io.location.SearchResults;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.Receiver;

public class DefaultSearchResults extends CachedLocation implements
	SearchResults {
    public static int MAX_RESULTS = 500;
    final SearchParameters parameters;
    final IOLocation directory;

    public DefaultSearchResults(IOLocation directory,
	    SearchParameters parameters) {
	this.parameters = parameters;
	this.directory = directory;
    }

    @Override
    protected String doGetPath() {
	return "search://" + directory.getPath();
    }

    @Override
    public String getParentPath() {
	return "search://";
    }

    public TreePath getTreePath() {
	return new TreePath(new String[] { "search://" });
    }

    @Override
    protected boolean doCanWrite() {
	return false;
    }

    public String getSearchText() {
	return parameters.getInput();
    }

    public IOLocation getSearchDirectory() {
	return directory;
    }

    public void mkdir() throws IOException {
	throw new IOException("operation not supported");
    }

    public IOLocation getChild(String name) {
	IOLocation[] children = listChildren(null, null);
	for (int a = 0; a < children.length; a++) {
	    if (children[a].getName().equalsIgnoreCase(name))
		return children[a];
	}
	return null;
    }

    @Override
    protected boolean doCanRead() {
	return true;
    }

    public InputStream createInputStream() throws IOException {
	throw new IOException();
    }

    public OutputStream createOutputStream() throws IOException {
	throw new IOException();
    }

    public void delete() throws IOException {
	throw new IOException();
    }

    protected boolean doExists() {
	return false;
    }

    @Override
    protected long doGetModificationDate() {
	return 0;
    }

    @Override
    protected String doGetName() {
	return "Search";
    }

    @Override
    public IOLocation getParent() {
	return null;
    }

    @Override
    protected boolean doIsDirectory() {
	return true;
    }

    @Override
    protected boolean doIsHidden() {
	return false;
    }

    @Override
    protected long doLength() {
	return 0;
    }

    private static Comparator<Number> reverseNumberComparator = new Comparator<Number>() {

	public int compare(Number o1, Number o2) {
	    if (o1.doubleValue() < o2.doubleValue())
		return 1;
	    if (o1.doubleValue() > o2.doubleValue())
		return -1;
	    return 0;
	}
    };

    @Override
    protected void doListChildren(Receiver<IOLocation> receiver,
	    Cancellable cancellable) {
	if (cancellable == null)
	    cancellable = new BasicCancellable();

	IOLocationTreeIterator i = new IOLocationTreeIterator(directory, false,
		null);

	/*
	 * FIXME: this approach requires traversing the entire directory
	 * structure to pick the BEST matches. As long as we're enforcing a
	 * quota: there isn't a model in place invoke receiver.add(X) and then
	 * (if we find out it doesn't make the cut) receciver.replace(X, Y)
	 */

	SortedMap<Number, LinkedList<IOLocation>> results = new TreeMap<Number, LinkedList<IOLocation>>(
		reverseNumberComparator);
	while (i.hasNext()) {
	    if (cancellable.isCancelled())
		return;
	    IOLocation loc = i.next();

	    float relevance = parameters.getRelevance(loc);
	    if (relevance > 0) {

		LinkedList<IOLocation> list = results.get(relevance);
		if (list == null) {
		    list = new LinkedList<IOLocation>();
		    results.put(relevance, list);
		}
		list.add(loc);
	    }
	}

	int ctr = 0;
	Iterator<Number> resultsKeyIter = results.keySet().iterator();
	while (ctr < MAX_RESULTS && resultsKeyIter.hasNext()) {
	    Number key = resultsKeyIter.next();
	    List<IOLocation> list = results.get(key);
	    while (ctr < MAX_RESULTS) {
		if (cancellable.isCancelled())
		    return;
		receiver.add(list.remove(0));
		ctr++;
	    }
	}
    }

    @Override
    protected boolean doIsNavigable() {
	return true;
    }

    @Override
    public IOLocation setName(String s) throws IOException {
	throw new IOException();
    }

    @Override
    protected boolean doIsAlias() {
	return false;
    }

    @Override
    public URL getURL() {
	return null;
    }
}