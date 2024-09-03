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
package com.pump.data.operator;

/**
 * This is passed to Operators to retrieve attributes from a bean / data source.
 */
public interface OperatorContext {
	/**
	 * Retrieve an attribute from a data source.
	 * <p>
	 * For example: if you use a custom PersonBean object that contains the
	 * fields "firstName" and "age", then you might set up Operators that
	 * evaluate "firstName == 'Sue'" or "age > 20". This OperatorContext object
	 * should be able to accept a bean and a field name (in this case
	 * "firstName" or "age") and return the value the Operator requires to
	 * evaluate.
	 * <p>
	 * If you have Apache's beanutils jar, then a simple bean-like
	 * implementation of this could simply be: <br>
	 * <code>PropertyUtils.getProperty(dataSource, attributeName);</code>
	 * <p>
	 * 
	 * @param dataSource
	 *            an object (probably a bean or a map) passed to an Operator to
	 *            evaluate.
	 * @param attributeName
	 *            the name of the attribute to retrieve.
	 *            <p>
	 *            This may be several layers removed from the original bean,
	 *            such as "person.address.state"
	 * @return the attribute from the given data source based on the attribute
	 *         name
	 */
	public Object getValue(Object dataSource, String attributeName);
}