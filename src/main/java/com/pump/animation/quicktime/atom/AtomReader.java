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
 * This reads Atoms from an InputStream.
 */
public class AtomReader {

	/**
	 * This indicates the AtomReader could not read a file; it is probably not a
	 * QuickTime file.
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
	 * A collection of atom types we generally expect to see towards the top of
	 * a file.
	 */
	private static final Collection<String> SUPPORTED_LEADING_ATOMS = Collections
			.unmodifiableCollection(createLeadingAtomTypes());

	private static Collection<String> createLeadingAtomTypes() {
		Collection<String> c = new HashSet<>();
		c.addAll(ParentAtom.PARENT_ATOM_TYPES);
		c.add(MovieHeaderAtom.ATOM_TYPE);
		c.add(MediaHeaderAtom.ATOM_TYPE);
		c.add(TrackHeaderAtom.ATOM_TYPE);
		c.add(DataReferenceAtom.ATOM_TYPE);
		c.add("mdat");
		c.add("ftyp");
		c.add("free");
		c.add("wide");
		c.add("pnot");
		c.add("skip");
		c.add("PICT");
		return c;
	}

	byte[] sizeArray = new byte[4];
	byte[] bigSizeArray = new byte[8];
	List<String> readAtomTypes = new ArrayList<>();
	FileType fileType = null;

