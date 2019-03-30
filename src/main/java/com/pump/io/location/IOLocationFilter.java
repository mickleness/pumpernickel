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

import java.util.ArrayList;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.pump.util.ObservableList;

/**
 * Filters <code>IOLocations</code> similar to a <code>FileFilter</code>.
 */
public abstract class IOLocationFilter {

	/**
	 * This filters a location.
	 * <P>
	 * If this returns <code>null</code> then the argument was not accepted. For
	 * example, a hidden file might not be accepted in a list of visible files.
	 * <P>
	 * Also this may return a completely new object. For example you can filter
	 * zip archives into a <code>ZipArchiveLocation</code> to make that archive
	 * traversable.
	 * <P>
	 * A lot of the time, however, this method will simply return the argument
	 * untouched.
	 * 
	 * @param loc
	 *            the location to consider.
	 * @return the location to use, or <code>null</code> if this location is not
	 *         accepted.
	 */
	public abstract IOLocation filter(IOLocation loc);

	/**
	 * This returns a filtered list of the source list. The source list is
	 * continually listened to, so the returned list is updated as necessary.
	 */
	public ObservableList<IOLocation> filter(
			final ObservableList<IOLocation> srcList) {
		final ObservableList<IOLocation> filteredList = new ObservableList<IOLocation>();
		ListDataListener listener = new ListDataListener() {

			public void contentsChanged(ListDataEvent e) {
				ArrayList<IOLocation> list = new ArrayList<IOLocation>(
						srcList.size());
				for (int a = 0; a < srcList.size(); a++) {
					IOLocation l = srcList.get(a);
					l = filter(l);
					if (l != null)
						list.add(l);
				}
				filteredList.setAll(list);
			}

			public void intervalAdded(ListDataEvent e) {
				if (e.getIndex1() != srcList.size() - 1) {
					contentsChanged(e);
					return;
				}
				for (int a = e.getIndex0(); a <= e.getIndex1(); a++) {
					IOLocation loc = srcList.get(a);
					loc = filter(loc);
					if (loc != null)
						filteredList.add(loc);
				}
			}

			public void intervalRemoved(ListDataEvent e) {
				contentsChanged(e);
			}
		};
		srcList.addSynchronizedListener(listener, true);

		// force it to run once to initialize filteredList:
		listener.contentsChanged(null);

		return filteredList;
	}
}