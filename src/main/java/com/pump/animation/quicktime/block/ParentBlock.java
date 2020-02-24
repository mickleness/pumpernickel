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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.swing.tree.TreeNode;

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;
import com.pump.util.EnumerationIterator;

/**
 * A ParentBlock is an Block that contains children blocks and no additional
 * data.
 */
public class ParentBlock extends Block {

	/**
	 * QuickTime movie blocks have a block type of 'moov'. These blocks act as a
	 * container for the information that describes a movie’s data. This
	 * information, or metadata, is stored in a number of different types of
	 * blocks. Generally speaking, only metadata is stored in a movie block.
	 * Sample data for the movie, such as audio or video samples, are referenced
	 * in the movie block, but are not contained in it.
	 * <P>
	 * The movie block is essentially a container of other blocks. These blocks,
	 * taken together, describe the contents of a movie. At the highest level,
	 * movie blocks typically contain track blocks, which in turn contain media
	 * blocks. At the lowest level are the leaf blocks, which contain non-block
	 * data, usually in the form of a table or a set of data elements. For
	 * example, a track block contains an edit block, which in turn contains an
	 * edit list block, a leaf block which contains data in the form of an edit
	 * list table.
	 */
	public static final String BLOCK_TYPE_MOVIE = "moov";

	/**
	 * Clipping blocks specify the clipping regions for movies and for tracks.
	 * The clipping block has an block type value of 'clip'.
	 */
	public static final String BLOCK_TYPE_MOVIE_CLIPPING_BLOCK = "clip";

	/**
	 * User data blocks allow you to define and store data associated with a
	 * QuickTime object, such as a movie 'moov', track 'trak', or media 'mdia'.
	 * This includes both information that QuickTime looks for, such as
	 * copyright information or whether a movie should loop, and arbitrary
	 * information—provided by and for your application—that QuickTime simply
	 * ignores.
	 * <p>
	 * A user data block whose immediate parent is a movie block contains data
	 * relevant to the movie as a whole. A user data block whose parent is a
	 * track block contains information relevant to that specific track. A
	 * QuickTime movie file may contain many user data blocks, but only one user
	 * data block is allowed as the immediate child of any given movie block or
	 * track block.
	 * <p>
	 * The user data block has an block type of 'udta'. Inside the user data
	 * block is a list of blocks describing each piece of user data. User data
	 * provides a simple way to extend the information stored in a QuickTime
	 * movie. For example, user data blocks can store a movie’s window position,
	 * playback characteristics, or creation information.
	 */
	public static final String BLOCK_TYPE_USER_DATA = "udta";

	/**
	 * Track matte blocks are used to visually blend the track’s image when it
	 * is displayed.
	 * <p>
	 * Track matte blocks have an block type value of 'matt'.
	 */
	public static final String BLOCK_TYPE_TRACK_MATTE = "matt";

	/**
	 * Track blocks have an block type value of 'trak'. The track block requires
	 * a track header block ('tkhd') and a media block ('mdia'). Other child
	 * blocks are optional, and may include a track clipping block ('clip'), a
	 * track matte block ('matt'), an edit block ('edts'), a track reference
	 * block ('tref'), a track load settings block ('load'), a track input map
	 * block ('imap'), and a user data block ('udta').
	 */
	public static final String BLOCK_TYPE_TRACK = "trak";

	/**
	 * You use edit blocks to define the portions of the media that are to be
	 * used to build up a track for a movie. The edits themselves are contained
	 * in an edit list table, which consists of time offset and duration values
	 * for each segment. Edit blocks have an block type value of 'edts'.
	 * <p>
	 * In the absence of an edit list, the presentation of a track starts
	 * immediately. An empty edit is used to offset the start time of a track.
	 */
	public static final String BLOCK_TYPE_EDITS = "edts";

	/**
	 * Media blocks describe and define a track’s media type and sample data.
	 * The media block contains information that specifies:
	 * <ul>
	 * <li>The media type, such as sound, video or timed metadata</li>
	 * <li>The media handler component used to interpret the sample data</li>
	 * <li>The media timescale and track duration</li>
	 * <li>Media-and-track-specific information, such as sound volume or
	 * graphics mode</li>
	 * <li>The media data references, which typically specify the file where the
	 * sample data is stored</li>
	 * <li>The sample table blocks, which, for each media sample, specify the
	 * sample description, duration, and byte offset from the data reference</li>
	 * </ul>
	 * <p>
	 * The media block has a block type of 'mdia'. It must contain a media
	 * header block ('mdhd'), and it can contain a handler reference ('hdlr')
	 * block, media information ('minf') block, and user data ('udta') block.
	 */
	public static final String BLOCK_TYPE_MEDIA = "mdia";

