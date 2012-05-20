package edu.upenn.cis555.mustang.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rice.p2p.commonapi.Id;
import edu.upenn.cis555.mustang.index.DistributedHashKey;
import edu.upenn.cis555.mustang.peer.NodeFactory;

public class Lexicon {
	private DistributedHashKey hashKey;
	private NodeFactory nodeFactory;
	private Map<Integer, Id> mapping; // (word id, node id)
	private List<String> words;
	
	public Lexicon(DistributedHashKey hashKey, NodeFactory nodeFactory) {
		this.hashKey = hashKey;
		this.nodeFactory = nodeFactory;
		mapping = new HashMap<Integer, Id>();
		words = new ArrayList<String>();
	}
	
	public List<String> getWords() {
		return words;
	}

	public int getWordId(String word) {
		return hashKey.getHash(word);
	}
	
	public Id getNodeId(String word) {
		return mapping.get(getWordId(word));
	}
	
	void addEntry(String word) {
		mapping.put(hashKey.getHash(word), nodeFactory.getId(word));
	}
}
