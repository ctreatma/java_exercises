package com.project550.controller;

import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.LinkBean;

public class Search extends ActionSupport{

	public String execute() throws Exception
	{
	        if (type == null || type.equalsIgnoreCase("keyword")) {
	            if(searchstring == null || searchstring.trim().length() == 0)
	            {
	                addActionError("<strong>You have not specified any search string</strong>");
	                return INPUT;
	            }
	            
	            String[] keywords = getSearchstring().replaceAll("\\W+", " ").split("\\s+");
	            setLinks(DBConnector.getLinksByKeyword(keywords, byRating));

	            return SUCCESS;
	        }
	        else if (type.equalsIgnoreCase("rating")) {
	            setLinks(DBConnector.getLinksByRating(minRating, maxRating, true, byRating));
	            return SUCCESS;
	        }
	        else if (type.equalsIgnoreCase("date")) {
                
                if (minDate == null) {
                    if (maxDate == null) {
                        addActionError("You must specify a date for searching!");
                        return INPUT;
                    }
                    //setLinks(DBConnector.getLinksByDate(new Timestamp(maxDate.getTime()), maxOp));
                    setLinks(DBConnector.getLinksByDate(new Timestamp(maxDate.getTime()), "<=", byRating));
                }
                else if (maxDate == null) {
                    //setLinks(DBConnector.getLinksByDate(new Timestamp(minDate.getTime()), minOp));
                    setLinks(DBConnector.getLinksByDate(new Timestamp(minDate.getTime()), ">=", byRating));
                }
                else {
                    setLinks(DBConnector.getLinksByDate(new Timestamp(minDate.getTime()), new Timestamp(maxDate.getTime()), true, byRating));
                }
                return SUCCESS;
	        }
	        else {
	            addActionError("Error performing search.  Please try again.");
	            return INPUT;
	        }
	}

	private LinkBean[] links;
	private String searchstring;
	private String type = null;
	private int minRating = 0;
	private int maxRating = 5;
	private Date minDate = null;
	private Date maxDate = null;
	private boolean byRating = false;

	public boolean isByRating() {
        return byRating;
    }
    public void setByRating(boolean byRating) {
        this.byRating = byRating;
    }
    public String getSearchstring()
	{
		return searchstring;
	}
	public void setSearchstring(String searchstring)
	{
		this.searchstring = searchstring;
	}
    public LinkBean[] getLinks() {
        return links;
    }
    public void setLinks(LinkBean[] links) {
        this.links = links;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getMinRating() {
        return minRating;
    }
    public void setMinRating(int minRating) {
        this.minRating = minRating;
    }
    public int getMaxRating() {
        return maxRating;
    }
    public void setMaxRating(int maxRating) {
        this.maxRating = maxRating;
    }
    public Date getMinDate() {
        return minDate;
    }
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }
    public Date getMaxDate() {
        return maxDate;
    }
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }
}
