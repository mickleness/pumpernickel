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
package com.pump.showcase.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.UIManager;

import com.pump.math.function.Function;
import com.pump.math.function.PolynomialFunction;

public class BarChartRenderer {

	// based on https://coolors.co/f9c80e-f86624-ea3546-662e9b-43bccd
	static final Color[] colors = new Color[] {
			// cyan
			new Color(0x43bccd),
			// yellow
			new Color(0xf9c80e),
			// red
			new Color(0xea3546),
			// purple
			new Color(0x662e9b),
			// orange
			new Color(0xf86624),
			// green
			new Color(0x02c34a)

	};

	/**
	 * Create a block of HTML that represents clusters of tables.
	 */
	public static String toHtml(Collection<Chart> charts) {
		StringBuilder sb = new StringBuilder();
		for (Chart chart : charts) {
			sb.append(chart.getName() + ":\n<table>\n");
			for (Map.Entry<String, Number> entry : chart.getSeriesData()) {
				sb.append("\t<tr>\n");
				sb.append("\t\t\t<td>" + entry.getKey() + "</td>\n");
				Number value = entry.getValue();
				String valueStr = chart.formatValue(value);
				sb.append("\t\t\t<td>" + valueStr + "</td>\n");
				sb.append("\t</tr>\n");
			}
			sb.append("</table>\n");
		}
		return sb.toString();
	}

	/**
	 * Create a block of plain text that represents clusters of tables.
	 */
	public static String toText(Collection<Chart> charts) {
		StringBuilder sb = new StringBuilder();
		for (Chart chart : charts) {
			sb.append("*** " + chart.getName() + "\n");
			for (Map.Entry<String, Number> entry : chart.getSeriesData()) {
				Number value = entry.getValue();
				String valueStr = chart.formatValue(value);
				sb.append(entry.getKey() + " = " + valueStr + "\n");
			}
			sb.append("\n");
		}
		return sb.toString().trim();
	}

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
		int TICK_HEIGHT = 5;

		String groupLabel;
		Rectangle2D groupLabelRect;
		Chart chart;
		Number maxValue;
		SortedMap<Double, String> tickMap;
		Font tickFont = new Font("Arial", 0, 12);
		Map<String, Number> orderedData = new LinkedHashMap<>();

		public DataRow(Chart chart) {
			this.chart = chart;
			this.groupLabel = chart.getName();
			groupLabelRect = getTextSize(groupLabel);

			Map<String, Number> chartMap = chart.toMap();
			TreeSet<Number> sortedLongs = new TreeSet<>(chartMap.values());
			maxValue = sortedLongs.last();
			Iterator<Number> iter = sortedLongs.iterator();
			while (iter.hasNext()) {
				Number z = iter.next();
				for (Entry<String, Number> e : chartMap.entrySet()) {
					if (e.getValue().equals(z)) {
						orderedData.put(e.getKey(), e.getValue());
					}
				}
			}
			BasicTickLabeler l = new BasicTickLabeler();
			tickMap = l.getLabeledTicks(0, maxValue.doubleValue(), chart);

		}

		@Override
		public int getHeight() {
			return (int) (Math.max(groupLabelRect.getHeight(),
					orderedData.size() * barHeight) + .5) + TICK_HEIGHT + 20;
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

			Function xFunction;

			g.setFont(tickFont);
			Double lastXTick = tickMap.lastKey();
			String lastTickStr = tickMap.get(lastXTick);
			Rectangle2D lastTickStrR = g.getFontMetrics()
					.getStringBounds(lastTickStr, g);
			boolean isTooBig = false;
			int xRightBound = xMax;
			do {
				xFunction = PolynomialFunction.createFit(0, xMin,
						maxValue.doubleValue(), xMax);

				int x = Math.round((float) xFunction.evaluate(lastXTick));
				isTooBig = x + lastTickStrR.getWidth() / 2 > xRightBound;
				if (isTooBig) {
					xMax--;
				}

			} while (isTooBig);

			int j = y;
			for (Entry<String, Number> e : orderedData.entrySet()) {
				Number v = e.getValue();
				Color barColor = getColor(e.getKey());
				int k = (int) (xFunction.evaluate(v.doubleValue()) + .5);
				Rectangle2D r = new Rectangle(xMin, j, k - xMin, barHeight);
				g.setColor(barColor);
				g.fill(r);
				g.setStroke(new BasicStroke(1));
				g.setColor(Color.black);
				g.draw(r);
				j += barHeight;
			}

			g.setColor(new Color(0, 0, 0, 200));
			for (Entry<Double, String> entry : tickMap.entrySet()) {
				int x = Math.round((float) xFunction
						.evaluate(entry.getKey().doubleValue()));
				g.drawLine(x, j, x, j + 5);
			}
			j += 5;

			g.setFont(tickFont);
			for (Entry<Double, String> entry : tickMap.entrySet()) {
				int x = Math.round((float) xFunction
						.evaluate(entry.getKey().doubleValue()));
				Rectangle2D r = g.getFontMetrics()
						.getStringBounds(entry.getValue(), g);
				g.drawString(entry.getValue(), (float) (x - r.getWidth() / 2),
						j + 14);

			}

			g.dispose();
		}
	}

	private Color getColor(String key) {
		int i = barLabelsList.indexOf(key);
		return colors[i % colors.length];
	}

	List<Chart> data;
	List<String> barLabelsList;
	Map<String, Rectangle2D> textSizeMap = new HashMap<>();
	List<Row> rows = new ArrayList<>();
	int groupGap = 5;
	int groupLabelLeftGap = 3;
	int groupLabelRightGap = 8;
	int barHeight = 15;
	Font font = UIManager.getFont("Label.font");
	FontRenderContext frc = new FontRenderContext(new AffineTransform(), true,
			true);

	public BarChartRenderer(List<Chart> data) {
		this.data = data;

		barLabelsList = new ArrayList<>();
		for (Chart chart : data) {
			if (!chart.isEmpty()) {
				DataRow row = new DataRow(chart);
				rows.add(row);
				for (String labelName : row.orderedData.keySet()) {
					if (!barLabelsList.contains(labelName))
						barLabelsList.add(labelName);
				}
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

	public BufferedImage paint(Dimension maxSize) {
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
		paint(g, maxSize);
		return bi;
	}

	public void paint(Graphics2D g, Dimension maxSize) {
		int x = 0;
		for (Row row : rows) {
			x = Math.max(x, row.getGroupLabelWidth()) + groupLabelLeftGap
					+ groupLabelRightGap;
		}

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
	}
}