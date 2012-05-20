package edu.upenn.cis555.mustang.peer;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

public class NodeMessage implements Message {
	private NodeHandle from;
	private MessageType type;
	private Request request;
	private Response response;
	
	NodeMessage(NodeHandle from, MessageType type, Request request, Response response) {
		this.from = from;
		this.request = request;
		this.type = type;
		this.response = response;
	}
	
	NodeMessage(NodeHandle from, MessageType type, Request request) {
		this(from, type, request, null);
	}
	
	NodeHandle getFrom() {
		return from;
	}

	MessageType getType() {
		return type;
	}
	
	Request getRequest() {
		return request;
	}

	Response getResponse() {
		return response;
	}
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}

	@Override
	public String toString() {
		return " from " + (from == null ? "unknown" : from.getId());
	}
}
