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
package com.pump.animation.quicktime.block;

/**
 * Graphics mode constants for the VideoMediaInformationHeaderBlock.
 */
public class GraphicsModeConstants {
	public static final int COPY = 0x00;
	public static final int DITHER_COPY = 0x40;
	public static final int BLEND = 0x20;
	public static final int TRANSPARENT = 0x24;
	public static final int STRAIGHT_ALPHA = 0x100;
	public static final int PREMUL_WHITE_ALPHA = 0x101;
	public static final int PREMUL_BLACK_ALPHA = 0x102;
	public static final int STRAIGHT_ALPHA_BLEND = 0x104;
	public static final int COMPOSITION = 0x103;
}