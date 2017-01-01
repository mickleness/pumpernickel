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
package com.pump.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JPanel;

import com.pump.awt.Dimension2D;
import com.pump.blog.Blurb;
import com.pump.image.pixel.GenericImageSinglePassIterator;
import com.pump.math.MathG;
import com.pump.swing.BasicConsole;
import com.pump.util.JVM;

/** This app uses a large sample JPEG (a few thousand pixels wide)
 * and applies different scaling techniques.
 * <p>The original incentive for this comparison was to test the
 * GenericImageSinglePassIterator class.
 *
 */
@Blurb (
filename = "ScalingJPEGDemo",
title = "Images: Scaling JPEGs and PNGs",
releaseDate = "May 2011",
summary = "Need to scale megapixel JPEGs on-the-fly without loading them entirely into memory? This might just do the trick.",
link = "http://javagraphics.blogspot.com/2011/05/images-scaling-jpegs-and-pngs.html",
sandboxDemo = true
)
public class ScalingJPEGDemo {

	static abstract class ScalingReader {
		PrintStream printStream;
		
		public abstract Image create(Dimension originalSize,Dimension maxSize) throws Throwable;
		public abstract String getName();
	}
	
	public static URL getImageURL() {
		//try changing this to "resources/bridge2.jpg" (a 15000-pixel wide image)
		//and see which ScalingReaders throw memory errors on which platforms.
		return ScalingJPEGDemo.class.getResource("bridge3.jpg");
	}
	
