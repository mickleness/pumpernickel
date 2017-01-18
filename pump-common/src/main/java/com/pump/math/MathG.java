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
package com.pump.math;

import com.pump.blog.Blurb;
import com.pump.math.function.Function;
import com.pump.math.function.PiecewiseFunction;
import com.pump.math.function.PolynomialFunction;


/** This provides some alternative implementations of a few methods from
 * the Math class.
 * <P>This class may use approximations with various levels of error.  The "G"
 * in the name stands for "Graphics", because it was originally conceived
 * as a tool to speed up graphics.  When I iterate over every pixel in an image
 * to perform some operation: I don't really need the precision that the Math
 * class offers.
 * <P>Many thanks to Oleg E. for some insights regarding machine error and
 * design.
 * <P>See MathGDemo.java for a set of tests comparing the speed/accuracy
 * of java.lang.Math and com.bric.math.MathG.
 *
 */
@Blurb (
title = "Performance: Studying Math",
releaseDate = "May 2009",
summary = "The <code><a href=\"http://java.sun.com/javase/6/docs/api/java/lang/Math.html\">java.lang.Math</a></code> "+
"class was designed with accuracy in mind.\n"+
"<p>But if you're dealing with graphics: you can probably fudge on some accuracy in exchange for speed.  What methods can be improved?",
article = "http://javagraphics.blogspot.com/2009/05/math-studying-performance.html"
)
public abstract class MathG {

	/** Finds the closest integer that is less than or equal to the argument as a double.
	 * <BR>Warning: do not use an argument greater than 1e10, or less than 1e-10.
	 * @param d the value to calculate the floor of.
	 * @return the closest integer that is less than the argument as a double.
	 */
	public static final double floorDouble(double d) {
		int id = (int)d;
		return d==id || d > 0 ? id : id-1;
	}
	
	/** Finds the closest integer that is less than or equal to the argument as an int.
	 * <BR>Warning: do not use an argument greater than 1e10, or less than 1e-10.
	 * @param d the value to calculate the floor of.
	 * @return the closest integer that is less than the argument as an int.
	 */
	public static final int floorInt(double d) {
		int id = (int)d;
		return d==id || d > 0 ? id : id-1;
	}

	/** Rounds a double to the nearest integer value.
	 * <BR>Warning: do not use an argument greater than 1e10, or less than 1e-10.
	 * @param d the value to round.
	 * @return the closest integer that is less than the argument.
	 */
	public static final int roundInt(double d) {
		int i;
		if(d>=0) {
			i = (int)(d+.5);
		} else {
			i = (int)(d-.5);
		}
		return i;
	}
	
	/** Rounds a double to the nearest integer value.
	 * <BR>Warning: do not use an argument greater than 1e10, or less than 1e-10.
	 * @param d the value to round.
	 * @return the closest integer that is less than the argument as a double.
	 */
	public static final double roundDouble(double d) {
		int i;
		if(d>=0) {
			i = (int)(d+.5);
		} else {
			i = (int)(d-.5);
		}
		return i;
	}
	
	/** Finds the closest integer that is greater than or equal to the argument as an int.
	 * <BR>Warning: do not use an argument greater than 1e10, or less than 1e-10.
	 * @param d the value to calculate the ceil of.
	 * @return the closest integer that is greater than the argument as an int.
	 */
	public static final int ceilInt(double d) {
		int id = (int)d;
		return d==id || d < 0 ? id : -((int)(-d))+1;
	}
	
	/** Finds the closest integer that is greater than or equal to the argument as a double.
	 * <BR>Warning: do not use an argument greater than 1e10, or less than 1e-10.
	 * @param d the value to calculate the ceil of.
	 * @return the closest integer that is greater than the argument as a double.
	 */
	public static final double ceilDouble(double d) {
		int id = (int)d;
		return d==id || d < 0 ? id : -((int)(-d))+1;
	}
	
	private static final double PI = Math.PI;
	private static final double TWO_PI = 2.0*Math.PI;
	private static final double PI_OVER_2 = Math.PI/2.0;
	
	private static Function sinFunction01 = PolynomialFunction.createFit(
			new double[] { 0, Math.PI/2},
			new double[] { Math.sin(0), Math.sin(Math.PI/2)}, 
			new double[] { Math.cos(0), Math.cos(Math.PI/2)} );
	
	private static Function sinFunction00004 = PolynomialFunction.createFit(
			new double[] { 0, Math.PI/4, Math.PI/2},
			new double[] { Math.sin(0), Math.sin(Math.PI/4), Math.sin(Math.PI/2)}, 
			new double[] { Math.cos(0), Math.cos(Math.PI/4), Math.cos(Math.PI/2)} );
	
	private static Function acosFunction;
	
