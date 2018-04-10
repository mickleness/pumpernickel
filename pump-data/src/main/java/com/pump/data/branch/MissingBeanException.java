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

public class MissingBeanException extends BranchException {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	Branch branch;
	Object beanId;

	@SuppressWarnings("rawtypes")
	public MissingBeanException(Branch branch, Object beanId) {
		this.branch = branch;
		this.beanId = beanId;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Branch getBranch() {
		return branch;
	}

	@Override
	public Object getBeanId() {
		return beanId;
	}
}