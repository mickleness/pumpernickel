package com.pump.data.branch;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.pump.data.BeanState;
import com.pump.data.Key;

/**
 * This is an implementation of {@link Branch} that stores all its revisions
 * in memory.
 * <p>
 * This should not be used for a large-scale implementation of hundreds of thousands
 * of beans, but it can generally accommodate thousands of beans.
 */
public class MemoryBranch<K> extends AbstractBranch<K> {

	private static Object NULL = new Object();
	private static final Key<Boolean> FIELD_DELETED = new Key<>(Boolean.class, MemoryBranch.class.getName()+"#isDeleted");
	private static final Key<Boolean> FIELD_CREATED = new Key<>(Boolean.class, MemoryBranch.class.getName()+"#isCreated");
	
	protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	protected Map<K, Map<String, TreeMap<Revision, Object>>> dataByBeanId = new HashMap<>();
	protected Collection<Revision> keyRevisions = new HashSet<>();
	protected Revision currentRevision = initialRevision.clone();
	protected Revision lastCommittedRevision = null;
	
	protected UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();
		}
	};

	public MemoryBranch(String name) {
		super(null, null, name);
	}
	
	protected MemoryBranch(Branch<K> parent,Revision parentRevision,String name) {
		super(parent, parentRevision, name);
	}

	@Override
	public Branch<K> createBranch(String name) {
		Object lock = acquireWriteLock();
		try {
			keyRevisions.add(getRevision());
			return new MemoryBranch<K>(this, currentRevision, name);
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public Revision getRevision() {
		Object lock = acquireWriteLock();
		try {
			keyRevisions.add(currentRevision);
			return currentRevision;
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public Lock acquireReadLock() {
		Lock returnValue = readWriteLock.readLock();
		returnValue.lock();
		return returnValue;
	}

	@Override
	public Lock acquireWriteLock() {
		Lock returnValue = readWriteLock.writeLock();
		returnValue.lock();
		return returnValue;
	}

	@Override
	public void releaseLock(Object lock) {
		Lock l = (Lock)lock;
		if(l instanceof WriteLock) {
			WriteLock w = (WriteLock)l;
			if( w.getHoldCount()==1 ) {
				currentRevision = currentRevision.increment();
			}
		}
		l.unlock();
	}
	
	@Override
	public BeanState getState(K beanId,Revision revision) {
		if(beanId==null)
			throw new NullPointerException();
		if(revision!=null && revision.getBranch()!=this)
			throw new IllegalRevisionBranchException("The revision provided relates to branch \""+revision.getBranch().getName()+"\" (not \""+getName()+"\"", this, revision);
		
		Lock lock = acquireReadLock();
		try {
			Map<String, TreeMap<Revision, Object>> beanData = dataByBeanId.get(beanId);
			TreeMap<Revision, Object> deletedField = beanData==null ? null : beanData.get(FIELD_DELETED.toString());
			TreeMap<Revision, Object> createdField = beanData==null ? null : beanData.get(FIELD_CREATED.toString());
			
			if(revision==null)
				revision = currentRevision;
			
			int thresholdSize = beanData==null ? 0 : beanData.size();
			if(deletedField!=null) thresholdSize--;
			if(createdField!=null) thresholdSize--;
			boolean exists = beanData!=null && beanData.size()>thresholdSize;
				
			
			Entry<Revision, Object> deletedFloor = deletedField==null ? null : deletedField.floorEntry(revision);
			Entry<Revision, Object> createdFloor = createdField==null ? null : createdField.floorEntry(revision);
			
			if(deletedFloor==null && createdFloor==null) {
				//this branch neither created nor deleted this bean
				if(exists)
					return BeanState.EXISTS;
				if(parent!=null) {
					BeanState returnValue = parent.getState(beanId, parentRevision);
					if(returnValue==BeanState.CREATED)
						returnValue = BeanState.EXISTS;
					return returnValue;
				} else {
					return BeanState.UNDEFINED;
				}
			} else if(deletedFloor==null && createdFloor!=null) {
				return BeanState.CREATED;
			} else if(deletedFloor!=null && createdFloor==null) {
				return BeanState.DELETED;
			} else {
				if(deletedFloor.getKey().compareTo(createdFloor.getKey())<0) {
					return BeanState.CREATED;
				} 
				return BeanState.DELETED;
			}
			
		} finally {
			releaseLock(lock);
		}
	}
	
	@Override
	public Object getField(K beanId,String fieldName,Revision revision) throws MissingBeanException {
		return doGetField(beanId, fieldName, revision)[0];
	}
	
	private Object[] doGetField(K beanId,String fieldName,Revision revision) throws MissingBeanException {
		if(beanId==null)
			throw new NullPointerException();
		if(fieldName==null)
			throw new NullPointerException();
		if(revision!=null && revision.getBranch()!=this)
			throw new IllegalRevisionBranchException("The revision provided relates to branch \""+revision.getBranch().getName()+"\" (not \""+getName()+"\"", this, revision);
		
		Lock lock = acquireReadLock();
		try {
			BeanState state = getState(beanId, revision);
			if(state==BeanState.DELETED)
				throw new DeletedBeanException(this, beanId);
			if(state==BeanState.UNDEFINED)
				throw new MissingBeanException(this, beanId);
			
			Map<String, TreeMap<Revision, Object>> beanData = dataByBeanId.get(beanId);
			TreeMap<Revision, Object> fieldRevisions = beanData==null ? null : beanData.get(fieldName);
			
			if(fieldRevisions==null) {
				boolean isRoot = parent==null;
				if(isRoot || state==BeanState.CREATED) {
					return new Object[] { null, null};
				}
				Object value = parent.getField(beanId, fieldName, parentRevision);
				return new Object[] { value, null };
			}
			
			if(revision==null)
				revision = currentRevision;
			
			Entry<Revision, Object> floorRevision = fieldRevisions.floorEntry(revision);
			Object value = floorRevision==null ? null : floorRevision.getValue();
			
			if(value==NULL) {
				value = null;
			}

			return new Object[] { value, floorRevision==null ? null : floorRevision.getKey() };
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public Object setField(K beanId, String fieldName, Object newValue) throws MissingBeanException {
		if(beanId==null)
			throw new NullPointerException();
		if(fieldName==null)
			throw new NullPointerException();
		
		Lock lock = acquireWriteLock();
		try {
			Object[] oldValue = doGetField(beanId, fieldName, null);
			Map<String, TreeMap<Revision, Object>> beanData = dataByBeanId.get(beanId);
			if(beanData==null) {
				beanData = new HashMap<>();
				dataByBeanId.put(beanId, beanData);
			}
			
			TreeMap<Revision, Object> fieldRevisionMap = beanData.get(fieldName);
			if(fieldRevisionMap==null) {
				fieldRevisionMap = new TreeMap<>();
				beanData.put(fieldName, fieldRevisionMap);
			}
			
			Object newAssignment = newValue==null ? NULL : newValue;
			fieldRevisionMap.put(currentRevision, newAssignment);
			
			return oldValue[0];
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public void createBean(K beanId) throws DuplicateBeanIdException {
		if(beanId==null)
			throw new NullPointerException();
		
		Lock lock = acquireWriteLock();
		try {
			BeanState state = getState(beanId, null);
			if(state==BeanState.CREATED || state==BeanState.EXISTS)
				throw new DuplicateBeanIdException(this, beanId);
			
			Map<String, TreeMap<Revision, Object>> beanData = dataByBeanId.get(beanId);
			
			if(beanData==null) {
				beanData = new HashMap<>();
				dataByBeanId.put(beanId, beanData);
			}
			
			TreeMap<Revision, Object> fieldRevisionMap = beanData.get(FIELD_CREATED.toString());
			if(fieldRevisionMap==null) {
				fieldRevisionMap = new TreeMap<>();
				beanData.put(FIELD_CREATED.toString(), fieldRevisionMap);
			}

			fieldRevisionMap.put(currentRevision, Boolean.TRUE);
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public void deleteBean(K beanId) throws MissingBeanException {
		if(beanId==null)
			throw new NullPointerException();
		
		Lock lock = acquireWriteLock();
		try {
			BeanState state = getState(beanId, null);
			if(state==BeanState.DELETED || state==BeanState.UNDEFINED)
				throw new MissingBeanException(this, beanId);
			
			Map<String, TreeMap<Revision, Object>> beanData = dataByBeanId.get(beanId);
			
			if(beanData==null) {
				beanData = new HashMap<>();
				dataByBeanId.put(beanId, beanData);
			}
			
			TreeMap<Revision, Object> fieldRevisionMap = beanData.get(FIELD_DELETED.toString());
			if(fieldRevisionMap==null) {
				fieldRevisionMap = new TreeMap<>();
				beanData.put(FIELD_DELETED.toString(), fieldRevisionMap);
			}
			fieldRevisionMap.put(currentRevision, Boolean.TRUE);
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public Revision getLastRevision(K beanId) {
		if(beanId==null)
			throw new NullPointerException();
		
		Object lock = acquireReadLock();
		try {
			Map<String, TreeMap<Revision, Object>> fieldData = dataByBeanId.get(beanId);
			Revision lastRevision = null;
			if(fieldData!=null) {
				for(Entry<String, TreeMap<Revision, Object>> entry : fieldData.entrySet()) {
					TreeMap<Revision, Object> revisionValueMap = entry.getValue();
					Revision fieldLastRevision = revisionValueMap.lastKey();
					if(lastRevision==null || fieldLastRevision.compareTo(lastRevision)>0) {
						lastRevision = fieldLastRevision;
					}
				}
			}
			
			return lastRevision;
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public Revision getLastRevision(K beanId, String fieldName) {
		if(beanId==null)
			throw new NullPointerException();
		if(fieldName==null)
			throw new NullPointerException();
		
		Object lock = acquireReadLock();
		try {
			Map<String, TreeMap<Revision, Object>> fieldData = dataByBeanId.get(beanId);
			TreeMap<Revision, Object> revisionValueMap = fieldData==null ? null : fieldData.get(fieldName);
			Revision lastRevision = revisionValueMap==null ? null : revisionValueMap.lastKey();
			return lastRevision;
		} finally {
			releaseLock(lock);
		}
	}

	@Override
	public Collection<K> getModifiedBeans() {
		Collection<K> returnValue = new HashSet<>();

		Object lock = acquireReadLock();
		try {
			returnValue.addAll( dataByBeanId.keySet() );
			returnValue.removeAll( getIgnorableBeans() );
			return returnValue;
		} finally {
			releaseLock(lock);
		}
	}
	
	
	@Override
	public void save() throws SaveException {
		if(parent==null)
			throw new RuntimeException("This branch has no parent to save data to.");

		BranchListener<K>[] myListeners = getListeners();
		
		Object myReadLock = acquireReadLock();
		Object parentWriteLock = parent.acquireWriteLock();
		Object parentReadLock = parent.acquireReadLock();
		try {
			try {
				Collection<K> ignoredBeans = getIgnorableBeans();
				
				List<SaveException> allProblems = validateCommit(ignoredBeans);
				for(BranchListener<K> listener : myListeners) {
					try {
						listener.beforeSave(parent, this);
					} catch(MultipleSaveException mme) {
						allProblems.addAll(mme.getSaveExceptions());
					} catch(SaveException me) {
						allProblems.add(me);
					}
					//any other exceptions: we can throw those to abort the commit
				}
				
				if(allProblems.size()==1)
					throw allProblems.get(0);
				
				if(allProblems.size()>1)
					throw new MultipleSaveException(this, allProblems);
				
				Revision r = initialRevision;
				while(r.compareTo(currentRevision)<0) {
					try {
						commitRevision(r, ignoredBeans);
					} catch(SaveException e) {
						allProblems.add(e);
					} catch(BranchException e) {
						allProblems.add(new SaveException(this, r, e));
					}
					r = r.increment();
				}
			} finally {
				lastCommittedRevision = currentRevision.clone();
				parentRevision = parent.getRevision();
				
				releaseLock(myReadLock);
				parent.releaseLock(parentWriteLock);
			}

			//we've released the parent's write lock, but we've retained
			//the parent's read lock while our listeners are being notified:
			
			for(BranchListener<K> listener : myListeners) {
				try {
					listener.branchSaved(parent, this);
				} catch(Exception e) {
					uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
				}
			}
		} finally {
			parent.releaseLock(parentReadLock);
		}
	}
	
	/**
	 * This handler will be notified if a BranchListener throws an exception during
	 * {@link BranchListener#branchSaved(Branch, Branch)}.
	 * <p>
	 * The default handler just called <code>ex.printStackTrace()</code>.
	 * 
	 * @param handler the 
	 */
	public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
		if(handler==null)
			throw new NullPointerException();
		
		uncaughtExceptionHandler = handler;	
	}

	protected List<SaveException> validateCommit(Collection<K> ignoredBeans) {
		Object myLock = acquireReadLock();
		try {
			List<SaveException> allProblems = new ArrayList<>();
			for(Entry<K, Map<String, TreeMap<Revision, Object>>> beanIdToFieldInfo : dataByBeanId.entrySet()) {
				K beanId = beanIdToFieldInfo.getKey();
				if(!ignoredBeans.contains(beanId)) {
					Map<String, TreeMap<Revision, Object>> beanDataByField = beanIdToFieldInfo.getValue();
					for(Entry<String, TreeMap<Revision, Object>> fieldNameToRevisions : beanDataByField.entrySet()) {
						String fieldName = fieldNameToRevisions.getKey();
						
						Revision myLastFieldRevision = fieldNameToRevisions.getValue().lastKey();
						if(lastCommittedRevision!=null && myLastFieldRevision.compareTo(lastCommittedRevision)<0) {
							continue;
						}
						
						if(FIELD_CREATED.toString().equals(fieldName) || FIELD_DELETED.toString().equals(fieldName) ) {
							BeanState myState = getState(beanId);
							BeanState parentState = parent.getState(beanId);
							Revision parentBeanRevision = parent.getLastRevision(beanId);
							if(parentBeanRevision!=null && parentBeanRevision.compareTo(parentRevision)<0) {
								//no matter what, this is OK: because the parent hasn't further modified the bean
							} else {
								if(myState==parentState) {
									//weird, both branches did the same thing... but OK.
								} else if(myState==BeanState.CREATED && parentState==BeanState.UNDEFINED) {
									//this is fine
								} else if(myState==BeanState.DELETED && (parentState==BeanState.CREATED || parentState==BeanState.EXISTS)) {
									//this is fine
								} else {
									allProblems.add(new SaveException(this, beanId, parentRevision, 
											"The bean \""+beanId+"\" is classified as "+myState+" in this branch, but it is classified as "+parentState+" in the parent branch."));
								}
							}
						} else {
							Revision parentFieldRevision = parent.getLastRevision(beanId, fieldName);
							if(parentFieldRevision!=null && parentFieldRevision.compareTo(parentRevision)>0) {
								Object parentValue;
								try {
									parentValue = parent.getField(beanId, fieldName);
									Object myValue = fieldNameToRevisions.getValue().lastEntry().getValue();
									if(Objects.equals(parentValue, myValue)) {
										//both the parent and this branch set the value to the same thing, so this is OK.
									} else {
										allProblems.add(new SaveException(this, beanId, parentFieldRevision, 
												"The field \""+fieldName+"\" for bean \""+beanId+"\" was modified in the parent branch (revision "+parentFieldRevision+")."));
									}
								} catch (MissingBeanException e) {
									//this shouldn't happen since we just established that the bean/field combo exists, right?
									throw new RuntimeException(e);
								}
							}
						}
					}
				}
			}
			return allProblems;
		} finally {
			releaseLock(myLock);
		}
	}

	private Collection<K> getIgnorableBeans() {
		Collection<K> returnValue = new HashSet<>();
		for(Entry<K, Map<String, TreeMap<Revision, Object>>> entry : dataByBeanId.entrySet()) {
			Map<String, TreeMap<Revision, Object>> fieldMap = entry.getValue();
			TreeMap<Revision, Object> creation = fieldMap.get(FIELD_CREATED.toString());
			TreeMap<Revision, Object> deletion = fieldMap.get(FIELD_DELETED.toString());
			if(creation!=null && deletion!=null) {
				Revision lastCreation = creation.lastKey();
				Revision lastDeletion = deletion.lastKey();
				if(lastDeletion.compareTo(lastCreation)>0) {
					K bean = entry.getKey();
					returnValue.add(bean);
				}
			}
		}
		return returnValue;
	}

	private void commitRevision(Revision r,Collection<K> ignorableBeans) throws DuplicateBeanIdException, MissingBeanException, SaveException {
		for(Entry<K, Map<String, TreeMap<Revision, Object>>> beanEntry : dataByBeanId.entrySet()) {
			K beanId = beanEntry.getKey();
			if(!ignorableBeans.contains(beanId)) {
				for(Entry<String, TreeMap<Revision, Object>> fieldRevisionEntry : beanEntry.getValue().entrySet()) {
					TreeMap<Revision, Object> revisionMap = fieldRevisionEntry.getValue();
	
					Object newValue = revisionMap.get(r);
					boolean isDefined = newValue!=null;
					if(isDefined) {
						String fieldName = fieldRevisionEntry.getKey();

						Revision lastFieldRevision = revisionMap.lastKey();
						
						if(lastCommittedRevision!=null && lastFieldRevision!=null && lastFieldRevision.compareTo(lastCommittedRevision)<0) {
							continue;
						}
						
						if(FIELD_CREATED.toString().equals(fieldName)) {
							parent.createBean(beanId);
						} else if(FIELD_DELETED.toString().equals(fieldName)) {
							parent.deleteBean(beanId);
						} else {
							if(newValue==NULL)
								newValue = null;
							try {
								Revision lastFieldParentRevision = parent.getLastRevision(beanId, fieldName);
								if(parentRevision!=null && lastFieldParentRevision!=null && parentRevision.compareTo(lastFieldParentRevision)<0)
									throw new SaveException(parent, beanId, lastFieldParentRevision, "The field \""+fieldName+"\" on bean \""+beanId+"\" was modified after "+parentRevision.toString().toLowerCase());
								
								parent.setField(beanId, fieldName, newValue);
							} catch(SaveException e) {
								throw new SaveException(this, beanId, r, "The parent branch contained a conflicting value for field \""+fieldName+"\" for bean \""+beanId+"\".");
							}
						}
					}
				}
			}
		}
	}

	@Override
	public Map<String, Object> getBean(K beanId) {
		if(beanId==null)
			throw new NullPointerException();
		
		Object lock = acquireReadLock();
		try {
			Map<String, Object> returnValue;
			if(parent==null) {
				returnValue = null;
			} else {
				returnValue = parent.getBean(beanId);
			}
			
			Map<String, TreeMap<Revision, Object>> beanDataByField = dataByBeanId.get(beanId);
			
			if(beanDataByField==null) {
				return returnValue;
			}
			
			TreeMap<Revision, Object> deletionMap = beanDataByField.get(FIELD_DELETED.toString());
			TreeMap<Revision, Object> creationMap = beanDataByField.get(FIELD_CREATED.toString());
			Revision lastDeletion = deletionMap==null ? null : deletionMap.lastKey();
			Revision lastCreation = creationMap==null ? null : creationMap.lastKey();
			if(lastDeletion!=null && (lastCreation==null || lastCreation.compareTo(lastDeletion)<0)) {
				return null;
			}
			
			if(lastCreation!=null) {
				returnValue = new HashMap<>();
			}
			
			for(Entry<String, TreeMap<Revision, Object>> entry : beanDataByField.entrySet()) {
				String fieldName = entry.getKey();
				if(!(FIELD_DELETED.toString().equals(fieldName) || FIELD_CREATED.toString().equals(fieldName))) {
					Revision fieldRevision = entry.getValue().lastKey();
					if(lastCreation==null || lastCreation.compareTo(fieldRevision)<0) {
						if(returnValue==null)
							throw new IllegalStateException("Bean data was detected for \""+beanId+"\" (field \""+fieldName+"\", but there is no record of that bean being created.");
						Object value = entry.getValue().lastEntry().getValue();
						if(value==NULL)
							value = null;
						returnValue.put(fieldName, value);
					}
				}
			}
			
			return returnValue;
		} finally {
			releaseLock(lock);
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
