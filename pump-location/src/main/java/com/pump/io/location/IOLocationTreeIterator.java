/*
 * @(#)IOLocationTreeIterator.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
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
package com.pump.io.location;


import java.util.ArrayList;
import java.util.List;

import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.BasicReceiver;
import com.pump.util.TreeIterator;

/** A <code>TreeIterator</code> similar to the <code>FileTreeIterator</code> class.
 */
public class IOLocationTreeIterator extends TreeIterator<IOLocation> {

	/** The optional filter applied to this iterator. IOLocations that
	 * do not pass this filter will not be returned.
	 */
	public final IOLocationFilter filter;
	
	private List<Cancellable> allCancellables;
	
	/**
	 * 
	 * @param parent the root node to begin iterating over.
	 * @param includeRoot whether this iterator should include the root node.
	 * @param filter an optional (but strongly recommended) filter. At the very least:
	 * consider applying a filter that strips out aliases. Otherwise this could lead
	 * to a recursive unending file tree.
	 */
	public IOLocationTreeIterator(IOLocation parent, boolean includeRoot,IOLocationFilter filter) {
		super(parent, includeRoot);
		this.filter = filter;
	}
	
	/** Cancels all directory listings taking place.
	 * 
	 */
	public void cancel() {
		init();
		synchronized(allCancellables) {
			for(Cancellable c : allCancellables) {
				c.cancel();
			}
		}
	}
	
	private void init() {
		if(allCancellables==null)
			allCancellables = new ArrayList<Cancellable>();
	}

	@Override
	protected IOLocation[] listChildren(IOLocation parent) {
		init();
		BasicReceiver<IOLocation> receiver = new BasicReceiver<IOLocation>() {
			@Override
			public void add(IOLocation... elements) {
				if(filter==null) {
					super.add(elements);
				} else {
					for(IOLocation e : elements) {
						IOLocation filtered = filter.filter(e);
						if(filtered!=null) {
							super.add(filtered);
						}
					}
				}
			}
		};
		BasicCancellable cancellable = new BasicCancellable();
		synchronized(allCancellables) {
			allCancellables.add(cancellable);
		}
		parent.listChildren(receiver, cancellable);
		return receiver.toArray(new IOLocation[receiver.getSize()]);
	}
}
