package edu.upenn.cis555.mustang.search;

import java.util.Collection;

public class SubCollection<T> {
	private Collection<T> subCollection;
	private int totalSize;
	private String words;
	
	public SubCollection(Collection<T> collection, int size) {
		subCollection = collection;
		totalSize = size;
	}

	public SubCollection(Collection<T> collection, int size, String ignoredWords) {
		subCollection = collection;
		totalSize = size;
		words = ignoredWords;
	}
	
	public Collection<T> getSubCollection() {
		return subCollection;
	}
		
	public int getTotalSize() {
		return totalSize;
	}
	
	public String getIgnoredWords() {
		return words;
	}	
}
