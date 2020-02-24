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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.pump.io.GuardedOutputStream;

/**
 * As samples are added to a media, they are collected into chunks that allow
 * optimized data access. A chunk contains one or more samples. Chunks in a
 * media may have different sizes, and the samples within a chunk may have
 * different sizes. The sample-to-chunk block stores chunk information for the
 * samples in a media.
 * <p>
 * Sample-to-chunk blocks have an block type of 'stsc'. The sample-to-chunk
 * block contains a table that maps samples to chunks in the media data stream.
 * By examining the sample-to-chunk block, you can determine the chunk that
 * contains a specific sample.
 */
public class SampleToChunkBlock extends LeafBlock {

	public static class SampleToChunkEntry {
		long firstChunk, samplesPerChunk, sampleDescriptionID;

		public SampleToChunkEntry(long firstChunk, long samplesPerChunk,
				long sampleDescriptionID) {
			this.firstChunk = firstChunk;
			this.samplesPerChunk = samplesPerChunk;
			this.sampleDescriptionID = sampleDescriptionID;
		}

		public SampleToChunkEntry(InputStream in) throws IOException {
			firstChunk = Block.read32Int(in);
			samplesPerChunk = Block.read32Int(in);
			sampleDescriptionID = Block.read32Int(in);
		}

		@Override
		public String toString() {
			return "[ " + firstChunk + ", " + samplesPerChunk + ", "
					+ sampleDescriptionID + "]";
		}

		protected void write(OutputStream out) throws IOException {
			Block.write32Int(out, firstChunk);
			Block.write32Int(out, samplesPerChunk);
			Block.write32Int(out, sampleDescriptionID);
		}
	}

	/** "stsc" */
	public static final String BLOCK_TYPE = "stsc";

	protected int version = 0;
	protected int flags = 0;
	protected SampleToChunkEntry[] entries = new SampleToChunkEntry[0];

	public SampleToChunkBlock(int version, int flags) {
		super(null);
		this.version = version;
		this.flags = flags;
	}

	public SampleToChunkBlock() {
		super(null);
	}

	public SampleToChunkBlock(Block parent, InputStream in) throws IOException {
		super(parent);
		version = in.read();
		flags = read24Int(in);
		int entryCount = (int) read32Int(in);
		entries = new SampleToChunkEntry[entryCount];
		for (int a = 0; a < entryCount; a++) {
			entries[a] = new SampleToChunkEntry(in);
		}
	}

	public void addChunk(long chunkIndex, long samplesPerChunk,
			long sampleDescriptionID) {
		if (entries.length == 0) {
			SampleToChunkEntry[] newArray = new SampleToChunkEntry[] { new SampleToChunkEntry(
					chunkIndex, samplesPerChunk, sampleDescriptionID) };
			entries = newArray;
		} else {
			for (int a = 0; a < entries.length; a++) {
				if (entries[a].firstChunk <= chunkIndex
						&& (a == entries.length - 1 || entries[a + 1].firstChunk > chunkIndex)) {
					// this is where this chunk belongs:
					if (entries[a].samplesPerChunk == samplesPerChunk
							&& entries[a].sampleDescriptionID == sampleDescriptionID) {
						// this new entry is implied; it doesn't need to be
						// written.
						return;
					} else {
						SampleToChunkEntry[] newTable = new SampleToChunkEntry[entries.length + 1];
						System.arraycopy(entries, 0, newTable, 0,
								entries.length);
						newTable[newTable.length - 1] = new SampleToChunkEntry(
								chunkIndex, samplesPerChunk,
								sampleDescriptionID);
						entries = newTable;
					}
				}
			}
		}
	}

	@Override
	public String getBlockType() {
		return BLOCK_TYPE;
	}

	@Override
	protected long getSize() {
		return 16 + 12 * entries.length;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		out.write(version);
		write24Int(out, flags);
		write32Int(out, entries.length);
		for (int a = 0; a < entries.length; a++) {
			entries[a].write(out);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for (int a = 0; a < entries.length; a++) {
			if (a != 0) {
				sb.append(", ");
			}
			sb.append(entries[a].toString());
		}
		sb.append(" ]");
		String entriesString = sb.toString();

		return "SampleToChunkBlock[ version=" + version + ", " + "flags="
				+ flags + ", " + "entries=" + entriesString + "]";
	}

	private SampleToChunkEntry getSampleToChunkEntry(int chunkIndex) {
		for (int a = 0; a < entries.length - 1; a++) {
			if (entries[a].firstChunk <= chunkIndex
					&& chunkIndex < entries[a + 1].firstChunk) {
				return entries[a];
			}
		}
		return entries[entries.length - 1];
	}

	/**
	 * The sample description ID for a specific chunk.
	 * 
	 * @param chunkIndex
	 *            the chunk to examine
	 * @return the sample description ID used in this chunk.
	 */
	public long getChunkSampleDescriptionID(int chunkIndex) {
		return getSampleToChunkEntry(chunkIndex).sampleDescriptionID;
	}

	/**
	 * Returns how many samples are in a given chunk.
	 * 
	 * @param chunkIndex
	 *            the chunk to examine
	 * @return how many samples are in a given chunk.
	 */
	public long getChunkSampleCount(int chunkIndex) {
		return getSampleToChunkEntry(chunkIndex).samplesPerChunk;
	}

	/**
	 * Return a 1-byte specification of the version of this sample-to-chunk
	 * block.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Return a 3-byte space for sample-to-chunk flags. Set this field to 0.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Return a table that maps samples to chunks.
	 */
	public SampleToChunkEntry[] getEntries() {
		SampleToChunkEntry[] copy = new SampleToChunkEntry[entries.length];
		System.arraycopy(entries, 0, copy, 0, entries.length);
		return copy;
	}
}