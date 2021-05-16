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
package com.pump.showcase.demo;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolTip;

import com.pump.showcase.app.ShowcaseDemoInfo;
import com.pump.swing.popover.BasicPopoverVisibility;
import com.pump.swing.popover.JPopover;
import com.pump.swing.popup.SliderThumbPopupTarget;

public abstract class ShowcaseDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	private ShowcaseDemoInfo showcaseDemoInfo;

	/**
	 * This is the title of the demo panel.
	 */
	public abstract String getTitle();

	/**
	 * Return text explaining this demo that appears immediately below the
	 * title.
	 * <p>
	 * Whenever possible this should begin with "This demonstrates" or "This
	 * compares". It is OK to include paragraphs of text, but preferably the
	 * first paragraph should be a very short/simple 1-2 sentence summary.
	 */
	public abstract String getSummary();

	/**
	 * Return the optional URL of a html resource to display for additional
	 * reading.
	 */
	public abstract URL getHelpURL();

	/**
	 * Return relevant keywords to assist in searches.
	 */
	public abstract String[] getKeywords();

	/**
	 * Return classes that are demonstrated to assist in searches.
	 */
	public abstract Class<?>[] getClasses();

	/**
	 * Add a popover labeling a slider.
	 * 
	 * @param suffix
	 *            the text to append after the numeric value, such as "%" or "
	 *            pixels".
	 */
	protected void addSliderPopover(final JSlider slider, final String suffix) {
		Callable<String> textGenerator = new Callable<String>() {

			@Override
			public String call() throws Exception {
				int v = slider.getValue();
				String newText;
				if (v == 1 && suffix.startsWith(" ") && suffix.endsWith("s")) {
					newText = v + suffix.substring(0, suffix.length() - 1);
				} else {
					newText = v + suffix;
				}
				return newText;
			}

		};
		addSliderPopover(slider, textGenerator);
	}

	/**
	 * Add a popover labeling a slider.
	 */
	protected void addSliderPopover(JSlider slider,
			final Callable<String> textGenerator) {
		JPopover<JToolTip> p = new JPopover<JToolTip>(slider, new JToolTip(),
				false) {

			@Override
			protected void doRefreshPopup() {
				String str = null;
				try {
					str = textGenerator.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (str == null) {
					refreshVisibility(true);
					return;
				}

				getContents().setTipText(str);

				// this is only because we have the JToolTipDemo so
				// colors might change:
				getContents().updateUI();
				getContents().setBorder(null);
			}
		};
		p.setTarget(new SliderThumbPopupTarget(slider));

		p.setVisibility(new BasicPopoverVisibility<JToolTip>() {

			@Override
			public boolean isVisible(JPopover<JToolTip> popover) {
				String str = null;
				try {
					str = textGenerator.call();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return super.isVisible(popover) && str != null;
			}

		});
	}

	/**
	 * Return a list of Runnables that should be invoked in a separate thread
	 * before this demo is shown to the user. This is used for demos that need
	 * to run thousands of timed comparisons to create charts.
	 */
	@SuppressWarnings("unchecked")
	public List<Runnable> getInitializationRunnables() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Assign the ShowcaseDemoInfo that created this object.
	 */
	public void setDemoInfo(ShowcaseDemoInfo showcaseDemoInfo) {
		if (this.showcaseDemoInfo != null)
			throw new IllegalArgumentException(
					"This method should only be called once.");
		this.showcaseDemoInfo = showcaseDemoInfo;
	}

	/**
	 * Return the ShowcaseDemoInfo that created this object.
	 */
	public ShowcaseDemoInfo getDemoInfo() {
		return showcaseDemoInfo;
	}
}