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
package com.pump.data.branch;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;

public class Revision implements Comparable<Revision>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final Key<Long> KEY_TIMESTAMP = new Key<>(Long.class,
			"timestamp");

	protected Map<String, Object> attributes = new HashMap<>();
	protected Number number;
	@SuppressWarnings("rawtypes")
	protected Branch branch;

	public Revision(@SuppressWarnings("rawtypes") Branch branch, Number number) {
		if (branch == null)
			throw new NullPointerException();
		if (number == null)
			throw new NullPointerException();
		this.number = number;
		this.branch = branch;
		KEY_TIMESTAMP.put(attributes, System.currentTimeMillis());
	}

	@Override
	public Revision clone() {
		Revision r = new Revision(branch, number);
		r.attributes.clear();
		r.attributes.putAll(attributes);
		return r;
	}

	public Number getNumber() {
		return number;
	}

	@Override
	public int compareTo(Revision other) {
		if (branch != other.branch)
			throw new IllegalArgumentException(
					"Revisions from different branches are not comparable. (\""
							+ branch.getName() + "\"!=\""
							+ other.getBranch().getName() + "\".");

		if (number instanceof BigInteger || other.number instanceof BigInteger) {
			BigInteger i1 = getNumberAsBigInteger();
			BigInteger i2 = other.getNumberAsBigInteger();
			return i1.compareTo(i2);
		}
		return Long.compare(number.longValue(), other.number.longValue());
	}

	private BigInteger getNumberAsBigInteger() {
		if (number instanceof BigInteger)
			return (BigInteger) number;
		return BigInteger.valueOf(number.longValue());
	}

	@Override
	public int hashCode() {
		return number.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Revision))
			return false;
		Revision other = (Revision) obj;
		if (other.branch != branch)
			return false;
		int k = compareTo(other);
		if (k != 0)
			return false;
		return attributes.equals(other.attributes);
	}

	public Object get(String attributeName) {
		return attributes.get(attributeName);
	}

	public <T> T get(Key<T> key) {
		return key.get(attributes);
	}

	public <T> T set(Key<T> key, T value) {
		return key.put(attributes, value);
	}

	public Object set(String keyName, Object value) {
		if (value == null) {
			return attributes.remove(keyName);
		} else {
			return attributes.put(keyName, value);
		}
	}

	@Override
	public String toString() {
		return "R" + number;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeObject(attributes);
		out.writeObject(number);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			attributes = (Map<String, Object>) in.readObject();
			number = (Number) in.readObject();
		} else {
			throw new RuntimeException("Unsupported internal version: "
					+ version);
		}
	}

	public Revision increment() {
		Number newNumber;
		if (number instanceof BigInteger) {
			newNumber = ((BigInteger) number).add(BigInteger.ONE);
		} else {
			newNumber = Long.valueOf(number.longValue() + 1);
			if (!(newNumber.longValue() > number.longValue())) {
				newNumber = BigInteger.valueOf(number.longValue());
				newNumber = ((BigInteger) newNumber).add(BigInteger.ONE);
			}
		}
		return new Revision(branch, newNumber);
	}

	/**
	 * Return the branch this revision is associated with.
	 * 
	 * @return the branch this revision is associated with.
	 */
	@SuppressWarnings("rawtypes")
	public Branch getBranch() {
		return branch;
	}
}