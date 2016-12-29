/*
 * @(#)PiecewiseFunction.java
 *
 * $Date: 2014-11-08 13:55:43 -0500 (Sat, 08 Nov 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.math.function;

import java.util.HashSet;
import java.util.Iterator;

/** This function pieces several separate functions together.
 * This makes no guarantees about continuity; it is possible this
 * function is discontinuous.
 */
public class PiecewiseFunction implements Function {
	
	/** This creates a <code>PiecewiseFunction</code> that resembles another
	 * function.  This method should be used when the argument <code>f</code>
	 * is normally expensive to calculate, and generally predictable/simple
	 * in curvature.
	 * <p>The pieces of the new <code>PiecewiseFunction</code> are going
	 * to be <code>PolynomialFunctions</code>.
	 * @param f the function to mimic.
	 * @param fDeriv a derivative of <code>f</code>.
	 * @param min the left edge of the domain you're interested in mimicking.
	 * @param max the right edge of the domain you're interested in mimicking.
	 * @param functions the number of functions/partitions the <code>PiecewiseFunction</code> should
	 * use.
	 * @return a function that resembles another function.
	 */
	public static PiecewiseFunction create(Function f,Function fDeriv,double min,double max,int functions) {
		Function[] array = new Function[functions];
		double[] bounds = new double[functions-1];
		for(int a = 0; a<functions; a++) {
			double minX = min+(max-min)*((a))/((functions));
			double maxX = min+(max-min)*((a+1))/((functions));
			
			double minY = f.evaluate(minX);
			double maxY = f.evaluate(maxX);

			if(Double.isNaN(minY) || Double.isNaN(maxY)) {
				throw new IllegalArgumentException("f("+minX+") = "+minY+", f("+maxX+") = "+maxY);
			}

			double minYDeriv = fDeriv.evaluate(minX);
			double maxYDeriv = fDeriv.evaluate(maxX);
			
			if(Double.isNaN(minYDeriv) || Double.isNaN(maxYDeriv) || 
					Double.isInfinite(minYDeriv) || Double.isInfinite(maxYDeriv)) {
				array[a] = f;
			} else {
				try {
					array[a] = PolynomialFunction.createFit(new double[] {minX, maxX},
							new double[] {minY, maxY},
							new double[] {minYDeriv, maxYDeriv});
				} catch(RuntimeException e) {
					System.err.println("a = "+a);
					System.err.println("mixX = "+minX);
					System.err.println("maxX = "+maxX);
					System.err.println("minY = "+minY);
					System.err.println("maxY = "+maxY);
					System.err.println("minYDeriv = "+minYDeriv);
					System.err.println("maxYDeriv = "+maxYDeriv);
					throw e;
				}
			}
			if(a!=0)
				bounds[a-1] = minX;
		}
		return new PiecewiseFunction(array, bounds);
	}
	
	Function[] functions;
	double[] upperBounds;
	
	/** If all our pieces are the same width, we can optimize how we locate
	 * the right piece for a given x value.
	 */
	private double fixedIntervalLength = -1;
	
	/** Creates a new <code>PiecewiseFunction</code>.
	 * 
	 * @param functions
	 * @param upperBounds the right-most edge of functions.  If there
	 * are n-elements in <code>functions</code>, there should be (n-1)
	 * elements in this array.
	 * <P>So if you pass 2 functions [a, b], and this value is the array [k],
	 * then for all values less than k: <code>a</code> will be used.  For
	 * all values greater than k, <code>b</code> will be be used.
	 */
	public PiecewiseFunction(Function[] functions,double[] upperBounds) {
		if(upperBounds.length+1!=functions.length)
			throw new IllegalArgumentException("there should be 1 less upperbounds ("+upperBounds.length+") than functions ("+functions.length+")");
		
		this.functions = new Function[functions.length];
		System.arraycopy(functions,0,this.functions,0,functions.length);
		
		this.upperBounds = new double[upperBounds.length];
		System.arraycopy(upperBounds,0,this.upperBounds,0,upperBounds.length);
		
		if(upperBounds.length>2) {
			double delta = upperBounds[1]-upperBounds[0];
			for(int a = 2; a<upperBounds.length; a++) {
				double k = upperBounds[a]-upperBounds[a-1];
				if(Math.abs(delta-k)>.00000000001) {
					return;
				}
			}
			fixedIntervalLength = delta;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("PiecewiseFunction[ ");
		sb.append(" x=(-inf, "+upperBounds[0]+"] "+functions[0]);
		for(int a = 1; a<upperBounds.length; a++) {
			sb.append(", ");
			sb.append(" x=("+upperBounds[a-1]+", "+upperBounds[a]+"] "+functions[a]);
		}
		sb.append(" x=("+upperBounds[upperBounds.length-1]+", +inf) "+functions[functions.length-1]);
		sb.append(" ]");
		return sb.toString();
	}
	
	public double evaluate(double x) {
		if(fixedIntervalLength>0) {
			double min = upperBounds[0]-fixedIntervalLength;
			double max = upperBounds[upperBounds.length-1]+fixedIntervalLength;
			int index = (int)( (x-min)/(max-min)*functions.length );
			if(index==functions.length)
				index--;
			return functions[index].evaluate(x);
		}
		
		for(int a = 0; a<upperBounds.length; a++) {
			if(x<upperBounds[a])
				return functions[a].evaluate(x);
		}
		return functions[functions.length-1].evaluate(x);
	}
	
	public double[] evaluateInverse(double y) {
		//TODO: there are better data structures to use here
		//I believe the gnu trove project has some sets for doubles...
		HashSet<Double> set = new HashSet<Double>();
		for(int a = 0; a<functions.length; a++) {
			double[] x = functions[a].evaluateInverse(y);
			double minX = a==0 ? Double.MIN_VALUE : upperBounds[a-1];
			double maxX = a==functions.length-1 ? Double.MAX_VALUE : upperBounds[a];
			for(int b = 0; b<x.length; b++) {
				if(x[b]>=minX && x[b]<=maxX) {
					set.add(new Double(x[b]));
				}
			}
		}
		int ctr = 0;
		double[] array = new double[set.size()];
		Iterator<Double> i = set.iterator();
		while(i.hasNext()) {
			array[ctr++] = (i.next()).doubleValue();
		}
		return array;
	}
	
	public Function getFunction(int index) {
		return functions[index];
	}
	
	public int getFunctionCount() {
		return functions.length;
	}
	
	public void setFunction(int index,Function f) {
		functions[index] = f;
	}
}
