package edu.upenn.cis555.youtube;

import java.util.HashMap;
import java.util.Map;

public class PastryMap {
    private Map<String, YouTubeMessage> pastryMap;
    
    public PastryMap() {
        pastryMap = new HashMap<String, YouTubeMessage>();
    }
    
    synchronized public void putMessage(String key, YouTubeMessage message) {
        pastryMap.put(key, message);
        notifyAll();
    }
    
    synchronized public YouTubeMessage getMessage(String key, YouTubeMessage.Type type) {
        while (!pastryMap.containsKey(key) || pastryMap.get(key).getType() != type) {
            try {
                wait();
            }
            catch (InterruptedException ex) {
                // Ignore
            }
        }
        return pastryMap.get(key);
    }
}
