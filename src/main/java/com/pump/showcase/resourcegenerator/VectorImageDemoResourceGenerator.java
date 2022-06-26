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
package com.pump.showcase.resourcegenerator;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.OutputStream;

import javax.swing.Icon;

import com.pump.graphics.vector.VectorImage;

public class VectorImageDemoResourceGenerator extends DemoResourceGenerator {

	@Override
	public void run(DemoResourceContext context) throws Exception {

		File jvgDir = context.getFile("resources/com/pump/showcase/demo/jvg"
				.replace("/", File.separator));
		context.indexDirectory(jvgDir);

		Icon[] icons = new Icon[] { new InhabitantsPiglet(704, 800),
				new KindergartenTeacher(1371, 1136), new HardDrive(361, 518),
				new FlashDrive(800, 800) };
		for (Icon icon : icons) {
			VectorImage img = new VectorImage();
			Graphics2D g = img.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();

			File file = new File(jvgDir, icon.getClass().getSimpleName() + "."
					+ VectorImage.FILE_EXTENSION.toLowerCase());
			try (OutputStream out = context.createFileOutputStream(file)) {
				img.save(out);
			}
			System.out.println("Wrote: " + file.getAbsolutePath());
		}

		context.removeOldFiles(jvgDir);
	}

}

/**
 * This class is based on ouput from
 * <a href="http://ebourg.github.io/flamingo-svg-transcoder/">Flamingo SVG
 * transcoder</a>.
 * <p>
 * The source image is available as public domain from <a href=
 * "https://openclipart.org/detail/207926/inhabitants-piglet">openclipart</a>.
 */
class InhabitantsPiglet implements Icon {

	/** The width of this icon. */
	private int width;

	/** The height of this icon. */
	private int height;

	/**
	 * Creates a new transcoded SVG image.
	 */
	public InhabitantsPiglet(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		double coef = Math.min((double) width / (double) 1,
				(double) height / (double) 1);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.scale(coef, coef);
		paint(g2);
		g2.dispose();
	}

	/**
	 * Paints the transcoded SVG image on the specified graphics context.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	private static void paint(Graphics2D g) {
		Shape shape = null;

		float origAlpha = 1.0f;

		java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();

		//
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0.024415255f, 0, 0, 0.024415255f,
				0.060134757f, 0));

		// _0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1.25f, 0, 0, -1.25f, 0, 40.958f));

		// _0_0

		// _0_0_0

		// _0_0_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(20.508, 4.001);
		((GeneralPath) shape).curveTo(20.559, 4.184, 20.625, 4.36, 20.706999,
				4.52);
		((GeneralPath) shape).curveTo(20.866999, 4.891, 21.012, 5.079,
				21.144999, 5.079);
		((GeneralPath) shape).curveTo(21.491999, 5.106, 21.878998, 5.184,
				22.304998, 5.321);
		((GeneralPath) shape).curveTo(22.651999, 5.454, 22.839998, 5.505,
				22.866999, 5.481);
		((GeneralPath) shape).curveTo(23.026999, 5.3719997, 22.839998, 5.173,
				22.304998, 4.88);
		((GeneralPath) shape).lineTo(23.065998, 4.919);
		((GeneralPath) shape).curveTo(22.823997, 4.3050003, 22.558998,
				3.8910003, 22.265999, 3.6800003);
		((GeneralPath) shape).curveTo(21.945, 3.4660003, 21.612999, 3.3990004,
				21.265999, 3.4810004);
		((GeneralPath) shape).lineTo(20.508, 4.0010004);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xBA7763));
		g.fill(shape);

		// _0_0_0_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(20.625, 4.719);
		((GeneralPath) shape).lineTo(21.508, 4.3599997);
		((GeneralPath) shape).curveTo(22.144999, 4.2269998, 22.664, 4.4149995,
				23.066, 4.9189997);
		((GeneralPath) shape).curveTo(22.824, 4.305, 22.559, 3.8909998, 22.266,
				3.6799998);
		((GeneralPath) shape).curveTo(21.945002, 3.4659998, 21.613, 3.399,
				21.266, 3.481);
		((GeneralPath) shape).lineTo(20.547, 4.079);
		((GeneralPath) shape).lineTo(20.625, 4.719);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x9E6554));
		g.fill(shape);

		// _0_0_0_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(14.027, 9.2);
		((GeneralPath) shape).curveTo(13.574, 10.051, 13.332001, 10.559, 13.305,
				10.719);
		((GeneralPath) shape).curveTo(13.254001, 11.227, 13.6640005, 11.505,
				14.547, 11.559);
		((GeneralPath) shape).curveTo(15.934, 11.641, 17.051, 11.212, 17.906,
				10.278);
		((GeneralPath) shape).curveTo(18.625, 9.481, 18.891, 8.665, 18.707,
				7.8409996);
		((GeneralPath) shape).curveTo(18.547, 7.0669994, 18.387001, 6.6139994,
				18.227001, 6.4809995);
		((GeneralPath) shape).lineTo(18.746, 6.1609993);
		((GeneralPath) shape).curveTo(19.227001, 5.945999, 19.812, 5.7589993,
				20.508, 5.597999);
		((GeneralPath) shape).lineTo(22.144999, 5.199999);
		((GeneralPath) shape).curveTo(21.984, 5.199999, 21.824, 5.183999,
				21.663998, 5.160999);
		((GeneralPath) shape).curveTo(21.292997, 5.105999, 21.050997, 4.9729986,
				20.944998, 4.758999);
		((GeneralPath) shape).curveTo(20.757998, 4.414999, 20.733997, 4.105999,
				20.866999, 3.840999);
		((GeneralPath) shape).curveTo(19.905998, 3.9729989, 18.772999, 4.199999,
				17.464998, 4.519999);
		((GeneralPath) shape).curveTo(14.905998, 5.133999, 13.690998, 5.8519993,
				13.823998, 6.6799994);
		((GeneralPath) shape).curveTo(14.011998, 7.0279994, 14.159999, 7.320999,
				14.265999, 7.5589995);
		((GeneralPath) shape).curveTo(14.425999, 8.011999, 14.347999, 8.559,
				14.0269985, 9.2);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(18.426, 5.118);
		((GeneralPath) shape).lineTo(19.027, 5.239);
		((GeneralPath) shape).curveTo(19.48, 5.2939997, 19.801, 5.278,
				19.984001, 5.2);
		((GeneralPath) shape).curveTo(20.281002, 5.067, 20.707, 4.7469997,
				21.266, 4.239);
		((GeneralPath) shape).curveTo(21.852001, 3.6799998, 22.105001,
				3.3209999, 22.027, 3.1609998);
		((GeneralPath) shape).curveTo(21.867, 2.9189997, 21.441, 3.0789998,
				20.746, 3.6409998);
		((GeneralPath) shape).lineTo(21.348, 2.6799998);
		((GeneralPath) shape).curveTo(20.546999, 2.415, 19.918, 2.3869998,
				19.465, 2.5979998);
		((GeneralPath) shape).curveTo(19.172, 2.7079997, 18.934, 2.919, 18.746,
				3.2389998);
		((GeneralPath) shape).lineTo(18.508, 3.6799998);
		((GeneralPath) shape).lineTo(18.425999, 5.118);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xBA7763));
		g.fill(shape);

		// _0_0_0_4
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(19.305, 4.399);
		((GeneralPath) shape).lineTo(19.586, 3.598);
		((GeneralPath) shape).curveTo(19.934, 3.04, 20.52, 2.7350001, 21.348,
				2.68);
		((GeneralPath) shape).curveTo(20.546999, 2.415, 19.918, 2.387, 19.465,
				2.598);
		((GeneralPath) shape).curveTo(19.172, 2.708, 18.934, 2.9190001, 18.746,
				3.239);
		((GeneralPath) shape).lineTo(18.508, 3.68);
		((GeneralPath) shape).lineTo(18.508, 4.118);
		((GeneralPath) shape).lineTo(19.305, 4.399);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x9E6554));
		g.fill(shape);

		// _0_0_0_5
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(14.547, 9.641);
		((GeneralPath) shape).lineTo(14.305, 10.801);
		((GeneralPath) shape).curveTo(14.945001, 12.344, 15.734, 12.891, 16.668,
				12.438);
		((GeneralPath) shape).curveTo(17.465, 12.094, 18.0, 11.399, 18.265999,
				10.36);
		((GeneralPath) shape).curveTo(18.800999, 8.278, 19.785, 6.305, 21.227,
				4.4379997);
		((GeneralPath) shape).curveTo(21.254, 4.415, 21.078, 4.305, 20.706999,
				4.1179996);
		((GeneralPath) shape).curveTo(19.933998, 3.6649995, 19.210999,
				3.6409996, 18.546999, 4.0399995);
		((GeneralPath) shape).curveTo(18.491999, 4.3329997, 18.370998,
				4.6799994, 18.188, 5.0789995);
		((GeneralPath) shape).curveTo(17.785, 5.9069996, 17.215, 6.5979996,
				16.465, 7.1609993);
		((GeneralPath) shape).curveTo(15.508, 7.879999, 14.867001, 8.707999,
				14.547, 9.640999);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_6
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(15.266, 8.321);
		((GeneralPath) shape).lineTo(15.026999, 9.801001);
		((GeneralPath) shape).curveTo(15.026999, 9.907001, 15.266, 10.184001,
				15.745999, 10.641001);
		((GeneralPath) shape).curveTo(16.227, 11.067, 16.772999, 11.493001,
				17.387, 11.919001);
		((GeneralPath) shape).curveTo(17.839998, 11.415001, 18.133, 10.891001,
				18.265999, 10.360001);
		((GeneralPath) shape).curveTo(18.48, 9.426001, 18.890999, 8.360001,
				19.508, 7.1610007);
		((GeneralPath) shape).curveTo(19.133, 7.212001, 18.866999, 7.067001,
				18.706999, 6.719001);
		((GeneralPath) shape).curveTo(18.574, 6.3720007, 18.585999, 5.946001,
				18.745998, 5.4380007);
		((GeneralPath) shape).curveTo(18.984, 4.8010006, 19.464998, 4.278001,
				20.187998, 3.8800006);
		((GeneralPath) shape).curveTo(19.811998, 3.7740006, 19.425999,
				3.7740006, 19.026999, 3.8800006);
		((GeneralPath) shape).lineTo(18.546999, 4.0400004);
		((GeneralPath) shape).curveTo(18.491999, 4.3330007, 18.370998,
				4.6800003, 18.188, 5.0790005);
		((GeneralPath) shape).curveTo(17.785, 5.9070005, 17.215, 6.5980005,
				16.465, 7.1610003);
		((GeneralPath) shape).curveTo(16.121, 7.426, 15.719, 7.813, 15.266,
				8.321);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_7
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(18.266, 29.598);
		((GeneralPath) shape).curveTo(18.348001, 29.227, 18.305, 28.800999,
				18.145, 28.321);
		((GeneralPath) shape).curveTo(17.773, 27.359999, 16.934, 26.598, 15.625,
				26.039999);
		((GeneralPath) shape).curveTo(14.586, 25.587, 13.293, 26.172998, 11.746,
				27.800999);
		((GeneralPath) shape).lineTo(9.746, 30.359999);
		((GeneralPath) shape).lineTo(10.348001, 31.039999);
		((GeneralPath) shape).curveTo(10.984, 31.492998, 11.891001, 31.758999,
				13.066, 31.841);
		((GeneralPath) shape).curveTo(14.824, 31.946, 16.227, 31.614, 17.265999,
				30.841);
		((GeneralPath) shape).curveTo(17.772999, 30.438, 18.105, 30.028,
				18.265999, 29.598);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		// _0_0_0_8
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1.145, 7.001);
		((GeneralPath) shape).curveTo(1.6800001, 6.8129997, 1.824, 6.641, 1.586,
				6.481);
		((GeneralPath) shape).curveTo(1.2379999, 6.184, 0.82399994, 6.184,
				0.34799993, 6.481);
		((GeneralPath) shape).curveTo(0.05099994, 6.641, -0.055000067, 6.946,
				0.02699992, 7.399);
		((GeneralPath) shape).curveTo(0.05099992, 7.188, 0.17199992, 7.04,
				0.38699993, 6.9620004);
		((GeneralPath) shape).curveTo(0.65199995, 6.852, 0.9059999, 6.8680005,
				1.145, 7.0010004);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_9
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(2.227, 7.438);
		((GeneralPath) shape).curveTo(2.199, 7.426, 2.172, 7.4110003, 2.145,
				7.399);
		((GeneralPath) shape).lineTo(1.9449999, 7.321);
		((GeneralPath) shape).curveTo(2.188, 7.6410003, 2.1599998, 7.852, 1.867,
				7.962);
		((GeneralPath) shape).curveTo(1.707, 8.04, 1.5699999, 8.012, 1.4649999,
				7.88);
		((GeneralPath) shape).curveTo(1.387, 7.747, 1.3709999, 7.6140003,
				1.4259999, 7.481);
		((GeneralPath) shape).curveTo(1.6089998, 7.204, 1.879, 7.1879997,
				2.2269998, 7.4379997);
		((GeneralPath) shape).closePath();
		((GeneralPath) shape).moveTo(0.94500005, 8.360001);
		((GeneralPath) shape).curveTo(1.1600001, 8.626, 1.4260001, 8.747001,
				1.746, 8.719001);
		((GeneralPath) shape).curveTo(2.387, 8.696001, 2.652, 8.360001, 2.547,
				7.719001);
		((GeneralPath) shape).lineTo(2.547, 7.700001);
		((GeneralPath) shape).curveTo(2.625, 7.8010006, 2.73, 7.942001,
				2.8669999, 8.118001);
		((GeneralPath) shape).curveTo(3.0509999, 8.360001, 3.2269998, 8.493001,
				3.3869998, 8.520001);
		((GeneralPath) shape).curveTo(3.5979998, 8.548001, 3.8119998, 8.426002,
				4.027, 8.161001);
		((GeneralPath) shape).curveTo(4.238, 7.8950014, 4.32, 7.598001, 4.266,
				7.2780013);
		((GeneralPath) shape).curveTo(4.16, 6.5590014, 3.4799998, 6.2000012,
				2.2269998, 6.2000012);
		((GeneralPath) shape).curveTo(1.2109997, 6.227001, 0.6799997, 6.6530013,
				0.62499976, 7.4810014);
		((GeneralPath) shape).curveTo(0.59799975, 7.8290014, 0.7069998,
				8.118001, 0.94499975, 8.360002);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		// _0_0_0_10
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(3.387, 8.52);
		((GeneralPath) shape).curveTo(3.598, 8.548, 3.812, 8.426001, 4.027,
				8.161);
		((GeneralPath) shape).curveTo(4.238, 7.8950005, 4.32, 7.598, 4.266,
				7.2780004);
		((GeneralPath) shape).curveTo(4.16, 6.5590005, 3.4799998, 6.2000003,
				2.2269998, 6.2000003);
		((GeneralPath) shape).curveTo(1.6129997, 6.227, 1.1599997, 6.4150004,
				0.86699975, 6.7590003);
		((GeneralPath) shape).lineTo(1.0659997, 6.6410003);
		((GeneralPath) shape).curveTo(1.4379997, 6.372, 1.8789997, 6.2660003,
				2.3869996, 6.321);
		((GeneralPath) shape).curveTo(2.8669996, 6.372, 3.1329997, 6.5750003,
				3.1879997, 6.919);
		((GeneralPath) shape).curveTo(3.2379997, 7.184, 3.1879997, 7.454,
				3.0269997, 7.7190003);
		((GeneralPath) shape).lineTo(2.7849996, 8.04);
		((GeneralPath) shape).curveTo(2.9999995, 8.36, 3.1989996, 8.5199995,
				3.3869996, 8.5199995);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_11
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(18.387, 16.919);
		((GeneralPath) shape).curveTo(17.397999, 16.579, 16.758, 15.966001,
				16.464998, 15.079);
		((GeneralPath) shape).curveTo(16.171999, 14.200001, 16.198997, 12.653,
				16.546999, 10.438001);
		((GeneralPath) shape).curveTo(16.73, 8.278001, 16.32, 6.6920004,
				15.304999, 5.680001);
		((GeneralPath) shape).curveTo(14.32, 4.692001, 13.1449995, 3.9620008,
				11.785, 3.481001);
		((GeneralPath) shape).curveTo(10.266, 2.9460008, 8.785, 2.852001, 7.348,
				3.200001);
		((GeneralPath) shape).curveTo(8.73, 2.587001, 10.211, 2.344001, 11.785,
				2.481001);
		((GeneralPath) shape).curveTo(13.051, 2.5910008, 14.164, 2.934001,
				15.125, 3.520001);
		((GeneralPath) shape).curveTo(12.926001, 2.106001, 10.414, 1.9580009,
				7.586, 3.079001);
		((GeneralPath) shape).curveTo(4.281, 4.4380007, 2.7339997, 7.067001,
				2.9450002, 10.962001);
		((GeneralPath) shape).curveTo(3.1050003, 13.52, 4.254, 15.946001, 6.387,
				18.239);
		((GeneralPath) shape).curveTo(7.48, 19.387001, 8.547, 20.278, 9.586,
				20.919);
		((GeneralPath) shape).curveTo(16.613, 26.884, 19.547, 25.548, 18.387001,
				16.919);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_12
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(18.387, 16.919);
		((GeneralPath) shape).curveTo(18.754, 17.059, 19.054998, 17.032001,
				19.285, 16.841002);
		((GeneralPath) shape).curveTo(19.52, 16.653002, 19.605, 16.559002,
				19.546999, 16.559002);
		((GeneralPath) shape).curveTo(19.331999, 16.227001, 19.144999,
				15.989002, 18.984, 15.8410015);
		((GeneralPath) shape).curveTo(18.719, 15.5980015, 18.441, 15.028002,
				18.144999, 14.118002);
		((GeneralPath) shape).curveTo(17.984, 13.587002, 18.133, 12.161002,
				18.585999, 9.8410015);
		((GeneralPath) shape).curveTo(18.663998, 7.4380016, 17.718998,
				5.4810014, 15.745998, 3.9620013);
		((GeneralPath) shape).curveTo(15.675999, 3.9030013, 15.601998,
				3.8520014, 15.5269985, 3.8010013);
		((GeneralPath) shape).lineTo(15.124998, 3.5200014);
		((GeneralPath) shape).curveTo(14.163998, 2.9340014, 13.050999,
				2.5870013, 11.784998, 2.4810014);
		((GeneralPath) shape).curveTo(10.210998, 2.3440013, 8.733997, 2.5870013,
				7.347998, 3.2000012);
		((GeneralPath) shape).curveTo(8.784998, 2.8520012, 10.265998, 2.9460013,
				11.784998, 3.4810011);
		((GeneralPath) shape).curveTo(13.144998, 3.962001, 14.319998, 4.6920013,
				15.304998, 5.6800013);
		((GeneralPath) shape).curveTo(16.319998, 6.6920013, 16.734, 8.278002,
				16.546999, 10.438002);
		((GeneralPath) shape).curveTo(16.199, 12.653002, 16.171999, 14.200002,
				16.464998, 15.079001);
		((GeneralPath) shape).curveTo(16.757998, 15.9660015, 17.397999,
				16.579002, 18.387, 16.919);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_13
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(25.367, 18.38);
		((GeneralPath) shape).lineTo(25.266, 18.438);
		((GeneralPath) shape).curveTo(23.934, 17.962, 23.027, 17.653, 22.547,
				17.52);
		((GeneralPath) shape).curveTo(21.664001, 17.278, 20.773, 17.145, 19.867,
				17.122);
		((GeneralPath) shape).curveTo(19.129, 17.102, 18.316, 16.86, 17.426,
				16.399);
		((GeneralPath) shape).curveTo(13.582001, 16.208, 10.973001, 17.731,
				9.586, 20.962);
		((GeneralPath) shape).curveTo(9.160001, 22.825, 9.199, 24.692, 9.707001,
				26.559);
		((GeneralPath) shape).curveTo(10.719001, 30.321, 13.531, 32.2, 18.145,
				32.2);
		((GeneralPath) shape).curveTo(21.266, 32.173, 23.453001, 31.149, 24.707,
				29.122002);
		((GeneralPath) shape).curveTo(24.891, 28.825, 25.266, 28.067001,
				25.824001, 26.841002);
		((GeneralPath) shape).curveTo(26.172, 26.04, 26.531002, 25.548002,
				26.906002, 25.36);
		((GeneralPath) shape).curveTo(27.734001, 24.934, 28.238003, 24.813,
				28.426003, 25.001001);
		((GeneralPath) shape).lineTo(28.785002, 23.919);
		((GeneralPath) shape).curveTo(28.918001, 22.985, 28.719002, 22.04,
				28.188002, 21.079);
		((GeneralPath) shape).curveTo(27.574001, 19.985, 26.824001, 19.188,
				25.945002, 18.68);
		((GeneralPath) shape).curveTo(25.785002, 18.583, 25.590002, 18.485,
				25.367002, 18.380001);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_14
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(25.367, 18.38);
		((GeneralPath) shape).lineTo(25.387001, 18.359999);
		((GeneralPath) shape).curveTo(25.137001, 18.246998, 24.777, 18.105999,
				24.305, 17.937998);
		((GeneralPath) shape).lineTo(23.887001, 17.800999);
		((GeneralPath) shape).curveTo(23.285002, 17.586998, 22.582, 17.359999,
				21.785002, 17.121998);
		((GeneralPath) shape).curveTo(20.801003, 16.828999, 20.109001,
				16.574997, 19.707, 16.359999);
		((GeneralPath) shape).curveTo(19.305, 16.145, 18.898, 15.953999,
				18.484001, 15.777999);
		((GeneralPath) shape).curveTo(18.078001, 15.609999, 17.723001,
				15.8169985, 17.426, 16.398998);
		((GeneralPath) shape).curveTo(18.316, 16.859999, 19.129, 17.101997,
				19.867, 17.121998);
		((GeneralPath) shape).curveTo(20.773, 17.144999, 21.664001, 17.277998,
				22.547, 17.519999);
		((GeneralPath) shape).curveTo(23.027, 17.652998, 23.934, 17.961998,
				25.266, 18.437998);
		((GeneralPath) shape).lineTo(25.367, 18.379997);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_15
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(14.145, 3.239);
		((GeneralPath) shape).lineTo(14.906, 3.3600001);
		((GeneralPath) shape).curveTo(15.441, 3.387, 15.812, 3.344, 16.027,
				3.239);
		((GeneralPath) shape).curveTo(16.348, 3.1060002, 16.828001, 2.7080002,
				17.465, 2.04);
		((GeneralPath) shape).curveTo(18.133, 1.348, 18.414, 0.90699995, 18.305,
				0.719);
		((GeneralPath) shape).curveTo(18.121, 0.454, 17.625, 0.68, 16.828001,
				1.3989999);
		((GeneralPath) shape).lineTo(17.508001, 0.19999993);
		((GeneralPath) shape).curveTo(16.52, -0.066000074, 15.758001,
				-0.066000074, 15.227001, 0.19999993);
		((GeneralPath) shape).curveTo(14.906001, 0.35999992, 14.641001,
				0.6259999, 14.426002, 1.0009999);
		((GeneralPath) shape).lineTo(14.188002, 1.52);
		((GeneralPath) shape).lineTo(14.145001, 3.2389998);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xBA7763));
		g.fill(shape);

		// _0_0_0_16
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(15.145, 2.36);
		((GeneralPath) shape).lineTo(15.465, 1.3989999);
		((GeneralPath) shape).curveTo(15.867001, 0.70799994, 16.547, 0.30499995,
				17.508, 0.19999993);
		((GeneralPath) shape).curveTo(16.519999, -0.066000074, 15.757999,
				-0.066000074, 15.226999, 0.19999993);
		((GeneralPath) shape).curveTo(14.905999, 0.35999992, 14.640999,
				0.6259999, 14.426, 1.0009999);
		((GeneralPath) shape).lineTo(14.188, 1.52);
		((GeneralPath) shape).lineTo(14.188, 2.079);
		((GeneralPath) shape).lineTo(15.1449995, 2.36);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x9E6554));
		g.fill(shape);

		// _0_0_0_17
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(10.586, 7.759);
		((GeneralPath) shape).curveTo(10.266001, 8.61, 10.172001, 9.410999,
				10.305, 10.160999);
		((GeneralPath) shape).lineTo(10.586, 11.000999);
		((GeneralPath) shape).lineTo(14.027, 8.199999);
		((GeneralPath) shape).lineTo(14.145, 2.519999);
		((GeneralPath) shape).curveTo(14.094001, 3.234999, 13.566, 4.074999,
				12.566, 5.039999);
		((GeneralPath) shape).curveTo(11.562, 6.004999, 10.902, 6.9109993,
				10.586, 7.758999);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_18
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(16.145, 6.7);
		((GeneralPath) shape).curveTo(15.957001, 6.1569996, 15.758, 5.7469997,
				15.547001, 5.481);
		((GeneralPath) shape).curveTo(15.148001, 6.786, 14.801001, 8.134,
				14.508001, 9.52);
		((GeneralPath) shape).lineTo(13.707002, 11.68);
		((GeneralPath) shape).curveTo(14.051002, 11.653, 14.574001, 11.255,
				15.266002, 10.481);
		((GeneralPath) shape).curveTo(15.688002, 9.989, 15.988002, 9.372,
				16.168001, 8.618);
		((GeneralPath) shape).curveTo(16.348001, 7.88, 16.340002, 7.2390003,
				16.145, 6.7);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		// _0_0_0_19
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(10.586, 11.001);
		((GeneralPath) shape).curveTo(11.652, 12.653, 12.812, 12.813001, 14.066,
				11.481);
		((GeneralPath) shape).curveTo(15.188, 10.278, 15.707, 9.051, 15.625,
				7.8009996);
		((GeneralPath) shape).curveTo(15.574, 6.786, 15.828, 5.5479994, 16.387,
				4.0789995);
		((GeneralPath) shape).curveTo(16.651999, 3.3599997, 16.934, 2.7349997,
				17.227, 2.1999996);
		((GeneralPath) shape).lineTo(16.585999, 1.9189996);
		((GeneralPath) shape).curveTo(16.133, 1.7349995, 15.331999, 1.5979996,
				14.187999, 1.5199995);
		((GeneralPath) shape).curveTo(14.159999, 2.0009995, 14.065999,
				2.5589995, 13.905999, 3.1999993);
		((GeneralPath) shape).curveTo(13.558999, 4.4809995, 13.026999,
				5.5199995, 12.304999, 6.320999);
		((GeneralPath) shape).curveTo(11.348, 7.359999, 10.773, 8.453999,
				10.5859995, 9.598);
		((GeneralPath) shape).lineTo(10.5859995, 11.000999);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_20
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(4.465, 8.899);
		((GeneralPath) shape).curveTo(4.8120003, 9.337, 5.57, 9.673, 6.7460003,
				9.899);
		((GeneralPath) shape).curveTo(7.9220004, 10.122, 8.895, 9.555,
				9.6640005, 8.2);
		((GeneralPath) shape).curveTo(10.441001, 6.8479996, 10.547001, 5.751,
				9.984, 4.8989997);
		((GeneralPath) shape).curveTo(9.418, 4.048, 9.426001, 3.4619997, 10.008,
				3.1409998);
		((GeneralPath) shape).curveTo(10.586, 2.817, 11.047001, 2.6099997,
				11.387, 2.5199997);
		((GeneralPath) shape).lineTo(10.906, 2.4809997);
		((GeneralPath) shape).curveTo(9.4140005, 2.4809997, 8.039, 2.8129997,
				6.7850003, 3.4809997);
		((GeneralPath) shape).curveTo(5.3980002, 4.2269998, 4.3710003, 5.278,
				3.7070003, 6.641);
		((GeneralPath) shape).lineTo(3.7460003, 6.8799996);
		((GeneralPath) shape).curveTo(3.8790002, 7.7899995, 4.117, 8.462, 4.465,
				8.899);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_21
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(13.664, 2.481);
		((GeneralPath) shape).curveTo(13.879, 2.3439999, 13.641, 2.1339998,
				12.945, 1.841);
		((GeneralPath) shape).lineTo(14.026999, 1.759);
		((GeneralPath) shape).curveTo(13.757999, 1.012, 13.426, 0.53199995,
				13.026999, 0.32099998);
		((GeneralPath) shape).curveTo(12.624999, 0.10599998, 12.198999,
				0.06699997, 11.745999, 0.19999999);
		((GeneralPath) shape).lineTo(10.624999, 0.962);
		((GeneralPath) shape).curveTo(10.679999, 1.173, 10.745999, 1.3870001,
				10.823999, 1.598);
		((GeneralPath) shape).curveTo(10.983999, 2.051, 11.16, 2.266, 11.348,
				2.239);
		((GeneralPath) shape).curveTo(11.801, 2.2120001, 12.32, 2.255,
				12.905999, 2.3600001);
		((GeneralPath) shape).curveTo(13.386999, 2.466, 13.640999, 2.505,
				13.664, 2.4810002);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xBA7763));
		g.fill(shape);

		// _0_0_0_22
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(10.664, 1.841);
		((GeneralPath) shape).lineTo(11.905999, 1.2779999);
		((GeneralPath) shape).curveTo(12.811999, 1.0399998, 13.5199995,
				1.1999999, 14.026999, 1.7589998);
		((GeneralPath) shape).curveTo(13.757999, 1.0119998, 13.426, 0.5319998,
				13.026999, 0.32099986);
		((GeneralPath) shape).curveTo(12.624999, 0.10599986, 12.198999,
				0.06699985, 11.745999, 0.19999987);
		((GeneralPath) shape).lineTo(10.664, 1.0399998);
		((GeneralPath) shape).curveTo(10.719, 1.2269999, 10.73, 1.4149998,
				10.707, 1.5979998);
		((GeneralPath) shape).lineTo(10.664, 1.8409998);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x9E6554));
		g.fill(shape);

		// _0_0_0_23
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(8.867, 3.481);
		((GeneralPath) shape).curveTo(9.370999, 3.184, 9.973, 2.962, 10.664,
				2.8009999);
		((GeneralPath) shape).curveTo(11.198999, 2.665, 11.757999, 2.4929998,
				12.348, 2.2779999);
		((GeneralPath) shape).curveTo(12.188, 2.3049998, 12.026999, 2.294,
				11.867, 2.2389998);
		((GeneralPath) shape).curveTo(11.492, 2.1839998, 11.238, 2.0279999,
				11.105, 1.7589998);
		((GeneralPath) shape).curveTo(10.917999, 1.3329998, 10.891, 0.96199983,
				11.026999, 0.6409998);
		((GeneralPath) shape).curveTo(10.039, 0.7739998, 8.879, 1.0399997,
				7.5469995, 1.4379997);
		((GeneralPath) shape).curveTo(4.9339995, 2.2119997, 3.6909995,
				3.0939999, 3.8239994, 4.0789995);
		((GeneralPath) shape).curveTo(4.0389996, 4.5319996, 4.1879992,
				4.9069996, 4.2659993, 5.2);
		((GeneralPath) shape).curveTo(4.452999, 5.7349997, 4.3709993, 6.3989997,
				4.0269995, 7.2);
		((GeneralPath) shape).curveTo(3.7299995, 7.868, 3.7459996, 8.505,
				4.0659995, 9.118);
		((GeneralPath) shape).curveTo(4.4409995, 9.759, 5.1209993, 10.118,
				6.1049995, 10.2);
		((GeneralPath) shape).curveTo(7.4919996, 10.278, 8.507999, 9.747,
				9.1449995, 8.598);
		((GeneralPath) shape).curveTo(9.679999, 7.6649995, 9.851999, 6.6649995,
				9.664, 5.5979996);
		((GeneralPath) shape).curveTo(9.507999, 4.641, 9.051, 4.0509996,
				8.304999, 3.8409996);
		((GeneralPath) shape).lineTo(8.867, 3.4809995);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_24
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(14.066, 28.68);
		((GeneralPath) shape).curveTo(14.387, 28.255001, 14.559, 27.692, 14.586,
				27.001);
		((GeneralPath) shape).curveTo(14.641001, 25.587, 13.961, 24.161,
				12.547001, 22.719);
		((GeneralPath) shape).curveTo(11.426001, 21.571, 9.441001, 21.653,
				6.586001, 22.962);
		((GeneralPath) shape).curveTo(5.172001, 23.598, 3.8670008, 24.36,
				2.668001, 25.239);
		((GeneralPath) shape).curveTo(2.668001, 25.559, 2.812001, 25.946001,
				3.105001, 26.399);
		((GeneralPath) shape).curveTo(3.668001, 27.305, 4.6800013, 28.118,
				6.145001, 28.841);
		((GeneralPath) shape).curveTo(8.387001, 29.934, 10.371, 30.227,
				12.105001, 29.719);
		((GeneralPath) shape).curveTo(12.984001, 29.480999, 13.641002, 29.134,
				14.066002, 28.68);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		// _0_0_0_25
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(14.547, 27.399);
		((GeneralPath) shape).curveTo(14.707, 26.305, 14.398, 25.184, 13.625,
				24.04);
		((GeneralPath) shape).curveTo(13.254, 23.774, 12.824, 23.598001, 12.348,
				23.52);
		((GeneralPath) shape).curveTo(10.398, 23.173, 8.733999, 23.426,
				7.3479996, 24.278);
		((GeneralPath) shape).lineTo(5.8239994, 25.68);
		((GeneralPath) shape).curveTo(5.878999, 25.946001, 6.0659995, 26.255001,
				6.3869996, 26.598);
		((GeneralPath) shape).curveTo(7.0779996, 27.293999, 8.172, 27.828999,
				9.667999, 28.199999);
		((GeneralPath) shape).curveTo(11.851999, 28.734999, 13.48, 28.466,
				14.546999, 27.398998);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE39C8C));
		g.fill(shape);

		// _0_0_0_26
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(9.668, 28.2);
		((GeneralPath) shape).curveTo(11.852, 28.735, 13.4800005, 28.466002,
				14.547001, 27.399);
		((GeneralPath) shape).lineTo(14.586, 26.759);
		((GeneralPath) shape).curveTo(13.305, 27.559, 11.719, 27.813002, 9.824,
				27.52);
		((GeneralPath) shape).curveTo(7.879, 27.227001, 6.5470004, 26.626001,
				5.8240004, 25.719);
		((GeneralPath) shape).curveTo(5.8240004, 25.962, 6.0390005, 26.278,
				6.465, 26.68);
		((GeneralPath) shape).curveTo(7.1600003, 27.321001, 8.227, 27.825,
				9.668, 28.2);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xBA7763));
		g.fill(shape);

		// _0_0_0_27
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(12.027, 25.801);
		((GeneralPath) shape).lineTo(11.746, 25.559);
		((GeneralPath) shape).lineTo(12.188001, 26.161);
		((GeneralPath) shape).curveTo(12.453001, 26.532, 12.973001, 26.824999,
				13.746, 27.039999);
		((GeneralPath) shape).lineTo(13.348001, 27.039999);
		((GeneralPath) shape).lineTo(12.066001, 26.841);
		((GeneralPath) shape).lineTo(12.906001, 27.199999);
		((GeneralPath) shape).curveTo(13.359001, 27.414999, 13.906001,
				27.480999, 14.547001, 27.398998);
		((GeneralPath) shape).curveTo(14.734001, 27.238998, 14.812001,
				27.000998, 14.785001, 26.679998);
		((GeneralPath) shape).curveTo(14.785001, 26.359999, 14.668001,
				26.066998, 14.426001, 25.800999);
		((GeneralPath) shape).lineTo(14.1050005, 25.841);
		((GeneralPath) shape).curveTo(13.867001, 25.841, 13.625001, 25.734999,
				13.387, 25.52);
		((GeneralPath) shape).lineTo(13.1050005, 25.2);
		((GeneralPath) shape).lineTo(13.027, 25.641);
		((GeneralPath) shape).curveTo(13.0, 25.825, 13.051001, 26.001001,
				13.188001, 26.161001);
		((GeneralPath) shape).lineTo(13.387001, 26.36);
		((GeneralPath) shape).curveTo(13.281001, 26.36, 13.051001, 26.294,
				12.707001, 26.161001);
		((GeneralPath) shape).curveTo(12.492001, 26.079, 12.266001, 25.962002,
				12.027, 25.801);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_28
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(21.945, 18.68);
		((GeneralPath) shape).curveTo(21.305, 18.973, 20.785, 19.266, 20.387,
				19.559);
		((GeneralPath) shape).lineTo(19.945, 19.88);
		((GeneralPath) shape).lineTo(20.387, 19.598);
		((GeneralPath) shape).curveTo(20.811998, 19.387, 21.397999, 19.161,
				22.144999, 18.918999);
		((GeneralPath) shape).curveTo(23.187998, 18.571, 24.144999, 18.425999,
				25.026999, 18.480999);
		((GeneralPath) shape).curveTo(25.479998, 18.505, 25.824, 18.574999,
				26.065998, 18.679998);
		((GeneralPath) shape).lineTo(26.226997, 18.800999);
		((GeneralPath) shape).lineTo(25.823997, 18.480999);
		((GeneralPath) shape).lineTo(25.065998, 18.161);
		((GeneralPath) shape).curveTo(23.917997, 18.078999, 22.878998, 18.255,
				21.944998, 18.679998);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xBA7763));
		g.fill(shape);

		// _0_0_0_29
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(23.188, 23.641);
		((GeneralPath) shape).curveTo(23.133, 22.895, 22.801, 22.266, 22.188,
				21.759);
		((GeneralPath) shape).curveTo(21.574, 21.278, 20.879, 21.067001, 20.105,
				21.122002);
		((GeneralPath) shape).curveTo(19.331999, 21.173002, 18.691, 21.493002,
				18.188, 22.079002);
		((GeneralPath) shape).curveTo(17.68, 22.669003, 17.453, 23.344002,
				17.508, 24.122002);
		((GeneralPath) shape).curveTo(17.559, 24.891, 17.890999, 25.532001,
				18.508, 26.04);
		((GeneralPath) shape).curveTo(19.121, 26.52, 19.812, 26.735, 20.585999,
				26.68);
		((GeneralPath) shape).curveTo(21.359, 26.598, 22.012, 26.266, 22.546999,
				25.68);
		((GeneralPath) shape).curveTo(23.050999, 25.067, 23.265999, 24.387001,
				23.188, 23.641);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFF5F5));
		g.fill(shape);

		// _0_0_0_30
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(23.027, 23.919);
		((GeneralPath) shape).curveTo(23.027, 23.387001, 22.824001, 22.919,
				22.426, 22.52);
		((GeneralPath) shape).curveTo(22.027, 22.145, 21.531, 21.962, 20.945,
				21.962);
		((GeneralPath) shape).curveTo(20.387, 21.962, 19.906, 22.145, 19.508,
				22.52);
		((GeneralPath) shape).curveTo(19.105, 22.919, 18.906, 23.387001, 18.906,
				23.919);
		((GeneralPath) shape).curveTo(18.906, 24.454, 19.105, 24.919, 19.508,
				25.321001);
		((GeneralPath) shape).curveTo(19.906, 25.692001, 20.387, 25.880001,
				20.945, 25.880001);
		((GeneralPath) shape).curveTo(21.531, 25.880001, 22.027, 25.692001,
				22.425999, 25.321001);
		((GeneralPath) shape).curveTo(22.824, 24.919, 23.026999, 24.454,
				23.026999, 23.919);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x292727));
		g.fill(shape);

		// _0_0_0_31
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(21.547, 24.759);
		((GeneralPath) shape).curveTo(21.547, 24.466002, 21.441, 24.2,
				21.227001, 23.962);
		((GeneralPath) shape).curveTo(20.984001, 23.747, 20.719002, 23.641,
				20.426, 23.641);
		((GeneralPath) shape).curveTo(20.105001, 23.641, 19.84, 23.747002,
				19.625, 23.962);
		((GeneralPath) shape).curveTo(19.414, 24.2, 19.305, 24.466, 19.305,
				24.759);
		((GeneralPath) shape).curveTo(19.305, 25.051, 19.414, 25.321001, 19.625,
				25.559);
		((GeneralPath) shape).curveTo(19.84, 25.774, 20.105, 25.88, 20.426,
				25.88);
		((GeneralPath) shape).curveTo(20.719, 25.88, 20.984001, 25.773998,
				21.227001, 25.559);
		((GeneralPath) shape).curveTo(21.441002, 25.321, 21.547, 25.051, 21.547,
				24.759);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFF5F5));
		g.fill(shape);

		// _0_0_0_32
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(20.066, 22.641);
		((GeneralPath) shape).lineTo(20.465, 22.641);
		((GeneralPath) shape).curveTo(20.812, 22.641, 21.145, 22.719, 21.465,
				22.880001);
		((GeneralPath) shape).curveTo(22.16, 23.2, 22.559, 23.626001, 22.668,
				24.161001);
		((GeneralPath) shape).curveTo(22.800999, 23.466002, 22.491999,
				22.907001, 21.745998, 22.481);
		((GeneralPath) shape).curveTo(21.347998, 22.266, 20.944998, 22.227001,
				20.546999, 22.36);
		((GeneralPath) shape).lineTo(20.065998, 22.641);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x8F5B59));
		g.fill(shape);

		// _0_0_0_33
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(18.746, 32.759);
		((GeneralPath) shape).curveTo(18.773, 32.653, 18.758, 32.575, 18.707,
				32.52);
		((GeneralPath) shape).curveTo(18.598001, 32.415, 18.453001, 32.344,
				18.266, 32.321);
		((GeneralPath) shape).lineTo(18.066, 32.321);
		((GeneralPath) shape).lineTo(19.066, 31.918999);
		((GeneralPath) shape).curveTo(19.387, 31.758999, 19.719, 31.547998,
				20.066, 31.277998);
		((GeneralPath) shape).curveTo(20.121, 31.172998, 19.785, 31.133999,
				19.066, 31.160997);
		((GeneralPath) shape).lineTo(17.824, 31.320997);
		((GeneralPath) shape).lineTo(18.144999, 30.840998);
		((GeneralPath) shape).curveTo(18.227, 30.597998, 18.227, 30.425997,
				18.144999, 30.320997);
		((GeneralPath) shape).curveTo(18.012, 30.320997, 17.772999, 30.414997,
				17.425999, 30.597998);
		((GeneralPath) shape).lineTo(16.745998, 31.078999);
		((GeneralPath) shape).lineTo(15.585999, 31.199999);
		((GeneralPath) shape).lineTo(15.425999, 31.398998);
		((GeneralPath) shape).lineTo(15.386999, 31.480999);
		((GeneralPath) shape).curveTo(15.437999, 31.558998, 15.664, 31.653,
				16.066, 31.758999);
		((GeneralPath) shape).lineTo(16.305, 32.001);
		((GeneralPath) shape).curveTo(16.52, 32.184, 16.707, 32.305, 16.867,
				32.36);
		((GeneralPath) shape).curveTo(17.16, 32.466, 17.785, 32.598, 18.746,
				32.759);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE8A09B));
		g.fill(shape);

		// _0_0_0_34
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(18.625, 31.321);
		((GeneralPath) shape).curveTo(18.254, 31.372, 17.945, 31.438, 17.707,
				31.519999);
		((GeneralPath) shape).lineTo(17.508001, 31.597998);
		((GeneralPath) shape).curveTo(17.879002, 31.066998, 18.051, 30.758997,
				18.027, 30.679998);
		((GeneralPath) shape).lineTo(17.984001, 30.519999);
		((GeneralPath) shape).curveTo(17.559002, 30.734999, 17.266, 30.906998,
				17.105001, 31.039999);
		((GeneralPath) shape).lineTo(16.824001, 31.278);
		((GeneralPath) shape).lineTo(14.3480015, 31.079);
		((GeneralPath) shape).curveTo(14.133001, 31.079, 13.961001, 31.118,
				13.824001, 31.2);
		((GeneralPath) shape).lineTo(13.707002, 31.321001);
		((GeneralPath) shape).lineTo(14.508001, 31.880001);
		((GeneralPath) shape).curveTo(15.172001, 32.278, 15.891002, 32.52,
				16.664001, 32.598);
		((GeneralPath) shape).curveTo(17.867, 32.735, 18.559002, 32.786,
				18.746002, 32.759);
		((GeneralPath) shape).curveTo(18.691002, 32.575, 18.480001, 32.466,
				18.105001, 32.438);
		((GeneralPath) shape).lineTo(17.586002, 32.399);
		((GeneralPath) shape).lineTo(18.266003, 32.161);
		((GeneralPath) shape).curveTo(18.746002, 31.973, 19.090002, 31.800999,
				19.305002, 31.640999);
		((GeneralPath) shape).lineTo(19.863003, 31.321);
		((GeneralPath) shape).curveTo(19.652002, 31.238998, 19.238003,
				31.238998, 18.625002, 31.321);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFBDBA));
		g.fill(shape);

		// _0_0_0_35
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(28.426, 24.962);
		((GeneralPath) shape).lineTo(28.785, 23.88);
		((GeneralPath) shape).curveTo(28.918, 22.946, 28.719, 22.001, 28.188,
				21.039999);
		((GeneralPath) shape).curveTo(27.68, 20.183998, 27.094, 19.466, 26.426,
				18.88);
		((GeneralPath) shape).curveTo(25.758001, 19.144999, 25.492, 19.962,
				25.625, 21.321);
		((GeneralPath) shape).curveTo(25.758, 22.734999, 26.266, 23.747, 27.145,
				24.359999);
		((GeneralPath) shape).curveTo(27.652, 24.652998, 28.0, 24.824999,
				28.188, 24.88);
		((GeneralPath) shape).lineTo(28.426, 24.962);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xFFCDCA));
		g.fill(shape);

		// _0_0_0_36
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(27.984, 22.88);
		((GeneralPath) shape).curveTo(27.879, 22.640999, 27.800999, 22.387,
				27.745998, 22.118);
		((GeneralPath) shape).curveTo(27.852, 21.747, 27.890999, 21.505,
				27.866999, 21.399);
		((GeneralPath) shape).curveTo(27.758, 20.973, 27.585999, 20.653, 27.348,
				20.438);
		((GeneralPath) shape).curveTo(27.105, 20.227, 26.961, 20.265999, 26.906,
				20.559);
		((GeneralPath) shape).curveTo(26.852, 21.012, 27.012001, 21.532,
				27.387001, 22.118);
		((GeneralPath) shape).curveTo(27.570002, 22.415, 27.773, 22.665,
				27.984001, 22.88);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE3A496));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0

		g.setTransform(transformations.pop()); // _0

	}
}

/**
 * This class has been automatically generated using
 * <a href="http://ebourg.github.io/flamingo-svg-transcoder/">Flamingo SVG
 * transcoder</a>.
 */

/**
 * This class is based on ouput from
 * <a href="http://ebourg.github.io/flamingo-svg-transcoder/">Flamingo SVG
 * transcoder</a>.
 * <p>
 * The source image is available as public domain from <a href=
 * "https://openclipart.org/detail/330404/kindergarten-teacher-3">openclipart</a>.
 */
class KindergartenTeacher implements javax.swing.Icon {

