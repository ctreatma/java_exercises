package edu.upenn.cis555.mustang.peer;

import edu.upenn.cis555.mustang.index.DistributedHashKey;

public class NodeHashKey implements DistributedHashKey {
	private NodeFactory nodeFactory;
	
	public NodeHashKey(NodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
	}
	
	@Override
	public int getHash(String key) {
		return nodeFactory.getKey(key).hashCode();
	}

	@Override
	public String getId(String key) {
		return nodeFactory.getKey(key).toStringFull();
	}
}
