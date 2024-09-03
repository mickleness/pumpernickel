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
package com.pump.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.pump.util.list.ObservableList;

/**
 * This <code>JPanel</code> presents a series of <code>Sections</code>. The key
 * distinction between simply adding components to a panel and adding
 * <code>Sections</code> to <code>SectionContainer</code> is: sections should be
 * considered more abstract. Adding a section may necessarily introduce other UI
 * components (like headers, tabs, close buttons, a draggable container, etc.)
 * <P>
 * The only current implementation of this is the {@link CollapsibleContainer}
 * and {@link ListSectionContainer}.
 * <p>
 * TODO: implement a tabbed subclass. Unlike the <code>JTabbedPane</code>, this
 * subclass would make the tabs complex components with button, contextual
 * menus, etc.
 * <P>
 * Another possible implementation of this could be similar to a
 * <code>CardLayout</code>, or a <code>CardLayout</code> with a
 * <code>JComboBox</code> or toolbar of modal icon buttons to toggle the visible
 * sections.
 * <p>
 * Or, lastly: this could similar to widgets or "Mission Control" on the Mac. (A
 * series of draggable components in a larger space.)
 * 
 */
public class SectionContainer extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * The property (used in <code>Section.getProperty())</code> to refer to a
	 * name.
	 */
	public static final String NAME = SectionContainer.class.getName()
			+ ".name";

	/**
	 * The property (used in <code>Section.getProperty())</code> to refer to an
	 * icon.
	 */
	public static final String ICON = SectionContainer.class.getName()
			+ ".icon";

	public static class Section implements Serializable {
		private static final long serialVersionUID = 1L;
		final String id;

		Map<String, Object> properties = new HashMap<String, Object>();
		List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
		JPanel body = new JPanel();

		protected Section(String id) {
			if (id == null)
				throw new NullPointerException("section ID must not be null");
			this.id = id;
		}

		public JPanel getBody() {
			return body;
		}

		public Object getProperty(String propertyName) {
			return properties.get(propertyName);
		}

		public String getID() {
			return id;
		}

		public void addPropertyChangeListener(PropertyChangeListener l) {
			listeners.add(l);
		}

		public void removePropertyChangeListener(PropertyChangeListener l) {
			listeners.remove(l);
		}

		public void setName(String newName) {
			if (newName == null)
				throw new NullPointerException();
			setProperty(NAME, newName);
		}

		public String getName() {
			return (String) properties.get(NAME);
		}

		public void setProperty(String propertyName, Object newValue) {
			PropertyChangeEvent evt = new PropertyChangeEvent(this,
					propertyName, properties.get(propertyName), newValue);
			if (newValue == null) {
				properties.remove(propertyName);
			} else {
				properties.put(propertyName, newValue);
			}
			for (PropertyChangeListener l : listeners) {
				try {
					l.propertyChange(evt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected final ObservableList<Section> sections = new ObservableList<Section>();

	protected Comparator<Section> comparator = null;

	public SectionContainer() {
		this(false);
	}

	public SectionContainer(boolean alphabetize) {
		if (alphabetize) {
			comparator = new Comparator<Section>() {
				@Override
				public int compare(Section o1, Section o2) {
					return o1.getName().toLowerCase()
							.compareTo(o2.getName().toLowerCase());
				}
			};
		}
	}

	public synchronized Section addSection(String id, String name) {
		for (Section s : sections) {
			if (s.id.equals(id))
				throw new IllegalArgumentException("the section ID \"" + id
						+ "\" is already reserved");
		}
		Section section = new Section(id);
		section.setName(name);

		if (comparator != null) {
			int index = Collections.binarySearch(sections, section, comparator);
			if (index >= 0)
				throw new RuntimeException(
						"Comparator returned invalid insertion index: " + index);
			index = -index - 1;
			sections.add(index, section);
		} else {
			sections.add(section);
		}
		return section;
	}

	public ObservableList<Section> getSections() {
		return sections;
	}
}