	/** The width of this icon. */
	private int width;

	/** The height of this icon. */
	private int height;

	/**
	 * Creates a new transcoded SVG image.
	 */
	public KindergartenTeacher(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		double coef = Math.min((double) width / (double) 1371,
				(double) height / (double) 1136);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.scale(coef, coef);
		paint(g2d);
		g2d.dispose();
	}

	/**
	 * Paints the transcoded SVG image on the specified graphics context.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	private static void paint(Graphics2D g) {
		Shape shape = null;

		float origAlpha = 1.0f;

		java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();

		//

		// _0

		// _0_0

		// _0_0_0

		// _0_0_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(197.917, 1120.99);
		((GeneralPath) shape).lineTo(341.917, 1120.99);
		((GeneralPath) shape).lineTo(341.251, 1064.32);
		((GeneralPath) shape).lineTo(333.917, 1036.99);
		((GeneralPath) shape).lineTo(211.917, 1044.99);
		((GeneralPath) shape).lineTo(197.917, 1120.99);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

		// _0_0_0_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(682.879, 572.924);
		((GeneralPath) shape).curveTo(685.341, 562.601, 687.27, 551.583,
				688.539, 539.296);
		((GeneralPath) shape).curveTo(698.113, 551.524, 707.939, 562.184,
				718.753, 572.924);
		((GeneralPath) shape).curveTo(706.845, 572.924, 694.937, 572.924,
				682.879, 572.924);
		((GeneralPath) shape).closePath();
		((GeneralPath) shape).moveTo(1370.26, 866.444);
		((GeneralPath) shape).curveTo(1369.6, 835.08, 1362.16, 805.757, 1345.87,
				779.143);
		((GeneralPath) shape).curveTo(1317.13, 732.193, 1275.02, 701.777,
				1223.48, 684.158);
		((GeneralPath) shape).curveTo(1195.39, 674.558, 1166.44, 669.647,
				1136.58, 673.013);
		((GeneralPath) shape).curveTo(1134.03, 673.301, 1132.01, 672.296,
				1129.91, 671.436);
		((GeneralPath) shape).curveTo(1109.53, 663.07, 1088.54, 657.783,
				1066.32, 658.988);
		((GeneralPath) shape).curveTo(1063.24, 659.155, 1062.37, 657.642,
				1061.37, 655.368);
		((GeneralPath) shape).curveTo(1054.76, 640.301, 1047.54, 625.535,
				1039.22, 611.333);
		((GeneralPath) shape).curveTo(1037.45, 608.313, 1037.49, 607.034,
				1041.09, 605.446);
		((GeneralPath) shape).curveTo(1062.4, 596.05, 1081.74, 583.536, 1099.01,
				567.87);
		((GeneralPath) shape).curveTo(1108.76, 559.036, 1117.41, 549.206,
				1126.06, 538.13);
		((GeneralPath) shape).curveTo(1126.47, 541.07, 1126.8, 543.126, 1127.03,
				545.193);
		((GeneralPath) shape).curveTo(1129.86, 570.421, 1136.72, 594.416,
				1149.49, 616.469);
		((GeneralPath) shape).curveTo(1163.14, 640.028, 1181.41, 658.959,
				1206.05, 671.197);
		((GeneralPath) shape).curveTo(1212.31, 674.301, 1214.84, 673.725,
				1219.18, 668.876);
		((GeneralPath) shape).curveTo(1221.74, 667.321, 1221.37, 665.016,
				1220.69, 662.695);
		((GeneralPath) shape).curveTo(1219.73, 659.405, 1217.27, 657.563,
				1214.34, 656.068);
		((GeneralPath) shape).curveTo(1193.89, 645.658, 1178.31, 630.078,
				1166.38, 610.656);
		((GeneralPath) shape).curveTo(1149.0, 582.367, 1143.7, 550.945, 1142.45,
				518.471);
		((GeneralPath) shape).curveTo(1142.36, 515.901, 1143.18, 513.554,
				1144.36, 511.314);
		((GeneralPath) shape).curveTo(1151.52, 497.736, 1157.8, 483.778,
				1162.19, 469.048);
		((GeneralPath) shape).curveTo(1163.31, 465.294, 1165.19, 464.966,
				1168.5, 466.254);
		((GeneralPath) shape).curveTo(1174.57, 468.617, 1180.41, 471.425,
				1186.16, 474.434);
		((GeneralPath) shape).curveTo(1221.19, 492.755, 1244.1, 521.142,
				1255.61, 558.769);
		((GeneralPath) shape).curveTo(1261.49, 577.99, 1264.06, 597.751,
				1263.79, 617.868);
		((GeneralPath) shape).curveTo(1263.73, 621.695, 1263.43, 625.559,
				1263.96, 629.344);
		((GeneralPath) shape).curveTo(1264.46, 632.987, 1265.41, 636.635,
				1270.16, 637.018);
		((GeneralPath) shape).curveTo(1277.59, 637.765, 1281.07, 635.186,
				1281.23, 628.566);
		((GeneralPath) shape).curveTo(1281.71, 608.204, 1280.64, 587.971,
				1276.08, 568.029);
		((GeneralPath) shape).curveTo(1265.99, 523.853, 1243.14, 488.538,
				1204.18, 464.401);
		((GeneralPath) shape).curveTo(1197.47, 460.247, 1190.39, 456.785,
				1183.3, 453.391);
		((GeneralPath) shape).curveTo(1183.84, 452.195, 1184.7, 452.133,
				1185.48, 451.86);
		((GeneralPath) shape).curveTo(1199.0, 447.118, 1207.68, 437.684,
				1212.16, 424.223);
		((GeneralPath) shape).curveTo(1215.92, 412.933, 1216.09, 401.314,
				1214.83, 389.662);
		((GeneralPath) shape).curveTo(1214.54, 386.937, 1215.09, 384.872,
				1216.41, 382.67);
		((GeneralPath) shape).curveTo(1247.55, 330.722, 1251.58, 276.201,
				1229.44, 220.356);
		((GeneralPath) shape).curveTo(1201.16, 149.042, 1153.28, 93.446,
				1088.18, 53.241);
		((GeneralPath) shape).curveTo(1032.94, 19.128, 973.138, -0.783, 907.338,
				0.831);
		((GeneralPath) shape).curveTo(875.585, 1.61, 844.34, 5.971, 813.962,
				15.368);
		((GeneralPath) shape).curveTo(796.261, 20.843, 779.091, 27.675, 765.993,
				41.679);
		((GeneralPath) shape).curveTo(764.771, 42.985, 763.425, 42.926, 761.956,
				43.041);
		((GeneralPath) shape).curveTo(741.471, 44.652, 722.065, 50.214, 703.823,
				59.566);
		((GeneralPath) shape).curveTo(662.032, 80.991, 630.768, 113.619,
				606.161, 152.95);
		((GeneralPath) shape).curveTo(585.881, 185.366, 573.723, 220.596,
				572.195, 259.081);
		((GeneralPath) shape).curveTo(570.56, 300.249, 581.324, 338.365, 601.06,
				374.196);
		((GeneralPath) shape).curveTo(602.047, 375.988, 602.761, 377.57,
				602.136, 379.769);
		((GeneralPath) shape).curveTo(599.038, 390.66, 598.611, 401.775,
				600.238, 412.92);
		((GeneralPath) shape).curveTo(602.953, 431.526, 611.459, 445.817,
				630.328, 452.071);
		((GeneralPath) shape).curveTo(631.002, 452.294, 631.45, 452.679,
				631.521, 453.416);
		((GeneralPath) shape).curveTo(581.46, 476.038, 550.981, 514.517, 538.83,
				567.851);
		((GeneralPath) shape).curveTo(537.305, 574.543, 537.266, 574.484,
				529.94, 573.741);
		((GeneralPath) shape).curveTo(488.046, 569.492, 446.153, 565.243,
				404.254, 561.044);
		((GeneralPath) shape).curveTo(361.527, 556.761, 318.793, 552.55,
				276.065, 548.273);
		((GeneralPath) shape).curveTo(269.288, 547.595, 265.628, 550.658,
				265.388, 557.488);
		((GeneralPath) shape).curveTo(265.323, 559.319, 265.377, 561.154,
				265.377, 562.987);
		((GeneralPath) shape).curveTo(265.377, 574.979, 265.377, 574.979,
				253.505, 574.111);
		((GeneralPath) shape).curveTo(244.614, 573.46, 241.385, 576.493,
				241.383, 585.558);
		((GeneralPath) shape).curveTo(241.375, 616.384, 241.38, 647.21, 241.379,
				678.036);
		((GeneralPath) shape).curveTo(241.379, 685.01, 241.377, 685.082,
				234.185, 684.887);
		((GeneralPath) shape).curveTo(208.3, 684.187, 183.091, 687.902, 158.66,
				696.521);
		((GeneralPath) shape).curveTo(108.114, 714.353, 69.721, 747.399, 41.498,
				792.267);
		((GeneralPath) shape).curveTo(9.341, 843.388, -6.265, 898.846, 2.338,
				959.567);
		((GeneralPath) shape).curveTo(6.379, 988.092, 17.08, 1014.11, 38.702,
				1033.89);
		((GeneralPath) shape).curveTo(66.333, 1059.16, 99.821, 1070.19, 136.999,
				1069.9);
		((GeneralPath) shape).curveTo(160.847, 1069.71, 183.841, 1066.01,
				202.892, 1049.15);
		((GeneralPath) shape).curveTo(200.119, 1061.47, 197.276, 1073.39,
				194.781, 1085.38);
		((GeneralPath) shape).curveTo(191.929, 1099.08, 189.939, 1112.91,
				189.771, 1126.94);
		((GeneralPath) shape).curveTo(189.697, 1133.19, 192.31, 1135.88,
				197.843, 1135.84);
		((GeneralPath) shape).curveTo(203.227, 1135.81, 205.518, 1133.09,
				205.742, 1126.68);
		((GeneralPath) shape).curveTo(206.62, 1101.57, 212.45, 1077.33, 218.817,
				1053.18);
		((GeneralPath) shape).curveTo(219.364, 1051.11, 220.233, 1050.52,
				222.274, 1050.92);
		((GeneralPath) shape).curveTo(236.574, 1053.75, 251.048, 1054.0,
				265.545, 1053.54);
		((GeneralPath) shape).curveTo(267.464, 1053.48, 268.221, 1054.01,
				268.701, 1055.71);
		((GeneralPath) shape).curveTo(273.526, 1072.77, 291.998, 1079.2,
				307.376, 1078.14);
		((GeneralPath) shape).curveTo(317.771, 1077.42, 325.898, 1072.48,
				332.929, 1064.62);
		((GeneralPath) shape).curveTo(333.286, 1065.65, 333.589, 1066.21,
				333.663, 1066.8);
		((GeneralPath) shape).curveTo(333.889, 1068.62, 334.041, 1070.44,
				334.199, 1072.26);
		((GeneralPath) shape).curveTo(335.78, 1090.55, 335.781, 1108.83, 333.85,
				1127.1);
		((GeneralPath) shape).curveTo(333.244, 1132.83, 336.346, 1135.93,
				341.981, 1135.84);
		((GeneralPath) shape).curveTo(347.265, 1135.77, 349.383, 1133.23,
				350.061, 1126.06);
		((GeneralPath) shape).curveTo(352.517, 1100.06, 351.454, 1074.18,
				347.448, 1048.41);
		((GeneralPath) shape).curveTo(345.915, 1038.54, 345.871, 1038.62,
				355.113, 1034.94);
		((GeneralPath) shape).curveTo(382.58, 1024.01, 407.388, 1009.05,
				425.716, 985.332);
		((GeneralPath) shape).curveTo(450.053, 953.836, 456.977, 918.08,
				449.796, 879.293);
		((GeneralPath) shape).curveTo(449.355, 876.912, 448.605, 875.207,
				452.305, 875.323);
		((GeneralPath) shape).curveTo(476.593, 876.09, 500.889, 876.612,
				525.174, 877.464);
		((GeneralPath) shape).curveTo(528.555, 877.582, 532.138, 876.883,
				535.413, 878.763);
		((GeneralPath) shape).curveTo(558.681, 892.12, 583.789, 894.827,
				609.758, 890.64);
		((GeneralPath) shape).curveTo(622.864, 888.527, 634.916, 883.721,
				641.058, 870.412);
		((GeneralPath) shape).curveTo(641.957, 868.463, 643.414, 868.932,
				644.808, 868.891);
		((GeneralPath) shape).curveTo(689.557, 867.607, 734.308, 866.363,
				779.055, 865.019);
		((GeneralPath) shape).curveTo(781.854, 864.935, 782.409, 866.008,
				782.346, 868.475);
		((GeneralPath) shape).curveTo(782.046, 880.122, 781.912, 891.773,
				781.637, 903.42);
		((GeneralPath) shape).curveTo(780.884, 935.368, 780.059, 967.313,
				779.342, 999.261);
		((GeneralPath) shape).curveTo(779.218, 1004.8, 781.764, 1008.08,
				786.183, 1008.77);
		((GeneralPath) shape).curveTo(790.445, 1009.44, 793.663, 1006.92,
				795.331, 1001.59);
		((GeneralPath) shape).curveTo(796.34, 999.979, 796.443, 998.158,
				796.663, 996.328);
		((GeneralPath) shape).curveTo(798.876, 977.931, 797.683, 959.413,
				798.254, 940.969);
		((GeneralPath) shape).curveTo(798.964, 918.031, 800.175, 895.06,
				799.597, 872.079);
		((GeneralPath) shape).curveTo(799.589, 871.747, 799.626, 871.412,
				799.607, 871.081);
		((GeneralPath) shape).curveTo(799.418, 867.715, 800.988, 866.466,
				804.361, 866.277);
		((GeneralPath) shape).curveTo(817.661, 865.53, 830.977, 865.584,
				844.276, 865.15);
		((GeneralPath) shape).curveTo(853.485, 864.85, 859.004, 870.482,
				864.407, 875.96);
		((GeneralPath) shape).curveTo(867.879, 879.479, 871.113, 882.683,
				875.772, 884.454);
		((GeneralPath) shape).curveTo(888.785, 889.4, 900.973, 886.869, 912.762,
				880.578);
		((GeneralPath) shape).curveTo(915.464, 879.136, 917.994, 878.051,
				921.262, 878.835);
		((GeneralPath) shape).curveTo(934.431, 881.99, 947.84, 883.233, 961.364,
				883.319);
		((GeneralPath) shape).curveTo(968.136, 883.363, 968.272, 883.567,
				967.512, 890.488);
		((GeneralPath) shape).curveTo(963.434, 927.586, 972.987, 960.807,
				996.328, 989.898);
		((GeneralPath) shape).curveTo(999.769, 994.187, 1003.11, 999.08,
				1008.94, 1000.84);
		((GeneralPath) shape).curveTo(1027.15, 1017.05, 1048.0, 1028.76,
				1070.88, 1036.93);
		((GeneralPath) shape).curveTo(1073.81, 1037.98, 1074.56, 1039.3,
				1074.11, 1042.3);
		((GeneralPath) shape).curveTo(1072.77, 1051.16, 1071.53, 1060.05,
				1070.65, 1068.97);
		((GeneralPath) shape).curveTo(1068.7, 1088.85, 1068.37, 1108.76,
				1070.97, 1128.63);
		((GeneralPath) shape).curveTo(1071.69, 1134.08, 1074.28, 1135.98,
				1079.7, 1135.87);
		((GeneralPath) shape).curveTo(1084.89, 1135.77, 1086.23, 1132.15,
				1087.21, 1128.1);
		((GeneralPath) shape).curveTo(1088.25, 1126.6, 1087.91, 1124.94,
				1087.88, 1123.28);
		((GeneralPath) shape).curveTo(1087.76, 1116.79, 1086.72, 1110.35,
				1086.62, 1103.88);
		((GeneralPath) shape).curveTo(1086.38, 1087.22, 1087.89, 1070.66,
				1089.96, 1054.16);
		((GeneralPath) shape).curveTo(1091.08, 1045.2, 1091.21, 1045.33,
				1099.82, 1047.29);
		((GeneralPath) shape).curveTo(1131.17, 1054.43, 1162.75, 1058.14,
				1194.82, 1053.05);
		((GeneralPath) shape).curveTo(1198.92, 1052.39, 1200.64, 1054.08,
				1201.53, 1057.7);
		((GeneralPath) shape).curveTo(1205.59, 1074.35, 1209.4, 1091.04,
				1211.79, 1108.04);
		((GeneralPath) shape).curveTo(1212.76, 1114.93, 1211.98, 1122.08,
				1214.79, 1128.69);
		((GeneralPath) shape).curveTo(1216.53, 1134.84, 1219.0, 1136.61,
				1224.73, 1135.83);
		((GeneralPath) shape).curveTo(1228.78, 1135.28, 1231.01, 1132.1,
				1230.89, 1126.46);
		((GeneralPath) shape).curveTo(1230.72, 1118.14, 1229.88, 1109.86,
				1228.64, 1101.63);
		((GeneralPath) shape).curveTo(1226.2, 1085.33, 1222.73, 1069.25,
				1218.35, 1053.37);
		((GeneralPath) shape).curveTo(1217.5, 1050.28, 1218.02, 1049.46,
				1221.38, 1049.54);
		((GeneralPath) shape).curveTo(1225.0, 1049.63, 1228.67, 1048.9, 1232.25,
				1048.23);
		((GeneralPath) shape).curveTo(1248.21, 1045.24, 1262.09, 1037.06,
				1276.14, 1029.68);
		((GeneralPath) shape).curveTo(1311.66, 1011.02, 1339.11, 984.221,
				1355.73, 947.238);
		((GeneralPath) shape).curveTo(1367.28, 921.553, 1370.84, 894.501,
				1370.26, 866.444);

		g.setPaint(new Color(0x6E4100));
		g.fill(shape);

		// _0_0_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(795.331, 1001.59);
		((GeneralPath) shape).curveTo(796.186, 965.837, 797.055, 930.082,
				797.886, 894.326);
		((GeneralPath) shape).curveTo(798.087, 885.673, 798.274, 877.016,
				798.234, 868.362);
		((GeneralPath) shape).curveTo(798.221, 865.5, 799.169, 864.721, 801.963,
				864.67);
		((GeneralPath) shape).curveTo(817.599, 864.386, 833.234, 863.97,
				848.862, 863.426);
		((GeneralPath) shape).curveTo(851.296, 863.341, 852.898, 863.972,
				854.385, 865.84);
		((GeneralPath) shape).curveTo(856.381, 868.348, 858.885, 870.269,
				862.142, 870.977);
		((GeneralPath) shape).curveTo(863.298, 871.228, 864.413, 871.618,
				864.931, 872.826);
		((GeneralPath) shape).curveTo(869.827, 884.266, 880.297, 885.195,
				890.416, 885.412);
		((GeneralPath) shape).curveTo(899.133, 885.6, 907.427, 882.678, 914.854,
				877.804);
		((GeneralPath) shape).curveTo(916.181, 876.934, 917.274, 876.133,
				919.311, 876.695);
		((GeneralPath) shape).curveTo(934.671, 880.935, 950.422, 881.972,
				966.271, 881.644);
		((GeneralPath) shape).curveTo(969.363, 881.579, 970.225, 881.974,
				969.73, 885.393);
		((GeneralPath) shape).curveTo(964.43, 922.001, 972.2, 955.358, 994.506,
				985.223);
		((GeneralPath) shape).curveTo(998.818, 990.997, 1004.22, 995.559,
				1008.94, 1000.84);
		((GeneralPath) shape).curveTo(939.857, 1000.85, 870.775, 1000.85,
				801.694, 1000.88);
		((GeneralPath) shape).curveTo(799.56, 1000.88, 797.348, 1000.444,
				795.331, 1001.59);

		g.setPaint(new Color(0xFFA89A));
		g.fill(shape);

		// _0_0_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1219.18, 668.876);
		((GeneralPath) shape).curveTo(1220.76, 663.1, 1219.68, 660.088, 1214.88,
				657.696);
		((GeneralPath) shape).curveTo(1183.45, 642.029, 1164.0, 616.155, 1152.2,
				583.888);
		((GeneralPath) shape).curveTo(1145.64, 565.952, 1143.09, 547.262,
				1141.59, 528.253);
		((GeneralPath) shape).curveTo(1140.83, 518.59, 1142.86, 510.475, 1147.4,
				502.217);
		((GeneralPath) shape).curveTo(1153.59, 490.978, 1157.89, 478.862,
				1161.62, 466.597);
		((GeneralPath) shape).curveTo(1162.53, 463.63, 1163.59, 463.027,
				1166.47, 464.1);
		((GeneralPath) shape).curveTo(1212.04, 481.031, 1242.74, 512.164,
				1257.07, 558.887);
		((GeneralPath) shape).curveTo(1263.87, 581.06, 1265.83, 603.864,
				1265.15, 626.938);
		((GeneralPath) shape).curveTo(1265.02, 631.357, 1266.26, 634.761,
				1270.16, 637.018);
		((GeneralPath) shape).curveTo(1266.78, 650.301, 1260.13, 661.113,
				1247.31, 667.271);
		((GeneralPath) shape).curveTo(1238.16, 671.673, 1228.71, 671.085,
				1219.18, 668.876);

		g.setPaint(new Color(0xD7AA61));
		g.fill(shape);

		// _0_0_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1087.21, 1128.1);
		((GeneralPath) shape).curveTo(1084.19, 1109.06, 1084.88, 1089.96,
				1086.52, 1070.88);
		((GeneralPath) shape).curveTo(1087.18, 1063.26, 1088.35, 1055.69,
				1089.23, 1048.09);
		((GeneralPath) shape).curveTo(1089.61, 1044.83, 1090.34, 1043.26,
				1094.5, 1044.34);
		((GeneralPath) shape).curveTo(1124.16, 1052.03, 1154.27, 1055.55,
				1184.93, 1052.82);
		((GeneralPath) shape).curveTo(1189.23, 1052.44, 1193.5, 1051.69,
				1197.75, 1050.97);
		((GeneralPath) shape).curveTo(1199.84, 1050.62, 1201.24, 1050.87,
				1201.85, 1053.23);
		((GeneralPath) shape).curveTo(1208.05, 1077.4, 1213.89, 1101.63,
				1214.93, 1126.71);
		((GeneralPath) shape).curveTo(1214.96, 1127.37, 1214.84, 1128.03,
				1214.79, 1128.69);
		((GeneralPath) shape).curveTo(1212.99, 1128.49, 1211.19, 1128.11,
				1209.39, 1128.11);
		((GeneralPath) shape).curveTo(1168.66, 1128.08, 1127.94, 1128.09,
				1087.21, 1128.1);

		g.setPaint(new Color(0x87C9D5));
		g.fill(shape);

		// _0_0_4
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(534.387, 348.069);
		((GeneralPath) shape).curveTo(531.383, 348.211, 528.801, 347.241,
				526.823, 344.96);
		((GeneralPath) shape).curveTo(513.422, 329.507, 499.968, 314.1, 486.705,
				298.53);
		((GeneralPath) shape).curveTo(482.748, 293.885, 483.365, 287.824,
				487.547, 284.233);
		((GeneralPath) shape).curveTo(491.895, 280.499, 498.084, 281.021,
				502.191, 285.744);
		((GeneralPath) shape).curveTo(515.176, 300.677, 528.118, 315.647,
				540.971, 330.694);
		((GeneralPath) shape).curveTo(545.354, 335.826, 545.272, 341.436,
				541.142, 345.403);
		((GeneralPath) shape).curveTo(539.262, 347.209, 537.046, 348.214,
				534.387, 348.069);

		g.setPaint(new Color(0xEF6F63));
		g.fill(shape);

		// _0_0_5
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(441.609, 362.798);
		((GeneralPath) shape).curveTo(442.254, 362.938, 443.748, 363.129,
				445.154, 363.589);
		((GeneralPath) shape).curveTo(462.692, 369.329, 480.229, 375.072,
				497.739, 380.899);
		((GeneralPath) shape).curveTo(503.113, 382.688, 505.604, 386.284,
				505.306, 391.267);
		((GeneralPath) shape).curveTo(505.022, 396.011, 501.771, 399.477,
				496.948, 400.535);
		((GeneralPath) shape).curveTo(495.001, 400.963, 493.267, 400.426,
				491.579, 399.878);
		((GeneralPath) shape).curveTo(473.715, 394.081, 455.859, 388.259,
				438.05, 382.298);
		((GeneralPath) shape).curveTo(432.5, 380.44, 430.198, 376.214, 431.142,
				370.623);
		((GeneralPath) shape).curveTo(431.902, 366.119, 435.913, 362.86,
				441.609, 362.798);

		g.fill(shape);

		// _0_0_6
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(450.494, 462.163);
		((GeneralPath) shape).curveTo(445.837, 462.163, 441.18, 462.214,
				436.525, 462.149);
		((GeneralPath) shape).curveTo(430.873, 462.071, 426.889, 458.507,
				426.368, 453.187);
		((GeneralPath) shape).curveTo(425.865, 448.055, 429.319, 443.456,
				434.811, 442.62);
		((GeneralPath) shape).curveTo(438.401, 442.074, 442.095, 442.168,
				445.745, 442.067);
		((GeneralPath) shape).curveTo(459.203, 441.692, 472.661, 441.329,
				486.121, 441.015);
		((GeneralPath) shape).curveTo(492.722, 440.861, 497.072, 444.56,
				497.324, 450.396);
		((GeneralPath) shape).curveTo(497.599, 456.777, 493.808, 460.7, 486.889,
				460.961);
		((GeneralPath) shape).curveTo(480.741, 461.192, 474.586, 461.285,
				468.433, 461.35);
		((GeneralPath) shape).curveTo(462.455, 461.413, 456.476, 461.363,
				450.497, 461.363);
		((GeneralPath) shape).curveTo(450.496, 461.63, 450.495, 461.896,
				450.494, 462.163);

		g.fill(shape);

		// _0_0_7
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(658.316, 421.898);
		((GeneralPath) shape).curveTo(650.48, 378.608, 654.401, 336.091,
				666.511, 294.063);
		((GeneralPath) shape).curveTo(677.806, 254.861, 695.328, 219.013,
				722.823, 188.48);
		((GeneralPath) shape).curveTo(749.086, 159.314, 781.107, 138.771,
				817.702, 124.991);
		((GeneralPath) shape).curveTo(821.281, 123.643, 821.899, 125.484,
				823.086, 127.771);
		((GeneralPath) shape).curveTo(840.599, 161.519, 862.207, 192.367,
				888.721, 219.67);
		((GeneralPath) shape).curveTo(914.011, 245.712, 943.218, 266.277,
				975.756, 282.398);
		((GeneralPath) shape).curveTo(1017.52, 303.091, 1059.99, 321.891,
				1105.05, 334.318);
		((GeneralPath) shape).curveTo(1120.78, 338.659, 1136.7, 342.173,
				1153.05, 343.107);
		((GeneralPath) shape).curveTo(1156.8, 343.321, 1158.04, 344.648,
				1158.34, 348.386);
		((GeneralPath) shape).curveTo(1160.16, 371.021, 1159.91, 393.563,
				1156.19, 416.01);
		((GeneralPath) shape).curveTo(1152.25, 415.382, 1151.55, 411.628,
				1149.72, 409.087);
		((GeneralPath) shape).curveTo(1141.16, 397.174, 1129.99, 389.65,
				1115.37, 387.095);
		((GeneralPath) shape).curveTo(1102.6, 384.864, 1090.99, 388.09, 1080.64,
				394.875);
		((GeneralPath) shape).curveTo(1061.52, 407.399, 1050.38, 425.265,
				1049.54, 448.378);
		((GeneralPath) shape).curveTo(1048.78, 469.602, 1062.19, 488.353,
				1082.22, 495.702);
		((GeneralPath) shape).curveTo(1096.91, 501.09, 1111.77, 503.575,
				1127.09, 498.15);
		((GeneralPath) shape).curveTo(1128.34, 497.707, 1130.17, 496.449,
				1131.12, 498.845);
		((GeneralPath) shape).curveTo(1111.94, 537.136, 1083.19, 566.004,
				1045.26, 585.707);
		((GeneralPath) shape).curveTo(1015.21, 601.318, 982.947, 609.324,
				949.46, 613.027);
		((GeneralPath) shape).curveTo(937.045, 614.399, 924.602, 615.017,
				912.131, 615.459);
		((GeneralPath) shape).curveTo(908.733, 615.579, 908.016, 614.405,
				908.062, 611.252);
		((GeneralPath) shape).curveTo(908.233, 599.594, 908.167, 587.932,
				908.117, 576.273);
		((GeneralPath) shape).curveTo(908.085, 568.854, 905.081, 565.831,
				897.793, 566.101);
		((GeneralPath) shape).curveTo(848.087, 567.945, 798.384, 569.846,
				748.68, 571.725);
		((GeneralPath) shape).curveTo(746.85, 571.794, 745.105, 571.801,
				743.457, 570.603);
		((GeneralPath) shape).curveTo(716.407, 550.925, 695.653, 525.998,
				681.058, 495.921);
		((GeneralPath) shape).curveTo(680.916, 495.63, 680.908, 495.274,
				680.837, 494.949);
		((GeneralPath) shape).curveTo(682.761, 493.885, 684.406, 495.105,
				686.042, 495.718);
		((GeneralPath) shape).curveTo(694.053, 498.722, 702.356, 500.889,
				710.826, 501.219);
		((GeneralPath) shape).curveTo(720.358, 501.59, 729.874, 500.145,
				738.005, 494.517);
		((GeneralPath) shape).curveTo(750.609, 485.794, 760.408, 474.754,
				765.32, 459.887);
		((GeneralPath) shape).curveTo(767.623, 452.916, 768.003, 445.768,
				766.564, 438.704);
		((GeneralPath) shape).curveTo(763.281, 422.589, 756.053, 408.4, 743.272,
				397.815);
		((GeneralPath) shape).curveTo(722.98, 381.008, 695.836, 383.41, 676.717,
				400.756);
		((GeneralPath) shape).curveTo(671.142, 405.815, 666.245, 411.445,
				662.342, 417.932);
		((GeneralPath) shape).curveTo(661.384, 419.524, 660.83, 421.781,
				658.316, 421.898);

		g.setPaint(new Color(0xFEF8EA));
		g.fill(shape);

		// _0_0_8
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(765.992, 59.151);
		((GeneralPath) shape).curveTo(769.148, 59.052, 772.309, 59.038, 775.458,
				58.836);
		((GeneralPath) shape).curveTo(780.129, 58.536, 783.479, 55.744, 783.098,
				51.411);
		((GeneralPath) shape).curveTo(782.731, 47.237, 785.256, 45.825, 787.92,
				44.199);
		((GeneralPath) shape).curveTo(803.897, 34.445, 821.532, 29.077, 839.624,
				25.109);
		((GeneralPath) shape).curveTo(869.495, 18.559, 899.742, 15.648, 930.312,
				17.019);
		((GeneralPath) shape).curveTo(954.211, 18.09, 977.395, 23.354, 1000.14,
				30.46);
		((GeneralPath) shape).curveTo(1058.08, 48.564, 1108.11, 79.606, 1149.43,
				124.066);
		((GeneralPath) shape).curveTo(1179.17, 156.061, 1202.93, 192.083,
				1217.28, 233.663);
		((GeneralPath) shape).curveTo(1232.74, 278.484, 1230.35, 322.268,
				1208.16, 364.569);
		((GeneralPath) shape).curveTo(1207.86, 365.138, 1207.48, 365.659,
				1206.88, 366.589);
		((GeneralPath) shape).curveTo(1199.33, 357.149, 1189.51, 353.343,
				1177.88, 353.993);
		((GeneralPath) shape).curveTo(1175.66, 354.117, 1174.77, 353.167,
				1174.89, 351.146);
		((GeneralPath) shape).curveTo(1175.05, 348.46, 1172.13, 345.263,
				1175.97, 343.171);
		((GeneralPath) shape).curveTo(1181.66, 340.076, 1183.29, 337.405,
				1181.98, 332.865);
		((GeneralPath) shape).curveTo(1180.8, 328.744, 1177.13, 327.329,
				1170.99, 327.658);
		((GeneralPath) shape).curveTo(1155.08, 328.509, 1139.48, 325.986,
				1124.04, 322.481);
		((GeneralPath) shape).curveTo(1086.06, 313.86, 1050.11, 299.498,
				1014.83, 283.323);
		((GeneralPath) shape).curveTo(993.804, 273.68, 972.876, 263.774,
				953.243, 251.453);
		((GeneralPath) shape).curveTo(920.097, 230.653, 892.81, 203.612,
				869.661, 172.224);
		((GeneralPath) shape).curveTo(853.399, 150.175, 839.681, 126.638,
				828.195, 101.782);
		((GeneralPath) shape).curveTo(825.761, 96.515, 822.188, 94.65, 817.473,
				96.127);
		((GeneralPath) shape).curveTo(813.482, 97.378, 811.623, 101.243,
				812.963, 106.731);
		((GeneralPath) shape).curveTo(813.776, 110.06, 811.557, 110.268,
				809.593, 111.015);
		((GeneralPath) shape).curveTo(786.274, 119.878, 764.491, 131.524,
				744.597, 146.651);
		((GeneralPath) shape).curveTo(727.0, 160.032, 711.584, 175.566, 698.088,
				193.053);
		((GeneralPath) shape).curveTo(696.719, 194.827, 695.927, 197.235,
				693.451, 198.005);
		((GeneralPath) shape).curveTo(690.955, 199.08, 689.357, 197.218,
				687.638, 196.089);
		((GeneralPath) shape).curveTo(674.023, 187.145, 667.056, 173.771,
				663.114, 158.54);
		((GeneralPath) shape).curveTo(661.317, 151.599, 660.536, 144.481,
				660.866, 137.259);
		((GeneralPath) shape).curveTo(661.009, 134.131, 661.887, 131.455,
				663.865, 129.006);
		((GeneralPath) shape).curveTo(687.55, 99.682, 716.745, 77.671, 751.415,
				62.905);
		((GeneralPath) shape).curveTo(754.327, 61.664, 757.352, 60.766, 760.425,
				60.034);
		((GeneralPath) shape).curveTo(762.307, 59.918, 764.167, 59.646, 765.992,
				59.151);

		g.setPaint(new Color(0xD7AA61));
		g.fill(shape);

		// _0_0_9
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(142.241, 720.865);
		((GeneralPath) shape).curveTo(163.869, 709.927, 186.826, 703.82,
				210.935, 701.719);
		((GeneralPath) shape).curveTo(241.001, 699.099, 270.419, 702.173,
				299.021, 711.837);
		((GeneralPath) shape).curveTo(334.501, 723.825, 363.094, 745.29,
				385.188, 775.477);
		((GeneralPath) shape).curveTo(385.545, 775.965, 386.209, 776.399,
				385.572, 777.438);
		((GeneralPath) shape).curveTo(371.488, 765.707, 356.351, 755.607,
				339.993, 747.38);
		((GeneralPath) shape).curveTo(333.147, 743.937, 326.118, 740.91,
				318.914, 738.316);
		((GeneralPath) shape).curveTo(316.656, 737.503, 315.422, 736.858,
				315.694, 733.86);
		((GeneralPath) shape).curveTo(316.082, 729.579, 313.744, 726.532,
				309.214, 725.837);
		((GeneralPath) shape).curveTo(304.435, 725.105, 301.172, 727.146,
				300.032, 732.098);
		((GeneralPath) shape).curveTo(296.704, 746.559, 292.304, 760.646,
				286.545, 774.335);
		((GeneralPath) shape).curveTo(285.017, 777.966, 284.073, 779.254,
				280.061, 776.551);
		((GeneralPath) shape).curveTo(269.914, 769.715, 258.769, 771.351,
				250.305, 780.249);
		((GeneralPath) shape).curveTo(248.744, 781.89, 247.334, 783.674,
				245.378, 785.946);
		((GeneralPath) shape).curveTo(244.067, 773.404, 243.279, 761.521,
				241.161, 749.807);
		((GeneralPath) shape).curveTo(240.539, 746.373, 239.982, 742.919,
				239.154, 739.533);
		((GeneralPath) shape).curveTo(237.974, 734.71, 233.919, 732.079,
				229.505, 733.037);
		((GeneralPath) shape).curveTo(225.128, 733.986, 222.459, 738.201,
				223.47, 743.013);
		((GeneralPath) shape).curveTo(228.18, 765.436, 229.753, 788.105,
				229.251, 810.978);
		((GeneralPath) shape).curveTo(228.928, 825.719, 229.074, 825.609,
				214.599, 826.919);
		((GeneralPath) shape).curveTo(205.065, 827.781, 195.967, 830.347,
				187.614, 835.178);
		((GeneralPath) shape).curveTo(175.14, 842.393, 171.502, 855.633,
				178.488, 868.234);
		((GeneralPath) shape).curveTo(182.991, 876.355, 189.185, 883.103,
				196.022, 889.237);
		((GeneralPath) shape).curveTo(198.893, 891.812, 200.049, 893.397,
				196.244, 896.276);
		((GeneralPath) shape).curveTo(191.201, 900.093, 188.92, 905.858,
				188.435, 911.954);
		((GeneralPath) shape).curveTo(187.501, 923.68, 189.739, 934.842,
				196.244, 944.853);
		((GeneralPath) shape).curveTo(199.299, 949.555, 203.337, 953.569,
				208.658, 955.287);
		((GeneralPath) shape).curveTo(212.795, 956.623, 213.334, 958.966,
				213.393, 962.616);
		((GeneralPath) shape).curveTo(213.623, 976.942, 213.91, 991.226,
				210.235, 1005.28);
		((GeneralPath) shape).curveTo(203.619, 1030.59, 187.986, 1046.61,
				162.062, 1051.42);
		((GeneralPath) shape).curveTo(136.734, 1056.12, 111.729, 1054.72,
				87.497, 1045.21);
		((GeneralPath) shape).curveTo(86.753, 1044.91, 86.085, 1044.43, 85.381,
				1044.04);
		((GeneralPath) shape).curveTo(82.122, 1041.2, 78.098, 1039.51, 74.74,
				1036.75);
		((GeneralPath) shape).curveTo(56.69, 1021.95, 45.872, 1002.61, 39.387,
				980.604);
		((GeneralPath) shape).curveTo(35.521, 967.485, 33.36, 954.082, 32.833,
				940.332);
		((GeneralPath) shape).curveTo(30.37, 876.094, 50.458, 819.201, 88.873,
				768.421);
		((GeneralPath) shape).curveTo(101.967, 751.113, 117.177, 735.73,
				136.166, 724.616);
		((GeneralPath) shape).curveTo(138.136, 723.283, 140.313, 722.276,
				142.241, 720.865);

		g.setPaint(new Color(0xDCAE53));
		g.fill(shape);

		// _0_0_10
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(836.963, 786.921);
		((GeneralPath) shape).curveTo(839.845, 793.017, 843.785, 798.284,
				849.463, 802.333);
		((GeneralPath) shape).curveTo(845.041, 805.925, 841.422, 809.8, 839.508,
				814.948);
		((GeneralPath) shape).curveTo(836.555, 822.887, 839.296, 829.995,
				847.048, 835.705);
		((GeneralPath) shape).curveTo(848.267, 836.602, 852.105, 836.758,
				850.083, 839.406);
		((GeneralPath) shape).curveTo(847.988, 842.148, 849.734, 847.546,
				843.33, 847.585);
		((GeneralPath) shape).curveTo(823.198, 847.707, 803.07, 848.489,
				782.942, 849.065);
		((GeneralPath) shape).curveTo(741.025, 850.265, 699.108, 851.496,
				657.191, 852.711);
		((GeneralPath) shape).curveTo(656.526, 852.731, 655.848, 852.647,
				655.194, 852.738);
		((GeneralPath) shape).curveTo(647.54, 853.807, 642.244, 852.874,
				639.073, 843.707);
		((GeneralPath) shape).curveTo(635.518, 833.432, 625.383, 829.561,
				612.906, 831.159);
		((GeneralPath) shape).curveTo(609.417, 831.606, 608.559, 830.939,
				608.012, 827.473);
		((GeneralPath) shape).curveTo(606.879, 820.298, 601.242, 815.482,
				593.304, 814.852);
		((GeneralPath) shape).curveTo(589.319, 814.536, 588.521, 813.025,
				588.562, 809.347);
		((GeneralPath) shape).curveTo(589.015, 768.882, 589.298, 728.415,
				589.64, 687.948);
		((GeneralPath) shape).curveTo(589.857, 662.136, 590.085, 636.323,
				590.331, 610.511);
		((GeneralPath) shape).curveTo(590.376, 605.851, 590.712, 601.188,
				590.575, 596.537);
		((GeneralPath) shape).curveTo(590.486, 593.527, 591.783, 593.091,
				594.42, 593.006);
		((GeneralPath) shape).curveTo(625.852, 591.993, 657.28, 590.877,
				688.708, 589.747);
		((GeneralPath) shape).curveTo(732.275, 588.181, 775.841, 586.568,
				819.408, 584.998);
		((GeneralPath) shape).curveTo(842.688, 584.158, 865.971, 583.405,
				889.247, 582.485);
		((GeneralPath) shape).curveTo(891.481, 582.397, 892.367, 582.876,
				892.153, 585.096);
		((GeneralPath) shape).curveTo(892.089, 585.756, 892.144, 586.428,
				892.144, 587.095);
		((GeneralPath) shape).curveTo(892.144, 642.076, 892.144, 697.058,
				892.144, 752.04);
		((GeneralPath) shape).curveTo(892.144, 753.52, 892.144, 755.0, 892.144,
				756.839);
		((GeneralPath) shape).curveTo(882.645, 754.164, 873.361, 754.727,
				864.091, 755.017);
		((GeneralPath) shape).curveTo(863.863, 754.765, 863.625, 754.491,
				863.595, 754.175);
		((GeneralPath) shape).curveTo(862.927, 747.065, 860.944, 745.868,
				854.158, 748.452);
		((GeneralPath) shape).curveTo(851.97, 749.285, 850.088, 750.698,
				847.979, 751.656);
		((GeneralPath) shape).curveTo(844.547, 753.215, 842.915, 752.174,
				843.845, 748.62);
		((GeneralPath) shape).curveTo(846.67, 737.828, 841.981, 729.578,
				835.433, 721.828);
		((GeneralPath) shape).curveTo(830.378, 715.845, 830.314, 713.412,
				835.025, 706.792);
		((GeneralPath) shape).curveTo(837.864, 705.956, 838.912, 703.827,
				839.114, 701.094);
		((GeneralPath) shape).curveTo(839.178, 700.637, 839.286, 700.189,
				839.408, 699.745);
		((GeneralPath) shape).curveTo(839.675, 698.849, 839.833, 697.931,
				839.955, 697.002);
		((GeneralPath) shape).curveTo(842.14, 690.172, 843.769, 683.223,
				843.874, 676.026);
		((GeneralPath) shape).curveTo(843.95, 670.871, 843.374, 665.642,
				841.364, 660.944);
		((GeneralPath) shape).curveTo(838.421, 654.066, 826.704, 652.379,
				821.626, 657.857);
		((GeneralPath) shape).curveTo(815.488, 664.478, 813.282, 672.988,
				811.661, 681.43);
		((GeneralPath) shape).curveTo(810.412, 687.932, 808.729, 694.41,
				808.351, 701.086);
		((GeneralPath) shape).curveTo(808.204, 703.684, 806.699, 704.987,
				804.005, 704.675);
		((GeneralPath) shape).curveTo(799.189, 704.115, 794.378, 704.003,
				789.551, 704.631);
		((GeneralPath) shape).curveTo(786.376, 705.044, 784.852, 703.23,
				784.667, 700.332);
		((GeneralPath) shape).curveTo(784.263, 693.988, 782.213, 687.943,
				781.361, 681.727);
		((GeneralPath) shape).curveTo(780.531, 675.679, 778.604, 670.09,
				776.528, 664.476);
		((GeneralPath) shape).curveTo(775.775, 662.439, 774.834, 660.389,
				773.329, 658.858);
		((GeneralPath) shape).curveTo(770.178, 655.654, 766.693, 651.781,
				761.819, 653.427);
		((GeneralPath) shape).curveTo(756.554, 655.205, 751.939, 657.952,
				750.212, 664.527);
		((GeneralPath) shape).curveTo(747.693, 674.121, 750.94, 682.513, 753.67,
				691.174);
		((GeneralPath) shape).curveTo(755.873, 698.164, 758.341, 705.066,
				760.74, 711.989);
		((GeneralPath) shape).curveTo(761.889, 715.305, 761.667, 718.201,
				759.287, 720.805);
		((GeneralPath) shape).curveTo(756.782, 723.547, 755.462, 727.045,
				753.324, 730.014);
		((GeneralPath) shape).curveTo(752.126, 731.802, 751.589, 733.846,
				751.079, 735.899);
		((GeneralPath) shape).curveTo(750.155, 742.188, 749.952, 748.456,
				751.155, 754.749);
		((GeneralPath) shape).curveTo(751.818, 758.214, 750.89, 758.962, 747.46,
				758.056);
		((GeneralPath) shape).curveTo(745.838, 757.628, 744.389, 756.792,
				742.926, 756.001);
		((GeneralPath) shape).curveTo(740.844, 754.874, 738.645, 754.16,
				736.255, 754.209);
		((GeneralPath) shape).curveTo(730.936, 754.319, 728.425, 758.531,
				731.121, 763.135);
		((GeneralPath) shape).curveTo(734.597, 769.071, 739.638, 773.708,
				744.402, 778.576);
		((GeneralPath) shape).curveTo(751.137, 785.457, 758.419, 791.702,
				766.002, 797.607);
		((GeneralPath) shape).curveTo(768.272, 799.375, 767.796, 801.898,
				767.592, 804.067);
		((GeneralPath) shape).curveTo(766.705, 813.507, 766.373, 822.968,
				766.055, 832.431);
		((GeneralPath) shape).curveTo(767.29, 836.789, 768.095, 837.145,
				773.921, 835.892);
		((GeneralPath) shape).curveTo(782.337, 834.55, 790.602, 837.61, 799.046,
				836.618);
		((GeneralPath) shape).curveTo(806.274, 835.769, 813.561, 836.002,
				820.839, 836.437);
		((GeneralPath) shape).curveTo(825.029, 836.687, 826.453, 835.131,
				826.586, 830.808);
		((GeneralPath) shape).curveTo(826.662, 828.323, 826.281, 825.843,
				826.335, 823.354);
		((GeneralPath) shape).curveTo(826.799, 821.005, 827.094, 818.652,
				826.423, 816.287);
		((GeneralPath) shape).curveTo(826.223, 809.846, 826.796, 803.416,
				826.582, 796.956);
		((GeneralPath) shape).curveTo(826.43, 792.39, 831.351, 788.092, 836.963,
				786.921);

		g.setPaint(new Color(0xFEFAF9));
		g.fill(shape);

		// _0_0_11
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1079.98, 675.085);
		((GeneralPath) shape).curveTo(1094.88, 676.179, 1109.34, 679.671,
				1122.6, 686.254);
		((GeneralPath) shape).curveTo(1128.46, 689.162, 1133.44, 689.73,
				1139.53, 688.953);
		((GeneralPath) shape).curveTo(1163.23, 685.928, 1186.3, 690.025,
				1208.97, 696.441);
		((GeneralPath) shape).curveTo(1244.37, 706.464, 1275.54, 724.271,
				1302.07, 749.745);
		((GeneralPath) shape).curveTo(1329.14, 775.745, 1347.02, 807.02,
				1352.54, 844.481);
		((GeneralPath) shape).curveTo(1358.8, 886.909, 1352.77, 927.137,
				1328.32, 963.331);
		((GeneralPath) shape).curveTo(1310.28, 990.027, 1285.37, 1008.48,
				1256.28, 1021.66);
		((GeneralPath) shape).curveTo(1249.02, 1024.96, 1242.12, 1029.07,
				1234.36, 1031.21);
		((GeneralPath) shape).curveTo(1229.07, 1032.67, 1223.81, 1033.79,
				1218.2, 1033.19);
		((GeneralPath) shape).curveTo(1211.35, 1032.46, 1207.29, 1028.78,
				1205.15, 1022.55);
		((GeneralPath) shape).curveTo(1202.37, 1014.47, 1202.58, 1006.11,
				1203.36, 997.819);
		((GeneralPath) shape).curveTo(1204.58, 984.709, 1207.82, 971.962,
				1211.19, 959.262);
		((GeneralPath) shape).curveTo(1211.86, 956.754, 1213.04, 955.018,
				1215.38, 953.516);
		((GeneralPath) shape).curveTo(1226.94, 946.067, 1230.34, 934.23,
				1232.13, 921.521);
		((GeneralPath) shape).curveTo(1233.25, 913.61, 1230.07, 906.147,
				1230.18, 898.365);
		((GeneralPath) shape).curveTo(1230.2, 896.575, 1229.14, 895.134,
				1227.75, 894.175);
		((GeneralPath) shape).curveTo(1217.58, 887.155, 1212.84, 876.551,
				1208.82, 865.503);
		((GeneralPath) shape).curveTo(1202.13, 847.084, 1199.05, 827.903,
				1197.21, 808.496);
		((GeneralPath) shape).curveTo(1196.81, 804.174, 1195.26, 800.98, 1190.9,
				800.061);
		((GeneralPath) shape).curveTo(1186.38, 799.108, 1183.28, 801.188,
				1181.34, 805.392);
		((GeneralPath) shape).curveTo(1180.03, 808.251, 1178.58, 811.12,
				1176.74, 813.655);
		((GeneralPath) shape).curveTo(1167.93, 825.763, 1154.86, 825.945,
				1145.37, 814.245);
		((GeneralPath) shape).curveTo(1138.45, 805.709, 1135.36, 795.556,
				1133.14, 785.072);
		((GeneralPath) shape).curveTo(1130.18, 771.155, 1129.44, 757.055,
				1129.94, 742.868);
		((GeneralPath) shape).curveTo(1130.0, 741.371, 1130.11, 739.846, 1129.9,
				738.375);
		((GeneralPath) shape).curveTo(1129.16, 733.107, 1123.99, 729.66,
				1119.31, 731.716);
		((GeneralPath) shape).curveTo(1108.35, 736.524, 1096.77, 739.66,
				1085.92, 744.798);
		((GeneralPath) shape).curveTo(1067.91, 753.329, 1051.31, 763.961,
				1036.03, 776.71);
		((GeneralPath) shape).curveTo(1029.85, 769.643, 1029.16, 759.891,
				1034.73, 752.41);
		((GeneralPath) shape).curveTo(1042.41, 742.108, 1053.65, 738.25,
				1065.67, 735.981);
		((GeneralPath) shape).curveTo(1065.99, 735.922, 1066.32, 736.083,
				1066.65, 736.141);
		((GeneralPath) shape).curveTo(1067.01, 736.205, 1067.27, 736.364,
				1067.95, 737.735);
		((GeneralPath) shape).curveTo(1058.76, 728.965, 1051.04, 720.472,
				1047.42, 709.156);
		((GeneralPath) shape).curveTo(1043.63, 697.326, 1048.14, 687.127,
				1059.06, 680.962);
		((GeneralPath) shape).curveTo(1065.61, 677.267, 1072.68, 675.872,
				1079.98, 675.085);

		g.setPaint(new Color(0xC9A76F));
		g.fill(shape);

		// _0_0_12
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1183.64, 830.322);
		((GeneralPath) shape).curveTo(1186.21, 845.193, 1189.19, 859.131,
				1194.2, 872.539);
		((GeneralPath) shape).curveTo(1196.06, 877.523, 1198.07, 882.458,
				1200.84, 887.019);
		((GeneralPath) shape).curveTo(1202.09, 889.081, 1202.1, 889.893,
				1199.39, 890.572);
		((GeneralPath) shape).curveTo(1193.48, 892.054, 1191.04, 895.709,
				1192.18, 900.48);
		((GeneralPath) shape).curveTo(1193.25, 904.923, 1197.05, 906.954,
				1202.75, 906.127);
		((GeneralPath) shape).curveTo(1212.59, 904.698, 1216.55, 908.176,
				1216.25, 917.986);
		((GeneralPath) shape).curveTo(1216.11, 922.356, 1215.19, 926.589,
				1213.67, 930.651);
		((GeneralPath) shape).curveTo(1210.01, 940.408, 1203.22, 943.781,
				1193.09, 941.059);
		((GeneralPath) shape).curveTo(1191.33, 940.587, 1189.58, 939.973,
				1187.79, 939.759);
		((GeneralPath) shape).curveTo(1183.56, 939.257, 1180.53, 941.202,
				1179.35, 945.176);
		((GeneralPath) shape).curveTo(1178.13, 949.316, 1179.57, 952.825,
				1183.58, 954.758);
		((GeneralPath) shape).curveTo(1185.66, 955.756, 1187.95, 956.678,
				1190.2, 956.825);
		((GeneralPath) shape).curveTo(1194.77, 957.122, 1194.76, 959.152,
				1193.77, 962.888);
		((GeneralPath) shape).curveTo(1189.5, 978.836, 1186.35, 994.962,
				1187.06, 1011.63);
		((GeneralPath) shape).curveTo(1187.41, 1019.88, 1189.26, 1027.7, 1193.4,
				1035.48);
		((GeneralPath) shape).curveTo(1179.54, 1037.88, 1166.04, 1038.17,
				1152.48, 1037.32);
		((GeneralPath) shape).curveTo(1116.38, 1035.07, 1081.8, 1027.11, 1049.7,
				1009.88);
		((GeneralPath) shape).curveTo(1039.85, 1004.59, 1030.62, 998.368,
				1022.28, 990.886);
		((GeneralPath) shape).curveTo(1021.38, 990.079, 1018.48, 989.196,
				1021.6, 987.454);
		((GeneralPath) shape).curveTo(1035.49, 979.698, 1039.22, 961.027,
				1033.05, 944.385);
		((GeneralPath) shape).curveTo(1026.02, 925.402, 1012.2, 913.195,
				994.151, 905.09);
		((GeneralPath) shape).curveTo(983.283, 900.211, 983.365, 900.102,
				985.399, 888.082);
		((GeneralPath) shape).curveTo(989.957, 861.155, 1001.91, 837.651,
				1019.05, 816.731);
		((GeneralPath) shape).curveTo(1042.98, 787.528, 1072.75, 766.31,
				1108.11, 753.006);
		((GeneralPath) shape).curveTo(1113.67, 750.914, 1113.76, 751.001,
				1113.92, 757.048);
		((GeneralPath) shape).curveTo(1114.42, 775.473, 1116.79, 793.568,
				1124.43, 810.536);
		((GeneralPath) shape).curveTo(1130.39, 823.786, 1139.28, 834.207,
				1154.12, 837.95);
		((GeneralPath) shape).curveTo(1164.93, 840.676, 1174.5, 837.918,
				1183.64, 830.322);

		g.setPaint(new Color(0xFEF8EA));
		g.fill(shape);

		// _0_0_13
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(348.438, 718.09);
		((GeneralPath) shape).curveTo(328.751, 705.278, 307.426, 696.295,
				284.567, 690.993);
		((GeneralPath) shape).curveTo(282.11, 690.423, 281.331, 689.482,
				281.335, 687.032);
		((GeneralPath) shape).curveTo(281.395, 647.39, 281.411, 607.748,
				281.309, 568.107);
		((GeneralPath) shape).curveTo(281.302, 565.112, 282.436, 564.968,
				284.848, 565.213);
		((GeneralPath) shape).curveTo(335.992, 570.407, 387.14, 575.563,
				438.291, 580.678);
		((GeneralPath) shape).curveTo(482.33, 585.081, 526.37, 589.475, 570.426,
				593.697);
		((GeneralPath) shape).curveTo(573.956, 594.035, 574.747, 595.208,
				574.715, 598.508);
		((GeneralPath) shape).curveTo(574.082, 662.269, 573.565, 726.032,
				572.972, 789.794);
		((GeneralPath) shape).curveTo(572.936, 793.749, 572.47, 797.699,
				572.204, 801.652);
		((GeneralPath) shape).curveTo(568.506, 801.058, 564.809, 800.465,
				561.111, 799.871);
		((GeneralPath) shape).curveTo(559.933, 798.594, 559.332, 797.02,
				559.166, 795.354);
		((GeneralPath) shape).curveTo(558.059, 784.249, 555.743, 773.328,
				554.171, 762.305);
		((GeneralPath) shape).curveTo(553.368, 760.406, 557.384, 759.98,
				555.569, 757.642);
		((GeneralPath) shape).curveTo(555.133, 757.08, 555.066, 756.696,
				553.908, 756.98);
		((GeneralPath) shape).curveTo(550.544, 751.197, 549.807, 744.543,
				547.763, 738.325);
		((GeneralPath) shape).curveTo(546.728, 735.176, 549.721, 734.766,
				551.166, 733.849);
		((GeneralPath) shape).curveTo(547.303, 733.867, 544.689, 730.808,
				543.737, 724.518);
		((GeneralPath) shape).curveTo(543.817, 724.041, 544.026, 723.63,
				544.369, 723.29);
		((GeneralPath) shape).curveTo(547.372, 720.739, 546.373, 718.222,
				543.904, 716.25);
		((GeneralPath) shape).curveTo(541.621, 714.427, 540.406, 712.353,
				540.385, 709.476);
		((GeneralPath) shape).curveTo(540.37, 707.405, 539.589, 705.567,
				538.346, 703.946);
		((GeneralPath) shape).curveTo(531.79, 695.394, 525.622, 686.533,
				518.277, 678.605);
		((GeneralPath) shape).curveTo(516.903, 677.122, 515.331, 675.662,
				513.436, 675.163);
		((GeneralPath) shape).curveTo(504.015, 672.683, 495.488, 667.593,
				485.409, 666.743);
		((GeneralPath) shape).curveTo(479.914, 666.28, 475.216, 667.621,
				470.569, 669.387);
		((GeneralPath) shape).curveTo(455.878, 674.968, 444.016, 684.836,
				432.702, 695.433);
		((GeneralPath) shape).curveTo(431.202, 696.836, 429.819, 698.181,
				429.86, 700.437);
		((GeneralPath) shape).curveTo(429.878, 701.459, 429.527, 702.466,
				428.823, 703.189);
		((GeneralPath) shape).curveTo(424.813, 707.305, 422.52, 712.539,
				419.475, 717.286);
		((GeneralPath) shape).curveTo(417.29, 720.692, 415.465, 720.928,
				412.901, 717.824);
		((GeneralPath) shape).curveTo(408.188, 712.118, 402.179, 710.394,
				395.03, 709.482);
		((GeneralPath) shape).curveTo(389.192, 708.737, 383.51, 706.66, 377.441,
				707.052);
		((GeneralPath) shape).curveTo(375.609, 707.17, 373.954, 707.31, 372.187,
				708.112);
		((GeneralPath) shape).curveTo(365.561, 711.118, 358.773, 713.777,
				352.723, 717.917);
		((GeneralPath) shape).curveTo(351.304, 718.888, 349.887, 718.563,
				348.438, 718.09);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

		// _0_0_14
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(400.394, 988.702);
		((GeneralPath) shape).curveTo(367.983, 1021.79, 281.873, 1048.52,
				216.097, 1033.36);
		((GeneralPath) shape).curveTo(221.341, 1023.79, 225.164, 1013.77,
				227.206, 1003.13);
		((GeneralPath) shape).curveTo(229.794, 989.642, 230.395, 976.03,
				229.292, 962.377);
		((GeneralPath) shape).curveTo(228.985, 958.587, 229.789, 956.539,
				233.725, 955.928);
		((GeneralPath) shape).curveTo(235.305, 955.683, 236.915, 954.9, 238.257,
				953.988);
		((GeneralPath) shape).curveTo(241.391, 951.859, 242.225, 948.691,
				241.19, 945.195);
		((GeneralPath) shape).curveTo(240.137, 941.636, 237.629, 939.653,
				233.791, 939.725);
		((GeneralPath) shape).curveTo(232.638, 939.747, 231.428, 939.819,
				230.351, 940.183);
		((GeneralPath) shape).curveTo(223.404, 942.527, 216.223, 943.818,
				211.001, 937.438);
		((GeneralPath) shape).curveTo(204.817, 929.883, 202.897, 920.778,
				205.121, 911.152);
		((GeneralPath) shape).curveTo(206.133, 906.772, 210.033, 905.913,
				213.795, 905.959);
		((GeneralPath) shape).curveTo(218.084, 906.012, 222.415, 906.383,
				226.64, 907.118);
		((GeneralPath) shape).curveTo(240.033, 909.448, 250.214, 903.98,
				255.386, 891.43);
		((GeneralPath) shape).curveTo(259.206, 882.161, 259.655, 872.302,
				260.068, 862.521);
		((GeneralPath) shape).curveTo(260.24, 858.43, 261.595, 857.551, 265.311,
				857.865);
		((GeneralPath) shape).curveTo(273.428, 858.551, 281.539, 859.05,
				289.716, 858.076);
		((GeneralPath) shape).curveTo(308.806, 855.802, 318.418, 842.133,
				313.731, 823.475);
		((GeneralPath) shape).curveTo(311.09, 812.959, 305.759, 803.784, 299.14,
				795.353);
		((GeneralPath) shape).curveTo(297.062, 792.706, 296.567, 790.619,
				298.168, 787.437);
		((GeneralPath) shape).curveTo(303.252, 777.33, 306.856, 766.6, 310.573,
				755.935);
		((GeneralPath) shape).curveTo(311.334, 753.752, 311.921, 752.648,
				314.683, 753.787);
		((GeneralPath) shape).curveTo(366.355, 775.079, 405.939, 809.094,
				427.856, 861.535);
		((GeneralPath) shape).curveTo(433.74, 875.614, 436.448, 890.496,
				436.554, 905.807);
		((GeneralPath) shape).curveTo(436.574, 908.697, 436.581, 910.439,
				432.384, 910.201);
		((GeneralPath) shape).curveTo(411.624, 909.024, 391.502, 925.933,
				386.222, 948.443);
		((GeneralPath) shape).curveTo(384.64, 955.185, 384.504, 961.975,
				385.734, 968.762);
		((GeneralPath) shape).curveTo(387.328, 977.555, 391.364, 984.7, 400.394,
				988.702);

		g.setPaint(new Color(0xFEF8EA));
		g.fill(shape);

		// _0_0_15
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(348.438, 718.09);
		((GeneralPath) shape).curveTo(354.051, 716.739, 358.83, 713.36, 363.634,
				710.606);
		((GeneralPath) shape).curveTo(373.127, 705.165, 382.47, 704.306,
				392.375, 708.035);
		((GeneralPath) shape).curveTo(394.448, 708.815, 396.239, 708.258,
				398.146, 708.436);
		((GeneralPath) shape).curveTo(404.826, 709.062, 410.25, 712.062,
				414.111, 717.365);
		((GeneralPath) shape).curveTo(415.997, 719.956, 417.157, 719.412,
				418.294, 717.353);
		((GeneralPath) shape).curveTo(421.233, 712.037, 426.124, 707.024,
				427.577, 701.982);
		((GeneralPath) shape).curveTo(429.579, 695.035, 434.759, 692.485,
				438.804, 688.475);
		((GeneralPath) shape).curveTo(443.092, 684.223, 448.206, 680.729,
				453.227, 677.191);
		((GeneralPath) shape).curveTo(458.612, 673.396, 464.506, 671.002,
				470.175, 668.129);
		((GeneralPath) shape).curveTo(479.588, 663.361, 489.481, 664.517,
				498.726, 668.929);
		((GeneralPath) shape).curveTo(502.695, 670.823, 506.93, 671.281,
				510.927, 672.826);
		((GeneralPath) shape).curveTo(514.754, 674.307, 517.836, 676.313,
				520.626, 679.337);
		((GeneralPath) shape).curveTo(527.338, 686.609, 532.692, 694.918,
				538.92, 702.551);
		((GeneralPath) shape).curveTo(540.7, 704.732, 542.216, 707.051, 541.577,
				710.194);
		((GeneralPath) shape).curveTo(541.269, 711.708, 542.013, 713.771,
				543.107, 714.145);
		((GeneralPath) shape).curveTo(547.582, 715.671, 546.808, 719.828,
				548.167, 723.174);
		((GeneralPath) shape).curveTo(546.816, 723.423, 545.486, 723.669,
				544.155, 723.915);
		((GeneralPath) shape).curveTo(542.984, 721.909, 543.635, 719.384,
				542.316, 717.42);
		((GeneralPath) shape).curveTo(543.659, 719.366, 542.811, 721.95,
				544.192, 723.885);
		((GeneralPath) shape).curveTo(545.039, 726.661, 545.407, 729.57,
				548.193, 731.486);
		((GeneralPath) shape).curveTo(549.25, 732.213, 549.572, 733.512,
				551.279, 732.577);
		((GeneralPath) shape).curveTo(552.101, 732.127, 553.18, 732.596,
				553.155, 734.003);
		((GeneralPath) shape).curveTo(553.11, 736.445, 551.88, 734.115, 551.54,
				734.42);
		((GeneralPath) shape).curveTo(550.27, 735.557, 548.025, 735.802, 549.07,
				738.772);
		((GeneralPath) shape).curveTo(551.14, 744.655, 552.593, 750.756,
				554.301, 756.768);
		((GeneralPath) shape).curveTo(555.639, 757.955, 554.586, 759.598,
				555.094, 760.943);
		((GeneralPath) shape).curveTo(557.7, 773.826, 559.701, 786.803, 561.112,
				799.871);
		((GeneralPath) shape).curveTo(550.519, 800.913, 540.963, 804.653,
				532.379, 810.918);
		((GeneralPath) shape).curveTo(527.683, 811.76, 523.048, 810.589,
				518.491, 810.007);
		((GeneralPath) shape).curveTo(506.626, 808.493, 494.541, 808.286,
				482.924, 805.308);
		((GeneralPath) shape).curveTo(474.15, 803.059, 465.074, 803.503,
				456.349, 801.395);
		((GeneralPath) shape).curveTo(455.881, 801.395, 455.41, 801.446,
				454.964, 801.574);
		((GeneralPath) shape).curveTo(453.397, 803.104, 454.629, 804.723,
				454.89, 806.248);
		((GeneralPath) shape).curveTo(455.846, 811.828, 457.645, 817.249,
				458.283, 822.884);
		((GeneralPath) shape).curveTo(458.829, 827.717, 457.556, 828.887,
				452.786, 828.439);
		((GeneralPath) shape).curveTo(449.659, 828.146, 446.835, 828.453,
				443.68, 829.58);
		((GeneralPath) shape).curveTo(439.031, 831.241, 433.845, 831.165,
				429.383, 827.97);
		((GeneralPath) shape).curveTo(425.404, 823.644, 424.707, 818.069,
				423.058, 812.717);
		((GeneralPath) shape).curveTo(419.375, 800.769, 413.501, 789.735,
				406.956, 779.083);
		((GeneralPath) shape).curveTo(391.86, 754.518, 372.445, 734.102,
				348.438, 718.09);

		g.setPaint(new Color(0xAAD874));
		g.fill(shape);

		// _0_0_16
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(836.963, 786.921);
		((GeneralPath) shape).curveTo(835.58, 789.287, 832.597, 789.002,
				830.695, 791.153);
		((GeneralPath) shape).curveTo(826.166, 796.273, 828.084, 802.105,
				827.82, 807.632);
		((GeneralPath) shape).curveTo(827.691, 810.342, 827.217, 813.042,
				827.313, 815.764);
		((GeneralPath) shape).curveTo(827.797, 818.176, 827.834, 820.585,
				827.291, 822.993);
		((GeneralPath) shape).curveTo(827.193, 824.634, 827.867, 826.135,
				827.871, 827.842);
		((GeneralPath) shape).curveTo(827.891, 838.14, 827.439, 838.38, 817.382,
				837.727);
		((GeneralPath) shape).curveTo(812.252, 837.393, 807.054, 837.046,
				801.828, 837.782);
		((GeneralPath) shape).curveTo(792.305, 839.122, 782.827, 836.329,
				773.279, 837.01);
		((GeneralPath) shape).curveTo(769.42, 837.874, 766.178, 837.526, 765.13,
				832.865);
		((GeneralPath) shape).curveTo(765.188, 825.433, 764.756, 817.976,
				765.851, 810.588);
		((GeneralPath) shape).curveTo(766.745, 804.553, 767.388, 799.196,
				760.988, 794.918);
		((GeneralPath) shape).curveTo(755.244, 791.078, 750.349, 785.938,
				745.395, 780.922);
		((GeneralPath) shape).curveTo(740.379, 775.844, 735.258, 770.951,
				730.926, 765.24);
		((GeneralPath) shape).curveTo(728.643, 762.231, 727.261, 759.219,
				729.728, 755.736);
		((GeneralPath) shape).curveTo(732.273, 752.142, 735.958, 752.326,
				739.647, 753.16);
		((GeneralPath) shape).curveTo(743.375, 754.003, 746.192, 757.115,
				750.526, 757.61);
		((GeneralPath) shape).curveTo(748.559, 750.313, 748.531, 743.053,
				749.995, 735.784);
		((GeneralPath) shape).curveTo(751.996, 734.247, 751.195, 731.735,
				752.075, 729.808);
		((GeneralPath) shape).curveTo(754.37, 726.396, 755.723, 722.421,
				758.787, 719.436);
		((GeneralPath) shape).curveTo(760.469, 717.798, 760.727, 715.637,
				759.812, 713.08);
		((GeneralPath) shape).curveTo(755.733, 701.679, 751.939, 690.176,
				748.023, 678.716);
		((GeneralPath) shape).curveTo(744.846, 669.419, 752.259, 654.169,
				761.22, 651.609);
		((GeneralPath) shape).curveTo(764.798, 650.587, 772.545, 654.557,
				775.132, 658.981);
		((GeneralPath) shape).curveTo(778.907, 665.439, 781.365, 672.325,
				782.381, 679.833);
		((GeneralPath) shape).curveTo(783.36, 687.072, 785.95, 694.069, 786.03,
				701.465);
		((GeneralPath) shape).curveTo(786.057, 703.959, 788.019, 703.721,
				789.124, 703.478);
		((GeneralPath) shape).curveTo(794.098, 702.388, 799.052, 702.578,
				803.929, 703.572);
		((GeneralPath) shape).curveTo(806.632, 704.123, 807.033, 703.128,
				807.24, 700.874);
		((GeneralPath) shape).curveTo(808.296, 689.347, 810.288, 677.954,
				814.281, 667.086);
		((GeneralPath) shape).curveTo(816.462, 661.151, 820.088, 655.901,
				825.926, 652.638);
		((GeneralPath) shape).curveTo(827.375, 651.828, 828.652, 651.633,
				830.146, 652.386);
		((GeneralPath) shape).curveTo(831.777, 653.209, 833.403, 654.185,
				835.15, 654.613);
		((GeneralPath) shape).curveTo(841.76, 656.235, 843.29, 660.877, 844.369,
				667.044);
		((GeneralPath) shape).curveTo(846.251, 677.804, 844.008, 687.816,
				841.155, 697.914);
		((GeneralPath) shape).curveTo(840.575, 698.466, 839.878, 698.951,
				840.015, 699.912);
		((GeneralPath) shape).curveTo(840.09, 700.227, 840.119, 700.546,
				840.101, 700.87);
		((GeneralPath) shape).curveTo(837.974, 702.376, 837.777, 705.134,
				836.148, 706.963);
		((GeneralPath) shape).curveTo(833.71, 709.276, 833.72, 712.663, 832.45,
				715.491);
		((GeneralPath) shape).curveTo(831.735, 717.082, 833.297, 717.709,
				834.232, 718.631);
		((GeneralPath) shape).curveTo(839.733, 724.058, 843.835, 730.29,
				845.711, 737.939);
		((GeneralPath) shape).curveTo(846.834, 742.522, 845.401, 746.747,
				844.469, 751.428);
		((GeneralPath) shape).curveTo(846.474, 751.68, 847.612, 750.346,
				848.987, 749.637);
		((GeneralPath) shape).curveTo(850.902, 748.649, 852.81, 747.6, 854.827,
				746.868);
		((GeneralPath) shape).curveTo(857.783, 745.796, 860.966, 744.532,
				863.743, 747.059);
		((GeneralPath) shape).curveTo(866.367, 749.446, 864.619, 752.326,
				864.091, 755.017);
		((GeneralPath) shape).curveTo(859.197, 756.399, 854.122, 757.071,
				849.415, 759.201);
		((GeneralPath) shape).curveTo(837.078, 764.784, 832.871, 774.023,
				836.963, 786.921);

		g.setPaint(new Color(0xFFC7BF));
		g.fill(shape);

		// _0_0_17
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(658.316, 421.898);
		((GeneralPath) shape).curveTo(664.197, 412.18, 670.818, 403.149,
				680.117, 396.278);
		((GeneralPath) shape).curveTo(705.403, 377.595, 737.103, 382.87,
				755.131, 408.857);
		((GeneralPath) shape).curveTo(762.962, 420.147, 768.378, 432.546,
				768.768, 446.263);
		((GeneralPath) shape).curveTo(769.112, 458.359, 764.322, 469.195,
				756.704, 478.752);
		((GeneralPath) shape).curveTo(747.675, 490.077, 737.331, 499.356,
				722.444, 501.825);
		((GeneralPath) shape).curveTo(707.831, 504.249, 694.178, 500.442,
				680.837, 494.949);
		((GeneralPath) shape).curveTo(671.334, 476.98, 665.635, 457.691, 661.21,
				437.981);
		((GeneralPath) shape).curveTo(660.018, 432.671, 659.269, 427.261,
				658.316, 421.898);

		g.setPaint(new Color(0xFFD5BA));
		g.fill(shape);

		// _0_0_18
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1131.12, 498.845);
		((GeneralPath) shape).curveTo(1125.04, 499.161, 1119.52, 501.964,
				1113.31, 502.246);
		((GeneralPath) shape).curveTo(1096.78, 502.997, 1081.64, 499.09,
				1068.35, 489.44);
		((GeneralPath) shape).curveTo(1057.78, 481.761, 1051.15, 471.05,
				1048.74, 458.091);
		((GeneralPath) shape).curveTo(1046.87, 448.054, 1048.35, 438.263,
				1052.01, 428.782);
		((GeneralPath) shape).curveTo(1060.16, 407.684, 1074.17, 392.608,
				1096.54, 386.745);
		((GeneralPath) shape).curveTo(1116.47, 381.521, 1138.1, 390.041,
				1149.86, 407.112);
		((GeneralPath) shape).curveTo(1151.92, 410.109, 1154.08, 413.045,
				1156.19, 416.009);
		((GeneralPath) shape).curveTo(1153.43, 440.785, 1146.6, 464.458,
				1136.78, 487.304);
		((GeneralPath) shape).curveTo(1135.09, 491.236, 1133.01, 495.002,
				1131.12, 498.845);

		g.fill(shape);

		// _0_0_19
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(673.315, 525.061);
		((GeneralPath) shape).curveTo(672.669, 540.388, 670.672, 555.174,
				667.098, 569.764);
		((GeneralPath) shape).curveTo(666.228, 573.315, 664.86, 574.838, 660.81,
				574.928);
		((GeneralPath) shape).curveTo(635.861, 575.482, 610.902, 576.063,
				585.984, 577.358);
		((GeneralPath) shape).curveTo(576.266, 577.863, 566.773, 577.127,
				557.175, 576.264);
		((GeneralPath) shape).curveTo(553.516, 575.936, 553.722, 574.411,
				554.305, 571.868);
		((GeneralPath) shape).curveTo(566.244, 519.739, 596.783, 483.719,
				646.956, 464.574);
		((GeneralPath) shape).curveTo(650.347, 463.28, 651.389, 463.951,
				652.421, 467.34);
		((GeneralPath) shape).curveTo(657.231, 483.133, 663.567, 498.332,
				671.668, 512.726);
		((GeneralPath) shape).curveTo(674.021, 516.907, 672.801, 521.167,
				673.315, 525.061);

		g.setPaint(new Color(0xD7AA61));
		g.fill(shape);

		// _0_0_20
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(761.117, 60.943);
		((GeneralPath) shape).curveTo(740.027, 67.818, 721.235, 78.911, 703.643,
				92.219);
		((GeneralPath) shape).curveTo(689.068, 103.244, 675.969, 115.774,
				664.643, 130.145);
		((GeneralPath) shape).curveTo(663.586, 131.487, 662.538, 132.76,
				662.452, 134.476);
		((GeneralPath) shape).curveTo(661.437, 154.764, 666.341, 173.138,
				680.328, 188.423);
		((GeneralPath) shape).curveTo(684.019, 192.456, 688.589, 195.477,
				693.451, 198.005);
		((GeneralPath) shape).curveTo(690.455, 204.504, 686.094, 210.127,
				681.975, 215.906);
		((GeneralPath) shape).curveTo(679.533, 217.455, 677.093, 216.264,
				674.74, 215.744);
		((GeneralPath) shape).curveTo(659.648, 212.413, 645.607, 206.412,
				632.257, 198.72);
		((GeneralPath) shape).curveTo(626.594, 195.457, 625.991, 195.533,
				623.638, 201.609);
		((GeneralPath) shape).curveTo(611.766, 232.27, 604.061, 263.698,
				604.416, 296.925);
		((GeneralPath) shape).curveTo(604.635, 317.415, 606.415, 337.454,
				613.98, 356.673);
		((GeneralPath) shape).curveTo(614.417, 357.784, 614.649, 358.904,
				614.243, 360.076);
		((GeneralPath) shape).curveTo(611.427, 363.05, 610.914, 359.682,
				610.282, 358.365);
		((GeneralPath) shape).curveTo(604.104, 345.497, 598.92, 332.233,
				595.207, 318.435);
		((GeneralPath) shape).curveTo(592.269, 307.52, 589.118, 296.629, 589.01,
				285.18);
		((GeneralPath) shape).curveTo(588.195, 283.183, 589.213, 280.979,
				588.314, 278.991);
		((GeneralPath) shape).curveTo(587.743, 271.297, 587.742, 263.602,
				588.315, 255.908);
		((GeneralPath) shape).curveTo(589.204, 253.914, 588.211, 251.713,
				588.998, 249.709);
		((GeneralPath) shape).curveTo(590.35, 227.911, 596.879, 207.497,
				605.623, 187.683);
		((GeneralPath) shape).curveTo(615.325, 165.7, 628.362, 145.765, 644.13,
				127.764);
		((GeneralPath) shape).curveTo(668.877, 99.513, 697.145, 75.941, 733.836,
				64.571);
		((GeneralPath) shape).curveTo(741.338, 62.246, 748.971, 60.157, 756.921,
				59.819);
		((GeneralPath) shape).curveTo(758.433, 59.772, 760.334, 58.271, 761.117,
				60.943);

		g.setPaint(new Color(0xF4EADB));
		g.fill(shape);

		// _0_0_21
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(614.243, 360.076);
		((GeneralPath) shape).curveTo(608.069, 348.406, 606.131, 335.581,
				604.187, 322.78);
		((GeneralPath) shape).curveTo(602.328, 310.547, 602.959, 298.206,
				603.144, 285.914);
		((GeneralPath) shape).curveTo(603.449, 265.524, 607.948, 245.798,
				613.524, 226.301);
		((GeneralPath) shape).curveTo(616.265, 216.715, 619.765, 207.388,
				623.519, 198.148);
		((GeneralPath) shape).curveTo(624.996, 194.51, 626.227, 193.293,
				630.257, 195.806);
		((GeneralPath) shape).curveTo(646.192, 205.74, 663.437, 212.547,
				681.975, 215.906);
		((GeneralPath) shape).curveTo(662.843, 248.209, 650.64, 283.102,
				643.412, 319.805);
		((GeneralPath) shape).curveTo(641.461, 329.71, 639.55, 339.661, 638.943,
				349.773);
		((GeneralPath) shape).curveTo(638.767, 352.689, 637.768, 353.864,
				634.718, 353.722);
		((GeneralPath) shape).curveTo(627.172, 353.372, 620.286, 355.44,
				614.243, 360.076);

		g.setPaint(new Color(0xECD8B9));
		g.fill(shape);

		// _0_0_22
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(137.061, 724.731);
		((GeneralPath) shape).curveTo(108.469, 744.029, 86.661, 769.548, 69.654,
				799.245);
		((GeneralPath) shape).curveTo(48.324, 836.492, 35.971, 876.479, 34.049,
				919.554);
		((GeneralPath) shape).curveTo(32.691, 950.0, 36.814, 979.303, 51.485,
				1006.46);
		((GeneralPath) shape).curveTo(59.29, 1020.9, 69.99, 1032.83, 83.875,
				1041.78);
		((GeneralPath) shape).curveTo(84.592, 1042.24, 86.463, 1042.25, 85.379,
				1044.04);
		((GeneralPath) shape).curveTo(47.568, 1030.21, 27.019, 1001.97, 19.37,
				963.626);
		((GeneralPath) shape).curveTo(10.927, 921.309, 17.989, 880.559, 34.181,
				841.175);
		((GeneralPath) shape).curveTo(53.701, 793.697, 84.217, 755.204, 128.407,
				728.22);
		((GeneralPath) shape).curveTo(130.949, 726.668, 133.62, 725.328,
				136.232, 723.89);
		((GeneralPath) shape).curveTo(136.487, 723.721, 136.754, 723.603,
				136.93, 723.709);
		((GeneralPath) shape).curveTo(137.372, 723.975, 137.222, 724.362,
				137.061, 724.731);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

		// _0_0_23
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(956.563, 865.977);
		((GeneralPath) shape).curveTo(948.801, 865.15, 941.039, 864.316,
				933.275, 863.502);
		((GeneralPath) shape).curveTo(930.392, 863.2, 929.909, 862.176, 931.257,
				859.41);
		((GeneralPath) shape).curveTo(941.438, 838.506, 941.083, 817.058,
				933.768, 795.493);
		((GeneralPath) shape).curveTo(932.461, 791.64, 931.878, 790.322, 936.93,
				790.262);
		((GeneralPath) shape).curveTo(958.736, 790.0, 980.432, 788.379, 1001.58,
				782.519);
		((GeneralPath) shape).curveTo(1003.56, 781.969, 1004.22, 782.853,
				1005.15, 784.095);
		((GeneralPath) shape).curveTo(1007.67, 787.432, 1010.44, 790.588,
				1013.87, 792.957);
		((GeneralPath) shape).curveTo(1016.45, 794.737, 1015.87, 795.87,
				1014.08, 797.837);
		((GeneralPath) shape).curveTo(996.917, 816.747, 983.468, 837.933,
				975.394, 862.293);
		((GeneralPath) shape).curveTo(974.663, 864.5, 973.528, 865.227, 971.538,
				865.239);
		((GeneralPath) shape).curveTo(966.556, 865.267, 961.573, 865.248,
				956.59, 865.248);
		((GeneralPath) shape).curveTo(956.581, 865.491, 956.572, 865.734,
				956.563, 865.977);

		g.fill(shape);

		// _0_0_24
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(877.275, 813.752);
		((GeneralPath) shape).curveTo(881.117, 814.009, 886.425, 814.468,
				891.742, 814.69);
		((GeneralPath) shape).curveTo(897.398, 814.926, 900.967, 812.244,
				901.526, 807.63);
		((GeneralPath) shape).curveTo(902.098, 802.91, 899.43, 799.685, 893.745,
				798.546);
		((GeneralPath) shape).curveTo(884.788, 796.752, 875.636, 795.828,
				866.951, 792.829);
		((GeneralPath) shape).curveTo(861.737, 791.028, 856.683, 788.857,
				853.573, 783.824);
		((GeneralPath) shape).curveTo(850.1, 778.203, 850.639, 776.035, 856.822,
				773.737);
		((GeneralPath) shape).curveTo(867.283, 769.849, 878.039, 769.472,
				888.688, 772.662);
		((GeneralPath) shape).curveTo(908.906, 778.719, 918.674, 793.697,
				921.978, 813.499);
		((GeneralPath) shape).curveTo(924.211, 826.882, 923.253, 840.093,
				916.856, 852.417);
		((GeneralPath) shape).curveTo(910.773, 864.135, 896.752, 871.44,
				885.427, 869.032);
		((GeneralPath) shape).curveTo(882.376, 868.383, 879.994, 867.418,
				878.862, 863.899);
		((GeneralPath) shape).curveTo(877.502, 859.669, 873.985, 857.177,
				869.656, 856.293);
		((GeneralPath) shape).curveTo(867.342, 855.821, 865.74, 854.839,
				864.541, 852.827);
		((GeneralPath) shape).curveTo(862.62, 849.605, 863.321, 847.503, 866.93,
				846.442);
		((GeneralPath) shape).curveTo(868.044, 846.115, 869.21, 845.961,
				870.311, 845.603);
		((GeneralPath) shape).curveTo(873.346, 844.618, 875.648, 842.853,
				876.196, 839.489);
		((GeneralPath) shape).curveTo(876.784, 835.878, 875.839, 832.729,
				872.545, 830.897);
		((GeneralPath) shape).curveTo(868.195, 828.478, 863.643, 826.425,
				859.234, 824.109);
		((GeneralPath) shape).curveTo(857.517, 823.208, 854.783, 822.527,
				854.975, 820.411);
		((GeneralPath) shape).curveTo(855.204, 817.883, 857.548, 816.216,
				859.998, 815.331);
		((GeneralPath) shape).curveTo(865.118, 813.481, 870.473, 813.705,
				877.275, 813.752);

		g.setPaint(new Color(0xFEF8EA));
		g.fill(shape);

		// _0_0_25
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(919.25, 771.874);
		((GeneralPath) shape).curveTo(915.664, 769.152, 912.847, 766.819,
				909.824, 764.793);
		((GeneralPath) shape).curveTo(908.094, 763.633, 908.122, 762.145,
				908.124, 760.466);
		((GeneralPath) shape).curveTo(908.136, 746.802, 908.252, 733.137,
				908.043, 719.476);
		((GeneralPath) shape).curveTo(907.993, 716.183, 909.366, 715.626,
				912.088, 715.506);
		((GeneralPath) shape).curveTo(920.601, 715.131, 928.943, 713.822,
				937.012, 710.89);
		((GeneralPath) shape).curveTo(956.627, 703.762, 968.472, 687.329,
				969.218, 666.561);
		((GeneralPath) shape).curveTo(969.654, 654.422, 970.631, 642.304,
				971.229, 630.169);
		((GeneralPath) shape).curveTo(971.364, 627.425, 972.209, 626.125,
				975.131, 625.556);
		((GeneralPath) shape).curveTo(983.448, 623.939, 991.716, 622.034,
				999.938, 619.983);
		((GeneralPath) shape).curveTo(1002.95, 619.232, 1003.12, 620.435,
				1003.16, 622.866);
		((GeneralPath) shape).curveTo(1003.55, 644.008, 1004.03, 665.149,
				1004.51, 686.289);
		((GeneralPath) shape).curveTo(1004.67, 693.444, 1004.99, 700.597,
				1005.05, 707.752);
		((GeneralPath) shape).curveTo(1005.07, 709.236, 1006.29, 712.03,
				1003.15, 711.778);
		((GeneralPath) shape).curveTo(1000.56, 711.571, 997.29, 713.88, 995.491,
				709.595);
		((GeneralPath) shape).curveTo(991.65, 700.443, 984.4, 695.54, 974.61,
				694.983);
		((GeneralPath) shape).curveTo(964.079, 694.384, 956.255, 699.285,
				951.54, 708.566);
		((GeneralPath) shape).curveTo(950.106, 711.389, 948.933, 712.246,
				945.75, 712.021);
		((GeneralPath) shape).curveTo(935.502, 711.297, 927.473, 715.546,
				922.598, 724.589);
		((GeneralPath) shape).curveTo(917.846, 733.404, 918.7, 742.202, 924.319,
				750.522);
		((GeneralPath) shape).curveTo(925.065, 751.628, 926.907, 752.486,
				925.356, 754.287);
		((GeneralPath) shape).curveTo(921.148, 759.174, 919.808, 765.134,
				919.25, 771.874);

		g.setPaint(new Color(0xFFA89A));
		g.fill(shape);

		// _0_0_26
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(589.24, 876.497);
		((GeneralPath) shape).curveTo(576.129, 876.359, 563.466, 874.124,
				551.367, 868.968);
		((GeneralPath) shape).curveTo(546.596, 866.935, 542.185, 864.346,
				538.302, 860.88);
		((GeneralPath) shape).curveTo(530.551, 853.963, 528.501, 844.985,
				533.004, 835.664);
		((GeneralPath) shape).curveTo(538.583, 824.113, 548.245, 818.048,
				560.692, 816.138);
		((GeneralPath) shape).curveTo(564.33, 815.581, 565.927, 817.369,
				565.654, 821.022);
		((GeneralPath) shape).curveTo(565.425, 824.102, 564.163, 826.766,
				562.692, 829.397);
		((GeneralPath) shape).curveTo(561.479, 831.568, 560.198, 833.736,
				559.308, 836.045);
		((GeneralPath) shape).curveTo(557.693, 840.24, 559.25, 844.557, 562.776,
				846.412);
		((GeneralPath) shape).curveTo(566.544, 848.394, 571.029, 847.2, 573.586,
				843.245);
		((GeneralPath) shape).curveTo(576.71, 838.413, 580.358, 834.212,
				585.657, 831.692);
		((GeneralPath) shape).curveTo(587.638, 830.75, 589.929, 829.671,
				591.779, 831.043);
		((GeneralPath) shape).curveTo(593.675, 832.45, 592.269, 834.789,
				591.828, 836.588);
		((GeneralPath) shape).curveTo(591.201, 839.148, 589.973, 841.554,
				589.202, 844.087);
		((GeneralPath) shape).curveTo(588.018, 847.977, 588.745, 851.455,
				592.381, 853.675);
		((GeneralPath) shape).curveTo(595.899, 855.824, 599.434, 855.516,
				602.364, 852.484);
		((GeneralPath) shape).curveTo(605.167, 849.584, 608.678, 848.312,
				612.45, 847.444);
		((GeneralPath) shape).curveTo(619.739, 845.765, 623.603, 847.169,
				625.911, 852.374);
		((GeneralPath) shape).curveTo(628.841, 858.98, 627.383, 865.342,
				621.864, 869.249);
		((GeneralPath) shape).curveTo(615.689, 873.621, 608.469, 874.924,
				601.179, 875.774);
		((GeneralPath) shape).curveTo(597.225, 876.234, 593.221, 876.27, 589.24,
				876.497);

		g.setPaint(new Color(0xFEF8EA));
		g.fill(shape);

		// _0_0_27
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(429.383, 827.97);
		((GeneralPath) shape).curveTo(432.972, 828.394, 436.558, 829.084,
				440.151, 829.113);
		((GeneralPath) shape).curveTo(441.602, 829.126, 443.498, 830.087,
				444.927, 827.263);
		((GeneralPath) shape).curveTo(445.963, 825.214, 450.106, 826.58, 452.56,
				827.193);
		((GeneralPath) shape).curveTo(457.7, 828.475, 457.811, 826.03, 456.991,
				822.341);
		((GeneralPath) shape).curveTo(455.592, 816.051, 454.171, 809.766,
				452.78, 803.474);
		((GeneralPath) shape).curveTo(452.458, 802.018, 451.158, 800.131,
				454.145, 799.926);
		((GeneralPath) shape).curveTo(454.514, 800.313, 454.874, 800.817,
				455.334, 800.018);
		((GeneralPath) shape).curveTo(469.207, 801.348, 482.854, 804.142,
				496.607, 806.217);
		((GeneralPath) shape).curveTo(507.372, 807.842, 518.331, 808.445,
				529.169, 809.839);
		((GeneralPath) shape).curveTo(530.313, 809.987, 531.519, 809.932,
				532.379, 810.919);
		((GeneralPath) shape).curveTo(521.323, 820.695, 513.976, 832.281,
				514.805, 847.695);
		((GeneralPath) shape).curveTo(514.955, 850.475, 513.278, 849.704,
				511.968, 849.586);
		((GeneralPath) shape).curveTo(496.916, 848.234, 481.868, 846.832,
				466.82, 845.441);
		((GeneralPath) shape).curveTo(458.055, 844.631, 449.298, 843.707,
				440.521, 843.074);
		((GeneralPath) shape).curveTo(438.036, 842.895, 436.754, 842.032,
				435.705, 839.781);
		((GeneralPath) shape).curveTo(433.822, 835.739, 431.514, 831.896,
				429.383, 827.97);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

		// _0_0_28
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(247.354, 841.967);
		((GeneralPath) shape).curveTo(244.449, 847.01, 244.762, 852.45, 244.393,
				857.667);
		((GeneralPath) shape).curveTo(243.878, 864.965, 243.63, 872.284,
				242.193, 879.477);
		((GeneralPath) shape).curveTo(241.741, 881.74, 241.096, 884.03, 240.12,
				886.112);
		((GeneralPath) shape).curveTo(237.657, 891.363, 234.318, 892.865,
				228.714, 891.133);
		((GeneralPath) shape).curveTo(217.694, 887.728, 209.273, 880.439,
				201.562, 872.268);
		((GeneralPath) shape).curveTo(198.261, 868.769, 195.177, 865.027,
				192.808, 860.776);
		((GeneralPath) shape).curveTo(189.851, 855.468, 190.61, 852.31, 195.827,
				849.082);
		((GeneralPath) shape).curveTo(201.752, 845.416, 208.435, 843.952,
				215.197, 842.978);
		((GeneralPath) shape).curveTo(223.289, 841.813, 231.445, 841.249,
				239.498, 843.226);
		((GeneralPath) shape).curveTo(242.347, 843.925, 244.655, 842.721,
				247.354, 841.967);

		g.setPaint(new Color(0xFF7164));
		g.fill(shape);

		// _0_0_29
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(279.731, 843.015);
		((GeneralPath) shape).curveTo(271.645, 841.068, 262.984, 843.22,
				254.749, 840.451);
		((GeneralPath) shape).curveTo(253.745, 840.113, 252.474, 840.539,
				251.331, 840.653);
		((GeneralPath) shape).curveTo(250.34, 840.752, 249.244, 841.071,
				249.286, 839.461);
		((GeneralPath) shape).curveTo(249.687, 824.143, 250.798, 808.967,
				258.997, 795.38);
		((GeneralPath) shape).curveTo(263.842, 787.352, 268.136, 786.387,
				274.938, 792.589);
		((GeneralPath) shape).curveTo(286.029, 802.702, 294.978, 814.385,
				298.534, 829.381);
		((GeneralPath) shape).curveTo(300.038, 835.723, 297.646, 839.753,
				291.302, 841.332);
		((GeneralPath) shape).curveTo(287.622, 842.248, 283.765, 842.454,
				279.731, 843.015);

		g.fill(shape);

		// _0_0_30
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1079.98, 675.085);
		((GeneralPath) shape).curveTo(1075.7, 676.337, 1071.4, 677.517, 1067.15,
				678.858);
		((GeneralPath) shape).curveTo(1061.02, 680.793, 1055.48, 683.802,
				1051.44, 688.925);
		((GeneralPath) shape).curveTo(1045.55, 696.374, 1046.49, 704.376,
				1050.03, 712.43);
		((GeneralPath) shape).curveTo(1054.17, 721.856, 1061.09, 729.23,
				1068.56, 736.576);
		((GeneralPath) shape).curveTo(1059.82, 738.272, 1051.68, 740.422,
				1044.48, 745.119);
		((GeneralPath) shape).curveTo(1032.44, 752.96, 1029.89, 761.185,
				1035.36, 774.346);
		((GeneralPath) shape).curveTo(1035.68, 775.096, 1035.81, 775.921,
				1036.03, 776.71);
		((GeneralPath) shape).curveTo(1035.4, 777.242, 1034.75, 777.766,
				1034.13, 778.309);
		((GeneralPath) shape).curveTo(1027.42, 784.157, 1027.1, 784.146,
				1020.73, 777.64);
		((GeneralPath) shape).curveTo(1020.03, 776.929, 1019.39, 776.159,
				1018.78, 775.378);
		((GeneralPath) shape).curveTo(1010.13, 764.424, 1011.46, 755.859,
				1023.09, 748.341);
		((GeneralPath) shape).curveTo(1029.15, 744.428, 1035.78, 741.853,
				1042.92, 740.672);
		((GeneralPath) shape).curveTo(1046.86, 740.019, 1050.34, 738.843,
				1051.59, 734.399);
		((GeneralPath) shape).curveTo(1052.85, 729.909, 1050.21, 727.255,
				1047.34, 724.562);
		((GeneralPath) shape).curveTo(1041.37, 718.947, 1036.18, 712.73, 1032.8,
				705.161);
		((GeneralPath) shape).curveTo(1028.29, 695.068, 1030.68, 687.404,
				1040.34, 682.081);
		((GeneralPath) shape).curveTo(1052.72, 675.256, 1066.19, 674.158,
				1079.98, 675.085);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

		// _0_0_31
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1199.55, 400.307);
		((GeneralPath) shape).curveTo(1199.28, 407.894, 1198.8, 414.838, 1196.1,
				421.429);
		((GeneralPath) shape).curveTo(1191.89, 431.705, 1182.21, 438.239,
				1171.24, 438.5);
		((GeneralPath) shape).curveTo(1168.55, 438.564, 1169.1, 437.325,
				1169.41, 435.841);
		((GeneralPath) shape).curveTo(1172.01, 423.115, 1173.68, 410.257,
				1174.55, 397.308);
		((GeneralPath) shape).curveTo(1175.08, 389.349, 1175.39, 381.369,
				1175.55, 373.393);
		((GeneralPath) shape).curveTo(1175.6, 370.705, 1176.69, 370.045, 1179.1,
				369.907);
		((GeneralPath) shape).curveTo(1186.88, 369.461, 1192.93, 373.144,
				1196.12, 380.404);
		((GeneralPath) shape).curveTo(1199.0, 386.957, 1199.25, 393.934,
				1199.55, 400.307);

		g.setPaint(new Color(0xFEF8EA));
		g.fill(shape);

		// _0_0_32
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(615.235, 401.314);
		((GeneralPath) shape).curveTo(615.695, 394.23, 616.026, 387.739,
				618.343, 381.571);
		((GeneralPath) shape).curveTo(621.217, 373.92, 626.946, 369.919,
				634.621, 369.902);
		((GeneralPath) shape).curveTo(636.997, 369.897, 638.204, 370.297,
				638.224, 373.133);
		((GeneralPath) shape).curveTo(638.37, 393.805, 640.124, 414.34, 644.167,
				434.636);
		((GeneralPath) shape).curveTo(644.833, 437.976, 643.766, 438.546,
				640.597, 438.126);
		((GeneralPath) shape).curveTo(627.681, 436.412, 618.934, 427.421,
				616.402, 412.633);
		((GeneralPath) shape).curveTo(615.73, 408.709, 615.571, 404.698,
				615.235, 401.314);

		g.fill(shape);

		// _0_0_33
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(984.3, 914.75);
		((GeneralPath) shape).curveTo(1003.59, 918.959, 1021.66, 938.652,
				1023.86, 957.55);
		((GeneralPath) shape).curveTo(1024.91, 966.514, 1020.78, 974.611,
				1013.61, 977.998);
		((GeneralPath) shape).curveTo(1011.73, 978.886, 1010.45, 979.179,
				1008.91, 977.297);
		((GeneralPath) shape).curveTo(994.047, 959.092, 986.394, 938.12, 984.3,
				914.75);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

		// _0_0_34
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(954.301, 646.739);
		((GeneralPath) shape).curveTo(953.364, 656.635, 954.251, 665.965,
				951.834, 674.907);
		((GeneralPath) shape).curveTo(948.98, 685.466, 941.523, 692.061, 931.46,
				695.829);
		((GeneralPath) shape).curveTo(924.858, 698.301, 917.945, 699.05,
				910.954, 699.488);
		((GeneralPath) shape).curveTo(908.817, 699.622, 908.048, 699.312,
				908.086, 697.018);
		((GeneralPath) shape).curveTo(908.208, 689.702, 908.171, 682.383,
				908.104, 675.066);
		((GeneralPath) shape).curveTo(908.086, 673.125, 908.461, 672.266,
				910.748, 671.968);
		((GeneralPath) shape).curveTo(933.652, 668.987, 938.015, 666.434,
				954.301, 646.739);

		g.fill(shape);

		// _0_0_35
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(417.342, 969.231);
		((GeneralPath) shape).curveTo(416.35, 970.746, 415.425, 972.311,
				414.352, 973.767);
		((GeneralPath) shape).curveTo(409.769, 979.982, 402.813, 979.204,
				399.312, 972.058);
		((GeneralPath) shape).curveTo(397.054, 967.447, 396.868, 962.474,
				397.03, 957.462);
		((GeneralPath) shape).curveTo(397.56, 941.024, 411.248, 925.053,
				427.146, 922.345);
		((GeneralPath) shape).curveTo(427.961, 922.206, 428.788, 922.091,
				429.613, 922.052);
		((GeneralPath) shape).curveTo(435.062, 921.794, 435.605, 922.392,
				434.449, 927.8);
		((GeneralPath) shape).curveTo(433.865, 930.529, 433.041, 933.207,
				432.326, 935.908);
		((GeneralPath) shape).curveTo(427.815, 947.233, 422.817, 958.339,
				417.342, 969.231);

		g.fill(shape);

		// _0_0_36
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(954.96, 753.463);
		((GeneralPath) shape).curveTo(954.907, 743.116, 962.786, 734.923,
				972.961, 734.745);
		((GeneralPath) shape).curveTo(983.11, 734.568, 992.007, 743.26, 991.947,
				753.294);
		((GeneralPath) shape).curveTo(991.887, 763.196, 983.444, 771.627,
				973.38, 771.835);
		((GeneralPath) shape).curveTo(963.442, 772.04, 955.012, 763.633, 954.96,
				753.463);

		g.fill(shape);

		// _0_0_37
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1020.44, 678.043);
		((GeneralPath) shape).curveTo(1019.93, 656.929, 1019.44, 637.011,
				1018.99, 617.093);
		((GeneralPath) shape).curveTo(1018.96, 615.862, 1018.53, 614.352,
				1020.44, 613.862);
		((GeneralPath) shape).curveTo(1022.24, 613.402, 1022.71, 614.667,
				1023.39, 615.831);
		((GeneralPath) shape).curveTo(1031.61, 629.909, 1038.84, 644.487,
				1045.6, 659.312);
		((GeneralPath) shape).curveTo(1046.66, 661.647, 1046.14, 662.378,
				1043.76, 663.096);
		((GeneralPath) shape).curveTo(1039.47, 664.395, 1035.4, 666.29, 1031.51,
				668.593);
		((GeneralPath) shape).curveTo(1027.62, 670.9, 1024.2, 673.748, 1020.44,
				678.043);

		g.fill(shape);

		// _0_0_38
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(265.38, 638.963);
		((GeneralPath) shape).curveTo(265.38, 653.615, 265.421, 668.267,
				265.319, 682.918);
		((GeneralPath) shape).curveTo(265.308, 684.472, 266.722, 687.44,
				262.954, 686.923);
		((GeneralPath) shape).curveTo(260.396, 686.572, 257.264, 687.571,
				257.291, 682.828);
		((GeneralPath) shape).curveTo(257.457, 653.193, 257.377, 623.556,
				257.37, 593.92);
		((GeneralPath) shape).curveTo(257.37, 592.042, 257.142, 589.84, 260.062,
				590.581);
		((GeneralPath) shape).curveTo(262.132, 591.106, 265.558, 589.112,
				265.473, 594.011);
		((GeneralPath) shape).curveTo(265.215, 608.991, 265.379, 623.979,
				265.38, 638.963);

		g.setPaint(new Color(0xFDFDFD));
		g.fill(shape);

		// _0_0_39
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(943.522, 629.866);
		((GeneralPath) shape).curveTo(939.429, 644.82, 926.083, 655.55, 911.606,
				655.97);
		((GeneralPath) shape).curveTo(909.236, 656.039, 907.944, 655.968,
				908.059, 652.981);
		((GeneralPath) shape).curveTo(908.295, 646.838, 908.16, 640.679,
				908.112, 634.527);
		((GeneralPath) shape).curveTo(908.097, 632.706, 908.126, 631.391,
				910.643, 631.386);
		((GeneralPath) shape).curveTo(921.582, 631.363, 932.509, 630.824,
				943.522, 629.866);

		g.setPaint(new Color(0xFEF8EA));
		g.fill(shape);

		// _0_0_40
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(325.17, 1044.71);
		((GeneralPath) shape).curveTo(319.516, 1060.29, 308.128, 1065.68,
				293.575, 1060.38);
		((GeneralPath) shape).curveTo(291.534, 1059.64, 289.555, 1058.71,
				288.015, 1057.14);
		((GeneralPath) shape).curveTo(286.782, 1055.88, 284.736, 1054.72,
				285.455, 1052.7);
		((GeneralPath) shape).curveTo(286.019, 1051.12, 288.085, 1051.85,
				289.476, 1051.65);
		((GeneralPath) shape).curveTo(301.256, 1049.91, 312.968, 1047.8, 325.17,
				1044.71);

		g.setPaint(new Color(0xFF7063));
		g.fill(shape);

		// _0_0_41
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(973.706, 718.752);
		((GeneralPath) shape).curveTo(972.383, 718.816, 970.39, 718.978,
				968.397, 718.982);
		((GeneralPath) shape).curveTo(967.374, 718.984, 966.113, 720.324,
				965.341, 718.714);
		((GeneralPath) shape).curveTo(964.783, 717.55, 965.495, 716.452,
				966.024, 715.419);
		((GeneralPath) shape).curveTo(968.803, 710.001, 975.941, 709.312,
				979.728, 714.099);
		((GeneralPath) shape).curveTo(980.788, 715.439, 981.829, 717.029,
				980.999, 718.644);
		((GeneralPath) shape).curveTo(980.228, 720.146, 978.604, 718.782,
				977.365, 718.912);
		((GeneralPath) shape).curveTo(976.385, 719.014, 975.375, 718.839,
				973.706, 718.752);

		g.setPaint(new Color(0xFDCA00));
		g.fill(shape);

		// _0_0_42
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(935.599, 736.209);
		((GeneralPath) shape).curveTo(935.566, 731.172, 940.716, 726.976,
				945.465, 727.994);
		((GeneralPath) shape).curveTo(947.636, 728.459, 948.34, 729.248,
				946.664, 731.227);
		((GeneralPath) shape).curveTo(944.625, 733.634, 943.058, 736.357,
				941.693, 739.223);
		((GeneralPath) shape).curveTo(941.12, 740.426, 941.328, 742.716,
				939.397, 742.528);
		((GeneralPath) shape).curveTo(937.697, 742.363, 936.828, 740.565,
				936.245, 738.979);
		((GeneralPath) shape).curveTo(935.905, 738.054, 935.787, 737.048,
				935.599, 736.209);

		g.setPaint(new Color(0xFCCA00));
		g.fill(shape);

		// _0_0_43
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1002.53, 727.87);
		((GeneralPath) shape).curveTo(1006.34, 728.091, 1008.85, 730.05,
				1010.46, 733.515);
		((GeneralPath) shape).curveTo(1011.88, 736.577, 1009.86, 737.919,
				1008.03, 739.671);
		((GeneralPath) shape).curveTo(1006.07, 741.55, 1005.49, 740.481,
				1004.59, 738.724);
		((GeneralPath) shape).curveTo(1003.39, 736.394, 1001.9, 734.217,
				1000.54, 731.969);
		((GeneralPath) shape).curveTo(999.975, 731.035, 998.381, 730.311,
				998.969, 729.202);
		((GeneralPath) shape).curveTo(999.572, 728.067, 1001.13, 727.833,
				1002.53, 727.87);

		g.fill(shape);

		// _0_0_44
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(417.342, 969.231);
		((GeneralPath) shape).curveTo(417.79, 963.216, 421.503, 958.443,
				423.782, 953.175);
		((GeneralPath) shape).curveTo(426.318, 947.312, 428.031, 940.935,
				432.326, 935.908);
		((GeneralPath) shape).curveTo(429.637, 948.053, 424.696, 959.184,
				417.342, 969.231);

		g.setPaint(new Color(0xFBF4E7));
		g.fill(shape);

		// _0_0_45
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(945.331, 774.53);
		((GeneralPath) shape).curveTo(941.613, 773.003, 937.79, 776.336,
				936.116, 772.051);
		((GeneralPath) shape).curveTo(934.664, 768.333, 936.537, 765.441,
				939.712, 762.999);
		((GeneralPath) shape).curveTo(941.482, 766.632, 943.124, 770.002,
				945.331, 774.53);

		g.setPaint(new Color(0xFDCA00));
		g.fill(shape);

		// _0_0_46
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(761.117, 60.943);
		((GeneralPath) shape).curveTo(760.049, 59.335, 758.286, 60.317, 756.92,
				59.819);
		((GeneralPath) shape).curveTo(759.872, 58.614, 762.926, 58.799, 765.992,
				59.151);
		((GeneralPath) shape).curveTo(764.582, 60.333, 762.831, 60.588, 761.117,
				60.943);

		g.setPaint(new Color(0xECD8B9));
		g.fill(shape);

		// _0_0_47
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(137.061, 724.731);
		((GeneralPath) shape).curveTo(136.784, 724.451, 136.508, 724.17,
				136.232, 723.89);
		((GeneralPath) shape).curveTo(137.998, 722.411, 139.951, 721.304,
				142.24, 720.865);
		((GeneralPath) shape).curveTo(141.073, 722.903, 139.013, 723.744,
				137.061, 724.731);

		g.setPaint(new Color(0xD4C2A4));
		g.fill(shape);

		// _0_0_48
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(588.314, 278.991);
		((GeneralPath) shape).curveTo(589.704, 280.924, 589.097, 283.082,
				589.01, 285.18);
		((GeneralPath) shape).curveTo(588.095, 283.194, 588.111, 281.103,
				588.314, 278.991);

		g.setPaint(new Color(0xECD8B9));
		g.fill(shape);

		// _0_0_49
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(588.998, 249.709);
		((GeneralPath) shape).curveTo(589.077, 251.809, 589.71, 253.971,
				588.315, 255.908);
		((GeneralPath) shape).curveTo(588.152, 253.798, 587.982, 251.688,
				588.998, 249.709);

		g.fill(shape);

		// _0_0_50
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(905.607, 562.141);
		((GeneralPath) shape).curveTo(891.397, 562.412, 880.275, 555.072,
				871.827, 543.123);
		((GeneralPath) shape).curveTo(863.008, 530.651, 858.617, 516.413,
				856.107, 501.523);
		((GeneralPath) shape).curveTo(855.758, 499.456, 855.55, 497.787,
				852.993, 497.167);
		((GeneralPath) shape).curveTo(849.51, 496.321, 848.217, 493.622,
				848.904, 490.265);
		((GeneralPath) shape).curveTo(849.676, 486.489, 852.387, 485.336,
				856.022, 485.739);
		((GeneralPath) shape).curveTo(870.257, 487.319, 884.526, 488.092,
				898.86, 487.962);
		((GeneralPath) shape).curveTo(915.864, 487.807, 932.73, 486.341,
				949.486, 483.493);
		((GeneralPath) shape).curveTo(954.454, 482.649, 957.739, 484.817,
				957.565, 489.561);
		((GeneralPath) shape).curveTo(956.849, 509.096, 953.444, 527.978,
				941.627, 544.239);
		((GeneralPath) shape).curveTo(933.061, 556.025, 921.332, 562.112,
				905.607, 562.141);

		g.setPaint(new Color(0x6E4100));
		g.fill(shape);

		// _0_0_51
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(769.916, 381.952);
		((GeneralPath) shape).curveTo(770.06, 371.72, 772.308, 364.016, 777.593,
				357.414);
		((GeneralPath) shape).curveTo(778.933, 355.74, 779.01, 354.491, 778.257,
				352.548);
		((GeneralPath) shape).curveTo(776.513, 348.048, 775.627, 343.289,
				775.028, 338.505);
		((GeneralPath) shape).curveTo(774.57, 334.844, 776.033, 332.233,
				779.862, 331.659);
		((GeneralPath) shape).curveTo(783.608, 331.097, 785.839, 333.063,
				786.758, 336.61);
		((GeneralPath) shape).curveTo(787.132, 338.056, 787.482, 339.517,
				787.693, 340.993);
		((GeneralPath) shape).curveTo(788.462, 346.366, 789.585, 351.245,
				796.172, 352.59);
		((GeneralPath) shape).curveTo(798.52, 353.069, 800.434, 355.202,
				801.921, 357.221);
		((GeneralPath) shape).curveTo(810.342, 368.651, 812.512, 381.197,
				807.044, 394.346);
		((GeneralPath) shape).curveTo(801.969, 406.549, 787.151, 409.116,
				777.733, 399.907);
		((GeneralPath) shape).curveTo(772.19, 394.487, 769.721, 387.824,
				769.916, 381.952);

		g.fill(shape);

		// _0_0_52
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1044.09, 380.083);
		((GeneralPath) shape).curveTo(1043.55, 386.257, 1042.95, 391.339,
				1040.33, 395.968);
		((GeneralPath) shape).curveTo(1034.44, 406.405, 1021.07, 408.56,
				1012.23, 400.454);
		((GeneralPath) shape).curveTo(1003.01, 391.993, 1000.53, 376.662,
				1008.0, 363.663);
		((GeneralPath) shape).curveTo(1011.81, 357.05, 1012.81, 351.412,
				1010.19, 344.61);
		((GeneralPath) shape).curveTo(1009.43, 342.63, 1009.21, 340.4, 1009.0,
				338.259);
		((GeneralPath) shape).curveTo(1008.66, 334.832, 1010.03, 332.34,
				1013.61, 331.711);
		((GeneralPath) shape).curveTo(1017.32, 331.06, 1019.75, 332.777,
				1020.67, 336.369);
		((GeneralPath) shape).curveTo(1021.59, 339.904, 1022.26, 343.5, 1023.12,
				347.049);
		((GeneralPath) shape).curveTo(1023.5, 348.632, 1023.81, 350.334,
				1025.79, 350.879);
		((GeneralPath) shape).curveTo(1035.69, 353.605, 1039.93, 361.24,
				1042.34, 370.294);
		((GeneralPath) shape).curveTo(1043.27, 373.803, 1043.63, 377.463,
				1044.09, 380.083);

		g.fill(shape);

		// _0_0_53
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(806.649, 211.871);
		((GeneralPath) shape).curveTo(806.664, 214.337, 805.841, 216.001,
				804.021, 216.762);
		((GeneralPath) shape).curveTo(801.924, 217.639, 799.893, 217.405,
				798.068, 215.687);
		((GeneralPath) shape).curveTo(788.552, 206.73, 783.708, 207.223,
				775.489, 218.018);
		((GeneralPath) shape).curveTo(773.787, 220.254, 771.608, 220.61,
				769.315, 219.774);
		((GeneralPath) shape).curveTo(766.816, 218.863, 765.945, 216.693,
				766.429, 214.11);
		((GeneralPath) shape).curveTo(767.726, 207.181, 776.911, 200.637,
				786.648, 199.647);
		((GeneralPath) shape).curveTo(794.495, 198.849, 803.525, 203.597,
				806.121, 209.918);
		((GeneralPath) shape).curveTo(806.431, 210.672, 806.553, 211.503,
				806.649, 211.871);

		g.fill(shape);

		// _0_0_54
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(904.022, 384.815);
		((GeneralPath) shape).curveTo(906.574, 384.831, 908.306, 385.375,
				909.44, 386.995);
		((GeneralPath) shape).curveTo(911.149, 389.435, 912.878, 391.879,
				914.353, 394.461);
		((GeneralPath) shape).curveTo(916.05, 397.432, 915.651, 400.374,
				912.852, 402.414);
		((GeneralPath) shape).curveTo(909.861, 404.594, 906.806, 404.067,
				904.504, 401.201);
		((GeneralPath) shape).curveTo(902.642, 398.883, 900.871, 396.414,
				899.528, 393.774);
		((GeneralPath) shape).curveTo(897.344, 389.481, 900.108, 384.883,
				904.022, 384.815);

		g.setPaint(new Color(0x6F4300));
		g.fill(shape);

		// _0_0_55
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(952.845, 122.124);
		((GeneralPath) shape).curveTo(956.76, 122.397, 959.162, 124.29, 961.091,
				127.353);
		((GeneralPath) shape).curveTo(979.578, 156.706, 1002.15, 182.687,
				1027.35, 206.387);
		((GeneralPath) shape).curveTo(1057.71, 234.943, 1090.86, 259.633,
				1128.67, 277.635);
		((GeneralPath) shape).curveTo(1142.06, 284.012, 1155.91, 289.198,
				1170.31, 292.821);
		((GeneralPath) shape).curveTo(1174.53, 293.882, 1177.03, 296.432,
				1177.06, 300.9);
		((GeneralPath) shape).curveTo(1177.09, 304.986, 1174.63, 307.244,
				1171.01, 308.517);
		((GeneralPath) shape).curveTo(1169.15, 309.171, 1167.41, 308.538,
				1165.64, 308.085);
		((GeneralPath) shape).curveTo(1137.78, 300.986, 1112.09, 288.868,
				1087.89, 273.647);
		((GeneralPath) shape).curveTo(1031.56, 238.218, 984.895, 192.795,
				948.207, 137.207);
		((GeneralPath) shape).curveTo(945.722, 133.441, 943.698, 129.658,
				947.131, 125.358);
		((GeneralPath) shape).curveTo(948.676, 123.423, 950.777, 122.551,
				952.845, 122.124);

		g.setPaint(new Color(0x6E4100));
		g.fill(shape);

		// _0_0_56
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(707.131, 747.13);
		((GeneralPath) shape).curveTo(707.608, 753.227, 705.098, 758.389,
				703.778, 763.809);
		((GeneralPath) shape).curveTo(703.189, 766.226, 700.747, 766.791,
				699.518, 768.955);
		((GeneralPath) shape).curveTo(695.989, 775.168, 698.138, 778.956,
				702.256, 783.441);
		((GeneralPath) shape).curveTo(710.909, 792.866, 709.664, 807.643,
				699.144, 816.81);
		((GeneralPath) shape).curveTo(693.003, 822.161, 686.836, 826.985,
				679.045, 829.354);
		((GeneralPath) shape).curveTo(674.806, 830.643, 677.8, 833.643, 677.177,
				835.762);
		((GeneralPath) shape).curveTo(676.712, 837.342, 678.378, 840.246,
				677.411, 840.332);
		((GeneralPath) shape).curveTo(674.59, 840.586, 673.103, 843.269,
				670.005, 843.155);
		((GeneralPath) shape).curveTo(663.184, 842.904, 656.334, 843.539,
				649.493, 843.67);
		((GeneralPath) shape).curveTo(647.775, 843.703, 645.766, 844.018,
				644.657, 841.994);
		((GeneralPath) shape).curveTo(643.461, 839.812, 645.13, 838.619,
				646.272, 837.254);
		((GeneralPath) shape).curveTo(646.891, 836.515, 647.502, 835.307,
				648.237, 835.208);
		((GeneralPath) shape).curveTo(651.63, 834.749, 651.591, 831.368,
				653.377, 829.588);
		((GeneralPath) shape).curveTo(651.405, 827.658, 649.341, 825.926,
				646.551, 825.21);
		((GeneralPath) shape).curveTo(642.232, 824.103, 637.563, 821.397,
				636.126, 818.191);
		((GeneralPath) shape).curveTo(632.419, 809.917, 628.357, 801.417,
				632.682, 791.417);
		((GeneralPath) shape).curveTo(634.433, 787.368, 635.761, 783.313,
				638.566, 779.814);
		((GeneralPath) shape).curveTo(639.939, 778.102, 640.387, 776.152,
				639.364, 773.806);
		((GeneralPath) shape).curveTo(635.861, 765.773, 634.25, 757.084,
				630.569, 749.053);
		((GeneralPath) shape).curveTo(627.019, 741.312, 632.146, 729.63,
				639.868, 726.196);
		((GeneralPath) shape).curveTo(643.542, 724.562, 648.403, 726.517,
				651.713, 730.741);
		((GeneralPath) shape).curveTo(654.987, 734.919, 656.608, 739.817,
				657.185, 744.876);
		((GeneralPath) shape).curveTo(657.933, 751.437, 660.779, 757.65,
				660.289, 764.372);
		((GeneralPath) shape).curveTo(660.111, 766.82, 661.701, 766.558, 663.17,
				766.386);
		((GeneralPath) shape).curveTo(666.954, 765.942, 670.708, 765.411,
				674.416, 766.926);
		((GeneralPath) shape).curveTo(676.63, 767.831, 677.016, 766.268,
				677.052, 764.62);
		((GeneralPath) shape).curveTo(677.25, 755.603, 679.466, 746.956,
				682.221, 738.479);
		((GeneralPath) shape).curveTo(683.899, 733.315, 687.389, 729.233,
				692.14, 726.4);
		((GeneralPath) shape).curveTo(694.94, 724.731, 696.592, 727.773,
				698.944, 728.218);
		((GeneralPath) shape).curveTo(703.581, 729.095, 706.403, 731.896,
				706.31, 737.213);
		((GeneralPath) shape).curveTo(706.252, 740.603, 708.19, 743.908,
				707.131, 747.13);

		g.setPaint(new Color(0xFFCF71));
		g.fill(shape);

		// _0_0_57
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(836.148, 706.963);
		((GeneralPath) shape).curveTo(836.301, 704.176, 835.714, 700.909,
				840.101, 700.87);
		((GeneralPath) shape).curveTo(840.121, 703.769, 840.256, 706.742,
				836.148, 706.963);

		g.setPaint(new Color(0xFFDED9));
		g.fill(shape);

		// _0_0_58
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(765.13, 832.865);
		((GeneralPath) shape).curveTo(766.804, 836.295, 769.715, 837.292,
				773.278, 837.01);
		((GeneralPath) shape).curveTo(767.564, 838.728, 765.047, 837.447,
				765.13, 832.865);

		g.setPaint(new Color(0xFFD5D0));
		g.fill(shape);

		// _0_0_59
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(752.075, 729.808);
		((GeneralPath) shape).curveTo(751.358, 731.792, 753.97, 734.933,
				749.996, 735.784);
		((GeneralPath) shape).curveTo(750.299, 733.656, 750.823, 731.605,
				752.075, 729.808);

		g.fill(shape);

		// _0_0_60
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(827.291, 822.993);
		((GeneralPath) shape).curveTo(827.297, 820.584, 827.304, 818.175,
				827.312, 815.765);
		((GeneralPath) shape).curveTo(828.58, 818.178, 828.594, 820.588,
				827.291, 822.993);

		g.setPaint(new Color(0xFFDAD4));
		g.fill(shape);

		// _0_0_61
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(840.015, 699.912);
		((GeneralPath) shape).curveTo(839.288, 699.73, 838.225, 699.632,
				838.671, 698.578);
		((GeneralPath) shape).curveTo(839.101, 697.564, 840.287, 698.037,
				841.155, 697.914);
		((GeneralPath) shape).curveTo(841.112, 698.772, 840.892, 699.53,
				840.015, 699.912);

		g.setPaint(new Color(0xFFDED9));
		g.fill(shape);

		// _0_0_62
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1093.69, 940.933);
		((GeneralPath) shape).curveTo(1074.06, 941.771, 1061.86, 924.873,
				1061.24, 903.729);
		((GeneralPath) shape).curveTo(1060.77, 887.863, 1077.77, 871.869,
				1094.25, 871.845);
		((GeneralPath) shape).curveTo(1113.85, 871.818, 1131.64, 887.533,
				1130.96, 905.078);
		((GeneralPath) shape).curveTo(1130.25, 923.34, 1112.77, 941.937,
				1093.69, 940.933);

		g.setPaint(new Color(0xFFD9B4));
		g.fill(shape);

		// _0_0_63
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1069.13, 845.418);
		((GeneralPath) shape).curveTo(1068.58, 851.79, 1067.04, 858.533,
				1059.88, 862.176);
		((GeneralPath) shape).curveTo(1054.88, 864.717, 1049.82, 864.677,
				1045.42, 860.897);
		((GeneralPath) shape).curveTo(1039.76, 856.02, 1037.78, 849.611,
				1039.53, 842.439);
		((GeneralPath) shape).curveTo(1040.91, 836.769, 1044.5, 832.306,
				1049.37, 829.082);
		((GeneralPath) shape).curveTo(1056.05, 824.663, 1062.75, 826.461,
				1066.55, 833.543);
		((GeneralPath) shape).curveTo(1068.4, 836.989, 1069.1, 840.712, 1069.13,
				845.418);

		g.setPaint(new Color(0x6E4100));
		g.fill(shape);

		// _0_0_64
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(1110.26, 779.079);
		((GeneralPath) shape).curveTo(1110.39, 782.359, 1109.12, 784.716,
				1105.63, 784.969);
		((GeneralPath) shape).curveTo(1102.46, 785.198, 1100.62, 783.354,
				1100.32, 780.249);
		((GeneralPath) shape).curveTo(1099.9, 775.973, 1098.71, 772.663,
				1093.82, 771.477);
		((GeneralPath) shape).curveTo(1091.02, 770.799, 1090.3, 768.021, 1091.1,
				765.33);
		((GeneralPath) shape).curveTo(1092.03, 762.167, 1094.56, 761.544,
				1097.47, 762.047);
		((GeneralPath) shape).curveTo(1104.44, 763.253, 1110.23, 771.005,
				1110.26, 779.079);

		g.setPaint(new Color(0x6F4300));
		g.fill(shape);

		// _0_0_65
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(346.032, 667.764);
		((GeneralPath) shape).curveTo(344.316, 668.134, 343.515, 667.881,
				344.17, 665.783);
		((GeneralPath) shape).curveTo(344.719, 664.02, 346.84, 664.055, 347.515,
				662.369);
		((GeneralPath) shape).curveTo(336.182, 664.387, 338.48, 653.221,
				333.806, 648.807);
		((GeneralPath) shape).curveTo(335.039, 647.23, 336.307, 649.957,
				337.519, 648.181);
		((GeneralPath) shape).curveTo(334.009, 647.422, 333.639, 645.614,
				334.421, 642.141);
		((GeneralPath) shape).curveTo(335.222, 638.58, 333.719, 634.599,
				335.254, 630.912);
		((GeneralPath) shape).curveTo(335.509, 629.025, 336.629, 627.811,
				338.288, 627.018);
		((GeneralPath) shape).curveTo(340.061, 627.721, 340.686, 626.193,
				341.259, 625.21);
		((GeneralPath) shape).curveTo(345.44, 618.035, 352.179, 616.479,
				359.683, 616.816);
		((GeneralPath) shape).curveTo(363.915, 617.007, 368.487, 616.061,
				372.162, 619.016);
		((GeneralPath) shape).curveTo(377.649, 623.428, 384.188, 626.593,
				388.508, 632.392);
		((GeneralPath) shape).curveTo(391.823, 636.841, 390.559, 645.994,
				386.21, 648.994);
		((GeneralPath) shape).curveTo(385.026, 649.81, 385.063, 650.95, 384.688,
				651.96);
		((GeneralPath) shape).curveTo(380.504, 663.206, 374.443, 667.398,
				362.72, 667.046);
		((GeneralPath) shape).curveTo(360.14, 671.002, 356.547, 664.636,
				353.267, 668.08);
		((GeneralPath) shape).curveTo(352.066, 669.341, 353.786, 666.262,
				352.739, 665.569);
		((GeneralPath) shape).curveTo(350.527, 664.898, 351.69, 670.883,
				348.272, 667.834);
		((GeneralPath) shape).curveTo(347.546, 667.163, 346.816, 666.607,
				346.032, 667.764);

		g.setPaint(new Color(0xFFD482));
		g.fill(shape);

		// _0_0_66
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(555.094, 760.943);
		((GeneralPath) shape).curveTo(553.914, 759.726, 554.262, 758.218,
				554.301, 756.768);
		((GeneralPath) shape).curveTo(554.979, 756.783, 555.837, 754.479,
				556.088, 756.419);
		((GeneralPath) shape).curveTo(556.338, 758.352, 558.022, 758.843,
				558.557, 760.222);
		((GeneralPath) shape).curveTo(557.626, 761.883, 555.996, 759.49,
				555.094, 760.943);

		g.setPaint(new Color(0xDEEFCB));
		g.fill(shape);

		// _0_0_67
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(555.197, 740.8);
		((GeneralPath) shape).curveTo(555.103, 741.819, 554.88, 742.533,
				553.702, 742.129);
		((GeneralPath) shape).curveTo(552.566, 741.74, 551.412, 741.202,
				551.716, 739.824);
		((GeneralPath) shape).curveTo(551.843, 739.248, 552.867, 738.492,
				553.495, 738.482);
		((GeneralPath) shape).curveTo(554.982, 738.456, 555.059, 739.783,
				555.197, 740.8);

		g.setPaint(new Color(0xCFE8B2));
		g.fill(shape);

		// _0_0_68
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(562.082, 784.447);
		((GeneralPath) shape).curveTo(560.898, 782.348, 560.787, 779.864,
				559.139, 777.981);
		((GeneralPath) shape).curveTo(561.918, 779.711, 561.918, 779.711,
				562.082, 784.447);

		g.setPaint(new Color(0xD1E9B7));
		g.fill(shape);

		// _0_0_69
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(338.288, 627.018);
		((GeneralPath) shape).curveTo(337.402, 628.414, 337.207, 630.348,
				335.253, 630.912);
		((GeneralPath) shape).curveTo(333.871, 627.79, 334.327, 627.205,
				338.288, 627.018);

		g.setPaint(new Color(0xFFEBCA));
		g.fill(shape);

		// _0_0_70
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(562.198, 768.909);
		((GeneralPath) shape).curveTo(561.805, 767.639, 559.278, 768.304,
				559.776, 766.434);
		((GeneralPath) shape).curveTo(559.988, 765.638, 561.222, 765.723,
				561.954, 766.015);
		((GeneralPath) shape).curveTo(563.856, 766.775, 563.299, 767.828,
				562.144, 768.862);
		((GeneralPath) shape).lineTo(562.198, 768.909);

		g.setPaint(new Color(0xD8EDC2));
		g.fill(shape);

		// _0_0_71
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(557.794, 755.204);
		((GeneralPath) shape).curveTo(558.772, 755.971, 559.75, 756.737,
				560.728, 757.503);
		((GeneralPath) shape).curveTo(558.534, 758.425, 557.975, 757.037,
				557.794, 755.204);

		g.setPaint(new Color(0xDEEFCB));
		g.fill(shape);

		// _0_0_72
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(346.032, 667.764);
		((GeneralPath) shape).curveTo(345.564, 666.556, 345.461, 665.202,
				347.071, 665.162);
		((GeneralPath) shape).curveTo(348.906, 665.116, 347.828, 666.937,
				348.272, 667.834);
		((GeneralPath) shape).curveTo(347.526, 667.81, 346.779, 667.787,
				346.032, 667.764);

		g.setPaint(new Color(0xFFECCB));
		g.fill(shape);

		// _0_0_73
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(562.144, 768.862);
		((GeneralPath) shape).curveTo(562.421, 769.916, 562.698, 770.97,
				562.976, 772.024);
		((GeneralPath) shape).curveTo(561.272, 771.257, 559.157, 770.551,
				562.198, 768.909);
		((GeneralPath) shape).lineTo(562.144, 768.862);

		g.setPaint(new Color(0xD8EDC2));
		g.fill(shape);

		// _0_0_74
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(558.988, 744.08);
		((GeneralPath) shape).curveTo(558.87, 745.665, 558.516, 746.212,
				557.778, 746.099);
		((GeneralPath) shape).curveTo(557.519, 746.06, 557.234, 745.408, 557.18,
				745.006);
		((GeneralPath) shape).curveTo(557.105, 744.447, 556.999, 743.604,
				557.834, 743.595);
		((GeneralPath) shape).curveTo(558.398, 743.589, 558.968, 744.07,
				558.988, 744.08);

		g.setPaint(new Color(0xD0E8B4));
		g.fill(shape);

		// _0_0_75
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(326.699, 940.934);
		((GeneralPath) shape).curveTo(308.952, 942.712, 289.848, 921.016,
				289.786, 904.755);
		((GeneralPath) shape).curveTo(289.717, 886.501, 309.427, 871.633,
				326.35, 871.812);
		((GeneralPath) shape).curveTo(342.865, 871.986, 359.483, 887.757,
				359.127, 903.815);
		((GeneralPath) shape).curveTo(358.662, 924.772, 346.027, 942.038,
				326.699, 940.934);

		g.setPaint(new Color(0xFFD9B4));
		g.fill(shape);

		// _0_0_76
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(351.304, 845.399);
		((GeneralPath) shape).curveTo(351.38, 843.961, 351.376, 843.627,
				351.418, 843.298);
		((GeneralPath) shape).curveTo(352.239, 836.878, 353.516, 830.542,
				360.176, 827.564);
		((GeneralPath) shape).curveTo(365.411, 825.224, 369.929, 827.547,
				373.776, 831.114);
		((GeneralPath) shape).curveTo(379.894, 836.788, 383.22, 843.529, 380.85,
				852.083);
		((GeneralPath) shape).curveTo(378.95, 858.939, 373.569, 863.652,
				367.593, 863.866);
		((GeneralPath) shape).curveTo(360.971, 864.103, 355.233, 859.798,
				352.911, 852.581);
		((GeneralPath) shape).curveTo(352.054, 849.918, 351.674, 847.102,
				351.304, 845.399);

		g.setPaint(new Color(0x6E4100));
		g.fill(shape);

		// _0_0_77
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(310.224, 778.536);
		((GeneralPath) shape).curveTo(310.668, 770.884, 315.674, 764.081,
				322.206, 762.296);
		((GeneralPath) shape).curveTo(325.451, 761.409, 328.322, 761.767,
				329.521, 765.405);
		((GeneralPath) shape).curveTo(330.626, 768.758, 328.935, 770.926,
				325.789, 771.821);
		((GeneralPath) shape).curveTo(321.658, 772.996, 320.592, 776.22,
				320.266, 779.804);
		((GeneralPath) shape).curveTo(319.95, 783.261, 318.279, 785.187,
				314.887, 784.925);
		((GeneralPath) shape).curveTo(311.12, 784.634, 310.053, 781.926,
				310.224, 778.536);

		g.setPaint(new Color(0x6F4300));
		g.fill(shape);

		// _0_0_78
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(533.025, 700.988);
		((GeneralPath) shape).curveTo(534.64, 702.373, 536.351, 703.666,
				537.829, 705.185);
		((GeneralPath) shape).curveTo(538.491, 705.866, 538.708, 707.124,
				537.554, 707.716);
		((GeneralPath) shape).curveTo(536.663, 708.173, 536.089, 707.367,
				535.599, 706.669);
		((GeneralPath) shape).curveTo(534.473, 705.068, 533.32, 703.486,
				532.179, 701.897);
		((GeneralPath) shape).curveTo(531.675, 700.862, 532.193, 700.779,
				533.025, 700.988);

		g.setPaint(new Color(0xF9FCF5));
		g.fill(shape);

		// _0_0_79
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(544.192, 723.885);
		((GeneralPath) shape).curveTo(541.573, 721.954, 543.069, 718.053,
				540.254, 716.216);
		((GeneralPath) shape).curveTo(544.091, 717.476, 544.091, 717.476,
				544.156, 723.915);
		((GeneralPath) shape).curveTo(544.155, 723.915, 544.192, 723.885,
				544.192, 723.885);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

		// _0_0_80
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(533.025, 700.988);
		((GeneralPath) shape).curveTo(532.743, 701.291, 532.461, 701.594,
				532.179, 701.897);
		((GeneralPath) shape).curveTo(530.162, 700.163, 529.943, 697.214,
				526.505, 695.033);
		((GeneralPath) shape).curveTo(531.126, 695.891, 532.477, 698.061,
				533.025, 700.988);

		g.setPaint(new Color(0xE7F4DA));
		g.fill(shape);

		// _0_0_81
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(400.316, 714.379);
		((GeneralPath) shape).curveTo(398.962, 713.99, 397.055, 714.585, 396.58,
				712.562);
		((GeneralPath) shape).curveTo(396.515, 712.285, 397.085, 711.562,
				397.384, 711.547);
		((GeneralPath) shape).curveTo(399.115, 711.465, 399.613, 712.985,
				400.316, 714.379);

		g.setPaint(new Color(0xDAEEC5));
		g.fill(shape);

		// _0_0_82
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(493.827, 669.273);
		((GeneralPath) shape).curveTo(492.011, 671.507, 490.459, 671.202,
				488.669, 670.327);
		((GeneralPath) shape).curveTo(489.961, 668.199, 491.647, 668.808,
				493.827, 669.273);

		g.setPaint(new Color(0xEDF6E3));
		g.fill(shape);

		// _0_0_83
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(381.722, 710.335);
		((GeneralPath) shape).curveTo(382.332, 708.749, 383.171, 707.68,
				385.028, 708.526);
		((GeneralPath) shape).curveTo(385.268, 708.635, 385.58, 709.18, 385.506,
				709.291);
		((GeneralPath) shape).curveTo(384.586, 710.688, 383.376, 711.214,
				381.722, 710.335);

		g.setPaint(new Color(0xE6F3D9));
		g.fill(shape);

		// _0_0_84
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(506.103, 674.593);
		((GeneralPath) shape).curveTo(507.111, 674.556, 507.418, 675.138,
				507.579, 675.778);
		((GeneralPath) shape).curveTo(507.797, 676.642, 507.264, 676.978,
				506.518, 676.885);
		((GeneralPath) shape).curveTo(505.816, 676.797, 505.272, 676.314,
				505.369, 675.575);
		((GeneralPath) shape).curveTo(505.419, 675.188, 505.895, 674.856,
				506.103, 674.593);

		g.setPaint(new Color(0xE2F1D2));
		g.fill(shape);

		// _0_0_85
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(455.334, 800.018);
		((GeneralPath) shape).curveTo(455.34, 800.858, 456.783, 802.126,
				455.454, 802.401);
		((GeneralPath) shape).curveTo(453.849, 802.734, 454.374, 800.906,
				454.146, 799.926);
		((GeneralPath) shape).curveTo(454.542, 799.957, 454.938, 799.988,
				455.334, 800.018);

		g.setPaint(new Color(0xD3EAB9));
		g.fill(shape);

		// _0_0_86
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(893.875, 499.911);
		((GeneralPath) shape).curveTo(909.098, 500.199, 924.863, 499.312,
				940.513, 496.946);
		((GeneralPath) shape).curveTo(944.746, 496.307, 945.395, 497.462,
				944.723, 501.506);
		((GeneralPath) shape).curveTo(942.829, 512.888, 940.279, 523.996,
				934.084, 533.918);
		((GeneralPath) shape).curveTo(920.563, 555.571, 894.454, 555.866,
				880.54, 534.517);
		((GeneralPath) shape).curveTo(874.093, 524.624, 870.797, 513.513,
				868.256, 502.114);
		((GeneralPath) shape).curveTo(867.593, 499.142, 868.982, 498.812,
				871.352, 499.086);
		((GeneralPath) shape).curveTo(878.635, 499.927, 885.952, 499.974,
				893.875, 499.911);

		g.setPaint(new Color(0xFEFEFE));
		g.fill(shape);

	}
}

/**
 * This class is based on ouput from
 * <a href="http://ebourg.github.io/flamingo-svg-transcoder/">Flamingo SVG
 * transcoder</a>.
 * <p>
 * The source image is available as public domain from
 * <a href= "https://openclipart.org/detail/285292/hard-drive">openclipart</a>.
 */
class HardDrive implements javax.swing.Icon {

