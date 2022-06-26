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
package com.pump.util.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import junit.framework.TestCase;

import com.pump.util.list.ObservableList.RecursiveListenerModificationException;

public class ObservableListTest extends TestCase {

	static class LogListListener implements ListListener {
		List<ListEvent> log = new ArrayList<>();

		@Override
		public void elementsAdded(AddElementsEvent event) {
			log.add(event);
		}

		@Override
		public void elementsRemoved(RemoveElementsEvent event) {
			log.add(event);
		}

		@Override
		public void elementChanged(ChangeElementEvent event) {
			log.add(event);
		}

		@Override
		public void elementsReplaced(ReplaceElementsEvent event) {
			log.add(event);

		}

		public void pullAddEvent(int index, Object... elements) {
			ListEvent event = log.remove(0);
			assertTrue(event instanceof AddElementsEvent);
			AddElementsEvent event2 = (AddElementsEvent) event;
			assertEquals(index, event2.getIndex());
			assertEquals(Arrays.asList(elements), event2.getNewElements());
		}

		public void pullRemoveEvent(int i1, Object obj1) {
			ListEvent event = log.remove(0);
			assertTrue(event instanceof RemoveElementsEvent);
			RemoveElementsEvent event2 = (RemoveElementsEvent) event;
			Map<Integer, Object> map = event2.getRemovedElements();
			assertEquals(1, map.size());
			assertEquals(obj1, map.get(i1));
		}

		public void pullRemoveEvent(int i1, Object obj1, int i2, Object obj2,
				int i3, Object obj3) {
			ListEvent event = log.remove(0);
			assertTrue(event instanceof RemoveElementsEvent);
			RemoveElementsEvent event2 = (RemoveElementsEvent) event;
			Map<Integer, Object> map = event2.getRemovedElements();
			assertEquals(3, map.size());
			assertEquals(obj1, map.get(i1));
			assertEquals(obj2, map.get(i2));
			assertEquals(obj3, map.get(i3));
		}

		public void pullRemoveEvent(int i1, Object obj1, int i2, Object obj2) {
			ListEvent event = log.remove(0);
			assertTrue(event instanceof RemoveElementsEvent);
			RemoveElementsEvent event2 = (RemoveElementsEvent) event;
			Map<Integer, Object> map = event2.getRemovedElements();
			assertEquals(2, map.size());
			assertEquals(obj1, map.get(i1));
			assertEquals(obj2, map.get(i2));
		}

		public void pullRemoveEvent(int i1, Object obj1, int i2, Object obj2,
				int i3, Object obj3, int i4, Object obj4, int i5, Object obj5) {
			ListEvent event = log.remove(0);
			assertTrue(event instanceof RemoveElementsEvent);
			RemoveElementsEvent event2 = (RemoveElementsEvent) event;
			Map<Integer, Object> map = event2.getRemovedElements();
			assertEquals(5, map.size());
			assertEquals(obj1, map.get(i1));
			assertEquals(obj2, map.get(i2));
			assertEquals(obj3, map.get(i3));
			assertEquals(obj4, map.get(i4));
			assertEquals(obj5, map.get(i5));
		}

		public void pullSetEvent(int index, Object oldElement, Object newElement) {
			ListEvent event = log.remove(0);
			assertTrue(event instanceof ChangeElementEvent);
			ChangeElementEvent event2 = (ChangeElementEvent) event;
			assertEquals(index, event2.getIndex());
			assertEquals(oldElement, event2.getOldElement());
			assertEquals(newElement, event2.getNewElement());
		}

		public void pullReplaceEvent(List oldElements, List newElements) {
			ListEvent event = log.remove(0);
			assertTrue(event instanceof ReplaceElementsEvent);
			ReplaceElementsEvent event2 = (ReplaceElementsEvent) event;
			assertEquals(oldElements, event2.getOldElements());
			assertEquals(newElements, event2.getNewElements());
		}
	}

