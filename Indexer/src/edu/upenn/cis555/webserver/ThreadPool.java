package edu.upenn.cis555.webserver;

import edu.upenn.cis555.youtube.P2PCache;
import edu.upenn.cis555.youtube.PastryMap;
import edu.upenn.cis555.youtube.PastryThread;

public class ThreadPool {
    private Thread[] threads = null;
    private int activeThreads = 0;

    public ThreadPool(int size, RequestQueue requests, PastryMap messages, P2PCache cacheApplication) {
        threads = new PastryThread[size];
        for (int i = 0; i < size; ++i) {
            threads[i] = new PastryThread(this, requests, messages, cacheApplication);
            threads[i].start();
        }
    }
    
    public ThreadPool(int size, RequestQueue requests) {
        threads = new RequestThread[size];
        for (int i = 0; i < size; ++i) {
            threads[i] = new RequestThread(this, requests);
            threads[i].start();
        }
    }

    synchronized public void shutDown() {
        try {
            while (activeThreads > 0) {
                wait();
            }
        }
        catch (InterruptedException ex) {
            // Ignore.
        }
    }

    synchronized public void startRequest() {
        activeThreads++;
        //System.out.println("Active threads: " + activeThreads);
        notifyAll();
    }

    synchronized public void finishRequest() {
        activeThreads--;
        //System.out.println("Active threads: " + activeThreads);
        notifyAll();
    }
    
    String getPoolStatus() {
        StringBuffer status = new StringBuffer("Status of threads in pool:\n");
        for (int i = 0; i < threads.length; ++i) {
            status.append("\t");
            status.append(threads[i].getName());
            status.append(": ");
            status.append(threads[i].getState().toString());
            status.append("\n");
        }
        return status.toString();
    }
}
