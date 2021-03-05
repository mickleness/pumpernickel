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

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolTip;

import com.pump.swing.popover.JPopover;
import com.pump.swing.popup.SliderThumbPopupTarget;

public abstract class ShowcaseDemo extends JPanel {
	private static final long serialVersionUID = 1L;

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
	protected void addSliderPopover(JSlider slider, final String suffix) {
		JPopover p = new JPopover<JToolTip>(slider, new JToolTip(), false) {

			@Override
			protected void doRefreshPopup() {
				JSlider js = (JSlider) getOwner();
				int v = js.getValue();
				String newText;
				if (v == 1 && suffix.startsWith(" ") && suffix.endsWith("s")) {
					newText = v + suffix.substring(0, suffix.length() - 1);
				} else {
					newText = v + suffix;
				}
				getContents().setTipText(newText);

				// this is only because we have the JToolTipDemo so
				// colors might change:
				getContents().updateUI();
				getContents().setBorder(null);
			}
		};
		p.setTarget(new SliderThumbPopupTarget(slider));
	}
}