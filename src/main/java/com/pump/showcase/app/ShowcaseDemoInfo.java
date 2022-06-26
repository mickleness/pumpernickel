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
package com.pump.showcase.app;

import java.beans.PropertyChangeListener;
import java.util.Objects;

import com.pump.data.AbstractAttributeDataImpl;
import com.pump.data.ComparableBoundsChecker;
import com.pump.data.Key;
import com.pump.math.Fraction;
import com.pump.showcase.demo.ShowcaseDemo;
import com.pump.util.HumanStringComparator;

/**
 * This contains meta information about a ShowcaseDemo.
 * <p>
 * Initially this just contains a human-readable name and a classname. Then a
 * separate thread (see DemoLoadThread) instantiates each demo and (optionally)
 * loads it.
 */
public class ShowcaseDemoInfo extends AbstractAttributeDataImpl
		implements Comparable<ShowcaseDemoInfo> {
	private static final long serialVersionUID = 1L;

	public static Key<ShowcaseDemoPanel> KEY_DEMO_PANEL = new Key<>(
			ShowcaseDemoPanel.class, "panel");
	public static Key<Fraction> KEY_LOADING_PROGRESS = new Key<>(Fraction.class,
			"loadingProgress");
	public static Key<String> KEY_NAME = new Key<>(String.class, "name");
	public static Key<String> KEY_SIMPLE_CLASS_NAME = new Key<>(String.class,
			"simpleClassName");
	public static Key<ShowcaseDemo> KEY_SHOWCASE_DEMO = new Key<>(
			ShowcaseDemo.class, "demo");
	public static Key<Throwable[]> KEY_LOADING_EXCEPTIONS = new Key<>(
			Throwable[].class, "loadingExceptions");
	static {
		KEY_LOADING_PROGRESS.addBoundsChecker(
				new ComparableBoundsChecker<Fraction>(new Fraction(0, 1),
						new Fraction(1, 1), true, true));
	}

	public ShowcaseDemoInfo(String name, String classSimpleName) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(classSimpleName);
		setAttribute(KEY_NAME, name);
		setAttribute(KEY_SIMPLE_CLASS_NAME, classSimpleName);
		setAttribute(KEY_LOADING_EXCEPTIONS, new Exception[0]);
	}

	public String getDemoName() {
		return getAttribute(KEY_NAME);
	}

	public String getDemoSimpleClassName() {
		return getAttribute(KEY_SIMPLE_CLASS_NAME);
	}

	@Override
	public String toString() {
		return getDemoName();
	}

	public ShowcaseDemo getDemo() {
		ShowcaseDemo demo = getAttribute(KEY_SHOWCASE_DEMO);
		if (demo == null) {
			try {
				String z = "com.pump.showcase.demo." + getDemoSimpleClassName();
				Class<?> demoClass = Class.forName(z);
				demo = (ShowcaseDemo) demoClass.getDeclaredConstructor()
						.newInstance();
				demo.setDemoInfo(this);
				setAttribute(KEY_SHOWCASE_DEMO, demo);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return demo;
	}

	public boolean isDemoLoaded() {
		ShowcaseDemo demo = getAttribute(KEY_SHOWCASE_DEMO);
		return demo != null;
	}

	@Override
	public int compareTo(ShowcaseDemoInfo o) {
		HumanStringComparator hsc = new HumanStringComparator();
		return hsc.compare(getDemoName(), o.getDemoName());
	}

	public void setLoadingProgress(Fraction progress) {
		setAttribute(KEY_LOADING_PROGRESS, progress);
	}

	public Fraction getLoadingProgress() {
		Fraction f = getAttribute(KEY_LOADING_PROGRESS);
		if (f == null)
			return new Fraction(0, 1);
		return f;
	}

	public void addLoadingException(Throwable t) {
		Throwable[] oldArray = getAttribute(KEY_LOADING_EXCEPTIONS);
		Throwable[] newArray = new Throwable[oldArray.length + 1];
		System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
		newArray[newArray.length - 1] = t;
		setAttribute(KEY_LOADING_EXCEPTIONS, newArray);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
	}

	public ShowcaseDemoPanel getPanel() {
		return getAttribute(KEY_DEMO_PANEL);
	}

	public void setPanel(ShowcaseDemoPanel panel) {
		setAttribute(KEY_DEMO_PANEL, panel);
	}
}