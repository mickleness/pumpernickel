package com.pump.data.branch;

/**
 * This exception is thrown if you try to relate a revision from one branch with another
 * branch. Revisions are uniquely associated with one branch, and although they may
 * contain meta information (such as timestamps) they should not be compared to revisions
 * that were created for other branches.
 */
public class IllegalRevisionBranchException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	protected Branch branch;
	protected Revision revision;
	
	@SuppressWarnings("rawtypes")
	public IllegalRevisionBranchException(String msg,Branch branch,Revision revision) {
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
