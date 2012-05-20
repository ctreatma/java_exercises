package edu.upenn.cis555.mustang.common;

public class SearchResult {
	private String title; 
	private String url;
	private String blurb;

	public SearchResult(String title, String url, String blurb) {
		this.title = title;
		this.url = url;
		this.blurb = blurb;
	}

	public String getTitle() {
		return title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getBlurb() {
		return blurb;
	}
}
