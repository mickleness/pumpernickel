package com.pump.image.pixel2.converter;

import java.util.LinkedList;

class ConverterUtils {


    /**
     * Swap the 1st and 3rd channels/samples of pixels. So if one pixel is represented as "0xAARRGGBB" then this
     * method will convert that to "0xAABBGGRR"
     */
    static void swapFirstAndThirdSamples(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO: revise. only try to anticipate scenario where dest==source && offsets match; everything else can add buffer
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset; srcIndex--, destIndex--) {
                int value = sourcePixels[srcIndex];
                int sampleA = value & 0xff;
                int sampleB = (value >> 16) & 0xff;
                destPixels[destIndex] = (value & 0xff00ff00) | sampleB | (sampleA << 16);
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; srcIndex++, destIndex++) {
            int value = sourcePixels[srcIndex];
            int sampleA = value & 0xff;
            int sampleB = (value >> 16) & 0xff;
            destPixels[destIndex] = (value & 0xff00ff00) | sampleB | (sampleA << 16);
        }
    }

    /**
     * Take 3-sample data and prepend an opaque "255" before each pixel. So {R1, G1, B1, R2, G2, B2, ...}
     * becomes {255, R1, G1, B1, 255, R2, G2, B2, ...}
     */
    static void prependAlpha(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        // TODO: revise. only try to anticipate scenario where dest==source && offsets match; everything else can add buffer
        int destEnd = destOffset + 4 * pixelCount;
        int srcEnd = srcOffset + 3 * pixelCount;
        if (destPixels != sourcePixels || destEnd < srcOffset || destOffset > srcEnd) {
            // we can iterate LTR
            int dstIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
                destPixels[dstIndex++] = -1;
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
            }
        } else if (destOffset >= srcOffset) {
            // we can iterate RTL
            int dstIndex = destEnd - 1;
            for (int srcIndex = srcEnd - 1; srcIndex >= srcOffset; ) {
                destPixels[dstIndex--] = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = -1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                prependAlpha(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static LinkedList<byte[]> byteArrays = new LinkedList<>();
    static int minByteArrayLength = 4000;

    static byte[] getScratchArray(int minLength) {
        synchronized (byteArrays) {
            minByteArrayLength = Math.max(minByteArrayLength, minLength);

            while (!byteArrays.isEmpty()) {
                byte[] array = byteArrays.remove(0);
                if (array.length >= minLength)
                    return array;
            }

            return new byte[ minByteArrayLength];
        }
    }

    private static void storeScratchArray(byte[] array) {
        synchronized (byteArrays) {
            if (array.length >= minByteArrayLength && byteArrays.size() < 10) {
                byteArrays.add(array);
            }
        }
    }

    static void swapFirstAndThirdSamples_4samples(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destIndex = destOffset;
            int srcEnd = srcOffset + 4 * pixelCount;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; destIndex += 4) {
                destPixels[destIndex + 0] = sourcePixels[srcIndex++];
                byte swap1 = sourcePixels[srcIndex++];
                destPixels[destIndex + 2] = sourcePixels[srcIndex++];
                byte swap2 = sourcePixels[srcIndex++];
                destPixels[destIndex + 1] = swap2;
                destPixels[destIndex + 3] = swap1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                prependAlpha(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }
}
