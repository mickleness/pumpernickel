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
package com.pump.geom.intersect;

/** This {@link IntersectionIdentifier} uses a 2D binary search
 * to hone in on potential intersections.
 * <P>It breaks curves up according to their critical points,
 * so each curve segment is guaranteed to only move in 1
 * general direction between any two give t values.
 * 
 * TODO warning: this project is buggy. The CurveIntersectionApp can
 * be used to highlight several instances where two cubic curves do not
 * produce intersections (even though the human eye can tell they should).
 * Also sometimes exceptions are thrown. The simplest starting point for these
 * issues is:
 * Curve 1: m 100.0 100.0 c 125.0 125.0 125.0 275.0 100.0 300.0
 * Curve 2: m 113.0 120.0 c 88.0 145.0 88.0 295.0 113.0 320.0
 */
public class BinarySearchIntersectionIdentifier extends IntersectionIdentifier {
	
	/** This calculates the t-values a curve needs to be separated into
	 * to isolate all critical points.  For quadratic curves this may
	 * return a list of 4 items (optionally including one and zero).
	 * 
	 * @param dest the array the list is stored in.
	 * @param includeOneAndZero whether the list should include, and be confined to,
	 * values within [0, 1]
	 * @return the number of items in this list.
	 */
	private static int getQuadraticTimes(double[] dest,double ax,double bx,double cx,double ay,double by,double cy,boolean includeOneAndZero) {
		int size = 0;
		
		double t1 = -bx/(2*ax);
		double t2 = -by/(2*ay);
		
		if(includeOneAndZero) {
			if(t1!=0 && t2!=0)
				dest[size++] = 0;
			if(t1>0 && t1<1) {
				if(t2>0 && t2<1) {
					if(t1<t2) {
						dest[size++] = t1;
						dest[size++] = t2;
					} else if(t1==t2) {
						dest[size++] = t1;
					} else {
						dest[size++] = t2;
						dest[size++] = t1;
					}
				} else {
					dest[size++] = t1;
				}
			} else if(t2>0 && t2<1) {
				dest[size++] = t2;
			}
			
			if(dest[size-1]!=1) {
				dest[size++] = 1;
			}
		} else {
			//include everything:
			if(t1<t2) {
				dest[size++] = t1;
				dest[size++] = t2;
			} else if(t1==t2) {
				dest[size++] = t1;
			} else {
				dest[size++] = t2;
				dest[size++] = t1;
			}
		}
		
		return size;
	}
	
	/** Add an element to a sorted list, returning false if the element already exists.
	 */
	private static boolean add(double[] dest,double value,int pos) {
		/* We could implement a binary search here to identify
		 * the exact position, but this method is only ever used for lists
		 * with at most 6 elements, so I'm not sure it's worth it here.
		 * 
		 * (Don't lecture me about big-O notations: 6 elements means
		 * this method might involve 6 comparisons. A binary search would
		 * 3 divisions and 3 comparisons; surely a division is more expensive
		 * than a comparison?)
		 * 
		 * If this is abstracted to a public static method for general
		 * use it should definitely be converted to a binary search.
		 */
		for(int a = 0; a<pos; a++) {
			if(dest[a]==value)
				return false;
			if(dest[a]>value) {
				System.arraycopy(dest, a, dest, a+1, pos-a);
				dest[a] = value;
				return true;
			}
		}
		dest[pos] = value;
		return true;
	}

	/** This calculates the t-values a curve needs to be separated into
	 * to isolate all critical points.  For cubic curves this may
	 * return a list of 6 items (optionally including one and zero).
	 * 
	 * @param dest the array the list is stored in.
	 * @param includeOneAndZero whether the list should include, and be confined to,
	 * values within [0, 1]
	 * @return the number of items in this list.
	 */
	private static int getCubicTimes(double[] dest,double ax,double bx,double cx,double dx,double ay,double by,double cy,double dy,boolean includeOneAndZero) {
		int size = 0;
		if(includeOneAndZero) {
			size = 2;
			dest[0] = 0;
			dest[1] = 1;
		}
		
		double det = 4*bx*bx-12*ax*cx;
		if(det>0) {
			det = Math.sqrt(det);
			double t = (-2*bx+det)/(6*ax);
			if( ((!includeOneAndZero) || (t>0 && t<1)) && 
					add(dest, t, size))
				size++;

			t = (-2*bx-det)/(6*ax);
			if( ((!includeOneAndZero) || (t>0 && t<1)) && 
					add(dest, t, size))
				size++;
			
		} else if(det==0) {
			double t = (-bx)/(3*ax);

			if( ((!includeOneAndZero) || (t>0 && t<1)) && 
					add(dest, t, size))
				size++;
		}

		det = 4*by*by-12*ay*cy;
		if(det>0) {
			det = Math.sqrt(det);
			double t = (-2*by+det)/(6*ay);
			if( ((!includeOneAndZero) || (t>0 && t<1)) && 
					add(dest, t, size))
				size++;

			t = (-2*by-det)/(6*ay);
			if( ((!includeOneAndZero) || (t>0 && t<1)) && 
					add(dest, t, size))
				size++;
			
		} else if(det==0) {
			double t = (-by)/(3*ay);

			if( ((!includeOneAndZero) || (t>0 && t<1)) && 
					add(dest, t, size))
				size++;
		}
		
		return size;
	}

