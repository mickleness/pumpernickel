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
package com.pump.awt;

import java.awt.PaintContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;

import com.pump.math.MathG;



/** This is a crude way to cover several pixels.
 * <P>This is created for gradients that have to span from P1 to P2.
 * Pixels that lie beyond P1 or P2 (or the gradient created between them)
 * are replaced with solid colors.
 * <P>However, because this context replaces several pixels it is inefficient.
 * A better solution would be to simply design the correct PaintContext in the
 * beginning.  A hybrid between a GradientPaint and a TexturePaint.  But
 * when I looked at the source code for the TexturePaint: I realized this
 * was a complicated task that I didn't have the time or energy to figure out.
 * Yet.
 * <P>This should only be used with TYPE_INT_ARGB rasters/images.
 */
class CoveredContext implements PaintContext {
	PaintContext context;
	double x1, y1, x2, y2;
	int color1, color2;
	AffineTransform transform;
	
	public CoveredContext(PaintContext context,double x1,double y1,int color1,double x2,double y2,int color2,AffineTransform transform) {
		this.context = context;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color1 = color1;
		this.color2 = color2;
		this.transform = transform;
	}
	
	public void dispose() {
		context.dispose();
	}

	public ColorModel getColorModel() {
		return context.getColorModel();
	}

