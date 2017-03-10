package com.pump.data.scrambler;

public abstract class ScramblerMarkerRule {
	
	public static class Fixed extends ScramblerMarkerRule {
		int marker;
		
		public Fixed(int marker) {
			this.marker = marker;
		}
		
		public boolean isMarker(int i) {
			return i==marker;
		}
	}

	public static class OneCount extends ScramblerMarkerRule {
		boolean[] bytes = new boolean[256];
		
		public OneCount(int oneCount) {
			if(oneCount<0 || oneCount>8)
				throw new IllegalArgumentException("The argument ("+oneCount+") should be between 0 and 8.");
			for(int a = 0; a<bytes.length; a++) {
				bytes[a] = getOneCount(a)==oneCount;
			}
		}
		
		public static int getOneCount(int i) {
			if(i<0 || i>255)
				throw new IllegalArgumentException("The argument ("+i+") must be between [0, 255].");
			
			int sum = 0;
			for(int a = 0; a<8; a++) {
				int j = i >> a;
				if(j%2==1)
					sum++;
			}
			return sum;
		}

		public boolean isMarker(int i) {
			return bytes[i];
		}
	}
	
	public abstract boolean isMarker(int i);
}
