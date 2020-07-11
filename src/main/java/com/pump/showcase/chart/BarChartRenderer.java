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
package com.pump.showcase.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.swing.UIManager;

import com.pump.math.function.Function;
import com.pump.math.function.PolynomialFunction;

public class BarChartRenderer {
	/**
	 * Use this value in your data to indicate an error occurred during
	 * calculations.
	 */
	public static final Long ERROR_CODE = Long.MIN_VALUE + 1;

	// some colors from https://flatuicolors.com/palette/ru
	static final Color[] colors = new Color[] { new Color(0x574b90),
			new Color(0xe77f67), new Color(0x3dc1d3), new Color(0xCAD3C8),
			new Color(0xf7d794), new Color(0xD6A2E8) };

	interface Row {
		public int getHeight();

		public int getGroupLabelWidth();

		public void paint(Graphics2D g, int xMin, int y, int xMax);
	}

	class KeyRow implements Row {
		int h = 20;

		@Override
		public int getHeight() {
			return barLabelsList.size() * h;
		}

		@Override
		public int getGroupLabelWidth() {
			return 0;
		}

		@Override
		public void paint(Graphics2D g, int xMin, int y, int xMax) {

			float width = 0;
			for (String str : barLabelsList) {
				Rectangle2D r = getTextSize(str);
				width = (float) Math.max(width, r.getWidth() + 30);
			}
			g = (Graphics2D) g.create();
			g.translate(xMax - width - 5, 0);
			for (String str : barLabelsList) {
				Rectangle2D r = getTextSize(str);
				g.setColor(Color.black);
				g.drawString(str, 30,
						(float) (y + 20 / 2 - r.getHeight() / 2 - r.getY()));
				g.setColor(getColor(str));
				int z = (int) r.getHeight();
				Rectangle r2 = new Rectangle(15 - z / 2, y + 20 / 2 - z / 2, z,
						z);
				g.fill(r2);
				g.setColor(Color.black);
				g.draw(r2);
				y += 20;
			}
			g.dispose();
		}

	}

	class DataRow implements Row {
		String groupLabel;
		Rectangle2D groupLabelRect;
		Map<String, Long> data = new LinkedHashMap<>();
		long maxValue;

		public DataRow(String groupLabel, Map<String, Long> data) {
			this.groupLabel = groupLabel;
			groupLabelRect = getTextSize(groupLabel);

			TreeSet<Long> sortedLongs = new TreeSet<>(data.values());
			maxValue = sortedLongs.last();
			Iterator<Long> iter = sortedLongs.descendingIterator();
			while (iter.hasNext()) {
				Long z = iter.next();
				for (Entry<String, Long> e : data.entrySet()) {
					if (e.getValue().equals(z)) {
						this.data.put(e.getKey(), e.getValue());
					}
				}
			}
		}

		@Override
		public int getHeight() {
			return (int) (Math.max(groupLabelRect.getHeight(),
					data.size() * barHeight) + .5);
		}

		@Override
		public int getGroupLabelWidth() {
			return (int) (groupLabelRect.getWidth() + .5);
		}

