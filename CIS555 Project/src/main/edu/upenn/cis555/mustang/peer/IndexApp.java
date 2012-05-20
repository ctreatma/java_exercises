package edu.upenn.cis555.mustang.peer;

import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.NodeReader;
import org.htmlparser.Parser;

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

import edu.upenn.cis555.mustang.common.SearchResult;
import edu.upenn.cis555.mustang.common.WordExtractor;
import edu.upenn.cis555.mustang.datastore.DataRepository;
import edu.upenn.cis555.mustang.datastore.DocumentIndex;
import edu.upenn.cis555.mustang.datastore.ForwardIndex;
import edu.upenn.cis555.mustang.datastore.InvertedIndex;

public class IndexApp implements Application {
	private DataRepository dataRepo;
	private NodeFactory nodeFactory;
	private Node node;
	private Endpoint endpoint;
	private Map<Request, Response> entries;
//	private static Map<Request, Response> entries = new HashMap<Request, Response>();
	private Parser parser;
	
	public IndexApp(NodeFactory nodeFactory, DataRepository dataRepo) {
		this.nodeFactory = nodeFactory;
		this.dataRepo = dataRepo;
		this.node = nodeFactory.getNode();
		this.endpoint = node.buildEndpoint(this, "Index Application");
		endpoint.register();
//		entries = new HashMap<Request, Response>();
		parser = new Parser();
	}

	public NodeFactory getNodeFactory() {
		return nodeFactory;
	}
	
	public Node getNode() {
		return node;
	}

	public void setEntries(Map<Request, Response> entries) {
		this.entries = entries;
	}
	
	public Map<Request, Response> getEntries() {
		return entries;
	}
	
	public void send(Id id, Request request, MessageType messageType) {
		NodeMessage message = new NodeMessage(node.getLocalNodeHandle(), messageType, request, new Response());
	    NodeHandle closest = getMappedNode(id);
		endpoint.route(node.getId(), message, closest);
	}

	public void send(NodeHandle handle, Request request, MessageType messageType) {
		NodeMessage message = new NodeMessage(node.getLocalNodeHandle(), messageType, request, new Response());
		endpoint.route(node.getId(), message, handle);
	}
	
	public void poll(Request request, MessageType messageType) {
//		entries.put(request, new Response());
		NodeMessage message = new NodeMessage(node.getLocalNodeHandle(), messageType, request, new Response());
		if (messageType == MessageType.DOCUMENT_TOTAL_REQUEST) {
			Set<NodeHandle> nodes = new HashSet<NodeHandle>();
			LeafSet leafSet = ((PastryNode) node).getLeafSet();
		    for (int i = -leafSet.ccwSize(); i <= leafSet.cwSize(); i++) {
		    	nodes.add(leafSet.get(i));
		    }
		    for (NodeHandle node : nodes) {
		    	endpoint.route(null, message, node);
		    }
		}
	}
	
