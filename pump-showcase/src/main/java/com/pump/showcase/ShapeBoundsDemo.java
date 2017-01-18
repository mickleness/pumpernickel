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
package com.pump.showcase;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import com.pump.geom.AreaX;
import com.pump.geom.ShapeBounds;
import com.pump.util.JVM;

/** A simple test showing off the efficiency of {@link ShapeBounds}.
 *
 **/
public class ShapeBoundsDemo extends OutputDemo {
	private static final long serialVersionUID = 1L;

	public ShapeBoundsDemo() {
		super("Run Tests...", true);
	}
	
	@Override
	public void run() {

		PrintStream out = console.createPrintStream(false);
		PrintStream blue = console.createPrintStream(new Color(0x550000ff,true));
		PrintStream err = console.createPrintStream(true);
		
		try {
			out.println(JVM.getProfile());
			out.println("\nThis application demonstrates the performance of the ShapeBounds class, which"
					+ " are highlighted in blue.");
			out.println("\nIf a table entry reads \"NA\" that means the bounds it returned were "
					+ "incorrect (therefore: clocking its performance is irrelevant).\n");
			
			
			out.print("# of Shape Segments\tGeneralPath\tPath2D.Double\tArea\t");
			blue.print("ShapeBounds");
			out.print("\tAreaX\t");
			out.println();
			
			for(int a = 5; a<70; a+=10) {
				runTest(a, out, err, blue);
			}
		} catch(Throwable t) {
			t.printStackTrace(err);
		} finally {
			out.println("\nFinished.");
		}
	}
	
	static abstract class Test {
		PrintStream stream;
		
		GeneralPath createGeneralPath(Random r,int numberOfSegments) {
			GeneralPath p = new GeneralPath();
			p.moveTo(1000*r.nextFloat(),1000*r.nextFloat());
			for(int b = 0; b<numberOfSegments; b++) {
				p.curveTo(1000*r.nextFloat(), 1000*r.nextFloat(),
						1000*r.nextFloat(), 1000*r.nextFloat(),
						1000*r.nextFloat(), 1000*r.nextFloat() );
			}
			p.closePath();
			return p;
		}
		
		Path2D.Double createPath2D(Random r,int numberOfSegments) {
			Path2D.Double p = new Path2D.Double();
			p.moveTo(1000*r.nextFloat(),1000*r.nextFloat());
			for(int b = 0; b<numberOfSegments; b++) {
				p.curveTo(1000*r.nextFloat(), 1000*r.nextFloat(),
						1000*r.nextFloat(), 1000*r.nextFloat(),
						1000*r.nextFloat(), 1000*r.nextFloat() );
			}
			p.closePath();
			return p;
		}
		
		public abstract Rectangle2D run(Random random,int numberOfSegments);
	}

	private static void runTest(int numberOfSegments,PrintStream out,PrintStream err,PrintStream blue) {
		out.print(numberOfSegments+"\t");

		Test[] tests = new Test[5];
		tests[0] = new Test() {
			public Rectangle2D run(Random random,int numberOfSegments) {
				return createGeneralPath(random, numberOfSegments).getBounds2D();
			}
		};
		tests[1] = new Test() {
			public Rectangle2D run(Random random,int numberOfSegments) {
				return createPath2D(random, numberOfSegments).getBounds2D();
			}
		};
		tests[2] = new Test() {
			public Rectangle2D run(Random random,int numberOfSegments) {
				return new Area(createGeneralPath(random, numberOfSegments)).getBounds2D();
			}
		};
		tests[3] = new Test() {
			public Rectangle2D run(Random random,int numberOfSegments) {
				return ShapeBounds.getBounds( createGeneralPath(random, numberOfSegments) );
			}
		};
		tests[0].stream = out;
		tests[1].stream = out;
		tests[2].stream = out;
		tests[3].stream = blue;
		tests[4] = new Test() {
			public Rectangle2D run(Random random,int numberOfSegments) {
				try {
					Shape gp = createGeneralPath(random, numberOfSegments);
					Shape areax = new AreaX(gp);
					return areax.getBounds2D();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		tests[4].stream = out;
		
		Random random = new Random();
		
		long[] rowData = new long[tests.length];
		Rectangle2D[] rectData = new Rectangle2D[tests.length];
		for(int a = 0; a<tests.length; a++) {
			long[] t = new long[10];
			for(int b = 0; b<t.length; b++) {
				random.setSeed(b*100);
				t[b] = System.currentTimeMillis();
				rectData[a] = tests[a].run(random, numberOfSegments);
				t[b] = System.currentTimeMillis() - t[b];
			}
			Arrays.sort(t);
			long median = t[t.length/2];
			rowData[a] = median;
		}
		
		if(!equivalent(rectData[2], rectData[3])) {
			System.err.println(rectData[2]+" vs "+rectData[3]);
			throw new RuntimeException("When assessing the accuracy of these approaches, I assumed Area and ShapeBounds would be equivalent.");
		}
		
		for(int a = 0; a<rowData.length; a++) {
			if(equivalent(rectData[a], rectData[3])) {
				tests[a].stream.print( rowData[a] );
			} else {
				err.print("NA");
			}
			out.print("\t");
		}
		
		out.println();
	}
	
	private static boolean equivalent(Rectangle2D r1,Rectangle2D r2) {
		double tolerance = .001;
		if( Math.abs(r1.getMinX() - r2.getMinX()) > tolerance)
			return false;
		if( Math.abs(r1.getMaxX() - r2.getMaxX()) > tolerance)
			return false;
		if( Math.abs(r1.getMinY() - r2.getMinY()) > tolerance)
			return false;
		if( Math.abs(r1.getMaxY() - r2.getMaxY()) > tolerance)
			return false;
		return true;
	}
}