package com.pump.awt.serialization;

import java.io.IOException;
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

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

public class AttributedStringSerializationWrapper
		extends AbstractSerializationWrapper<AttributedString> {

	private static final long serialVersionUID = 1L;

	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof AttributedString) {
				AttributedString as = (AttributedString) object;
				return new AttributedStringSerializationWrapper(as);
			}
			return null;
		}
	};

	private static class Run implements Serializable {
		private static final long serialVersionUID = 1L;

		Object value;
		int endIndex;

		Run(Object value, int endIndex) {
			this.value = value;
			this.endIndex = endIndex;
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws IOException {
			out.writeInt(0);
			out.writeObject(value);
			out.writeInt(endIndex);
		}

		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			int version = in.readInt();
			if (version == 0) {
				value = in.readObject();
				endIndex = in.readInt();
			} else {
				throw new IOException(
						"unsupported internal version " + version);
			}
		}

		@Override
		public String toString() {
			return "Run[ " + value + ", " + endIndex + "]";
		}
	}

	protected static final String KEY_STRING = "string";
	protected static final String KEY_RUN_MAP = "runMap";

	public AttributedStringSerializationWrapper(AttributedString as) {
		AttributedCharacterIterator iter = as.getIterator();
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
			AttributedCharacterIterator iter2 = as
					.getIterator(new Attribute[] { attribute });

			List<Run> runs = new ArrayList<>();
			int index = 0;
			while (true) {
				if (iter2.current() == CharacterIterator.DONE)
					break;

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

			for (Run run : runs) {
				for (SerializationFilter filter : AWTSerializationUtils.FILTERS) {
					Object filteredValue = filter.filter(run.value);
					if (filteredValue != null)
						run.value = filteredValue;
				}
			}

			runMap.put(attribute, runs);
		}

		map.put(KEY_STRING, sb.toString());
		map.put(KEY_RUN_MAP, runMap);
	}

	@Override
	public AttributedString create() {
		String str = (String) map.get(KEY_STRING);
		AttributedString returnValue = new AttributedString(str);

		Map<Attribute, List<Run>> runMap = (Map<Attribute, List<Run>>) map
				.get(KEY_RUN_MAP);
		for (Entry<Attribute, List<Run>> entry : runMap.entrySet()) {
			for (Run run : entry.getValue()) {
				if (run.value instanceof SerializationWrapper) {
					run.value = ((SerializationWrapper<?>) run.value).create();
				}
			}

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
