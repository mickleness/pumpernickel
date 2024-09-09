/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.awt.converter;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;
import com.pump.data.converter.ConverterUtils;

/**
 * This is a BeanMapConverter for AttributedStrings.
 */
public class AttributedStringMapConverter
		implements BeanMapConverter<AttributedString> {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * This property defines the unattributed String.
	 */
	public static final Key<String> PROPERTY_STRING = new Key<>(String.class,
			"string");

	/**
	 * This property defines a Map of Runs.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_RUNS = new Key<>(Map.class, "runs");

	@Override
	public Class<AttributedString> getType() {
		return AttributedString.class;
	}

	private static class Run implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		Object value;
		int endIndex;

		Run(Object value, int endIndex) {
			this.value = value;
			this.endIndex = endIndex;
		}

		@Serial
		private void writeObject(java.io.ObjectOutputStream out)
				throws IOException {
			out.writeInt(0);
			out.writeInt(endIndex);
			ConverterUtils.writeObject(out, value);
		}

		@Serial
		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			int version = in.readInt();
			if (version == 0) {
				endIndex = in.readInt();
				value = ConverterUtils.readObject(in);
			} else {
				throw new IOException(
						"unsupported internal version " + version);
			}
		}

		@Override
		public String toString() {
			return "Run[ " + value + ", " + endIndex + "]";
		}

		@Override
		public int hashCode() {
			return endIndex;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Run other))
				return false;
			if (other.endIndex != endIndex)
				return false;
			return ConverterUtils.equals(value, other.value);
		}
	}

	@Override
	public Map<String, Object> createAtoms(AttributedString attrString) {
		AttributedCharacterIterator iter = attrString.getIterator();
		Set<Attribute> allAttributes = iter.getAllAttributeKeys();
		StringBuilder sb = new StringBuilder();
		while (true) {
			char ch = iter.current();
			if (ch == CharacterIterator.DONE)
				break;
			sb.append(ch);
			iter.next();
		}

		Map<Attribute, List<Run>> runMap = new HashMap<>();
		for (Attribute attribute : allAttributes) {
			AttributedCharacterIterator iter2 = attrString
					.getIterator(new Attribute[] { attribute });

			List<Run> runs = new ArrayList<>();
			int index = 0;
			while (iter2.current() != CharacterIterator.DONE) {

				Run lastRun = runs.isEmpty() ? null : runs.get(runs.size() - 1);
				Object value = iter2.getAttribute(attribute);

				if (lastRun != null && Objects.equals(lastRun.value, value)) {
					lastRun.endIndex++;
				} else {
					Run newRun = new Run(value, index);
					runs.add(newRun);
				}

				index++;
				iter2.next();
			}

			runMap.put(attribute, runs);
		}

		Map<String, Object> atoms = new HashMap<>(2);
		PROPERTY_STRING.put(atoms, sb.toString());
		PROPERTY_RUNS.put(atoms, runMap);
		return atoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AttributedString createFromAtoms(Map<String, Object> atoms) {
		String str = PROPERTY_STRING.get(atoms);
		AttributedString returnValue = new AttributedString(str);

		Map<Attribute, List<Run>> runMap = PROPERTY_RUNS.get(atoms);
		for (Entry<Attribute, List<Run>> entry : runMap.entrySet()) {
			int index = -1;
			for (Run run : entry.getValue()) {
				if (run.value != null) {
					returnValue.addAttribute(entry.getKey(), run.value,
							index + 1, run.endIndex + 1);
				}
				index = run.endIndex;
			}
		}

		return returnValue;
	}
}