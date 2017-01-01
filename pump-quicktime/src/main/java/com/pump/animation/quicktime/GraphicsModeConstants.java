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
package com.pump.animation.quicktime;

 /** This is not a public class because I expect to make some significant
  * changes to this project in the next year.
  * <P>Use at your own risk.  This class (and its package) may change in future releases.
  * <P>Not that I'm promising there will be future releases.  There may not be.  :)
  */
class GraphicsModeConstants {
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