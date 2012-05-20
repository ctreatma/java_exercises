package edu.upenn.cis555.mustang.webserver;

import junit.framework.TestCase;
import edu.upenn.cis555.mustang.common.Queue;

public class QueueTest extends TestCase {
	private Queue<String> queue;
	
	public QueueTest(String testName) {
		super(testName);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		queue = new Queue<String>();
	}
	
	public void testQueue() {
		assertTrue(queue.isEmpty());
		queue.enqueue("item1");
		queue.enqueue("item2");
		assertFalse(queue.isEmpty());
		assertEquals(queue.dequeue(), "item1");
		assertEquals(queue.dequeue(), "item2");
		assertTrue(queue.isEmpty());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		queue = null;
	}
}
