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
package com.pump.print;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pump.awt.Paintable;

/**
 * This encapsulates information about the layout of several tiles on one page.
 * <P>
 * All length information on this page is presented in 1/72 of an inch. So
 * insets of (72,72,72,72) are 1-inch insets. This is keeping in convention with
 * Java's printing conventions.
 * <P>
 * The following are elements in this layout information:
 * <ul><LI>Header/Footer: this is html-formatted text that may appear at the
 * top/bottom of each page.</li>
 * <LI>Insets: these are margins for this page.</li>
 * <LI>Inner Padding: this is the space that will appear between all
 * components. This includes the space between cells, and the space between
 * cells and headers/footers.</li>
 * <LI>Flip Pattern: an array of flip instructions that will be followed
 * alternatingly. For example, an array of {NONE, NONE, BOTH, BOTH} will flip
 * the last 2 tiles, so if you folder the paper correctly you can make 4 tiles
 * into a greeting card. I know, it's complicated... and if you don't ever want
 * to flip tiles: you never need to use it.</LI></ul>
 * 
 */
public class PrintLayout {

	/**
	 * This describes a <code>PageFormat</code> as a String. This is provided
	 * as a debugging tool, because <code>PageFormat.toString()</code> doesn't
	 * support this itself.
	 */
	public static String toString(PageFormat f) {
		if (f == null)
			return "null";
		String orientation;
		if (f.getOrientation() == PageFormat.LANDSCAPE) {
			orientation = "LANDSCAPE";
		} else if (f.getOrientation() == PageFormat.PORTRAIT) {
			orientation = "PORTRAIT";
		} else if (f.getOrientation() == PageFormat.REVERSE_LANDSCAPE) {
			orientation = "REVERSE_LANDSCAPE";
		} else {
			orientation = "UNKNOWN";
		}
		return ("PageFormat[ " + f.getWidth() + "x" + f.getHeight()
				+ " imageable=(" + f.getImageableX() + ", " + f.getImageableY()
				+ ", " + f.getImageableWidth() + ", " + f.getImageableHeight()
				+ ") orientation=" + orientation + "]");
	}

	private int rows = 2;
	private int columns = 2;
	private int[] flipPattern = new int[] { NONE };
	private PageFormat pageFormat = new PageFormat();
	private Insets insets = new Insets(72, 72, 72, 72);
	private int innerPadding = 36;
	private String header = "";
	private String footer = "";
	private boolean headerActive = true;
	private boolean footerActive = true;
	private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	Map<String, Object> properties;

	/**
	 * When used in the flip array, this indicates that the tile should not be
	 * flipped.
	 */
	public static final int NONE = GridBagConstraints.NONE;
	/**
	 * When used in the flip array, this indicates that the tile should be
	 * flipped horizontally.
	 */
	public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;
	/**
	 * When used in the flip array, this indicates that the tile should be
	 * flipped vertically .
	 */
	public static final int VERTICAL = GridBagConstraints.VERTICAL;
	/**
	 * When used in the flip array, this indicates that the tile should be
	 * flipped both horizontally and vertically.
	 */
	public static final int BOTH = GridBagConstraints.BOTH;

	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * number of rows changes.
	 */
	public static final String PROPERTY_ROWS = "rows";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * number of columns changes.
	 */
	public static final String PROPERTY_COLUMNS = "columns";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * flip pattern changes.
	 */
	public static final String PROPERTY_FLIP_PATTERN = "flip_pattern";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * inner padding changes.
	 */
	public static final String PROPERTY_INNER_PADDING = "inner_padding";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * insets change.
	 */
	public static final String PROPERTY_INSETS = "insets";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * orientation changes.
	 */
	public static final String PROPERTY_ORIENTATION = "orientation";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * width changes.
	 */
	public static final String PROPERTY_WIDTH = "width";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * height changes.
	 */
	public static final String PROPERTY_HEIGHT = "height";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * imageable bounds change.
	 */
	public static final String PROPERTY_IMAGEABLE_BOUNDS = "imageable_bounds";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * header changes.
	 */
	public static final String PROPERTY_HEADER = "header";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * footer changes.
	 */
	public static final String PROPERTY_FOOTER = "footer";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * header is toggled on/off.
	 */
	public static final String PROPERTY_FOOTER_ACTIVE = "footer_active";
	/**
	 * This is used to notify <code>PropertyChangeListeners</code> when the
	 * footer is toggled on/off.
	 */
	public static final String PROPERTY_HEADER_ACTIVE = "header_active";

