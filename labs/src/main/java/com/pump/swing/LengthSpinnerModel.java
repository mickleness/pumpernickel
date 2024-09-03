/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.swing;

import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractSpinnerModel;

import com.pump.math.Length;

/**
 * This is the SpinnerModel used for the <code>LengthSpinner</code>.
 * <P>
 * The minimum, maximum and step size this model uses can change depending on
 * the unit that this model is currently using.
 * <P>
 * The step size is the most obvious example: if the user has type "6 in", then
 * it makes sense for the step size to be in inches. But then if they type
 * "5 cm", you want the step size to switch to centimeters.
 * <P>
 * Likewise it's possible that the min and max should also change to present
 * nice, round numbers.
 * <P>
 * These flexible values are provide in Hashtables using a key of Length.Unit
 * and a value of Length. If a table doesn't contain a value for the current
 * unit, then the value for the default unit is used. The constructors will
 * throw an exception if the hashtables don't contain a key/value pair for the
 * default unit.
 * <P>
 * The <code>ChangeListeners</code> in this model fire when either the value
 * changes or the unit changes.
 */
class LengthSpinnerModel extends AbstractSpinnerModel {

	private final Length.Unit defaultUnit;
	Length v;
	private Map<Length.Unit, Length> min, max, incr;

	/**
	 * Creates a new SpinnerLengthModel.
	 * 
	 * @param value
	 *            the initial value, in the default unit.
	 * @param minimum
	 *            a table of minimum values. Each key is a
	 *            <code>Length.Unit</code>, and each value is a
	 *            <code>Length</code>. At the minimum this table must have an
	 *            entry for the default unit.
	 * @param maximum
	 *            a table of maximum values. Each key is a
	 *            <code>Length.Unit</code>, and each value is a
	 *            <code>Length</code>. At the minimum this table must have an
	 *            entry for the default unit.
	 * @param stepSize
	 *            a table of step size values. Each key is a
	 *            <code>Length.Unit</code>, and each value is a
	 *            <code>Length</code>. At the minimum this table must have an
	 *            entry for the default unit.
	 * @param unit
	 *            the default unit.
	 */
	public LengthSpinnerModel(double value, Map<Length.Unit, Length> minimum,
			Map<Length.Unit, Length> maximum,
			Map<Length.Unit, Length> stepSize, Length.Unit unit) {
		v = new Length(value, unit);
		min = minimum;
		max = maximum;
		incr = stepSize;
		defaultUnit = unit;

		if (min.containsKey(defaultUnit) == false)
			throw new IllegalArgumentException(
					"the minimum table must define the default unit");
		if (max.containsKey(defaultUnit) == false)
			throw new IllegalArgumentException(
					"the maximum table must define the default unit");
		if (incr.containsKey(defaultUnit) == false)
			throw new IllegalArgumentException(
					"the stepSize table must define the default unit");
	}

	/** Returns the default unit this model was defined with. */
	public Length.Unit getDefaultUnit() {
		return defaultUnit;
	}

	/** Returns the current unit this model uses. */
	public Length.Unit getCurrentUnit() {
		return v.getUnit();
	}

	private Length getValue(Map<Length.Unit, Length> table) {
		Length l = table.get(v.getUnit());
		if (l != null) {
			return l;
		}
		l = table.get(defaultUnit);
		if (l == null) {
			// I suppose this is remotely possible since
			// we don't clone the Hashtables when we construct
			// this object? Come on people: don't make me
			// clone everything.
			throw new NullPointerException();
		}
		return l;
	}

	/**
	 * Returns the minimum of this model in the current unit. (If there is no
	 * minimum defined for the current unit, the minimum for the default unit is
	 * used.)
	 */
	public Length getMinimum() {
		return getValue(min);
	}

	/** Iterates over all the minimums defined for this object. */
	Iterator<Length> getMinimums() {
		return min.values().iterator();
	}

	/** Iterates over all the maximums defined for this object. */
	Iterator<Length> getMaximums() {
		return max.values().iterator();
	}

	/**
	 * Returns the maximum of this model in the current unit. (If there is no
	 * maximum defined for the current unit, the maximum for the default unit is
	 * used.)
	 */
	public Length getMaximum() {
		return getValue(max);
	}

	/**
	 * Returns the next value of this model in the current unit. (If there is no
	 * step size defined for the current unit, the step size for the default
	 * unit is used.)
	 */
	public Object getNextValue() {
		Length newValue = new Length(v);
		newValue.add(getValue(incr));
		return proof(newValue);
	}

	/**
	 * Make sure we're within [min,max]
	 * 
	 * @param newValue
	 * @return
	 */
	private Length proof(Length value) {
		Length newValue = value;

		Length minimum = getValue(min);
		Length maximum = getValue(max);

		if (newValue.compareTo(minimum) < 0)
			newValue = new Length(minimum);
		if (newValue.compareTo(maximum) > 0)
			newValue = new Length(maximum);

		newValue.setUnit(value.getUnit());

		return newValue;
	}

	/**
	 * Returns the previous value of this model in the current unit. (If there
	 * is no step size defined for the current unit, the step size for the
	 * default unit is used.)
	 */
	public Object getPreviousValue() {
		Length newValue = new Length(v);
		newValue.subtract(getValue(incr));
		return proof(newValue);
	}

	/** Returns the current value. */
	public Object getValue() {
		return new Length(v);
	}

	/**
	 * Changes the unit of this model without changing the abstract length. This
	 * will fire the <code>ChangeListeners</code> if the unit changes.
	 * 
	 * @param u
	 *            the new unit to use.
	 */
	public void setUnit(Length.Unit u) {
		if (v.getUnit().equals(u))
			return;

		double length = v.getValue(u);
		Length newV = new Length(length, u);
		setValue(newV);
	}

	/**
	 * Changes the value of this model. The <code>ChangeListeners</code> are
	 * notified if the length changes or if the unit changes.
	 */
	public void setValue(Object obj) {
		if (!(obj instanceof Length))
			throw new IllegalArgumentException("Length object required. ("
					+ obj.getClass().getName() + ")");

		Length newValue = (Length) obj;
		newValue = proof(newValue);

		if (v.equals(newValue) && v.getUnit().equals(newValue.getUnit()))
			return;

		v = new Length(newValue);
		fireStateChanged();
	}
}