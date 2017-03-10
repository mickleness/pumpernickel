package com.pump.data.scrambler;

public interface ScramblerSubstitutionModel {
	
	/** This alters bytes (ranging from [0,255]) so the data is transformed.
	 * This is invoked on bytes inside a run if this Scrambler was constructed
	 * with "allowSubstitutions" set to true.
	 * <p>
	 * The tricky thing about rearranging bytes is the Scrambler class
	 * relies on the number of 1's/0's in a byte, so that number may not
	 * change. For example: the value "00110000" may be transformed
	 * into "00001100", but it can't be transformed into "11001111".
	 * 
	 * @param array the array containing bytes to alter
	 * @param arrayOffset the first element in the array to alter
	 * @param length the number of elements in the array to alter
	 */
	public void applySubstitutions(ScramblerMarkerRule markerRule,int[] array, int arrayOffset, int length);
	
	public ScramblerSubstitutionModel clone();
	
	public ScramblerSubstitutionModel nextLayer();
}