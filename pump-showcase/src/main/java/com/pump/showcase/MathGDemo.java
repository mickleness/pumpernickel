package com.pump.showcase;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import com.pump.math.MathG;

public class MathGDemo extends OutputDemo {

	public MathGDemo() {
		super("Run...", false);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		boolean showBiggestError = false;
		PrintStream out = this.console.createPrintStream(false);

    	long[] times = new long[20];
		double[] values = new double[1000000];
		double[] smallValues = new double[1000000];
		Random random = new Random(0);
		for(int a = 0; a<values.length; a++) {
			if(false) { //only positive numbers
				values[a] = (random.nextDouble())*10000;
			} else { //include negative numbers
				values[a] = (random.nextDouble()-.5)*10000*2;
			}
		}
		for(int a = 0; a<smallValues.length; a++) {
			smallValues[a] = random.nextDouble()*2-1;
		}
		
		/*
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				MathG.sin01(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.sin01() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				MathG.sin00004(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.sin00004() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.sin(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMath.sin() median time: "+times[times.length/2]+" ms");
		
		/////////////////////////////
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				MathG.cos01(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.cos01() median time: "+times[times.length/2]+" ms");

		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				MathG.cos00004(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.cos00004() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.cos(values[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMath.cos() median time: "+times[times.length/2]+" ms");

		////////////////////////////////
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				MathG.acos(smallValues[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.acos() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int b = 0; b<values.length; b++) {
				Math.acos(smallValues[b]);
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMath.acos() median time: "+times[times.length/2]+" ms");
		*/

		////////////////////////////////
		
		int extraTimes = 9000000;
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					MathG.floorDouble(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.floorDouble() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					MathG.floorInt(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.floorInt() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					Math.floor(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMath.floorDouble() median time: "+times[times.length/2]+" ms");
		
		/////////////////////////
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					MathG.ceilDouble(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.ceilDouble() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					MathG.ceilInt(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.ceilInt() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					Math.ceil(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMath.ceilDouble() median time: "+times[times.length/2]+" ms");
		
		///////////////////////////////////

		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					MathG.roundDouble(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.roundDouble() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					MathG.roundInt(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMathG.roundInt() median time: "+times[times.length/2]+" ms");
		
		for(int a = 0; a<times.length; a++) {
			Thread.yield();
			times[a] = System.currentTimeMillis();
			for(int c = 0; c<extraTimes; c++) {
				for(int b = 0; b<values.length; b++) {
					Math.round(values[b]);
				}
			}
			times[a] = System.currentTimeMillis()-times[a];
		}
		
		Arrays.sort(times);
		out.println("\tMath.round() median time: "+times[times.length/2]+" ms");
	
		/////////////
		
    }
}
