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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.pump.inspector.InspectorGridBagLayout;

public class AddRulesTest extends BasicTestElement {
	static class Case {
		List<Shape> shapes = new ArrayList<Shape>();
		String name;
		BufferedImage image;

		public Case(Shape[] shapes, String name) {
			for (int a = 0; a < shapes.length; a++) {
				this.shapes.add(shapes[a]);
			}
			this.name = name;
			image = render();
		}

		public Case(String name) {
			URL url = AddRulesTest.class.getResource(name);
			InputStream in = null;
			try {
				in = url.openStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				String s = br.readLine();
				while (s != null) {
					add(s);
					s = br.readLine();
				}
			} catch (IOException e) {
				RuntimeException e2 = new RuntimeException();
				e2.initCause(e);
				throw e2;
			} finally {
				try {
					in.close();
				} catch (Exception e) {
				}
				;
			}
			image = scale(render(), new Dimension(240, 240));

			StringBuffer sb = new StringBuffer(name.length());
			for (int a = 0; a < name.length(); a++) {
				char c = name.charAt(a);
				if (a != 0 && Character.isUpperCase(c)) {
					sb.append(' ');
				}
				sb.append(c);
			}
			this.name = sb.toString();
		}

		/**
		 * This is a VERY expensive call that will break apart clip art
		 * represented in massive strings with multiple subpaths.
		 */
		@SuppressWarnings("unused")
		private Case split() {
			List<Shape> newShapes = new ArrayList<Shape>();
			for (int a = 0; a < shapes.size(); a++) {
				Shape shape = shapes.get(a);
				split(shape, newShapes);
			}
			Case returnValue = new Case(newShapes.toArray(new Shape[newShapes
					.size()]), name + " Split");
			try {
				returnValue.write();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return returnValue;
		}

		/**
		 * Serializes this case as a ".shapes" file.
		 * 
		 * @throws IOException
		 *             if an IO problem occurs.
		 */
		public void write() throws IOException {
			File file = new File(name + ".shapes");
			file.createNewFile();
			OutputStream out = null;
			try {
				out = new FileOutputStream(file);
				PrintStream ps = new PrintStream(out);
				for (int a = 0; a < shapes.size(); a++) {
					Shape shape = shapes.get(a);
					ps.println(ShapeStringUtils.toString(shape));
				}
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
				}
			}
		}

		private static void split(Shape shape, List<Shape> newShapes) {
			String s = ShapeStringUtils.toString(shape).trim();
			List<Object> subpaths = new ArrayList<Object>();
			while (s.indexOf('m') != -1) {
				int i = s.indexOf('m');
				int i2 = s.indexOf('m', i + 1);
				if (i2 != -1) {
					subpaths.add(s.substring(i, i2).trim());
					s = s.substring(i2).trim();
				} else {
					subpaths.add(s);
					s = "";
				}
			}

			for (int b = 0; b < subpaths.size(); b++) {
				String source = (String) subpaths.get(b);
				AreaX area = new AreaX(
						ShapeStringUtils.createGeneralPath(source));
				subpaths.set(b, new Object[] { source, area });
			}
			for (int b = 0; b < subpaths.size(); b++) {
				Object[] array1 = (Object[]) subpaths.get(b);
				for (int c = b + 1; c < subpaths.size(); c++) {
					Object[] array2 = (Object[]) subpaths.get(c);
					int rel = ((AreaX) array1[1]).getRelationship(null,
							((AreaX) array2[1]), null);
					if (rel != AreaX.RELATIONSHIP_NONE) {
						String newPath = ((String) array1[0]) + " "
								+ ((String) array2[0]);
						array1[0] = newPath;
						AreaX t = new AreaX(
								ShapeStringUtils.createGeneralPath(newPath));
						array1[1] = t;
						subpaths.remove(c);
						c = b;
					}
				}
				String source = (String) array1[0];
				newShapes.add(ShapeStringUtils.createGeneralPath(source));
			}
		}

		private void add(String shape) {
			add(ShapeStringUtils.createGeneralPath(shape));
		}

		private void add(Shape shape) {
			shapes.add(shape);
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}

		public Rectangle2D getBounds() {
			Rectangle2D sum = null;
			for (int a = 0; a < shapes.size(); a++) {
				Shape shape = shapes.get(a);
				try {
					Rectangle2D r = ShapeBounds.getBounds(shape);
					if (sum == null) {
						sum = r;
					} else {
						sum.add(r);
					}
				} catch (EmptyPathException e) {

				}
			}
			return sum;
		}

