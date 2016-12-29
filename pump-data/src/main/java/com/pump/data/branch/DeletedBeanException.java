package com.pump.data.branch;

public class DeletedBeanException extends MissingBeanException {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public DeletedBeanException(Branch branch, Object beanId) {
		super(branch, beanId);
	}

}
