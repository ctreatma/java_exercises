package edu.upenn.cis555.mustang.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rice.p2p.commonapi.Id;

import edu.upenn.cis555.mustang.index.DistributedHashKey;
import edu.upenn.cis555.mustang.peer.IndexApp;
import edu.upenn.cis555.mustang.peer.MessageType;
import edu.upenn.cis555.mustang.peer.Request;
import edu.upenn.cis555.mustang.peer.Response;
import edu.upenn.cis555.mustang.peer.ResponseStatus;

public class Ranker {
	private IndexApp indexApp;
	private DistributedHashKey hashKey;
	private static Map<Request, Response> entries = new ConcurrentHashMap<Request, Response>();

	public Ranker(DistributedHashKey hashKey, IndexApp indexApp) {
		this.indexApp = indexApp;
		this.hashKey = hashKey;
	}
	
	public List<RankedDocument> rank(List<String> words) {
		Request request = new Request();
		indexApp.setEntries(entries); // gateway
		indexApp.poll(request, MessageType.DOCUMENT_TOTAL_REQUEST);
    	synchronized (request) {
    		while (indexApp.getEntries().get(request) == null || indexApp.getEntries().get(request).getStatus() == ResponseStatus.PROCESSING) {
    			try {
    				request.wait();
    			} catch (InterruptedException e) { }
    		}
    	}
		Response response = entries.get(request);
    	// total number of docs in the collection
    	int documentTotal = response.getDocumentCount();
    	indexApp.getEntries().remove(request);
    	
    	// handle duplicate terms in query
    	HashMap<String, Integer> terms = new HashMap<String, Integer>();
    	for (String word : words) {
    		terms.put(word, (terms.containsKey(word) ? terms.get(word) : 0) + 1);
    	}
    	
    	HashMap<String, Integer> wordIds = new HashMap<String, Integer>();
    	Map<String, Integer> docIds = new HashMap<String, Integer>(); // {docId, size} 
        Map<String, Double> pageRanks = new HashMap<String, Double>(); // {docId, size} 
    	// data structure <wordId, IDF>
    	Map<Integer, Double> idf = new HashMap<Integer, Double>();
    	// position hit {(docId, (wordId, {hit}))}
    	Map<String, Map<Integer, List<Integer>>> hitLists = new HashMap<String, Map<Integer, List<Integer>>>();
    	// context hint - promote byte to int to accommodate multi-terms    	
    	Map<String, Integer> hintList = new HashMap<String, Integer>();	
		for (String term : terms.keySet()) {
			Id nodeId = indexApp.getNodeFactory().getKey(term);
			int wordId = hashKey.getHash(term);
			wordIds.put(term, wordId);
	    	request.setWordId(wordId);
	    	indexApp.send(nodeId, request, MessageType.INVERTED_INDEX_REQUEST);
	    	synchronized (request) {
	    		while (indexApp.getEntries().get(request) == null || indexApp.getEntries().get(request).getStatus() == ResponseStatus.PROCESSING) {
	    			try {
	    				request.wait();
	    			} catch (InterruptedException e) { }
	    		}
	    	}
	    	response = indexApp.getEntries().get(request);
	    	// number of docs containing word by word id
	    	Map<String, List<Integer>> docHitLists = response.getPositionHitListByWordId();	// (docId, {positions})
	    	Map<String, Byte> docHintLists = response.getContextHintList();	// (docId, {hints})
	    	if (docHitLists != null) {
		    	for (Map.Entry<String, List<Integer>> hitListEntry : docHitLists.entrySet()) {
		    		String docId = hitListEntry.getKey();
		    		Map<Integer, List<Integer>> hitList = hitLists.get(docId);
		    		if (hitList == null) {
		    			hitList = new HashMap<Integer, List<Integer>>();
		    			hitLists.put(docId, hitList);
		    		}
		    		hitList.put(wordId, hitListEntry.getValue());
		    		byte hint = docHintLists.containsKey(docId) ? docHintLists.get(docId) : 0;
		    		hintList.put(docId, (hintList.containsKey(docId) ? hintList.get(docId) : 0) + hint);
		    	}
	    	}
	    	if (docHitLists != null && !docHitLists.isEmpty()) {
	    		idf.put(wordId, Math.log(documentTotal) - Math.log(docHitLists.keySet().size()));
	    		for (String docId : hitLists.keySet()) {
	    			docIds.put(docId, 0);
	    		}
	    	} else { // in case no documents containing the word
	    		idf.put(wordId, 0.0);
	    	}
	    	indexApp.getEntries().remove(request);
		}
		
		// data structure <docId, <wordId, TF>>
		Map<String, Map<Integer, Double>> tf = new HashMap<String, Map<Integer, Double>>();
		for (Map.Entry<String, Integer> docEntry: docIds.entrySet()) {
			String docId = docEntry.getKey();
			request.setDocId(docId);
			Id nodeId = indexApp.getNodeFactory().getKey(docId);
			indexApp.send(nodeId, request, MessageType.FORWARD_INDEX_REQUEST);
			synchronized (request) {
				while (indexApp.getEntries().get(request) == null || indexApp.getEntries().get(request).getStatus() == ResponseStatus.PROCESSING) {
					try {
						request.wait();
					} catch (InterruptedException e) { }
				}
			}
			response = indexApp.getEntries().get(request);
			// get the PageRank for each document
			double pageRank = response.getPageRank() / documentTotal; // Normalize PageRanks before using for search ranking
            pageRanks.put(docId, pageRank);
			// count the word frequency in each document
			Map<Integer, Double> frequency = tf.get(docId);
			if (frequency == null) {
				frequency = new HashMap<Integer, Double>();
				tf.put(docId, frequency);
			}
			Map<Integer, List<Integer>> occurrences = response.getPositionHitListByDocId();
			int mostFrequency = 0;
			for (Map.Entry<Integer, List<Integer>> entry : occurrences.entrySet()) {
				int wordFrequency = entry.getValue().size();
				if (wordFrequency > mostFrequency) {
					mostFrequency = wordFrequency; 
				}
			}
			// normalize the frequency
			for (Integer wordId : wordIds.values()) {
				frequency.put(wordId, occurrences.containsKey(wordId) ? 1.0 * occurrences.get(wordId).size() / mostFrequency : 0.0);
			}
			indexApp.getEntries().remove(request);
		}
		// convert TF to term weight as TF * IDF
		for (Map.Entry<String, Map<Integer, Double>> docIdEntry : tf.entrySet()) {
			for (Map.Entry<Integer, Double> wordIdEntry : docIdEntry.getValue().entrySet()) {
				wordIdEntry.setValue(wordIdEntry.getValue() * idf.get(wordIdEntry.getKey()));
			}
		}
		
		Map<String, RankedDocument> matchedDocuments = new HashMap<String, RankedDocument>();
		// retrieve document and size (aka # of terms)
		for (Map.Entry<String, Integer> docEntry: docIds.entrySet()) {
			String docId = docEntry.getKey();
			request.setDocId(docId);
			request.setQueryTerms(words);
			Id nodeId = indexApp.getNodeFactory().getKey(docId);
			indexApp.send(nodeId, request, MessageType.DOCUMENT_REQUEST);
			synchronized (request) {
				while (indexApp.getEntries().get(request) == null || indexApp.getEntries().get(request).getStatus() == ResponseStatus.PROCESSING) {
					try {
						request.wait();
					} catch (InterruptedException e) { }
				}
			}
			response = indexApp.getEntries().get(request);
			docEntry.setValue(response.getDocumentSize());
			matchedDocuments.put(docId, new RankedDocument(response.getMatchedDocument()));
			indexApp.getEntries().remove(request);
		}

		// query term weights
		Map<Integer, Double> weights = new HashMap<Integer, Double>();
		int maxFrequency = 0;
		for (Integer termFrequency: terms.values()) {
			if (termFrequency > maxFrequency) {
				maxFrequency = termFrequency; 
			}
		}
		for (Map.Entry<String, Integer> termEntry : wordIds.entrySet()) {
			double weight = (0.5 + (0.5 * terms.get(termEntry.getKey()) / maxFrequency)) * 
				idf.get(termEntry.getValue());
			weights.put(termEntry.getValue(), weight);
		}
		
		List<Integer> positions = new ArrayList<Integer>();
		// compute similarity in cosine
		for (Map.Entry<String, Integer> simEntry : docIds.entrySet()) {
			String docId = simEntry.getKey();
			// similarity in cosine
			double cosine = 0.0f;
			// proximity
			positions.clear();
			for (int wordId : wordIds.values()) {
				cosine += tf.get(docId).get(wordId) * weights.get(wordId) / 
					(simEntry.getValue() * words.size());
				if (hitLists.get(docId).get(wordId) != null) {
					positions.addAll(hitLists.get(docId).get(wordId));
				}
			}
			DocumentRank documentRank = new DocumentRank();
			documentRank.setSimilarity(cosine);
			// context hint
			documentRank.setContextHint(hintList.get(docId));
			// proximity as average closeness
			documentRank.setProximity(computeAverageProximity(positions));
			computeAverageProximity(positions);
			// PageRank
			documentRank.setPageRank(pageRanks.get(docId));
			matchedDocuments.get(docId).setRank(documentRank);
		}
		List<RankedDocument> rankedDocs = new ArrayList<RankedDocument>(matchedDocuments.values());
		Collections.sort(rankedDocs, new Ranking());
		return rankedDocs;
	}
	
	private double computeAverageProximity(List<Integer> positions) {
		if (positions.size() == 1) {
			return -0.1;
		}
		
		Collections.sort(positions);
		int closeness = 0;
		for (int i = 0; i < positions.size() - 1; i++) {
			closeness += positions.get(i+1) - positions.get(i); 
		}
		return 1.0 * closeness / positions.size(); 
	}
	
	private class Ranking implements Comparator<RankedDocument> {
		@Override
		public int compare(RankedDocument rankedDoc1, RankedDocument rankedDoc2) {
			DocumentRank rank1 = rankedDoc1.getRank();
			DocumentRank rank2 = rankedDoc2.getRank();
/*			if (rank1.getProximity() == 0) {
				return -1;
			} else if (rank2.getProximity() == 0) {
				return 1;
			} else {
				return (int) Math.signum((rank2.getSimilarity() - rank1.getSimilarity()) * 0.5 + 
					(1.0 / rank2.getProximity() - 1.0 / rank1.getProximity()) * 0.3 +
					(Math.log1p(rank2.getContextHint()) - Math.log1p(rank1.getContextHint())) * 0.2);
			} */
			return (int) Math.signum(rank2.getScore() - rank1.getScore());
		}
	}
}
