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
package com.pump.showcase.demo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pump.desktop.error.ErrorManager;
import com.pump.image.jpeg.JPEGMarker;
import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.jpeg.JPEGMetaDataListener;
import com.pump.io.parser.html.HTMLEncoding;
import com.pump.text.html.QHTMLEditorKit;

/**
 * A simple demo for the {@link JPEGMetaData} class.
 */
public class JPEGMetaDataDemo extends ShowcaseResourceExampleDemo<URL> {
	private static final long serialVersionUID = 1L;

	JTextPane textPane = new JTextPane();

	JPEGMetaDataListener listener = new JPEGMetaDataListener() {

		String currentMarkerCode = null;
		StringBuilder htmlBody = new StringBuilder();

		@Override
		public boolean isThumbnailAccepted(String markerName, int width,
				int height) {
			return true;
		}

		@Override
		public void addProperty(String markerName, String propertyName,
				Object value) {
			setMarker(markerName);
			htmlBody.append("<strong>" + HTMLEncoding.encode(propertyName)
					+ "</strong>: " + HTMLEncoding.encode(String.valueOf(value))
					+ "<br/>");
		}

		@Override
		public void addThumbnail(String markerName, BufferedImage bi) {
			setMarker(markerName);

			try {
				byte[] jpgBytes;
				try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
					ImageIO.write(bi, "jpg", byteOut);
					jpgBytes = byteOut.toByteArray();
				}

				String base64 = new String(
						Base64.getEncoder().encode(jpgBytes));
				htmlBody.append(
						"<strong>Thumbnail</strong>: <img src=\"data:image/jpg;base64,"
								+ base64 + "\"/><br>");
			} catch (Exception e) {
				String stacktrace = ErrorManager.getStackTrace(e);
				htmlBody.append(
						"<pre>" + HTMLEncoding.encode(stacktrace) + "</pre>br");
			}
		}

		@Override
		public void addComment(String markerName, String comment) {
			setMarker(markerName);
			htmlBody.append("<strong>Comment</strong>: "
					+ HTMLEncoding.encode(String.valueOf(comment)) + "<br>");
		}

		private void setMarker(String markerCode) {
			if (markerCode.equals(currentMarkerCode)) {
				return;
			}
			if (currentMarkerCode != null)
				htmlBody.append("</div>");

			JPEGMarker marker = JPEGMarker.getMarkerForByteCode(markerCode);

			String str = marker != null ? marker.name() : "Unknown";
			str += " (" + markerCode + ")";

			htmlBody.append(
					"<h3>" + HTMLEncoding.encode(str) + " Identified: </h3>");
			htmlBody.append("<div class='indented-section'>");
			currentMarkerCode = markerCode;
		}

		@Override
		public void startFile() {
			htmlBody = new StringBuilder();
		}

		@Override
		public void endFile() {
			if (currentMarkerCode != null)
				htmlBody.append("</div>");

			htmlBody.insert(0, "<html>");
			htmlBody.append("</html>");
			textPane.setText(htmlBody.toString());
		}

		@Override
		public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
			htmlBody.append("<strong>bitsPerPixel</strong>: "
					+ bitsPerPixel + "<br>");
			htmlBody.append("<strong>width</strong>: "
					+ width + "<br>");
			htmlBody.append("<strong>height</strong>: "
					+ height + "<br>");
			htmlBody.append("<strong>numberOfComponents</strong>: "
					+ numberOfComponents + "<br>");
		}

		@Override
		public void processException(Exception e, String markerCode) {
			String str = ErrorManager.getStackTrace(e);
			htmlBody.append(HTMLEncoding.encode(str));
		}

	};

	public JPEGMetaDataDemo() {
		super(URL.class, true, "jpg", "jpeg");

		configurationLabel.setText("Input:");
		exampleLabel.setText("Output:");

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(500, 200));
		examplePanel.add(scrollPane, c);

		textPane.setEditable(false);
		HTMLEditorKit kit = new QHTMLEditorKit();
		textPane.setEditorKit(kit);

		StyleSheet styleSheet = kit.getStyleSheet();

		styleSheet.addRule(
				"body {  padding: 5px; font-family: sans-serif; color: black; background: white; }");

		styleSheet.addRule("h3 { color: #624cab; font: bold 110%;}");
		styleSheet.addRule(
				".indented-section { margin-left: 10px; line-height:1.5; }");
	}

	@Override
	public String getTitle() {
		return "JPEGMetaData Demo";
	}

	@Override
	public String getSummary() {
		return "This demos the JPEGMetaData's ability to parse meta data.";
	}

	@Override
	public URL getHelpURL() {
		return JPEGMetaDataDemo.class.getResource("jpegMetaDataDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "jpeg", "jpg", "thumbnail", "preview", "exif",
				"performance" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JPEGMetaData.class, JPEGMetaDataListener.class };
	}

	@Override
	protected void refreshFile(URL url, String str) {
		if (url == null) {
			if (str.isEmpty()) {
				textPane.setText(
						"<html>Select a file or paste a URL to read the JPEG meta data.</html>");
			} else {
				textPane.setText("<html>Unable to read \""
						+ HTMLEncoding.encode(str) + "\"</html>");
			}
		} else {
			try (InputStream in = url.openStream()) {
				JPEGMetaData.read(in, listener);
			} catch (Exception e) {
				String stacktrace = ErrorManager.getStackTrace(e);
				textPane.setText("<html>Unable to read \""
						+ HTMLEncoding.encode(str) + "<p></p><pre>"
						+ HTMLEncoding.encode(stacktrace) + "</pre></html>");
			}
		}
	}
}