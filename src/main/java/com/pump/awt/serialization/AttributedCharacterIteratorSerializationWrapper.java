package com.pump.awt.serialization;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import com.pump.io.serialization.AbstractSerializationWrapper;
import com.pump.io.serialization.SerializationFilter;
import com.pump.io.serialization.SerializationWrapper;

/**
 * This is a SerializationWrapper for AttributedCharacterIterators.
 */
public class AttributedCharacterIteratorSerializationWrapper
		extends AbstractSerializationWrapper<AttributedCharacterIterator> {

	private static final long serialVersionUID = 1L;

	/**
	 * This filter converts an AttributedCharacterIterator into a
	 * AttributedCharacterIteratorSerializationWrapper.
	 */
	public static SerializationFilter FILTER = new SerializationFilter() {
		@Override
		public SerializationWrapper<?> filter(Object object) {
			if (object instanceof AttributedCharacterIterator) {
				AttributedCharacterIterator iter = (AttributedCharacterIterator) object;
				return new AttributedCharacterIteratorSerializationWrapper(
						iter);
			}
			return null;
		}
	};

	protected static final String KEY_ATTRIBUTED_STRING = "attributedString";
	protected static final String KEY_INDEX = "index";

	public AttributedCharacterIteratorSerializationWrapper(
			AttributedCharacterIterator iter) {
		int index = iter.getIndex();
		try {
			AttributedString as = new AttributedString(iter);
			map.put(KEY_ATTRIBUTED_STRING,
					new AttributedStringSerializationWrapper(as));
			map.put(KEY_INDEX, index);
		} finally {
			iter.setIndex(index);
		}
	}

	@Override
	public AttributedCharacterIterator create() {
		int index = ((Number) map.get(KEY_INDEX)).intValue();
		AttributedStringSerializationWrapper w = (AttributedStringSerializationWrapper) map
				.get(KEY_ATTRIBUTED_STRING);
		AttributedString as = w.create();
		AttributedCharacterIterator iter = as.getIterator();
		iter.setIndex(index);
		return iter;
	}
}
