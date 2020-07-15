package com.pump.image.shadow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import com.pump.desktop.logging.SessionLog;
import com.pump.geom.StarPolygon;
import com.pump.geom.TransformUtils;
import com.pump.image.shadow.CompositeShadowRenderer.Combo;

/**
 * This generates the lookup table info for the CompositeShadowRenderer.
 * <p>
 * There's probably a lot that can be optimized here, but this is only intended
 * to be run once to generate the tables. This is not a public class.
 * <p>
 * (Also this generates a lot of files to help visually confirm the results.)
 */
class CompositeShadowRendererGenerator implements Callable<CompositeShadowRendererGenerator.Results> {
	private static GaussianShadowRenderer gaussianRenderer = new GaussianShadowRenderer();
	private static final float SHADOW_OPACITY = .75f;

	
	private static final BufferedImage star = createStar();
	private static final ARGBPixels starPixels = new ARGBPixels(star, true);
	private static final BigDecimal gaussianIncr = BigDecimal.ONE.divide( BigDecimal.valueOf(10L) );
	private static final BigDecimal gaussianMax = BigDecimal.valueOf(100);
	

	// this is arbitrary, the most we can support is 1/255
	private static final BigDecimal fastIncr = BigDecimal.ONE.divide( BigDecimal.valueOf(256) );
	private static final BigDecimal fastMax = BigDecimal.valueOf(40);
	
	public static void main(String[] args) throws Exception {
		SessionLog.initialize("CompositeShadowRendererGenerator", 10);
		
		// I tried multithreading, but that led to memory errors. There's really
		// no hurry for this one-time cost, so single-thread seems simplest for now:
		
			for(BigDecimal gaussianRadius = gaussianIncr; 
					gaussianRadius.compareTo(gaussianMax)<=0; 
					gaussianRadius = gaussianRadius.add(gaussianIncr)) {
				CompositeShadowRendererGenerator generator = new CompositeShadowRendererGenerator(gaussianRadius);
				System.out.println( generator.call().output() );
			}
		
		System.out.println("done");
		System.exit(0);
	}
	
	BigDecimal gaussianRadius;
	BufferedImage gaussianShadowImage;
	
	CompositeShadowRendererGenerator(BigDecimal gaussianRadius) {
		this.gaussianRadius = gaussianRadius;
	}
	
	private static final DecimalFormat format = new DecimalFormat("#.0");
	
	static class Results implements Comparable<Results> {
		long error = Long.MAX_VALUE;
		Combo combo = null;
		ARGBPixels pixels = null;
		BigDecimal value, incr;
		BigDecimal gaussianRadius;
		BufferedImage gaussianShadowImage;
		
		public Results(long error, Combo combo, ARGBPixels pixels, BigDecimal value, BigDecimal incr, BigDecimal gaussianRadius, BufferedImage gaussianShadowImage) {
			this.error = error;
			this.combo = combo;
			this.pixels = pixels;
			this.value = value;
			this.incr = incr;
			this.gaussianRadius = gaussianRadius;
			this.gaussianShadowImage = gaussianShadowImage;
		}


		@Override
		public int compareTo(Results o) {
			return Long.compare(error, o.error);
		}
		
		String output() throws Exception {
			String name = "k" + gaussianRadius;
			ImageIO.write(gaussianShadowImage, "png", new File(name + ".png"));
			ImageIO.write(pixels.createBufferedImage(true), "png",
					new File(name + "-" + combo.sortedRadii + ".png"));
			
			
			StringBuilder sb = new StringBuilder();
			sb.append(format.format(gaussianRadius));
			for(int a = 0; a<combo.sortedRadii.size(); a++) {
				sb.append(","+combo.sortedRadii.get(a));
			}
			
			return sb.toString();
		}
	}
	
