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

import java.util.List;

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

	@Override
	public List<S> parse(String str) {
		// TODO Auto-generated method stub
		return null;
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
			//TODO: look for commas, maybe escape chars in embedded value, maybe give special treatment for the STRING encoder
			sb.append(encodedElement);
		}
		return sb.toString();
	}

}