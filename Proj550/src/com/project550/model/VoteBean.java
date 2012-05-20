package com.project550.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This object stores all of the attributes associated with a user's
 * vote for a particular link.
 */
public class VoteBean {

	/** Id number of the user who placed the vote. */
	private int uid;
	/** Id number of the link the user is voting on. */
	private int lid;
	/** The rating/level of the vote specified by the user. */
	private int rating;
	
	private Timestamp postDateTime;
	
	/**
	 * Default constructor which populates VoteBean object with "null" values.
	 */
	public VoteBean() {
		uid = -1;
		lid = -1;
		rating = -1;
		postDateTime = null;
	}

	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * @return the lid
	 */
	public int getLid() {
		return lid;
	}

	/**
	 * @param lid the lid to set
	 */
	public void setLid(int lid) {
		this.lid = lid;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) throws Exception {
	    if (rating < 0 || rating > 5) throw new Exception("Rating must be in 0-5 range!");
		this.rating = rating;
	}

    public Timestamp getPostDateTime() {
        return postDateTime;
    }

    public void setPostDateTime(Timestamp postDateTime) {
        this.postDateTime = postDateTime;
    }
    
    public String getFormattedDate() {
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(postDateTime.getTime());
        return df.format(c.getTime());
    }
}