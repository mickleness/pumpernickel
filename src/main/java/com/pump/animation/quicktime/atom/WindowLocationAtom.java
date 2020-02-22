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
import java.io.InputStream;

import com.pump.io.GuardedOutputStream;

/**
 * Default window location for movie—two 16-bit values, {x,y}
 */
public class WindowLocationAtom extends LeafAtom {
	/** "WLOC" */
	public static final String ATOM_TYPE = "WLOC";

	protected int x, y;

	public WindowLocationAtom(Atom parent, InputStream in) throws IOException {
		super(parent);
		x = read16Int(in);
		y = read16Int(in);
	}

	@Override
	protected String getIdentifier() {
		return ATOM_TYPE;
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

	/**
	 * Return the x-coordinate of the window location.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Return the y-coordinate of the window location.
	 */
	public int getY() {
		return y;
	}
}