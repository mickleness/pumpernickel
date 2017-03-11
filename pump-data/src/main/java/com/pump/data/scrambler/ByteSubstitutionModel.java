package com.pump.data.scrambler;



/**
 * This SubstitutionModel treats all data as bytes.
 * <p>
 * This may make data "ugly", because a letter that has a simple
 * ASCII representation like 'A' might get mutated to strange punctuation
 * or a backspace (ASCII 8).
 */
public class ByteSubstitutionModel implements ScramblerSubstitutionModel {

	private static int[] reverseByteLUT;
	
	private int runCtr = 0;
	
	public ByteSubstitutionModel() {
		if(reverseByteLUT==null) {
			reverseByteLUT = new int[256];
			//this is a little kludgy, but it's a one-time expense:
			for(int a = 0; a<reverseByteLUT.length; a++) {
				String s = Integer.toBinaryString(a);
				while(s.length()<8) {
					s = "0"+s;
				}
				StringBuffer reverse = new StringBuffer();
				for(int b = s.length()-1; b>=0; b--) {
					reverse.append(s.charAt(b));
				}
				reverseByteLUT[a] = Integer.parseInt( reverse.toString(), 2 );
			}
		}
	}
	
	private ByteSubstitutionModel(ByteSubstitutionModel other) {
		this.runCtr = other.runCtr;
	}
	
	@Override
	public ByteSubstitutionModel clone() {
		return new ByteSubstitutionModel(this);
	}
	
	@Override
	public void applySubstitutions(ScramblerMarkerRule markerRule, int[] array, int arrayOffset, int length) {
		
		runCtr = (runCtr+1)%3;
		switch(runCtr) {
			case 0:
				//reverse the middle two bits: 00011000
				for(int a = 0; a<length; a++) {
					int d = array[a+arrayOffset];
					int newValue = (d & 0xE7) + (reverseByteLUT[d] & 0x18);
					if(!markerRule.isMarker(newValue))
						array[a+arrayOffset] = newValue;
				}
				break;
			case 1:
				//reverse the middle four bits: 00111100
				for(int a = 0; a<length; a++) {
					int d = array[a+arrayOffset];
					int newValue = (d & 0xC3) + (reverseByteLUT[d] & 0x3C);
					if(!markerRule.isMarker(newValue))
						array[a+arrayOffset] = newValue;
				}
				break;
			default:
				//reverse the middle six bits: 01111110
				for(int a = 0; a<length; a++) {
					int d = array[a+arrayOffset];
					int newValue = (d & 0x81) + (reverseByteLUT[d] & 0x7E);
					if(!markerRule.isMarker(newValue))
						array[a+arrayOffset] = newValue;
				}
				break;
		}
	}
}