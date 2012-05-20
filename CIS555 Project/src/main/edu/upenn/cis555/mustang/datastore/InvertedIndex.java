package edu.upenn.cis555.mustang.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import edu.upenn.cis555.mustang.index.HitList;

@Entity
public class InvertedIndex {
	@PrimaryKey
//	@SecondaryKey(relate=ONE_TO_ONE, relatedEntity=LexiconEntry.class)
	private int wordId;
//	private Map<Integer, List<HitListEntry>> hitList; // {(docId, {hit})} 
	private Map<String, List<Integer>> positionHitLists; // {(docId, {occurrence})}
	private Map<String, Byte> contextHintLists; // {(docId, {hint})} for extra credit
//	private List<Integer> headerHitLists; // {docId}
//	private List<Integer> anchorHitLists; // {docId}
	
	public InvertedIndex() {
/*		positionHitLists = new TreeMap<String, List<Integer>>(new Comparator<String>() {
			public int compare(String docId1, String docId2) {
				return docId1.compareTo(docId2);
			}
		}); */
		positionHitLists = new HashMap<String, List<Integer>>();
		contextHintLists = new HashMap<String, Byte>();
	}
	
	public int getWordId() {
		return wordId;
	}
	
	void setWordId(int wordId) {
		this.wordId = wordId;
	}
	
	public Map<String, List<Integer>> getPositionHitList() {
		return positionHitLists;
	}
	
	public Map<String, Byte> getContextHintList() {
		return contextHintLists;
	}
	
	public int getDocumentCount() {
		return positionHitLists.keySet().size();
	}

	void occur(String docId, HitList hitList) {
		List<Integer> hitListEntries = positionHitLists.get(docId);
		if (hitListEntries == null) {
			hitListEntries = new ArrayList<Integer>();
			positionHitLists.put(docId, hitListEntries);
		}
		for (int position : hitList.getPositions()) {
			hitListEntries.add(position);
		}
		byte hint = 0;
		if (hitList.isHeader()) {
			hint += 1;
		}
		if (hitList.isAnchor()) {
			hint += 2;
		}
		if (hint > 0) {
			contextHintLists.put(docId, hint);
		}
	}	
/*	
	void occur(String docId, HitListEntry hitListEntry) {
		List<Integer> hitList = positionHitLists.get(docId);
		if (hitList == null) {
			hitList = new ArrayList<Integer>();
			positionHitLists.put(docId, hitList);
		}
		hitList.add(hitListEntry.getPosition());
		byte hint = 0;
		if (hitListEntry.isHeader()) {
			hint += 1;
		}
		if (hitListEntry.isAnchor()) {
			hint += 2;
		}
		if (hint > 0) {
			contextHintLists.put(docId, hint);
		}
	}
	
	void reset(String docId) {
		List<Integer> hitList = positionHitLists.get(docId);
		if (hitList != null) {
			positionHitLists.remove(docId);
			contextHintLists.remove(docId);
		}
	} */ 
}
