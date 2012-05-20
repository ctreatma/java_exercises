package edu.upenn.cis555.youtube;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class P2PCacheTest {
    final String query = "onomotopeia";
    static P2PCache cache = null;
    static int bootPort = 8001;
    static int bindPort = 8001;
    static int daemonPort = 8000;
    
    @BeforeClass
    public static void setUp() throws Exception {
        InetSocketAddress bootAddress = new InetSocketAddress(InetAddress.getLocalHost(), bootPort);
        NodeFactory factory = new NodeFactory(bindPort, bootAddress);
            
        cache = new P2PCache(factory, daemonPort);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        cache.shutdownApplication();
    }
    
    @org.junit.Test public void testQueryYouTubeSucceeds() throws Exception {
        URL url = new URL("http://localhost:" + daemonPort + PastryThread.REST_PATH + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.connect();
        
        Assert.assertTrue(conn.getResponseCode() == HttpURLConnection.HTTP_OK);
    }
    
    @org.junit.Test public void testQueryGetsCached() throws Exception {
        // Don't need to re-run the query;  if the caching works then the results are already cached.
        Assert.assertTrue(cache.getLocalCache().containsKey(query));
    }

}
