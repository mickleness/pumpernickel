/*
 * @(#)GifApplicationExtension.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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
package com.pump.image.gif.block;

import java.util.Arrays;

/**
 * This block contains application-specific information.
 * <P>
 * The only use I know of this block is the Netscape block that is used for
 * looping.
 */
public class GifApplicationExtension extends GifExtensionBlock {
	byte[] b;
	byte[] data;

	/**
	 * Creates a <code>GifApplicationExtension</code>. The byte arrays used
	 * to create this object are NOT cloned, it's assumed they will not be
	 * changed or reused after this object is constructed.
	 * 
	 * @param header
	 *            the 11-byte header of this block. This is the 8-byte
	 *            application identifier, followed by the 3-byte authentication
	 *            code.
	 * @param data
	 *            the application data. In the original GIF file, this is
	 *            presented as a series of sub-blocks, but this argument should
	 *            NOT be presented as sub-blocks, it should already be parsed.
	 */
	protected GifApplicationExtension(byte[] header, byte[] data) {
		this.b = header;
		this.data = data;
	}

	/**
	 * Creates a <code>GifApplicationExtension</code>. All strings in this
	 * constructor must be ASCII-compatible, or an exception will be thrown.
	 * 
	 * @param applicationIdentifier
	 *            at most 8 bytes, this identifies the application.
	 * @param authenticationCode
	 *            at most 3 bytes, this can be used to authenticate the body of
	 *            this extension
	 * @param data
	 *            the application data; there is no limit to the size of this
	 *            <code>String</code>.
	 */
	public GifApplicationExtension(String applicationIdentifier,
			String authenticationCode, String data) {
		b = new byte[11];
		setApplicationIdentifier(applicationIdentifier);
		setApplicationAuthenticationCode(authenticationCode);
		setApplicationData(data);
	}

	/**
	 * Creates a <code>GifApplicationExtension</code>. All strings in this
	 * constructor must be ASCII-compatible, or an exception will be thrown.
	 * 
	 * @param applicationIdentifier
	 *            at most 8 bytes, this identifies the application.
	 * @param authenticationCode
	 *            at most 3 bytes, this can be used to authenticate the body of
	 *            this extension
	 * @param data
	 *            the application data; there is no limit to the size of this
	 *            array.
	 */
	public GifApplicationExtension(String applicationIdentifier,
			String authenticationCode, byte[] data) {
		b = new byte[11];
		setApplicationIdentifier(applicationIdentifier);
		setApplicationAuthenticationCode(authenticationCode);
		setApplicationData(data);
	}

	/**
	 * This returns <code>true</code> if this block actually describes how
	 * this GIF should loop. If this returns <code>true</code>, then
	 * <code>convertToLoopingExtension()</code> should not return
	 * <code>null</code>.
	 */
	public boolean isLoopingExtension() {
		if (Arrays.equals(b, GifLoopingApplicationExtension.NETSCAPE_HEADER) == false)
			return false;
		if (data.length == 3 && data[0] == 0x01)
			return true;
		return false;
	}