	static Point2D sharedPoint = new Point2D.Double();
	static SoftReference<int[]> lastArrayRef;
	public Raster getRaster(int x, int y, int w, int h) {
		WritableRaster raster = (WritableRaster)context.getRaster(x, y, w, h);
		
		//Here's the first draft.  It works fine, but it's
		//not that efficient.  It might be more readable than
		//the final draft, though, so I'm leaving it here
		//for posterity.
		/*try {
			int[] data = new int[w];
			AffineTransform inverse = transform.createInverse();
			double dx = x2-x1;
			double dy = y2-y1;
			double angle = Math.atan2(dy,dx);
			double distance = Math.sqrt(dx*dx+dy*dy);
			
			//this transform will swivel everything
			//around so the gradient is expressed
			//vertically from (0,0) to (0,distance)
			AffineTransform tx = new AffineTransform();
			tx.rotate(-angle+Math.PI/2);
			tx.translate(-x1, -y1);
			tx.concatenate(inverse);
			
			double[] matrix = new double[6];
			tx.getMatrix(matrix);
			
			//look at every pixel
			for(int y2 = 0; y2<h; y2++) {
				raster.getDataElements(0, y2, w, 1, data);
				
				for(int x2 = 0; x2<data.length; x2++) {
					//Point2D p = new Point2D.Double(x2+x,y2+y);
					//tx.transform(p, p);
					//double y3 = p.getY();
					
					double x3 = x2+x;
					double y3 = y2+y;
					// apply the transform manually:
					// matrix = { m00 m10 m01 m11 m02 m12 }
					// { matrix[0] matrix[2] matrix[4]
					//   matrix[1] matrix[3] matrix[5] }
					y3 = matrix[1]*x3+matrix[3]*y3+matrix[5];
					if(y3<1) {
						data[x2] = color1;
					} else if(y3>=distance-1) {
						data[x2] = color2;
					}
				}
				
				raster.setDataElements(0, y2, w, 1, data);
			}
		} catch(NoninvertibleTransformException e) {
			return raster;
		}*/
		
		/** TODO: often (at least on my 10.5.8 Mac) this method
		 * is called several times to define a 32x32 area.
		 * The first thing this method should do is calculate
		 * possible intersections and if none are found: immediately
		 * manage our own raster and flood fill it.  Don't
		 * delegate and then overwrite all those pixels.
		 */

		/** Several things should happen here
		 * as we set up the int array "data":
		 * 1.  If possible, we should reuse the
		 * most recent array.  This saves a lot
		 * of memory allocation.
		 * 2.  Also we should reuse the
		 * SoftReference object pointing to that array.
		 * This saves a modest (but noticeable)
		 * amount of memory allocation.  (Basically
		 * we should eliminate constructing anything
		 * in this method if we can help it.)
		 * 3.  However: we should carefully make sure
		 * the static array doesn't get reused
		 * by 2 threads simultaneously.
		 * 
		 * Also, while we're here let's manage
		 * references to "sharedPoint": the static
		 * Point2D we recycle.
		 * 
		 */

		Point2D p = sharedPoint;
		int[] lastArray = null;
		SoftReference<int[]> theReference = null;
		
		synchronized(CoveredContext.class) {
			if(lastArrayRef!=null) {
				lastArray = lastArrayRef.get();
				theReference = lastArrayRef;
				lastArrayRef = null;
			}
			
			if(p==null) {
				p = new Point2D.Double();
			}
			sharedPoint = null;
		}
		
		int[] data;
		
		if(lastArray==null || w>lastArray.length) {
			data = new int[w];
			theReference = new SoftReference<int[]>(data);
		} else {
			data = lastArray;
		}
		

		try {
			p.setLocation(x1, y1);
			transform.transform(p, p);
			double tx1 = p.getX();
			double ty1 = p.getY();
	
			p.setLocation(x2, y2);
			transform.transform(p, p);
			double tx2 = p.getX();
			double ty2 = p.getY();
	
			double dx = x2-x1;
			double dy = y2-y1;
			double angle = Math.atan2(dy,dx)+Math.PI/2;
		
			double cos = Math.cos(angle);
			double sin = Math.sin(angle);
			
			//special case: the gradient is 100% vertical,
			//so we just have entire rows to fill:
			if(Math.abs(sin)<.0000001 && cos>0) {
				int state = 0;
				for(int row = 0; row<h; row++) {
					if(row+y<ty2+1) {
						if(state!=2) {
							state = 2;
							for(int col = 0; col<w; col++) {
								data[col] = color2;
							}
						}
						raster.setDataElements(0, row, w, 1, data);
					} else if(row+y>ty1-1) {
						if(state!=1) {
							state = 1;
							for(int col = 0; col<w; col++) {
								data[col] = color1;
							}
						}
						raster.setDataElements(0, row, w, 1, data);
					}
				}
				return raster;
			} else if(Math.abs(sin)<.0000001 && cos<0) {
				int state = 0;
				for(int row = 0; row<h; row++) {
					if(row+y<ty1+1) {
						if(state!=1) {
							state = 1;
							for(int col = 0; col<w; col++) {
								data[col] = color1;
							}
						}
						raster.setDataElements(0, row, w, 1, data);
					} else if(row+y>ty2-1) {
						if(state!=2) {
							state = 2;
							for(int col = 0; col<w; col++) {
								data[col] = color2;
							}
						}
						raster.setDataElements(0, row, w, 1, data);
					}
				}
				return raster;
			}
			
			//these constants were found by trial and error
			//they reduce antialias artifacts where the tiles join
			//together
			int ky1 = 0;
			int kx1 = 0;
			int ky2 = 0;
			int kx2 = 0;
			if(angle>0) {
				if(angle<Math.PI/4) {
					ky1 = 1;
					ky2 = -1;
				} else if(angle<3*Math.PI/4) {
					kx1 = 1;
					kx2 = -1;
				} else if(angle<Math.PI) {
					ky1 = -1;
					kx1 = 1;
					ky2 = 1;
				} else if(angle<5*Math.PI/4) {
					kx1 = 1;
					ky1 = -1;
					ky2 = 1;
				} else {
					kx2 = 1;
					ky2 = 1;
				}
			} else {
				if(angle>-Math.PI/4) {
					kx1 = 1;
					ky1 = 1;
					ky2 = -1;
				} else {
					kx2 = 1;
				}
			}
	
			for(int row = 0; row<h; row++) {
				raster.getDataElements(0, row, w, 1, data);
				
				double xIntersect1 = cos/sin*(row+y-ty1+ky1)+tx1+kx1;
				double xIntersect2 = cos/sin*(row+y-ty2+ky2)+tx2+kx2;
				
				if(xIntersect1<xIntersect2) {
					int ceil = MathG.ceilInt(xIntersect1-x);
					for(int col = 0; col<ceil && col<w; col++) {
						data[col] = color1;
					}
					for(int col = Math.max(0,(int)xIntersect2-x); col<w; col++) {
						data[col] = color2;
					}
				} else {
					int ceil = MathG.ceilInt(xIntersect2-x);
					for(int col = 0; col<ceil && col<w; col++) {
						data[col] = color2;
					}
					for(int col = Math.max(0,(int)xIntersect1-x); col<w; col++) {
						data[col] = color1;
					}
				}
				
				raster.setDataElements(0, row, w, 1, data);
			}
			return raster;
		} finally {
			lastArrayRef = theReference;
			sharedPoint = p;
		}
	}
}