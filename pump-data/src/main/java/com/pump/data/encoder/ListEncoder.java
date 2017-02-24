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
package com.pump.data.encoder;

import java.util.ArrayList;
import java.util.List;

import com.pump.io.java.JavaEncoding;

public class ListEncoder<S> extends ValueEncoder<List<S>> {

	ValueEncoder<S> elementEncoder;
	
	public ListEncoder(Class<S> elementType) {
		super((Class)List.class);
		
		if(elementType==null)
			throw new NullPointerException();
		elementEncoder = ValueEncoder.getDefaultEncoder(elementType);
		
		if(elementEncoder==null)
			throw new NullPointerException("There is no default encoder for "+elementType.getName()+". Please provide an explicit encoder.");
	}
	
	public ListEncoder(ValueEncoder<S> elementEncoder) {
		super((Class)List.class);
		
		if(elementEncoder==null)
			throw new NullPointerException();
		
		this.elementEncoder = elementEncoder;
	}

	/**
	 * Return the encoder responsible for elements in this list.
	 * 
	 * @return the encoder responsible for elements in this list.
	 */
	public ValueEncoder<S> getElementEncoder() {
		return elementEncoder;
	}
	
	@Override
	public List<S> parse(String str) {
		List<S> returnValue;
		if(elementEncoder.getType().equals(String.class)) {
			returnValue = (List<S>) parseStringList(str);
		} else {
			//TODO: examine more complex values, including nested lists
			//we can searh opened brackets/parentheses, then quotes, then find their closing pairs, etc.
			String[] terms = str.split(",");
			returnValue = new ArrayList<>(terms.length);
			for(int a = 0; a<terms.length; a++) {
				S value = elementEncoder.parse(terms[a]);
				returnValue.add( value );
			}
		}
		
		return returnValue;
	}

	private List<String> parseStringList(String str) {
		List<String> returnValue = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while(i<str.length()) {
			while(i<str.length() && Character.isWhitespace(str.charAt(i))) {
				i++;
			}
			
			if(i>=str.length())
				break;
			
			char ch = str.charAt(i);
			if(ch!='"')
				throw new IllegalArgumentException("Unexpected character at start of phrase: "+ch);
			i++;
			
			readValue : while(true) {
				int j = JavaEncoding.decode(str, i, sb);
				if(str.substring(i, j).equals("\"")) {
					returnValue.add( sb.substring(0, sb.length()-1).toString() );
					sb = new StringBuffer();
					i = j;
					break readValue;
				}
				i = j;
			}

			while(i<str.length() && Character.isWhitespace(str.charAt(i))) {
				i++;
			}
			
			if(i>=str.length())
				break;
			
			ch = str.charAt(i);
			if(ch!=',')
				throw new IllegalArgumentException("Unexpected character after phrase: "+ch);
			i++;
		}
		
		return returnValue;
	}

	@Override
	public String encode(List<S> value) {
		StringBuilder sb = new StringBuilder();
		for(int a = 0; a<value.size(); a++) {
			S element = value.get(a);
			String encodedElement = elementEncoder.encode(element);
			if(sb.length()>0) {
				sb.append(",");
			}
			sb.append(encodedElement);
		}
		return sb.toString();
	}

}