	/** Returns the imageable bounds of the paper */
	public Rectangle2D getPaperImageableBounds() {
		return new Rectangle2D.Double(pageFormat.getImageableX(), pageFormat
				.getImageableY(), pageFormat.getImageableWidth(), pageFormat
				.getImageableHeight());
	}

	/**
	 * This adjusts the imageable bounds of the paper. It is not recommended
	 * that you use this method directly. The methods that set the insets, paper
	 * width, paper height, and orientation automatically adjust this property
	 * for you to fit your needs.
	 * 
	 * @param r
	 *            the new imageable bounds.
	 * @return <code>true</code> if a change occurred.
	 */
	public boolean setPaperImageableBounds(Rectangle2D r) {
		Rectangle2D oldBounds = getPaperImageableBounds();
		if (r.equals(oldBounds))
			return false;
		Paper paper = pageFormat.getPaper();
		paper.setImageableArea(r.getX(), r.getY(), r.getWidth(), r.getHeight());
		pageFormat.setPaper(paper);
		firePropertyChange(PROPERTY_IMAGEABLE_BOUNDS, oldBounds,
				r.clone());
		return true;
	}

	/** Returns the paper width. */
	public double getPaperWidth() {
		return pageFormat.getWidth();
	}

	/** Returns whether the header is active. */
	public boolean isHeaderActive() {
		return headerActive;
	}

	/** Toggles whether the header is active or not. */
	public void setHeaderActive(boolean b) {
		if (b == headerActive)
			return;
		headerActive = b;
		firePropertyChange(PROPERTY_HEADER_ACTIVE, new Boolean(!headerActive),
				new Boolean(headerActive));
	}

	/** Returns whether the footer is active. */
	public boolean isFooterActive() {
		return footerActive;
	}

	/** Toggles whether the footer is active or not. */
	public void setFooterActive(boolean b) {
		if (b == footerActive)
			return;
		footerActive = b;
		firePropertyChange(PROPERTY_FOOTER_ACTIVE, new Boolean(!footerActive),
				new Boolean(footerActive));
	}

	/** Returns the paper height. */
	public double getPaperHeight() {
		return pageFormat.getHeight();
	}

	/**
	 * Sets the width for this page. This also validates the imageable bounds of
	 * the paper.
	 */
	public boolean setPaperWidth(double width) {
		if (width == pageFormat.getWidth())
			return false;

		double oldWidth = pageFormat.getWidth();
		Paper paper = pageFormat.getPaper();
		paper.setSize(width, paper.getHeight());
		pageFormat.setPaper(paper);
		firePropertyChange(PROPERTY_WIDTH, new Double(oldWidth), new Double(
				width));

		validateImageableBounds();

		return true;
	}

	/**
	 * Sets the height for this page. This also validates the imageable bounds
	 * of the paper.
	 */
	public boolean setPaperHeight(double height) {
		if (height == pageFormat.getHeight())
			return false;

		double oldHeight = pageFormat.getHeight();
		Paper paper = pageFormat.getPaper();
		paper.setSize(paper.getWidth(), height);
		pageFormat.setPaper(paper);
		firePropertyChange(PROPERTY_WIDTH, new Double(oldHeight), new Double(
				height));

		validateImageableBounds();

		return true;
	}

	private void validateImageableBounds() {
		Rectangle2D insetsBased;
		if (getOrientation() == PageFormat.PORTRAIT) {
			insetsBased = new Rectangle2D.Double(insets.left, insets.top,
					getPaperWidth() - insets.left - insets.right,
					getPaperHeight() - insets.top - insets.bottom);
		} else {
			insetsBased = new Rectangle2D.Double(insets.top, insets.left,
					getPaperHeight() - insets.top - insets.bottom,
					getPaperWidth() - insets.left - insets.right);
		}
		Rectangle2D sum = insetsBased;
		setPaperImageableBounds(sum);
	}

	/**
	 * Returns a newly create <code>PageFormat</code> that includes most of
	 * the information in this object.
	 */
	public PageFormat createPageFormat() {
		return (PageFormat) pageFormat.clone();
	}

