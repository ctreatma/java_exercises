package edu.upenn.cis555.mustang.peer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.upenn.cis555.mustang.common.SearchResult;

public class Response implements Serializable{
//	private int nodeCount;
	private int documentCount;
	private int documentSize;
	private double pageRank;
	private Map<Integer, List<Integer>> positionHitListByDocId;
	private Map<String, Byte> contextHintList;
	private Map<String, List<Integer>> positionHitListByWordId;
	private SearchResult matchedDocument;
	private ResponseStatus status;
/*	
	int getNodeCount() {
		return nodeCount;
	}

	void countNode() {
		nodeCount++;
	}

	void addDocumentCount(int documentCount) {
		this.documentCount += documentCount;
	}
*/
	void setDocumentCount(int documentCount) {
		this.documentCount = documentCount; 
	}
	
	public int getDocumentCount() {
		return documentCount; 
	}

	void setDocumentSize(int documentSize) {
		this.documentSize = documentSize; 
	}
	
	public int getDocumentSize() {
		return documentSize; 
	}
	
	void setPageRank(double pageRank) {
	    this.pageRank = pageRank;
	}
	
	public double getPageRank() {
	    return pageRank;
	}
	
	public Map<Integer, List<Integer>> getPositionHitListByDocId() {
		return positionHitListByDocId;
	}

	void setPositionHitListByDocId(Map<Integer, List<Integer>> positionHitList) {
		this.positionHitListByDocId = positionHitList;
	}

	public Map<String, List<Integer>> getPositionHitListByWordId() {
		return positionHitListByWordId;
	}

	void setPositionHitListByWordId(Map<String, List<Integer>> positionHitList) {
		this.positionHitListByWordId = positionHitList;
	}
	
	public Map<String, Byte> getContextHintList() {
		return contextHintList;
	}

	void setContextHintList(Map<String, Byte> contextHintList) {
		this.contextHintList = contextHintList;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public SearchResult getMatchedDocument() {
		return matchedDocument;
	}

	void setMatchedDocument(SearchResult matchedDocument) {
		this.matchedDocument = matchedDocument;
	}
}
