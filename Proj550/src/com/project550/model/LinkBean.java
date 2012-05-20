package com.project550.model;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This object stores all of the attributes from a link entity.
 */
public class LinkBean {

	/** Unique id number identifying a link. */
	private int lid;
	/** The url for the link. */
	private URL linkUrl;
	/** Link title entered by the user. */
	private String title;
	/** Link description entered by the user. */
	private String description;
	/** Category of the link specified by the user. */
	private String category;
	/** The date and time the link was posted. */
	private Timestamp postDateTime;
	/**
	 * Specifies the two possible access options:
	 * public or private access.
	 */
	public static enum Access {PUBLIC, PRIVATE};
	/** Links access constraints (private or public). */
	private Access access;
	private UserBean user;
	private float rating;
	
	public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
	 * Default constructor which populates LinkBean object with "null" values.
	 */
	public LinkBean() {
		lid = -1;
		linkUrl = null;
		title = null;
		description = null;
		postDateTime = null;
		//Public access by default
		access = Access.PUBLIC;
		user = null;
		rating = 0;
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
	 * @return the linkUrl
	 */
	public URL getLinkUrl() {
		return linkUrl;
	}

	/**
	 * @param linkUrl the linkUrl to set
	 */
	public void setLinkUrl(URL linkUrl) {
		this.linkUrl = linkUrl;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the postDateTime
	 */
	public Timestamp getPostDateTime() {
		return postDateTime;
	}

	/**
	 * @param postDateTime the postDateTime to set
	 */
	public void setPostDateTime(Timestamp postDateTime) {
		this.postDateTime = postDateTime;
	}

	/**
	 * @return the access
	 */
	public Access getAccess() {
		return access;
	}

	/**
	 * @param access the access to set
	 */
	public void setAccess(Access access) {
		this.access = access;
	}

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
	
	public String getFormattedDate() {
	    SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	    Calendar c = Calendar.getInstance();
	    c.setTimeInMillis(postDateTime.getTime());
	    return df.format(c.getTime());
	}
}