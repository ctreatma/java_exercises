package edu.upenn.cis555.mustang.search;

import java.util.Collection;

public class SubCollection<T> {
	private Collection<T> subCollection;
	private int totalSize;
	
	public SubCollection(Collection<T> collection, int size) {
		subCollection = collection;
		totalSize = size;
	}
	
	public Collection<T> getSubCollection() {
		return subCollection;
	}
		
	public int getTotalSize() {
		return totalSize;
	}
}
