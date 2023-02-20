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
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            int sampleA = value & 0xff;
            int sampleB = (value >> 16) & 0xff;
            destPixels[destIndex++] = (value & 0xff00ff00) | sampleB | (sampleA << 16);
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
                convert_G_bytes_to_XYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
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

    static void convert_XYZA_bytes_to_AZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int destIndex = destOffset;
        int srcEnd = srcOffset + 4 * pixelCount;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            destPixels[destIndex++] = (sourcePixels[srcIndex++] & 0xff) |
                    ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    ((sourcePixels[srcIndex++] & 0xff) << 16) |
                    ((sourcePixels[srcIndex++] & 0xff) << 24) ;

        }
    }

    static void convert_AXYZ_ints_to_XYZ_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = (byte) ( (axyz & 0xff0000) >> 16);
                    destPixels[destIndex++] = (byte) ( (axyz & 0xff00) >> 8);
                    destPixels[destIndex++] = (byte) ( (axyz & 0xff));
                    break;
                default:
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff0000) >> 16) * alpha / 255);
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff00) >> 8) * alpha / 255);
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff) * alpha / 255 ));
                    break;
            }
        }
    }

    static void convert_AXYZ_bytes_to_XYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 3 * pixelCount;
            int srcIndex = srcOffset;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                int alpha = sourcePixels[srcIndex++] & 0xff;
                switch(alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        break;
                    default:
                        destPixels[dstIndex++] = (byte) ((sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        destPixels[dstIndex++] = (byte) ((sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        destPixels[dstIndex++] = (byte) ((sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_AXYZ_bytes_to_XYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_ints_to_ZYX_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int destIndex = destOffset;
        int srcEnd = srcOffset + pixelCount;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = (byte)( axyz & 0xff);
                    destPixels[destIndex++] = (byte)( (axyz & 0xff00) >> 8);
                    destPixels[destIndex++] = (byte)( (axyz & 0xff0000) >> 16);
                    break;
                default:
                    destPixels[destIndex++] = (byte)( (axyz & 0xff) * alpha / 255);
                    destPixels[destIndex++] = (byte)( ((axyz & 0xff00) >> 8) * alpha / 255);
                    destPixels[destIndex++] = (byte)( ((axyz & 0xff0000) >> 16) * alpha / 255);
                    break;
            }
        }
    }

    static void convert_AXYZ_bytes_to_ZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 3 * pixelCount;
            int srcIndex = srcOffset;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                int alpha = sourcePixels[srcIndex++] & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        byte x1 = sourcePixels[srcIndex++];
                        byte y1 = sourcePixels[srcIndex++];
                        byte z1 = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = (byte) (z1 * alpha / 255);
                        destPixels[dstIndex++] = (byte) (y1 * alpha / 255);
                        destPixels[dstIndex++] = (byte) (x1 * alpha / 255);
                        break;
                    default:
                        int x2 = (sourcePixels[srcIndex++] & 0xff);
                        int y2 = (sourcePixels[srcIndex++] & 0xff);
                        int z2 = (sourcePixels[srcIndex++] & 0xff);
                        destPixels[dstIndex++] = (byte) (z2 * alpha / 255);
                        destPixels[dstIndex++] = (byte) (y2 * alpha / 255);
                        destPixels[dstIndex++] = (byte) (x2 * alpha / 255);
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_AXYZ_bytes_to_ZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZA_bytes_to_XYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 3 * pixelCount;
            int srcIndex = srcOffset;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                int alpha = sourcePixels[srcIndex + 3] & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 4;
                        break;
                    case 255:
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        srcIndex++;
                        break;
                    default:
                        destPixels[dstIndex++] = (byte) ( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        destPixels[dstIndex++] = (byte) ( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        destPixels[dstIndex++] = (byte) ( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        srcIndex++;
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_XYZA_bytes_to_XYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZA_bytes_to_ZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 3 * pixelCount;
            int srcIndex = srcOffset;
            int x1, y1, z1;
            byte x2, y2, z2;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                int alpha = sourcePixels[srcIndex + 3] & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex+=4;
                        break;
                    case 255:
                        x2 = sourcePixels[srcIndex++];
                        y2 = sourcePixels[srcIndex++];
                        z2 = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = z2;
                        destPixels[dstIndex++] = y2;
                        destPixels[dstIndex++] = x2;
                        srcIndex++;
                        break;
                    default:
                        x1 = (sourcePixels[srcIndex++] & 0xff);
                        y1 = (sourcePixels[srcIndex++] & 0xff);
                        z1 = (sourcePixels[srcIndex++] & 0xff);
                        destPixels[dstIndex++] = (byte) (z1 * alpha / 255);
                        destPixels[dstIndex++] = (byte) (y1 * alpha / 255);
                        destPixels[dstIndex++] = (byte) (x1 * alpha / 255);
                        srcIndex++;
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_XYZA_bytes_to_ZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_ints_to_XYZ_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                int alpha = (value >> 24) & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[destIndex--] = 0;
                        break;
                    case 255:
                        destPixels[destIndex--] = value & 0xffffff;
                        break;
                    default:
                        destPixels[destIndex--] = (((value & 0xff0000) * alpha / 255) & 0xff0000) |
                                (((value & 0xff00) * alpha / 255) & 0xff00) |
                                ((value & 0xff) * alpha / 255) ;
                        break;
                }
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            int alpha = (value >> 24) & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = value & 0xffffff;
                    break;
                default:
                    destPixels[destIndex++] = (((value & 0xff0000) * alpha / 255) & 0xff0000) |
                            (((value & 0xff00) * alpha / 255) & 0xff00) |
                            ((value & 0xff) * alpha / 255) ;
                    break;
            }
        }
    }

    static void convert_AXYZ_ints_to_ZYX_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                int alpha = (value >> 24) & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[destIndex--] = 0;
                        break;
                    case 255:
                        destPixels[destIndex--] = ((value >> 16) & 0xff) |
                                (value & 0x00ff00) |
                                ((value & 0xff) << 16);
                        break;
                    default:
                        destPixels[destIndex--] = (((value >> 16) & 0xff) * alpha / 255) |
                                ((((value >> 8) & 0xff) * alpha / 255) << 8) |
                                (((value & 0xff) * alpha / 255) << 16);
                        break;
                }
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            int alpha = (value >> 24) & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = ((value >> 16) & 0xff) |
                            (value & 0x00ff00) |
                            ((value & 0xff) << 16);
                    break;
                default:
                    destPixels[destIndex++] = (((value >> 16) & 0xff) * alpha / 255) |
                            ((((value >> 8) & 0xff) * alpha / 255) << 8) |
                            (((value & 0xff) * alpha / 255) << 16);
                    break;
            }
        }
    }

    static void convert_AXYZ_bytes_to_XYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex++] & 0xff;
            int x = sourcePixels[srcIndex++] & 0xff;
            int y = sourcePixels[srcIndex++] & 0xff;
            int z = sourcePixels[srcIndex++] & 0xff;
            destPixels[destIndex++] = (((x * alpha / 255) & 0xff) << 16) |
                    (((y * alpha / 255) & 0xff) << 8) |
                    (((z * alpha / 255) & 0xff));
        }
    }

    static void convert_AXYZ_bytes_to_ZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        int x, y, z;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex++] & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 3;
                    break;
                case 255:
                    x = sourcePixels[srcIndex++] & 0xff;
                    y = sourcePixels[srcIndex++] & 0xff;
                    z = sourcePixels[srcIndex++] & 0xff;
                    destPixels[destIndex++] = (z << 16) |
                            (y << 8) |
                            x;
                    break;
                default:
                    x = sourcePixels[srcIndex++] & 0xff;
                    y = sourcePixels[srcIndex++] & 0xff;
                    z = sourcePixels[srcIndex++] & 0xff;
                    destPixels[destIndex++] = ((z * alpha / 255) << 16) |
                            ((y * alpha / 255) << 8) |
                            ((x * alpha / 255));
                    break;
            }
        }
    }

    static void convert_XYZA_bytes_to_XYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex + 3] & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 4;
                    break;
                case 255:
                    destPixels[destIndex++] = (( (sourcePixels[srcIndex++] & 0xff)) << 16) |
                            ((sourcePixels[srcIndex++] & 0xff) << 8) |
                            (sourcePixels[srcIndex++] & 0xff);
                    srcIndex++;
                    break;
                default:
                    destPixels[destIndex++] = ((( (sourcePixels[srcIndex++] & 0xff) * alpha / 255)) << 16) |
                            ((( (sourcePixels[srcIndex++] & 0xff) * alpha / 255) << 8)) |
                            ( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                    srcIndex++;
                    break;
            }
        }
    }

    static void convert_XYZA_bytes_to_ZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        int x, y, z;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex + 3] & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 4;
                    break;
                case 255:
                    x = sourcePixels[srcIndex++] & 0xff;
                    y = sourcePixels[srcIndex++] & 0xff;
                    z = sourcePixels[srcIndex++] & 0xff;
                    destPixels[destIndex++] = (z << 16) |
                            (y << 8) |
                            (x);
                    srcIndex++;
                    break;
                default:
                    x = sourcePixels[srcIndex++] & 0xff;
                    y = sourcePixels[srcIndex++] & 0xff;
                    z = sourcePixels[srcIndex++] & 0xff;
                    destPixels[destIndex++] = ((z * alpha / 255) << 16) |
                            ((y * alpha / 255) << 8) |
                            ((x * alpha / 255) );
                    srcIndex++;
                    break;
            }
        }
    }

    static void convert_AXYZPre_bytes_to_ZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 3 * pixelCount;
            int srcIndex = srcOffset;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                srcIndex++; // skip the A channel
                byte x = sourcePixels[srcIndex++];
                byte y = sourcePixels[srcIndex++];
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
                destPixels[dstIndex++] = y;
                destPixels[dstIndex++] = x;
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_AXYZPre_bytes_to_ZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZPre_bytes_to_XYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 3 * pixelCount;
            int srcIndex = srcOffset;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                // in most other cases if alpha == 0 (premultiplied or not) we make the RGB channels zero.
                // But in this case: it's more efficient to leave them alone. It's OK to assume our incoming
                // premultiplied bytes really are premultiplied. And if they're not: that's not our responsibility.

                srcIndex++; // skip the A channel
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
            }
        } else {
            byte[] scratch = getScratchArray(3 * pixelCount);
            try {
                convert_AXYZPre_bytes_to_XYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZPre_ints_to_AZYX_bytes(byte[] destPixels,
                                                   int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;

            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = -1;
                    destPixels[destIndex++] = (byte) (axyz & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 8) & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 16) & 0xff);
                    break;
                default:
                    destPixels[destIndex++] = (byte) alpha;
                    destPixels[destIndex++] = (byte) Math.min(255, ((axyz) & 0xff) * 255 / alpha);
                    destPixels[destIndex++] = (byte) Math.min(255, ((axyz >> 8) & 0xff) * 255 / alpha);
                    destPixels[destIndex++] = (byte) Math.min(255, ((axyz >> 16) & 0xff) * 255 / alpha);
                    break;
            }
        }
    }

    static void convert_AXYZPre_ints_to_AXYZ_bytes(byte[] destPixels,
                                                   int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;

            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = -1;
                    destPixels[destIndex++] = (byte) ((axyz >> 16) & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 8) & 0xff);
                    destPixels[destIndex++] = (byte) (axyz & 0xff);
                    break;
                default:
                    destPixels[destIndex++] = (byte) alpha;
                    destPixels[destIndex++] = (byte) (Math.min(255, (((axyz >> 16) & 0xff) * 255 / alpha)));
                    destPixels[destIndex++] = (byte) (Math.min(255, (((axyz >> 8) & 0xff) * 255 / alpha)));
                    destPixels[destIndex++] = (byte) (Math.min(255, ((axyz & 0xff) * 255 / alpha)));
                    break;
            }
        }
    }

    static void convert_AXYZPre_ints_to_ZYXA_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;

            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = (byte) (axyz & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 8) & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 16) & 0xff);
                    destPixels[destIndex++] = -1;
                    break;
                default:
                    destPixels[destIndex++] = (byte) (Math.min(255, (axyz & 0xff) * 255 / alpha ));
                    destPixels[destIndex++] = (byte) (Math.min(255, ( (axyz & 0xff00) >> 8) * 255 / alpha ));
                    destPixels[destIndex++] = (byte) (Math.min(255, ( (axyz & 0xff0000) >> 16) * 255 / alpha ));
                    destPixels[destIndex++] = (byte) alpha;
                    break;
            }
        }
    }

    static void convert_AXYZ_ints_to_AZYXPre_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;

            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = -1;
                    destPixels[destIndex++] = (byte) (axyz & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 8) & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 16) & 0xff);
                    break;
                default:
                    destPixels[destIndex++] = (byte) alpha;
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff) * alpha / 255));
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff00) >> 8) * alpha / 255);
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff0000) >> 16) * alpha / 255);
                    break;
            }
        }
    }

    static void convert_AXYZ_ints_to_AXYZPre_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;

            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = -1;
                    destPixels[destIndex++] = (byte) ((axyz >> 16) & 0xff);
                    destPixels[destIndex++] = (byte) ((axyz >> 8) & 0xff);
                    destPixels[destIndex++] = (byte) (axyz & 0xff);
                    break;
                default:
                    destPixels[destIndex++] = (byte) alpha;
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff0000) >> 16) * alpha / 255 );
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff00) >> 8) * alpha / 255 );
                    destPixels[destIndex++] = (byte) ( ((axyz & 0xff) * alpha / 255));
                    break;
            }
        }
    }

    static void convert_AXYZPre_bytes_to_AXYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex++] & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 3;
                    break;
                case 255:
                    destPixels[destIndex++] = 0xff000000 |
                            ((sourcePixels[srcIndex++] & 0xff) << 16) |
                            ((sourcePixels[srcIndex++] & 0xff) << 8) |
                            (sourcePixels[srcIndex++]);
                    break;
                default:
                    int x = Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha);
                    int y = Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha);
                    int z = Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha);
                    destPixels[destIndex++] = (alpha << 24) | (x << 16) | (y << 8) | (z);
            }
        }
    }

    static void convert_AXYZPre_bytes_to_AZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex++] & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 3;
                    break;
                case 255:
                    destPixels[destIndex++] = 0xff000000 |
                            (sourcePixels[srcIndex++]) |
                            ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    ((sourcePixels[srcIndex++] & 0xff) << 16);
                    break;
                default:
                    destPixels[destIndex++] = (alpha << 24) |
                            Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha) |
                            (Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha) << 8) |
                            (Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha) << 16);
            }
        }
    }

    static void convert_AXYZ_bytes_to_AXYZPre_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex++];
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 3;
                    break;
                case -1:
                    destPixels[destIndex++] = 0xff000000 |
                            ((sourcePixels[srcIndex++] & 0xff) << 16) |
                            ((sourcePixels[srcIndex++] & 0xff) << 8) |
                            (sourcePixels[srcIndex++] & 0xff);
                    break;
                default:
                    alpha = alpha & 0xff;
                    destPixels[destIndex++] = (alpha << 24) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255) << 16) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255) << 8) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255));
                    break;
            }
        }
    }

    static void convert_AXYZ_bytes_to_AZYXPre_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex++];
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 3;
                    break;
                case -1:
                    destPixels[destIndex++] = 0xff000000 |
                                (sourcePixels[srcIndex++] & 0xff) |
                                ((sourcePixels[srcIndex++] & 0xff) << 8) |
                                ((sourcePixels[srcIndex++] & 0xff) << 16);
                    break;
                default:
                    alpha = alpha & 0xff;
                    destPixels[destIndex++] = (alpha << 24) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255)) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255) << 8) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255) << 16);
                    break;
            }
        }
    }

    static void convert_XYZA_bytes_to_AZYXPre_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int alpha = sourcePixels[srcIndex + 3];
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    srcIndex += 4;
                    break;
                case -1:
                    destPixels[destIndex++] = 0xff000000 |
                            (sourcePixels[srcIndex++] & 0xff) |
                            ((sourcePixels[srcIndex++] & 0xff) << 8) |
                            ((sourcePixels[srcIndex++] & 0xff) << 16);
                    srcIndex++;
                    break;
                default:
                    alpha = alpha & 0xff;
                    destPixels[destIndex++] = (alpha << 24) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255)) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255) << 8) |
                            (((sourcePixels[srcIndex++] & 0xff) * alpha / 255) << 16);
                    srcIndex++;
            }
        }
    }

    static void convert_AXYZPre_ints_to_AXYZ_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                int alpha = (value >> 24) & 0xff;
                switch (alpha) {
                    case 255:
                        destPixels[destIndex--] = value;
                        break;
                    case 0:
                        destPixels[destIndex--] = 0;
                        break;
                    default:
                        destPixels[destIndex--] = (value & 0xff000000) |
                                (Math.min(255, ((value & 0xff0000) >> 16 ) * 255 / alpha) << 16) |
                                (Math.min(255, ((value & 0xff00) >> 8) * 255 / alpha) << 8) |
                                Math.min(255, (value & 0xff) * 255 / alpha);
                        break;
                }
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            int alpha = (value >> 24) & 0xff;
            switch (alpha) {
                case 255:
                    destPixels[destIndex++] = value;
                    break;
                case 0:
                    destPixels[destIndex++] = 0;
                    break;
                default:
                    destPixels[destIndex++] = (value & 0xff000000) |
                            (Math.min(255, ((value & 0xff0000) >> 16 ) * 255 / alpha) << 16) |
                            (Math.min(255, ((value & 0xff00) >> 8) * 255 / alpha) << 8) |
                            Math.min(255, (value & 0xff) * 255 / alpha);
                    break;
            }
        }
    }

    static void convert_AXYZ_ints_to_AXYZPre_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                int alpha = (value >> 24) & 0xff;
                switch (alpha) {
                    case 255:
                        destPixels[destIndex--] = value;
                        break;
                    case 0:
                        destPixels[destIndex--] = 0;
                        break;
                    default:
                        destPixels[destIndex--] = (value & 0xff000000) |
                                ((((value & 0xff0000) >> 16) * alpha / 255) << 16) |
                                ((((value & 0xff00) >> 8) * alpha / 255) << 8) |
                                (((value & 0xff) * alpha / 255));
                        break;
                }
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            int alpha = (value >> 24) & 0xff;
            switch (alpha) {
                case 255:
                    destPixels[destIndex++] = value;
                    break;
                case 0:
                    destPixels[destIndex++] = 0;
                    break;
                default:
                    destPixels[destIndex++] = (value & 0xff000000) |
                            ((((value & 0xff0000) >> 16) * alpha / 255) << 16) |
                            ((((value & 0xff00) >> 8) * alpha / 255) << 8) |
                            (((value & 0xff) * alpha / 255));
            }
        }
    }

    static void convert_AXYZPre_ints_to_ZYX_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                destPixels[destIndex--] = ((value >> 16) & 0xff) |
                        ((value >> 8) & 0xff00) |
                        ((value << 16) & 0xff0000);
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = ((value >> 16) & 0xff) |
                    (value & 0xff00) |
                    ((value << 16) & 0xff0000);
        }
    }

    static void convert_AXYZPre_ints_to_XYZ_ints(int[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels == sourcePixels && destOffset > srcOffset) {
            int destIndex = destOffset + pixelCount - 1;
            for (int srcIndex = srcOffset + pixelCount - 1; srcIndex >= srcOffset;) {
                int value = sourcePixels[srcIndex--];
                destPixels[destIndex--] = value & 0xffffff;
            }
            return;
        }
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int value = sourcePixels[srcIndex++];
            destPixels[destIndex++] = value & 0xffffff;
        }
    }

    static void convert_AXYZPre_bytes_to_ZYX_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int dstIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            srcIndex++;
            destPixels[dstIndex++] = ((sourcePixels[srcIndex++] & 0xff)) |
                    ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    ((sourcePixels[srcIndex++] & 0xff) << 16);
        }
    }

    static void convert_AXYZPre_bytes_to_XYZ_ints(int[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + 4 * pixelCount;
        int dstIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            srcIndex++;
            destPixels[dstIndex++] = ((sourcePixels[srcIndex++] & 0xff) << 16) |
                    ((sourcePixels[srcIndex++] & 0xff) << 8) |
                    ((sourcePixels[srcIndex++] & 0xff));
        }
    }

    static void convert_AXYZPre_bytes_to_AXYZ_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                int alpha = sourcePixels[srcIndex] & 0xff;
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        break;
                    default:
                        destPixels[dstIndex++] = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        destPixels[dstIndex++] = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        destPixels[dstIndex++] = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZPre_bytes_to_AXYZ_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZPre_bytes_to_AZYX_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            byte x, z, y;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                int alpha = sourcePixels[srcIndex] & 0xff;
                destPixels[dstIndex++] = sourcePixels[srcIndex++];
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        x = sourcePixels[srcIndex++];
                        y = sourcePixels[srcIndex++];
                        z = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = z;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = x;
                        break;
                    default:
                        x = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        y = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        z = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        destPixels[dstIndex++] = z;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = x;
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZPre_bytes_to_AZYX_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZPre_bytes_to_ZYXA_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            byte x, z, y, a;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                a = sourcePixels[srcIndex++];
                int alpha = a & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        x = sourcePixels[srcIndex++];
                        y = sourcePixels[srcIndex++];
                        z = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = z;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = x;
                        destPixels[dstIndex++] = a;
                        break;
                    default:
                        x = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        y = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        z = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        destPixels[dstIndex++] = z;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = x;
                        destPixels[dstIndex++] = a;
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZPre_bytes_to_ZYXA_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZPre_bytes_to_XYZA_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            byte a;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                a = sourcePixels[srcIndex++];
                int alpha = a & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = a;
                        break;
                    default:
                        destPixels[dstIndex++] = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        destPixels[dstIndex++] = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        destPixels[dstIndex++] = (byte)( Math.min(255, (sourcePixels[srcIndex++] & 0xff) * 255 / alpha));
                        destPixels[dstIndex++] = a;
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZPre_bytes_to_XYZA_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_bytes_to_AXYZPre_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                byte a = destPixels[dstIndex++] = sourcePixels[srcIndex++];
                int alpha = a & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = sourcePixels[srcIndex++];
                        break;
                    default:
                        destPixels[dstIndex++] = (byte)( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        destPixels[dstIndex++] = (byte)( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        destPixels[dstIndex++] = (byte)( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZ_bytes_to_AXYZPre_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_bytes_to_AZYXPre_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            byte x, z, y, a;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                a = destPixels[dstIndex++] = sourcePixels[srcIndex++];
                int alpha = a & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case 255:
                        x = sourcePixels[srcIndex++];
                        y = sourcePixels[srcIndex++];
                        z = sourcePixels[srcIndex++];
                        destPixels[dstIndex++] = z;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = x;
                        break;
                    default:
                        x = (byte)( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        y = (byte)( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        z = (byte)( (sourcePixels[srcIndex++] & 0xff) * alpha / 255);
                        destPixels[dstIndex++] = z;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = x;
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_AXYZ_bytes_to_AZYXPre_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZA_bytes_to_AXYZPre_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            byte x, z, y, a;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                x = sourcePixels[srcIndex++];
                y = sourcePixels[srcIndex++];
                z = sourcePixels[srcIndex++];
                a = sourcePixels[srcIndex++];
                int alpha = a & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        break;
                    case 255:
                        destPixels[dstIndex++] = a;
                        destPixels[dstIndex++] = x;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = z;
                        break;
                    default:
                        destPixels[dstIndex++] = a;
                        destPixels[dstIndex++] = (byte)( ((x & 0xff) * alpha / 255));
                        destPixels[dstIndex++] = (byte)( ((y & 0xff) * alpha / 255));
                        destPixels[dstIndex++] = (byte)( ((z & 0xff) * alpha / 255));
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_XYZA_bytes_to_AXYZPre_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZA_bytes_to_AZYXPre_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int dstEnd = destOffset + 4 * pixelCount;
            int srcIndex = srcOffset;
            byte x, z, y, a;
            for (int dstIndex = destOffset; dstIndex < dstEnd;) {
                x = sourcePixels[srcIndex++];
                y = sourcePixels[srcIndex++];
                z = sourcePixels[srcIndex++];
                a = sourcePixels[srcIndex++];
                int alpha = a & 0xff;
                switch (alpha) {
                    case 0:
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        destPixels[dstIndex++] = 0;
                        break;
                    case 255:
                        destPixels[dstIndex++] = a;
                        destPixels[dstIndex++] = z;
                        destPixels[dstIndex++] = y;
                        destPixels[dstIndex++] = x;
                        break;
                    default:
                        destPixels[dstIndex++] = a;
                        destPixels[dstIndex++] = (byte)( ((z & 0xff) * alpha) >> 8);
                        destPixels[dstIndex++] = (byte)( ((y & 0xff) * alpha) >> 8);
                        destPixels[dstIndex++] = (byte)( ((x & 0xff) * alpha) >> 8);
                }
            }
        } else {
            byte[] scratch = getScratchArray(4 * pixelCount);
            try {
                convert_XYZA_bytes_to_AZYXPre_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZ_ints_to_G_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    break;
                case 255:
                    destPixels[destIndex++] = (byte) ( (((axyz >> 16) & 0xff) + ((axyz >> 8) & 0xff) + (axyz & 0xff)) / 3);
                    break;
                default:
                    destPixels[destIndex++] = (byte) ( (((axyz >> 16) & 0xff) + ((axyz >> 8) & 0xff) + (axyz & 0xff)) * alpha / 765);
                    break;
            }
        }
    }

    static void convert_AXYZ_bytes_to_G_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int srcEnd = srcOffset + 4 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
                byte a = sourcePixels[srcIndex++];
                switch(a) {
                    case 0:
                        destPixels[destIndex++] = 0;
                        srcIndex += 3;
                        break;
                    case -1:
                        destPixels[destIndex++] =  (byte) (((sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++])) / 3);
                        break;
                    default:
                        int alpha = a & 0xff;
                        destPixels[destIndex++] =  (byte) ( (((sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++])) * alpha / 765) & 0xff );
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(pixelCount);
            try {
                convert_AXYZ_bytes_to_G_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_AXYZPre_ints_to_G_bytes(byte[] destPixels, int destOffset, int[] sourcePixels, int srcOffset, int pixelCount) {
        int srcEnd = srcOffset + pixelCount;
        int destIndex = destOffset;
        for (int srcIndex = srcOffset; srcIndex < srcEnd;) {
            int axyz = sourcePixels[srcIndex++];
            int alpha = (axyz >> 24) & 0xff;
            switch (alpha) {
                case 0:
                    destPixels[destIndex++] = 0;
                    break;
                default:
                    destPixels[destIndex++] = (byte) ( (((axyz >> 16) & 0xff) + ((axyz >> 8) & 0xff) + (axyz & 0xff)) / 3);
                    break;
            }
        }
    }

    static void convert_AXYZPre_bytes_to_G_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int srcEnd = srcOffset + 4 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
                byte a = sourcePixels[srcIndex++];
                switch(a) {
                    case 0:
                        destPixels[destIndex++] = 0;
                        break;
                    default:
                        destPixels[destIndex++] =  (byte) ( ((sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++])) / 3);
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(pixelCount);
            try {
                convert_AXYZPre_bytes_to_G_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }

    static void convert_XYZA_bytes_to_G_bytes(byte[] destPixels, int destOffset, byte[] sourcePixels, int srcOffset, int pixelCount) {
        if (destPixels != sourcePixels || destOffset == srcOffset) {
            int srcEnd = srcOffset + 4 * pixelCount;
            int destIndex = destOffset;
            for (int srcIndex = srcOffset; srcIndex < srcEnd; ) {
                byte a = sourcePixels[srcIndex + 3];
                switch(a) {
                    case 0:
                        destPixels[destIndex++] = 0;
                        srcIndex += 4;
                        break;
                    case -1:
                        destPixels[destIndex++] =  (byte) (((sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++])) / 3);
                        srcIndex++;
                        break;
                    default:
                        int alpha = a & 0xff;
                        destPixels[destIndex++] =  (byte) ( ((((sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++] & 0xff) + (sourcePixels[srcIndex++])) * alpha / 3) >> 8) & 0xff );
                        srcIndex++;
                        break;
                }
            }
        } else {
            byte[] scratch = getScratchArray(pixelCount);
            try {
                convert_XYZA_bytes_to_G_bytes(scratch, 0, sourcePixels, srcOffset, pixelCount);
                System.arraycopy(scratch, 0, destPixels, destOffset, pixelCount);
            } finally {
                storeScratchArray(scratch);
            }
        }
    }
}
