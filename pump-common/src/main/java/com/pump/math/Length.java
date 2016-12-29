/*
 * @(#)Length.java
 *
 * $Date: 2016-01-30 19:07:08 -0500 (Sat, 30 Jan 2016) $
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
package com.pump.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** This is a measure of length.
 * A <code>Length</code> object includes a value and a unit.
 * <P>A set of common units of length are included in this class.
 * 
 */
public class Length implements Comparable<Length> {

	/** The localized strings. */
	private static ResourceBundle strings = ResourceBundle.getBundle("com.bric.math.Length");

	private static Map<String, Unit> unitTable = new HashMap<String, Unit>();
	private static ArrayList<Unit> unitsList = new ArrayList<Unit>();

	/** The unit definition of a kilometer. */
	public static final Unit KILOMETER = new Unit("kilometer", 1000);
	
	/** The unit definition of a meter. */
	public static final Unit METER = new Unit("meter", 1);
	
	/** The unit definition of a decimeter. */
	public static final Unit DECIMETER = new Unit("decimeter", .1);
	
	/** The unit definition of a centimeter. */
	public static final Unit CENTIMETER = new Unit("centimeter", .01);
	
	/** The unit definition of a millimeter. */
	public static final Unit MILLIMETER = new Unit("millimeter", .001);
	
	/** The unit definition of a micrometer. */
	public static final Unit MICROMETER = new Unit("micrometer", .000001);
	
	/** The unit definition of an inch. */
	public static final Unit INCH = new Unit("inch", 0.0254 );
	
	/** The unit definition of a foot. */
	public static final Unit FOOT = new Unit("foot", INCH.conversionFactor*12 );
	
	/** The unit definition of a mile. */
	public static final Unit MILE = new Unit("mile", FOOT.conversionFactor*5280 );
	
	/** The unit definition of a yard. */
	public static final Unit YARD = new Unit("yard", FOOT.conversionFactor*3 );
	
	/** A unit of measurement for length. */
	public static class Unit {
		/** The root name key.  This is used to get a list of
		 * possible names from the localization file.
		 */
		final String nameKey;
		/** The number of meters in 1 unit of this type. */
		final double conversionFactor;

		/** Create a new Unit.
		 * <P>This constructor is private because you need to
		 * update the localization files when you add units, so I
		 * don't want to make creating new Length.Units too easy.
		 * 
		 * @param nameKey used to retrieve names from the localization file.
		 * @param conversionFactor the number of meters in 1 unit.
		 */
		private Unit(String nameKey,double conversionFactor) {
			this.nameKey = nameKey;
			this.conversionFactor = conversionFactor;
			
			String firstName = strings.getString(nameKey);
			unitTable.put(firstName.toLowerCase(), this);

			String abbrName = strings.getString(nameKey+"Abbr");
			unitTable.put(abbrName.toLowerCase(), this);
			
			int ctr = 2;
			String s = null;
			try {
				while( (s = strings.getString(nameKey+ctr))!=null ) {
					unitTable.put(s.toLowerCase(), this);
					ctr++;
				}
			} catch(MissingResourceException e) {
				/** Erg.  I wish there was a method like:
				 * strings.containsKey()
				 * But there isn't.  So short of enumerating through
				 * the keys ourself, we'll just eat this exception and
				 * move on.
				 */
			}
			
			unitsList.add(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Unit))
				return false;
			Unit u = (Unit)obj;
			return u.conversionFactor==conversionFactor;
		}

		@Override
		public int hashCode() {
			return nameKey.hashCode();
		}
		
		public String getAbbreviation() {
			return strings.getString(nameKey+"Abbr");
		}
	}
	
	/** Returns all the available units. */
	public static Unit[] getUnits() {
		return unitsList.toArray(new Unit[unitsList.size()]);
	}
	
	/** The number value of this length. */
	private double v;
	
	/** The units the value is measured in. */
	private Unit u;
	
	/** Create a new <code>Length</code> object.
	 * 
	 * @param v the number value of this length.
	 * @param u the units that value is measured in.
	 */
	public Length(double v,Unit u) {
		this.v = v;
		this.u = u;
	}
	
	/** Clones a <code>Length</code> object. */
	public Length(Length l) {
		this(l.v, l.u);
	}
	
	@Override
	public Object clone() {
		return new Length(this);
	}
	
	/** Changes the unit of this length, but the actual
	 * distance measured remains the same.
	 * (That is, if this obviously previously represented
	 * "100 cm", and you called <code>setUnit(Length.METER)</code>,
	 * then this object now represents "1 m".)
	 */
	public void setUnit(Unit u2) {
		double newValue = getValue(u2);
		u = u2;
		v = newValue;
	}
	
	/** Returns a unit associated with a given name.
	 * <P>This looks for abbreviations and full spellings
	 * of unit names.
	 * 
	 * @param name a name for a unit.  This is not
	 * case sensitive.
	 * @return the unit associated with that name, or
	 * <code>null</code> if no units are found.
	 */
	public static Length.Unit getUnitByName(String name) {
		Unit f = unitTable.get(name.toLowerCase());
		return f;
	}
	
	/** This returns <code>true</code> if two <code>Length</code>
	 * objects measure the same distance, regardless of their units.
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Length))
			return false;
		return compareTo((Length)obj)==0;
	}
	
	/** Returns the numeric value of this distance in the 
	 * unit specified.
	 * @param u the unit to get the value in
	 * @return the numeric value of this distance
	 */
	public double getValue(Unit u) {
		return v*this.u.conversionFactor/u.conversionFactor;
	}
	
	/** Redefine this <code>Length</code> object.
	 */
	public void setValue(double v,Unit u) {
		this.v = v;
		this.u = u;
	}
	
	/** Add the argument to this <code>Length</code> object.
	 * The units of both objects are unchanged.
	 */
	public void add(Length l) {
		this.v += l.getValue(u);
	}

	/** Subtracts the argument from this <code>Length</code> object.
	 * The units of both objects are unchanged.
	 */
	public void subtract(Length l) {
		this.v -= l.getValue(u);
	}
	
	/** Returns the unit of this <code>Length</code> object.
	 */
	public Unit getUnit() {
		return u;
	}

	/** Compares the distance of this <code>Length</code>
	 * object with another.
	 */
	public int compareTo(Length l) {
		double v1 = getValue(METER);
		double v2 = l.getValue(METER);
		if(v1<v2)
			return -1;
		if(v1>v2)
			return 1;
		return 0;
	}
	
	/** Returns the value of this length followed by
	 * the unit abbreviation.  For example: "3 mi" or "2.0 mm".
	 */
	@Override
	public String toString() {
		return v+" "+u.getAbbreviation();
	}
}
