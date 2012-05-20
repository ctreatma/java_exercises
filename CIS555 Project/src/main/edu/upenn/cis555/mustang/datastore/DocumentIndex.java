package edu.upenn.cis555.mustang.datastore;

import static com.sleepycat.persist.model.Relationship.*;
/*
import java.util.Date; 
import java.util.HashSet;
import java.util.Set;
*/
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class DocumentIndex {
	@PrimaryKey
	private String docId;
	@SecondaryKey(relate=ONE_TO_ONE)
	private String url;
	private int size; 	// number of terms
	private String content;
	private double pageRank;
	private int id;
/*	private Set<String> locations;	// extra credit
	private Date crawled;
	
	public DocumentIndex() {
		locations = new HashSet<String>();
	    pageRank = 0;
	}
*/	
	public String getDocId() {
		return docId;
	}

	void setDocId(String docId) {
		this.docId = docId;
	}
	
	public int getId() {
		return id;
	}
	
	void setId(int id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	
	void setUrl(String url) {
		this.url = url;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getContent() {
		return content;
	}
	
	void setContent(String content) {
		this.content = content;
	}
	
	public double getPageRank() {
	    return pageRank;
	}
	
	public void setPageRank(double pageRank) {
	    this.pageRank = pageRank;
	}
/*	
	public Set<String> getLocation() {
		return locations;
	}
	
	public void addLocation(String location) {
		locations.add(location);
	}

	public Date getCrawled() {
		return crawled;
	}
	
	public void setCrawled(Date crawled) {
		this.crawled = crawled;
	} */
}
