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

import java.util.Map;

import junit.framework.TestCase;

import com.pump.data.BeanState;

public abstract class BranchTest<B extends Branch<String>> extends TestCase {
	public abstract B createRoot();
	
	public void testScenario1() throws Exception {
		B root = createRoot();
		
		root.createBean("1");
		root.setField("1", "moon", "landing");
		Branch<String> other = root.createBranch("other");
		root.setField("1", "moon", "walk");
		other.setField("1", "moon", "pie");
		
		try {
			other.save();
			fail();
		} catch(SaveException e) {
			//pass!
			assertEquals("1", e.getBeanId());
			assertEquals(other, e.getBranch());
		}

		assertEquals("walk", root.getField("1", "moon"));
	}
	
	public void testScenario2() throws Exception {
		B root = createRoot();
		
		root.createBean("1");
		root.setField("1", "moon", "landing");
		Branch<String> other = root.createBranch("other");
		root.setField("1", "moon", "walk");
		other.setField("1", "sun", "dial");
		
		other.save();

		assertEquals("walk", root.getField("1", "moon"));
		assertEquals("dial", root.getField("1", "sun"));
	}
	
	public void testScenario3() throws Exception {
		B root = createRoot();
		
		root.createBean("1");
		root.setField("1", "moon", "landing");
		root.setField("1", "sun", "roof");
		Branch<String> other = root.createBranch("other");
		other.setField("1", "moon", "walk");
		other.setField("1", "sun", "dial");
		
		other.save();

		assertEquals("walk", root.getField("1", "moon"));
		assertEquals("dial", root.getField("1", "sun"));
	}
	
	public void testScenario4() throws Exception {
		B root = createRoot();
		
		root.createBean("1");
		root.setField("1", "moon", "landing");
		root.setField("1", "sun", "roof");
		Branch<String> other = root.createBranch("other");
		other.setField("1", "moon", "walk");
		other.setField("1", "sun", "dial");
		root.setField("1", "mars", "bar");
		
		other.save();

		assertEquals("walk", root.getField("1", "moon"));
		assertEquals("dial", root.getField("1", "sun"));
		assertEquals("bar", root.getField("1", "mars"));
	}
	
	public void testScenario5() throws Exception {
		B root = createRoot();
		
		root.createBean("1");
		root.setField("1", "moon", "landing");
		root.setField("1", "sun", "roof");
		Branch<String> other = root.createBranch("other");
		other.setField("1", "moon", "walk");
		other.setField("1", "sun", "dial");
		other.setField("1", "mars", "bar");
		root.setField("1", "mars", "rover");

		try {
			other.save();
			fail();
		} catch(SaveException e) {
			//pass!
			assertEquals("1", e.getBeanId());
			assertEquals(other, e.getBranch());
		}


		assertEquals("landing", root.getField("1", "moon"));
		assertEquals("roof", root.getField("1", "sun"));
		assertEquals("rover", root.getField("1", "mars"));
	}

	
	public void testScenario6() throws Exception {
		B root = createRoot();

		Branch<String> other = root.createBranch("other");
		other.createBean("1");
		other.deleteBean("1");
		assertEquals( 0, other.getModifiedBeans().size() );
		
		other.save();
		
		assertEquals( BeanState.UNDEFINED, root.getState("1") );
		assertEquals( 0, root.getModifiedBeans().size() );
	}

	public void testScenario7() throws Exception {
		B root = createRoot();

		Branch<String> other = root.createBranch("other");
		other.createBean("1");
		other.deleteBean("1");
		assertEquals( BeanState.DELETED, other.getState("1") );
		assertEquals( BeanState.UNDEFINED, root.getState("1") );
		assertEquals( 0, other.getModifiedBeans().size() );
		
		other.save();

		assertEquals( BeanState.UNDEFINED, root.getState("1") );
		assertEquals( 0, root.getModifiedBeans().size() );
	}
	
