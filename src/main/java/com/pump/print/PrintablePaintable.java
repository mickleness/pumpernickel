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
package com.pump.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import com.pump.awt.Paintable;

/**
 * This Printable presents one Paintable object per page. Each Paintable object
 * is scaled proportionally to fit on the page as best as possible.
 * 
 */
public class PrintablePaintable implements Printable {
	Paintable[] pages;

	/** Creates a new PrintablePaintable that paints the argument on one page. */
	public PrintablePaintable(Paintable page) {
		this(new Paintable[] { page });
	}

	/**
	 * Creates a new PrintablePaintable that paints each argument element on one
	 * page.
	 */
	public PrintablePaintable(Paintable[] pages) {
		this.pages = new Paintable[pages.length];
		System.arraycopy(pages, 0, this.pages, 0, pages.length);
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if (pageIndex >= pages.length)
			return Printable.NO_SUCH_PAGE;

		Paintable paintable = pages[pageIndex];
		double pWidth = paintable.getWidth();
		double pHeight = paintable.getHeight();

		double wRatio = pageFormat.getWidth() / pWidth;
		double hRatio = pageFormat.getHeight() / pHeight;
		double zoom = Math.min(wRatio, hRatio);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(pageFormat.getWidth() / 2 - pWidth * zoom / 2,
				pageFormat.getHeight() / 2 - pHeight * zoom / 2);
		g2.scale(zoom, zoom);

		paintable.paint(g2);

		return Printable.PAGE_EXISTS;
	}
}