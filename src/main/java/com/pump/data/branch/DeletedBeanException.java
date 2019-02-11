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

public class DeletedBeanException extends MissingBeanException {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public DeletedBeanException(Branch branch, Object beanId) {
		super(branch, beanId);
	}

}