package edu.upenn.cis555.mustang.peer;

import java.io.Serializable;

import edu.upenn.cis555.mustang.index.HitList;

public class BatchRequest implements Serializable {
	private int wordId;
	private String wordName;
	private HitList hitList;

	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public String getWordName() {
		return wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}

	public HitList getHitList() {
		return hitList;
	}

	public void setHitList(HitList hitList) {
		this.hitList = hitList;
	}
}
