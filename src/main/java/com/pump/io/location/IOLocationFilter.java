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

import com.pump.util.list.AddElementsEvent;
import com.pump.util.list.ChangeElementEvent;
import com.pump.util.list.ListListener;
import com.pump.util.list.ObservableList;
import com.pump.util.list.RemoveElementsEvent;
import com.pump.util.list.ReplaceElementsEvent;

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
		ListListener<IOLocation> listener = new ListListener<IOLocation>() {

			@Override
			public void elementsAdded(AddElementsEvent<IOLocation> event) {
				ObservableList<IOLocation> master = (ObservableList<IOLocation>) event
						.getSource();
				if (event.getIndex() != master.size()
						- event.getNewElements().size()) {
					contentsChanged();
					return;
				}
				for (IOLocation loc : event.getNewElements()) {
					loc = filter(loc);
					if (loc != null)
						filteredList.add(loc);
				}
			}

			@Override
			public void elementsRemoved(RemoveElementsEvent<IOLocation> event) {
				contentsChanged();
			}

			@Override
			public void elementChanged(ChangeElementEvent<IOLocation> event) {
				contentsChanged();
			}

			@Override
			public void elementsReplaced(ReplaceElementsEvent<IOLocation> event) {
				contentsChanged();
			}

			private void contentsChanged() {
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
		};
		srcList.addListListener(listener, true);

		// force it to run once to initialize filteredList:
		listener.elementChanged(null);

		return filteredList;
	}
}