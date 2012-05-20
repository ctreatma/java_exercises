package edu.upenn.cis555.indexer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.util.ServiceException;

import edu.upenn.cis555.webserver.RequestQueue;
import edu.upenn.cis555.webserver.ServerDaemon;
import edu.upenn.cis555.webserver.ThreadPool;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

public class Indexer implements Application {
    private static final String INSTANCE_NAME = "ctreatma/P2PCache";
    private static final int POOL_SIZE = 10;
    NodeFactory factory;
    Node node;
    Endpoint endpoint;
    ServerDaemon daemon = null;
    ThreadPool workers;
    YouTubeService service;
    PastryMap pastryMap;
    Map<String,String> localCache;
    
    public Indexer(NodeFactory factory, int daemonPort) throws IOException {
        localCache = new HashMap<String,String>();
        this.factory = factory;
        this.node = this.factory.getNode();
        this.endpoint = this.node.buildEndpoint(this, Indexer.INSTANCE_NAME);
        
        if (daemonPort >= 0) {
            RequestQueue requests = new RequestQueue();
            pastryMap = new PastryMap();
            daemon = new ServerDaemon(daemonPort, requests);
            daemon.start();
            
            workers = new ThreadPool(Indexer.POOL_SIZE, requests, pastryMap, this);
        }
        
        service = new YouTubeService(Indexer.INSTANCE_NAME);
        
        this.endpoint.register();
    }

    public Map<String,String> getLocalCache() {
        return localCache;
    }

    public void route(String mapKey, String keyword) {
        try {
            MessageDigest hash = MessageDigest.getInstance("SHA1");
            Id id = factory.getIdFromBytes(hash.digest(keyword.getBytes()));
            IndexerMessage message = new IndexerMessage(node.getLocalNodeHandle(), mapKey, IndexerMessage.Type.QUERY, keyword);
            endpoint.route(id, message, null);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    public void deliver(Id id, Message message) {
        IndexerMessage msg = (IndexerMessage) message;
        System.out.println(endpoint.getId() + " received message from " + msg.getFrom());
        switch (msg.getType()) {
        case QUERY:
            String responseContents;
            if (localCache.containsKey(msg.getKeyword())) {
                responseContents = localCache.get(msg.getKeyword());
            }
            else {
                responseContents = fetchYouTubeResults(msg.getKeyword());
                localCache.put(msg.getKeyword(), responseContents);
            }
            
            IndexerMessage response = new IndexerMessage(node.getLocalNodeHandle(), msg.getMapKey(),
                    IndexerMessage.Type.RESULT, msg.getKeyword(), responseContents);
            
            endpoint.route(null, response, msg.getFrom());
            break;
        case RESULT:
            pastryMap.putMessage(msg.getMapKey(), msg);
            break;
        }
    }
    
    public void shutdownApplication() {
        factory.shutdownNode(node);
        if (daemon != null) {
            daemon.stopServer();
        }
    }
    
    private String fetchYouTubeResults(String keyword) {
        String responseContents = null;
        
        try {
            YouTubeQuery query = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));

            // search for puppies and include restricted content in the search results
            query.setFullTextQuery(keyword);
            query.setSafeSearch(YouTubeQuery.SafeSearch.NONE);

            VideoFeed videoFeed = service.query(query, VideoFeed.class);
            
            responseContents = generateHtml(keyword, videoFeed);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        
        return responseContents;
    }

    public boolean forward(RouteMessage message) {
        // Always return true per assignment directions
        return true;
    }

    public void update(NodeHandle handle, boolean joined) {
        // Don't think this needs to be implemented...
    }
    
    private String generateHtml(String keyword, VideoFeed videoFeed) {
        StringBuffer html = new StringBuffer();
        html.append("<html><head><title>YouTube Search: '" + keyword + "'</title>");
        html.append("<link rel='stylesheet' type='text/css' href='results.css' /></head><body>");
        html.append("<h2>" + videoFeed.getTotalResults() + " results for '" + keyword + "':</h2>");
        html.append("<table id='resultsTable'>");
        html.append("<tr><th>Title</th><th>Description</th><th>Avg. Rating</th>");

        do {
            generateFeedHtml(videoFeed, html);
            if(videoFeed.getNextLink() != null) {
                try {
                    videoFeed = service.getFeed(new URL(videoFeed.getNextLink().getHref()), 
                            VideoFeed.class);
                } catch (MalformedURLException e) {
                    videoFeed = null;
                    e.printStackTrace();
                } catch (IOException e) {
                    videoFeed = null;
                    e.printStackTrace();
                } catch (ServiceException e) {
                    videoFeed = null;
                    e.printStackTrace();
                }
            }
            else {
                videoFeed = null;
            }
        }
        while(videoFeed != null);
        
        return html.toString();
    }
    
    private void generateFeedHtml(VideoFeed videoFeed, StringBuffer out) {
        for (VideoEntry entry : videoFeed.getEntries()) {
            generateEntryHtml(entry, out);
        }
    }
    
    private void generateEntryHtml(VideoEntry entry, StringBuffer out) {
        YouTubeMediaGroup mediaGroup = entry.getMediaGroup();
        String title = entry.getTitle().getPlainText().replaceAll("\\s", "&nbsp;");
        Rating rating = entry.getRating();
        
        out.append("<tr>");
        out.append("<td><a href='" + mediaGroup.getPlayer().getUrl() + "'>" + title + "</a></td>");
        out.append("<td>" + mediaGroup.getDescription().getPlainTextContent() + "</td>");
        out.append("<td>");
        if (rating != null) {
            out.append(rating.getAverage());
        }
        out.append("</td>");
        out.append("</tr>");
    }
    
    public static void main(String[] args) {
        try {
            int numNodes = Integer.parseInt(args[0]);
            InetAddress bootHost = InetAddress.getByName(args[1]);
            int bootPort = Integer.parseInt(args[2]);
            int bindPort = Integer.parseInt(args[3]);
            int daemonPort = -1;
            
            if (args.length > 4) {
                daemonPort = Integer.parseInt(args[4]);
            }

            List<Indexer> cacheServers = new ArrayList<Indexer>();

            Runtime.getRuntime().addShutdownHook(new P2PShutdownThread(cacheServers));
            
            InetSocketAddress bootAddress = new InetSocketAddress(bootHost, bootPort);
            NodeFactory factory;
            if (bootAddress.getAddress().isLoopbackAddress()) {
                factory = new NodeFactory(bindPort);
            }
            else {
                factory = new NodeFactory(bindPort, bootAddress);
            }
            
            cacheServers.add(new Indexer(factory, daemonPort));
            
            for (int i = 1; i < numNodes; ++i) {
                cacheServers.add(new Indexer(factory, -1));
            }
        }
        catch (Exception ex) {
            System.out.println("Usage:");
            System.out.println("java edu.upenn.cis555.youtube.P2PCache <number of nodes> <bootstrap IP> <bootstrap port> <bind port> [<daemon port>]");
        }
    }
}
