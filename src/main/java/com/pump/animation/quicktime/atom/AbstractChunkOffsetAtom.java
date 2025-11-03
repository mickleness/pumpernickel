package com.pump.animation.quicktime.atom;

/**
 * This models the "stco" and "co64" atoms interchangeably.
 */
public abstract class AbstractChunkOffsetAtom extends LeafAtom {

    protected AbstractChunkOffsetAtom(Atom parent) {
        super(parent);
    }

    /**
     * Add a new chunk offset to this table.
     */
    public abstract void addChunkOffset(long dataStart);

    public abstract long getChunkOffset(int index);

    public abstract int getChunkOffsetCount();

    /**
     * Return a 1-byte specification of the version of this chunk offset atom.
     */
    public abstract int getVersion();

    /**
     * Return a 3-byte space for chunk offset flags. Set this field to 0.
     */
    public abstract int getFlags();

    /**
     * Set a chunk offset.
     *
     * @param index
     *            the element in the table to replace
     * @value the new value to insert into the table
     */
    public abstract void setChunkOffset(int index, long value);
}