	/** Returns the orientation of the <code>PageFormat</code>. */
	public int getOrientation() {
		return pageFormat.getOrientation();
	}

	/**
	 * Sets the orientation for this page. This also validates the imageable
	 * bounds of the paper.
	 * @param orientation must be PageFormat.LANDSCAPE or PageFormat.PORTRAIT
	 */
	public boolean setOrientation(int orientation) {
		if(!(orientation==PageFormat.LANDSCAPE || orientation==PageFormat.PORTRAIT))
			throw new IllegalArgumentException("orientation must be PageFormat.LANDSCAPE or PageFormat.PORTRAIT");
		if (orientation == pageFormat.getOrientation())
			return false;
		int oldOrientation = pageFormat.getOrientation();
		pageFormat.setOrientation(orientation);
		firePropertyChange(PROPERTY_ORIENTATION, new Integer(oldOrientation),
				new Integer(orientation));

		validateImageableBounds();

		return true;
	}

	/** Define a client key. */
	public boolean setProperty(String key,Object value) {
		if(properties==null) {
			properties = new HashMap<String, Object>();
			properties.put(key, value);
			firePropertyChange(key, null, value);
			return true;
		} else {
			Object oldValue = properties.get(key);
			if(oldValue==null && value==null) return false;
			if(oldValue!=null && oldValue.equals(value)) return false;
			if(value==null) {
				properties.remove(key);
				firePropertyChange(key, oldValue, null);
				return true;
			} else {
				properties.put(key, value);
				firePropertyChange(key, oldValue, value);
				return true;
			}
		}
	}
	
	/** Retrieves a client key. */
	public Object getProperty(String key) {
		if(properties==null) return null;
		return properties.get(key);
	}

	/** Returns a copy of the insets used in this <code>PrintLayout</code>. */
	public Insets getInsets() {
		return (Insets) insets.clone();
	}

	/**
	 * Sets the insets for the tiles on this page. This also validates the
	 * imageable bounds of the paper, and adjusts them to allow for these
	 * insets.
	 */
	public boolean setInsets(Insets insets) {
		if (this.insets.left == insets.left && this.insets.top == insets.top
				&& this.insets.bottom == insets.bottom
				&& this.insets.right == insets.right) {
			return false;
		}
		Insets oldInsets = this.insets;
		this.insets = (Insets) insets.clone();

		firePropertyChange(PROPERTY_INSETS, oldInsets, insets.clone());

		validateImageableBounds();
		return true;
	}

	/**
	 * Returns the inner padding of this <code>PrintLayout</code>.
	 * 
	 */
	public int getInnerPadding() {
		return innerPadding;
	}

	/**
	 * Sets the inner padding of this <code>PrintLayout</code>.
	 * 
	 */
	public boolean setInnerPadding(int innerPadding) {
		if (innerPadding == this.innerPadding)
			return false;
		int oldInnerPadding = this.innerPadding;
		this.innerPadding = innerPadding;
		firePropertyChange(PROPERTY_INNER_PADDING,
				new Integer(oldInnerPadding), new Integer(innerPadding));
		return true;
	}

	private static int[] clone(int[] intArray) {
		int[] arrayCopy = new int[intArray.length];
		System.arraycopy(intArray, 0, arrayCopy, 0, intArray.length);
		return arrayCopy;
	}

	/**
	 * Returns the flip pattern of this <code>PrintLayout</code>.
	 * 
	 */
	public int[] getFlipPattern() {
		return clone(flipPattern);
	}

	/**
	 * Sets the flip pattern of this <code>PrintLayout</code>.
	 * 
	 */
	public boolean setFlipPattern(int[] flipPattern) {
		if (this.flipPattern.length == flipPattern.length) {
			boolean different = false;
			for (int a = 0; a < flipPattern.length && (different == false); a++) {
				if (this.flipPattern[a] != flipPattern[a]) {
					different = true;
				}
			}
			if (different == false)
				return false;
		}

		int[] oldFlipPattern = this.flipPattern;
		this.flipPattern = clone(flipPattern);
		firePropertyChange(PROPERTY_FLIP_PATTERN, oldFlipPattern,
				clone(flipPattern));
		return true;
	}

	/** Returns the number of columns. */
	public int getColumns() {
		return columns;
	}