		@Override
		public void paint(Graphics2D g, int xMin, int y, int xMax) {
			g = (Graphics2D) g.create();
			int h = getHeight();
			float labelX = (float) (xMin - groupLabelRightGap
					- groupLabelRect.getWidth());
			float labelY = (float) (y + h / 2 - groupLabelRect.getHeight() / 2
					- groupLabelRect.getY());
			g.setColor(Color.black);
			g.drawString(groupLabel, labelX, labelY);
			Function xFunction = PolynomialFunction.createFit(0, xMin, maxValue,
					xMax);
			Font font = g.getFont();

			int j = y;
			for (Entry<String, Long> e : data.entrySet()) {
				Long v = e.getValue();
				Color barColor = getColor(e.getKey());
				if (ERROR_CODE.equals(v)) {
					int r = 3;
					String str = "Error";
					double w = g.getFontMetrics().getStringBounds(str, g)
							.getWidth();
					Shape[] bullets = new Shape[] {
							new Ellipse2D.Double(xMin + 6 - r,
									j + barHeight / 2 - r + 1, 2 * r, 2 * r),
							new Ellipse2D.Double(xMin + 15 - r,
									j + barHeight / 2 - r + 1, 2 * r, 2 * r),
							new Ellipse2D.Double(xMin + w + 18 + 5 - r,
									j + barHeight / 2 - r + 1, 2 * r, 2 * r),
							new Ellipse2D.Double(xMin + w + 18 + 14 - r,
									j + barHeight / 2 - r + 1, 2 * r, 2 * r) };
					for (Shape bullet : bullets) {
						g.setColor(barColor);
						g.fill(bullet);
						g.setColor(Color.black);
						g.draw(bullet);
					}

					g.setColor(Color.black);
					g.setFont(font.deriveFont(font.getSize2D() - 2));
					g.drawString(str, xMin + 20, j + barHeight - 3);
				} else {
					int k = (int) (xFunction.evaluate(v) + .5);
					Rectangle2D r = new Rectangle(xMin, j, k - xMin, barHeight);
					g.setColor(barColor);
					g.fill(r);
					g.setStroke(new BasicStroke(1));
					g.setColor(Color.black);
					g.draw(r);
				}
				j += barHeight;
			}
			g.dispose();
		}
	}

	private Color getColor(String key) {
		int i = barLabelsList.indexOf(key);
		return colors[i % colors.length];
	}

	Map<String, Map<String, Long>> data;
	Map<String, Long> maxMap;
	List<String> barLabelsList;
	Map<String, Rectangle2D> textSizeMap = new HashMap<>();
	List<Row> rows = new ArrayList<>();
	int groupGap = 5;
	int groupLabelLeftGap = 3;
	int groupLabelRightGap = 3;
	int barHeight = 15;
	Font font = UIManager.getFont("Label.font");
	FontRenderContext frc = new FontRenderContext(new AffineTransform(), true,
			true);

	public BarChartRenderer(Map<String, Map<String, Long>> data) {
		this.data = data;
		maxMap = createMaxMap();

		barLabelsList = new ArrayList<>();
		for (Entry<String, Map<String, Long>> entry : data.entrySet()) {
			DataRow row = new DataRow(entry.getKey(), entry.getValue());
			rows.add(row);

			// populate barLabelsList in the order rows will be seen
			for (String str : row.data.keySet()) {
				if (!barLabelsList.contains(str))
					barLabelsList.add(str);
			}
		}
		rows.add(new KeyRow());
	}

	private Rectangle2D getTextSize(String str) {
		Rectangle2D r = textSizeMap.get(str);
		if (r == null) {
			r = font.getStringBounds(str, frc);
			textSizeMap.put(str, r);
		}
		return r;
	}

	public BufferedImage render(Dimension maxSize) {
		int height = 0;
		int x = 0;
		for (Row row : rows) {
			height += row.getHeight() + groupGap;
			x = Math.max(x, row.getGroupLabelWidth()) + groupLabelLeftGap
					+ groupLabelRightGap;
		}
		height -= groupGap;
		height++;

		BufferedImage bi = new BufferedImage(maxSize.width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setColor(Color.black);
		g.setFont(font);

		int y = 0;
		for (Row row : rows) {
			row.paint(g, x, y, maxSize.width - 1);
			y += row.getHeight() + groupGap;
		}

		g.dispose();

		return bi;
	}

	private Map<String, Long> createMaxMap() {
		Map<String, Long> m = new HashMap<>();
		for (Entry<String, Map<String, Long>> entry : data.entrySet()) {
			long max = 0;
			for (Long value : entry.getValue().values()) {
				max = Math.max(max, value);
			}
			m.put(entry.getKey(), max);
		}
		return m;
	}
}