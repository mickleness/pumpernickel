package com.pump.thread;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.pump.thread.EventDispatchThreadMonitor.Listener;

import junit.framework.TestCase;

public class EventDispatchThreadMonitorTest extends TestCase {

	long testStartTime;

	/**
	 * This adds a few Listeners, stimulates a frozen EDT, and then makes sure
	 * the listeners are notified appropriately. The listeners are added for
	 * varying thresholds, so this test makes sure they're notified around the
	 * appropriate time.
	 */
	public void testMultipleThresholds()
			throws InvocationTargetException, InterruptedException {

		// just do a little something (anything) to jump start the EDT:
		JFrame f = new JFrame();
		f.pack();
		f.setVisible(true);

		// also jumpstart EventDispatchThreadMonitor class
		EventDispatchThreadMonitor m = EventDispatchThreadMonitor.get();
		while (!m.isInitialized()) {
			Thread.sleep(50);
		}
		f.dispose();

		// another few seconds to start up:
		Thread.sleep(3000);

		List<Integer> millis = new ArrayList<>();
		millis.add(500);
		millis.add(500);
		millis.add(500);
		millis.add(1000);
		millis.add(1000);
		millis.add(1000);
		millis.add(1500);
		millis.add(2000);
		millis.add(2000);

		Collections.shuffle(millis);

		List<Object[]> eventLog = new LinkedList<>();

		int ctr = 0;
		for (Integer i : millis) {
			String id = Character.toString('A' + (ctr++));
			m.addListener(i, new Listener() {

				@Override
				public void becameUnresponsive(Thread eventDispatchThread,
						long unresponsiveMillis,
						long lastSuccessfulPingMillis) {
					eventLog.add(new Object[] { "unresponsive", i, id,
							unresponsiveMillis });
				}

				@Override
				public void becameResponsive(Thread eventDispatchThread,
						long unresponsiveMillis,
						long lastSuccessfulPingMillis) {
					eventLog.add(new Object[] { "responsive", i, id,
							unresponsiveMillis });
				}

			});
		}

		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				testStartTime = System.currentTimeMillis();
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});

		Thread.sleep(500);
		m.removeAllListeners();

		List<String> idsOrderedByAppearance = new LinkedList<>();

		System.out.println(Arrays.asList(eventLog.get(0)));
		assertEquals("unresponsive", eventLog.get(0)[0]);
		assertEquals(500, eventLog.get(0)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(0)[3]) - 500) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(0)[2]);

		System.out.println(Arrays.asList(eventLog.get(1)));
		assertEquals("unresponsive", eventLog.get(1)[0]);
		assertEquals(500, eventLog.get(1)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(1)[3]) - 500) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(1)[2]);

		System.out.println(Arrays.asList(eventLog.get(2)));
		assertEquals("unresponsive", eventLog.get(2)[0]);
		assertEquals(500, eventLog.get(2)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(2)[3]) - 500) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(2)[2]);

		System.out.println(Arrays.asList(eventLog.get(3)));
		assertEquals("unresponsive", eventLog.get(3)[0]);
		assertEquals(1000, eventLog.get(3)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(3)[3]) - 1000) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(3)[2]);

		System.out.println(Arrays.asList(eventLog.get(4)));
		assertEquals("unresponsive", eventLog.get(4)[0]);
		assertEquals(1000, eventLog.get(4)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(4)[3]) - 1000) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(4)[2]);

		System.out.println(Arrays.asList(eventLog.get(5)));
		assertEquals("unresponsive", eventLog.get(5)[0]);
		assertEquals(1000, eventLog.get(5)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(5)[3]) - 1000) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(5)[2]);

		System.out.println(Arrays.asList(eventLog.get(6)));
		assertEquals("unresponsive", eventLog.get(6)[0]);
		assertEquals(1500, eventLog.get(6)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(6)[3]) - 1500) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(6)[2]);

		System.out.println(Arrays.asList(eventLog.get(7)));
		assertEquals("unresponsive", eventLog.get(7)[0]);
		assertEquals(2000, eventLog.get(7)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(7)[3]) - 2000) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(7)[2]);

		System.out.println(Arrays.asList(eventLog.get(8)));
		assertEquals("unresponsive", eventLog.get(8)[0]);
		assertEquals(2000, eventLog.get(8)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(8)[3]) - 2000) < 100);
		idsOrderedByAppearance.add((String) eventLog.get(8)[2]);

		// check that we notified our listeners in the same order when the UI
		// became responsive again

		System.out.println(Arrays.asList(eventLog.get(9)));
		assertEquals("responsive", eventLog.get(9)[0]);
		assertEquals(500, eventLog.get(9)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(9)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(0), eventLog.get(9)[2]);

		System.out.println(Arrays.asList(eventLog.get(10)));
		assertEquals("responsive", eventLog.get(10)[0]);
		assertEquals(500, eventLog.get(10)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(10)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(1), eventLog.get(10)[2]);

		System.out.println(Arrays.asList(eventLog.get(11)));
		assertEquals("responsive", eventLog.get(11)[0]);
		assertEquals(500, eventLog.get(11)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(11)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(2), eventLog.get(11)[2]);

		System.out.println(Arrays.asList(eventLog.get(12)));
		assertEquals("responsive", eventLog.get(12)[0]);
		assertEquals(1000, eventLog.get(12)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(12)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(3), eventLog.get(12)[2]);

		System.out.println(Arrays.asList(eventLog.get(13)));
		assertEquals("responsive", eventLog.get(13)[0]);
		assertEquals(1000, eventLog.get(13)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(13)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(4), eventLog.get(13)[2]);

		System.out.println(Arrays.asList(eventLog.get(14)));
		assertEquals("responsive", eventLog.get(14)[0]);
		assertEquals(1000, eventLog.get(14)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(14)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(5), eventLog.get(14)[2]);

		System.out.println(Arrays.asList(eventLog.get(15)));
		assertEquals("responsive", eventLog.get(15)[0]);
		assertEquals(1500, eventLog.get(15)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(15)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(6), eventLog.get(15)[2]);

		System.out.println(Arrays.asList(eventLog.get(16)));
		assertEquals("responsive", eventLog.get(16)[0]);
		assertEquals(2000, eventLog.get(16)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(16)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(7), eventLog.get(16)[2]);

		System.out.println(Arrays.asList(eventLog.get(17)));
		assertEquals("responsive", eventLog.get(17)[0]);
		assertEquals(2000, eventLog.get(17)[1]);
		assertTrue(Math.abs(((Long) eventLog.get(17)[3]) - 2500) < 200);
		assertEquals(idsOrderedByAppearance.get(8), eventLog.get(17)[2]);

		for (Object[] array : eventLog) {
			System.out.println(Arrays.asList(array));
		}

	}
}
