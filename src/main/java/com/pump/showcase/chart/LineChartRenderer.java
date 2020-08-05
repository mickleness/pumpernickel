package com.pump.showcase.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.pump.geom.TransformUtils;

public class LineChartRenderer {

	private static class DataContainer {

		double domainMin = Double.MAX_VALUE;
		double domainMax = Double.MIN_VALUE;
		double rangeMin = Double.MAX_VALUE;
		double rangeMax = Double.MIN_VALUE;

		Map<String, SortedMap<Double, Double>> data;

		DataContainer(Map<String, SortedMap<Double, Double>> data) {
			this.data = data;
			for (Entry<String, SortedMap<Double, Double>> entry : data
					.entrySet()) {
				for (Entry<Double, Double> entry2 : entry.getValue()
						.entrySet()) {
					rangeMin = Math.min(rangeMin, entry2.getValue());
					rangeMax = Math.max(rangeMax, entry2.getValue());
					domainMin = Math.min(domainMin, entry2.getKey());
					domainMax = Math.max(domainMax, entry2.getKey());
				}
			}
		}
	}

	private class VerticalAxis {

		SortedMap<Double, String> labeledTicks;
		int labelWidth = 0;
		Font font;

		public VerticalAxis(Font font, FontRenderContext frc,
				Dimension imageSize) {
			this.font = font;
			labeledTicks = new BasicTickLabeler().getLabeledTicks(
					dataContainer.rangeMin, dataContainer.rangeMax);

			dataContainer.rangeMax = labeledTicks.lastKey();

			for (String labeledTick : labeledTicks.values()) {
				Rectangle2D r = font.getStringBounds(labeledTick, frc);
				labelWidth = Math.max(labelWidth, (int) (r.getWidth() + .5));
			}
		}

		public int getWidth() {
			return 5 + labelWidth;
		}

		public void paint(Graphics2D g, Rectangle chartRect,
				AffineTransform dataToPanelTransform) {
			g.setColor(Color.gray);
			g.drawLine(chartRect.x, chartRect.y + chartRect.height, chartRect.x,
					chartRect.y);

			for (Double tickValue : labeledTicks.keySet()) {
				Point2D tickPoint = dataToPanelTransform
						.transform(new Point2D.Double(0, tickValue), null);
				Line2D line = new Line2D.Double(tickPoint.getX(),
						tickPoint.getY(), tickPoint.getX() - 4,
						tickPoint.getY());
				g.draw(line);

				g.setFont(font);
				String tickLabel = labeledTicks.get(tickValue);
				Rectangle2D r = font.getStringBounds(tickLabel,
						g.getFontRenderContext());
				g.drawString(tickLabel,
						(float) (tickPoint.getX() - r.getWidth() - 4),
						(float) (tickPoint.getY() + (-.4 * r.getY())));

			}
		}

	}

	private class HorizontalAxis {

		SortedMap<Double, String> labeledTicks;
		int labelWidth = 0;
		Font font;

		public HorizontalAxis(Font font, FontRenderContext frc,
				Dimension imageSize) {
			this.font = font;
			labeledTicks = new BasicTickLabeler().getLabeledTicks(
					dataContainer.domainMin, dataContainer.domainMax);

			dataContainer.domainMax = labeledTicks.lastKey();
		}

		public int getHeight() {
			// 4 for tick mark height
			return (int) (font.getSize2D() * 5 / 3) + 4;
		}

		public void paint(Graphics2D g, Rectangle chartRect,
				AffineTransform dataToPanelTransform) {
			g.setColor(Color.gray);
			g.drawLine(chartRect.x, chartRect.y + chartRect.height,
					chartRect.x + chartRect.width,
					chartRect.y + chartRect.height);

			for (Double tickValue : labeledTicks.keySet()) {
				Point2D tickPoint = dataToPanelTransform
						.transform(new Point2D.Double(tickValue, 0), null);
				Line2D line = new Line2D.Double(tickPoint.getX(),
						tickPoint.getY(), tickPoint.getX(),
						tickPoint.getY() + 4);
				g.draw(line);

				g.setFont(font);
				String tickLabel = labeledTicks.get(tickValue);
				Rectangle2D r = font.getStringBounds(tickLabel,
						g.getFontRenderContext());
				g.drawString(tickLabel,
						(float) (tickPoint.getX() - r.getWidth() / 2),
						(float) (tickPoint.getY() + 6 + (-r.getY())));

			}
		}

