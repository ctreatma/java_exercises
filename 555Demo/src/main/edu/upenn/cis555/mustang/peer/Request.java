package edu.upenn.cis555.mustang.peer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Request implements Serializable {
	// for ranking
	private int wordId;
//	private String wordName;
	// for indexing and ranking
	private String docId;
//	private HitListEntry hitList;
	private List<BatchRequest> batchRequests;
    private double pageRank;
	
	// for document polling in indexing
	private int nodeCount;
	private int documentCount;
	// for document retrieval in ranking
	private List<String> words;
	
	public void addBatchRequest(BatchRequest batchRequest) {
		if (batchRequests == null) {
			batchRequests = new ArrayList<BatchRequest>();
		}
		batchRequests.add(batchRequest);
	}
	
	public List<BatchRequest> getBatchRequest() {
		return batchRequests;
	}
	
	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}
/*
	public String getWordName() {
		return wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}
*/
	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
/*
	public HitListEntry getHitListEntry() {
		return hitList;
	}

	public void setHitListEntry(HitListEntry hitList) {
		this.hitList = hitList;
	}
*/
	public List<String> getQueryTerms() {
		return words;
	}

	public void setQueryTerms(List<String> words) {
		this.words = words;
	}

	int getNodeCount() {
		return nodeCount;
	}

	void countNode() {
		nodeCount++;
	}
	
	void addDocumentCount(int documentCount) {
		this.documentCount += documentCount;
	}

	int getDocumentCount() {
		return documentCount; 
	}	
	
	public double getPageRank() {
	    return pageRank;
	}
	
	public void setPageRank(double pageRank) {
	    this.pageRank = pageRank;
	}
/*	
	@Override
	public int hashCode() {
		return wordId == 0 ? docId.hashCode() : wordId; 
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		
		Request that = (Request) object;
		if (docId != that.docId) {
			return false;
		}
		if (wordId != that.wordId) {
			return false;
		}
		return true;
	} */	
}