	/** Sets the number of columns. */
	public boolean setColumns(int columns) {
		if (columns == this.columns)
			return false;
		if (columns <= 0)
			throw new IllegalArgumentException("The columns (" + columns
					+ ") must be greater than zero.");
		int oldColumns = this.columns;
		this.columns = columns;
		firePropertyChange(PROPERTY_COLUMNS, new Integer(oldColumns),
				new Integer(columns));
		return true;
	}

	/** Returns the header. */
	public String getHeader() {
		return header;
	}

	/** Returns the footer. */
	public String getFooter() {
		return footer;
	}

	/** Sets the header. */
	public boolean setHeader(String newHeader) {
		if (newHeader == null)
			newHeader = "";

		if (header.equals(newHeader))
			return false;
		String oldHeader = header;
		header = newHeader;
		firePropertyChange(PROPERTY_HEADER, oldHeader, header);
		return true;
	}

	/** Sets the footer. */
	public boolean setFooter(String newFooter) {
		if (newFooter == null)
			newFooter = "";

		if (footer.equals(newFooter))
			return false;

		String oldFooter = footer;
		footer = newFooter;
		firePropertyChange(PROPERTY_FOOTER, oldFooter, footer);
		return true;
	}

	/** Returns the number of rows. */
	public int getRows() {
		return rows;
	}

	/** Sets the number of rows. */
	public boolean setRows(int rows) {
		if (rows == this.rows)
			return false;
		if (rows <= 0)
			throw new IllegalArgumentException("The rows (" + rows
					+ ") must be greater than zero.");
		int oldRows = this.rows;
		this.rows = rows;
		firePropertyChange(PROPERTY_ROWS, new Integer(oldRows), new Integer(
				rows));
		return true;
	}

	protected void firePropertyChange(String name, Object oldValue,
			Object newValue) {
		for (int a = 0; a < listeners.size(); a++) {
			PropertyChangeListener listener = listeners
					.get(a);
			try {
				listener.propertyChange(new PropertyChangeEvent(this, name,
						oldValue, newValue));
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (l == null)
			throw new NullPointerException();
		if (listeners.contains(l))
			return;
		listeners.add(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.remove(l);
	}
	
	public PrintLayout() {
		this(1, 1, new PageFormat());
	}

	public PrintLayout(int rows, int columns, PageFormat format) {
		this(rows, columns, format, new Insets(72, 72, 72, 72), 36,
				new int[] { NONE });
	}

	public PrintLayout(int rows, int columns, PageFormat format, Insets i,
			int innerPadding, int[] flipPattern) {
		this.rows = rows;
		this.columns = columns;
		this.pageFormat = (PageFormat) format.clone();
		this.insets = (Insets) i.clone();
		this.innerPadding = innerPadding;
		this.flipPattern = new int[flipPattern.length];
		System.arraycopy(flipPattern, 0, this.flipPattern, 0,
				flipPattern.length);

		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String s = e.getPropertyName();
				if (s.equals(PROPERTY_HEADER) || s.equals(PROPERTY_WIDTH)
						|| s.equals(PROPERTY_INNER_PADDING)) {
					headerPaintable.define(header, (int) (getPaperWidth()
							- insets.left - insets.right));
				}
				if (s.equals(PROPERTY_FOOTER) || s.equals(PROPERTY_WIDTH)
						|| s.equals(PROPERTY_INNER_PADDING)) {
					footerPaintable.define(footer, (int) (getPaperWidth()
							- insets.left - insets.right));
				}
			}
		});
	}

	public PrintLayout(PrintLayout l) {
		this(1, 1, new PageFormat(), new Insets(72, 72, 72, 72), 36,
				new int[] { NONE });
		setLayout(l);
	}

	public void setLayout(PrintLayout l) {
		setColumns(l.getColumns());
		setFlipPattern(l.getFlipPattern());
		setFooter(l.getFooter());
		setFooterActive(l.isFooterActive());
		setHeader(l.getHeader());
		setHeaderActive(l.isHeaderActive());
		setInnerPadding(l.getInnerPadding());
		setInsets(l.getInsets());
		setOrientation(l.getOrientation());
		setPaperHeight(l.getPaperHeight());
		setPaperImageableBounds(l.getPaperImageableBounds());
		setPaperWidth(l.getPaperWidth());
		setRows(l.getRows());
	}