	//define the acosFunction:
	static {
		Function acos = new Function() {
			public double evaluate(double x) {
				return Math.acos(x);
			}
			public double[] evaluateInverse(double y) {
				throw new UnsupportedOperationException();
			}
		};
		Function acosD = new Function() {
			public double evaluate(double x) {
				return -1.0/Math.sqrt(1-x*x);
			}
			public double[] evaluateInverse(double y) {
				throw new UnsupportedOperationException();
			}
		};

		PiecewiseFunction p = PiecewiseFunction.create(acos, acosD, 0, 1, 512);
		p.setFunction(p.getFunctionCount()-1, 
				PiecewiseFunction.create(acos, acosD, 
				1.0-1.0/(p.getFunctionCount()), 
				1, 64));
		acosFunction = p;
	}

	/** Returns an approximate value of the sin(v) that should be
	 * within plus-or-minus .0108 of the value returned by Math.sin().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.sin().
	 * 
	 * @param v
	 * @return an approximate value of sin(v)
	 */
	public static final double sin01(double v) {
		/** This is exactly the same code as sin00004, except it
		 * uses the smaller polynomial.  I avoided refactoring
		 * the code to use a common method in the theory that
		 * avoiding adding a method to the stack trace may
		 * shave off just a tiny bit of performance.  Normally
		 * this would be excessive, but my goal is optimum
		 * performance in really tight loops: every line counts!
		 */
		double finalMultiplier;
		if(v<0) {
			finalMultiplier = -1;
			v = -v;
		} else {
			finalMultiplier = 1;
		}
		
		if(v>1.0E10) {
			if(printedOverflowError==false) {
				printedOverflowError = true;
				System.err.println("Warning: MathG is not designed to estimate the sine of values of 1.0e10.  Math.sin() will be used, which may result in slower performance.");
			}
			return finalMultiplier*Math.sin(v);
		} else if(v<.01) {
			//if we're that small, then y=sin(x) -> y=x
			//sin(.01)-.01 = -1.6666583333574403E-7
			return v*finalMultiplier;
		}
		
		if(v>TWO_PI) {
			//v = v%TWO_PI;
			long m = (long)(v/TWO_PI);
			v = v-m*TWO_PI;
		}
		if(v>PI) {
			v = v-PI;
			finalMultiplier = -finalMultiplier;
		}
		if(v>PI_OVER_2) {
			v = PI-v;
		}
		
		
		double result = sinFunction01.evaluate(v);
		result = result*finalMultiplier;
		
		return result;
	}
	
	/** Controls whether an error message has been printed to
	 * System.err. yet this session regarding calling
	 * calculateSin() on values that are too large.
	 */
	private static boolean printedOverflowError = false;

	/** Returns an approximate value of the cos(v) that should be
	 * within plus-or-minus .0108 of the value returned by Math.cos().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.cos().
	 * 
	 * @param v
	 * @return an approximate value of cos(v)
	 */
	public static final double cos01(double v) {
		if(v>1e10 || v<1e-10)
			return Math.cos(v);
		return sin01(v-PI_OVER_2);
	}

	/** Returns an approximate value of the sin(v) that should be
	 * within plus-or-minus .00004 of the value returned by Math.sin().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.sin().
	 * 
	 * @param v
	 * @return an approximate value of sin(v)
	 */
	public static final double sin00004(double v) {
		double finalMultiplier;
		if(v<0) {
			finalMultiplier = -1;
			v = -v;
		} else {
			finalMultiplier = 1;
		}
		
		if(v<.01) {
			//if we're that small, then y=sin(x) -> y=x
			//sin(.01)-.01 = -1.6666583333574403E-7
			return v*finalMultiplier;
		} else if(v>1.0E10) {
			if(printedOverflowError==false) {
				printedOverflowError = true;
				System.err.println("Warning: MathG is not designed to estimate the sine of values of 1.0e10.  Math.sin() will be used, which may result in slower performance.");
			}
			return finalMultiplier*Math.sin(v);
		}
		
		if(v>TWO_PI) {
			//v = v%TWO_PI;
			long m = (long)(v/TWO_PI);
			v = v-m*TWO_PI;
		}
		if(v>PI) {
			v = v-PI;
			finalMultiplier = -finalMultiplier;
		}
		if(v>PI_OVER_2) {
			v = PI-v;
		}

		double result = sinFunction00004.evaluate(v);
		result = result*finalMultiplier;
		
		return result;
	}
	
	/** Returns an approximate value of the acos(v) that should be
	 * within plus-or-minus .00004 of the value returned by Math.acos().
	 * 
	 * @param v
	 * @return an approximate value of acos(v)
	 */
	public static final double acos(double v) {
		if(v<-1 || v>1) throw new IllegalArgumentException("v ("+v+") must be within [-1,1]");
		if(v<0) {
			v = -v;
			return Math.PI-acos(v);
		}
		return acosFunction.evaluate(v);
	}


	/** Returns an approximate value of the cos(v) that should be
	 * within plus-or-minus .00004 of the value returned by Math.cos().
	 * <P>If the argument is greater in magnitude than 1e10, then
	 * this delegates to Math.cos().
	 * 
	 * @param v
	 * @return an approximate value of cos(v)
	 */
	public static final double cos00004(double v) {
		if(v>1e10 || v<1e-10)
			return Math.cos(v);
		return sin00004(v-PI_OVER_2);
	}
}