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
package com.pump.plaf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.plaf.basic.BasicPanelUI;

import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;
import com.pump.util.WeakSet;

public abstract class AbstractPanelUI extends BasicPanelUI {

	protected ObservableProperties properties = new ObservableProperties();

	protected Collection<JPanel> installedPanels = new WeakSet<JPanel>();

	public AbstractPanelUI() {
		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				repaintInstalledPanels();
			}
		});
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		synchronized (installedPanels) {
			installedPanels.add((JPanel) c);
		}
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		synchronized (installedPanels) {
			installedPanels.remove(c);
		}
	}

	/**
	 * Repaint all panels using this UI.
	 */
	protected void repaintInstalledPanels() {
		for (JComponent jc : getInstalledPanels()) {
			jc.repaint();
		}
	}

	/**
	 * Return all the panels that have installed this UI.
	 */
	protected JPanel[] getInstalledPanels() {
		synchronized (installedPanels) {
			return installedPanels.toArray(new JPanel[installedPanels.size()]);
		}
	}

	protected abstract boolean isSupported(Key<?> key);

	/**
	 * Return a property.
	 * 
	 * @throws IllegalArgumentException
	 *             if the key provided is not supported by this class.
	 */
	public <T> T getProperty(Key<T> key) {
		if (!isSupported(key))
			throw new IllegalArgumentException("This class does not support \""
					+ key + "\"");
		return properties.get(key);
	}

	/**
	 * Assign a property.
	 * 
	 * @throws IllegalArgumentException
	 *             if the key provided is not supported by this class.
	 */
	public <T> void setProperty(Key<T> key, T value) {
		if (!isSupported(key))
			throw new IllegalArgumentException("This class does not support \""
					+ key + "\"");
		properties.set(key, value);
	}

	/**
	 * Adds a PropertyChangeListener to this object.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		properties.addListener(pcl);
	}

	/**
	 * Removes a PropertyChangeListener from this object.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		properties.removeListener(pcl);
	}

	/**
	 * This is invoked by {@link BasicPanelUI#installUI(JComponent)} , and this
	 * in turn invokes {@link #installColorsAndFont(JPanel)},
	 * {@link #installBorder(JPanel)}, and {@link #installOpacity(JPanel)}.
	 */
	protected void installDefaults(JPanel p) {
		installColorsAndFont(p);
		installBorder(p);
		installOpacity(p);
	}

	/**
	 * Install the default panel background, foreground, and font.
	 */
	protected void installColorsAndFont(JPanel p) {
		LookAndFeel.installColorsAndFont(p, "Panel.background",
				"Panel.foreground", "Panel.font");
	}

	/**
	 * Install the default panel border.
	 */
	protected void installBorder(JPanel p) {
		LookAndFeel.installBorder(p, "Panel.border");
	}

	/**
	 * Install the default panel opacity.
	 */
	protected void installOpacity(JPanel p) {
		LookAndFeel.installProperty(p, "opaque", Boolean.TRUE);
	}

	/**
	 * This is invoked by
	 * {@link BasicPanelUI#uninstallUI(javax.swing.JComponent)} , and this in
	 * turn invokes {@link #uninstallBorder(JPanel)}
	 */
	protected void uninstallDefaults(JPanel p) {
		uninstallBorder(p);
	}

	protected void uninstallBorder(JPanel p) {
		LookAndFeel.uninstallBorder(p);
	}

	@Override
	public int hashCode() {
		return properties.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!getClass().equals(obj.getClass()))
			return false;
		AbstractPanelUI other = (AbstractPanelUI) obj;
		if (!properties.equals(other.properties))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + properties;
	}
}