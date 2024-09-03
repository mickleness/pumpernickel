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
package com.pump.text.html.view;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SizeRequirements;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.BlockView;

/**
 * This is 100% copied-and-pasted from the non-public
 * HTMLEditorKit.BodyBlockView class. If that class were public then this class
 * would not exist.
 */
public class SwingBodyBlockView extends BlockView implements ComponentListener {
	public SwingBodyBlockView(Element elem) {
		super(elem, View.Y_AXIS);
	}

	// reimplement major axis requirements to indicate that the
	// block is flexible for the body element... so that it can
	// be stretched to fill the background properly.
	@Override
	protected SizeRequirements calculateMajorAxisRequirements(int axis,
			SizeRequirements r) {
		r = super.calculateMajorAxisRequirements(axis, r);
		r.maximum = Integer.MAX_VALUE;
		return r;
	}

	@Override
	protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets,
			int[] spans) {
		Container container = getContainer();
		Container parentContainer;
		if (container != null && (container instanceof javax.swing.JEditorPane)
				&& (parentContainer = container.getParent()) != null
				&& (parentContainer instanceof javax.swing.JViewport)) {
			JViewport viewPort = (JViewport) parentContainer;
			if (cachedViewPort != null) {
				JViewport cachedObject = cachedViewPort.get();
				if (cachedObject != null) {
					if (cachedObject != viewPort) {
						cachedObject.removeComponentListener(this);
					}
				} else {
					cachedViewPort = null;
				}
			}
			if (cachedViewPort == null) {
				viewPort.addComponentListener(this);
				cachedViewPort = new WeakReference<JViewport>(viewPort);
			}

			componentVisibleWidth = viewPort.getExtentSize().width;
			if (componentVisibleWidth > 0) {
				Insets insets = container.getInsets();
				viewVisibleWidth = componentVisibleWidth - insets.left
						- getLeftInset();
				// try to use viewVisibleWidth if it is smaller than targetSpan
				targetSpan = Math.min(targetSpan, viewVisibleWidth);
			}
		} else {
			if (cachedViewPort != null) {
				JViewport cachedObject = cachedViewPort.get();
				if (cachedObject != null) {
					cachedObject.removeComponentListener(this);
				}
				cachedViewPort = null;
			}
		}
		super.layoutMinorAxis(targetSpan, axis, offsets, spans);
	}

	@Override
	public void setParent(View parent) {
		// if parent == null unregister component listener
		if (parent == null) {
			if (cachedViewPort != null) {
				Object cachedObject;
				if ((cachedObject = cachedViewPort.get()) != null) {
					((JComponent) cachedObject).removeComponentListener(this);
				}
				cachedViewPort = null;
			}
		}
		super.setParent(parent);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (!(e.getSource() instanceof JViewport)) {
			return;
		}
		JViewport viewPort = (JViewport) e.getSource();
		if (componentVisibleWidth != viewPort.getExtentSize().width) {
			Document doc = getDocument();
			if (doc instanceof AbstractDocument) {
				AbstractDocument document = (AbstractDocument) getDocument();
				document.readLock();
				try {
					layoutChanged(X_AXIS);
					preferenceChanged(null, true, true);
				} finally {
					document.readUnlock();
				}

			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	/*
	 * we keep weak reference to viewPort if and only if BodyBoxView is
	 * listening for ComponentEvents only in that case cachedViewPort is not
	 * equal to null. we need to keep this reference in order to remove
	 * BodyBoxView from viewPort listeners.
	 *
	 */
	private Reference<JViewport> cachedViewPort = null;
	private boolean isListening = false;
	private int viewVisibleWidth = Integer.MAX_VALUE;
	private int componentVisibleWidth = Integer.MAX_VALUE;
}