	public void testScenario8() throws Exception {
		B root = createRoot();
		root.createBean("1");

		Branch<String> other = root.createBranch("other");
		other.createBean("2");
		assertEquals( BeanState.CREATED, other.getState("2") );
		assertEquals( BeanState.UNDEFINED, root.getState("2") );
		
		other.deleteBean("2");
		assertEquals( BeanState.CREATED, root.getState("1") );
		assertEquals( BeanState.DELETED, other.getState("2") );
		
		assertEquals( 0, other.getModifiedBeans().size() );
		
		other.save();

		assertEquals( BeanState.CREATED, root.getState("1") );
		assertEquals( BeanState.UNDEFINED, root.getState("2") );
		assertEquals( 1, root.getModifiedBeans().size() );
		assertEquals( "1", root.getModifiedBeans().iterator().next() );
	}
	
	public void testScenario9() throws Exception {
		B root = createRoot();
		root.createBean("1");

		Branch<String> other = root.createBranch("other");
		other.deleteBean("1");
		other.createBean("2");
		assertEquals( BeanState.DELETED, other.getState("1") );
		assertEquals( BeanState.CREATED, other.getState("2") );
		
		assertEquals( 2, other.getModifiedBeans().size() );
		
		other.save();

		assertEquals( BeanState.DELETED, root.getState("1") );
		assertEquals( BeanState.CREATED, root.getState("2") );
		assertEquals( 1, root.getModifiedBeans().size() );
		assertEquals( "2", root.getModifiedBeans().iterator().next() );
	}
	
	public void testScenario10() throws Exception {
		B root = createRoot();
		assertNull(root.getBean("1"));
		assertEquals(BeanState.UNDEFINED, root.getState("1"));
		assertNull(root.getLastRevision("1", "variety"));
		assertNull(root.getLastRevision("1", "chair"));
		
		root.createBean("1");
		assertNotNull(root.getBean("1"));
		assertEquals(BeanState.CREATED, root.getState("1"));
		assertNull(root.getLastRevision("1", "variety"));
		assertNull(root.getLastRevision("1", "chair"));
		
		root.deleteBean("1");
		assertNull(root.getBean("1"));
		assertEquals(BeanState.DELETED, root.getState("1"));
		assertNull(root.getLastRevision("1", "variety"));
		assertNull(root.getLastRevision("1", "chair"));
		
		///////////
		
		root.createBean("1");
		assertNotNull(root.getBean("1"));
		assertEquals(BeanState.CREATED, root.getState("1"));
		
		root.setField("1", "variety", "pack");
		root.setField("1", "chair", "person");

		assertNotNull(root.getLastRevision("1", "variety"));
		assertNotNull(root.getLastRevision("1", "chair"));
		
		Map<String, Object> beanMap = root.getBean("1");
		assertEquals(2, beanMap.size());
		assertEquals("pack", beanMap.get("variety"));
		assertEquals("person", beanMap.get("chair"));
		assertEquals(BeanState.CREATED, root.getState("1"));
		
		root.deleteBean("1");
		assertNull(root.getBean("1"));
		assertEquals(BeanState.DELETED, root.getState("1"));
		assertNotNull(root.getLastRevision("1", "variety"));
		assertNotNull(root.getLastRevision("1", "chair"));
		
		///////////////
		
		root.createBean("1");
		assertNotNull(root.getBean("1"));
		assertEquals(BeanState.CREATED, root.getState("1"));
		
		root.setField("1", "variety", "show");
		beanMap = root.getBean("1");
		assertEquals(1, beanMap.size());
		assertEquals("show", beanMap.get("variety"));
		assertEquals(BeanState.CREATED, root.getState("1"));
		
		root.deleteBean("1");
		assertNull(root.getBean("1"));
		assertEquals(BeanState.DELETED, root.getState("1"));
		assertNotNull(root.getLastRevision("1", "variety"));
		assertNotNull(root.getLastRevision("1", "chair"));
	}
	