	/**
	 * Create a printable object from several tiles using this layout.
	 * 
	 * @param paintables
	 *            the tiles to arrange.
	 * @param pgNumber
	 *            the page number to start counting from in the header/footer.
	 *            In most cases this should be zero, but for some previews it
	 *            can be a preset value.
	 *            <P>
	 *            This value is used to replace the text "&lt;PageNumber/" in the
	 *            header/footer text.
	 * @param pgCount
	 *            the total number of pages.
	 *            <P>
	 *            This value is used to replace the text "&lt;PageCount/" in the
	 *            header/footer text.
	 * @return a printable object based on the arguments provided.
	 */
	public Printable createPrintable(Paintable[] paintables, int pgNumber,
			int pgCount) {
		Paintable[] pages = createPaintables(paintables, pgNumber, pgCount);
		return new PrintablePaintable(pages);
	}

	private static Object[] add(Object[] array, Object newElement) {
		Class<?> type = array.getClass().getComponentType();
		Object[] newArray = (Object[]) Array
				.newInstance(type, array.length + 1);
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[newArray.length - 1] = newElement;
		return newArray;
	}

	private HTMLPaintable headerPaintable = new HTMLPaintable();
	private HTMLPaintable footerPaintable = new HTMLPaintable();

	/**
	 * Create several pages of Paintable objects using input tiles that are
	 * arranged according to this layout.
	 * 
	 * @param paintables
	 * @param pgNumber
	 *            the page number to start counting from in the header/footer.
	 *            In most cases this can be zero, but for some previews it can
	 *            be a preset value.
	 *            <P>
	 *            This value is used to replace the text "&lt;PageNumber/" in the
	 *            header/footer text.
	 * @param pgCount
	 *            the total number of pages.
	 *            <P>
	 *            This value is used to replace the text "&lt;PageCount/" in the
	 *            header/footer text.
	 * @return 1 or more pages that arrange the incoming tiles as necessary.
	 */
	public Paintable[] createPaintables(Paintable[] paintables, int pgNumber,
			int pgCount) {
		// we must update the header/footer before calling getCellLayout
		// in case that changes the height:

		String customHeaderText = header.replace("<PageNumber/>", Integer
				.toString(1));
		customHeaderText = customHeaderText.replace("<PageCount/>",
				Integer.toString(1));
		headerPaintable.define(customHeaderText, (int) (getPaperWidth()
				- insets.left - insets.right));
		String customFooterText = footer.replace("<PageNumber/>", Integer
				.toString(1));
		customFooterText = customFooterText.replace("<PageCount/>",
				Integer.toString(1));
		footerPaintable.define(customFooterText, (int) (getPaperWidth()
				- insets.left - insets.right));

		Rectangle2D[][] rects = getCellLayout(paintables.length);
		Paintable[] pages = new Paintable[rects.length];

		int offset = 0;
		for (int page = 0; page < pages.length; page++) {
			Paintable[] pageElements = new Paintable[rects[page].length];
			Rectangle2D[] cellRects = rects[page];
			System.arraycopy(paintables, offset, pageElements, 0,
					pageElements.length);

			offset += pageElements.length;

			String thisHeader = header.replace("<PageNumber/>", Integer
					.toString(page + pgNumber + 1));
			thisHeader = thisHeader.replace("<PageCount/>", Integer
					.toString(pgCount));
			HTMLPaintable customHeader = headerPaintable;
			headerPaintable.define(thisHeader, (int) (getPaperWidth()
					- insets.left - insets.right));
			if (header.indexOf("<PageNumber/>") != -1
					|| header.indexOf("<PageCount>") != -1) {
				// have to clone it, otherwise every "page" will reference
				// the same page header, and the page number won't
				// be unique per page.
				customHeader = (HTMLPaintable) customHeader.clone();
			}
			if (customHeader.getHeight() != 0 && isHeaderActive()) {
				pageElements = (Paintable[]) add(pageElements, customHeader);
				cellRects = (Rectangle2D[]) add(cellRects,
						new Rectangle2D.Double(insets.left, insets.top,
								customHeader.getWidth(), customHeader
										.getHeight()));
			}

			String thisFooter = footer.replace("<PageNumber/>", Integer
					.toString(page + pgNumber + 1));
			thisFooter = thisFooter.replace("<PageCount/>", Integer
					.toString(pgCount));
			HTMLPaintable customFooter = footerPaintable;
			footerPaintable.define(thisFooter, (int) (getPaperWidth()
					- insets.left - insets.right));
			if (footer.indexOf("<PageNumber/>") != -1
					|| footer.indexOf("<PageFooter/>") != -1) {
				customFooter = (HTMLPaintable) footerPaintable.clone();
			}

			if (customFooter.getHeight() != 0 && isFooterActive()) {
				pageElements = (Paintable[]) add(pageElements, customFooter);
				cellRects = (Rectangle2D[]) add(cellRects,
						new Rectangle2D.Double(insets.left, getPaperHeight()
								- insets.bottom - footerPaintable.getHeight(),
								customFooter.getWidth(), customFooter
										.getHeight()));
			}

			pages[page] = new LayoutPaintable((int) pageFormat.getWidth(),
					(int) pageFormat.getHeight(), cellRects, pageElements, true);
		}
		return pages;
	}

