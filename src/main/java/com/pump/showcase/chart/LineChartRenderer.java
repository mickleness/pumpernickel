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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
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

	DataContainer dataContainer;

	public LineChartRenderer(Map<String, SortedMap<Double, Double>> data) {
		dataContainer = new DataContainer(data);
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

		int topY = (int) (g.getFont().getSize2D() * 2 / 3);
		int bottomY = bi.getHeight() - horizontalAxis.getHeight();
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
			g.setColor(BarChartRenderer.colors[index
					% BarChartRenderer.colors.length]);
			path.transform(tx);
			g.draw(path);
			index++;
		}

		g.dispose();
		return bi;
	}
}
