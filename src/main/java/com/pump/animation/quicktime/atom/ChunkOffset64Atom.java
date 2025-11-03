package com.pump.animation.quicktime.atom;

import com.pump.io.GuardedOutputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is the "co64" atom capable of writing mov files over 4GB. See also the legacy
 * {@link ChunkOffsetAtom}.
 */
public class ChunkOffset64Atom extends AbstractChunkOffsetAtom {

    /** "co64" */
    public static final String ATOM_TYPE = "co64";

    protected int version = 0;
    protected int flags = 0;
    protected long[] offsetTable = new long[0];

    public ChunkOffset64Atom(int version, int flags) {
        super(null);
        this.version = version;
        this.flags = flags;
    }

    public ChunkOffset64Atom() {
        super(null);
    }

    public ChunkOffset64Atom(Atom parent, InputStream in) throws IOException {
        super(parent);
        version = in.read();
        flags = read24Int(in);
        int arraySize = (int) read32Int(in);
        offsetTable = new long[arraySize];
        for (int a = 0; a < offsetTable.length; a++) {
            offsetTable[a] = read64Int(in);
        }
    }

    @Override
    public long getChunkOffset(int index) {
        return offsetTable[index];
    }

    @Override
    public int getChunkOffsetCount() {
        return offsetTable.length;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public void setChunkOffset(int index, long value) {
        offsetTable[index] = value;
    }

    @Override
    public void addChunkOffset(long offset) {
        long[] newArray = new long[offsetTable.length + 1];
        System.arraycopy(offsetTable, 0, newArray, 0, offsetTable.length);
        newArray[newArray.length - 1] = offset;
        offsetTable = newArray;
    }

    @Override
    public String getIdentifier() {
        return ATOM_TYPE;
    }

    @Override
    protected long getSize() {
        // size = header (8) + version/flags (4) + count (4) + entries * 8 bytes
        return 16 + offsetTable.length * 8;
    }

    @Override
    protected void writeContents(GuardedOutputStream out) throws IOException {
        out.write(version);
        write24Int(out, flags);
        write32Int(out, offsetTable.length);
        for (long offset : offsetTable) {
            write64Int(out, offset);
        }
    }

    @Override
    public String toString() {
        String entriesString;
        if (offsetTable.length > 50 && ABBREVIATE) {
            entriesString = "[ ... ]";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            for (int a = 0; a < offsetTable.length; a++) {
                if (a != 0) {
                    sb.append(", ");
                }
                sb.append(offsetTable[a]);
            }
            sb.append(" ]");
            entriesString = sb.toString();
        }

        return "ChunkOffset64Atom[ version=" + version + ", flags=" + flags
                + ", sizeTable=" + entriesString + " ]";
    }
}