	public void testAddMethod1() {
		ObservableList<String> list = new ObservableList<String>();
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		list.add("E");
		listener.pullAddEvent(0, "A");
		listener.pullAddEvent(1, "B");
		listener.pullAddEvent(2, "C");
		listener.pullAddEvent(3, "D");
		listener.pullAddEvent(4, "E");

		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 5);
		assertTrue(
				list.toString(),
				Arrays.equals(list.toArray(), new String[] { "A", "B", "C",
						"D", "E" }));
	}

	public void testAddMethod2() {
		ObservableList<String> list = new ObservableList<String>();
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		list.add(0, "A");
		list.add(1, "B");
		list.add(0, "C");
		list.add(1, "D");
		list.add(1, "E");
		listener.pullAddEvent(0, "A");
		listener.pullAddEvent(1, "B");
		listener.pullAddEvent(0, "C");
		listener.pullAddEvent(1, "D");
		listener.pullAddEvent(1, "E");
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 5);
		assertTrue(
				list.toString(),
				Arrays.equals(list.toArray(), new String[] { "C", "E", "D",
						"A", "B" }));
	}

	private ObservableList<String> createList(char firstChar, char lastChar) {
		if (firstChar > lastChar)
			throw new IllegalArgumentException(firstChar + ">=" + lastChar);
		ObservableList<String> list = new ObservableList<String>();
		for (char c = firstChar; c <= lastChar; c++) {
			list.add(Character.toString(c));
		}
		list.setTimeoutSeconds(0);
		// normally an exception is just dumped to the console, but we want all
		// exceptions to actually make the unit test fail
		list.setListenerUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				throw new RuntimeException(e);
			}
		});
		return list;
	}

	public void testAddAllMethod1() {
		ObservableList<String> list = new ObservableList<String>();
		List<String> otherList = createList('A', 'C');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		list.addAll(otherList);
		list.addAll(otherList);
		list.addAll(otherList);
		listener.pullAddEvent(0, "A", "B", "C");
		listener.pullAddEvent(3, "A", "B", "C");
		listener.pullAddEvent(6, "A", "B", "C");

		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 9);
		assertTrue(
				list.toString(),
				Arrays.equals(list.toArray(), new String[] { "A", "B", "C",
						"A", "B", "C", "A", "B", "C" }));

		// make sure this method doesn't lock/timeout:
		list.addAll(list);
	}

	public void testAddAllMethod2() {
		ObservableList<String> list = new ObservableList<String>();
		List<String> otherList = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		list.addAll(0, otherList);
		list.addAll(2, otherList);
		list.addAll(5, otherList);

		listener.pullAddEvent(0, "A", "B", "C", "D", "E");
		listener.pullAddEvent(2, "A", "B", "C", "D", "E");
		listener.pullAddEvent(5, "A", "B", "C", "D", "E");
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 15);
		assertTrue(
				list.toString(),
				Arrays.equals(list.toArray(), new String[] { "A", "B", "A",
						"B", "C", "A", "B", "C", "D", "E", "D", "E", "C", "D",
						"E" }));

		// make sure this method doesn't lock/timeout:
		list.addAll(5, list);
	}

	public void testClearMethod() {
		ObservableList<String> list = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		list.clear();
		listener.pullRemoveEvent(0, "A", 1, "B", 2, "C", 3, "D", 4, "E");
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 0);
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] {}));
	}

	@SuppressWarnings("unchecked")
	public void testCloneMethod() {
		ObservableList<String> list = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		ObservableList<String> copy = (ObservableList<String>) list.clone();
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 5);
		assertTrue(
				list.toString(),
				Arrays.equals(list.toArray(), new String[] { "A", "B", "C",
						"D", "E" }));
		assertTrue(
				copy.toString(),
				Arrays.equals(copy.toArray(), new String[] { "A", "B", "C",
						"D", "E" }));
	}

	public void testContainsMethod() {
		ObservableList<String> list = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertTrue(list.contains("A"));
		assertTrue(list.contains("B"));
		assertTrue(list.contains("C"));
		assertTrue(list.contains("D"));
		assertTrue(list.contains("E"));
		assertFalse(list.contains("a"));
		assertFalse(list.contains("b"));
		assertFalse(list.contains("c"));
		assertFalse(list.contains("F"));
		assertFalse(list.contains("G"));
		assertFalse(list.contains(null));
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 5);
	}

	public void testContainsAllMethod() {
		ObservableList<String> list = createList('A', 'E');
		ObservableList<String> otherList = createList('B', 'D');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertTrue(list.containsAll(otherList));
		assertFalse(otherList.containsAll(list));
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 5);
		assertTrue(otherList.size() == 3);

		// make sure this method doesn't lock/timeout:
		assertTrue(list.containsAll(list));
	}

	public void testEqualsMethod() {
		ObservableList<String> list1 = createList('A', 'E');
		ObservableList<String> list2 = createList('A', 'E');
		ObservableList<String> otherList = createList('B', 'D');
		LogListListener listener = new LogListListener();
		list1.addListListener(listener, false);
		assertTrue(list1.equals(list2));
		assertFalse(otherList.equals(list1));
		assertFalse(list1.equals(otherList));
		assertTrue(listener.log.isEmpty());
		assertTrue(list1.size() == 5);
		assertTrue(otherList.size() == 3);
		assertTrue(list1.equals(list1));
	}

	public void testGetMethod() {
		ObservableList<String> list = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertEquals(list.get(0), "A");
		assertEquals(list.get(1), "B");
		assertEquals(list.get(2), "C");
		assertEquals(list.get(3), "D");
		assertEquals(list.get(4), "E");
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 5);
		try {
			list.get(-1);
			fail();
		} catch (RuntimeException e) {
			// pass
		}
		try {
			list.get(5);
			fail();
		} catch (RuntimeException e) {
			// pass
		}
	}

	public void testIndexOfMethod() {
		ObservableList<String> list = createList('A', 'E');
		list.addAll(createList('A', 'E'));
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertTrue(list.indexOf("A") == 0);
		assertTrue(list.indexOf("B") == 1);
		assertTrue(list.indexOf("C") == 2);
		assertTrue(list.indexOf("D") == 3);
		assertTrue(list.indexOf("E") == 4);
		assertTrue(list.indexOf("F") == -1);
		assertTrue(list.indexOf("a") == -1);
		assertTrue(list.indexOf(null) == -1);
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 10);
	}

	public void testIsEmptyMethod() {
		ObservableList<String> list1 = createList('A', 'E');
		ObservableList<String> list2 = new ObservableList<String>();
		assertFalse(list1.isEmpty());
		assertTrue(list2.isEmpty());
	}

	public void testLastIndexOfMethod() {
		ObservableList<String> list = createList('A', 'E');
		list.addAll(createList('A', 'E'));
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertEquals(list.lastIndexOf("A"), 5);
		assertEquals(list.lastIndexOf("B"), 6);
		assertEquals(list.lastIndexOf("C"), 7);
		assertEquals(list.lastIndexOf("D"), 8);
		assertEquals(list.lastIndexOf("E"), 9);
		assertEquals(list.lastIndexOf("F"), -1);
		assertEquals(list.lastIndexOf("a"), -1);
		assertEquals(list.lastIndexOf(null), -1);
		assertTrue(listener.log.isEmpty());
		assertTrue(list.size() == 10);
	}

	public void testRemoveMethod1() {
		ObservableList<String> list = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertEquals(list.remove(0), "A");
		assertTrue(list.toString(), Arrays.equals(list.toArray(), new String[] {
				"B", "C", "D", "E" }));
		assertEquals(list.remove(1), "C");
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] { "B", "D", "E" }));
		assertEquals(list.remove(0), "B");
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] { "D", "E" }));
		assertEquals(list.remove(1), "E");
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] { "D" }));
		assertEquals(list.remove(0), "D");
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] {}));
		listener.pullRemoveEvent(0, "A");
		listener.pullRemoveEvent(1, "C");
		listener.pullRemoveEvent(0, "B");
		listener.pullRemoveEvent(1, "E");
		listener.pullRemoveEvent(0, "D");
		assertTrue(listener.log.isEmpty());
	}

	public void testRemoveMethod2() {
		ObservableList<String> list = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertEquals(list.remove("F"), false);
		assertTrue(
				list.toString(),
				Arrays.equals(list.toArray(), new String[] { "A", "B", "C",
						"D", "E" }));
		assertEquals(list.remove("A"), true);
		assertTrue(list.toString(), Arrays.equals(list.toArray(), new String[] {
				"B", "C", "D", "E" }));
		assertEquals(list.remove("C"), true);
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] { "B", "D", "E" }));
		assertEquals(list.remove("B"), true);
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] { "D", "E" }));
		assertEquals(list.remove("E"), true);
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] { "D" }));
		assertEquals(list.remove("D"), true);
		assertTrue(list.toString(),
				Arrays.equals(list.toArray(), new String[] {}));
		listener.pullRemoveEvent(0, "A");
		listener.pullRemoveEvent(1, "C");
		listener.pullRemoveEvent(0, "B");
		listener.pullRemoveEvent(1, "E");
		listener.pullRemoveEvent(0, "D");
		assertTrue(listener.log.isEmpty());
	}

	public void testRemoveAllMethod() {
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('B', 'D');
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.removeAll(otherList));
			assertFalse(list.removeAll(otherList));
			assertTrue(list.toString(),
					Arrays.equals(list.toArray(), new String[] { "A", "E" }));
			listener.pullRemoveEvent(1, "B", 2, "C", 3, "D");
			assertTrue(listener.log.isEmpty());
		}

		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = new ObservableList<String>();
			otherList.add("B");
			otherList.add("D");
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.removeAll(otherList));
			assertFalse(list.removeAll(otherList));
			assertTrue(list.toString(), Arrays.equals(list.toArray(),
					new String[] { "A", "C", "E" }));
			listener.pullRemoveEvent(1, "B", 3, "D");
			assertTrue(listener.log.isEmpty());
		}

		{
			ObservableList<String> list = createList('A', 'E');
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);
			list.removeAll(list);
			listener.pullRemoveEvent(0, "A", 1, "B", 2, "C", 3, "D", 4, "E");
			assertEquals(0, list.size());
			assertTrue(listener.log.isEmpty());

		}
	}

	public void testRetainAllMethod() {
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('B', 'D');
			otherList.add("Z");
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.retainAll(otherList));
			assertFalse(list.retainAll(otherList));
			assertTrue(list.toString(), Arrays.equals(list.toArray(),
					new String[] { "B", "C", "D" }));
			listener.pullRemoveEvent(0, "A", 4, "E");
			assertTrue(listener.log.isEmpty());
		}

		// detailed event #2:
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('A', 'C');
			otherList.add("Z");
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.retainAll(otherList));
			assertFalse(list.retainAll(otherList));
			listener.pullRemoveEvent(3, "D", 4, "E");
			assertTrue(list.toString(), Arrays.equals(list.toArray(),
					new String[] { "A", "B", "C" }));
			assertTrue(listener.log.isEmpty());
		}

		// detailed event #3:
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('C', 'E');
			otherList.add("Z");
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.retainAll(otherList));
			assertFalse(list.retainAll(otherList));
			assertTrue(list.toString(), Arrays.equals(list.toArray(),
					new String[] { "C", "D", "E" }));
			listener.pullRemoveEvent(0, "A", 1, "B");
			assertTrue(listener.log.isEmpty());
		}

		// detailed event #4:
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = new ObservableList<String>();
			otherList.add("B");
			otherList.add("D");
			otherList.add("E");
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.retainAll(otherList));
			assertFalse(list.retainAll(otherList));
			assertTrue(list.toString(), Arrays.equals(list.toArray(),
					new String[] { "B", "D", "E" }));
			listener.pullRemoveEvent(0, "A", 2, "C");
			assertTrue(listener.log.isEmpty());
		}

		// detailed event #5:
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = new ObservableList<String>();
			otherList.add("A");
			otherList.add("B");
			otherList.add("D");
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.retainAll(otherList));
			assertFalse(list.retainAll(otherList));
			assertTrue(list.toString(), Arrays.equals(list.toArray(),
					new String[] { "A", "B", "D" }));
			listener.pullRemoveEvent(2, "C", 4, "E");
			assertTrue(listener.log.isEmpty());
		}
	}

	public void testSetAll() {
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('a', 'd');
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.setAll(otherList));
			assertFalse(list.setAll(otherList));
			assertTrue(
					list.toString(),
					Arrays.equals(list.toArray(), new String[] { "a", "b", "c",
							"d" }));
			listener.pullReplaceEvent(Arrays.asList("A", "B", "C", "D", "E"),
					Arrays.asList("a", "b", "c", "d"));
			assertTrue(listener.log.isEmpty());
		}

		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('A', 'E');
			otherList.set(2, "c");
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.setAll(otherList));
			assertFalse(list.setAll(otherList));
			assertTrue(
					list.toString(),
					Arrays.equals(list.toArray(), new String[] { "A", "B", "c",
							"D", "E" }));
			listener.pullSetEvent(2, "C", "c");
			assertTrue(listener.log.isEmpty());
		}

		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('A', 'E');
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertFalse(list.setAll(otherList));
			assertTrue(
					list.toString(),
					Arrays.equals(list.toArray(), new String[] { "A", "B", "C",
							"D", "E" }));
			assertTrue(listener.log.isEmpty());
		}

		// detailed event #1:
		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = createList('B', 'D');
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);

			assertTrue(list.setAll(otherList));
			assertFalse(list.setAll(otherList));
			assertTrue(list.toString(), Arrays.equals(list.toArray(),
					new String[] { "B", "C", "D" }));
			listener.pullReplaceEvent(Arrays.asList("A", "B", "C", "D", "E"),
					Arrays.asList("B", "C", "D"));
			assertTrue(listener.log.isEmpty());
		}

		{
			ObservableList<String> list = new ObservableList<>();
			ObservableList<String> otherList = createList('A', 'E');
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);
			assertTrue(list.setAll(otherList));
			listener.pullAddEvent(0, "A", "B", "C", "D", "E");
			assertTrue(listener.log.isEmpty());
		}

		{
			ObservableList<String> list = createList('A', 'E');
			ObservableList<String> otherList = new ObservableList<>();
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);
			assertTrue(list.setAll(otherList));
			listener.pullRemoveEvent(0, "A", 1, "B", 2, "C", 3, "D", 4, "E");
			assertTrue(listener.log.isEmpty());
		}

		{
			ObservableList<String> list = createList('A', 'E');
			LogListListener listener = new LogListListener();
			list.addListListener(listener, false);
			assertFalse(list.setAll(list));
			assertTrue(listener.log.isEmpty());
		}
	}

	public void testSetMethod() {
		ObservableList<String> list = createList('A', 'E');
		LogListListener listener = new LogListListener();
		list.addListListener(listener, false);
		assertEquals(list.set(0, "a"), "A");
		assertEquals(list.set(1, "b"), "B");
		assertEquals(list.set(2, "c"), "C");
		assertEquals(list.set(3, "d"), "D");
		assertEquals(list.set(4, "e"), "E");

		try {
			list.set(-1, "z");
			fail();
		} catch (RuntimeException e) {
		}

		try {
			list.set(5, "z");
			fail();
		} catch (RuntimeException e) {
		}

		listener.pullSetEvent(0, "A", "a");
		listener.pullSetEvent(1, "B", "b");
		listener.pullSetEvent(2, "C", "c");
		listener.pullSetEvent(3, "D", "d");
		listener.pullSetEvent(4, "E", "e");
		assertTrue(listener.log.isEmpty());
		assertTrue(
				list.toString(),
				Arrays.equals(list.toArray(), new String[] { "a", "b", "c",
						"d", "e" }));

		// does redundantly setting an element trigger events?
		assertEquals(list.set(0, "a"), "a");
		assertEquals(list.set(1, "b"), "b");
		assertEquals(list.set(2, "c"), "c");
		assertEquals(list.set(3, "d"), "d");
		assertEquals(list.set(4, "e"), "e");
		assertTrue(listener.log.isEmpty());

	}

	public void testRecursiveListenerModificationException() {

		class RecursiveModificationListener implements ListListener {
			boolean expectFailure;
			int recursionCtr = 0;

			RecursiveModificationListener(boolean expectFailure) {
				this.expectFailure = expectFailure;
			}

			@SuppressWarnings("unchecked")
			public void contentsChanged(ListEvent e) {
				if (recursionCtr > 0)
					return;

				recursionCtr++;
				try {
					ObservableList<String> list = (ObservableList<String>) e
							.getSource();
					list.add("Z");
					if (expectFailure)
						fail("expected failure, but modification passed");
				} catch (RecursiveListenerModificationException e2) {
					if (expectFailure == false)
						fail("modification failed, but no failure was expected");
				} finally {
					recursionCtr--;
				}
			}

			@Override
			public void elementsAdded(AddElementsEvent event) {
				contentsChanged(event);
			}

			@Override
			public void elementsRemoved(RemoveElementsEvent event) {
				contentsChanged(event);
			}

			@Override
			public void elementChanged(ChangeElementEvent event) {
				contentsChanged(event);
			}

			@Override
			public void elementsReplaced(ReplaceElementsEvent event) {
				contentsChanged(event);
			}
		}

		// test #1:
		{
			ObservableList<String> list = createList('A', 'E');
			list.addListListener(new RecursiveModificationListener(true), false);
			list.add("z");
		}

		// test #2:
		{
			ObservableList<String> list = createList('A', 'E');
			list.addListListener(new RecursiveModificationListener(false), true);
			list.add("z");
		}
	}

	/**
	 * This makes sure a second thread can't access our ObservableList as long
	 * as we're still holding on to our write lock.
	 * 
	 */
	public void testWriteLockWithSecondThread() throws InterruptedException {
		final ObservableList<String> list = createList('A', 'E');
		list.addChangeListener(new ChangeListener() {

			Exception otherThreadException;

			@Override
			public void stateChanged(ChangeEvent e) {
				// just prove that we can read the list in this thread
				list.clone();

				// double-check that another thread can access things, too.
				Thread otherThread = new Thread() {
					public void run() {
						try {
							list.clone();
						} catch (Exception e) {
							otherThreadException = e;
						}
					}
				};
				otherThread.start();
				try {
					otherThread.join();
				} catch (Exception e2) {
					e2.printStackTrace();
					fail();
				}
				if (otherThreadException instanceof ObservableList.TimeoutException) {
					// this is what we wanted to see
				} else {
					throw new RuntimeException(otherThreadException);
				}
			}

		}, false);
		list.add("z");

	}
}