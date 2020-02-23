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
package com.pump.animation.quicktime.atom;

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
 * A ParentAtom is an Atom that contains children atoms and no additional data.
 */
public class ParentAtom extends Atom {

	/**
	 * QuickTime movie atoms have an atom type of 'moov'. These atoms act as a
	 * container for the information that describes a movie’s data. This
	 * information, or metadata, is stored in a number of different types of
	 * atoms. Generally speaking, only metadata is stored in a movie atom.
	 * Sample data for the movie, such as audio or video samples, are referenced
	 * in the movie atom, but are not contained in it.
	 * <P>
	 * The movie atom is essentially a container of other atoms. These atoms,
	 * taken together, describe the contents of a movie. At the highest level,
	 * movie atoms typically contain track atoms, which in turn contain media
	 * atoms. At the lowest level are the leaf atoms, which contain non-atom
	 * data, usually in the form of a table or a set of data elements. For
	 * example, a track atom contains an edit atom, which in turn contains an
	 * edit list atom, a leaf atom which contains data in the form of an edit
	 * list table.
	 */
	public static final String ATOM_TYPE_MOVIE = "moov";

	/**
	 * Clipping atoms specify the clipping regions for movies and for tracks.
	 * The clipping atom has an atom type value of 'clip'.
	 */
	public static final String ATOM_TYPE_MOVIE_CLIPPING_ATOM = "clip";

	/**
	 * User data atoms allow you to define and store data associated with a
	 * QuickTime object, such as a movie 'moov', track 'trak', or media 'mdia'.
	 * This includes both information that QuickTime looks for, such as
	 * copyright information or whether a movie should loop, and arbitrary
	 * information—provided by and for your application—that QuickTime simply
	 * ignores.
	 * <p>
	 * A user data atom whose immediate parent is a movie atom contains data
	 * relevant to the movie as a whole. A user data atom whose parent is a
	 * track atom contains information relevant to that specific track. A
	 * QuickTime movie file may contain many user data atoms, but only one user
	 * data atom is allowed as the immediate child of any given movie atom or
	 * track atom.
	 * <p>
	 * The user data atom has an atom type of 'udta'. Inside the user data atom
	 * is a list of atoms describing each piece of user data. User data provides
	 * a simple way to extend the information stored in a QuickTime movie. For
	 * example, user data atoms can store a movie’s window position, playback
	 * characteristics, or creation information.
	 */
	public static final String ATOM_TYPE_USER_DATA = "udta";

	/**
	 * Track matte atoms are used to visually blend the track’s image when it is
	 * displayed.
	 * <p>
	 * Track matte atoms have an atom type value of 'matt'.
	 */
	public static final String ATOM_TYPE_TRACK_MATTE = "matt";

	/**
	 * Track atoms have an atom type value of 'trak'. The track atom requires a
	 * track header atom ('tkhd') and a media atom ('mdia'). Other child atoms
	 * are optional, and may include a track clipping atom ('clip'), a track
	 * matte atom ('matt'), an edit atom ('edts'), a track reference atom
	 * ('tref'), a track load settings atom ('load'), a track input map atom
	 * ('imap'), and a user data atom ('udta').
	 */
	public static final String ATOM_TYPE_TRACK = "trak";

	/**
	 * You use edit atoms to define the portions of the media that are to be
	 * used to build up a track for a movie. The edits themselves are contained
	 * in an edit list table, which consists of time offset and duration values
	 * for each segment. Edit atoms have an atom type value of 'edts'.
	 * <p>
	 * In the absence of an edit list, the presentation of a track starts
	 * immediately. An empty edit is used to offset the start time of a track.
	 */
	public static final String ATOM_TYPE_EDITS = "edts";

