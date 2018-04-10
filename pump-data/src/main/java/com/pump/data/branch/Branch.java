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

import java.util.Collection;
import java.util.Map;

import com.pump.data.BeanState;
import com.pump.data.Key;

/**
 * This is a collection of bean data that keeps track of revisions.
 * <p>
 * A branch can create a child branch which represents a snapshot of the parent
 * branch at that instant. The child branch can merge back to the parent with a
 * call to {@link #save()}. (Otherwise: if a child branch is abandoned without
 * saving then it should eventually be garbage collected and disappear.)
 * <p>
 * Internally a branch considers a "bean" to basically be a map of Strings to
 * Objects. If possible: it is preferred to use {@link com.pump.data.Key keys}
 * to refer to the keys in these maps, because that offers type safety and
 * possible {@link com.pump.data.BoundsChecker bounds checking}. But that is
 * optional, and it is up to the caller to responsibly make sure keys don't
 * conflict with one other. (For example: if one <code>Key</code> object is
 * named "roomNumber" and relates to an Integer, but another <code>Key</code> is
 * also named "roomNumber" and relates to a Short: sooner or later a
 * ClassCastException will probably result as you interact with this data,
 * because internally both are simply keyed with the String "roomNumber".)
 * <p>
 * It's important to note that the <code>Revision</code> objects this Branch
 * interacts with are unique to this Branch. Although a <code>Revision</code>
 * may contain meta information (such as timestamps, authors, comments, etc.),
 * it is not explicitly aware of or comparable to its parent's revisions.
 *
 * @param <K>
 *            the type of bean ids. In simple cases, this may just be a String.
 *            You can also create a more complex identifying object that
 *            represents a String ID and an object type.
 */
public interface Branch<K> {

	/**
	 * Return the optional parent of this branch.
	 */
	public Branch<K> getParent();

	/**
	 * Create a new child of this branch.
	 * 
	 * @param name
	 *            a human-readable name that may be mentioned in Exceptions or
	 *            other feedback. For example, if you try to save this branch
	 *            the error will be much more helpful if it can resemble: "The
	 *            branch 'add new page' couldn't be added to 'root'." This is
	 *            optional but recommended.
	 * 
	 * @return a new child of this branch.
	 */
	public Branch<K> createBranch(String name);

	/** Return the optional name of this branch. */
	public String getName();

	/**
	 * Add a listener that will be notified before and after a branch is saved.
	 * 
	 * @param branchListener
	 *            the new branch listener.
	 */
	public void addBranchListener(BranchListener<K> branchListener);

	/**
	 * Remove a BranchListener.
	 * 
	 * @param branchListener
	 *            the listener to remove.
	 */
	public void removeBranchListener(BranchListener<K> branchListener);

	/**
	 * Acquire a reentrant read lock. This object must be returned to
	 * {@link #releaseLock(Object)}.
	 * <p>
	 * Implementations may choose how to reasonable manage their locks. For
	 * example, an implementation may not distinguish between a read and a write
	 * lock. Locks must be reentrant within the same thread, though.
	 * 
	 * @return a reentrant read lock for this branch.
	 */
	public Object acquireReadLock();

	/**
	 * Acquire a reentrant write lock. This object must be returned to
	 * {@link #releaseLock(Object)}.
	 * 
	 * @return a reentrant write lock for this branch.
	 */
	public Object acquireWriteLock();

	/**
	 * Release a lock previously acquired via {@link #acquireReadLock()} or
	 * {@link #acquireWriteLock()}.
	 * 
	 * @param lock
	 *            a read or write lock previously previously acquired via
	 *            {@link #acquireReadLock()} or {@link #acquireWriteLock()}.
	 */
	public void releaseLock(Object lock);

	/**
	 * Return the state of a bean relative to this branch.
	 * <p>
	 * For example, if this branch is aware of deleting this bean, then it may
	 * return <code>DELETED</code>. However if this branch created this bean,
	 * deleted it, and then saved to its parent branch: the parent branch may
	 * return <code>UNDEFINED</code>.
	 * <p>
	 * If a bean was created in a parent branch, then this may return
	 * <code>EXISTS</code>, but if a bean was created in this branch (and it is
	 * still exists at this point) then this should return <code>CREATED</code>.
	 * <p>
	 * These complex scenarios are open to some interpretation by subclasses.
	 * 
	 * @param beanId
	 *            the id of the bean to examine.
	 * @return the state of this bean relative to this branch.
	 */
	public BeanState getState(K beanId);

