package com.pump.text.html;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.text.html.HTMLEditorKit;

import org.junit.Test;

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
	static String batDataURL = "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE8AAABQCAYAAABYtCjIAAAAAXNSR0IArs4c6QAAAPJlWElmTU0AKgAAAAgABgESAAMAAAABAAEAAAEaAAUAAAABAAAAVgEbAAUAAAABAAAAXgExAAIAAAARAAAAZoKYAAIAAABPAAAAeIdpAAQAAAABAAAAyAAAAAAAAAC5AAAAAQAAALkAAAABd3d3Lmlua3NjYXBlLm9yZwAAQ0MwIFB1YmxpYyBEb21haW4gRGVkaWNhdGlvbiBodHRwOi8vY3JlYXRpdmVjb21tb25zLm9yZy9wdWJsaWNkb21haW4vemVyby8xLjAvAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAAT6ADAAQAAAABAAAAUAAAAACxoqkGAAAACXBIWXMAABxzAAAccwHWg+9QAAADgGlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp0aWZmPSJodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyIKICAgICAgICAgICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICAgICAgICAgICB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICAgICA8ZGM6dGl0bGU+CiAgICAgICAgICAgIDxyZGY6QWx0PgogICAgICAgICAgICAgICA8cmRmOmxpIHhtbDpsYW5nPSJ4LWRlZmF1bHQiPlZpbnRhZ2UgSGFsbG93ZWVuIGJhdDwvcmRmOmxpPgogICAgICAgICAgICA8L3JkZjpBbHQ+CiAgICAgICAgIDwvZGM6dGl0bGU+CiAgICAgICAgIDxkYzpyaWdodHM+CiAgICAgICAgICAgIDxyZGY6QWx0PgogICAgICAgICAgICAgICA8cmRmOmxpIHhtbDpsYW5nPSJ4LWRlZmF1bHQiPkNDMCBQdWJsaWMgRG9tYWluIERlZGljYXRpb24gaHR0cDovL2NyZWF0aXZlY29tbW9ucy5vcmcvcHVibGljZG9tYWluL3plcm8vMS4wLzwvcmRmOmxpPgogICAgICAgICAgICA8L3JkZjpBbHQ+CiAgICAgICAgIDwvZGM6cmlnaHRzPgogICAgICAgICA8eG1wOkNyZWF0b3JUb29sPnd3dy5pbmtzY2FwZS5vcmc8L3htcDpDcmVhdG9yVG9vbD4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+CoKd9cYAAA8xSURBVHgB7Zp70KZjHcfpIOQUWrRZu61FDlFKSu1uWHKMUSmpVZtRDaPRH0yDmgwzmsYYpoTFMs5hUJShhhwaRHJcFrvLrnU+rVOEPp/nub77Xu6e59131/vuLo/vzPe+ftf5ur737zo89/suvdTiwfvp9nX4c3gkfBneCu+FD5VwKuEDcC4US8MPwtfgm7BnoRBiGBwPfw8VpMmHSTsTbgUD6y6TSK+GETDzH41xEYyA8bDELydvhxQmtL4e3LNw8h+CLsdgAsZdUNH+A18odkS8nvguMHgfxgcS6cVQL1IAhRDaJ8MI5p6oJ75Rpf0de1MYuJSb3py8nglrL9ydWb8IFfGVEirif4vtoXMWHAGFnpwX0EroxYcCRMSNsB+FtYC1B5r+DJwIg55exhFh2WKsQehVRqFeKuHNhDcV23R5GoxwPX8io0XrQDFcHkZAPe856Ol7EIx4hgq6JhTv7YOIkCXsyXw7jFh64cfgBnBalf409nZQuA/2/EGSZbgWYjwBI+CD2MHZGEk3/HHJeE9AhMge+KUikvdARTodBr/BMC2HyoElQ+/teQ906YoDoCLlCvNdEwuOIDQvV5p9S3qWf4n2XqD35C53IbYiyTlwFRgcgmF6PDDi9ryA7mFiNejhEAGPNRFkeR+FXQs43kzQ8wJGgMMQQ4Fehf76GANFlvep2BFXoT9uJkj9dqzHnlm6LtX7YQQ6seiwXKXHLVX+lSW9Xv5V0d4x4107V+J4eR5RJPhwCdcnzC8TRZ5U0t/zviJE7V1+oRb+VIvAChbv9DOXl2uR/bMd67FnxNmLeUec2dgeJsLlHYEuw06ZKdgiee1Yjz2z963AvB+DEWe/ooPiRKBPYPttMGXGlTL5mFCivRVk7zq+EsbP9iLipkxOZwW8qlWir0yJ9lYQz9qKacernsf2U5bwZE2ZFbFnQsu9Dj8HRX47t2M99FQc4fXkARgBtzMRZFlGoANIS5ljWiXeu/e1ZDilEua4hjARUZHzCcvQ/VIkvx3roWcmvgdzjlfdg509L2FO58Orct8sOmVfLNGFC1wG3bhwLQ59rYjnh1F/qimg+95IKJKfcDRp+bPmSa0SbaGzBZSkgQVuqCpv43lLdU0bNb0up92pbF1vUdmORfi5fiqM921jIsh+p52yl2Bbzq/T1hOZk/PqxlaZZNqwJ89r0G9gfsZpwk5Mr8tpm279vFHMxQLH4Qv2Z5jLNRhZjNqjIt4VJW8Twi2LrSa2lfk65yZb83bCZujm2rq8R/cWcBRcGdqpF8vHoV8lHob/hHdCP4cruPWFg5I2LhclnKwTt99ZVccji22+K8v56CDiZLgNXBV6wRbOZyB4M97yU0pPguvCfAubXwMK+QicDv8ML4CKGdHi1Yk7+KGE7efFPVB1tHGxHUfGYtIIuB78C3TeP4N+SFAT2wmdh3ZCnc1DR4da6jpox036BlwCbqoW9O8Fr8Bsxs3yCvdHeBDcCNawcwdVL5063/SUMewPKdupLbcPsSt0fL7gb0Bh3lbwSPgP6B/Km3NYkPhVDuBS6L1Hl3bT1Ha5DoeqLWxUMTO47AGmi7yVdqz9/CvBZHg5fL6dNE9A2+oPCp2llXKO1TE2PajO1yN8wcJ9zPF5h9sZeoVxW+oE5xPhdA7nU6/AZ4m7rB+GM6BzOxf+H6yo+24GdeVrYRrW+xx84gnt3HQnrDBJN5wNj4Xrw0ARFKP2nh8Q3w+uAoUCBpZzXN1Ql7XMZ6B9uk/XY3GMjs9xatdOYHq9qmYQ/x3cBY6EtZhE27BjB1ZPpGTNC3bEugFmIHUnSWuGlqnLKfwxcA0Y6CnCky71Z2Jn2StyLdr2xKdAD6sJUNST+h7x62DaMlQo+zas02MrYD3Oe4j7IleETaiRq89xzYOJ0oFK9x0LZZlitqAnugTtuOlhGUwzdGAupaRbfzJ0OdmnOBSaP7eELo+InDEcX/LSzkHEg7UxrobJM7RPx6g4dXpt632J34a9E1wJBjqWL1ix1CTjxRwYFLNWehjxS6Gd1m8sg+gWOpFa8POIB9/CSHsR8IRkEu5d8vUeDzHLjoVic+jBkPqOyXLdxpH0LFvjv4Tu876gzaBw7x80qHqWmY3+CWbAGdBAQicW0f3/O7EqnAWtHy99CvtQ+CN4NzQvwuqZvlBf7AxoXkQdyBgsE69zDHp3+vg0tojHt2OD9IyAvqkZ0IEM5E3Xk8rA9Zh1odgeRtTaQ1Mvec9S7qtWAAdD818uYcrOL3QPtMzZUFwMjfsCPg/FkIhnwxHQk8hOF1Q860QM3/hHoBgPzQstk4mmn6+QJkbDLNf+9rW0lTDtTaO+e5qIeJYZ10rpm2OJDl6QjdMT6VFopwsjYCbyIPW3hV8rbT1EuBuMOHtinwv1DLEpnA3tt5OXmt6JKetLiYdhvuW/8MebAOIg7ViHZ5TvkDWgJPccBykiqF4QTzDdMuYZNuHSUPRR8EpoPfEiHAFzXViv2G7iU6AirwIVoz7MiHaFbWe+X8e+Edq/QnqvFS5/91qRsbRjg/j02BafhHaigA4idqe3bpqTrU+5lFPAeG72w+TV8dq2v5RJaP+Wkdr1eOJ1J5Mulm0HrRd7M7ZtuBI8vcV897y8iXbxgT/jZe5VseMBvrmp0PvcStCDxXLDYfpzciIvwdDBO9l4s2VMN+7EDY0rmmH6w5x3ctp+xmO67flSrGuebR4LRcr5k1QvFpa3L+F4hgQRYSyt24nXi19D95HVYA0H6XLzU5f3qDnQOtKBxm6GTqSZ1oxbJleb5BnPXpq0tHULeUHEX58E91LL+uLXgiJzbMcG8ZmO96ZNO/1Co20F823n7dbZKxI5EGbZ9SdgJt8ptJ6eZJ7iTIZbwzWh4xkP94MXwJS7ElvEE7UnwLR/D3ZEs8yQIKKsQeublB7cIxQ1eSW5FZjmoOp9xL3lSejAM7lMYn5hLfj51Pf0FfZxBrwNngqDL2PMhrabS3D2vEkl3Twv/sJtYUhRixRPHEiHDsx9RvitzUHXYgxUOO+HW8MaRxNJ/XhZxuZ2cgIcWyrkKuIemDqHlbz6JZekwQ9q91+Q1hVeWv8OWAvoBu+e5bKWChvPjMizSFMMEW93wtNhhPA6I8yPgK0EHvYbXIuROruUxGb5lF1iwiyNs8vg3egjTiZThwqpsKZNgsKlFw+aiJ3yM7E96UWEyovOizPPg2wGtJ5bSA6L1CFpycb1DM/Bv1xCrzh3QTfvB+GjMKIYzoUjoYhw2rfClDvKBOAeqFhN5MVtTUbqXFMKvSOEywROqyZwEvbq0CWoMN78ja8Lz4JOVA/9LBT5ZbA9dkTwJXh5F92Wn6KK02HqHdJK6TttS3TJDCLet8sELp7PMF2iM0tZrzoi4lyNHRHOMwPYfievi2cNIz+nvfe89aFIfjvWz3PABftpY2GzMjH3MeHeJ/Q480LHqBB6nPuecG8UhnvBcdC7ngJmyVrPeBMRfAcycujcgH1vKdipTrONxR6P5+3PSNzXXJqi0wuN0LuR7y+ZfMLyyjMDOmF5OhQRqB3re9ZtK1jq5YqSMfXVWMKtlRmf3iYiUjvW/RkRjqSIAui97nUjoEh+O9b3XKaYuxNGOMPsod1E72thCbJqsWq72xAVJRfsrbGduMvZ8GAochi0Y31P208f7q8R7299RbqKXhVZskwn1M1TmiON5/j7eA6MAFdXBbu1FVE9iev75K6lbtqumnr3mJm8M7oMRjjvhWubCPpbdhH1HMql7p2tWu1HvLJKeneYtXCnMiUn7084Q09NkX2zHXvrM6LuTLJ14nn5OVa3/9aa7/BYvZzOKJPP97dcbOsyzenG4zxJZ5T6Cng6FO+4E7Y97Pk/402KcyV00i+W8LeEQnG6LTnT41UnYltf1r9j33XiOel4k78E/lUm7f7m5M+CQTwr8TqM+PuSaD0v2Yb1Fxei7x64P0WQDbGnQycc4S7EDuJVidehP+fEVrAW7vxWavePBiX7nRUoWDzFkcdbnHjuciebUdCfcGlnNGVfgLYhH4F6suivfrvEAJ950wMsPujFskRteBQ8AzrZfL/Tzk8ozH43+bTl9eVBGOGewh4Dhd49qFDARbV5uqfVm3kmorflJPXnlhPXc/xNKxxjtxdd75P+3HoaRjgPmY2hiLjt2Nt82ulaVRu6s29msIW0H9u0/WbbbuD/hk72DRiP86fTOlA4pm7CpV3LbQtzj7M9PzqsB0WWczs2CM81aONZeBxcvdGeA7ZD31a3gTeqzIsqlnWsaxvN+qb5OekWGA/JpPWU/WDgOGyvE2pPmkSBtGU4G44plexv0OGgroF2NgceBXX7TvANOxEH3B8t0xSLpFbdCYTe0abBTPT1ynYsWWK20fRSklpw3DlRTTgJ2l5+edyH/VEoaoHbKYP49HP21TCTMbwJHg2/AzeHC3M6OUGvGz+E58A7Yd1Hbf+BvHEw6DZh26zHMoK4y9u2sl/eg50Pnd3aocjbQwbiHqO3TIEupU6YSqLe8hicCWfDFaB/pXoVWt8rxUi4InQfHQ5Hw+VgJ9jWufAUeEdVwLZeq+KaeqCemHQF3B/+Ctpf4D1uX+i9UOEc25CifpMH0JN7jm/SJZBTr/aSt2M7qSvgPrCetC9S0erlrq0ApgeOdSKcDutxPEd8EgzqOkkbstCBZuDrYV8CMzjfnkLqob750PSU6RZ6GN0Hp8B94BhYQzHSr+kRsbnclidvb+iSbPZ1JmnDoYiHtmND+HSgTdSuvhOZh8MtSiHFs44DdALa98NZUAH0Uj3rITgdzoBO1uWpNwd6hW3YXtoxLweHdvBFDK8ye8DRSSzhNMKfwKtKvB57SVr0gZOrvcHB3w7rN67XOdl74S9g06NIWiisTK0t4ZHQA+YNWPer/SQ8BGYv9UU45kWKTp6XASieg3KJCst+H06EY2ETL5AwE94G4216pMtWjwwV3Ul77/LAGQE3gKOg24VcC3aC7Z4GJ8NnSgFFiweXpCUnUDT3pRou4yOgXtf0imZcz1EwT+K50J9NLm2XcZZps04dt+xlcE/oWALHVMeTvsjCBe3cPUVPdHJCD/oU3B7uCDeCetPbgaK6R94I9eKL4P0wcAx6mi9lsWJBxXOw1nGpKGCWNGYLo3i67FyGG8Jh0Hug4TpQb1Gcp+Ac+ARUKO3H4d3wkWK/RBikzyVCtAzqfxltLlAWEUDiAAAAAElFTkSuQmCC')";

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

	interface TextPaneFormatter {
		JComponent format(JEditorPane p);

	}

	/**
	 * Return the visible Operations used to render certain HTML.
	 * <p>
	 * If an Operation would not be visible (because of clipping) then it is not
	 * included in the returned list.
	 * 
	 * @param useQHTMLKit
	 * @param html
	 * @return
	 */
	private List<Operation> getOperations(boolean useQHTMLKit, String html) {
		return getOperations(useQHTMLKit, html, null);
	}

	private List<Operation> getOperations(boolean useQHTMLKit, String html,
			TextPaneFormatter textPaneFormatter) {

		HTMLEditorKit kit = useQHTMLKit ? new QHTMLEditorKit()
				: new HTMLEditorKit();

		JEditorPane p = new JEditorPane();
		p.setEditorKit(kit);
		p.setText(html);

		p.setPreferredSize(htmlPaneSize);
		p.setSize(htmlPaneSize);

		JComponent jc = textPaneFormatter == null ? p
				: textPaneFormatter.format(p);

		VectorImage vi = new VectorImage();
		VectorGraphics2D vig = vi.createGraphics();
		jc.paint(vig);
		vig.dispose();

		List<Operation> returnValue = vi.getOperations();
		Iterator<Operation> iter = returnValue.iterator();
		while (iter.hasNext()) {
			Operation op = iter.next();
			if (op.getBounds() == null)
				iter.remove();
		}
		return returnValue;
	}

	public void testBackgroundRepeat_base64img_no_repeat() {
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
				"    <h1>LOREM IPSUM</h1>\n" + 
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
				"    <h1>LOREM IPSUM</h1>\n" + 
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

	public void testBackgroundRepeat_base64img_repeat_x() {
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
				"    <h1>LOREM IPSUM</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		List<Operation> ops1 = getOperations(true, html);

		ops1.remove(0); // background color
		ops1.remove(ops1.size() - 1); // text

		assertEquals(14, ops1.size());

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

	public void testBackgroundRepeat_base64img_repeat_y() {
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
				"    <h1>LOREM IPSUM</h1>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		List<Operation> ops1 = getOperations(true, html);

		ops1.remove(0); // background color
		ops1.remove(ops1.size() - 1); // text

		assertEquals(14, ops1.size());

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

	public void testBackgroundRepeat_base64img_repeat() {
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
				"    <h1>LOREM IPSUM</h1>\n" + 
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

	/**
	 * Make sure when no position is specified that the image is in the
	 * top-left. This test was added as a result of an observed failure.
	 */
	public void testBackgroundPosition_base64img_none() {
		//@formatter:off
		String html = "<html>\n" + 
				"  <head>\n" + 
				"    <style>\n" + 
				"      body {\n" + 
				"background-repeat: no-repeat;\n"+
				"background-image: "+batDataURL+"\n" + 
				" }\n" + 
				"    </style>\n" + 
				"  </head>\n" + 
				"  <body>\n" + 
				"  </body>\n" + 
				"</html>";
		//@formatter:on

		List<Operation> ops = getOperations(true, html);

		assertEquals(2, ops.size());

		// page background:
		assertTrue(ops.get(0) instanceof FillOperation);
		// h1 background:
		assertTrue(ops.get(1) instanceof ImageOperation);

		ImageOperation io = (ImageOperation) ops.get(1);
		assertEquals(0, io.getDestRect().x);
		assertEquals(0, io.getDestRect().y);

		// we can't test the Swing renderer using a base64 image
	}

	/**
	 * Test that h1 tags support background-colors. This test was added as a
	 * result of an observed failure.
	 */
	public void testBackground_color_h1() {
		//@formatter:off
		String html = "<html>\n" + 
				"  <body> \n" + 
				"    <h1 style=\"background-color:#F0F;font-size: 30pt;\">LOREM IPSUM</h1>\n" + 
				"  </body> \n" + 
				"</html> ";
		//@formatter:on

		for (boolean useNewKit : new boolean[] { true, false }) {
			List<Operation> ops = getOperations(useNewKit, html);

			assertEquals(3, ops.size());

			// page background:
			assertTrue(ops.get(0) instanceof FillOperation);
			// h1 background:
			assertTrue(ops.get(1) instanceof FillOperation);
			assertTrue(ops.get(2) instanceof StringOperation);

			assertEquals(new Color(255, 0, 255),
					ops.get(1).getContext().getPaint());
		}
	}

	/**
	 * Test that an overflowing div clips its contents as expected when
	 * "overflow:hidden" is used.
	 */
	public void testOverflow_hidden_div() {
		//@formatter:off
		String html = "<html>\n" + 
				"  <body> \n" + 
				"    <div style=\"overflow:hidden;font-size: 30pt;height:30px;width:40px;background-color:#6FF;\">little baby buggy bumpers</div>\n" + 
				"  </body> \n" + 
				"</html> ";
		//@formatter:on

		List<Operation> ops1 = getOperations(true, html);

		assertEquals(3, ops1.size());

		// page background:
		assertTrue(ops1.get(0) instanceof FillOperation);
		// div background:
		assertTrue(ops1.get(1) instanceof FillOperation);
		assertTrue(ops1.get(2) instanceof StringOperation);

		FillOperation f = (FillOperation) ops1.get(1);
		Rectangle r = f.getBounds().getBounds();
		assertEquals(40, r.width);
		assertEquals(30, r.height);

		r = ops1.get(2).getBounds().getBounds();
		assertTrue(r.getWidth() <= 40);
		assertTrue(r.getHeight() <= 30);

		// the old Swing should ignore "hidden":
		List<Operation> ops2 = getOperations(false, html);

		// we have multiple lines of text
		assertEquals(3, ops2.size());
		assertTrue(ops2.get(0) instanceof FillOperation);
		assertTrue(ops2.get(1) instanceof FillOperation);
		assertTrue(ops2.get(2) instanceof StringOperation);
		f = (FillOperation) ops2.get(1);
		r = f.getBounds().getBounds();
		assertFalse(r.width == 40);
		assertFalse(r.height == 30);

		r = ops2.get(2).getBounds().getBounds();
		assertTrue(r.getWidth() > 40);
	}

	/**
	 * This is derived from testOverflow_hidden_div, except when we use spans we
	 * should NOT successfully clip anything. (When testing in chrome: spans
	 * don't clip but divs do.)
	 */
	public void testOverflow_hidden_span() {
		//@formatter:off
		String html = "<html>\n" + 
				"  <body> \n" + 
				"    <span style=\"overflow:hidden;font-size: 30pt;height:30px;width:40px;background-color:#6FF;\">little baby buggy bumpers</span>\n" + 
				"  </body> \n" + 
				"</html> ";
		//@formatter:on

		for (boolean useNewKit : new boolean[] { true, false }) {
			List<Operation> ops = getOperations(useNewKit, html);

			assertEquals(3, ops.size());

			// page background:
			assertTrue(ops.get(0) instanceof FillOperation);
			// div background:
			assertTrue(ops.get(1) instanceof FillOperation);
			assertTrue(ops.get(2) instanceof StringOperation);

			FillOperation f = (FillOperation) ops.get(1);
			Rectangle r = f.getBounds().getBounds();
			assertTrue(r.getWidth() > 40);

			r = ops.get(2).getBounds().getBounds();
			assertTrue(r.getWidth() > 40);
		}
	}

	/**
	 * Test that using "background-attachment:fixed" vs
	 * "background-attachment:scroll" positions a background image in a
	 * JScrollPane appropriately. (When the property is "fixed" the image should
	 * render in the location, and when it is "scroll" the background image
	 * should move.)
	 */
	public void testBackgroundAttachment_base64img() {
		class ScrollPaneFormatter implements TextPaneFormatter {
			int xOffset, yOffset;

			public ScrollPaneFormatter(int xOffset, int yOffset) {
				this.xOffset = xOffset;
				this.yOffset = yOffset;
			}

			@Override
			public JComponent format(JEditorPane p) {
				JScrollPane scrollPane = new JScrollPane(p,
						JScrollPane.VERTICAL_SCROLLBAR_NEVER,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				scrollPane.getViewport()
						.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
				Dimension d = new Dimension(400, 200);
				scrollPane.setSize(d);
				scrollPane.setBorder(null);
				scrollPane.getViewport().setBorder(null);
				scrollPane.doLayout();
				scrollPane.getViewport()
						.setViewPosition(new Point(xOffset, yOffset));

				return scrollPane;
			}
		}

		for (String attachmentProperty : new String[] { "fixed", "scroll" }) {

			//@formatter:off
			String html = "<html>\n" + 
					"  <head>\n" + 
					"    <style>\n" + 
					"      body { background-color: #387;\n" + 
					"      background-repeat:no-repeat;\n" + 
					"      background-attachment:"+attachmentProperty+";\n" + 
					"      background-image:"+batDataURL+"; \n" + 
					"}\n" + 
					"      h1   { color: rgba(0,240,0,1); font-weight: bold;}\n" + 
					"    </style>\n" + 
					"  </head>\n" + 
					"  <body>\n" + 
					"    <h1 style=\"font-size: 100pt;\">LOREM IPSUM</h1>\n" + 
					"  </body>\n" + 
					"</html>";
			//@formatter:on

			Collection<Integer> imageYs = new HashSet<>();
			Collection<Double> string1Ys = new HashSet<>();
			for (int y = 0; y < 500; y += 50) {
				ScrollPaneFormatter formatter = new ScrollPaneFormatter(0, y);

				List<Operation> ops = getOperations(true, html, formatter);

				assertEquals(5, ops.size());

				// scrollpane/page background:
				assertTrue(ops.get(0) instanceof FillOperation);
				assertTrue(ops.get(1) instanceof FillOperation);
				assertTrue(ops.get(2) instanceof ImageOperation);
				// two lines of text:
				assertTrue(ops.get(3) instanceof StringOperation);
				assertTrue(ops.get(4) instanceof StringOperation);

				ImageOperation io = (ImageOperation) ops.get(2);
				Rectangle imageRect = io.getContext().getTransform()
						.createTransformedShape(io.getDestRect()).getBounds()
						.getBounds();

				imageYs.add(imageRect.y);

				StringOperation so = (StringOperation) ops.get(3);
				Point2D stringPos = so.getContext().getTransform().transform(
						new Point2D.Float(so.getX(), so.getY()), null);
				string1Ys.add(stringPos.getY());
			}

			// this is the main event: did our image always paint at the same
			// position
			if ("fixed".equals(attachmentProperty)) {
				assertEquals(1, imageYs.size());
			} else {
				assertEquals(10, imageYs.size());
			}

			// make sure the text is scrolling; this proves our viewport was
			// changing each iteration.
			assertEquals(10, string1Ys.size());
		}
	}

	/**
	 * This identifies a bug related line wrapping.
	 * <p>
	 * The QInlineView is cloned as line wrapping is calculated. In earlier
	 * versions of the QViewHelper class we kept a reference to the original
	 * (uncloned) QInlineView. So painting the newly wrapped QInlineView
	 * actually invoked the old unwrapped QInlineView. The end result for the
	 * user is the text was printed twice.
	 */
	public void testLineWrapping() {

		//@formatter:off
		String html = "<html>\n"
				+ "  <body>\n"
				+ "    <h1 style=\"font-size: 100pt;\">LOREM IPSUM</h1>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		TextPaneFormatter formatter = new TextPaneFormatter() {

			@Override
			public JComponent format(JEditorPane p) {
				p.setSize(new Dimension(540, 540));
				return p;
			}
		};
		List<Operation> ops = getOperations(true, html, formatter);
		int charCtr = 0;
		for (Operation op : ops) {
			if (op instanceof StringOperation) {
				StringOperation strOp = (StringOperation) op;
				charCtr += strOp.getString().length();
			}
		}
		assertEquals("LOREM IPSUM".length(), charCtr);
	}

	/**
	 * Text shadows should always render as if the text was 100% opaque.
	 * <p>
	 * This one surprised me. (I assumed the shadow's alpha should be multiplied
	 * by the text's alpha?) But if we assume "correct behavior" is defined as
	 * "what existing browsers already do", then this test checks for the
	 * correct behavior
	 */
	public void testTextShadowWithTransparentText() throws Exception {
		// render text in purple (no shadow)
		String plainHtml = "<html>\n" + "  <body>\n"
				+ "    <h1 style=\"font-size: 100pt; color: #F0F;\">LOREM IPSUM</h1>\n"
				+ "  </body>\n" + "</html>";

		// render text transparently, but apply a purple (unblurred) shadow:
		String transparentHtml = "<html>\n" + "  <body>\n"
				+ "    <h1 style=\"font-size: 100pt; color: transparent; text-shadow: 0px 0px 0px rgba(255,0,255,1);\">LOREM IPSUM</h1>\n"
				+ "  </body>\n" + "</html>";

		// these two snippets of HTML should visually be the same

		BufferedImage bi1 = getImage(transparentHtml);
		BufferedImage bi2 = getImage(plainHtml);
		assertImageEquals(bi1, bi2, 0);

		List<Operation> ops1 = getOperations(true, plainHtml);
		List<Operation> ops2 = getOperations(true, transparentHtml);

		assertEquals(2, ops1.size());
		assertEquals(2, ops2.size());

		// paint using a shadow (BufferedImage)
		assertTrue(ops2.get(0) instanceof FillOperation);
		assertTrue(ops2.get(1) instanceof ImageOperation);

		// don't paint using a shadow:
		assertTrue(ops1.get(0) instanceof FillOperation);
		assertFalse(ops1.get(1) instanceof ImageOperation);
	}

	/**
	 * At one point a bug made an inner view inherit the border of its parent.
	 * The correct behavior is for views to NOT inherit their parent's border.
	 */
	public void testSingleBorder() {
		String html = "<html>\n" + "  <body>  \n"
				+ "    <p style=\"border: solid red 7px;\">Lorem Ipsum</div>\n"
				+ "  </body>\n" + "</html>";

		List<Operation> ops = getOperations(true, html);
		assertEquals(3, ops.size());
		assertEquals(Color.red,
				((FillOperation) ops.get(1)).getContext().getPaint());
	}

	/**
	 * Test that when giving a nested div both the outer and inner div show the
	 * appropriate CSS border.
	 */
	public void testSelector_nestedDiv() {
		//@formatter:off
		String html = "<html>\n"
				+ "  <head>\n"
				+ "    <style>\n"
				+ "      div   { border: 10px solid #ff0000; }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <div>\n"
				+ "      <div style=\"font-size: 10pt;\">LOREM IPSUM</div>\n"
				+ "    </div>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		List<Operation> ops = getOperations(true, html);
		assertEquals(4, ops.size());

		// the white background:
		assertTrue(ops.get(0) instanceof FillOperation);
		assertEquals(Color.white, ops.get(0).getContext().getPaint());

		// the outer div border:
		assertTrue(ops.get(1) instanceof FillOperation);
		assertEquals(Color.red, ops.get(1).getContext().getPaint());

		// the inner div border:
		assertTrue(ops.get(2) instanceof FillOperation);
		assertEquals(Color.red, ops.get(2).getContext().getPaint());

		// the inner div border:
		assertTrue(ops.get(3) instanceof StringOperation);
	}

	public void testSelector_nestedList() {
		//@formatter:off
		String html = "<html>\n"
				+ "  <head>\n"
				+ "    <style>\n"
				+ "        li { list-style-type: none;}\n"
				+ "        li div { color: red; border: 10px solid red; }\n"
				+ "        ul li div { border: 10px solid green; }\n"
				+ "        ul li div { border: 10px solid blue; }\n"
				+ "        div { border: 10px solid gray; }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <div>div</div>\n"
				+ "    <ul>\n"
				+ "      <li>\n"
				+ "        <div>li div</div>\n"
				+ "      </li>\n"
				+ "      <li>\n"
				+ "        <ul>\n"
				+ "          <li>\n"
				+ "            <div>li li div</div>\n"
				+ "          </li>\n"
				+ "        </ul>\n"
				+ "      </li>\n"
				+ "    </ul>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		List<Operation> ops = getOperations(true, html);
		assertEquals(7, ops.size());

		// the white background:
		assertTrue(ops.get(0) instanceof FillOperation);
		assertEquals(Color.white, ops.get(0).getContext().getPaint());

		// the outer div border:
		assertTrue(ops.get(1) instanceof FillOperation);
		assertEquals(Color.gray, ops.get(1).getContext().getPaint());

		assertTrue(ops.get(2) instanceof StringOperation);

		// the first (simple) list
		assertTrue(ops.get(3) instanceof FillOperation);
		assertEquals(Color.blue, ops.get(3).getContext().getPaint());

		assertTrue(ops.get(4) instanceof StringOperation);

		// the list-within-the-list
		assertTrue(ops.get(5) instanceof FillOperation);
		assertEquals(Color.blue, ops.get(5).getContext().getPaint());

		assertTrue(ops.get(6) instanceof StringOperation);
	}

	/**
	 * Test a basic unordered list.
	 * <p>
	 * At one point any reference to a list tag failed (with a NPE) because
	 * antialiasing hints were undefined.
	 */
	public void testList() {
		//@formatter:off
		String html = "<html>\n"
				+ "  <body>\n"
				+ "    Outside\n"
				+ "    <ul>\n"
				+ "      <li>Item 1</li>\n"
				+ "      <li>Item 2</li>\n"
				+ "      <li>Item 3</li>\n"
				+ "    </ul>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		List<Operation> ops = getOperations(true, html);
		assertEquals(8, ops.size());

		// the white background:
		assertTrue(ops.get(0) instanceof FillOperation);
		assertEquals(Color.white, ops.get(0).getContext().getPaint());

		assertTrue(ops.get(1) instanceof StringOperation);
		assertEquals("Outside", ((StringOperation) (ops.get(1))).getString());
		float outerX = ((StringOperation) (ops.get(1))).getX();

		// ops.get(2) is a bullet shape

		assertTrue(ops.get(3) instanceof StringOperation);
		StringOperation li1 = (StringOperation) (ops.get(3));
		assertEquals("Item 1", li1.getString());
		assertTrue(li1.getX() > outerX);

		// ops.get(4) is a bullet shape

		assertTrue(ops.get(5) instanceof StringOperation);
		StringOperation li2 = (StringOperation) (ops.get(5));
		assertEquals("Item 2", li2.getString());
		assertTrue(li2.getX() > outerX);

		// ops.get(6) is a bullet shape

		assertTrue(ops.get(7) instanceof StringOperation);
		StringOperation li3 = (StringOperation) (ops.get(7));
		assertEquals("Item 3", li3.getString());
		assertTrue(li3.getX() > outerX);
	}

	/**
	 * Confirm that the background of 200x200 div with a border radius of 50%
	 * resembles a circle.
	 */
	@Test
	public BufferedImage testBorderRadius() {
		//@formatter:off
		String html = "<html>\n"
				+ "<body>\n"
				+ "<div style=\"border-radius: 50%; width: 200px; height: 200px; background-color: #F0B;\"></div>\n"
				+ "</body>\n"
				+ "</html>";
		//@formatter:on

		BufferedImage bi1 = getImage(html);

		BufferedImage bi2 = new BufferedImage(1000, 1000,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi2.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.white);
		g.fillRect(0, 0, bi2.getWidth(), bi2.getHeight());
		g.setColor(new Color(0xff00bb));
		g.fill(new Ellipse2D.Float(0, 0, 200, 200));
		g.dispose();

		assertImageEquals(bi1, bi2, 0);

		return bi1;
	}

	/**
	 * This tests an ID selector (in this case "myDIV"). The actual style here
	 * is just another way of writing testBorderRadius().
	 */
	public void testSelector_plainID() {
		//@formatter:off
		String html = "<html>\n" 
				+ "  <head>\n"
				+ "    <style>\n" 
				+ "      #myDIV { \n" 
				+ "        border-radius: 50%;\n"
				+ "        width: 200px;\n" 
				+ "        height: 200px;\n"
				+ "        background-color: #F0B;\n" 
				+ "      }\n" 
				+ "    </style>  \n"
				+ "  </head>\n" 
				+ "  <body>\n" 
				+ "    <div id=\"myDIV\"></div>\n"
				+ "  </body>\n" 
				+ "</html>\n";
		//@formatter:on

		BufferedImage bi1 = getImage(html);
		BufferedImage bi2 = testBorderRadius();
		assertImageEquals(bi1, bi2, 0);
	}

	/**
	 * When there are competing constituent attributes to define a border: make
	 * sure we use the last one.
	 */
	public void testBorderConstituentOrder_border() {
		//@formatter:off
		String html = "<html>\n"
				+ "  <head>\n"
				+ "    <style>\n"
				+ "      body { \n"
				+ "             border-color: #00ff22;\n"
				+ "             border-top-color: #ffff02;\n"
				+ "             border: solid red;\n"
				+ "      }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <div>LOREM IPSUM</div>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		List<Operation> ops = getOperations(true, html);
		assertEquals(3, ops.size());
		assertTrue(ops.get(1) instanceof FillOperation);
		assertEquals(0xffff0000, ops.get(1).getContext().getColor().getRGB());
	}

	/**
	 * When there are competing constituent attributes to define a border: make
	 * sure we use the last one.
	 */
	public void testBorderConstituentOrder_border_color() {
		//@formatter:off
		String html = "<html>\n"
				+ "  <head>\n"
				+ "    <style>\n"
				+ "      body { \n"
				+ "             border: solid red;\n"
				+ "             border-top-color: #ffff02;\n"
				+ "             border-color: #00ff22;\n"
				+ "      }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <div>LOREM IPSUM</div>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		List<Operation> ops = getOperations(true, html);
		assertEquals(3, ops.size());
		assertTrue(ops.get(1) instanceof FillOperation);
		assertEquals(0xff00ff22, ops.get(1).getContext().getColor().getRGB());
	}

	/**
	 * When there are competing constituent attributes to define a border: make
	 * sure we use the last ones.
	 */
	public void testBorderConstituentOrder_border_top_color() {
		//@formatter:off
		String html = "<html>\n"
				+ "  <head>\n"
				+ "    <style>\n"
				+ "      body { \n"
				+ "             border: solid red;\n"
				+ "             border-color: #00ff22;\n"
				+ "             border-top-color: #ffff02;\n"
				+ "      }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <div>LOREM IPSUM</div>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		List<Operation> ops = getOperations(true, html);
		assertEquals(4, ops.size());
		assertTrue(ops.get(1) instanceof FillOperation);
		assertTrue(ops.get(2) instanceof FillOperation);

		// the exact order they're painted in might not match the css above,
		// but just the fact that we have two colors indicates the css was
		// parsed correctly
		Collection<Integer> rgbs = new HashSet<>();
		rgbs.add(ops.get(1).getContext().getColor().getRGB());
		rgbs.add(ops.get(2).getContext().getColor().getRGB());

		assertTrue(rgbs.contains(0xff00ff22));
		assertTrue(rgbs.contains(0xffffff02));
	}

	/**
	 * This tests "width: max-content" and "width: min-content".
	 */
	public void testWidth() {
		//@formatter:off
		String maxContent = "<html>\n"
				+ "  <head>\n"
				+ "    <style>\n"
				+ "      h1   { width: max-content; background-color: red; }\n"
				+ "    </style>\n"
				+ "  </head>\n"
				+ "  <body>\n"
				+ "    <h1>LOREM  IPSUM</h1>\n"
				+ "  </body>\n"
				+ "</html>";
		//@formatter:on

		// first test the orig Swing implementation:
		{
			List<Operation> ops = getOperations(false, maxContent);

			assertEquals(3, ops.size());

			FillOperation bodyBackground = (FillOperation) ops.get(0);
			FillOperation h1Background = (FillOperation) ops.get(1);

			assertEquals(1000, bodyBackground.getBounds().getBounds().width);

			// it didn't recognize "max-content":
			assertTrue(h1Background.getBounds().getBounds().width > 900);
		}

		// our new implementation should use smaller width:
		{
			List<Operation> maxContentOps = getOperations(true, maxContent);

			{
				assertEquals(3, maxContentOps.size());

				FillOperation bodyBackground = (FillOperation) maxContentOps
						.get(0);
				FillOperation h1Background = (FillOperation) maxContentOps
						.get(1);

				assertEquals(1000,
						bodyBackground.getBounds().getBounds().width);

				// ... we should support "max-content":
				assertTrue(h1Background.getBounds().getBounds().width < 200);
			}

			// and now a little extra test: let's compare the min-content
			// against the max-content. Since we're using two words ("LOREM
			// IPSUM") the min-content should produce two wrapping lines of
			// text.

			//@formatter:off
			String minContent = "<html>\n"
					+ "  <head>\n"
					+ "    <style>\n"
					+ "      h1   { width: min-content; background-color: red; }\n"
					+ "    </style>\n"
					+ "  </head>\n"
					+ "  <body>\n"
					+ "    <h1>LOREM  IPSUM</h1>\n"
					+ "  </body>\n"
					+ "</html>";
			//@formatter:on

			List<Operation> minContentOps = getOperations(true, minContent);

			// 2 lines of text now (before we had 1)
			assertEquals(4, minContentOps.size());

			FillOperation h1BackgroundMin = (FillOperation) minContentOps
					.get(1);
			FillOperation h1BackgroundMax = (FillOperation) maxContentOps
					.get(1);

			// is our "min-content" narrower than our max-content
			assertTrue(h1BackgroundMin.getBounds()
					.getBounds().width < h1BackgroundMax.getBounds()
							.getBounds().width);
		}
	}

	/**
	 * This tests that margins of "auto" can be used to left-align, right-align
	 * or center-align h1 views.
	 */
	public void testMargin_auto() {
		{
			//@formatter:off
			String maxContent = "<html>\n"
					+ "  <head>\n"
					+ "    <style>\n"
					+ "      h1   { width: max-content; background-color: red; margin-right:auto;}\n"
					+ "    </style>\n"
					+ "  </head>\n"
					+ "  <body>\n"
					+ "    <h1>LOREM  IPSUM</h1>\n"
					+ "  </body>\n"
					+ "</html>";
			//@formatter:on

			List<Operation> ops = getOperations(true, maxContent);
			assertEquals(3, ops.size());

			Rectangle h1Background = ops.get(1).getBounds().getBounds();
			assertTrue(h1Background.getMaxX() < 200);
		}

		{
			//@formatter:off
			String maxContent = "<html>\n"
					+ "  <head>\n"
					+ "    <style>\n"
					+ "      h1   { width: max-content; background-color: red; margin-left:auto;}\n"
					+ "    </style>\n"
					+ "  </head>\n"
					+ "  <body>\n"
					+ "    <h1>LOREM  IPSUM</h1>\n"
					+ "  </body>\n"
					+ "</html>";
			//@formatter:on

			List<Operation> ops = getOperations(true, maxContent);
			assertEquals(3, ops.size());

			Rectangle h1Background = ops.get(1).getBounds().getBounds();
			assertTrue(h1Background.getMinX() > htmlPaneSize.width - 200);
		}

		{
			//@formatter:off
			String maxContent = "<html>\n"
					+ "  <head>\n"
					+ "    <style>\n"
					+ "      h1   { width: max-content; background-color: red; margin-left:auto; margin-right:auto}\n"
					+ "    </style>\n"
					+ "  </head>\n"
					+ "  <body>\n"
					+ "    <h1>LOREM  IPSUM</h1>\n"
					+ "  </body>\n"
					+ "</html>";
			//@formatter:on

			List<Operation> ops = getOperations(true, maxContent);
			assertEquals(3, ops.size());

			Rectangle h1Background = ops.get(1).getBounds().getBounds();
			assertTrue(Math.abs(
					h1Background.getCenterX() - htmlPaneSize.width / 2) < 5);
		}
	}

	/**
	 * This checks that if there are competing instructions for "margin" and
	 * "margin-right" that we respect the last instruction.
	 */
	public void testMarginConstituentOrder() {

		// this should be left-aligned:
		{
			//@formatter:off
			String maxContent = "<html>\n"
					+ "  <head>\n"
					+ "    <style>\n"
					+ "      h1   { width: max-content; background-color: red; "
					+ "             margin: 0; "
					+ "             margin-right:auto;}\n"
					+ "    </style>\n"
					+ "  </head>\n"
					+ "  <body>\n"
					+ "    <h1>LOREM  IPSUM</h1>\n"
					+ "  </body>\n"
					+ "</html>";
			//@formatter:on

			List<Operation> ops = getOperations(true, maxContent);
			assertEquals(3, ops.size());

			Rectangle h1Background = ops.get(1).getBounds().getBounds();
			assertTrue(h1Background.getMaxX() < 200);
		}

		// this should be centered
		{
			//@formatter:off
			String maxContent = "<html>\n"
					+ "  <head>\n"
					+ "    <style>\n"
					+ "      h1   { width: max-content; background-color: red; "
					+ "             margin-right:auto;\n"
					+ "             margin: 0 }"
					+ "    </style>\n"
					+ "  </head>\n"
					+ "  <body>\n"
					+ "    <h1>LOREM  IPSUM</h1>\n"
					+ "  </body>\n"
					+ "</html>";
			//@formatter:on

			List<Operation> ops = getOperations(true, maxContent);
			assertEquals(3, ops.size());

			Rectangle h1Background = ops.get(1).getBounds().getBounds();
			assertTrue(Math.abs(
					h1Background.getCenterX() - htmlPaneSize.width / 2) < 5);
		}

	}

	private static void assertImageEquals(BufferedImage bi1, BufferedImage bi2,
			int tolerance) {
		assertEquals(bi1.getWidth(), bi2.getWidth());
		assertEquals(bi1.getHeight(), bi2.getHeight());

		assertEquals(bi1.getType(), BufferedImage.TYPE_INT_ARGB);
		assertEquals(bi2.getType(), BufferedImage.TYPE_INT_ARGB);

		int[] row1 = new int[bi1.getWidth()];
		int[] row2 = new int[bi1.getWidth()];
		for (int y = 0; y < bi1.getHeight(); y++) {
			bi1.getRaster().getDataElements(0, y, row1.length, 1, row1);
			bi2.getRaster().getDataElements(0, y, row1.length, 1, row2);
			for (int x = 0; x < bi1.getWidth(); x++) {
				int red1 = (row1[x] >> 16) & 0xff;
				int green1 = (row1[x] >> 8) & 0xff;
				int blue1 = (row1[x] >> 0) & 0xff;
				int alpha1 = (row1[x] >> 24) & 0xff;

				int red2 = (row2[x] >> 16) & 0xff;
				int green2 = (row2[x] >> 8) & 0xff;
				int blue2 = (row2[x] >> 0) & 0xff;
				int alpha2 = (row2[x] >> 24) & 0xff;

				assertTrue(
						"red1 = " + red1 + ", red2 = " + red2 + " at (" + x
								+ "," + y + ")",
						Math.abs(red1 - red2) <= tolerance);
				assertTrue(
						"green1 = " + green1 + ", green2 = " + green2 + " at ("
								+ x + "," + y + ")",
						Math.abs(green1 - green2) <= tolerance);
				assertTrue(
						"blue1 = " + blue1 + ", blue2 = " + blue2 + " at (" + x
								+ "," + y + ")",
						Math.abs(blue1 - blue2) <= tolerance);
				assertTrue(
						"alpha1 = " + alpha1 + ", alpha2 = " + alpha2 + " at ("
								+ x + "," + y + ")",
						Math.abs(alpha1 - alpha2) <= tolerance);
			}
		}
	}

	private BufferedImage getImage(String html) {

		HTMLEditorKit kit = new QHTMLEditorKit();

		JEditorPane p = new JEditorPane();
		p.setEditorKit(kit);
		p.setText(html);

		p.setPreferredSize(htmlPaneSize);
		p.setSize(htmlPaneSize);

		BufferedImage bi = new BufferedImage(htmlPaneSize.width,
				htmlPaneSize.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		p.paint(g);
		g.dispose();

		return bi;
	}
}
