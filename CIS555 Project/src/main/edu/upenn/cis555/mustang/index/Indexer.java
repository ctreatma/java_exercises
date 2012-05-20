package edu.upenn.cis555.mustang.index;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.htmlparser.NodeReader;
import org.htmlparser.Parser;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;

import edu.upenn.cis555.mustang.common.Queue;
import edu.upenn.cis555.mustang.common.StopWords;
import edu.upenn.cis555.mustang.common.WordExtractor;
import edu.upenn.cis555.mustang.crawl.Document;
import edu.upenn.cis555.mustang.datastore.DataRepository;
import edu.upenn.cis555.mustang.peer.BatchRequest;
import edu.upenn.cis555.mustang.peer.IndexApp;
import edu.upenn.cis555.mustang.peer.MessageType;
import edu.upenn.cis555.mustang.peer.Request;

public class Indexer implements Runnable {
	private static final int POSITION_LIMIT = 4096; // 4k 
	
	private Queue<Document> queue;
	private DataRepository dataRepo;
	private IndexApp indexApp;
	private DistributedHashKey hashKey;
	
	public Indexer(Queue<Document> queue, DistributedHashKey hashKey, IndexApp indexApp, 
		DataRepository dataRepo) {
		this.queue = queue;
		this.dataRepo = dataRepo;
		this.indexApp = indexApp;
		this.hashKey = hashKey;
	}
	
	public void run() {
		Parser parser = new Parser();
		Stemmer stemmer = new Stemmer();
//		List<String> headerWords = new ArrayList<String>();
//		List<String> anchorWords = new ArrayList<String>();
		int count = 0;
		
		while (true) {
			Document document = queue.dequeue();
			count++;
			long start = System.currentTimeMillis();
//	    	int from = 0;
	    	int position = 0;
	    	String inputHtml = document.getPage();
//	    	String header = getHeader(inputHtml); // extra credit
//	    	List<String> anchors = getAnchors(inputHtml); // extra credit
//	    	headerWords.clear();
//			anchorWords.clear();
	    	parser.setReader(new NodeReader(new StringReader(inputHtml), "")); // no URL
	    	StringTokenizer tokens = new StringTokenizer(new WordExtractor(parser).getStrings());
	    	// document index
			String docId = hashKey.getId(document.getUrl());
			document.setSize(tokens.countTokens());
	    	dataRepo.createDocumentIndex(docId, document);
	    	int wordCount = 0;
	    	Map<NodeHandle, Request> batchedRequests = new HashMap<NodeHandle, Request>();
	    	while (tokens.hasMoreTokens()) {
	    		String token = tokens.nextToken();// replaceAll("[^\\w\\-']", "").toLowerCase();
	    		// word stemming
	        	char[] chars = token.toCharArray();
	        	stemmer.add(chars, chars.length);
	        	stemmer.stem();
	        	String word = stemmer.toString();
	        	position++;
	        	if (!StopWords.contains(word)) {
	        		wordCount++;
	        		// forward index
	        		Id nodeId = indexApp.getNodeFactory().getKey(word);
	        		int wordId = hashKey.getHash(word);
	        		HitListEntry hitListEntry = new HitListEntry();
//        			hitList.setPosition(inputHtml.indexOf(word, from));
	        		hitListEntry.setPosition(position);
	        		dataRepo.createForwardIndex(docId, wordId, hitListEntry);
//        			from = hitList.getPosition() + word.length();
	        		// inverted index
	            	BatchRequest batchRequest = new BatchRequest();
	            	batchRequest.setWordName(word);
	            	batchRequest.setWordId(wordId);
	            	HitList hitList = new HitList();
	            	hitList.addPosition(position);
	            	// extra credit
	        	/*	if (headerWords.contains(word)) {
	        			hitList.setHeader(true); 
	        		} else if (isHeader(word, header)) {
	        			hitList.setHeader(true);
	        			headerWords.add(word);
	        		}
	        		// extra credit
	        		if (anchorWords.contains(word)) {
		        		hitList.setAnchor(true);
	        		} else if (isAnchor(word, anchors)) {
	        			hitList.setAnchor(true);
	        			anchorWords.add(word);
	        		}*/
	        		batchRequest.setHitList(hitList);
	        		// batch up at document-level and send to node mapped to word 
	        		NodeHandle mappedNode = indexApp.getMappedNode(nodeId);
	        		Request request = batchedRequests.get(mappedNode);
	        		if (request == null) {
	        			request = new Request();
	        			batchedRequests.put(mappedNode, request);
	        		}
	        		request.setDocId(docId);
	        		request.addBatchRequest(batchRequest);
	        	}
	        	if (position > POSITION_LIMIT) {
	        		break;
	        	}
	    	}
	    	// dispatch the batch
	    	for (Map.Entry<NodeHandle, Request> batchedRequest : batchedRequests.entrySet()) {
	    	    indexApp.send(batchedRequest.getKey(), batchedRequest.getValue(), MessageType.INDEX);
	    	}
	    	if (wordCount != 0) {
	    	    System.out.println("\t" + count + ", position: " + position + ", words: " + wordCount + ", ms per word: " + (System.currentTimeMillis() - start)/wordCount);
	    	}
		}
	}
	
	private static boolean isHeader(String word, String header) {
/*		Pattern pattern = Pattern.compile("(?i)\\b" + word + "\\b");
		Matcher matcher = pattern.matcher(header);
		return matcher.find(); */
		return header.indexOf(" " + word) > -1 || header.indexOf(word + " ") > -1;
	}
	
	private static boolean isAnchor(String word, List<String> anchors) {
		for (String anchor : anchors) {
/*			Pattern pattern = Pattern.compile("(?i)\\b" + word + "\\b");
		    Matcher matcher = pattern.matcher(anchor);
			if (matcher.find()) {
				return true;
			} */
			if (anchor.indexOf(" " + word) > -1 || anchor.indexOf(word + " ") > -1) {
				return true;
			}
		}
		return false;		
	}

	private static String getHeader(String page) {
		String title = null;
		String[] fragments = page.split("(?i)</title>");
		if (fragments.length > 1) {
			String fragment = fragments[0];
			if (fragment.matches("(?i).*<title\\b.*>.*")) {
				title = fragment.replaceFirst("(?i).*<title\\b.*>", "").toLowerCase();
			}
		}
		return title;
	}
	
	private static List<String> getAnchors(String page) {
		List<String> anchors = new ArrayList<String>();
		String[] fragments = page.split("</[aA]\\s*>");
		for (String fragment : fragments) {
			if (fragment.matches("(?i).*<a\\b.*href=\"([^\"]*)\".*>.*")) {
				String anchor = fragment.replaceFirst("(?i).*<a\\b.*href=\"([^\"]*)\".*>", "");
				anchors.add(anchor.toLowerCase());
			}
		}
		return anchors;
	}
	
	public static void main(String[] args) {
		String header = getHeader("<html><head><title>Foo Bar</title></head><body><h2>Foo</h2><div><a href=\"foo.html\">Foo bar baz</a> Test</div></body></html>");
		List<String> anchors = getAnchors("<html><head><title>Foo</title></head><body><h2>Foo</h2><div><a href=\"foo.html\">Foo bar baz</a> Test</div></body></html>");
		System.out.println(isHeader("baz", header));		
		System.out.println(isAnchor("foo", anchors));
	}
}
