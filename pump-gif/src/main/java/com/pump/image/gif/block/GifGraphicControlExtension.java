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
package com.pump.image.gif.block;

/**
 * This optional block may precede any
 * {@link com.bric.image.gif.block.GifGraphicRenderingBlock}.
 * <P>
 * The four static disposal modes are described below. The GIF file format
 * describes these, but the following webpage describes them much
 * more thoroughly: <BR>
 * http://www.webreference.com/content/studio/disposal.html
 * <P>
 * This is what the GIF file format says:
 * <P>
 * "The Graphic Control Extension contains parameters used when processing a
 * graphic rendering block. The scope of this extension is the first graphic
 * rendering block to follow.
 * <P>
 * This block is OPTIONAL; at most one Graphic Control Extension may precede a
 * graphic rendering block. This is the only limit to the number of Graphic
 * Control Extensions that may be contained in a Data Stream."
 */
public class GifGraphicControlExtension extends GifExtensionBlock {
	/** The disposal methods available for each frame of a gif. */
	public static enum DisposalMethod {
		/**
		 * Use this option to replace one full-size, non-transparent frame with
		 * another.
		 * <P>
		 * (This description copied from
		 * {@code http://www.webreference.com/content/studio/disposal.html })
		 * <P>
		 * That site was generally very useful, but when I followed this
		 * implementation I found at least one GIF that didn't load correctly. This
		 * is a very cryptic disposal mode. The GIF file format specification
		 * explains this as: "No disposal specified. The decoder is not required to
		 * take any action." So it sounds like a wild guess -- at best -- to me. I
		 * end up treating it mostly like <code>DISPOSAL_MODE_LEAVE</code>, and
		 * that seems to yield better results.
		 */
		NONE(0), 
		
		/**
		 * In this option, any pixels not covered up by the next frame continue to
		 * display. This is the setting used most often for optimized animations.
		 * <P>
		 * (This description copied from
		 * {@code http://www.webreference.com/content/studio/disposal.html })
		 * <P>
		 * Or the GIF file format specification explains this as: "Do not dispose.
		 * The graphic is to be left in place."
		 */
		LEAVE(1), 
		
		/**
		 * The background color or background tile - rather than a previous frame -
		 * shows through transparent pixels. In the GIF specification, you can set a
		 * background color.
		 * <P>
		 * (This description copied from {code
		 * http://www.webreference.com/content/studio/disposal.html })
		 * <P>
		 * Or the GIF file format specification explains this as: "Restore to
		 * background color. The area used by the graphic must be restored to the
		 * background color."
		 * <P>
		 * However of these are misleading/incorrect. There is a very important
		 * exception: if a transparent pixel is defined, and this is the disposal
		 * mode, then the image needs to be made transparent. Note this only applies
		 * to the rectangle a frame defines, so if the gif is 100x100 pixels
		 * but a frame only spans 20x20 pixels: then only those 20x20 pixels are affected.
		 */
		RESTORE_BACKGROUND(2), 

		/**
		 * Restores to the state of a previous, undisposed frame.
		 * <P>
		 * (This description copied from
		 * {@code http://www.webreference.com/content/studio/disposal.html })
		 * <P>
		 * Or the GIF file format specification explains this as: "Restore to
		 * previous. The decoder is required to restore the area overwritten by the
		 * graphic with what was there prior to rendering the graphic."
		 */
		PREVIOUS(3);
		
		/** The numeric encoded value of this disposal method. */
		public final int value;
		
		DisposalMethod(int v) {
			value = v;
		}
		
		/** Return the DisposalMethod that corresponds to a certain constant. */
		public static DisposalMethod valueOf(int c) {
			DisposalMethod[] all = DisposalMethod.values();
			for(DisposalMethod m : all) {
				if(m.value==c)
					return m;
			}
			throw new IllegalArgumentException("unrecognized disposal method: "+c);
		}
	}

	byte[] b;

	protected GifGraphicControlExtension(byte[] b) {
		if (b.length != 4) {
			System.err.println("b.length = " + b.length);
			throw new IllegalArgumentException(
					"There must be exactly 4 bytes of data in a GIF graphic control extension.");
		}
		this.b = b;
	}

	/**
	 * Creates a <code>GifGraphicControlExtension</code>
	 * 
	 * @param delayTime
	 *            the time, in hundredths of a second, this graphic shows
	 * @param disposalMethod
	 *            the disposal method.
	 * @see #setDisposalMethod(DisposalMethod)
	 * @param transparentIndex
	 *            the transparent index of the upcoming graphic. A value of -1
	 *            indicates that there is no transparent index.
	 */
	public GifGraphicControlExtension(int delayTime, DisposalMethod disposalMethod,
			int transparentIndex) {
		b = new byte[4];
		setDelayTime(delayTime);
		setDisposalMethod(disposalMethod);
		setTransparentColorIndex(transparentIndex);
	}

