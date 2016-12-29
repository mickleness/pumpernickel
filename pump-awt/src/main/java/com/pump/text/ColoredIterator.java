/*
 * @(#)ColoredIterator.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
