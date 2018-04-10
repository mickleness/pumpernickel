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
package com.pump.animation.quicktime;

import java.io.IOException;
import java.io.InputStream;

import com.pump.io.GuardedOutputStream;

/**
 * This is not a public class because I expect to make some significant changes
 * to this project in the next year.
 * <P>
 * Use at your own risk. This class (and its package) may change in future
 * releases.
 * <P>
 * Not that I'm promising there will be future releases. There may not be. :)
 */
class WindowLocationAtom extends LeafAtom {
	int x, y;

	public WindowLocationAtom(Atom parent, InputStream in) throws IOException {
		super(parent);
		x = read16Int(in);
		y = read16Int(in);
	}

	@Override
	protected String getIdentifier() {
		return "WLOC";
	}

	@Override
	protected long getSize() {
		return 12;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		write16Int(out, x);
		write16Int(out, y);
	}
}