		public int getRightPadding(FontRenderContext frc) {
			Double lastTick = labeledTicks.lastKey();
			String tickLabel = labeledTicks.get(lastTick);
			Rectangle2D r = font.getStringBounds(tickLabel, frc);
			return (int) (r.getWidth() / 2 + 2);
		}

	}

	private class Legend {

		class Layout {
			class Cell {
				String text;
				int seriesIndex;
				int textX, textY;
				Rectangle cellBounds;
				int colorSwatchWidth = 10;
				int colorSwatchPadding = 5;
				Rectangle colorSwatchBounds;

				public Cell(int x, int y, int seriesIndex,
						Entry<String, Rectangle2D> stringEntry) {

					x += cellInsets.left;
					y += cellInsets.top;

					this.seriesIndex = seriesIndex;
					this.text = stringEntry.getKey();
					int height = (int) (stringEntry.getValue().getMaxY()
							- stringEntry.getValue().getMinY() + .5);
					int width = (int) (stringEntry.getValue().getWidth()
							+ colorSwatchWidth + colorSwatchPadding + .5);

					colorSwatchBounds = new Rectangle(x, y, colorSwatchWidth,
							height - 2);
					textX = colorSwatchBounds.x + colorSwatchBounds.width
							+ colorSwatchPadding;
					textY = (int) (y - stringEntry.getValue().getY() + .5);
					cellBounds = new Rectangle(x, y, width, height);
				}

				public void paint(Graphics2D g) {
					g.setColor(getSeriesColor(seriesIndex));
					g.fill(colorSwatchBounds);
					g.setColor(Color.black);
					g.draw(colorSwatchBounds);

					g.drawString(text, textX, textY);
				}
			}

			Cell[][] cells;

			int imageWidth;
			Insets cellInsets = new Insets(5, 5, 5, 5);

			public Layout(int columnCount, int imageWidth,
					Map<String, Rectangle2D> stringBounds) {
				int rows = (int) (Math.ceil(
						((double) stringBounds.size()) / ((double) columnCount))
						+ .5);
				cells = new Cell[rows][columnCount];

				int rowIndex = 0;
				int columnIndex = 0;
				int x = 0;
				int y = 0;
				int seriesIndex = 0;
				for (Entry<String, Rectangle2D> stringEntry : stringBounds
						.entrySet()) {
					cells[rowIndex][columnIndex] = new Cell(x, y, seriesIndex,
							stringEntry);

					rowIndex++;
					if (rowIndex == rows) {
						rowIndex = 0;
						columnIndex++;
						x = getWidth();
						y = 0;
					} else {
						y += cells[rowIndex - 1][columnIndex].cellBounds.height
								+ cellInsets.top + cellInsets.bottom;
					}

					seriesIndex++;
				}

				this.imageWidth = imageWidth;
			}

			public boolean isValid() {
				return getWidth() < imageWidth;
			}

			public Rectangle getBounds() {
				Rectangle sum = null;
				for (int row = 0; row < cells.length; row++) {
					for (int column = 0; column < cells[row].length; column++) {
						if (cells[row][column] != null) {
							if (sum == null) {
								sum = new Rectangle(
										cells[row][column].cellBounds);
							} else {
								sum.add(cells[row][column].cellBounds);
							}
						}
					}
				}
				return sum;
			}

			public int getWidth() {
				return getBounds().width + cellInsets.right;
			}

			public int getHeight() {
				return getBounds().height + cellInsets.bottom;
			}
		}

		Layout layout;

