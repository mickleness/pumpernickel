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
package com.pump.swing;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

import com.pump.math.function.ConstantFunction;
import com.pump.math.function.Function;
import com.pump.math.function.PolynomialFunction;
import com.pump.plaf.ScrollUIEffect;

public class ScrollPaneUtils {

	public static ScrollUIEffect animateHorizontalBounce(
			JScrollPane scrollPane, int pixels) {
		Point p = scrollPane.getViewport().getViewPosition();

		Function xFunction = PolynomialFunction.createFit(new double[] { 0, .5,
				1 }, new double[] { p.x, p.x + pixels, p.x });
		Function yFunction = new ConstantFunction(p.y);

		return new ScrollUIEffect(scrollPane, xFunction, yFunction, 300);
	}

	public static ScrollUIEffect animateScroll(JScrollPane scrollPane, int dx,
			int dy, int bounce) {
		Point p = scrollPane.getViewport().getViewPosition();
		p.x += dx;
		p.y += dy;
		return animateScroll(scrollPane, p, bounce);
	}

	public static ScrollUIEffect animateScroll(JScrollPane scrollPane,
			Point finalViewPosition, int bounce) {
		int startX = scrollPane.getViewport().getViewPosition().x;
		int startY = scrollPane.getViewport().getViewPosition().y;

		int endX = finalViewPosition.x;
		int endY = finalViewPosition.y;

		// we take direction into account, but the caller may not realize that:
		bounce = Math.abs(bounce);

		Function xFunction;
		if (endX == startX) {
			xFunction = new ConstantFunction(endX);
		} else {
			int peakX = endX > startX ? endX + bounce : endX - bounce;
			xFunction = PolynomialFunction.createFit(new double[] { 0, .8, 1 },
					new double[] { startX, peakX, endX });
		}

		Function yFunction;
		if (endY == startY) {
			yFunction = new ConstantFunction(endY);
		} else {
			int peakY = endY > startY ? endY + bounce : endY - bounce;
			yFunction = PolynomialFunction.createFit(new double[] { 0, .8, 1 },
					new double[] { startY, peakY, endY });
		}

		return new ScrollUIEffect(scrollPane, xFunction, yFunction, 400);
	}

