package edu.upenn.cis555.mustang.datastore;

import static com.sleepycat.persist.model.Relationship.*;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class LexiconEntry {
	@PrimaryKey
	private int wordId;
	@SecondaryKey(relate=ONE_TO_ONE)
	private String wordName;
	//Pastry node id?
	
	public int getWordId() {
		return wordId;
	}
	
	void setWordId(int wordId) {
		this.wordId = wordId;
	}
	
	public String getWordName() {
		return wordName;
	}
	
	void setWordName(String wordName) {
		this.wordName = wordName;
	}
}