	@Override
	public void deliver(Id id, Message message) {
		NodeMessage nodeMessage = (NodeMessage) message;
		Request request = nodeMessage.getRequest();
		if (nodeMessage.getType() == MessageType.INDEX) {	// MessageType.REINDEX
			List<BatchRequest> batchRequests = request.getBatchRequest(); 
			for (BatchRequest batchRequest : batchRequests) {
//				dataRepo.createLexiconEntry(batchRequest.getWordId(), batchRequest.getWordName());
				dataRepo.createInvertedIndex(batchRequest.getWordId(), request.getDocId(), batchRequest.getHitList());
			}
		} else if (nodeMessage.getType() == MessageType.DOCUMENT_TOTAL_REQUEST) {
			int	documentCount = dataRepo.getDocumentCount();
			Response response = nodeMessage.getResponse();
			response.setDocumentCount(documentCount);
			NodeMessage resultMessage = new NodeMessage(node.getLocalNodeHandle(), MessageType.DOCUMENT_TOTAL_RESPONSE, request, response);
			endpoint.route(null, resultMessage, nodeMessage.getFrom());
		} else if (nodeMessage.getType() == MessageType.DOCUMENT_TOTAL_RESPONSE) {
//			Response response = entries.get(request);
			Response response = nodeMessage.getResponse();
			request.countNode();
			request.addDocumentCount(nodeMessage.getResponse().getDocumentCount());
//System.out.println("request.countNode(): " + request.getNodeCount() + " - " + request.getDocumentCount());
//System.out.println("((PastryNode) node).getLeafSet().getUniqueCount():" + ((PastryNode) node).getLeafSet().getUniqueCount());
			if (request.getNodeCount() == ((PastryNode) node).getLeafSet().getUniqueCount()) {
				response.setDocumentCount(request.getDocumentCount());
				entries.put(request, response);
				response.setStatus(ResponseStatus.PROCESSED);
				synchronized (request) {
					request.notify();
				}
			}
		} else if (nodeMessage.getType() == MessageType.INVERTED_INDEX_REQUEST) {
			int wordId = request.getWordId();
			InvertedIndex invertedIndex = dataRepo.getInvertedIndex(wordId);
			Response response = nodeMessage.getResponse();
			if (invertedIndex != null) {
				response.setPositionHitListByWordId(invertedIndex.getPositionHitList());
				response.setContextHintList(invertedIndex.getContextHintList());
			}
			NodeMessage resultMessage = new NodeMessage(node.getLocalNodeHandle(), MessageType.INVERTED_INDEX_RESPONSE, request, response);
			endpoint.route(null, resultMessage, nodeMessage.getFrom());
		} else if (nodeMessage.getType() == MessageType.FORWARD_INDEX_REQUEST) {
			String docId = request.getDocId();
			ForwardIndex forwardIndex = dataRepo.getForwardIndex(docId);
            DocumentIndex documentIndex = dataRepo.getDocumentIndex(docId);
			Response response = nodeMessage.getResponse();
			response.setPositionHitListByDocId(forwardIndex.getPositionHitList());
			response.setPageRank(documentIndex.getPageRank());
			NodeMessage resultMessage = new NodeMessage(node.getLocalNodeHandle(), MessageType.FORWARD_INDEX_RESPONSE, request, response);
			endpoint.route(null, resultMessage, nodeMessage.getFrom());
		} else if (nodeMessage.getType() == MessageType.INVERTED_INDEX_RESPONSE || 
			nodeMessage.getType() == MessageType.FORWARD_INDEX_RESPONSE ||
				nodeMessage.getType() == MessageType.DOCUMENT_RESPONSE) {			
			Response response = nodeMessage.getResponse();
			response.setStatus(ResponseStatus.PROCESSED);
			entries.put(request, response);
			synchronized (request) {
				request.notify();
			}
		} else if (nodeMessage.getType() == MessageType.DOCUMENT_REQUEST) {
			String docId = request.getDocId();
			DocumentIndex documentIndex = dataRepo.getDocumentIndex(docId);
			Response response = nodeMessage.getResponse();
			response.setDocumentSize(documentIndex.getSize());
			response.setMatchedDocument(buildSearchResult(documentIndex, request.getQueryTerms()));
			response.setPageRank(documentIndex.getPageRank());
			NodeMessage resultMessage = new NodeMessage(node.getLocalNodeHandle(), MessageType.DOCUMENT_RESPONSE, request, response);
			endpoint.route(null, resultMessage, nodeMessage.getFrom());
		} else if (nodeMessage.getType() == MessageType.RANK) {
		    dataRepo.updateDocumentIndex(request.getDocId(), request.getPageRank());
		}
	}

	@Override
	public boolean forward(RouteMessage message) {
		return true;
	}

	@Override
	public void update(NodeHandle handle, boolean joined) {
		// no-op
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
	
	private SearchResult buildSearchResult(DocumentIndex documentIndex, List<String> words) {
        Pattern pattern = Pattern.compile("(?i)<head>.*<title>(.*)</title>.*</head>");
        Matcher matcher = pattern.matcher(documentIndex.getContent());
		String title = matcher.find() ? matcher.group(matcher.groupCount()) : "";
        
		StringBuilder buffer = new StringBuilder();
		int position = 0;
		parser.setReader(new NodeReader(new StringReader(documentIndex.getContent()), "")); // no URL
		String text = new WordExtractor(parser).getStrings();
    	StringTokenizer tokens = new StringTokenizer(text);
        while (tokens.hasMoreTokens()) {
        	String token = tokens.nextToken().replaceAll("[^\\w\\-']", "");
        	position++;
        	if (words.contains(token)) {
        		break;
        	}
        }
        int window = 10;
        if (position == 0) {
        	window *= 2; 
        }
    	int displacement = 0;
        tokens = new StringTokenizer(text);
        while (tokens.hasMoreTokens()) {
        	String token = tokens.nextToken().replaceAll("[^\\w\\-']", "");
        	displacement++;
        	if (Math.abs(position - displacement) < window) {
        		buffer.append(token).append(" ");
        	}
        }
		SearchResult result = new SearchResult(title, documentIndex.getUrl(), buffer.toString());
		return result;
	}
}