	private static List<Animation> animations = new ArrayList<Animation>();
	private static ActionListener timerListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			synchronized (animations) {
				int ctr = 0;
				while (ctr < animations.size()) {
					Animation anim = animations.get(ctr);
					if (anim.iterate()) {
						animations.remove(ctr);
					} else {
						ctr++;
					}
				}
				if (animations.size() == 0) {
					timer.stop();
				}
			}
		}
	};
	private static Timer timer = new Timer(20, timerListener);

	private static class Animation {
		long startTime;
		long endTime;
		Function xFunction;
		Function yFunction;
		JScrollPane scrollPane;

		Animation(long startTime, long endTime, Function xFunction,
				Function yFunction, JScrollPane scrollPane) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.xFunction = xFunction;
			this.yFunction = yFunction;
			this.scrollPane = scrollPane;
		}

		public boolean iterate() {
			long time = System.currentTimeMillis();
			int x, y;
			boolean returnValue;
			if (time < startTime) {
				x = (int) (xFunction.evaluate(0) + .5);
				y = (int) (yFunction.evaluate(0) + .5);
				returnValue = false;
			} else if (time > endTime) {
				x = (int) (xFunction.evaluate(1) + .5);
				y = (int) (yFunction.evaluate(1) + .5);
				returnValue = true;
			} else {
				long elapsed = time - startTime;
				long total = endTime - startTime;
				float f = ((float) elapsed) / ((float) total);
				x = (int) (xFunction.evaluate(f) + .5);
				y = (int) (yFunction.evaluate(f) + .5);
				returnValue = false;
			}

			scrollPane.getViewport().setViewPosition(new Point(x, y));

			return returnValue;
		}
	}

	private static class InvokeScrollToRunnable1 implements Runnable {
		JScrollPane scrollPane;
		JComponent[] components;

		public InvokeScrollToRunnable1(JScrollPane scrollPane,
				JComponent[] components) {
			this.scrollPane = scrollPane;
			this.components = components;
		}

		public void run() {
			animateScrollTo(scrollPane, components);
		}
	}

	public static void scrollTo(JScrollPane scrollPane, JComponent component) {
		scrollTo(scrollPane, new JComponent[] { component });
	}

	public static void scrollTo(JScrollPane scrollPane, JComponent[] components) {
		Point p = getFinalPosition(scrollPane, components);
		if (p != null)
			scrollPane.getViewport().setViewPosition(p);
	}

	public static void animateScrollTo(JScrollPane scrollPane,
			JComponent component, int pixelsPerSecond) {
		animateScrollTo(scrollPane, new JComponent[] { component },
				pixelsPerSecond);
	}

	public static void animateScrollTo(JScrollPane scrollPane,
			JComponent component) {
		animateScrollTo(scrollPane, new JComponent[] { component });
	}

	private static Point getFinalPosition(JScrollPane scrollPane,
			JComponent[] components) {
		Rectangle sum = null;
		for (int a = 0; a < components.length; a++) {
			if (components[a] == null)
				throw new NullPointerException();
			Rectangle bounds = components[a].getBounds();
			Border border = components[a].getBorder();
			if (border != null) {
				Insets i = border.getBorderInsets(components[a]);
				bounds.x -= i.left;
				bounds.y -= i.top;
				bounds.width += i.left + i.top;
				bounds.height += i.top + i.bottom;
			}
			Container container = components[a].getParent();
			while (container.getParent() != scrollPane.getViewport()) {
				Point p = container.getLocation();
				bounds.x += p.x;
				bounds.y += p.y;
				container = container.getParent();
				if (container == null)
					throw new IllegalArgumentException(
							"a component is not a descendant of the scrollpane");
			}
			if (sum == null) {
				sum = bounds;
			} else {
				sum.add(bounds);
			}
		}

		if (scrollPane.getViewport().getViewRect().contains(sum))
			return null;

		int ctr = 0;
		while (ctr < animations.size()) {
			Animation anim = animations.get(ctr);
			if (anim.scrollPane == scrollPane) {
				animations.remove(ctr);
			} else {
				ctr++;
			}
		}

		int startX = scrollPane.getViewport().getViewPosition().x;
		int endX;
		if (sum.x + sum.width / 2 < startX) {
			endX = sum.x;
		} else {
			endX = sum.x - scrollPane.getViewport().getWidth() + sum.width;
		}

		int startY = scrollPane.getViewport().getViewPosition().y;
		int endY;
		if (sum.y + sum.height / 2 < startY) {
			endY = sum.y;
		} else {
			endY = sum.y - scrollPane.getViewport().getHeight() + sum.height;
		}

		Point p = new Point(endX, endY);
		return p;
	}

	public static void animateScrollTo(JScrollPane scrollPane,
			JComponent[] components, int pixelsPerSecond) {
		if (SwingUtilities.isEventDispatchThread() == false) {
			SwingUtilities.invokeLater(new InvokeScrollToRunnable1(scrollPane,
					components));
			return;
		}

		synchronized (animations) {
			Point endLoc = getFinalPosition(scrollPane, components);
			if (endLoc == null)
				return;

			int startX = scrollPane.getViewport().getViewPosition().x;
			int startY = scrollPane.getViewport().getViewPosition().y;

			int endX = endLoc.x;
			int endY = endLoc.y;

			Function xFunction = PolynomialFunction.createFit(new double[] { 0,
					.2, .8, 1 }, new double[] { startX,
					startX * .8 + endX * .2, startX * .2 + endX * .8, endX });
			Function yFunction = PolynomialFunction.createFit(new double[] { 0,
					.2, .8, 1 }, new double[] { startY,
					startY * .8 + endY * .2, startY * .2 + endY * .8, endY });

			double distance = Point2D.distance(startX, startY, endX, endY);
			long startTime = System.currentTimeMillis();
			long endTime = (long) (startTime + distance / pixelsPerSecond
					* 1000);

			Animation newAnimation = new Animation(startTime, endTime,
					xFunction, yFunction, scrollPane);
			animations.add(newAnimation);
			if (timer.isRunning() == false) {
				timer.start();
			}
		}
	}

	public static void animateScrollTo(JScrollPane scrollPane,
			JComponent[] components) {
		if (SwingUtilities.isEventDispatchThread() == false) {
			SwingUtilities.invokeLater(new InvokeScrollToRunnable1(scrollPane,
					components));
			return;
		}

		synchronized (animations) {
			Point endLoc = getFinalPosition(scrollPane, components);
			if (endLoc == null)
				return;

			int startX = scrollPane.getViewport().getViewPosition().x;
			int startY = scrollPane.getViewport().getViewPosition().y;

			int endX = endLoc.x;
			int endY = endLoc.y;
			int peakX = endX > startX ? endX + 5 : endX - 5;
			int peakY = endY > startY ? endY + 5 : endY - 5;

			Function xFunction = PolynomialFunction.createFit(new double[] { 0,
					.8, 1 }, new double[] { startX, peakX, endX });
			Function yFunction = PolynomialFunction.createFit(new double[] { 0,
					.8, 1 }, new double[] { startY, peakY, endY });

			long startTime = System.currentTimeMillis();
			long endTime;
			if (scrollPane.isShowing() == false) {
				endTime = startTime + 1;
			} else {
				endTime = startTime + 400;
			}

			Animation newAnimation = new Animation(startTime, endTime,
					xFunction, yFunction, scrollPane);
			animations.add(newAnimation);
			if (timer.isRunning() == false) {
				timer.start();
			}
		}
	}

}