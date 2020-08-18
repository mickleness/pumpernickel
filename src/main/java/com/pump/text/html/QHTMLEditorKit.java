package com.pump.text.html;

import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;

/**
 * This is an enhanced HTMLEditorKit.
 */
public class QHTMLEditorKit extends HTMLEditorKit {
	private static final long serialVersionUID = 1L;

	private static final ViewFactory defaultFactory = new QHTMLFactory();

	@Override
	public ViewFactory getViewFactory() {
		return defaultFactory;
	}
}