	/** The width of this icon. */
	private int width;

	/** The height of this icon. */
	private int height;

	/**
	 * Creates a new transcoded SVG image.
	 */
	public HardDrive(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		double coef = Math.min((double) width / (double) 361,
				(double) height / (double) 518);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.scale(coef, coef);
		paint(g2d);
		g2d.dispose();
	}

	/**
	 * Paints the transcoded SVG image on the specified graphics context.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	private static void paint(Graphics2D g) {
		Shape shape = null;

		float origAlpha = 1.0f;

		java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();

		//

		// _0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1, 0, 0, 1, 254.29533f, -116.64788f));

		// _0_0

		// _0_0_0

		// _0_0_0_0
		shape = new RoundRectangle2D.Double(-254.2953338623047,
				116.64788055419922, 360, 517.3228149414062, 20, 20);
		g.setPaint(new Color(0x414D5B));
		g.fill(shape);
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-0.81944454f, 0, 0, 0.81944454f,
				-3774.5166f, 5183.338f));

		// _0_0_0_1
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.882118f, 0, 0, 2.882118f, 12565.448f,
				7645.2124f));

		// _0_0_0_1_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-0.34696707f, 0, 0, 0.34696707f,
				-7485.203f, -2258.3506f));

		// _0_0_0_1_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-4302.875, -6966.4375);
		((GeneralPath) shape).curveTo(-4311.656, -6966.4375, -4318.7812,
				-6959.312, -4318.7812, -6950.5312);
		((GeneralPath) shape).curveTo(-4318.7812, -6941.7505, -4311.656,
				-6934.625, -4302.875, -6934.625);
		((GeneralPath) shape).lineTo(-4272.5938, -6934.625);
		((GeneralPath) shape).lineTo(-4272.5938, -6966.4375);
		((GeneralPath) shape).lineTo(-4302.875, -6966.4375);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_1_0_0

		g.setTransform(transformations.pop()); // _0_0_0_1_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(3.9132938f, 0, 0, 3.9132938f, 18734.2f,
				12451.798f));

		// _0_0_0_1_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5987.671, -4667.6646);
		((GeneralPath) shape).curveTo(-5987.671, -4666.4, -5988.696, -4665.375,
				-5989.9604, -4665.375);
		((GeneralPath) shape).curveTo(-5991.225, -4665.375, -5992.25, -4666.4,
				-5992.25, -4667.6646);
		((GeneralPath) shape).curveTo(-5992.25, -4668.9287, -5991.225,
				-4669.954, -5989.9604, -4669.954);
		((GeneralPath) shape).curveTo(-5988.6963, -4669.954, -5987.671,
				-4668.929, -5987.671, -4667.6646);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x353C46));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_1_1

		g.setTransform(transformations.pop()); // _0_0_0_1

		// _0_0_0_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-242.3222, 183.49792);
		((GeneralPath) shape).lineTo(-242.3222, 563.22156);
		((GeneralPath) shape).lineTo(-210.96791, 594.62476);
		((GeneralPath) shape).lineTo(-210.96791, 618.1373);
		((GeneralPath) shape).lineTo(38.76738, 618.1373);
		((GeneralPath) shape).lineTo(91.55058, 565.273);
		((GeneralPath) shape).lineTo(91.55058, 467.0433);
		((GeneralPath) shape).lineTo(56.56708, 434.77222);
		((GeneralPath) shape).lineTo(56.56708, 404.55292);
		((GeneralPath) shape).lineTo(93.47359, 300.42792);
		((GeneralPath) shape).lineTo(93.47359, 184.90482);
		((GeneralPath) shape).lineTo(38.626797, 130.05322);
		((GeneralPath) shape).lineTo(-188.8738, 130.05322);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x333C45));
		g.fill(shape);
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.3871126f, 0, 0, 2.3881643f,
				13985.201f, 11299.506f));

		// _0_0_0_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5822.5, -4606.25);
		((GeneralPath) shape).curveTo(-5822.5, -4569.109, -5852.609, -4539.0,
				-5889.75, -4539.0);
		((GeneralPath) shape).curveTo(-5926.891, -4539.0, -5957.0, -4569.109,
				-5957.0, -4606.25);
		((GeneralPath) shape).curveTo(-5957.0, -4643.391, -5926.891, -4673.5,
				-5889.75, -4673.5);
		((GeneralPath) shape).curveTo(-5852.609, -4673.5, -5822.5, -4643.391,
				-5822.5, -4606.25);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);
		g.setPaint(new Color(0x333C45));
		g.setStroke(new BasicStroke(6.282358f, 0, 0, 4));
		g.draw(shape);

		g.setTransform(transformations.pop()); // _0_0_0_3
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1, 0, 0, 1, 3785.2202f, 7619.2373f));

		// _0_0_0_4

		// _0_0_0_4_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-4012.6746, -7192.246);
		((GeneralPath) shape).lineTo(-4012.6746, -7068.8237);
		((GeneralPath) shape).lineTo(-3959.9463, -7016.018);
		((GeneralPath) shape).lineTo(-3848.631, -7016.018);
		((GeneralPath) shape).lineTo(-3848.631, -7059.226);
		((GeneralPath) shape).lineTo(-3868.6213, -7075.909);
		((GeneralPath) shape).lineTo(-3887.9548, -7106.981);
		((GeneralPath) shape).lineTo(-3914.6648, -7167.8267);
		((GeneralPath) shape).lineTo(-3962.4265, -7167.8267);
		((GeneralPath) shape).lineTo(-3981.1765, -7192.2456);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.520988f, 0, 0, 2.5248652f, 10926.072f,
				4204.308f));

		// _0_0_0_4_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x76A8D4));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_4_1
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.520988f, 0, 0, 2.5248652f, 11053.93f,
				4343.918f));

		// _0_0_0_4_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_4_2
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.520988f, 0, 0, 2.5248652f, 10943.854f,
				4330.07f));

		// _0_0_0_4_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_4_3

		g.setTransform(transformations.pop()); // _0_0_0_4
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1.2369502f, 0, 0, 1.2369502f, 4700.738f,
				9354.381f));

		// _0_0_0_5
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0.6196284f, 0, 0, 0.61990124f,
				-210.87166f, -4465.2925f));

		// _0_0_0_5_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5822.5, -4606.25);
		((GeneralPath) shape).curveTo(-5822.5, -4569.109, -5852.609, -4539.0,
				-5889.75, -4539.0);
		((GeneralPath) shape).curveTo(-5926.891, -4539.0, -5957.0, -4569.109,
				-5957.0, -4606.25);
		((GeneralPath) shape).curveTo(-5957.0, -4643.391, -5926.891, -4673.5,
				-5889.75, -4673.5);
		((GeneralPath) shape).curveTo(-5852.609, -4673.5, -5822.5, -4643.391,
				-5822.5, -4606.25);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);
		g.setPaint(new Color(0x333C45));
		g.setStroke(new BasicStroke(13.044305f, 0, 0, 4));
		g.draw(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.183598f, 0, 0, 2.1845593f, 9061.428f,
				2502.7007f));

		// _0_0_0_5_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xCCCCCC));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_1
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.183598f, 0, 0, 2.1845593f, 9060.131f,
				2555.8718f));

		// _0_0_0_5_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xD0D0D0));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_2
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0, 2.1845593f, -2.183598f, 0,
				-13679.419f, 5606.7314f));

		// _0_0_0_5_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_3
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0, -2.1845593f, 2.183598f, 0, 5958.762f,
				-20248.158f));

		// _0_0_0_5_4
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_4
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.183598f, 0, 0, 2.1845593f, 9061.428f,
				2502.7007f));

		// _0_0_0_5_5
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_5
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1.5447167f, 1.5447167f, -1.544037f,
				1.544037f, -1662.2863f, 8763.769f));

		// _0_0_0_5_6
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_6
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1.5447167f, -1.5447167f, 1.544037f,
				1.544037f, 12224.509f, -9518.398f));

		// _0_0_0_5_7
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_7
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-1.5447167f, -1.5447167f, 1.544037f,
				-1.544037f, -6057.658f, -23405.193f));

		// _0_0_0_5_8
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_8
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-1.5447167f, 1.5447167f, -1.544037f,
				-1.544037f, -19944.453f, -5123.027f));

		// _0_0_0_5_9
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_5_9

		g.setTransform(transformations.pop()); // _0_0_0_5
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0, 0.81944454f, 0.81944454f, 0,
				4853.611f, 3993.4287f));

		// _0_0_0_6
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.037965f, -2.037965f, 2.037965f,
				2.037965f, 17022.52f, -8513.172f));

		// _0_0_0_6_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-0.34696707f, 0, 0, 0.34696707f,
				-7483.7515f, -2122.9128f));

		// _0_0_0_6_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-4298.6562, -7356.7812);
		((GeneralPath) shape).curveTo(-4307.437, -7356.7812, -4314.5625,
				-7349.656, -4314.5625, -7340.875);
		((GeneralPath) shape).curveTo(-4314.5625, -7332.1045, -4307.454,
				-7324.9854, -4298.6875, -7324.9688);
		((GeneralPath) shape).curveTo(-4292.5234, -7324.9688, -4286.3535,
				-7324.9688, -4280.1875, -7324.9688);
		((GeneralPath) shape).lineTo(-4271.375, -7333.7812);
		((GeneralPath) shape).curveTo(-4269.4165, -7335.7397, -4268.4062,
				-7338.319, -4268.4062, -7340.875);
		((GeneralPath) shape).curveTo(-4268.4062, -7343.431, -4269.4165,
				-7345.979, -4271.375, -7347.9375);
		((GeneralPath) shape).lineTo(-4280.1875, -7356.75);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_6_0_0

		g.setTransform(transformations.pop()); // _0_0_0_6_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.7671165f, -2.7671165f, 2.7671165f,
				2.7671165f, 24783.256f, -9476.369f));

		// _0_0_0_6_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5987.671, -4667.6646);
		((GeneralPath) shape).curveTo(-5987.671, -4666.4, -5988.696, -4665.375,
				-5989.9604, -4665.375);
		((GeneralPath) shape).curveTo(-5991.225, -4665.375, -5992.25, -4666.4,
				-5992.25, -4667.6646);
		((GeneralPath) shape).curveTo(-5992.25, -4668.9287, -5991.225,
				-4669.954, -5989.9604, -4669.954);
		((GeneralPath) shape).curveTo(-5988.6963, -4669.954, -5987.671,
				-4668.929, -5987.671, -4667.6646);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x353C46));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_6_1

		g.setTransform(transformations.pop()); // _0_0_0_6
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0, 0.81944454f, -0.81944454f, 0,
				-5002.2007f, 3993.4292f));

		// _0_0_0_7
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.037965f, -2.037965f, 2.037965f,
				2.037965f, 17022.52f, -8513.172f));

		// _0_0_0_7_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-0.34696707f, 0, 0, 0.34696707f,
				-7483.7515f, -2122.9128f));

		// _0_0_0_7_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-4298.6562, -7356.7812);
		((GeneralPath) shape).curveTo(-4307.437, -7356.7812, -4314.5625,
				-7349.656, -4314.5625, -7340.875);
		((GeneralPath) shape).curveTo(-4314.5625, -7332.1045, -4307.454,
				-7324.9854, -4298.6875, -7324.9688);
		((GeneralPath) shape).curveTo(-4292.5234, -7324.9688, -4286.3535,
				-7324.9688, -4280.1875, -7324.9688);
		((GeneralPath) shape).lineTo(-4271.375, -7333.7812);
		((GeneralPath) shape).curveTo(-4269.4165, -7335.7397, -4268.4062,
				-7338.319, -4268.4062, -7340.875);
		((GeneralPath) shape).curveTo(-4268.4062, -7343.431, -4269.4165,
				-7345.979, -4271.375, -7347.9375);
		((GeneralPath) shape).lineTo(-4280.1875, -7356.75);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_7_0_0

		g.setTransform(transformations.pop()); // _0_0_0_7_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.7671165f, -2.7671165f, 2.7671165f,
				2.7671165f, 24783.256f, -9476.369f));

		// _0_0_0_7_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5987.671, -4667.6646);
		((GeneralPath) shape).curveTo(-5987.671, -4666.4, -5988.696, -4665.375,
				-5989.9604, -4665.375);
		((GeneralPath) shape).curveTo(-5991.225, -4665.375, -5992.25, -4666.4,
				-5992.25, -4667.6646);
		((GeneralPath) shape).curveTo(-5992.25, -4668.9287, -5991.225,
				-4669.954, -5989.9604, -4669.954);
		((GeneralPath) shape).curveTo(-5988.6963, -4669.954, -5987.671,
				-4668.929, -5987.671, -4667.6646);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x353C46));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_7_1

		g.setTransform(transformations.pop()); // _0_0_0_7
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0, -0.81944454f, 0.81944454f, 0,
				4853.6113f, -3242.8098f));

		// _0_0_0_8
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.037965f, -2.037965f, 2.037965f,
				2.037965f, 17022.52f, -8513.172f));

		// _0_0_0_8_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-0.34696707f, 0, 0, 0.34696707f,
				-7483.7515f, -2122.9128f));

		// _0_0_0_8_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-4298.6562, -7356.7812);
		((GeneralPath) shape).curveTo(-4307.437, -7356.7812, -4314.5625,
				-7349.656, -4314.5625, -7340.875);
		((GeneralPath) shape).curveTo(-4314.5625, -7332.1045, -4307.454,
				-7324.9854, -4298.6875, -7324.9688);
		((GeneralPath) shape).curveTo(-4292.5234, -7324.9688, -4286.3535,
				-7324.9688, -4280.1875, -7324.9688);
		((GeneralPath) shape).lineTo(-4271.375, -7333.7812);
		((GeneralPath) shape).curveTo(-4269.4165, -7335.7397, -4268.4062,
				-7338.319, -4268.4062, -7340.875);
		((GeneralPath) shape).curveTo(-4268.4062, -7343.431, -4269.4165,
				-7345.979, -4271.375, -7347.9375);
		((GeneralPath) shape).lineTo(-4280.1875, -7356.75);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_8_0_0

		g.setTransform(transformations.pop()); // _0_0_0_8_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.7671165f, -2.7671165f, 2.7671165f,
				2.7671165f, 24783.256f, -9476.369f));

		// _0_0_0_8_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5987.671, -4667.6646);
		((GeneralPath) shape).curveTo(-5987.671, -4666.4, -5988.696, -4665.375,
				-5989.9604, -4665.375);
		((GeneralPath) shape).curveTo(-5991.225, -4665.375, -5992.25, -4666.4,
				-5992.25, -4667.6646);
		((GeneralPath) shape).curveTo(-5992.25, -4668.9287, -5991.225,
				-4669.954, -5989.9604, -4669.954);
		((GeneralPath) shape).curveTo(-5988.6963, -4669.954, -5987.671,
				-4668.929, -5987.671, -4667.6646);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x353C46));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_8_1

		g.setTransform(transformations.pop()); // _0_0_0_8
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0, -0.81944454f, -0.81944454f, 0,
				-5002.2007f, -3242.8098f));

		// _0_0_0_9
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.037965f, -2.037965f, 2.037965f,
				2.037965f, 17022.52f, -8513.172f));

		// _0_0_0_9_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(-0.34696707f, 0, 0, 0.34696707f,
				-7483.7515f, -2122.9128f));

		// _0_0_0_9_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-4298.6562, -7356.7812);
		((GeneralPath) shape).curveTo(-4307.437, -7356.7812, -4314.5625,
				-7349.656, -4314.5625, -7340.875);
		((GeneralPath) shape).curveTo(-4314.5625, -7332.1045, -4307.454,
				-7324.9854, -4298.6875, -7324.9688);
		((GeneralPath) shape).curveTo(-4292.5234, -7324.9688, -4286.3535,
				-7324.9688, -4280.1875, -7324.9688);
		((GeneralPath) shape).lineTo(-4271.375, -7333.7812);
		((GeneralPath) shape).curveTo(-4269.4165, -7335.7397, -4268.4062,
				-7338.319, -4268.4062, -7340.875);
		((GeneralPath) shape).curveTo(-4268.4062, -7343.431, -4269.4165,
				-7345.979, -4271.375, -7347.9375);
		((GeneralPath) shape).lineTo(-4280.1875, -7356.75);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_9_0_0

		g.setTransform(transformations.pop()); // _0_0_0_9_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.7671165f, -2.7671165f, 2.7671165f,
				2.7671165f, 24783.256f, -9476.369f));

		// _0_0_0_9_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5987.671, -4667.6646);
		((GeneralPath) shape).curveTo(-5987.671, -4666.4, -5988.696, -4665.375,
				-5989.9604, -4665.375);
		((GeneralPath) shape).curveTo(-5991.225, -4665.375, -5992.25, -4666.4,
				-5992.25, -4667.6646);
		((GeneralPath) shape).curveTo(-5992.25, -4668.9287, -5991.225,
				-4669.954, -5989.9604, -4669.954);
		((GeneralPath) shape).curveTo(-5988.6963, -4669.954, -5987.671,
				-4668.929, -5987.671, -4667.6646);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x353C46));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_9_1

		g.setTransform(transformations.pop()); // _0_0_0_9
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1, 0, 0, 1, 3786.0327f, 7619.7373f));

		// _0_0_0_10
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1, 0, 0, 1, 37.884876f, 14));

		// _0_0_0_10_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(4.958678f, 0, 0, 4.958678f, 25618.229f,
				15204.905f));

		// _0_0_0_10_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5952.4375, -4506.6875);
		((GeneralPath) shape).curveTo(-5952.4375, -4502.5107, -5955.823,
				-4499.125, -5960.0, -4499.125);
		((GeneralPath) shape).curveTo(-5964.177, -4499.125, -5967.5625,
				-4502.5107, -5967.5625, -4506.6875);
		((GeneralPath) shape).curveTo(-5967.5625, -4510.8643, -5964.177,
				-4514.25, -5960.0, -4514.25);
		((GeneralPath) shape).curveTo(-5955.823, -4514.25, -5952.4375,
				-4510.8643, -5952.4375, -4506.6875);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x333C45));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_10_0_0
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(3.4173353f, 0, 0, 3.4223576f,
				16431.826f, 8281.19f));

		// _0_0_0_10_0_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5952.4375, -4506.6875);
		((GeneralPath) shape).curveTo(-5952.4375, -4502.5107, -5955.823,
				-4499.125, -5960.0, -4499.125);
		((GeneralPath) shape).curveTo(-5964.177, -4499.125, -5967.5625,
				-4502.5107, -5967.5625, -4506.6875);
		((GeneralPath) shape).curveTo(-5967.5625, -4510.8643, -5964.177,
				-4514.25, -5960.0, -4514.25);
		((GeneralPath) shape).curveTo(-5955.823, -4514.25, -5952.4375,
				-4510.8643, -5952.4375, -4506.6875);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xF9794C));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_10_0_1

		// _0_0_0_10_0_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-3816.4849, -7259.719);
		((GeneralPath) shape).lineTo(-3917.7249, -7123.5103);
		((GeneralPath) shape).lineTo(-3954.2595, -7160.0986);
		((GeneralPath) shape).closePath();

		g.fill(shape);
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.4151313f, 0, 0, 2.418681f, 10458.691f,
				3757.933f));

		// _0_0_0_10_0_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5952.4375, -4506.6875);
		((GeneralPath) shape).curveTo(-5952.4375, -4502.5107, -5955.823,
				-4499.125, -5960.0, -4499.125);
		((GeneralPath) shape).curveTo(-5964.177, -4499.125, -5967.5625,
				-4502.5107, -5967.5625, -4506.6875);
		((GeneralPath) shape).curveTo(-5967.5625, -4510.8643, -5964.177,
				-4514.25, -5960.0, -4514.25);
		((GeneralPath) shape).curveTo(-5955.823, -4514.25, -5952.4375,
				-4510.8643, -5952.4375, -4506.6875);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);
		g.setPaint(new Color(0x333C45));
		g.setStroke(new BasicStroke(1.6550089f, 0, 0, 4));
		g.draw(shape);

		g.setTransform(transformations.pop()); // _0_0_0_10_0_3

		g.setTransform(transformations.pop()); // _0_0_0_10_0

		g.setTransform(transformations.pop()); // _0_0_0_10

		// _0_0_0_11
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(1, 0, 0, 1, 3788.0327f, 7619.2954f));

		// _0_0_0_11_0

		// _0_0_0_11_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-3813.5542, -7092.3955);
		((GeneralPath) shape).lineTo(-3813.5542, -7073.3457);
		((GeneralPath) shape).lineTo(-3833.0994, -7073.3457);
		((GeneralPath) shape).lineTo(-3833.0994, -7016.018);
		((GeneralPath) shape).lineTo(-3756.1401, -7016.018);
		((GeneralPath) shape).lineTo(-3730.225, -7041.9727);
		((GeneralPath) shape).lineTo(-3730.225, -7074.9185);
		((GeneralPath) shape).lineTo(-3718.053, -7087.1094);
		((GeneralPath) shape).lineTo(-3734.4133, -7103.495);
		((GeneralPath) shape).lineTo(-3746.0183, -7092.3955);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xEAEAEA));
		g.fill(shape);

		// _0_0_0_11_0_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-3790.693, -7087.6777);
		((GeneralPath) shape).lineTo(-3790.693, -7057.266);
		((GeneralPath) shape).lineTo(-3826.9915, -7057.266);
		((GeneralPath) shape).lineTo(-3826.9915, -7021.7856);
		((GeneralPath) shape).lineTo(-3759.1064, -7021.7856);
		((GeneralPath) shape).lineTo(-3736.2456, -7044.682);
		((GeneralPath) shape).lineTo(-3736.2456, -7057.6157);
		((GeneralPath) shape).lineTo(-3754.0457, -7057.6157);
		((GeneralPath) shape).lineTo(-3754.0457, -7087.6777);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xF9CD54));
		g.fill(shape);

		// _0_0_0_11_0_2
		shape = new Rectangle2D.Double(-3782.426025390625, -7082.90576171875,
				20.113880157470703, 20.51557731628418);
		g.setPaint(new Color(0x3E3E40));
		g.fill(shape);
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.520988f, 0, 0, 2.5248652f, 11094.983f,
				4319.039f));

		// _0_0_0_11_0_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x76A8D4));
		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_11_0_3
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(2.520988f, 0, 0, 2.5248652f, 11185.259f,
				4295.86f));

		// _0_0_0_11_0_4
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-5915.4785, -4508.892);
		((GeneralPath) shape).curveTo(-5915.4785, -4507.6963, -5916.448,
				-4506.7266, -5917.644, -4506.7266);
		((GeneralPath) shape).curveTo(-5918.84, -4506.7266, -5919.8096,
				-4507.6963, -5919.8096, -4508.892);
		((GeneralPath) shape).curveTo(-5919.8096, -4510.088, -5918.84,
				-4511.0576, -5917.644, -4511.0576);
		((GeneralPath) shape).curveTo(-5916.448, -4511.0576, -5915.4785,
				-4510.088, -5915.4785, -4508.892);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0_0_11_0_4

		g.setTransform(transformations.pop()); // _0_0_0_11_0

		// _0_0_0_11_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-19.714312, 561.76416);
		((GeneralPath) shape).curveTo(-16.466112, 540.92474, -10.437311,
				451.25195, -83.118904, 499.72375);

		g.setPaint(new Color(0xF9CD54));
		g.setStroke(new BasicStroke(4.841931f, 1, 1, 4));
		g.draw(shape);

		// _0_0_0_11_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-81.500114, 501.57825);
		((GeneralPath) shape).lineTo(-95.982315, 512.0646);
		((GeneralPath) shape).lineTo(-84.866905, 498.00217);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		g.setTransform(transformations.pop()); // _0_0

	}
}

/**
 * This class is based on ouput from
 * <a href="http://ebourg.github.io/flamingo-svg-transcoder/">Flamingo SVG
 * transcoder</a>.
 * <p>
 * The source image is available as public domain from <a href=
 * "https://openclipart.org/detail/27680/pendrive-icon">openclipart</a>.
 */
class FlashDrive implements javax.swing.Icon {

