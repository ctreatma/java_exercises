package edu.upenn.cis555.mustang.search;

import edu.upenn.cis555.mustang.common.SearchResult;

public class RankedDocument {
	private SearchResult result;
	private DocumentRank rank;
	
	public RankedDocument(SearchResult matchedDocument) {
		result = matchedDocument;
	}

	public String getTitle() {
		return result.getTitle();
	}
	
	public String getUrl() {
		return result.getUrl();
	}
	
	public String getBlurb() {
		return result.getBlurb();
	}
	
	public DocumentRank getRank() {
		return rank;
	}

	void setRank(DocumentRank rank) {
		this.rank = rank;
	}
}