	/**
	 * Returns a matrix of Rectangle2Ds describing how to lay out a certain
	 * number of cells. Each element of the array is a page, and within each
	 * page can be multiple tiles.
	 * <P>
	 * This will allow space for a header and/or footer if appropriate, but that
	 * rectangle is <i>not</i> included in the returned set of rectangles from
	 * this method.
	 * 
	 * @param cellCount
	 *            the number of cells to design for.
	 * @return multiple pages, with multiple cells on each page.
	 * 
	 */
	public Rectangle2D[][] getCellLayout(int cellCount) {
		int cellsPerPage = columns * rows;
		int pageCount = (cellCount + cellsPerPage - 1) / cellsPerPage;
		Rectangle2D[][] cells = new Rectangle2D[pageCount][];
		double availableWidth = pageFormat.getWidth() - insets.left
				- insets.right - innerPadding * (columns - 1);
		double availableHeight = pageFormat.getHeight() - insets.bottom
				- insets.top - innerPadding * (rows - 1);

		double y = insets.top;

		if (headerPaintable.getHeight() > 0 && isHeaderActive()) {
			availableHeight = availableHeight - headerPaintable.getHeight()
					- innerPadding;
			y = y + headerPaintable.getHeight() + innerPadding;
		}
		if (footerPaintable.getHeight() > 0 && isFooterActive()) {
			availableHeight = availableHeight - footerPaintable.getHeight()
					- innerPadding;
		}

		double cellWidth = availableWidth / (columns);
		double cellHeight = availableHeight / (rows);

		int cellCtr = 0;
		bigLoop: for (int page = 0; page < pageCount; page++) {
			int cellsOnThisPage = Math.min(cellCount - page * cellsPerPage,
					cellsPerPage);

			cells[page] = new Rectangle2D[cellsOnThisPage];

			double pageY = y;
			for (int row = 0; row < rows; row++, pageY += cellHeight
					+ innerPadding) {

				double x = insets.left;
				for (int column = 0; column < columns; column++, x += cellWidth
						+ innerPadding) {
					int index = row * columns + column;
					if (index < cells[page].length) {
						cells[page][index] = new Rectangle2D.Double(x, pageY,
								cellWidth, cellHeight);
						int flip = flipPattern[cellCtr % flipPattern.length];
						if (flip == HORIZONTAL) {
							cells[page][index].setFrame(cells[page][index]
									.getX()
									+ cells[page][index].getWidth(),
									cells[page][index].getY(),
									-cells[page][index].getWidth(),
									cells[page][index].getHeight());
						} else if (flip == VERTICAL) {
							cells[page][index].setFrame(cells[page][index]
									.getX(), cells[page][index].getY()
									+ cells[page][index].getHeight(),
									cells[page][index].getWidth(),
									-cells[page][index].getHeight());
						} else if (flip == BOTH) {
							cells[page][index].setFrame(cells[page][index]
									.getX()
									+ cells[page][index].getWidth(),
									cells[page][index].getY()
											+ cells[page][index].getHeight(),
									-cells[page][index].getWidth(),
									-cells[page][index].getHeight());
						}
						cellCtr++;
					} else {
						break bigLoop;
					}
				}
			}
		}

		return cells;
	}
}