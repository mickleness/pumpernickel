package com.pump.image.pixel2.converter;

import java.util.LinkedList;

class ConverterUtils {

    /**
     * Convert RGB ints into BGR bytes
     */
    static void swapFirstAndThirdSamples(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                int sampleA = value & 0xff;
                int sampleB = (value >> 16) & 0xff;
                destPixels[destIndex--] = (value & 0xff00ff00) | sampleB | (sampleA << 16);
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
     * Convert RGB bytes into ARGB int, where the alpha channel is assumed to be 255.
     */
    public static void prependAlpha(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = 0xff000000 |
                    ((sourcePixels[srcIndex++] & 0xff) << 16) |
                    ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    (sourcePixels[srcIndex++] & 0xff);
        }
    }


    /**
     * Convert RGB bytes into ABGR int, where the alpha channel is assumed to be 255.
     */
    public static void prependAlpha_swapFirstAndThirdSamples(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = 0xff000000 |
                    ((int)(sourcePixels[srcIndex++] & 0xff)) |
                    ((int)(sourcePixels[srcIndex++] & 0xff) << 8) |
                    ((int)(sourcePixels[srcIndex++] & 0xff) << 16);
        }
    }

    /**
     * Convert RGB bytes into ARGB bytes, where the alpha channel is assumed to be 255.
     */
    static void prependAlpha(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int destEnd = destOffset + 4 * pixelCount;
        int srcEnd = srcOffset + 3 * pixelCount;
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            // we'll iterate RTL:
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

    /**
     * Convert RGB ints into ARGB bytes, where the alpha channel is assumed to be 255.
     */
    public static void prependAlpha(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = -1;
            destPixels[destIndex++] = (byte) ((value >> 16) & 0xff);
            destPixels[destIndex++] = (byte) ((value >> 8) & 0xff);
            destPixels[destIndex++] = (byte) (value & 0xff);
        }
    }

    /**
     * Convert RGB ints into ABGR bytes, where the alpha channel is assumed to be 255.
     */
    public static void prependAlpha_swapFirstAndThirdSamples(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = -1;
            destPixels[destIndex++] = (byte) (value & 0xff);
            destPixels[destIndex++] = (byte) ((value >> 8) & 0xff);
            destPixels[destIndex++] = (byte) ((value >> 16) & 0xff);
        }
    }

    /**
     * Convert RGB bytes into ABGR bytes, where the alpha channel is assumed to be 255.
     */
    public static void prependAlpha_swapFirstAndThirdSamples(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int destEnd = destOffset + 4 * pixelCount;
        int srcEnd = srcOffset + 3 * pixelCount;
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            // we'll iterate RTL:
            int dstIndex = destEnd - 1;
            for (int srcIndex = srcEnd - 1; srcIndex >= srcOffset; ) {
                byte v1 = sourcePixels[srcIndex--];
                byte v2 = sourcePixels[srcIndex--];
                byte v3 = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = v3;
                destPixels[dstIndex--] = v2;
                destPixels[dstIndex--] = v1;
                destPixels[dstIndex--] = -1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                prependAlpha_swapFirstAndThirdSamples(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    /**
     * Convert RGB ints into ARGB ints, where the alpha channel is assumed to be 255.
     */
    public static void prependAlpha(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int alpha = 0xff000000;

        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                destPixels[destIndex--] = alpha | (sourcePixels[srcIndex--] & 0xffffff);
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            destPixels[destIndex++] = alpha | (sourcePixels[srcIndex++] & 0xffffff);
        }
    }

    /**
     * Convert RGB ints into ABGR ints, where the alpha channel is assumed to be 255.
     */
    public static void prependAlpha_swapFirstAndThirdSamples(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int alpha = 0xff000000;

        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                int sampleA = value & 0xff;
                int sampleB = (value >> 16) & 0xff;
                destPixels[destIndex--] = alpha | (value & 0xff00) | sampleB | (sampleA << 16);
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            int sampleA = value & 0xff;
            int sampleB = (value >> 16) & 0xff;
            destPixels[destIndex++] = alpha | (value & 0xff00) | sampleB | (sampleA << 16);
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

    /**
     * Convert ARGB bytes into ABGR bytes.
     */
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
                swapFirstAndThirdSamples_4samples(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    /**
     * Convert RGB bytes into RGB ints.
     */
    static void convert3samples(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = (((sourcePixels[srcIndex++]) & 0xff) << 16) | (((sourcePixels[srcIndex++]) & 0xff) << 8) | ((sourcePixels[srcIndex++]) & 0xff);
        }
    }

    /**
     * Convert RGB bytes into BGR ints.
     */
    static void convert3samples_swapFirstAndThirdSamples(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = ((sourcePixels[srcIndex++]) & 0xff) | (((sourcePixels[srcIndex++]) & 0xff) << 8) | (((sourcePixels[srcIndex++]) & 0xff) << 16);
        }
    }

    /**
     * Convert RGB ints into grayscale bytes.
     */
    public static void average3Samples(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte) ( ( ((value >> 16) & 0xff) + ((value >> 8) & 0xff) + (value & 0xff)) / 3);
        }
    }

    /**
     * Convert RGB bytes into grayscale bytes.
     */
    public static void average3Samples(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int srcEnd = srcOffset + 3 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
                destPixels[destIndex++] = (byte) ( ( ((sourcePixels[srcIndex++]) & 0xff) + ((sourcePixels[srcIndex++]) & 0xff) + ((sourcePixels[srcIndex++]) & 0xff)) / 3);
            }
        } else {
            byte[] scratch = getScratchArray(pixelCount);
            try {
                average3Samples(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    /** Convert ARGB ints into ARGB bytes. */
    public static void convert4samples(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte) ((value >> 24) & 0xff);
            destPixels[destIndex++] = (byte) ((value >> 16) & 0xff);
            destPixels[destIndex++] = (byte) ((value >> 8) & 0xff);
            destPixels[destIndex++] = (byte) (value & 0xff);
        }
    }

    /** Convert ARGB ints into ABGR bytes. */
    public static void convert4samples_swapFirstAndThird(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte) ((value >> 24) & 0xff);
            destPixels[destIndex++] = (byte) (value & 0xff);
            destPixels[destIndex++] = (byte) ((value >> 8) & 0xff);
            destPixels[destIndex++] = (byte) ((value >> 16) & 0xff);
        }
    }

    /** Convert ARGB bytes into ARGB ints. */
    public static void convert4samples(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] =
                    ((sourcePixels[srcIndex++] & 0xff) << 24) |
                    ((sourcePixels[srcIndex++] & 0xff) << 16) |
                    ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    ((sourcePixels[srcIndex++] & 0xff));
        }
    }

    /** Convert ARGB bytes into ABGR ints. */
    public static void convert4samples_swapFirstAndThird(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] =
                    ((sourcePixels[srcIndex++] & 0xff) << 24) |
                            ((sourcePixels[srcIndex++] & 0xff)) |
                            ((sourcePixels[srcIndex++] & 0xff) << 8) |
                            ((sourcePixels[srcIndex++] & 0xff) << 16);
        }
    }
}