	/** The width of this icon. */
	private int width;

	/** The height of this icon. */
	private int height;

	/**
	 * Creates a new transcoded SVG image.
	 */
	public FlashDrive(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		double coef = Math.min((double) width / (double) 1,
				(double) height / (double) 1);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.scale(coef, coef);
		paint(g2d);
		g2d.dispose();
	}

	/**
	 * Paints the transcoded SVG image on the specified graphics context.
	 * 
	 * @param g
	 *            Graphics context.
	 */
	private static void paint(Graphics2D g) {
		Shape shape = null;

		float origAlpha = 1.0f;

		java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();

		//
		transformations.push(g.getTransform());
		g.transform(
				new AffineTransform(0.020833334f, 0, 0, 0.020833334f, 0, 0));

		// _0

		// _0_0

		// _0_0_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(33.301, 7.6358);
		((GeneralPath) shape).curveTo(31.036999, 7.6548, 28.924, 8.1083, 27.345,
				9.0201);
		((GeneralPath) shape).lineTo(12.058999, 17.848999);
		((GeneralPath) shape).curveTo(11.705999, 18.053, 11.528999, 18.3,
				11.523999, 18.561998);
		((GeneralPath) shape).lineTo(11.523999, 26.287998);
		((GeneralPath) shape).curveTo(11.5199995, 26.630999, 11.808999,
				26.998999, 12.381, 27.328999);
		((GeneralPath) shape).lineTo(25.729, 35.036);
		((GeneralPath) shape).curveTo(26.74, 35.62, 27.959, 35.694, 28.771,
				35.225);
		((GeneralPath) shape).lineTo(44.063, 26.395998);
		((GeneralPath) shape).curveTo(45.678, 25.463999, 46.464, 24.208998,
				46.463, 22.869999);
		((GeneralPath) shape).lineTo(46.463, 15.094999);
		((GeneralPath) shape).curveTo(46.436, 13.343999, 45.071003, 11.452,
				42.463, 9.9464);
		((GeneralPath) shape).curveTo(39.785, 8.400499, 36.402, 7.6096997,
				33.301003, 7.6357994);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x999999));
		g.fill(shape);
		g.setComposite(AlphaComposite.getInstance(3, 0.3f * origAlpha));

