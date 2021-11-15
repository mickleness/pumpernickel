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
package com.pump.text.html;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import com.pump.text.html.css.image.CssUrlImageValue;
import com.pump.text.html.view.BufferedImageView;
import com.pump.text.html.view.PaintableView;
import com.pump.text.html.view.QBlockView;
import com.pump.text.html.view.QBodyBlockView;
import com.pump.text.html.view.QHtmlBlockView;
import com.pump.text.html.view.QInlineView;
import com.pump.text.html.view.QParagraphView;

/**
 * This is an enhanced HTMLFactory that produces certain specialized Views.
 */
public class QHTMLFactory extends HTMLFactory {

	@Override
	public View create(Element elem) {
		if (elem.getAttributes()
				.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.IMG) {
			String src = (String) elem.getAttributes()
					.getAttribute(HTML.Attribute.SRC);
			View b64img = createBase64Image(elem, src);
			if (b64img != null) {
				return b64img;
			}
		}

		View returnValue = createQView(elem);
		if (returnValue == null)
			returnValue = super.create(elem);

		return returnValue;
	}

	/**
	 * Return a specialized View, or null if the super class should be used.
	 */
	protected View createQView(Element elem) {
		// This method is copied and pasted from super.create(..)
		// But when possible we return a Q*View class.

		AttributeSet attrs = elem.getAttributes();
		Object elementName = attrs
				.getAttribute(AbstractDocument.ElementNameAttribute);
		Object o = (elementName != null) ? null
				: attrs.getAttribute(StyleConstants.NameAttribute);
		if (o instanceof HTML.Tag) {
			HTML.Tag kind = (HTML.Tag) o;
			if (kind == HTML.Tag.NOFRAMES || kind == HTML.Tag.ISINDEX) {
				// these are deprecated tags we don't care about
				return null;
			} else if (kind == HTML.Tag.BODY) {
				return new QBodyBlockView(elem);
			} else if (kind == HTML.Tag.HR || kind == HTML.Tag.BR
					|| kind == HTML.Tag.TABLE || kind == HTML.Tag.FRAME
					|| kind == HTML.Tag.COMMENT || kind == HTML.Tag.TITLE
					|| kind == HTML.Tag.META || kind == HTML.Tag.LINK
					|| kind == HTML.Tag.STYLE || kind == HTML.Tag.SCRIPT
					|| kind == HTML.Tag.AREA || kind == HTML.Tag.MAP
					|| kind == HTML.Tag.PARAM || kind == HTML.Tag.HEAD
					|| kind == HTML.Tag.APPLET || kind == HTML.Tag.FRAMESET
					|| kind instanceof HTML.UnknownTag) {
				// if you look in super.create(View), these tags don't return a
				// public class we can extend. (We can address these as needed
				// in the future?)
				return null;
			} else if (kind == HTML.Tag.CONTENT) {
				return new QInlineView(elem);
			} else if (kind == HTML.Tag.IMPLIED) {
				String ws = (String) elem.getAttributes()
						.getAttribute(CSS.Attribute.WHITE_SPACE);
				if ((ws != null) && ws.equals("pre")) {
					// super.create(view) creates a LineView here, but that's
					// not public
					return null;
				}
				return new QParagraphView(elem);
			} else if ((kind == HTML.Tag.P) || (kind == HTML.Tag.H1)
					|| (kind == HTML.Tag.H2) || (kind == HTML.Tag.H3)
					|| (kind == HTML.Tag.H4) || (kind == HTML.Tag.H5)
					|| (kind == HTML.Tag.H6) || (kind == HTML.Tag.DT)) {
				return new QParagraphView(elem);
			} else if ((kind == HTML.Tag.MENU) || (kind == HTML.Tag.DIR)
					|| (kind == HTML.Tag.UL) || (kind == HTML.Tag.OL)) {
				// should we create a QListView?
				return null;
			} else if (kind == HTML.Tag.HTML) {
				return new QHtmlBlockView(elem, View.Y_AXIS);
			} else if ((kind == HTML.Tag.LI) || (kind == HTML.Tag.CENTER)
					|| (kind == HTML.Tag.DL) || (kind == HTML.Tag.DD)
					|| (kind == HTML.Tag.DIV) || (kind == HTML.Tag.BLOCKQUOTE)
					|| (kind == HTML.Tag.PRE) || (kind == HTML.Tag.FORM)) {
				return new QBlockView(elem, View.Y_AXIS);
			} else if (kind == HTML.Tag.IMG) {
				// should we create a QImageView?
				return null;
			} else if ((kind == HTML.Tag.INPUT) || (kind == HTML.Tag.SELECT)
					|| (kind == HTML.Tag.TEXTAREA)) {
				// should we create a QFormView?
				return null;
			} else if (kind == HTML.Tag.OBJECT) {
				// should we create a QObjectView?
				return null;
			}
		}

		return null;
	}

	/**
	 * Create a View for a base64-encoded image.
	 * 
	 * @param elem
	 * @param src
	 * @return
	 */
	protected View createBase64Image(Element elem, String src) {
		try {
			if (src.startsWith("data:image/")) {
				CssUrlImageValue.ImageDataValue d = CssUrlImageValue
						.createPaintableFromDataUrl(src);
				if (d.bufferedImage != null) {
					// use BufferedImageView by default, in case it
					// inherits valuable features the PaintableView
					// may (currently) lack.
					return new BufferedImageView(elem, d.bufferedImage);
				}
				return new PaintableView(elem, d.paintable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}