	/**
	 * Media atoms describe and define a track’s media type and sample data. The
	 * media atom contains information that specifies:
	 * <ul>
	 * <li>The media type, such as sound, video or timed metadata</li>
	 * <li>The media handler component used to interpret the sample data</li>
	 * <li>The media timescale and track duration</li>
	 * <li>Media-and-track-specific information, such as sound volume or
	 * graphics mode</li>
	 * <li>The media data references, which typically specify the file where the
	 * sample data is stored</li>
	 * <li>The sample table atoms, which, for each media sample, specify the
	 * sample description, duration, and byte offset from the data reference</li>
	 * </ul>
	 * <p>
	 * The media atom has an atom type of 'mdia'. It must contain a media header
	 * atom ('mdhd'), and it can contain a handler reference ('hdlr') atom,
	 * media information ('minf') atom, and user data ('udta') atom.
	 */
	public static final String ATOM_TYPE_MEDIA = "mdia";

	/**
	 * Media information atoms (defined by the 'minf' atom type) store
	 * handler-specific information for a track’s media data. The media handler
	 * uses this information to map from media time to media data and to process
	 * the media data.
	 * <p>
	 * These atoms contain information that is specific to the type of data
	 * defined by the media. Further, the format and content of media
	 * information atoms are dictated by the media handler that is responsible
	 * for interpreting the media data stream. Another media handler would not
	 * know how to interpret this information.
	 * <p>
	 * This atom may contain atoms that store media information for the video
	 * ('vmhd'), sound ('smhd'), and base ('gmhd') portions of QuickTime movies.
	 */
	public static final String ATOM_TYPE_MEDIA_INFORMATION = "minf";

	/**
	 * The base media information header atom indicates that this media
	 * information atom pertains to a base media.
	 */
	public static final String ATOM_TYPE_BASE_MEDIA_INFORMATION_HEADER = "gmhd";

	/**
	 * The handler reference atom contains information specifying the data
	 * handler component that provides access to the media data. The data
	 * handler component uses the data information atom to interpret the media’s
	 * data. Data information atoms have an atom type value of 'dinf'.
	 */
	public static final String ATOM_TYPE_DATA_INFORMATION = "dinf";

	/**
	 * The sample table atom contains information for converting from media time
	 * to sample number to sample location. This atom also indicates how to
	 * interpret the sample (for example, whether to decompress the video data
	 * and, if so, how). This section describes the format and content of the
	 * sample table atom.
	 * <p>
	 * The sample table atom has an atom type of 'stbl'. It can contain the
	 * sample description atom, the time-to-sample atom, the sync sample atom,
	 * the sample-to-chunk atom, the sample size atom, the chunk offset atom,
	 * and the shadow sync atom.
	 * <p>
	 * The sample table atom contains all the time and data indexing of the
	 * media samples in a track. Using tables, it is possible to locate samples
	 * in time, determine their type, and determine their size, container, and
	 * offset into that container.
	 * <p>
	 * If the track that contains the sample table atom references no data, then
	 * the sample table atom does not need to contain any child atoms (not a
	 * very useful media track).
	 * <p>
	 * If the track that the sample table atom is contained in does reference
	 * data, then the following child atoms are required: sample description,
	 * sample size, sample to chunk, and chunk offset. All of the subtables of
	 * the sample table use the same total sample count.
	 * <p>
	 * The sample description atom must contain at least one entry. A sample
	 * description atom is required because it contains the data reference index
	 * field that indicates which data reference atom to use to retrieve the
	 * media samples. Without the sample description, it is not possible to
	 * determine where the media samples are stored. The sync sample atom is
	 * optional. If the sync sample atom is not present, all samples are
	 * implicitly sync samples.
	 */
	public static final String ATOM_TYPE_SAMPLE_TABLE = "stbl";

	/**
	 * Track reference atoms define relationships between tracks. Track
	 * reference atoms allow one track to specify how it is related to other
	 * tracks. For example, if a movie has three video tracks and three sound
	 * tracks, track references allow you to identify the related sound and
	 * video tracks. Track reference atoms have an atom type value of 'tref'.
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
	 * If this atom is not present, the track is not referencing any other track
	 * in any way. Note that the array of track reference type atoms is sized to
	 * fill the track reference atom. Track references with a reference index of
	 * 0 are permitted. This indicates no reference.
	 */
	public static final String ATOM_TYPE_TRACK_REFERENCE = "tref";

	/**
	 * Track input map atoms define how data being sent to this track from its
	 * nonprimary sources is to be interpreted. Track references of type 'ssrc'
	 * define a track’s secondary data sources. These sources provide additional
	 * data that is to be used when processing the track. Track input map atoms
	 * have an atom type value of 'imap'.
	 */
	public static final String ATOM_TYPE_TRACK_INPUT_MAP = "imap";

