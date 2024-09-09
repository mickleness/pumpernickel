/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.pixel;

import com.pump.image.QBufferedImage;
import com.pump.io.SerializationUtils;
import junit.framework.TestCase;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ImageTypeTest extends TestCase {

    public void testTestToString() {
        assertEquals("INT_ARGB", ImageType.toString(BufferedImage.TYPE_INT_ARGB));
        assertEquals("3BYTE_BGR", ImageType.toString(BufferedImage.TYPE_3BYTE_BGR));
        assertEquals("BYTE_GRAY", ImageType.toString(BufferedImage.TYPE_BYTE_GRAY));
        assertEquals("USHORT_565_RGB", ImageType.toString(BufferedImage.TYPE_USHORT_565_RGB));

        assertEquals("3BYTE_RGB", ImageType.toString(ImageType.TYPE_3BYTE_RGB));
        assertEquals("4BYTE_ARGB", ImageType.toString(ImageType.TYPE_4BYTE_ARGB));
        assertEquals("4BYTE_BGRA", ImageType.toString(ImageType.TYPE_4BYTE_BGRA));
        assertEquals("4BYTE_ARGB_PRE", ImageType.toString(ImageType.TYPE_4BYTE_ARGB_PRE));
    }

    /**
     * Our original conversion from ARGB_PRE to ARGB just divided each color
     * channel by the alpha. But in this case that results in "0x100", and not
     * "0xff". So we had to modify the conversion to use "Math.min(0xff,
     * component / alpha)"
     * <p>
     * This problem was observed with some NSImages, such as "IconLocked".
     */
    @Test
    public void testARGBPreDivision_small() {
        int[] pixel = new int[] { 0xc3c3c3c3 };
        int[] converted = ImageType.INT_ARGB.convertFrom(ImageType.INT_ARGB_PRE, pixel, 0,null, 0, 1);
        assertEquals(0xc3ffffff, converted[0] );
    }

    /**
     * This is an extension of the test above that uses several dozen pixels
     * instead of one.
     */
    @Test
    public void testARGBPreDivision_medium() {
        // @formatter:off
        int[][] in = new int[][] {
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1010101, 0x2020202, 0x2020202, 0x2020202, 0x1010101, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3030303, 0x12121212, 0x1c1c1c1c, 0x1f1f1f1f, 0x1c1c1c1c, 0x15151515, 0x6060606, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xa0a0a0a, 0x25252525, 0x41414141, 0x5c5b5c5b, 0x74717272, 0x81808080, 0x83838383, 0x81818181, 0x77777777, 0x61616161, 0x42424242, 0x28272727, 0xf0f0f0f, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3030303, 0x14131313, 0x42414141, 0x8a8a8a8a, 0xc3c3c3c3, 0xe4e3e3e3, 0xf1eceeef, 0xf4f2f4f4, 0xf6f5f5f6, 0xf5f4f5f5, 0xf2f1f2f2, 0xe8e5e7e8, 0xcac9caca, 0x92929292, 0x4e4e4e4e, 0x15141414, 0x4030303, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1010101, 0x2f2f2e2e, 0x7d77797a, 0xc4babcbe, 0xe4e3e4e4, 0xf8f8f8f8, 0xfffafdff, 0xffecf0f4, 0xffe1e6ea, 0xffdee2e6, 0xffe0e5e9, 0xffe8eef1, 0xfff5f9fb, 0xfbfafbfb, 0xe8e8e8e8, 0xc5c5c5c5, 0x8b8b8b8b, 0x37373737, 0x2020201, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x7070707, 0x33333333, 0x8c8b8c8c, 0xdfd9dcdc, 0xfffeffff, 0xfff7f9fa, 0xffe3e7e9, 0xffc9cfd3, 0xf3aab2b9, 0xe5929aa2, 0xe0878f97, 0xe48c949c, 0xf09fa7ae, 0xfebdc3c7, 0xffdde1e2, 0xfff5f8f9, 0xffffffff, 0xeaeae9e9, 0x9b9a9b9b, 0x423f4242, 0x9080909, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x33323232, 0x97959596, 0xe8e5e7e7, 0xffffffff, 0xffeff3f5, 0xfbbdc5cb, 0xf087929b, 0xdb5a6974, 0xb542515b, 0x9636434c, 0x89303c45, 0x932d3b46, 0xaf33424d, 0xd245555f, 0xee69777f, 0xfba9b2b8, 0xffdee2e6, 0xfefafbfb, 0xf4f2f4f4, 0xb0a9afb0, 0x33323333, 0x5050505, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3030303, 0x1d1d1d1d, 0x7d777879, 0xe6d9dddf, 0xfffdffff, 0xffeaeef1, 0xfdb0b9bf, 0xd278848e, 0x8d3f4c5b, 0x50111e2e, 0x35071016, 0x27060b0e, 0x24050a0e, 0x2701090d, 0x2f00070b, 0x48051117, 0x7f1e2f3c, 0xce51606c, 0xff98a1a8, 0xffd5dadf, 0xfffbfcfe, 0xe8e8e8e8, 0x94949393, 0x2b2b2b2b, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xb0a0a0b, 0x5f56595c, 0xc7bbbfc2, 0xfffbfdfd, 0xffedf2f5, 0xf7b7c2c8, 0xcb6b7b86, 0x74353f48, 0x2910161f, 0x4000204, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1000001, 0x1f020e14, 0x6b051526, 0xbe3d4a56, 0xf6949da5, 0xffe0e3e8, 0xfefcfdfe, 0xe2dddfe0, 0x68656666, 0xa0a0a0a, 0x1010101, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1e171819, 0xa48a9299, 0xf6e7edf2, 0xfffbfcfd, 0xffc8d0d6, 0xcb809099, 0x68415057, 0x160d1012, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x2000101, 0xc000005, 0x510c1621, 0xce4a5964, 0xfbb1b9c0, 0xffecf2f6, 0xfbf5f9fa, 0xa4a0a2a3, 0x39393939, 0x7070707, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xb0b0b0a, 0x46434343, 0xcab1b7bc, 0xfff2f9ff, 0xfeebf0f5, 0xeeabb7c1, 0x884f5b64, 0x170f1214, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1b01090d, 0x78253842, 0xd77a8792, 0xffd0d6dc, 0xfdfcfcfd, 0xd3d2d3d3, 0x706e7070, 0xd0d0d0d, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x201d1f20, 0x756d7275, 0xdeccd3d8, 0xfff4fcff, 0xf6d6dfe6, 0xba939ba2, 0x4c383c3f, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x4010203, 0x2d0f191f, 0xa84f5b65, 0xfeb3bbc2, 0xfff7f9fb, 0xf2ebf0f2, 0x9c909698, 0x19171818, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
        };
        int[][] expected = new int[][] {
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1ffffff, 0x2ffffff, 0x2ffffff, 0x2ffffff, 0x1ffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3ffffff, 0x12ffffff, 0x1cffffff, 0x1fffffff, 0x1cffffff, 0x15ffffff, 0x6ffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xaffffff, 0x25ffffff, 0x41ffffff, 0x5cfcfffc, 0x74f8fafa, 0x81fdfdfd, 0x83ffffff, 0x81ffffff, 0x77ffffff, 0x61ffffff, 0x42ffffff, 0x28f8f8f8, 0xfffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3ffffff, 0x14f2f2f2, 0x42fbfbfb, 0x8affffff, 0xc3ffffff, 0xe4fdfdfd, 0xf1f9fbfc, 0xf4fcffff, 0xf6fdfdff, 0xf5fdffff, 0xf2fdffff, 0xe8fbfdff, 0xcafdffff, 0x92ffffff, 0x4effffff, 0x15f2f2f2, 0x4bfbfbf, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1ffffff, 0x2ffff9f9, 0x7df2f6f8, 0xc4f1f4f7, 0xe4fdffff, 0xf8ffffff, 0xfffafdff, 0xffecf0f4, 0xffe1e6ea, 0xffdee2e6, 0xffe0e5e9, 0xffe8eef1, 0xfff5f9fb, 0xfbfdffff, 0xe8ffffff, 0xc5ffffff, 0x8bffffff, 0x37ffffff, 0x2ffff7f, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x7ffffff, 0x33ffffff, 0x8cfdffff, 0xdff8fbfb, 0xfffeffff, 0xfff7f9fa, 0xffe3e7e9, 0xffc9cfd3, 0xf3b2bac2, 0xe5a2abb4, 0xe099a2ab, 0xe49ca5ae, 0xf0a8b1b8, 0xfebdc3c7, 0xffdde1e2, 0xfff5f8f9, 0xffffffff, 0xeafffdfd, 0x9bfdffff, 0x42f3ffff, 0x9e2ffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x33fafafa, 0x97fbfbfd, 0xe8fbfdfd, 0xffffffff, 0xffeff3f5, 0xfbc0c8ce, 0xf08f9ba4, 0xdb687a87, 0xb55c7280, 0x965b7181, 0x89596f80, 0x934e6679, 0xaf4a6070, 0xd2536773, 0xee707f88, 0xfbabb4ba, 0xffdee2e6, 0xfefafbfb, 0xf4fcffff, 0xb0f4fdff, 0x33faffff, 0x5ffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3ffffff, 0x1dffffff, 0x7df2f4f6, 0xe6f0f5f7, 0xfffdffff, 0xffeaeef1, 0xfdb1bac0, 0xd291a0ac, 0x8d7189a4, 0x50365f92, 0x35214c69, 0x2727475b, 0x24234663, 0x27063a55, 0x2f00253b, 0x48113c51, 0x7f3c5e78, 0xce647685, 0xff98a1a8, 0xffd5dadf, 0xfffbfcfe, 0xe8ffffff, 0x94fffdfd, 0x2bffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xbe7e7ff, 0x5fe6eef6, 0xc7eff4f8, 0xfffbfdfd, 0xffedf2f5, 0xf7bcc8ce, 0xcb869aa8, 0x74748a9e, 0x296388c0, 0x4007fff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10000ff, 0x1f1073a4, 0x6b0b325a, 0xbe516373, 0xf699a2ab, 0xffe0e3e8, 0xfefcfdff, 0xe2f9fbfc, 0x68f7fafa, 0xaffffff, 0x1ffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1ec3ccd4, 0xa4d6e3ed, 0xf6eff5fa, 0xfffbfcfd, 0xffc8d0d6, 0xcba0b4c0, 0x689fc4d5, 0x1696b9d0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x2007f7f, 0xc00006a, 0x51254567, 0xce5b6e7b, 0xfbb3bbc3, 0xffecf2f6, 0xfbf8fcfd, 0xa4f8fbfd, 0x39ffffff, 0x7ffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xbffffe7, 0x46f4f4f4, 0xcadfe7ed, 0xfff2f9ff, 0xfeebf0f5, 0xeeb7c4ce, 0x8894aabb, 0x17a6c7dd, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1b09557a, 0x784e778c, 0xd790a0ad, 0xffd0d6dc, 0xfdfdfdff, 0xd3fdffff, 0x70faffff, 0xdffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0},
                { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x20e7f7ff, 0x75edf8ff, 0xdeeaf2f8, 0xfff4fcff, 0xf6dde7ee, 0xbac9d4de, 0x4cbbc9d3, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x43f7fbf, 0x2d558daf, 0xa8778a99, 0xfeb3bbc2, 0xfff7f9fb, 0xf2f7fcff, 0x9cebf5f8, 0x19eaf4f4, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0}
        };
        // @on

        for (int a = 0; a < in.length; a++) {
            int[] converted = ImageType.INT_ARGB.convertFrom(ImageType.INT_ARGB_PRE, in[a], 0,null, 0, in[a].length);
            assertEquals("a = " + a, expected[a], (int[]) converted);
        }
    }

    /**
     * Convert pixel data into several ARGB formats and back into an INT_ARGB format.
     */
    public void testARGBConversions() {
        int[] intArray = new int[] { 0xe6f1f5f8, 0xfffdffff, 0xffeaeef1, 0xfdb2bbc1, 0xd292a0ad, 0x8d7289a5, 0x50366093, 0x35214d6a, 0x2727485b };

        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_ABGR);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_ARGB);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_BGRA);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_ABGR, ImageType.BYTE_ARGB, ImageType.BYTE_BGRA);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_ARGB, ImageType.BYTE_ABGR, ImageType.BYTE_BGRA);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_ABGR, ImageType.BYTE_BGRA, ImageType.BYTE_ARGB);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_ARGB, ImageType.BYTE_BGRA, ImageType.BYTE_ABGR);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_BGRA, ImageType.BYTE_ABGR, ImageType.BYTE_ARGB);
        testConversions(ImageType.INT_ARGB, intArray, ImageType.BYTE_BGRA, ImageType.BYTE_ARGB, ImageType.BYTE_ABGR);
    }

    private void testConversions(ImageType inputType, int[] input, ImageType... types) {
        PixelIterator<?> iter = new ArrayPixelIterator(input, input.length, 1, 0, input.length, inputType.getCode());
        iter = types[0].createPixelIterator(iter);
        for (int a = 1; a < types.length; a++) {
            iter = types[a].createPixelIterator(iter);
        }
        PixelIterator lastIter = inputType.createPixelIterator(iter);

        int[] actual = new int[input.length];
        lastIter.next(actual, 0);

        assertEquals(Arrays.asList(types).toString(), input, actual);
    }


    /**
     * Convert pixel data into several RGB formats and back into an INT_RGB format.
     */
    public void testRGBConversions() {
        int[] intArray = new int[] { 0xe1f5f8, 0xfdffff, 0xeaeef1, 0xb2bbc1, 0x92a0ad, 0x7289a5, 0x366093, 0x214d6a, 0x27485b };

        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_BGR);

        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_BGR, ImageType.BYTE_RGB, ImageType.INT_RGB, ImageType.INT_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_BGR, ImageType.BYTE_RGB, ImageType.INT_BGR, ImageType.INT_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_BGR, ImageType.INT_RGB, ImageType.BYTE_RGB, ImageType.INT_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_BGR, ImageType.INT_RGB, ImageType.INT_BGR, ImageType.BYTE_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_BGR, ImageType.INT_BGR, ImageType.BYTE_RGB, ImageType.INT_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_BGR, ImageType.INT_BGR, ImageType.INT_RGB, ImageType.BYTE_RGB);

        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_RGB, ImageType.BYTE_BGR, ImageType.INT_RGB, ImageType.INT_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_RGB, ImageType.BYTE_BGR, ImageType.INT_BGR, ImageType.INT_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_RGB, ImageType.INT_RGB, ImageType.BYTE_BGR, ImageType.INT_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_RGB, ImageType.INT_RGB, ImageType.INT_BGR, ImageType.BYTE_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_RGB, ImageType.INT_BGR, ImageType.BYTE_BGR, ImageType.INT_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.BYTE_RGB, ImageType.INT_BGR, ImageType.INT_RGB, ImageType.BYTE_BGR);

        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_RGB, ImageType.BYTE_RGB, ImageType.BYTE_BGR, ImageType.INT_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_RGB, ImageType.BYTE_RGB, ImageType.INT_BGR, ImageType.BYTE_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_RGB, ImageType.BYTE_BGR, ImageType.BYTE_RGB, ImageType.INT_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_RGB, ImageType.BYTE_BGR, ImageType.INT_BGR, ImageType.BYTE_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_RGB, ImageType.INT_BGR, ImageType.BYTE_RGB, ImageType.BYTE_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_RGB, ImageType.INT_BGR, ImageType.BYTE_BGR, ImageType.BYTE_RGB);

        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_BGR, ImageType.BYTE_RGB, ImageType.INT_RGB, ImageType.BYTE_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_BGR, ImageType.BYTE_RGB, ImageType.BYTE_BGR, ImageType.INT_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_BGR, ImageType.INT_RGB, ImageType.BYTE_RGB, ImageType.BYTE_BGR);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_BGR, ImageType.INT_RGB, ImageType.BYTE_BGR, ImageType.BYTE_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_BGR, ImageType.BYTE_BGR, ImageType.BYTE_RGB, ImageType.INT_RGB);
        testConversions(ImageType.INT_RGB, intArray, ImageType.INT_BGR, ImageType.BYTE_BGR, ImageType.INT_RGB, ImageType.BYTE_RGB);
    }


    void assertEquals(String msg, int[] expectedArray, int[] actualArray) {
        for (int a = 0; a < expectedArray.length; a++) {
            String str = msg == null ? "" : msg;
            str = str + " x = " + a + " " + Integer.toUnsignedString(expectedArray[a], 16)+", "+Integer.toUnsignedString(actualArray[a], 16);
            str = str.trim();

            assertEquals(str, expectedArray[a], actualArray[a]);
        }
    }

    public void testSerialization() {
        for (ImageType type : ImageType.values()) {
            byte[] serialized = SerializationUtils.serialize(type);
            ImageType type2 = (ImageType) SerializationUtils.deserialize(serialized);
            assertEquals(type, type2);
        }
    }

    @Test
    public void testConvertImage_accuracy() {
        for (ImageType srcType : ImageType.values(true)) {
            for (ImageType dstType : ImageType.values(true)) {
                if (srcType == dstType || srcType == ImageType.BYTE_GRAY || dstType == ImageType.BYTE_GRAY)
                    continue;

                boolean highAccuracy = srcType.getColorModel().hasAlpha() == dstType.getColorModel().hasAlpha();
                if (srcType.getColorModel().hasAlpha())
                    highAccuracy = highAccuracy && srcType.getColorModel().isAlphaPremultiplied() == dstType.getColorModel().isAlphaPremultiplied();

                String msg = "Testing " + srcType + " -> " + dstType + " (" + (highAccuracy ? "high accuracy" : "low accuracy") + ")";
                System.out.println(msg);

                boolean canAllocateNewImage;
                if (srcType.isByte() == dstType.isByte() && srcType.getSampleCount() == dstType.getSampleCount()) {
                    canAllocateNewImage = false;
                } else {
                    canAllocateNewImage = true;
                }

                Color backgroundColor = srcType.getColorModel().hasAlpha() && !dstType.getColorModel().hasAlpha() ? Color.black : null;
                int scale = highAccuracy ? 40 : 1;
                BufferedImage orig = createBufferedImage(srcType.getCode(), scale, backgroundColor);
                BufferedImage in = createBufferedImage(srcType.getCode(), scale, null);

                // include subimages in accuracy test:
                orig = orig.getSubimage( (int)(orig.getWidth() * .1), (int)(orig.getHeight() * .1), (int)(orig.getWidth() * .8), (int)(orig.getHeight() * .8) );
                in = in.getSubimage( (int)(in.getWidth() * .1), (int)(in.getHeight() * .1), (int)(in.getWidth() * .8), (int)(in.getHeight() * .8) );

                QBufferedImage out = dstType.convert(in);
                if (out == null)
                    out = dstType.create(in.getWidth(), in.getHeight());
                out.copyFrom(in, 8);

                // 37 is what's required to get this unit test to pass
                int tolerance = highAccuracy ? 1 : 40;
                assertTrue(out.equals(orig, tolerance));
            }
        }
    }

    /**
     * This is not a unit test really, it's more of a performance comparison.
     */
    @Test
    public void testConvertImage_performance() {
        Collection<Collection<ImageType>> usedPairs = new HashSet<>();
        for (ImageType type1 : ImageType.values(true)) {
            for (ImageType type2 : ImageType.values(true)) {
                if (type1 != type2 &&
                        (type1.isByte() == type2.isByte() && type1.getSampleCount() == type2.getSampleCount()) ) {
                    if (!usedPairs.add(new HashSet<>(Arrays.asList(type1, type2))))
                        continue;

                    long[] samples = new long[10];
                    for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                        BufferedImage t = createBufferedImage(type1.getCode(), 40, null);
                        samples[sampleIndex] = System.currentTimeMillis();
                        for (int i = 0; i < 50; i++) {
                            BufferedImage newImage = new BufferedImage(t.getWidth(), t.getHeight(), type2.getCode());
                            Graphics2D g = newImage.createGraphics();
                            g.drawImage(t, 0, 0, null);
                            g.dispose();
                            t = newImage;

                            newImage = new BufferedImage(t.getWidth(), t.getHeight(), type1.getCode());
                            g = newImage.createGraphics();
                            g.drawImage(t, 0, 0, null);
                            g.dispose();
                            t = newImage;
                        }
                        samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
                    }
                    Arrays.sort(samples);
                    long time1 = (samples[samples.length / 2]);

                    for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                        BufferedImage t = createBufferedImage(type1.getCode(), 40, null);
                        samples[sampleIndex] = System.currentTimeMillis();
                        for (int i = 0; i < 50; i++) {
                            t = type2.convert(t).copyFrom(t, 8);
                            t = type1.convert(t).copyFrom(t, 8);
                        }
                        samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
                    }
                    Arrays.sort(samples);
                    long time2 = (samples[samples.length / 2]);

                    String percent = DecimalFormat.getPercentInstance().format(((double) time2) / ((double) time1));
                    System.out.println(type1 + " -> " + type2 + "\t" + time1 + "\t" + time2 + "\t" + percent);
                }
            }
        }
    }

    private BufferedImage createBufferedImage(int imageType, int scale, Color backgroundColor) {
        BufferedImage bi = new BufferedImage(100 * scale, 100 * scale, imageType);
        Graphics2D g = bi.createGraphics();
        if (backgroundColor != null) {
            g.setColor(backgroundColor);
            g.fillRect(0,0,bi.getWidth(),bi.getHeight());
        }
        g.scale(scale, scale);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xddffbe0b, true));
        g.fill(new Ellipse2D.Float(5,5,40,40));
        g.setColor(new Color(0xaafb5607, true));
        g.fill(new Ellipse2D.Float(55,5,40,40));
        g.setColor(new Color(0xbbff006e, true));
        g.fill(new Ellipse2D.Float(5,55,40,40));
        g.setColor(new Color(0x998338ec, true));
        g.fill(new Ellipse2D.Float(55,55,40,40));
        g.setColor(new Color(0x883a86ff, true));
        g.fill(new Ellipse2D.Float(15, 15, 70, 70));
        g.dispose();
        return bi;
    }
}