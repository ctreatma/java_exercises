package edu.upenn.cis555.youtube;

import java.util.List;

public class P2PShutdownThread extends Thread {
    List<P2PCache> cacheServers;
    
    public P2PShutdownThread(List<P2PCache> cacheServers) {
        this.cacheServers = cacheServers;
    }
    
    public void run() {
        for (int i = 0; i < cacheServers.size(); ++i) {
            cacheServers.get(i).shutdownApplication();
        }
    }
}