	/**
	 * Media information blocks (defined by the 'minf' block type) store
	 * handler-specific information for a track’s media data. The media handler
	 * uses this information to map from media time to media data and to process
	 * the media data.
	 * <p>
	 * These blocks contain information that is specific to the type of data
	 * defined by the media. Further, the format and content of media
	 * information blocks are dictated by the media handler that is responsible
	 * for interpreting the media data stream. Another media handler would not
	 * know how to interpret this information.
	 * <p>
	 * This block may contain blocks that store media information for the video
	 * ('vmhd'), sound ('smhd'), and base ('gmhd') portions of QuickTime movies.
	 */
	public static final String BLOCK_TYPE_MEDIA_INFORMATION = "minf";

	/**
	 * The base media information header block indicates that this media
	 * information block pertains to a base media.
	 */
	public static final String BLOCK_TYPE_BASE_MEDIA_INFORMATION_HEADER = "gmhd";

	/**
	 * The handler reference block contains information specifying the data
	 * handler component that provides access to the media data. The data
	 * handler component uses the data information block to interpret the
	 * media’s data. Data information blocks have a block type value of 'dinf'.
	 */
	public static final String BLOCK_TYPE_DATA_INFORMATION = "dinf";

	/**
	 * The sample table block contains information for converting from media
	 * time to sample number to sample location. This block also indicates how
	 * to interpret the sample (for example, whether to decompress the video
	 * data and, if so, how). This section describes the format and content of
	 * the sample table block.
	 * <p>
	 * The sample table block has an block type of 'stbl'. It can contain the
	 * sample description block, the time-to-sample block, the sync sample
	 * block, the sample-to-chunk block, the sample size block, the chunk offset
	 * block, and the shadow sync block.
	 * <p>
	 * The sample table block contains all the time and data indexing of the
	 * media samples in a track. Using tables, it is possible to locate samples
	 * in time, determine their type, and determine their size, container, and
	 * offset into that container.
	 * <p>
	 * If the track that contains the sample table block references no data,
	 * then the sample table block does not need to contain any child blocks
	 * (not a very useful media track).
	 * <p>
	 * If the track that the sample table block is contained in does reference
	 * data, then the following child blocks are required: sample description,
	 * sample size, sample to chunk, and chunk offset. All of the subtables of
	 * the sample table use the same total sample count.
	 * <p>
	 * The sample description block must contain at least one entry. A sample
	 * description block is required because it contains the data reference
	 * index field that indicates which data reference block to use to retrieve
	 * the media samples. Without the sample description, it is not possible to
	 * determine where the media samples are stored. The sync sample block is
	 * optional. If the sync sample block is not present, all samples are
	 * implicitly sync samples.
	 */
	public static final String BLOCK_TYPE_SAMPLE_TABLE = "stbl";

	/**
	 * Track reference blocks define relationships between tracks. Track
	 * reference blocks allow one track to specify how it is related to other
	 * tracks. For example, if a movie has three video tracks and three sound
	 * tracks, track references allow you to identify the related sound and
	 * video tracks. Track reference blocks have an block type value of 'tref'.
	 * <p>
	 * Track references are unidirectional and point from the recipient track to
	 * the source track. For example, a video track may reference a time code
	 * track to indicate where its time code is stored, but the time code track
	 * would not reference the video track. The time code track is the source of
	 * time information for the video track.
	 * <p>
	 * A single track may reference multiple tracks. For example, a video track
	 * could reference a sound track to indicate that the two are synchronized
	 * and a time code track to indicate where its time code is stored.
	 * <p>
	 * A single track may also be referenced by multiple tracks. For example,
	 * both a sound and video track could reference the same time code track if
	 * they share the same timing information.
	 * <p>
	 * If this block is not present, the track is not referencing any other
	 * track in any way. Note that the array of track reference type blocks is
	 * sized to fill the track reference block. Track references with a
	 * reference index of 0 are permitted. This indicates no reference.
	 */
	public static final String BLOCK_TYPE_TRACK_REFERENCE = "tref";