		// _0_0_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(24.0, 26.316);
		((GeneralPath) shape).lineTo(24.0, 34.038002);
		((GeneralPath) shape).curveTo(24.39, 34.263, 25.729, 35.036003, 25.729,
				35.036003);
		((GeneralPath) shape).curveTo(26.74, 35.620003, 27.959, 35.694004,
				28.771, 35.225002);
		((GeneralPath) shape).curveTo(28.771, 35.225002, 30.267, 34.362003,
				30.687, 34.120003);
		((GeneralPath) shape).lineTo(30.687, 26.398003);
		((GeneralPath) shape).curveTo(30.267, 26.640003, 28.771, 27.503002,
				28.771, 27.503002);
		((GeneralPath) shape).curveTo(27.959, 27.972002, 26.74, 27.898003,
				25.729, 27.314003);
		((GeneralPath) shape).curveTo(25.729, 27.314003, 24.39, 26.541002, 24.0,
				26.316004);
		((GeneralPath) shape).closePath();

		g.setPaint(new LinearGradientPaint(
				new Point2D.Double(245.5, 781.1300048828125),
				new Point2D.Double(368.2200012207031, 781.1300048828125),
				new float[] { 0, 0.5f, 1 },
				new Color[] { new Color(0xFFFFFF, true), Color.WHITE,
						new Color(0x999999) },
				CycleMethod.NO_CYCLE, ColorSpaceType.SRGB, new AffineTransform(
						0.054487f, 0, 0, 0.054487f, 10.624f, -11.637f)));
		g.fill(shape);
		g.setComposite(AlphaComposite.getInstance(3, 1 * origAlpha));

