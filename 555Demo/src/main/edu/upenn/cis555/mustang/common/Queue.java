package edu.upenn.cis555.mustang.common;

/**
 * Generic queue emulating a linked list in which each queue item is of type T.
 * @see java.util.LinkedList
 */
public class Queue<T> {
    private int size;
    private Entry<T> first; // start of queue
    private Entry<T> last; // end of queue

    // default constructor to create an empty queue
    public Queue() {
        first = null;
        last  = null;
    }

    /**
     * Determines is the queue is empty
     * @return boolean
     */
    public boolean isEmpty() {
    	return first == null; 
    }
    
    /**
     * Returns the number of items in the queue.
     * @return int
     */
    public int size() {
    	return size;         
    }

    /** 
     * Adds an item to the queue
     * @param item
     */
    public synchronized void enqueue(T item) {
        Entry<T> entry = new Entry<T>();
        entry.item = item;
        if (isEmpty()) {
        	first = entry;
        	last = entry;
        	notify();
        } else { 
        	last.next = entry; 
        	last = entry; 
        }
        size++;
    }

    /**
     * Removes and returns the least recently added item
     */
    public synchronized T dequeue() {
    	while (isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            	return null;
           }
        }
        
        T item = first.item;
        first = first.next;
        size--;
        return item;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Entry<T> entry = first; entry != null; entry = entry.next) {
        	builder.append(entry.item).append(", ");
        }
        builder.replace(builder.length() - 1, builder.length(), "]");
        return builder.toString();
    }
    
    // helper linked list class
    private static class Entry<T> {
        private T item;
        private Entry<T> next;
    }
}
