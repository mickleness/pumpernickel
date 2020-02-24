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
import java.util.List;

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

/**
 * This atom represents a range of different IDs that all begin with the "©"
 * character.
 * <p>
 * All user data list entries whose type begins with the © character (ASCII 169)
 * are defined to be international text. These list entries must contain a list
 * of text strings with associated language codes. By storing multiple versions
 * of the same text, a single user data text item can contain translations for
 * different languages.
 * <p>
 * The list of text strings uses a small integer atom format, which is identical
 * to the QuickTime atom format except that it uses 16-bit values for size and
 * type instead of 32-bit values. The first value is the size of the string,
 * including the size and type, and the second value is the language code for
 * the string.
 * <p>
 * User data text strings may use either Macintosh text encoding or Unicode text
 * encoding. The format of the language code determines the text encoding
 * format. Macintosh language codes are followed by Macintosh-encoded text. If
 * the language code is specified using the ISO language codes listed in
 * specification ISO 639-2/T, the text uses Unicode text encoding. When Unicode
 * is used, the text is in UTF-8 unless it starts with a byte-order-mark (BOM,
 * 0xFEFF), in which case the text is in UTF-16. Both the BOM and the UTF-16
 * text should be big-endian. Multiple versions of the same text may use
 * different encoding schemes.
 * <p>
 * Important: Language code values less than 0x400 are Macintosh language codes.
 * Language code values greater than or equal to 0x400 are ISO language codes.
 * The exception to this rule is language code 0x7FFF, which indicates an
 * unspecified Macintosh language.
 * <p>
 * ISO language codes are three-character codes. In order to fit inside a 16-bit
 * field, the characters must be packed into three 5-bit subfields. This packing
 * is described in “ISO Language Codes”.
 * <p>
 * 
 * @see TextTypes
 */
public class UserDataTextAtom extends LeafAtom {

	public static class TextEntry {
		int language;
		byte[] data;

		public TextEntry(int l, byte[] d) {
			this.language = l;
			this.data = d;
		}
	}

	protected List<TextEntry> entries = new ArrayList<TextEntry>();

	protected String id;

	public UserDataTextAtom(Atom parent, String id, GuardedInputStream in)
			throws IOException {
		super(parent);
		this.id = id;
		while (in.isAtLimit() == false) {
			int size = read16Int(in);
			int language = read16Int(in);
			byte[] data = new byte[size];
			read(in, data);
			entries.add(new TextEntry(language, data));
		}
	}

	public TextEntry[] getTextEntries() {
		return entries.toArray(new TextEntry[entries.size()]);
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		for (int a = 0; a < entries.size(); a++) {
			TextEntry e = entries.get(a);
			write16Int(out, e.data.length);
			write16Int(out, e.language);
			out.write(e.data);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("UserDataTextAtom[ \""
				+ getIdentifier() + "\" ");
		for (int a = 0; a < entries.size(); a++) {
			TextEntry e = entries.get(a);
			sb.append("\"" + (new String(e.data)) + "\" ");
		}
		sb.append("]");
		return sb.toString();
	}
}