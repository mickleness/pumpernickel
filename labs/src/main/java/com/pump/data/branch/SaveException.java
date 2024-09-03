/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.data.branch;

public class SaveException extends BranchException {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	Branch branch;
	Object beanId;
	Revision revision;

	public SaveException(@SuppressWarnings("rawtypes") Branch branch,
			Revision r, BranchException e) {
		super(describe(branch, r), e);
		this.branch = branch;
		this.revision = r;
	}

	public SaveException(@SuppressWarnings("rawtypes") Branch branch,
			Object beanId, Revision r, String msg) {
		super(msg);
		this.branch = branch;
		this.beanId = beanId;
		this.revision = r;
	}

	public SaveException(@SuppressWarnings("rawtypes") Branch branch,
			String msg, Throwable cause) {
		super(msg, cause);
		this.branch = branch;
	}

	private static String describe(@SuppressWarnings("rawtypes") Branch branch,
			Revision r) {
		return "The branch \"" + branch.getName()
				+ "\" couldn't be merged with its parent (\""
				+ branch.getParent().getName() + "\"), because at revision "
				+ r + " an error occurred.";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Branch getBranch() {
		return branch;
	}

	public Revision getRevision() {
		return revision;
	}

	@Override
	public Object getBeanId() {
		return beanId;
	}

}