	/**
	 * Return the state of a bean relative to this branch as of a certain
	 * revision.
	 * <p>
	 * For example, if this branch is aware of deleting this bean, then it may
	 * return <code>DELETED</code>. However if this branch created this bean,
	 * deleted it, and then saved to its parent branch: the parent branch may
	 * return <code>UNDEFINED</code>.
	 * <p>
	 * If a bean was created in a parent branch, then this may return
	 * <code>EXISTS</code>, but if a bean was created in this branch (and it is
	 * still exists at this point) then this should return <code>CREATED</code>.
	 * <p>
	 * These complex scenarios are open to some interpretation by subclasses.
	 * 
	 * @param beanId
	 *            the id of the bean to examine.
	 * @param revision
	 *            the latest revision to consider. If this is null you can
	 *            assume you're interacting with the most recent revision.
	 * 
	 * @return the state of this bean relative to this branch.
	 */
	public BeanState getState(K beanId, Revision revision);

	/**
	 * Return a field from a bean.
	 * 
	 * @param beanId
	 *            the bean to get the field of.
	 * @param fieldName
	 *            the name of the field to get.
	 * @param revision
	 *            the revision for which the field value should be dated. For
	 *            example if a field starts out with the value of "abc" at
	 *            revision #1, and then at revision #10 it becomes "xyz", then
	 *            if you pass revision #5 to this method: it will return "abc".
	 *            <p>
	 *            If this is null: the current value will be returned.
	 *            <p>
	 *            This is used by branches to reconstruct a model of a bean
	 *            based on exactly when the branch was created.
	 * 
	 * @return the indicated field from the indicated bean as of the indicated
	 *         revision, or null if that field is undefined.
	 * 
	 * @throws MissingBeanException
	 *             if the beanId provided isn't a recognized bean.
	 */
	public Object getField(K beanId, String fieldName, Revision revision)
			throws MissingBeanException;

	/**
	 * Return a field from a bean.
	 * 
	 * @param beanId
	 *            the bean to get the field of.
	 * @param fieldName
	 *            the name of the field to get.
	 * 
	 * @return the indicated field from the indicated bean, or null if that
	 *         field is undefined.
	 * 
	 * @throws MissingBeanException
	 *             if the beanId provided isn't a recognized bean.
	 */
	public Object getField(K beanId, String fieldName)
			throws MissingBeanException;

	/**
	 * Return a field from a bean.
	 * 
	 * @param beanId
	 *            the bean to get the field of.
	 * @param field
	 *            the field to get.
	 * 
	 * @return the indicated field from the indicated bean, or null if that
	 *         field is undefined.
	 * 
	 * @throws MissingBeanException
	 *             if the beanId provided isn't a recognized bean.
	 */
	public <V> V getField(K beanId, Key<V> field) throws MissingBeanException;

	/**
	 * Return a field from a bean.
	 * 
	 * @param beanId
	 *            the bean to get the field of.
	 * @param field
	 *            the field to get.
	 * @param revision
	 *            the revision for which the field value should be dated. For
	 *            example if a field starts out with the value of "abc" at
	 *            revision #1, and then at revision #10 it becomes "xyz", then
	 *            if you pass revision #5 to this method: it will return "abc".
	 *            <p>
	 *            If this is null: the current value will be returned.
	 *            <p>
	 *            This is used by branches to reconstruct a model of a bean
	 *            based on exactly when the branch was created.
	 * 
	 * @return the indicated field from the indicated bean as of the indicated
	 *         revision, or null if that field is undefined.
	 * 
	 * @throws MissingBeanException
	 *             if the beanId provided isn't a recognized bean.
	 */
	public <V> V getField(K beanId, Key<V> field, Revision revision)
			throws MissingBeanException;

	/**
	 * Create a new bean with a given ID.
	 * 
	 * @param beanId
	 *            the new bean ID to use.
	 * 
	 * @throws DuplicateBeanIdException
	 *             if a bean already exists with this ID.
	 */
	public void createBean(K beanId) throws DuplicateBeanIdException;

	/**
	 * Delete a bean.
	 * 
	 * @param beanId
	 *            the ID of the bean to delete.
	 * 
	 * @throws MissingBeanException
	 *             if no bean is found with the indicated ID.
	 */
	public void deleteBean(K beanId) throws MissingBeanException;

	/**
	 * Assign a field for a bean.
	 * <p>
	 * This method will consult the key's
	 * {@link com.pump.data.Key#getBoundsCheckers() bounds checkers} to validate
	 * the incoming value.
	 * <p>
	 * Because this performs additional validations and is parameterized, this
	 * is the prefered method to set field data if possible.
	 * 
	 * @param beanId
	 *            the id of the bean to modify.
	 * @param field
	 *            the field to modify.
	 * @param newValue
	 *            the new value to assign to this field (may be null).
	 * 
	 * @return the previous value this field held, or null if it was previously
	 *         undefined.
	 * 
	 * @throws MissingBeanException
	 *             if the beanId provided isn't a recognized bean.
	 */
	public <V> V setField(K beanId, Key<V> field, V newValue)
			throws MissingBeanException;

