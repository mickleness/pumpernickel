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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.data.BeanState;
import com.pump.data.BoundsChecker;
import com.pump.data.Key;

/**
 * This is a helper class implements or redirects a few basic methods
 * of the {@link Branch} interface.
 *
 * @param <K> the type of bean ids. For example, if all beans are identified
 * by a unique UID this may be a String. This could also be a class that encapsulates
 * a bean type and a bean name (so when ID might be "Student" and "123", which would be
 * separate than "Staff" and "123").
 */
public abstract class AbstractBranch<K> implements Branch<K> {

	protected Branch<K> parent;
	protected Revision parentRevision;
	protected String name;
	protected Revision initialRevision = new Revision( this, Long.valueOf(0) );
	protected List<BranchListener<K>> listeners = new ArrayList<>();
	
	public AbstractBranch(String name) {
		this(null, null, name);
	}

	protected AbstractBranch(Branch<K> parent,Revision parentRevision,String name) {
		if(name==null)
			name = "Untitled";
		if(parent!=null && parentRevision==null)
			throw new NullPointerException();
		if(parent!=null && parentRevision!=null && parentRevision.getBranch()!=parent)
			throw new IllegalRevisionBranchException("The revision provided relates to branch \""+parentRevision.getBranch().getName()+"\" (not \""+parent.getName()+"\"", parent, parentRevision);
		
		this.parent = parent;
		this.name = name;
		this.parentRevision = parentRevision;
	}

	@Override
	public boolean setBean(K beanId,Map<String, Object> beanData, boolean completeReplacement) {
		if(beanId==null)
			throw new NullPointerException();
		if(beanData==null)
			throw new NullPointerException();
		
		Object writeLock = acquireWriteLock();
		boolean returnValue;
		try {
			try {
				BeanState state = getState(beanId);
				if(state==BeanState.DELETED || state==BeanState.UNDEFINED) {
					try {
						createBean(beanId);
						returnValue = true;
					} catch (DuplicateBeanIdException e) {
						//this shouldn't happen if we just confirmed the bean doesn't exist
						throw new RuntimeException(e);
					}
				} else {
					returnValue = false;
					
					if(completeReplacement) {
						Map<String, Object> existingBean = getBean(beanId);
						Collection<String> fieldsToDelete = existingBean.keySet();
						fieldsToDelete.removeAll(beanData.keySet());
						for(String field : fieldsToDelete) {
							setField(beanId, field, null);
						}
					}
				}
			
				for(Entry<String, Object> entry : beanData.entrySet()) {
					setField(beanId, entry.getKey(), entry.getValue());
				}
			} catch(MissingBeanException e) {
				//this shouldn't happen if we just created the bean as necessary...
				throw new RuntimeException(e);
			}
		} finally {
			releaseLock(writeLock);
		}
		
		return returnValue;
	}

	@SuppressWarnings("unchecked")
	public BranchListener<K>[] getListeners() {
		synchronized(listeners) {
			return listeners.toArray(new BranchListener[listeners.size()]);
		}
	}
	
	@Override
	public void addBranchListener(BranchListener<K> branchListener) {
		if(branchListener==null)
			return;
		
		synchronized(listeners) {
			listeners.add(branchListener);
		}
	}

	@Override
	public void removeBranchListener(BranchListener<K> branchListener) {
		if(branchListener==null)
			return;
		
		synchronized(listeners) {
			listeners.remove(branchListener);
		}
	}

	@Override
	public Revision getFirstRevision() {
		return initialRevision.clone();
	}

	@Override
	public Branch<K> getParent() {
		return parent;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getField(K beanId,String fieldName) throws MissingBeanException {
		return getField(beanId, fieldName, null);
	}

	@Override
	public BeanState getState(K beanId) {
		return getState(beanId, null);
	}

	@Override
	public <V> V getField(K beanId, Key<V> field) throws MissingBeanException {
		return getField(beanId, field, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V getField(K beanId, Key<V> field, Revision revision)
			throws MissingBeanException {
		Object value = getField(beanId, field.toString(), revision);
		Class<V> type = field.getType();
		if(value==null || type.isInstance(value))
		{
			return (V) value;
		}
		throw new ClassCastException("The field \""+field.toString()+"\" should be a "+type.getName()+", but it was a "+value.getClass().getName()+".");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V setField(K beanId, Key<V> field, V newValue)
			throws MissingBeanException {
		if(field==null)
			throw new NullPointerException();
		
		for(BoundsChecker<V> boundsChecker : field.getBoundsCheckers()) {
			boundsChecker.check(field, newValue);
		}
		
		Object value = setField(beanId, field.toString(), newValue);
		Class<V> type = field.getType();
		if(value==null || type.isInstance(value))
		{
			return (V) value;
		}
		throw new ClassCastException("The field \""+field.toString()+"\" should be a "+type.getName()+", but it was a "+value.getClass().getName()+".");
	}

	@Override
	public Revision getLastRevision(K beanId, Key<?> field) {
		return getLastRevision(beanId, field.toString());
	}
}