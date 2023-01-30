package com.pump.image.pixel2.converter;

import java.util.LinkedList;

class ConverterUtils {

    static void convert_XYZ_ints_to_ZYX_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_XYZ_bytes_to_AXYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = 0xff000000 |
                    ((sourcePixels[srcIndex++] & 0xff) << 16) |
                    ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    (sourcePixels[srcIndex++] & 0xff);
        }
    }

    static void convert_XYZ_bytes_to_AZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = 0xff000000 |
                    (sourcePixels[srcIndex++] & 0xff) |
                    ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    ((sourcePixels[srcIndex++] & 0xff) << 16);
        }
    }

    static void convert_XYZ_bytes_to_AXYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destEnd = destOffset + 4 * pixelCount;
            int srcEnd = srcOffset + 3 * pixelCount;

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
                convert_XYZ_bytes_to_AXYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZ_ints_to_AXYZ_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_XYZ_ints_to_AZYX_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_XYZ_bytes_to_AZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destEnd = destOffset + 4 * pixelCount;
            int srcEnd = srcOffset + 3 * pixelCount;

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
                convert_XYZ_bytes_to_AZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZ_ints_to_AXYZ_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_XYZ_ints_to_AZYX_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
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

    private static final LinkedList<byte[]> byteArrays = new LinkedList<>();
    private static int minByteArrayLength = 4000;

    private static byte[] getScratchArray(int minLength) {
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

    // TODO: once finished: search this class for `storeScratchArray` and check against copy-paste misuse
    private static void storeScratchArray(byte[] array) {
        synchronized (byteArrays) {
            if (array.length >= minByteArrayLength && byteArrays.size() < 10) {
                byteArrays.add(array);
            }
        }
    }

    static void convert_AXYZ_bytes_to_AZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destIndex = destOffset;
            int srcEnd = srcOffset + 4 * pixelCount;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; destIndex += 4) {
                destPixels[destIndex] = sourcePixels[srcIndex++];
                byte swap1 = sourcePixels[srcIndex++];
                destPixels[destIndex + 2] = sourcePixels[srcIndex++];
                byte swap2 = sourcePixels[srcIndex++];
                destPixels[destIndex + 1] = swap2;
                destPixels[destIndex + 3] = swap1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZ_bytes_to_AZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZ_bytes_to_XYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = (((sourcePixels[srcIndex++]) & 0xff) << 16) | (((sourcePixels[srcIndex++]) & 0xff) << 8) | ((sourcePixels[srcIndex++]) & 0xff);
        }
    }

    static void convert_XYZ_bytes_to_ZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 3 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            destPixels[destIndex++] = ((sourcePixels[srcIndex++]) & 0xff) | (((sourcePixels[srcIndex++]) & 0xff) << 8) | (((sourcePixels[srcIndex++]) & 0xff) << 16);
        }
    }

    static void convert_XYZ_ints_to_G_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte) ( ( ((value >> 16) & 0xff) + ((value >> 8) & 0xff) + (value & 0xff)) / 3);
        }
    }

    static void convert_XYZ_bytes_to_G_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int srcEnd = srcOffset + 3 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
                destPixels[destIndex++] = (byte) ( ( ((sourcePixels[srcIndex++]) & 0xff) + ((sourcePixels[srcIndex++]) & 0xff) + ((sourcePixels[srcIndex++]) & 0xff)) / 3);
            }
        } else {
            byte[] scratch = getScratchArray(pixelCount);
            try {
                convert_XYZ_bytes_to_G_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_ints_to_AXYZ_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_AXYZ_ints_to_AZYX_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_AXYZ_bytes_to_AXYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_AXYZ_bytes_to_AZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
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

    static void convert_G_bytes_to_AXYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destIndex = destOffset + 4 * pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset; ) {
                destPixels[destIndex--] = sourcePixels[srcIndex];
                destPixels[destIndex--] = sourcePixels[srcIndex];
                destPixels[destIndex--] = sourcePixels[srcIndex--];
                destPixels[destIndex--] = -1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_G_bytes_to_AXYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_G_bytes_to_XYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destIndex = destOffset + 3 * pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset; ) {
                destPixels[destIndex--] = sourcePixels[srcIndex];
                destPixels[destIndex--] = sourcePixels[srcIndex];
                destPixels[destIndex--] = sourcePixels[srcIndex--];
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_G_bytes_to_AXYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_G_bytes_to_AXYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int dstIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int gray = sourcePixels[srcIndex++] & 0xff;
            destPixels[dstIndex++] = 0xff000000 | (gray << 16) | (gray << 8) | gray;
        }
    }

    static void convert_G_bytes_to_XYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int dstIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int gray = sourcePixels[srcIndex++] & 0xff;
            destPixels[dstIndex++] = (gray << 16) | (gray << 8) | gray;
        }
    }

    static void convert_XYZ_ints_to_XYZ_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int xyz = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte)( (xyz >> 16) & 0xff);
            destPixels[destIndex++] = (byte)( (xyz >> 8) & 0xff);
            destPixels[destIndex++] = (byte)( xyz & 0xff);
        }
    }

    static void convert_XYZ_ints_to_ZYX_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int xyz = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte)( xyz & 0xff);
            destPixels[destIndex++] = (byte)( (xyz >> 8) & 0xff);
            destPixels[destIndex++] = (byte)( (xyz >> 16) & 0xff);
        }
    }

    static void convert_XYZ_bytes_to_ZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            int dstEnd = destOffset + 3 * pixelCount;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                byte v1 = destPixels[dstIndex];
                byte v2 = destPixels[dstIndex + 2];
                destPixels[dstIndex + 2] = v1;
                destPixels[dstIndex] = v2;
                dstIndex += 3;
            }
        } else if (destPixels != sourcePixels) {
            int srcEnd = srcOffset + 3 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
                byte v1 = sourcePixels[srcIndex++];
                byte v2 = sourcePixels[srcIndex++];
                byte v3 = sourcePixels[srcIndex++];
                destPixels[destIndex++] = v3;
                destPixels[destIndex++] = v2;
                destPixels[destIndex++] = v1;
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_XYZ_bytes_to_ZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_ints_to_ZYXA_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int destIndex = destOffset;
        int srcEnd = srcOffset + pixelCount;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte)( axyz & 0xff );
            destPixels[destIndex++] = (byte)( (axyz >> 8) & 0xff );
            destPixels[destIndex++] = (byte)( (axyz >> 16) & 0xff );
            destPixels[destIndex++] = (byte)( (axyz >> 24) & 0xff );
        }
    }

    static void convert_XYZ_ints_to_XYZA_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int destIndex = destOffset;
        int srcEnd = srcOffset + pixelCount;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte)( (axyz >> 16) & 0xff );
            destPixels[destIndex++] = (byte)( (axyz >> 8) & 0xff );
            destPixels[destIndex++] = (byte)( axyz & 0xff );
            destPixels[destIndex++] = -1;
        }
    }

    static void convert_XYZ_ints_to_ZYXA_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int destIndex = destOffset;
        int srcEnd = srcOffset + pixelCount;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            destPixels[destIndex++] = (byte)( axyz & 0xff );
            destPixels[destIndex++] = (byte)( (axyz >> 8) & 0xff );
            destPixels[destIndex++] = (byte)( (axyz >> 16) & 0xff );
            destPixels[destIndex++] = -1;
        }
    }

    static void convert_AXYZ_bytes_to_ZYXA_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                byte v1 = destPixels[dstIndex];
                byte v2 = destPixels[dstIndex + 1];
                byte v3 = destPixels[dstIndex + 2];
                byte v4 = destPixels[dstIndex + 3];

                destPixels[dstIndex++] = v4;
                destPixels[dstIndex++] = v3;
                destPixels[dstIndex++] = v2;
                destPixels[dstIndex++] = v1;
            }
        } else if (destPixels != sourcePixels) {
            int srcEnd = srcOffset + 4 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
                byte v1 = sourcePixels[srcIndex++];
                byte v2 = sourcePixels[srcIndex++];
                byte v3 = sourcePixels[srcIndex++];
                byte v4 = sourcePixels[srcIndex++];
                destPixels[destIndex++] = v4;
                destPixels[destIndex++] = v3;
                destPixels[destIndex++] = v2;
                destPixels[destIndex++] = v1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZ_bytes_to_ZYXA_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_bytes_to_XYZA_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                byte v1 = destPixels[dstIndex];
                byte v2 = destPixels[dstIndex + 1];
                byte v3 = destPixels[dstIndex + 2];
                byte v4 = destPixels[dstIndex + 3];

                destPixels[dstIndex++] = v2;
                destPixels[dstIndex++] = v3;
                destPixels[dstIndex++] = v4;
                destPixels[dstIndex++] = v1;
            }
        } else if (destPixels != sourcePixels) {
            int srcEnd = srcOffset + 4 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
                byte v1 = sourcePixels[srcIndex++];
                byte v2 = sourcePixels[srcIndex++];
                byte v3 = sourcePixels[srcIndex++];
                byte v4 = sourcePixels[srcIndex++];
                destPixels[destIndex++] = v2;
                destPixels[destIndex++] = v3;
                destPixels[destIndex++] = v4;
                destPixels[destIndex++] = v1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZ_bytes_to_XYZA_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_G_bytes_to_XYZA_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destEnd = destOffset + 4 * pixelCount;
            int srcEnd = srcOffset + pixelCount;

            // we'll iterate RTL:
            int dstIndex = destEnd - 1;
            for (int srcIndex = srcEnd - 1; srcIndex >= srcOffset; ) {
                byte g = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = -1;
                destPixels[dstIndex--] = g;
                destPixels[dstIndex--] = g;
                destPixels[dstIndex--] = g;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_G_bytes_to_XYZA_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZ_bytes_to_ZYXA_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destEnd = destOffset + 4 * pixelCount;
            int srcEnd = srcOffset + 3 * pixelCount;

            // we'll iterate RTL:
            int dstIndex = destEnd - 1;
            for (int srcIndex = srcEnd - 1; srcIndex >= srcOffset; ) {
                byte z = sourcePixels[srcIndex--];
                byte y = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = -1;
                destPixels[dstIndex--] = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = y;
                destPixels[dstIndex--] = z;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_XYZ_bytes_to_ZYXA_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZ_bytes_to_XYZA_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int destEnd = destOffset + 4 * pixelCount;
            int srcEnd = srcOffset + 3 * pixelCount;

            // we'll iterate RTL:
            int dstIndex = destEnd - 1;
            for (int srcIndex = srcEnd - 1; srcIndex >= srcOffset; ) {
                destPixels[dstIndex--] = -1;
                destPixels[dstIndex--] = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = sourcePixels[srcIndex--];
                destPixels[dstIndex--] = sourcePixels[srcIndex--];
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_XYZ_bytes_to_XYZA_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, 4 * pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZA_bytes_to_AXYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                byte v1 = destPixels[dstIndex];
                byte v2 = destPixels[dstIndex + 1];
                byte v3 = destPixels[dstIndex + 2];
                byte v4 = destPixels[dstIndex + 3];

                destPixels[dstIndex++] = v4;
                destPixels[dstIndex++] = v1;
                destPixels[dstIndex++] = v2;
                destPixels[dstIndex++] = v3;
            }
        } else if (destPixels != sourcePixels) {
            int srcEnd = srcOffset + 4 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
                byte v1 = sourcePixels[srcIndex++];
                byte v2 = sourcePixels[srcIndex++];
                byte v3 = sourcePixels[srcIndex++];
                byte v4 = sourcePixels[srcIndex++];
                destPixels[destIndex++] = v4;
                destPixels[destIndex++] = v1;
                destPixels[destIndex++] = v2;
                destPixels[destIndex++] = v3;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_XYZA_bytes_to_AXYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZA_bytes_to_AZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                byte v1 = destPixels[dstIndex];
                byte v2 = destPixels[dstIndex + 1];
                byte v3 = destPixels[dstIndex + 2];
                byte v4 = destPixels[dstIndex + 3];

                destPixels[dstIndex++] = v4;
                destPixels[dstIndex++] = v3;
                destPixels[dstIndex++] = v2;
                destPixels[dstIndex++] = v1;
            }
        } else if (destPixels != sourcePixels) {
            int srcEnd = srcOffset + 4 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
                byte v1 = sourcePixels[srcIndex++];
                byte v2 = sourcePixels[srcIndex++];
                byte v3 = sourcePixels[srcIndex++];
                byte v4 = sourcePixels[srcIndex++];
                destPixels[destIndex++] = v4;
                destPixels[destIndex++] = v3;
                destPixels[destIndex++] = v2;
                destPixels[destIndex++] = v1;
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_XYZA_bytes_to_AZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }
}