	/**
	 * A container atom that stores information for video correction in the form
	 * of three required atoms. This atom is optionally included in the track
	 * atom. The type of the track aperture mode dimensions atom is ‘tapt’.
	 */
	public static final String ATOM_TYPE_TRACK_APERTURE_MODE_DIMENSIONS = "tapt";

	/**
	 * A reference movie atom contains references to one or more movies. It can
	 * optionally contain a list of system requirements in order for each movie
	 * to play, and a quality rating for each movie. It is typically used to
	 * specify a list of alternate movies to be played under different
	 * conditions.
	 * <p>
	 * A reference movie atom’s parent is always a movie atom ('moov'). Only one
	 * reference movie atom is allowed in a given movie atom.
	 */
	public static final String ATOM_TYPE_REFERENCE_MOVIE = "rmra";

	/**
	 * Each reference movie descriptor atom contains other atoms that describe
	 * where a particular movie can be found, and optionally what the system
	 * requirements are to play that movie, as well as an optional quality
	 * rating for that movie.
	 * <p>
	 * A reference movie descriptor atom’s parent is always a movie reference
	 * atom ('rmra'). Multiple reference movie descriptor atoms are allowed in a
	 * given movie reference atom, and more than one is usually present.
	 */
	public static final String ATOM_TYPE_REFERENCE_MOVIE_DESCRIPTOR = "rmda";

	/**
	 * This does not appear in the QuickTime specs, but this is used as a parent
	 * atom in related files like "m4a" audio files.
	 */
	private static final String ATOM_TYPE_META = "meta";

	/**
	 * This does not appear in the QuickTime specs, but this is used as a parent
	 * atom in related files like "m4a" audio files.
	 */
	private static final String ATOM_TYPE_ILST = "ilst";

	/**
	 * A collection of all the ATOM TYPE constants in this class.
	 */
	public static final Collection<String> PARENT_ATOM_TYPES = Collections
			.unmodifiableCollection(new HashSet<>(Arrays.asList(
					ATOM_TYPE_MOVIE, ATOM_TYPE_USER_DATA, ATOM_TYPE_TRACK,
					ATOM_TYPE_EDITS, ATOM_TYPE_MEDIA, ATOM_TYPE_TRACK_MATTE,
					ATOM_TYPE_MEDIA_INFORMATION, ATOM_TYPE_DATA_INFORMATION,
					ATOM_TYPE_BASE_MEDIA_INFORMATION_HEADER,
					ATOM_TYPE_REFERENCE_MOVIE, ATOM_TYPE_SAMPLE_TABLE,
					ATOM_TYPE_TRACK_REFERENCE, ATOM_TYPE_MOVIE_CLIPPING_ATOM,
					ATOM_TYPE_TRACK_INPUT_MAP,
					ATOM_TYPE_REFERENCE_MOVIE_DESCRIPTOR,
					ATOM_TYPE_TRACK_APERTURE_MODE_DIMENSIONS, ATOM_TYPE_META,
					ATOM_TYPE_ILST)));

	protected List<Atom> children = new ArrayList<Atom>();
	protected String id;

	public ParentAtom(String id) {
		super(null);
		this.id = id;
	}

	public ParentAtom(AtomReader reader, Atom parent, String id,
			GuardedInputStream in) throws IOException {
		super(parent);
		this.id = id;
		while (in.isAtLimit() == false) {
			Atom next = reader.read(this, in);
			children.add(next);
		}
	}

	public void add(Atom a) {
		children.add(a);
		a.parent = this;
	}

	@Override
	public Enumeration<Atom> children() {
		return new EnumerationIterator<Atom>(children.iterator());
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public Atom getChildAt(int childIndex) {
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
			Atom atom = children.get(a);
			sum += atom.getSize();
		}
		return sum;
	}

	@Override
	protected String getIdentifier() {
		return id;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		for (int a = 0; a < children.size(); a++) {
			Atom atom = children.get(a);
			atom.write(out);
		}
	}
}