		// _0_0_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(12.382, 19.607);
		((GeneralPath) shape).lineTo(25.729, 27.313);
		((GeneralPath) shape).curveTo(26.74, 27.897, 27.959, 27.972, 28.772,
				27.503);
		((GeneralPath) shape).lineTo(44.063, 18.674);
		((GeneralPath) shape).curveTo(47.807, 16.512, 47.093998, 12.62, 42.463,
				9.9459);
		((GeneralPath) shape).curveTo(37.831, 7.2720003, 31.087002, 6.8592,
				27.344002, 9.0206);
		((GeneralPath) shape).lineTo(12.0580015, 17.848);
		((GeneralPath) shape).curveTo(11.241001, 18.32, 11.370002, 19.024,
				12.382002, 19.607);

		g.setPaint(new Color(0xCCCCCC));
		g.fill(shape);
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0.054487f, 0, 0, 0.054487f, 10.624f,
				-11.637f));

		// _0_0_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(657.73, 493.63);
		((GeneralPath) shape).curveTo(656.33997, 518.25, 643.23, 534.49, 626.31,
				548.0);
		((GeneralPath) shape).lineTo(338.38, 715.28);
		((GeneralPath) shape).curveTo(309.69, 731.72003, 293.94, 722.24005,
				274.41, 713.25);
		((GeneralPath) shape).lineTo(41.309998, 577.91003);
		((GeneralPath) shape).curveTo(9.244999, 562.10004, 13.430998, 551.05005,
				26.365997, 541.14);

