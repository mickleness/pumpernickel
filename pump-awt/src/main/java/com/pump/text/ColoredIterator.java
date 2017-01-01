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
package com.pump.text;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Set;

/** This filters the <code>AttributedCharacterIterator</code> so all text
 * is rendered in one color only.
 *
 */
public class ColoredIterator implements AttributedCharacterIterator {
	AttributedCharacterIterator i;
	Color textColor;
	
	public ColoredIterator(AttributedCharacterIterator aci,Color color) {
		i = aci;
		textColor = color;
	}

	@Override
	public Object clone() {
		return new ColoredIterator( (AttributedCharacterIterator)i.clone(), textColor);
	}

	public char current() {
		return i.current();
	}

	public char first() {
		return i.first();
	}

	public Set<Attribute> getAllAttributeKeys() {
		return i.getAllAttributeKeys();
	}

	public Object getAttribute(Attribute attribute) {
		if(attribute.equals(TextAttribute.FOREGROUND)) {
			return textColor;
		}
		return i.getAttribute(attribute);
	}

	public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() {
		Map<AttributedCharacterIterator.Attribute, Object> map = i.getAttributes();
		if(map.get(TextAttribute.FOREGROUND)!=null) {
			map.put(TextAttribute.FOREGROUND, textColor);
		}
		return map;
	}

	public int getBeginIndex() {
		return i.getBeginIndex();
	}

	public int getEndIndex() {
		return i.getEndIndex();
	}

	public int getIndex() {
		return i.getIndex();
	}

	public int getRunLimit() {
		return i.getRunLimit();
	}

	public int getRunLimit(Attribute attribute) {
		return i.getRunLimit(attribute);
	}

	public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> attributes) {
		return i.getRunLimit(attributes);
	}

	public int getRunStart() {
		return i.getRunStart();
	}

	public int getRunStart(Attribute attribute) {
		return i.getRunStart(attribute);
	}

	public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> attributes) {
		return i.getRunStart(attributes);
	}

	public char last() {
		return i.last();
	}

	public char next() {
		return i.next();
	}

	public char previous() {
		return i.previous();
	}

	public char setIndex(int position) {
		return i.setIndex(position);
	}
	
	
}