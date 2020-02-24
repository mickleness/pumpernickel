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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.pump.io.GuardedInputStream;
import com.pump.io.MeasuredInputStream;

/**
 * This reads Blocks from an InputStream.
 */
public class BlockReader {

	/**
	 * This indicates the BlockReader could not read a file; it is probably not
	 * a QuickTime file.
	 */
	public static class UnsupportedFileException extends IOException {

		private static final long serialVersionUID = 1L;

		public UnsupportedFileException(String msg) {
			super(msg);
		}

		public UnsupportedFileException(Exception e) {
			super(e);
		}
	}

	/**
	 * A collection of block types we generally expect to see towards the top of
	 * a file.
	 */
	private static final Collection<String> SUPPORTED_LEADING_BLOCK_TYPES = Collections
			.unmodifiableCollection(createLeadingBlockTypes());

	private static Collection<String> createLeadingBlockTypes() {
		Collection<String> c = new HashSet<>();
		c.addAll(ParentBlock.PARENT_BLOCK_TYPES);
		c.add(MovieHeaderBlock.BLOCK_TYPE);
		c.add(MediaHeaderBlock.BLOCK_TYPE);
		c.add(TrackHeaderBlock.BLOCK_TYPE);
		c.add(DataReferenceBlock.BLOCK_TYPE);
		c.add("ftyp");
		c.add("free");
		return c;
	}

	byte[] sizeArray = new byte[4];
	byte[] bigSizeArray = new byte[8];
	List<String> readBlockTypes = new ArrayList<>();
	FileType fileType = null;

