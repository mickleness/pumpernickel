package com.pump.data.scrambler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pump.data.scrambler.Scrambler.MarkerRule;



/**
 * This model assumes you have a fixed set of characters you plan on working with.
 * <p>
 * This is a great fit if you want to obfuscate a registration code (where you are
 * sure you understand the original character set), but it is not appropriate if
 * arbitrary data might be input.
 */
public class CharacterSubstitutionModel implements ScramblerSubstitutionModel {
	Map<Integer, List<Integer>> charMap = new HashMap<>();
	

	public CharacterSubstitutionModel(String chars) {
		this(chars.toCharArray());
	}

	private CharacterSubstitutionModel(CharacterSubstitutionModel other) {
		this.charMap = other.charMap;
	}

	@Override
	public CharacterSubstitutionModel clone() {
		return new CharacterSubstitutionModel(this);
	}
	
	public CharacterSubstitutionModel(char... chars) {
		charMap = new HashMap<>();
		for(int a = 0; a<chars.length; a++) {
			int i = (int)chars[a];
			if(i<0 || i>=256)
				throw new RuntimeException("character index "+a+" ("+chars[a]+") cannot be represented as an int within [0,255].");
			int ones = countOnes(i);
			
			List<Integer> k = charMap.get(ones);
			if(k==null) {
				k = new ArrayList<>();
				charMap.put(ones, k);
			}
			if(!k.contains(i)) {
				k.add(i);
			}
		}
	}
	
	public static int countOnes(int k) {
		String s = Integer.toBinaryString(k);
		int sum = 0;
		for(int a = 0; a<s.length(); a++) {
			if(s.charAt(a)=='1')
				sum++;
		}
		return sum;
	}
	
	protected int[] getSections(int sectionCount,int total) {
		int[] returnValue = new int[sectionCount];
		int charCount = total;
		for(int a = 0; a<returnValue.length; a++) {
			returnValue[a] = charCount / (sectionCount - a);
			charCount -= returnValue[a];
		}
		return returnValue;
	}

	@Override
	public void applySubstitutions(MarkerRule markerRule,int[] array, int arrayOffset, int length) {
		for(int a = 0; a<length; a++) {
			int oldValue = array[arrayOffset + a];
			int ones = countOnes(oldValue);
			
			List<Integer> candidates = charMap.get(ones);
			Integer position = candidates.indexOf(oldValue);
			if(position==-1)
				throw new IllegalArgumentException("The byte "+oldValue+" ("+((char)oldValue)+") was not included in the original characters used to created this CharacterSubstitutionModel.");
			
			
			int newPos = candidates.size() - 1 - position;
			int newValue = candidates.get(newPos);
			if(!markerRule.isMarker(newValue))
				array[arrayOffset + a] = newValue;
		}
	}
}