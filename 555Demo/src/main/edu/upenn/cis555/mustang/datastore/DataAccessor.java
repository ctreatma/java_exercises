package edu.upenn.cis555.mustang.datastore;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class DataAccessor {
	PrimaryIndex<String, DocumentIndex> documentById;
	PrimaryIndex<Integer, InvertedIndex> invertedIndexByWordId;
	PrimaryIndex<String, ForwardIndex> forwardIndexByDocId;
	PrimaryIndex<Integer, LexiconEntry> lexiconByWordId;
	
	public DataAccessor(EntityStore store) {
		documentById = store.getPrimaryIndex(String.class, DocumentIndex.class);
		lexiconByWordId = store.getPrimaryIndex(Integer.class, LexiconEntry.class);
		invertedIndexByWordId = store.getPrimaryIndex(Integer.class, InvertedIndex.class);
		forwardIndexByDocId = store.getPrimaryIndex(String.class, ForwardIndex.class);
	}
}
