package com.project550.controller;

import java.util.*;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.UserBean;

public class ViewFriends extends ActionSupport {

    public String execute() throws Exception {
        Map session = ActionContext.getContext().getSession();
        UserBean user;

        	if (session.containsKey("user"))
        	{
        	    user = (UserBean) session.get("user");

        	    setFriends(DBConnector.getFriends(user.getUid()));

        	    if(friends == null) {
                    addActionError("We are experiencing technical difficulties.  Please try again later.");
                    friends = new UserBean[0];
                    return SUCCESS;
        	    }
        	    else if(friends.length==0)
        	    {
        	        addActionError("You currently do not have any friends. You should add some!");
        	        return SUCCESS;
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

    private UserBean[] friends;

    public UserBean[] getFriends() {
        return friends;
    }

    public void setFriends(UserBean[] friends) {
        this.friends = friends;
    }
}