	/**
	 * @return the time, in hundredths of a second, that this graphic should be
	 *         visible.
	 *         <P>
	 *         The concept of a zero-length frame is not really addressed in the
	 *         GIF documentation; in rare cases like this, most renderers make a
	 *         point to render every frame, even if it takes more time than this
	 *         value allows.
	 *         <P>
	 *         I've noticed its often the convention of GIF decoders to force
	 *         GIF frames to display for a small fixed interval, greater than
	 *         1/100 of a second, regardless of this value.
	 */
	public int getDelayTime() {
		return (b[2] & 0xFF) * 256 + (b[1] & 0xFF);
	}

	/**
	 * Assigns the time, in hundredths of a second, that this graphic should be
	 * visible.
	 * 
	 * @param i
	 *            the new delay of this frame. This must be between 0 and 65535.
	 */
	public void setDelayTime(int i) {
		if (i < 0 || i > 65535)
			throw new IllegalArgumentException("The delay (" + i
					+ ") must be between 0 and 65535");

		b[1] = (byte) (i % 256);
		b[2] = (byte) (i / 256);
	}

	/**
	 * @return <code>true</code> if the method
	 *         <code>getTransparentColorIndex()</code> will return a useful
	 *         value.
	 */
	private boolean hasTransparentColorIndex() {
		return ((b[0] & 0xFF) & 0x1) > 0;
	}

	/**
	 * @return one of the four <code>DISPOSAL_MODE</code> values in this
	 *         field. This determines what should happen after a graphic has
	 *         been rendered.
	 */
	public DisposalMethod getDisposalMethod() {
		int i = (b[0] & 0xFF) >> 2;
		return DisposalMethod.valueOf(i & 0x07);
	}

	/**
	 * Sets the disposal method of this graphic.
	 * 
	 * @param m the new disposal method for this graphic.
	 */
	public void setDisposalMethod(DisposalMethod m) {
		int i = m.value;
		if (i > 7 || i < 0)
			throw new IllegalArgumentException(
					"The disposal method must be a value between 0 and 3.  Illegal request to set it to "
							+ i + ".");
		b[0] = (byte) ((b[0] & 0xE3) + (i << 2));
	}

	/**
	 * @return <code>true</code> if this graphic should not proceed to the
	 *         next graphic without waiting for user input.
	 *         <P>
	 *         This is a great idea, but I have never seen this feature
	 *         supported. Ever. So I wouldn't worry about this too much.
	 *         <P>
	 *         The GIF file format specification explains this as follows:
	 *         <P>
	 *         "Indicates whether or not user input is expected before
	 *         continuing. If the flag is set, processing will continue when
	 *         user input is entered. The nature of the User input is determined
	 *         by the application (Carriage Return, Mouse Button Click, etc.).
	 *         <P>
	 *         When a Delay Time is used and the User Input Flag is set,
	 *         processing will continue when user input is received or when the
	 *         delay time expires, whichever occurs first."
	 */
	public boolean isWaitingForUserInput() {
		return (b[0] & 1) > 0;
	}

	/**
	 * Indicates whether this graphic should possibly wait for user input (if
	 * user input comes before the specified delay time). See comments in
	 * <code>isWaitingForUserInput()</code>.
	 * 
	 * @param b
	 *            whether this flag is true or not.
	 */
	public void setWaitingForUserInput(boolean b) {
		int k = 0;
		if (b)
			k = 0x10;
		this.b[0] = (byte) ((this.b[0] & 0xFD) + k);
	}

	/**
	 * @return the index in the active color table that should be treated as
	 *         transparent. More specifically: when this index is encountered, a
	 *         pixel should be skipped, and the next pixel should should
	 *         rendered.
	 *         <P>
	 *         This may return <code>-1</code> if no transparent color index
	 *         is provided.
	 */
	public int getTransparentColorIndex() {
		if (hasTransparentColorIndex() == false)
			return -1;
		return (b[3] & 0xFF);
	}

	/**
	 * If i is non-negative, this assigns the transparent color index of this
	 * graphic to i.
	 * <P>
	 * If i is negative, this "turns off" the transparent color index.
	 * 
	 * @param i
	 *            a value between -1 and 255. A value of -1 means there will be
	 *            no transparent color index in the upcoming graphic.
	 */
	public void setTransparentColorIndex(int i) {
		if (i > 255)
			throw new IllegalArgumentException(
					"The transparent color index cannot be greater than 255.  ("
							+ i + ")");
		if (i < 0) {
			b[0] = (byte) (b[0] & 0xFE);
		} else {
			b[0] = (byte) ((b[0] & 0xFE) + 1);
			b[3] = (byte) i;
		}
	}

	public byte[] getBytes() {
		byte[] d = new byte[b.length + 4];
		d[0] = 0x21;
		d[1] = (byte) 0xF9;
		d[2] = 4;
		d[7] = 0;
		System.arraycopy(b, 0, d, 3, b.length);
		return d;
	}

	public int getByteCount() {
		return b.length + 4;
	}
}