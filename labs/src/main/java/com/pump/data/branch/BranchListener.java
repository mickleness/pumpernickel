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

/**
 * This listener supports two methods related to saving a branch to its parent
 * branch.
 * <p>
 * One method is notified before a save, and is encouraged to throw an
 * exception. Any exception will effectively abort the save, so this acts as a
 * form of validation.
 * <p>
 * The other method is notified after a save, and it is not supposed to throw an
 * exception. This may be used to trigger an update in other parts of the
 * application, for example.
 */
public interface BranchListener<K> {

	/**
	 * This method is called when a child branch is being saved to its parent
	 * before data is saved to the parent. During invocation we should have a
	 * write lock on the parent and a read lock on the child.
	 * <p>
	 * This method may throw a <code>SaveException</code> if it wants to abort
	 * this commit. This method functions as a hook to add custom validation
	 * logic to review a branch before changes are saved. Any other
	 * <code>RuntimeException</code> will also abort the commit.
	 * <p>
	 * If one listener throws a SaveException here, other listeners may also be
	 * notified so the exceptions can be collected and combined.
	 * 
	 * @param parent
	 *            the parent branch the child is being saved to.
	 * @param child
	 *            the child branch the parent is absorbing.
	 * 
	 * @throws SaveException
	 *             if this save request should be rejected. Note this can be a
	 *             {@link MultipleSaveException} to represent multiple problems.
	 */
	public void beforeSave(Branch<K> parent, Branch<K> child)
			throws SaveException;

	/**
	 * This method is called after a branch has been saved to its parent. During
	 * invocation we should have a read lock on both the parent and child
	 * branch.
	 * <p>
	 * This method should not throw an exception. This method functions more as
	 * a traditional listener: it should inspect, but not typically edit. If one
	 * listener throws an exception, it should not prevent other listeners from
	 * also being notified.
	 * 
	 * @param parent
	 *            the parent branch the child was committed to.
	 * @param child
	 *            the child branch the parent absorbed.
	 */
	public void branchSaved(Branch<K> parent, Branch<K> child);
}