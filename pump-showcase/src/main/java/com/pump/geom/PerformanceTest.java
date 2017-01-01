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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.pump.inspector.InspectorGridBagLayout;

public class PerformanceTest extends BasicTestElement {
	
	@Override
	public String getDescription() {
		return "This creates several identical randomly placed shapes and " +
			"combines them.  The test should automatically trigger and end the Shark profiler " +
			"to measure results.";
	}

	JLabel typeLabel = new JLabel("Type:");
	JComboBox type = new JComboBox();
	JLabel repeatLabel = new JLabel("Repeat:");
	JSpinner repeat = new JSpinner(new SpinnerNumberModel(5,1,1000,1));
	JLabel additionsLabel = new JLabel("Additions:");
	JSpinner additions = new JSpinner(new SpinnerNumberModel(100,1,1000,1));
	PrintStream printStream;
	
	public PerformanceTest(PrintStream stream) {
		printStream = stream;
		type.addItem("Linear");
		type.addItem("Quadratic");
		type.addItem("Cubic");
	}

	@Override
	public void addControls(InspectorGridBagLayout layout) {
		layout.addRow(typeLabel, type, false);
		layout.addRow(additionsLabel, additions, false);
		layout.addRow(repeatLabel, repeat, false);
	}
	
	@Override
	public String getName() {
		return "Performance Test";
	}

	@Override
	public void doTest() {
		testAdditions( ((Number)repeat.getValue()).intValue() ,((Number)additions.getValue()).intValue());
		printStream.println("Done.");
	}
		
	public void testAdditions(int iterations,int additions) {
		Random random = new Random(0);
		long[] times = new long[iterations];
		for(int a = 0; a<iterations; a++) {
			random.setSeed(0);
			AreaX sum = new AreaX();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<additions; b++) {
				if(cancelled) return;
				float percent = ((float)a)/((float)iterations);
				percent = percent + 1f/(iterations)*(b)/(additions);
				progress.setValue( (int)(percent*(progress.getMaximum()-progress.getMinimum()))+progress.getMinimum() );
				
				double x = 1000*random.nextDouble();
				double y = 1000*random.nextDouble();
				Shape shape;
				if(type.getSelectedIndex()==0) {
					shape = createDiamond((float)x, (float)y);
				} else if(type.getSelectedIndex()==1) {
					shape = createQuad((float)x, (float)y);
				} else { //use cubics
					shape = new Ellipse2D.Double(x, y, 30, 30);
				}
				AreaX k = new AreaX(shape);
				sum.add(k);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		Arrays.sort(times);
		printStream.println("Median Time: "+times[times.length/2]+" ms");
	}
}