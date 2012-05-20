package edu.upenn.cis555.mustang.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import edu.upenn.cis555.mustang.index.HitListEntry;

@Entity
public class ForwardIndex {
	@PrimaryKey	
//	@SecondaryKey(relate=ONE_TO_ONE, relatedEntity=DocumentIndex.class)
	private String docId;
//	private Map<Integer, List<HitListEntry>> hitLists; // {(wordId, {hit})}
	private Map<Integer, List<Integer>> positionHitLists;
	
	public ForwardIndex() {
		positionHitLists = new HashMap<Integer, List<Integer>>();
	}
	
	public String getDocId() {
		return docId;
	}
	
	void setDocId(String docId) {
		this.docId = docId;
	}
	
	public Map<Integer, List<Integer>> getPositionHitList() {
		return positionHitLists;
	}
	
	void occur(int wordId, HitListEntry hitListEntry) {
		List<Integer> hitList = positionHitLists.get(wordId);
		if (hitList == null) {
			hitList = new ArrayList<Integer>();
			positionHitLists.put(wordId, hitList);
		}
		hitList.add(hitListEntry.getPosition());
	}
}