	public void testScenario11() throws Exception {
		B root = createRoot();

		assertNull( root.getLastRevision("1") );
		assertEquals( BeanState.UNDEFINED, root.getState("1"));
		
		for(int a = 0; a<2; a++) {
			try {
				root.getField("1", "banana");
				fail();
			} catch(MissingBeanException e) {
				//pass!
			}
				
			root.createBean("1");
			assertEquals( BeanState.CREATED, root.getState("1"));
			assertNotNull( root.getLastRevision("1"));
			assertNull( root.getField("1", "banana"));
			assertNull( root.getLastRevision("1", "banana") );
			try {
				root.createBean("1");
				fail();
			} catch(DuplicateBeanIdException e) {
				//pass!
			}
			assertEquals( BeanState.CREATED, root.getState("1"));
	
			root.deleteBean("1");
			assertEquals( BeanState.DELETED, root.getState("1"));
			try {
				root.deleteBean("1");
				fail();
			} catch(MissingBeanException e) {
				//pass!
			}
			assertNotNull( root.getLastRevision("1"));
			assertEquals( BeanState.DELETED, root.getState("1"));

			try {
				root.getField("1", "banana");
				fail();
			} catch(MissingBeanException e) {
				//pass!
			}
			
			assertNull( root.getLastRevision("1", "banana") );
		}
	}
	
	public void testScenario12() throws Exception {
		B root = createRoot();
		root.createBean("1");
		Branch<String> other = root.createBranch("other");
		other.setField("1", "corn", "on the cob");
		other.save();
		
		root.setField("1", "corn", "maze");
		
		other.setField("1", "moon", "shine");
		
		//really what we want to see most is:
		//does the second call to commit ONLY commit the changes after the 1st commit?
		//... or will it throw an exception because we tried to delete bean #1 twice?
		other.save();
		assertEquals( "maze", root.getField("1",  "corn"));
		assertEquals( "shine", root.getField("1",  "moon"));
	}

	public void testScenario13() throws Exception {
		B root = createRoot();
		root.createBean("1");
		Branch<String> other = root.createBranch("other");
		other.deleteBean("1");
		root.deleteBean("1");
		other.save();
		assertNull( root.getBean("1") );

		//but this should fail:
		root.createBean("2");
		Branch<String> other2 = root.createBranch("other2");
		other2.deleteBean("2");
		root.deleteBean("2");
		other2.createBean("2");
		try {
			other2.save();
			fail();
		} catch(SaveException e) {
			//pass!
		}
	}

	public void testScenario14() throws Exception {
		B root = createRoot();
		Branch<String> other = root.createBranch("other");
		other.createBean("1");
		root.createBean("1");
		other.save();
		assertEquals(0, root.getBean("1").size() );

		//slightly more complex: each branch creates
		//the same bean ID, but the make different changes.
		Branch<String> other2 = root.createBranch("other2");
		other2.createBean("2");
		other2.setField("2", "solar", "flare");
		root.createBean("2");
		root.setField("2", "lunar", "calendar");
		other2.save();
		
		assertEquals("flare", root.getBean("2").get("solar") );
		assertEquals("calendar", root.getBean("2").get("lunar") );

		//OK, but prove they fail for a conflict:
		Branch<String> other3 = root.createBranch("other3");
		other3.createBean("3");
		other3.setField("3", "lunar", "cycle");
		root.createBean("3");
		root.setField("3", "lunar", "calendar");
		try {
			other3.save();
			fail();
		} catch(SaveException e) {
			//pass!
		}
	}

	public void testScenario15() throws Exception {
		B root = createRoot();
		root.createBean("1");
		Branch<String> other = root.createBranch("other");
		other.setField("1", "solar", "panel");
		root.setField("1", "solar", "panel");
		other.save();
		assertEquals( "panel", root.getBean("1").get("solar") );

		//OK, but prove they fail for a conflict:
		root.createBean("2");
		Branch<String> other2 = root.createBranch("other2");
		other2.setField("2", "solar", "flare");
		root.setField("2", "solar", "panel");
		try {
			other2.save();
			fail();
		} catch(SaveException e) {
			//pass
		}
	}
	
}