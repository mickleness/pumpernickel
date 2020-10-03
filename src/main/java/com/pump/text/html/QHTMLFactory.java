package com.pump.text.html;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Base64;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import com.pump.image.ImageLoader;
import com.pump.text.html.view.BufferedImageView;
import com.pump.text.html.view.QBlockView;
import com.pump.text.html.view.QHtmlBlockView;
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
			} else if (kind == HTML.Tag.BODY || kind == HTML.Tag.HR
					|| kind == HTML.Tag.BR || kind == HTML.Tag.TABLE
					|| kind == HTML.Tag.FRAME || kind == HTML.Tag.COMMENT
					|| kind == HTML.Tag.TITLE || kind == HTML.Tag.META
					|| kind == HTML.Tag.LINK || kind == HTML.Tag.STYLE
					|| kind == HTML.Tag.SCRIPT || kind == HTML.Tag.AREA
					|| kind == HTML.Tag.MAP || kind == HTML.Tag.PARAM
					|| kind == HTML.Tag.HEAD || kind == HTML.Tag.APPLET
					|| kind == HTML.Tag.FRAMESET
					|| kind instanceof HTML.UnknownTag) {
				// if you look in super.create(View), these tags don't return a
				// public class we can extend. (We can address these as needed
				// in the future?)
				return null;
			} else if (kind == HTML.Tag.CONTENT) {
				// should we create a QInlineView?
				return null;
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
				return new QBlockView(elem);
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
				src = src.substring("data:image/".length());
				int i = src.indexOf(';');
				String imageType = src.substring(0, i).toLowerCase();
				imageType = imageType.toLowerCase();

				String imageData = src.substring(i + 1);
				if (!imageData.startsWith("base64,")) {
					int i2 = imageData.indexOf(',');
					if (i2 == -1 || i2 > 30)
						i2 = Math.min(30, imageData.length());
					String sample = imageData.substring(0, i2);
					throw new UnsupportedOperationException(
							"This decoder does not support image data that begins with \""
									+ sample + "\"");
				}
				imageData = imageData.substring("base64,".length());

				if (imageType.equals("jpg") || imageType.equals("jpeg")
						|| imageType.equals("png")) {
					byte[] bytes = Base64.getDecoder().decode(imageData);
					BufferedImage img = ImageLoader.createImage(
							Toolkit.getDefaultToolkit().createImage(bytes));
					return new BufferedImageView(elem, img);
				} else {
					// regarding gifs: we could support them, we just haven't
					// bothered yet.
					throw new UnsupportedOperationException(
							"This decoder does not support bas64 encoded "
									+ imageType + ".");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
