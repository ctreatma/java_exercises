package com.project550.controller;
import java.sql.Timestamp;

import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.LinkBean;

public class Home extends ActionSupport {

	public String execute()throws Exception
	{
		 links = DBConnector.getTopTenLinks(byRating);
		 if(links == null) {
		     addActionError("We are experiencing technical difficulties.  Please try again later.");
		     return SUCCESS;
		 }
		 else if(links.length==0)
		 {
			 addActionError("There are no links in our database!! Sorry");
			 return SUCCESS;
		 }
		 else
			 return SUCCESS;

	}

	private LinkBean[] links;
	private boolean byRating = false;

	public boolean isByRating() {
        return byRating;
    }
    public void setByRating(boolean byRating) {
        this.byRating = byRating;
    }
    public LinkBean[] getLinks()
	{
		return this.links;
	}
	public void setLinks(LinkBean[] links)
	{
		this.links = links;
	}

}