	public static void main(String[] args) {
		BasicConsole console = BasicConsole.create("ScalingJPEGDemo", false, true, true);
		PrintStream out = console.createPrintStream(false);
		PrintStream blue = console.createPrintStream(new Color(0x550000ff,true));
		PrintStream err = console.createPrintStream(true);

		try {
			try {
				Thread.sleep(2000);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			URL url = getImageURL();
			final Dimension imageSize = ImageSize.get(url);
			
			ScalingReader singlePassReader = new ScalingReader() {
				public Image create(Dimension originalSize,Dimension maxSize) throws Throwable {
					URL url = getImageURL();
					return GenericImageSinglePassIterator.createScaledImage( url, maxSize);
				}
				
				public String getName() {
					return "singlePassReader";
				}
			};
			singlePassReader.printStream = blue;
	
			ScalingReader scalingGraphicsReader = new ScalingReader() {
				Component component = new JPanel();
				MediaTracker mediaTracker = new MediaTracker(component);
				public Image create(Dimension originalSize,Dimension maxSize) throws Throwable {
					Dimension newSize = Dimension2D.scaleProportionally(originalSize, maxSize);
	
					URL url = getImageURL();
					Image image = Toolkit.getDefaultToolkit().createImage(url);
					mediaTracker.addImage(image, 0);
					mediaTracker.waitForAll();
					
					BufferedImage thumbnail = new BufferedImage(newSize.width, newSize.height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = thumbnail.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.drawImage(image, 0, 0, newSize.width, newSize.height, null);
					g.dispose();
	
					mediaTracker.removeImage(image);
					image.flush();
	
					return thumbnail;
				}
				
				public String getName() {
					return "scalingGraphicsReader";
				}
			};
	
			ScalingReader transformReader = new ScalingReader() {
				Component component = new JPanel();
				MediaTracker mediaTracker = new MediaTracker(component);
				public Image create(Dimension originalSize,Dimension maxSize) throws Throwable {
					Dimension newSize = Dimension2D.scaleProportionally(originalSize, maxSize);
					
					URL url = getImageURL();
					Image image = Toolkit.getDefaultToolkit().createImage(url);
					mediaTracker.addImage(image, 0);
					mediaTracker.waitForAll();
					
					BufferedImage thumbnail = new BufferedImage(newSize.width, newSize.height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = thumbnail.createGraphics();
					double scale = ((double)newSize.width)/((double)originalSize.width);
					g.scale(scale, scale);
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.drawImage(image, 0, 0, null);
					g.dispose();
	
					mediaTracker.removeImage(image);
					image.flush();
	
					return thumbnail;
				}
				
				public String getName() {
					return "transformReader";
				}
			};
	
			ScalingReader scaledInstanceReader = new ScalingReader() {
				Component component = new JPanel();
				MediaTracker mediaTracker = new MediaTracker(component);
				public Image create(Dimension originalSize,Dimension maxSize) throws Throwable {
					Dimension newSize = Dimension2D.scaleProportionally(originalSize, maxSize);
					
					URL url = getImageURL();
					Image image = Toolkit.getDefaultToolkit().createImage(url);
					mediaTracker.addImage(image, 0);
					mediaTracker.waitForID(0);
					
					Image image2 = image.getScaledInstance(newSize.width, newSize.height, Image.SCALE_SMOOTH);
					mediaTracker.addImage(image2, 1);
					mediaTracker.waitForID(1);
					
					mediaTracker.removeImage(image);
					mediaTracker.removeImage(image2);
					image.flush();
					image2.flush();
	
					return image2;
				}
				
				public String getName() {
					return "scaledInstanceReader";
				}
			};
	
			ScalingReader replicateScaleFilter = new ScalingReader() {
				Component component = new JPanel();
				MediaTracker mediaTracker = new MediaTracker(component);
				public Image create(Dimension originalSize,Dimension maxSize) throws Throwable {
					Dimension newSize = Dimension2D.scaleProportionally(originalSize, maxSize);
					
					URL url = getImageURL();
					Image image = Toolkit.getDefaultToolkit().createImage(url);
					mediaTracker.addImage(image, 0);
					mediaTracker.waitForAll();
					
					ImageProducer producer = image.getSource();
					ImageFilter scalingFilter = new ReplicateScaleFilter(newSize.width, newSize.height);
					ImageProducer scaledProducer = new FilteredImageSource(producer, scalingFilter);
					Image image2 = Toolkit.getDefaultToolkit().createImage(scaledProducer);
					
					mediaTracker.addImage(image2, 1);
					mediaTracker.waitForID(1);
					
					mediaTracker.removeImage(image);
					mediaTracker.removeImage(image2);
					image.flush();
					image2.flush();
					
					return image2;
				}
				
				public String getName() {
					return "replicateScaleFilter";
				}
			};
	
			ScalingReader areaAveragingScaleFilter = new ScalingReader() {
				Component component = new JPanel();
				MediaTracker mediaTracker = new MediaTracker(component);
				public Image create(Dimension originalSize,Dimension maxSize) throws Throwable {
					Dimension newSize = Dimension2D.scaleProportionally(originalSize, maxSize);
					
					URL url = getImageURL();
					Image image = Toolkit.getDefaultToolkit().createImage(url);
					mediaTracker.addImage(image, 0);
					mediaTracker.waitForAll();
					
					ImageProducer producer = image.getSource();
					ImageFilter scalingFilter = new AreaAveragingScaleFilter(newSize.width, newSize.height);
					ImageProducer scaledProducer = new FilteredImageSource(producer, scalingFilter);
					Image image2 = Toolkit.getDefaultToolkit().createImage(scaledProducer);
					
					mediaTracker.addImage(image2, 1);
					mediaTracker.waitForID(1);
					
					mediaTracker.removeImage(image);
					mediaTracker.removeImage(image2);
					image.flush();
					image2.flush();
	
					return image2;
				}
				
				public String getName() {
					return "areaAveragingScaleFilter";
				}
			};
			out.println(JVM.getProfile());
			out.println("\nThis takes an image that is 2750x2063 and uses a variety of methods to scale it:");
			out.println("\nhttps://java.net/svn/javagraphics~svn/trunk/tests/com/bric/image/bridge3.jpg");
			out.println("\nThe \"singlePassReader\" is highlighted in blue, and represents the summary findings of ");
			out.println("this project by using the GenericImageSinglePassIterator.\n");
			out.println("These tests include several 2-second gaps to help the MediaTracker's threads finish, so they");
			out.println("will take well over an hour to finish.\n");
	
			List<ScalingReader> readers = new ArrayList<ScalingReader>();
			readers.add(singlePassReader);
			readers.add(scalingGraphicsReader);
			readers.add(transformReader);
			readers.add(scaledInstanceReader);
			readers.add(replicateScaleFilter);
			readers.add(areaAveragingScaleFilter);
	
			/** Note the following subsampling model isn't really accurate.
			 * If you look at the exported images: the rounding involved
			 * causes some severe problems.
			 * 
			 * Also: as of March of 2015 this reader will cause all readers to show
			 * the error "Too many open files in system".
			 * Apple may have reduced the open file limit, but this is still a scary error.
			 * ( http://superuser.com/questions/433746/is-there-a-fix-for-the-too-many-open-files-in-system-error-on-os-x-10-7-1 )
			 *
			 * Does ImageIO just not close resources correctly? Am I doing something wrong?
			 * Creepy stuff.
			 */
			List<ImageReader> ioReaderList = getIOReaderList();
			for(int a = 0; a<ioReaderList.size(); a++) {
				/** Arg. The first implementation just used the same ImageReader hundreds of times,
				 * but apparently that results in too many open files for the file system to manage.
				 * (Observed on Mac 10.10. This even affected other apps trying to open files.)
				 * 
				 * So this approach called reader.dispose() constantly, which means we have
				 * to access the ImageReader by its index in the list.
				 */
				final int readerIndex = a;
				
				//define the name:
				String n;
				{
					if(ioReaderList.size()==1) {
						n = "imageIOReader";
					} else {
						String s = ioReaderList.get(a).getClass().getName();
						if(s.indexOf('.')!=-1) {
							s = s.substring(s.lastIndexOf('.')+1);
						}
						n = "imageIOReader<"+s+">";
					}
				}
				final String name = n;
				
				ScalingReader imageIOReader = new ScalingReader() {
					public Image create(Dimension originalSize,Dimension maxSize) throws Throwable {
						URL url = getImageURL();
						Dimension newSize = Dimension2D.scaleProportionally(originalSize, maxSize);

						ImageReader reader = getIOReaderList().get(readerIndex);
						try(InputStream in = url.openStream();
							ImageInputStream imageIn = ImageIO.createImageInputStream( in ) ) {
							
							reader.setInput(imageIn);
							int w = reader.getWidth(0);
							int h = reader.getHeight(0);
	
							int sub = 1;
							if (w>h)
								sub = MathG.ceilInt(w/maxSize.getWidth());
							else
								sub = MathG.ceilInt(h/maxSize.getHeight());
	
							BufferedImage bi = new BufferedImage(newSize.width, newSize.height,BufferedImage.TYPE_INT_RGB);
	
							ImageReadParam param = reader.getDefaultReadParam();
							param.setSourceSubsampling(sub,sub,0,0);
							param.setDestination(bi);
							return reader.read(0,param);
						} finally {
							reader.dispose();
						}
					}
				
					public String getName() {
						return name;
					}
				};
				readers.add(imageIOReader);
			};
	
			out.print("size\t");
			for(int a = 0; a<readers.size(); a++) {
				ScalingReader reader = readers.get(a);
				PrintStream ps = reader.printStream==null ? out : reader.printStream;
				ps.print( reader.getName()+"\t" );
			}
			out.println();
			out.print("(pixels)\t");
			for(int a = 0; a<readers.size(); a++) {
				ScalingReader reader = readers.get(a);
				PrintStream ps = reader.printStream==null ? out : reader.printStream;
				ps.print( "(s)\t" );
			}
			out.println();
			
			
			List<Throwable> errors = new ArrayList<Throwable>();
			DecimalFormat format = new DecimalFormat("#.0##");
			Map<String, String> memoryResults = new HashMap<String, String>();
			for(int size = 100; size<=2000; size+=100) {
				Dimension maxSize = new Dimension(size, size);
				out.print(size+"\t");
				for(int a = 0; a<readers.size(); a++) {
					ScalingReader reader = readers.get(a);
					String memoryResultsKey = size+" "+reader.getName();
					try {
						long[] time = new long[5];
						long[] memory = new long[time.length];
						for(int b = 0; b<time.length; b++) {
							time[b] = System.currentTimeMillis();
							memory[b] = Runtime.getRuntime().freeMemory();
							Image thumbnail = reader.create(imageSize, maxSize);
							int w = thumbnail.getWidth(null);
							int h = thumbnail.getHeight(null);
							if(!(w==maxSize.width || h==maxSize.height))
								throw new RuntimeException("thumbnail is not the correct size: "+w+"x"+h);
							time[b] = System.currentTimeMillis()-time[b];
							memory[b] = memory[b] - Runtime.getRuntime().freeMemory();
							thumbnail = null;
							
							/** The AWT "Image Fetcher" threads need some time
							 * to die.  And while we're waiting: let's occasionally
							 * run gc.
							 * 
							 * (When I only called finalization/gc once with no
							 * sleeping: my memory results had HUGE variations.
							 * Adding this pause and the repeated gc calls
							 * made the performance numbers much more linear/explainable.
							 * 
							 */
							for(int c = 0; c<5; c++) {
								Thread.sleep(2000);
								Runtime.getRuntime().runFinalization();
								Runtime.getRuntime().gc();
							}
						}
						Arrays.sort(time);
						Arrays.sort(memory);
						double medianTime = time[time.length/2];
						medianTime = medianTime/1000.0;
						PrintStream ps = reader.printStream;
						if(ps==null) ps = out;
						ps.print(format.format(medianTime)+"\t");
						double medianMemory = memory[memory.length/2];
						medianMemory = medianMemory/1024.0/1024.0;
						memoryResults.put(memoryResultsKey, format.format(medianMemory));
					} catch(Throwable t) {
						errors.add(t);
						err.print("error-"+(errors.size())+"\t");
						memoryResults.put(memoryResultsKey, "error-"+(errors.size()));
					}
				}
				out.println();
			}
			
			//print the memory data
			out.println("\n");

			out.print("size\t");
			for(int a = 0; a<readers.size(); a++) {
				ScalingReader reader = readers.get(a);
				PrintStream ps = reader.printStream==null ? out : reader.printStream;
				ps.print( reader.getName()+"\t" );
			}
			out.println();
			out.print("(pixels)\t");
			for(int a = 0; a<readers.size(); a++) {
				ScalingReader reader = readers.get(a);
				PrintStream ps = reader.printStream==null ? out : reader.printStream;
				ps.print( "(MB)\t" );
			}
			out.println();
			
			for(int size = 100; size<=2000; size+=100) {
				out.print(size+"\t");
				for(int a = 0; a<readers.size(); a++) {
					ScalingReader reader = readers.get(a);
					PrintStream ps = reader.printStream;
					if(ps==null) ps = out;
					String memoryResultsKey = size+" "+reader.getName();
					String results = memoryResults.get(memoryResultsKey);
					if("error".equals(results))
						ps = err;
					ps.print(results+"\t");
					
				}
				out.println();
			}
			
			for(int a = 0; a<errors.size(); a++) {
				err.println("\nerror #"+(a+1)+":");
				errors.get(a).printStackTrace(err);
			}
			
		} catch(Exception e) {
			e.printStackTrace(err);
		}
	}
	
	/** Return a list of all ImageReaders associated with jpegs.
	 */
	private static List<ImageReader> getIOReaderList() {
		Iterator<ImageReader> ioReaders = ImageIO.getImageReadersByFormatName("jpeg");
		final List<ImageReader> ioReaderList = new ArrayList<>();
		while(ioReaders.hasNext()) {
			ImageReader reader = ioReaders.next();
			ioReaderList.add(reader);
		}
		return ioReaderList;
	}
}