	/**
	 * Return all the Blocks in a File.
	 */
	public synchronized Block[] readAll(File file) throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			return readAll(in);
		}
	}

	/**
	 * Return all the Blocks in an InputStream.
	 */
	public synchronized Block[] readAll(InputStream in) throws IOException {
		MeasuredInputStream in2 = new MeasuredInputStream(in);
		List<Block> v = new ArrayList<>();
		while (true) {
			Block block = read(null, in2);
			if (block == null)
				break;
			v.add(block);
		}
		return v.toArray(new Block[v.size()]);
	}

	/**
	 * Read one root (parent-less) Block from an InputStream.
	 */
	public synchronized Block read(InputStream in) throws IOException {
		return read(null, in);
	}

	/**
	 * Read one Block from an InputStream.
	 * <p>
	 * After reading the first 8 bytes (the size and type) this calls
	 * {@link #read(Block, GuardedInputStream, String)} for the remaining data.
	 * 
	 * @param parent
	 *            the optional parent for the new Block.
	 */
	protected synchronized Block read(Block parent, InputStream in)
			throws IOException {
		long size;
		String type;
		try {
			// the normal pattern is: [4-byte size] [4 byte identifier]

			// ... but if the size is 0: we have a weird invalid block that
			// we should just skip. (A block has to *always* be at least 8
			// bytes, because the size includes those 8 bytes of header...)
			size = 0;
			while (size == 0) {
				try {
					size = Block.read32Int(in);
				} catch (EOFException e) {
					if (readBlockTypes.isEmpty())
						throw new UnsupportedFileException(e);
					return null;
				}
			}

			if (in instanceof GuardedInputStream) {
				GuardedInputStream gis = (GuardedInputStream) in;
				long inputLimit = gis.getRemainingLimit() + 4;
				if (size > inputLimit) {
					throw new IOException("expected size is too large (" + size
							+ ">" + inputLimit + ")");
				}
			}

			try {
				type = Block.read32String(in);
			} catch (IOException e) {
				type = null;
			}

			if (size == 0 || type == null) {
				// Don't know why this can happen.
				// This kind of block has no type.
				if (readBlockTypes.isEmpty())
					throw new UnsupportedFileException(
							"An empty leading block is not allowed.");
				return new EmptyBlock(parent);
			}

			if (size == 1) { // this is a special code indicating the size won't
								// fit in 4 bytes
				Block.read(in, bigSizeArray);
				size = ((sizeArray[0] & 0xff) << 56)
						+ ((sizeArray[1] & 0xff) << 48)
						+ ((sizeArray[2] & 0xff) << 40)
						+ ((sizeArray[3] & 0xff) << 32)
						+ ((sizeArray[4] & 0xff) << 24)
						+ ((sizeArray[5] & 0xff) << 16)
						+ ((sizeArray[6] & 0xff) << 8)
						+ ((sizeArray[7] & 0xff) << 0);
			}
		} catch (IOException e) {
			if (readBlockTypes.size() < 3)
				throw new UnsupportedFileException(e);
			throw e;
		} catch (RuntimeException e) {
			if (readBlockTypes.size() < 3)
				throw new UnsupportedFileException(e);
			throw e;
		}

		GuardedInputStream blockIn = new GuardedInputStream(in, size - 8, false);

		return read(parent, blockIn, type);
	}

	/**
	 * Read a specific block give its type and size.
	 * 
	 * @param parent
	 *            the optional parent node.
	 * @param in
	 *            this InputStream has been limited to return only a fixed
	 *            amount of bytes (based on the block's size)
	 * @param blockType
	 *            the 4-letter block type, like "mvhd" (for "movie header") or
	 *            "dref" (for "data reference")
	 * @return the block read from the input stream. This may return a specific
	 *         Block subclass (like "MovieHeaderBlock"), or a generic
	 *         UnknownLeafBlock.
	 * 
	 * @throws IOException
	 */
	protected Block read(Block parent, GuardedInputStream in, String blockType)
			throws IOException {
		try {
			validateBlockType(blockType);

			switch (blockType) {
			case MovieHeaderBlock.BLOCK_TYPE:
				return new MovieHeaderBlock(parent, in);
			case MediaHeaderBlock.BLOCK_TYPE:
				return new MediaHeaderBlock(parent, in);
			case SoundMediaInformationHeaderBlock.BLOCK_TYPE:
				return new SoundMediaInformationHeaderBlock(parent, in);
			case HandlerReferenceBlock.BLOCK_TYPE:
				return new HandlerReferenceBlock(parent, in);
			case VideoMediaInformationHeaderBlock.BLOCK_TYPE:
				return new VideoMediaInformationHeaderBlock(parent, in);
			case TrackHeaderBlock.BLOCK_TYPE:
				return new TrackHeaderBlock(parent, in);
			case DataReferenceBlock.BLOCK_TYPE:
				return new DataReferenceBlock(parent, in);
			case EditListBlock.BLOCK_TYPE:
				return new EditListBlock(parent, in);
			case SampleDescriptionBlock.BLOCK_TYPE:
				if (parent == null)
					throw new NullPointerException(
							"sample description blocks must have a parent");

				if (parent.getParent() != null
						&& parent.getParent().getChild(
								VideoMediaInformationHeaderBlock.class) != null) {
					return new VideoSampleDescriptionBlock(parent, in);
				} else if (parent.getParent() != null
						&& parent.getParent().getChild(
								SoundMediaInformationHeaderBlock.class) != null) {
					return new SoundSampleDescriptionBlock(parent, in);
				} else {
					return new SampleDescriptionBlock(parent, in);
				}
			case TimeToSampleBlock.BLOCK_TYPE:
				return new TimeToSampleBlock(parent, in);
			case SampleToChunkBlock.BLOCK_TYPE:
				return new SampleToChunkBlock(parent, in);
			case SampleSizeBlock.BLOCK_TYPE:
				return new SampleSizeBlock(parent, in);
			case ChunkOffsetBlock.BLOCK_TYPE:
				return new ChunkOffsetBlock(parent, in);
			case WindowLocationBlock.BLOCK_TYPE:
				return new WindowLocationBlock(parent, in);
			}
			if (getFileType() == FileType.QUICKTIME) {
				if (blockType.charAt(0) == '©')
					return new UserDataTextBlock(parent, blockType, in);
			} else {
				if (DataBlock.BLOCK_TYPE.equals(blockType)) {
					return new DataBlock(parent, in);
				}
				if (blockType.charAt(0) == '©' && parent != null
						&& "ilst".equals(parent.getBlockType()))
					return new ParentBlock(this, parent, blockType, in);
			}
			if (ParentBlock.PARENT_BLOCK_TYPES.contains(blockType))
				return new ParentBlock(this, parent, blockType, in);

			return new UnknownLeafBlock(parent, blockType, in);
		} finally {
			readBlockTypes.add(blockType);
		}
	}

	protected void validateBlockType(String blockType) throws IOException {
		if (SUPPORTED_LEADING_BLOCK_TYPES.contains(blockType))
			return;
		if (readBlockTypes.size() < 3)
			throw new UnsupportedFileException("The block type \"" + blockType
					+ "\" is not supported.");
	}

	public FileType getFileType() {
		return fileType;
	}
}