	/**
	 * If <code>isLoopingExtension()</code> returns <code>true</code>, then
	 * this method converts this block to a
	 * {@link com.bric.image.gif.block.GifLoopingApplicationExtension}.
	 * <P>
	 * Otherwise this returns <code>null</code>, and prints an exception to
	 * the console.
	 */
	public GifLoopingApplicationExtension convertToLoopingExtension() {
		try {
			return new GifLoopingApplicationExtension(b, data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] getBytes() {
		byte[] k = new byte[b.length + 3];
		k[0] = 0x21;
		k[1] = (byte) (0xFF);
		k[2] = 11;
		System.arraycopy(b, 0, k, 3, b.length);
		return concatenate(k, writeSubBlocks(data));
	}

	public int getByteCount() {
		return getBytes().length;
	}

	/**
	 * @return an eight-character string identifying an application.
	 *         <P>
	 *         This is encoded as eight ASCII bytes, so international characters
	 *         are not an option.
	 */
	public String getApplicationIdentifier() {
		return new String(b, 0, 8);
	}

	/**
	 * Assigns the application identifier of this block.
	 * 
	 * @param s
	 *            this must be 8 characters. If it is less than 8 characters,
	 *            then spaces are appended to it to make it 8 characters, if it
	 *            more than 8 characters, an exception is thrown.
	 *            <P>
	 *            Also, this must be composed of ASCII-compatible bytes. (That
	 *            is, characters smaller than an int value of 256.)
	 */
	public void setApplicationIdentifier(String s) {
		while (s.length() < 8)
			s = s + " ";
		if (s.length() > 8)
			throw new IllegalArgumentException(
					"The string \""
							+ s
							+ "\" is over 8 characters long.  An application identifier must be only 8 characters.");
		byte[] t = new byte[8];
		for (int a = 0; a < s.length(); a++) {
			if (s.charAt(a) < 256) {
				t[a] = (byte) s.charAt(a);
			} else {
				throw new IllegalArgumentException("The character \""
						+ s.charAt(a) + "\" is not ASCII-compatible.");
			}
		}
		System.arraycopy(t, 0, b, 0, 8);
	}

	/**
	 * @return sequence of three bytes used to authenticate the Application
	 *         Identifier.
	 *         <P>
	 *         In practice, this is most often simply a version number. "2.0" is
	 *         the authenitication code for the popular "NETSCAPE" looping
	 *         extension.
	 * 
	 * <P>
	 * The GIF file format specification says:
	 * <P>
	 * An Application program may use an algorithm to compute a binary code that
	 * uniquely identifies it as the application owning the Application
	 * Extension.
	 */
	public byte[] getApplicationAuthenticationCode() {
		byte[] b2 = new byte[] { b[8], b[9], b[10] };
		return b2;
	}

	/**
	 * Assigns the application authentication code for this block.
	 * 
	 * @param s
	 *            a string less than 3 characters in length, that can be
	 *            converted to ASCII bytes. (If this string is more than 3
	 *            characters, or the bytes are not ASCII-friendly, an exception
	 *            is thrown. The GIF file format pre-dates modern worries about
	 *            international characters; it has to be ASCII.)
	 *            <P>
	 *            If this string is less than 3 bytes, then spaces are appended
	 *            to it to make it exactly 3 bytes.
	 */
	public void setApplicationAuthenticationCode(String s) {
		if (s.length() > 3) {
			throw new IllegalArgumentException(
					"The application authentication code must not be more than 3 bytes.  (3!="
							+ s.length() + ")");
		}
		while (s.length() < 3)
			s = s + " ";
		byte[] array = new byte[3];
		for (int a = 0; a < 3; a++) {
			if (s.charAt(a) < 256) {
				array[a] = (byte) s.charAt(a);
			} else {
				throw new IllegalArgumentException(
						"At least one character in this authentication code is not an ASCII byte.  All characters must map to values less than 256.");
			}
		}
	}

	/**
	 * Assigns the application authentication code for this block.
	 * 
	 * @param code
	 *            a 3-byte code. An exception will be thrown if this is not 3
	 *            bytes.
	 */
	public void setApplicationAuthenticationCode(byte[] code) {
		if (code.length != 3)
			throw new IllegalArgumentException(
					"This value must be 3 bytes long.  (3!=" + code.length
							+ ")");
		b[8] = code[0];
		b[9] = code[1];
		b[10] = code[2];
	}

	/** @return the actual data itself. */
	public String getApplicationData() {
		return new String(data);
	}

	/**
	 * Sets the actual data of this block.
	 * 
	 * @param s
	 *            this can be of any length, but all the characters must be
	 *            ASCII-compatible bytes. An exception is thrown if a character
	 *            is not between [0,255].
	 */
	public void setApplicationData(String s) {
		byte[] b2 = new byte[s.length()];
		for (int a = 0; a < s.length(); a++) {
			if (s.charAt(a) < 256) {
				b2[a] = (byte) s.charAt(a);
			} else {
				throw new IllegalArgumentException("At least one character ('"
						+ s.charAt(a) + "') is not an ASCII-compatible byte.");
			}
		}
		data = b2;
	}

	/**
	 * Sets the actual data of this block. This clones the array provided, so it
	 * can be reused later.
	 * 
	 * @param newData
	 *            there are no restrictions on this array.
	 */
	public void setApplicationData(byte[] newData) {
		data = new byte[newData.length];
		System.arraycopy(newData, 0, data, 0, data.length);
	}

}
