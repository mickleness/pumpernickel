package com.pump.text.html;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

import com.pump.graphics.vector.FillOperation;
import com.pump.graphics.vector.ImageOperation;
import com.pump.graphics.vector.Operation;
import com.pump.graphics.vector.StringOperation;
import com.pump.graphics.vector.VectorGraphics2D;
import com.pump.graphics.vector.VectorImage;
import com.pump.text.html.css.CssColorParser;

import junit.framework.TestCase;

/**
 * This renders a series of HTML and makes sure misc visual features are
 * painted. Most tests check to see that the QHTMLEditorKit produces certain
 * results AND that the default HTMLEditorKit fails to produce those same
 * results. In the unlikely event that the HTMLEditorKit is ever enhanced and
 * some of its shortcomings are addressed: we should remove some of the extra
 * work the QHTMLEditorKit is doing.
 */
public class QHtmlTest extends TestCase {

	static Dimension batDataSize = new Dimension(79, 80);
	/**
	 * A 79x80 PNG of a bat, see
	 * https://openclipart.org/detail/324128/vintage-halloween-bat
	 */
	static String batDataURL = "url('data:image/png;base64,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')";

	static Dimension htmlPaneSize = new Dimension(1000, 1000);

	/**
	 * Test that "darkorchid" is resolved correctly.
	 */
	public void test_named_color_h3_inline() throws Exception {
		//@formatter:off
		String html = "<html>\n" + 
					  "  <h3 style=\"color: darkorchid;font-size: 150%;\">Lorem Ipsum</h3>\n" + 
				      "</html>";
		//@formatter:on
		Color darkorchid = CssColorParser.getNamedColor("darkorchid");

		List<Operation> ops1 = getOperations(true, html);
		assertEquals(darkorchid, ops1.get(1).getContext().getPaint());

		List<Operation> ops2 = getOperations(false, html);
		assertEquals(Color.black, ops2.get(1).getContext().getPaint());
	}

	/**
	 * Test that "darkorchid" is resolved correctly.
	 */
	public void test_named_color_h1_internalSheet() throws Exception {
		//@formatter:off
		String html = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      h1   {color: darkorchid;}\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1 style=\"font-size: 150%;\">Lorem Ipsum</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		Color darkorchid = CssColorParser.getNamedColor("darkorchid");

		List<Operation> ops1 = getOperations(true, html);
		assertEquals(darkorchid, ops1.get(1).getContext().getPaint());

		List<Operation> ops2 = getOperations(false, html);
		assertEquals(Color.black, ops2.get(1).getContext().getPaint());
	}

	/**
	 * Test that a 4-digit hex code with an alpha component is rendered
	 * correctly.
	 */
	public void test_hex_alpha_h3_inline() throws Exception {
		//@formatter:off
		String html = "<html>\n" + 
				"  <h3 style=\"color: #321C;font-size: 150%;\">Lorem Ipsum</h3>\n" + 
				"</html>";
		//@formatter:on
		List<Operation> ops1 = getOperations(true, html);
		assertEquals(204, ops1.get(1).getContext().getColor().getAlpha());

		List<Operation> ops2 = getOperations(false, html);
		assertEquals(255, ops2.get(1).getContext().getColor().getAlpha());
	}