	/**
	 * Return all the Atoms in a File.
	 */
	public synchronized Atom[] readAll(File file) throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			return readAll(in);
		}
	}

	/**
	 * Return all the Atoms in an InputStream.
	 */
	public synchronized Atom[] readAll(InputStream in) throws IOException {
		MeasuredInputStream in2 = new MeasuredInputStream(in);
		List<Atom> v = new ArrayList<Atom>();
		while (true) {
			Atom atom = read(null, in2);
			if (atom == null)
				break;
			v.add(atom);
		}
		return v.toArray(new Atom[v.size()]);
	}

	/**
	 * Read one root (parent-less) Atom from an InputStream.
	 */
	public synchronized Atom read(InputStream in) throws IOException {
		return read(null, in);
	}

	/**
	 * Read one Atom from an InputStream.
	 * <p>
	 * After reading the first 8 bytes (the size and type) this calls
	 * {@link #read(Atom, GuardedInputStream, String)} for the remaining data.
	 * 
	 * @param parent
	 *            the optional parent for the new Atom.
	 */
	protected synchronized Atom read(Atom parent, InputStream in)
			throws IOException {
		long size;
		String type;
		int readSoFar;

		try {
			// the normal pattern is: [4-byte size] [4 byte identifier]

			// ... but if the size is 0: we have a weird invalid atom that
			// we should just skip. (At atom has to *always* be at least 8
			// bytes, because the size includes those 8 bytes of header...)
			size = 0;
			while (size == 0) {
				try {
					size = Atom.read32Int(in);
				} catch (EOFException e) {
					if (readAtomTypes.isEmpty())
						throw new UnsupportedFileException(e);
					return null;
				}
			}

			if (in instanceof GuardedInputStream) {
				GuardedInputStream gis = (GuardedInputStream) in;
				long inputLimit = gis.getRemainingLimit() + 4;
				if (size > inputLimit) {
					size = inputLimit;
					// throw new IOException("expected size is too large (" +
					// size
					// + ">" + inputLimit + ")");
				}
			}

			try {
				type = Atom.read32String(in);
			} catch (IOException e) {
				type = null;
			}

			if (size == 0 || type == null) {
				// Don't know why this can happen.
				// This kind of atom has no type.
				if (readAtomTypes.isEmpty())
					throw new UnsupportedFileException(
							"An empty leading atom is not allowed.");
				return new EmptyAtom(parent);
			}
			readSoFar = 8;

			if (size == 1) { // this is a special code indicating the size won't
								// fit in 4 bytes
				Atom.read(in, bigSizeArray);
				long j0 = (bigSizeArray[0] & 0xff);
				long j1 = (bigSizeArray[1] & 0xff);
				long j2 = (bigSizeArray[2] & 0xff);
				long j3 = (bigSizeArray[3] & 0xff);
				long j4 = (bigSizeArray[4] & 0xff);
				long j5 = (bigSizeArray[5] & 0xff);
				long j6 = (bigSizeArray[6] & 0xff);
				long j7 = (bigSizeArray[7] & 0xff);
				size = (j0 << 56L) + (j1 << 48L) + (j2 << 40L) + (j3 << 32L)
						+ (j4 << 24L) + (j5 << 16L) + (j6 << 8L) + (j7 << 0L);
				readSoFar += 8;
			}
		} catch (IOException e) {
			if (readAtomTypes.size() < 3)
				throw new UnsupportedFileException(e);
			throw e;
		} catch (RuntimeException e) {
			if (readAtomTypes.size() < 3)
				throw new UnsupportedFileException(e);
			throw e;
		}

		GuardedInputStream atomIn = new GuardedInputStream(in, size - readSoFar,
				false);

		return read(parent, atomIn, type);
	}

	/**
	 * Read a specific atom give its type and size.
	 * 
	 * @param parent
	 *            the optional parent node.
	 * @param in
	 *            this InputStream has been limited to return only a fixed
	 *            amount of bytes (based on the atom's size)
	 * @param atomType
	 *            the 4-letter atom type, like "mvhd" (for "movie header") or
	 *            "dref" (for "data reference")
	 * @return the atom read from the input stream. This may return a specific
	 *         Atom subclass (like "MovieHeaderAtom"), or a generic
	 *         UnknownLeafAtom.
	 * 
	 * @throws IOException
	 */
	protected Atom read(Atom parent, GuardedInputStream in, String atomType)
			throws IOException {
		try {
			validateAtomType(atomType);

			switch (atomType) {
			case MovieHeaderAtom.ATOM_TYPE:
				return new MovieHeaderAtom(parent, in);
			case MediaHeaderAtom.ATOM_TYPE:
				return new MediaHeaderAtom(parent, in);
			case SoundMediaInformationHeaderAtom.ATOM_TYPE:
				return new SoundMediaInformationHeaderAtom(parent, in);
			case HandlerReferenceAtom.ATOM_TYPE:
				return new HandlerReferenceAtom(parent, in);
			case VideoMediaInformationHeaderAtom.ATOM_TYPE:
				return new VideoMediaInformationHeaderAtom(parent, in);
			case TrackHeaderAtom.ATOM_TYPE:
				return new TrackHeaderAtom(parent, in);
			case DataReferenceAtom.ATOM_TYPE:
				return new DataReferenceAtom(parent, in);
			case EditListAtom.ATOM_TYPE:
				return new EditListAtom(parent, in);
			case SampleDescriptionAtom.ATOM_TYPE:
				if (parent == null)
					throw new NullPointerException(
							"sample description atoms must have a parent");

				if (parent.getParent() != null
						&& ((Atom) parent.getParent()).getChild(
								VideoMediaInformationHeaderAtom.class) != null) {
					return new VideoSampleDescriptionAtom(parent, in);
				} else if (parent.getParent() != null
						&& ((Atom) parent.getParent()).getChild(
								SoundMediaInformationHeaderAtom.class) != null) {
					return new SoundSampleDescriptionAtom(parent, in);
				} else {
					return new SampleDescriptionAtom(parent, in);
				}
			case TimeToSampleAtom.ATOM_TYPE:
				return new TimeToSampleAtom(parent, in);
			case SampleToChunkAtom.ATOM_TYPE:
				return new SampleToChunkAtom(parent, in);
			case SampleSizeAtom.ATOM_TYPE:
				return new SampleSizeAtom(parent, in);
			case ChunkOffsetAtom.ATOM_TYPE:
				return new ChunkOffsetAtom(parent, in);
			case WindowLocationAtom.ATOM_TYPE:
				return new WindowLocationAtom(parent, in);
			}
			if (getFileType() == FileType.QUICKTIME) {
				if (atomType.charAt(0) == '©')
					return new UserDataTextAtom(parent, atomType, in);
			} else {
				if (DataAtom.ATOM_TYPE.equals(atomType)) {
					return new DataAtom(parent, in);
				}
				if (atomType.charAt(0) == '©' && parent != null
						&& "ilst".equals(parent.getIdentifier()))
					return new ParentAtom(this, parent, atomType, in);
			}
			if (ParentAtom.PARENT_ATOM_TYPES.contains(atomType))
				return new ParentAtom(this, parent, atomType, in);

			// boolean allLetters = true;
			// for (int a = 0; a < atomType.length(); a++) {
			// if (!Character.isLetterOrDigit(atomType.charAt(a)))
			// allLetters = false;
			// }
			// if (!allLetters)
			// System.err.println("AtomFactory: warning: unusual atom type \""
			// + atomType + "\", size = " + atomType);
			return new UnknownLeafAtom(parent, atomType, in);
		} finally {
			readAtomTypes.add(atomType);
			if (in.getRemainingLimit() > 0) {
				Atom.skip(in, in.getRemainingLimit());
			}
		}
	}

	protected void validateAtomType(String atomType) throws IOException {
		if (SUPPORTED_LEADING_ATOMS.contains(atomType))
			return;
		if (readAtomTypes.size() < 2)
			throw new UnsupportedFileException(
					"The atom type \"" + atomType + "\" is not supported.");
	}

	public FileType getFileType() {
		return fileType;
	}
}