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
package com.pump.swing;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import com.pump.plaf.QPanelUI;

/**
 * This PopupFactory creates {@link QPopup QPopups} for tooltips, and includes
 * some controls to format all JToolTip UI's that pass through it.
 */
public class QPopupFactory extends PopupFactory {
	private static final int UNDEFINED = -99999;

	private PopupFactory delegate;
	private boolean tooltipCallout;

	/**
	 * Create a new QPopupFactory that delegates calls to the argument provided.
	 * 
	 * @param delegate
	 *            the PopupFactory that will process calls this PopupFactory
	 *            doesn't have interest in overriding. The default
	 *            recommendation for this argument is
	 *            {@link PopupFactory#getSharedInstance()}.
	 */
	public QPopupFactory(PopupFactory delegate) {
		Objects.requireNonNull(delegate);
		this.delegate = delegate;
	}

	public void setToolTipCallout(boolean callout) {
		tooltipCallout = callout;
	}

	public boolean isToolTipCallout() {
		return tooltipCallout;
	}

	@Override
	public Popup getPopup(Component owner, Component contents, int x, int y)
			throws IllegalArgumentException {
		Popup p;
		if (contents instanceof JToolTip) {
			JComponent c = (JComponent) contents;
			return getQPopup(owner, c, x, y);
		} else {
			p = delegate.getPopup(owner, contents, x, y);
		}
		return p;
	}

	/**
	 * Create a QPopup that is guaranteed to have a callout.
	 */
	public QPopup getQPopup(JComponent jc, JComponent contents) {
		return getQPopup(jc, contents, UNDEFINED, UNDEFINED);
	}

	/**
	 * 
	 * @param owner
	 * @param content
	 * @param x
	 *            if isToolTipCallout is false, then this is the x-coordinate of
	 *            where the tooltip will be placed.
	 * @param y
	 *            if isToolTipCallout is false, then this is the y-coordinate of
	 *            where the tooltip will be placed.
	 * @return
	 */
	public QPopup getQPopup(Component owner, JComponent content, int x, int y) {
		content.setBorder(null);
		content.setOpaque(false);
		JPanel container = new JPanel();
		container.setOpaque(false);
		container.add(content);
		QPanelUI ui = QPanelUI.createToolTipUI();
		container.setUI(ui);
		ui.setFillColor(content.getBackground());
		boolean useCallout = isToolTipCallout() || x == UNDEFINED
				|| y == UNDEFINED;

		if (!useCallout)
			ui.setCalloutSize(0);

		Insets i = container.getBorder().getBorderInsets(container);
		x -= i.left;
		y -= i.top;

		content.setSize(content.getPreferredSize());
		content.validate();

		QPopup p = useCallout ? new QPopup(owner, container) : new QPopup(
				owner, container, new Point(x, y));
		return p;
	}

	public PopupFactory getParentDelegate() {
		return delegate;
	}
}