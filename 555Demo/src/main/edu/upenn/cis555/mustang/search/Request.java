package edu.upenn.cis555.mustang.search;

public class Request {
	private String query;
	
	Request(String query) {
		this.query = query;
	}

	String getQuery() {
		return query;
	}
	
	@Override
	public String toString() {
		return query;
	}
}
