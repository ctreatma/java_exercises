package edu.upenn.cis555.mustang.peer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.commonapi.Id.Distance;
import rice.pastry.PastryNode;
import rice.pastry.leafset.LeafSet;
import edu.upenn.cis555.mustang.crawl.Crawler;

public class CrawlerApp implements Application {
	private NodeFactory nodeFactory;
	private Node node;
	private Endpoint endpoint;
	private LeafSet leafSet; 
	private Crawler crawler;
	private int count;
	private boolean digestSeen;
	private volatile boolean ready;

	public CrawlerApp(NodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
		this.node = nodeFactory.getNode();
		this.endpoint = node.buildEndpoint(this, "Crawler Application");
		endpoint.register();
	//	node.getEnvironment().getParameters().setInt("pastry_socket_writer_max_queue_length", 30000);
	}
	
	public CrawlerApp(NodeFactory nodeFactory, Node node) {
		this.nodeFactory = nodeFactory;
		this.node = node;
		this.endpoint = node.buildEndpoint(this, "Crawler Application");
		endpoint.register();
		node.getEnvironment().getParameters().setInt("pastry_socket_writer_max_queue_length", 3000);
	}
	
	public void setCrawler(Crawler c) {
		crawler = c;
	}

	public NodeFactory getNodeFactory() {
		return nodeFactory;
	}
	
	public Node getNode() {
		return node;
	}
	
	public boolean getReady() {
		return ready;
	}
	
	public List<String> sendLinks(List<String> links) {
		Map<NodeHandle, List<String>> toMap = new HashMap<NodeHandle, List<String>>();
		for(String s : links) {
			try {
				URL url = new URL(s);
				Id id = nodeFactory.getId(url.getHost());
				NodeHandle closest = getMappedNode(id);
				List<String> list = toMap.get(closest);
				if(list == null)
					list = new ArrayList<String>();
				list.add(s);
				toMap.put(closest, list);
				
			} catch(Exception e) {
			    //			e.printStackTrace();
			}
			
		}
		
		for(NodeHandle nh : toMap.keySet()) {
			CrawlerMessage message = new CrawlerMessage(node.getLocalNodeHandle(), MessageType.LINK, toMap.get(nh));
		//	System.out.println("Sending from: " + node.getId() + " to: " + closest.getId());
			try {
			    endpoint.route(null, message, nh);
		//	node.getEnvironment().getTimeSource().sleep(50);
			} catch(Exception e) {
			    //		e.printStackTrace();
			}
		}
		return null;
	}
	
	public NodeHandle getMappedNode(Id id) {
	    Map<NodeHandle, Distance> nodes = new HashMap<NodeHandle, Distance>();
		LeafSet leafSet = ((PastryNode) node).getLeafSet();
	    for (int i = -leafSet.ccwSize(); i <= leafSet.cwSize(); i++) {
	    	nodes.put(leafSet.get(i), id.distanceFromId(leafSet.get(i).getId()));
	    }
	    Set<Map.Entry<NodeHandle, Distance>> ring = 
	    	new TreeSet<Map.Entry<NodeHandle, Distance>>(new Comparator<Map.Entry<NodeHandle, Distance>>() {
            public int compare(Map.Entry<NodeHandle, Distance> node1, Map.Entry<NodeHandle, Distance> node2) {
                return node1.getValue().compareTo(node2.getValue());
            }
        });
	    ring.addAll(nodes.entrySet());
	    NodeHandle closest = null;
	    if (ring.iterator().hasNext()) {
	    	closest = ring.iterator().next().getKey();
	    } else {
	    	closest = node.getLocalNodeHandle();
	    }
	    return closest;
	}
	
	public void digestQuery(byte[] digest) {
		digestSeen = false;
		ready = false;
		count = 0;
		CrawlerMessage message = new CrawlerMessage(node.getLocalNodeHandle(), MessageType.DIGEST_QUERY, digest);
		if (leafSet == null) {
			leafSet = ((PastryNode) node).getLeafSet();
		}
		if(leafSet.getUniqueCount()==1) {
			synchronized (crawler) {
				ready = true;
				crawler.notifyAll();
			}
		}
		for (int i = 1; i <= leafSet.cwSize(); i++) {
			try {
				endpoint.route(null, message, leafSet.get(i));
		//		node.getEnvironment().getTimeSource().sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean digestSeen() {
		return digestSeen;
	}
	
	@Override
	public void deliver(Id id, Message msg) {
		CrawlerMessage message = (CrawlerMessage) msg;
		if (message.getType() == MessageType.DIGEST_QUERY) {
	//		System.out.println("DigestQuery received");
			byte[] digest = message.getDigest();
			boolean result = crawler.hasSeenDigest(digest);
			CrawlerMessage resultMessage = new CrawlerMessage(node.getLocalNodeHandle(), MessageType.DIGEST_RESPONSE, result);
			endpoint.route(null, resultMessage, message.getFrom());
			
		} else if (message.getType() == MessageType.DIGEST_RESPONSE) {
	//		System.out.println("DigestResponse received");
			count++;
			if(!digestSeen)
				digestSeen = message.getSeen();
			if(digestSeen || count == leafSet.getUniqueCount()-1)
				synchronized (crawler) {
	//				System.out.println("waking up");
					ready = true;
					crawler.notifyAll();
				}
			
		} else if (message.getType() == MessageType.LINK) {
		//	System.out.println("Link received");
		    crawler.enqueue(message.getLink());
		}
	}

	@Override
	public boolean forward(RouteMessage arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void update(NodeHandle arg0, boolean arg1) {
		// TODO Auto-generated method stub
	}

}