	/** Checks if the rectangles formed by 2 line segments intersect.
	 * 
	 * @return whether the rectangle (x0, y0)->(x1, y1) intersects the rectangle (x2, y2)->(x3, y3) 
	 */
	private static boolean intersects(double x0,double y0,double x1,double y1,double x2,double y2,double x3,double y3) {
		double minXA, maxXA, minYA, maxYA;
		if(x0 < x1) {
			minXA = x0;
			maxXA = x1;
		} else {
			minXA = x1;
			maxXA = x0;
		}
		if(y0 < y1) {
			minYA = y0;
			maxYA = y1;
		} else {
			minYA = y1;
			maxYA = y0;
		}
		
		double minXB, maxXB, minYB, maxYB;
		if(x2 < x3) {
			minXB = x2;
			maxXB = x3;
		} else {
			minXB = x3;
			maxXB = x2;
		}
		if(y2 < y3) {
			minYB = y2;
			maxYB = y3;
		} else {
			minYB = y3;
			maxYB = y2;
		}
		
		return (maxXA > minXB &&
				maxYA > minYB &&
				minXA < maxXB &&
				minYA < maxYB);
	}

	private static double TOLERANCE = .0000000001;
		
	/** This searches two cubic curves for a possible intersection.
	 * It is essential that the values from [startT, endT] move
	 * in only 1 direction: it is the caller's responsibility to
	 * separate critical points in the curve so this method is
	 * used correctly.
	 * <p>This method is also applied to quadratic curves
	 * by defining the "a" terms as zero. (And likewise lines
	 * can treat the "a" and "b" terms as zero.)
	 * 
	 * @param ax1 the coefficient of the t^3 term in the first x parametric equation.
	 * @param bx1 the coefficient of the t^2 term in the first x parametric equation.
	 * @param cx1 the coefficient of the t^1 term in the first x parametric equation.
	 * @param dx1 the coefficient of the t^0 term in the first x parametric equation.
	 * @param ay1 the coefficient of the t^3 term in the first y parametric equation.
	 * @param by1 the coefficient of the t^2 term in the first y parametric equation.
	 * @param cy1 the coefficient of the t^1 term in the first y parametric equation.
	 * @param dy1 the coefficient of the t^0 term in the first y parametric equation.
	 * @param startT1 the min t-value to use in the first parametric curve.
	 * @param endT1 the max t-value to use in the first parametric curve.
	 * @param ax2 the coefficient of the t^3 term in the second x parametric equation.
	 * @param bx2 the coefficient of the t^2 term in the second x parametric equation.
	 * @param cx2 the coefficient of the t^1 term in the second x parametric equation.
	 * @param dx2 the coefficient of the t^0 term in the second x parametric equation.
	 * @param ay2 the coefficient of the t^3 term in the second y parametric equation.
	 * @param by2 the coefficient of the t^2 term in the second y parametric equation.
	 * @param cy2 the coefficient of the t^1 term in the second y parametric equation.
	 * @param dy2 the coefficient of the t^0 term in the second y parametric equation.
	 * @param startT2 the min t-value to use in the second parametric curve.
	 * @param endT2 the max t-value to use in the second parametric curve.
	 * @param returnType which values to store in the destination array.
	 * @return whether the two curves intersect.
	 */
	private static boolean binarySearch(
			double[] dest,int arrayOffset,
			double ax1, double bx1, double cx1, double dx1,
			double ay1, double by1, double cy1, double dy1, double startT1,double endT1,
			double ax2, double bx2, double cx2, double dx2,
			double ay2, double by2, double cy2, double dy2, double startT2,double endT2,
			Return returnType) {
		
		double startX1 = ((ax1*startT1+bx1)*startT1+cx1)*startT1+dx1;
		double startY1 = ((ay1*startT1+by1)*startT1+cy1)*startT1+dy1;
		double endX1 = ((ax1*endT1+bx1)*endT1+cx1)*endT1+dx1;
		double endY1 = ((ay1*endT1+by1)*endT1+cy1)*endT1+dy1;

		double startX2 = ((ax2*startT2+bx2)*startT2+cx2)*startT2+dx2;
		double startY2 = ((ay2*startT2+by2)*startT2+cy2)*startT2+dy2;
		double endX2 = ((ax2*endT2+bx2)*endT2+cx2)*endT2+dx2;
		double endY2 = ((ay2*endT2+by2)*endT2+cy2)*endT2+dy2;
		
		boolean intersects = intersects( startX1, startY1, endX1, endY1, startX2, startY2, endX2, endY2);
		
		if(!intersects)
			return false;
		
		/** When possible: use an iterative approach to refine t's.
		 * If that's not possible, use a recursive approach to explore
		 * entire quadrants.
		 */
		while(true) {
			double kx1 = startX1-endX1;
			double ky1 = startY1-endY1;
			double kx2 = startX2-endX2;
			double ky2 = startY2-endY2;
			if(kx1<0) kx1 = -kx1;
			if(ky1<0) ky1 = -ky1;
			if(kx2<0) kx2 = -kx2;
			if(ky2<0) ky2 = -ky2;
			
			//can we break each path into halves?
			boolean split1 = kx1>TOLERANCE || ky1>TOLERANCE;
			boolean split2 = kx2>TOLERANCE || ky2>TOLERANCE;
			
			if( (!split1) && (!split2) ) {
				//don't split any more: we've really burrowed deep
				//and it's safe to assume there IS an intersection:
				double t1  = (startT1+endT1)/2;
				switch(returnType) {
				case T1_T2:
					dest[arrayOffset] = t1;
					dest[arrayOffset+1] = (startT2+endT2)/2;
					break;
				case X_Y_T1_T2:
					dest[arrayOffset+2] = t1;
					dest[arrayOffset+3] = (startT2+endT2)/2;
				case X_Y:
					dest[arrayOffset] = ((ax1*t1+bx1)*t1+cx1)*t1+dx1;
					dest[arrayOffset+1] = ((ay1*t1+by1)*t1+cy1)*t1+dy1;
					break;
				}
				return true;
			} else if(split1 && split2) {
				double midT1 = (startT1+endT1)/2;
				double midX1 = ((ax1*midT1+bx1)*midT1+cx1)*midT1+dx1;
				double midY1 = ((ay1*midT1+by1)*midT1+cy1)*midT1+dy1;

				double midT2 = (startT2+endT2)/2;
				double midX2 = ((ax2*midT2+bx2)*midT2+cx2)*midT2+dx2;
				double midY2 = ((ay2*midT2+by2)*midT2+cy2)*midT2+dy2;

				boolean intersects1 = intersects(startX1, startY1, midX1, midY1, startX2, startY2, midX2, midY2);
				boolean intersects2 = intersects(midX1, midY1, endX1, endY1, startX2, startY2, midX2, midY2);
				boolean intersects3 = intersects(startX1, startY1, midX1, midY1, midX2, midY2, endX2, endY2);
				boolean intersects4 = intersects(midX1, midY1, endX1, endY1, midX2, midY2, endX2, endY2);

				if( (intersects1) && (!intersects2) && (!intersects3) && (!intersects4)) {
					endT1 = midT1;
					endX1 = midX1;
					endY1 = midY1;
					endT2 = midT2;
					endX2 = midX2;
					endY2 = midY2;
				} else if( (!intersects1) && (intersects2) && (!intersects3) && (!intersects4)) {
					startT1 = midT1;
					startX1 = midX1;
					startY1 = midY1;
					endT2 = midT2;
					endX2 = midX2;
					endY2 = midY2;
				} else if( (!intersects1) && (!intersects2) && (intersects3) && (!intersects4)) {
					endT1 = midT1;
					endX1 = midX1;
					endY1 = midY1;
					startT2 = midT2;
					startX2 = midX2;
					startY2 = midY2;
				} else if( (!intersects1) && (!intersects2) && (!intersects3) && (intersects4)) {
					startT1 = midT1;
					startX1 = midX1;
					startY1 = midY1;
					startT2 = midT2;
					startX2 = midX2;
					startY2 = midY2;
				} else {
					if(intersects1 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, startT1, midT1,
							ax2, bx2, cx2, dx2,
							ay2, by2, cy2, dy2, startT2, midT2, returnType ) )
						return true;
					
					if(intersects2 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, midT1, endT1,
							ax2, bx2, cx2, dx2, 
							ay2, by2, cy2, dy2, startT2, midT2, returnType ) )
						return true;

					if(intersects3 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, startT1, midT1,
							ax2, bx2, cx2, dx2,
							ay2, by2, cy2, dy2, midT2, endT2, returnType ) )
						return true;
					
					if(intersects4 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, midT1, endT1,
							ax2, bx2, cx2, dx2, 
							ay2, by2, cy2, dy2, midT2, endT2, returnType ) )
						return true;
					
