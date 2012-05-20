package com.project550.controller;

import java.util.Map;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class StartComment extends ActionSupport{

	public String execute()throws Exception{

        Map session = ActionContext.getContext().getSession();
        
        if (session.containsKey("user")) {
            setLid(urlid);
            setRep(reply);

            return SUCCESS;
        }
        else {
            addActionError("You must be logged in to do that!");
            return LOGIN;
        }
	}

	private String title;
	private int reply;
	private int urlid;
	private int rep;
	private int lid;

	public String getTitle()
	{
		return this.title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public int getReply()
	{
		return this.reply;
	}
	public void setReply(int reply)
	{
		this.reply = reply;
	}
	public int getUrlid()
	{
		return this.urlid;
	}
	public void setUrlid(int urlid)
	{
		this.urlid = urlid;
	}
	public int getLid()
	{
		return lid;
	}
	public void setLid(int lid)
	{
		this.lid = lid;
	}

	public int getRep()
	{
		return rep;
	}

	public void setRep(int rep)
	{
		this.rep = rep;
	}
}
