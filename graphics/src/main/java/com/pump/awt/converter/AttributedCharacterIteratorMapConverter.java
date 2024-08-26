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
package com.pump.awt.converter;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for AttributedCharacterIterators.
 */
public class AttributedCharacterIteratorMapConverter
		implements BeanMapConverter<AttributedCharacterIterator> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines atoms to create an AttributedString. See
	 * AttributedStringMapConverter.
	 */
	@SuppressWarnings("rawtypes")
	public static final Key<Map> PROPERTY_ATTRIBUTED_STRING = new Key<>(
			Map.class, "attributed-string");

	/**
	 * This property defines the current index of the iterator.
	 */
	public static final Key<Integer> PROPERTY_ITERATOR_INDEX = new Key<>(
			Integer.class, "iterator-index");

	@Override
	public Class<AttributedCharacterIterator> getType() {
		return AttributedCharacterIterator.class;
	}

	@Override
	public Map<String, Object> createAtoms(AttributedCharacterIterator iter) {
		int index = iter.getIndex();
		try {
			AttributedString as = new AttributedString(iter);
			Map<String, Object> atoms = new HashMap<>(2);
			PROPERTY_ATTRIBUTED_STRING.put(atoms,
					new AttributedStringMapConverter().createAtoms(as));
			PROPERTY_ITERATOR_INDEX.put(atoms, index);
			return atoms;
		} finally {
			iter.setIndex(index);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public AttributedCharacterIterator createFromAtoms(
			Map<String, Object> atoms) {
		Map<String, Object> attributedStringAtoms = PROPERTY_ATTRIBUTED_STRING
				.get(atoms);
		int index = PROPERTY_ITERATOR_INDEX.get(atoms);
		AttributedString as = new AttributedStringMapConverter()
				.createFromAtoms(attributedStringAtoms);
		AttributedCharacterIterator iter = as.getIterator();
		iter.setIndex(index);
		return iter;
	}
}