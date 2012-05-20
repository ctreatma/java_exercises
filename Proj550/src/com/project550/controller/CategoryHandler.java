package com.project550.controller;

import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.LinkBean;

public class CategoryHandler extends ActionSupport {

	public String execute()throws Exception
	{
		links = DBConnector.getLinksByCategory(category, byRating);
        if(links == null)
        {
            addActionError("We are experiencing technical difficulties.  Please try again later.");
            return INPUT;
        }
        
		if(links.length==0)
		{
			addActionError("Sorry there are no links in this category");
			return INPUT;
		}

		return SUCCESS;
	}

	private String category;
	private LinkBean[] links;
	private boolean byRating = false;

	public LinkBean[] getLinks() {
        return links;
    }

    public void setLinks(LinkBean[] links) {
        this.links = links;
    }

    public void setCategory(String category)
	{
		this.category = category;
	}

	public String getCategory ()
	{
		return this.category;
	}

    public boolean isByRating() {
        return byRating;
    }

    public void setByRating(boolean byRating) {
        this.byRating = byRating;
    }

}