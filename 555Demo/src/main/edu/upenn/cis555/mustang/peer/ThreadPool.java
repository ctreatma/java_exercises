package edu.upenn.cis555.mustang.peer;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.index.DistributedHashKey;
import edu.upenn.cis555.mustang.search.Searcher;

public class ThreadPool {
	public final static int DEFAULT_SIZE = 10;
	
	private List<Thread> threads;
	
	public ThreadPool(int size, DistributedHashKey hashKey, IndexApp indexApp, Queue<Socket> queue) {
		if (size < 1) {
			size = DEFAULT_SIZE; 
		}
		threads = new ArrayList<Thread>(size);
		for (int i = 0; i < size; i++) {
			threads.add(new Thread(new Searcher(queue, hashKey, indexApp)));
		}
	}
	
	public void start() {
		for (Thread thread : threads) {
//			thread.setDaemon(true); // a way to terminate but not as good
			thread.start();
		}
	}
	
	public synchronized void stop() {
		// wake up all the threads
		notifyAll();
		// allow time to respond to the wake up call 
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) { }
		for (Thread thread : threads) {
			// wait for thread to finish request processing
			while (true) {
				if (thread.getState() != Thread.State.RUNNABLE) {
//					thread.stop(); // deprecated - Don't use
					thread.interrupt();
					break;
				}
			}
		}
	}
	
	public void status() {
		for (Thread thread : threads) {
			System.out.println(thread.getName() + ": " + thread.getState());
		}
	}
}
