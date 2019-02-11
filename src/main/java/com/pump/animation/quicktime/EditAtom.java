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

import com.pump.io.GuardedInputStream;

public class EditAtom extends ParentAtom {

	public EditAtom(Atom parent, GuardedInputStream in) throws IOException {
		super(parent, "edts", in);
	}

	public EditAtom() {
		super("edts");
	}

}