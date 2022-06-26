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

import java.util.ArrayList;
import java.util.List;

import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.BasicReceiver;
import com.pump.util.TreeIterator;

/**
 * A <code>TreeIterator</code> similar to the <code>FileTreeIterator</code>
 * class.
 */
public class IOLocationTreeIterator extends TreeIterator<IOLocation> {

	/**
	 * The optional filter applied to this iterator. IOLocations that do not
	 * pass this filter will not be returned.
	 */
	public final IOLocationFilter filter;

	private List<Cancellable> allCancellables;

	/**
	 * 
	 * @param parent
	 *            the root node to begin iterating over.
	 * @param includeRoot
	 *            whether this iterator should include the root node.
	 * @param filter
	 *            an optional (but strongly recommended) filter. At the very
	 *            least: consider applying a filter that strips out aliases.
	 *            Otherwise this could lead to a recursive unending file tree.
	 */
	public IOLocationTreeIterator(IOLocation parent, boolean includeRoot,
			IOLocationFilter filter) {
		super(parent, includeRoot);
		this.filter = filter;
	}

	/**
	 * Cancels all directory listings taking place.
	 * 
	 */
	public void cancel() {
		init();
		synchronized (allCancellables) {
			for (Cancellable c : allCancellables) {
				c.cancel();
			}
		}
	}

	private void init() {
		if (allCancellables == null)
			allCancellables = new ArrayList<Cancellable>();
	}

	@Override
	protected IOLocation[] listChildren(IOLocation parent) {
		init();
		BasicReceiver<IOLocation> receiver = new BasicReceiver<IOLocation>() {
			@Override
			public void add(IOLocation... elements) {
				if (filter == null) {
					super.add(elements);
				} else {
					for (IOLocation e : elements) {
						IOLocation filtered = filter.filter(e);
						if (filtered != null) {
							super.add(filtered);
						}
					}
				}
			}
		};
		BasicCancellable cancellable = new BasicCancellable();
		synchronized (allCancellables) {
			allCancellables.add(cancellable);
		}
		parent.listChildren(receiver, cancellable);
		return receiver.toArray(new IOLocation[receiver.getSize()]);
	}
}