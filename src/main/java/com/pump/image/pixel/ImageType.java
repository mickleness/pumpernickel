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

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.pump.image.pixel.converter.IndexColorModelLUT;

/**
 * Constants related to images encoded using bytes and integers.
 */
public abstract class ImageType {

	private static Map<Integer, ImageType> imageTypeByCode = new HashMap<>();

	public static final int TYPE_4BYTE_BGRA = 24;
	public static final int TYPE_3BYTE_RGB = 25;
	public static final int TYPE_4BYTE_ARGB = 26;
	public static final int TYPE_4BYTE_ARGB_PRE = 27;

	public static final ImageTypeInt INT_RGB = new ImageTypeInt("INT_RGB",
			BufferedImage.TYPE_INT_RGB) {

		@Override
		public void convertFromARGBPre(int[] pixels, int width) {
			// intentionally empty; do nothing
		}

		@Override
		public void convertFromARGB(int[] pixels, int width) {
			for (int a = 0; a < width; a++) {
				int alpha = (pixels[a] >> 24) & 0xff;
				int r = (((pixels[a] & 0xff0000) * alpha) >> 8) & 0xff0000;
				int g = (((pixels[a] & 0xff00) * alpha) >> 8) & 0xff00;
				int b = (((pixels[a] & 0xff) * alpha) >> 8) & 0xff;
				pixels[a] = r | g | b;
			}
		}

		@Override
		public void convertFromRGB(int[] pixels, int width) {
			// intentionally empty; do nothing
		}

		@Override
		public void convertFromBGR(int[] pixels, int width) {
			invertLast3Channels_noAlpha(pixels, width);
		}

		@Override
		public void convertFromBGR(byte[] bytesIn, int[] pixels, int width) {
			for (int byteCtr = 0, intCtr = 0; intCtr < width;) {
				pixels[intCtr++] = (bytesIn[byteCtr++] & 0xff)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| ((bytesIn[byteCtr++] & 0xff) << 16);
			}
		}

		@Override
		public void convertFromABGRPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				byteCtr++; // drop the alpha

				int b = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int r = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (r << 16) | (g << 8) | b;
			}
		}

		@Override
		public void convertFromABGR(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int alpha = (bytesIn[byteCtr++] >> 24) & 0xff;
				int b = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int r = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (((r * alpha) << 8) & 0xff0000)
						| ((g * alpha) & 0xff00) | (((b * alpha) >> 8) & 0xff);
			}
		}

		@Override
		public void convertFromARGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int alpha = (bytesIn[byteCtr++] >> 24) & 0xff;
				int r = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int b = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (((r * alpha) << 8) & 0xff0000)
						| ((g * alpha) & 0xff00) | (((b * alpha) >> 8) & 0xff);
			}
		}

		@Override
		public void convertFromARGBPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				byteCtr++; // drop the alpha

				int r = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int b = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (r << 16) | (g << 8) | b;
			}
		}

		@Override
		public void convertFromGray(byte[] bytesIn, int[] pixels, int width) {
			for (int a = 0; a < width;) {
				int gray = bytesIn[a] & 0xff;
				pixels[a++] = (gray << 16) | (gray << 8) | (gray);
			}
		}

		@Override
		public void convertFromIndex(byte[] bytesIn, int[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			for (int ctr = 0; ctr < width;) {
				int index = bytesIn[ctr] & 0xff;
				pixels[ctr++] = (indexLUT.redTable_int[index] << 16)
						| (indexLUT.greenTable_int[index] << 8)
						| indexLUT.blueTable_int[index];
			}
		}

		@Override
		public void convertFromRGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = ((bytesIn[byteCtr++] & 0xff) << 16)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| (bytesIn[byteCtr++] & 0xff);
			}
		}

		@Override
		public void convertFromBGRA(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width; byteCtr += 4) {
				int alpha = (bytesIn[byteCtr + 3] >> 24) & 0xff;
				int b = bytesIn[byteCtr] & 0xff;
				int g = bytesIn[byteCtr + 1] & 0xff;
				int r = bytesIn[byteCtr + 2] & 0xff;
				pixels[intCtr++] = (((r * alpha) << 8) & 0xff0000)
						| ((g * alpha) & 0xff00) | (((b * alpha) >> 8) & 0xff);
			}
		}
	};
	public static final ImageTypeInt INT_ARGB = new ImageTypeInt("INT_ARGB",
			BufferedImage.TYPE_INT_ARGB) {

		@Override
		public void convertFromARGBPre(int[] pixels, int width) {
			for (int a = 0; a < width; a++) {
				int alpha1 = pixels[a] & 0xff000000;
				int alpha2 = (pixels[a] >> 24) & 0xff;

				if (alpha2 == 255 || alpha2 == 0) {
					// intentionally empty; do nothing
				} else {
					int r = (pixels[a] >> 8) & 0xff00;
					int g = pixels[a] & 0xff00;
					int b = (pixels[a] << 8) & 0xff00;

					r = Math.min(255, r / alpha2);
					g = Math.min(255, g / alpha2);
					b = Math.min(255, b / alpha2);

					pixels[a] = alpha1 | (r << 16) | (g << 8) | b;
				}
			}
		}

		@Override
		public void convertFromARGB(int[] pixels, int width) {
			// intentionally empty; do nothing
		}

		@Override
		public void convertFromRGB(int[] pixels, int width) {
			replaceAlpha(pixels, width);
		}

		@Override
		public void convertFromBGR(int[] pixels, int width) {
			invertLast3Channels_replaceAlpha(pixels, width);
		}

		@Override
		public void convertFromBGR(byte[] bytesIn, int[] pixels, int width) {
			invertLast3Channels_replaceAlpha(bytesIn, pixels, width);
		}

		@Override
		public void convertFromABGRPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int alpha = bytesIn[byteCtr++];

				if (alpha == 0) {
					pixels[intCtr++] = 0;
				} else if (alpha == -1) {
					pixels[intCtr++] = 0xff000000 | (bytesIn[byteCtr++] & 0xff)
							| ((bytesIn[byteCtr++] & 0xff) << 8)
							| ((bytesIn[byteCtr++] & 0xff) << 16);
				} else {
					alpha = alpha & 0xff;

					int b = (bytesIn[byteCtr++] & 0xff) << 8;
					int g = (bytesIn[byteCtr++] & 0xff) << 8;
					int r = (bytesIn[byteCtr++] & 0xff) << 8;

					r = Math.min(255, r / alpha);
					g = Math.min(255, g / alpha);
					b = Math.min(255, b / alpha);

					pixels[intCtr++] = (alpha << 24) | (r << 16) | (g << 8) | b;
				}
			}
		}

		@Override
		public void convertFromABGR(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = ((bytesIn[byteCtr++] & 0xff) << 24)
						| (bytesIn[byteCtr++] & 0xff)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| ((bytesIn[byteCtr++] & 0xff) << 16);
			}
		}

		@Override
		public void convertFromARGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = ((bytesIn[byteCtr++] & 0xff) << 24)
						| ((bytesIn[byteCtr++] & 0xff) << 16)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| (bytesIn[byteCtr++] & 0xff);
			}
		}

		@Override
		public void convertFromARGBPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int alpha = bytesIn[byteCtr++];

				if (alpha == 0) {
					pixels[intCtr++] = 0;
				} else if (alpha == -1) {
					pixels[intCtr++] = 0xff000000
							| ((bytesIn[byteCtr++] & 0xff) << 16)
							| ((bytesIn[byteCtr++] & 0xff) << 8)
							| (bytesIn[byteCtr++] & 0xff);
				} else {
					alpha = alpha & 0xff;

					int r = (bytesIn[byteCtr++] & 0xff) << 8;
					int g = (bytesIn[byteCtr++] & 0xff) << 8;
					int b = (bytesIn[byteCtr++] & 0xff) << 8;

					r = Math.min(255, r / alpha);
					g = Math.min(255, g / alpha);
					b = Math.min(255, b / alpha);

					pixels[intCtr++] = (alpha << 24) | (r << 16) | (g << 8) | b;
				}
			}
		}

		@Override
		public void convertFromGray(byte[] bytesIn, int[] pixels, int width) {
			for (int a = 0; a < width;) {
				int gray = bytesIn[a] & 0xff;
				pixels[a++] = 0xff000000 | (gray << 16) | (gray << 8) | (gray);
			}
		}

		@Override
		public void convertFromIndex(byte[] bytesIn, int[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			for (int ctr = 0; ctr < width;) {
				int index = bytesIn[ctr] & 0xff;
				pixels[ctr++] = (indexLUT.alphaTable_int[index] << 24)
						| (indexLUT.redTable_int[index] << 16)
						| (indexLUT.greenTable_int[index] << 8)
						| indexLUT.blueTable_int[index];
			}
		}

		@Override
		public void convertFromRGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = 0xff000000
						| ((bytesIn[byteCtr++] & 0xff) << 16)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| (bytesIn[byteCtr++] & 0xff);
			}
		}

		@Override
		public void convertFromBGRA(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = (bytesIn[byteCtr++] & 0xff)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| ((bytesIn[byteCtr++] & 0xff) << 16)
						| ((bytesIn[byteCtr++] & 0xff) << 24);
			}
		}
	};

	public static final ImageTypeInt INT_ARGB_PRE = new ImageTypeInt(
			"INT_ARGB_PRE", BufferedImage.TYPE_INT_ARGB_PRE) {

		@Override
		public void convertFromARGBPre(int[] pixels, int width) {
			// intentionally empty; do nothing
		}

		@Override
		public void convertFromARGB(int[] pixels, int width) {
			for (int a = 0; a < width; a++) {
				int alpha1 = pixels[a] & 0xff000000;
				int alpha2 = alpha1 >> 24;

				if (alpha2 == -1 || alpha2 == 0) {
					// intentionally empty; do nothing
				} else {
					alpha2 = alpha2 & 0xff;

					int r = (pixels[a] >> 16) & 0xff;
					int g = (pixels[a] >> 8) & 0xff;
					int b = pixels[a] & 0xff;

					r = (r * alpha2) & 0xff00;
					g = (g * alpha2) & 0xff00;
					b = (b * alpha2) & 0xff00;

					pixels[a] = alpha1 | (r << 8) | g | (b >> 8);
				}
			}
		}

		@Override
		public void convertFromRGB(int[] pixels, int width) {
			replaceAlpha(pixels, width);
		}

		@Override
		public void convertFromBGR(int[] pixels, int width) {
			invertLast3Channels_replaceAlpha(pixels, width);
		}

		@Override
		public void convertFromBGR(byte[] bytesIn, int[] pixels, int width) {
			invertLast3Channels_replaceAlpha(bytesIn, pixels, width);
		}

		@Override
		public void convertFromABGRPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = ((bytesIn[byteCtr++] & 0xff) << 24)
						| (bytesIn[byteCtr++] & 0xff)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| ((bytesIn[byteCtr++] & 0xff) << 16);
			}

		}

		@Override
		public void convertFromABGR(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				byte alpha = bytesIn[byteCtr++];

				if (alpha == 0) {
					pixels[intCtr++] = 0;
				} else if (alpha == -1) {
					pixels[intCtr++] = 0xff000000 | (bytesIn[byteCtr++] & 0xff)
							| ((bytesIn[byteCtr++] & 0xff) << 8)
							| ((bytesIn[byteCtr++] & 0xff) << 16);
				} else {
					int alphaInt = alpha & 0xff;

					int b = bytesIn[byteCtr++] & 0xff;
					int g = bytesIn[byteCtr++] & 0xff;
					int r = bytesIn[byteCtr++] & 0xff;

					r = (r * alphaInt) & 0xff00;
					g = (g * alphaInt) & 0xff00;
					b = (b * alphaInt) & 0xff00;

					pixels[intCtr++] = (alphaInt << 24) | (r << 8) | g
							| (b >> 8);
				}
			}
		}

		@Override
		public void convertFromARGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int alpha = bytesIn[byteCtr++];

				if (alpha == 0) {
					pixels[intCtr++] = 0;
				} else if (alpha == -1) {
					pixels[intCtr++] = 0xff000000
							| ((bytesIn[byteCtr++] & 0xff) << 16)
							| ((bytesIn[byteCtr++] & 0xff) << 8)
							| (bytesIn[byteCtr++] & 0xff);
				} else {
					alpha = alpha & 0xff;

					int r = bytesIn[byteCtr++] & 0xff;
					int g = bytesIn[byteCtr++] & 0xff;
					int b = bytesIn[byteCtr++] & 0xff;

					r = (r * alpha) & 0xff00;
					g = (g * alpha) & 0xff00;
					b = (b * alpha) & 0xff00;

					pixels[intCtr++] = (alpha << 24) | (r << 8) | g | (b >> 8);
				}
			}
		}

		@Override
		public void convertFromARGBPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = ((bytesIn[byteCtr++] & 0xff) << 24)
						| ((bytesIn[byteCtr++] & 0xff) << 16)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| (bytesIn[byteCtr++] & 0xff);
			}
		}

		@Override
		public void convertFromGray(byte[] bytesIn, int[] pixels, int width) {
			for (int a = 0; a < width;) {
				int gray = bytesIn[a] & 0xff;
				pixels[a++] = 0xff000000 | (gray << 16) | (gray << 8) | (gray);
			}
		}

		@Override
		public void convertFromIndex(byte[] bytesIn, int[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			for (int ctr = 0; ctr < width;) {
				int index = bytesIn[ctr] & 0xff;

				int alpha = indexLUT.alphaTable_int[index];

				if (alpha == 0) {
					pixels[ctr++] = 0;
				} else if (alpha == -1) {
					pixels[ctr++] = 0xff000000
							| (indexLUT.redTable_int[index] << 16)
							| (indexLUT.greenTable_int[index] << 8)
							| indexLUT.blueTable_int[index];
				} else {
					alpha = alpha & 0xff;

					int r = (indexLUT.redTable_int[index] * alpha) & 0xff00;
					int g = (indexLUT.greenTable_int[index] * alpha) & 0xff00;
					int b = (indexLUT.blueTable_int[index] * alpha) & 0xff00;

					pixels[ctr++] = (alpha << 24) | (r << 8) | g | (b >> 8);
				}
			}

			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = (indexLUT.alphaTable_int[bytesIn[byteCtr++]
						& 0xff] << 24)
						| (indexLUT.redTable_int[bytesIn[byteCtr++]
								& 0xff] << 16)
						| (indexLUT.greenTable_int[bytesIn[byteCtr++]
								& 0xff] << 8)
						| indexLUT.blueTable_int[bytesIn[byteCtr++] & 0xff];
			}
		}

		@Override
		public void convertFromRGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = 0xff000000
						| ((bytesIn[byteCtr++] & 0xff) << 16)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| (bytesIn[byteCtr++] & 0xff);
			}
		}

		@Override
		public void convertFromBGRA(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width; byteCtr += 4) {
				byte alpha = bytesIn[byteCtr + 3];

				if (alpha == 0) {
					pixels[intCtr++] = 0;
				} else if (alpha == -1) {
					pixels[intCtr++] = 0xff000000 | (bytesIn[byteCtr] & 0xff)
							| ((bytesIn[byteCtr + 1] & 0xff) << 8)
							| ((bytesIn[byteCtr + 2] & 0xff) << 16);
				} else {
					int alphaInt = alpha & 0xff;

					int b = bytesIn[byteCtr] & 0xff;
					int g = bytesIn[byteCtr + 1] & 0xff;
					int r = bytesIn[byteCtr + 2] & 0xff;

					r = (r * alphaInt) & 0xff00;
					g = (g * alphaInt) & 0xff00;
					b = (b * alphaInt) & 0xff00;

					pixels[intCtr++] = (alphaInt << 24) | (r << 8) | g
							| (b >> 8);
				}
			}
		}
	};

	public static final ImageTypeInt INT_BGR = new ImageTypeInt("INT_BGR",
			BufferedImage.TYPE_INT_BGR) {

		@Override
		public void convertFromARGBPre(int[] pixels, int width) {
			for (int a = 0; a < width; a++) {
				int r = (pixels[a] >> 16) & 0xff;
				int g = pixels[a] & 0xff00;
				int b = (pixels[a] << 16) & 0xff0000;
				pixels[a] = r | g | b;
			}
		}

		@Override
		public void convertFromARGB(int[] pixels, int width) {
			for (int a = 0; a < width; a++) {
				int alpha = (pixels[a] >> 24) & 0xff;
				int r = (((pixels[a] & 0xff0000) * alpha) >> 24) & 0xff;
				int g = (((pixels[a] & 0xff00) * alpha) >> 8) & 0xff00;
				int b = (((pixels[a] & 0xff) * alpha) << 8) & 0xff0000;
				pixels[a] = r | g | b;
			}
		}

		@Override
		public void convertFromRGB(int[] pixels, int width) {
			invertLast3Channels_noAlpha(pixels, width);
		}

		@Override
		public void convertFromBGR(int[] pixels, int width) {
			// intentionally empty; do nothing
		}

		@Override
		public void convertFromBGR(byte[] bytesIn, int[] pixels, int width) {
			for (int byteCtr = 0, intCtr = 0; intCtr < width;) {
				pixels[intCtr++] = ((bytesIn[byteCtr++] & 0xff) << 16)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| (bytesIn[byteCtr++] & 0xff);
			}
		}

		@Override
		public void convertFromABGRPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				byteCtr++; // drop the alpha

				int b = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int r = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (b << 16) | (g << 8) | r;
			}
		}

		@Override
		public void convertFromABGR(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int alpha = (bytesIn[byteCtr++] >> 24) & 0xff;
				int b = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int r = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (((b * alpha) << 8) & 0xff0000)
						| ((g * alpha) & 0xff00) | (((r * alpha) >> 8) & 0xff);
			}
		}

		@Override
		public void convertFromARGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int alpha = (bytesIn[byteCtr++] >> 24) & 0xff;
				int r = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int b = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (((b * alpha) << 8) & 0xff0000)
						| ((g * alpha) & 0xff00) | (((r * alpha) >> 8) & 0xff);
			}
		}

		@Override
		public void convertFromARGBPre(byte[] bytesIn, int[] pixels,
				int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				byteCtr++; // drop the alpha

				int r = bytesIn[byteCtr++] & 0xff;
				int g = bytesIn[byteCtr++] & 0xff;
				int b = bytesIn[byteCtr++] & 0xff;
				pixels[intCtr++] = (b << 16) | (g << 8) | r;
			}
		}

		@Override
		public void convertFromGray(byte[] bytesIn, int[] pixels, int width) {
			for (int a = 0; a < width;) {
				int gray = bytesIn[a] & 0xff;
				pixels[a++] = (gray << 16) | (gray << 8) | (gray);
			}
		}

		@Override
		public void convertFromIndex(byte[] bytesIn, int[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			for (int ctr = 0; ctr < width;) {
				int index = bytesIn[ctr] & 0xff;
				pixels[ctr++] = indexLUT.redTable_int[index]
						| (indexLUT.greenTable_int[index] << 8)
						| (indexLUT.blueTable_int[index] << 16);
			}
		}

		@Override
		public void convertFromRGB(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				pixels[intCtr++] = 0xff000000 | (bytesIn[byteCtr++] & 0xff)
						| ((bytesIn[byteCtr++] & 0xff) << 8)
						| ((bytesIn[byteCtr++] & 0xff) << 16);
			}
		}

		@Override
		public void convertFromBGRA(byte[] bytesIn, int[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width; byteCtr += 4) {
				int alpha = (bytesIn[byteCtr + 3] >> 24) & 0xff;
				int b = bytesIn[byteCtr] & 0xff;
				int g = bytesIn[byteCtr + 1] & 0xff;
				int r = bytesIn[byteCtr + 2] & 0xff;
				pixels[intCtr++] = (((b * alpha) << 8) & 0xff0000)
						| ((g * alpha) & 0xff00) | (((r * alpha) >> 8) & 0xff);
			}
		}
	};

	public static final ImageTypeByte BYTE_BGR = new ImageTypeByte("BYTE_BGR",
			BufferedImage.TYPE_3BYTE_BGR, 3) {

		@Override
		public void convertFromARGBPre(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int argbPre = intIn[intCtr++];
				pixels[byteCtr++] = (byte) (argbPre & 0xff);
				pixels[byteCtr++] = (byte) ((argbPre >> 8) & 0xff);
				pixels[byteCtr++] = (byte) ((argbPre >> 16) & 0xff);
			}
		}

		@Override
		public void convertFromARGB(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int argb = intIn[intCtr++];
				int alpha = (argb >> 24) & 0xff;
				if (alpha == 255) {
					pixels[byteCtr++] = (byte) ((argb) & 0xff);
					pixels[byteCtr++] = (byte) ((argb >> 8) & 0xff);
					pixels[byteCtr++] = (byte) ((argb >> 16) & 0xff);
				} else if (alpha == 0) {
					pixels[byteCtr++] = 0;
					pixels[byteCtr++] = 0;
					pixels[byteCtr++] = 0;
				} else {
					pixels[byteCtr++] = (byte) ((((argb) & 0xff) * alpha) >> 8);
					pixels[byteCtr++] = (byte) ((((argb >> 8) & 0xff)
							* alpha) >> 8);
					pixels[byteCtr++] = (byte) ((((argb >> 16) & 0xff)
							* alpha) >> 8);
				}
			}
		}

		@Override
		public void convertFromRGB(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int rgb = intIn[intCtr++];
				pixels[byteCtr++] = (byte) (rgb & 0xff);
				pixels[byteCtr++] = (byte) ((rgb >> 8) & 0xff);
				pixels[byteCtr++] = (byte) ((rgb >> 16) & 0xff);
			}
		}

		@Override
		public void convertFromBGR(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int rgb = intIn[intCtr++];
				pixels[byteCtr++] = (byte) ((rgb >> 16) & 0xff);
				pixels[byteCtr++] = (byte) ((rgb >> 8) & 0xff);
				pixels[byteCtr++] = (byte) (rgb & 0xff);
			}
		}

		@Override
		public void convertFromBGR(byte[] pixels, int width) {
			// do nothing; intentionally empty
		}

		@Override
		public void convertFromABGR(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				int alpha = pixels[i1++] & 0xff;
				if (alpha == 0) {
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
				} else if (alpha == 255) {
					pixels[i2++] = pixels[i1++];
					pixels[i2++] = pixels[i1++];
					pixels[i2++] = pixels[i1++];
				} else {
					int b = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int g = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int r = ((pixels[i1++] & 0xff) * alpha) >> 8;
					pixels[i2++] = (byte) b;
					pixels[i2++] = (byte) g;
					pixels[i2++] = (byte) r;
				}
			}
		}

		@Override
		public void convertFromABGRPre(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				i1++; // skip alpha pixel

				pixels[i2++] = pixels[i1++];
				pixels[i2++] = pixels[i1++];
				pixels[i2++] = pixels[i1++];
			}
		}

		@Override
		public void convertFromRGB(byte[] pixels, int width) {
			int lastPixel = width * 3;
			for (int i = 0; i < lastPixel; i += 3) {
				byte swap = pixels[i];
				pixels[i] = pixels[i + 2];
				pixels[i + 2] = swap;
			}
		}

		@Override
		public void convertFromARGB(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				int alpha = pixels[i1++] & 0xff;
				if (alpha == 0) {
					pixels[i2] = 0;
					pixels[i2 + 1] = 0;
					pixels[i2 + 2] = 0;
				} else if (alpha == 255) {
					byte r = pixels[i1++];
					byte g = pixels[i1++];
					byte b = pixels[i1++];
					pixels[i2++] = b;
					pixels[i2++] = g;
					pixels[i2++] = r;
				} else {
					int r = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int g = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int b = ((pixels[i1++] & 0xff) * alpha) >> 8;
					pixels[i2++] = (byte) b;
					pixels[i2++] = (byte) g;
					pixels[i2++] = (byte) r;
				}
			}
		}

		@Override
		public void convertFromARGBPre(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				i1++;
				byte r = pixels[i1++];
				byte g = pixels[i1++];
				byte b = pixels[i1++];
				pixels[i2++] = b;
				pixels[i2++] = g;
				pixels[i2++] = r;
			}
		}

		@Override
		public void convertFromGray(byte[] pixels, int width) {
			for (int i1 = (width - 1), i2 = 3 * (width - 1); i1 < width;) {
				byte gray = pixels[i1--];
				pixels[i2--] = gray;
				pixels[i2--] = gray;
				pixels[i2--] = gray;
			}
		}

		@Override
		public void convertFromIndex(byte[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			// iterate right-to-left because our target format requires more
			// bytes
			for (int i1 = width - 1, i2 = 3 * (width - 1) + 2; i1 >= 0;) {
				int index = pixels[i1--] & 0xff;
				int alpha = indexLUT.alphaTable_byte[index];
				if (alpha == 255) {
					pixels[i2--] = indexLUT.redTable_byte[index];
					pixels[i2--] = indexLUT.greenTable_byte[index];
					pixels[i2--] = indexLUT.blueTable_byte[index];
				} else {
					pixels[i2--] = (byte) (indexLUT.redTable_int[index]
							* alpha >> 8);
					pixels[i2--] = (byte) (indexLUT.greenTable_int[index]
							* alpha >> 8);
					pixels[i2--] = (byte) (indexLUT.blueTable_int[index]
							* alpha >> 8);
				}
			}
		}

		@Override
		public void convertFromBGRA(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {

				byte blue = pixels[i1];
				byte green = pixels[i1 + 1];
				byte red = pixels[i1 + 2];
				byte alpha = pixels[i1 + 3];

				if (alpha == 0) {
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
				} else if (alpha == -1) {
					pixels[i2++] = blue;
					pixels[i2++] = green;
					pixels[i2++] = red;
				} else {
					int redInt = ((red & 0xff) * alpha) >> 8;
					int greenInt = ((green & 0xff) * alpha) >> 8;
					int blueInt = ((blue & 0xff) * alpha) >> 8;
					pixels[i2++] = (byte) blueInt;
					pixels[i2++] = (byte) greenInt;
					pixels[i2++] = (byte) redInt;
				}
			}
		}
	};
	public static final ImageTypeByte BYTE_ABGR = new ImageTypeByte("BYTE_ABGR",
			BufferedImage.TYPE_4BYTE_ABGR, 4) {

		@Override
		public void convertFromARGBPre(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int argbPre = intIn[i1++];
				int alpha = (argbPre >> 24) & 0xff;
				if (alpha == 0) {
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
				} else if (alpha == 255) {
					pixels[i2++] = -1;
					pixels[i2++] = (byte) (argbPre & 0xff);
					pixels[i2++] = (byte) ((argbPre >> 8) & 0xff);
					pixels[i2++] = (byte) ((argbPre >> 16) & 0xff);
				} else {
					pixels[i2++] = (byte) alpha;
					int red = (argbPre >> 8) & 0xff00;
					int green = (argbPre) & 0xff00;
					int blue = (argbPre << 8) & 0xff00;

					red = Math.min(255, red / alpha);
					green = Math.min(255, green / alpha);
					blue = Math.min(255, blue / alpha);

					pixels[i2++] = (byte) (red);
					pixels[i2++] = (byte) (green);
					pixels[i2++] = (byte) (blue);
				}
			}
		}

		@Override
		public void convertFromARGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int argb = intIn[i1++];
				pixels[i2++] = (byte) ((argb >> 24) & 0xff);
				pixels[i2++] = (byte) ((argb) & 0xff);
				pixels[i2++] = (byte) ((argb >> 8) & 0xff);
				pixels[i2++] = (byte) ((argb >> 16) & 0xff);
			}
		}

		@Override
		public void convertFromRGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int rgb = intIn[i1++];
				pixels[i2++] = -1;
				pixels[i2++] = (byte) ((rgb) & 0xff);
				pixels[i2++] = (byte) ((rgb >> 8) & 0xff);
				pixels[i2++] = (byte) ((rgb >> 16) & 0xff);
			}
		}

		@Override
		public void convertFromBGR(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int bgr = intIn[i1++];
				pixels[i2++] = -1;
				pixels[i2++] = (byte) ((bgr >> 16) & 0xff);
				pixels[i2++] = (byte) ((bgr >> 8) & 0xff);
				pixels[i2++] = (byte) ((bgr) & 0xff);
			}
		}

		@Override
		public void convertFromBGR(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				pixels[i2--] = pixels[i1--];
				pixels[i2--] = pixels[i1--];
				pixels[i2--] = pixels[i1--];
				pixels[i2--] = -1;
			}
		}

		@Override
		public void convertFromABGR(byte[] pixels, int width) {
			// do nothing; intentionally empty
		}

		@Override
		public void convertFromABGRPre(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i = 0; i < lastPixel; i += 4) {
				byte alpha = pixels[i];
				if (alpha == 0 || alpha == -1) {
					// intentionally empty
				} else {
					int alphaInt = alpha & 0xff;
					int blue = pixels[i + 1] & 0xff;
					int green = pixels[i + 2] & 0xff;
					int red = pixels[i + 3] & 0xff;

					red = Math.min(255, (red << 8) / alphaInt);
					green = Math.min(255, (green << 8) / alphaInt);
					blue = Math.min(255, (blue << 8) / alphaInt);

					pixels[i + 1] = (byte) (blue);
					pixels[i + 2] = (byte) (green);
					pixels[i + 3] = (byte) (red);
				}
			}
		}

		@Override
		public void convertFromRGB(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				byte b = pixels[i1--];
				byte g = pixels[i1--];
				byte r = pixels[i1--];
				pixels[i2--] = r;
				pixels[i2--] = g;
				pixels[i2--] = b;
				pixels[i2--] = -1;
			}
		}

		@Override
		public void convertFromARGB(byte[] pixels, int width) {
			int lastPixel = 4 * width;
			for (int i1 = 0; i1 < lastPixel; i1 += 4) {
				byte swap = pixels[i1 + 1];
				pixels[i1 + 1] = pixels[i1 + 3];
				pixels[i1 + 3] = swap;
			}
		}

		@Override
		public void convertFromARGBPre(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i = 0; i < lastPixel; i += 4) {
				byte alpha = pixels[i];
				if (alpha == 0 || alpha == -1) {
					// intentionally empty
				} else {
					int alphaInt = alpha & 0xff;
					int red = pixels[i + 1] & 0xff;
					int green = pixels[i + 2] & 0xff;
					int blue = pixels[i + 3] & 0xff;

					red = Math.min(255, (red << 8) / alphaInt);
					green = Math.min(255, (green << 8) / alphaInt);
					blue = Math.min(255, (blue << 8) / alphaInt);

					pixels[i + 1] = (byte) blue;
					pixels[i + 2] = (byte) green;
					pixels[i + 3] = (byte) red;
				}
			}
		}

		@Override
		public void convertFromGray(byte[] pixels, int width) {
			for (int i1 = (width - 1), i2 = 3 * (width - 1); i1 < width;) {
				byte gray = pixels[i1--];
				pixels[i2--] = -1;
				pixels[i2--] = gray;
				pixels[i2--] = gray;
				pixels[i2--] = gray;
			}
		}

		@Override
		public void convertFromIndex(byte[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			// iterate right-to-left because our target format requires more
			// bytes
			for (int i1 = width - 1, i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				int index = pixels[i1--] & 0xff;

				pixels[i2--] = indexLUT.redTable_byte[index];
				pixels[i2--] = indexLUT.greenTable_byte[index];
				pixels[i2--] = indexLUT.blueTable_byte[index];
				pixels[i2--] = indexLUT.alphaTable_byte[index];
			}
		}

		@Override
		public void convertFromBGRA(byte[] pixels, int width) {
			int lastPixel = 4 * width;
			for (int i1 = 0; i1 < lastPixel; i1 += 4) {
				byte blue = pixels[i1];
				byte green = pixels[i1 + 1];
				byte red = pixels[i1 + 2];
				byte alpha = pixels[i1 + 3];

				pixels[i1] = alpha;
				pixels[i1 + 1] = blue;
				pixels[i1 + 2] = green;
				pixels[i1 + 3] = red;
			}
		}
	};
	public static final ImageTypeByte BYTE_GRAY = new ImageTypeByte("BYTE_GRAY",
			BufferedImage.TYPE_BYTE_GRAY, 1) {

		@Override
		public void convertFromARGBPre(int[] intIn, byte[] pixels, int width) {
			for (int i = 0; i < width; i++) {
				int argbPre = intIn[i];
				int redPre = (argbPre >> 16) & 0xff;
				int greenPre = (argbPre >> 8) & 0xff;
				int bluePre = (argbPre) & 0xff;
				int grayPre = (redPre + greenPre + bluePre) / 3;
				pixels[i] = (byte) grayPre;
			}
		}

		@Override
		public void convertFromARGB(int[] intIn, byte[] pixels, int width) {
			for (int i = 0; i < width; i++) {
				int argb = intIn[i];
				int red = (argb >> 16) & 0xff;
				int green = (argb >> 8) & 0xff;
				int blue = (argb) & 0xff;

				int alpha = (argb >> 24) & 0xff;
				int gray = (((red + green + blue) / 3) * alpha) >> 8;
				pixels[i] = (byte) gray;
			}
		}

		@Override
		public void convertFromRGB(int[] intIn, byte[] pixels, int width) {
			for (int i = 0; i < width; i++) {
				int arg = intIn[i];
				int red = (arg >> 16) & 0xff;
				int green = (arg >> 8) & 0xff;
				int blue = (arg) & 0xff;

				int gray = (red + green + blue) / 3;
				pixels[i] = (byte) gray;
			}
		}

		@Override
		public void convertFromBGR(int[] intIn, byte[] pixels, int width) {
			convertFromRGB(intIn, pixels, width);
		}

		@Override
		public void convertFromBGR(byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i2 < width;) {
				int blue = pixels[i1++] & 0xff;
				int green = pixels[i1++] & 0xff;
				int red = pixels[i1++] & 0xff;
				int gray = (blue + green + red) / 3;
				pixels[i2++] = (byte) gray;
			}
		}

		@Override
		public void convertFromABGR(byte[] pixels, int width) {
			convertFromARGB(pixels, width);
		}

		@Override
		public void convertFromABGRPre(byte[] pixels, int width) {
			convertFromARGBPre(pixels, width);
		}

		@Override
		public void convertFromRGB(byte[] pixels, int width) {
			convertFromBGR(pixels, width);
		}

		@Override
		public void convertFromARGB(byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i2 < width;) {
				int alpha = pixels[i1++];
				int red = pixels[i1++];
				int green = pixels[i1++];
				int blue = pixels[i1++];
				int gray = (((red + green + blue) / 3) * alpha) >> 8;
				pixels[i2++] = (byte) gray;
			}
		}

		@Override
		public void convertFromARGBPre(byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i2 < width;) {
				i1++; // skip alpha
				int red = pixels[i1++];
				int green = pixels[i1++];
				int blue = pixels[i1++];
				int gray = (red + green + blue) / 3;
				pixels[i2++] = (byte) gray;
			}
		}

		@Override
		public void convertFromGray(byte[] pixels, int width) {
			// do nothing; intentionally empty
		}

		@Override
		public void convertFromIndex(byte[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			for (int i = 0; i < width;) {
				int index = pixels[i] & 0xff;

				int red = indexLUT.redTable_int[index];
				int green = indexLUT.greenTable_int[index];
				int blue = indexLUT.blueTable_int[index];
				int gray = (red + green + blue) / 3;

				int alpha = indexLUT.alphaTable_int[index];
				gray = gray * alpha >> 8;

				pixels[i] = (byte) gray;
			}
		}

		@Override
		public void convertFromBGRA(byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i2 < width;) {
				int blue = pixels[i1++];
				int green = pixels[i1++];
				int red = pixels[i1++];
				int alpha = pixels[i1++];
				int gray = (((red + green + blue) / 3) * alpha) >> 8;
				pixels[i2++] = (byte) gray;
			}
		}
	};

	/**
	 * This is used to reflect when 3 bytes are used for [red, green, blue]
	 * data.
	 */
	public static final ImageTypeByte BYTE_RGB = new ImageTypeByte("BYTE_RGB",
			TYPE_3BYTE_RGB, 3) {

		@Override
		public void convertFromARGBPre(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int argbPre = intIn[intCtr++];
				pixels[byteCtr++] = (byte) ((argbPre >> 16) & 0xff);
				pixels[byteCtr++] = (byte) ((argbPre >> 8) & 0xff);
				pixels[byteCtr++] = (byte) (argbPre & 0xff);
			}
		}

		@Override
		public void convertFromARGB(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int argb = intIn[intCtr++];
				int alpha = (argb >> 24) & 0xff;
				if (alpha == 255) {
					pixels[byteCtr++] = (byte) ((argb >> 16) & 0xff);
					pixels[byteCtr++] = (byte) ((argb >> 8) & 0xff);
					pixels[byteCtr++] = (byte) ((argb) & 0xff);
				} else if (alpha == 0) {
					pixels[byteCtr++] = 0;
					pixels[byteCtr++] = 0;
					pixels[byteCtr++] = 0;
				} else {
					pixels[byteCtr++] = (byte) ((((argb >> 16) & 0xff)
							* alpha) >> 8);
					pixels[byteCtr++] = (byte) ((((argb >> 8) & 0xff)
							* alpha) >> 8);
					pixels[byteCtr++] = (byte) ((((argb) & 0xff) * alpha) >> 8);
				}
			}
		}

		@Override
		public void convertFromRGB(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int rgb = intIn[intCtr++];
				pixels[byteCtr++] = (byte) ((rgb >> 16) & 0xff);
				pixels[byteCtr++] = (byte) ((rgb >> 8) & 0xff);
				pixels[byteCtr++] = (byte) (rgb & 0xff);
			}
		}

		@Override
		public void convertFromBGR(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int rgb = intIn[intCtr++];
				pixels[byteCtr++] = (byte) (rgb & 0xff);
				pixels[byteCtr++] = (byte) ((rgb >> 8) & 0xff);
				pixels[byteCtr++] = (byte) ((rgb >> 16) & 0xff);
			}
		}

		@Override
		public void convertFromBGR(byte[] pixels, int width) {
			// just swap first and third bytes
			BYTE_BGR.convertFromRGB(pixels, width);
		}

		@Override
		public void convertFromABGR(byte[] pixels, int width) {
			int lastPixel = 4 * width;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				byte alpha = pixels[i1++];
				if (alpha == 0) {
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					i1 += 3;
				} else if (alpha == -1) {
					byte blue = pixels[i1++];
					byte green = pixels[i1++];
					byte red = pixels[i1++];
					pixels[i2++] = red;
					pixels[i2++] = green;
					pixels[i2++] = blue;
				} else {
					int blue = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int green = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int red = ((pixels[i1++] & 0xff) * alpha) >> 8;
					pixels[i2++] = (byte) red;
					pixels[i2++] = (byte) green;
					pixels[i2++] = (byte) blue;
				}
			}
		}

		@Override
		public void convertFromABGRPre(byte[] pixels, int width) {
			int lastPixel = 4 * width;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				i1++; // skip alpha channel
				byte blue = pixels[i1++];
				byte green = pixels[i1++];
				byte red = pixels[i1++];
				pixels[i2++] = red;
				pixels[i2++] = green;
				pixels[i2++] = blue;
			}
		}

		@Override
		public void convertFromRGB(byte[] pixels, int width) {
			// do nothing; intentionally empty
		}

		@Override
		public void convertFromARGB(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				int alpha = pixels[i1++];
				if (alpha == 0) {
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
				} else if (alpha == -1) {
					pixels[i2++] = pixels[i1++];
					pixels[i2++] = pixels[i1++];
					pixels[i2++] = pixels[i1++];
				} else {
					alpha = alpha & 0xff;
					int r = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int g = ((pixels[i1++] & 0xff) * alpha) >> 8;
					int b = ((pixels[i1++] & 0xff) * alpha) >> 8;
					pixels[i2++] = (byte) r;
					pixels[i2++] = (byte) g;
					pixels[i2++] = (byte) b;
				}
			}
		}

		@Override
		public void convertFromARGBPre(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				i1++;
				pixels[i2++] = pixels[i1++];
				pixels[i2++] = pixels[i1++];
				pixels[i2++] = pixels[i1++];
			}
		}

		@Override
		public void convertFromGray(byte[] pixels, int width) {
			BYTE_BGR.convertFromGray(pixels, width);
		}

		@Override
		public void convertFromIndex(byte[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			// iterate right-to-left because our target format requires more
			// bytes
			for (int i1 = width - 1, i2 = 3 * (width - 1) + 2; i1 >= 0;) {
				int index = pixels[i1--] & 0xff;
				int alpha = indexLUT.alphaTable_byte[index];
				if (alpha == 255) {
					pixels[i2--] = indexLUT.blueTable_byte[index];
					pixels[i2--] = indexLUT.greenTable_byte[index];
					pixels[i2--] = indexLUT.redTable_byte[index];
				} else {
					pixels[i2--] = (byte) (indexLUT.blueTable_int[index]
							* alpha >> 8);
					pixels[i2--] = (byte) (indexLUT.greenTable_int[index]
							* alpha >> 8);
					pixels[i2--] = (byte) (indexLUT.redTable_int[index]
							* alpha >> 8);
				}
			}
		}

		@Override
		public void convertFromBGRA(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i1 = 0, i2 = 0; i1 < lastPixel;) {
				byte blue = pixels[i1++];
				byte green = pixels[i1++];
				byte red = pixels[i1++];
				byte alpha = pixels[i1++];

				if (alpha == 0) {
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
				} else if (alpha == -1) {
					pixels[i2++] = red;
					pixels[i2++] = green;
					pixels[i2++] = blue;
				} else {
					int alphaInt = alpha & 0xff;
					int r = ((red & 0xff) * alphaInt) >> 8;
					int g = ((green & 0xff) * alphaInt) >> 8;
					int b = ((blue & 0xff) * alphaInt) >> 8;
					pixels[i2++] = (byte) r;
					pixels[i2++] = (byte) g;
					pixels[i2++] = (byte) b;
				}
			}
		}
	};

	/**
	 * This is used to reflect when 4 bytes are used for [alpha, red, green,
	 * blue] data.
	 */
	public static final ImageTypeByte BYTE_ARGB = new ImageTypeByte("BYTE_ARGB",
			TYPE_4BYTE_ARGB, 4) {

		@Override
		public void convertFromARGBPre(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int argbPre = intIn[i1++];
				int alpha = (argbPre >> 24) & 0xff;
				if (alpha == 0) {
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
					pixels[i2++] = 0;
				} else if (alpha == 255) {
					pixels[i2++] = -1;
					pixels[i2++] = (byte) ((argbPre >> 16) & 0xff);
					pixels[i2++] = (byte) ((argbPre >> 8) & 0xff);
					pixels[i2++] = (byte) (argbPre & 0xff);
				} else {
					pixels[i2++] = (byte) alpha;
					int red = (argbPre >> 8) & 0xff00;
					int green = (argbPre) & 0xff00;
					int blue = (argbPre << 8) & 0xff00;

					red = Math.min(255, red / alpha);
					green = Math.min(255, green / alpha);
					blue = Math.min(255, blue / alpha);

					pixels[i2++] = (byte) red;
					pixels[i2++] = (byte) green;
					pixels[i2++] = (byte) blue;
				}
			}
		}

		@Override
		public void convertFromARGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int argb = intIn[i1++];
				pixels[i2++] = (byte) ((argb >> 24) & 0xff);
				pixels[i2++] = (byte) ((argb >> 16) & 0xff);
				pixels[i2++] = (byte) ((argb >> 8) & 0xff);
				pixels[i2++] = (byte) ((argb) & 0xff);
			}
		}

		@Override
		public void convertFromRGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int rgb = intIn[i1++];
				pixels[i2++] = -1;
				pixels[i2++] = (byte) ((rgb >> 16) & 0xff);
				pixels[i2++] = (byte) ((rgb >> 8) & 0xff);
				pixels[i2++] = (byte) ((rgb) & 0xff);
			}
		}

		@Override
		public void convertFromBGR(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int bgr = intIn[i1++];
				pixels[i2++] = -1;
				pixels[i2++] = (byte) ((bgr) & 0xff);
				pixels[i2++] = (byte) ((bgr >> 8) & 0xff);
				pixels[i2++] = (byte) ((bgr >> 16) & 0xff);
			}
		}

		@Override
		public void convertFromBGR(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				byte red = pixels[i1--];
				byte green = pixels[i1--];
				byte blue = pixels[i1--];
				pixels[i2--] = blue;
				pixels[i2--] = green;
				pixels[i2--] = red;
				pixels[i2--] = -1;
			}
		}

		@Override
		public void convertFromABGR(byte[] pixels, int width) {
			// swap 2nd & 4th component
			BYTE_ABGR.convertFromARGB(pixels, width);
		}

		@Override
		public void convertFromABGRPre(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i = 0; i < lastPixel; i += 4) {
				byte alpha = pixels[i];
				if (alpha == 0 || alpha == -1) {
					// intentionally empty
				} else {
					int alphaInt = alpha & 0xff;
					int blue = pixels[i + 1] & 0xff;
					int green = pixels[i + 2] & 0xff;
					int red = pixels[i + 3] & 0xff;

					red = Math.min(255, (red << 8) / alphaInt);
					green = Math.min(255, (green << 8) / alphaInt);
					blue = Math.min(255, (blue << 8) / alphaInt);

					pixels[i + 1] = (byte) red;
					pixels[i + 2] = (byte) green;
					pixels[i + 3] = (byte) blue;
				}
			}
		}

		@Override
		public void convertFromRGB(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				byte blue = pixels[i1--];
				byte green = pixels[i1--];
				byte red = pixels[i1--];
				pixels[i2--] = blue;
				pixels[i2--] = green;
				pixels[i2--] = red;
				pixels[i2--] = -1;
			}
		}

		@Override
		public void convertFromARGB(byte[] pixels, int width) {
			// do nothing; intentionally empty
		}

		@Override
		public void convertFromARGBPre(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i = 0; i < lastPixel; i += 4) {
				byte alpha = pixels[i];
				if (alpha == 0 || alpha == -1) {
					// intentionally empty
				} else {
					int alphaInt = alpha & 0xff;
					int red = pixels[i + 1] & 0xff;
					int green = pixels[i + 2] & 0xff;
					int blue = pixels[i + 3] & 0xff;

					red = Math.min(255, (red << 8) / alphaInt);
					green = Math.min(255, (green << 8) / alphaInt);
					blue = Math.min(255, (blue << 8) / alphaInt);

					pixels[i + 1] = (byte) red;
					pixels[i + 2] = (byte) green;
					pixels[i + 3] = (byte) blue;
				}
			}
		}

		@Override
		public void convertFromGray(byte[] pixels, int width) {
			BYTE_ABGR.convertFromGray(pixels, width);
		}

		@Override
		public void convertFromIndex(byte[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			// iterate right-to-left because our target format requires more
			// bytes
			for (int i1 = width - 1, i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				int index = pixels[i1--] & 0xff;

				pixels[i2--] = indexLUT.blueTable_byte[index];
				pixels[i2--] = indexLUT.greenTable_byte[index];
				pixels[i2--] = indexLUT.redTable_byte[index];
				pixels[i2--] = indexLUT.alphaTable_byte[index];
			}
		}

		@Override
		public void convertFromBGRA(byte[] pixels, int width) {
			int lastPixel = width * 4;
			for (int i = 0; i < lastPixel; i += 4) {
				byte blue = pixels[i];
				byte green = pixels[i + 1];
				byte red = pixels[i + 2];
				byte alpha = pixels[i + 3];

				pixels[i] = alpha;
				pixels[i + 1] = red;
				pixels[i + 2] = green;
				pixels[i + 3] = blue;
			}
		}
	};

	/**
	 * This is used to reflect when 4 bytes are used for [alpha, red, green,
	 * blue] premultiplied data.
	 */
	public static final ImageTypeByte BYTE_ARGB_PRE = new ImageTypeByte(
			"BYTE_ARGB_PRE", TYPE_4BYTE_ARGB_PRE, 4) {

		@Override
		public void convertFromARGBPre(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int argbPre = intIn[intCtr++];
				pixels[byteCtr++] = (byte) ((argbPre >> 24) & 0xff);
				pixels[byteCtr++] = (byte) ((argbPre >> 16) & 0xff);
				pixels[byteCtr++] = (byte) ((argbPre >> 8) & 0xff);
				pixels[byteCtr++] = (byte) (argbPre & 0xff);
			}
		}

		@Override
		public void convertFromARGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int argb = intIn[i1++];
				int alpha = (argb >> 24) & 0xff;
				int red = ((argb & 0xff0000) * alpha) >> 24;
				int green = ((argb & 0xff00) * alpha) >> 16;
				int blue = ((argb & 0xff) * alpha) >> 8;
				pixels[i2++] = (byte) alpha;
				pixels[i2++] = (byte) red;
				pixels[i2++] = (byte) green;
				pixels[i2++] = (byte) blue;
			}
		}

		@Override
		public void convertFromRGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int rgb = intIn[i1++];
				pixels[i2++] = -1;
				pixels[i2++] = (byte) ((rgb >> 16) & 0xff);
				pixels[i2++] = (byte) ((rgb >> 8) & 0xff);
				pixels[i2++] = (byte) (rgb & 0xff);
			}
		}

		@Override
		public void convertFromBGR(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int rgb = intIn[i1++];
				pixels[i2++] = -1;
				pixels[i2++] = (byte) (rgb & 0xff);
				pixels[i2++] = (byte) ((rgb >> 8) & 0xff);
				pixels[i2++] = (byte) ((rgb >> 16) & 0xff);
			}
		}

		@Override
		public void convertFromBGR(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				byte red = pixels[i1--];
				byte green = pixels[i1--];
				byte blue = pixels[i1--];

				pixels[i2--] = blue;
				pixels[i2--] = green;
				pixels[i2--] = red;
				pixels[i2--] = -1;
			}
		}

		@Override
		public void convertFromABGR(byte[] pixels, int width) {
			int lastIndex = 4 * width;
			for (int i = 0; i < lastIndex; i += 4) {
				byte alpha = pixels[i];
				byte blue = pixels[i + 1];
				byte green = pixels[i + 2];
				byte red = pixels[i + 3];
				if (alpha == 0 || alpha == -1) {
					pixels[i + 1] = red;
					pixels[i + 2] = green;
					pixels[i + 3] = blue;
				} else {
					int alphaInt = alpha & 0xff;
					pixels[i + 1] = (byte) (((red & 0xff) * alphaInt) >> 8);
					pixels[i + 2] = (byte) (((green & 0xff) * alphaInt) >> 8);
					pixels[i + 3] = (byte) (((blue & 0xff) * alphaInt) >> 8);
				}
			}
		}

		@Override
		public void convertFromABGRPre(byte[] pixels, int width) {
			// swap 2nd & 4th component
			BYTE_ABGR.convertFromARGB(pixels, width);
		}

		@Override
		public void convertFromRGB(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				byte blue = pixels[i1--];
				byte green = pixels[i1--];
				byte red = pixels[i1--];

				pixels[i2--] = blue;
				pixels[i2--] = green;
				pixels[i2--] = red;
				pixels[i2--] = -1;
			}
		}

		@Override
		public void convertFromARGB(byte[] pixels, int width) {
			int lastIndex = 4 * width;
			for (int i = 0; i < lastIndex; i += 4) {
				byte alpha = pixels[i];
				byte red = pixels[i + 1];
				byte green = pixels[i + 2];
				byte blue = pixels[i + 3];
				if (alpha == 0 || alpha == -1) {
					pixels[i + 1] = red;
					pixels[i + 2] = green;
					pixels[i + 3] = blue;
				} else {
					int alphaInt = alpha & 0xff;
					pixels[i + 1] = (byte) (((red & 0xff) * alphaInt) >> 8);
					pixels[i + 2] = (byte) (((green & 0xff) * alphaInt) >> 8);
					pixels[i + 3] = (byte) (((blue & 0xff) * alphaInt) >> 8);
				}
			}
		}

		@Override
		public void convertFromARGBPre(byte[] pixels, int width) {
			// do nothing; intentionally empty
		}

		@Override
		public void convertFromGray(byte[] pixels, int width) {
			BYTE_ABGR.convertFromGray(pixels, width);
		}

		@Override
		public void convertFromIndex(byte[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			// iterate right-to-left because our target format requires more
			// bytes
			for (int i1 = width - 1, i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				int index = pixels[i1--] & 0xff;

				int alpha = indexLUT.alphaTable_int[index];
				if (alpha == 255) {
					pixels[i2--] = indexLUT.blueTable_byte[index];
					pixels[i2--] = indexLUT.greenTable_byte[index];
					pixels[i2--] = indexLUT.redTable_byte[index];
				} else if (alpha == 0) {
					pixels[i2--] = 0;
					pixels[i2--] = 0;
					pixels[i2--] = 0;
				} else {
					pixels[i2--] = (byte) ((indexLUT.blueTable_int[index]
							* alpha) >> 8);
					pixels[i2--] = (byte) ((indexLUT.greenTable_int[index]
							* alpha) >> 8);
					pixels[i2--] = (byte) ((indexLUT.redTable_int[index]
							* alpha) >> 8);
				}
				pixels[i2--] = indexLUT.alphaTable_byte[index];
			}
		}

		@Override
		public void convertFromBGRA(byte[] pixels, int width) {
			int lastIndex = 4 * width;
			for (int i = 0; i < lastIndex; i += 4) {
				byte blue = pixels[i];
				byte green = pixels[i + 1];
				byte red = pixels[i + 2];
				byte alpha = pixels[i + 3];
				if (alpha == 0 || alpha == -1) {
					pixels[i] = blue;
					pixels[i + 1] = green;
					pixels[i + 2] = red;
				} else {
					int alphaInt = alpha & 0xff;
					pixels[i] = (byte) (((blue & 0xff) * alphaInt) >> 8);
					pixels[i + 1] = (byte) (((green & 0xff) * alphaInt) >> 8);
					pixels[i + 2] = (byte) (((red & 0xff) * alphaInt) >> 8);
				}
				pixels[i + 3] = alpha;
			}
		}
	};

	/**
	 * This is used to reflect when 4 bytes are used for [blue, green, red,
	 * alpha] data.
	 */
	public static final ImageTypeByte BYTE_BGRA = new ImageTypeByte("BYTE_BGRA",
			TYPE_4BYTE_BGRA, 4) {

		@Override
		public void convertFromARGBPre(int[] intIn, byte[] pixels, int width) {
			for (int intCtr = 0, byteCtr = 0; intCtr < width;) {
				int argbPre = intIn[intCtr++];

				int alpha = (argbPre >> 24) & 0xff;
				if (alpha == 0) {
					pixels[byteCtr++] = 0;
					pixels[byteCtr++] = 0;
					pixels[byteCtr++] = 0;
					pixels[byteCtr++] = 0;
				} else if (alpha == 255) {
					pixels[byteCtr++] = (byte) (argbPre & 0xff);
					pixels[byteCtr++] = (byte) ((argbPre >> 8) & 0xff);
					pixels[byteCtr++] = (byte) ((argbPre >> 16) & 0xff);
					pixels[byteCtr++] = -1;
				} else {
					int blue = (argbPre << 8) & 0xff00;
					int green = (argbPre) & 0xff00;
					int red = (argbPre >> 8) & 0xff00;

					blue = Math.min(255, blue / alpha);
					red = Math.min(255, red / alpha);
					green = Math.min(255, green / alpha);

					pixels[byteCtr++] = (byte) blue;
					pixels[byteCtr++] = (byte) green;
					pixels[byteCtr++] = (byte) red;
					pixels[byteCtr++] = (byte) alpha;
				}

			}
		}

		@Override
		public void convertFromARGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int argb = intIn[i1++];

				pixels[i2++] = (byte) (argb & 0xff);
				pixels[i2++] = (byte) ((argb >> 8) & 0xff);
				pixels[i2++] = (byte) ((argb >> 16) & 0xff);
				pixels[i2++] = (byte) ((argb >> 24) & 0xff);
			}
		}

		@Override
		public void convertFromRGB(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int rgb = intIn[i1++];
				pixels[i2++] = (byte) (rgb & 0xff);
				pixels[i2++] = (byte) ((rgb >> 8) & 0xff);
				pixels[i2++] = (byte) ((rgb >> 16) & 0xff);
				pixels[i2++] = -1;
			}
		}

		@Override
		public void convertFromBGR(int[] intIn, byte[] pixels, int width) {
			for (int i1 = 0, i2 = 0; i1 < width;) {
				int bgr = intIn[i1++];
				pixels[i2++] = (byte) ((bgr >> 16) & 0xff);
				pixels[i2++] = (byte) ((bgr >> 8) & 0xff);
				pixels[i2++] = (byte) (bgr & 0xff);
				pixels[i2++] = -1;
			}
		}

		@Override
		public void convertFromBGR(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				byte red = pixels[i1--];
				byte green = pixels[i1--];
				byte blue = pixels[i1--];

				pixels[i2--] = -1;
				pixels[i2--] = red;
				pixels[i2--] = green;
				pixels[i2--] = blue;
			}
		}

		@Override
		public void convertFromABGR(byte[] pixels, int width) {
			int lastIndex = 4 * width;
			for (int i = 0; i < lastIndex; i += 4) {
				byte alpha = pixels[i];
				byte blue = pixels[i + 1];
				byte green = pixels[i + 2];
				byte red = pixels[i + 3];

				pixels[i] = blue;
				pixels[i + 1] = green;
				pixels[i + 2] = red;
				pixels[i + 3] = alpha;
			}
		}

		@Override
		public void convertFromABGRPre(byte[] pixels, int width) {
			int lastIndex = 4 * width;
			for (int i = 0; i < lastIndex; i += 4) {
				byte alpha = pixels[i];
				byte blue = pixels[i + 1];
				byte green = pixels[i + 2];
				byte red = pixels[i + 3];

				if (alpha == -1 || alpha == 0) {
					pixels[i] = blue;
					pixels[i + 1] = green;
					pixels[i + 2] = red;
				} else {
					int blueInt = blue & 0xff;
					int redInt = red & 0xff;
					int greenInt = green & 0xff;

					blueInt = Math.min(255, (blueInt << 8) / alpha);
					greenInt = Math.min(255, (greenInt << 8) / alpha);
					redInt = Math.min(255, (redInt << 8) / alpha);

					pixels[i] = (byte) blueInt;
					pixels[i + 1] = (byte) greenInt;
					pixels[i + 2] = (byte) redInt;
				}
				pixels[i + 3] = alpha;
			}

		}

		@Override
		public void convertFromRGB(byte[] pixels, int width) {
			for (int i1 = 3 * (width - 1) + 2,
					i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				byte blue = pixels[i1--];
				byte green = pixels[i1--];
				byte red = pixels[i1--];

				pixels[i2--] = -1;
				pixels[i2--] = red;
				pixels[i2--] = green;
				pixels[i2--] = blue;
			}
		}

		@Override
		public void convertFromARGB(byte[] pixels, int width) {
			int lastIndex = 4 * width;
			for (int i = 0; i < lastIndex; i += 4) {
				byte alpha = pixels[i];
				byte red = pixels[i + 1];
				byte green = pixels[i + 2];
				byte blue = pixels[i + 3];
				pixels[i] = blue;
				pixels[i + 1] = green;
				pixels[i + 2] = red;
				pixels[i + 3] = alpha;
			}
		}

		@Override
		public void convertFromARGBPre(byte[] pixels, int width) {
			int lastIndex = 4 * width;
			for (int i = 0; i < lastIndex; i += 4) {
				byte alpha = pixels[i];
				byte red = pixels[i + 1];
				byte green = pixels[i + 2];
				byte blue = pixels[i + 3];

				if (alpha == -1 || alpha == 0) {
					pixels[i] = blue;
					pixels[i + 1] = green;
					pixels[i + 2] = red;
				} else {
					int blueInt = blue & 0xff;
					int redInt = red & 0xff;
					int greenInt = green & 0xff;

					blueInt = Math.min(255, (blueInt << 8) / alpha);
					greenInt = Math.min(255, (greenInt << 8) / alpha);
					redInt = Math.min(255, (redInt << 8) / alpha);

					pixels[i] = (byte) blueInt;
					pixels[i + 1] = (byte) greenInt;
					pixels[i + 2] = (byte) redInt;
				}
				pixels[i + 3] = alpha;
			}
		}

		@Override
		public void convertFromGray(byte[] pixels, int width) {
			for (int i1 = (width - 1), i2 = 3 * (width - 1); i1 < width;) {
				byte gray = pixels[i1--];
				pixels[i2--] = gray;
				pixels[i2--] = gray;
				pixels[i2--] = gray;
				pixels[i2--] = -1;
			}
		}

		@Override
		public void convertFromIndex(byte[] pixels, int width,
				IndexColorModelLUT indexLUT) {
			// iterate right-to-left because our target format requires more
			// bytes
			for (int i1 = width - 1, i2 = 4 * (width - 1) + 3; i1 >= 0;) {
				int index = pixels[i1--] & 0xff;

				pixels[i2--] = indexLUT.alphaTable_byte[index];
				pixels[i2--] = indexLUT.redTable_byte[index];
				pixels[i2--] = indexLUT.greenTable_byte[index];
				pixels[i2--] = indexLUT.blueTable_byte[index];
			}
		}

		@Override
		public void convertFromBGRA(byte[] pixels, int width) {
			// do nothing; intentionally empty
		}
	};

	/**
	 * Return the ImageType associated with a constant.
	 * 
	 * @param imageType
	 *            the image type constant, such as
	 *            {@linK BufferedImage#TYPE_3BYTE_BGR} or
	 *            {@link #TYPE_4BYTE_ARGB}, or null if the int provided doesn't
	 *            match an ImageType constant.
	 */
	public static ImageType get(int imageType) {
		return imageTypeByCode.get(imageType);
	}

	public final int code;
	public final String name;

	protected ImageType(String name, int code) {
		this.code = code;
		this.name = Objects.requireNonNull(name);
		imageTypeByCode.put(code, this);
	}

	public abstract PixelIterator<?> createConverter(PixelIterator<?> srcIter);

	public abstract PixelIterator<?> createConverter(BufferedImage image);

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImageType))
			return false;
		ImageType other = (ImageType) obj;
		return code == other.code;
	}
}