		public Legend(Font font, FontRenderContext fontRenderContext,
				Dimension imageSize) {
			Map<String, Rectangle2D> stringBounds = new LinkedHashMap<>();
			for (String key : dataContainer.data.keySet()) {
				Rectangle2D bounds = font.getStringBounds(key,
						fontRenderContext);
				stringBounds.put(key, bounds);
			}
			layout = layoutCells(stringBounds, imageSize.width);
		}

		private Layout layoutCells(Map<String, Rectangle2D> stringBounds,
				int imageWidth) {
			for (int columnCount = stringBounds
					.size(); columnCount >= 1; columnCount--) {
				Layout layout = new Layout(columnCount, imageWidth,
						stringBounds);
				if (layout.isValid() || columnCount == 1) {
					return layout;
				}
			}
			// we should only reach this in bizarre cases where there is no
			// chart data
			return null;
		}

		public int getHeight() {
			if (layout == null)
				return 0;
			return layout.getHeight();
		}

		public void paint(Graphics2D g, Rectangle emptySpace) {
			if (layout == null)
				return;

			g = (Graphics2D) g.create();
			g.translate(emptySpace.x, emptySpace.y);

			for (int y = 0; y < layout.cells.length; y++) {
				for (int x = 0; x < layout.cells[y].length; x++) {
					if (layout.cells[y][x] != null)
						layout.cells[y][x].paint(g);
				}
			}
			g.dispose();
		}

	}

	DataContainer dataContainer;

	public LineChartRenderer(Map<String, SortedMap<Double, Double>> data) {
		dataContainer = new DataContainer(data);
	}

	public Color getSeriesColor(int seriesIndex) {
		return BarChartRenderer.colors[seriesIndex
				% BarChartRenderer.colors.length];
	}

	public BufferedImage render(Dimension maxSize) {
		Dimension imageSize = maxSize;
		BufferedImage bi = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		VerticalAxis verticalAxis = new VerticalAxis(g.getFont(),
				g.getFontRenderContext(), imageSize);
		HorizontalAxis horizontalAxis = new HorizontalAxis(g.getFont(),
				g.getFontRenderContext(), imageSize);
		Legend legend = new Legend(g.getFont(), g.getFontRenderContext(),
				imageSize);

		int topY = (int) (g.getFont().getSize2D() * 2 / 3);
		int bottomY = bi.getHeight() - horizontalAxis.getHeight()
				- legend.getHeight();
		int leftX = verticalAxis.getWidth();
		int rightX = bi.getWidth()
				- horizontalAxis.getRightPadding(g.getFontRenderContext());
		Rectangle chartRect = new Rectangle(leftX, topY, rightX - leftX,
				bottomY - topY);

		AffineTransform tx = TransformUtils.createAffineTransform(0,
				dataContainer.rangeMax, 0, 0, dataContainer.domainMax, 0,
				chartRect.getMinX(), chartRect.getMinY(), chartRect.getMinX(),
				chartRect.getMaxY(), chartRect.getMaxX(), chartRect.getMaxY());

		verticalAxis.paint(g, chartRect, tx);
		horizontalAxis.paint(g, chartRect, tx);

		g.setStroke(new BasicStroke(1));
		int index = 0;
		for (Entry<String, SortedMap<Double, Double>> entry : dataContainer.data
				.entrySet()) {
			Path2D path = new Path2D.Double();
			for (Entry<Double, Double> entry2 : entry.getValue().entrySet()) {
				if (path.getCurrentPoint() == null) {
					path.moveTo(entry2.getKey(), entry2.getValue());
				} else {
					path.lineTo(entry2.getKey(), entry2.getValue());
				}
			}
			g.setColor(getSeriesColor(index));
			path.transform(tx);
			g.draw(path);
			index++;
		}

		int k = chartRect.y + chartRect.height + horizontalAxis.getHeight();
		Rectangle emptySpace = new Rectangle(0, k, bi.getWidth(),
				bi.getHeight() - k);
		legend.paint(g, emptySpace);

		g.dispose();
		return bi;
	}
}