	public Results call() {
		try {
			ShadowAttributes attr = new ShadowAttributes(gaussianRadius.floatValue(), SHADOW_OPACITY);
			gaussianShadowImage = gaussianRenderer.createShadow(star, attr);
			
			Results results = getBestFit(fastIncr, fastMax, BigDecimal.ONE);
			while(results.incr.compareTo(fastIncr)>=0) {
				BigDecimal newIncr = results.incr.divide(new BigDecimal(2));
				results = getBestFit(results.value.subtract(results.incr.multiply(BigDecimal.valueOf(4))), 
						results.value.add(results.incr.multiply(BigDecimal.valueOf(4))), newIncr);
			}
			
			return results;
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	

	private Results getBestFit(BigDecimal min, BigDecimal max,
			BigDecimal incr) throws Exception {
		if(min.compareTo(fastIncr)<=0)
			min = fastIncr;
		

		Results bestResults = null;
		for(BigDecimal v1 = min; v1.compareTo(max)<=0; v1 = v1.add(incr)) {
			for(BigDecimal v2 = v1; v2.compareTo(max)<=0; v2 = v2.add(incr)) {
				Combo combo = new Combo(v1.floatValue(), v2.floatValue());
				ARGBPixels fastShadow = combo.createShadow(starPixels, null, SHADOW_OPACITY);
				long error = getError(gaussianShadowImage, fastShadow.createBufferedImage(true));
				Results results = new Results(error, combo, fastShadow, v1, incr, gaussianRadius, gaussianShadowImage);
				if(bestResults==null || results.compareTo(bestResults)<0) {
					bestResults = results;
				}
			}
		}
		
		return bestResults;
	}


	private static long getError(BufferedImage bi1, BufferedImage bi2) {
		long sum = 0;

		int w1 = bi1.getWidth();
		int h1 = bi1.getHeight();
		int w2 = bi2.getWidth();
		int h2 = bi2.getHeight();

		int maxW = Math.max(w1, w2);
		int maxH = Math.max(h1, h2);
		
		BufferedImage bi1_resized = new BufferedImage(maxW, maxH, BufferedImage.TYPE_INT_ARGB);
		BufferedImage bi2_resized = new BufferedImage(maxW, maxH, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = bi1_resized.createGraphics();
		g.drawImage(bi1, bi1_resized.getWidth()/2 - bi1.getWidth()/2,  bi1_resized.getHeight()/2 - bi1.getHeight()/2, null); 
		g.dispose();
		
		g = bi2_resized.createGraphics();
		g.drawImage(bi2, bi2_resized.getWidth()/2 - bi2.getWidth()/2,  bi2_resized.getHeight()/2 - bi2.getHeight()/2, null); 
		g.dispose();

		int[] row1 = new int[maxW];
		int[] row2 = new int[maxW];
		
		for(int y = 0; y<maxH; y++) {
			bi1_resized.getRaster().getDataElements(0, y, maxW, 1, row1);
			bi2_resized.getRaster().getDataElements(0, y, maxW, 1, row2);
			for(int x = 0; x<maxW; x++) {
				int argb1 = row1[x];
				int argb2 = row2[x];
				
				int a1 = (argb1 >> 24) & 0xff;
				int a2 = (argb2 >> 24) & 0xff;
				int error = a1 - a2;
				sum += error * error;
			}
		}
		return sum;
	}

	/**
	 * Create an image of a star. This is the image we test shadows against.
	 */
	private static BufferedImage createStar()  {
		BufferedImage starImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = starImage.createGraphics();
		StarPolygon p = new StarPolygon(1);
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setTransform(TransformUtils.createAffineTransform(p.getBounds2D(), new Rectangle(50,50,900,900)));
		g.fill(p);
		
		// punch a whole in the center:
		g.setComposite(AlphaComposite.Clear);
		AffineTransform tx = AffineTransform.getRotateInstance(.4, 500, 500);
		tx.concatenate( TransformUtils.createAffineTransform(p.getBounds2D(), new Rectangle(300, 300, 400, 400)) );
		g.setTransform(tx);
		g.fill(p);
		
		g.dispose();
		return starImage;
	}
}
