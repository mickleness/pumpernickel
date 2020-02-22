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
 * 
 * ISO language codes are three-character codes. In order to fit inside a 16-bit
 * field, the characters must be packed into three 5-bit subfields. This packing
 * is described in “ISO Language Codes”.
 * 
 * 
 * <table>
 * <tr>
 * <th>Atom ID</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>©arg</td>
 * <td>Name of arranger</td>
 * </tr>
 * <tr>
 * <td>©ark</td>
 * <td>Keywords for arranger</td>
 * </tr>
 * <tr>
 * <td>©cok</td>
 * <td>Keywords for composer</td>
 * </tr>
 * <tr>
 * <td>©cpy</td>
 * <td>Copyright statement</td>
 * </tr>
 * <tr>
 * <td>©day</td>
 * <td>Date the movie content was created</td>
 * </tr>
 * <tr>
 * <td>©dir</td>
 * <td>Name of movie’s director</td>
 * </tr>
 * <tr>
 * <td>©ed1-©ed9</td>
 * <td>Edit dates and descriptions</td>
 * </tr>
 * <tr>
 * <td>©fmt</td>
 * <td>Indication of movie format (computer-generated, digitized, and so on)</td>
 * </tr>
 * <tr>
 * <td>©inf</td>
 * <td>Information about the movie</td>
 * </tr>
 * <tr>
 * <td>©isr</td>
 * <td>ISRC code</td>
 * </tr>
 * <tr>
 * <td>©lab</td>
 * <td>Name of record label</td>
 * </tr>
 * <tr>
 * <td>©lal</td>
 * <td>URL of record label</td>
 * </tr>
 * <tr>
 * <td>©mak</td>
 * <td>Name of file creator or maker</td>
 * </tr>
 * <tr>
 * <td>©mal</td>
 * <td>URL of file creator or maker</td>
 * </tr>
 * <tr>
 * <td>©nak</td>
 * <td>Title keywords of the content</td>
 * </tr>
 * <tr>
 * <td>©nam</td>
 * <td>Title of the content</td>
 * </tr>
 * <tr>
 * <td>©pdk</td>
 * <td>Keywords for producer</td>
 * </tr>
 * <tr>
 * <td>©phg</td>
 * <td>Recording copyright statement, normally preceded by the copyright symbol</td>
 * </tr>
 * <tr>
 * <td>©prd</td>
 * <td>Name of producer</td>
 * </tr>
 * <tr>
 * <td>©prf</td>
 * <td>Names of performers</td>
 * </tr>
 * <tr>
 * <td>©prk</td>
 * <td>Keywords of main artist and performer</td>
 * </tr>
 * <tr>
 * <td>©prl</td>
 * <td>URL of main artist and performer</td>
 * </tr>
 * <tr>
 * <td>©req</td>
 * <td>Special hardware and software requirements</td>
 * </tr>
 * <tr>
 * <td>©snk</td>
 * <td>Subtitle keywords of the content</td>
 * </tr>
 * <tr>
 * <td>©snm</td>
 * <td>Subtitle of content</td>
 * </tr>
 * <tr>
 * <td>©src</td>
 * <td>Credits for those who provided movie source content</td>
 * </tr>
 * <tr>
 * <td>©swf</td>
 * <td>Name of songwriter</td>
 * </tr>
 * <tr>
 * <td>©swk</td>
 * <td>Keywords for songwriter</td>
 * </tr>
 * <tr>
 * <td>©swr</td>
 * <td>Name and version number of the software (or hardware) that generated this
 * movie</td>
 * </tr>
 * <tr>
 * <td>©wrt</td>
 * <td>Name of movie’s writer</td>
 * </tr>
 * </table>
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
	protected String getIdentifier() {
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
		StringBuffer sb = new StringBuffer("UserDataTextAtom[ ");
		for (int a = 0; a < entries.size(); a++) {
			TextEntry e = entries.get(a);
			sb.append("\"" + (new String(e.data)) + "\" ");
		}
		sb.append("]");
		return sb.toString();
	}
}