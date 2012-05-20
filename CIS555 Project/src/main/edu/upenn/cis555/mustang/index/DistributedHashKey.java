package edu.upenn.cis555.mustang.index;

public interface DistributedHashKey {
	
	int getHash(String key);
	
	String getId(String key);
	
}