		g.setPaint(Color.WHITE);
		g.setStroke(new BasicStroke(4, 0, 0, 4));
		g.draw(shape);

		g.setTransform(transformations.pop()); // _0_0_3
		g.setComposite(AlphaComposite.getInstance(3, 0.8f * origAlpha));

		// _0_0_4
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(33.301, 7.6358);
		((GeneralPath) shape).curveTo(33.177, 7.6368, 33.052, 7.639, 32.928997,
				7.6426);
		((GeneralPath) shape).curveTo(32.885998, 7.6439, 32.843, 7.6461,
				32.799995, 7.6477);
		((GeneralPath) shape).curveTo(32.697994, 7.6514997, 32.594994,
				7.6556997, 32.493996, 7.6612997);
		((GeneralPath) shape).curveTo(32.481995, 7.6619997, 32.470997, 7.6624,
				32.458996, 7.6629996);
		((GeneralPath) shape).curveTo(32.433994, 7.6644998, 32.407997,
				7.6665998, 32.382996, 7.6681995);
		((GeneralPath) shape).curveTo(32.325996, 7.6716995, 32.269997,
				7.6759996, 32.213997, 7.6800995);
		((GeneralPath) shape).curveTo(32.080997, 7.6897993, 31.948997,
				7.7011995, 31.817997, 7.7140994);
		((GeneralPath) shape).curveTo(31.656998, 7.7298994, 31.495996, 7.747999,
				31.336996, 7.7685995);
		((GeneralPath) shape).curveTo(31.282995, 7.7756996, 31.228996,
				7.7830997, 31.175997, 7.7906995);
		((GeneralPath) shape).curveTo(31.150997, 7.7942996, 31.125998,
				7.7972994, 31.100996, 7.8009996);
		((GeneralPath) shape).curveTo(31.078997, 7.8041997, 31.057997,
				7.8078995, 31.035995, 7.8111997);
		((GeneralPath) shape).curveTo(30.978996, 7.8198, 30.921995, 7.8275,
				30.865995, 7.8366995);
		((GeneralPath) shape).curveTo(30.856995, 7.8381996, 30.847996,
				7.8402996, 30.837996, 7.8417993);
		((GeneralPath) shape).curveTo(30.798996, 7.848399, 30.759996, 7.855399,
				30.720995, 7.8622994);
		((GeneralPath) shape).curveTo(30.694996, 7.8667994, 30.668995,
				7.8711996, 30.642996, 7.8758993);
		((GeneralPath) shape).curveTo(30.639996, 7.8763995, 30.636995,
				7.8770995, 30.633995, 7.8775992);
		((GeneralPath) shape).curveTo(30.487995, 7.9040995, 30.342995,
				7.9317994, 30.199995, 7.9626994);
		((GeneralPath) shape).curveTo(30.192995, 7.964299, 30.184996, 7.9661994,
				30.177996, 7.967799);
		((GeneralPath) shape).curveTo(30.139996, 7.9759994, 30.102995, 7.984899,
				30.064995, 7.993399);
		((GeneralPath) shape).curveTo(30.008995, 8.006299, 29.951994, 8.020599,
				29.894995, 8.034199);
		((GeneralPath) shape).curveTo(29.857994, 8.043399, 29.819994, 8.051999,
				29.782995, 8.061499);
		((GeneralPath) shape).curveTo(29.765995, 8.065799, 29.748995, 8.070699,
				29.731995, 8.075099);
		((GeneralPath) shape).curveTo(29.696995, 8.084099, 29.661995, 8.093099,
				29.627995, 8.102299);
		((GeneralPath) shape).curveTo(29.589994, 8.112498, 29.551994, 8.1224985,
				29.513994, 8.132998);
		((GeneralPath) shape).curveTo(29.511993, 8.133498, 29.509995, 8.134198,
				29.508995, 8.134699);
		((GeneralPath) shape).curveTo(29.492994, 8.138999, 29.477995, 8.143899,
				29.462996, 8.148299);
		((GeneralPath) shape).curveTo(29.356995, 8.178399, 29.250996, 8.209199,
				29.147995, 8.242);
		((GeneralPath) shape).curveTo(29.113995, 8.2526, 29.080996, 8.263499,
				29.046995, 8.2743);
		((GeneralPath) shape).curveTo(28.990995, 8.2927, 28.933994, 8.311299,
				28.878996, 8.3305);
		((GeneralPath) shape).curveTo(28.828997, 8.3476, 28.778996, 8.3656,
				28.730995, 8.3833);
		((GeneralPath) shape).curveTo(28.696995, 8.3955, 28.662994, 8.408299,
				28.629995, 8.4208);
		((GeneralPath) shape).curveTo(28.627995, 8.4216, 28.624996, 8.4216,
				28.622995, 8.422501);
		((GeneralPath) shape).curveTo(28.571995, 8.441601, 28.521996, 8.460501,
				28.471996, 8.480301);
		((GeneralPath) shape).curveTo(28.406996, 8.506101, 28.341997, 8.531801,
				28.277996, 8.558701);
		((GeneralPath) shape).curveTo(28.243996, 8.572901, 28.210997, 8.588401,
				28.176996, 8.6029005);
		((GeneralPath) shape).curveTo(28.156996, 8.611601, 28.136995, 8.6197,
				28.117996, 8.628501);
		((GeneralPath) shape).curveTo(28.088997, 8.640901, 28.061996, 8.655001,
				28.033997, 8.667601);
		((GeneralPath) shape).curveTo(28.009996, 8.6787, 27.984997, 8.688801,
				27.960997, 8.700001);
		((GeneralPath) shape).curveTo(27.936996, 8.711101, 27.912996, 8.722701,
				27.888996, 8.7341);
		((GeneralPath) shape).curveTo(27.826996, 8.7641, 27.764996, 8.794701,
				27.703997, 8.826);
		((GeneralPath) shape).curveTo(27.668997, 8.8436, 27.635996, 8.8625,
				27.601997, 8.8805);
		((GeneralPath) shape).curveTo(27.574997, 8.8945, 27.547997, 8.9071,
				27.521997, 8.9214);
		((GeneralPath) shape).curveTo(27.461998, 8.9537, 27.402998, 8.9865,
				27.344997, 9.0201);
		((GeneralPath) shape).lineTo(12.058997, 17.848999);
		((GeneralPath) shape).curveTo(12.036997, 17.860998, 12.0149975,
				17.874998, 11.994997, 17.887999);
		((GeneralPath) shape).curveTo(11.973997, 17.901, 11.953997, 17.913998,
				11.934997, 17.926998);
		((GeneralPath) shape).curveTo(11.915997, 17.939999, 11.8969965,
				17.953999, 11.878997, 17.967999);
		((GeneralPath) shape).curveTo(11.860997, 17.981998, 11.843997,
				17.994999, 11.827997, 18.008999);
		((GeneralPath) shape).curveTo(11.810997, 18.022999, 11.794997,
				18.036999, 11.779997, 18.050999);
		((GeneralPath) shape).curveTo(11.767997, 18.061998, 11.754997, 18.074,
				11.743997, 18.085);
		((GeneralPath) shape).curveTo(11.740996, 18.088, 11.7389965, 18.092999,
				11.735996, 18.095999);
		((GeneralPath) shape).curveTo(11.722997, 18.109, 11.709996, 18.120998,
				11.697996, 18.134998);
		((GeneralPath) shape).curveTo(11.684997, 18.149998, 11.6729965, 18.168,
				11.660996, 18.183998);
		((GeneralPath) shape).curveTo(11.659996, 18.184998, 11.657996,
				18.185999, 11.656997, 18.187998);
		((GeneralPath) shape).curveTo(11.6469965, 18.200998, 11.638997,
				18.214998, 11.629996, 18.227999);
		((GeneralPath) shape).curveTo(11.620996, 18.242998, 11.610996,
				18.256998, 11.602996, 18.272999);
		((GeneralPath) shape).curveTo(11.601995, 18.273998, 11.602996, 18.275,
				11.602996, 18.276);
		((GeneralPath) shape).curveTo(11.5949955, 18.290998, 11.587996,
				18.304998, 11.580996, 18.32);
		((GeneralPath) shape).curveTo(11.579995, 18.322, 11.579995, 18.323,
				11.578996, 18.324);
		((GeneralPath) shape).curveTo(11.572996, 18.338999, 11.565996,
				18.352999, 11.559996, 18.368);
		((GeneralPath) shape).curveTo(11.554996, 18.383, 11.548996, 18.398,
				11.544995, 18.414);
		((GeneralPath) shape).lineTo(11.544995, 18.418999);
		((GeneralPath) shape).curveTo(11.532995, 18.463999, 11.525995,
				18.508999, 11.524995, 18.554998);
		((GeneralPath) shape).lineTo(11.524995, 18.561998);
		((GeneralPath) shape).lineTo(11.524995, 18.571);
		((GeneralPath) shape).lineTo(11.524995, 26.286999);
		((GeneralPath) shape).curveTo(11.519995, 26.630999, 11.808995,
				26.998999, 12.380995, 27.328999);
		((GeneralPath) shape).lineTo(25.728994, 35.036);
		((GeneralPath) shape).curveTo(26.739994, 35.62, 27.958994, 35.694,
				28.770994, 35.225);
		((GeneralPath) shape).lineTo(44.062996, 26.395998);
		((GeneralPath) shape).curveTo(45.677998, 25.463999, 46.462997,
				24.209997, 46.462997, 22.869999);
		((GeneralPath) shape).lineTo(46.462997, 15.234999);
		((GeneralPath) shape).curveTo(46.463997, 15.187999, 46.462997,
				15.141998, 46.462997, 15.094998);
		((GeneralPath) shape).lineTo(46.462997, 15.089998);
		((GeneralPath) shape).curveTo(46.461998, 15.041998, 46.46, 14.992998,
				46.457996, 14.943998);
		((GeneralPath) shape).curveTo(46.454998, 14.906999, 46.451996,
				14.869998, 46.448997, 14.832998);
		((GeneralPath) shape).lineTo(46.446, 14.796998);
		((GeneralPath) shape).curveTo(46.445, 14.786998, 46.443, 14.775998,
				46.441998, 14.764998);
		((GeneralPath) shape).curveTo(46.441998, 14.758999, 46.440998,
				14.753999, 46.44, 14.747998);
		((GeneralPath) shape).curveTo(46.434998, 14.698998, 46.427998,
				14.649999, 46.42, 14.601998);
		((GeneralPath) shape).lineTo(46.42, 14.599998);
		((GeneralPath) shape).curveTo(46.414997, 14.567999, 46.408997,
				14.534999, 46.403, 14.502998);
		((GeneralPath) shape).curveTo(46.396, 14.464998, 46.389, 14.426998,
				46.381, 14.388998);
		((GeneralPath) shape).curveTo(46.372, 14.347998, 46.362, 14.306998,
				46.352, 14.265998);
		((GeneralPath) shape).curveTo(46.347, 14.245997, 46.342003, 14.2249975,
				46.337, 14.204998);
		((GeneralPath) shape).curveTo(46.327003, 14.169998, 46.318, 14.134998,
				46.308002, 14.098998);
		((GeneralPath) shape).curveTo(46.307003, 14.097998, 46.306004,
				14.095998, 46.306004, 14.093998);
		((GeneralPath) shape).curveTo(46.295002, 14.057998, 46.284004,
				14.022998, 46.272003, 13.986998);
		((GeneralPath) shape).curveTo(46.266003, 13.967998, 46.261, 13.9489975,
				46.255005, 13.930998);
		((GeneralPath) shape).curveTo(46.252007, 13.922997, 46.249004,
				13.9159975, 46.246006, 13.908998);
		((GeneralPath) shape).curveTo(46.234005, 13.873998, 46.222008,
				13.838998, 46.209007, 13.804997);
		((GeneralPath) shape).curveTo(46.203007, 13.788998, 46.198006,
				13.773997, 46.19201, 13.758998);
		((GeneralPath) shape).curveTo(46.173008, 13.708998, 46.152008,
				13.659998, 46.13101, 13.610998);
		((GeneralPath) shape).curveTo(46.113007, 13.569998, 46.09601, 13.529998,
				46.078007, 13.489998);
		((GeneralPath) shape).curveTo(46.073006, 13.479998, 46.069008,
				13.470998, 46.064007, 13.460998);
		((GeneralPath) shape).lineTo(46.049007, 13.427998);
		((GeneralPath) shape).curveTo(46.030006, 13.3899975, 46.01101,
				13.350998, 45.99101, 13.312998);
		((GeneralPath) shape).curveTo(45.966007, 13.262998, 45.94001, 13.213998,
				45.91301, 13.164998);
		((GeneralPath) shape).curveTo(45.88601, 13.116998, 45.85901, 13.068998,
				45.83101, 13.020998);
		((GeneralPath) shape).curveTo(45.83001, 13.019998, 45.82901, 13.017998,
				45.82801, 13.015998);
		((GeneralPath) shape).curveTo(45.81001, 12.985998, 45.79201, 12.955997,
				45.77301, 12.925998);
		((GeneralPath) shape).curveTo(45.76101, 12.906998, 45.749012, 12.886998,
				45.73701, 12.867998);
		((GeneralPath) shape).curveTo(45.72901, 12.854999, 45.720013, 12.841998,
				45.71201, 12.828999);
		((GeneralPath) shape).curveTo(45.68801, 12.792998, 45.66501, 12.755999,
				45.64001, 12.719998);
		((GeneralPath) shape).curveTo(45.61201, 12.677999, 45.58301, 12.635999,
				45.55301, 12.593998);
		((GeneralPath) shape).curveTo(45.548008, 12.586998, 45.54301, 12.578998,
				45.53801, 12.571998);
		((GeneralPath) shape).curveTo(45.50301, 12.523997, 45.46701, 12.476997,
				45.43101, 12.428998);
		((GeneralPath) shape).curveTo(45.43001, 12.427998, 45.43001, 12.426998,
				45.429012, 12.425998);
		((GeneralPath) shape).curveTo(45.388012, 12.371998, 45.345013,
				12.316998, 45.301014, 12.263998);
		((GeneralPath) shape).curveTo(45.292015, 12.251998, 45.282013,
				12.240998, 45.272015, 12.229998);
		((GeneralPath) shape).curveTo(45.237015, 12.185998, 45.201015,
				12.143997, 45.163013, 12.0999975);
		((GeneralPath) shape).curveTo(45.132015, 12.063997, 45.099014,
				12.025997, 45.066013, 11.989998);
		((GeneralPath) shape).curveTo(45.051014, 11.971998, 45.035015,
				11.953998, 45.019012, 11.936997);
		((GeneralPath) shape).curveTo(44.991013, 11.905997, 44.962013,
				11.874997, 44.934013, 11.844997);
		((GeneralPath) shape).curveTo(44.912014, 11.821998, 44.890015,
				11.797997, 44.867012, 11.774998);
		((GeneralPath) shape).curveTo(44.844013, 11.750998, 44.82001, 11.725998,
				44.796013, 11.701998);
		((GeneralPath) shape).curveTo(44.767014, 11.672997, 44.737015,
				11.643998, 44.707012, 11.614998);
		((GeneralPath) shape).curveTo(44.65301, 11.561997, 44.59701, 11.5079975,
				44.540012, 11.454998);
		((GeneralPath) shape).curveTo(44.499012, 11.416998, 44.45801, 11.378998,
				44.41601, 11.340998);
		((GeneralPath) shape).curveTo(44.399014, 11.325997, 44.38301, 11.310998,
				44.367012, 11.296998);
		((GeneralPath) shape).curveTo(44.359013, 11.289998, 44.352013,
				11.2839985, 44.344013, 11.277998);
		((GeneralPath) shape).curveTo(44.315014, 11.251998, 44.28401, 11.225998,
				44.254013, 11.199998);
		((GeneralPath) shape).curveTo(44.230015, 11.178998, 44.207012,
				11.158998, 44.183014, 11.137998);
		((GeneralPath) shape).curveTo(44.122013, 11.086998, 44.059013,
				11.035997, 43.995014, 10.984998);
		((GeneralPath) shape).curveTo(43.945015, 10.943997, 43.893013,
				10.902998, 43.840015, 10.861998);
		((GeneralPath) shape).curveTo(43.826015, 10.851997, 43.812016,
				10.840998, 43.798016, 10.829998);
		((GeneralPath) shape).curveTo(43.753017, 10.795998, 43.707016,
				10.761998, 43.662014, 10.727998);
		((GeneralPath) shape).curveTo(43.639015, 10.710998, 43.616013,
				10.694998, 43.594013, 10.678998);
		((GeneralPath) shape).curveTo(43.593014, 10.677998, 43.592014,
				10.678998, 43.592014, 10.678998);
		((GeneralPath) shape).curveTo(43.523014, 10.628998, 43.454014,
				10.577998, 43.382015, 10.528998);
		((GeneralPath) shape).curveTo(43.317017, 10.482999, 43.249016,
				10.437999, 43.181015, 10.391998);
		((GeneralPath) shape).curveTo(43.175014, 10.387999, 43.169014,
				10.382998, 43.163013, 10.378999);
		((GeneralPath) shape).curveTo(43.138012, 10.361999, 43.113014,
				10.345999, 43.088013, 10.328999);
		((GeneralPath) shape).curveTo(43.038013, 10.296999, 42.989014,
				10.263999, 42.93801, 10.231998);
		((GeneralPath) shape).curveTo(42.921013, 10.221998, 42.90301, 10.210999,
				42.88501, 10.199999);
		((GeneralPath) shape).curveTo(42.82501, 10.162999, 42.76401, 10.126999,
				42.70301, 10.088999);
		((GeneralPath) shape).curveTo(42.624012, 10.041999, 42.54401, 9.993399,
				42.46301, 9.946399);
		((GeneralPath) shape).curveTo(42.37901, 9.897999, 42.29401, 9.850299,
				42.209007, 9.803399);
		((GeneralPath) shape).curveTo(42.12401, 9.756699, 42.038006, 9.710699,
				41.952007, 9.6654);
		((GeneralPath) shape).curveTo(41.90101, 9.6387, 41.850006, 9.6116,
				41.799007, 9.5854);
		((GeneralPath) shape).curveTo(41.76301, 9.5673, 41.72701, 9.5487995,
				41.69201, 9.5309);
		((GeneralPath) shape).curveTo(41.65101, 9.5108, 41.611008, 9.4911,
				41.57101, 9.4713);
		((GeneralPath) shape).curveTo(41.52301, 9.448, 41.47601, 9.4244,
				41.42801, 9.4015);
		((GeneralPath) shape).curveTo(41.39901, 9.3877, 41.37001, 9.3743,
				41.341007, 9.3607);
		((GeneralPath) shape).curveTo(41.281006, 9.332399, 41.22101, 9.3048,
				41.160007, 9.2772);
		((GeneralPath) shape).curveTo(41.14301, 9.2694, 41.126007, 9.2611,
				41.10901, 9.2534);
		((GeneralPath) shape).curveTo(41.03701, 9.2206, 40.96301, 9.1882,
				40.890007, 9.1563);
		((GeneralPath) shape).curveTo(40.885006, 9.154099, 40.879005, 9.1517,
				40.87401, 9.1495);
		((GeneralPath) shape).curveTo(40.78801, 9.1124, 40.702007, 9.0763,
				40.61501, 9.0405);
		((GeneralPath) shape).curveTo(40.55901, 9.017099, 40.50101, 8.995299,
				40.444008, 8.9724);
		((GeneralPath) shape).curveTo(40.40801, 8.9583, 40.37301, 8.9437,
				40.33801, 8.9299);
		((GeneralPath) shape).curveTo(40.278008, 8.906301, 40.21801, 8.883,
				40.15701, 8.8601);
		((GeneralPath) shape).curveTo(40.125008, 8.8476, 40.09201, 8.8349,
				40.05901, 8.822599);
		((GeneralPath) shape).curveTo(40.01101, 8.804699, 39.96201, 8.787299,
				39.91401, 8.769799);
		((GeneralPath) shape).curveTo(39.868008, 8.7532, 39.82201, 8.736699,
				39.77601, 8.720399);
		((GeneralPath) shape).curveTo(39.74001, 8.707799, 39.70501, 8.695399,
				39.66901, 8.682999);
		((GeneralPath) shape).curveTo(39.66201, 8.680499, 39.65401, 8.678699,
				39.64701, 8.676199);
		((GeneralPath) shape).curveTo(39.57201, 8.650399, 39.49701, 8.624399,
				39.422012, 8.599499);
		((GeneralPath) shape).curveTo(39.350014, 8.575899, 39.27801, 8.552599,
				39.206013, 8.529698);
		((GeneralPath) shape).lineTo(39.17301, 8.519499);
		((GeneralPath) shape).curveTo(39.088013, 8.492599, 39.00201, 8.466899,
				38.91601, 8.441198);
		((GeneralPath) shape).curveTo(38.844013, 8.419699, 38.77201, 8.398798,
				38.700012, 8.378199);
		((GeneralPath) shape).curveTo(38.67501, 8.371199, 38.651012, 8.362899,
				38.62701, 8.356098);
		((GeneralPath) shape).curveTo(38.55801, 8.336898, 38.48901, 8.318298,
				38.42101, 8.299898);
		((GeneralPath) shape).curveTo(38.33601, 8.277098, 38.25201, 8.254998,
				38.167007, 8.233499);
		((GeneralPath) shape).curveTo(38.125008, 8.222698, 38.083008, 8.211498,
				38.041008, 8.201098);
		((GeneralPath) shape).curveTo(37.99801, 8.1905985, 37.95601, 8.180698,
				37.91301, 8.170499);
		((GeneralPath) shape).curveTo(37.85801, 8.157099, 37.80201, 8.144099,
				37.74601, 8.131299);
		((GeneralPath) shape).curveTo(37.71701, 8.124499, 37.68701, 8.117499,
				37.65801, 8.110899);
		((GeneralPath) shape).curveTo(37.58901, 8.095199, 37.520008, 8.079799,
				37.45001, 8.064899);
		((GeneralPath) shape).curveTo(37.43401, 8.0615, 37.418007, 8.058,
				37.402008, 8.0547);
		((GeneralPath) shape).curveTo(37.320007, 8.0372, 37.237007, 8.0199995,
				37.154007, 8.0036);
		((GeneralPath) shape).curveTo(37.15101, 8.003, 37.148006, 8.0024,
				37.145008, 8.0019);
		((GeneralPath) shape).curveTo(37.06001, 7.9849997, 36.974007, 7.97,
				36.88801, 7.9542);
		((GeneralPath) shape).curveTo(36.82401, 7.9424, 36.76001, 7.9312997,
				36.696007, 7.9202);
		((GeneralPath) shape).curveTo(36.674007, 7.9164, 36.653008, 7.9119,
				36.63101, 7.9082);
		((GeneralPath) shape).curveTo(36.60701, 7.9041996, 36.583008, 7.9003,
				36.56001, 7.8963);
		((GeneralPath) shape).curveTo(36.49701, 7.8859, 36.43501, 7.8754,
				36.37201, 7.8657);
		((GeneralPath) shape).curveTo(36.33501, 7.8599, 36.299007, 7.8542,
				36.26201, 7.8486);
		((GeneralPath) shape).curveTo(36.21301, 7.8413, 36.16401, 7.8350997,
				36.11501, 7.8282);
		((GeneralPath) shape).curveTo(36.06401, 7.8209, 36.01301, 7.8129,
				35.96201, 7.8061);
		((GeneralPath) shape).curveTo(35.92701, 7.8013, 35.89201, 7.797,
				35.85601, 7.7925);
		((GeneralPath) shape).curveTo(35.77001, 7.7814, 35.68501, 7.7717,
				35.59901, 7.7618);
		((GeneralPath) shape).curveTo(35.51401, 7.7518997, 35.42801, 7.7416,
				35.34201, 7.7328997);
		((GeneralPath) shape).curveTo(35.25101, 7.7235994, 35.15901, 7.7152996,
				35.068012, 7.7072997);
		((GeneralPath) shape).curveTo(35.045013, 7.7053, 35.023014, 7.7040997,
				35.00001, 7.7022);
		((GeneralPath) shape).curveTo(34.92401, 7.6958, 34.84801, 7.6889,
				34.77201, 7.6835);
		((GeneralPath) shape).curveTo(34.67401, 7.6765, 34.57801, 7.6702,
				34.48101, 7.6647);
		((GeneralPath) shape).curveTo(34.47901, 7.6647, 34.47701, 7.6648,
				34.47501, 7.6647);
		((GeneralPath) shape).curveTo(34.42201, 7.6617002, 34.36801, 7.6588,
				34.31401, 7.6562);
		((GeneralPath) shape).curveTo(34.30901, 7.656, 34.30501, 7.6563997,
				34.30001, 7.6562);
		((GeneralPath) shape).curveTo(34.22001, 7.6525, 34.14001, 7.6486998,
				34.06001, 7.646);
		((GeneralPath) shape).curveTo(34.00201, 7.6440997, 33.94301, 7.6423,
				33.88501, 7.6409);
		((GeneralPath) shape).curveTo(33.85801, 7.6403003, 33.83101, 7.6397,
				33.805008, 7.6392);
		((GeneralPath) shape).curveTo(33.73301, 7.6378, 33.66301, 7.6364,
				33.592007, 7.6358004);
		((GeneralPath) shape).curveTo(33.494007, 7.635, 33.398006, 7.635,
				33.301006, 7.6358004);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x000080));
		g.fill(shape);
		g.setComposite(AlphaComposite.getInstance(3, 0.3f * origAlpha));

		// _0_0_5
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(28.253, 13.633);
		((GeneralPath) shape).curveTo(27.775, 13.909, 27.764, 14.373, 28.168001,
				14.777);
		((GeneralPath) shape).lineTo(25.878002, 16.1);
		((GeneralPath) shape).curveTo(25.819002, 16.134, 25.781002, 16.179,
				25.768002, 16.23);
		((GeneralPath) shape).lineTo(24.854002, 19.823);
		((GeneralPath) shape).lineTo(24.118002, 20.248);
		((GeneralPath) shape).curveTo(23.402002, 19.99, 22.561003, 19.987999,
				22.066002, 20.272999);
		((GeneralPath) shape).curveTo(21.463001, 20.620998, 21.604002, 21.268,
				22.381002, 21.716);
		((GeneralPath) shape).curveTo(23.159002, 22.164999, 24.279003, 22.247,
				24.881002, 21.899);
		((GeneralPath) shape).curveTo(25.359003, 21.623001, 25.369003, 21.159,
				24.967003, 20.756);
		((GeneralPath) shape).lineTo(25.893003, 20.221);
		((GeneralPath) shape).lineTo(25.898003, 20.223001);
		((GeneralPath) shape).lineTo(25.898003, 20.218002);
		((GeneralPath) shape).lineTo(27.954002, 19.031002);
		((GeneralPath) shape).lineTo(27.957003, 19.034002);
		((GeneralPath) shape).lineTo(33.512, 18.562002);
		((GeneralPath) shape).curveTo(33.601, 18.555002, 33.679, 18.534002,
				33.739002, 18.500002);
		((GeneralPath) shape).lineTo(35.832, 17.291002);
		((GeneralPath) shape).lineTo(36.408, 17.624002);
		((GeneralPath) shape).lineTo(36.696, 17.790003);
		((GeneralPath) shape).lineTo(36.92, 17.661003);
		((GeneralPath) shape).lineTo(38.679996, 16.645002);
		((GeneralPath) shape).lineTo(38.902996, 16.516003);
		((GeneralPath) shape).lineTo(38.614998, 16.349003);
		((GeneralPath) shape).lineTo(36.495, 15.125003);
		((GeneralPath) shape).lineTo(36.207, 14.9590025);
		((GeneralPath) shape).lineTo(35.983, 15.088002);
		((GeneralPath) shape).lineTo(34.223003, 16.104002);
		((GeneralPath) shape).lineTo(34.000004, 16.233002);
		((GeneralPath) shape).lineTo(34.288002, 16.399002);
		((GeneralPath) shape).lineTo(34.968002, 16.792002);
		((GeneralPath) shape).lineTo(32.965004, 17.948002);
		((GeneralPath) shape).lineTo(29.290005, 18.260002);
		((GeneralPath) shape).lineTo(37.178005, 13.706002);
		((GeneralPath) shape).lineTo(38.287006, 14.346003);
		((GeneralPath) shape).lineTo(38.685005, 14.575003);
		((GeneralPath) shape).lineTo(38.755005, 14.306003);
		((GeneralPath) shape).lineTo(39.279003, 12.246002);
		((GeneralPath) shape).lineTo(39.350002, 11.976002);
		((GeneralPath) shape).lineTo(38.882004, 12.015001);
		((GeneralPath) shape).lineTo(35.310005, 12.317001);
		((GeneralPath) shape).lineTo(34.844006, 12.358002);
		((GeneralPath) shape).lineTo(35.242004, 12.587002);
		((GeneralPath) shape).lineTo(36.314003, 13.207002);
		((GeneralPath) shape).lineTo(26.196003, 19.048002);
		((GeneralPath) shape).lineTo(26.832003, 16.546001);
		((GeneralPath) shape).lineTo(29.020002, 15.283001);
		((GeneralPath) shape).curveTo(29.734003, 15.540001, 30.573002,
				15.542001, 31.067001, 15.257001);
		((GeneralPath) shape).curveTo(31.670002, 14.909001, 31.53, 14.264001,
				30.753002, 13.815001);
		((GeneralPath) shape).curveTo(29.976004, 13.366, 28.856003, 13.285001,
				28.253002, 13.633);

		g.setPaint(Color.WHITE);
		g.fill(shape);
		g.setComposite(AlphaComposite.getInstance(3, 1 * origAlpha));

		// _0_0_6
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(33.13, 7.0504);
		((GeneralPath) shape).curveTo(30.563002, 7.0597997, 27.987001, 7.7718,
				25.863, 9.2286);
		((GeneralPath) shape).curveTo(21.061, 12.021999, 16.220001, 14.751999,
				11.446001, 17.592);
		((GeneralPath) shape).curveTo(10.778001, 18.181, 11.017001, 19.154999,
				10.988001, 19.935);
		((GeneralPath) shape).lineTo(10.988001, 21.678);
		((GeneralPath) shape).curveTo(7.683401, 23.6, 4.354501, 25.463999,
				1.0762005, 27.432999);
		((GeneralPath) shape).curveTo(0.6865805, 28.048998, 0.9875805, 28.817,
				0.89728045, 29.496998);
		((GeneralPath) shape).curveTo(0.93239045, 30.964998, 0.78944045, 32.453,
				0.98111045, 33.908997);
		((GeneralPath) shape).curveTo(1.5347004, 34.511997, 2.3671105,
				34.732998, 3.0234103, 35.203995);
		((GeneralPath) shape).curveTo(6.3582106, 37.111996, 9.66561, 39.073994,
				13.01601, 40.947994);
		((GeneralPath) shape).curveTo(13.741011, 40.995995, 14.23001, 40.355995,
				14.85801, 40.103992);
		((GeneralPath) shape).curveTo(17.94601, 38.32099, 21.03301, 36.537994,
				24.12101, 34.753994);
		((GeneralPath) shape).curveTo(25.31101, 35.472996, 26.63401, 36.347996,
				28.095009, 36.082993);
		((GeneralPath) shape).curveTo(29.38901, 35.818993, 30.39301, 34.887993,
				31.557009, 34.322994);
		((GeneralPath) shape).curveTo(36.097008, 31.674994, 40.69201, 29.113995,
				45.19101, 26.397995);
		((GeneralPath) shape).curveTo(46.45501, 25.474995, 47.28801, 23.901995,
				47.104008, 22.316996);
		((GeneralPath) shape).curveTo(47.064007, 19.665997, 47.19801, 17.008995,
				47.028008, 14.361996);
		((GeneralPath) shape).curveTo(46.57501, 12.196996, 44.80401, 10.625996,
				42.97701, 9.542995);
		((GeneralPath) shape).curveTo(40.02501, 7.7809954, 36.55001, 7.0162954,
				33.13001, 7.0503955);
		((GeneralPath) shape).closePath();

		g.setPaint(Color.BLACK);
		g.setStroke(new BasicStroke(1.4f, 1, 1, 4));
		g.draw(shape);
		transformations.push(g.getTransform());
		g.transform(new AffineTransform(0.054487f, 0, 0, 0.054487f, 10.624f,
				-11.637f));

		// _0_0_7

		// _0_0_7_0
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-168.77, 830.91);
		((GeneralPath) shape).lineTo(46.03, 954.92);
		((GeneralPath) shape).lineTo(260.83002, 830.91);
		((GeneralPath) shape).lineTo(46.030014, 706.88995);
		((GeneralPath) shape).lineTo(-168.76999, 830.91);
		((GeneralPath) shape).closePath();

		g.setPaint(Color.GRAY);
		g.fill(shape);

		// _0_0_7_1
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-165.7, 829.13);
		((GeneralPath) shape).lineTo(52.17, 703.35);
		((GeneralPath) shape).lineTo(52.165997, 604.13);
		((GeneralPath) shape).lineTo(-165.704, 729.92);
		((GeneralPath) shape).lineTo(-165.704, 829.13);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x4D4D4D));
		g.fill(shape);

		// _0_0_7_2
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(30.686, 928.35);
		((GeneralPath) shape).lineTo(258.826, 795.47);
		((GeneralPath) shape).lineTo(258.826, 777.75995);
		((GeneralPath) shape).lineTo(30.68599, 910.62994);
		((GeneralPath) shape).lineTo(30.68199, 928.3499);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xB3B3B3));
		g.fill(shape);

		// _0_0_7_3
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-153.43, 822.05);
		((GeneralPath) shape).lineTo(-153.43, 804.33);
		((GeneralPath) shape).lineTo(30.690002, 910.63);
		((GeneralPath) shape).lineTo(30.686003, 928.35);
		((GeneralPath) shape).lineTo(-153.43399, 822.05);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xECECEC));
		g.fill(shape);

		// _0_0_7_4
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(30.686, 910.63);
		((GeneralPath) shape).lineTo(-153.43399, 804.33);
		((GeneralPath) shape).lineTo(76.70601, 671.46);
		((GeneralPath) shape).lineTo(260.826, 777.76);
		((GeneralPath) shape).lineTo(30.68599, 910.63);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xCCCCCC));
		g.fill(shape);

		// _0_0_7_5
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-103.86, 802.29);
		((GeneralPath) shape).lineTo(-80.846, 815.57);
		((GeneralPath) shape).lineTo(122.743996, 698.03);
		((GeneralPath) shape).lineTo(99.732994, 684.74005);
		((GeneralPath) shape).lineTo(-103.857, 802.29004);
		((GeneralPath) shape).moveTo(-65.5, 824.43005);
		((GeneralPath) shape).lineTo(-42.485, 837.72003);
		((GeneralPath) shape).lineTo(161.105, 720.18005);
		((GeneralPath) shape).lineTo(138.095, 706.8901);
		((GeneralPath) shape).lineTo(-65.494995, 824.43005);
		((GeneralPath) shape).lineTo(-65.49799, 824.43005);
		((GeneralPath) shape).moveTo(-27.139992, 846.5801);
		((GeneralPath) shape).lineTo(-4.125992, 859.87006);
		((GeneralPath) shape).curveTo(-4.125792, 859.87006, 199.464, 742.32007,
				199.464, 742.32007);
		((GeneralPath) shape).lineTo(176.444, 729.04004);
		((GeneralPath) shape).lineTo(-27.136002, 846.58);
		((GeneralPath) shape).lineTo(-27.141, 846.58);
		((GeneralPath) shape).moveTo(11.215998, 868.72003);
		((GeneralPath) shape).lineTo(34.230995, 882.01);
		((GeneralPath) shape).lineTo(237.82098, 764.47003);
		((GeneralPath) shape).lineTo(214.80098, 751.18005);
		((GeneralPath) shape).lineTo(11.210983, 868.72003);
		((GeneralPath) shape).lineTo(11.212983, 868.72003);

		g.setPaint(Color.GRAY);
		g.fill(shape);

		// _0_0_7_6
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(46.031, 600.59);
		((GeneralPath) shape).lineTo(-168.77899, 724.59);
		((GeneralPath) shape).lineTo(46.031006, 848.62);
		((GeneralPath) shape).lineTo(260.841, 724.58997);
		((GeneralPath) shape).lineTo(46.031006, 600.58997);
		((GeneralPath) shape).closePath();
		((GeneralPath) shape).moveTo(30.687998, 680.31006);
		((GeneralPath) shape).lineTo(61.375, 698.03);
		((GeneralPath) shape).lineTo(0.0, 733.47003);
		((GeneralPath) shape).lineTo(-30.688, 715.75006);
		((GeneralPath) shape).lineTo(30.688, 680.31006);
		((GeneralPath) shape).closePath();
		((GeneralPath) shape).moveTo(92.062, 715.75006);
		((GeneralPath) shape).lineTo(122.75, 733.47003);
		((GeneralPath) shape).lineTo(61.375, 768.91003);
		((GeneralPath) shape).lineTo(30.688, 751.19006);
		((GeneralPath) shape).lineTo(92.062, 715.75006);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		// _0_0_7_7
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(46.029, 954.92);
		((GeneralPath) shape).lineTo(46.029, 848.62);
		((GeneralPath) shape).lineTo(260.829, 724.61);
		((GeneralPath) shape).lineTo(260.829, 830.91);
		((GeneralPath) shape).lineTo(46.029007, 954.92);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x4D4D4D));
		g.fill(shape);

		// _0_0_7_8
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-27.617, 717.52);
		((GeneralPath) shape).lineTo(-30.686, 715.75);
		((GeneralPath) shape).lineTo(30.686, 680.31);
		((GeneralPath) shape).lineTo(30.686, 683.86);
		((GeneralPath) shape).lineTo(-27.617, 717.51996);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x333333));
		g.fill(shape);

		// _0_0_7_9
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(30.686, 680.31);
		((GeneralPath) shape).lineTo(30.686, 683.86);
		((GeneralPath) shape).lineTo(58.303, 699.8);
		((GeneralPath) shape).lineTo(61.372, 698.02997);
		((GeneralPath) shape).lineTo(30.686, 680.31);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE6E6E6));
		g.fill(shape);

		// _0_0_7_10
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-168.78, 724.59);
		((GeneralPath) shape).lineTo(-168.78, 830.91003);
		((GeneralPath) shape).lineTo(46.03, 954.91003);
		((GeneralPath) shape).lineTo(46.031, 848.62006);
		((GeneralPath) shape).lineTo(-168.77899, 724.5901);
		((GeneralPath) shape).closePath();
		((GeneralPath) shape).moveTo(-165.72, 729.91003);
		((GeneralPath) shape).lineTo(42.97, 850.41003);
		((GeneralPath) shape).lineTo(42.969, 949.59);
		((GeneralPath) shape).lineTo(-165.72101, 829.12);
		((GeneralPath) shape).lineTo(-165.72101, 729.91);
		((GeneralPath) shape).closePath();

		g.fill(shape);

		// _0_0_7_11
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(33.755, 752.95);
		((GeneralPath) shape).lineTo(30.686, 751.18);
		((GeneralPath) shape).lineTo(92.058, 715.75);
		((GeneralPath) shape).lineTo(92.058, 719.29);
		((GeneralPath) shape).lineTo(33.754997, 752.94995);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0x333333));
		g.fill(shape);

		// _0_0_7_12
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(92.058, 715.75);
		((GeneralPath) shape).lineTo(92.058, 719.29);
		((GeneralPath) shape).lineTo(119.68, 735.24);
		((GeneralPath) shape).lineTo(122.74, 733.45996);
		((GeneralPath) shape).lineTo(92.058, 715.74994);
		((GeneralPath) shape).closePath();

		g.setPaint(new Color(0xE6E6E6));
		g.fill(shape);
		g.setComposite(AlphaComposite.getInstance(3, 0.6f * origAlpha));

		// _0_0_7_13
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(33.755, 752.95);
		((GeneralPath) shape).lineTo(92.058, 719.29004);
		((GeneralPath) shape).lineTo(119.68, 735.24005);
		((GeneralPath) shape).lineTo(61.372, 768.9);
		((GeneralPath) shape).lineTo(33.755, 752.95);
		((GeneralPath) shape).closePath();

		g.setPaint(Color.BLACK);
		g.fill(shape);

		// _0_0_7_14
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(-27.617, 717.52);
		((GeneralPath) shape).lineTo(30.686, 683.86005);
		((GeneralPath) shape).lineTo(58.303, 699.80005);
		((GeneralPath) shape).lineTo(0.0, 733.46);
		((GeneralPath) shape).lineTo(-27.617, 717.52);
		((GeneralPath) shape).closePath();

		g.fill(shape);
		g.setComposite(AlphaComposite.getInstance(3, 0.24f * origAlpha));

		// _0_0_7_15
		shape = new GeneralPath();
		((GeneralPath) shape).moveTo(47.104, 848.01);
		((GeneralPath) shape).lineTo(258.39398, 726.01);

		g.setPaint(Color.WHITE);
		g.setStroke(new BasicStroke(4, 0, 0, 4));
		g.draw(shape);

		g.setTransform(transformations.pop()); // _0_0_7

		g.setTransform(transformations.pop()); // _0

	}
}