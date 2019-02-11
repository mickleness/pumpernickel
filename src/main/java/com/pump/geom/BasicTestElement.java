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
package com.pump.geom;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.pump.inspector.InspectorGridBagLayout;

public abstract class BasicTestElement extends TestElement {
	JTextArea description = new JTextArea(getDescription());
	JProgressBar progress = new JProgressBar();
	JButton start = new JButton("Start");
	JButton cancel = new JButton("Cancel");
	JPanel panel;
	TestThread thread;
	boolean cancelled = false;

	ActionListener buttonListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == start) {
				start.setEnabled(false);
				thread = new TestThread();
				thread.start();
			} else if (e.getSource() == cancel) {
				cancelled = true;
			}
		}
	};

	public BasicTestElement() {
		description.setEditable(false);
		description.setWrapStyleWord(true);
		description.setOpaque(false);
		description.setLineWrap(true);

		progress.setEnabled(false);
		cancel.setEnabled(false);

		start.addActionListener(buttonListener);
		cancel.addActionListener(buttonListener);
	}

	public abstract void addControls(InspectorGridBagLayout layout);

	public abstract String getDescription();

	@Override
	public JComponent getComponent() {
		if (panel == null) {
			panel = new JPanel();

			InspectorGridBagLayout layout = new InspectorGridBagLayout(panel);
			layout.addRow(description, SwingConstants.CENTER, true);
			addControls(layout);
			layout.addRow(null, progress, false);
			layout.addRow(start, SwingConstants.LEFT, false);
			layout.addRow(cancel, SwingConstants.LEFT, false);
		}
		return panel;
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	/**
	 * This method is responsible for updating the progress bar and checking the
	 * cancelled boolean.
	 */
	public abstract void doTest();

	private static int[] array1 = new int[image1.getWidth()];
	private static int[] array2 = new int[image1.getWidth()];

	public static boolean isEmpty(BufferedImage bi) {

		int[] row = new int[bi.getWidth()];

		for (int y = 0; y < bi.getHeight(); y++) {
			bi.getRaster().getDataElements(0, y, row.length, 1, row);
			for (int x = 0; x < row.length; x++) {
				int alpha = (row[x] >> 24) & 0xff;

				if (alpha > 20) {
					return false;
				}
			}
		}

		return true;
	}

	public synchronized static boolean equals(Shape s1, Shape s2,
			PrintStream printStream) {
		Rectangle2D sum = s1.getBounds2D();
		sum.add(s2.getBounds2D());

		clear(image1);
		Graphics2D g = image1.createGraphics();
		g.transform(RectangularTransform.create(sum,
				new Rectangle(0, 0, image1.getWidth(), image1.getHeight())));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fill(s1);
		g.dispose();

		clear(image2);
		g = image2.createGraphics();
		g.transform(RectangularTransform.create(sum,
				new Rectangle(0, 0, image1.getWidth(), image1.getHeight())));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fill(s1);
		g.dispose();

		return equals(image1, image2, printStream);
	}

	/**
	 * Check to see if two images are equal, give or take a 3x3 cluster of
	 * pixels. This assumes both images are black-on-transparent.
	 * 
	 * @param bi1
	 * @param bi2
	 */
	public static boolean equals(BufferedImage bi1, BufferedImage bi2,
			PrintStream printStream) {
		if (bi1.getWidth() != array1.length)
			throw new IllegalArgumentException();
		if (bi2.getWidth() != array2.length)
			throw new IllegalArgumentException();
		if (bi1.getHeight() != bi2.getHeight())
			throw new IllegalArgumentException();

		HashSet<Point> errors = null;

		for (int y = 0; y < bi1.getHeight(); y++) {
			bi1.getRaster().getDataElements(0, y, array1.length, 1, array1);
			bi2.getRaster().getDataElements(0, y, array2.length, 1, array2);
			for (int x = 0; x < array1.length; x++) {
				int alpha1 = (array1[x] >> 24) & 0xff;
				int alpha2 = (array2[x] >> 24) & 0xff;

				int diff = alpha1 - alpha2;
				if (diff > 250) {
					if (errors == null)
						errors = new HashSet<Point>();
					errors.add(new Point(x, y));
				}
			}
		}

		if (errors != null) {
			Point p2 = new Point();
			Iterator<Point> i = errors.iterator();
			while (i.hasNext()) {
				Point p = i.next();
				p2.x = p.x - 1;
				p2.y = p.y - 1;
				if (errors.contains(p2)) {
					p2.x = p.x;
					if (errors.contains(p2)) {
						p2.x = p.x + 1;
						if (errors.contains(p2)) {
							p2.y = p.y;
							p2.x = p.x - 1;
							if (errors.contains(p2)) {
								p2.x = p.x + 1;
								if (errors.contains(p2)) {
									p2.y = p.y + 1;
									p2.x = p.x - 1;
									if (errors.contains(p2)) {
										p2.x = p.x;
										if (errors.contains(p2)) {
											p2.x = p.x + 1;
											if (errors.contains(p2)) {
												try {
													File f1 = new File(
															"problem" + (ctr++)
																	+ ".png");
													File f2 = new File(
															"problem" + (ctr++)
																	+ ".png");
													ImageIO.write(bi1, "png",
															f1);
													ImageIO.write(bi2, "png",
															f2);
													printStream
															.println("\tx = "
																	+ p.x
																	+ ", y = "
																	+ p.y);
													printStream
															.println("\t"
																	+ f1.getAbsolutePath());
													printStream
															.println("\t"
																	+ f2.getAbsolutePath());
												} catch (Exception e) {
													e.printStackTrace();
												}
												return false;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	private static int ctr;

	class TestThread extends Thread {
		public TestThread() {
			super(BasicTestElement.this.getName());
		}

		protected void setEnabled(final Container container, final boolean b) {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setEnabled(container, b);
					}
				});
				return;
			}
			for (int a = 0; a < container.getComponentCount(); a++) {
				Component c = container.getComponent(a);
				if (c instanceof JTextComponent || c instanceof JLabel) {
					// don't disable text
				} else {
					if (c == cancel || c == progress) {
						c.setEnabled(!b);
					} else {
						c.setEnabled(b);
					}
					if (c instanceof Container)
						setEnabled((Container) c, b);
				}
			}
			if (b)
				progress.setValue(0);
		}

		@Override
		public void run() {
			setEnabled(panel, false);
			try {
				cancelled = false;
				doTest();
			} finally {
				setEnabled(panel, true);
			}
		}
	}

	public static void clear(BufferedImage bi) {
		Graphics2D g = bi.createGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g.dispose();
	}

	public static void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}

	public static Shape createDiamond(float x, float y) {
		GeneralPath path = new GeneralPath();
		path.moveTo(x, y);
		path.lineTo(x + 15, y + 15);
		path.lineTo(x, y + 30);
		path.lineTo(x - 15, y + 15);
		path.closePath();
		return path;
	}

	public static Shape createQuad(float x, float y) {
		GeneralPath path = new GeneralPath();
		path.moveTo(x, y);
		path.quadTo(x + 15, 0, x + 15, y + 15);
		path.quadTo(x + 15, y + 30, x, y + 30);
		path.quadTo(x - 15, y + 30, x - 15, y + 15);
		path.quadTo(x - 15, y, x, y);
		path.closePath();
		return path;
	}
}