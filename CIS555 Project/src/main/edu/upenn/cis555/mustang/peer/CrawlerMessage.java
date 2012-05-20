package edu.upenn.cis555.mustang.peer;

import java.util.List;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

public class CrawlerMessage implements Message {
	private NodeHandle from;
	private MessageType type;
	private byte[] digest;
	private boolean seen;
	private List<String> links;
	
	CrawlerMessage(NodeHandle from, MessageType type) {
		this.from = from;
		this.type = type;
	}
	CrawlerMessage(NodeHandle from, MessageType type, byte[] dig) {
		this.from = from;
		this.type = type;
		digest = dig;
	}
	CrawlerMessage(NodeHandle from, MessageType type, boolean searched) {
		this.from = from;
		this.type = type;
		seen = searched;
	}
	CrawlerMessage(NodeHandle from, MessageType type, List<String> l) {
		this.from = from;
		this.type = type;
		links = l;
	}
	
	public byte[] getDigest() {
		return digest;
	}

	public void setDigest(byte[] digest) {
		this.digest = digest;
	}

	public boolean getSeen() {
		return seen;
	}

	public void setSeen(boolean alreadySearched) {
		this.seen = alreadySearched;
	}

	public List<String> getLink() {
		return links;
	}

	public void setLink(List<String> links) {
		this.links = links;
	}
	
	public NodeHandle getFrom() {
		return from;
	}
	
	public MessageType getType() {
		return type;
	}

	@Override
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}
	
	@Override
	public String toString() {
		return " from " + (from == null ? "unknown" : from.getId());
	}
	
	

}