	/**
	 * Assign a field for a bean.
	 * 
	 * @param beanId
	 *            the id of the bean to modify.
	 * @param fieldName
	 *            the name of the field to modify.
	 * @param newValue
	 *            the new value to assign to this field (may be null).
	 * 
	 * @return the previous value this field held, or null if it was previously
	 *         undefined.
	 * 
	 * @throws MissingBeanException
	 *             if the beanId provided isn't a recognized bean.
	 */
	public Object setField(K beanId, String fieldName, Object newValue)
			throws MissingBeanException;

	/**
	 * Return the current revision of this branch.
	 * 
	 * @return the current revision of this branch.
	 */
	public Revision getRevision();

	/**
	 * Return the last revision in which a bean was modified, or null if this
	 * bean was never modified in this branch. Note this may be null if a bean
	 * exists but was last modified in a parent branch.
	 * 
	 * @param beanId
	 *            the ID of the bean to examine.
	 * @return the last revision in which a bean was modified. Note this may be
	 *         null if a bean exists but was last modified in a parent branch.
	 */
	public Revision getLastRevision(K beanId);

	/**
	 * Return the last revision in which a particular field was modified for a
	 * given bean, or null if there is no record in this branch of any such
	 * modification.
	 * 
	 * @param beanId
	 *            the bean ID to examine.
	 * @param fieldName
	 *            the field to examine.
	 * 
	 * @return the last revision in which a particular field was modified for a
	 *         given bean, or null if there is no record in this branch of any
	 *         such modification.
	 */
	public Revision getLastRevision(K beanId, String fieldName);

	/**
	 * Return the last revision in which a particular field was modified for a
	 * given bean, or null if there is no record in this branch of any such
	 * modification.
	 * 
	 * @param beanId
	 *            the bean ID to examine.
	 * @param field
	 *            the field to examine.
	 * 
	 * @return the last revision in which a particular field was modified for a
	 *         given bean, or null if there is no record in this branch of any
	 *         such modification.
	 */
	public Revision getLastRevision(K beanId, Key<?> field);

	/**
	 * Return the first revision of this branch.
	 * 
	 * @return the first revision of this branch.
	 */
	public Revision getFirstRevision();

	/**
	 * Return all the beans this branch modified.
	 * <p>
	 * This may choose not to mention beans that were both created and destroyed
	 * in this branch.
	 */
	public Collection<K> getModifiedBeans();

	/**
	 * Apply the key/value entries in a map to a bean in this branch.
	 * 
	 * @param beanId
	 *            the id of the bean to modify. If this bean does not already
	 *            exist, a new bean with this ID is created.
	 * @param beanData
	 *            the data to store in this bean. This may be empty, but it may
	 *            not be null.
	 * @param completeReplacement
	 *            if false then the incoming data is merged with any existing
	 *            data. If true then any existing data is deleted and replaced
	 *            with the previous argument.
	 * 
	 * @return true if this created a bean, false if it modified an existing
	 *         bean.
	 */
	public boolean setBean(K beanId, Map<String, Object> beanData,
			boolean completeReplacement);

	/**
	 * Return all the key/value pairs for a bean.
	 * <p>
	 * This consults all possible parents to create a complete representation of
	 * the bean.
	 * 
	 * @param beanId
	 *            the bean to fetch the key/value pairs of.
	 * 
	 * @return a map representing all the key/value pairs in a bean, or null if
	 *         the bean is undefined.
	 */
	public abstract Map<String, Object> getBean(K beanId);

	/**
	 * This saves all the changes in this branch to its parent. (This may also
	 * be considered "merge", "commit" or "push".)
	 * <p>
	 * This will immediately throw a <code>NullPointerException</code> if there
	 * is no parent.
	 * <p>
	 * This requires acquiring a read lock on this branch and a write lock on
	 * its parent.
	 * <p>
	 * This method only saves changes performed since the last call to
	 * <code>save()</code>.
	 * 
	 * @throws SaveException
	 *             if this branch contains changes that may conflict with its
	 *             parent.
	 *             <p>
	 *             This method may choose to NOT throw a SaveException if both
	 *             branches modified a bean/field in the same way. For example,
	 *             if both branches deleted bean X, or assigned the same value
	 *             to the same field: then we don't necessarily have a collision
	 *             problem.
	 */
	public void save() throws SaveException;
}