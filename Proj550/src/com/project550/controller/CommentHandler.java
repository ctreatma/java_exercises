package com.project550.controller;

import java.util.Map;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.CommentBean;
import com.project550.model.DBConnector;
import com.project550.model.UserBean;

public class CommentHandler extends ActionSupport{

	public String execute()throws Exception
	{
		Map session = ActionContext.getContext().getSession();

		if (session.containsKey("user")) {

			if(getCommentTitle()==null)
	        {
	            return INPUT;
	        }
			
			UserBean user = (UserBean) session.get("user");

            comment.setUid(user.getUid());
            comment.setLid(lid);
            comment.setReply(rep);

            int response = DBConnector.insertComment(comment);
            if(response < 0)
            {
                addActionError("Sorry, your comment could not be entered at this time. Please try again later!!");
                return INPUT;
            }
            else
            {
                return SUCCESS;
            }
		}
        else
        {
            addActionError("You must be logged in to do that!");
            return LOGIN;
        }
	}

	private CommentBean comment = new CommentBean();
	private int lid;
	private int rep;

	public String getCommentTitle()
	{
		return comment.getTitle();
	}
	public void setCommentTitle(String commentTitle)
	{
		comment.setTitle(commentTitle);
	}
	public String getContent()
	{
		return comment.getContent();
	}
	public void setContent(String content)
	{
		comment.setContent(content);
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
