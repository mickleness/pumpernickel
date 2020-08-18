package com.pump.text.html;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Base64;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import com.pump.image.ImageLoader;

/**
 * This is an enhanced HTMLFactory.
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
		return super.create(elem);
	}

	private View createBase64Image(Element elem, String src) {
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