					return false;
				}
			} else if(split1) {
				double midT1 = (startT1+endT1)/2;
				double midX1 = ((ax1*midT1+bx1)*midT1+cx1)*midT1+dx1;
				double midY1 = ((ay1*midT1+by1)*midT1+cy1)*midT1+dy1;
				
				boolean intersects1 = intersects(startX1, startY1, midX1, midY1, startX2, startY2, endX2, endY2);
				boolean intersects2 = intersects(midX1, midY1, endX1, endY1, startX2, startY2, endX2, endY2);

				if( (intersects1) && (!intersects2)) {
					endT1 = midT1;
					endX1 = midX1;
					endY1 = midY1;
				} else if( (!intersects1) && (intersects2)) {
					startT1 = midT1;
					startX1 = midX1;
					startY1 = midY1;
				} else {
					if(intersects1 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, startT1, midT1,
							ax2, bx2, cx2, dx2, 
							ay2, by2, cy2, dy2, startT2, endT2, returnType ) )
						return true;
					
					if(intersects2 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, midT1, endT1,
							ax2, bx2, cx2, dx2, 
							ay2, by2, cy2, dy2, startT2, endT2, returnType ) )
						return true;
					
					return false;
				}
			} else if(split2) {
				double midT2 = (startT2+endT2)/2;
				double midX2 = ((ax2*midT2+bx2)*midT2+cx2)*midT2+dx2;
				double midY2 = ((ay2*midT2+by2)*midT2+cy2)*midT2+dy2;
				
				boolean intersects1 = intersects(startX1, startY1, endX1, endY1, startX2, startY2, midX2, midY2);
				boolean intersects2 = intersects(startX1, startY1, endX1, endY1, midX2, midY2, endX2, endY2);

				if( (intersects1) && (!intersects2)) {
					endT2 = midT2;
					endX2 = midX2;
					endY2 = midY2;
				} else if( (!intersects1) && (intersects2)) {
					startT2 = midT2;
					startX2 = midX2;
					startY2 = midY2;
				} else {
					if(intersects1 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, startT1, endT1,
							ax2, bx2, cx2, dx2, 
							ay2, by2, cy2, dy2, startT2, midT2, returnType ) )
						return true;
					
					if(intersects2 && binarySearch(dest, arrayOffset,
							ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, startT1, endT1,
							ax2, bx2, cx2, dx2, 
							ay2, by2, cy2, dy2, midT2, endT2, returnType ) )
						return true;
					
					return false;
				}
			}
		}
	}
	
	@Override
	public int lineQuadratic(double ax1, double bx1, double ay1, double by1,
			double ax2, double bx2, double cx2, double ay2, double by2,
			double cy2, double[] dest, int offset, Return returnType) {
		double[] array1 = new double[] {0, 1};
		double[] array2 = new double[4];
		
		int size1 = 2;
		int size2 = getQuadraticTimes(array2, ax2, bx2, cx2, ay2, by2, cy2, true);
		

		int returnTypeLength = returnType.getLength();
		int hits = 0;
		for(int i1 = 0; i1<size1-1; i1++) {
			for(int i2 = 0; i2<size2-1; i2++) {
				if(binarySearch(dest, offset+returnTypeLength*hits, 
						0, 0, ax1, bx1, 0, 0, ay1, by1, array1[i1], array1[i1+1], 
						0, ax2, bx2, cx2, 0, ay2, by2, cy2, array2[i2], array2[i2+1], returnType ))
					hits++;
			}
		}
		return hits;
	}

	@Override
	public int lineCubic(double ax1, double bx1, double ay1, double by1,
			double ax2, double bx2, double cx2, double dx2, double ay2,
			double by2, double cy2, double dy2, double[] dest, int offset,
			Return returnType) {
		
		double[] array1 = new double[] {0, 1};
		double[] array2 = new double[6];
		
		int size1 = 2;
		int size2 = getCubicTimes(array2, ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, true);
		

		int returnTypeLength = returnType.getLength();
		int hits = 0;
		for(int i1 = 0; i1<size1-1; i1++) {
			for(int i2 = 0; i2<size2-1; i2++) {
				if(binarySearch(dest, offset+returnTypeLength*hits, 
						0, 0, ax1, bx1, 0, 0, ay1, by1, array1[i1], array1[i1+1], 
						ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, array2[i2], array2[i2+1], returnType ))
					hits++;
			}
		}
		return hits;
	}

	@Override
	public int quadraticQuadratic(double ax1, double bx1, double cx1,
			double ay1, double by1, double cy1, double ax2, double bx2,
			double cx2, double ay2, double by2, double cy2, double[] dest,
			int offset, Return returnType) {
		double[] array1 = new double[4];
		double[] array2 = new double[4];

		int size1 = getQuadraticTimes(array1, ax1, bx1, cx1, ay1, by1, cy1, true);
		int size2 = getQuadraticTimes(array2, ax2, bx2, cx2, ay2, by2, cy2, true);
		

		int returnTypeLength = returnType.getLength();
		int hits = 0;
		for(int i1 = 0; i1<size1-1; i1++) {
			for(int i2 = 0; i2<size2-1; i2++) {
				if(binarySearch(dest, offset+returnTypeLength*hits, 
						0, ax1, bx1, cx1, 0, ay1, by1, cy1, array1[i1], array1[i1+1], 
						0, ax2, bx2, cx2, 0, ay2, by2, cy2, array2[i2], array2[i2+1], returnType ))
					hits++;
			}
		}
		return hits;
	}

	@Override
	public int quadraticCubic(double ax1, double bx1, double cx1, double ay1,
			double by1, double cy1, double ax2, double bx2, double cx2,
			double dx2, double ay2, double by2, double cy2, double dy2,
			double[] dest, int offset, Return returnType) {
		double[] array1 = new double[4];
		double[] array2 = new double[6];

		int size1 = getQuadraticTimes(array1, ax1, bx1, cx1, ay1, by1, cy1, true);
		int size2 = getCubicTimes(array2, ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, true);
		

		int returnTypeLength = returnType.getLength();
		int hits = 0;
		for(int i1 = 0; i1<size1-1; i1++) {
			for(int i2 = 0; i2<size2-1; i2++) {
				if(binarySearch(dest, offset+returnTypeLength*hits, 
						0, ax1, bx1, cx1, 0, ay1, by1, cy1, array1[i1], array1[i1+1], 
						ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, array2[i2], array2[i2+1], returnType ))
					hits++;
			}
		}
		return hits;
	}

	@Override
	public int cubicCubic(double ax1, double bx1, double cx1, double dx1,
			double ay1, double by1, double cy1, double dy1, double ax2,
			double bx2, double cx2, double dx2, double ay2, double by2,
			double cy2, double dy2, double[] dest, int offset, Return returnType) {
		double[] array1 = new double[4];
		double[] array2 = new double[6];

		int size1 = getCubicTimes(array1, ax1, bx1, cx1, dx1, ay1, by1, cy1, dy2, true);
		int size2 = getCubicTimes(array2, ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, true);
		

		int returnTypeLength = returnType.getLength();
		int hits = 0;
		for(int i1 = 0; i1<size1-1; i1++) {
			for(int i2 = 0; i2<size2-1; i2++) {
				if(binarySearch(dest, offset+returnTypeLength*hits, 
						ax1, bx1, cx1, dx1, ay1, by1, cy1, dy1, array1[i1], array1[i1+1], 
						ax2, bx2, cx2, dx2, ay2, by2, cy2, dy2, array2[i2], array2[i2+1], returnType ))
					hits++;
			}
		}
		return hits;
	}
}