	/**
	 * Track input map blocks define how data being sent to this track from its
	 * nonprimary sources is to be interpreted. Track references of type 'ssrc'
	 * define a track’s secondary data sources. These sources provide additional
	 * data that is to be used when processing the track. Track input map blocks
	 * have a block type value of 'imap'.
	 */
	public static final String BLOCK_TYPE_TRACK_INPUT_MAP = "imap";

	/**
	 * A container block that stores information for video correction in the
	 * form of three required blocks. This block is optionally included in the
	 * track block. The type of the track aperture mode dimensions block is
	 * ‘tapt’.
	 */
	public static final String BLOCK_TYPE_TRACK_APERTURE_MODE_DIMENSIONS = "tapt";

	/**
	 * A reference movie block contains references to one or more movies. It can
	 * optionally contain a list of system requirements in order for each movie
	 * to play, and a quality rating for each movie. It is typically used to
	 * specify a list of alternate movies to be played under different
	 * conditions.
	 * <p>
	 * A reference movie block's parent is always a movie block ('moov'). Only
	 * one reference movie block is allowed in a given movie block.
	 */
	public static final String BLOCK_TYPE_REFERENCE_MOVIE = "rmra";

	/**
	 * Each reference movie descriptor block contains other blocks that describe
	 * where a particular movie can be found, and optionally what the system
	 * requirements are to play that movie, as well as an optional quality
	 * rating for that movie.
	 * <p>
	 * A reference movie descriptor block's parent is always a movie reference
	 * block ('rmra'). Multiple reference movie descriptor blocks are allowed in
	 * a given movie reference block, and more than one is usually present.
	 */
	public static final String BLOCK_TYPE_REFERENCE_MOVIE_DESCRIPTOR = "rmda";

	/**
	 * This does not appear in the QuickTime specs, but this is used as a parent
	 * block in related files like "m4a" audio files.
	 */
	private static final String BLOCK_TYPE_META = "meta";

	/**
	 * This does not appear in the QuickTime specs, but this is used as a parent
	 * block in related files like "m4a" audio files.
	 */
	private static final String BLOCK_TYPE_ILST = "ilst";

	/**
	 * A collection of all the BLOCK TYPE constants in this class.
	 */
	public static final Collection<String> PARENT_BLOCK_TYPES = Collections
			.unmodifiableCollection(new HashSet<>(Arrays.asList(
					BLOCK_TYPE_MOVIE, BLOCK_TYPE_USER_DATA, BLOCK_TYPE_TRACK,
					BLOCK_TYPE_EDITS, BLOCK_TYPE_MEDIA, BLOCK_TYPE_TRACK_MATTE,
					BLOCK_TYPE_MEDIA_INFORMATION, BLOCK_TYPE_DATA_INFORMATION,
					BLOCK_TYPE_BASE_MEDIA_INFORMATION_HEADER,
					BLOCK_TYPE_REFERENCE_MOVIE, BLOCK_TYPE_SAMPLE_TABLE,
					BLOCK_TYPE_TRACK_REFERENCE,
					BLOCK_TYPE_MOVIE_CLIPPING_BLOCK,
					BLOCK_TYPE_TRACK_INPUT_MAP,
					BLOCK_TYPE_REFERENCE_MOVIE_DESCRIPTOR,
					BLOCK_TYPE_TRACK_APERTURE_MODE_DIMENSIONS, BLOCK_TYPE_META,
					BLOCK_TYPE_ILST)));

	protected List<Block> children = new ArrayList<>();
	protected String blockType;

	public ParentBlock(String blockType) {
		super(null);
		this.blockType = blockType;
	}

	public ParentBlock(BlockReader reader, Block parent, String blockType,
			GuardedInputStream in) throws IOException {
		super(parent);
		this.blockType = blockType;
		while (in.isAtLimit() == false) {
			Block next = reader.read(this, in);
			children.add(next);
		}
	}

	public void add(Block a) {
		children.add(a);
		a.parent = this;
	}

	@Override
	public Enumeration<Block> children() {
		return new EnumerationIterator<Block>(children.iterator());
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public Block getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	@Override
	public boolean isLeaf() {
		return children.size() == 0;
	}

	@Override
	protected long getSize() {
		long sum = 8;
		for (int a = 0; a < children.size(); a++) {
			Block block = children.get(a);
			sum += block.getSize();
		}
		return sum;
	}

	@Override
	public String getBlockType() {
		return blockType;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		for (int a = 0; a < children.size(); a++) {
			Block block = children.get(a);
			block.write(out);
		}
	}
}