	/**
	 * Test that a 8-digit hex code with an alpha component is rendered
	 * correctly.
	 */
	public void test_hex_alpha_h1_internalSheet() throws Exception {
		//@formatter:off
		String html = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      h1   {color: #00331199;}\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1 style=\"font-size: 150%;\">Lorem Ipsum</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on
		List<Operation> ops1 = getOperations(true, html);
		assertEquals(153, ops1.get(1).getContext().getColor().getAlpha());

		List<Operation> ops2 = getOperations(false, html);
		assertEquals(255, ops2.get(1).getContext().getColor().getAlpha());
	}

	/**
	 * Test a text-shadow for an h1 tag.
	 */
	public void test_text_shadow_h1_internalSheet() throws Exception {
		//@formatter:off
		String html = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      h1   {color:darkorchid; text-shadow: 2px 2px 4px plum;}\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1 style=\"font-size: 150%;\">Lorem Ipsum</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		testTextShadow(html);
	}

	/**
	 * Test a text-shadow for an h3 tag.
	 */
	public void test_text_shadow_h3_inline() throws Exception {
		//@formatter:off
		String html = "<html>\n" + 
				"  <h3 style=\"color:darkorchid; text-shadow: 2px 2px 4px plum;font-size: 150%;\">Lorem Ipsum</h3>\n" + 
				"</html>";
		//@formatter:on

		testTextShadow(html);
	}

	/**
	 * Test a text-shadow for a span tag.
	 */
	public void test_text_shadow_span_inline() throws Exception {
		//@formatter:off
		String html = "<html>\n" + 
				"  <span style=\"color:darkorchid; text-shadow: 2px 2px 4px plum;font-size: 150%;\">Lorem Ipsum</span>\n" + 
				"</html>";
		//@formatter:on

		testTextShadow(html);
	}

	/**
	 * This asserts that HTML produces exactly 3 layers: a background, an image
	 * (the shadow), and text
	 */
	private void testTextShadow(String html) {
		List<Operation> ops1 = getOperations(true, html);

		assertEquals(3, ops1.size());

		// background:
		assertTrue(ops1.get(0) instanceof FillOperation);

		// shadow:
		assertTrue(ops1.get(1) instanceof ImageOperation);

		// text:
		assertTrue(ops1.get(2) instanceof StringOperation);

		List<Operation> ops2 = getOperations(false, html);

		assertEquals(2, ops2.size());

		// background:
		assertTrue(ops2.get(0) instanceof FillOperation);

		// text:
		assertTrue(ops2.get(1) instanceof StringOperation);
	}

	private List<Operation> getOperations(boolean useQHTMLKit, String html) {

		HTMLEditorKit kit = useQHTMLKit ? new QHTMLEditorKit()
				: new HTMLEditorKit();

		JEditorPane p = new JEditorPane();
		p.setEditorKit(kit);
		p.setText(html);

		p.setSize(htmlPaneSize);

		VectorImage vi = new VectorImage();
		VectorGraphics2D vig = vi.createGraphics();
		p.paint(vig);
		vig.dispose();

		return vi.getOperations();
	}

	public void testRepeatBackground_base64img_no_repeat() {
		//@formatter:off
		
		// the same layout, but test a single "no-repeat" argument vs two consecutive "no-repeat" arguments.
		String html1 = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      body {\n"+
				"background-repeat:no-repeat;\n" + 
				"background-image: "+batDataURL+"\n" + 
				" }\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1>LOREM IMPSUM</h1>\n" + 
				"  </body>\n" + 
				"</html>";

		String html2 = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      body {\n"+
				"background-repeat:no-repeat no-repeat;\n" + 
				"background-image: "+batDataURL+"\n" + 
				" }\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1>LOREM IMPSUM</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		for (String html : new String[] { html1, html2 }) {
			List<Operation> ops1 = getOperations(true, html);
			assertEquals(3, ops1.size());

			assertTrue(ops1.get(0) instanceof FillOperation);

			// image:
			assertTrue(ops1.get(1) instanceof ImageOperation);
			ImageOperation io = (ImageOperation) ops1.get(1);
			assertEquals(batDataSize.width, io.getDestRect().width);
			assertEquals(batDataSize.height, io.getDestRect().height);

			// text:
			assertTrue(ops1.get(2) instanceof StringOperation);

			// Swing's default renderer should support no-repeat, but not with a
			// base64-encoded image
			List<Operation> ops2 = getOperations(false, html);
			assertEquals(2, ops2.size());
			assertTrue(ops2.get(0) instanceof FillOperation);
			assertTrue(ops2.get(1) instanceof StringOperation);
		}
	}

	public void testRepeatBackground_base64img_repeat_x() {
		//@formatter:off
		String html = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      body {\n"+
				"background-repeat:repeat-x;\n" + 
				"background-image: "+batDataURL+"\n" + 
				" }\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1>LOREM IMPSUM</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		List<Operation> ops1 = getOperations(true, html);

		ops1.remove(0); // background color
		ops1.remove(ops1.size() - 1); // text

		assertEquals(13, ops1.size());

		for (int a = 0; a < ops1.size(); a++) {
			assertTrue(ops1.get(a) instanceof ImageOperation);
			ImageOperation first = (ImageOperation) ops1.get(0);
			ImageOperation c = (ImageOperation) ops1.get(a);
			assertEquals(first.getDestRect().y, c.getDestRect().y);
		}

		// Swing's default renderer should support repeat-x, but not with a
		// base64-encoded image
		List<Operation> ops2 = getOperations(false, html);
		assertEquals(2, ops2.size());
		assertTrue(ops2.get(0) instanceof FillOperation);
		assertTrue(ops2.get(1) instanceof StringOperation);
	}

	public void testRepeatBackground_base64img_repeat_y() {
		//@formatter:off
		String html = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      body {\n"+
				"background-repeat:repeat-y;\n" + 
				"background-image: "+batDataURL+"\n" + 
				" }\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1>LOREM IMPSUM</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		List<Operation> ops1 = getOperations(true, html);

		ops1.remove(0); // background color
		ops1.remove(ops1.size() - 1); // text

		assertEquals(13, ops1.size());

		for (int a = 0; a < ops1.size(); a++) {
			assertTrue(ops1.get(a) instanceof ImageOperation);
			ImageOperation first = (ImageOperation) ops1.get(0);
			ImageOperation c = (ImageOperation) ops1.get(a);
			assertEquals(first.getDestRect().x, c.getDestRect().x);
		}

		// Swing's default renderer should support repeat-x, but not with a
		// base64-encoded image
		List<Operation> ops2 = getOperations(false, html);
		assertEquals(2, ops2.size());
		assertTrue(ops2.get(0) instanceof FillOperation);
		assertTrue(ops2.get(1) instanceof StringOperation);
	}

	public void testRepeatBackground_base64img_repeat() {
		//@formatter:off
		String html = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      body {\n"+
				"background-repeat:repeat;\n" + 
				"background-image: "+batDataURL+"\n" + 
				" }\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"    <h1>LOREM IMPSUM</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		List<Operation> ops1 = getOperations(true, html);

		ops1.remove(0); // background color
		ops1.remove(ops1.size() - 1); // text

		Area sum = new Area();
		for (int a = 0; a < ops1.size(); a++) {
			assertTrue(ops1.get(a) instanceof ImageOperation);
			ImageOperation c = (ImageOperation) ops1.get(a);
			sum.add(new Area(c.getDestRect()));
		}

		// did our tiles cover everything:
		assertTrue(sum.contains(
				new Rectangle(0, 0, htmlPaneSize.width, htmlPaneSize.height)));

		// Swing's default renderer should support repeat-x, but not with a
		// base64-encoded image
		List<Operation> ops2 = getOperations(false, html);
		assertEquals(2, ops2.size());
		assertTrue(ops2.get(0) instanceof FillOperation);
		assertTrue(ops2.get(1) instanceof StringOperation);
	}
}
