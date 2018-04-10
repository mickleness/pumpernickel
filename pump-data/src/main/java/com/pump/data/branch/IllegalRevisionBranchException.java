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
package com.pump.data.branch;

/**
 * This exception is thrown if you try to relate a revision from one branch with
 * another branch. Revisions are uniquely associated with one branch, and
 * although they may contain meta information (such as timestamps) they should
 * not be compared to revisions that were created for other branches.
 */
public class IllegalRevisionBranchException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	protected Branch branch;
	protected Revision revision;

	@SuppressWarnings("rawtypes")
	public IllegalRevisionBranchException(String msg, Branch branch,
			Revision revision) {
		super(msg);
		this.branch = branch;
		this.revision = revision;
	}

	@SuppressWarnings("rawtypes")
	public Branch getBranch() {
		return branch;
	}

	public Revision getRevision() {
		return revision;
	}
}