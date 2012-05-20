package edu.upenn.cis555.mustang.search;

import java.util.ArrayList;
import java.util.List;

public class Response {
	private String query;
	private List<RankedDocument> results;
	private int resultCount;
	
	Response() {
		results = new ArrayList<RankedDocument>();
	}

	String getQuery() {
		return query;
	}

	void setQuery(String query) {
		this.query = query;
	}

	void addResult(RankedDocument result) {
		results.add(result);
	}
	
	List<RankedDocument> getResults() {
		return results;
	}

	int getResultCount() {
		return resultCount;
	}

	void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
}
