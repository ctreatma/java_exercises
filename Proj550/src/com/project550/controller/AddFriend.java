package com.project550.controller;

import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.project550.model.DBConnector;
import com.project550.model.UserBean;

public class AddFriend extends ActionSupport {

    public String execute() throws Exception {
        Map session = ActionContext.getContext().getSession();

        if (session.containsKey("user")) {
            user = (UserBean) session.get("user");
        }
        else
        {
            addActionError("You must be logged in to do that!");
            return LOGIN;
        }
        if(DBConnector.addFriend(user.getUid(), fid))
        {
        	return SUCCESS;
        }
        else
        {
        	addActionError("Sorry your request could not be completed. Please try again later");
        	return INPUT;
        }
    }

    private int fid;
    private UserBean user;
    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }
}