		public BufferedImage render() {
			Rectangle2D bounds = getBounds();
			Rectangle r = bounds.getBounds();
			BufferedImage bi = new BufferedImage(r.width, r.height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.translate(-r.x, -r.y);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			for (int a = 0; a < shapes.size(); a++) {
				float hue = (a) / 11f;
				float bri = .75f + .25f * a / ((shapes.size()));
				Shape shape = shapes.get(a);
				g.setColor(new Color(Color.HSBtoRGB(hue, .75f, bri)));
				g.fill(shape);
			}
			g.dispose();
			return bi;
		}
	}

	JLabel iconLabel = new JLabel();
	List<Case> cases = new ArrayList<Case>();
	JLabel caseLabel = new JLabel("Case:");
	JComboBox comboBox;
	PrintStream printStream;

	public AddRulesTest(PrintStream stream) {
		printStream = stream;

		// define the cases:
		cases.add(new Case("Ambulance.shapes"));
		cases.add(new Case("Book.shapes"));
		cases.add(new Case("Campfire.shapes"));
		// cases.add(new Case("Chalet.shapes"));
		cases.add(new Case("Cornucopia.shapes"));
		cases.add(new Case("Dinosaur.shapes"));
		// cases.add(new Case("Fireworks.shapes"));
		cases.add(new Case("KingTut.shapes"));
		cases.add(new Case("Kitten.shapes"));
		cases.add(new Case("Library.shapes"));
		cases.add(new Case("Lincoln.shapes"));
		cases.add(new Case("Lion.shapes"));
		cases.add(new Case("Pancakes.shapes"));
		cases.add(new Case("RomanRelief.shapes"));
		cases.add(new Case("SpaceShuttle.shapes"));
		cases.add(new Case("Tiger.shapes"));
		cases.add(new Case("Truck.shapes"));
		cases.add(new Case("Washington.shapes"));
		cases.add(new Case("Windmill.shapes"));
		cases.add(new Case("Wren.shapes"));
		cases.add(new Case("BookSplit.shapes"));
		cases.add(new Case("KingTutSplit.shapes"));
		cases.add(new Case("TruckSplit.shapes"));
		cases.add(new Case("WashingtonSplit.shapes"));
		cases.add(new Case("WindmillSplit.shapes"));
		comboBox = new JComboBox();
		comboBox.addItem("All");
		for (int a = 0; a < cases.size(); a++) {
			comboBox.addItem(cases.get(a));
		}

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object value = comboBox.getSelectedItem();
				if (value instanceof Case) {
					iconLabel.setIcon(new ImageIcon(((Case) value).image));
				} else {
					iconLabel.setIcon(null);
				}
			}
		});
	}

	public static BufferedImage scale(BufferedImage render, Dimension maxSize) {
		Dimension imageSize = new Dimension(render.getWidth(),
				render.getHeight());
		Dimension newSize = scaleDimensionsProportionally(imageSize, maxSize);
		BufferedImage newImage = new BufferedImage(newSize.width,
				newSize.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		double sx = ((double) newSize.width) / ((double) imageSize.width);
		double sy = ((double) newSize.height) / ((double) imageSize.height);
		g.scale(sx, sy);
		g.drawImage(render, 0, 0, null);
		g.dispose();
		return newImage;
	}

	/**
	 * Copied from Scaling.java to simplify class dependencies. This is a
	 * convenience method to calculate how to scale down an image
	 * proportionally.
	 * 
	 * @param originalSize
	 *            the original image dimensions.
	 * @param maxSize
	 *            the maximum new dimensions.
	 * @return dimensions that are <code>maxSize</code> or smaller.
	 */
	public static Dimension scaleDimensionsProportionally(
			Dimension originalSize, Dimension maxSize) {
		float widthRatio = ((float) maxSize.width)
				/ ((float) originalSize.width);
		float heightRatio = ((float) maxSize.height)
				/ ((float) originalSize.height);
		int w, h;
		if (widthRatio < heightRatio) {
			w = maxSize.width;
			h = (int) (widthRatio * originalSize.height);
		} else {
			h = maxSize.height;
			w = (int) (heightRatio * originalSize.width);
		}
		return new Dimension(w, h);
	}

	@Override
	public JComponent getComponent() {
		if (panel == null) {
			panel = new JPanel(new GridBagLayout());
			panel.setOpaque(false);
			JPanel controls = new JPanel();
			controls.setOpaque(false);

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(8, 8, 8, 8);
			panel.add(description, c);
			c.gridwidth = 1;
			c.weightx = 1;
			c.weighty = 1;
			c.gridy++;
			c.anchor = GridBagConstraints.NORTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			panel.add(controls, c);
			c.gridx++;
			panel.add(iconLabel, c);

			InspectorGridBagLayout layout = new InspectorGridBagLayout(controls);
			addControls(layout);
			layout.addRow(null, progress, false);
			layout.addRow(start, SwingConstants.LEFT, false);
			layout.addRow(cancel, SwingConstants.LEFT, false);
		}
		return panel;
	}

	@Override
	public void addControls(InspectorGridBagLayout layout) {
		layout.addRow(caseLabel, comboBox, false);
	}

	@Override
	public void doTest() {
		Object value = comboBox.getSelectedItem();
		Case[] cases;
		if (value instanceof Case) {
			cases = new Case[] { (Case) value };
		} else { // all
			cases = this.cases.toArray(new Case[this.cases.size()]);
		}

		AreaXRules[] rules = new AreaXRules[] { new AreaXRules(),
				new BoundsRules(true, false), new BoundsRules(false, true),
				new BoundsRules(true, true) };
		String[] names = new String[] { "No Rules", "Inner", "Merging", "Both" };

		long[][] times = new long[cases.length][rules.length + 1];
		long[] tempList = new long[5]; // how many times we repeat each method

		Area goldenStandard = null;

		for (int a = 0; a < cases.length; a++) {
			if (cases.length > 1)
				comboBox.setSelectedIndex(a + 1);
			float base = ((float) a) / ((float) cases.length);

			for (int b = 0; b < tempList.length; b++) {
				System.gc();
				System.runFinalization();
				if (cancelled)
					return;

				float m = base + ((float) b) / ((float) tempList.length)
						/ (cases.length) / (rules.length + 1f);
				progress.setValue((int) (m * (progress.getMaximum() - progress
						.getMinimum())));

				tempList[b] = System.currentTimeMillis();
				Area sum = new Area();
				for (int c = 0; c < cases[a].shapes.size(); c++) {
					sum.add(new Area(cases[a].shapes.get(c)));
				}
				goldenStandard = sum;
				sum.getPathIterator(null);
				tempList[b] = System.currentTimeMillis() - tempList[b];
			}
			Arrays.sort(tempList);
			times[a][0] = tempList[tempList.length / 2];

			for (int i = 0; i < rules.length; i++) {
				AreaX copy = null;
				for (int b = 0; b < tempList.length; b++) {
					System.gc();
					System.runFinalization();
					if (cancelled)
						return;

					float m = base + (i + 1f) / (rules.length + 1f)
							/ (cases.length) + ((float) b)
							/ ((float) tempList.length) / (cases.length) / 3f;
					progress.setValue((int) (m * (progress.getMaximum() - progress
							.getMinimum())));

					tempList[b] = System.currentTimeMillis();
					AreaX sum = new AreaX();
					sum.setRules(rules[i]);
					for (int c = 0; c < cases[a].shapes.size(); c++) {
						sum.add((cases[a].shapes.get(c)));
					}
					sum.getPathIterator(null);
					copy = sum;
					tempList[b] = System.currentTimeMillis() - tempList[b];

				}
				if (equals(goldenStandard, copy, printStream) == false) {
					reportFailure(cases[a].getName() + " - " + names[i],
							goldenStandard, copy);
				}
				Arrays.sort(tempList);
				times[a][i + 1] = tempList[tempList.length / 2];
			}
		}

		if (cases.length > 1)
			comboBox.setSelectedIndex(0);

		String header = new String("\tArea");
		for (int a = 0; a < names.length; a++) {
			header = header + "\t" + names[a];
		}
		for (int a = 0; a < names.length; a++) {
			header = header + "\t" + names[a] + " (%)";
		}
		printStream.println(header);
		for (int a = 0; a < times.length; a++) {
			String line = cases[a].getName() + "\t" + times[a][0];
			for (int i = 1; i < times[a].length; i++) {
				line = line + "\t" + times[a][i];
			}
			for (int i = 1; i < times[a].length; i++) {
				double percent = ((double) times[a][i])
						/ ((double) times[a][0]) * 100;
				line = line + "\t" + percent;
			}
			printStream.println(line);
		}
	}

	static int ctr = 0;

	private void reportFailure(String id, Shape good, Shape bad) {
		Rectangle2D r = good.getBounds2D();
		r.add(bad.getBounds2D());

		BufferedImage image = new BufferedImage(500, 500,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.transform(RectangularTransform.create(r,
				new Rectangle(0, 0, image.getWidth(), image.getHeight())));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.blue);
		g.fill(good);
		g.setColor(new Color(255, 0, 0, 128));
		g.fill(bad);
		g.dispose();
		printStream.println("the resulting shape for \"" + id
				+ "\" wasn't correct.");
		try {
			ImageIO.write(image, "png", new File("comparison " + (ctr++)
					+ ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "This examines the time taken to calculate the total outline of a variety of clip art using 3 different approaches: the Area class, the AreaX class with no rules applied, and the AreaX class with merging rules applied.\n\n"
				+ "(Only the shape data of each piece of clip art was preserved.  As you thumb through the list of clip art a thumbnail will appear to the right using vivid semi-random colors just to communicate the basic composition of the clip art.)";
	}

	@Override
	public String getName() {
		return "Add Rules Test";
	}

}