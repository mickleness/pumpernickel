package com.pump.io;


/** A series of ByteEncoders linked together.
 * <p>As an abstract example: consider a series of translators.
 * [ German->French, French->English, English->Chinese].
 *  By inputting German into this model, you would eventually
 *  receive Chinese.
 */
public class ChainedByteEncoder extends ByteEncoder {
	ByteEncoder[] encoders;	

	public ChainedByteEncoder(ByteEncoder... encoders) {
		this.encoders = encoders;
	}

	public synchronized void addEncoders(ByteEncoder... newEncoders) {
		ByteEncoder[] copy = new ByteEncoder[encoders.length + newEncoders.length];
		System.arraycopy(encoders, 0, copy, 0, encoders.length);
		System.arraycopy(newEncoders, 0, copy, encoders.length, newEncoders.length);
		encoders = copy;
	}

	@Override
	public synchronized void push(int b) {
		//TODO: make non-recursive implementation
		push(encoders.length-1, b);
	}
	
	protected void push(int encoderIndex,int data) {
		encoders[encoderIndex].push(data);
		
		int[] newChunk = encoders[encoderIndex].pullImmediately();
		if(newChunk!=null && newChunk.length>0) {
			if(encoderIndex==0) {
				pushChunk(newChunk);
			} else {
				for(int a = 0; a<newChunk.length; a++) {
					push(encoderIndex-1, newChunk[a]);
				}
			}
		}
	}

	@Override
	protected void flush() {
		int i = encoders.length-1;
		while(i>=0) {
			int[] data = encoders[i].pullImmediately();
			if(i>0) {
				for(int a = 0; a<data.length; a++) {
					push(i-1, data[a]);
				}
			} else {
				pushChunk(data);
			}
			encoders[i].close();
			data = encoders[i].pullImmediately();
			if(i>0) {
				for(int a = 0; data!=null && a<data.length; a++) {
					push(i-1, data[a]);
				}
			} else {
				pushChunk(data);
			}
			i--;
		}
	}
}
