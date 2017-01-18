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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.pump.util.JVM;
import com.pump.util.ResourcePool;

/** This simple app makes a few comparison related to
 * whether the ResourcePool marks an improvement or not.
 * 
 **/
public class ResourcePoolDemo extends OutputDemo {
	
	
	public ResourcePoolDemo() {
		super("Run Tests...", true);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		PrintStream out = console.createPrintStream(false);
		PrintStream blue = console.createPrintStream(new Color(0x550000ff,true));
		PrintStream err = console.createPrintStream(true);
		
		out.println(JVM.getProfile()+"\n");
		try {
			//give everyone else a chance to catch up:
			out.println("Image Comparison:");
			out.print("Paint\tClear\t");
			err.print("Construction (ms)");
			out.print("\t");
			blue.println("ResourcePool (ms)");
			
			out.print("true\ttrue\t");
			err.print(runImageTest(false, true, true));
			out.print("\t");
			blue.println(runImageTest(true, true, true));
			
			out.print("true\tfalse\t");
			err.print(runImageTest(false, true, false));
			out.print("\t");
			blue.println(runImageTest(true, true, false) );
			
			out.print("false\ttrue\t");
			err.print(runImageTest(false, false, true));
			out.print("\t");
			blue.println(runImageTest(true, false, true) );
			
			out.print("false\tfalse\t");
			err.print(runImageTest(false, false, false));
			out.print("\t");
			blue.println( runImageTest(true, false, false) );
			
			out.print("\nPrimitive\t");
			err.print("Construction (ms)");
			out.print("\t");
			blue.println("ResourcePool (ms)");
			
			out.print("int[]\t");
			err.print(runIntTest(false));
			out.print("\t");
			blue.println(runIntTest(true));
			
			out.print("float[]\t");
			err.print(runFloatTest(false));
			out.print("\t");
			blue.println(runFloatTest(true));
			
			out.print("long[]\t");
			err.print(runLongTest(false));
			out.print("\t");
			blue.println(runLongTest(true));
			
			out.print("double[]\t");
			err.print(runDoubleTest(false));
			out.print("\t");
			blue.println(runDoubleTest(true));
			
			runScalingLongTest(out, err, blue);
			out.println("\nDone.");
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	protected String runImageTest(boolean useCache,boolean paint,boolean clearImage) {
		long[] times = new long[20];
		for(int a = 0; a<times.length; a++) {
			long start = System.currentTimeMillis();
			for(int b = 0; b<50; b++) {
				BufferedImage bi;
				if(useCache) {
					bi = ResourcePool.get().getImage(1000, 1000, BufferedImage.TYPE_INT_ARGB, clearImage);
				} else {
					bi = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
				}
				if(paint) {
					Graphics2D g = bi.createGraphics();
					g.setPaint(new Color(255,0,0,100));
					g.fillRect(0,0,1000,1000);
					g.dispose();
				}
				if(useCache) {
					ResourcePool.get().put(bi);
				}
			}
			times[a] = System.currentTimeMillis()-start;
		}
		Arrays.sort(times);
		return times[times.length/2]+"";
	}
	
	protected String runIntTest(boolean useCache) {
		long[] times = new long[20];
		int arraySize = 1000000;
		for(int a = 0; a<times.length; a++) {
			long start = System.currentTimeMillis();
			for(int b = 0; b<50; b++) {
				int[] array;
				if(useCache) {
					array = ResourcePool.get().getIntArray(arraySize);
				} else {
					array = new int[arraySize];
				}
				//do something--anything--to make 100% sure
				//compilers don't discard this unused array
				array[arraySize/2] = 1;
				if(useCache) {
					ResourcePool.get().put(array);
				}
			}
			times[a] = System.currentTimeMillis()-start;
		}
		Arrays.sort(times);
		return times[times.length/2]+"";
	}
	
	protected String runFloatTest(boolean useCache) {
		long[] times = new long[20];
		int arraySize = 1000000;
		for(int a = 0; a<times.length; a++) {
			long start = System.currentTimeMillis();
			for(int b = 0; b<50; b++) {
				float[] array;
				if(useCache) {
					array = ResourcePool.get().getFloatArray(arraySize);
				} else {
					array = new float[arraySize];
				}
				array[arraySize/2] = 1;
				if(useCache) {
					ResourcePool.get().put(array);
				}
			}
			times[a] = System.currentTimeMillis()-start;
		}
		Arrays.sort(times);
		return times[times.length/2]+"";
	}
	
	protected String runLongTest(boolean useCache) {
		long[] times = new long[20];
		int arraySize = 1000000;
		for(int a = 0; a<times.length; a++) {
			long start = System.currentTimeMillis();
			for(int b = 0; b<50; b++) {
				long[] array;
				if(useCache) {
					array = ResourcePool.get().getLongArray(arraySize);
				} else {
					array = new long[arraySize];
				}
				array[arraySize/2] = 1;
				if(useCache) {
					ResourcePool.get().put(array);
				}
			}
			times[a] = System.currentTimeMillis()-start;
		}
		Arrays.sort(times);
		return times[times.length/2]+"";
	}
	
	protected void runScalingLongTest(PrintStream out,PrintStream err,PrintStream blue) {
		long[] times = new long[20];
		out.println("\nScaling Test:");
		out.print("Array Size\t");
		blue.print("ResourcePool");
		out.print("\t");
		err.print("Construction");
		out.println("\tPercent");
		for(int arraySize = 10; arraySize<=1000000; arraySize*=10) {
			out.print(""+arraySize+"\t");
			int loopCount = 50 * 1000000 / arraySize;
			for(int a = 0; a<times.length; a++) {
				long start = System.currentTimeMillis();
				for(int b = 0; b<loopCount; b++) {
					long[] array = ResourcePool.get().getLongArray(arraySize);
					array[arraySize/2] = 1;
					ResourcePool.get().put(array);
				}
				times[a] = System.currentTimeMillis()-start;
			}
			Arrays.sort(times);
			double poolTime = times[times.length/2];
			blue.print(poolTime);
			out.print("\t");
			for(int a = 0; a<times.length; a++) {
				long start = System.currentTimeMillis();
				for(int b = 0; b<loopCount; b++) {
					long[] array = new long[arraySize];
					array[arraySize/2] = 1;
				}
				times[a] = System.currentTimeMillis()-start;
			}
			Arrays.sort(times);
			double constructionTime = times[times.length/2];
			err.print(constructionTime);
			double fraction = poolTime / constructionTime * 100;
			out.println("\t"+(new DecimalFormat("#.##")).format(fraction)+"%");
		}
	}
	
	protected String runDoubleTest(boolean useCache) {
		long[] times = new long[20];
		int arraySize = 1000000;
		for(int a = 0; a<times.length; a++) {
			long start = System.currentTimeMillis();
			for(int b = 0; b<50; b++) {
				double[] array;
				if(useCache) {
					array = ResourcePool.get().getDoubleArray(arraySize);
				} else {
					array = new double[arraySize];
				}
				array[arraySize/2] = 1;
				if(useCache) {
					ResourcePool.get().put(array);
				}
			}
			times[a] = System.currentTimeMillis()-start;
		}
		Arrays.sort(times);
		return times[times.length/2]+"";
	}
}